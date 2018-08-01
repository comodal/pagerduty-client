package systems.comodal.pagerduty.event.data;

public interface PagerDutyEventResponse {

  static PagerDutyEventResponse.Builder build() {
    return new PagerDutyEventResponseVal.PagerDutyEventResponseBuilder();
  }

  String getStatus();

  String getMessage();

  String getDedupeKey();

  interface Builder extends PagerDutyEventResponse {

    PagerDutyEventResponse create();

    Builder status(final String status);

    Builder message(final String message);

    Builder dedupeKey(final String dedupeKey);
  }
}
