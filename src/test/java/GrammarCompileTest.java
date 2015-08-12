import static org.junit.Assert.*;

import org.junit.Test;

import ca.l5.expandingdev.jsgf.Grammar;
import ca.l5.expandingdev.jsgf.RequiredGrouping;
import ca.l5.expandingdev.jsgf.Rule;
import ca.l5.expandingdev.jsgf.RuleReference;


public class GrammarCompileTest {

	@Test
	public void test() {
		Grammar g = new Grammar();
		g.addRule(new Rule("greet", new RequiredGrouping(new RuleReference("greetWord"), new RequiredGrouping(new RuleReference("name")))));
	//	g.addRule(new Rule("name", new AlternateSimpleWordList(new String[]{ "computer", "world", "paul", "peter", "sue", "amanda"})));
	//	g.addRule(new Rule("greetWord", new AlternateSimpleWordList(new String[]{"hello", "hi", "hey"})));
		System.out.println(g.compileGrammar());
	}

}
