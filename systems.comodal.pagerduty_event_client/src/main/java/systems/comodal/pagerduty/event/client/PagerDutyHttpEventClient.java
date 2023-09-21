package systems.comodal.pagerduty.event.client;

import systems.comodal.pagerduty.event.data.PagerDutyChangeEventPayload;
import systems.comodal.pagerduty.event.data.PagerDutyEventPayload;
import systems.comodal.pagerduty.event.data.PagerDutyEventResponse;
import systems.comodal.pagerduty.event.data.PagerDutyImageRef;
import systems.comodal.pagerduty.event.data.adapters.PagerDutyEventAdapter;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static java.net.http.HttpRequest.BodyPublishers.ofString;
import static java.net.http.HttpResponse.BodyHandlers.ofByteArray;
import static java.nio.charset.StandardCharsets.UTF_8;

record PagerDutyHttpEventClient(String defaultClientName,
                                String defaultClientUrl,
                                String defaultRoutingKey,
                                String authorizationHeader,
                                URI eventUri,
                                URI changeEventUri,
                                HttpClient httpClient,
                                PagerDutyEventAdapter adapter) implements PagerDutyEventClient {

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

  private HttpRequest createRequest(final URI endpoint, final String jsonBody) {
    return HttpRequest.newBuilder(endpoint).headers(
            "Authorization", authorizationHeader,
            "Accept", "application/json",
            "Content-Type", "application/json")
        .POST(ofString(jsonBody, UTF_8)).build();
  }

  private CompletableFuture<PagerDutyEventResponse> createAndSendEventRequest(final URI uri, final String jsonBody) {
    return httpClient.sendAsync(createRequest(uri, jsonBody), ofByteArray())
        .thenApplyAsync(adapter::adaptResponse);
  }

  @Override
  public CompletableFuture<PagerDutyEventResponse> acknowledgeEvent(final String routingKey, final String dedupKey) {
    return eventAction(routingKey, dedupKey, "acknowledge");
  }

  @Override
  public CompletableFuture<PagerDutyEventResponse> resolveEvent(final String routingKey, final String dedupKey) {
    return eventAction(routingKey, dedupKey, "resolve");
  }

  private CompletableFuture<PagerDutyEventResponse> eventAction(final String routingKey,
                                                                final String dedupKey,
                                                                final String eventAction) {
    Objects.requireNonNull(routingKey, "Routing key is a required field.");
    Objects.requireNonNull(dedupKey, "De-duplication key is a required field.");
    final var json = String.format("""
            {"routing_key":"%s","dedup_key":"%s","event_action":"%s"}""",
        routingKey, dedupKey, eventAction);
    return createAndSendEventRequest(eventUri, json);
  }

  @Override
  public CompletableFuture<PagerDutyEventResponse> triggerEvent(final String clientName,
                                                                final String clientUrl,
                                                                final String routingKey,
                                                                final PagerDutyEventPayload payload) {
    Objects.requireNonNull(routingKey, "Routing key is a required field.");
    final var payloadJson = payload.getPayloadJson();
    final var linksJson = payload.getLinksJson();
    final var imagesJson = payload.getImages().isEmpty()
        ? ""
        : payload.getImages().stream().map(PagerDutyImageRef::toJson)
        .collect(Collectors.joining(",", ",\"images\":[", "]"));
    final var json = String.format("""
            {"event_action":"trigger","routing_key":"%s","dedup_key":"%s","payload":%s,"client":"%s"%s%s%s}""",
        routingKey, payload.getDedupKey(), payloadJson, clientName,
        (clientUrl == null ? "" : ",\"client_url\":\"" + clientUrl + '"'),
        imagesJson, linksJson);
    return createAndSendEventRequest(eventUri, json);
  }

  @Override
  public CompletableFuture<PagerDutyEventResponse> changeEvent(final String clientName,
                                                               final String clientUrl,
                                                               final String routingKey,
                                                               final PagerDutyChangeEventPayload payload) {
    Objects.requireNonNull(routingKey, "Routing key is a required field.");
    final var payloadJson = payload.getPayloadJson();
    final var linksJson = payload.getLinksJson();
    final var json = String.format("""
            {"routing_key":"%s","payload":%s%s}""",
        routingKey, payloadJson, linksJson);
    return createAndSendEventRequest(changeEventUri, json);
  }

  @Override
  public String toString() {
    return "PagerDutyHttpEventClient{defaultClientName='" + defaultClientName + '\'' +
        ", defaultClientUrl='" + defaultClientUrl + '\'' +
        ", defaultRoutingKey='" + defaultRoutingKey + '\'' +
        ", eventUri=" + eventUri + '\'' +
        ", changeEventUri=" + changeEventUri + '}';
  }
}
