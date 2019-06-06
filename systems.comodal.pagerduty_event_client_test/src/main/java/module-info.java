import systems.comodal.test.pagerduty.EventClientTests;

module systems.comodal.pagerduty_event_client_test {
  requires transitive systems.comodal.pagerduty_event_client;
  requires transitive jdk.httpserver;
  requires org.junit.jupiter.api;

  uses systems.comodal.test.pagerduty.ClientTest;

  provides systems.comodal.test.pagerduty.ClientTest with
      EventClientTests;
}
