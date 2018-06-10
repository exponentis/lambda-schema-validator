package logicaltruth.validation.fluent;

import logicaltruth.validation.constraint.Constraint;
import logicaltruth.validation.constraint.ValidationResult;
import logicaltruth.validation.functional.Choice;

import java.util.function.Consumer;
import java.util.function.Function;

public class ChoiceHelper {

  public static <T, R, C> Consumer<Choice<T, R, C>> when(C route, Function<T, R> handler) {
    return sl -> sl.when(route).then(handler);
  }

  public static <T, C> Constraint<T> constraintChoice(Function<T, C> router, Consumer<Choice<T, ValidationResult, C>>... fields) {
    Choice<T, ValidationResult, C> sl = Choice.with(router);
    for(Consumer<Choice<T, ValidationResult, C>> field : fields) {
      field.accept(sl);
    }
    return t -> sl.apply(t);
  }

  public static <T, C> Consumer<Choice<T, ValidationResult, C>> constraintCase(C route, Constraint<T> constraint) {
    Function<T, ValidationResult> f = t -> constraint.validate(t);
    return sl -> sl.when(route).then(f);
  }
}
