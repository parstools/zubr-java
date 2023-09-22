package ll;

import grammar.Grammar;
import grammar.Nonterminal;
import grammar.Rule;
import set.*;

import java.util.ArrayList;
import java.util.List;

import static java.lang.System.out;

public class ParsingTable {
    Grammar grammar;
    List<TableElem> rows;
    List<TokenSet> ruleSets;

    ParsingTable(Grammar grammar) {
        this.grammar = grammar;
        ruleSets = new ArrayList<>();
        rows = new ArrayList<>();
        for (int i = 0; i < grammar.nonterminals.size(); i++) {
            rows.add(new TableElem(grammar));
        }
    }

    boolean createLL(int k) {
        SetContainer sc = new SetContainer(grammar);
        sc.reset(k);
        sc.makeFirstSetsK(k);
        sc.makeFollowSetsK(k);
        for (Rule rule : grammar.globalRules) {
            TokenSet set = new TokenSet(grammar, k);
            sc.addFirstOfRuleK(set, k, rule, 0);
            set = set.concat(sc.followSets.get(rule.owner.index));
            assert (!set.hasEpsilon());
            assert (ruleSets.size() == rule.globalIndex);
            ruleSets.add(set);
        };
        for (int i = 0; i < grammar.nonterminals.size(); i++) {
            Nonterminal nt = grammar.getNT(i);
            TableElem row = rows.get(i);
            for (int j = 0; j < nt.ruleCount(); j++) {
                Rule rule = nt.rules.get(j);
                row.addAlt(rule);
            }
            if (!row.incLookahead(new Sequence(grammar), ruleSets)) {
                out.println("LL failed");
                return false;
            }
        }
        out.println("LL OK");
        return true;
    }
}
