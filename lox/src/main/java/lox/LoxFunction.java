package lox;

import java.util.List;

/**
 * This class is the base of all function.
 *
 * It will be used to let interpreter to call function.
 */
public class LoxFunction implements LoxCallable {

  private final Statement.Function declaration;

  public LoxFunction(Statement.Function declaration) {
    this.declaration = declaration;
  }

  @Override
  public Object call(Interpreter interpreter, List<Object> arguments) {
    // we need to make sure every function called will have its own environment
    // think about recursion.
    Environment environment = new Environment(interpreter.global);
    for (int i = 0; i < declaration.params.size(); i++) {
      environment.define(declaration.params.get(i).lexeme, arguments.get(i));
    }
    interpreter.executeBlock(declaration.body, environment);

    return null;
  }

  @Override
  public int arity() {
    return declaration.params.size();
  }

  @Override
  public String toString() {
    return "<function " +declaration.name.lexeme + " >";
  }
}
