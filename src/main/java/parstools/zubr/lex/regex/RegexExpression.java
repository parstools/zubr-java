package parstools.zubr.lex.regex;

import java.util.Set;

public abstract class RegexExpression {
    abstract void addLiteralsToSet(Set<String> literalSet);
}
