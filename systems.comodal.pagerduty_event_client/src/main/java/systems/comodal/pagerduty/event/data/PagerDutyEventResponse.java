package systems.comodal.pagerduty.event.data;

public interface PagerDutyEventResponse {

  static PagerDutyEventResponse.Builder build() {
    return new PagerDutyEventResponseVal.PagerDutyEventResponseBuilder();
  }

  String getStatus();

  String getMessage();

  String getDedupKey();

  interface Builder extends PagerDutyEventResponse {

    PagerDutyEventResponse create();

    Builder status(final String status);

    Builder message(final String message);

    Builder dedupKey(final String dedupKey);
  }
}
