package systems.comodal.pagerduty.config;

public enum PagerDutySysProp implements SysProp {

  DEBUG(".debug"),
  PAGER_DUTY_EVENT_CLIENT_ENDPOINT(".event_client_endpoint"),

  PAGER_DUTY_EVENT_CLIENT_URL(".event_client_url"),
  PAGER_DUTY_EVENT_CLIENT_ROUTING_KEY(".event_client_routing_key"),
  PAGER_DUTY_EVENT_CLIENT_AUTH_TOKEN(".event_client_auth_token"),

  PAGER_DUTY_EVENT_CLIENT_FACTORY(".event_client_factory"),
  PAGER_DUTY_EVENT_ADAPTER_FACTORY(".event_adapter_factory"),
  PAGER_DUTY_HTTP_CLIENT_PROVIDER(".http_client_provider");

  private final String keyPath;

  PagerDutySysProp(final String keyPath) {
    this.keyPath = keyPath;
  }

  public String getPropertyKeyPath() {
    return keyPath;
  }
}
