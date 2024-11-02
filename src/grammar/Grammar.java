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

    boolean minLenOK = false;
    public boolean grammarOK() {
        return minLenOK;
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
            rule.add(new Symbol(this, false, newNt.getIndex()));
            nt.addRule(rule);
        }
        for (Rule rule: recursiveRules) {
            rule.remove(0);
            assert (!rule.isEmpty());
            rule.owner = newNt;
            rule.add(new Symbol(this, false, newNt.getIndex()));
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
                    if (newRule.size()!=1 || newRule.get(0).terminal || newRule.get(0).index != owner.getIndex()) {
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

    private Nonterminal insertNonterminal(int sourceIndex) {
        Nonterminal newNt = new Nonterminal(this);
        newNt.rules = new ArrayList<>();
        newNt.setIndex(sourceIndex + 1);
        insertName(sourceIndex);
        updateNtIndices(newNt.getIndex());
        updateRuleSymbolsForInsert(newNt.getIndex());
        nonterminals.add(newNt.getIndex(), newNt);
        ntNamesToInt.put(ntNames.get(newNt.getIndex()), newNt.getIndex());
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
        int n;
        if (Name.hasNameSuffixNumber(s)) {
            n = Name.suffixNumber(s);
        } else n = 0;
        String nameAlone = Name.nameWithoutNumber(s);
        String newName = s;
        do {
            n++;
            newName = nameAlone + String.valueOf(n);
        } while (ntNamesToInt.containsKey(newName));
        return newName;
    }

    private void updateNtIndices(int index) {
        for (Nonterminal nt : nonterminals)
            if (nt.getIndex() >= index)
                nt.incIndex();
    }

    private void updateRuleSymbolsForInsert(int index) {
        for (Nonterminal nt : nonterminals) {
            for (Rule rule : nt.rules) {
                for (Symbol symbol : rule)
                    symbol.updateNtFrom(index);
            }
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
        nt.setIndex(n);
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
            int ntIndex = ntNamesToInt.get(ntName);
            int n = line.indexOf("->");
            String ruleString = line.substring(n + 2).trim();
            Nonterminal nt = nonterminals.get(ntIndex);
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
                        graph.addEdge(nt.getIndex(), symbol.index, rule);
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
                    graph.addEdge(nt.getIndex(), symbol.index, rule);
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
                    eliminationDirectRecursionForNt(cycle.minOwner.getIndex());
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
        return;
        /*List<Integer> reachedList = new ArrayList<>();
        Set<Integer> reachedSet = new HashSet<>();
        reachedList.add(0);
        reachedSet.add(0);
        int index = 0;
        while (index<reachedList.size()) {
            Nonterminal nt = nonterminals.get(index);
            for (Rule rule : nt.rules)
                for (Symbol symbol: rule)
                    if (!symbol.terminal) {
                        if (!reachedSet.contains(symbol.index)) {
                            reachedList.add(symbol.index);
                            reachedSet.add(symbol.index);
                        }
                    }
            index++;
        }
        List<Nonterminal> nonterminals_new = new ArrayList<>();
        List<String> ntNames_new = new ArrayList<>();
        Map<String, Integer> ntNamesToInt_new = new HashMap<>();
        Map<Integer,Integer> ruleMap = new HashMap<>();
        for (Nonterminal nt : nonterminals) {
            if (reachedSet.contains(nt.index)) {
                int n = nonterminals_new.size();
                String newName = ntNames.get(n);
                ntNames_new.add(newName);
                ntNamesToInt_new.put(newName, n);
                ruleMap.put(nt.index,n);
                nt.index = n;
                nonterminals_new.add(nt);
            }
        }
        nonterminals = nonterminals_new;
        ntNames = ntNames_new;
        ntNamesToInt = ntNamesToInt_new;
        correctRulesIds(ruleMap);*/
    }

    private void correctRulesIds(Map<Integer, Integer> ruleMap) {
        for (Nonterminal nt : nonterminals) {
            for (Rule rule : nt.rules)
                for (Symbol symbol : rule)
                    if (!symbol.terminal)
                        symbol.index = ruleMap.get(symbol.index);
        }
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
