import systems.comodal.pagerduty.client.PagerDutyHttpClientProvider;

module systems.comodal.pagerduty_event_client {
  // requires java.net.http;
  requires jdk.incubator.httpclient;
  requires systems.comodal.json_iterator;

  exports systems.comodal.pagerduty.client;
  exports systems.comodal.pagerduty.config;
  exports systems.comodal.pagerduty.event.client;
  exports systems.comodal.pagerduty.event.data;
  exports systems.comodal.pagerduty.event.data.adapters;

  uses systems.comodal.pagerduty.event.client.PagerDutyEventClientFactory;
  uses PagerDutyHttpClientProvider;
  uses systems.comodal.pagerduty.event.data.adapters.PagerDutyEventAdapterFactory;
}
