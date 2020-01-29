import ca.l5.expandingdev.jsgf.*;
import org.junit.Assert;
import org.junit.Test;

import java.rmi.UnexpectedException;

public class GrammarCompileRegexTest {

    @Test
    public void test() throws UnexpectedException {
        String rules = "" +
                "<telephone> = /\\d{3}\\-?\\d{2}\\-?\\d{2}/ ;" +
                "<call> = (call | ring) <telephone> {phone} ;";
        Grammar g = Grammar.parseGrammarFromString(rules);
        //Grammar testGram = Grammar.parseGrammarFromString(g.compileGrammar());
        System.out.println(g.toString());
        System.out.println("MATCH TEST");
        String testString = "call 152-53-72";
        System.out.println("Test String:" + testString);
        Rule matchingRule = g.getMatchingRule(testString);
        Assert.assertNotNull(matchingRule);
        Assert.assertEquals("call", matchingRule.name);
        System.out.println(matchingRule.name);
    }
}
