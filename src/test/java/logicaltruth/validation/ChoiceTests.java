package logicaltruth.validation;

import logicaltruth.validation.constraint.Constraint;
import logicaltruth.validation.constraint.ValidationResult;
import logicaltruth.validation.constraint.common.StringConstraints;
import logicaltruth.validation.functional.Choice;
import logicaltruth.validation.schema.BeanSchema;
import org.junit.Test;

import java.util.function.Function;

import static logicaltruth.validation.CustomerConstraints.CustomerType.ADULT;
import static logicaltruth.validation.CustomerConstraints.CustomerType.CHILD;
import static logicaltruth.validation.constraint.common.StringConstraints.contains;
import static logicaltruth.validation.constraint.common.StringConstraints.stringRequired;
import static logicaltruth.validation.constraint.common.Value.required;
import static logicaltruth.validation.fluent.ChoiceHelper.constraintCase;
import static logicaltruth.validation.fluent.ChoiceHelper.constraintChoice;
import static logicaltruth.validation.fluent.ChoiceHelper.when;
import static logicaltruth.validation.fluent.ValidationHelper.asConstraint;
import static logicaltruth.validation.fluent.ValidationHelper.asFunction;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class ChoiceTests {
  @Test
  public void choice_simple_default() {
    Function<String, Integer> flow = Choice.<String, Integer, Boolean>with(x -> x.contains("a"))
      .when(true).then(x -> -1)
      .withDefault(String::length);

    Integer result = flow.apply("abc");
    assertThat(result, is(-1));

    result = flow.apply("xbc");
    assertThat(result, is(3));
  }

  @Test
  public void choice_simple_multiple() {
    Function<String, Integer> flow = Choice.with(x -> x.contains("a"),
      when(true, x -> -1),
      when(false, x -> 1));

    Integer result = flow.apply("abc");
    assertThat(result, is(-1));

    result = flow.apply("xbc");
    assertThat(result, is(1));
  }

  @Test
  public void constraint_cases_generic_choice() {

    Function<String, ValidationResult> aSwitch =
      Choice.with(x -> x.length() > 2,
        constraintCase(true, contains("a")),
        constraintCase(false, contains("b"))
      );

    ValidationResult result = aSwitch.apply("a12");
    assertThat(result.isValid(), is(true));

    result = aSwitch.apply("a1");
    assertThat(result.isValid(), is(false));

    result = aSwitch.apply("b12");
    assertThat(result.isValid(), is(false));

    result = aSwitch.apply("b1");
    assertThat(result.isValid(), is(true));
  }

  @Test
  public void constraint_choice() {

    Constraint<String> constraint =
      constraintChoice(x -> x.length() > 2,
        constraintCase(true, contains("a")),
        constraintCase(false, contains("b"))
      );

    ValidationResult result = constraint.validate("a12");
    assertThat(result.isValid(), is(true));

    result = constraint.validate("a1");
    assertThat(result.isValid(), is(false));

    result = constraint.validate("b12");
    assertThat(result.isValid(), is(false));

    result = constraint.validate("b1");
    assertThat(result.isValid(), is(true));
  }

  @Test
  public void constraint_generic_choice() {

    Function<String, ValidationResult> aSwitch =
      Choice.with(x -> x.length() > 2,
        when(true, s -> contains("a").validate(s)),
        when(false, asFunction(contains("b")))
      );

    ValidationResult result = aSwitch.apply("a12");
    assertThat(result.isValid(), is(true));

    result = aSwitch.apply("a1");
    assertThat(result.isValid(), is(false));

    result = aSwitch.apply("b12");
    assertThat(result.isValid(), is(false));

    result = aSwitch.apply("b1");
    assertThat(result.isValid(), is(true));
  }

  @Test
  public void constraint_choice_variation() {
    Constraint<String> constraint = asConstraint(
      Choice.with(x -> x.length() > 2,
        when(true, s -> contains("a").validate(s)),
        when(false, asFunction(contains("b")))
      ));

    ValidationResult result = constraint.validate("a12");
    assertThat(result.isValid(), is(true));

    result = constraint.validate("a1");
    assertThat(result.isValid(), is(false));

    result = constraint.validate("b12");
    assertThat(result.isValid(), is(false));

    result = constraint.validate("b1");
    assertThat(result.isValid(), is(true));
  }

  @Test
  public void constraint_choice_complex() {

    Constraint<Customer> adultValidator = new BeanSchema<>(Customer.class)
      .field("driversLicense", String.class, stringRequired);

    Constraint<Customer> childValidator = new BeanSchema<>(Customer.class);

    Customer adult = new Customer();
    adult.setAge(25);

    Customer child = new Customer();
    child.setAge(15);

    Constraint<Customer> constraint = constraintChoice(CustomerConstraints::customerType,
      constraintCase(ADULT, adultValidator),
      constraintCase(CHILD, childValidator));

    ValidationResult result = constraint.validate(adult);
    assertThat(result.isValid(), is(false));
    assertThat(result.getConstraintViolations().get(0).getContext(), is(".driversLicense"));

    adult.setDriversLicense("ABCDEFGH");
    result = constraint.validate(adult);
    assertThat(result.isValid(), is(true));

    result = constraint.validate(child);
    assertThat(result.isValid(), is(true));
  }
}
