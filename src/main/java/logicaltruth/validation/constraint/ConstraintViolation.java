package logicaltruth.validation.constraint;

public class ConstraintViolation {
  public static final String ROOT_CONTEXT = ".";

  private String context = ROOT_CONTEXT;
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
    if(ROOT_CONTEXT.equals(context)) {
      context = name;
    } else {
      context = name.concat(context);
    }
  }
}
