package systems.comodal.pagerduty.event.data;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public interface PagerDutyChangeEventPayload {

  static PagerDutyChangeEventPayload.Builder build() {
    return new PagerDutyChangeEventPayloadRecord.PagerDutyChangeEventPayloadBuilder();
  }

  static PagerDutyChangeEventPayload.Builder build(final PagerDutyChangeEventPayload prototype) {
    return prototype == null ? build() : new PagerDutyChangeEventPayloadRecord.PagerDutyChangeEventPayloadBuilder(prototype);
  }

  ZonedDateTime getTimestamp();

  String getSummary();

  String getSource();

  Map<String, Object> getCustomDetails();

  List<PagerDutyLinkRef> getLinks();

  default String getLinksJson() {
    final var links = getLinks();
    return links.isEmpty() ? "" : links.stream().map(PagerDutyLinkRef::toJson)
        .collect(Collectors.joining(",", ",\"links\":[", "]"));
  }

  String getPayloadJson();

  interface Builder extends PagerDutyChangeEventPayload {


    PagerDutyChangeEventPayload create();

    Builder summary(final String summary);

    Builder timestamp(final ZonedDateTime timestamp);

    Builder source(final String source);

    Builder customDetails(final String field, final String fieldValue);

    Builder customDetails(final String field, final Boolean fieldValue);

    Builder customDetails(final String field, final Number fieldValue);

    Builder customDetails(final String field, final Object fieldValue);

    Builder link(final PagerDutyLinkRef link);
  }
}
