package systems.comodal.pagerduty.event.service;

import systems.comodal.pagerduty.event.client.PagerDutyEventClient;
import systems.comodal.pagerduty.event.data.PagerDutyEventPayload;

final class PagerDutyServiceBuilder implements PagerDutyService.Builder {

  private PagerDutyEventClient client;
  private PagerDutyEventPayload eventPrototype;

  PagerDutyServiceBuilder() {
  }

  @Override
  public PagerDutyService create() {
    return new PagerDutyServiceVal(client, eventPrototype);
  }

  @Override
  public PagerDutyService.Builder client(final PagerDutyEventClient client) {
    this.client = client;
    return this;
  }

  @Override
  public PagerDutyService.Builder eventPrototype(final PagerDutyEventPayload eventPrototype) {
    this.eventPrototype = eventPrototype;
    return this;
  }
}
