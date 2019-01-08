package ca.l5.expandingdev.jsgf;

import java.rmi.UnexpectedException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Grammar {
    public static final String specialCharacterRegex = "[;=<>*+\\[\\]()|{} ]";
    public String name;
    private List<Rule> rules;
    private List<Import> imports;
    private GrammarHeader header;

    public Grammar() {
        header = new GrammarHeader();
        rules = new ArrayList<Rule>();
        imports = new ArrayList<Import>();
        name = "default";
    }

    public Grammar(String n) {
        header = new GrammarHeader();
        rules = new ArrayList<Rule>();
        imports = new ArrayList<Import>();
        name = n;
    }


    public List<MatchInfo> getMatchingExpansions(Expansion e, String[] words, int wordPosition) {
        List<MatchInfo> matchList = new ArrayList<>();
        if (e instanceof Token) {
            Token t = (Token) e;
            if (t.getText().equals(words[wordPosition])) {
                String matchedPart = words[wordPosition];
                matchList.add(new MatchInfo(t, words[wordPosition]));
            }
            else {
                // No match
            }
        }
        else if (e instanceof RuleReference) {
            RuleReference ref = (RuleReference) e;
            try {
                Rule rule = this.getRule(ref.getRuleName()); // Try to get the rule
                List<MatchInfo> m1 = (this.getMatchingExpansions(rule.getChildExpansion(), words, wordPosition));
                if (m1.size() != 0) {
                    matchList.add(new MatchInfo(e, "")); // Need to mark that the rule was matched!
                    matchList.addAll(m1);
                }
            }
            catch (RuntimeException exception) {
                // Rule couldnt be found
            }
        }
        else if (e instanceof OptionalGrouping) {
            OptionalGrouping og = (OptionalGrouping) e;
            List<MatchInfo> m1 = this.getMatchingExpansions(og.getChildExpansion(), words, wordPosition);
            if (m1.size() == 0) {
                // Optional, so it can match. Used for sequences
               // matchList.add(new MatchInfo(e, ""));
            }
            else {
                //Matches
                matchList.add(new MatchInfo(e, ""));
                matchList.addAll(this.getMatchingExpansions(og.getChildExpansion(), words, wordPosition));
            }
        }
        else if (e instanceof RequiredGrouping) {
            RequiredGrouping rg = (RequiredGrouping) e;
            List<MatchInfo> m1 = (this.getMatchingExpansions(rg.getChildExpansion(), words, wordPosition));

            if (m1.size() != 0) {
                matchList.add(new MatchInfo(e, ""));
                matchList.addAll(m1);
            }
        }
        else if (e instanceof Tag) {
            Tag t = (Tag) e;
            List<MatchInfo> m1 = this.getMatchingExpansions(t.getChildExpansion(), words, wordPosition);
            if (m1.size() != 0) {
                matchList.add(new MatchInfo(e,""));
                matchList.addAll(m1); // Found a match! Add it to the list
            }
        }
        else if (e instanceof AlternativeSet) {
            AlternativeSet as = (AlternativeSet) e;
            for (Expansion x : as.getChildExpansions()) {
                List<MatchInfo> m1 = this.getMatchingExpansions(x, words, wordPosition);

                if ((x instanceof KleeneStar || x instanceof OptionalGrouping) && m1.size() == 0) { // Stupid OptionalGrouping
                    continue;
                }

                if (m1.size() != 0) {
                    matchList.add(new MatchInfo(e,""));
                    matchList.addAll(m1); // Found a match! Add it to the list
                    break;
                }
            }
        }
        else if (e instanceof Sequence) {
            Sequence seq = (Sequence) e;
            List<MatchInfo> localMatchList = new ArrayList<>();
            int matchedCount = 0;
            for (Object o : seq) {
                Expansion x = (Expansion) o;
                List<MatchInfo> m1 = this.getMatchingExpansions(x, words, wordPosition);
                if (m1.size() == 0 && (x instanceof KleeneStar || x instanceof OptionalGrouping)) {
                    matchedCount++; // Still counts a match
                    continue;
                }

                if (m1.size() != 0) {
                    matchedCount++;
                    for (MatchInfo localMatch : m1) {
                        if(!localMatch.getMatchingStringSection().equals("")) {
                            wordPosition += localMatch.getMatchingStringSection().split(" ").length;
                        }
                    }
                    localMatchList.addAll(m1); // Found a match! Add it to the list
                }
                else { // Doesn't match! Sequence aborted.
                    localMatchList.clear();
                    break;
                }

                if (wordPosition > words.length - 1) { // Sequence is longer than provided words! Abort!
                    break;
                }
            }

            if (matchedCount != seq.size()) { // Not all of the required matches were met!
                localMatchList.clear();
            }

            if (localMatchList.size() != 0) {
                matchList.add(new MatchInfo(e, ""));
                matchList.addAll(localMatchList);
            }
        }
        else if (e instanceof KleeneStar) {
            KleeneStar ks = (KleeneStar) e;
            boolean done = false;
            List<MatchInfo> m1;
            matchList.add(new MatchInfo(e, ""));
            while(!done) {
                if (wordPosition > words.length - 1) {
                    break;
                }
                m1 = this.getMatchingExpansions(ks.getChildExpansion(), words, wordPosition);
                if (m1.size() == 0) {
                    // No matches
                    done = true;
                } else {
                    //Matches
                    for (MatchInfo mi2 : m1) {
                        if(!mi2.getMatchingStringSection().equals("")) {
                            wordPosition += mi2.getMatchingStringSection().split(" ").length;
                        }
                    }
                    matchList.addAll(m1);
                    matchList.add(new MatchInfo(e, ""));
                }
            }
        }
        else if (e instanceof PlusOperator) {
            PlusOperator po = (PlusOperator) e;
            boolean done = false;
            List<MatchInfo> m1;
            while(!done) {
                if (wordPosition > words.length-1) {
                    break;
                }
                m1 = this.getMatchingExpansions(po.getChildExpansion(), words, wordPosition);
                if (m1.size() == 0) {
                    // No matches
                    done = true;
                } else {
                    //Matches
                    matchList.add(new MatchInfo(e, ""));
                    for (MatchInfo mi2 : m1) {
                        if(!mi2.getMatchingStringSection().equals("")) {
                            wordPosition += mi2.getMatchingStringSection().split(" ").length;
                        }
                    }
                    matchList.addAll(m1);
                }
            }
        }

        return matchList;
    }

    public boolean matchesRule(String ruleName, String test) {
        try {
            Rule rule = this.getRule(ruleName); // Try to get the rule
            return this.matchesRule(rule, test);
        }
        catch (RuntimeException exception) {
            return false;
        }
    }

    public boolean matchesRule(Rule rule, String test) {
        String[] words = test.split(" ");
        List<MatchInfo> m1 = this.getMatchingExpansions(rule.getChildExpansion(), words, 0);
        int matchCount = 0;
        for (MatchInfo mi2 : m1) {
            if (!mi2.getMatchingStringSection().equals("")) {
                matchCount++;
            }
        }
        return matchCount == words.length; // Must match all the words!
    }

    public Rule getMatchingRule(String test) {
        for (Rule r : rules) {
            if (matchesRule(r,test)) {
                return r;
            }
        }
        return null;
    }

    private static Expansion parseAlternativeSets(List<Expansion> exp) {

        //Remove all leftover UnparsedSections
        Iterator<Expansion> expansionIterator;
        Expansion e;
        for (expansionIterator = exp.iterator(); (expansionIterator.hasNext() && ((e = expansionIterator.next()) != null)); ) {
            if (e instanceof UnparsedSection) {
                UnparsedSection up = ((UnparsedSection) e);
                if (up.text.equals("") || up.text.equals(" ")) {
                    expansionIterator.remove();
                } else {
                    up.text = up.text.trim();
                }
            }
        }

        List<Expansion> tempExp = new ArrayList<Expansion>();
        Sequence currentSequence = new Sequence();
        AlternativeSet set = new AlternativeSet();
        for (expansionIterator = exp.iterator(); (expansionIterator.hasNext() && ((e = expansionIterator.next()) != null)); ) {
            if (e instanceof UnparsedSection) {
                UnparsedSection up = (UnparsedSection) e;
                if (up.text.contains("|")) {
                    set.addExpansion(currentSequence.simplestForm());
                    currentSequence = new Sequence();
                } else {
                    currentSequence.addExpansion(up);
                }
            } else {
                currentSequence.addExpansion(e);
            }
        }

        if (set.getChildExpansions().size() > 0) {
            set.addExpansion(currentSequence.simplestForm());
            return set;
        } else {
            return currentSequence.simplestForm();
        }
    }

    private static List<Expansion> parseUnaryOperators(List<Expansion> exp) {
        List<Expansion> tempExp = new ArrayList<Expansion>();
        //Parse Plus and Unary operators
        //Note: Each symbol cannot be nested
        boolean expansionFound = false; //We found an expansion that a Unary operator can be applied to, check to see if the next char is a unary operator
        Expansion selectedExpansion = null;
        Iterator<Expansion> expansionIterator;
        Expansion e;
        for (expansionIterator = exp.iterator(); (expansionIterator.hasNext() && ((e = expansionIterator.next()) != null)); ) {
            if (e instanceof UnparsedSection) {
                if (expansionFound) {
                    UnparsedSection up = (UnparsedSection) e;
                    if (up.text.startsWith("*")) { // Kleene star operator
                        tempExp.add(new KleeneStar(selectedExpansion));
                        String newUnprocessedText = up.text.replaceFirst("\\*", ""); // Remove the ) token from the  UnparsedSection
                        tempExp.add(new UnparsedSection(newUnprocessedText.trim()));
                        expansionFound = false;
                    } else if (up.text.startsWith("+")) { // Plus operator
                        tempExp.add(new PlusOperator(selectedExpansion));
                        String newUnprocessedText = up.text.replaceFirst("\\+", ""); // Remove the ) token from the  UnparsedSection
                        tempExp.add(new UnparsedSection(newUnprocessedText.trim()));
                        expansionFound = false;
                    } else {
                        tempExp.add(selectedExpansion);
                        tempExp.add(e);
                        expansionFound = false;
                    }
                } else {
                    tempExp.add(e);
                }
            } else {
                if (expansionFound) { // If we already had found an expansion before this and we didnt find a unary operator after it, add it to the list of processed expansions
                    tempExp.add(selectedExpansion);
                }
                expansionFound = true;
                selectedExpansion = e;
            }
        }

        if (expansionFound) { // If we reached the end of the loop with a taggable expansion selected, but didnt add it to the list of expressions
            tempExp.add(selectedExpansion);
        }

        exp = tempExp;

        tempExp = new ArrayList<Expansion>();
        boolean foundLegalExpansion = false;
        boolean foundStart = false;
        String currentTag = "";
        Tag tagExpansion = null;
        //NOTE: A single expansion is allowed to have multiple tags!
        for (expansionIterator = exp.iterator(); (expansionIterator.hasNext() && ((e = expansionIterator.next()) != null)); ) {
            if (foundLegalExpansion) {
                if (foundStart) {
                    if (e instanceof UnparsedSection) { // Could contain the ending }
                        UnparsedSection up = (UnparsedSection) e;
                        if (up.text.startsWith("}") || up.text.equals("}")) { // Found the end of the tag!
                            tagExpansion.addTag(currentTag);
                            currentTag = ""; // Reset the tag string contents
                            String upText = up.text.replaceFirst("\\}", ""); // Remove the } token from the  UnparsedSection
                            upText = upText.trim();

                            if (upText.endsWith("{")) { // Test to see if another tag follows this one
                                foundStart = true;
                                upText = upText.substring(0, upText.length() - 1); // Remove the last char, which should be the { symbol
                            } else {
                                foundStart = false;
                                tempExp.add(tagExpansion);
                                foundLegalExpansion = false;
                            }

                            tempExp.add(new UnparsedSection(upText.trim()));
                        } else {
                            tempExp.add(e);
                        }
                    } else {
                        currentTag += e.getString(); // Add the expansions text contents to the tag string
                    }
                } else {
                    // Looking for a starting bracket {
                    if (e instanceof UnparsedSection) { // May contain the { we're looking for
                        UnparsedSection up = (UnparsedSection) e;
                        if (up.text.endsWith("{")) { // Found the start of the tag!
                            foundStart = true;
                            String newUnprocessedText = up.text.substring(0, up.text.length() - 1); // Remove the last char, which should be the { symbol
                            tempExp.add(new UnparsedSection(newUnprocessedText.trim())); // Add the updated UnprocessedText section to the list
                        } else {
                            tempExp.add(tagExpansion.getChildExpansion());
                            tempExp.add(e);
                            foundLegalExpansion = false;
                        }
                    } else { // No tag possible for the selected expansion, but check to see if this expansion can be tagged
                        tempExp.add(tagExpansion.getChildExpansion());
                        if (!(e instanceof PlusOperator || e instanceof KleeneStar)) {
                            tagExpansion = new Tag(e);
                        } else { // current expansion cannot be tagged, begin search for taggable expansion over again
                            foundLegalExpansion = false;
                            tempExp.add(e);
                        }
                    }
                }
            } else { // Looking for a expansion that is taggable
                if (!(e instanceof PlusOperator || e instanceof KleeneStar || e instanceof UnparsedSection)) {
                    foundLegalExpansion = true; // Found a taggable expansion, select it and start searching for tags
                    tagExpansion = new Tag(e);
                } else { // Unary operators and UnparsedSections cannot be tagged, pass over them
                    tempExp.add(e);
                }
            }
        }

        if (foundLegalExpansion) { // Reached end of loop and had selected a taggable expansion, but no tags found
            tempExp.add(tagExpansion.getChildExpansion());
        }

        exp = tempExp;

        return exp;
    }

    private static List<Expansion> parseRequiredGroupings(List<Expansion> exp) {
        List<Expansion> tempExp = new ArrayList<>();
        List<Expansion> children = new ArrayList<>();
        int nestCount = 0;
        final char startChar = '(';
        final char endChar = ')';
        for (Expansion e : exp) {
            if (e instanceof UnparsedSection) {
                UnparsedSection up = (UnparsedSection) e;
                StringBuilder childString = new StringBuilder();
                StringBuilder outsideString = new StringBuilder();
                for (char c : up.text.toCharArray()) {
                    if (c == startChar) {
                        nestCount++;
                        if (nestCount == 1) {
                            if (outsideString.toString().length() > 0) {
                                tempExp.add(new UnparsedSection(outsideString.toString()));
                            }
                            outsideString = new StringBuilder();
                            continue;
                        }
                    } else if (c == endChar) {
                        nestCount--;
                        if (nestCount == 0) { // Transition to the end of the string
                            children.add(new UnparsedSection(childString.toString()));
                            children = parseRequiredGroupings(children);
                            children = parseOptionalGroupings(children);
                            children = parseUnaryOperators(children);
                            tempExp.add(new RequiredGrouping(parseAlternativeSets(children)));
                            childString = new StringBuilder();
                            children = new ArrayList<>();
                        }
                    }

                    if (nestCount >= 1) {
                        childString.append(c);
                    } else if (c != endChar) {
                        outsideString.append(c);
                    }
                }

                if (outsideString.toString().length() > 0) {
                    tempExp.add(new UnparsedSection(outsideString.toString()));
                }

                if (childString.toString().length() > 0) {
                    if (nestCount > 0) {
                        children.add(new UnparsedSection(childString.toString()));
                    } else {
                        tempExp.add(new UnparsedSection(childString.toString()));
                    }
                }
            } else {
                if (nestCount >= 1) { // Element is part of this grouping's children
                    children.add(e);
                } else {
                    tempExp.add(e);
                }
            }
        }
        exp = tempExp;
        return exp;
    }

    private static List<Expansion> parseOptionalGroupings(List<Expansion> exp) {
        List<Expansion> tempExp = new ArrayList<>();
        List<Expansion> children = new ArrayList<>();
        int nestCount = 0;
        final char startChar = '[';
        final char endChar = ']';
        for (Expansion e : exp) {
            if (e instanceof UnparsedSection) {
                UnparsedSection up = (UnparsedSection) e;
                StringBuilder childString = new StringBuilder();
                StringBuilder outsideString = new StringBuilder();
                for (char c : up.text.toCharArray()) {
                    if (c == startChar) {
                        nestCount++;
                        if (nestCount == 1) {
                            if (outsideString.toString().length() > 0) {
                                tempExp.add(new UnparsedSection(outsideString.toString()));
                            }
                            outsideString = new StringBuilder();
                            continue;
                        }
                    } else if (c == endChar) {
                        nestCount--;
                        if (nestCount == 0) { // Transition to the end of the string
                            children.add(new UnparsedSection(childString.toString()));
                            children = parseOptionalGroupings(children);
                            children = parseUnaryOperators(children);
                            tempExp.add(new OptionalGrouping(parseAlternativeSets(children)));
                            childString = new StringBuilder();
                            children = new ArrayList<>();
                        }
                    }

                    if (nestCount >= 1) {
                        childString.append(c);
                    } else if (c != endChar) {
                        outsideString.append(c);
                    }
                }

                if (outsideString.toString().length() > 0) {
                    tempExp.add(new UnparsedSection(outsideString.toString()));
                }

                if (childString.toString().length() > 0) {
                    if (nestCount > 0) {
                        children.add(new UnparsedSection(childString.toString()));
                    } else {
                        tempExp.add(new UnparsedSection(childString.toString()));
                    }
                }
            } else {
                if (nestCount >= 1) { // Element is part of this grouping's children
                    children.add(e);
                } else {
                    tempExp.add(e);
                }
            }
        }
        exp = tempExp;
        return exp;
    }

    private static List<Expansion> parseRuleReferences(List<Expansion> exp) throws UnexpectedException {
        //Parse Rule References because they have next highest precedence
        //Pattern: "<"+TOKEN+">"
        ArrayList<Expansion> tempExp = new ArrayList<Expansion>(); // Temporary list that will be copied into exp
        boolean startSearch; // True = looking for a <
        boolean endSearch; // True = looking for a >
        boolean tokenSearch; // True = hoping that the next Expansion is a Token object containing the name of the rule that is being referenced
        Token selectedToken; // Only set to null to avoid compiler warnings.
        Iterator<Expansion> expansionIterator;
        Expansion e;
        boolean iterationNeeded = true;
        while (iterationNeeded) {
            iterationNeeded = false;
            tempExp = new ArrayList<>();
            startSearch = true;
            endSearch = false;
            tokenSearch = false;
            selectedToken = null;
            for (expansionIterator = exp.iterator(); (expansionIterator.hasNext() && ((e = expansionIterator.next()) != null)); ) {
                if (startSearch) {
                    if (e instanceof UnparsedSection) {
                        UnparsedSection up = (UnparsedSection) e;
                        if (up.text.endsWith("<")) {
                            startSearch = false;
                            tokenSearch = true;
                            //Found the < that starts the rule reference, so we need to remove it from the old UnparsedSection and add the new  UnparsedSection to the list
                            String newUnprocessedText = up.text.substring(0, up.text.length() - 1); // Remove the last char, which should be the < symbol
                            tempExp.add(new UnparsedSection(newUnprocessedText.trim())); // Add the updated UnprocessedText section to the list
                        } else {
                            tempExp.add(up); // UnparsedSection does not end in a < so it must not contain the start to a rule reference
                        }
                    } else {
                        tempExp.add(e); // Not an UnparsedSection with text, continue on
                    }
                } else if (endSearch) {
                    if (e instanceof UnparsedSection) {
                        UnparsedSection up = (UnparsedSection) e;
                        if (up.text.startsWith(">")) {
                            endSearch = false;
                            startSearch = true;
                            tempExp.add(new RuleReference(selectedToken.getText()));
                            //Found the > that ends the rule reference, so we need to remove it from the old  UnparsedSection and add the new  UnparsedSection to the list
                            String newUnprocessedText = up.text.replaceFirst(">", ""); // Remove the > token from the  UnparsedSection
                            tempExp.add(new UnparsedSection(newUnprocessedText.trim()));
                            iterationNeeded = true;
                        } else {
                            throw new UnexpectedException("Found start of rule reference, but did not find > immediately afterwards! found: " + e.toString());
                        }
                    } else {
                        throw new UnexpectedException("Found start of rule reference, but did not find > immediately afterwards! found: " + e.toString());
                    }
                } else if (tokenSearch) {
                    if (e instanceof Token) {
                        endSearch = true;
                        tokenSearch = false;
                        selectedToken = (Token) e;
                    } else {
                        throw new UnexpectedException("Found < character denoting rule reference, but did not find rule name immediately afterwards! found: " + e.toString());
                    }
                } else {
                    tempExp.add(e);
                }
            }
            exp = tempExp;
        }

        return exp;
    }

    public static List<Expansion> parseTokensFromString(String part) {
        List<Expansion> exp = new ArrayList<Expansion>();

        //Parse Tokens because they have the highest precedence
        String passed = ""; // All characters that are not part of a token
        int position = 0;
        boolean escapedMode; // If turned true, the next character will be added to the currentToken string without being evaluated
        boolean quotedMode; // If turned true, all characters between quotes are added to the currentToken string
        char quoteType; // Which quote character to seek for to signify end of quote
        boolean tokenMode = false; // If True, test and add the following characters to the currentToken string
        String currentToken;

        while (position < part.length()) {
            escapedMode = false;
            quotedMode = false;
            quoteType = '\'';
            tokenMode = false;
            currentToken = ""; // This holds the string of characters that are being scanned into one Token

            String a; // This holds the current character that is being scanned

            while (!tokenMode && position < part.length()) {
                a = "" + part.charAt(position);
                if (!a.matches(specialCharacterRegex)) {
                    exp.add(new UnparsedSection(passed.trim()));
                    passed = "";
                    tokenMode = true;
                    //DO NOT INCREMENT THE POSITION COUNTER, WE ARE LETTING THE NEXT LOOP EVALUATE THIS CHARACTER
                } else {
                    passed += a;
                    position++;
                }
            }

            if (!tokenMode) { // We reached the end of the string without finding a token, so add what we passed over
                exp.add(new UnparsedSection(passed.trim()));
                passed = "";
            }

            while (tokenMode && position < part.length()) {
                a = "" + part.charAt(position); // Retrieve the current char we are using
                if (escapedMode) { // escape mode ensures that the next character is not processed literally
                    escapedMode = false;
                    currentToken += a;
                    position++;
                } else {
                    if (a.equals("\\")) { // Test to see if char triggers escape mode
                        escapedMode = true;
                        position++;
                    } else if (quotedMode) { // Token is either like: "blarg" or: 'blarg'
                        if (a.equals(quoteType)) {
                            quotedMode = false;
                            tokenMode = false;
                            currentToken += quoteType; // The last character is a part of the token (Either ' or " )
                            position++; // Last character was added to the token, so it was processed, increment the position counter
                            //Entire token has now been scanned into currentToken
                            Token t = new Token(currentToken);
                            exp.add(t);
                            currentToken = "";
                        } else { // Characters within the quotes
                            currentToken += a;
                            position++;
                        }
                    } else if (a.equals("\"")) {
                        quotedMode = true;
                        quoteType = '\"';
                        currentToken += a;
                        position++;
                    } else if (a.equals("\'")) {
                        quotedMode = true;
                        quoteType = '\'';
                        currentToken += a;
                        position++;
                    } else if (a.matches(specialCharacterRegex)) { // Check to see if char matches special characters
                        tokenMode = false;
                        //Entire token has now been scanned into currentToken
                        Token t = new Token(currentToken);
                        exp.add(t);
                        currentToken = "";
                        passed = a;
                        position++;
                    } else {
                        currentToken += a;
                        position++;
                    }
                }
            }
            if (tokenMode) { // Reached end of string before end of token
                Token t = new Token(currentToken);
                exp.add(t);
                currentToken = "";
            }
        }

        if (!tokenMode) { // We reached the end of the string without finding a token, so add what we passed over
            exp.add(new UnparsedSection(passed.trim()));
        }

        //Everything that can be Tokenized has been tokenized, so remove all of the whitespace as it serves no function now
        Iterator<Expansion> expansionIterator;
        List<Expansion> tempExp = new ArrayList<>();
        Expansion e;
        for (expansionIterator = exp.iterator(); expansionIterator.hasNext() && ((e = expansionIterator.next()) != null); ) {
            if (e instanceof UnparsedSection) {
                UnparsedSection up = (UnparsedSection) e;
                String test = up.text.replaceAll(" ", "");
                if (!test.equals("")) {
                    tempExp.add(new UnparsedSection(test));
                }
            } else {
                tempExp.add(e);
            }
        }

        exp = tempExp;

        return exp;
    }

    public static Expansion parseExpansionsFromString(String part) throws UnexpectedException {
        String toParse = part;
        List<Expansion> exp = parseTokensFromString(part);
        exp = parseRuleReferences(exp);
        exp = parseRequiredGroupings(exp);
        exp = parseOptionalGroupings(exp);
        exp = parseUnaryOperators(exp);
        Expansion rootExpansion = parseAlternativeSets(exp);

        return rootExpansion;
    }

    public static Grammar parseGrammarFromString(String s) {
        Grammar grammar = new Grammar();
        String noComments = s.replaceAll("(\\#+.*[\\n|\\r|\\v])|([//]+.*[\\n|\\r|\\v])", ""); // Remove all commented out lines
        String[] statements = noComments.split("(?<!\"\');(?!\"\')"); // Split into statements with each semicolon, but ignore semicolons within quotes!

        try {
            for (String statement : statements) {
                statement = statement.trim();
                //Remove extra whitespace between characters
                statement = statement.replaceAll(" {2,}", " ");

                if (statement.startsWith("grammar ")) {
                    String[] parts = statement.split(" ");
                    grammar.name = parts[1];
                } else if (statement.startsWith("public <")) {
                    statement = statement.replaceFirst("public ", "");
                    String[] parts = statement.split("=");
                    String ruleName = parts[0].replaceAll("<|>", "");
                    ruleName = ruleName.trim();
                    Expansion exp = Grammar.parseExpansionsFromString(parts[1]);
                    grammar.addRule(new Rule(ruleName, true, exp));
                } else if (statement.startsWith("<")) {
                    String[] parts = statement.split("=");
                    String ruleName = parts[0].replaceAll("<|>", "");
                    ruleName = ruleName.trim();
                    Expansion exp = Grammar.parseExpansionsFromString(parts[1]);
                    grammar.addRule(new Rule(ruleName, false, exp));
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("EXCEPTION: " + e.getMessage());
        }

        return grammar;
    }

    public String compileGrammar() {
        String f = header.getHeader();
        f = f.concat("grammar " + name + ";\n");

        for (Import i : imports) {
            f = f.concat(i.getString() + "\n");
        }
        for (Rule r : rules) {
            f = f.concat(r.getRuleString() + "\n");
        }
        return f;
    }

    public String toString() {
        return compileGrammar();
    }

    public void addRule(Rule r) {
        rules.add(r);
    }

    public Rule getRule(String ruleName) {
        for (Rule r : rules) {
            if (r.name.equals(ruleName)) {
                return r;
            }
        }
        throw new RuntimeException("Could not find rule by name: " + ruleName);
    }

    public void addImport(Import i) {
        imports.add(i);
    }

    public void addImport(String i) {
        imports.add(new Import(i));
    }

    public List<Import> getImports() {
        return imports;
    }

    public List<Rule> getRules() {
        return rules;
    }

    public GrammarHeader getGrammarHeader() {
        return header;
    }

    public void setGrammarHeader(GrammarHeader h) {
        header = h;
    }

    static class UnparsedSection implements Expansion {
        public String text;

        public UnparsedSection(String section) {
            text = section;
        }

        @Override
        public String getString() {
            return "UnparsedSection:" + text;
        }

        @Override
        public String toString() {
            return "UnparsedSection:" + text;
        }

        @Override
        public boolean hasChildren() {
            return false;
        }

        @Override
        public boolean hasUnparsedChildren() {
            return false;
        }
    }
}
