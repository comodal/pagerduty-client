package systems.comodal.test.pagerduty;

import com.sun.net.httpserver.HttpHandler;
import systems.comodal.pagerduty.event.client.PagerDutyEventClient;
import systems.comodal.pagerduty.event.data.PagerDutyEventPayload;
import systems.comodal.pagerduty.event.data.PagerDutyEventResponse;
import systems.comodal.pagerduty.event.data.PagerDutySeverity;

import java.time.ZonedDateTime;
import java.util.function.BiConsumer;

import static java.time.ZoneOffset.UTC;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
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
      assertEquals("{\"event_action\":\"trigger\",\"payload\":" +
          "{\"summary\":\"test-summary\",\"source\":\"test-source\",\"severity\":\"critical\",\"timestamp\":\"2018-08-01T00:00Z\",\"component\":\"test-component\",\"group\":\"test-group\",\"class\":\"test-class\"," +
          "\"custom_details\":{\"test-num-metric\":1,\"test-string-metric\":\"val\"}" +
          "},\"routing_key\":\"" + routingKey +
          "\",\"client\":\"" + clientName +
          "\"}", body);
      writeResponse(httpExchange, "{\"status\":\"success\",\"message\":\"Event processed\",\"dedup_key\":\"dedupekey\"}");
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
        .create();
    final var response = client.triggerDefaultRouteEvent(payload).join();
    validateTriggerResponse(response);
  }

  private void validateTriggerResponse(final PagerDutyEventResponse response) {
    assertEquals("success", response.getStatus());
    assertEquals("Event processed", response.getMessage());
    assertEquals("dedupekey", response.getDedupeKey());
  }
}
