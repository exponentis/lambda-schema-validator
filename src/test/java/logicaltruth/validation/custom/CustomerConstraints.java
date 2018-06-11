package logicaltruth.validation.custom;

import logicaltruth.validation.constraint.Constraint;
import logicaltruth.validation.constraint.impl.StandardConstraint;

public class CustomerConstraints {
  public static Constraint<Customer> customerExists() {
    return StandardConstraint.withPredicate(c -> true, "customer should exist");
  }

  public static Constraint<Customer> samePasswords() {
    return StandardConstraint.withPredicate(c -> c.getPassword1() == c.getPassword2(), "password1 and password2 should be the same");
  }

  public static CustomerType customerType(Customer c) {
    return c.getAge() > 16 ? CustomerType.ADULT : CustomerType.CHILD;
  }

  public enum CustomerType {
    ADULT, CHILD
  }
}
