package systems.comodal.pagerduty.exceptions;

import java.net.http.HttpResponse;
import java.util.List;

public interface PagerDutyClientException {

  boolean canBeRetried();

  HttpResponse<?> getHttpResponse();

  long getErrorCode();

  List<String> getErrors();
}
