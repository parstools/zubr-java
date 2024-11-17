package parstools.zubr.ebnf;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EBNFGrammar {
    List<EBNFRule> rules;
    Set<String> terminals = new HashSet<>();
    Set<String> nonTerminals = new HashSet<>();
    private String startSymbol;
    EBNFGrammar(List<EBNFRule> rules) {
        startSymbol = rules.getFirst().nonTerminal;
        this.rules = rules;
        for (EBNFRule rule: rules)
            nonTerminals.add(rule.nonTerminal);
        for (EBNFRule rule: rules) {
            Set<String> literals = rule.production.literals();
            for (String literal: literals)
                if (!nonTerminals.contains(literal))
                    terminals.add(literal);
            System.out.println(terminals);
        }
    }
}
