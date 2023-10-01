package grammar;

import graph.DG;
import graph.JohnsonsAlgorithm;
import graph.VertexEdge;
import util.Hash;
import util.Name;

import java.util.*;

public class Grammar implements Cloneable {
    public List<Nonterminal> nonterminals = new ArrayList<>();
    Map<String, Integer> ntNamesToInt = new HashMap<>();
    List<String> ntNames = new ArrayList<>();
    Map<String, Integer> tNamesToInt = new HashMap<>();
    List<String> tNames = new ArrayList<>();

    int globalRuleCounter = 0;
    boolean minLenOK = false;
    public List<Rule> globalRules = new ArrayList<>();

    Rule getGlobalRule(int globalIndex) {
        return globalRules.get(globalIndex);
    }

    public String getTerminalName(int t) {
        if (t == -1)
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

    int directLeftRecursiveNt() {
        for (int i = 0; i < nonterminals.size(); i++) {
            Nonterminal nt = nonterminals.get(i);
            for (Rule rule : nt.rules)
                if (rule.directLeftRecursive(i))
                    return i;
        }
        return -1;
    }

    public void eliminationDirectRecursion() {
        while (true) {
            int index = directLeftRecursiveNt();
            if (index < 0) break;
            eliminationDirectRecursionForNt(index);
        }
    }

    void eliminationDirectRecursionForNt(int index) {
        List<Rule> recursiveRules = new ArrayList<>();
        List<Rule> nonrecursiveRules = new ArrayList<>();
        Nonterminal nt = nonterminals.get(index);
        for (Rule rule : nt.rules) {
            if (rule.directLeftRecursive(index)) {
                if (rule.size()>1) //without rules A->A
                    recursiveRules.add(rule);
            } else
                nonrecursiveRules.add(rule);
        }
        assert (!recursiveRules.isEmpty());
        Nonterminal newNt = insertNonterminal(index);
        nt.rules.clear();
        for (Rule rule: nonrecursiveRules) {
            rule.add(new Symbol(this, false, newNt.index));
            nt.addRule(rule);
        }
        for (Rule rule: recursiveRules) {
            rule.remove(0);
            assert (!rule.isEmpty());
            rule.owner = newNt;
            rule.add(new Symbol(this, false, newNt.index));
            newNt.addRule(rule);
        }
        newNt.addRule(new Rule(nt.grammar, newNt));
    }

    private Nonterminal insertNonterminal(int sourceIndex) {
        Nonterminal newNt = new Nonterminal(this);
        newNt.rules = new ArrayList<>();
        newNt.index = sourceIndex + 1;
        insertName(sourceIndex);
        updateNtIndices(newNt.index);
        updateRuleSymbolsForInsert(newNt.index);
        nonterminals.add(newNt.index, newNt);
        return newNt;
    }

    private void insertName(int sourceIndex) {
        for (Map.Entry<String, Integer> entry : ntNamesToInt.entrySet()) {
            if (entry.getValue() > sourceIndex)
                entry.setValue(entry.getValue() + 1);
        }
        ntNames.add(sourceIndex + 1, newNameFrom(ntNames.get(sourceIndex)));
    }

    private String newNameFrom(String s) {
        if (Name.hasNameSuffixNumber(s)) {
            int n = Name.suffixNumber(s);
            String nameAlone = Name.nameWithoutNumber(s);
            String newName = s;
            do {
                n++;
                newName = nameAlone + String.valueOf(n);
            } while (ntNamesToInt.containsKey(newName));
            return newName;
        } else return s + "1";
    }

    private void updateNtIndices(int index) {
        for (Nonterminal nt : nonterminals)
            if (nt.index>=index)
                nt.index++;
    }

    private void updateRuleSymbolsForInsert(int index) {
        for (Nonterminal nt : nonterminals) {
            for (Rule rule : nt.rules)
                for (Symbol symbol : rule)
                    symbol.updateNtFrom(index);
        }
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
        if (n < 0)
            throw new RuntimeException("not found -> in grammar in line " + line);
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
            String ruleString = line.substring(n + 2).trim();
            Nonterminal nt = nonterminals.get(ntIndex);
            Rule rule = new Rule(this, nt);
            globalRules.add(rule);
            globalRuleCounter++;
            rule.parse(ruleString);
            nt.addRule(rule);
        }
        computeMinLen();
        minLenOK = checkMinLen();
        if (minLenOK) {
            computeNonNullableCount();
            detectCycles();
        }
    }

    private void computeNonNullableCount() {
        for (Nonterminal nt : nonterminals)
            for (Rule rule : nt.rules)
                rule.computeNonNullableCount();
    }

    private boolean checkMinLen() {
        for (Nonterminal nt : nonterminals) {
            if (nt.minLen < 0)
                return false;
            for (Rule rule : nt.rules)
                if (rule.minLen < 0)
                    return false;
        }
        return true;
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
        for (int i = 0; i < nonterminals.size(); i++) {
            Nonterminal nt = nonterminals.get(i);
            h.add(i);
            h.add(nt.hashCode());
        }
        return h.hash();
    }

    public List<String> toLines() {
        List<String> lines = new ArrayList<>();
        for (int i = 0; i < nonterminals.size(); i++) {
            Nonterminal nt = nonterminals.get(i);
            for (Rule rule : nt.rules)
                lines.add(rule.toString());
        }
        return lines;
    }

    public Object clone() {
        Grammar newGrammar = new Grammar();
        newGrammar.nonterminals = new ArrayList<>();
        for (Nonterminal nt : nonterminals) {
            Nonterminal ntCloned = (Nonterminal) nt.clone();
            ntCloned.grammar = newGrammar;
            newGrammar.nonterminals.add(ntCloned);
        }
        return newGrammar;
    }

    public Cycles cycles;

    void detectCycles() {
        DG graph = new DG(nonterminals.size());
        for (Nonterminal nt : nonterminals)
            for (Rule rule : nt.rules)
                if (rule.cycleSuspected()) {
                    for (Symbol symbol : rule)
                        graph.addEdge(nt.index, symbol.index, rule.globalIndex);
                }
        List<List<VertexEdge>> johnsonResult = JohnsonsAlgorithm.calculateCycles(graph);
        cycles = new Cycles(this, johnsonResult);
    }
}
