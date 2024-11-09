package lr;

import grammar.Grammar;

import java.util.ArrayList;
import java.util.List;

public class LR0 extends Transitions {
    LR0(Grammar g) {
        StatesLR0 states = new StatesLR0(g);
        states.createStates(this);
    }
}
