package systems.comodal.pagerduty.exceptions;

import java.net.http.HttpResponse;

public final class PagerDutyParseException extends RuntimeException implements PagerDutyClientException {

  private final HttpResponse<?> httpResponse;
  private final String buffer;

  public static PagerDutyParseException unhandledField(final HttpResponse<?> httpResponse, final String context, final String field, final String buffer) {
    return new PagerDutyParseException(httpResponse, "Unhandled " + context + " field '" + field + '\'', buffer);
  }

  public PagerDutyParseException(final HttpResponse<?> httpResponse, final String message, final String buffer) {
    super(message);
    this.httpResponse = httpResponse;
    this.buffer = buffer;
  }

  public PagerDutyParseException(final HttpResponse<?> httpResponse, final String message, final Throwable cause) {
    super(message, cause);
    this.httpResponse = httpResponse;
    this.buffer = null;
  }

  public PagerDutyParseException(final HttpResponse<?> httpResponse, final Throwable cause) {
    super(cause);
    this.httpResponse = httpResponse;
    this.buffer = null;
  }

  public PagerDutyParseException(final HttpResponse<?> httpResponse, final Throwable cause, final String buffer) {
    super(cause);
    this.httpResponse = httpResponse;
    this.buffer = buffer;
  }

  @Override
  public boolean canBeRetried() {
    return httpResponse == null || httpResponse.statusCode() > 400;
  }

  @Override
  public HttpResponse<?> getHttpResponse() {
    return httpResponse;
  }

  public String getBuffer() {
    return buffer;
  }
}
