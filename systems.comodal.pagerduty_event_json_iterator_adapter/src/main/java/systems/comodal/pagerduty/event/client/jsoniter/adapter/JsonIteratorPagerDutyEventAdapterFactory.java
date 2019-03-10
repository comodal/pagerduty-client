package systems.comodal.pagerduty.event.client.jsoniter.adapter;

import systems.comodal.pagerduty.event.data.adapters.PagerDutyEventAdapter;
import systems.comodal.pagerduty.event.data.adapters.PagerDutyEventAdapterFactory;

public final class JsonIteratorPagerDutyEventAdapterFactory implements PagerDutyEventAdapterFactory {

  @Override
  public PagerDutyEventAdapter create() {
    return JsonIteratorPagerDutyEventAdapter.INSTANCE;
  }
}
