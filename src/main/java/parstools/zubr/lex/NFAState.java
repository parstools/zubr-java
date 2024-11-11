package parstools.zubr.lex;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class NFAState {
    private final Map<Character, Set<NFAState>> transitions = new HashMap<>();
    private final Set<NFAState> epsilonTransitions = new HashSet<>();
    public boolean accept;

    public void addTransition(char symbol, NFAState toState) {
        Set<NFAState> toStates = transitions.computeIfAbsent(symbol, key -> new HashSet<>());
        toStates.add(toState);
    }

    public void addEpsilonTransition(NFAState toState) {
        epsilonTransitions.add(toState);
    }

    public Map<Character, Set<NFAState>> getTransitions() {
        return Collections.unmodifiableMap(transitions);
    }

    public Set<NFAState> getEpsilonTransitions() {
        return Collections.unmodifiableSet(epsilonTransitions);
    }

    public boolean isAccept() {
        return accept;
    }
}