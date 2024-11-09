package lr;

import grammar.Grammar;

public class LALRk extends Transitions {
    LALRk(Grammar g) {
        StatesLRk states = new StatesLRk(g);
        states.createStates(this, null);
    }
}
