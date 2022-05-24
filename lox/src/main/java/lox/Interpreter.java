package lox;

public class Interpreter implements Expr.Visitor<Object>{

  @Override
  public Object visitBinaryExpr(Expr.Binary expr) {
    Object left = evaluate(expr.left);
    Object right = evaluate(expr.right);
    switch (expr.operator.type) {
      case MINUS:
        return (double)left - (double) right;
      case SLASH:
        return (double)left / (double) right;
      case STAR:
        return (double)left * (double) right;
      case PLUS:
        if (left instanceof Double && right instanceof Double) {
          return (double) left + (double)right;
        }
        if (left instanceof String && right instanceof String) {
          return left + (String)right;
        }
        break;
      case GREATER:
        return (double)left > (double)right;
      case GREATER_EQUAL:
        return (double)left >= (double) right;
      case LESS:
        return (double)left < (double) right;
      case LESS_EQUAL:
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
