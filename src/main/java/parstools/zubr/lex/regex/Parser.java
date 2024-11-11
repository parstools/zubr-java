package parstools.zubr.lex.regex;

class Parser {
    final private String pattern;
    private int index;

    public Parser(String pattern) {
        this.pattern = pattern;
        this.index = 0;
    }

    public RegexExpression parse() throws RuntimeException {
        RegexExpression expr = parseExpression();
        if (index < pattern.length()) {
            throw new RuntimeException("Unexpected symbol in position " + index);
        }
        return expr;
    }

    private RegexExpression parseExpression() throws RuntimeException {
        RegexExpression term = parseTerm();
        if (index < pattern.length() && peek() == '|') {
            Alternation alt = new Alternation();
            alt.addAlternative(term);
            while (index < pattern.length() && peek() == '|') {
                consume(); // konsumuje '|'
                RegexExpression nextTerm = parseTerm();
                alt.addAlternative(nextTerm);
            }
            return alt;
        } else {
            return term;
        }
    }

    private RegexExpression parseTerm() throws RuntimeException {
        Concatenation concat = new Concatenation();
        while (index < pattern.length() && peek() != ')' && peek() != '|') {
            RegexExpression factor = parseFactor();
            concat.addExpression(factor);
        }
        if (concat.getExpressions().size() == 1) {
            return concat.getExpressions().getFirst();
        }
        return concat;
    }

    private RegexExpression parseFactor() throws RuntimeException {
        RegexExpression base = parseBase();
        if (index < pattern.length()) {
            char next = peek();
            if (next == '*' || next == '+' || next == '?') {
                consume();
                Quantifier quant = switch (next) {
                    case '*' -> Quantifier.ZERO_OR_MORE;
                    case '+' -> Quantifier.ONE_OR_MORE;
                    case '?' -> Quantifier.ZERO_OR_ONE;
                    default -> throw new RuntimeException("Unknown quantifier: " + next);
                };
                return new QuantifierExpression(base, quant);
            }
        }
        return base;
    }

    private RegexExpression parseBase() throws RuntimeException {
        char current = peek();
        if (current == '(') {
            consume();
            RegexExpression expr = parseExpression();
            if (index >= pattern.length() || peek() != ')') {
                throw new RuntimeException("Expected ')' in position " + index);
            }
            consume();
            return expr;
        } else {
            consume();
            return new Literal(current);
        }
    }

    private char peek() {
        assert (index < pattern.length());
        return pattern.charAt(index);
    }

    private void consume() {
        index++;
    }
}