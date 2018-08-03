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
  uses systems.comodal.pagerduty.client.PagerDutyHttpClientProvider;
  uses systems.comodal.pagerduty.event.data.adapters.PagerDutyEventAdapterFactory;

  provides systems.comodal.pagerduty.event.client.PagerDutyEventClientFactory with
      systems.comodal.pagerduty.event.client.PagerDutyHttpEventClientFactory;

  provides systems.comodal.pagerduty.client.PagerDutyHttpClientProvider with
      systems.comodal.pagerduty.client.DefaultPagerDutyHttpClientProvider;

  provides systems.comodal.pagerduty.event.data.adapters.PagerDutyEventAdapterFactory with
      systems.comodal.pagerduty.event.data.adapters.JsonIteratorPagerDutyEventAdapterFactory;
}
