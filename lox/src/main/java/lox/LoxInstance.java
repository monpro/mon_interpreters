package lox;

import java.util.HashMap;
import java.util.Map;

public class LoxInstance {

  private LoxClass loxClass;
  private final Map<String, Object> fields = new HashMap<>();

  public LoxInstance(LoxClass loxClass) {
    this.loxClass = loxClass;
  }

  @Override
  public String toString() {
    return "LoxInstance{" +
        "loxClass=" + loxClass +
        '}';
  }

  public Object get(Token name) {
    if (fields.containsKey(name.lexeme)) {
      return fields.get(name.lexeme);
    }
    throw new RunTimeError( name, "Undefined property '" + name.lexeme + "'.");
  }
}
