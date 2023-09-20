package systems.comodal.pagerduty.event.data;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static java.time.ZoneOffset.UTC;

final class PagerDutyEventPayloadBuilder implements PagerDutyEventPayload.Builder {

  private static final Map<String, Object> NO_CUSTOM_DETAILS = Map.of();
  private static final List<PagerDutyLinkRef> NO_LINKS = List.of();
  private static final List<PagerDutyImageRef> NO_IMAGES = List.of();

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
    this.customDetails = NO_CUSTOM_DETAILS;
    this.links = NO_LINKS;
    this.images = NO_IMAGES;
  }

  PagerDutyEventPayloadBuilder(final PagerDutyEventPayload prototype) {
    this.dedupKey(prototype.getDedupKey());
    this.summary(prototype.getSummary());
    this.source(prototype.getSource());
    this.severity(prototype.getSeverity());
    this.timestamp(prototype.getTimestamp());
    this.component(prototype.getComponent());
    this.group(prototype.getGroup());
    this.type(prototype.getType());
    final var customDetails = prototype.getCustomDetails();
    this.customDetails = customDetails == null || customDetails.isEmpty()
        ? NO_CUSTOM_DETAILS
        : customDetails.size() > 1
        ? new LinkedHashMap<>(customDetails)
        : Map.copyOf(customDetails);
    final var links = prototype.getLinks();
    this.links = links == null || links.isEmpty()
        ? NO_LINKS
        : links.size() > 1
        ? new ArrayList<>(links)
        : List.copyOf(links);
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
  public Builder dedupKey(final String dedupKey) {
    if (dedupKey != null && dedupKey.length() > 255) {
      throw new IllegalArgumentException("Max length for 'dedup_key' is 255");
    }
    this.dedupKey = dedupKey;
    return this;
  }

  @Override
  public Builder summary(final String summary) {
    this.summary = summary.length() > 1_024 ? summary.substring(0, 1_024) : summary;
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

  private static void appendString(final StringBuilder jsonBuilder, final String field, final String str) {
    if (str != null && !str.isBlank()) {
      jsonBuilder.append(",\"");
      jsonBuilder.append(field);
      jsonBuilder.append("\":\"");
      jsonBuilder.append(str);
      jsonBuilder.append('"');
    }
  }

  private static String escapeQuotes(final String str) {
    final char[] chars = str.toCharArray();
    final char[] escaped = new char[chars.length << 1];
    char c;
    for (int escapes = 0, from = 0, dest = 0, to = 0; ; to++) {
      if (to == chars.length) {
        if (from == 0) {
          return str;
        } else {
          final int len = to - from;
          System.arraycopy(chars, from, escaped, dest, len);
          dest += len;
          return new String(escaped, 0, dest);
        }
      } else {
        c = chars[to];
        if (c == '\\') {
          escapes++;
        } else if (c == '"' && (escapes & 1) == 0) {
          final int len = to - from;
          System.arraycopy(chars, from, escaped, dest, len);
          dest += len;
          escaped[dest++] = '\\';
          from = to;
          escapes = 0;
        } else {
          escapes = 0;
        }
      }
    }
  }

  private static String toJson(final Map<String, Object> object) {
    return object.entrySet().stream().map(entry -> {
      final var key = entry.getKey();
      final var val = entry.getValue();
      return switch (val) {
        case BigDecimal num -> String.format("""
            "%s":"%s\"""", key, num.toPlainString());
        case BigInteger num -> String.format("""
            "%s":"%s\"""", key, num);
        case Number num -> String.format("""
            "%s":%s""", key, num);
        case Object obj -> {
          final var str = obj.toString();
          yield String.format("""
                  "%s":"%s\"""",
              key, str.indexOf('"') < 0 ? str : escapeQuotes(str));
        }
      };
    }).collect(Collectors.joining(",", "{", "}"));
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

  @Override
  public String toString() {
    return getPayloadJson();
  }
}
