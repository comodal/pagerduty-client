package systems.comodal.pagerduty.event.data;

import java.util.Objects;

final class PagerDutyEventResponseVal implements PagerDutyEventResponse {

  private final String status;
  private final String message;
  private final String dedupeKey;

  private PagerDutyEventResponseVal(final String status,
                                    final String message,
                                    final String dedupeKey) {
    this.status = status;
    this.message = message;
    this.dedupeKey = dedupeKey;
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
  public String getDedupeKey() {
    return dedupeKey;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    PagerDutyEventResponseVal that = (PagerDutyEventResponseVal) o;
    return Objects.equals(status, that.status) &&
        Objects.equals(message, that.message) &&
        Objects.equals(dedupeKey, that.dedupeKey);
  }

  @Override
  public int hashCode() {
    return Objects.hash(status, message, dedupeKey);
  }

  static final class PagerDutyEventResponseBuilder implements PagerDutyEventResponse.Builder {

    private String status;
    private String message;
    private String dedupeKey;

    PagerDutyEventResponseBuilder() {
    }

    @Override
    public PagerDutyEventResponse create() {
      return new PagerDutyEventResponseVal(status, message, dedupeKey);
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
    public Builder dedupeKey(final String dedupeKey) {
      this.dedupeKey = dedupeKey;
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
    public String getDedupeKey() {
      return dedupeKey;
    }

    @Override
    public String toString() {
      return `{"_class":"PagerDutyEventResponseBuilder", ` +
          `"status":` + (status == null ? "null" : '"' + status + '"') + ", " +
          `"message":` + (message == null ? "null" : '"' + message + '"') + ", " +
          `"dedupeKey":` + (dedupeKey == null ? "null" : '"' + dedupeKey + '"') +
          '}';
    }
  }

  @Override
  public String toString() {
    return `{"_class":"PagerDutyEventResponseVal", ` +
        `"status":` + (status == null ? "null" : '"' + status + '"') + ", " +
        `"message":` + (message == null ? "null" : '"' + message + '"') + ", " +
        `"dedupeKey":` + (dedupeKey == null ? "null" : '"' + dedupeKey + '"') +
        '}';
  }
}
