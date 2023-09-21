package ll;

import grammar.Grammar;
import grammar.Nonterminal;
import grammar.Rule;
import set.Sequence;
import set.SequenceSet;
import set.SetContainer;
import set.TokenSet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.System.out;

public class ParsingTable {
    Grammar grammar;
    List<Map<Integer, Integer>> maps;

    ParsingTable(Grammar grammar) {
        this.grammar = grammar;
        maps = new ArrayList<>();
        for (int i = 0; i < grammar.nonterminals.size(); i++) {
            maps.add(new HashMap<>());
        }
    }

    void clear() {
        for (Map<Integer, Integer> map : maps)
            map.clear();
    }

    void createLL1() {
        SetContainer sc = new SetContainer(grammar);
        sc.reset(1);
        sc.makeFirstSetsK(1);
        sc.makeFollowSetsK(1);
        for (int i = 0; i < grammar.nonterminals.size(); i++) {
            Nonterminal nt = grammar.getNT(i);
            for (int j = 0; j < nt.ruleCount(); j++) {
                Rule rule = nt.rules.get(j);
                TokenSet set = new TokenSet(grammar, 1);
                sc.addFirstOfRule1(set, rule, 0);
                if (set.hasEpsilon()) {
                    set.removeEpsilon();
                    set.unionWith(sc.followSets.get(i));
                }
                SequenceSet prefixes = set.getPrefixes(1);
                for (Sequence seq: prefixes) {
                    if (maps.get(i).containsKey(seq.get(0)))
                        out.println("can't create LL");
                    maps.get(i).put(seq.get(0), j);
                    out.println(i + ":" + seq.get(0) + "," + j);
                }
            }
        }
    }

    void createLL(int k) {
        SetContainer sc = new SetContainer(grammar);
        sc.reset(k);
        sc.makeFirstSetsK(k);
        sc.makeFollowSetsK(k);
        for (int i = 0; i < grammar.nonterminals.size(); i++) {
            Nonterminal nt = grammar.getNT(i);
            for (int j = 0; j < nt.ruleCount(); j++) {
                Rule rule = nt.rules.get(j);
                TokenSet set = new TokenSet(grammar, 1);
                sc.addFirstOfRuleK(set, k, rule, 0);
                set = set.concat(sc.followSets.get(i));
                assert (!set.hasEpsilon());
                SequenceSet prefixes = set.getPrefixes(1);
                for (Sequence seq: prefixes) {
                    if (maps.get(i).containsKey(seq.get(0)))
                        out.println("can't create LL");
                    maps.get(i).put(seq.get(0), j);
                    out.println(i + ":" + seq.get(0) + "," + j);
                }
            }
        }
    }
}
