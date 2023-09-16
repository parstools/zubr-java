package grammar;

import graph.VertexEdge;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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
                    int g1 = c1.get(i);
                    int g2 = c2.get(i);
                    if (g1 < g2)
                        return -1;
                    else if (g1 > g2)
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
