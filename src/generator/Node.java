package generator;

import grammar.Rule;
import grammar.Symbol;
import set.Sequence;
import set.SequenceSet;
import set.Set;

import java.util.ArrayList;
import java.util.List;

public class Node {
    private final List<Node> childs = new ArrayList<>();
    int ruleIndex = 0;
    int ruleCount;
    Symbol symbol;
    Generator generator;

    public Node(Generator generator, Symbol symbol) {
        this.generator = generator;
        this.symbol = symbol;
        this.ruleCount = generator.ruleCount(symbol);
    }

    public String string() {
        if (symbol.terminal)
            return symbol.toString();
        else {
            StringBuilder sb = new StringBuilder();
            for (Node child : childs)
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
            for (Node child : childs)
                sb.append(child.parenString());
            sb.append(")");
            return sb.toString();
        }
    }

    boolean ruleIndexOK() {
        return ruleIndex < ruleCount;
    }

    boolean nextRuleIndexOK() {
        return ruleIndex + 1 < ruleCount;
    }

    private void generateChilds(int start, int maxLen) {
        assert (!symbol.terminal);
        Rule rule = generator.getRule(symbol.index, ruleIndex);
        for (int i = start; i < rule.size(); i++) {
            Node child = new Node(generator, rule.get(i));
            child.first(maxLen);
            maxLen -= child.getLen();
            childs.add(child);
        }
    }

    void first(int maxLen) {
        assert (childs.isEmpty());
        assert (ruleIndex == 0);
        if (!symbol.terminal)
            generateChilds(0, maxLen);
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

    public boolean next(int maxLen) {
        if (!ruleIndexOK())
            return false;
        assert (!symbol.terminal);//terminal has ruleCount == 0
        if (nextRuleIndexOK()) {
            NTInfo ntINfo = generator.ntInfos.get(symbol.index);
            RuleInfo ruleInfo = ntINfo.ruleInfos.get(ruleIndex + 1);
            if (maxLen <  ruleInfo.minLen)
                return false;
        }
        int lens[] = new int[childs.size()];
        int sumlens[] = new int[childs.size()];
        int sum = 0;
        for (int i = 0; i < childs.size(); i++) {
            lens[i] = childs.get(i).getLen();
            sum += lens[i];
            sumlens[i] = sum;
        }

        int canNextIndex = -1;
        for (int i = childs.size() - 1; i >= 0; i--)
            if (childs.get(i).next(i > 0 ? maxLen - sumlens[i - 1] : maxLen)) {
                canNextIndex = i;
                break;
            } else {
                childs.remove(i);
            }

        if (canNextIndex < 0)
            ruleIndex++;
        if (ruleIndexOK()) {
            generateChilds(canNextIndex + 1, maxLen);
            if (getLen()>maxLen)
                return false;
            else
                return true;
        } else
            return false;
    }

    public SequenceSet collectFirst(int ntNumber, int k, Sequence current) {
        return new SequenceSet();
    }
}
