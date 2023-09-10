package generator;

import grammar.Nonterminal;
import grammar.Rule;

import java.util.*;

import static java.lang.System.out;

public class NTInfo {
    Nonterminal nonterminal;
    List<RuleInfo> ruleInfos = new ArrayList<>();

    int minLen = 0;

    boolean computeMinLen() {
        int old = minLen;
        boolean changed = false;
        for (RuleInfo ruleInfo : ruleInfos) {
            if (ruleInfo.computeMinLen())
                changed = true;
        }
        minLen = Integer.MAX_VALUE;
        for (RuleInfo ruleInfo : ruleInfos) {
            minLen = Math.min(minLen, ruleInfo.minLen);
        }
        return minLen != old || changed;
    }

    Generator generator;

    void sortRules() {
        Collections.sort(ruleInfos, new Comparator<RuleInfo>() {
            @Override
            public int compare(RuleInfo r1, RuleInfo r2) {
                if (r1.hasNt && !r2.hasNt)
                    return 1;
                else if (!r1.hasNt && r2.hasNt)
                    return -1;
                else if (r1.minLen < r2.minLen)
                    return -1;
                else if (r1.minLen > r2.minLen)
                    return 1;
                else if (r1.originIndex < r2.originIndex)
                    return -1;
                else if (r1.originIndex > r2.originIndex)
                    return 1;
                else
                    return 0;
            }
        });
    }

    NTInfo(Generator generator, Nonterminal nonterminal) {
        this.generator = generator;
        this.nonterminal = nonterminal;
        for (int i = 0; i < nonterminal.rules.size(); i++) {
            Rule rule = nonterminal.rules.get(i);
            RuleInfo ruleInfo = new RuleInfo(generator, rule);
            ruleInfo.originIndex = i;
            ruleInfos.add(ruleInfo);
        }
    }
}
