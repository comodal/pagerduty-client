# PagerDuty Event Client

#### Configuration
Configuration is handled via system properties namespaced by a client name.  See [PagerDutySysProp.java](systems.comodal.pagerduty_event_client/src/main/java/systems/comodal/pagerduty/config/PagerDutySysProp.java) for all available system properties.

 - -Dsystems.comodal.pagerduty.{CLIENT_NAME}.event_client_routing_key={INTEGRATION_KEY}
 - -Dsystems.comodal.pagerduty.{CLIENT_NAME}.event_client_auth_token={AUTH_TOKEN}



#### Hello Event Trigger

```java
var clientName = "client-name";
var client = PagerDutyEventClientFactory.load(clientName);

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
    .create();

var triggerResponse = client.triggerDefaultRouteEvent(payload).join();
System.out.println(triggerResponse);

var resolveResponse = client.resolveEvent(triggerResponse.getDedupeKey()).join();
System.out.println(resolveResponse);
```
