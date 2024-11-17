package parstools.zubr.lex.regex;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class Alternation extends RegexExpression implements Iterable<RegexExpression> {
    final private List<RegexExpression> alternatives;

    public Alternation() {
        this.alternatives = new ArrayList<>();
    }

    @Override
    public Iterator<RegexExpression> iterator() {
        return alternatives.iterator();
    }

    public void addAlternative(RegexExpression expr) {
        alternatives.add(expr);
    }

    public List<RegexExpression> getAlternatives() {
        return alternatives;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (RegexExpression alt: alternatives) {
            if (!first)
                result.append("|");
            first = false;
            String altStr = alt.toString();
            result.append(altStr);
        }
        return result.toString();
    }

    @Override
    void addLiteralsToSet(Set<String> literalSet) {
        for (RegexExpression expression: alternatives)
            expression.addLiteralsToSet(literalSet);
    }
}