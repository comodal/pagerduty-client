package systems.comodal.pagerduty.exceptions;

import java.net.http.HttpResponse;

public interface PagerDutyClientException {

  boolean canBeRetried();

  HttpResponse<?> getHttpResponse();
}
