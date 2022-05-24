package lox;

public class Interpreter implements Expr.Visitor<Object>{

  public void interpret(Expr expr) {
    try {
      Object value = evaluate(expr);
      System.out.println(stringify(value));
    } catch (RunTimeError error) {
      Lox.runTimeError(error);
    }
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
