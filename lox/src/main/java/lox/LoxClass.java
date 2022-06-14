package lox;

public class LoxClass {
  private final String name;

  public LoxClass(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return "class name : " + name;
  }
}
