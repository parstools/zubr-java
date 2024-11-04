package ll;

import grammar.Grammar;
import grammar.Nonterminal;
import grammar.Rule;
import grammar.Symbol;
import set.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParsingTable {
    Grammar grammar;
    List<TableElem> rows;
    Map<Rule, TokenSet> ruleSets;

    public ParsingTable(Grammar grammar) {
        this.grammar = grammar;
    }

    private boolean isConflict() {
        for (Nonterminal nt : grammar.nonterminals)
            for (int i = 0; i < nt.rules.size()-1; i++) {
                Rule rule0 = nt.rules.get(i);
                TokenSet ts0 = ruleSets.get(rule0);
                for (int j = i + 1; j < nt.rules.size(); j++) {
                    Rule rule1 = nt.rules.get(j);
                    TokenSet ts1 = ruleSets.get(rule1);
                    if (ts0.isIntersecion(ts1))
                        return true;
                }
            }
        return false;
    }

    public boolean createLL(int k) {
        ruleSets = new HashMap<>();
        rows = new ArrayList<>();
        for (int i = 0; i < grammar.nonterminals.size(); i++) {
            rows.add(new TableElem(grammar));
        }
        SetContainer sc = new SetContainer(grammar);
        sc.reset(k);
        sc.makeFirstSetsK(k);
        sc.makeFollowSetsK(k);
        for (Nonterminal nt : grammar.nonterminals) {
            for (Rule rule : nt.rules) {
                TokenSet set = new TokenSet(grammar, k);
                sc.addFirstOfRuleK(set, k, rule, 0);
                assert(rule.owner == nt);
                set = set.concat(sc.followSets.get(nt.getIndex()));
                assert (set.dbgWellFormedLLSet());
                ruleSets.put(rule, set);
            }
        }
        if (isConflict()) return false;
        return true;
        /*for (int i = 0; i < grammar.nonterminals.size(); i++) {
            Nonterminal nt = grammar.nonterminals.get(i);
            TableElem row = rows.get(i);
            for (int j = 0; j < nt.ruleCount(); j++) {
                Rule rule = nt.rules.get(j);
                row.addAlt(rule);
            }
            if (!row.incLookahead(new Sequence(grammar), ruleSets))
                return false;
        }
        return true;*/
    }
}
