package parstools.zubr.regex;

import java.util.HashSet;
import java.util.Set;

public class Regular {
    final private RegexExpression root;
    private Set<String> literalSet = new HashSet<>();

    public Set<String> literals() {
        return literalSet;
    }

    public Regular(String pattern) throws RuntimeException {
        Parser parser = new Parser(pattern);
        this.root = parser.parse();
        root.addLiteralsToSet(literalSet);
    }

    public RegexExpression getRoot() {
        return root;
    }

    @Override
    public String toString() {
        return root.toString();
    }
}