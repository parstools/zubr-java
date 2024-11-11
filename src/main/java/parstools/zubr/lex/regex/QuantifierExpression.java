package parstools.zubr.lex.regex;

public class QuantifierExpression extends RegexExpression {
    private RegexExpression expression;
    private Quantifier quantifier;

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
        String quant;
        switch (quantifier) {
            case ZERO_OR_MORE:
                quant = "*";
                break;
            case ONE_OR_MORE:
                quant = "+";
                break;
            case ZERO_OR_ONE:
                quant = "?";
                break;
            default:
                quant = "";
        }
        String exprString = expression.toString();
        if (exprString.length() > 1)
            return "(" + exprString + ")" + quant;
        else
            return exprString + quant;
    }
}
