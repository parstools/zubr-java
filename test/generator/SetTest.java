package generator;

import grammar.Grammar;
import org.junit.jupiter.api.Test;
import set.Sequence;
import set.TokenSet;

import static java.lang.System.out;

public class SetTest {
    @Test
    void addSeq() {
        Grammar grammar = TestGrammars.grammar2();
        TokenSet set = new TokenSet(grammar, 3);
        Sequence eps = new Sequence(grammar, "");
        set.addSeq(eps);
        set.addSeq(new Sequence(grammar, "aa"));
        set.addSeq(new Sequence(grammar, "ab"));
        set.addSeq(new Sequence(grammar, "ac"));
        out.println(set);
    }
}
