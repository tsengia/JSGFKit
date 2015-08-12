import static org.junit.Assert.*;

import org.junit.Test;

import ca.l5.expandingdev.jsgf.AlternativeSet;
import ca.l5.expandingdev.jsgf.Expansion;
import ca.l5.expandingdev.jsgf.Grammar;
import ca.l5.expandingdev.jsgf.RequiredGrouping;
import ca.l5.expandingdev.jsgf.Rule;
import ca.l5.expandingdev.jsgf.RuleReference;
import ca.l5.expandingdev.jsgf.Token;


public class GrammarCompileTest {

	@Test
	public void test() {
		Grammar g = new Grammar();
		g.addRule(new Rule("greet", new RequiredGrouping(new RuleReference("greetWord")), new RequiredGrouping(new RuleReference("name"))));
		g.addRule(new Rule("greetWord", new AlternativeSet(new Token("hello"), new Token("hi"))));
		g.addRule(new Rule("name", new AlternativeSet(new Token("peter"), new Token("john"), new Token("mary"), new Token("anna"))));
		g.addRule(new Rule("findThat", new Token("can you please find that "), new RuleReference("object")));
		System.out.println(g.compileGrammar());
	}

}
