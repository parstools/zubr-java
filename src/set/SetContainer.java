package set;

import generator.Generator;
import grammar.Grammar;
import util.Hash;

import java.util.ArrayList;
import java.util.List;

import static java.lang.System.out;

public class SetContainer {
    Grammar grammar;

    List<TokenSet> firstSets = new ArrayList<>();
    List<TokenSet> followSets = new ArrayList<>();

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (TokenSet ts: firstSets)
            sb.append(ts.toString());
        sb.append(" | ");
        for (TokenSet ts: followSets)
            sb.append(ts.toString());
        sb.append("]");
        return sb.toString();
    }

    @Override
    public int hashCode() {
        Hash h = new Hash();
        for (TokenSet ts: firstSets)
            h.add(ts.hashCode());
        for (TokenSet ts: followSets)
            h.add(ts.hashCode());
        return h.hash();
    }

    public SetContainer(Grammar grammar) {
        this.grammar = grammar;
    }

    public void computeSetsByGeneration(int k, int maxLen) {
        firstSets.clear();
        followSets.clear();
        Generator generator = new Generator(grammar, maxLen);
        for (int i=0; i<grammar.nonterminals.size(); i++) {
            TokenSet firstSet = generator.collectFirstAllGenerated(i, k);
            firstSets.add(firstSet);
            TokenSet followSet = generator.collectFollowAllGenerated(i, k);
            followSets.add(followSet);
        }
    }

    public void readTest1(List<String> lines) {
        firstSets.clear();
        followSets.clear();
        for (int i=1; i<lines.size(); i++) {
            String line = lines.get(i);
            String[] parts = line.split("\t");
            assert(parts.length==4);
            int nt = grammar.findSymbol(parts[0]).index;
            out.println(nt);
            boolean nullable = parts[1].equals("✔");
            TokenSet first = new TokenSet(grammar,1);
            if (nullable) {
                Sequence seq = new Sequence(grammar, "");
                first.addSeq(seq);
            }

            String[] parts2 = parts[2].split(", ");
            for (String part: parts2) {
                Sequence seq = new Sequence(grammar, part);
                first.addSeq(seq);
            }
            TokenSet follow = new TokenSet(grammar,1);
            String[] parts3 = parts[3].split(", ");
            for (String part: parts3) {
                Sequence seq = new Sequence(grammar, part);
                follow.addSeq(seq);
            }
            firstSets.add(first);
            followSets.add(follow);
        }
    }
}
