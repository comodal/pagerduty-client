package systems.comodal.test.pagerduty;

import systems.comodal.pagerduty.event.client.PagerDutyEventClient;
import systems.comodal.pagerduty.event.data.PagerDutyEventPayload;
import systems.comodal.pagerduty.event.data.PagerDutyImageRef;
import systems.comodal.pagerduty.event.data.PagerDutyLinkRef;
import systems.comodal.pagerduty.event.data.PagerDutySeverity;

import java.time.ZonedDateTime;

import static java.time.ZoneOffset.UTC;

public final class Integ {

  private Integ() {
  }

  public static void main(final String[] args) {
    final var client = PagerDutyEventClient.build()
        .defaultClientName("comodal-systems-integ")
        .defaultRoutingKey("INTEGRATION_KEY")
        .authToken("AUTH_TOKEN")
        .create();

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
            .text("Github pagerduty-client")
            .create())
        .image(PagerDutyImageRef.build()
            .src("https://www.pagerduty.com/wp-content/uploads/2016/05/pagerduty-logo-green.png")
            .href("https://www.pagerduty.com/")
            .alt("pagerduty")
            .create())
        .create();

    final var triggerResponse = client.triggerDefaultRouteEvent(payload).join();
    System.out.println(triggerResponse);

    final var ackResponse = client.acknowledgeEvent(triggerResponse.getDedupKey()).join();
    System.out.println(ackResponse);

    final var resolveResponse = client.resolveEvent(triggerResponse.getDedupKey()).join();
    System.out.println(resolveResponse);
  }
}
