package systems.comodal.test.pagerduty;

import com.sun.net.httpserver.HttpHandler;
import systems.comodal.pagerduty.event.client.PagerDutyEventClient;
import systems.comodal.pagerduty.event.data.*;

import java.time.ZonedDateTime;
import java.util.function.BiConsumer;

import static java.time.ZoneOffset.UTC;
import static org.junit.jupiter.api.Assertions.*;
import static systems.comodal.pagerduty.config.PagerDutySysProp.PAGER_DUTY_EVENT_CLIENT_AUTH_TOKEN;
import static systems.comodal.pagerduty.config.PagerDutySysProp.PAGER_DUTY_EVENT_CLIENT_ROUTING_KEY;

public final class EventClientTests implements EventClientTest {

  @Override
  public void createContext(final String clientName,
                            final BiConsumer<String, HttpHandler> server) {
    final var authToken = PAGER_DUTY_EVENT_CLIENT_AUTH_TOKEN.getMandatoryStringProperty(clientName);
    final var routingKey = PAGER_DUTY_EVENT_CLIENT_ROUTING_KEY.getMandatoryStringProperty(clientName);
    server.accept("/v2/enqueue", httpExchange -> {
      assertEquals("POST", httpExchange.getRequestMethod());

      assertNull(httpExchange.getRequestURI().getQuery());

      final var headers = httpExchange.getRequestHeaders();
      assertEquals("application/json", headers.getFirst("Content-Type"));
      assertEquals("Token token=" + authToken, headers.getFirst("Authorization"));
      assertEquals("application/vnd.pagerduty+json;version=2", headers.getFirst("Accept"));

      final var body = new String(httpExchange.getRequestBody().readAllBytes());
      if (body.startsWith("{\"event_action\":\"trigger")) {
        assertEquals("{\"event_action\":\"trigger\",\"payload\":" +
            "{\"summary\":\"test-summary\",\"source\":\"test-source\",\"severity\":\"critical\",\"timestamp\":\"2018-08-01T00:00Z\",\"component\":\"test-component\",\"group\":\"test-group\",\"class\":\"test-class\"," +
            "\"custom_details\":{\"test-num-metric\":1,\"test-string-metric\":\"val\"}" +
            "},\"routing_key\":\"" + routingKey +
            "\",\"client\":\"" + clientName +
            "\",\"images\":[{\"src\":\"https://www.pagerduty.com/wp-content/uploads/2016/05/pagerduty-logo-green.png\",\"href\":\"https://www.pagerduty.com/\",\"alt\":\"pagerduty\"}]" +
            ",\"links\":[{\"href\":\"https://github.com/comodal/pagerduty-client\",\"text\":\"Github pagerduty-client\"}]}", body);
        writeResponse(httpExchange, "{\"status\":\"success\",\"message\":\"Event processed\",\"dedup_key\":\"030a787c595b4e2cb7d7702c0c978996\"}");
        return;
      }

      if (body.endsWith("acknowledge\"}")) {
        assertEquals("{\"routing_key\":\"" + routingKey + "\",\"dedup_key\":\"030a787c595b4e2cb7d7702c0c978996\",\"event_action\":\"acknowledge\"}", body);
        writeResponse(httpExchange, "{\"status\":\"success\",\"message\":\"Event processed\",\"dedup_key\":\"030a787c595b4e2cb7d7702c0c978996\"}");
        return;
      }

      if (body.endsWith("resolve\"}")) {
        assertEquals("{\"routing_key\":\"" + routingKey + "\",\"dedup_key\":\"030a787c595b4e2cb7d7702c0c978996\",\"event_action\":\"resolve\"}", body);
        writeResponse(httpExchange, "{\"status\":\"success\",\"message\":\"Event processed\",\"dedup_key\":\"030a787c595b4e2cb7d7702c0c978996\"}");
        return;
      }

      fail("Invalid request body: " + body);
    });
  }

  @Override
  public void test(final PagerDutyEventClient client) {
    final var payload = PagerDutyEventPayload.build()
        .summary("test-summary")
        .source("test-source")
        .severity(PagerDutySeverity.critical)
        .timestamp(ZonedDateTime.of(2018, 8, 1, 0, 0, 0, 0, UTC))
        .component("test-component")
        .group("test-group")
        .type("test-class")
        .customDetails("test-num-metric", 1)
        .customDetails("test-string-metric", "val")
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

    final var response = client.triggerDefaultRouteEvent(payload).join();
    validateResponse(response);

    final var ackResponse = client.acknowledgeEvent(response.getDedupeKey()).join();
    validateResponse(ackResponse);

    final var resolveResponse = client.resolveEvent(response.getDedupeKey()).join();
    validateResponse(resolveResponse);
  }

  private void validateResponse(final PagerDutyEventResponse response) {
    assertEquals("success", response.getStatus());
    assertEquals("Event processed", response.getMessage());
    assertEquals("030a787c595b4e2cb7d7702c0c978996", response.getDedupeKey());
  }
}
