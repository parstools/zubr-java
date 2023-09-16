package graph;

import org.junit.jupiter.api.Test;

import java.util.List;

public class JohnsonTest {
    @Test
    void vertexCycle() {
        DG graph = new DG(3);
        graph.addEdge(0,1,3);
        graph.addEdge(1,2,5);
        graph.addEdge(2,0,7);
        List<List<Integer>> johnsonResult = JohnsonsAlgorithm.calculateCycles(graph);
        JohnsonsAlgorithm.outputJohnson(johnsonResult);
    }
}
