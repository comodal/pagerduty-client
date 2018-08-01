package systems.comodal.pagerduty.event.data.adapters;

import jdk.incubator.http.HttpResponse;
import systems.comodal.pagerduty.event.data.PagerDutyEventResponse;

import java.io.InputStream;

public interface PagerDutyEventAdapter {

  default void verifyHttpResponseCode(final HttpResponse<InputStream> response) {
    final var code = response.statusCode();
    if (code < 200 || code >= 300) {
      throw errorResponse(response);
    }
  }

  RuntimeException errorResponse(final HttpResponse<InputStream> response);

  PagerDutyEventResponse adaptResponse(final HttpResponse<InputStream> response);
}
