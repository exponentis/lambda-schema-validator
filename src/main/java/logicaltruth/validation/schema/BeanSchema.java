package logicaltruth.validation.schema;

import logicaltruth.validation.util.LambdaMetafactoryHelper;

import java.util.function.Function;

public class BeanSchema<K> extends Schema<K> {
  private Class<K> clazz;

  public BeanSchema(Class<K> clazz) {
    this.clazz = clazz;
  }

  @Override
  public <T> Function<K, T> fieldGetter(String name, Class<T> fieldType) {
    String properName = name.substring(0, 1).toUpperCase() + name.substring(1);
    return LambdaMetafactoryHelper.getGetterLambda("get" + properName, clazz, fieldType);
  }
}
