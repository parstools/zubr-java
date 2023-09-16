package grammar;

import graph.VertexEdge;
import util.Hash;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Cycle extends ArrayList<Integer> {
    Grammar grammar;
    int xorHash;

    Cycle(Grammar grammar, List<VertexEdge> johnsonCycle) {
        this.grammar = grammar;
        assert (johnsonCycle.size() >= 2 && johnsonCycle.get(0).getEdge() == -1);
        xorHash = Hash.intHash(-1);
        for (int i = 1; i < johnsonCycle.size(); i++) {
            int g = johnsonCycle.get(i).getEdge();
            this.add(g);
            xorHash = Hash.intXor(xorHash, g);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < size(); i++) {
            int glob = this.get(i);
            if (i > 0)
                sb.append(", ");
            sb.append(grammar.getGlobalRule(glob).toString());
        }
        sb.append("]");
        return sb.toString();
    }
}
