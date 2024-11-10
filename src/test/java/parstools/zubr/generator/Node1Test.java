package parstools.zubr.generator;

import org.junit.jupiter.api.Test;
import parstools.zubr.grammar.Symbol;
import parstools.zubr.grammar.TestGrammars;

import java.util.Stack;
import static org.junit.jupiter.api.Assertions.*;

public class Node1Test {
    @Test
    void reverse() {
        Generator generator = new Generator(TestGrammars.grammar1(), 5, RuleOrder.roSort);
        Symbol symbol = generator.getNT(0);
        Node node = new Node(generator, symbol, 5);
        Stack<String> stack = new Stack<>();
        while (node.next())
            stack.push(node.string());

        generator.ruleOrder = RuleOrder.roRevereSort;
        node = new Node(generator, symbol, 5);
        while (node.next()) {
            assertEquals(stack.peek(), node.string());
            stack.pop();
        }
        assertTrue(stack.isEmpty());
    }

    @Test
    void grammar1CTest() {
        Generator generator = new Generator(TestGrammars.grammar1(), 5, RuleOrder.roSort);
        Symbol symbol = generator.getNT(1);
        Node node = new Node(generator, symbol, 5);
        node.next();
        assertEquals("d",node.string());
        assertTrue(node.next());
        assertEquals("ed",node.string());
        assertTrue(node.next());
        assertEquals("C(eC(eC(d)))",node.parenString());
        assertEquals("eed",node.string());
        assertTrue(node.next());
        assertEquals("eeed",node.string());
        assertTrue(node.next());
        assertEquals("eeeed",node.string());
        assertFalse(node.next());
    }

    @Test
    void grammar1Test() {
        Generator generator = new Generator(TestGrammars.grammar1(), 5, RuleOrder.roSort);
        Symbol symbol = generator.getNT(0);
        Node node = new Node(generator, symbol, 5);
        node.next();
        assertEquals("dd",node.string());
        assertTrue(node.next());
        assertEquals("ded",node.string());
        assertTrue(node.next());
        assertEquals("deed",node.string());
        assertTrue(node.next());
        assertEquals("deeed",node.string());
        assertTrue(node.next());
        assertEquals("edd",node.string());
        assertTrue(node.next());
        assertEquals("eded",node.string());
        assertTrue(node.next());
        assertEquals("edeed",node.string());
        assertTrue(node.next());
        assertEquals("eedd",node.string());
        assertTrue(node.next());
        assertEquals("eeded",node.string());
        assertTrue(node.next());
        assertEquals("eeedd",node.string());
        assertFalse(node.next());
    }
}