package systems.comodal.pagerduty.config;


import java.lang.reflect.InvocationTargetException;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public final class ServiceUtil {

  private ServiceUtil() {
  }

  private static IllegalStateException providerClassNotFoundException(final String providerClassName) {
    return new IllegalStateException(String.format("There is no available provider of type %s.", providerClassName));
  }

  private static IllegalStateException providerNotFoundException(final ServiceLoader<?> serviceLoader) {
    serviceLoader.reload();
    if (!serviceLoader.iterator().hasNext()) {
      return new IllegalStateException("No Providers found by " + serviceLoader);
    }
    final var availableProviders = serviceLoader.stream().map(provider -> provider.type().getName()).collect(Collectors.joining(","));
    return new IllegalStateException("Provider not found by " + serviceLoader + ".\nAvailable:\n" + availableProviders);
  }

  @SuppressWarnings("unchecked")
  public static <S> S loadService(final ServiceLoader<S> serviceLoader,
                                  final String providerClassName) {
    if (providerClassName == null || providerClassName.isEmpty()) {
      return serviceLoader.findFirst().orElseThrow(() -> providerNotFoundException(serviceLoader));
    }
    try {
      final var providerClass = Class.forName(providerClassName);
      final var optionalProvider = serviceLoader.stream()
          .filter(serviceProvider -> providerClass.equals(serviceProvider.type())).map(ServiceLoader.Provider::get).findFirst();
      return optionalProvider.isPresent()
          ? optionalProvider.orElseThrow()
          : (S) providerClass.getDeclaredConstructor().newInstance();
    } catch (final ClassNotFoundException classNotFoundEx) {
      throw providerClassNotFoundException(providerClassName);
    } catch (final IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
      throw new IllegalStateException(String.format("Desired provider class %s does not have a zero argument public constructor.", providerClassName));
    }
  }

  public static <S> S loadService(final ServiceLoader<S> serviceLoader,
                                  final Predicate<ServiceLoader.Provider<S>> filter) {
    return tryLoadService(serviceLoader, filter).orElseThrow(() -> providerNotFoundException(serviceLoader));
  }

  static <S> Optional<S> tryLoadService(final ServiceLoader<S> serviceLoader,
                                        final Predicate<ServiceLoader.Provider<S>> filter) {
    return filter == null
        ? serviceLoader.findFirst()
        : serviceLoader.stream().filter(filter).map(ServiceLoader.Provider::get).findFirst();
  }
}

