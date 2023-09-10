package set;

import grammar.Grammar;

import java.util.ArrayList;

public class Sequence extends ArrayList<Integer> {
    Grammar grammar;
    Sequence(Grammar grammar) {
        this.grammar = grammar;
    }
}
