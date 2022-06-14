package lox;

public class LoxInstance {

  private LoxClass loxClass;

  public LoxInstance(LoxClass loxClass) {
    this.loxClass = loxClass;
  }

  @Override
  public String toString() {
    return "LoxInstance{" +
        "loxClass=" + loxClass +
        '}';
  }
}
