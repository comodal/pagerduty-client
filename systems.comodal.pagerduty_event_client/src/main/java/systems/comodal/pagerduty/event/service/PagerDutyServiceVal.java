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

record PagerDutyServiceVal(PagerDutyEventClient client,
                           PagerDutyEventPayload eventPrototype) implements PagerDutyService {

  private static final System.Logger log = System.getLogger(PagerDutyService.class.getPackageName());

  @Override
  public PagerDutyEventClient getClient() {
    return client;
  }

  @Override
  public PagerDutyEventPayload getEventPrototype() {
    return eventPrototype;
  }

  private void logFailure(final Throwable throwable,
                          final int numFailures,
                          final long retryDelay,
                          final TimeUnit timeUnit,
                          final String context) {
    if (throwable.getCause() instanceof final PagerDutyClientException pdException) {
      log.log(ERROR, format("Http Error Code: %s, Service Error Code: %s, Failure Count: %d, Last Delay: %d %s, Service Errors: %s, %s",
          pdException.getHttpResponse() == null ? "?" : String.valueOf(pdException.getHttpResponse().statusCode()),
          pdException.getErrorCode() == 0 ? "?" : String.valueOf(pdException.getErrorCode()),
          numFailures,
          retryDelay, timeUnit,
          pdException.getErrors().toString(),
          context), throwable.getCause());
    } else {
      log.log(ERROR, format("Failure Count: %d, Last Delay: %d %s, %s",
          numFailures, retryDelay, timeUnit, context), throwable.getCause());
    }
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
      logFailure(throwable, numFailures, retryDelay, timeUnit, String.format("to resolve event with dedupe key '%s'.", dedupeKey));
      if (canBeRetried(throwable)) {
        return resolveEvent(dedupeKey, numFailures, retryDelayFn, timeUnit);
      } else if (throwable instanceof RuntimeException) {
        throw (RuntimeException) throwable;
      } else if (throwable.getCause() instanceof RuntimeException) {
        throw (RuntimeException) throwable.getCause();
      } else if (throwable.getCause() == null) {
        throw new RuntimeException(throwable);
      } else {
        throw new RuntimeException(throwable.getCause());
      }
    };
    if (retryDelay > 0) {
      return responseFuture.exceptionallyComposeAsync(exceptionally, delayedExecutor(retryDelay, timeUnit));
    } else {
      return responseFuture.exceptionallyCompose(exceptionally);
    }
  }

  private static boolean canBeRetried(final Throwable throwable) {
    if (throwable instanceof PagerDutyClientException) {
      return ((PagerDutyClientException) throwable).canBeRetried();
    } else if ((throwable.getCause() instanceof PagerDutyClientException)) {
      return ((PagerDutyClientException) throwable.getCause()).canBeRetried();
    } else {
      return true;
    }
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
      logFailure(throwable, numFailures, retryDelay, timeUnit, String.format("to trigger event:%n  %s", payload));
      if (canBeRetried(throwable)) {
        return triggerEvent(payload, numFailures, retryDelayFn, timeUnit);
      } else if (throwable instanceof RuntimeException) {
        throw (RuntimeException) throwable;
      } else if (throwable.getCause() instanceof RuntimeException) {
        throw (RuntimeException) throwable.getCause();
      } else if (throwable.getCause() == null) {
        throw new RuntimeException(throwable);
      } else {
        throw new RuntimeException(throwable.getCause());
      }
    };
    if (retryDelay > 0) {
      return responseFuture.exceptionallyComposeAsync(exceptionally, delayedExecutor(retryDelay, timeUnit));
    } else {
      return responseFuture.exceptionallyCompose(exceptionally);
    }
  }

  @Override
  public String toString() {
    return "PagerdutyServiceVal{client=" + client + ", eventPrototype=" + eventPrototype + '}';
  }
}
