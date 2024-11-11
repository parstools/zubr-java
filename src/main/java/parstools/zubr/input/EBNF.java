package parstools.zubr.input;

import parstools.zubr.lex.regex.*;

import java.util.ArrayList;
import java.util.List;

public class EBNF {
    boolean preferRightRecursion;
    List<String> lines = new ArrayList<>();
    List<String> newLines;

    EBNF(boolean preferRightRecursion) {
        this.preferRightRecursion = preferRightRecursion;
    }

    public String[] toLines() {
        return newLines.toArray(new String[0]);
    }

    public void add(String row) {
        lines.add(row);
    }

    public void convert()  {
        newLines = new ArrayList<>();
        for (String line: lines) {
            String nt = line.substring(0,1);
            String arrow = line.substring(1,3);
            if (!arrow.equals("->"))
                throw new RuntimeException("must be ->");
            String regStr = line.substring(3);
            Regular reg = new Regular(regStr);
            RegexExpression root = reg.getRoot();
            if (root instanceof Alternation) {
                convAlternation(nt, (Alternation)reg.getRoot());
            } else if (root instanceof QuantifierExpression) {
                convQuantifierExpression(nt, (QuantifierExpression)reg.getRoot());
            }
            else throw new RuntimeException("I can't convert "+line);
        }
    }

    private void convQuantifierExpression(String nt, QuantifierExpression quantExpr) {
        Quantifier quant = quantExpr.getQuantifier();
        if (quant == Quantifier.ZERO_OR_ONE) {
            newLines.add(nt + "->" + quantExpr.getExpression());
            newLines.add(nt + "->");
        } else {
            if (preferRightRecursion) {
                newLines.add(nt + "->" + quantExpr.getExpression() + nt);
            } else {
                newLines.add(nt + "->" + nt + quantExpr.getExpression());
            }
            if (quant == Quantifier.ONE_OR_MORE)
                newLines.add(nt + "->" + quantExpr.getExpression());
            else
                newLines.add(nt + "->");
        }
    }

    private void convAlternation(String nt, Alternation alts) {
        for (RegexExpression alt: alts)
            newLines.add(nt + "->" + alt);
    }
}
