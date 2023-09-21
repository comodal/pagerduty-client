package systems.comodal.pagerduty.event.data;

import java.time.ZonedDateTime;
import java.util.*;

import static java.time.ZoneOffset.UTC;

final class PagerDutyEventPayloadBuilder
    extends PagerDutyChangeEventPayloadRecord.PagerDutyChangeEventPayloadBuilder
    implements PagerDutyEventPayload.Builder {

  private static final List<PagerDutyImageRef> NO_IMAGES = List.of();

  private String dedupKey;
  private PagerDutySeverity severity;
  private String component;
  private String group;
  private String type;
  private List<PagerDutyImageRef> images;

  PagerDutyEventPayloadBuilder() {
    super();
    this.images = NO_IMAGES;
  }

  PagerDutyEventPayloadBuilder(final PagerDutyEventPayload prototype) {
    super(prototype);
    this.dedupKey(prototype.getDedupKey());
    this.severity(prototype.getSeverity());
    this.component(prototype.getComponent());
    this.group(prototype.getGroup());
    this.type(prototype.getType());
    final var images = prototype.getImages();
    this.images = images == null || images.isEmpty()
        ? NO_IMAGES
        : images.size() > 1
        ? new ArrayList<>(images)
        : List.copyOf(images);
  }

  @Override
  public PagerDutyEventPayload create() {
    Objects.requireNonNull(summary, "'Summary' is a required payload field.");
    Objects.requireNonNull(source, "'Source' is a required payload field.");
    Objects.requireNonNull(severity, "'Severity' is a required payload field.");
    if (dedupKey == null || dedupKey.isBlank()) {
      dedupKey = UUID.randomUUID().toString();
    }
    if (timestamp == null) {
      timestamp = ZonedDateTime.now(UTC);
    }
    final var json = getPayloadJson();
    return new PagerDutyEventPayloadRecord(
        dedupKey,
        summary,
        source,
        severity,
        timestamp,
        component, group, type,
        customDetails.size() > 1 ? Collections.unmodifiableMap(customDetails) : customDetails,
        links.size() > 1 ? Collections.unmodifiableList(links) : links,
        images.size() > 1 ? Collections.unmodifiableList(images) : images,
        json);
  }

  @Override
  public PagerDutyEventPayload.Builder dedupKey(final String dedupKey) {
    if (dedupKey != null && dedupKey.length() > 255) {
      throw new IllegalArgumentException("Max length for 'dedup_key' is 255");
    }
    this.dedupKey = dedupKey;
    return this;
  }

  @Override
  public PagerDutyEventPayload.Builder summary(final String summary) {
    super.summary(summary);
    return this;
  }

  @Override
  public PagerDutyEventPayload.Builder source(final String source) {
    super.source(source);
    return this;
  }

  @Override
  public PagerDutyEventPayload.Builder severity(final PagerDutySeverity severity) {
    this.severity = severity;
    return this;
  }

  @Override
  public PagerDutyEventPayload.Builder timestamp(final ZonedDateTime timestamp) {
    super.timestamp(timestamp);
    return this;
  }

  @Override
  public PagerDutyEventPayload.Builder component(final String component) {
    this.component = component;
    return this;
  }

  @Override
  public PagerDutyEventPayload.Builder group(final String group) {
    this.group = group;
    return this;
  }

  @Override
  public PagerDutyEventPayload.Builder type(final String type) {
    this.type = type;
    return this;
  }

  @Override
  public PagerDutyEventPayload.Builder customDetails(final String field, final String fieldValue) {
    super.customDetailsObject(field, fieldValue);
    return this;
  }

  @Override
  public PagerDutyEventPayload.Builder customDetails(final String field, final Boolean fieldValue) {
    super.customDetailsObject(field, fieldValue);
    return this;
  }

  @Override
  public PagerDutyEventPayload.Builder customDetails(final String field, final Number fieldValue) {
    super.customDetailsObject(field, fieldValue);
    return this;
  }

  @Override
  public PagerDutyEventPayload.Builder customDetails(final String field, final Object fieldValue) {
    super.customDetailsObject(field, fieldValue);
    return this;
  }

  @Override
  public PagerDutyEventPayload.Builder link(final PagerDutyLinkRef link) {
    super.link(link);
    return this;
  }

  @Override
  public PagerDutyEventPayload.Builder image(final PagerDutyImageRef image) {
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
  public PagerDutySeverity getSeverity() {
    return severity;
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
  public List<PagerDutyImageRef> getImages() {
    return images;
  }


  @Override
  public String getPayloadJson() {
    final var jsonBuilder = new StringBuilder(2_048);
    jsonBuilder.append(String.format("""
            {"summary":"%s","source":"%s","severity":"%s","timestamp":"%s\"""",
        summary, source, severity, timestamp));
    appendString(jsonBuilder, "component", component);
    appendString(jsonBuilder, "group", group);
    appendString(jsonBuilder, "class", type);
    if (!customDetails.isEmpty()) {
      jsonBuilder.append("""
          ,"custom_details":""");
      jsonBuilder.append(toJson(customDetails));
    }
    return jsonBuilder.append('}').toString();
  }
}
