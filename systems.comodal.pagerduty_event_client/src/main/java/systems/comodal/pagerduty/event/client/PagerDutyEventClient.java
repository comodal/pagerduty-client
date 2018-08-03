package systems.comodal.pagerduty.event.client;

import systems.comodal.pagerduty.event.data.PagerDutyEventPayload;
import systems.comodal.pagerduty.event.data.PagerDutyEventResponse;
import systems.comodal.pagerduty.event.data.PagerDutyImageRef;
import systems.comodal.pagerduty.event.data.PagerDutyLinkRef;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface PagerDutyEventClient {

  String getClientName();

  String getClientUrl();

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
    return triggerEvent(getDefaultRoutingKey(), null, payload, List.of(), List.of());
  }

  default CompletableFuture<PagerDutyEventResponse> triggerEvent(final String routingKey,
                                                                 final PagerDutyEventPayload payload) {
    return triggerEvent(routingKey, null, payload, List.of(), List.of());
  }

  default CompletableFuture<PagerDutyEventResponse> triggerDefaultRouteEvent(final String dedupeKey,
                                                                             final PagerDutyEventPayload payload) {
    return triggerEvent(getDefaultRoutingKey(), dedupeKey, payload, List.of(), List.of());
  }

  default CompletableFuture<PagerDutyEventResponse> triggerEvent(final String routingKey,
                                                                 final String dedupeKey,
                                                                 final PagerDutyEventPayload payload) {
    return triggerEvent(routingKey, dedupeKey, payload, List.of(), List.of());
  }

  default CompletableFuture<PagerDutyEventResponse> triggerDefaultRouteEvent(final PagerDutyEventPayload payload,
                                                                             final List<PagerDutyImageRef> images,
                                                                             final List<PagerDutyLinkRef> links) {
    return triggerEvent(getDefaultRoutingKey(), null, payload, images, links);
  }

  default CompletableFuture<PagerDutyEventResponse> triggerEvent(final String routingKey,
                                                                 final PagerDutyEventPayload payload,
                                                                 final List<PagerDutyImageRef> images,
                                                                 final List<PagerDutyLinkRef> links) {
    return triggerEvent(routingKey, null, payload, images, links);
  }


  default CompletableFuture<PagerDutyEventResponse> triggerDefaultRouteEvent(final String dedupeKey,
                                                                             final PagerDutyEventPayload payload,
                                                                             final List<PagerDutyImageRef> images,
                                                                             final List<PagerDutyLinkRef> links) {
    return triggerEvent(getDefaultRoutingKey(), dedupeKey, payload, images, links);
  }

  default CompletableFuture<PagerDutyEventResponse> triggerEvent(final String routingKey,
                                                                 final String dedupeKey,
                                                                 final PagerDutyEventPayload payload,
                                                                 final List<PagerDutyImageRef> images,
                                                                 final List<PagerDutyLinkRef> links) {
    return triggerEvent(getClientName(), getClientUrl(), routingKey, dedupeKey, payload, images, links);
  }


  CompletableFuture<PagerDutyEventResponse> triggerEvent(final String clientName,
                                                         final String clientUrl,
                                                         final String routingKey,
                                                         final String dedupeKey,
                                                         final PagerDutyEventPayload payload,
                                                         final List<PagerDutyImageRef> images,
                                                         final List<PagerDutyLinkRef> links);
}
