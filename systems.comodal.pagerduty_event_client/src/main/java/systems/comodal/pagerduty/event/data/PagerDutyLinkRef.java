package systems.comodal.pagerduty.event.data;

public interface PagerDutyLinkRef {

  static PagerDutyLinkRef.Builder build() {
    return new PagerDutyLinkRefVal.PagerDutyLinkRefBuilder();
  }

  String getHref();

  String getText();

  default String toJson() {
    final var text = getText();
    if (text == null || text.isBlank()) {
      return String.format("""
          {"href":"%s"}""", getHref());
    } else {
      return String.format("""
          {"href":"%s","text":"%s"}""", getHref(), text);
    }
  }

  interface Builder extends PagerDutyLinkRef {

    PagerDutyLinkRef create();

    Builder href(final String href);

    Builder text(final String text);
  }
}
