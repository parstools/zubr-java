package parstools.zubr.grammar;

import parstools.zubr.graph.VertexEdge;

import java.util.*;

public class Cycles extends ArrayList<Cycle> {
    Grammar grammar;

    Cycles(Grammar grammar, List<List<VertexEdge>> johnsonResult) {
        this.grammar = grammar;
        for (List<VertexEdge> johnsonCycle : johnsonResult)
            this.add(new Cycle(grammar, johnsonCycle));
        Collections.sort(this, new Comparator<Cycle>() {
            @Override
            public int compare(Cycle c1, Cycle c2) {
                int commonLen = Math.min(c1.size(), c2.size());
                for (int i = 0; i < commonLen; i++) {
                    Rule g1 = c1.get(i);
                    Rule g2 = c2.get(i);
                    if (g1.owner.getIndex() < g2.owner.getIndex())
                        return -1;
                    else if (g1.owner.getIndex() > g2.owner.getIndex())
                        return 1;
                    else if (g1.index < g2.index)
                        return -1;
                    else if (g1.index > g2.index)
                        return 1;
                }
                if (c1.size() < c2.size())
                    return -1;
                else if (c1.size() > c2.size())
                    return 1;
                else
                    return 0;
            }
        });
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size(); i++) {
            Cycle cycle = this.get(i);
            if (i > 0)
                sb.append(", ");
            sb.append(cycle.toString());
        }
        return sb.toString();
    }
}
