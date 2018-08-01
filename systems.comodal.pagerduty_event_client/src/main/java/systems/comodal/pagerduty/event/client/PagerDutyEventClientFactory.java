package systems.comodal.pagerduty.event.client;

import java.util.ServiceLoader;

import static systems.comodal.pagerduty.config.PagerDutySysProp.PAGER_DUTY_EVENT_CLIENT_FACTORY;
import static systems.comodal.pagerduty.config.ServiceUtil.loadService;

public interface PagerDutyEventClientFactory {

  static PagerDutyEventClient load(final String clientName) {
    return loadService(ServiceLoader.load(PagerDutyEventClientFactory.class),
        PAGER_DUTY_EVENT_CLIENT_FACTORY.getStringProperty().orElse(null))
        .create(clientName);
  }

  PagerDutyEventClient create(final String clientName);
}
