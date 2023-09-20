import generator.Generator;
import generator.RuleOrder;
import grammar.Grammar;
import set.SetContainer;
import set.TokenSet;
import util.NoMinLenGrammarException;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static java.lang.System.getenv;
import static java.lang.System.out;

public class Main {
    static void makeKTest(List<String> lines) throws IOException {
        int n = 0;
        int counter = 0;
        int counterFailed = 0;
        long start = System.nanoTime();
        FileWriter fileWriter = new FileWriter("k4.dat");
        PrintWriter printWriter = new PrintWriter(fileWriter);
        while (n < lines.size()) {
            List<String> gramLines = new ArrayList<>();
            n = readGramLines(lines, n, gramLines);
            Grammar grammar = new Grammar(gramLines);
            SetContainer sc1 = new SetContainer(grammar);
            out.println(counter);
            sc1.reset(1);
            sc1.computeSetsByGeneration(1, 20, 1000);
            counter++;
            n++;
            List<String> expectLines = new ArrayList<>();
            n = readExpectLines(lines, n, expectLines);
            SetContainer sc2 = new SetContainer(grammar);
            sc2.readTest1(expectLines);
            if (sc1.hashCode() == sc2.hashCode()) {
                out.println("OK");
                for (String line : gramLines)
                    printWriter.println(line);
                int limit = 2000;
                for (int k = 1; k <= 4; k++) {
                    printWriter.println("===" + k);
                    SetContainer sc = new SetContainer(grammar);
                    sc.reset(k);
                    sc.computeSetsByRangeGeneration(k, 4, 20, limit);
                    sc.dump(printWriter);
                    limit *= 2;
                }
                printWriter.println();
            } else {
                counterFailed++;
                out.println(sc1);
                out.println(sc2);
                out.println("differ..");
            }
            n++;
        }
        printWriter.close();
        out.println(counter);
        out.println("failed " + counterFailed);
        long duration = System.nanoTime() - start;
        out.println("duration=" + duration / 1e9);
    }

    static void nextCounter(List<String> lines) {
        int n = 0;
        int count = 0;
        int countNoMin = 0;
        long start = System.nanoTime();
        while (n < lines.size()) {
            List<String> gramLines = new ArrayList<>();
            n = readGramLines(lines, n, gramLines);
            try {
                Grammar grammar = new Grammar(gramLines);
                int limit = 1000 * 1000;
                gramLines.forEach(out::println);
                out.println("---");
                for (int maxLen = 1; maxLen <= 64; maxLen++) {
                    Generator generator = new Generator(grammar, maxLen, RuleOrder.roSort);
                    int nc = 0;
                    while (generator.next()) {
                        nc++;
                        if (nc >= limit)
                            break;
                    }
                    out.println(maxLen + ": " + nc);
                    if (nc == limit)
                        break;
                }
                out.println();
            } catch (NoMinLenGrammarException e) {
                countNoMin++;
            }
            n++;
            count++;
            List<String> expectLines = new ArrayList<>();
            n = readExpectLines(lines, n, expectLines);
            n++;
        }
        out.println(count + " grammars, " + countNoMin + " exceptions");
        long duration = System.nanoTime() - start;
        out.println("duration=" + duration / 1e9);
    }

    static void testK4grammars() throws IOException {
        Path path = Paths.get("res/k4.dat");
        List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
        int n = 0;
        int count = 0;
        while (n < lines.size()) {
            List<String> gramLines = new ArrayList<>();
            n = readGramLines(lines, n, gramLines);
            Grammar grammar = new Grammar(gramLines);
            count++;
            List<String> expectLines = new ArrayList<>();
            n = readExpectLines(lines, n, expectLines);
            testK4grammar(grammar, expectLines);
            n++;
        }
        out.println(count + " grammars");
    }

    private static void testK4grammar(Grammar grammar, List<String> expectLines) {
        int n = 0;
        while ((n = testK(grammar, n, expectLines)) < expectLines.size()) {
        }
    }

    private static int testK(Grammar grammar, int n, List<String> lines) {
        int failed = 0;
        String line = lines.get(n).trim();
        assert (line.startsWith("==="));
        int k = Integer.valueOf(line.substring(3,line.length()));
        SetContainer sc = new SetContainer(grammar);
        sc.reset(k);
        sc.makeFirstSetsK(k);
        sc.makeFollowSetsK(k);
        n++;
        line = lines.get(n).trim();
        assert (line.equals("FIRST:"));
        n++;
        for (int i = 0; i < sc.firstSets.size(); i++) {
            line = lines.get(n).trim();
            if (!compareSetWithLine(grammar, k, sc.firstSets.get(i), line))
                failed++;
            n++;
        }
        line = lines.get(n).trim();
        assert (line.equals("FOLLOW:"));
        n++;
        for (int i = 0; i < sc.followSets.size(); i++) {
            line = lines.get(n).trim();
            if (!compareSetWithLine(grammar, k, sc.followSets.get(i), line))
                failed++;
            n++;
        }
        return n;
    }

