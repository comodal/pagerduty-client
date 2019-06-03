package systems.comodal.pagerduty.event.client;

import systems.comodal.pagerduty.event.data.PagerDutyEventPayload;
import systems.comodal.pagerduty.event.data.PagerDutyEventResponse;
import systems.comodal.pagerduty.event.data.PagerDutyImageRef;
import systems.comodal.pagerduty.event.data.PagerDutyLinkRef;
import systems.comodal.pagerduty.event.data.adapters.PagerDutyEventAdapter;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static java.net.http.HttpRequest.BodyPublishers.ofString;
import static java.net.http.HttpResponse.BodyHandlers.ofInputStream;
import static java.nio.charset.StandardCharsets.UTF_8;

final class PagerDutyHttpEventClient implements PagerDutyEventClient {

  private final String defaultClientName;
  private final String defaultClientUrl;
  private final String defaultRoutingKey;
  private final String authorizationHeader;
  private final URI eventUri;
  private final HttpClient httpClient;
  private final PagerDutyEventAdapter adapter;

  PagerDutyHttpEventClient(final String defaultClientName,
                           final String defaultClientUrl,
                           final String defaultRoutingKey,
                           final String authorizationHeader,
                           final URI eventUri,
                           final HttpClient httpClient,
                           final PagerDutyEventAdapter adapter) {
    this.defaultClientName = defaultClientName;
    this.defaultClientUrl = defaultClientUrl;
    this.defaultRoutingKey = defaultRoutingKey;
    this.authorizationHeader = authorizationHeader;
    this.eventUri = eventUri;
    this.httpClient = httpClient;
    this.adapter = adapter;
  }

  @Override
  public String getDefaultClientName() {
    return defaultClientName;
  }

  @Override
  public String getDefaultClientUrl() {
    return defaultClientUrl;
  }

  @Override
  public String getDefaultRoutingKey() {
    return defaultRoutingKey;
  }

  @Override
  public CompletableFuture<PagerDutyEventResponse> acknowledgeEvent(final String routingKey, final String dedupKey) {
    return eventAction(routingKey, dedupKey, "\",\"event_action\":\"acknowledge\"}");
  }

  @Override
  public CompletableFuture<PagerDutyEventResponse> resolveEvent(final String routingKey, final String dedupKey) {
    return eventAction(routingKey, dedupKey, "\",\"event_action\":\"resolve\"}");
  }

  private CompletableFuture<PagerDutyEventResponse> eventAction(final String routingKey,
                                                                final String dedupKey,
                                                                final String actionBody) {
    Objects.requireNonNull(routingKey, "Routing key is a required field.");
    Objects.requireNonNull(dedupKey, "De-duplication key is a required field.");
    final var json = "{\"routing_key\":\"" + routingKey + "\",\"dedup_key\":\"" + dedupKey + actionBody;
    return createAndSendRequest(routingKey, json);
  }

  @Override
  public CompletableFuture<PagerDutyEventResponse> triggerEvent(final String clientName,
                                                                final String clientUrl,
                                                                final String routingKey,
                                                                final PagerDutyEventPayload payload) {
    Objects.requireNonNull(routingKey, "Routing key is a required field.");
    final var payloadJson = payload.getPayloadJson();
    final var imagesJson = payload.getImages().isEmpty()
        ? ""
        : payload.getImages().stream().map(PagerDutyImageRef::toJson)
        .collect(Collectors.joining(",", ",\"images\":[", "]"));
    final var linksJson = payload.getLinks().isEmpty()
        ? ""
        : payload.getLinks().stream().map(PagerDutyLinkRef::toJson)
        .collect(Collectors.joining(",", ",\"links\":[", "]"));
    final var json = "{\"event_action\":\"trigger\",\"payload\":" + payloadJson
        + ",\"routing_key\":\"" + routingKey + '"'
        + ",\"dedup_key\":\"" + payload.getDedupKey() + '"'
        + ",\"client\":\"" + clientName + '"'
        + (clientUrl == null ? "" : ",\"client_url\":\"" + clientUrl + '"')
        + imagesJson
        + linksJson
        + '}';
    return createAndSendRequest(routingKey, json);
  }

  private HttpRequest createRequest(final String routingKey, final String jsonBody) {
    return HttpRequest.newBuilder(eventUri)
        .headers(
            "Authorization", authorizationHeader,
            "Accept", "application/vnd.pagerduty+json;version=2",
            "Content-Type", "application/json",
            "X-Routing-Key", routingKey)
        .POST(ofString(jsonBody, UTF_8)).build();
  }

  private CompletableFuture<PagerDutyEventResponse> createAndSendRequest(final String routingKey, final String jsonBody) {
    return httpClient.sendAsync(createRequest(routingKey, jsonBody), ofInputStream())
        .thenApplyAsync(adapter::adaptResponse);
  }

  @Override
  public String toString() {
    return "PagerDutyHttpEventClient{defaultClientName='" + defaultClientName + '\'' +
        ", defaultClientUrl='" + defaultClientUrl + '\'' +
        ", defaultRoutingKey='" + defaultRoutingKey + '\'' +
        ", eventUri=" + eventUri + '}';
  }
}
