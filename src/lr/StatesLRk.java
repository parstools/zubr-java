package lr;

import grammar.Grammar;
import grammar.Nonterminal;
import set.Sequence;
import set.SetContainer;

public class StatesLRk extends States{
    int k;
    StatesLRk(Grammar grammar, int k) {
        super(grammar);
        this.k = k;
    }

    public void createStates(Transitions transitions) {
        Nonterminal startNt = grammar.addStartNt();
        Sequence sequence = new Sequence(grammar);
        sequence.add(-1);
        ItemLRk item = new ItemLRk(startNt.rules.get(0), 0, sequence);
        State state = new StateLRk(grammar);
        SetContainer sc = new SetContainer(grammar);
        sc.reset(k);
        sc.makeFirstSetsK(k);
        sc.makeFollowSetsK(k);
        super.createStates(transitions, state);
    }
}
