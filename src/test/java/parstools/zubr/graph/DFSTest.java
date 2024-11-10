package parstools.zubr.graph;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DFSTest {
    @Test
    void detectCycle() {
        DG graph = new DG(6);
        graph.addEdge(0, 1, "01");
        graph.addEdge(0, 2, "02");
        graph.addEdge(1, 2, "12");
        graph.addEdge(2, 0, "20");
        graph.addEdge(2, 3, "23");
        graph.addEdge(3, 3, "33");
        graph.addEdge(4, 5, "45");
        assertEquals(true,graph.detectCycle(0));
        assertEquals(true,graph.detectCycle(1));
        assertEquals(true,graph.detectCycle(2));
        assertEquals(true,graph.detectCycle(3));
        assertEquals(false,graph.detectCycle(4));
        assertEquals(false,graph.detectCycle(5));
    }
}
