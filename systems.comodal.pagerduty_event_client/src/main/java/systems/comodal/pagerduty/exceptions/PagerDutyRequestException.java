package systems.comodal.pagerduty.exceptions;

import jdk.incubator.http.HttpResponse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class PagerDutyRequestException extends RuntimeException {

  private final String status;
  private final List<String> errors;
  private final HttpResponse<?> httpResponse;

  public PagerDutyRequestException(final String status,
                                   final String message,
                                   final List<String> errors,
                                   final HttpResponse<?> httpResponse) {
    super(message);
    this.status = status;
    this.errors = errors;
    this.httpResponse = httpResponse;
  }

  public List<String> getErrors() {
    return errors;
  }

  public HttpResponse<?> getHttpResponse() {
    return httpResponse;
  }

  public String getStatus() {
    return status;
  }

  @Override
  public String toString() {
    return "{\"_class\":\"PagerDutyRequestException\", " +
        "\"status\":" + (status == null ? "null" : "\"" + status + "\"") + ", " +
        "\"message\":" + (getMessage() == null ? "null" : "\"" + getMessage() + "\"") + ", " +
        "\"errors\":" + (errors == null ? "null" : Arrays.toString(errors.toArray())) + ", " +
        "\"httpResponse\":" + (httpResponse == null ? "null" : httpResponse) +
        "}";
  }

  public static PagerDutyRequestException.Builder build(final HttpResponse<?> response) {
    return new PagerDutyRequestExceptionBuilder(response);
  }

  private static final class PagerDutyRequestExceptionBuilder implements Builder {

    private final HttpResponse<?> response;
    private String status;
    private String message;
    private List<String> errors;

    private PagerDutyRequestExceptionBuilder(final HttpResponse<?> response) {
      this.response = response;
    }

    @Override
    public PagerDutyRequestException create() {
      if (errors == null) {
        errors = List.of();
      } else if (errors.size() > 1) {
        errors = List.copyOf(errors);
      }
      return new PagerDutyRequestException(status, message, errors, response);
    }

    @Override
    public Builder status(final String status) {
      this.status = status;
      return this;
    }

    @Override
    public Builder message(final String message) {
      this.message = message;
      return this;
    }

    @Override
    public Builder error(final String error) {
      if (errors == null) {
        errors = List.of(error);
        return this;
      }
      if (errors.size() == 1) {
        errors = new ArrayList<>(errors);
      }
      errors.add(error);
      return this;
    }
  }

  public interface Builder {

    PagerDutyRequestException create();

    Builder status(final String status);

    Builder message(final String message);

    Builder error(final String error);
  }
}
