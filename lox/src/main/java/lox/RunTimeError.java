package lox;

public class RunTimeError extends RuntimeException {
  public final Token token;
  public RunTimeError(Token token, String message) {
    super(message);
    this.token = token;
  }
}
