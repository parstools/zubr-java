package grammar;

import java.util.ArrayList;
import java.util.List;

public class Nonterminal {
    Grammar grammar;
    public List<Rule> rules = new ArrayList<>();

    public int ruleCount() {
        return rules.size();
    }

    void addRule(Rule rule) {
        rules.add(rule);
    }
    Nonterminal(Grammar grammar) {
        this.grammar = grammar;
    }
}
