package logicaltruth.validation;

import logicaltruth.validation.constraint.ValidationResult;
import logicaltruth.validation.constraint.common.Value;
import logicaltruth.validation.custom.Address;
import logicaltruth.validation.custom.Customer;
import logicaltruth.validation.dsl.field.Fields;
import logicaltruth.validation.schema.BeanSchema;
import logicaltruth.validation.schema.MapSchema;
import logicaltruth.validation.schema.Schema;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static logicaltruth.validation.constraint.common.CollectionValidators.*;
import static logicaltruth.validation.constraint.common.IntegerConstraints.*;
import static logicaltruth.validation.constraint.common.StringConstraints.*;
import static logicaltruth.validation.constraint.common.Value.mapRequired;
import static logicaltruth.validation.constraint.impl.StandardConstraint.withPredicate;
import static logicaltruth.validation.custom.CustomerConstraints.customerExists;
import static logicaltruth.validation.custom.CustomerConstraints.samePasswords;
import static logicaltruth.validation.dsl.ValidationHelper.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.Assert.assertEquals;

public class SchemaDslTests {
  @Test
  public void map_schema_nested_declarative_valid() {

    Schema<Map> mapSchema = schema(
      field("name", String.class, stringRequired.and(rangeLength(2, 5))),
      field("nested1", Map.class,
        schema(
          field("inner1", String.class, stringRequired.and(contains("x"))),
          field("nested2", Map.class,
            schema(
              field("inner2", String.class, stringRequired.and(contains("y")))
            )
          )
        )
      )
    );

    Map value = new HashMap() {{
      put("name", "1234");
      put("nested1", new HashMap() {{
        put("inner1", "xa");
        put("nested2", new HashMap() {{
          put("inner2", "yb");
        }});
      }});
    }};

    ValidationResult result = mapSchema.validate(value);

    assertEquals(result.isValid(), true);
    assertThat(result.getValue(), is(value));
    assertThat(result.getConstraintViolations(), hasSize(0));
  }

  @Test
  public void map_schema_deep_nested_declarative_invalid() {

    Schema<Map> mapSchema = schema(
      field("name", String.class, stringRequired.and(rangeLength(2, 5))),
      field("nested1", Map.class,
        schema(
          field("inner1", String.class, stringRequired.and(contains("x"))),
          field("nested2", Map.class,
            schema(
              field("inner2", String.class, stringRequired.and(contains("y")))
            )
          )
        )
      )
    );

    Map value = new HashMap() {{
      put("name", "123456");
      put("nested1", new HashMap() {{
        put("inner1", "za");
        put("nested2", new HashMap() {{
          put("inner2", "zb");
        }});
      }});
    }};

    ValidationResult result = mapSchema.validate(value);

    assertEquals(result.isValid(), false);
    assertThat(result.getValue(), is(value));
    assertThat(result.getConstraintViolations(), hasSize(3));
    assertEquals(result.getConstraintViolations().get(0).getContext(), ".name");
    assertEquals(result.getConstraintViolations().get(1).getContext(), ".nested1.inner1");
    assertEquals(result.getConstraintViolations().get(2).getContext(), ".nested1.nested2.inner2");
  }

  @Test
  public void schema_fields_map_list_valid() {

    Schema<Map> customerSchema = new MapSchema()
      .field("name", String.class, stringRequired.and(rangeLength(2, 5)))
      .field("age", Integer.class, integerRequired.orElseBreak().and(greaterThan(18)))
      .listField("someList", Integer.class, Value.<Integer>listRequired().orElseBreak().and(listConstraint(max(5))))
      .mapField("someMap", String.class, mapRequired(String.class).orElseBreak().and((mapConstraint(contains("x")))));

    Map customer = new HashMap() {{
      put("name", "abc");
      put("age", 25);
      put("someList", Arrays.asList(1, 3, 4, 2));
      put("someMap", new HashMap() {{
        put("a", "x1");
        put("b", "xyz");
        put("c", "x2");
      }});
    }};

    ValidationResult result = customerSchema.validate(customer);

    assertEquals(result.isValid(), true);
    assertThat(result.getValue(), is(customer));
    assertThat(result.getConstraintViolations(), hasSize(0));
  }

