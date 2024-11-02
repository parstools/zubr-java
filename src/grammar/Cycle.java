package grammar;

import graph.VertexEdge;
import util.Hash;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Cycle extends ArrayList<Rule> {
    Grammar grammar;
    int xorHash;

    Cycle(Grammar grammar, List<VertexEdge> johnsonCycle) {
        this.grammar = grammar;
        assert (johnsonCycle.size() >= 2 && johnsonCycle.get(0).getEdge() == null);
        xorHash = Hash.intHash(-1);
        for (int i = 1; i < johnsonCycle.size(); i++) {
            Rule g = (Rule)johnsonCycle.get(i).getEdge();
            this.add(g);
            xorHash = Hash.intXor(xorHash, g.owner.getIndex());
            xorHash = Hash.intXor(xorHash, g.index);
        }
    }
}
