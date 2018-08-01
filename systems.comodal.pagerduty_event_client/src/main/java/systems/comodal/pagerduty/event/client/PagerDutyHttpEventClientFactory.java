package systems.comodal.pagerduty.event.client;

public final class PagerDutyHttpEventClientFactory implements PagerDutyEventClientFactory {

  @Override
  public PagerDutyEventClient create(final String clientName) {
    return new PagerDutyHttpEventClient(clientName);
  }
}
