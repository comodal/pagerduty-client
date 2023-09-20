package systems.comodal.pagerduty.event.data;

record PagerDutyEventResponseVal(String status,
                                 String message,
                                 String dedupKey) implements PagerDutyEventResponse {

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
}
