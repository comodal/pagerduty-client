package systems.comodal.pagerduty.event.data.adapters;

import systems.comodal.pagerduty.event.data.PagerDutyEventResponse;

import java.net.http.HttpResponse;

public interface PagerDutyEventAdapter {

  default void verifyHttpResponseCode(final HttpResponse<byte[]> response) {
    final var code = response.statusCode();
    if (code < 200 || code >= 300) {
      throw errorResponse(response);
    }
  }

  RuntimeException errorResponse(final HttpResponse<byte[]> response);

  PagerDutyEventResponse adaptResponse(final HttpResponse<byte[]> response);
}
