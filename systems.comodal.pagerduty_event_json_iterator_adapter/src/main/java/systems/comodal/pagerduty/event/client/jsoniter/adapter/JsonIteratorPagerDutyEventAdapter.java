package systems.comodal.pagerduty.event.client.jsoniter.adapter;

import systems.comodal.jsoniter.ContextFieldBufferPredicate;
import systems.comodal.jsoniter.JsonIterator;
import systems.comodal.pagerduty.event.data.PagerDutyEventResponse;
import systems.comodal.pagerduty.event.data.adapters.PagerDutyEventAdapter;
import systems.comodal.pagerduty.exceptions.PagerDutyParseException;
import systems.comodal.pagerduty.exceptions.PagerDutyRequestException;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.http.HttpResponse;
import java.util.concurrent.ConcurrentLinkedQueue;

import static systems.comodal.jsoniter.JsonIterator.fieldEquals;

final class JsonIteratorPagerDutyEventAdapter implements PagerDutyEventAdapter {

  static final JsonIteratorPagerDutyEventAdapter INSTANCE = new JsonIteratorPagerDutyEventAdapter();

  private static final ConcurrentLinkedQueue<JsonIterator> JSON_ITERATOR_POOL = new ConcurrentLinkedQueue<>();

  private JsonIteratorPagerDutyEventAdapter() {
  }

  private static JsonIterator createInputStreamJsonIterator(final InputStream inputStream) {
    final var ji = JSON_ITERATOR_POOL.poll();
    return ji == null ? JsonIterator.parse(inputStream, 1_024) : ji.reset(inputStream);
    // final var responseBytes = slowRead(inputStream);
    // System.out.println(new String(responseBytes, java.nio.charset.StandardCharsets.UTF_8));
    // return JsonIterator.parse(responseBytes);
  }
//  private static byte[] slowRead(final InputStream inputStream) {
//    try (final var byteArrayOutputStream = new ByteArrayOutputStream()) {
//      inputStream.transferTo(byteArrayOutputStream);
//      return byteArrayOutputStream.toByteArray();
//    } catch (final IOException e) {
//      throw new UncheckedIOException(e);
//    }
//  }

  private static void returnJsonIterator(final JsonIterator ji) {
    JSON_ITERATOR_POOL.add(ji);
  }

  private static JsonIterator createInputStreamJsonIterator(final HttpResponse<InputStream> response) {
    return createInputStreamJsonIterator(response.body());
  }

  @Override
  public RuntimeException errorResponse(final HttpResponse<InputStream> response) {
    final var exception = PagerDutyRequestException.build(response);
    if (response.statusCode() == 429) {
      throw exception.message("Too many requests").create();
    }
    if (response.statusCode() == 404) {
      throw exception.message(response.request().uri() + " Not Found").create();
    }
    if (response.statusCode() == 400) {
      exception.message("Bad Request - Check that the JSON is valid.");
    } else if (response.statusCode() == 401) {
      exception.message("Unauthorized");
    } else if (response.statusCode() == 402) {
      exception.message("Payment Required");
    } else if (response.statusCode() == 403) {
      exception.message("Forbidden");
    } else if (response.statusCode() >= 500 && response.statusCode() < 600) {
      exception.message("Internal Server Error - the PagerDuty server experienced an error while processing the event.");
    }
    try (final var ji = createInputStreamJsonIterator(response)) {
      try {
        throw adaptException(exception, ji);
      } catch (final UncheckedIOException ioEx) {
        throw ioEx;
      } catch (final RuntimeException runtimeCause) {
        try {
          throw new PagerDutyParseException(response, runtimeCause, ji.currentBuffer());
        } catch (final RuntimeException ex) {
          throw new PagerDutyParseException(response, "Failed to adapt error response.", runtimeCause);
        }
      } finally {
        returnJsonIterator(ji);
      }
    } catch (final IOException ioEx) {
      throw new UncheckedIOException(ioEx);
    }
  }

  @Override
  public PagerDutyEventResponse adaptResponse(final HttpResponse<InputStream> response) {
    verifyHttpResponseCode(response);
    try (final var ji = createInputStreamJsonIterator(response)) {
      try {
        return adaptResponse(ji);
      } catch (final UncheckedIOException ioEx) {
        throw ioEx;
      } catch (final RuntimeException runtimeCause) {
        try {
          throw new PagerDutyParseException(response, runtimeCause, ji.currentBuffer());
        } catch (final RuntimeException ex) {
          throw new PagerDutyParseException(response, "Failed to adapt event response.", runtimeCause);
        }
      } finally {
        returnJsonIterator(ji);
      }
    } catch (final IOException ioEx) {
      throw new UncheckedIOException(ioEx);
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

  private PagerDutyEventResponse adaptResponse(final JsonIterator ji) {
    return ji.testObject(PagerDutyEventResponse.build(), EVENT_RESPONSE_PARSER).create();
  }

  private static final ContextFieldBufferPredicate<PagerDutyRequestException.Builder> EXCEPTION_PARSER = (exception, buf, offset, len, ji) -> {
    if (fieldEquals("status", buf, offset, len)) {
      exception.status(ji.readString());
    } else if (fieldEquals("message", buf, offset, len)) {
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

  private PagerDutyRequestException adaptException(final PagerDutyRequestException.Builder exception, final JsonIterator ji) {
    return ji.testObject(exception, EXCEPTION_PARSER).create();
  }
}
