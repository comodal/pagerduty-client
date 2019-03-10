package systems.comodal.pagerduty.event.client;

import systems.comodal.pagerduty.event.data.PagerDutyEventPayload;
import systems.comodal.pagerduty.event.data.PagerDutyEventResponse;
import systems.comodal.pagerduty.event.data.adapters.PagerDutyEventAdapter;

import java.net.http.HttpClient;
import java.util.concurrent.CompletableFuture;

public interface PagerDutyEventClient {

  static PagerDutyEventClient.Builder build() {
    return new PagerDutyEventClientBuilder();
  }

  String getDefaultClientName();

  String getDefaultClientUrl();

  String getDefaultRoutingKey();

  default CompletableFuture<PagerDutyEventResponse> acknowledgeEvent(final String dedupeKey) {
    return acknowledgeEvent(getDefaultRoutingKey(), dedupeKey);
  }

  CompletableFuture<PagerDutyEventResponse> acknowledgeEvent(final String routingKey, final String dedupeKey);

  default CompletableFuture<PagerDutyEventResponse> resolveEvent(final String dedupeKey) {
    return resolveEvent(getDefaultRoutingKey(), dedupeKey);
  }

  CompletableFuture<PagerDutyEventResponse> resolveEvent(final String routingKey, final String dedupeKey);

  default CompletableFuture<PagerDutyEventResponse> triggerDefaultRouteEvent(final PagerDutyEventPayload payload) {
    return triggerEvent(getDefaultRoutingKey(), null, payload);
  }


  default CompletableFuture<PagerDutyEventResponse> triggerDefaultRouteEvent(final String dedupeKey,
                                                                             final PagerDutyEventPayload payload) {
    return triggerEvent(getDefaultRoutingKey(), dedupeKey, payload);
  }

  default CompletableFuture<PagerDutyEventResponse> triggerEvent(final String routingKey,
                                                                 final PagerDutyEventPayload payload) {
    return triggerEvent(routingKey, null, payload);
  }

  default CompletableFuture<PagerDutyEventResponse> triggerEvent(final String routingKey,
                                                                 final String dedupeKey,
                                                                 final PagerDutyEventPayload payload) {
    return triggerEvent(getDefaultClientName(), getDefaultClientUrl(), routingKey, dedupeKey, payload);
  }

  CompletableFuture<PagerDutyEventResponse> triggerEvent(final String clientName,
                                                         final String clientUrl,
                                                         final String routingKey,
                                                         final String dedupeKey,
                                                         final PagerDutyEventPayload payload);

  interface Builder {

    PagerDutyEventClient create();

    Builder defaultClientName(final String defaultClientName);

    Builder defaultClientUrl(final String defaultClientUrl);

    Builder defaultRoutingKey(final String defaultRoutingKey);

    Builder authToken(final String authToken);

    Builder pagerDutyUri(final String pagerDutyUri);

    Builder httpClient(final HttpClient httpClient);

    Builder responseAdapter(final PagerDutyEventAdapter adapter);

    String getDefaultClientName();

    String getDefaultClientUrl();

    String getDefaultRoutingKey();

    String getAuthToken();

    String getPagerDutyUri();

    HttpClient getHttpClient();

    PagerDutyEventAdapter getResponseAdapter();
  }
}
