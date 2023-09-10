package generator;

import grammar.Grammar;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GeneratorTest {
    @Test
    void grammar3Test() {
        Generator generator = new Generator(TestGrammars.grammar3(), 5);
        assertEquals("aaa", generator.string());
        assertTrue(generator.next());
        assertEquals("aab", generator.string());
        assertTrue(generator.next());
        assertEquals("aba", generator.string());
        assertTrue(generator.next());
        assertEquals("abb", generator.string());
        assertTrue(generator.next());
        assertEquals("baa", generator.string());
        assertTrue(generator.next());
        assertEquals("bab", generator.string());
        assertTrue(generator.next());
        assertEquals("bba", generator.string());
        assertTrue(generator.next());
        assertEquals("bbb", generator.string());
        assertFalse(generator.next());
    }
}
