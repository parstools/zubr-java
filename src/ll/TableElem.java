package ll;

import java.util.ArrayList;
import java.util.List;

public class TableElem {
    List<Integer> alts;
    TableMap subMap;

    TableElem(int ruleIndex) {
        this.alts = new ArrayList<>();
        alts.add(ruleIndex);
    }
}
