package set;

import generator.Generator;
import grammar.Grammar;

import static java.lang.System.out;

public class SetContainer {
    Grammar grammar;
    public SetContainer(Grammar grammar) {
        this.grammar = grammar;
    }

    public void firstSetsByGeneration(int k) {
        Generator generator = new Generator(grammar, 5);
        generator.first();
        out.println(generator.string());
        while (generator.next())
            out.println(generator.string());
    }
}
