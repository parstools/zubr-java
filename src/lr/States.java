package lr;

import grammar.Grammar;

import java.util.ArrayList;

public class States extends ArrayList<StateLR0> {
    Grammar grammar;
    public States(Grammar grammar) {
        this.grammar = grammar;
    }

    protected void createStates(Transitions transitions, State state) {
    }
}
