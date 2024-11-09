package grammar;

import generator.Generator;
import generator.RuleOrder;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

import static java.lang.System.out;

public class GenerateTest {

    @Disabled
    @Test
    void random () throws FileNotFoundException {
        Grammar g = new Grammar();
        int cnt = 0, i = 0;
        PrintWriter writer = new PrintWriter("generated100k.dat");
        while (true) {
            g.createRandom(i);
            i++;
            Generator generator = new Generator(g, 20, RuleOrder.roOriginal);
            int limit = 100;
            int counter = 0;
            while (generator.next()) {
                counter++;
                if (counter >= limit)
                    break;
            }
            if (counter==limit) {
                System.out.printf("%d %d%n",cnt,i);
                writer.printf(";%d%n",cnt);
                g.toLines().forEach(writer::println);
                writer.println();
                if (cnt % 100 == 0)
                    writer.flush();
                cnt++;
                if (cnt >= 100000)
                    break;
            }
        }
        writer.close();
    }
}
