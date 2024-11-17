package parstools.zubr.regex;

import java.util.Set;

public class QuantifierExpression extends RegexExpression {
    final private RegexExpression expression;
    final private Quantifier quantifier;

    public QuantifierExpression(RegexExpression expression, Quantifier quantifier) {
        this.expression = expression;
        this.quantifier = quantifier;
    }

    public RegexExpression getExpression() {
        return expression;
    }
    public Quantifier getQuantifier() {
        return quantifier;
    }

    @Override
    public String toString() {
        String quant = switch (quantifier) {
            case ZERO_OR_MORE -> "*";
            case ONE_OR_MORE -> "+";
            case ZERO_OR_ONE -> "?";
        };
        String exprString = expression.toString();
        if (exprString.length() > 1)
            return "(" + exprString + ")" + quant;
        else
            return exprString + quant;
    }

    @Override
    void addLiteralsToSet(Set<String> literalSet) {
        expression.addLiteralsToSet(literalSet);
    }
}
