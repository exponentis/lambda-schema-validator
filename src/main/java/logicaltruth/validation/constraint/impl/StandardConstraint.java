package logicaltruth.validation.constraint.impl;

import logicaltruth.validation.constraint.Constraint;
import logicaltruth.validation.constraint.ConstraintViolation;
import logicaltruth.validation.constraint.ValidationResult;

import java.util.Collections;
import java.util.function.Predicate;

public class StandardConstraint<K> implements Constraint<K> {

  private Predicate<K> predicate;
  private String message;

  private StandardConstraint(Predicate<K> predicate, String message) {
    this.predicate = predicate;
    this.message = message;
  }

  public static <K> StandardConstraint<K> withPredicate(Predicate<K> predicate, String message) {
    return new StandardConstraint<K>(predicate, message);
  }

  public static <K> StandardConstraint<K> withPredicate(Predicate<K> predicate) {
    return new StandardConstraint<K>(predicate, null);
  }

  public StandardConstraint<K> withMessage(String message) {
    return withPredicate(predicate, message);
  }

  @Override
  public ValidationResult validate(K value) {
    ValidationResult result = new ValidationResult(value);
    if(predicate.test(value)) {
      return result;
    }

    result.addConstraintViolations(Collections.singletonList(new ConstraintViolation(message)));
    return result;
  }

}
