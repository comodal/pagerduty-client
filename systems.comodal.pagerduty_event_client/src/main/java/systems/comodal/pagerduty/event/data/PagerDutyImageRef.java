package systems.comodal.pagerduty.event.data;

public interface PagerDutyImageRef {

  static PagerDutyImageRef.Builder build() {
    return new PagerDutyImageRefVal.PagerDutyImageRefBuilder();
  }

  String getSrc();

  String getHref();

  String getAlt();

  default String toJson() {
    final var href = getHref();
    final var alt = getAlt();
    if (href == null || href.isBlank()) {
      if (alt == null || alt.isBlank()) {
        return String.format("""
            {"src":"%s"}""", getSrc());
      } else {
        return String.format("""
            {"src":"%s","alt":"%s"}""", getSrc(), alt);
      }
    } else if (alt == null || alt.isBlank()) {
      return String.format("""
          {"src":"%s","href":"%s"}""", getSrc(), href);
    } else {
      return String.format("""
          {"src":"%s","href":"%s","alt":"%s"}""", getSrc(), href, alt);
    }
  }

  interface Builder extends PagerDutyImageRef {

    PagerDutyImageRef create();

    Builder src(final String src);

    Builder href(final String href);

    Builder alt(final String alt);
  }
}
