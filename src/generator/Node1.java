package generator;

import grammar.Grammar;
import grammar.Rule;
import grammar.Symbol;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.lang.System.out;

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
            if (generator.reverse)
                Collections.reverse(ruleInfos);
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

    boolean nextRuleIndexOK() {
        return ruleIndex + 1 < ruleInfos.size();
    }

    boolean initSuffixForChild(int start, int maxLen) {
        Node1 child = childs.get(start);
        int reservedLen = maxLen;
        Rule rule = ruleInfos.get(ruleIndex).rule;
        for (int i = rule.size() - 1; i > start; i--)
            reservedLen -= generator.getMinLen(rule.get(i));
        if (!child.symbol.terminal)
            if (!child.next(reservedLen))
                return false;
        if (start + 1 < ruleInfos.get(ruleIndex).rule.size())
            initSuffix(start + 1, maxLen - childs.get(start).getLen());
        return true;
    }

    void initSuffix(int start, int maxLen) {
        assert (childs.size() == start);
        if (start >= ruleInfos.get(ruleIndex).rule.size())
            return;
        Node1 child = new Node1(generator, ruleInfos.get(ruleIndex).rule.get(start));
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
            NTInfo ntINfo = generator.ntInfos.get(symbol.index);
            RuleInfo ruleInfo = ntINfo.ruleInfos.get(ruleIndex + 1);
            if (maxLen < ruleInfo.minLen) {
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
            while (ruleIndex < ruleInfos.size() && ruleInfos.get(ruleIndex).minLen > maxLen)
                ruleIndex++;
            if (!ruleIndexOK())
                return false;
            childs.clear();
            initSuffix(0, maxLen);
        }
        assert(ruleIndex>=0);
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
}
