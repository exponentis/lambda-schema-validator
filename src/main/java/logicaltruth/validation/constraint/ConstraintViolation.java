package logicaltruth.validation.constraint;

public class ConstraintViolation {
  private String context = ".";
  private String message;

  public ConstraintViolation(String message) {
    this.message = message;
  }

  public String getMessage() {
    return message;
  }

  public String getContext() {
    return context;
  }

  public void appendParentContext(String name) {
    if(context == ".") {
      context = name;
    } else {
      context = name.concat(context);
    }
  }
}
