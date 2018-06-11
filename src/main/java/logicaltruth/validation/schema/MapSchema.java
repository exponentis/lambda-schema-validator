package logicaltruth.validation.schema;

import logicaltruth.validation.constraint.Constraint;

import java.util.Map;
import java.util.function.Function;

public class MapSchema extends Schema<Map> {

  @Override
  public <T> Function<Map, T> fieldGetter(String name, Class<T> fieldType) {
    return value -> (T) value.get(name);
  }
}

