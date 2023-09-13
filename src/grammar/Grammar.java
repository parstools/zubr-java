package grammar;

import util.NoMinLenGrammarException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Grammar {
    public List<Nonterminal> nonterminals = new ArrayList<>();
    Map<String, Integer> ntNamesToInt = new HashMap<>();
    List<String> ntNames = new ArrayList<>();
    Map<String, Integer> tNamesToInt = new HashMap<>();
    List<String> tNames = new ArrayList<>();

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
            rule.parse(ruleString);
            nt.addRule(rule);
        }
        boolean changed = true;
        while (changed) {
            changed = false;
            for (Nonterminal nt : nonterminals) {
                if (nt.computeMinLen())
                    changed = true;
            }
        }
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

    public Symbol findSymbolAddingTerminal(String name) {
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
}
