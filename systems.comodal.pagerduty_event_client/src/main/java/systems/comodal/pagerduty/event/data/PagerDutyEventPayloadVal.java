package systems.comodal.pagerduty.event.data;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

final class PagerDutyEventPayloadVal implements PagerDutyEventPayload {

  private final String dedupKey;
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
  private final String json;

  PagerDutyEventPayloadVal(final String dedupKey,
                           final String summary,
                           final String source,
                           final PagerDutySeverity severity,
                           final ZonedDateTime timestamp,
                           final String component,
                           final String group,
                           final String type,
                           final Map<String, Object> customDetails,
                           final List<PagerDutyLinkRef> links,
                           final List<PagerDutyImageRef> images) {
    this.dedupKey = dedupKey;
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
    this.json = "{\"summary\":\"" + summary
        + "\",\"source\":\"" + source
        + "\",\"severity\":\"" + severity
        + "\",\"timestamp\":\"" + timestamp
        + (component == null ? '"' : "\",\"component\":\"" + component + '"')
        + (group == null ? "" : ",\"group\":\"" + group + '"')
        + (type == null ? "" : ",\"class\":\"" + type + '"')
        + (customDetails.isEmpty() ? "" : ",\"custom_details\":" + toJson(customDetails))
        + '}';
  }

  private static String escapeQuotes(final String str) {
    final char[] chars = str.toCharArray();
    final char[] escaped = new char[chars.length << 1];
    char c;
    for (int escapes = 0, from = 0, dest = 0, to = 0; ; to++) {
      if (to == chars.length) {
        final int len = to - from;
        if (len > 0) {
          System.arraycopy(chars, from, escaped, dest, len);
          dest += len;
        }
        return new String(escaped, 0, dest);
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

  static String toJson(final Map<String, Object> object) {
    return object.entrySet().stream().map(entry -> {
      final var val = entry.getValue();
      if (val instanceof Number || val instanceof Boolean) {
        return '"' + entry.getKey() + "\":" + val;
      } else {
        final var str = val.toString();
        return '"' + entry.getKey() + "\":\"" + (str.indexOf('"') < 0 ? str : escapeQuotes(str)) + '"';
      }
    }).collect(Collectors.joining(",", "{", "}"));
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

  @Override
  public String getPayloadJson() {
    return json;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    PagerDutyEventPayloadVal that = (PagerDutyEventPayloadVal) o;
    return Objects.equals(dedupKey, that.dedupKey) &&
        Objects.equals(summary, that.summary) &&
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
    return Objects.hash(dedupKey, summary, source, severity, timestamp, component, group, type, customDetails);
  }

  @Override
  public String toString() {
    return getPayloadJson();
  }
}
