package grammar;

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

    public static Grammar grammar7() {
        List<String> lines = new ArrayList<>();
        lines.add("S -> M");
        lines.add("S -> U");
        lines.add("M -> i e t M e M");
        lines.add("M -> o");
        lines.add("U -> i e t S");
        lines.add("U -> i e t M e U");
        return new Grammar(lines);
    }

    public static Grammar grammar8() {
        List<String> lines = new ArrayList<>();
        lines.add("X -> Y");
        lines.add("X -> b Y a");
        lines.add("Y -> c");
        lines.add("Y -> c a");
        return new Grammar(lines);
    }

    public static Grammar grammar9() {
        List<String> lines = new ArrayList<>();
        lines.add("S -> i e t S");
        lines.add("S -> i e t W e S");
        lines.add("S -> o");
        lines.add("W -> i e t W e W");
        lines.add("W -> o");
        return new Grammar(lines);
    }

    public static Grammar cycle1() {
        List<String> lines = new ArrayList<>();
        lines.add("A -> A");
        lines.add("A -> a");
        return new Grammar(lines);
    }

    public static Grammar cycle2() {
        List<String> lines = new ArrayList<>();
        lines.add("A -> B");
        lines.add("A -> a");
        lines.add("A -> c B");
        lines.add("B -> A");
        lines.add("B -> b");
        return new Grammar(lines);
    }

    public static Grammar cycle112() {
        List<String> lines = new ArrayList<>();
        lines.add("A -> A");
        lines.add("B -> B");
        lines.add("A -> B");
        lines.add("A -> a");
        lines.add("A -> c B");
        lines.add("B -> A");
        lines.add("B -> b");
        return new Grammar(lines);
    }

    public static Grammar cycle3() {
        List<String> lines = new ArrayList<>();
        lines.add("A -> B");
        lines.add("A -> a");
        lines.add("B -> C");
        lines.add("B -> b");
        lines.add("C -> A");
        lines.add("C -> c");
        return new Grammar(lines);
    }

    public static Grammar cycle32() {
        List<String> lines = new ArrayList<>();
        lines.add("A -> B");
        lines.add("A -> D");
        lines.add("A -> a");
        lines.add("B -> C");
        lines.add("B -> b");
        lines.add("C -> A");
        lines.add("C -> c");
        lines.add("D -> E");
        lines.add("D -> d");
        lines.add("E -> D");
        lines.add("E -> e");
        return new Grammar(lines);
    }

    public static Grammar cycle2wide() {
        List<String> lines = new ArrayList<>();
        lines.add("A -> B C");
        lines.add("A -> a");
        lines.add("B -> A C");
        lines.add("B -> b");
        lines.add("C ->");
        lines.add("C -> c");
        return new Grammar(lines);
    }

    public static Grammar cycle3with2() {
        List<String> lines = new ArrayList<>();
        lines.add("A -> B D");
        lines.add("A -> a");
        lines.add("B -> C");
        lines.add("B -> b");
        lines.add("C -> A");
        lines.add("C -> c");
        lines.add("D ->E");
        lines.add("D ->");
        lines.add("E ->D");
        return new Grammar(lines);
    }

    public static Grammar cycle332() {
        List<String> lines = new ArrayList<>();
        lines.add("A -> B E");
        lines.add("A -> a");
        lines.add("B -> C");
        lines.add("B -> b");
        lines.add("C -> A");
        lines.add("C -> c");
        lines.add("C ->");
        lines.add("D -> E");
        lines.add("E ->D C");
        lines.add("E ->");
        return new Grammar(lines);
    }

    public static Grammar cycle31() {
        List<String> lines = new ArrayList<>();
        lines.add("A -> B D");
        lines.add("A -> a");
        lines.add("B -> C");
        lines.add("B -> b");
        lines.add("C -> A");
        lines.add("C -> F");
        lines.add("C -> c");
        lines.add("D ->E");
        lines.add("D ->");
        lines.add("E ->D C");
        lines.add("F ->F");
        lines.add("F ->f");
        return new Grammar(lines);
    }

    public static Grammar cycle31a() {
        List<String> lines = new ArrayList<>();
        lines.add("A -> B D");
        lines.add("A -> a");
        lines.add("B -> C F");
        lines.add("B -> b");
        lines.add("C -> A");
        lines.add("C -> c");
        lines.add("D ->E");
        lines.add("D ->");
        lines.add("E ->D C");
        lines.add("F -> F");
        lines.add("F ->");
        return new Grammar(lines);
    }

    public static Grammar cycle322() {
        List<String> lines = new ArrayList<>();
        lines.add("A -> B D");
        lines.add("A -> a");
        lines.add("B -> C F");
        lines.add("B -> b");
        lines.add("C -> A");
        lines.add("C -> c");
        lines.add("D ->E");
        lines.add("D ->");
        lines.add("E ->D C");
        lines.add("F -> B");
        lines.add("F ->");
        return new Grammar(lines);
    }


}
