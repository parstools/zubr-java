package grammar;

import grammar.Grammar;

import java.util.ArrayList;
import java.util.List;

public class TestGrammars {
    public static Grammar grammar1() {
        List<String> lines = new ArrayList<>();
        lines.add("S -> C C");
        lines.add("C -> e C");
        lines.add("C -> d");
        return new Grammar(lines);
    }

    public static Grammar grammar2() {
        List<String> lines = new ArrayList<>();
        lines.add("S -> a S A");
        lines.add("S ->");
        lines.add("A -> a b S");
        lines.add("A -> c");
        return new Grammar(lines);
    }

    public static Grammar grammar3() {
        List<String> lines = new ArrayList<>();
        lines.add("S -> C C C");
        lines.add("C -> a");
        lines.add("C -> b");
        return new Grammar(lines);
    }

    public static Grammar grammar4() {
        List<String> lines = new ArrayList<>();
        lines.add("S -> a S A");
        lines.add("S ->");
        lines.add("A -> a b S");
        lines.add("A -> c");
        return new Grammar(lines);
    }

    // bad grammar
    public static Grammar grammar5() {
        List<String> lines = new ArrayList<>();
        lines.add("A -> B");
        lines.add("A -> b B");
        lines.add("B -> A");
        lines.add("B -> a A");
        return new Grammar(lines);
    }

    public static Grammar grammar6() {
        List<String> lines = new ArrayList<>();
        lines.add("S -> S S");
        lines.add("S -> i");
        return new Grammar(lines);
    }

    public static Grammar canonicalForm() {
        List<String> lines = new ArrayList<>();
        lines.add("E -> E + T");
        lines.add("E -> T");
        lines.add("T -> T * F");
        lines.add("T -> F");
        lines.add("F -> ( E )");
        lines.add("F -> i");
        return new Grammar(lines);
    }

    public static Grammar shuffled() {
        List<String> lines = new ArrayList<>();
        lines.add("E -> T");
        lines.add("E -> E + T");
        lines.add("F -> ( E )");
        lines.add("F -> i");
        lines.add("T -> F");
        lines.add("T -> T * F");
        return new Grammar(lines);
    }
}
