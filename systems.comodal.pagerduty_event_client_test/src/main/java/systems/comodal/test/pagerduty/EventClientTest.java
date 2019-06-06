package systems.comodal.test.pagerduty;

import com.sun.net.httpserver.HttpServer;
import systems.comodal.pagerduty.event.client.PagerDutyEventClient;

public interface EventClientTest extends ClientTest {

  default void test(final HttpServer httpServer) {
    final int port = httpServer.getAddress().getPort();
    final var client = PagerDutyEventClient.build()
        .defaultClientName("test-" + port)
        .pagerDutyUri("http://localhost:" + port)
        .defaultRoutingKey("routing-key-" + port)
        .authToken("auth-token-" + port)
        .create();
    test(client);
  }

  void test(final PagerDutyEventClient client);
}
