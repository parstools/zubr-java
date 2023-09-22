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
    List<TableMap> maps;
    List<TokenSet> ruleSets;

    ParsingTable(Grammar grammar) {
        this.grammar = grammar;
        ruleSets = new ArrayList<>();
        maps = new ArrayList<>();
        for (int i = 0; i < grammar.nonterminals.size(); i++) {
            maps.add(new TableMap());
        }
    }

    void clear() {
        for (TableMap map : maps)
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
                SingleTokenSet sts = set.firstTokens();
                for (int t : sts) {
                    if (maps.get(i).containsKey(t))
                        out.println("can't create LL");
                    else
                        maps.get(i).put(t, new TableElem(i));
                    out.println(i + ":" + t + "," + j);
                }
            }
        }
    }

    void createLL(int k) {
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
            TableMap row = maps.get(i);
            for (int j = 0; j < nt.ruleCount(); j++) {
                Rule rule = nt.rules.get(j);
                SingleTokenSet sts = ruleSets.get(rule.globalIndex).firstTokens();
                for (int t : sts) {
                    if (row.containsKey(t))
                        row.get(t).alts.add(rule.globalIndex);
                    else
                        row.put(t, new TableElem(j));
                    out.println(i + ":" + t + "," + j);
                }
            }
            for (int t = -1; t < grammar.nonterminals.size(); t++) {
                if (!row.containsKey(t))
                    continue;
                int altCount = row.get(t).alts.size();
                assert (altCount >= 1);
                if (altCount > 1) {
                    Sequence seq = new Sequence(grammar);
                    seq.add(t);
                    for (int j=0; j<altCount; j++) {
                        TokenSet set = ruleSets.get(row.get(t).alts.get(j));
                        SingleTokenSet sts = set.nthTokens(seq);
                        row.get(t).subMap = new TableMap();
                        TableMap map1 = row.get(t).subMap;
                        for (int t1 : sts) {
                            if (map1.containsKey(t1))
                                map1.get(t1).alts.add(j);
                            else
                                map1.put(t1, new TableElem(j));
                        }
                        for (int t1 = -1; t1 < grammar.nonterminals.size(); t1++) {
                            if (!map1.containsKey(t1))
                                continue;
                            int altCount1 = map1.get(t1).alts.size();
                            if (altCount1>1)
                                out.println("can't create LL");
                        }
                    }
                }
            }
        }
    }
}
