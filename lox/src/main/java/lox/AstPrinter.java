package lox;

public class AstPrinter implements Expr.Visitor<String> {

  String print(Expr expr) {
    return expr.accept(this);
  }

  @Override
  public String visitBinaryExpr(Expr.Binary expr) {
    return parenthesize(expr.operator.lexeme, expr.left, expr.right);
  }

  @Override
  public String visitGroupingExpr(Expr.Grouping expr) {
    return parenthesize("group", expr.expression);
  }

  @Override
  public String visitLiteralExpr(Expr.Literal expr) {
    if (expr.value == null) return "nil";
    return expr.value.toString();
  }

  @Override
  public String visitUnaryExpr(Expr.Unary expr) {
    return parenthesize(expr.operator.lexeme, expr.right);
  }

  @Override
  public String visitVariableExpr(Expr.Variable expr) {
    return expr.name.toString();
  }

  @Override
  public String visitAssignExpr(Expr.Assign expr) {
    return expr.name.toString() + " = " + expr.value.toString();
  }

  private String parenthesize(String name, Expr... expressions) {
    final StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("(").append(name);
    for (Expr expr : expressions) {
      stringBuilder.append(" ");
      stringBuilder.append(expr.accept(this));
    }
    stringBuilder.append(")");
    return stringBuilder.toString();
  }

  public static void main(String[] args) {
    Expr expression = new Expr.Binary(
        new Expr.Unary(
            new Token(TokenType.MINUS, "-", null, 1),
            new Expr.Literal(456)
        ),
        new Token(TokenType.DOT, ".", null, 1),
        new Expr.Grouping(
            new Expr.Literal(777)
        )
    );
  }
}
