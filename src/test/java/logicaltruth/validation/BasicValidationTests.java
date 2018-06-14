package logicaltruth.validation;

import logicaltruth.validation.constraint.ValidationResult;
import org.junit.Test;

import static logicaltruth.validation.constraint.common.StringConstraints.*;
import static logicaltruth.validation.constraint.impl.StandardConstraint.withPredicate;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.Assert.assertEquals;

public class BasicValidationTests {

  @Test
  public void string_contraint_valid() {
    ValidationResult result = stringRequired.orElseThrow().and(rangeLength(2, 5)).validate("ab");
    assertEquals(result.isValid(), true);
    assertEquals(result.getValue(), "ab");
    assertThat(result.getConstraintViolations(), hasSize(0));
  }

  @Test
  public void string_constraint_invalid() {
    ValidationResult result = stringRequired.orElseThrow().and(rangeLength(2, 5)).validate("a");
    assertEquals(result.isValid(), false);
    assertEquals(result.getValue(), "a");
    assertThat(result.getConstraintViolations(), hasSize(1));
    assertEquals(result.getConstraintViolations().get(0).getContext(), ".");
  }

  @Test
  public void string_constraint_invalid_null() {
    ValidationResult result = stringRequired.orElseBreak().and(rangeLength(2, 5)).validate(null);
    assertEquals(result.isValid(), false);
    assertEquals(result.getValue(), null);
    assertThat(result.getConstraintViolations(), hasSize(1));
    assertEquals(result.getConstraintViolations().get(0).getContext(), ".");
  }

  @Test
  public void string_constraint_valid_or_left() {
    ValidationResult result = stringRequired.orElseThrow()
      .and(maxLength(2).or(minLength(4)))
      .validate("a");

    assertEquals(result.isValid(), true);
    assertEquals(result.getValue(), "a");
    assertThat(result.getConstraintViolations(), hasSize(0));
  }

  @Test
  public void string_constraint_valid_or_right() {
    ValidationResult result = stringRequired.orElseBreak()
      .and(maxLength(2).or(minLength(4)))
      .validate("abcd");

    assertEquals(result.isValid(), true);
    assertEquals(result.getValue(), "abcd");
    assertThat(result.getConstraintViolations(), hasSize(0));
  }

  @Test
  public void string_constraint_invalid_or() {
    ValidationResult result = stringRequired.orElseBreak()
      .and(maxLength(2).or(minLength(4)))
      .validate("abc");

    assertEquals(result.isValid(), false);
    assertEquals(result.getValue(), "abc");
    assertThat(result.getConstraintViolations(), hasSize(2));
  }

  @Test
  public void string_constraint_withPredicate_valid_or_left() {
    ValidationResult result = stringRequired.orElseThrow()
      .and(maxLength(2).or(minLength(4)))
      .and(withPredicate(s -> s.contains("a"), "Oops, no 'a'"))
      .validate("ab");

    assertEquals(result.isValid(), true);
    assertEquals(result.getValue(), "ab");
    assertThat(result.getConstraintViolations(), hasSize(0));
  }

  @Test
  public void string_constraint_withPredicate_valid_or_right() {
    ValidationResult result = stringRequired.orElseThrow()
      .and(maxLength(2).or(minLength(4)))
      .and(withPredicate(s -> s.contains("a"), "Oops, no 'a'"))
      .validate("abcd");

    assertEquals(result.isValid(), true);
    assertEquals(result.getValue(), "abcd");
    assertThat(result.getConstraintViolations(), hasSize(0));
  }

  @Test
  public void string_constraint_withPredicate_invalid_or() {
    ValidationResult result = stringRequired.orElseThrow()
      .and(maxLength(2).or(minLength(4)))
      .and(withPredicate(s -> s.contains("a"), "Oops, no 'a'"))
      .validate("xb");

    assertEquals(result.isValid(), false);
    assertEquals(result.getValue(), "xb");
    assertThat(result.getConstraintViolations(), hasSize(1));
  }
}
