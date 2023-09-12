package generator;

import grammar.Symbol;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class Node1Test {
    @Test
    void grammar1Test() {
        Generator generator = new Generator(TestGrammars.grammar1(), 5);
        Symbol symbol = generator.getNT(0);
        Node1 node = new Node1(generator, symbol);
        node.next(5);
        assertEquals("dd",node.string());
        assertTrue(node.next(5));
        assertEquals("ded",node.string());
        assertTrue(node.next(5));
        assertEquals("deed",node.string());
        assertTrue(node.next(5));
        assertEquals("deeed",node.string());
        assertTrue(node.next(5));
        assertEquals("edd",node.string());
        assertTrue(node.next(5));
        assertEquals("eded",node.string());
        assertTrue(node.next(5));
        assertEquals("edeed",node.string());
        assertTrue(node.next(5));
        assertEquals("eedd",node.string());
        assertTrue(node.next(5));
        assertEquals("eeded",node.string());
        assertTrue(node.next(5));
        assertEquals("eeedd",node.string());
        node.next(5);
        assertFalse(node.next(5));
    }
}