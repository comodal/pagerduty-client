package systems.comodal.pagerduty.event.data.adapters;

public final class JsonIteratorPagerDutyEventAdapterFactory implements PagerDutyEventAdapterFactory {

  @Override
  public PagerDutyEventAdapter create() {
    return JsonIteratorPagerDutyEventAdapter.INSTANCE;
  }
}
