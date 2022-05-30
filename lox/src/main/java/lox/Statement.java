package lox;

import java.util.List;

abstract class Statement {
  interface Visitor<R> {
    R visitExpressionStatement(Expression statement);
    R visitPrintStatement(Print statement);
    R visitVarStatement(Var statement);
    R visitBlockStatement(Block statement);
    R visitIfStatement(If statement);
  }

  static class Expression extends Statement {
    Expression(Expr expression) {
      this.expression = expression;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitExpressionStatement(this);
    }
  final Expr expression;
  }

  static class Print extends Statement {
    Print(Expr expression) {
      this.expression = expression;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitPrintStatement(this);
    }
  final Expr expression;
  }

  static class Var extends Statement {
    Var(Token name, Expr initializer) {
      this.name = name;
      this.initializer = initializer;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitVarStatement(this);
    }
  final Token name;
  final Expr initializer;
  }

  static class Block extends Statement {
    Block(List<Statement> statements) {
      this.statements = statements;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitBlockStatement(this);
    }
  final List<Statement> statements;
  }

  static class If extends Statement {
    If(Expr condition, Statement thenBranch, Statement elseBranch) {
      this.condition = condition;
      this.thenBranch = thenBranch;
      this.elseBranch = elseBranch;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitIfStatement(this);
    }
  final Expr condition;
  final Statement thenBranch;
  final Statement elseBranch;
  }


  abstract <R> R accept(Visitor<R> visitor);
}
