package logicaltruth.validation.dsl;

import logicaltruth.validation.constraint.Constraint;
import logicaltruth.validation.constraint.ValidationResult;
import logicaltruth.validation.constraint.common.Value;
import logicaltruth.validation.constraint.impl.StandardConstraint;
import logicaltruth.validation.dsl.field.Fields;
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

  private static void listOfFields(Schema schema, Consumer<Schema>... fields) {
    Arrays.stream(fields).forEach(field -> field.accept(schema));
  }

  public static Schema<Map> schema(Consumer<Schema>... fields) {
    Schema<Map> schema = new MapSchema();
    listOfFields(schema, fields);
    return schema;
  }

  public static Schema<Map> schema(Fields listCollection, Consumer<Schema>... fields) {
    Schema<Map> schema = new MapSchema();
    listCollection.fields().forEach(f -> schema.field(f.getName(), f.getType(), f.getConstraint()));
    listCollection.requiredChildren().forEach((k, v) -> schema.field(k, Map.class,
      Value.<Map>required().orElseBreak().and(schema(v))));
    listCollection.optionalChildren().forEach((k, v) -> schema.field(k, Map.class,
      Value.<Map>optional().or(schema(v))));
    listOfFields(schema, fields);
    return schema;
  }

  public static <T> Schema<T> schema(Class<T> clazz, Consumer<Schema>... fields) {
    Schema<T> schema = new BeanSchema<T>(clazz);
    listOfFields(schema, fields);
    return schema;
  }

  public static <T> Schema<T> schema(Class<T> clazz, Fields listCollection, Consumer<Schema>... fields) {
    Schema<T> schema = new BeanSchema<T>(clazz);
    listCollection.fields().forEach(f -> schema.field(f.getName(), f.getType(), f.getConstraint()));
    listOfFields(schema, fields);
    return schema;
  }

  public static Fields fields(Consumer<Fields>... fields) {
    Fields schema = new Fields();
    listOfFields(schema, fields);
    return schema;
  }

  private static void listOfFields(Fields schema, Consumer<Fields>... fields) {
    Arrays.stream(fields).forEach(field -> field.accept(schema));
  }

  public static <T> Consumer<Fields> _field(String name, Class<T> fieldType, Constraint<T> constraint) {
    return s -> s.field(name, fieldType, constraint);
  }

  public static <T> Consumer<Fields> _listField(String name, Class<T> fieldType, Constraint<List<T>> constraint) {
    return s -> s.listField(name, fieldType, constraint);
  }

  public static <T> Consumer<Fields> _mapField(String name, Class<T> fieldType, Constraint<Map<Object, T>> constraint) {
    return s -> s.mapField(name, fieldType, constraint);
  }

  public static <T> Consumer<Fields> _requiredField(String name, Fields fields) {
    return s -> s.requiredField(name, fields);
  }

  public static <T> Consumer<Fields> _optionalField(String name, Fields fields) {
    return s -> s.optionalField(name, fields);
  }

  public static <T> Function<T, ValidationResult> asFunction(Constraint<T> constraint) {
    return t -> constraint.validate(t);
  }

  public static <T> Constraint<T> asConstraint(Function<T, ValidationResult> f) {
    return t -> f.apply(t);
  }

}
