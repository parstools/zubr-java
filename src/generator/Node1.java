package generator;

import grammar.Grammar;
import grammar.Symbol;

import java.util.ArrayList;
import java.util.List;

public class Node1 {
    List<Node1> childs = null;
    List<RuleInfo> ruleInfos = null;
    int ruleIndex = -1;
    Symbol symbol;
    Generator generator;
    Grammar grammar;

    public Node1(Generator generator, Symbol symbol) {
        this.generator = generator;
        this.grammar = generator.grammar;
        this.symbol = symbol;
        if (!symbol.terminal) {
            childs = new ArrayList<>();
            ruleInfos = new ArrayList<>(generator.ntInfos.get(symbol.index).ruleInfos);
        }
    }

    public String string() {
        if (symbol.terminal)
            return symbol.toString();
        else {
            StringBuilder sb = new StringBuilder();
            for (Node1 child : childs)
                sb.append(child.string());
            return sb.toString();
        }
    }

    public String parenString() {
        if (symbol.terminal)
            return symbol.toString();
        else {
            StringBuilder sb = new StringBuilder();
            sb.append(symbol.toString() + "(");
            for (Node1 child : childs)
                sb.append(child.parenString());
            sb.append(")");
            return sb.toString();
        }
    }

    boolean ruleIndexOK() {
        return ruleIndex < ruleInfos.size();
    }

    void initSuffixForChild(int start, int maxLen) {
        Node1 child = childs.get(start);
        int reservedLen = maxLen;
        for (int i = ruleInfos.size() - 1; i > start; i--)
            reservedLen -= ruleInfos.get(i).minLen;
        if (!child.symbol.terminal)
            child.next(reservedLen);
        if (start + 1 < ruleInfos.get(ruleIndex).rule.size())
            initSuffix(start + 1, maxLen - childs.get(start).getLen());
    }

    void initSuffix(int start, int maxLen) {
        Node1 child = new Node1(generator, ruleInfos.get(ruleIndex).rule.get(start));
        childs.add(child);
        initSuffixForChild(start, maxLen);
    }

    void removeChildFrom(int start) {
        for (int i = childs.size() - 1; i >= start; i--)
            childs.remove(i);
    }

    int getLen() {
        if (symbol.terminal) {
            return 1;
        } else {
            int len = 0;
            for (int i = childs.size() - 1; i >= 0; i--)
                len += childs.get(i).getLen();
            return len;
        }
    }

    boolean nextSuffix(int start, int maxLen) {
        assert (start <= ruleInfos.size());
        if (start == ruleInfos.size()) return true;
        if (!nextSuffix(start + 1, maxLen - childs.get(start).getLen())) {
            removeChildFrom(start + 1);
            initSuffixForChild(start, maxLen);
        }
        return true;
    }

    boolean next(int maxLen) {
        assert (maxLen >= 1);
        assert (!symbol.terminal);
        if (ruleIndex < 0 || !nextSuffix(0, maxLen)) {
            ruleIndex++;
            while (ruleIndex < ruleInfos.size() && ruleInfos.get(ruleIndex).minLen > maxLen)
                ruleIndex++;
            if (!ruleIndexOK())
                return false;
            childs.clear();
            initSuffix(0, maxLen);
        }
        return true;
    }
}
