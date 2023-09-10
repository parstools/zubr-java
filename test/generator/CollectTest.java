package generator;

import grammar.Grammar;
import grammar.Symbol;
import set.Sequence;
import set.SequenceSet;
import set.Set;
import org.junit.jupiter.api.Test;

import static java.lang.System.out;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CollectTest {
    @Test
    void constructed() {
        Grammar grammar = TestGrammars.grammar2();
        Generator generator = new Generator(grammar, 5);
        generator.createFromString("S(aS()S(b))");
        SequenceSet sset = new SequenceSet(grammar);
        generator.root.collectFirst(0,1, sset);
        out.println(sset);
    }
    @Test
    void grammar2Collect() {
        Set firstS,firstA;
        Set followS, followA;
        Generator generator = new Generator(TestGrammars.grammar2(), 5);
        Symbol symbol = generator.getNT(0);
        Node node = new Node(generator, symbol);
        node.first(5);
        assertEquals("",node.string());
        assertEquals("S()",node.parenString());
        for (int k=1; k<3; k++) {
            firstS = generator.collectFirst(0, k);
            firstA = generator.collectFirst(1, k);
            followS = generator.collectFollow(0, k);
            followA = generator.collectFollow(1, k);
            assertEquals("eps", firstS.toString());
            assertEquals("", firstA.toString());
            assertEquals("$", followS.toString());
            assertEquals("", followA.toString());
        }
        assertTrue(node.next(5));
        assertEquals("ac",node.string());
        assertEquals("S(aS()A(c))",node.parenString());

        firstS = generator.collectFirst(0, 1);
        firstA = generator.collectFirst(1, 1);
        followS = generator.collectFollow(0, 1);
        followA = generator.collectFollow(1, 1);
        assertEquals("eps,a", firstS.toString());
        assertEquals("c", firstA.toString());
        assertEquals("$,c", followS.toString());
        assertEquals("$", followA.toString());

        firstS = generator.collectFirst(0, 2);
        firstA = generator.collectFirst(1, 2);
        followS = generator.collectFollow(0, 2);
        followA = generator.collectFollow(1, 2);
        assertEquals("eps,ac", firstS.toString());
        assertEquals("c", firstA.toString());
        assertEquals("$,c$", followS.toString());
        assertEquals("$", followA.toString());

        assertTrue(node.next(5));
        assertEquals("aab",node.string());
        assertEquals("S(aS()A(abS()))",node.parenString());

        firstS = generator.collectFirst(0, 1);
        firstA = generator.collectFirst(1, 1);
        followS = generator.collectFollow(0, 1);
        followA = generator.collectFollow(1, 1);
        assertEquals("eps,a", firstS.toString());
        assertEquals("a", firstA.toString());
        assertEquals("$,a", followS.toString());
        assertEquals("$", followA.toString());

        firstS = generator.collectFirst(0, 2);
        firstA = generator.collectFirst(1, 2);
        followS = generator.collectFollow(0, 2);
        followA = generator.collectFollow(1, 2);
        assertEquals("eps,aa", firstS.toString());
        assertEquals("ab", firstA.toString());
        assertEquals("$,ab", followS.toString());
        assertEquals("$", followA.toString());

        assertTrue(node.next(5));
        assertEquals("aabac",node.string());
        assertEquals("S(aS()A(abS(aS()A(c))))",node.parenString());

        firstS = generator.collectFirst(0, 1);
        firstA = generator.collectFirst(1, 1);
        followS = generator.collectFollow(0, 1);
        followA = generator.collectFollow(1, 1);
        assertEquals("eps,a", firstS.toString());
        assertEquals("a,c", firstA.toString());
        assertEquals("$,a,c", followS.toString());
        assertEquals("$", followA.toString());

        firstS = generator.collectFirst(0, 2);
        firstA = generator.collectFirst(1, 2);
        followS = generator.collectFollow(0, 2);
        followA = generator.collectFollow(1, 2);
        assertEquals("eps,aa", firstS.toString());
        assertEquals("ab,c", firstA.toString());
        assertEquals("$,ab,c$", followS.toString());
        assertEquals("$", followA.toString());

        firstS = generator.collectFirst(0, 3);
        firstA = generator.collectFirst(1, 3);
        followS = generator.collectFollow(0, 3);
        followA = generator.collectFollow(1, 3);

        assertEquals("eps,aaa", firstS.toString());
        assertEquals("aba,c", firstA.toString());
        assertEquals("$,aba,c$", followS.toString());
        assertEquals("$", followA.toString());
    }
}
