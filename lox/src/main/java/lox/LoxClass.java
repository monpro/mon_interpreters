package lox;

import java.util.List;

public class LoxClass implements LoxCallable {
  private final String name;

  public LoxClass(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return "class name : " + name;
  }

  @Override
  public Object call(Interpreter interpreter, List<Object> arguments) {
    return new LoxInstance(this);
  }

  @Override
  public int arity() {
    return 0;
  }
}
