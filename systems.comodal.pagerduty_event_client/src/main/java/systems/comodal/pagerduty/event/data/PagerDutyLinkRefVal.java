package systems.comodal.pagerduty.event.data;

final record PagerDutyLinkRefVal(String href, String text) implements PagerDutyLinkRef {

  @Override
  public String getHref() {
    return href;
  }

  @Override
  public String getText() {
    return text;
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
