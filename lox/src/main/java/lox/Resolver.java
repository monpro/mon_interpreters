package lox;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class Resolver implements Expr.Visitor<Void>, Statement.Visitor<Void> {

  private final Interpreter interpreter;
  private final Stack<Map<String, Boolean>> scopes = new Stack<>();

  public Resolver(Interpreter interpreter) {
    this.interpreter = interpreter;
  }

  private void resolve(List<Statement> statements) {
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
    return null;
  }

  @Override
  public Void visitCallExpr(Expr.Call expr) {
    return null;
  }

  @Override
  public Void visitGroupingExpr(Expr.Grouping expr) {
    return null;
  }

  @Override
  public Void visitLiteralExpr(Expr.Literal expr) {
    return null;
  }

  @Override
  public Void visitUnaryExpr(Expr.Unary expr) {
    return null;
  }

  @Override
  public Void visitVariableExpr(Expr.Variable expr) {
    return null;
  }

  @Override
  public Void visitAssignExpr(Expr.Assign expr) {
    return null;
  }

  @Override
  public Void visitLogicalExpr(Expr.Logical expr) {
    return null;
  }

  @Override
  public Void visitExpressionStatement(Statement.Expression statement) {
    return null;
  }

  @Override
  public Void visitFunctionStatement(Statement.Function statement) {
    return null;
  }

  @Override
  public Void visitPrintStatement(Statement.Print statement) {
    return null;
  }

  @Override
  public Void visitVarStatement(Statement.Var statement) {
    return null;
  }

  @Override
  public Void visitBlockStatement(Statement.Block statement) {
    beginScope();
    resolve(statement.statements);
    endScope();
    return null;
  }

  @Override
  public Void visitIfStatement(Statement.If statement) {
    return null;
  }

  @Override
  public Void visitWhileStatement(Statement.While statement) {
    return null;
  }

  @Override
  public Void visitReturnStatement(Statement.Return statement) {
    return null;
  }
}
