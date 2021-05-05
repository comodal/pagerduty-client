# PagerDuty Event Client

This client aims to be compliant with the latest version of the [PagerDuty Event API](https://v2.developer.pagerduty.com/docs/events-api-v2), currently V2.

## Hello Event Trigger

```java
var client = PagerDutyEventClient.build()
    .defaultClientName("CLIENT_NAME")
    .defaultRoutingKey("INTEGRATION_KEY")
    .authToken("AUTH_TOKEN")
    .create();

var payload = PagerDutyEventPayload.build()
    .summary("summary")
    .source("source")
    .severity(PagerDutySeverity.critical)
    .timestamp(ZonedDateTime.now(UTC))
    .component("component")
    .group("group")
    .type("class")
    .customDetails("num-metric", 1)
    .customDetails("string-metric", "val")
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

var triggerResponse = client.triggerDefaultRouteEvent(payload).join();
System.out.println(triggerResponse);

var resolveResponse = client.resolveEvent(triggerResponse.getDedupeKey()).join();
System.out.println(resolveResponse);
```

## Library Layout 

The core module [systems.comodal.pagerduty_event_client](systems.comodal.pagerduty_event_client/src/main/java/module-info.java) has direct dependencies on `java.base`, `java.net.http` and a [PagerDutyEventAdapterFactory](systems.comodal.pagerduty_event_client/src/main/java/systems/comodal/pagerduty/event/data/adapters/PagerDutyEventAdapterFactory.java).  The module [systems.comodal.pagerduty_event_json_iterator_adapter](systems.comodal.pagerduty_event_json_iterator_adapter/src/main/java/module-info.java) provides this factory and has a dependency on [systems.comodal.json_iterator](https://github.com/comodal/json-iterator).  This separation is intended to make it easy to provide your own json parser if desired.

```bash
> ./gradlew pagerduty-event-json-iterator-adapter:dependencies

+--- project :pagerduty-event-client
\--- systems.comodal:json-iterator:+
```

## Example Gradle Configuration

```groovy
ext {
  pagerDutyClient = "+"
}

dependencies {
  implementation "systems.comodal:pagerduty-event-json-iterator-adapter:$pagerDutyClient"
  implementation "systems.comodal:pagerduty-event-client:$pagerDutyClient"
}
```