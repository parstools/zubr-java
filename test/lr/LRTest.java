package lr;

import grammar.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import set.SetContainer;

public class LRTest {
    @Test
    void createStatesLR0() {
        Grammar g = TestGrammars.LRwiki();
        SetContainer sc = new SetContainer(g);
        sc.reset(1);
        sc.makeFirstSets1();
        sc.makeFollowSets1();
        StatesLR0 states = new StatesLR0(g);
        states.createStates();
        assertEquals(9, states.size());
    }
}
