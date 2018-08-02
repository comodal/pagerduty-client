package systems.comodal.test.pagerduty;

import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.DynamicTest;
import systems.comodal.pagerduty.config.PagerDutySysProp;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.InetSocketAddress;
import java.util.ServiceLoader;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ForkJoinPool;

import static org.junit.jupiter.api.DynamicTest.dynamicTest;

final class ClientTests {

  private static final ConcurrentLinkedQueue<HttpServer> HTTP_SERVER_POOL = new ConcurrentLinkedQueue<>();

  static {
    PagerDutySysProp.DEBUG.set("false");
    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      for (var httpServer = HTTP_SERVER_POOL.poll(); httpServer != null; httpServer = HTTP_SERVER_POOL.poll()) {
        httpServer.stop(0);
      }
    }));
  }

  private ClientTests() {
  }

  static <C> DynamicTest createTest(final ClientTest<C> test) {
    return dynamicTest(test.getClass().getSimpleName(), () -> runTest(test));
  }

  static <C> DynamicTest createTest(final ServiceLoader.Provider<? extends ClientTest<C>> testProvider) {
    return dynamicTest(testProvider.type().getSimpleName(), () -> runTest(testProvider.get()));
  }

  private static <C> void runTest(final ClientTest<C> clientTest) {
    final var httpServer = createServer();
    final var port = httpServer.getAddress().getPort();
    final var clientName = "test-" + port;
    final var testFuture = createContext(clientTest, clientName, httpServer);
    final var client = clientTest.createClient(clientName);
    try {
      // Run async within the future in case future is terminated on the server side.
      // This will ensure an early termination and proper reporting in the event of a server side exception.
      testFuture.completeAsync(() -> {
        try {
          clientTest.test(client);
        } catch (final IOException e) {
          throw new UncheckedIOException(e);
        }
        return null;
      }).join();
    } finally {
      HTTP_SERVER_POOL.add(httpServer);
    }
  }

  private static <C> CompletableFuture<Void> createContext(final ClientTest<C> clientTest,
                                                           final String clientName,
                                                           final HttpServer httpServer) {
    clientTest.configureClient(clientName, httpServer);
    final var testFuture = new CompletableFuture<Void>();
    clientTest.createContext(clientName, (path, httpHandler) -> {
      final var httpContext = httpServer.createContext(path, httpExchange -> {
        try {
          httpHandler.handle(httpExchange);
        } catch (final Throwable throwable) {
          testFuture.completeExceptionally(throwable);
        }
      });
      testFuture.whenComplete((_void, throwable) -> httpServer.removeContext(httpContext));
    });
    return testFuture;
  }

  private static HttpServer createServer() {
    var httpServer = HTTP_SERVER_POOL.poll();
    if (httpServer != null) {
      return httpServer;
    }
    try {
      httpServer = HttpServer.create(new InetSocketAddress(0), 0);
      httpServer.setExecutor(ForkJoinPool.commonPool());
      httpServer.start();
      return httpServer;
    } catch (final IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}
