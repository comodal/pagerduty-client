package systems.comodal.test.pagerduty;

import systems.comodal.pagerduty.event.client.PagerDutyEventClient;
import systems.comodal.pagerduty.event.data.*;

import java.math.BigDecimal;
import java.math.BigInteger;
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

    final var bigInteger = new BigInteger("20988936657440586486151264256610222593863921");
    final var payload = PagerDutyEventPayload.build()
        .summary("test-summary")
        .source("test-source")
        .severity(PagerDutySeverity.critical)
        .timestamp(ZonedDateTime.now(UTC))
        .component("test-component")
        .group("test-group")
        .type("test-class")
        .customDetails("test-num-metric", 1)
        .customDetails("test-boolean", true)
        .customDetails("test-string", "val")
        .customDetails("test-nested-json", """
            {"test": "json"}""")
        .customDetails("test-big-decimal", new BigDecimal(bigInteger).add(BigDecimal.valueOf(0.123456789)))
        .customDetails("test-big-integer", bigInteger)
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

    final var triggerResponseFuture = client.triggerDefaultRouteEvent(payload);

    final var changeEventPayload = PagerDutyChangeEventPayload.build(payload).create();
    final var changeEventResponseFuture = client.defaultRouteChangeEvent(changeEventPayload);

    final var triggerResponse = triggerResponseFuture.join();
    System.out.println(triggerResponse);

    final var ackResponse = client.acknowledgeEvent(triggerResponse.getDedupKey()).join();
    System.out.println(ackResponse);

    final var resolveResponse = client.resolveEvent(triggerResponse.getDedupKey()).join();
    System.out.println(resolveResponse);

    final var changeEventResponse = changeEventResponseFuture.join();
    System.out.println(changeEventResponse);
  }
}
