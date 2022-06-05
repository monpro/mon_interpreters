package lox;

import java.util.List;

public interface LoxCallable {
  // return the value that the call expression produces
  Object call(Interpreter interpreter, List<Object> arguments);
}
