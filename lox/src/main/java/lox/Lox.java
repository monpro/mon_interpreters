package lox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Lox {

  private static boolean hasError = false;
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
    final Expr expr = parser.parse();
    // for now just print the tokens
    if (hasError) return;
    System.out.println(new AstPrinter().print(expr));
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

}
