package graph;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static java.lang.System.out;

public class JohnsonTest {
    @Test
    void vertexCycle() {
        DG graph = new DG(3);
        graph.addEdge(0,1,3);
        graph.addEdge(1,2,5);
        graph.addEdge(2,0,7);
        graph.addEdge(1,2,11);
        graph.addEdge(2,0,12);
        List<List<Integer>> johnsonResult = JohnsonsAlgorithm.calculateCycles(graph);
        List<List<Set<Integer>>> edgeResult = graph.vertexAsEdgeCycles(johnsonResult);
        out.println(edgeResult);
    }
}
