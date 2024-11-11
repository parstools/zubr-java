package parstools.zubr.lex.regex;

public class Regular {
    private RegexExpression root;

    public Regular(String pattern) throws Exception {
        Parser parser = new Parser(pattern);
        this.root = parser.parse();
    }

    public RegexExpression getRoot() {
        return root;
    }

    @Override
    public String toString() {
        return root.toString();
    }
}