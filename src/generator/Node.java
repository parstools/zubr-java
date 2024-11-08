package generator;

import grammar.Grammar;
import grammar.Nonterminal;
import grammar.Rule;
import grammar.Symbol;
import set.Sequence;
import set.SequenceSet;
import util.Hash;

import java.util.*;

import static java.lang.System.out;

public class Node {
    List<Node> childs = null;
    List<Rule> rules = null;
    int ruleIndex = -1;
    final Symbol symbol;
    final Generator generator;
    final Grammar grammar;

    void addChild(Node child) {
        childs.add(child);
    }

    final int nodeMaxLen;

    public Node(Generator generator, Symbol symbol, int nodeMaxLen) {
        this(generator, symbol, nodeMaxLen, 0);
    }

    private int ruleHash;
    public Node(Generator generator, Symbol symbol, int nodeMaxLen, int ruleHash) {
        this.nodeMaxLen = nodeMaxLen;
        this.generator = generator;
        this.grammar = generator.grammar;
        this.symbol = symbol;
        this.ruleHash = ruleHash;
        if (!symbol.terminal) {
            childs = new ArrayList<>();
            rules = new ArrayList<>(((Nonterminal)symbol).rules);
            if (generator.ruleOrder == RuleOrder.roSort
                    || generator.ruleOrder == RuleOrder.roRevereSort)
                sortRules();
            if (generator.ruleOrder == RuleOrder.roRevereSort)
                Collections.reverse(rules);
            else if (generator.ruleOrder == RuleOrder.roShuffle)
                Collections.shuffle(rules, new Random(generator.rand.nextLong()));
        }
    }

    public Sequence seq() {
        Sequence buildSeq = new Sequence(grammar);
        seq(buildSeq);
        return buildSeq;
    }

