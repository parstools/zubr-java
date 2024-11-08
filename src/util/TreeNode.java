package util;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import static java.lang.System.out;

class TreeNode {
    private String text;
    private List<TreeNode> children;
    private TreeNode parent;

    public TreeNode(String text) {
        this.text = text;
        this.children = new ArrayList<>();
    }

    boolean last() {
        if (parent == null)
            return true;
        else
            return parent.children.getLast() == this;
    }

    public void addChild(TreeNode child) {
        children.add(child);
        child.parent = this;
    }

    public TreeNode addChild(String text) {
        TreeNode child = new TreeNode(text);
        addChild(child);
        return child;
    }

    public String getText() {
        return text;
    }

    public List<TreeNode> getChildren() {
        return children;
    }

    @Override
    public String toString() {
        return text;
    }

    public void print() {
        toLines().forEach(out::println);
    }

    public List<String> toLines() {
        List<String> lines = new ArrayList<>();
        String line = "";
        Stack<Boolean> lastStack = new Stack<>();
        TreeNode ancestor = this;
        while (ancestor !=  null) {
            lastStack.push(ancestor.last());
            ancestor = ancestor.parent;
        }
        lastStack.pop();
        while (!lastStack.empty()) {
            boolean last = lastStack.pop();
            if (!last) {
                if (!lastStack.empty())
                    line = line.concat("│   ");
                else
                    line = line.concat("├── ");
            } else {
                if (!lastStack.empty())
                    line = line.concat("    ");
                else
                    line = line.concat("└── ");
            }
        }
        line = line.concat(text);
        lines.add(line);
        for (TreeNode child: children) {
            lines.addAll(child.toLines());
        }
        return lines;
    }
}
