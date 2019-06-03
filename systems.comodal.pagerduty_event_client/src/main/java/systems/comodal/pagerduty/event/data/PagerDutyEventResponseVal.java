package systems.comodal.pagerduty.event.data;

import java.util.Objects;

final class PagerDutyEventResponseVal implements PagerDutyEventResponse {

  private final String status;
  private final String message;
  private final String dedupKey;

  private PagerDutyEventResponseVal(final String status,
                                    final String message,
                                    final String dedupKey) {
    this.status = status;
    this.message = message;
    this.dedupKey = dedupKey;
  }

  @Override
  public String getStatus() {
    return status;
  }

  @Override
  public String getMessage() {
    return message;
  }

  @Override
  public String getDedupKey() {
    return dedupKey;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    PagerDutyEventResponseVal that = (PagerDutyEventResponseVal) o;
    return Objects.equals(status, that.status) &&
        Objects.equals(message, that.message) &&
        Objects.equals(dedupKey, that.dedupKey);
  }

  @Override
  public int hashCode() {
    return Objects.hash(status, message, dedupKey);
  }

  static final class PagerDutyEventResponseBuilder implements PagerDutyEventResponse.Builder {

    private String status;
    private String message;
    private String dedupKey;

    PagerDutyEventResponseBuilder() {
    }

    @Override
    public PagerDutyEventResponse create() {
      return new PagerDutyEventResponseVal(status, message, dedupKey);
    }

    @Override
    public Builder status(final String status) {
      this.status = status;
      return this;
    }

    @Override
    public Builder message(final String message) {
      this.message = message;
      return this;
    }

    @Override
    public Builder dedupKey(final String dedupKey) {
      this.dedupKey = dedupKey;
      return this;
    }

    @Override
    public String getStatus() {
      return status;
    }

    @Override
    public String getMessage() {
      return message;
    }

    @Override
    public String getDedupKey() {
      return dedupKey;
    }

    @Override
    public String toString() {
      return "PagerDutyEventResponseBuilder{status='" + status + '\'' +
          ", message='" + message + '\'' +
          ", dedupKey='" + dedupKey + '\'' + '}';
    }
  }

  @Override
  public String toString() {
    return "PagerDutyEventResponseVal{status='" + status + '\'' +
        ", message='" + message + '\'' +
        ", dedupKey='" + dedupKey + '\'' + '}';
  }
}
