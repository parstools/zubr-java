package parstools.zubr.graph;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JohnsonTest {
    @Test
    void vertexCycle() {
        DG graph = new DG(3);
        graph.addEdge(0,1,3);
        graph.addEdge(1,2,5);
        graph.addEdge(2,0,7);
        graph.addEdge(1,2,11);
        List<List<VertexEdge>> johnsonResult = JohnsonsAlgorithm.calculateCycles(graph);
        assertEquals("[[[0,null], [1,3], [2,5], [0,7]], [[0,null], [1,3], [2,11], [0,7]]]",johnsonResult.toString());
    }

    @Test
    void cycle2() {
        DG graph = new DG(3);
        graph.addEdge(0,1,3);
        graph.addEdge(1,0,3);
        List<List<VertexEdge>> johnsonResult = JohnsonsAlgorithm.calculateCycles(graph);
        assertEquals("[[[0,null], [1,3], [0,3]]]",johnsonResult.toString());
    }

    @Test
    void cycle1a() {
        DG graph = new DG(3);
        graph.addEdge(0,0,3);
        List<List<VertexEdge>> johnsonResult = JohnsonsAlgorithm.calculateCycles(graph);
        assertEquals("[[[0,null], [0,3]]]",johnsonResult.toString());
    }

    @Test
    void cycle1b() {
        DG graph = new DG(3);
        graph.addEdge(0,0,3);
        graph.addEdge(0,0,5);
        List<List<VertexEdge>> johnsonResult = JohnsonsAlgorithm.calculateCycles(graph);
        assertEquals("[[[0,null], [0,3]], [[0,null], [0,5]]]",johnsonResult.toString());
    }
}
