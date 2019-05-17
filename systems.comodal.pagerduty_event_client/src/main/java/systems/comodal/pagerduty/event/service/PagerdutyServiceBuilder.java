package systems.comodal.pagerduty.event.service;

import systems.comodal.pagerduty.event.client.PagerDutyEventClient;
import systems.comodal.pagerduty.event.data.PagerDutyEventPayload;

final class PagerdutyServiceBuilder implements PagerdutyService.Builder {

  private PagerDutyEventClient client;
  private PagerDutyEventPayload eventPrototype;

  PagerdutyServiceBuilder() {
  }

  @Override
  public PagerdutyService create() {
    return new PagerdutyServiceVal(client, eventPrototype);
  }

  @Override
  public PagerdutyService.Builder clientName(final PagerDutyEventClient client) {
    this.client = client;
    return this;
  }

  @Override
  public PagerdutyService.Builder eventPrototype(final PagerDutyEventPayload eventPrototype) {
    this.eventPrototype = eventPrototype;
    return this;
  }
}
