//DEPRECATED!!
package parstools.zubr.input;

import parstools.zubr.lex.regex.*;

import java.util.*;

public class EBNF {
    boolean preferRightRecursion;
    List<String> lines = new ArrayList<>();
    Set<Character> availableNtNames = new TreeSet<>();
    List<String> newLines;

    EBNF(boolean preferRightRecursion) {
        this.preferRightRecursion = preferRightRecursion;
        for (char c = 'A'; c<='Z'; c++)
            availableNtNames.add(c);
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
            Character nt = line.charAt(0);
            availableNtNames.remove(nt);
            String arrow = line.substring(1,3);
            if (!arrow.equals("->"))
                throw new RuntimeException("must be ->");
            String regStr = line.substring(3);
            Regular reg = new Regular(regStr);
            RegexExpression root = reg.getRoot();
            if (root instanceof Alternation) {
                convertAlternation(nt, (Alternation)reg.getRoot());
            } else if (root instanceof QuantifierExpression) {
                convertQuantifierExpression(nt, (QuantifierExpression)reg.getRoot());
            } else if (root instanceof Concatenation) {
                convertConcatenation(nt, (Concatenation)reg.getRoot());
            }
            else throw new RuntimeException("I can't convert "+line);
        }
    }

    private void convertConcatenation(Character nt, Concatenation concat) {
        int numStar = 0;
        int numPlus = 0;
        int numQuestions = 0;
        for (RegexExpression part : concat.getExpressions()) {
            if (part instanceof QuantifierExpression) {
                QuantifierExpression qExpr = (QuantifierExpression) part;
                qExpr.toString();
                //here stop...
                switch (qExpr.getQuantifier()) {
                    case ZERO_OR_MORE -> numStar++;
                    case ONE_OR_MORE -> numPlus++;
                    case ZERO_OR_ONE -> numQuestions++;
                }
            }
        }
        if (numStar + numPlus + numQuestions == 0) {
            newLines.add(nt + "->" + concat);
            return;
        }
        boolean expandQuest = numQuestions > 1;
        StringBuilder sb = new StringBuilder();
        for (RegexExpression part : concat.getExpressions()) {
            if (part instanceof QuantifierExpression)
                ;
            else
                sb.append(part.toString());
        }
        newLines.add(sb.toString());
    }

    private void convertQuantifierExpression(Character nt, QuantifierExpression quantExpr) {
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

    private void convertAlternation(Character nt, Alternation alts) {
        for (RegexExpression alt: alts)
            newLines.add(nt + "->" + alt);
    }
}
