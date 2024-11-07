package lr;

import grammar.Grammar;
import grammar.Nonterminal;
import grammar.Symbol;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class StatesLR0 {
    Grammar grammar;
    public StatesLR0(Grammar grammar) {
        this.grammar = grammar;
    }

    List<StateLR0> states = new ArrayList<>();

    int size() {
        return states.size();
    }

    void createStates() {
        Set<Long> hashSet = new HashSet<>();
        Nonterminal startNt = grammar.addStartNt();
        ItemLR0 item = new ItemLR0(startNt.rules.get(0), 0);
        StateLR0 stateLR0 = new StateLR0(grammar);
        stateLR0.add(item);
        stateLR0.closure();
        states.add(stateLR0);
        List<Symbol> symList = new ArrayList<>();
        symList.addAll(grammar.terminals);
        symList.addAll(grammar.nonterminals);
        int index = 0;
        while(index < states.size()) {
            stateLR0 = states.get(index);
            for (Symbol symbol: symList) {
                StateLR0 newState = stateLR0.goto_(symbol);
                if (newState != null && !hashSet.contains(newState.longHash)) {
                    hashSet.add(newState.longHash);
                    states.add(newState);
                }
            }
            index++;
        }
    }
}
