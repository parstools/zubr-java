package lr;

import grammar.Grammar;
import grammar.Nonterminal;

import java.util.HashSet;
import java.util.Set;

public class StatesLR1 extends States{
    StatesLR1(Grammar g) {
        super(g);
    }

    public void createStates(Transitions transitions) {
        Set<Long> hashSet = new HashSet<>();
        Nonterminal startNt = grammar.addStartNt();
        ItemLR1 item = new ItemLR1(startNt.rules.get(0), 0, Grammar.EOF);
        State state = new StateLR1();
        super.createStates(transitions, state);
    }
}
