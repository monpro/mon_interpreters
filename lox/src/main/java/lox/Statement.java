package lox;

abstract class Statement {
  interface Visitor<R> {
    R visitExpressionStatement(Expression statement);

    R visitPrintStatement(Print statement);
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


  abstract <R> R accept(Visitor<R> visitor);
}
