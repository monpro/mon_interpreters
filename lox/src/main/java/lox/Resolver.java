package lox;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import static lox.FunctionType.INITIALIZER;

public class Resolver implements Expr.Visitor<Void>, Statement.Visitor<Void> {

  private final Interpreter interpreter;
  // [{this: true}, {abc: false}], it means this is defined within the scope while
  // the abc is not defined
  private final Stack<Map<String, Boolean>> scopes = new Stack<>();
  private FunctionType currentFunctionType = FunctionType.NONE;
  private ClassType currentClassType = ClassType.NONE;

  public Resolver(Interpreter interpreter) {
    this.interpreter = interpreter;
  }

  protected void resolve(List<Statement> statements) {
    for(Statement statement : statements) {
      resolve(statement);
    }
  }

  private void resolve(Statement statement) {
    statement.accept(this);
  }

  private void resolve(Expr expr) {
    expr.accept(this);
  }

  private void beginScope() {
    scopes.push(new HashMap<>());
  }

  private void endScope() {
    scopes.pop();
  }

  @Override
  public Void visitBinaryExpr(Expr.Binary expr) {
    resolve(expr.left);
    resolve(expr.right);
    return null;
  }

  @Override
  public Void visitCallExpr(Expr.Call expr) {
    resolve(expr.callee);
    for (Expr argument : expr.arguments) {
      resolve(argument);
    }
    return null;
  }

  @Override
  public Void visitGetExpr(Expr.Get expr) {
    resolve(expr.object);
    return null;
  }

  @Override
  public Void visitSetExpr(Expr.Set expr) {
    resolve(expr.value);
    resolve(expr.object);
    return null;
  }

  @Override
  public Void visitThisExpr(Expr.This expr) {
    if (currentClassType == ClassType.NONE) {
      Lox.error(expr.keyword, "Can't use 'this' outside of a class.");
    }
    resolveLocal(expr, expr.keyword);
    return null;
  }

  @Override
  public Void visitGroupingExpr(Expr.Grouping expr) {
    resolve(expr.expression);
    return null;
  }

  @Override
  public Void visitLiteralExpr(Expr.Literal expr) {
    return null;
  }

  @Override
  public Void visitUnaryExpr(Expr.Unary expr) {
    resolve(expr.right);
    return null;
  }

  @Override
  public Void visitVariableExpr(Expr.Variable expr) {
    // means the variable has not been defined
    if (!scopes.isEmpty() && scopes.peek().get(expr.name.lexeme) == Boolean.FALSE) {
      Lox.error(expr.name, "Can't read local variable in its own initializer.");
    }
    resolveLocal(expr, expr.name);
    return null;
  }

  private void resolveLocal(Expr expr, Token name) {
    for (int i = scopes.size() - 1; i >= 0; i--) {
      if (scopes.get(i).containsKey(name.lexeme)) {
        // we walk from the innermost scope to global
        // if we find the variable, we resolve it and pass in the number
        // of scopes between the current innermost scope and the scope
        // where the variable is found.
        interpreter.resolve(expr, scopes.size() - 1 - i);
        return;
      }
    }
  }

  @Override
  public Void visitAssignExpr(Expr.Assign expr) {
    resolve(expr.value);
    resolveLocal(expr, expr.name);
    return null;
  }

  @Override
  public Void visitLogicalExpr(Expr.Logical expr) {
    resolve(expr.left);
    resolve(expr.right);
    return null;
  }

  @Override
  public Void visitExpressionStatement(Statement.Expression statement) {
    resolve(statement.expression);
    return null;
  }

  @Override
  public Void visitFunctionStatement(Statement.Function statement) {
    declare(statement.name);
    declare(statement.name);
    resolveFunction(statement, FunctionType.FUNCTION);
    return null;
  }

  private void resolveFunction(Statement.Function statement, FunctionType functionType) {
    FunctionType enclosingFunctionType = currentFunctionType;
    currentFunctionType = functionType;
    beginScope();
    for (Token param : statement.params) {
      declare(param);
      define(param);
    }
    resolve(statement.body);
    endScope();
    currentFunctionType = enclosingFunctionType;
  }

  @Override
  public Void visitPrintStatement(Statement.Print statement) {
    resolve(statement.expression);
    return null;
  }

  /**
   * For var statement, we split into three steps.
   *  1.declare - var a;
   *  2.resolve - resolving bindings;
   *  3.define - a = a;
   * @param statement Statement.Var
   * @return Void.
   */
  @Override
  public Void visitVarStatement(Statement.Var statement) {
    declare(statement.name);
    if (statement.initializer != null) {
      resolve(statement.initializer);
    }
    define(statement.name);
    return null;
  }

  private void declare(Token name) {
    if (scopes.isEmpty()) return;
    Map<String, Boolean> scope = scopes.peek();
    if (scope.containsKey(name.lexeme)) {
      Lox.error(name, "Already a variable with same name in the scope");
    }
    // false means the variable has not been defined.
    scope.put(name.lexeme, false);
  }

  private void define(Token name) {
    if (scopes.isEmpty()) return;
    // true means the variable has been defined.
    scopes.peek().put(name.lexeme, true);
  }

  @Override
  public Void visitBlockStatement(Statement.Block statement) {
    beginScope();
    resolve(statement.statements);
    endScope();
    return null;
  }

  @Override
  public Void visitClassStatement(Statement.Class statement) {
    ClassType enclosingClass = currentClassType;
    currentClassType = ClassType.CLASS;
    declare(statement.name);
    define(statement.name);
    if (statement.superclass != null && statement.name.lexeme.equals(statement.superclass.name.lexeme)) {
       Lox.error(statement.superclass.name, "A class cannot inherit from itself.");
    }
    if (statement.superclass != null) {
      resolve(statement.superclass);
    }
    beginScope();
    scopes.peek().put("this", true);
    for (Statement.Function method : statement.methods) {
      FunctionType functionType = FunctionType.METHOD;
      if (method.name.lexeme.equals("init")) {
        functionType = INITIALIZER;
      }
      resolveFunction(method, functionType);
    }
    endScope();
    currentClassType = enclosingClass;
    return null;
  }

  @Override
  public Void visitIfStatement(Statement.If statement) {
    resolve(statement.condition);
    resolve(statement.thenBranch);
    if (statement.elseBranch != null) {
      resolve(statement.elseBranch);
    }
    return null;
  }

  @Override
  public Void visitWhileStatement(Statement.While statement) {
    resolve(statement.condition);
    resolve(statement.body);
    return null;
  }

  @Override
  public Void visitReturnStatement(Statement.Return statement) {
    if (currentFunctionType == FunctionType.NONE) {
      Lox.error(statement.keyword, "Cannot return from top-level");
    }
    if (statement.value != null) {
      if (currentFunctionType == FunctionType.INITIALIZER) {
        Lox.error(statement.keyword, "cannot return value from an initializer.");
      }
      resolve(statement.value);
    }
    return null;
  }
}