  @Test
  public void schema_fields_map_list_invalid() {

    Schema<Map> customerSchema = new MapSchema()
      .field("name", String.class, stringRequired.and(rangeLength(2, 5)))
      .field("age", Integer.class, integerRequired.orElseBreak().and(greaterThan(18)))
      .listField("someList", Integer.class, Value.<Integer>listRequired().orElseBreak().and(listConstraint(max(5))))
      .mapField("someMap", String.class, mapRequired(String.class).orElseBreak().and((mapConstraint(contains("x")))));

    Map customer = new HashMap() {{
      put("name", "abcdef");
      put("age", 25);
      put("someList", Arrays.asList(1, 3, 4, 7, 2));
      put("someMap", new HashMap() {{
        put("a", "x1");
        put("b", "yz");
        put("c", "x2");
      }});
    }};

    ValidationResult result = customerSchema.validate(customer);

    assertEquals(result.isValid(), false);
    assertThat(result.getValue(), is(customer));
    assertThat(result.getConstraintViolations(), hasSize(3));
    assertEquals(result.getConstraintViolations().get(0).getContext(), ".name");
    assertEquals(result.getConstraintViolations().get(1).getContext(), ".someList[3]");
    assertEquals(result.getConstraintViolations().get(2).getContext(), ".someMap[b]");
  }

  @Test
  public void bean_schema_complex_valid() {

    Schema<Address> addressSchema = schema(Address.class,
      field("street", String.class, stringRequired.and(maxLength(10))));

    Schema<Customer> customerSchema = schema(Customer.class,
      field("name", String.class, stringRequired.and(rangeLength(2, 5))),
      field("age", Integer.class, integerRequired.orElseBreak().and(greaterThan(18))),
      listField("someList", Integer.class, Value.<Integer>listRequired().orElseBreak().and(listConstraint(max(5)))),
      mapField("someMap", String.class, mapRequired(String.class).orElseBreak().and((mapConstraint(contains("x"))))),
      field("password1", String.class, stringRequired),
      field("password2", String.class, stringRequired),
      constraint("passwords", samePasswords()),
      constraint("exists", customerExists()),
      field("address", Address.class, addressSchema)
    );

    Address address = new Address();
    address.setStreet("0123456789");

    Customer customer = new Customer();
    customer.setName("abc");
    customer.setAge(25);
    customer.setPassword1("pass1");
    customer.setPassword2("pass1");
    customer.setAddress(address);

    customer.setSomeList(Arrays.asList(1, 3, 4, 2, 0));

    customer.setSomeMap(new HashMap<String, String>() {{
      put("a", "x1");
      put("b", "xyz");
      put("c", "x2");
    }});

    ValidationResult result = customerSchema.validate(customer);

    assertEquals(result.isValid(), true);
    assertThat(result.getValue(), is(customer));
    assertThat(result.getConstraintViolations(), hasSize(0));
  }

  @Test
  public void bean_schema_complex_invalid() {

    Schema<Address> addressSchema = schema(Address.class,
      field("street", String.class, stringRequired.and(maxLength(10))));

    Schema<Customer> customerSchema = schema(Customer.class,
      field("name", String.class, stringRequired.and(rangeLength(2, 5))),
      field("age", Integer.class, integerRequired.orElseBreak().and(greaterThan(18))),
      listField("someList", Integer.class, Value.<Integer>listRequired().orElseBreak().and(listConstraint(max(5)))),
      mapField("someMap", String.class, mapRequired(String.class).orElseBreak().and((mapConstraint(contains("x"))))),
      field("password1", String.class, stringRequired),
      field("password2", String.class, stringRequired),
      constraint("passwords", samePasswords()),
      constraint("exists", customerExists()),
      field("address", Address.class, addressSchema)
    );

    Address address = new Address();
    address.setStreet("0123456789x");

    Customer customer = new Customer();
    customer.setName("abcdef");
    customer.setAge(15);
    customer.setPassword1("pass1");
    customer.setPassword2("pass2");
    customer.setAddress(address);

    customer.setSomeList(Arrays.asList(1, 3, 4, 2, 6));

    customer.setSomeMap(new HashMap<String, String>() {{
      put("a", "x1");
      put("b", "yz");
      put("c", "x2");
    }});

    ValidationResult result = customerSchema.validate(customer);

    assertEquals(result.isValid(), false);
    assertThat(result.getValue(), is(customer));
    assertThat(result.getConstraintViolations(), hasSize(6));
    assertEquals(result.getConstraintViolations().get(0).getContext(), ".address.street");
    assertEquals(result.getConstraintViolations().get(1).getContext(), ".age");
    assertEquals(result.getConstraintViolations().get(2).getContext(), ".name");
    assertEquals(result.getConstraintViolations().get(3).getContext(), ".passwords");
    assertEquals(result.getConstraintViolations().get(4).getContext(), ".someList[4]");
    assertEquals(result.getConstraintViolations().get(5).getContext(), ".someMap[b]");
  }

