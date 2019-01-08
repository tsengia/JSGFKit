import ca.l5.expandingdev.jsgf.*;
import org.junit.Test;

import java.rmi.UnexpectedException;

public class GrammarCompileTest {

	@Test
	public void test() throws UnexpectedException {
		Grammar g = new Grammar();
		g.name = "test-name-hopefully-works";
		g.addRule(new Rule("greet", new Tag(new Sequence(new RequiredGrouping(new RuleReference("greetWord")), new RequiredGrouping(new RuleReference("name"))),"greeting", "social")));

		g.addRule(new Rule("greetWord", new AlternativeSet(new Token("hello"), new Token("hi"))));
		g.addRule(new Rule("name", new AlternativeSet(new Token("peter"), new Token("john"), new Token("mary"), new Token("anna"))));
		g.addRule(new Rule("findThat", Grammar.parseExpansionsFromString("((can you please find that+){hello}{hi})")));
		System.out.println(g.compileGrammar());
		System.out.println("PARSE TEST");
		Grammar testGram = Grammar.parseGrammarFromString("<test> = [(hello | hi) {greeting} {social}] [wonderful] (world |  earth) {place} [(how (are (you|we) | is everyone) today)] { how_are_you } ");
		//Grammar testGram = Grammar.parseGrammarFromString(g.compileGrammar());
		System.out.println(testGram.toString());
		System.out.println("MATCH TEST");
		String testString = "hello mary";
		System.out.println("Test String:" + testString);
		if (g.getMatchingRule(testString) != null) {
			System.out.println(g.getMatchingRule(testString).name);
		}
	}
}
