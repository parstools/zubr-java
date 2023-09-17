package set;

import generator.Generator;
import generator.RuleOrder;
import grammar.Grammar;
import grammar.Symbol;
import util.Hash;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import grammar.Rule;

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

    void addRuleFirst1(TokenSet outSet, Rule rule, int start) {
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
                TokenSet firstY = setForSymbol(symbol);
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
    }

    private TokenSet setForSymbol(Symbol symbol) {
        return null;
    }
}
