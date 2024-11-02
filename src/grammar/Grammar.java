package grammar;

import graph.DG;
import graph.JohnsonsAlgorithm;
import graph.VertexEdge;
import util.Hash;
import util.Name;

import java.util.*;

public class Grammar implements Cloneable {
    public List<Nonterminal> nonterminals = new ArrayList<>();
    public List<Terminal> terminals = new ArrayList<>();

    boolean minLenOK = false;
    public boolean grammarOK() {
        return minLenOK;
    }

    int directLeftRecursiveNt() {
        for (int i = 0; i < nonterminals.size(); i++) {
            Nonterminal nt = nonterminals.get(i);
            for (Rule rule : nt.rules)
                if (rule.directLeftRecursive(nt))
                    return i;
        }
        return -1;
    }

    void eliminationDirectRecursionForNt(Nonterminal nt) {
        List<Rule> recursiveRules = new ArrayList<>();
        List<Rule> nonrecursiveRules = new ArrayList<>();
        for (Rule rule : nt.rules) {
            if (rule.directLeftRecursive(nt)) {
                if (rule.size()>1) //without rules A->A
                    recursiveRules.add(rule);
            } else
                nonrecursiveRules.add(rule);
        }
        assert (!recursiveRules.isEmpty());
        Nonterminal newNt = insertNonterminal(nt);
        nt.rules.clear();
        for (Rule rule: nonrecursiveRules) {
            rule.add(newNt);
            nt.addRule(rule);
        }
        for (Rule rule: recursiveRules) {
            rule.remove(0);
            assert (!rule.isEmpty());
            rule.owner = newNt;
            rule.add(newNt);
            newNt.addRule(rule);
        }
        newNt.addRule(new Rule(nt.grammar, newNt));
    }

    private void substituteRules(Nonterminal owner, Nonterminal toSubstitute) {
        List<Rule> newRules = new ArrayList<>();
        for (Rule rule : owner.rules) {
            if (rule.startWithNonterminal(toSubstitute)) {
                for (Rule substRule : toSubstitute.rules) {
                    Rule newRule = substRule.clone(owner);
                    for (int k = 1; k < rule.size(); k++)
                        newRule.add(rule.get(k));
                    //eliminate ident rules: A->A
                    if (newRule.size()!=1 || newRule.getFirst().terminal || newRule.getFirst() != owner) {
                        newRule.index = newRules.size();
                        newRules.add(newRule);
                    }
                }
            } else {
                rule.index = newRules.size();
                newRules.add(rule);
            }
        }
        owner.rules = newRules;
    }

    private void substituteRules(RecurCycle  cycle) {
        assert(cycle.size() >= 2);
        substituteRules(cycle.get(0).owner, cycle.get(1).owner);
    }

    private Nonterminal insertNonterminal(Nonterminal owner) {
        Nonterminal newNt = new Nonterminal(this, newNameFrom(owner.name));
        newNt.rules = new ArrayList<>();
        nonterminals.add(owner.getIndex() + 1, newNt);
        return newNt;
    }

    public Nonterminal findNt(String name) {
        for (Nonterminal nt: nonterminals)
            if (nt.name.equals(name))
                return nt;
        return null;
    }

    public Terminal findT(String name) {
        for (Terminal t: terminals)
            if (t.name.equals(name))
                return t;
        return null;
    }

    private String newNameFrom(String s) {
        int n;
        if (Name.hasNameSuffixNumber(s)) {
            n = Name.suffixNumber(s);
        } else n = 0;
        String nameAlone = Name.nameWithoutNumber(s);
        String newName = s;
        do {
            n++;
            newName = nameAlone + String.valueOf(n);
        } while (findNt(newName) != null);
        return newName;
    }

    void addNT(String ntName) {
        if (findNt(ntName) != null)
            return;
        Nonterminal nt = new Nonterminal(this, ntName);
        nonterminals.add(nt);
    }

