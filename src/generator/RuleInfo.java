package generator;

import grammar.Rule;
import grammar.Symbol;

public class RuleInfo {
    Rule rule;
    int originIndex;
    boolean hasNt = false;
    int minLen = 0;
    Generator generator;

    boolean computeMinLen() {
        int old = minLen;
        minLen = 0;
        for (Symbol symbol : rule)
            if (symbol.terminal)
                minLen++;
            else
                minLen += generator.ntInfos.get(symbol.index).minLen;
        return minLen != old;
    }

    RuleInfo(Generator generator, Rule rule) {
        this.generator = generator;
        this.rule = rule;
        for (Symbol symbol : rule)
            if (!symbol.terminal) {
                hasNt = true;
                break;
            }
    }
}
