package lox;

import java.util.List;
import java.util.Map;

public class LoxClass implements LoxCallable {
  private final String name;
  private final Map<String, LoxFunction> methods;

  public LoxClass(String name, Map<String, LoxFunction> methods) {
    this.name = name;
    this.methods = methods;
  }

  @Override
  public String toString() {
    return "class name : " + name;
  }

  @Override
  public Object call(Interpreter interpreter, List<Object> arguments) {
    LoxInstance loxInstance = new LoxInstance(this);
    LoxFunction init = findMethod("init");
    if (init != null) {
      init.bind(loxInstance).call(interpreter, arguments);
    }
    return loxInstance;
  }

  @Override
  public int arity() {
    LoxFunction init = findMethod("init");
    if (init != null) {
      return init.arity();
    }
    return 0;
  }

  public LoxFunction findMethod(String lexeme) {
    if (methods.containsKey(lexeme)) {
      return methods.get(lexeme);
    }
    return null;
  }
}
