package generator;

import grammar.Symbol;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Node1Test {
    @Test
    void grammar1Test() {
        Generator generator = new Generator(TestGrammars.grammar1(), 3);
        Symbol symbol = generator.getNT(0);
        Node1 node = new Node1(generator, symbol);
        node.next(3);
        assertEquals("dd", node.string());
    }
}