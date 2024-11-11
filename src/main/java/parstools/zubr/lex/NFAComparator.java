package parstools.zubr.lex;

import java.util.*;

public class NFAComparator {
    public static boolean areEqual(NFA nfa1, NFA nfa2) {
        List<NFAState> states1 = nfa1.getStates();
        List<NFAState> states2 = nfa2.getStates();

        if (states1.size() != states2.size()) {
            return false;
        }

        Map<NFAState, Integer> indexMap1 = createStateIndexMap(states1);
        Map<NFAState, Integer> indexMap2 = createStateIndexMap(states2);

        for (int i = 0; i < states1.size(); i++) {
            NFAState s1 = states1.get(i);
            NFAState s2 = states2.get(i);

            if (s1.isAccept() != s2.isAccept()) {
                return false;
            }

            if (!compareTransitions(s1.getTransitions(), s2.getTransitions(), indexMap1, indexMap2)) {
                return false;
            }

            if (!compareEpsilonTransitions(s1.getEpsilonTransitions(), s2.getEpsilonTransitions(), indexMap1, indexMap2)) {
                return false;
            }
        }
        return true;
    }

    private static Map<NFAState, Integer> createStateIndexMap(List<NFAState> states) {
        Map<NFAState, Integer> map = new HashMap<>();
        for (int i = 0; i < states.size(); i++) {
            map.put(states.get(i), i);
        }
        return map;
    }

    private static boolean compareTransitions(
            Map<Character, Set<NFAState>> t1,
            Map<Character, Set<NFAState>> t2,
            Map<NFAState, Integer> indexMap1,
            Map<NFAState, Integer> indexMap2) {

        if (t1.size() != t2.size()) {
            return false;
        }

        for (Map.Entry<Character, Set<NFAState>> entry : t1.entrySet()) {
            char symbol = entry.getKey();
            Set<NFAState> targets1 = entry.getValue();
            Set<NFAState> targets2 = t2.get(symbol);

            if (targets2 == null) {
                return false;
            }

            if (targets1.size() != targets2.size()) {
                return false;
            }

            Set<Integer> indices1 = getIndices(targets1, indexMap1);
            Set<Integer> indices2 = getIndices(targets2, indexMap2);

            if (!indices1.equals(indices2)) {
                return false;
            }
        }

        return true;
    }

    private static boolean compareEpsilonTransitions(
            Set<NFAState> e1,
            Set<NFAState> e2,
            Map<NFAState, Integer> indexMap1,
            Map<NFAState, Integer> indexMap2) {

        if (e1.size() != e2.size()) {
            return false;
        }

        Set<Integer> indices1 = getIndices(e1, indexMap1);
        Set<Integer> indices2 = getIndices(e2, indexMap2);

        return indices1.equals(indices2);
    }

    private static Set<Integer> getIndices(Set<NFAState> states, Map<NFAState, Integer> indexMap) {
        Set<Integer> indices = new HashSet<>();
        for (NFAState state : states) {
            Integer index = indexMap.get(state);
            if (index == null) {
                throw new IllegalArgumentException("State not found in index map.");
            }
            indices.add(index);
        }
        return indices;
    }
}