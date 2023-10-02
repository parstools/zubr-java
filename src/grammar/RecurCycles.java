package grammar;

import graph.VertexEdge;

import java.util.*;

public class RecurCycles extends ArrayList<RecurCycle> {
    Grammar grammar;
    public Set<Integer> xors = new HashSet<>();

    RecurCycles(Grammar grammar, List<List<VertexEdge>> johnsonResult) {
        this.grammar = grammar;
        for (List<VertexEdge> johnsonCycle : johnsonResult) {
            RecurCycle cycle = new RecurCycle(grammar, johnsonCycle);
            assert (cycle.size() == 1 || !cycle.oneNt);
            this.add(cycle);
        }
        Collections.sort(this, new Comparator<RecurCycle>() {
            @Override
            public int compare(RecurCycle c1, RecurCycle c2) {
                if (c1.minOwner.index < c2.minOwner.index)
                    return -1;
                else if (c1.minOwner.index > c2.minOwner.index)
                    return 1;
                if (c1.size() < c2.size())
                    return -1;
                else if (c1.size() > c2.size())
                    return 1;

                int commonLen = Math.min(c1.size(), c2.size());
                for (int i = 0; i < commonLen; i++) {
                    int g1 = c1.get(i);
                    int g2 = c2.get(i);
                    if (g1 < g2)
                        return -1;
                    else if (g1 > g2)
                        return 1;
                }
                return 0;
            }
        });
    }
}
