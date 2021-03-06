package systems.comodal.pagerduty.event.client.jsoniter.adapter;

import systems.comodal.jsoniter.ContextFieldBufferPredicate;
import systems.comodal.jsoniter.JsonIterator;
import systems.comodal.pagerduty.event.data.PagerDutyEventResponse;
import systems.comodal.pagerduty.event.data.adapters.PagerDutyEventAdapter;
import systems.comodal.pagerduty.exceptions.PagerDutyClientException;
import systems.comodal.pagerduty.exceptions.PagerDutyParseException;
import systems.comodal.pagerduty.exceptions.PagerDutyRequestException;

import java.net.http.HttpResponse;

import static systems.comodal.jsoniter.JsonIterator.fieldEquals;

final class JsonIteratorPagerDutyEventAdapter implements PagerDutyEventAdapter {

  static final JsonIteratorPagerDutyEventAdapter INSTANCE = new JsonIteratorPagerDutyEventAdapter();

  private JsonIteratorPagerDutyEventAdapter() {
  }

  @Override
  public RuntimeException errorResponse(final HttpResponse<byte[]> response) {
    final var exception = PagerDutyRequestException.build(response);
    final int statusCode = response.statusCode();
    if (statusCode == 429) {
      throw exception.message("Too many requests").create();
    } else if (statusCode == 404) {
      throw exception.message(response.request().uri() + " Not Found").create();
    } else if (statusCode == 400) {
      exception.message("Bad Request - Check that the JSON is valid.");
    } else if (statusCode == 401) {
      exception.message("Unauthorized");
    } else if (statusCode == 402) {
      exception.message("Payment Required");
    } else if (statusCode == 403) {
      exception.message("Forbidden");
    } else if (statusCode >= 500 && statusCode < 600) {
      exception.message("Internal Server Error - the PagerDuty server experienced an error while processing the event.");
    }
    final var ji = JsonIterator.parse(response.body());
    final RuntimeException responseError;
    try {
      responseError = ji.testObject(exception, EXCEPTION_PARSER).create();
    } catch (final RuntimeException runtimeCause) {
      try {
        throw new PagerDutyParseException(response,
            String.format("Failed to adapt %d error response: '%s'", statusCode, ji.currentBuffer()),
            runtimeCause);
      } catch (final RuntimeException ex) {
        throw new PagerDutyParseException(response,
            String.format("Failed to adapt %d error response: '%s'", statusCode, new String(response.body())),
            runtimeCause);
      }
    }
    throw responseError;
  }

  @Override
  public PagerDutyEventResponse adaptResponse(final HttpResponse<byte[]> response) {
    verifyHttpResponseCode(response);
    final var ji = JsonIterator.parse(response.body());
    try {
      return ji.testObject(PagerDutyEventResponse.build(), EVENT_RESPONSE_PARSER).create();
    } catch (final RuntimeException runtimeCause) {
      if (runtimeCause instanceof PagerDutyClientException) {
        throw runtimeCause;
      }
      try {
        throw new PagerDutyParseException(response, runtimeCause, ji.currentBuffer());
      } catch (final RuntimeException ex) {
        throw new PagerDutyParseException(response, "Failed to adapt event response.", runtimeCause);
      }
    }
  }

  private static final ContextFieldBufferPredicate<PagerDutyEventResponse.Builder> EVENT_RESPONSE_PARSER = (response, buf, offset, len, ji) -> {
    if (fieldEquals("status", buf, offset, len)) {
      response.status(ji.readString());
    } else if (fieldEquals("message", buf, offset, len)) {
      response.message(ji.readString());
    } else if (fieldEquals("dedup_key", buf, offset, len)) {
      response.dedupKey(ji.readString());
    } else {
      ji.skip();
    }
    return true;
  };

  private static final ContextFieldBufferPredicate<PagerDutyRequestException.Builder> EXCEPTION_PARSER = (exception, buf, offset, len, ji) -> {
    if (fieldEquals("status", buf, offset, len)) {
      exception.status(ji.readString());
    } else if (fieldEquals("message", buf, offset, len)) {
      exception.message(ji.readString());
    } else if (fieldEquals("code", buf, offset, len)) {
      exception.message(ji.readString());
    } else if (fieldEquals("errors", buf, offset, len)) {
      while (ji.readArray()) {
        exception.error(ji.readString());
      }
    } else {
      ji.skip();
    }
    return true;
  };
}
