package systems.comodal.pagerduty.event.data.adapters;

import java.util.ServiceLoader;

import static systems.comodal.pagerduty.config.PagerDutySysProp.PAGER_DUTY_EVENT_ADAPTER_FACTORY;
import static systems.comodal.pagerduty.config.ServiceUtil.loadService;

public interface PagerDutyEventAdapterFactory {

  static PagerDutyEventAdapter load() {
    return loadService(ServiceLoader.load(PagerDutyEventAdapterFactory.class),
        PAGER_DUTY_EVENT_ADAPTER_FACTORY.getStringProperty().orElse(null)).create();
  }

  PagerDutyEventAdapter create();
}