    private void seq(Sequence buildSeq) {
        if (symbol.terminal) {
            buildSeq.add(symbol.getIndex());
        } else {
            for (Node child : childs)
                child.seq(buildSeq);
        }
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

    public String usedRules() {
        if (symbol.terminal)
            return "";
        else {
            StringBuilder sb = new StringBuilder();
            sb.append(String.valueOf(ruleIndex));
            if (childs != null) {
                sb.append(symbol.toString() + "(");
                for (int i = 0; i < childs.size(); i++) {
                    Node child = childs.get(i);
                    if (i > 0)
                        sb.append(",");
                    sb.append(child.usedRules());
                }
                sb.append(")");
            }
            return sb.toString();
        }
    }

    boolean ruleIndexOK() {
        return ruleIndex < rules.size();
    }

    boolean nextRuleIndexOK() {
        return ruleIndex + 1 < rules.size();
    }

    boolean initSuffix(int start, int maxLen) {
        assert (childs.size() == start);
        if (maxLen < 0)
            return false;
        if (start >= rules.get(ruleIndex).size())
            return true;
        int reservedLen = maxLen;
        Rule rule = rules.get(ruleIndex);
        for (int i = rule.size() - 1; i > start; i--)
            reservedLen -= grammar.getMinLen(rule.get(i));
        if (reservedLen < grammar.getMinLen(rule.get(start)))
            return false;
        assert (reservedLen <= nodeMaxLen);
        int childRuleHash;
        if (reservedLen == nodeMaxLen) {
            childRuleHash = ruleHash;
        } else childRuleHash = 0;
        Node child = new Node(generator, rules.get(ruleIndex).get(start), reservedLen, childRuleHash);
        childs.add(child);
        if (!child.symbol.terminal)
            if (!child.next())
                return false;
        if (start + 1 < rules.get(ruleIndex).size())
            if (!initSuffix(start + 1, maxLen - childs.get(start).getLen()))
                return false;
        return true;
    }

    void removeChildsFrom(int start) {
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

    private boolean nextWithTheSameRule(int maxLen) {
        if (symbol.terminal)
            return false;
        if (!ruleIndexOK())
            return false;
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
            minLens[i] = grammar.getMinLen(childs.get(i).symbol);
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
            assert (childs.get(i).nodeMaxLen == newMaxLen);
            if (childs.get(i).next()) {
                int len = childs.get(i).getLen();
                sumlens[i] += len - lens[i];
                lens[i] = len;
                canNextIndex = i;
                break;
            } else {
                childs.remove(i);
            }
        }
        if (canNextIndex < 0)
            return false;
        else
            return initSuffix(canNextIndex + 1, maxLen - (canNextIndex >= 0 ? sumlens[canNextIndex] : 0));
    }

    int globKey() {
        return (symbol.getIndex() << 10) + ruleIndex;
    }

    void addCycleRule() {
        int key = globKey();
        if (grammar.cycleRules.contains(key))
            generator.cycleRules.add(key);
    }

    void removeCycleRule() {
        int key = globKey();
        if (grammar.cycleRules.contains(key))
            generator.cycleRules.remove(key);
    }

    boolean isRuleCycle() {
        int key = globKey();
        return (generator.cycleRules.contains(key));
    }

    private boolean initWithNextCorrectRule() {
        if (ruleIndex>=0)
            removeCycleRule();
        ruleIndex++;
        while (ruleIndex < rules.size() &&
                ((rules.get(ruleIndex).minLen > nodeMaxLen) || isRuleCycle()))
            ruleIndex++;
        if (!ruleIndexOK())
            return false;
        childs.clear();
        addCycleRule();
        return initSuffix(0, nodeMaxLen);
    }

    boolean next() {
        assert (nodeMaxLen >= 0);
        if (symbol.terminal)
            return false;
        if (ruleIndex < 0 || !nextWithTheSameRule(nodeMaxLen))
            return initWithNextCorrectRule();
        else
            return true;
    }

    private void printDotPart(String name) {
        String shapeStr;
        if (childs == null)
            shapeStr = "; shape = doublecircle";
        else
            shapeStr = "";
        out.println("  P" + name + " [label = " + symbol.name + shapeStr + "]");
        if (!name.isEmpty()) {
            String upName = name.substring(0, name.length() - 1);
            out.println("  P" + upName + " -> P" + name);
        }
        if (childs != null)
            for (int i = 0; i < childs.size(); i++) {
                int as_int = 'a';
                char c = (char) (as_int + i);
                String s = name + String.valueOf(c);
                childs.get(i).printDotPart(s);
            }
    }

    public void printDot() {
        if (childs.size() > 26)
            throw new RuntimeException();
        out.println("digraph {");
        printDotPart("");
        out.println("}");
    }

    private Sequence appendTerminals(int k) {
        assert (k > 0);
        Sequence seq = new Sequence(grammar);
        if (symbol.terminal)
            seq.add(symbol.getIndex());
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

    public void collectFirst(int ntNumber, int k, SequenceSet sset) {
        if (!symbol.terminal && symbol.getIndex() == ntNumber) {
            Sequence seq = appendTerminals(k);
            sset.add(seq);
        }
        if (childs != null)
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

    public void collectFollow(int ntNumber, int k, Stack<Sequence> stackSeq, SequenceSet sset) {
        if (!symbol.terminal && symbol.getIndex() == ntNumber) {
            Sequence seq = kSymbolsFromStack(k, stackSeq);
            sset.add(seq);
        }
        if (childs != null)
            for (int i = 0; i < childs.size(); i++) {
                Node child = childs.get(i);
                Sequence seq = terminalsFrom(i + 1, k);
                stackSeq.push(seq);
                child.collectFollow(ntNumber, k, stackSeq, sset);
                stackSeq.pop();
            }
    }


    void sortRules() {
        Collections.sort(rules, new Comparator<Rule>() {
            @Override
            public int compare(Rule r1, Rule r2) {
                if (r1.hasNt && !r2.hasNt)
                    return 1;
                else if (!r1.hasNt && r2.hasNt)
                    return -1;
                else if (r1.minLen < r2.minLen)
                    return -1;
                else if (r1.minLen > r2.minLen)
                    return 1;
                else if (r1.owner.getIndex() < r2.owner.getIndex())
                    return -1;
                else if (r1.owner.getIndex() > r2.owner.getIndex())
                    return 1;
                else if (r1.index < r2.index)
                    return -1;
                else if (r1.index > r2.index)
                    return 1;
                else
                    return 0;
            }
        });
    }
}
