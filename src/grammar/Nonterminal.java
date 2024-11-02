package grammar;

import util.Hash;

import java.util.ArrayList;
import java.util.List;

public class Nonterminal extends Symbol {
    @Override
    public int getIndex() {
        return grammar.nonterminals.indexOf(this);
    }
    public List<Rule> rules = new ArrayList<>();

    public Object clone() {
        Nonterminal newNt = new Nonterminal(grammar, name);
        newNt.rules = new ArrayList<>(rules);
        return newNt;
    }

    public int ruleCount() {
        return rules.size();
    }
    public boolean isRecursive = false;

    void addRule(Rule rule) {
        rule.owner = this;
        rule.index = rules.size();
        rules.add(rule);
    }

    Nonterminal(Grammar grammar, String name) {
        super(grammar, false, name);
        this.grammar = grammar;
        minLen = -1;
        maxLen = -1;
    }

    boolean computeMinLen() {
        int old = minLen;
        boolean changed = false;
        for (Rule rule : rules) {
            if (rule.computeMinLen())
                changed = true;
        }
        for (Rule rule : rules) {
            if (rule.minLen >= 0) {
                if (minLen < 0)
                    minLen = rule.minLen;
                else
                    minLen = Math.min(minLen, rule.minLen);
            }
        }
        return minLen != old || changed;
    }

    boolean computeMaxLen() {
        final int infinity = Integer.MAX_VALUE;
        int old = maxLen;
        boolean changed = false;
        for (Rule rule : rules) {
            if (rule.computeMaxLen())
                changed = true;
        }
        for (Rule rule : rules) {
            if (rule.maxLen >= 0) {
                if (maxLen < 0)
                    maxLen = rule.maxLen;
                else
                    maxLen = Math.max(maxLen, rule.maxLen);
            }
        }
        return maxLen != old || changed;
    }

    @Override
    public int hashCode() {
        Hash h = new Hash();
        for (Rule rule : rules)
            h.add(rule.hashCode());
        return h.hash();
    }
}
