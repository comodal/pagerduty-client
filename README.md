# PagerDuty Event Client [![Build Status](https://travis-ci.org/comodal/pagerduty-client.svg?branch=master)](https://travis-ci.org/comodal/pagerduty-client) [![Download](https://api.bintray.com/packages/comodal/libraries/pagerduty-event-client/images/download.svg)](https://bintray.com/comodal/libraries/pagerduty-event-client/_latestVersion)

This client aims to be compliant with the latest GA JDK and [PagerDuty Event API](https://v2.developer.pagerduty.com/docs/events-api-v2), currently V2.

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
