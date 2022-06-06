package lox;

/**
 * Use Return as an thrown exception control flow.
 *
 * Once catching this exception, we return the value.
 *
 * say: while {
 *   if() {
 *     while {
 *       return;
 *     }
 *   }
 * }
 *
 * we need to catch the return value at the outermost stack.
 */
public class Return extends RuntimeException {
  final Object value;

  public Return(Object value) {
    super(null, null, false, false);
    this.value = value;
  }

}
