package grammar;

import graph.VertexEdge;
import util.Hash;

import java.util.ArrayList;
import java.util.List;

public class RecurCycle extends ArrayList<Rule> {
    Grammar grammar;
    Nonterminal minOwner;
    boolean oneNt;

    RecurCycle(Grammar grammar, List<VertexEdge> johnsonCycle) {
        this.grammar = grammar;
        assert (johnsonCycle.size() >= 2 && johnsonCycle.get(0).getEdge() == null);
        Rule g = (Rule)johnsonCycle.get(1).getEdge();
        minOwner = grammar.nonterminals.get(johnsonCycle.get(1).getN());
        this.add(g);
        oneNt = true;
        for (int i = 2; i < johnsonCycle.size(); i++) {
            g = (Rule)johnsonCycle.get(i).getEdge();
            Nonterminal minOwner1 = grammar.nonterminals.get(johnsonCycle.get(i).getN());
            if (minOwner1 != minOwner)
                oneNt = false;
            if (minOwner1.getIndex() < minOwner.getIndex())
                minOwner = minOwner1;
            this.add(g);
        }
    }
}
