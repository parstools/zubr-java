package set;

import grammar.Grammar;
import grammar.TestGrammars;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TierTest {
    @Test
    void addSeq() {
        Grammar grammar = TestGrammars.tokenGrammar();
        Tier tier = new Tier(grammar,3);
        boolean res = tier.addSeq("abc");
        assertTrue(res);
        assertEquals("abc", tier.toString());
        res = tier.addSeq("abb");
        assertTrue(res);
        assertEquals("abb abc", tier.toString());
        res = tier.addSeq("abc"); //try add duplicate
        assertFalse(res);
        assertEquals("abb abc", tier.toString());
        res = tier.addSeq("aca");
        assertTrue(res);
        assertEquals("abb abc aca", tier.toString());
        res = tier.addSeq("bac");
        assertTrue(res);
        assertEquals("abb abc aca bac", tier.toString());
    }

    @Test
    void concat() {
        Grammar grammar = TestGrammars.stdLL1();
        Tier tier0 = new Tier(grammar,2);
        tier0.addSeq("+i");
        assertEquals("+i", tier0.toString());
        Tier tier1 = new Tier(grammar,3);
        tier1.addSeq("+i*");
        assertEquals("+i*", tier1.toString());
        Tier tier3 = tier0.concat(tier1, 3);
        assertEquals("+i+", tier3.toString());
    }

    @Test
    void multi() {
        Grammar grammar = TestGrammars.stdLL1();
        Tier tier = new Tier(grammar,3);
        tier.addSeq("+ii");
        tier.addSeq("+i*");
        tier.addSeq("+i+");
        assertEquals("+i+ +i* +ii", tier.toString());
    }

    @Test
    void intersection1() {
        Grammar grammar = TestGrammars.tokenGrammar();
        Tier tier0 = new Tier(grammar,1);
        Tier tier1 = new Tier(grammar,1);
        tier0.addSeq("a");
        tier0.addSeq("b");
        tier0.addSeq("c");
        tier1.addSeq("a");
        tier1.addSeq("c");
        tier1.addSeq("d");
        Tier tier = tier0.intersection(tier1);
        boolean b = tier0.isIntersection(tier1);
        assertEquals("a c", tier.toString());
        assertTrue(b);
    }

    @Test
    void intersection1_empty() {
        Grammar grammar = TestGrammars.tokenGrammar();
        Tier tier0 = new Tier(grammar,1);
        Tier tier1 = new Tier(grammar,1);
        tier0.addSeq("a");
        tier0.addSeq("b");
        tier1.addSeq("c");
        tier1.addSeq("d");
        Tier tier = tier0.intersection(tier1);
        boolean b = tier0.isIntersection(tier1);
        assertNull(tier);
        assertFalse(b);
    }

    @Test
    void intersection2() {
        Grammar grammar = TestGrammars.tokenGrammar();
        Tier tier0 = new Tier(grammar,2);
        Tier tier1 = new Tier(grammar,2);
        tier0.addSeq("ac");
        tier0.addSeq("ad");
        tier0.addSeq("ba");
        tier0.addSeq("bc");
        tier1.addSeq("ca");
        tier1.addSeq("cb");
        tier1.addSeq("ba");
        tier1.addSeq("bd");
        Tier tier = tier0.intersection(tier1);
        boolean b = tier0.isIntersection(tier1);
        assertEquals("ba", tier.toString());
        assertTrue(b);
    }

    @Test
    void intersection2_empty() {
        Grammar grammar = TestGrammars.tokenGrammar();
        Tier tier0 = new Tier(grammar,2);
        Tier tier1 = new Tier(grammar,2);
        tier0.addSeq("ac");
        tier0.addSeq("ad");
        tier0.addSeq("ba");
        tier0.addSeq("bc");
        tier1.addSeq("ca");
        tier1.addSeq("cb");
        tier1.addSeq("be");
        tier1.addSeq("bd");
        Tier tier = tier0.intersection(tier1);
        boolean b = tier0.isIntersection(tier1);
        assertNull(tier);
        assertFalse(b);
    }

    @Test
    void intersection3() {
        Grammar grammar = TestGrammars.tokenGrammar();
        Tier tier0 = new Tier(grammar,3);
        Tier tier1 = new Tier(grammar,3);
        tier0.addSeq("daa");
        tier0.addSeq("dab");
        tier0.addSeq("dbd");
        tier0.addSeq("dba");
        tier0.addSeq("aca");
        tier0.addSeq("acb");
        tier0.addSeq("adc");
        tier0.addSeq("adb");
        tier1.addSeq("dcc");
        tier1.addSeq("dcd");
        tier1.addSeq("dbb");
        tier1.addSeq("dba");
        tier1.addSeq("acc");
        tier1.addSeq("acb");
        tier1.addSeq("adc");
        tier1.addSeq("ada");
        Tier tier = tier0.intersection(tier1);
        boolean b = tier0.isIntersection(tier1);
        assertEquals("acb adc dba", tier.toString());
        assertTrue(b);
    }
}
