package systems.comodal.pagerduty.event.data;

import java.time.ZonedDateTime;
import java.util.*;

import static java.time.ZoneOffset.UTC;

final class PagerDutyEventPayloadBuilder implements PagerDutyEventPayload.Builder {

  private String dedupKey;
  private String summary;
  private String source;
  private PagerDutySeverity severity;
  private ZonedDateTime timestamp;
  private String component;
  private String group;
  private String type;
  private Map<String, Object> customDetails;
  private List<PagerDutyLinkRef> links;
  private List<PagerDutyImageRef> images;

  PagerDutyEventPayloadBuilder() {
    this.customDetails = Map.of();
    this.links = List.of();
    this.images = List.of();
  }

  PagerDutyEventPayloadBuilder(final PagerDutyEventPayload prototype) {
    this.dedupKey = prototype.getDedupKey();
    this.summary = prototype.getSummary();
    this.source = prototype.getSource();
    this.severity = prototype.getSeverity();
    this.timestamp = prototype.getTimestamp();
    this.component = prototype.getComponent();
    this.group = prototype.getGroup();
    this.type = prototype.getType();
    this.customDetails = prototype.getCustomDetails().size() > 1
        ? new LinkedHashMap<>(prototype.getCustomDetails())
        : Map.copyOf(prototype.getCustomDetails());
    this.links = prototype.getLinks().size() > 1
        ? new ArrayList<>(prototype.getLinks())
        : List.copyOf(prototype.getLinks());
    this.images = prototype.getImages().size() > 1
        ? new ArrayList<>(prototype.getImages())
        : List.copyOf(prototype.getImages());
  }

  @Override
  public PagerDutyEventPayload create() {
    return new PagerDutyEventPayloadVal(
        dedupKey == null || dedupKey.isBlank() ? UUID.randomUUID().toString() : dedupKey,
        Objects.requireNonNull(summary, "'Summary' is a required payload field."),
        Objects.requireNonNull(source, "'Source' is a required payload field."),
        Objects.requireNonNull(severity, "'Severity' is a required payload field."),
        timestamp == null ? ZonedDateTime.now(UTC) : timestamp,
        component, group, type,
        getCustomDetails(),
        links,
        images);
  }

  @Override
  public Builder dedupKey(final String dedupKey) {
    this.dedupKey = dedupKey;
    return this;
  }

  @Override
  public Builder summary(final String summary) {
    this.summary = summary;
    return this;
  }

  @Override
  public Builder source(final String source) {
    this.source = source;
    return this;
  }

  @Override
  public Builder severity(final PagerDutySeverity severity) {
    this.severity = severity;
    return this;
  }

  @Override
  public Builder timestamp(final ZonedDateTime timestamp) {
    this.timestamp = timestamp;
    return this;
  }

  @Override
  public Builder component(final String component) {
    this.component = component;
    return this;
  }

  @Override
  public Builder group(final String group) {
    this.group = group;
    return this;
  }

  @Override
  public Builder type(final String type) {
    this.type = type;
    return this;
  }

  private Builder customDetailsObject(final String field, final Object fieldValue) {
    final var val = fieldValue == null ? "null" : fieldValue;
    if (customDetails == null || customDetails.isEmpty()) {
      customDetails = Map.of(field, val);
      return this;
    } else if (customDetails.size() == 1) {
      customDetails = new LinkedHashMap<>(customDetails);
    }
    customDetails.put(field, val);
    return this;
  }

  @Override
  public Builder customDetails(final String field, final String fieldValue) {
    return customDetailsObject(field, fieldValue);
  }

  @Override
  public Builder customDetails(final String field, final Boolean fieldValue) {
    return customDetailsObject(field, fieldValue);
  }

  @Override
  public Builder customDetails(final String field, final Number fieldValue) {
    return customDetailsObject(field, fieldValue);
  }

  @Override
  public Builder customDetails(final String field, final Object fieldValue) {
    return customDetailsObject(field, fieldValue == null ? null : fieldValue.toString());
  }

  @Override
  public Builder link(final PagerDutyLinkRef link) {
    if (links.isEmpty()) {
      links = List.of(link);
      return this;
    } else if (links.size() == 1) {
      links = new ArrayList<>(links);
    }
    links.add(link);
    return this;
  }

  @Override
  public Builder image(final PagerDutyImageRef image) {
    if (images.isEmpty()) {
      images = List.of(image);
      return this;
    } else if (images.size() == 1) {
      images = new ArrayList<>(images);
    }
    images.add(image);
    return this;
  }

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
    return customDetails == null ? Map.of() : customDetails;
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
    return "{\"summary\":\"" + summary
        + "\",\"source\":\"" + source
        + "\",\"severity\":\"" + severity
        + "\",\"timestamp\":\"" + timestamp
        + (component == null ? '"' : "\",\"component\":\"" + component + '"')
        + (group == null ? "" : ",\"group\":\"" + group + '"')
        + (type == null ? "" : ",\"type\":\"" + type + '"')
        + (customDetails == null ? "" : ",\"custom_details\":" + PagerDutyEventPayloadVal.toJson(customDetails))
        + '}';
  }

  @Override
  public String toString() {
    return getPayloadJson();
  }
}
