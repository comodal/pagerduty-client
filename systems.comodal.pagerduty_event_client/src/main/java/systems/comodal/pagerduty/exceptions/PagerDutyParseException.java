package systems.comodal.pagerduty.exceptions;

public final class PagerDutyParseException extends RuntimeException {

  private final String buffer;

  public static PagerDutyParseException unhandledField(final String context, final String field, final String buffer) {
    return new PagerDutyParseException("Unhandled " + context + " field '" + field + '\'', buffer);
  }

  public PagerDutyParseException(final String message, final String buffer) {
    super(message);
    this.buffer = buffer;
  }

  public PagerDutyParseException(final String message, final Throwable cause) {
    super(message, cause);
    this.buffer = null;
  }

  public PagerDutyParseException(final Throwable cause) {
    super(cause);
    this.buffer = null;
  }

  public PagerDutyParseException(final Throwable cause, final String buffer) {
    super(cause);
    this.buffer = buffer;
  }
}
