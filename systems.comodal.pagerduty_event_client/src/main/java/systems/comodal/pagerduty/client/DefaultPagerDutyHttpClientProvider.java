package systems.comodal.pagerduty.client;

import java.net.http.HttpClient;

import static java.net.http.HttpClient.Version.HTTP_2;

public final class DefaultPagerDutyHttpClientProvider implements PagerDutyHttpClientProvider {

  @Override
  public HttpClient get() {
    return HttpClient.newBuilder().version(HTTP_2).build();
  }
}