    private static boolean compareSetWithLine(Grammar grammar, int k, TokenSet tokenSet, String line) {
        int pos = line.indexOf("{");
        String tokenStr = line.substring(pos, line.length());
        TokenSet expectSet = new TokenSet(grammar, k);
        expectSet.parse(tokenStr);
        boolean eq = expectSet.toString().equals(tokenSet.toString());
        if (!eq) {
            out.println("differ "+expectSet.toString() + " " + tokenSet.toString());
        }
        return eq;
    }

    static void detectCycles(List<String> lines) {
        int n = 0;
        int count = 0;
        int countNoMin = 0;
        while (n < lines.size()) {
            List<String> gramLines = new ArrayList<>();
            n = readGramLines(lines, n, gramLines);
            try {
                Grammar grammar = new Grammar(gramLines);
                gramLines.forEach(out::println);
                Generator generator = new Generator(grammar, 5, RuleOrder.roOriginal);
                int limit = 100;
                int nc = 0;
                while (generator.next()) {
                    nc++;
                    out.println(generator.string() + "  " + generator.parenString());
                    if (nc >= limit)
                        break;
                }
                out.println(nc);
                out.println("===============");
            } catch (NoMinLenGrammarException e) {
                countNoMin++;
            }
            n++;
            count++;
            List<String> expectLines = new ArrayList<>();
            n = readExpectLines(lines, n, expectLines);
            n++;
        }
        out.println(count + " grammars, " + countNoMin + " exceptions");
    }

    static void testCount(List<String> lines) {
        int n = 0;
        int count = 0;
        int countFailed = 0;
        long start = System.nanoTime();
        while (n < lines.size()) {
            List<String> gramLines = new ArrayList<>();
            n = readGramLines(lines, n, gramLines);
            n++;
            count++;
            out.println(count);
            for (String line : gramLines)
                out.println(line);
            List<String> expectLines = new ArrayList<>();
            n = readExpectLines(lines, n, expectLines);
            n++;
            Grammar grammar = new Grammar(gramLines);
            int limit = 1000 * 1000;
            int limit2 = 1000 * 1000;
            for (int i = 0; i < expectLines.size(); i++) {
                String[] parts = expectLines.get(i).split(": ");
                int maxLen = Integer.parseInt(parts[0]);
                int expectedCount = Integer.parseInt(parts[1]);
                if (expectedCount > limit2)
                    break;
                Generator generator = new Generator(grammar, maxLen, RuleOrder.roShuffle);
                int nc = 0;
                while (generator.next()) {
                    nc++;
                    if (nc >= limit)
                        break;
                }
                if (nc != expectedCount) {
                    out.println("differ maxLen=" + maxLen + " expected=" + expectedCount + " found " + nc);
                    countFailed++;
                }
            }
        }
        out.println(count + " grammars, " + countFailed + " differ count");
        long duration = System.nanoTime() - start;
        out.println("duration=" + duration / 1e9);
    }

    static void testBad(List<String> lines) {
        int n = 0;
        int count = 0;
        int countNoMin = 0;
        while (n < lines.size()) {
            List<String> gramLines = new ArrayList<>();
            n = readGramLines(lines, n, gramLines);
            try {
                Grammar grammar = new Grammar(gramLines);
                out.println(count);
            } catch (NoMinLenGrammarException e) {
                countNoMin++;
            }
            n++;
            count++;
            List<String> expectLines = new ArrayList<>();
            n = readExpectLines(lines, n, expectLines);
            n++;
        }
        out.println(count + " grammars, " + countNoMin + " exceptions");
    }

    private static int readExpectLines(List<String> lines, int n, List<String> expectLines) {
        while (n < lines.size()) {
            String line = lines.get(n).trim();
            if (line.isEmpty()) break;
            expectLines.add(line);
            n++;
        }
        return n;
    }

    private static int readGramLines(List<String> lines, int n, List<String> gramLines) {
        while (n < lines.size()) {
            String line = lines.get(n).trim();
            if (!line.isEmpty() && line.charAt(0) == ';') {
                n++;
                continue;
            }
            if (line.isEmpty() || line.equals("---") || line.startsWith("===")) break;
            gramLines.add(line);
            n++;
        }
        return n;
    }

    public static void main(String[] args) {
        try {
            testK4grammars();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}