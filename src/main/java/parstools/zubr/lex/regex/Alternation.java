package parstools.zubr.lex.regex;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Alternation extends RegexExpression {
    private List<RegexExpression> alternatives;

    public Alternation() {
        this.alternatives = new ArrayList<>();
    }

    public void addAlternative(RegexExpression expr) {
        alternatives.add(expr);
    }

    public List<RegexExpression> getAlternatives() {
        return alternatives;
    }

    @Override
    public String toString() {
        return alternatives.stream()
                .map(Object::toString)
                .collect(Collectors.joining("|"));
    }
}