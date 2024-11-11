package parstools.zubr.lex.regex;

import java.util.ArrayList;
import java.util.List;

public class Alternation extends RegexExpression {
    final private List<RegexExpression> alternatives;

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
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (RegexExpression alt: alternatives) {
            if (!first)
                result.append("|");
            first = false;
            String altStr = alt.toString();
            if (altStr.length() > 1) {
                result.append("(");
                result.append(altStr);
                result.append(")");
            } else
                result.append(altStr);
        }
        return result.toString();
    }
}