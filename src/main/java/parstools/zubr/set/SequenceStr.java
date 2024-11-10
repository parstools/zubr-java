package parstools.zubr.set;

import java.util.ArrayList;

public class SequenceStr extends ArrayList<String> {
    public SequenceStr(Sequence seq) {
        for (int i: seq)
            add(seq.grammar.terminals.get(i).name);
    }

    public String toString() {
        if (isEmpty())
            return "eps";
        else {
            StringBuilder sb = new StringBuilder();
            for (String s : this)
                sb.append(s);
            return sb.toString();
        }
    }
}
