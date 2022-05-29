package lox;

import java.util.HashMap;
import java.util.Map;

public class Environment {

  private final Map<String, Object> values = new HashMap<>();

  public void define(String name, Object value) {
    values.put(name, value);
  }

  public Object get(Token name) {
    if (values.containsKey(name.lexeme)) {
      return values.get(name.lexeme);
    }
    throw new RunTimeError(name, "Undefined Variable: '" + name.lexeme + "' .");
  }

  public void assign(Token name, Object value) {
    if (values.containsKey(name.lexeme)) {
      values.put(name.lexeme, value);
      return;
    }
    throw new RunTimeError(name, "Undefined variable '" + name.lexeme + "'.");
  }
}
