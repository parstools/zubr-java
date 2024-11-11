package parstools.zubr.lex;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class NFATest {
    NFA sample1() {
        NFA nfa = new NFA();
        NFAState s0_1 = nfa.createState();
        NFAState s1_1 = nfa.createState();
        NFAState s3_1 = nfa.createState();
        NFAState s5_1 = nfa.createState();
        s5_1.accept = true;
        s0_1.addTransition('a', s3_1);
        s0_1.addEpsilonTransition(s5_1);
        return nfa;
    }
    NFA sample2() {
        NFA nfa = new NFA();
        NFAState s0_3 = nfa.createState();
        NFAState s1_3 = nfa.createState();
        NFAState s3_3 = nfa.createState();
        NFAState s5_3 = nfa.createState();
        s5_3.accept = true;
        s0_3.addTransition('b', s3_3); // different symbol
        s0_3.addEpsilonTransition(s5_3);
        return nfa;
    }
    @Test
    public void testCompare() {
        NFA nfa1 = sample1();
        NFA nfa2 = sample1();
        NFA nfa3 = sample2();
        boolean areEqual1 = NFAComparator.areEqual(nfa1, nfa2);
        boolean areEqual2 = NFAComparator.areEqual(nfa1, nfa3);
        assertTrue(areEqual1);
        assertFalse(areEqual2);
    }
}