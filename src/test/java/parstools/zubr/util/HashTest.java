package parstools.zubr.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HashTest {
    @Test
    void permutations() {
        Hash h0 = new Hash();
        h0.xor(1);
        h0.xor(2);
        h0.xor(3);
        Hash h1 = new Hash();
        h1.xor(2);
        h1.xor(3);
        h1.xor(1);
        assertEquals(h0.hash(), h1.hash());
    }
}
