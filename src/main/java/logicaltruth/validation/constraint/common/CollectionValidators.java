package logicaltruth.validation.constraint.common;

import logicaltruth.validation.constraint.Constraint;
import logicaltruth.validation.constraint.ConstraintViolation;
import logicaltruth.validation.constraint.ValidationResult;

import java.util.List;
import java.util.Map;

public class CollectionValidators {
  public static <T> Constraint<List<T>> listConstraint(Constraint<T> constraint) {
    return l -> validateList(l, constraint);
  }

  public static <T> Constraint<Map<Object, T>> mapConstraint(Constraint<T> constraint) {
    return m -> validateMap(m, constraint);
  }

  public static <T> ValidationResult validateList(List<T> value, Constraint<T> constraint) {
    ValidationResult result = new ValidationResult(value);
    for(int i = 0; i < value.size(); i++) {
      T entry = value.get(i);
      ValidationResult vr = constraint.validate(entry);
      for(ConstraintViolation cv : vr.getConstraintViolations()) {
        String name = "[" + i + "]";
        cv.appendParentContext(name);
      }

      result.addConstraintViolations(vr.getConstraintViolations());
    }
    return result;
  }

  public static <T> ValidationResult validateMap(Map<Object, T> value, Constraint<T> constraint) {
    ValidationResult result = new ValidationResult(value);
    value.forEach((k, v) -> {
      ValidationResult vr = constraint.validate(v);
      for(ConstraintViolation cv : vr.getConstraintViolations()) {
        String name = "[" + k + "]";
        cv.appendParentContext(name);
      }

      result.addConstraintViolations(vr.getConstraintViolations());
    });

    return result;
  }
}
