package systems.comodal.test.pagerduty;

import com.sun.net.httpserver.HttpServer;
import systems.comodal.pagerduty.event.client.PagerDutyEventClient;
import systems.comodal.pagerduty.event.client.PagerDutyEventClientFactory;

import java.io.IOException;

import static systems.comodal.pagerduty.config.PagerDutySysProp.*;

public interface EventClientTest extends ClientTest<PagerDutyEventClient> {

  default void configureClient(final String clientName, final HttpServer httpServer) {
    final int port = httpServer.getAddress().getPort();
    PAGER_DUTY_EVENT_CLIENT_ENDPOINT.set(clientName, "http://localhost:" + port);
    PAGER_DUTY_EVENT_CLIENT_ROUTING_KEY.set(clientName, "routing-key-" + port);
    PAGER_DUTY_EVENT_CLIENT_AUTH_TOKEN.set(clientName, "auth-token-" + port);
  }

  default PagerDutyEventClient createClient(final String clientName) {
    return PagerDutyEventClientFactory.load(clientName);
  }

  void test(final PagerDutyEventClient client) throws IOException;
}
