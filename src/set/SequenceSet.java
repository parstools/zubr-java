package set;

import grammar.Grammar;

import java.util.HashSet;

public class SequenceSet extends HashSet<Sequence> {
    Grammar grammar;
    public SequenceSet(Grammar grammar) {
        this.grammar = grammar;
    }
}
