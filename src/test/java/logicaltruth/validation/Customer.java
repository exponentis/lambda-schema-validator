package logicaltruth.validation;

import java.util.List;
import java.util.Map;

public class Customer {
  private String name;
  private Integer age;
  private String driversLicense;
  private String password1;
  private String password2;
  private List<Integer> someList;
  private Map<Object, String> someMap;

  private Address address;
  private Map address2;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Integer getAge() {
    return age;
  }

  public void setAge(Integer age) {
    this.age = age;
  }

  public String getDriversLicense() {
    return driversLicense;
  }

  public void setDriversLicense(String driversLicense) {
    this.driversLicense = driversLicense;
  }

  public String getPassword1() {
    return password1;
  }

  public void setPassword1(String password1) {
    this.password1 = password1;
  }

  public String getPassword2() {
    return password2;
  }

  public void setPassword2(String password2) {
    this.password2 = password2;
  }

  public List<Integer> getSomeList() {
    return someList;
  }

  public void setSomeList(List<Integer> someList) {
    this.someList = someList;
  }

  public Map<Object, String> getSomeMap() {
    return someMap;
  }

  public void setSomeMap(Map<Object, String> someMap) {
    this.someMap = someMap;
  }

  public Address getAddress() {
    return address;
  }

  public void setAddress(Address address) {
    this.address = address;
  }

  public Map getAddress2() {
    return address2;
  }

  public void setAddress2(Map address2) {
    this.address2 = address2;
  }
}
