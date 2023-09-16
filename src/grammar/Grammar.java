package grammar;

import graph.DG;
import graph.JohnsonsAlgorithm;
import graph.VertexEdge;
import util.Hash;
import util.NoMinLenGrammarException;

import java.util.*;

import static java.lang.System.out;

public class Grammar implements Cloneable {
    public List<Nonterminal> nonterminals = new ArrayList<>();
    Map<String, Integer> ntNamesToInt = new HashMap<>();
    List<String> ntNames = new ArrayList<>();
    Map<String, Integer> tNamesToInt = new HashMap<>();
    List<String> tNames = new ArrayList<>();

    int globalRuleCounter = 0;
    List<Rule> globalRules = new ArrayList<>();

    Rule getGlobalRule(int globalIndex) {
        return globalRules.get(globalIndex);
    }

    public String getTerminalName(int t) {
        if (t==-1)
            return "$"; //end of stream
        else
            return tNames.get(t);
    }

    public String getNonTerminalName(int t) {
        return ntNames.get(t);
    }

    public String getSymbolName(Symbol symbol) {
        if (symbol.terminal)
            return getTerminalName(symbol.index);
        else
            return ntNames.get(symbol.index);
    }

    public Nonterminal getNT(int ntIndex) {
        return nonterminals.get(ntIndex);
    }

    public List<Rule> getNTRules(int ntIndex) {
        return getNT(ntIndex).rules;
    }

    public Rule getNTRule(int ntIndex, int ruleIdx) {
        return getNTRules(ntIndex).get(ruleIdx);
    }

    void addNT(String ntName) {
        int n = nonterminals.size();
        if (ntNamesToInt.containsKey(ntName))
            return;
        ntNamesToInt.put(ntName, n);
        ntNames.add(ntName);
        Nonterminal nt = new Nonterminal(this);
        nt.index = n;
        nonterminals.add(nt);
    }

    void addT(String tName) {
        if (tNamesToInt.containsKey(tName))
            return;
        tNamesToInt.put(tName, tNamesToInt.size());
        tNames.add(tName);
    }

    String parseNTname(String line) {
        int n = line.indexOf("->");
        return line.substring(0, n).trim();
    }

    public Grammar() {

    }

    public Grammar(List<String> lines) {
        for (String line : lines) {
            addNT(parseNTname(line));
        }
        for (String line : lines) {
            String ntName = parseNTname(line);
            int ntIndex = ntNamesToInt.get(ntName);
            int n = line.indexOf("->");
            String ruleString = line.substring(n+2).trim();
            Nonterminal nt = nonterminals.get(ntIndex);
            Rule rule = new Rule(this, nt);
            globalRules.add(rule);
            globalRuleCounter++;
            rule.parse(ruleString);
            nt.addRule(rule);
        }
        computeMinLen();
        checkMinLen();
        computeNonNullableCount();
        detectCycles();
    }

    private void computeNonNullableCount() {
        for (Nonterminal nt : nonterminals)
            for (Rule rule: nt.rules)
                rule.computeNonNullableCount();
    }

    private void checkMinLen() {
        int index = 0;
        for (Nonterminal nt : nonterminals) {
            if (nt.minLen < 0)
                throw new NoMinLenGrammarException("not computed minLen for " + getNonTerminalName(index));
            for (Rule ruleInfo: nt.rules)
                if (ruleInfo.minLen < 0)
                    throw new NoMinLenGrammarException("not computed minLen for " + ruleInfo.toString());
            index++;
        }
    }

    private void computeMinLen() {
        boolean changed = true;
        while (changed) {
            changed = false;
            for (Nonterminal nt : nonterminals) {
                if (nt.computeMinLen())
                    changed = true;
            }
        }
    }

    public int findTerminal(String name) {
        if (name.equals("$"))
            return -1;
        else
            return tNamesToInt.get(name);
    }

    public Symbol findSymbol(String name) {
        if (ntNamesToInt.containsKey(name)) {
            return new Symbol(this, false, ntNamesToInt.get(name));
        } else {
            return new Symbol(this, true, tNamesToInt.get(name));
        }
    }

    public Symbol findSymbolAndAddTerminal(String name) {
        if (ntNamesToInt.containsKey(name)) {
            return new Symbol(this, false, ntNamesToInt.get(name));
        } else {
            if (!tNamesToInt.containsKey(name))
                addT(name);
            return new Symbol(this, true, tNamesToInt.get(name));
        }
    }

    public int getMinLen(Symbol symbol) {
        if (symbol.terminal)
            return 1;
        else
            return getNT(symbol.index).minLen;
    }

    @Override
    public int hashCode() {
        Hash h = new Hash();
        for (int i=0; i<nonterminals.size(); i++) {
            Nonterminal nt = nonterminals.get(i);
            h.add(i);
            h.add(nt.hashCode());
        }
        return h.hash();
    }

    public List<String> lines() {
        List<String> lines = new ArrayList<>();
        for (int i=0; i<nonterminals.size(); i++) {
            Nonterminal nt = nonterminals.get(i);
            for (Rule rule: nt.rules)
                lines.add(rule.toString());
        }
        return lines;
    }

    public Object clone() {
        Grammar newGrammar = new Grammar();
        newGrammar.nonterminals = new ArrayList<>();
        for (Nonterminal nt: nonterminals) {
            Nonterminal ntCloned = (Nonterminal) nt.clone();
            ntCloned.grammar = newGrammar;
            newGrammar.nonterminals.add(ntCloned);
        }
        return newGrammar;
    }

    Cycles cycles;

    void detectCycles() {
        DG graph = new DG(nonterminals.size());
        for (Nonterminal nt : nonterminals)
            for (Rule rule: nt.rules)
                if (rule.cycleSuspected()) {
                    for (Symbol symbol : rule)
                        graph.addEdge(nt.index, symbol.index, rule.globalIndex);
                }
        List<List<VertexEdge>> johnsonResult = JohnsonsAlgorithm.calculateCycles(graph);
        cycles = new Cycles(this, johnsonResult);
    }
}
