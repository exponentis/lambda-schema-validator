package logicaltruth.validation.schema;

import logicaltruth.validation.constraint.Constraint;
import logicaltruth.validation.constraint.ValidationResult;

import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.Function;

import static logicaltruth.validation.constraint.ConstraintViolation.ROOT_CONTEXT;

public abstract class Schema<K> implements Constraint<K> {
  private SortedMap<String, Constraint<K>> fieldConstraintMap = new TreeMap<>();

  public <T> Schema<K> constraint(String name, Constraint<K> constraint) {
    fieldConstraintMap.put(name, constraint);
    return this;
  }

  public <T> Schema<K> projection(String name, Function<K, T> lens, Constraint<T> constraint) {
    constraint(name, value -> constraint.validate(lens.apply(value)));
    return this;
  }

  public <T> Schema<K> field(String name, Class<T> fieldType, Constraint<T> constraint) {
    return projection(name, fieldGetter(name, fieldType), constraint);
  }

  public <T> Schema<K> listField(String name, Constraint<List<T>> constraint) {
    return field(name, (Class<List<T>>) (Object) List.class, constraint);
  }

  public <T> Schema<K> listField(String name, Class<T> fieldType, Constraint<List<T>> constraint) {
    return field(name, (Class<List<T>>) (Object) List.class, constraint);
  }

  public <T> Schema<K> mapField(String name, Constraint<Map<Object, T>> constraint) {
    return field(name, (Class<Map<Object, T>>) (Object) List.class, constraint);
  }

  public <T> Schema<K> mapField(String name, Class<T> valueType, Constraint<Map<Object, T>> constraint) {
    return field(name, (Class<Map<Object, T>>) (Object) Map.class, constraint);
  }

  @Override
  public ValidationResult validate(K value) {
    ValidationResult results = new ValidationResult(value);
    fieldConstraintMap.forEach((name, v) -> {
      ValidationResult result = v.validate(value);
      result.getConstraintViolations().forEach(cv -> cv.appendContext(ROOT_CONTEXT + name));
      results.addConstraintViolations(result.getConstraintViolations());
    });

    return results;
  }

  public abstract <T> Function<K, T> fieldGetter(String name, Class<T> fieldType);
}
