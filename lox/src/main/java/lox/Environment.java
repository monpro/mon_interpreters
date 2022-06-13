package lox;

import java.util.HashMap;
import java.util.Map;

public class Environment {

  private final Map<String, Object> values = new HashMap<>();
  private final Environment enclosing;

  public Environment() {
    this.enclosing = null;
  }

  public Environment(Environment enclosing) {
    this.enclosing = enclosing;
  }

  public void define(String name, Object value) {
    values.put(name, value);
  }

  public Object get(Token name) {
    if (values.containsKey(name.lexeme)) {
      return values.get(name.lexeme);
    }
    if (enclosing != null) {
      return enclosing.get(name);
    }
    throw new RunTimeError(name, "Undefined Variable: '" + name.lexeme + "' .");
  }

  public void assign(Token name, Object value) {
    if (values.containsKey(name.lexeme)) {
      values.put(name.lexeme, value);
      return;
    }
    if (enclosing != null) {
      enclosing.assign(name, value);
      return;
    }
    throw new RunTimeError(name, "Undefined variable '" + name.lexeme + "'.");
  }

  public Object getAt(Integer distance, String name) {
    return ancestor(distance).values.get(name);
  }

  private Environment ancestor(Integer distance) {
    Environment environment = this;
    for (int i = 0; i < distance; i++) {
      environment = environment.enclosing;
    }
    return environment;
  }
}
