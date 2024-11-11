package parstools.zubr.lex;

import java.util.ArrayList;
import java.util.List;

public class NFA {
    private final List<NFAState> states = new ArrayList<>();

    public NFAState createState() {
        NFAState state = new NFAState();
        states.add(state);
        return state;
    }

    public List<NFAState> getStates() {
        return states;
    }
}