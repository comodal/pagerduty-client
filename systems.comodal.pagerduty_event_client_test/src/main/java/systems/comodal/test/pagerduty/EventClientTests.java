package systems.comodal.test.pagerduty;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import systems.comodal.pagerduty.event.client.PagerDutyEventClient;
import systems.comodal.pagerduty.event.data.*;

import java.time.ZonedDateTime;
import java.util.UUID;
import java.util.function.BiConsumer;

import static java.time.ZoneOffset.UTC;
import static org.junit.jupiter.api.Assertions.*;

public final class EventClientTests implements EventClientTest {

  private final String dedupKey = UUID.randomUUID().toString();
  private final String response = String.format("""
      {"status":"success","message":"Event processed","dedup_key":"%s"}""", dedupKey);

  @Override
  public void createContext(final HttpServer httpServer,
                            final BiConsumer<String, HttpHandler> server) {
    final int port = httpServer.getAddress().getPort();
    final var clientName = "test-" + port;
    final var authToken = "auth-token-" + port;
    final var routingKey = "routing-key-" + port;

    server.accept("/v2/enqueue", httpExchange -> {
      assertEquals("POST", httpExchange.getRequestMethod());

      assertNull(httpExchange.getRequestURI().getQuery());

      final var headers = httpExchange.getRequestHeaders();
      assertEquals("application/json", headers.getFirst("Content-Type"));
      assertEquals("Token token=" + authToken, headers.getFirst("Authorization"));
      assertEquals("application/vnd.pagerduty+json;version=2", headers.getFirst("Accept"));
      assertEquals(routingKey, headers.getFirst("X-Routing-Key"));

      final var body = new String(httpExchange.getRequestBody().readAllBytes());

      if (body.startsWith("{\"event_action\":\"trigger")) {
        final var expected = String.format("""
                {
                "event_action":"trigger",
                "routing_key":"%s",
                "dedup_key":"%s",
                "payload":{"summary":"test-summary","source":"test-source","severity":"critical","timestamp":"2018-08-01T02:03:04Z","component":"test-component","group":"test-group","class":"test-class","custom_details":{"test-num-metric":1,"test-string-metric":"val"}},
                "client":"%s",
                "images":[{"src":"https://www.pagerduty.com/wp-content/uploads/2016/05/pagerduty-logo-green.png","href":"https://www.pagerduty.com/","alt":"pagerduty"}],
                "links":[{"href":"https://github.com/comodal/pagerduty-client","text":"Github pagerduty-client"}]
                }""".replaceAll("\\n", ""),
            routingKey, dedupKey, clientName);
        assertEquals(expected, body);
        writeResponse(httpExchange, response);
      } else if (body.endsWith("acknowledge\"}")) {
        final var expected = String.format("""
            {"routing_key":"%s","dedup_key":"%s","event_action":"acknowledge"}""", routingKey, dedupKey);
        assertEquals(expected, body);
        writeResponse(httpExchange, response);
      } else if (body.endsWith("resolve\"}")) {
        final var expected = String.format("""
            {"routing_key":"%s","dedup_key":"%s","event_action":"resolve"}""", routingKey, dedupKey);
        assertEquals(expected, body);
        writeResponse(httpExchange, response);
      } else {
        fail("Invalid request body: " + body);
      }
    });
  }

  @Override
  public void test(final PagerDutyEventClient client) {
    final var payload = PagerDutyEventPayload.build()
        .dedupKey(dedupKey)
        .summary("test-summary")
        .source("test-source")
        .severity(PagerDutySeverity.critical)
        .timestamp(ZonedDateTime.of(2018, 8, 1, 2, 3, 4, 0, UTC))
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

    final var ackResponse = client.acknowledgeEvent(response.getDedupKey()).join();
    validateResponse(ackResponse);

    final var resolveResponse = client.resolveEvent(response.getDedupKey()).join();
    validateResponse(resolveResponse);
  }

  private void validateResponse(final PagerDutyEventResponse response) {
    assertEquals("success", response.getStatus());
    assertEquals("Event processed", response.getMessage());
    assertEquals(dedupKey, response.getDedupKey());
  }
}
