package lox;

import java.util.List;

abstract class Statement {
  interface Visitor<R> {
    R visitExpressionStatement(Expression statement);
    R visitFunctionStatement(Function statement);
    R visitPrintStatement(Print statement);
    R visitVarStatement(Var statement);
    R visitBlockStatement(Block statement);
    R visitClassStatement(Class statement);
    R visitIfStatement(If statement);
    R visitWhileStatement(While statement);
    R visitReturnStatement(Return statement);
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

  static class Function extends Statement {
    Function(Token name, List<Token> params, List<Statement> body) {
      this.name = name;
      this.params = params;
      this.body = body;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitFunctionStatement(this);
    }
  final Token name;
  final List<Token> params;
  final List<Statement> body;
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

  static class Class extends Statement {
    Class(Token name, Expr.Variable superclass, List<Statement.Function> methods) {
      this.name = name;
      this.superclass = superclass;
      this.methods = methods;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitClassStatement(this);
    }
  final Token name;
  final Expr.Variable superclass;
  final List<Statement.Function> methods;
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

  static class While extends Statement {
    While(Expr condition, Statement body) {
      this.condition = condition;
      this.body = body;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitWhileStatement(this);
    }
  final Expr condition;
  final Statement body;
  }

  static class Return extends Statement {
    Return(Token keyword, Expr value) {
      this.keyword = keyword;
      this.value = value;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitReturnStatement(this);
    }
  final Token keyword;
  final Expr value;
  }


  abstract <R> R accept(Visitor<R> visitor);
}
