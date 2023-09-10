package generator;

import grammar.Grammar;
import grammar.Rule;
import grammar.Symbol;
import set.Sequence;
import set.SequenceSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class Node {
    final List<Node> childs = new ArrayList<>();

    void addChild(Node child) {
        childs.add(child);
    }

    int ruleIndex = 0;
    int ruleCount;
    Symbol symbol;
    Generator generator;
    Grammar grammar;

    public Node(Generator generator, Symbol symbol) {
        this.generator = generator;
        this.grammar = generator.grammar;
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
            if (maxLen < ruleInfo.minLen)
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
            if (getLen() > maxLen)
                return false;
            else
                return true;
        } else
            return false;
    }

    public void collectFirst(int ntNumber, int k, SequenceSet sset) {
        if (!symbol.terminal && symbol.index == ntNumber) {
            Sequence seq = appendTerminals(k);
            sset.add(seq);
        }
        for (Node child : childs)
            child.collectFirst(ntNumber, k, sset);
    }

    Sequence kSymbolsFromStack(int k, Stack<Sequence> stackSeq) {
        int remaining = k;
        Sequence seq = new Sequence(grammar);
        for (int i = stackSeq.size() - 1; i >= 0; i--) {
            Sequence stackItem = stackSeq.get(i);
            if (remaining >= stackItem.size()) {
                seq.addAll(stackItem);
                remaining -= stackItem.size();
            } else {
                for (int j = 0; j < remaining; j++)
                    seq.add(stackItem.get(j));
                remaining = 0;
                break;
            }
            if (remaining == 0) break;
        }
        return seq;
    }

    public void collectFollow(int ntNumber, int k, Stack<Sequence> stackSeq, SequenceSet sset) {
        if (!symbol.terminal && symbol.index == ntNumber) {
            Sequence seq = kSymbolsFromStack(k, stackSeq);
            sset.add(seq);
        }
        for (int i = 0; i < childs.size(); i++) {
            Node child = childs.get(i);
            Sequence seq = terminalsFrom(i + 1, k);
            stackSeq.push(seq);
            child.collectFollow(ntNumber, k, stackSeq, sset);
            stackSeq.pop();
        }
    }

    private Sequence appendTerminals(int k) {
        assert (k > 0);
        Sequence seq = new Sequence(grammar);
        if (symbol.terminal)
            seq.add(symbol.index);
        else {
            int remaining = k;
            for (Node child : childs) {
                Sequence subSeq = child.appendTerminals(remaining);
                seq.addAll(subSeq);
                remaining -= subSeq.size();
                assert (remaining >= 0);
                if (remaining == 0)
                    break;
            }
        }
        return seq;
    }

    Sequence terminalsFrom(int start, int k) {
        assert (k > 0);
        Sequence seq = new Sequence(grammar);
        int remaining = k;
        for (int i = start; i < childs.size(); i++) {
            Node child = childs.get(i);
            Sequence subSeq = child.appendTerminals(remaining);
            seq.addAll(subSeq);
            remaining -= subSeq.size();
            assert (remaining >= 0);
            if (remaining == 0)
                break;
        }
        return seq;
    }
}
