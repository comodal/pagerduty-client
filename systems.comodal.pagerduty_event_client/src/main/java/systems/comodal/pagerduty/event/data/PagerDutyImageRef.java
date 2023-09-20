package systems.comodal.pagerduty.event.data;

public interface PagerDutyImageRef {

  static PagerDutyImageRef.Builder build() {
    return new PagerDutyImageRefVal.PagerDutyImageRefBuilder();
  }

  String getSrc();

  String getHref();

  String getAlt();

  default String toJson() {
    return String.format("""
        {"src":"%s","href":"%s","alt":"%s"}""", getSrc(), getHref(), getAlt());
  }

  interface Builder extends PagerDutyImageRef {

    PagerDutyImageRef create();

    Builder src(final String src);

    Builder href(final String href);

    Builder alt(final String alt);
  }
}
