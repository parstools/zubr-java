package generator;

import grammar.Grammar;
import grammar.Nonterminal;
import grammar.Rule;
import grammar.Symbol;
import set.Sequence;
import set.SequenceSet;
import set.TokenSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import static java.lang.Character.*;

public class Generator {
    Grammar grammar;
    int limit = 40 * 1000;
    Node root;

    RuleOrder ruleOrder;
    int maxLen;

    public Generator(Grammar grammar, int maxLen, RuleOrder ruleOrder) {
        this.grammar = grammar;
        this.maxLen = maxLen;
        this.ruleOrder = ruleOrder;
        root = new Node(this, getNT(0));
    }

    void restart() {
        root = new Node(this, getNT(0));
    }

    Symbol getNT(int ntIndex) {
        return new Symbol(grammar, false, ntIndex);
    }

    public String string() {
        return root.string();
    }

    public String parenString() {
        return root.parenString();
    }

    public boolean next() {
        return root.next(maxLen);
    }

    public TokenSet collectFirst(int ntNumber, int k) {
        SequenceSet sset = new SequenceSet();
        root.collectFirst(ntNumber, k, sset);
        TokenSet result = new TokenSet(grammar, k);
        result.addAllSSeq(sset);
        return result;
    }

    public TokenSet collectFirstAllGenerated(int ntNumber, int k) {
        TokenSet result = new TokenSet(grammar, k);
        int counter = 0;
        restart();
        while (next()) {
            counter++;
            SequenceSet sset = new SequenceSet();
            root.collectFirst(ntNumber, k, sset);
            result.addAllSSeq(sset);
            if (counter >= limit)
                return result;
        }
        return result;
    }

    public TokenSet collectFollow(int ntNumber, int k) {
        SequenceSet sset = new SequenceSet();
        Sequence upSeq = new Sequence(grammar, "$");
        Stack<Sequence> stackSeq = new Stack<>();
        stackSeq.add(upSeq);
        root.collectFollow(ntNumber, k, stackSeq, sset);
        TokenSet result = new TokenSet(grammar, k);
        result.addAllSSeq(sset);
        return result;
    }

    public TokenSet collectFollowAllGenerated(int ntNumber, int k) {
        Sequence upSeq = new Sequence(grammar, "$");
        TokenSet result = new TokenSet(grammar, k);
        Stack<Sequence> stackSeq = new Stack<>();
        stackSeq.add(upSeq);
        int counter = 0;
        restart();
        while (next()) {
            counter++;
            SequenceSet sset = new SequenceSet();
            assert (stackSeq.size() == 1);
            root.collectFollow(ntNumber, k, stackSeq, sset);
            result.addAllSSeq(sset);
            if (counter >= limit)
                return result;
        }
        return result;
    }

    static int afterParen(String parenStr, int start) {
        char c = parenStr.charAt(start);
        assert (c == '(');
        int depth = 1;
        int pos = start + 1;
        while (pos < parenStr.length() && depth > 0) {
            c = parenStr.charAt(pos);
            if (c == '(') depth++;
            else if (c == ')') depth--;
            pos++;
        }
        return pos;
    }

    private void createChildsFromString(String parenStr, Node node) {
        int pos = 0;
        while (pos < parenStr.length()) {
            char c = parenStr.charAt(pos);
            Node child;
            if (isUpperCase(c)) {
                int pos1 = afterParen(parenStr, pos + 1);
                String sub = parenStr.substring(pos, pos1);
                pos = pos1;
                child = new Node(this, new Symbol(grammar, false, 0));
                createFromString(sub, child);
            } else {
                pos++;
                Symbol symbol = grammar.findSymbol(Character.toString(c));
                child = new Node(this, symbol);
            }
            node.addChild(child);
        }
    }

    public void createFromString(String parenStr) {
        root = new Node(this, getNT(0));
        createFromString(parenStr, root);
    }

    private void createFromString(String parenStr, Node node) {
        assert (!parenStr.isEmpty());
        char c = parenStr.charAt(0);
        assert (isAlphabetic(c));
        node.symbol = grammar.findSymbol(Character.toString(c));
        if (isUpperCase(c)) {
            assert (parenStr.charAt(1) == '(');
            int pos = afterParen(parenStr, 1);
            String sub = parenStr.substring(2, pos - 1);
            if (!sub.isEmpty())
                createChildsFromString(sub, node);
        }
    }
}
