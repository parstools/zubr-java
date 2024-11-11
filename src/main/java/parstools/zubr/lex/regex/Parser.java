package parstools.zubr.lex.regex;

import java.util.*;

class Parser {
    private String pattern;
    private int index;

    public Parser(String pattern) {
        this.pattern = pattern;
        this.index = 0;
    }

    public RegexExpression parse() throws Exception {
        RegexExpression expr = parseExpression();
        if (index < pattern.length()) {
            throw new RuntimeException("Unexpected symbol in position " + index);
        }
        return expr;
    }

    private RegexExpression parseExpression() throws Exception {
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

    private RegexExpression parseTerm() throws Exception {
        Concatenation concat = new Concatenation();
        while (index < pattern.length() && peek() != ')' && peek() != '|') {
            RegexExpression factor = parseFactor();
            concat.addExpression(factor);
        }
        if (concat.getExpressions().size() == 1) {
            return concat.getExpressions().get(0);
        }
        return concat;
    }

    private RegexExpression parseFactor() throws Exception {
        RegexExpression base = parseBase();
        if (index < pattern.length()) {
            char next = peek();
            if (next == '*' || next == '+' || next == '?') {
                consume();
                Quantifier quant;
                switch (next) {
                    case '*':
                        quant = Quantifier.ZERO_OR_MORE;
                        break;
                    case '+':
                        quant = Quantifier.ONE_OR_MORE;
                        break;
                    case '?':
                        quant = Quantifier.ZERO_OR_ONE;
                        break;
                    default:
                        throw new RuntimeException("Unknown quantifier: " + next);
                }
                return new QuantifierExpression(base, quant);
            }
        }
        return base;
    }

    private RegexExpression parseBase() throws Exception {
        char current = peek();
        if (current == '(') {
            consume(); // konsumuje '('
            RegexExpression expr = parseExpression();
            if (index >= pattern.length() || peek() != ')') {
                throw new RuntimeException("Expected ')' in position " + index);
            }
            consume(); // konsumuje ')'
            return expr;
        } else {
            consume();
            return new Literal(current);
        }
    }

    private char peek() throws Exception {
        if (index < pattern.length()) {
            return pattern.charAt(index);
        } else {
            throw new RuntimeException("End of pattern reached, more characters expected.");
        }
    }

    private void consume() {
        index++;
    }
}