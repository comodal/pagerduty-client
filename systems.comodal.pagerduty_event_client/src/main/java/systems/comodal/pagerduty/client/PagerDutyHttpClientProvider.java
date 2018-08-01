package systems.comodal.pagerduty.client;

import jdk.incubator.http.HttpClient;

import java.util.ServiceLoader;

import static systems.comodal.pagerduty.config.PagerDutySysProp.PAGER_DUTY_HTTP_CLIENT_PROVIDER;
import static systems.comodal.pagerduty.config.ServiceUtil.loadService;

public interface PagerDutyHttpClientProvider {

  static HttpClient load() {
    return loadService(ServiceLoader.load(PagerDutyHttpClientProvider.class),
        PAGER_DUTY_HTTP_CLIENT_PROVIDER.getStringProperty().orElse(null)).get();
  }

  HttpClient get();
}
