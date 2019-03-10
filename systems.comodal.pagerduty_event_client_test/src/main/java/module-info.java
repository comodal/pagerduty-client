import systems.comodal.test.pagerduty.EventClientTests;

module systems.comodal.pagerduty_event_client_test {
  requires transitive systems.comodal.pagerduty_event_client;
  requires systems.comodal.pagerduty_event_json_iterator_adapter;
  requires transitive jdk.httpserver;
  requires java.net.http;
  requires org.junit.jupiter.api;
  requires org.opentest4j;

  uses systems.comodal.test.pagerduty.ClientTest;

  provides systems.comodal.test.pagerduty.ClientTest with
      EventClientTests;
}
