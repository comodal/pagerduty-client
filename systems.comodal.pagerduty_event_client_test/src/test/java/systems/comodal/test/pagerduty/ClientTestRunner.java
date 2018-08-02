package systems.comodal.test.pagerduty;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.List;
import java.util.ServiceLoader;
import java.util.stream.Stream;

final class ClientTestRunner {

  @Disabled
  @TestFactory
  List<DynamicTest> runSingleTest() {
    return List.of(ClientTests.createTest(new EventClientTests()));
  }

  @TestFactory
  Stream<DynamicTest> runAllEventClientTests() {
    return ServiceLoader.load(EventClientTest.class).stream().parallel()
        .map(ClientTests::createTest);
  }
}
