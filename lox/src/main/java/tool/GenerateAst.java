package tool;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

public class GenerateAst {
  public static void main(String[] args) throws IOException {
    if (args.length != 1) {
      System.err.println("Usage: generate_ast <output directory>");
      System.exit(64);
    }
    String outputDir = args[0];
    System.out.println(outputDir);
    defineAst(outputDir, "Expr", Arrays.asList(
        "Binary   : Expr left, Token operator, Expr right",
        "Call     : Expr callee, Token paren, List<Expr> arguments",
        "Grouping : Expr expression",
        "Literal  : Object value",
        "Unary    : Token operator, Expr right",
        "Variable : Token name",
        "Assign   : Token name, Expr value",
        "Logical  : Expr left, Token operator, Expr right"
    ));

    defineAst(outputDir, "Statement", Arrays.asList(
        "Expression : Expr expression",
        "Print      : Expr expression",
        "Var        : Token name, Expr initializer",
        "Block      : List<Statement> statements",
        "If         : Expr condition, Statement thenBranch, Statement elseBranch",
        "While      : Expr condition, Statement body"
    ));

  }

  /**
   * T
   * @param outputDir String.
   * @param baseName base class name.
   * @param types an expression string as className : field,field
   *              "Binary : Expr left, Token operator, Expr right"
   * @throws IOException Exception.
   */
  private static void defineAst(String outputDir, String baseName, List<String> types) throws IOException {
    String path = outputDir + "/" + baseName + ".java";
    PrintWriter writer = new PrintWriter(path, StandardCharsets.UTF_8);
    writer.println("package lox;");
    writer.println();
    writer.println("import java.util.List;");
    writer.println();
    writer.println("abstract class " + baseName + " {");
    defineVisitor(writer, baseName, types);
    for (String type: types) {
      String className = type.split(":")[0].trim();
      String fieldsListString = type.split(":")[1].trim();
      defineType(writer, baseName, className, fieldsListString);
    }
    writer.println();
    writer.println("  abstract <R> R accept(Visitor<R> visitor);");
    writer.println("}");
    writer.close();
  }

  private static void defineVisitor(PrintWriter writer, String baseName, List<String> types) {
    writer.println("  interface Visitor<R> {");
    for (String type : types) {
      String typeName = type.split(":")[0].trim();
      writer.println("    R visit" + typeName + baseName + "(" +
          typeName + " " + baseName.toLowerCase() + ");");
    }
    writer.println("  }");
    writer.println();
  }

  private static void defineType(PrintWriter writer, String baseName, String className, String fieldsListString) {
    writer.println("  static class " + className + " extends " + baseName + " {" );
    writer.println("    " + className + "(" + fieldsListString + ") {");
    String[] fields = fieldsListString.split(", ");
    for (String field : fields) {
      String name = field.split(" ")[1];
      writer.println("      this." + name + " = " + name + ";");
    }
    writer.println("    }");
    writer.println();
    writer.println("    @Override");
    writer.println("    <R> R accept(Visitor<R> visitor) {");
    writer.println("      return visitor.visit" + className + baseName + "(this);");
    writer.println("    }");
    for (String field : fields) {
      writer.println("  final " + field + ";");
    }
    writer.println("  }");
    writer.println();
  }
}
