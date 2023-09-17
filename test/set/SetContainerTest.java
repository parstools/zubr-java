package set;

import grammar.Grammar;
import grammar.TestGrammars;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static java.lang.System.out;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SetContainerTest {
    @Test
    void FirstFollow1() {
        Grammar g = TestGrammars.testFirstFollow();
        SetContainer sc = new SetContainer(g);
        sc.reset(1);
        sc.makeFirstSets1();
        assertEquals("{( i}", sc.firstSetForIndex(0).toString());
        assertEquals("{eps +}", sc.firstSetForIndex(1).toString());
        assertEquals("{( i}", sc.firstSetForIndex(2).toString());
        assertEquals("{eps *}", sc.firstSetForIndex(3).toString());
        assertEquals("{( i}", sc.firstSetForIndex(4).toString());

        sc.makeFollowSets1();
        assertEquals("{$ )}", sc.followSetForIndex(0).toString());
        assertEquals("{$ )}", sc.followSetForIndex(1).toString());
        assertEquals("{$ + )}", sc.followSetForIndex(2).toString());
        assertEquals("{$ + )}", sc.followSetForIndex(3).toString());
        assertEquals("{$ + * )}", sc.followSetForIndex(4).toString());
    }

    @Test
    void FirstFollowK_2() {
        Grammar g = TestGrammars.testFirstFollow();
        SetContainer sc = new SetContainer(g);
        sc.reset(2);
        sc.makeFirstSetsK(2);
        assertEquals("{i (( (i i+ i*}", sc.firstSetForIndex(0).toString());
        assertEquals("{eps +( +i}", sc.firstSetForIndex(1).toString());
        assertEquals("{i (( (i i*}", sc.firstSetForIndex(2).toString());
        assertEquals("{eps *( *i}", sc.firstSetForIndex(3).toString());
        assertEquals("{i (( (i}", sc.firstSetForIndex(4).toString());

        /*sc.makeFollowSetsK();
        assertEquals("{$ )$ )+ )* ))}", sc.followSetForIndex(0).toString());
        assertEquals("{$ )$ )+ )* ))}", sc.followSetForIndex(1).toString());
        assertEquals("{$ +( +i )$ )+ )* ))}", sc.followSetForIndex(2).toString());
        assertEquals("{$ +( +i )$ )+ )* ))}", sc.followSetForIndex(3).toString());
        assertEquals("{$ +( +i *( *i )$ )+ )* ))}", sc.followSetForIndex(4).toString());*/
    }

    @Disabled("only for generate data to tests")
    @Test
    void FirstFollowK_2_gen() {
        Grammar g = TestGrammars.testFirstFollow();
        SetContainer sc1 = new SetContainer(g);
        sc1.reset(2);
        sc1.computeSetsByGeneration(2, 20, 100000);
        for (int i=0;i<=4; i++)
            out.println("assertEquals(\"" + sc1.firstSetForIndex(i).toString() + "\", sc.firstSetForIndex("+i+").toString());");
        for (int i=0;i<=4; i++)
            out.println("assertEquals(\"" + sc1.followSetForIndex(i).toString() + "\", sc.followSetForIndex("+i+").toString());");
    }
}
