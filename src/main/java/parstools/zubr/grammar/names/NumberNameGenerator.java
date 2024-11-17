package parstools.zubr.grammar.names;

import parstools.zubr.util.Name;

import java.util.HashSet;
import java.util.Set;

public class NumberNameGenerator extends NameGenerator {
    Set<String> usedNames = new HashSet<>();
    public void registerName(String name) {
        usedNames.add(name);
    }

    public String generate(String s) {
        int n;
        if (Name.hasNameSuffixNumber(s)) {
            n = Name.suffixNumber(s);
        } else n = 0;
        String nameAlone = Name.nameWithoutNumber(s);
        String newName = s;
        do {
            n++;
            newName = nameAlone + String.valueOf(n);
        } while (usedNames.contains(newName));
        registerName(newName);
        return newName;
    }
}
