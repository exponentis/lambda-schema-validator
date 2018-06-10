package logicaltruth.validation.schema;

import logicaltruth.validation.constraint.Constraint;
import logicaltruth.validation.util.LambdaMetafactoryHelper;

import java.util.function.Function;

public class BeanSchema<K> extends Schema<K> {
  private Class<K> clazz;

  public BeanSchema(Class<K> clazz) {
    this.clazz = clazz;
  }

  @Override
  public <T> Schema<K> field(String name, Class<T> fieldType, Constraint<T> constraint) {
    String properName = name.substring(0, 1).toUpperCase() + name.substring(1);
    Function<K, T> lens = LambdaMetafactoryHelper.getGetterLambda("get" + properName, clazz, fieldType);
    projection(name, lens, constraint);
    return this;
  }
}
