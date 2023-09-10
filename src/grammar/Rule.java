package grammar;

import java.util.ArrayList;
import java.util.Scanner;

public class Rule extends ArrayList<Symbol> {
    Grammar grammar;
    Nonterminal owner;
    int index;

    public Rule(Grammar grammar, Nonterminal owner) {
        this.grammar = grammar;
        this.owner = owner;
        index = owner.ruleCount();
    }

    public void parse(String input) {
        Scanner scanner = new Scanner(input);
        while (scanner.hasNext()) {
            String symbolName = scanner.next();
            add(grammar.findSymbol(symbolName));
        }
    }
}