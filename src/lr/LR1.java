package lr;

import grammar.Grammar;

public class LR1 extends Transitions {
    LR1(Grammar g) {
        StatesLR1 states = new StatesLR1(g);
        states.createStates(this);
    }
}
