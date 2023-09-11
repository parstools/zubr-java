package set;

import generator.Generator;
import grammar.Grammar;

import java.util.ArrayList;
import java.util.List;

import static java.lang.System.out;

public class SetContainer {
    Grammar grammar;

    List<TokenSet> firstSets = new ArrayList<>();
    List<TokenSet> followSets = new ArrayList<>();
    public SetContainer(Grammar grammar) {
        this.grammar = grammar;
    }

    public void computeSetsByGeneration(int k) {
        firstSets.clear();
        followSets.clear();
        Generator generator = new Generator(grammar, 7);
        for (int i=0; i<grammar.nonterminals.size(); i++) {
            TokenSet firstSet = generator.collectFirstAllGenerated(i, k);
            firstSets.add(firstSet);
            out.println(firstSet.hashCode());
            TokenSet followSet = generator.collectFollowAllGenerated(i, k);
            followSets.add(followSet);
        }
    }
}
