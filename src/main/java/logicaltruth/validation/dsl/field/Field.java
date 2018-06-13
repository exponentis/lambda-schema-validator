package logicaltruth.validation.dsl.field;

import logicaltruth.validation.constraint.Constraint;

public class Field<T> {
  private String name;
  private Class<T> type;
  private Constraint<T> constraint;

  public Field(String name, Class<T> type, Constraint<T> constraint) {
    this.name = name;
    this.type = type;
    this.constraint = constraint;
  }

  public String getName() {
    return name;
  }

  public Class<T> getType() {
    return type;
  }

  public Constraint<T> getConstraint() {
    return constraint;
  }
}
