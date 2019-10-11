# PagerDuty Event Client  [![Build](https://github.com/comodal/pagerduty-client/workflows/Gradle%20Check/badge.svg)](https://github.com/comodal/pagerduty-client/actions) [![Download](https://api.bintray.com/packages/comodal/libraries/pagerduty-event-client/images/download.svg)](https://bintray.com/comodal/libraries/pagerduty-event-client/_latestVersion)
 
This client aims to be compliant with the latest GA JDK and [PagerDuty Event API](https://v2.developer.pagerduty.com/docs/events-api-v2), currently JDK13 and V2 respectively.

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

## Project Configuration 

The core module [systems.comodal.pagerduty_event_client](systems.comodal.pagerduty_event_client/src/main/java/module-info.java) has dependencies only on `java.base` and `java.net.http`.  However, it relies on a [PagerDutyEventAdapterFactory](systems.comodal.pagerduty_event_client/src/main/java/systems/comodal/pagerduty/event/data/adapters/PagerDutyEventAdapterFactory.java) service which creates a [PagerDutyEventAdapter](systems.comodal.pagerduty_event_client/src/main/java/systems/comodal/pagerduty/event/data/adapters/PagerDutyEventAdapter.java) needed to parse JSON responses.  The module [systems.comodal.pagerduty_event_json_iterator_adapter](systems.comodal.pagerduty_event_json_iterator_adapter/src/main/java/module-info.java) is provided to serve this purpose and has a dependency on [systems.comodal.json_iterator](https://github.com/comodal/json-iterator).

```bash
> ./gradlew pagerduty-event-json-iterator-adapter:dependencies

+--- project :pagerduty-event-client
\--- systems.comodal:json-iterator:+
```

#### Example Gradle Configuration

```groovy
ext {
  pdEventClientVer = "+"
}

dependencies {
  compile "systems.comodal:pagerduty-event-json-iterator-adapter:$pdEventClientVer"
  compile "systems.comodal:pagerduty-event-client:$pdEventClientVer"
}
```