package parstools.zubr.lr;

import parstools.zubr.grammar.Grammar;

public class SLR extends Transitions {
    SLR(Grammar g) {
        StatesLR0 states = new StatesLR0(g);
        states.createStates(this);
    }
}
