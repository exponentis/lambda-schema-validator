package logicaltruth.validation.constraint;

@FunctionalInterface
public interface Constraint<K> {

  ValidationResult validate(K value);

  default Constraint<K> and(Constraint<K> other) {
    return value -> {
      ValidationResult firstResult = Constraint.this.validate(value);
      if(firstResult.isBreak()) {
        return firstResult;
      }

      ValidationResult otherResult = other.validate(value);
      if(!firstResult.isValid() && !otherResult.isValid()) {
        ValidationResult result = new ValidationResult(value);
        result.addConstraintViolations(firstResult.getConstraintViolations());
        result.addConstraintViolations(otherResult.getConstraintViolations());
        return result;
      }

      if(!firstResult.isValid())
        return firstResult;
      if(!otherResult.isValid())
        return otherResult;

      return new ValidationResult(value);
    };
  }

  default Constraint<K> or(Constraint<K> other) {
    return value -> {
      ValidationResult firstResult = Constraint.this.validate(value);
      if(firstResult.isValid()) {
        return new ValidationResult(value);
      }

      if(firstResult.isBreak()) {
        return firstResult;
      }
      ValidationResult otherResult = other.validate(value);
      if(otherResult.isValid()) {
        return new ValidationResult(value);
      }


      ValidationResult result = new ValidationResult(value);
      result.addConstraintViolations(firstResult.getConstraintViolations());
      result.addConstraintViolations(otherResult.getConstraintViolations());
      return result;
    };
  }

  default Constraint<K> orElseThrow() {
    return value -> {
      ValidationResult result = Constraint.this.validate(value);
      if(!result.isValid())
        throw new RuntimeException("INVALID");
      return result;
    };
  }

  default Constraint<K> orElseThrow(Throwable t) {
    return value -> {
      ValidationResult result = Constraint.this.validate(value);
      if(!result.isValid())
        throw new RuntimeException(t);
      return result;
    };
  }

  default Constraint<K> orElseBreak() {
    return value -> {
      ValidationResult result = Constraint.this.validate(value);
      if(!result.isValid()) {
        result.setBreak(true);
        return result;
      }
      return result;
    };
  }
}
