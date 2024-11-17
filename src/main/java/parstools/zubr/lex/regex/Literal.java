package parstools.zubr.lex.regex;

import java.util.Set;

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

    @Override
    void addLiteralsToSet(Set<String> literalSet) {
        literalSet.add(""+value);//todo value ma być stringiem

    }
}