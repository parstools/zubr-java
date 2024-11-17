package parstools.zubr.ebnf;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EBNFGrammar {
    List<EBNFRule> rules;
    Set<String> terminals = new HashSet<>();
    Set<String> nonterminals = new HashSet<>();
    public EBNFGrammar(List<EBNFRule> rules) {
        this.rules = rules;
        for (EBNFRule rule: rules)
            nonterminals.add(rule.nonTerminal);
        for (EBNFRule rule: rules) {
            Set<String> literals = rule.production.literals();
            for (String literal: literals)
                if (!nonterminals.contains(literal))
                    terminals.add(literal);
        }
    }

    private static List<EBNFRule> rulesFromStrings(String[] input) {
        List<EBNFRule> erules = new ArrayList<>();
        for (String line: input)
            erules.add(new EBNFRule(line));
        return erules;
    }

    public EBNFGrammar(String[] input) {
        this(rulesFromStrings(input));
    }
}
