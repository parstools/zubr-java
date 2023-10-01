package util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class NameTest {
    @Test
    void hasNameSuffixNumber() {
        assertFalse(Name.hasNameSuffixNumber("abc"));
        assertTrue(Name.hasNameSuffixNumber("abc0"));
        assertTrue(Name.hasNameSuffixNumber("abc1"));
        assertFalse(Name.hasNameSuffixNumber("abc1a"));
        assertTrue(Name.hasNameSuffixNumber("abc12"));
    }

    @Test
    void suffixNumber() {
    }

    {
        assertEquals(-1, Name.suffixNumber("abc"));
        assertEquals(0, Name.suffixNumber("abc0"));
        assertEquals(1, Name.suffixNumber("abc1"));
        assertEquals(-1, Name.suffixNumber("abc1a"));
        assertEquals(12, Name.suffixNumber("abc12"));
    }

    @Test
    void nameWithoutNumber() {
        assertEquals("abc", Name.nameWithoutNumber("abc"));
        assertEquals("abc", Name.nameWithoutNumber("abc0"));
        assertEquals("abc", Name.nameWithoutNumber("abc1"));
        assertEquals("abc1a", Name.nameWithoutNumber("abc1a"));
        assertEquals("abc", Name.nameWithoutNumber("abc12"));
    }
}
