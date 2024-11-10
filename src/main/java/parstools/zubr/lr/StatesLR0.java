package parstools.zubr.lr;

import parstools.zubr.grammar.Grammar;
import parstools.zubr.grammar.Nonterminal;

import java.util.*;

public class StatesLR0 extends States {
    public StatesLR0(Grammar grammar) {
        super(grammar);
    }

    public void createStates(Transitions transitions) {
        Nonterminal startNt = grammar.addStartNt();
        ItemLR0 item = new ItemLR0(startNt.rules.get(0), 0);
        State state = new StateLR0(grammar);
        state.add(item);
        super.createStates(transitions, state);
    }
}
