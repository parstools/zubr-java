package grammar;

import org.junit.jupiter.api.Test;

import java.util.List;

import static java.lang.System.out;


public class FactorizationTest {
    @Test
    void factorization() {
        Grammar g = TestGrammars.factor();
        g.factorization(1);
        List<String> lines1 = g.toLines();
    }
}
