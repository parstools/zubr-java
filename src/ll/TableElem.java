package ll;

import grammar.Grammar;
import grammar.Rule;
import set.Sequence;
import set.SingleTokenSet;
import set.TokenSet;

import java.util.ArrayList;
import java.util.List;

public class TableElem {
    List<Integer> alts;
    TableMap subMap;
    Grammar grammar;

    void addAlt(Rule rule) {
        alts.add(rule.globalIndex);
    }

    TableElem(Grammar grammar) {
        this.grammar = grammar;
        this.alts = new ArrayList<>();
    }

    boolean incLookahead(Sequence seq, List<TokenSet> ruleSets) {
        subMap = new TableMap();
        for (int i = 0; i < alts.size(); i++) {
            int globRuleIndex = alts.get(i);
            TokenSet set = ruleSets.get(globRuleIndex);
            SingleTokenSet sts = set.nthTokens(seq);
            if(sts.isEmpty())
                return false;
            for (int t : sts) {
                if (!subMap.containsKey(t))
                    subMap.put(t, new TableElem(grammar));
                subMap.get(t).alts.add(globRuleIndex);
            }
            for (int t = -1; t < grammar.nonterminals.size(); t++) {
                if (!subMap.containsKey(t))
                    continue;
                int altCount2 = subMap.get(t).alts.size();
                if (altCount2 > 1) {
                    Sequence seq1 = seq.clone();
                    seq1.add(t);
                    if (!subMap.get(t).incLookahead(seq1, ruleSets))
                        return false;
                }
            }
        }
        return true;
    }
}