  @Test
  public void map_schema_complex_valid() {

    Schema<Map> customerSchema = schema(
      field("name", String.class, stringRequired.and(rangeLength(2, 5))),
      field("age", Integer.class, integerRequired.orElseBreak().and(greaterThan(18))),
      listField("someList", Integer.class, Value.<Integer>listRequired().orElseBreak().and(listConstraint(max(5)))),
      mapField("someMap", String.class, mapRequired(String.class).orElseBreak().and((mapConstraint(contains("x")))))
    );

    Map customer = new HashMap() {{
      put("name", "abc");
      put("age", 25);
      put("someList", Arrays.asList(1, 3, 4, 2));
      put("someMap", new HashMap() {{
        put("a", "x1");
        put(8, "xyz");
        put("c", "x2");
      }});
    }};

    ValidationResult result = customerSchema.validate(customer);

    assertEquals(result.isValid(), true);
    assertThat(result.getValue(), is(customer));
    assertThat(result.getConstraintViolations(), hasSize(0));
  }

  @Test
  public void map_schema_complex_invalid() {

    Schema<Map> customerSchema = schema(
      field("name", String.class, stringRequired.and(rangeLength(2, 5))),
      field("age", Integer.class, integerRequired.orElseBreak().and(greaterThan(18))),
      listField("someList", Integer.class, Value.<Integer>listRequired().orElseBreak().and(listConstraint(max(5)))),
      mapField("someMap", String.class, mapRequired(String.class).orElseBreak().and((mapConstraint(contains("x")))))
    );

    Map customer = new HashMap() {{
      put("name", "abcdef");
      put("age", 25);
      put("someList", Arrays.asList(1, 3, 7, 2));
      put("someMap", new HashMap() {{
        put("a", "x1");
        put(8, "yz");
        put("c", "x2");
      }});
    }};

    ValidationResult result = customerSchema.validate(customer);

    assertEquals(result.isValid(), false);
    assertThat(result.getValue(), is(customer));
    assertThat(result.getConstraintViolations(), hasSize(3));
    assertEquals(result.getConstraintViolations().get(0).getContext(), ".name");
    assertEquals(result.getConstraintViolations().get(1).getContext(), ".someList[2]");
    assertEquals(result.getConstraintViolations().get(2).getContext(), ".someMap[8]");
  }

  @Test
  public void schema_nested_list_of_maps_valid() {

    Schema<Map> mapSchema = schema(
      field("name", String.class, stringRequired.and(rangeLength(2, 5))),
      listField("innerList", Map.class, Value.<Map>listRequired().orElseBreak().and(listConstraint(
        schema(
          mapField("innerInnerMap", String.class, mapRequired(String.class).orElseBreak().and((mapConstraint(contains("x")))))
        )
      )))
    );

    Map customer = new HashMap() {{
      put("name", "abcde");
      put("innerList", Arrays.asList(
        new HashMap() {{
          put("innerInnerMap", new HashMap() {{
            put("a", "x1");
            put("b", "x11");
            put("c", "x111");
          }});
        }},
        new HashMap() {{
          put("innerInnerMap", new HashMap() {{
            put("a", "x2");
            put("b", "x22");
            put("c", "x333");
          }});
        }}
      ));
    }};

    ValidationResult result = mapSchema.validate(customer);

    assertEquals(result.isValid(), true);
    assertThat(result.getValue(), is(customer));
    assertThat(result.getConstraintViolations(), hasSize(0));
  }

