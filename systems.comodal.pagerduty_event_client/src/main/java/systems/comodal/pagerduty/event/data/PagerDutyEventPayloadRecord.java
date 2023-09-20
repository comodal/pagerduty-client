package systems.comodal.pagerduty.event.data;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

record PagerDutyEventPayloadRecord(String dedupKey,
                                   String summary,
                                   String source,
                                   PagerDutySeverity severity,
                                   ZonedDateTime timestamp,
                                   String component,
                                   String group,
                                   String type,
                                   Map<String, Object> customDetails,
                                   List<PagerDutyLinkRef> links,
                                   List<PagerDutyImageRef> images,
                                   String json) implements PagerDutyEventPayload {

  @Override
  public String getDedupKey() {
    return dedupKey;
  }

  @Override
  public String getSummary() {
    return summary;
  }

  @Override
  public String getSource() {
    return source;
  }

  @Override
  public PagerDutySeverity getSeverity() {
    return severity;
  }

  @Override
  public ZonedDateTime getTimestamp() {
    return timestamp;
  }

  @Override
  public String getComponent() {
    return component;
  }

  @Override
  public String getGroup() {
    return group;
  }

  @Override
  public String getType() {
    return type;
  }

  @Override
  public Map<String, Object> getCustomDetails() {
    return customDetails;
  }

  @Override
  public List<PagerDutyLinkRef> getLinks() {
    return links;
  }

  @Override
  public List<PagerDutyImageRef> getImages() {
    return images;
  }

  @Override
  public String getPayloadJson() {
    return json;
  }

  @Override
  public String toString() {
    return json;
  }
}
