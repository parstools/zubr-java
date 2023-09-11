import grammar.Grammar;
import set.SetContainer;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static java.lang.System.out;

public class Main {
    static void testFF1(List<String> lines) {
        int n = 0;
        int counter = 0;
        while (n<lines.size()) {
            List<String> gramLines = new ArrayList<>();
            n = readGramLines(lines, n, gramLines);
            Grammar grammar = new Grammar(gramLines);
            SetContainer sc = new SetContainer(grammar);
            sc.firstSetsByGeneration(1);
            counter++;
            n++;
            n = readExpect1Lines(lines, n);
            n++;
        }
        out.println(counter);
    }

    private static int readExpect1Lines(List<String> lines, int n) {
        while(n<lines.size()) {
            String line = lines.get(n).trim();
            if (line.isEmpty()) break;
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
            lines.stream().forEach(out::println);
            testFF1(lines);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}