package systems.comodal.pagerduty.event.data;

import java.util.Objects;

final class PagerDutyLinkRefVal implements PagerDutyLinkRef {

  private final String href;
  private final String text;

  private PagerDutyLinkRefVal(final String href, final String text) {
    this.href = href;
    this.text = text;
  }

  @Override
  public String getHref() {
    return href;
  }

  @Override
  public String getText() {
    return text;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    PagerDutyLinkRefVal that = (PagerDutyLinkRefVal) o;
    return Objects.equals(href, that.href) &&
        Objects.equals(text, that.text);
  }

  @Override
  public int hashCode() {
    return Objects.hash(href, text);
  }

  static final class PagerDutyLinkRefBuilder implements PagerDutyLinkRef.Builder {

    private String href;
    private String text;

    PagerDutyLinkRefBuilder() {
    }

    @Override
    public PagerDutyLinkRef create() {
      return new PagerDutyLinkRefVal(href, text);
    }

    @Override
    public Builder href(final String href) {
      this.href = href;
      return this;
    }

    @Override
    public Builder text(final String text) {
      this.text = text;
      return this;
    }

    @Override
    public String getHref() {
      return href;
    }

    @Override
    public String getText() {
      return text;
    }

    @Override
    public String toString() {
      return toJson();
    }
  }

  @Override
  public String toString() {
    return toJson();
  }
}
