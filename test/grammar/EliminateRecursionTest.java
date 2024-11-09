package grammar;

import generator.Generator;
import generator.RuleOrder;
import org.junit.jupiter.api.Test;

public class EliminateRecursionTest {
    @Test
    void eliminate() {
        Grammar g = TestGrammars.eliminateRecursionHalt();
        g.eliminationRecursion();
    }
}
