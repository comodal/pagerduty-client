module systems.comodal.pagerduty_event_json_iterator_adapter {
  requires java.net.http;
  requires systems.comodal.pagerduty_event_client;
  requires systems.comodal.json_iterator;

  provides systems.comodal.pagerduty.event.data.adapters.PagerDutyEventAdapterFactory with
    systems.comodal.pagerduty.event.client.jsoniter.adapter.JsonIteratorPagerDutyEventAdapterFactory;
}
