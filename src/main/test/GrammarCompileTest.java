import ca.l5.expandingdev.jsgf.*;
import org.junit.Assert;
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

	@Test
	public void testWithImport() throws UnexpectedException {
		String grammarText = "grammar ca.l5.expandingdev.test;" +
				"import <ca.l5.expandingdev.common.*>;" +
				"public <weather> = (What is the weather in <city>);" +
				"<weather2> = (What is the weather in <other_city>);";
		Grammar grammar = Grammar.parseGrammarFromString(grammarText);

		String grammarText2 = "grammar ca.l5.expandingdev.common;" +
				"public <city> = (Beijing|<other_city>);" +
				"<other_city> = (Shanghai|Hangzhou);";
		Grammar grammar2 = Grammar.parseGrammarFromString(grammarText2);

		GrammarGroup grammarGroup = new GrammarGroup();
		grammarGroup.addGrammar(grammar);
		grammarGroup.addGrammar(grammar2);

		System.out.println(grammar.toString());
		System.out.println(grammar2.toString());

		{
			String testString = "What is the weather in Beijing";
			System.out.println("Test String:" + testString);
			Assert.assertTrue(grammarGroup.matchesRule(grammar, "weather", testString));
			Assert.assertFalse(grammarGroup.matchesRule(grammar, "weather2", testString));
			Assert.assertFalse(grammarGroup.matchesRule(grammar, "weather_invalid", testString));
		}

		{
			String testString = "What is the weather in Shanghai";
			System.out.println("Test String:" + testString);
			Assert.assertTrue(grammarGroup.matchesRule(grammar, "weather", testString));
			Assert.assertFalse(grammarGroup.matchesRule(grammar, "weather2", testString));
			Assert.assertFalse(grammarGroup.matchesRule(grammar, "weather_invalid", testString));
		}
	}
}
