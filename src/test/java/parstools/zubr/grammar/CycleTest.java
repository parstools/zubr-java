package parstools.zubr.grammar;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CycleTest {
    @Test
    void noCycle() {
        Grammar g = TestGrammars.canonicalForm();
        assertEquals("",g.cycles.toString());
    }

    @Test
    void cycle1() {
        Grammar g = TestGrammars.cycle1();
        assertEquals("[A -> A]",g.cycles.toString());
    }

    @Test
    void cycle2() {
        Grammar g = TestGrammars.cycle2();
        assertEquals("[A -> B, B -> A]",g.cycles.toString());
    }

    @Test
    void cycle112() {
        Grammar g = TestGrammars.cycle112();
        assertEquals("[A -> A], [A -> B, B -> A], [B -> B]",g.cycles.toString());
    }

    @Test
    void cycle3() {
        Grammar g = TestGrammars.cycle3();
        assertEquals("[A -> B, B -> C, C -> A]",g.cycles.toString());
    }

    @Test
    void cycle32() {
        Grammar g = TestGrammars.cycle32();
        assertEquals("[A -> B, B -> C, C -> A], [D -> E, E -> D]",g.cycles.toString());
    }

    @Test
    void cycle2wide() {
        Grammar g = TestGrammars.cycle2wide();
        assertEquals("[A -> B C, B -> A C]",g.cycles.toString());
    }

    @Test
    void cycle3with2() {
        Grammar g = TestGrammars.cycle3with2();
        assertEquals("[A -> B D, B -> C, C -> A], [D -> E, E -> D]",g.cycles.toString());
    }

    @Test
    void cycle332() {
        Grammar g = TestGrammars.cycle332();
        assertEquals("[A -> B E, B -> C, C -> A], [A -> B E, E -> D C, C -> A], [D -> E, E -> D C]",g.cycles.toString());
    }

    @Test
    void cycle31() {
        Grammar g = TestGrammars.cycle31();
        assertEquals("[A -> B D, B -> C, C -> A], [F -> F]",g.cycles.toString());
    }

    @Test
    void cycle31a() {
        Grammar g = TestGrammars.cycle31a();
        assertEquals("[A -> B D, B -> C F, C -> A], [F -> F]",g.cycles.toString());
    }

    void cycle322() {
        Grammar g = TestGrammars.cycle322();
        assertEquals("[A -> B D, B -> C F, C -> A], [D -> E, E -> D C],[B -> C F, F->B]",g.cycles.toString());
    }
}
