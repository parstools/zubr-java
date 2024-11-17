package parstools.zubr.ebnf;

import parstools.zubr.grammar.Grammar;
import parstools.zubr.grammar.Nonterminal;
import parstools.zubr.grammar.Rule;
import parstools.zubr.grammar.Terminal;
import parstools.zubr.grammar.names.NameGenerator;
import parstools.zubr.lex.regex.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Converter class that transforms EBNF rules into BNF.
 */
public class EBNFtoBNFConverter {
    private Grammar grammar;
    private EBNFGrammar egrammar;
    boolean preferRightRecursion;
    private NameGenerator generator;

    public EBNFtoBNFConverter(boolean preferRightRecursion, NameGenerator generator) {
        this.preferRightRecursion = preferRightRecursion;
        this.generator = generator;
    }

    public Grammar convert(EBNFGrammar egrammar) throws RuntimeException {
        this.egrammar = egrammar;
        List<EBNFRule> ebnfRules = egrammar.rules;
        fillGrammarSymbols(egrammar);
        for (EBNFRule rule : ebnfRules) {
            RegexExpression expr = rule.production.getRoot();
            List<String> symbols = processExpression(expr, rule.nonTerminal, false);
            if (expr instanceof Concatenation)
                grammar.addRule(new Rule(grammar, rule.nonTerminal, symbols));
        }
        return grammar;
    }

    private void fillGrammarSymbols(EBNFGrammar egrammar) {
        grammar = new Grammar();
        for (String ntName: egrammar.nonTerminals) {
            generator.registerName(ntName);
            grammar.nonterminals.add(new Nonterminal(grammar, ntName));
        }
        for (String tName: egrammar.terminals) {
            generator.registerName(tName);
            grammar.terminals.add(new Terminal(grammar, tName));
        }
    }

    /**
     * Recursively process a RegexExpression and generate corresponding BNF symbols.
     *
     * @param expr    The regex expression to process
     * @param parent  The parent non-terminal (for generating unique names)
     * @return List of Symbols representing the processed expression
     */
    private List<String> processExpression(RegexExpression expr, String parent, boolean generateNew) {
        List<String> symbols = new ArrayList<>();
        if (expr instanceof Concatenation) {
            processConcatenation((Concatenation) expr, parent, symbols);
        } else if (expr instanceof Alternation) {
            processAlternation((Alternation) expr, parent, symbols);
        } else if (expr instanceof QuantifierExpression) {
            processQuantifierExpression((QuantifierExpression) expr, parent, symbols, generateNew);
        } else if (expr instanceof Literal) {
            Literal lit = (Literal) expr;
            symbols.add(""+lit.getValue());//todo value ma być stringiem
        } else {
            throw new RuntimeException("Unsupported RegexExpression: " + expr.getClass().getName());
        }
        return symbols;
    }

    private void processQuantifierExpression(QuantifierExpression expr, String parent, List<String> symbols, boolean generateNew) {
        QuantifierExpression qexpr = expr;
        Quantifier quant = qexpr.getQuantifier();
        RegexExpression subExpr = qexpr.getExpression();
        List<String> subSymbols = processExpression(subExpr, parent, true);
        // Depending on the quantifier, create new productions
        String nt;
        if (generateNew)
            nt = generator.generate(parent);
        else
            nt = parent;
        switch (quant) {
            case ZERO_OR_MORE: // *
                List<String> p1 = new ArrayList<>();
                if (preferRightRecursion) {
                    p1.addAll(subSymbols);
                    p1.add(nt);
                } else {
                    p1.add(nt);
                    p1.addAll(subSymbols);
                }
                grammar.addRule(new Rule(grammar, nt, p1));
                grammar.addRule(new Rule(grammar, nt, null));// Empty production = ε
                break;
            case ONE_OR_MORE: // +
                List<String> p2 = new ArrayList<>();
                if (preferRightRecursion) {
                    p2.addAll(subSymbols);
                    p2.add(nt);
                } else {
                    p2.add(nt);
                    p2.addAll(subSymbols);
                }
                grammar.addRule(new Rule(grammar, nt, p2));
                grammar.addRule(new Rule(grammar, nt, subSymbols));
                break;
            case ZERO_OR_ONE: // ?
                grammar.addRule(new Rule(grammar, nt, subSymbols));
                grammar.addRule(new Rule(grammar, nt, null));// Empty production = ε
                break;
        }
        symbols.add(nt);
    }

    private void processAlternation(Alternation expr, String parent, List<String> symbols) {
        Alternation alt = expr;
        for (RegexExpression alternative : alt.getAlternatives()) {
            List<String> altSymbols = processExpression(alternative, parent, true);
            grammar.addRule(new Rule(grammar, parent, altSymbols));
        }
        symbols.add(parent);
    }

    private void processConcatenation(Concatenation expr, String parent, List<String> symbols) {
        Concatenation concat = expr;
        for (RegexExpression subExpr : concat.getExpressions()) {
            symbols.addAll(processExpression(subExpr, parent, true));
        }
    }
}