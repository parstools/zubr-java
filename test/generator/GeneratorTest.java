package generator;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GeneratorTest {
    @Test
    void grammar3Test() {
        Generator generator = new Generator(TestGrammars.grammar3(), 5);
        assertTrue(generator.next());
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

    @Test
    void creationFromString() {
        Generator generator = new Generator(TestGrammars.grammar2(), 5);
        String parenStr = "A(S()a)";
        generator.createFromString(parenStr);
        assertEquals(parenStr, generator.parenString());
        parenStr = "S(aA(b)aA(S()a)b)";
        generator.createFromString(parenStr);
        assertEquals(parenStr, generator.parenString());
        parenStr = "S(aA(a)b)";
        generator.createFromString(parenStr);
        assertEquals(parenStr, generator.parenString());
    }

    @Test
    void grammar4Test() {
        Generator generator = new Generator(TestGrammars.grammar4(), 5);
        assertTrue(generator.next());
        assertEquals("S()", generator.parenString());
        assertTrue(generator.next());
        assertEquals("S(aS()A(c))", generator.parenString());
        assertTrue(generator.next());
        assertEquals("S(aS()A(abS()))", generator.parenString());
        assertTrue(generator.next());
        assertEquals("S(aS()A(abS(aS()A(c))))", generator.parenString());
        assertEquals("aabac", generator.string());
        assertTrue(generator.next());
        assertEquals("S(aS(aS()A(c))A(c))", generator.parenString());
        while (generator.next())
            assertTrue(generator.string().length() <= 5);
    }
}