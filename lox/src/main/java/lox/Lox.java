package lox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Lox {
  private static final Interpreter interpreter = new Interpreter();
  private static boolean hasError = false;
  private static boolean hadRunTimeError = false;
  private static boolean hadResolverError = false;

  public static void main(String[] args) throws IOException {
    if (args.length > 1) {
      System.out.println("Usage: lox [script]");
      System.exit(64);
    } else if (args.length == 1) {
      runFile(args[0]);
    } else {
      runPrompt();
    }
  }

  private static void runFile(String path) throws IOException {
    final byte[] bytes = Files.readAllBytes(Paths.get(path));
    run(new String(bytes, Charset.defaultCharset()));
    if (hasError) {
      System.exit(65);
    }
    if (hadRunTimeError) {
      System.exit(70);
    }
  }

  private static void runPrompt() throws IOException {
    final InputStreamReader input = new InputStreamReader(System.in);
    final BufferedReader reader = new BufferedReader(input);

    while (true){
      System.out.println(">  ");
      final String line = reader.readLine();
      if (line == null) break;
      run(line);
      hasError = false;
    }
  }


  // core logic of our interpreter
  private static void run(String source) {
    final Scanner scanner = new Scanner(source);
    final List<Token> tokens = scanner.scanTokens();
    final Parser parser = new Parser(tokens);
    final List<Statement> statements = parser.parse();
    final Resolver resolver = new Resolver(interpreter);
    // for now just print the tokens
    if (hasError) return;
    // first do static analysis of statements
    resolver.resolve(statements);
    // if had resolver logic, do not interpret statements
    if (hadResolverError) return;
    // then interpret statements
    interpreter.interpret(statements);
  }

  static void error(int line, String message) {
    report(line, "", message);
  }

  static void error(Token token, String message) {
    if (token.type == TokenType.EOF) {
      report(token.line, " at end", message);
    } else {
      report(token.line, " at '" + token.lexeme + "'", message);
    }
  }
  private static void report(int line, String where, String message) {
    System.err.println("[line " + line + "] Error" + where + ": " + message);
    hasError = true;
  }

  protected static void runTimeError(RunTimeError error) {
    System.out.println(error.getMessage() + "\n[line " + error.token.line + "]");
    hadRunTimeError = true;
  }
}
