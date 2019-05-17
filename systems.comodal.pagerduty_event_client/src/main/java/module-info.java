module systems.comodal.pagerduty_event_client {
  requires java.net.http;

  exports systems.comodal.pagerduty.event.client;
  exports systems.comodal.pagerduty.event.data;
  exports systems.comodal.pagerduty.event.data.adapters;
  exports systems.comodal.pagerduty.event.service;
  exports systems.comodal.pagerduty.exceptions;

  uses systems.comodal.pagerduty.event.data.adapters.PagerDutyEventAdapterFactory;
}
