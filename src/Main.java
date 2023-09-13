import grammar.Grammar;
import set.SetContainer;

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
        while (n<lines.size()) {
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
            n = readExpect1Lines(lines, n, expectLines);
            SetContainer sc2 = new SetContainer(grammar);
            sc2.readTest1(expectLines);
            if (sc1.hashCode()==sc2.hashCode()) {
                out.println("OK");
                for (String line: gramLines)
                    printWriter.println(line);
                int limit = 2000;
                for (int k=1; k<=4; k++) {
                    printWriter.println("==="+k);
                    SetContainer sc = new SetContainer(grammar);
                    sc.reset(k);
                    sc.computeSetsByRangeGeneration(k, 4, 20, limit);
                    sc.dump(printWriter);
                    limit *=2;
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
        out.println("failed "+counterFailed);
        long duration = System.nanoTime()-start;
        out.println("duration="+duration/1e9);
    }

    private static int readExpect1Lines(List<String> lines, int n, List<String> expectLines) {
        while(n<lines.size()) {
            String line = lines.get(n).trim();
            if (line.isEmpty()) break;
            expectLines.add(line);
            n++;
        }
        return n;
    }

    private static int readGramLines(List<String> lines, int n, List<String> gramLines) {
        while(n<lines.size()) {
            String line = lines.get(n).trim();
            if (line.isEmpty() || line.equals("---")) break;
            n++;
            gramLines.add(line);
        }
        return n;
    }

    public static void main(String[] args) {
        Path path = Paths.get("res/test1.dat");
        try {
            List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
             makeKTest(lines);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}