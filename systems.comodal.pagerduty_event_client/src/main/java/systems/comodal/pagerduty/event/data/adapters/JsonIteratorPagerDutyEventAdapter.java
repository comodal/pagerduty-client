package systems.comodal.pagerduty.event.data.adapters;

import jdk.incubator.http.HttpResponse;
import systems.comodal.jsoniter.JsonException;
import systems.comodal.jsoniter.JsonIterator;
import systems.comodal.pagerduty.config.PagerDutySysProp;
import systems.comodal.pagerduty.event.data.PagerDutyEventResponse;
import systems.comodal.pagerduty.exceptions.PagerDutyParseException;
import systems.comodal.pagerduty.exceptions.PagerDutyRequestException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ConcurrentLinkedQueue;

final class JsonIteratorPagerDutyEventAdapter implements PagerDutyEventAdapter {

  static final JsonIteratorPagerDutyEventAdapter INSTANCE = new JsonIteratorPagerDutyEventAdapter();

  private static final boolean DEBUG = PagerDutySysProp.DEBUG.getBooleanProperty().orElse(Boolean.FALSE);
  private static final ConcurrentLinkedQueue<JsonIterator> JSON_ITERATOR_POOL = new ConcurrentLinkedQueue<>();

  private JsonIteratorPagerDutyEventAdapter() {
  }

  private static JsonIterator createInputStreamJsonIterator(final InputStream inputStream) throws IOException {
    if (DEBUG) {
      final var responseBytes = slowRead(inputStream); // inputStream.readAllBytes();
      System.out.println(new String(responseBytes, java.nio.charset.StandardCharsets.UTF_8));
      return JsonIterator.parse(responseBytes);
    }
    final var jsonIterator = JSON_ITERATOR_POOL.poll();
    return jsonIterator == null ? JsonIterator.parse(inputStream, 2_048) : jsonIterator.reset(inputStream);
  }

  private static byte[] slowRead(final InputStream inputStream) throws IOException {
    try (final var byteArrayOutputStream = new ByteArrayOutputStream()) {
      inputStream.transferTo(byteArrayOutputStream);
      return byteArrayOutputStream.toByteArray();
    }
  }

  private static void returnJsonIterator(final JsonIterator jsonIterator) {
    if (!DEBUG) {
      JSON_ITERATOR_POOL.add(jsonIterator);
    }
  }

  private static JsonIterator createInputStreamJsonIterator(final HttpResponse<InputStream> response) throws IOException {
    return createInputStreamJsonIterator(response.body());
  }

  @Override
  public RuntimeException errorResponse(final HttpResponse<InputStream> response) {
    try (final var jsonIterator = createInputStreamJsonIterator(response)) {
      try {
        final var exception = PagerDutyRequestException.build(response);
        if (response.statusCode() == 429) {
          throw exception.message("Too many requests").create();
        }
        throw adaptException(exception, jsonIterator);
      } catch (final IOException | JsonException | NullPointerException runtimeCause) {
        throw new PagerDutyParseException(runtimeCause, jsonIterator.currentBuffer());
      } finally {
        returnJsonIterator(jsonIterator);
      }
    } catch (final IOException ioEx) {
      throw new PagerDutyParseException(ioEx);
    } catch (final JsonException | ArrayIndexOutOfBoundsException | NullPointerException runtimeCause) {
      throw new PagerDutyParseException("Failed to adapt error response.", runtimeCause);
    }
  }

  @Override
  public PagerDutyEventResponse adaptResponse(final HttpResponse<InputStream> response) {
    verifyHttpResponseCode(response);
    try (final var jsonIterator = createInputStreamJsonIterator(response)) {
      try {
        return adaptResponse(jsonIterator);
      } catch (final IOException | JsonException | NullPointerException runtimeCause) {
        throw new PagerDutyParseException(runtimeCause, jsonIterator.currentBuffer());
      } finally {
        returnJsonIterator(jsonIterator);
      }
    } catch (final IOException ioEx) {
      throw new PagerDutyParseException(ioEx);
    } catch (final JsonException | ArrayIndexOutOfBoundsException | NullPointerException runtimeCause) {
      throw new PagerDutyParseException("Failed to adapt event response.", runtimeCause);
    }
  }

  private PagerDutyEventResponse adaptResponse(final JsonIterator jsonIterator) throws IOException {
    final var response = PagerDutyEventResponse.build();
    for (var field = jsonIterator.readObject(); field != null; field = jsonIterator.readObject()) {
      switch (field) {
        case "status":
          response.status(jsonIterator.readString());
          continue;
        case "message":
          response.message(jsonIterator.readString());
          continue;
        case "dedup_key":
          response.dedupeKey(jsonIterator.readString());
          continue;
        default:
          throw PagerDutyParseException.unhandledField("event response", field, jsonIterator.currentBuffer());
      }
    }
    return response.create();
  }

  private PagerDutyRequestException adaptException(final PagerDutyRequestException.Builder exception, final JsonIterator jsonIterator) throws IOException {
    for (var field = jsonIterator.readObject(); field != null; field = jsonIterator.readObject()) {
      switch (field) {
        case "status":
          exception.status(jsonIterator.readString());
          continue;
        case "message":
          exception.message(jsonIterator.readString());
          continue;
        case "errors":
          while (jsonIterator.readArray()) {
            exception.error(jsonIterator.readString());
          }
          continue;
        default:
          throw PagerDutyParseException.unhandledField("error", field, jsonIterator.currentBuffer());
      }
    }
    return exception.create();
  }
}
