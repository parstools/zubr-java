package grammar;

import generator.Generator;
import generator.RuleOrder;
import org.junit.jupiter.api.Test;

import static java.lang.System.out;

public class GenerateTest {

    @Test
    void random () {
        Grammar g = new Grammar();
        int cnt = 0, i = 0;
        while (true) {
            g.createRandom(i);
            i++;
            g.toLines().forEach(System.out::println);
            System.out.println();
            Generator generator = new Generator(g, 30, RuleOrder.roOriginal);
            int limit = 100;
            int counter = 0;
            while (generator.next()) {
                counter++;
                if (counter >= limit)
                    break;
            }
            if (counter==limit) {
//                g.toLines().forEach(System.out::println);
//                System.out.println();
                cnt++;
                if (cnt >= 1000)
                    break;
            }
        }
    }
}
