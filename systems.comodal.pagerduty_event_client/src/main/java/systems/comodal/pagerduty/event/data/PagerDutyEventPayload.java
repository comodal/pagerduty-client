package systems.comodal.pagerduty.event.data;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

public interface PagerDutyEventPayload {

  static PagerDutyEventPayload.Builder build() {
    return new PagerDutyEventPayloadBuilder();
  }

  static PagerDutyEventPayload.Builder build(final PagerDutyEventPayload prototype) {
    return prototype == null ? build() : new PagerDutyEventPayloadBuilder(prototype);
  }

  String getDedupKey();

  String getSummary();

  String getSource();

  PagerDutySeverity getSeverity();

  ZonedDateTime getTimestamp();

  String getComponent();

  String getGroup();

  String getType();

  Map<String, Object> getCustomDetails();

  List<PagerDutyLinkRef> getLinks();

  List<PagerDutyImageRef> getImages();

  String getPayloadJson();

  interface Builder extends PagerDutyEventPayload {

    PagerDutyEventPayload create();

    Builder dedupKey(final String dedupKey);

    Builder summary(final String summary);

    Builder source(final String source);

    Builder severity(final PagerDutySeverity severity);

    Builder timestamp(final ZonedDateTime timestamp);

    Builder component(final String component);

    Builder group(final String group);

    Builder type(final String type);

    Builder customDetails(final String field, final String fieldValue);

    Builder customDetails(final String field, final Number fieldValue);

    Builder link(final PagerDutyLinkRef link);

    Builder image(final PagerDutyImageRef image);
  }
}
