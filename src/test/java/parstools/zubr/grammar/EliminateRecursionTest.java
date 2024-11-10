package parstools.zubr.grammar;

import org.junit.jupiter.api.Test;

public class EliminateRecursionTest {
    @Test
    void eliminate() {
        Grammar g = TestGrammars.eliminateRecursionHalt();
        g.eliminationRecursion();
    }
}
