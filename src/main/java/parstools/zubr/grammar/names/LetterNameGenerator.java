package parstools.zubr.grammar.names;

import java.util.Set;
import java.util.TreeSet;

public class LetterNameGenerator extends NameGenerator {
    Set<String> availableNtNames = new TreeSet<>();

    LetterNameGenerator(){
        for (char c = 'A'; c<='Z'; c++)
            availableNtNames.add("" + c);
    }

    public void registerName(String name) {
        availableNtNames.remove(name);
    }

    public String generate(String base) {
        String result = availableNtNames.iterator().next();
        availableNtNames.remove(result);
        return result;
    }

}
