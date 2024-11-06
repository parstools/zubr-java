package generator;

import grammar.Grammar;
import grammar.Symbol;
import set.Sequence;
import set.SequenceSet;
import set.TokenSet;

import java.util.Random;
import java.util.Stack;

import static java.lang.Character.*;
import static java.lang.System.exit;
import static java.lang.System.out;

public class Generator {
    Grammar grammar;
    Node root;

    RuleOrder ruleOrder;
    int maxLen;

    Random rand;


    public Generator(Grammar grammar, int maxLen, RuleOrder ruleOrder) {
        if (!grammar.grammarOK()) {
            throw new RuntimeException("bad grammar for generator");
        }
        this.grammar = grammar;
        this.maxLen = maxLen;
        this.ruleOrder = ruleOrder;
        rand = new Random();
        root = new Node(this, getNT(0), maxLen);
    }

    public Generator(Grammar grammar, int maxLen, RuleOrder ruleOrder, long seed) {
        this.grammar = grammar;
        this.maxLen = maxLen;
        this.ruleOrder = ruleOrder;
        rand = new Random(seed);
        root = new Node(this, getNT(0), maxLen);
    }

    void restart() {
        root = new Node(this, getNT(0), maxLen);
    }

    Symbol getNT(int ntIndex) {
        return grammar.nonterminals.get(ntIndex);
    }

    public String string() {
        return root.string();
    }

    public Sequence seq() {
        return root.seq();
    }

    public String parenString() {
        return root.parenString();
    }

    public boolean next() {
        return root.next();
    }

    public void collectFirst(int ntNumber, int k, SequenceSet sset) {
        root.collectFirst(ntNumber, k, sset);
    }

    public TokenSet collectFirst(int ntNumber, int k) {
        SequenceSet sset = new SequenceSet();
        root.collectFirst(ntNumber, k, sset);
        TokenSet result = new TokenSet(grammar, k);
        result.addAllSeqDoneOrEof(sset);
        return result;
    }

    public void collectFollow(int ntNumber, int k, SequenceSet sset) {
        Sequence upSeq = new Sequence(grammar, "$");
        Stack<Sequence> stackSeq = new Stack<>();
        stackSeq.add(upSeq);
        root.collectFollow(ntNumber, k, stackSeq, sset);
    }

    public TokenSet collectFollow(int ntNumber, int k) {
        SequenceSet sset = new SequenceSet();
        collectFollow(ntNumber, k, sset);
        TokenSet result = new TokenSet(grammar, k);
        result.addAllSeqDoneOrEof(sset);
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
                String substring = parenStr.substring(pos, pos1);
                pos = pos1;
                child = createFromString(substring);
            } else {
                pos++;
                Symbol symbol = grammar.findSymbol(Character.toString(c));
                child = new Node(this, symbol, maxLen); //maxLen is not important here
            }
            node.addChild(child);
        }
    }

    public void createRootFromString(String parenStr) {
        root = createFromString(parenStr);
    }

    private Node createFromString(String parenStr) {
        assert (!parenStr.isEmpty());
        char c = parenStr.charAt(0);
        assert (isAlphabetic(c));
        Symbol symbol = grammar.findSymbol(Character.toString(c));
        Node node = new Node(this, symbol, maxLen);
        if (!symbol.terminal) {
            assert (parenStr.charAt(1) == '(');
            int pos = afterParen(parenStr, 1);
            String sub = parenStr.substring(2, pos - 1);
            if (!sub.isEmpty())
                createChildsFromString(sub, node);
        }
        return node;
    }
}
