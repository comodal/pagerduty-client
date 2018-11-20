package systems.comodal.pagerduty.event.data;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static java.time.ZoneOffset.UTC;

final class PagerDutyEventPayloadVal implements PagerDutyEventPayload {

  private final String summary;
  private final String source;
  private final PagerDutySeverity severity;
  private final ZonedDateTime timestamp;
  private final String component;
  private final String group;
  private final String type;
  private final Map<String, Object> customDetails;
  private final List<PagerDutyLinkRef> links;
  private final List<PagerDutyImageRef> images;

  private PagerDutyEventPayloadVal(final String summary,
                                   final String source,
                                   final PagerDutySeverity severity,
                                   final ZonedDateTime timestamp,
                                   final String component,
                                   final String group,
                                   final String type,
                                   final Map<String, Object> customDetails,
                                   final List<PagerDutyLinkRef> links,
                                   final List<PagerDutyImageRef> images) {
    this.summary = summary;
    this.source = source;
    this.severity = severity;
    this.timestamp = timestamp;
    this.component = component;
    this.group = group;
    this.type = type;
    this.customDetails = customDetails;
    this.links = links;
    this.images = images;
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
    return `{"summary":"` + summary
        + `","source":"` + source
        + `","severity":"` + severity
        + `","timestamp":"` + timestamp
        + (component == null ? '"' : `","component":"` + component + '"')
        + (group == null ? "" : `,"group":"` + group + '"')
        + (type == null ? "" : `,"class":"` + type + '"')
        + (customDetails.isEmpty() ? "" : `,"custom_details":` + toJson(customDetails))
        + '}';
  }

  private static String toJson(final Map<String, Object> object) {
    return object.entrySet().stream().map(entry -> {
      final var val = entry.getValue();
      if (val instanceof Number) {
        return '"' + entry.getKey() + `":` + val;
      }
      return '"' + entry.getKey() + `":"` + val + '"';
    }).collect(Collectors.joining(",", "{", "}"));
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    PagerDutyEventPayloadVal that = (PagerDutyEventPayloadVal) o;
    return Objects.equals(summary, that.summary) &&
        Objects.equals(source, that.source) &&
        severity == that.severity &&
        Objects.equals(timestamp, that.timestamp) &&
        Objects.equals(component, that.component) &&
        Objects.equals(group, that.group) &&
        Objects.equals(type, that.type) &&
        Objects.equals(customDetails, that.customDetails);
  }

  @Override
  public int hashCode() {
    return Objects.hash(summary, source, severity, timestamp, component, group, type, customDetails);
  }

  static final class PagerDutyEventPayloadBuilder implements PagerDutyEventPayload.Builder {

    private String summary;
    private String source;
    private PagerDutySeverity severity;
    private ZonedDateTime timestamp;
    private String component;
    private String group;
    private String type;
    private Map<String, Object> customDetails;
    private List<PagerDutyLinkRef> links = List.of();
    private List<PagerDutyImageRef> images = List.of();

    PagerDutyEventPayloadBuilder() {
    }

    @Override
    public PagerDutyEventPayload create() {
      return new PagerDutyEventPayloadVal(
          Objects.requireNonNull(summary, "Summary is a required payload field."),
          Objects.requireNonNull(source, "Source is a required payload field."),
          Objects.requireNonNull(severity, "Severity is a required payload field."),
          timestamp == null ? ZonedDateTime.now(UTC) : timestamp,
          component, group, type,
          getCustomDetails(),
          links,
          images);
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
      if (customDetails == null) {
        customDetails = new LinkedHashMap<>();
      }
      customDetails.put(field, fieldValue);
      return this;
    }

    @Override
    public Builder customDetails(final String field, final String fieldValue) {
      return customDetailsObject(field, fieldValue);
    }

    @Override
    public Builder customDetails(final String field, final Number fieldValue) {
      return customDetailsObject(field, fieldValue);
    }

    @Override
    public Builder link(final PagerDutyLinkRef link) {
      if (links.isEmpty()) {
        links = List.of(link);
        return this;
      }
      if (links.size() == 1) {
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
      }
      if (images.size() == 1) {
        images = new ArrayList<>(images);
      }
      images.add(image);
      return this;
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
      return customDetails == null
          ? Map.of()
          : customDetails;
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
      return `{"summary":"` + summary
          + `","source":"` + source
          + `","severity":"` + severity
          + `","timestamp":"` + timestamp
          + (component == null ? '"' : `","component":"` + component + '"')
          + (group == null ? "" : `,"group":"` + group + '"')
          + (type == null ? "" : `,"type":"` + type + '"')
          + (customDetails == null ? "" : `,"custom_details":` + PagerDutyEventPayloadVal.toJson(customDetails))
          + '}';
    }

    @Override
    public String toString() {
      return getPayloadJson();
    }
  }

  @Override
  public String toString() {
    return getPayloadJson();
  }
}
