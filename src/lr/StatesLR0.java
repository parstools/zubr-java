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
        Set<Long> hashSet = new HashSet<>();
        Nonterminal startNt = grammar.addStartNt();
        ItemLR0 item = new ItemLR0(startNt.rules.get(0), 0);
        StateLR0 stateLR0 = new StateLR0(grammar);
        stateLR0.add(item);
        stateLR0.closure();
        add(stateLR0);
        List<Symbol> symList = new ArrayList<>();
        symList.addAll(grammar.terminals);
        symList.addAll(grammar.nonterminals);
        int index = 0;
        while(index < size()) {
            stateLR0 = get(index);
            for (Symbol symbol: symList) {
                StateLR0 newState = stateLR0.goto_(symbol);
                if (newState != null && !hashSet.contains(newState.longHash)) {
                    hashSet.add(newState.longHash);
                    transitions.add(index, symbol, size());
                    add(newState);
                }
            }
            index++;
        }
    }
}
