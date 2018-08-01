package systems.comodal.pagerduty.event.data;

import java.util.Objects;

final class PagerDutyImageRefVal implements PagerDutyImageRef {

  private final String src;
  private final String href;
  private final String alt;

  private PagerDutyImageRefVal(final String src,
                               final String href,
                               final String alt) {
    this.src = src;
    this.href = href;
    this.alt = alt;
  }

  @Override
  public String getSrc() {
    return src;
  }

  @Override
  public String getHref() {
    return href;
  }

  @Override
  public String getAlt() {
    return alt;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    PagerDutyImageRefVal that = (PagerDutyImageRefVal) o;
    return Objects.equals(src, that.src) &&
        Objects.equals(href, that.href) &&
        Objects.equals(alt, that.alt);
  }

  @Override
  public int hashCode() {
    return Objects.hash(src, href, alt);
  }

  static final class PagerDutyImageRefBuilder implements PagerDutyImageRef.Builder {

    private String src;
    private String href;
    private String alt;

    PagerDutyImageRefBuilder() {
    }

    @Override
    public PagerDutyImageRef create() {
      return new PagerDutyImageRefVal(src, href, alt);
    }

    @Override
    public Builder src(final String src) {
      this.src = src;
      return this;
    }

    @Override
    public Builder href(final String href) {
      this.href = href;
      return this;
    }

    @Override
    public Builder alt(final String alt) {
      this.alt = alt;
      return this;
    }

    @Override
    public String getSrc() {
      return src;
    }

    @Override
    public String getHref() {
      return href;
    }

    @Override
    public String getAlt() {
      return alt;
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
