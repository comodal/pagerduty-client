package systems.comodal.pagerduty.event.service;

import systems.comodal.pagerduty.event.client.PagerDutyEventClient;
import systems.comodal.pagerduty.event.data.PagerDutyEventPayload;
import systems.comodal.pagerduty.event.data.PagerDutyEventResponse;
import systems.comodal.pagerduty.exceptions.PagerDutyClientException;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.LongUnaryOperator;

import static java.lang.String.format;
import static java.lang.System.Logger.Level.ERROR;
import static java.util.concurrent.CompletableFuture.delayedExecutor;

final class PagerDutyServiceVal implements PagerDutyService {

  private static final System.Logger log = System.getLogger(PagerDutyService.class.getSimpleName());

  private final PagerDutyEventClient client;
  private final PagerDutyEventPayload eventPrototype;

  PagerDutyServiceVal(final PagerDutyEventClient client, final PagerDutyEventPayload eventPrototype) {
    this.client = client;
    this.eventPrototype = eventPrototype;
  }

  @Override
  public PagerDutyEventClient getClient() {
    return client;
  }

  @Override
  public PagerDutyEventPayload getEventPrototype() {
    return eventPrototype;
  }

  @Override
  public CompletableFuture<PagerDutyEventResponse> resolveEvent(final String dedupeKey,
                                                                final long stepDelay,
                                                                final long maxDelay,
                                                                final TimeUnit timeUnit) {
    return dedupeKey == null ? null
        : resolveEvent(dedupeKey, 0, PagerDutyService.createRetryDelayFn(stepDelay, maxDelay), timeUnit);
  }

  @Override
  public CompletableFuture<PagerDutyEventResponse> resolveEvent(final String dedupeKey,
                                                                final Duration giveUpAfter,
                                                                final long stepDelay,
                                                                final long maxDelay,
                                                                final TimeUnit timeUnit) {
    if (dedupeKey == null) {
      return null;
    }
    final int maxRetries = (int) Math.min(Integer.MAX_VALUE, giveUpAfter.toMillis() / timeUnit.toMillis(stepDelay));
    return resolveEvent(dedupeKey, 0, PagerDutyService.createRetryDelayFn(maxRetries, stepDelay, maxDelay), timeUnit);
  }

  @Override
  public CompletableFuture<PagerDutyEventResponse> resolveEvent(final String dedupeKey,
                                                                final int maxRetries,
                                                                final long stepDelay,
                                                                final long maxDelay,
                                                                final TimeUnit timeUnit) {
    return dedupeKey == null ? null
        : resolveEvent(dedupeKey, 0, PagerDutyService.createRetryDelayFn(maxRetries, stepDelay, maxDelay), timeUnit);
  }

  @Override
  public CompletableFuture<PagerDutyEventResponse> resolveEvent(final String dedupeKey,
                                                                final LongUnaryOperator retryDelayFn,
                                                                final TimeUnit timeUnit) {
    return dedupeKey == null ? null : resolveEvent(dedupeKey, 0, retryDelayFn, timeUnit);
  }

  private CompletableFuture<PagerDutyEventResponse> resolveEvent(final String dedupeKey,
                                                                 final int retry,
                                                                 final LongUnaryOperator retryDelayFn,
                                                                 final TimeUnit timeUnit) {
    final long retryDelay = retryDelayFn.applyAsLong(retry);
    if (retryDelay < 0) {
      return null;
    }
    final var responseFuture = client.resolveEvent(dedupeKey);
    final Function<Throwable, CompletableFuture<PagerDutyEventResponse>> exceptionally = throwable -> {
      final int numFailures = retry + 1;
      log.log(ERROR, format("Failed %d time(s), last delay was %d %s, to resolve event with dedupe key '%s'.",
          numFailures, retryDelay, dedupeKey, timeUnit), throwable);
      return canBeRetried(throwable)
          ? resolveEvent(dedupeKey, numFailures, retryDelayFn, timeUnit)
          : null;
    };
    if (retryDelay > 0) {
      return responseFuture.exceptionallyComposeAsync(exceptionally, delayedExecutor(retryDelay, timeUnit));
    }
    return responseFuture.exceptionallyCompose(exceptionally);
  }

  private static boolean canBeRetried(final Throwable throwable) {
    if (throwable instanceof PagerDutyClientException && !((PagerDutyClientException) throwable).canBeRetried()) {
      return false;
    }
    return !(throwable.getCause() instanceof PagerDutyClientException) || ((PagerDutyClientException) throwable.getCause()).canBeRetried();
  }

  @Override
  public CompletableFuture<PagerDutyEventResponse> triggerEvent(final PagerDutyEventPayload payload, final long stepDelay, final long maxDelay, final TimeUnit timeUnit) {
    return triggerEvent(payload, 0, PagerDutyService.createRetryDelayFn(stepDelay, maxDelay), timeUnit);
  }

  @Override
  public CompletableFuture<PagerDutyEventResponse> triggerEvent(final PagerDutyEventPayload payload,
                                                                final Duration giveUpAfter,
                                                                final long stepDelay,
                                                                final long maxDelay,
                                                                final TimeUnit timeUnit) {
    final int maxRetries = (int) Math.min(Integer.MAX_VALUE, giveUpAfter.toMillis() / timeUnit.toMillis(stepDelay));
    return triggerEvent(payload, 0, PagerDutyService.createRetryDelayFn(maxRetries, stepDelay, maxDelay), timeUnit);
  }

  @Override
  public CompletableFuture<PagerDutyEventResponse> triggerEvent(final PagerDutyEventPayload payload,
                                                                final int maxRetries,
                                                                final long stepDelay,
                                                                final long maxDelay,
                                                                final TimeUnit timeUnit) {
    return triggerEvent(payload, 0, PagerDutyService.createRetryDelayFn(maxRetries, stepDelay, maxDelay), timeUnit);
  }

  @Override
  public CompletableFuture<PagerDutyEventResponse> triggerEvent(final PagerDutyEventPayload payload,
                                                                final LongUnaryOperator retryDelayFn,
                                                                final TimeUnit timeUnit) {
    return triggerEvent(payload, 0, retryDelayFn, timeUnit);
  }

  private CompletableFuture<PagerDutyEventResponse> triggerEvent(final PagerDutyEventPayload payload,
                                                                 final int retry,
                                                                 final LongUnaryOperator retryDelayFn,
                                                                 final TimeUnit timeUnit) {
    final long retryDelay = retryDelayFn.applyAsLong(retry);
    if (retryDelay < 0) {
      return null;
    }
    final var responseFuture = client.triggerDefaultRouteEvent(payload);
    final Function<Throwable, CompletableFuture<PagerDutyEventResponse>> exceptionally = throwable -> {
      final int numFailures = retry + 1;
      log.log(ERROR, format("Failed %d time(s), last delay was %d %s, to trigger event:%n%s%n",
          numFailures, retryDelay, payload, timeUnit), throwable);
      return canBeRetried(throwable)
          ? triggerEvent(payload, numFailures, retryDelayFn, timeUnit)
          : null;
    };
    if (retryDelay > 0) {
      return responseFuture.exceptionallyComposeAsync(exceptionally, delayedExecutor(retryDelay, timeUnit));
    }
    return responseFuture.exceptionallyCompose(exceptionally);
  }

  @Override
  public String toString() {
    return "PagerdutyServiceVal{client=" + client + ", eventPrototype=" + eventPrototype + '}';
  }
}
