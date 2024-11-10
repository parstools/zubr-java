package parstools.zubr.lr;

import parstools.zubr.grammar.Grammar;

public class LALRk extends Transitions {
    int k;
    LALRk(Grammar g) {
        this.k = k;
        StatesLRk states = new StatesLRk(g, k);
        states.createStates(this, null);
    }
}
