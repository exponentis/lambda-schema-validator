package logicaltruth.validation.constraint.common;

import logicaltruth.validation.constraint.Constraint;
import logicaltruth.validation.constraint.impl.StandardConstraint;

import static java.lang.String.format;

public class IntegerConstraints {

  public static Constraint<Integer> integerRequired = Value.<Integer>required();

  public static Constraint<Integer> lessThan(Integer max) {
    return StandardConstraint.withPredicate(i -> i != null && i < max, format("must be less than %s", max));
  }

  public static Constraint<Integer> max(Integer max) {
    return StandardConstraint.withPredicate(i -> i != null && i <= max, format("must be at most %s", max));
  }

  public static Constraint<Integer> greaterThan(Integer min) {
    return StandardConstraint.withPredicate(i -> i != null && i > min, format("must be greater than %s", min));
  }

  public static Constraint<Integer> min(Integer min) {
    return StandardConstraint.withPredicate(i -> i != null && i >= min, format("must be at least %s", min));
  }

  public static Constraint<Integer> integerRange(Integer min, Integer max) {
    return min(min).and(max(max));
  }

}