    void addT(String tName) {
        if (findT(tName) != null)
            return;
        Terminal t = new Terminal(this, tName);
        terminals.add(t);
    }

    String parseNTname(String line) {
        int n = line.indexOf("->");
        if (n < 0)
            throw new RuntimeException("not found -> in grammar in line " + line);
        return line.substring(0, n).trim();
    }

    public Grammar() {

    }

    void setMinLen() {
        computeMinLen();
        minLenOK = checkMinLen();
        if (minLenOK) {
            computeNonNullableCount();
            detectCycles();
        }
    }

    public Grammar(List<String> lines) {
        for (String line : lines) {
            addNT(parseNTname(line));
        }
        for (String line : lines) {
            String ntName = parseNTname(line);
            int n = line.indexOf("->");
            String ruleString = line.substring(n + 2).trim();
            Nonterminal nt = findNt(ntName);
            Rule rule = new Rule(this, nt);
            rule.parse(ruleString);
            nt.addRule(rule);
        }
        setMinLen();
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

    public Symbol findSymbol(String name) {
        Symbol symbol = findNt(name);
        if (symbol == null)
            symbol = findT(name);
        return symbol;
    }

    public int getMinLen(Symbol symbol) {
        if (symbol.terminal)
            return 1;
        else
            return symbol.minLen;
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
                        graph.addEdge(nt.getIndex(), symbol.getIndex(), rule);
                }
        List<List<VertexEdge>> johnsonResult = JohnsonsAlgorithm.calculateCycles(graph);
        cycles = new Cycles(this, johnsonResult);
    }

    RecurCycles detectRecursion() {
        DG graph = new DG(nonterminals.size());
        for (Nonterminal nt : nonterminals)
            for (Rule rule : nt.rules)
                if (rule.startWithNonterminal()) {
                    Symbol symbol = rule.get(0);
                    graph.addEdge(nt.getIndex(), symbol.getIndex(), rule);
                }
        List<List<VertexEdge>> johnsonResult = JohnsonsAlgorithm.calculateCycles(graph);
        return new RecurCycles(this, johnsonResult);
    }

    void eliminationDirectRecursion() {
        boolean wasDirectRecursion;
        do {
            wasDirectRecursion = false;
            RecurCycles cycles = detectRecursion();
            for (RecurCycle cycle: cycles)
                if (cycle.size() == 1) {
                    eliminationDirectRecursionForNt(cycle.minOwner);
                    wasDirectRecursion = true;
                    break;
                }
        } while (wasDirectRecursion);
    }

    void eliminationIndirectRecursion() {
        boolean wasIndirectRecursion = false;
        RecurCycles cycles = detectRecursion();
        for (RecurCycle cycle: cycles) {
            if (cycle.size() < 2)
                throw new RuntimeException("must be not direct recursion");
            wasIndirectRecursion = true;
            substituteRules(cycle);
            break;
        }
        if (wasIndirectRecursion)
            eliminateUnreachedNonterminals();
    }

    private void eliminateUnreachedNonterminals() {
        List<Nonterminal> reachedList = new ArrayList<>();
        Set<Symbol> reachedSet = new HashSet<>();
        reachedList.add(nonterminals.getFirst());
        reachedSet.add(nonterminals.getFirst());
        int index = 0;
        while (index<reachedList.size()) {
            Nonterminal nt = nonterminals.get(index);
            for (Rule rule : nt.rules)
                for (Symbol symbol: rule)
                    if (!symbol.terminal) {
                        if (!reachedSet.contains(symbol)) {
                            reachedList.add((Nonterminal) symbol);
                            reachedSet.add(symbol);
                        }
                    }
            index++;
        }
        nonterminals = reachedList;
    }

    public void eliminationRecursion() {
        while (true) {
            eliminationDirectRecursion();
            eliminationIndirectRecursion();
            RecurCycles cycles = detectRecursion();
            if (cycles.isEmpty()) break;
        }
        setMinLen();
    }
}
