package parstools.zubr.lr;

import java.util.HashMap;
import java.util.Map;

public class RowLR {
    Map<Integer, Action> action = new HashMap<>();
    Map<Integer, Integer> goto_ = new HashMap<>();

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        sb.append("]");
        return sb.toString();
    }
}
