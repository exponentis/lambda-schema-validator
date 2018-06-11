package logicaltruth.validation.dsl;

import logicaltruth.validation.constraint.Constraint;
import logicaltruth.validation.constraint.ValidationResult;
import logicaltruth.validation.choice.Choice;

import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.Function;

public class ChoiceHelper {

  public static <T, R, C> Consumer<Choice<T, R, C>> when(C route, Function<T, R> handler) {
    return choice -> choice.when(route).then(handler);
  }

  public static <T, C> Constraint<T> constraintChoice(Function<T, C> router, Consumer<Choice<T, ValidationResult, C>>... fields) {
    Choice<T, ValidationResult, C> choice = Choice.with(router);
    Arrays.stream(fields).forEach(field -> field.accept(choice));
    return t -> choice.apply(t);
  }

  public static <T, C> Consumer<Choice<T, ValidationResult, C>> constraintCase(C route, Constraint<T> constraint) {
    Function<T, ValidationResult> handler = t -> constraint.validate(t);
    return when(route, handler);
  }
}
