package set;

import generator.Generator;
import generator.RuleOrder;
import grammar.Grammar;
import grammar.Nonterminal;
import grammar.Symbol;
import util.Hash;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import grammar.Rule;

import static java.lang.System.out;

public class SetContainer {
    Grammar grammar;
    List<TokenSet> firstSets = new ArrayList<>();
    List<TokenSet> followSets = new ArrayList<>();

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (TokenSet ts : firstSets)
            sb.append(ts.toString());
        sb.append(" | ");
        for (TokenSet ts : followSets)
            sb.append(ts.toString());
        sb.append("]");
        return sb.toString();
    }

    @Override
    public int hashCode() {
        Hash h = new Hash();
        for (TokenSet ts : firstSets)
            h.add(ts.hashCode());
        for (TokenSet ts : followSets)
            h.add(ts.hashCode());
        return h.hash();
    }

    public SetContainer(Grammar grammar) {
        this.grammar = grammar;
    }

    public void reset(int k) {
        firstSets.clear();
        followSets.clear();
        for (int i = 0; i < grammar.nonterminals.size(); i++) {
            TokenSet firstSet = new TokenSet(grammar, k);
            firstSets.add(firstSet);
            TokenSet followSet = new TokenSet(grammar, k);
            followSets.add(followSet);
        }
    }

    public void computeSetsByGeneration(int k, int maxLen, int nextLimit) {
        Generator generator = new Generator(grammar, maxLen, RuleOrder.roShuffle);
        int counter = 0;
        while (generator.next()) {
            counter++;
            for (int i = 0; i < grammar.nonterminals.size(); i++) {
                SequenceSet sset = new SequenceSet();
                generator.collectFirst(i, k, sset);
                firstSets.get(i).addAllSSeq(sset);
                sset = new SequenceSet();
                generator.collectFollow(i, k, sset);
                followSets.get(i).addAllSSeq(sset);
            }
            if (counter >= nextLimit)
                break;
        }
    }

    public void computeSetsByRangeGeneration(int k, int maxLenStart, int maxLenEnd, int nextLimit) {
        for (int maxLen = maxLenStart; maxLen <= maxLenEnd; maxLen++)
            computeSetsByGeneration(k, maxLen, nextLimit);
    }

    public void readTest1(List<String> lines) {
        firstSets.clear();
        followSets.clear();
        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i);
            String[] parts = line.split("\t");
            assert (parts.length == 4);
            int nt = grammar.findSymbol(parts[0]).index;
            boolean nullable = parts[1].equals("✔");
            TokenSet first = new TokenSet(grammar, 1);
            if (nullable) {
                Sequence seq = new Sequence(grammar, "");
                first.addSeq(seq);
            }

            String[] parts2 = parts[2].split(", ");
            for (String part : parts2) {
                Sequence seq = new Sequence(grammar, part);
                first.addSeq(seq);
            }
            TokenSet follow = new TokenSet(grammar, 1);
            String[] parts3 = parts[3].split(", ");
            for (String part : parts3) {
                Sequence seq = new Sequence(grammar, part);
                follow.addSeq(seq);
            }
            firstSets.add(first);
            followSets.add(follow);
        }
    }

    public void dump(PrintWriter printWriter) {
        printWriter.println("FIRST:");
        for (int i = 0; i < firstSets.size(); i++)
            printWriter.println(grammar.getNonTerminalName(i) + " " + firstSets.get(i).toString());
        printWriter.println("FOLLOW:");
        for (int i = 0; i < followSets.size(); i++)
            printWriter.println(grammar.getNonTerminalName(i) + " " + followSets.get(i).toString());
    }

    boolean addFirstOfRule1(TokenSet outSet, Rule rule, int start) {
        boolean isEpsilon = true;
        boolean changed = false;
        for (int i = start; i < rule.size(); i++) {
            Symbol symbol = rule.get(i);
            if (symbol.terminal) {
                isEpsilon = false;
                Sequence seq = new Sequence(grammar, symbol);
                if (outSet.addSeq(seq))
                    changed = true;
                break;
            } else {
                TokenSet firstY = firstSetForSymbol(symbol);
                if (outSet.addTier(firstY.tiers.get(1)))
                    changed = true;
                if (!firstY.hasEpsilon()) {
                    isEpsilon = false;
                    break;
                }
            }
        }
        if (isEpsilon) {
            if (outSet.addEpsilon())
                changed = true;
        }
        return changed;
    }

    boolean addFirstOfRuleK(TokenSet outSet, int k, Rule rule, int start) {
        TokenSet tempSet = new TokenSet(grammar, k);
        tempSet.addEpsilon();
        for (int i = start; i < rule.size(); i++) {
            Symbol symbol = rule.get(i);
            if (symbol.terminal) {
                tempSet.appendStrings(symbol);
            } else {
                TokenSet firstY = firstSetForSymbol(symbol);
                if (!firstY.hasLen(Math.min(grammar.getNT(symbol.index).minLen, k - tempSet.minLen()))) {
                    break;
                }
                tempSet.concatPrefixes(firstY);
            }
            if (!tempSet.concatenable()) break;
        }
        boolean changed = outSet.unionWith(tempSet);//at start tempSet) has epislon, but vcan be replaced by sequence
        return changed;
    }

    TokenSet firstSetForIndex(int index) {
        return firstSets.get(index);
    }

    TokenSet followSetForIndex(int index) {
        return followSets.get(index);
    }

    TokenSet firstSetForSymbol(Symbol symbol) {
        assert (!symbol.terminal);
        return firstSetForIndex(symbol.index);
    }

    TokenSet followSetForSymbol(Symbol symbol) {
        assert (!symbol.terminal);
        return followSetForIndex(symbol.index);
    }

    public void makeFirstSets1() {
        boolean changed;
        do {
            changed = false;
            for (int i = grammar.nonterminals.size() - 1; i >= 0; i--) {
                Nonterminal X = grammar.getNT(i);
                for (int j = X.ruleCount() - 1; j >= 0; j--) {
                    Rule rule = X.rules.get(j);
                    boolean retChanged = addFirstOfRule1(firstSetForIndex(i), rule, 0);
                    if (retChanged) changed = true;
                }
            }
        } while (changed);
    }

    public void makeFirstSetsK(int k) {
        boolean changed;
        do {
            changed = false;
            for (int i = grammar.nonterminals.size() - 1; i >= 0; i--) {
                Nonterminal X = grammar.getNT(i);
                for (int j = X.ruleCount() - 1; j >= 0; j--) {
                    Rule rule = X.rules.get(j);
                    boolean retChanged = addFirstOfRuleK(firstSetForIndex(i), k, rule, 0);
                    if (retChanged) changed = true;
                }
            }
        } while (changed);
    }

    public void makeFollowSets1() {
        followSetForIndex(0).addSeq(new Sequence(grammar, "$"));
        boolean changed;
        do {
            changed = false;
            for (int i = 0; i < grammar.nonterminals.size(); i++) {
                Nonterminal X = grammar.getNT(i);
                for (int j = 0; j < X.ruleCount(); j++) {
                    Rule rule = X.rules.get(j);
                    for (int k = 0; k < rule.size(); k++) {
                        Symbol symbol = rule.get(k);
                        if (!symbol.terminal) {
                            TokenSet tempSet = new TokenSet(grammar, 1);
                            addFirstOfRule1(tempSet, rule, k + 1);
                            boolean retChanged = followSetForSymbol(symbol).addTier(tempSet.tiers.get(1));
                            if (retChanged) changed = true;
                            if (tempSet.hasEpsilon()) {
                                retChanged = followSetForSymbol(symbol).addTier(followSetForIndex(i).tiers.get(1));
                                if (retChanged) changed = true;
                            }
                        }
                    }
                }
            }
        } while (changed);
    }

    public void makeFollowSetsK() {
        followSetForIndex(0).addSeq(new Sequence(grammar, "$"));
        boolean changed;
        do {
            changed = false;
            for (int i = 0; i < grammar.nonterminals.size(); i++) {
                Nonterminal X = grammar.getNT(i);
                for (int j = 0; j < X.ruleCount(); j++) {
                    Rule rule = X.rules.get(j);
                    for (int k = 0; k < rule.size(); k++) {
                        Symbol symbol = rule.get(k);
                        if (!symbol.terminal) {
                            TokenSet tempSet = new TokenSet(grammar, 1);
                            addFirstOfRule1(tempSet, rule, k + 1);
                            boolean retChanged = followSetForSymbol(symbol).addTier(tempSet.tiers.get(1));
                            if (retChanged) changed = true;
                            if (tempSet.hasEpsilon()) {
                                retChanged = followSetForSymbol(symbol).addTier(followSetForIndex(i).tiers.get(1));
                                if (retChanged) changed = true;
                            }
                        }
                    }
                }
            }
        } while (changed);
    }
}
