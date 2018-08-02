package systems.comodal.test.pagerduty;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import systems.comodal.pagerduty.event.client.PagerDutyEventClient;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.BiConsumer;

import static java.nio.charset.StandardCharsets.UTF_8;

public interface ClientTest<T> {

  Path DATA_PATH = Paths.get("src/test/resources/systems/comodal/test/pagerduty/").toAbsolutePath();

  void configureClient(final String clientName, final HttpServer httpServer);

  void createContext(final String clientName, final BiConsumer<String, HttpHandler> server);

  T createClient(final String clientName);

  void test(final T client) throws IOException;

  default void writeResponse(final HttpExchange httpExchange, final String response) {
    final var responseBytes = response.getBytes(UTF_8);
    try {
      httpExchange.sendResponseHeaders(200, responseBytes.length);
      try (final var os = httpExchange.getResponseBody()) {
        os.write(responseBytes);
      }
    } catch (final IOException ioEx) {
      throw new UncheckedIOException(ioEx);
    }
  }

  default void writeResponse(final HttpExchange httpExchange, final Path filePath) {
    try {
      try (final var is = Files.newInputStream(filePath);
           final var os = httpExchange.getResponseBody()) {
        httpExchange.sendResponseHeaders(200, filePath.toFile().length());
        is.transferTo(os);
      }
    } catch (final IOException ioEx) {
      throw new UncheckedIOException(ioEx);
    }
  }
}