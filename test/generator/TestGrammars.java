package generator;

import grammar.Grammar;

import java.util.ArrayList;
import java.util.List;

public class TestGrammars {
    static Grammar grammar1() {
        List<String> lines = new ArrayList<>();
        lines.add("S -> C C");
        lines.add("C -> e C");
        lines.add("C -> d");
        return new Grammar(lines);
    }

    static Grammar grammar2() {
        List<String> lines = new ArrayList<>();
        lines.add("S -> a S A");
        lines.add("S ->");
        lines.add("A -> a b S");
        lines.add("A -> c");
        return new Grammar(lines);
    }

    static Grammar grammar3() {
        List<String> lines = new ArrayList<>();
        lines.add("S -> C C C");
        lines.add("C -> a");
        lines.add("C -> b");
        return new Grammar(lines);
    }
}
