package util;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TreeNodeTest {
    @Test
    void toLines() {
        TreeNode root = new TreeNode("a");
        TreeNode node0 = root.addChild("123");
        TreeNode node1 = root.addChild("b");
        TreeNode leaf00 = node0.addChild("eee");
        TreeNode leaf01 = node0.addChild("www");
        TreeNode leaf02 = node0.addChild("34");
        TreeNode leaf10 = node1.addChild("ff");
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
