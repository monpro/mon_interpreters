package lox;

public class Token {

  protected final TokenType type;
  protected String lexeme;
  protected final Object literal;
  protected final int line;

  Token(TokenType type, String lexeme, Object literal, int line) {
    this.type = type;
    this.lexeme = lexeme;
    this.literal = literal;
    this.line = line;
  }

  @Override
  public String toString() {
    return "lox.Token{" +
        "type=" + type +
        ", lexeme='" + lexeme + '\'' +
        ", literal=" + literal +
        ", line=" + line +
        '}';
  }
}
