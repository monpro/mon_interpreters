package lox;

import java.util.ArrayList;
import java.util.List;

import static lox.TokenType.*;

public class Parser {
  private final List<Token> tokens;
  private int current = 0;

  Parser(List<Token> tokens) {
    this.tokens = tokens;
  }

  public List<Statement> parse() {
    List<Statement> statements = new ArrayList<>();
    while (!isAtEnd()) {
      statements.add(declaration());
    }
    return statements;
  }

  private Statement declaration() {
    try {
      if (match(VAR)) return varDeclaration();
      else return statement();
    } catch (ParseError error) {
      synchronize();
      return null;
    }
  }

  private Statement varDeclaration() {
    Token name = consume(IDENTIFIER, "Expect variable name");
    Expr init = null;

    if (match(EQUAL)) {
      init = expression();
    }
    consume(SEMICOLON, "Expect ; after variable declaration");
    return new Statement.Var(name, init);
  }

  private Statement statement() {
    // TODO: fill in more statement types later
    if (match(PRINT)) return printStateStatement();
    else return expressionStatement();
  }

  /**
   * once we met a error say passing too many arguments into function.
   * we should not just break the interpreter
   * instead we should skip those `invalid` statement and go to the next valid one.
   */
  private void synchronize() {
    advance();
    while (!isAtEnd()) {
      if (previous().type == SEMICOLON) return;

      switch (peek().type) {
        case CLASS: case FOR: case FUN: case IF: case PRINT:
        case RETURN: case VAR: case WHILE:
          return;
      }
      advance();
    }
  }

  private Statement printStateStatement() {
    Expr value = expression();
    consume(SEMICOLON, "Expect ';' after value.");
    return new Statement.Print(value);
  }

  private Statement expressionStatement() {
    Expr value = expression();
    consume(SEMICOLON, "Expect ';' after expression.");
    return new Statement.Expression(value);
  }

  private Expr expression() {
    return assignment();
  }

  private Expr assignment() {
    Expr expr = equality();
    if (match(EQUAL)) {
      Token equals = previous();
      Expr value = assignment();
      if (expr instanceof Expr.Variable) {
        Token name = ((Expr.Variable) expr).name;
        return new Expr.Assign(name, value);
      }
      error(equals, "Invalid assignment target.");
    }
    return expr;
  }
  private Expr equality() {
    Expr expr = comparison();
    while (match(BANG_EQUAL, EQUAL_EQUAL)) {
      Token operator = previous();
      Expr right = comparison();
      expr = new Expr.Binary(expr, operator, right);
    }
    return expr;
  }

  private boolean match(TokenType... types) {
    for (TokenType type : types) {
      if (check(type)) {
        advance();
        return true;
      }
    }
    return false;
  }

  private boolean check(TokenType type) {
    if (isAtEnd()) return false;
    return peek().type == type;
  }

  private Token advance() {
    if (!isAtEnd()) current++;
    return previous();
  }

  private boolean isAtEnd() {
    return peek().type == EOF;
  }

  private Token peek() {
    return tokens.get(current);
  }

  private Token previous() {
    return tokens.get(current - 1);
  }

  private Expr comparison() {
    Expr expr = term();
    while (match(GREATER, GREATER_EQUAL, LESS, LESS_EQUAL)) {
      Token operator = previous();
      Expr right = term();
      expr = new Expr.Binary(expr, operator, right);
    }
    return expr;
  }

  private Expr term() {
    Expr expr = factor();
    while (match(MINUS, PLUS)) {
      Token operator = previous();
      Expr right = factor();
      expr = new Expr.Binary(expr, operator, right);
    }
    return expr;
  }

  private Expr factor() {
    Expr expr = unary();
    while (match(SLASH, STAR)) {
      Token operator = previous();
      Expr right = unary();
      expr = new Expr.Binary(expr, operator, right);
    }
    return expr;
  }

  private Expr unary() {
    if (match(BANG, MINUS)) {
      Token operator = previous();
      Expr right = unary();
      return new Expr.Unary(operator, right);
    }
    return primary();
  }

  private Expr primary() {
    if (match(FALSE)) return new Expr.Literal(false);
    if (match(TRUE)) return new Expr.Literal(true);
    if (match(NIL)) return new Expr.Literal(null);
    if (match(NUMBER, STRING)) {
      return new Expr.Literal(previous().literal);
    }
    if (match(IDENTIFIER)) {
      return new Expr.Variable(previous());
    }
    if (match(LEFT_PAREN)) {
      Expr expr = expression();
      consume(RIGHT_PAREN, "Expect ) after expression.");
      return new Expr.Grouping(expr);
    }
    throw error(peek(), "Expect expression");
  }

  /**
   * if current type is matched with given type, advance().
   * Otherwise throw error with current peek token and given message.
   * @param type TokenType.
   * @param message error message
   * @return Token.
   */
  private Token consume(TokenType type, String message) {
    if (check(type)) return advance();
    throw error(peek(), message);
  }

  private ParseError error(Token token, String message) {
    Lox.error(token, message);
    return new ParseError();
  }
}
