package parstools.zubr.input;

import parstools.zubr.lex.regex.Regular;

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
            Regular reg = new Regular(line);
        }
    }
}
