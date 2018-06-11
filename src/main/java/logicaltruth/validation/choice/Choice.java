package logicaltruth.validation.choice;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

public class Choice<T, R, C> implements Function<T, R> {
  private Function<T, C> router;
  private Map<C, Function<T, R>> handlers = new HashMap<>();
  private Function<T, R> defaultValue;

  public static <T, R, C> Choice<T, R, C> choice(Function<T, C> router) {
    return new Choice().router(router);
  }

  public static <T, R, C> Choice<T, R, C> choice(Function<T, C> router, Consumer<Choice<T, R, C>>... fields) {
    Choice<T, R, C> sl = choice(router);
    Arrays.stream(fields).forEach(field -> field.accept(sl));
    return sl;
  }

  public Choice<T, R, C> router(Function<T, C> router) {
    this.router = router;
    return this;
  }

  public Acceptor<T, R, C> when(C route) {
    return new Acceptor<>(this, route);
  }

  public Choice<T, R, C> withDefault(Function<T, R> defaultValue) {
    this.defaultValue = defaultValue;
    return this;
  }

  public Choice<T, R, C> withDefault(R defaultValue) {
    this.defaultValue = t -> defaultValue;
    return this;
  }

  @Override
  public R apply(T t) {
    C route = router.apply(t);
    Function<T, R> handler = handlers.get(route);
    if(handler == null) {
      handler = defaultValue;
    }
    return handler.apply(t);
  }

  public static class Acceptor<T, R, C> {
    private final Choice<T, R, C> visitor;
    private final C route;

    Acceptor(Choice<T, R, C> visitor, C route) {
      this.visitor = visitor;
      this.route = route;
    }

    public Choice<T, R, C> then(Function<T, R> handler) {
      visitor.handlers.put(route, handler);
      return visitor;
    }

    public Choice<T, R, C> then(Consumer<T> handler, R toReturn) {
      visitor.handlers.put(route, t -> {
        handler.accept(t);
        return toReturn;
      });
      return visitor;
    }

    public Choice<T, R, C> then(Consumer<T> consumer) {
      return then(consumer, null);
    }
  }
}
