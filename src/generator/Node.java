package generator;

import grammar.Grammar;
import grammar.Nonterminal;
import grammar.Rule;
import grammar.Symbol;
import set.Sequence;
import set.SequenceSet;

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

    public Node(Generator generator, Symbol symbol) {
        this.generator = generator;
        this.grammar = generator.grammar;
        this.symbol = symbol;
        if (!symbol.terminal) {
            childs = new ArrayList<>();
            rules = new ArrayList<>(grammar.getNTRules(symbol.index));
            if (generator.ruleOrder == RuleOrder.roSort
                    || generator.ruleOrder == RuleOrder.roRevereSort)
                sortRules();
            if (generator.ruleOrder == RuleOrder.roRevereSort)
                Collections.reverse(rules);
            else if (generator.ruleOrder == RuleOrder.roShuffle)
                Collections.shuffle(rules);
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

    boolean ruleIndexOK() {
        return ruleIndex < rules.size();
    }

    boolean nextRuleIndexOK() {
        return ruleIndex + 1 < rules.size();
    }

    boolean initSuffixForChild(int start, int maxLen) {
        Node child = childs.get(start);
        int reservedLen = maxLen;
        Rule rule = rules.get(ruleIndex);
        for (int i = rule.size() - 1; i > start; i--)
            reservedLen -= grammar.getMinLen(rule.get(i));
        if (!child.symbol.terminal)
            if (!child.next(reservedLen))
                return false;
        if (start + 1 < rules.get(ruleIndex).size())
            initSuffix(start + 1, maxLen - childs.get(start).getLen());
        return true;
    }

    void initSuffix(int start, int maxLen) {
        assert (childs.size() == start);
        if (start >= rules.get(ruleIndex).size())
            return;
        Node child = new Node(generator, rules.get(ruleIndex).get(start));
        childs.add(child);
        initSuffixForChild(start, maxLen);
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

    public boolean nextTry(int maxLen) {
        if (symbol.terminal)
            return false;
        if (!ruleIndexOK())
            return false;
        if (nextRuleIndexOK()) {
            Nonterminal nt = grammar.getNT(symbol.index);
            Rule rule = rules.get(ruleIndex + 1);
            if (maxLen < rule.minLen) {
                ruleIndex++;
                return false;
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
            if (childs.get(i).nextTry(newMaxLen)) {
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
            ruleIndex++;
        if (ruleIndexOK()) {
            initSuffix(canNextIndex + 1, maxLen - (canNextIndex >= 0 ? sumlens[canNextIndex] : 0));
            assert (getLen() <= maxLen);
            return true;
        } else
            return false;
    }


    boolean next(int maxLen) {
        assert (maxLen >= 0);
        assert (!symbol.terminal);
        if (ruleIndex < 0 || !nextTry(maxLen)) {
            ruleIndex++;
            while (ruleIndex < rules.size() && rules.get(ruleIndex).minLen > maxLen)
                ruleIndex++;
            if (!ruleIndexOK())
                return false;
            childs.clear();
            initSuffix(0, maxLen);
        }
        assert (ruleIndex >= 0);
        return true;
    }

    private void printDotPart(String name) {
        String shapeStr;
        if (childs == null)
            shapeStr = "; shape = doublecircle";
        else
            shapeStr = "";
        out.println("  P" + name + " [label = " + grammar.getSymbolName(symbol) + shapeStr + "]");
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

    public void collectFirst(int ntNumber, int k, SequenceSet sset) {
        if (!symbol.terminal && symbol.index == ntNumber) {
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
        if (!symbol.terminal && symbol.index == ntNumber) {
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
                else if (r1.globalIndex < r2.globalIndex)
                    return -1;
                else if (r1.globalIndex > r2.globalIndex)
                    return 1;
                else
                    return 0;
            }
        });
    }
}
