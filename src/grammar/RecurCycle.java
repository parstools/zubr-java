package grammar;

import graph.VertexEdge;
import util.Hash;

import java.util.ArrayList;
import java.util.List;

public class RecurCycle extends ArrayList<Integer> {
    Grammar grammar;
    Nonterminal minOwner;
    boolean oneNt;

    RecurCycle(Grammar grammar, List<VertexEdge> johnsonCycle) {
        this.grammar = grammar;
        assert (johnsonCycle.size() >= 2 && johnsonCycle.get(0).getEdge() == -1);
        int g = johnsonCycle.get(1).getEdge();
        minOwner = grammar.nonterminals.get(johnsonCycle.get(1).getN());
        this.add(g);
        oneNt = true;
        for (int i = 2; i < johnsonCycle.size(); i++) {
            g = johnsonCycle.get(i).getEdge();
            Nonterminal minOwner1 = grammar.nonterminals.get(johnsonCycle.get(i).getN());
            if (minOwner1 != minOwner)
                oneNt = false;
            if (minOwner1.index < minOwner.index)
                minOwner = minOwner1;
            this.add(g);
        }
    }
}
