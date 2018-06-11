package logicaltruth.validation.dsl;

import logicaltruth.validation.constraint.Constraint;
import logicaltruth.validation.constraint.ValidationResult;
import logicaltruth.validation.constraint.impl.StandardConstraint;
import logicaltruth.validation.schema.BeanSchema;
import logicaltruth.validation.schema.MapSchema;
import logicaltruth.validation.schema.Schema;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class ValidationHelper {

  public static <T> Consumer<Schema> constraint(String name, Predicate<T> predicate, String message) {
    return s -> s.constraint(name, StandardConstraint.<T>withPredicate(predicate, message));
  }

  public static <T> Consumer<Schema> constraint(String name, Constraint<T> constraint) {
    return s -> s.constraint(name, constraint);
  }

  public static <T> Consumer<Schema> field(String name, Class<T> fieldType, Constraint<T> constraint) {
    return s -> s.field(name, fieldType, constraint);
  }

  public static <T> Consumer<Schema> listField(String name, Class<T> fieldType, Constraint<List<T>> constraint) {
    return s -> s.listField(name, fieldType, constraint);
  }

  public static <T> Consumer<Schema> mapField(String name, Class<T> fieldType, Constraint<Map<Object, T>> constraint) {
    return s -> s.mapField(name, fieldType, constraint);
  }

  private static void fields(Schema schema, Consumer<Schema>... fields) {
    Arrays.stream(fields).forEach(field -> field.accept(schema));
  }

  public static Schema<Map> schema(Consumer<Schema>... fields) {
    Schema<Map> schema = new MapSchema();
    fields(schema, fields);
    return schema;
  }

  public static <T> Schema<T> schema(Class<T> clazz, Consumer<Schema>... fields) {
    Schema<T> schema = new BeanSchema<T>(clazz);
    fields(schema, fields);
    return schema;
  }

  public static <T> Function<T, ValidationResult> asFunction(Constraint<T> constraint) {
    return t -> constraint.validate(t);
  }

  public static <T> Constraint<T> asConstraint(Function<T, ValidationResult> f) {
    return t -> f.apply(t);
  }

}
