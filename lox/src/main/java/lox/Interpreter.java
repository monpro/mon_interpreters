package lox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Interpreter implements Expr.Visitor<Object>, Statement.Visitor<Void>{

  private final Environment global = new Environment();
  private Environment environment = global;
  private final Map<Expr, Integer> locals = new HashMap<>();

  public Interpreter() {
    global.define("clock", new LoxCallable() {
      @Override
      public Object call(Interpreter interpreter, List<Object> arguments) {
        return (double) System.currentTimeMillis() / 1000.0;
      }

      @Override
      public int arity() {
        return 0;
      }

      @Override
      public String toString() {
        return "native function";
      }
    });
  }

  public void interpret(List<Statement> statements) {
    try {
      for(Statement statement : statements) {
        execute(statement);
      }
    } catch (RunTimeError error) {
      Lox.runTimeError(error);
    }
  }

  private void execute(Statement statement) {
    statement.accept(this);
  }

   void resolve(Expr expr, int depth) {
    locals.put(expr, depth);
  }

  private String stringify(Object object) {
    if (object == null) return "nil";
    if (object instanceof Double) {
      String text = object.toString();
      if (text.endsWith(".0")) {
        text = text.substring(0, text.length() - 2);
      }
      return text;
    }
    return object.toString();
  }

  @Override
  public Object visitBinaryExpr(Expr.Binary expr) {
    Object left = evaluate(expr.left);
    Object right = evaluate(expr.right);
    switch (expr.operator.type) {
      case MINUS:
        checkNumberOperand(expr.operator, left, right);
        return (double)left - (double) right;
      case SLASH:
        checkNumberOperand(expr.operator, left, right);
        return (double)left / (double) right;
      case STAR:
        checkNumberOperand(expr.operator, left, right);
        return (double)left * (double) right;
      case PLUS:
        if (left instanceof Double && right instanceof Double) {
          return (double) left + (double)right;
        }
        if (left instanceof String && right instanceof String) {
          return left + (String)right;
        }
        throw new RunTimeError(expr.operator, "operands must be two numbers or two strings");
      case GREATER:
        checkNumberOperand(expr.operator, left, right);
        return (double)left > (double)right;
      case GREATER_EQUAL:
        checkNumberOperand(expr.operator, left, right);
        return (double)left >= (double) right;
      case LESS:
        checkNumberOperand(expr.operator, left, right);
        return (double)left < (double) right;
      case LESS_EQUAL:
        checkNumberOperand(expr.operator, left, right);
        return (double)left <= (double)right;
      case BANG_EQUAL:
        return !isEqual(left, right);
      case EQUAL_EQUAL:
        return isEqual(left, right);
      default:
        break;
    }
    return null;
  }

  @Override
  public Object visitCallExpr(Expr.Call expr) {
    // look up the function by evaluating callee
    Object callee = evaluate(expr.callee);

    List<Object> arguments = new ArrayList<>();
    for (Expr argument : expr.arguments) {
      arguments.add(evaluate(argument));
    }
    if (!(callee instanceof LoxCallable)) {
      throw new RunTimeError(expr.paren, "you can only call functions and classes.");
    }
    LoxCallable function = (LoxCallable) callee;
    if (arguments.size() != function.arity()) {
      throw new RunTimeError(expr.paren, "Expected " +
          function.arity() +
          " arguments but got " +
          arguments.size() + ".");
    }

      return function.call(this, arguments);
  }

  @Override
  public Object visitGetExpr(Expr.Get expr) {
    Object object = evaluate(expr.object);
    if (object instanceof LoxInstance) {
      return ((LoxInstance) object).get(expr.name);
    }
    throw new RunTimeError(expr.name, "only instances have properties");
  }

  @Override
  public Object visitSetExpr(Expr.Set expr) {
    return null;
  }

  private void checkNumberOperand(Token operator, Object leftOperand, Object rightOperand) {
    if (leftOperand instanceof Double && rightOperand instanceof Double) return;
    throw new RunTimeError(operator, "operands must be numbers");
  }

  private void checkNumberOperand(Token operator, Object operand) {
    if (operand instanceof Double) return;
    throw new RunTimeError(operator, "operand must be a number");
  }

  @Override
  public Object visitGroupingExpr(Expr.Grouping expr) {
    return evaluate(expr.expression);
  }


  @Override
  public Object visitLiteralExpr(Expr.Literal expr) {
    return expr.value;
  }

  @Override
  public Object visitUnaryExpr(Expr.Unary expr) {
    Object right = evaluate(expr.right);
    switch (expr.operator.type) {
      case MINUS:
        checkNumberOperand(expr.operator, right);
        return -(double)right;
      case BANG:
        return !isTruthy(right);
      default:
        break;
    }
    return null;
  }

  @Override
  public Object visitVariableExpr(Expr.Variable expr) {
    return lookUpVariable(expr.name, expr);
  }

  private Object lookUpVariable(Token name, Expr.Variable expr) {
    Integer distance = locals.get(expr);
    if (distance != null) {
      return environment.getAt(distance, name.lexeme);
    } else {
      return global.get(name);
    }
  }

  @Override
  public Object visitAssignExpr(Expr.Assign expr) {
    Object value = evaluate(expr.value);
    Integer distance = locals.get(expr);
    if (distance != null) {
      environment.assignAt(distance, expr.name, value);
    } else {
      global.assign(expr.name, value);
    }
    return value;
  }

  @Override
  public Object visitLogicalExpr(Expr.Logical expr) {
    Object left = evaluate(expr.left);
    if (expr.operator.type == TokenType.OR) {
      if (isTruthy(left)) {
        return left;
      }
    } else {
      if (!isTruthy(left)) {
        return left;
      }
    }
    return evaluate(expr.right);
  }

  @Override
  public Void visitExpressionStatement(Statement.Expression statement) {
    evaluate(statement.expression);
    return null;
  }

  @Override
  public Void visitFunctionStatement(Statement.Function statement) {
    LoxFunction function = new LoxFunction(statement, environment);
    environment.define(statement.name.lexeme, function);
    return null;
  }

  @Override
  public Void visitPrintStatement(Statement.Print statement) {
    Object value = evaluate(statement.expression);
    System.out.println(stringify(value));
    return null;
  }

  @Override
  public Void visitVarStatement(Statement.Var statement) {
    Object value = null;
    if (statement.initializer != null) {
      value = evaluate(statement.initializer);
    }
    environment.define(statement.name.lexeme, value);
    return null;
  }

  @Override
  public Void visitBlockStatement(Statement.Block statement) {
    // create a new environment for the block scope
    executeBlock(statement.statements, new Environment(environment));
    return null;
  }

  @Override
  public Void visitClassStatement(Statement.Class statement) {
    environment.define(statement.name.lexeme, null);
    LoxClass loxClass = new LoxClass(statement.name.lexeme);
    environment.assign(statement.name, loxClass);
    return null;
  }

  @Override
  public Void visitIfStatement(Statement.If statement) {
    if (isTruthy(evaluate(statement.condition))) {
      execute(statement.thenBranch);
    } else if (statement.elseBranch != null) {
      execute(statement.elseBranch);
    }
    return null;
  }

  @Override
  public Void visitWhileStatement(Statement.While statement) {
    while (isTruthy(evaluate(statement.condition))) {
      execute(statement.body);
    }
    return null;
  }

  @Override
  public Void visitReturnStatement(Statement.Return statement) {
    Object value = null;
    if (statement.value != null) value = evaluate(statement.value);
    throw new Return(value);
  }

  protected void executeBlock(List<Statement> statements, Environment environment) {
    // we need to mutate env to current block one
    Environment previous = this.environment;
    try {
      this.environment = environment;
      for (Statement statement : statements) {
        execute(statement);
      }
    } finally {
      // we need to restore it back to global one
      this.environment = previous;
    }
  }

  /**
   * return false if the object is null or false
   * @param object Object.
   * @return boolean
   */
  private boolean isTruthy(Object object) {
    if (object == null) return false;
    if (object instanceof Boolean) return (boolean) object;
    return true;
  }

  /**
   * return true if two object is equal, false otherwise
   * @param object1 Object.
   * @param object2 Object.
   * @return Boolean.
   */
  private boolean isEqual(Object object1, Object object2) {
    if (object1 == null && object2 == null) return true;
    if (object1 == null) return false;
    return object1.equals(object2);
  }



  private Object evaluate(Expr expr) {
    return expr.accept(this);
  }

}
