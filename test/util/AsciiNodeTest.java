package util;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AsciiNodeTest {
    @Test
    void toLines() {
        AsciiNode root = new AsciiNode("a");
        AsciiNode node0 = root.addChild("123");
        AsciiNode node1 = root.addChild("b");
        AsciiNode leaf00 = node0.addChild("eee");
        AsciiNode leaf01 = node0.addChild("www");
        AsciiNode leaf02 = node0.addChild("34");
        AsciiNode leaf10 = node1.addChild("ff");
        List<String> lines = root.toLines();
        assertEquals(lines.get(0), "a");
        assertEquals(lines.get(1), "├── 123");
        assertEquals(lines.get(2), "│   ├── eee");
        assertEquals(lines.get(3), "│   ├── www");
        assertEquals(lines.get(4), "│   └── 34");
        assertEquals(lines.get(5), "└── b");
        assertEquals(lines.get(6), "    └── ff");
    }
}