  @Test
  public void schema_nested_list_of_maps_invalid() {

    Schema<Map> mapSchema = schema(
      field("name", String.class, stringRequired.and(rangeLength(2, 5))),
      listField("innerList", Map.class, Value.<Map>listRequired().orElseBreak().and(listConstraint(
        schema(
          mapField("innerInnerMap", String.class, mapRequired(String.class).orElseBreak().and((mapConstraint(contains("x")))))
        )
      )))
    );

    Map customer = new HashMap() {{
      put("name", "abcdef");
      put("innerList", Arrays.asList(
        new HashMap() {{
          put("innerInnerMap", new HashMap() {{
            put("a", "x1");
            put("b", "x11");
            put("c", "x111");
          }});
        }},
        new HashMap() {{
          put("innerInnerMapAbsent", new HashMap() {{
            put("a", "x");
          }});
        }},
        new HashMap() {{
          put("innerInnerMap", new HashMap() {{
            put("a", "x2");
            put("b", "y22");
            put("c", "x333");
          }});
        }}
      ));
    }};

    ValidationResult result = mapSchema.validate(customer);

    assertEquals(result.isValid(), false);
    assertThat(result.getValue(), is(customer));
    assertThat(result.getConstraintViolations(), hasSize(3));
    assertEquals(result.getConstraintViolations().get(0).getContext(), ".innerList[1].innerInnerMap");
    assertEquals(result.getConstraintViolations().get(1).getContext(), ".innerList[2].innerInnerMap[b]");
    assertEquals(result.getConstraintViolations().get(2).getContext(), ".name");

  }

  @Test
  public void schema_nested_list_of_generic_maps_valid() {

    Schema<Map> mapSchema = schema(
      field("name", String.class, stringRequired.and(rangeLength(2, 5))),
      listField("someList", Map.class, Value.<Map>listRequired().orElseBreak().and(
        l -> validateList(l, m -> validateMap(m, contains("x")))
      ))
    );

    Map customer = new HashMap() {{
      put("name", "abcde");
      put("someList", Arrays.asList(
        new HashMap() {{
          put("a", "x1");
          put("b", "x11");
          put("c", "x111");
        }},
        new HashMap() {{
          put("a", "x2");
          put("b", "x22");
          put("c", "x222");
        }}
      ));
    }};

    ValidationResult result = mapSchema.validate(customer);

    assertEquals(result.isValid(), true);
    assertThat(result.getValue(), is(customer));
    assertThat(result.getConstraintViolations(), hasSize(0));
  }

  @Test
  public void schema_nested_list_of_generic_maps_invalid() {

    Schema<Map> mapSchema = schema(
      field("name", String.class, stringRequired.and(rangeLength(2, 5))),
      listField("someList", Map.class, Value.<Map>listRequired().orElseBreak().and(
        l -> validateList(l, m -> validateMap(m, contains("x")))
      ))
    );

    Map customer = new HashMap() {{
      put("name", "abcdef");
      put("someList", Arrays.asList(
        new HashMap() {{
          put("a", "x1");
          put("b", "y11");
          put("c", "x111");
        }},
        new HashMap() {{
          put("a", "x2");
          put("b", "x22");
          put("c", "y222");
        }}
      ));
    }};

    ValidationResult result = mapSchema.validate(customer);

    assertEquals(result.isValid(), false);
    assertThat(result.getValue(), is(customer));
    assertThat(result.getConstraintViolations(), hasSize(3));
    assertEquals(result.getConstraintViolations().get(0).getContext(), ".name");
    assertEquals(result.getConstraintViolations().get(1).getContext(), ".someList[0][b]");
    assertEquals(result.getConstraintViolations().get(2).getContext(), ".someList[1][c]");
  }

  @Test
  public void schema_nested_mapt_of_generic_lists_valid() {

    Schema<Map> mapSchema = schema(
      field("name", String.class, stringRequired.and(rangeLength(2, 5))),
      mapField("someMap", List.class, Value.<List>mapRequired().orElseBreak().and(
        m -> validateMap(m, l -> validateList(l, max(5)))
      ))
    );

    Map customer = new HashMap() {{
      put("name", "abcde");
      put("someMap", new HashMap() {{
          put("a", Arrays.asList(1, 3, 4));
          put("b", Arrays.asList(0, 2, 3));
        }}
      );
    }};

    ValidationResult result = mapSchema.validate(customer);

    assertEquals(result.isValid(), true);
    assertThat(result.getValue(), is(customer));
    assertThat(result.getConstraintViolations(), hasSize(0));
  }

  @Test
  public void schema_nested_maps_of_generic_lists_invalid() {

    Schema<Map> mapSchema = schema(
      field("name", String.class, stringRequired.and(rangeLength(2, 5))),
      mapField("someMap", List.class, Value.<List>mapRequired().orElseBreak().and(
        m -> validateMap(m, l -> validateList(l, max(5)))
      ))

    );

    Map customer = new HashMap() {{
      put("name", "abcdef");
      put("someMap", new HashMap() {{
          put("a", Arrays.asList(1, 3, 7));
          put("b", Arrays.asList(9, 2, 3));
        }}
      );
    }};

    ValidationResult result = mapSchema.validate(customer);

    assertEquals(result.isValid(), false);
    assertThat(result.getValue(), is(customer));
    assertThat(result.getConstraintViolations(), hasSize(3));
    assertEquals(result.getConstraintViolations().get(0).getContext(), ".name");
    assertEquals(result.getConstraintViolations().get(1).getContext(), ".someMap[a][2]");
    assertEquals(result.getConstraintViolations().get(2).getContext(), ".someMap[b][0]");
  }

