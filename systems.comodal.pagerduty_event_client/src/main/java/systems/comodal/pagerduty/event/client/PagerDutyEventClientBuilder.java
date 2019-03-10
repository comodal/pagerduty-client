package systems.comodal.pagerduty.event.client;

import systems.comodal.pagerduty.event.data.adapters.PagerDutyEventAdapter;
import systems.comodal.pagerduty.event.data.adapters.PagerDutyEventAdapterFactory;

import java.net.URI;
import java.net.http.HttpClient;
import java.util.ServiceLoader;

import static java.net.http.HttpClient.Version.HTTP_2;
import static java.util.Objects.requireNonNull;

final class PagerDutyEventClientBuilder implements PagerDutyEventClient.Builder {

  private String defaultClientName;
  private String defaultClientUrl;
  private String defaultRoutingKey;
  private String authToken;
  private String pagerDutyUri = "https://events.pagerduty.com";
  private HttpClient httpClient;
  private PagerDutyEventAdapter responseAdapter;

  PagerDutyEventClientBuilder() {
  }
  
  @Override
  public PagerDutyEventClient create() {
    final var authHeader = "Token token=" + requireNonNull(authToken, "Auth token is required.");
    final var pageDutyEventsUri = URI.create(pagerDutyUri).resolve("/v2/enqueue");
    return new PagerDutyHttpEventClient(defaultClientName, defaultClientUrl,
        defaultRoutingKey,
        authHeader,
        pageDutyEventsUri,
        httpClient == null ? HttpClient.newBuilder().version(HTTP_2).build() : httpClient,
        responseAdapter == null
            ? ServiceLoader.load(PagerDutyEventAdapterFactory.class).findFirst().orElseThrow().create()
            : responseAdapter);
  }

  @Override
  public PagerDutyEventClient.Builder defaultClientName(final String defaultClientName) {
    this.defaultClientName = defaultClientName;
    return this;
  }

  @Override
  public PagerDutyEventClient.Builder defaultClientUrl(final String defaultClientUrl) {
    this.defaultClientUrl = defaultClientUrl;
    return this;
  }

  @Override
  public PagerDutyEventClient.Builder defaultRoutingKey(final String defaultRoutingKey) {
    this.defaultRoutingKey = defaultRoutingKey;
    return this;
  }

  @Override
  public PagerDutyEventClient.Builder authToken(final String authToken) {
    this.authToken = authToken;
    return this;
  }

  @Override
  public PagerDutyEventClient.Builder pagerDutyUri(final String pagerDutyUri) {
    this.pagerDutyUri = pagerDutyUri;
    return this;
  }

  @Override
  public PagerDutyEventClient.Builder httpClient(final HttpClient httpClient) {
    this.httpClient = httpClient;
    return this;
  }

  @Override
  public PagerDutyEventClient.Builder responseAdapter(final PagerDutyEventAdapter responseAdapter) {
    this.responseAdapter = responseAdapter;
    return this;
  }

  @Override
  public String getDefaultClientName() {
    return defaultClientName;
  }

  @Override
  public String getDefaultClientUrl() {
    return defaultClientUrl;
  }

  @Override
  public String getDefaultRoutingKey() {
    return defaultRoutingKey;
  }

  @Override
  public String getAuthToken() {
    return authToken;
  }

  @Override
  public String getPagerDutyUri() {
    return pagerDutyUri;
  }

  @Override
  public HttpClient getHttpClient() {
    return httpClient;
  }

  @Override
  public PagerDutyEventAdapter getResponseAdapter() {
    return responseAdapter;
  }
}
