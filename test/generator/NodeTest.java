package generator;

import grammar.Grammar;
import grammar.Symbol;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class NodeTest {
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
        node.next();
        assertFalse(node.next());
    }

    @Test
    void grammar2ATest() {
        Generator generator = new Generator(TestGrammars.grammar2(), 5, RuleOrder.roSort);
        Symbol symbol = generator.getNT(1);
        Node node = new Node(generator, symbol, 5);
        node.next();
        assertEquals("c",node.string());
        assertEquals("A(c)",node.parenString());
        assertTrue(node.next());
        assertEquals("ab",node.string());
        assertEquals("A(abS())",node.parenString());
        assertTrue(node.next());
        assertEquals("abac",node.string());
        assertEquals("A(abS(aS()A(c)))",node.parenString());
        assertTrue(node.next());
        assertEquals("abaab",node.string());
        assertEquals("A(abS(aS()A(abS())))",node.parenString());
        assertFalse(node.next());
    }

    @Test
    void grammar2Test() {
        Generator generator = new Generator(TestGrammars.grammar2(), 5, RuleOrder.roSort);
        Symbol symbol = generator.getNT(0);
        Node node = new Node(generator, symbol, 5);
        node.next();
        assertEquals("",node.string());
        assertEquals("S()",node.parenString());
        assertTrue(node.next());
        assertEquals("ac",node.string());
        assertEquals("S(aS()A(c))",node.parenString());
        assertTrue(node.next());
        assertEquals("aab",node.string());
        assertEquals("S(aS()A(abS()))",node.parenString());
        assertTrue(node.next());
        assertEquals("aabac",node.string());
        assertEquals("S(aS()A(abS(aS()A(c))))",node.parenString());
    }
}