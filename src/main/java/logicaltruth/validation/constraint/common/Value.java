package logicaltruth.validation.constraint.common;

import logicaltruth.validation.constraint.Constraint;

import java.util.List;
import java.util.Map;

public class Value {
  public static <T> Constraint<T> required() {
    return StandardConstraint.<T>withPredicate((s) -> s != null, "must not be null");
  }

  public static <T> Constraint<T> required(Class<T> clazz) {
    return StandardConstraint.<T>withPredicate((s) -> s != null, "must not be null");
  }

  public static <T> Constraint<T> optional() {
    return StandardConstraint.<T>withPredicate((s) -> s == null, "must be null");
  }

  public static <T> Constraint<T> optional(Class<T> clazz) {
    return StandardConstraint.<T>withPredicate((s) -> s == null, "must be null");
  }

  public static <T> Constraint<List<T>> listRequired() {
    return Value.required();
  }

  public static <T> Constraint<List<T>> listRequired(Class<T> clazz) {
    return Value.required();
  }

  public static <T> Constraint<Map<Object, T>> mapRequired() {
    return Value.required();
  }

  public static <T> Constraint<Map<Object, T>> mapRequired(Class<T> clazz) {
    return Value.required();
  }
}
