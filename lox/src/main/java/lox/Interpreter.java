package lox;

public class Interpreter implements Expr.Visitor<Object>{

  @Override
  public Object visitBinaryExpr(Expr.Binary expr) {
    return null;
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
        return -(double)right;
      case BANG:
        return !isTruthy(right);
      default:
        break;
    }
    return null;
  }

  /**
   * return false if object is null or false
   * @param object Object.
   * @return boolean
   */
  private boolean isTruthy(Object object) {
    if (object == null) return false;
    if (object instanceof Boolean) return (boolean) object;
    return true;
  }

  private Object evaluate(Expr expr) {
    return expr.accept(this);
  }
}
