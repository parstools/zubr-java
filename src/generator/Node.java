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

    int ruleIndex = -1;
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
        int minLens[] = new int[rule.size()];
        int sumBackMinLens[] = new int[rule.size()];
        int sum = 0;
        for (int i = rule.size() - 1; i >= 0; i--) {
            minLens[i] = generator.getMinLen(rule.get(i));
            sum += minLens[i];
            sumBackMinLens[i] = sum;
        }
        for (int i = start; i < rule.size(); i++) {
            Node child = new Node(generator, rule.get(i));
            if (child.next(maxLen - (i < rule.size() - 1 ? sumBackMinLens[i + 1] : 0)) == -2)
                return;
            maxLen -= child.getLen();
            childs.add(child);
        }
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

    public boolean nextFit(int maxLen) {
        while (true) {
            int result = next(maxLen);
            if (result == 0)
                return true;
            else if (result == -1)
                return false;
        }
    }

    //retuns: 0: ok, -1: end of rules, -2: not enough space
    public int next(int maxLen) {
        if (symbol.terminal)
            return -1;
        if (!ruleIndexOK())
            return -1;
        if (nextRuleIndexOK()) {
            NTInfo ntINfo = generator.ntInfos.get(symbol.index);
            RuleInfo ruleInfo = ntINfo.ruleInfos.get(ruleIndex + 1);
            if (maxLen < ruleInfo.minLen) {
                ruleIndex++;
                return -2;
            }
        }
        int lens[] = new int[childs.size()];
        int sumlens[] = new int[childs.size()];
        int sum = 0;
        for (int i = 0; i < childs.size(); i++) {
            lens[i] = childs.get(i).getLen();
            sum += lens[i];
            sumlens[i] = sum;
        }

        int minLens[] = new int[childs.size()];
        int sumBackMinLens[] = new int[childs.size()];
        sum = 0;
        for (int i = childs.size() - 1; i >= 0; i--) {
            minLens[i] = generator.getMinLen(childs.get(i).symbol);
            sum += minLens[i];
            sumBackMinLens[i] = sum;
        }

        int canNextIndex = -1;
        for (int i = childs.size() - 1; i >= 0; i--) {
            int newMaxLen = maxLen;
            if (i > 0)
                newMaxLen -= sumlens[i - 1];
            if (i < sumBackMinLens.length - 1)
                newMaxLen -= sumBackMinLens[i + 1];
            if (childs.get(i).next(newMaxLen) == 0) {
                int len = childs.get(i).getLen();
                canNextIndex = i;
                break;
            } else {
                childs.remove(i);
            }
        }

        if (canNextIndex < 0)
            ruleIndex++;
        if (ruleIndexOK()) {
            generateChilds(canNextIndex + 1, maxLen);
            if (getLen() > maxLen)
                return -2;
            else
                return 0;
        } else
            return -1;
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
