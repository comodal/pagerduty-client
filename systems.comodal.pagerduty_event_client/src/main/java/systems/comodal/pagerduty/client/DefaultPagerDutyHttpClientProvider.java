package systems.comodal.pagerduty.client;

import jdk.incubator.http.HttpClient;

import static jdk.incubator.http.HttpClient.Version.HTTP_2;

public final class DefaultPagerDutyHttpClientProvider implements PagerDutyHttpClientProvider {

  @Override
  public HttpClient get() {
    return HttpClient.newBuilder().version(HTTP_2).build();
  }
}
