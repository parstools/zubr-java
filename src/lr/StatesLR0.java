package lr;

import grammar.Grammar;
import grammar.Nonterminal;
import grammar.Symbol;

import java.util.*;

public class StatesLR0 extends States {
    StatesLR0(Grammar grammar) {
        super(grammar);
    }
    void createStates(Transitions transitions) {
        Nonterminal startNt = grammar.addStartNt();
        ItemLR0 item = new ItemLR0(startNt.rules.get(0), 0);
        State state = new StateLR0(grammar);
        state.add(item);
        super.createStates(transitions, state);
    }
}
