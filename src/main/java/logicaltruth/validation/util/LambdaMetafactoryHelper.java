package logicaltruth.validation.util;

import java.lang.invoke.*;
import java.util.function.Function;

public class LambdaMetafactoryHelper {
  public static <B, F> Function<B, F> getGetterLambda(String methodName, Class<B> beanClass, Class<F> fieldClass) {
    try {
      MethodHandles.Lookup caller = MethodHandles.lookup();
      MethodType getter = MethodType.methodType(fieldClass);
      MethodHandle target = caller.findVirtual(beanClass, methodName, getter);
      MethodType func = target.type();
      CallSite site = LambdaMetafactory.metafactory(caller,
          "apply",
          MethodType.methodType(Function.class),
          func.generic(), target, func);

      MethodHandle factory = site.getTarget();
      return (Function<B, F>) factory.invoke();
    } catch(Throwable throwable) {
      throw new RuntimeException("Unable to build lambda getter");
    }
  }
}
