package parstools.zubr.grammar.names;

public abstract class NameGenerator {
    public abstract void registerName(String name);
    public abstract String generate(String base);
}
