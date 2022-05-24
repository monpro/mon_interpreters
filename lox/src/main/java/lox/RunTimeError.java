package lox;

public class RunTimeError extends RuntimeException {
  private final Token token;
  public RunTimeError(Token token, String message) {
    super(message);
    this.token = token;
  }
}
