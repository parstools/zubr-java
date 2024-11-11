package parstools.zubr.lex.regex;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RegularTest {
    @Test
    void parsing() throws Exception {
        String[] patterns =  {"(a|b)*abb", "(a|b)*ab+bc?", "(ab)|c","a|(bc)"};
        for (String pattern: patterns) {
            Regular reg = new Regular(pattern);
            assertEquals(pattern, reg.toString());
        }
    }
}
