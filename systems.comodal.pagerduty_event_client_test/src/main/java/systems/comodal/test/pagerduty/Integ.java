package systems.comodal.test.pagerduty;

import systems.comodal.pagerduty.event.client.PagerDutyEventClientFactory;
import systems.comodal.pagerduty.event.data.PagerDutyEventPayload;
import systems.comodal.pagerduty.event.data.PagerDutyLinkRef;
import systems.comodal.pagerduty.event.data.PagerDutySeverity;

import java.time.ZonedDateTime;

import static java.time.ZoneOffset.UTC;
import static systems.comodal.pagerduty.config.PagerDutySysProp.PAGER_DUTY_EVENT_CLIENT_AUTH_TOKEN;
import static systems.comodal.pagerduty.config.PagerDutySysProp.PAGER_DUTY_EVENT_CLIENT_ROUTING_KEY;

public final class Integ {

  private Integ() {
  }

  public static void main(final String[] args) {
    final var clientName = "comodal-systems-integ";
    final var integrationKey = "";
    final var authToken = "";
    PAGER_DUTY_EVENT_CLIENT_ROUTING_KEY.set(clientName, integrationKey);
    PAGER_DUTY_EVENT_CLIENT_AUTH_TOKEN.set(clientName, authToken);

    final var client = PagerDutyEventClientFactory.load(clientName);

    var payload = PagerDutyEventPayload.build()
        .summary("test-summary")
        .source("test-source")
        .severity(PagerDutySeverity.critical)
        .timestamp(ZonedDateTime.now(UTC))
        .component("test-component")
        .group("test-group")
        .type("test-class")
        .customDetails("test-num-metric", 1)
        .customDetails("test-string-metric", "val")
        .link(PagerDutyLinkRef.build()
            .href("https://github.com/comodal/pagerduty-client")
            .text("Github pagerduty-client").create())
        .create();

    final var triggerResponse = client.triggerDefaultRouteEvent(payload).join();
    System.out.println(triggerResponse);

    final var ackResponse = client.acknowledgeEvent(triggerResponse.getDedupeKey()).join();
    System.out.println(ackResponse);

    final var resolveResponse = client.resolveEvent(triggerResponse.getDedupeKey()).join();
    System.out.println(resolveResponse);
  }
}
