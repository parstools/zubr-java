package parstools.zubr.util;

import org.junit.jupiter.api.Test;
import parstools.zubr.grammar.Grammar;
import parstools.zubr.grammar.TestGrammars;
import parstools.zubr.lr.StatesLR0;
import parstools.zubr.lr.Transitions;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class TestObject extends ZObject implements Cloneable {
    TestObject parent;
    TestObject child;
    TestObject(String str, int i32, long i64) {
        this.str = str;
        this.i32 = i32;
        this.i64 = i64;
    }

    @Override
    public TestObject clone() throws CloneNotSupportedException {
        return (TestObject) super.clone();
    }

    void addChild(TestObject child) {
        this.child = child;
        child.parent = this;
    }
    String str;
    int i32;
    long i64;

    @Override
    protected byte[] getBytes() {
        byte[] stringBytes = str.getBytes(StandardCharsets.UTF_8);
        ByteBuffer buffer = ByteBuffer.allocate(stringBytes.length + INT_SIZE + LONG_SIZE);
        buffer.put(stringBytes);
        buffer.putInt(i32);
        buffer.putLong(i64);
        return buffer.array();
    }

    @Override
    protected long deepHash(long seed) {
        long h = shallowHash(seed);
        if (child != null)
            h = child.deepHash(h);
        return h;
    }
}

public class HashTest {
    @Test
    void simpleChildParent() throws CloneNotSupportedException {
        TestObject parent = new TestObject("aaa", 123,467);
        TestObject child = new TestObject("bbb", 1000,2000);
        TestObject grandchild = parent.clone();
        long a0 = parent.computeHash();
        long a1 = child.computeHash();
        long a2 = grandchild.computeHash();
        child.addChild(grandchild);
        parent.addChild(child);
        long c0 = parent.computeHash();
        long c1 = child.computeHash();
        long c2 = grandchild.computeHash();
        assertNotEquals(a0, c0);
        assertNotEquals(a1, c1);
        assertEquals(a2, c2); //grandChild has not childs
    }

    @Test
    void cachedRuleHash() {
        Grammar g = TestGrammars.LRwikiLR0();
        StatesLR0 states = new StatesLR0(g);
        Transitions transitions = new Transitions();
        states.createStates(transitions);
        long first = states.getFirst().computeHash();
        long second = states.getFirst().computeHash();
        assertEquals(first, second);
    }
}
