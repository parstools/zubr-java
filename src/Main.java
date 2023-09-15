import generator.Generator;
import generator.RuleOrder;
import grammar.Grammar;
import set.SetContainer;
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
                int limit = 100 * 1000;
                for (String line : gramLines)
                    out.println(line);
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
            int limit = 100 * 1000;
            int limit2 = 100 * 1000;
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
            if (line.isEmpty() || line.equals("---")) break;
            gramLines.add(line);
            n++;
        }
        return n;
    }

    public static void main(String[] args) {
        Path path = Paths.get("res/to100K.txt");
        try {
            List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
            testCount(lines);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}