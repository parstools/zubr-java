package parstools.zubr.lex.regex;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Concatenation extends RegexExpression {
    final private List<RegexExpression> expressions;

    public Concatenation() {
        this.expressions = new ArrayList<>();
    }

    public void addExpression(RegexExpression expr) {
        expressions.add(expr);
    }

    public List<RegexExpression> getExpressions() {
        return expressions;
    }

    @Override
    public String toString() {
        return expressions.stream()
                .map(Object::toString)
                .collect(Collectors.joining());
    }
}
