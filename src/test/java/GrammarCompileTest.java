import static org.junit.Assert.*;

import org.junit.Test;

import ca.l5.expandingdev.jsgf.AlternateSimpleWordList;
import ca.l5.expandingdev.jsgf.Grammar;
import ca.l5.expandingdev.jsgf.RequiredWord;
import ca.l5.expandingdev.jsgf.Rule;
import ca.l5.expandingdev.jsgf.RuleReference;


public class GrammarCompileTest {

	@Test
	public void test() {
		//fail("Not yet implemented");
		Grammar g = new Grammar();
		g.addRule(new Rule("greet", new RequiredWord(new RuleReference("greetWord"), new RequiredWord(new RuleReference("name")))));
		g.addRule(new Rule("name", new AlternateSimpleWordList(new String[]{ "computer", "world", "paul", "peter", "sue", "amanda"})));
		g.addRule(new Rule("greetWord", new AlternateSimpleWordList(new String[]{"hello", "hi", "hey"})));
		System.out.println(g.compileGrammar());
	}

}
