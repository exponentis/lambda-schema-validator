package logicaltruth.validation.dsl.field;

import logicaltruth.validation.constraint.Constraint;

import java.util.*;

public class Fields {

  private SortedMap<String, Field> constraintMap = new TreeMap<>();
  private SortedMap<String, Fields> requiredChildMap = new TreeMap<>();
  private SortedMap<String, Fields> optionalChildMap = new TreeMap<>();


  public <T> Fields field(String name, Class<T> fieldType, Constraint<T> constraint) {
    constraintMap.put(name, new Field(name, fieldType, constraint));
    return this;
  }

  public <T> Fields listField(String name, Constraint<List<T>> constraint) {
    return field(name, (Class<List<T>>) (Object) List.class, constraint);
  }

  public <T> Fields listField(String name, Class<T> fieldType, Constraint<List<T>> constraint) {
    return field(name, (Class<List<T>>) (Object) List.class, constraint);
  }

  public <T> Fields mapField(String name, Constraint<Map<Object, T>> constraint) {
    return field(name, (Class<Map<Object, T>>) (Object) List.class, constraint);
  }

  public <T> Fields mapField(String name, Class<T> valueType, Constraint<Map<Object, T>> constraint) {
    return field(name, (Class<Map<Object, T>>) (Object) Map.class, constraint);
  }

  public Fields requiredField(String name, Fields child) {
    requiredChildMap.put(name, child);
    return this;
  }

  public Fields optionalField(String name, Fields child) {
    optionalChildMap.put(name, child);
    return this;
  }

  public List<Field> fields() {
    return new ArrayList(constraintMap.values());
  }

  public Map<String, Fields> requiredChildren() {
    return requiredChildMap;
  }

  public SortedMap<String, Fields> optionalChildren() {
    return optionalChildMap;
  }
}
