package logicaltruth.validation.constraint.common;

import logicaltruth.validation.constraint.Constraint;
import logicaltruth.validation.constraint.impl.StandardConstraint;

import static java.lang.String.format;


public class StringConstraints {

  public static Constraint<String> stringRequired = Value.<String>required();

  public static Constraint<String> minLength(int size) {
    return StandardConstraint.withPredicate((s) -> s != null && s.length() >= size, format("must have at least %s chars", size));
  }

  public static Constraint<String> maxLength(int size) {
    return StandardConstraint.withPredicate((s) -> s != null && s.length() <= size, format("must have at most %s chars", size));
  }

  public static Constraint<String> rangeLength(int minSize, int maxSize) {
    return minLength(minSize).and(maxLength(maxSize));
  }

  public static Constraint<String> contains(String c) {
    return StandardConstraint.withPredicate((s) -> s.contains(c), format("must contain %s", c));
  }

  public static Constraint<String> regex(String c) {
    return null;
  }
}
