package logicaltruth.validation.schema;

import logicaltruth.validation.constraint.Constraint;

import java.util.Map;
import java.util.function.Function;

public class MapSchema extends Schema<Map> {

  @Override
  public <T> Schema<Map> field(String name, Class<T> fieldType, Constraint<T> constraint) {
    Function<Map, T> lens = value -> (T) value.get(name);
    return projection(name, lens, constraint);
  }
}

