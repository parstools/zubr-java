package parstools.zubr.lex.regex;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RegularTest {
    @Test
    void parsing() throws Exception {
        String pattern = "(a|b)*ab+bc?";
        Regular reg = new Regular(pattern);
        assertEquals(pattern, reg.toString());
    }
}
