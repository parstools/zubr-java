package parstools.zubr.lex.regex;

public class Literal extends RegexExpression {
    final private char value;

    public Literal(char value) {
        this.value = value;
    }

    public char getValue() {
        return value;
    }

    @Override
    public String toString() {
        return Character.toString(value);
    }
}