  @Test
  public void map_and_bean_schema_from_fields_def() {
    Fields addressFields = fields(
      _field("street", String.class, stringRequired.and(maxLength(10)))
    );

    Fields fields = fields(
      _field("name", String.class, stringRequired.and(rangeLength(2, 5))),
      _field("age", Integer.class, integerRequired.orElseBreak().and(greaterThan(18))),
      _listField("someList", Integer.class, Value.<Integer>listRequired().orElseBreak().and(listConstraint(max(5)))),
      _mapField("someMap", String.class, mapRequired(String.class).orElseBreak().and((mapConstraint(contains("x"))))),
      _requiredField("address", addressFields),
      _optionalField("address2", fields(
        _field("apt", Integer.class, integerRequired)
      ))
    );

    Schema mapSchema = schema(fields);
    Schema beanSchema = schema(Customer.class, fields);

    Schema mapSchemaX = schema(
      fields,
      field("extra", String.class, stringRequired)
    );

    Schema beanSchemaX = schema(Customer.class,
      fields,
      field("driversLicense", String.class, stringRequired)
    );

    Map customerMap = new HashMap() {{
      put("name", "abcdef");
      put("age", 25);
      put("address", new HashMap() {{
        put("street", "0123456789x");
        }});
      put("someList", Arrays.asList(1, 3, 7, 2));
      put("someMap", new HashMap() {{
        put("a", "x1");
        put("b", "yz");
        put("c", "x2");
      }});
    }};

    ValidationResult result = mapSchema.validate(customerMap);

    assertEquals(result.isValid(), false);
    assertThat(result.getValue(), is(customerMap));
    assertThat(result.getConstraintViolations(), hasSize(4));
    assertEquals(result.getConstraintViolations().get(0).getContext(), ".address.street");
    assertEquals(result.getConstraintViolations().get(1).getContext(), ".name");
    assertEquals(result.getConstraintViolations().get(2).getContext(), ".someList[2]");
    assertEquals(result.getConstraintViolations().get(3).getContext(), ".someMap[b]");

    result = mapSchemaX.validate(customerMap);

    assertEquals(result.isValid(), false);
    assertThat(result.getValue(), is(customerMap));
    assertThat(result.getConstraintViolations(), hasSize(5));
    assertEquals(result.getConstraintViolations().get(0).getContext(), ".address.street");
    assertEquals(result.getConstraintViolations().get(1).getContext(), ".extra");
    assertEquals(result.getConstraintViolations().get(2).getContext(), ".name");
    assertEquals(result.getConstraintViolations().get(3).getContext(), ".someList[2]");
    assertEquals(result.getConstraintViolations().get(4).getContext(), ".someMap[b]");

    Customer customer = new Customer();
    customer.setName("abcdef");
    customer.setAge(25);

    customer.setSomeList(Arrays.asList(1, 3, 7, 2));

    customer.setSomeMap(new HashMap<String, String>() {{
      put("a", "x1");
      put("b", "yz");
      put("c", "x2");
    }});


    result = beanSchema.validate(customer);

    assertEquals(result.isValid(), false);
    assertThat(result.getValue(), is(customer));
    assertThat(result.getConstraintViolations(), hasSize(3));
    assertEquals(result.getConstraintViolations().get(0).getContext(), ".name");
    assertEquals(result.getConstraintViolations().get(1).getContext(), ".someList[2]");
    assertEquals(result.getConstraintViolations().get(2).getContext(), ".someMap[b]");

    result = beanSchemaX.validate(customer);

    assertEquals(result.isValid(), false);
    assertThat(result.getValue(), is(customer));
    assertThat(result.getConstraintViolations(), hasSize(4));
    assertEquals(result.getConstraintViolations().get(0).getContext(), ".driversLicense");
    assertEquals(result.getConstraintViolations().get(1).getContext(), ".name");
    assertEquals(result.getConstraintViolations().get(2).getContext(), ".someList[2]");
    assertEquals(result.getConstraintViolations().get(3).getContext(), ".someMap[b]");
  }
}
