package systems.comodal.pagerduty.event.data;

public interface PagerDutyLinkRef {

  static PagerDutyLinkRef.Builder build() {
    return new PagerDutyLinkRefVal.PagerDutyLinkRefBuilder();
  }

  String getHref();

  String getText();

  default String toJson() {
    return String.format("""
        {"href":"%s","text":"%s"}""", getHref(), getText());
  }

  interface Builder extends PagerDutyLinkRef {

    PagerDutyLinkRef create();

    Builder href(final String href);

    Builder text(final String text);
  }
}
