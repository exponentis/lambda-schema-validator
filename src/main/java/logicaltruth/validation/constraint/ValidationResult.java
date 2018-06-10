package logicaltruth.validation.constraint;

import java.util.ArrayList;
import java.util.List;

public class ValidationResult {

  private boolean isBreak;

  private Object value;

  private List<ConstraintViolation> list = new ArrayList<>();

  public ValidationResult(Object value) {
    this.value = value;
  }

  public boolean isBreak() {
    return isBreak;
  }

  public void setBreak(boolean aBreak) {
    isBreak = aBreak;
  }

  public boolean isValid() {
    return list.isEmpty();
  }

  public void addConstraintViolations(List<ConstraintViolation> cv) {
    list.addAll(cv);
  }

  public List<ConstraintViolation> getConstraintViolations() {
    return list;
  }

  public <K> K getValue() {
    return (K) value;
  }
}
