package systems.comodal.pagerduty.config;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Supplier;

public interface SysProp {

  private static String getBaseDomain() {
    return "systems.comodal.pagerduty";
  }

  String getPropertyKeyPath();

  static void loadProps() {
    loadProps(getBaseDomain() + ".properties_file");
  }

  static void loadProps(final String propFilePathKey) {
    final var propFilePathString = System.getProperty(propFilePathKey);
    if (propFilePathString == null) {
      return;
    }
    loadProps(Paths.get(propFilePathString));
  }

  static void loadProps(final Path propFilePath) {
    if (!Files.isReadable(propFilePath)) {
      throw new IllegalStateException("Properties file " + propFilePath + " is not readable.");
    }
    final var propertiesCopy = new Properties(System.getProperties());
    try {
      propertiesCopy.load(Files.newInputStream(propFilePath));
    } catch (final IOException ioException) {
      throw new UncheckedIOException(ioException);
    }
    System.setProperties(propertiesCopy);
  }

  static String formatPath(final String path) {
    return path.replace('/', '.');
  }

  static String formatPath(final Path path) {
    return formatPath(path.toAbsolutePath().toString());
  }

  default void set(final String val) {
    System.setProperty(topLevel(), val);
  }

  default void set(final String subDomain, final String val) {
    System.setProperty(resolve(subDomain), val);
  }

  default String topLevel() {
    return getBaseDomain() + getPropertyKeyPath();
  }

  default String resolve(final String subDomain) {
    return getBaseDomain() + '.' + subDomain + getPropertyKeyPath();
  }

  default String resolve(final Path path) {
    return resolve(formatPath(path));
  }

  default Supplier<RuntimeException> getMandatoryExceptionSupplier() {
    return getMandatoryExceptionSupplier(null);
  }

  default Supplier<RuntimeException> getMandatoryExceptionSupplier(final String subDomain) {
    return () -> getMandatoryException(subDomain);
  }

  default RuntimeException getMandatoryException(final String subDomain) {
    return new IllegalStateException(String.format("'%s' must be provided.",
        subDomain == null ? topLevel() : resolve(subDomain)));
  }

  default Optional<String> getStringProperty() {
    return getStringProperty(null);
  }

  default Optional<String> getStringProperty(final String subDomain) {
    return getStringProperty(subDomain, null);
  }

  default Optional<String> getStringProperty(final String subDomain, final String defaultPropVal) {
    return Optional.ofNullable(System
        .getProperty(subDomain == null ? topLevel() : resolve(subDomain), defaultPropVal));
  }

  default String getMandatoryStringProperty() {
    return getMandatoryStringProperty(null);
  }

  default String getMandatoryStringProperty(final String subDomain) {
    final var val = System.getProperty(subDomain == null ? topLevel() : resolve(subDomain));
    if (val == null || val.isEmpty()) {
      throw getMandatoryException(subDomain);
    }
    return val;
  }

  default OptionalInt getIntProperty() {
    return getIntProperty(null);
  }

  default OptionalInt getIntProperty(final String subDomain) {
    return getIntProperty(subDomain, null);
  }

  default OptionalInt getIntProperty(final String subDomain, final String defaultPropVal) {
    return getStringProperty(subDomain, defaultPropVal)
        .map(Integer::parseInt)
        .map(OptionalInt::of)
        .orElse(OptionalInt.empty());
  }

  default int getMandatoryIntProperty() {
    return getMandatoryIntProperty(null);
  }

  default int getMandatoryIntProperty(final String subDomain) {
    return getStringProperty(subDomain)
        .map(Integer::parseInt)
        .orElseThrow(getMandatoryExceptionSupplier(subDomain));
  }

  default OptionalLong getLongProperty() {
    return getLongProperty(null);
  }

  default OptionalLong getLongProperty(final String subDomain) {
    return getLongProperty(subDomain, null);
  }

  default OptionalLong getLongProperty(final String subDomain, final String defaultPropVal) {
    return getStringProperty(subDomain, defaultPropVal)
        .map(Long::parseLong)
        .map(OptionalLong::of)
        .orElse(OptionalLong.empty());
  }

  default long getMandatoryLongProperty() {
    return getMandatoryLongProperty(null);
  }

  default long getMandatoryLongProperty(final String subDomain) {
    return getStringProperty(subDomain)
        .map(Long::parseLong)
        .orElseThrow(getMandatoryExceptionSupplier(subDomain));
  }

  default OptionalDouble getDoubleProperty() {
    return getDoubleProperty(null);
  }

  default OptionalDouble getDoubleProperty(final String subDomain) {
    return getDoubleProperty(subDomain, null);
  }

  default OptionalDouble getDoubleProperty(final String subDomain, final String defaultPropVal) {
    return getStringProperty(subDomain, defaultPropVal)
        .map(Double::parseDouble)
        .map(OptionalDouble::of)
        .orElse(OptionalDouble.empty());
  }

  default double getMandatoryDoubleProperty() {
    return getMandatoryDoubleProperty(null);
  }

  default double getMandatoryDoubleProperty(final String subDomain) {
    return getStringProperty(subDomain)
        .map(Double::parseDouble)
        .orElseThrow(getMandatoryExceptionSupplier(subDomain));
  }

  default Optional<Boolean> getBooleanProperty() {
    return getBooleanProperty(null);
  }

  default Optional<Boolean> getBooleanProperty(final String subDomain) {
    return getBooleanProperty(subDomain, null);
  }

  default Optional<Boolean> getBooleanProperty(final String subDomain,
                                               final String defaultPropVal) {
    return getStringProperty(subDomain, defaultPropVal)
        .map(Boolean::parseBoolean);
  }

  default boolean getMandatoryBooleanProperty() {
    return getMandatoryBooleanProperty(null);
  }

  default boolean getMandatoryBooleanProperty(final String subDomain) {
    return getStringProperty(subDomain)
        .map(Boolean::parseBoolean)
        .orElseThrow(getMandatoryExceptionSupplier(subDomain));
  }
}

