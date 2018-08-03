package systems.comodal.pagerduty.event.client;

import jdk.incubator.http.HttpClient;
import jdk.incubator.http.HttpRequest;
import systems.comodal.pagerduty.client.PagerDutyHttpClientProvider;
import systems.comodal.pagerduty.event.data.PagerDutyEventPayload;
import systems.comodal.pagerduty.event.data.PagerDutyEventResponse;
import systems.comodal.pagerduty.event.data.PagerDutyImageRef;
import systems.comodal.pagerduty.event.data.PagerDutyLinkRef;
import systems.comodal.pagerduty.event.data.adapters.PagerDutyEventAdapter;
import systems.comodal.pagerduty.event.data.adapters.PagerDutyEventAdapterFactory;

import java.net.URI;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.UTF_8;
import static jdk.incubator.http.HttpRequest.BodyPublisher.fromString;
import static jdk.incubator.http.HttpResponse.BodyHandler.asInputStream;
import static systems.comodal.pagerduty.config.PagerDutySysProp.*;

final class PagerDutyHttpEventClient implements PagerDutyEventClient {

  private final String clientName;
  private final String clientUrl;
  private final String defaultRoutingKey;
  private final String authTokenHeaderVal;
  private final URI eventUriPath;
  private final HttpClient httpClient;
  private final PagerDutyEventAdapter adapter;

  PagerDutyHttpEventClient(final String clientName) {
    this.clientName = clientName;
    this.clientUrl = PAGER_DUTY_EVENT_CLIENT_URL.getStringProperty(clientName).orElse(null);
    this.defaultRoutingKey = PAGER_DUTY_EVENT_CLIENT_ROUTING_KEY.getStringProperty(clientName).orElse(null);
    this.authTokenHeaderVal = "Token token=" + PAGER_DUTY_EVENT_CLIENT_AUTH_TOKEN.getMandatoryStringProperty(clientName);
    this.httpClient = PagerDutyHttpClientProvider.load();
    this.adapter = PagerDutyEventAdapterFactory.load();
    final var endpoint = URI.create(PAGER_DUTY_EVENT_CLIENT_ENDPOINT.getStringProperty(clientName).orElse("https://events.pagerduty.com"));
    this.eventUriPath = endpoint.resolve("/v2/enqueue");
  }

  @Override
  public String getClientName() {
    return clientName;
  }

  @Override
  public String getClientUrl() {
    return clientUrl;
  }

  @Override
  public String getDefaultRoutingKey() {
    return defaultRoutingKey;
  }

  @Override
  public CompletableFuture<PagerDutyEventResponse> acknowledgeEvent(final String routingKey, final String dedupeKey) {
    return eventAction(routingKey, dedupeKey, "\",\"event_action\":\"acknowledge\"}");
  }

  @Override
  public CompletableFuture<PagerDutyEventResponse> resolveEvent(final String routingKey, final String dedupeKey) {
    return eventAction(routingKey, dedupeKey, "\",\"event_action\":\"resolve\"}");
  }

  private CompletableFuture<PagerDutyEventResponse> eventAction(final String routingKey, final String dedupeKey, final String actionBody) {
    Objects.requireNonNull(routingKey, "Routing key is a required field.");
    Objects.requireNonNull(dedupeKey, "De-duplication key is a required field.");
    final var json = "{\"routing_key\":\"" + routingKey
        + "\",\"dedup_key\":\"" + dedupeKey
        + actionBody;
    return createAndSendRequest(json);
  }

  @Override
  public CompletableFuture<PagerDutyEventResponse> triggerEvent(final String clientName,
                                                                final String clientUrl,
                                                                final String routingKey,
                                                                final String dedupeKey,
                                                                final PagerDutyEventPayload payload,
                                                                final List<PagerDutyImageRef> images,
                                                                final List<PagerDutyLinkRef> links) {
    Objects.requireNonNull(routingKey, "Routing key is a required field.");

    final var payloadJson = payload.toJson();
    final var imagesJson = images.isEmpty()
        ? ""
        : images.stream().map(PagerDutyImageRef::toJson)
        .collect(Collectors.joining(",", ",\"images\":[", "]"));
    final var linksJson = links.isEmpty()
        ? ""
        : links.stream().map(PagerDutyLinkRef::toJson)
        .collect(Collectors.joining(",", ",\"links\":[", "]"));

    final var json = "{\"event_action\":\"trigger\",\"payload\":" + payloadJson
        + ",\"routing_key\":\"" + routingKey + '"'
        + (dedupeKey == null ? "" : ",\"dedup_key\":\"" + dedupeKey + '"')
        + ",\"client\":\"" + clientName + '"'
        + (clientUrl == null ? "" : ",\"client_url\":\"" + clientUrl + '"')
        + imagesJson
        + linksJson
        + "}";

    return createAndSendRequest(json);
  }

  private HttpRequest createRequest(final String jsonBody) {
    return HttpRequest.newBuilder(eventUriPath)
        .headers(
            "Authorization", authTokenHeaderVal,
            "Accept", "application/vnd.pagerduty+json;version=2",
            "Content-Type", "application/json")
        .POST(fromString(jsonBody, UTF_8)).build();
  }

  private CompletableFuture<PagerDutyEventResponse> createAndSendRequest(final String jsonBody) {
    return httpClient.sendAsync(createRequest(jsonBody), asInputStream())
        .thenApplyAsync(adapter::adaptResponse);
  }

  @Override
  public String toString() {
    return "{\"_class\":\"PagerDutyHttpEventClient\", " +
        "\"clientName\":" + (clientName == null ? "null" : "\"" + clientName + "\"") + ", " +
        "\"clientUrl\":" + (clientUrl == null ? "null" : "\"" + clientUrl + "\"") + ", " +
        "\"eventUriPath\":" + (eventUriPath == null ? "null" : eventUriPath) + "}";
  }
}
