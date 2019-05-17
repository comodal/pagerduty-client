package systems.comodal.pagerduty.event.service;

import systems.comodal.pagerduty.event.client.PagerDutyEventClient;
import systems.comodal.pagerduty.event.data.PagerDutyEventPayload;
import systems.comodal.pagerduty.event.data.PagerDutyEventResponse;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.LongUnaryOperator;

public interface PagerDutyService {

  static PagerDutyService.Builder build() {
    return new PagerDutyServiceBuilder();
  }

  static LongUnaryOperator createRetryDelayFn(final int maxRetries,
                                              final long stepDelay,
                                              final long maxDelay) {
    return numFailures -> numFailures > maxRetries
        ? Long.MIN_VALUE
        : Math.min(maxDelay, numFailures * stepDelay);
  }

  PagerDutyEventClient getClient();

  PagerDutyEventPayload getEventPrototype();

  CompletableFuture<PagerDutyEventResponse> resolveEvent(final PagerDutyEventResponse triggerResponse,
                                                         final Duration giveUpAfter,
                                                         final long stepDelay,
                                                         final long maxDelay,
                                                         final TimeUnit timeUnit);

  CompletableFuture<PagerDutyEventResponse> resolveEvent(final PagerDutyEventResponse triggerResponse,
                                                         final int maxRetries,
                                                         final long stepDelay,
                                                         final long maxDelay,
                                                         final TimeUnit timeUnit);

  CompletableFuture<PagerDutyEventResponse> resolveEvent(final PagerDutyEventResponse triggerResponse,
                                                         final LongUnaryOperator retryDelayFn,
                                                         final TimeUnit timeUnit);

  CompletableFuture<PagerDutyEventResponse> triggerEvent(final PagerDutyEventPayload payload,
                                                         final Duration giveUpAfter,
                                                         final long stepDelay,
                                                         final long maxDelay,
                                                         final TimeUnit timeUnit);

  CompletableFuture<PagerDutyEventResponse> triggerEvent(final PagerDutyEventPayload payload,
                                                         final int maxRetries,
                                                         final long stepDelay,
                                                         final long maxDelay,
                                                         final TimeUnit timeUnit);

  CompletableFuture<PagerDutyEventResponse> triggerEvent(final PagerDutyEventPayload payload,
                                                         final LongUnaryOperator retryDelayFn,
                                                         final TimeUnit timeUnit);

  interface Builder {

    PagerDutyService create();

    Builder client(final PagerDutyEventClient client);

    Builder eventPrototype(final PagerDutyEventPayload eventPrototype);
  }
}
