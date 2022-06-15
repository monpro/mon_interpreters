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
    LoxFunction method = loxClass.findMethod(name.lexeme);
    if (method != null) {
      return method;
    }
    throw new RunTimeError( name, "Undefined property '" + name.lexeme + "'.");
  }

  public void set(Token name, Object value) {
    fields.put(name.lexeme, value);
  }
}
