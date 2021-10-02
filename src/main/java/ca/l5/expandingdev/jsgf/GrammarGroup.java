package ca.l5.expandingdev.jsgf;

import java.util.*;

public class GrammarGroup {
    private Map<String, Grammar> grammarMap;

    public GrammarGroup() {
        grammarMap = new HashMap<>();
    }

    public void addGrammar(Grammar grammar) {
        grammarMap.put(grammar.name, grammar);
    }

    public boolean matchesRule(Grammar grammar, String ruleName, String test) {
        try {
            Rule rule = grammar.getRule(ruleName); // Try to get the rule
            return this.matchesRule(grammar, rule, test);
        }
        catch (RuntimeException exception) {
            return false;
        }
    }

    public boolean matchesRule(Grammar grammar, Rule rule, String test) {
        String[] words = test.split(" ");
        List<MatchInfo> m1 = this.getMatchingExpansions(grammar, rule.getChildExpansion(), words, 0);
        int matchCount = 0;
        for (MatchInfo mi2 : m1) {
            if (!mi2.getMatchingStringSection().equals("")) {
                matchCount++;
            }
        }
        return matchCount == words.length; // Must match all the words!
    }

    private List<MatchInfo> getMatchingExpansions(
            Grammar grammar, Expansion expansion, String[] words, int wordPosition) {
        List<MatchInfo> matchList = new ArrayList<>();
        if (expansion instanceof Token) {
            Token token = (Token) expansion;
            if (token.getText().equals(words[wordPosition])) {
                String matchedPart = words[wordPosition];
                matchList.add(new MatchInfo(token, words[wordPosition]));
            }
        } else if (expansion instanceof RuleReference) {
            RuleReference ref = (RuleReference) expansion;
            try {
                // Try to get the rule
                Rule rule = this.getRule(grammar, ref.getRuleName());
                Grammar grammarReference = grammar;
                if (rule == null) {
                    // rule reference may be import rule
                    Grammar importGrammar = findImportGrammarByRule(grammar, ref.getRuleName());
                    if (importGrammar != null) {
                        rule = this.getRule(importGrammar, ref.getRuleName());
                        grammarReference = importGrammar;
                    }
                }
                if (rule == null) {
                    throw new RuntimeException("Could not find rule by name: " + ref.getRuleName());
                }
                List<MatchInfo> matchingExpansions = this.getMatchingExpansions(
                        grammarReference, rule.getChildExpansion(), words, wordPosition);
                if (matchingExpansions.size() != 0) {
                    // Need to mark that the rule was matched!
                    matchList.add(new MatchInfo(expansion, ""));
                    matchList.addAll(matchingExpansions);
                }
            } catch (RuntimeException exception) {
                // Rule couldnt be found
            }
        } else if (expansion instanceof OptionalGrouping) {
            OptionalGrouping optionalGrouping = (OptionalGrouping) expansion;
            List<MatchInfo> matchingExpansions = this.getMatchingExpansions(
                    grammar, optionalGrouping.getChildExpansion(), words, wordPosition);
            if (matchingExpansions.size() == 0) {
                // Optional, so it can match. Used for sequences
                // matchList.add(new MatchInfo(e, ""));
            } else {
                //Matches
                matchList.add(new MatchInfo(expansion, ""));
                matchList.addAll(this.getMatchingExpansions(
                        grammar, optionalGrouping.getChildExpansion(), words, wordPosition));
            }
        } else if (expansion instanceof RequiredGrouping) {
            RequiredGrouping requiredGrouping = (RequiredGrouping) expansion;
            List<MatchInfo> matchingExpansions = (this.getMatchingExpansions(grammar, requiredGrouping.getChildExpansion(), words, wordPosition));

            if (matchingExpansions.size() != 0) {
                matchList.add(new MatchInfo(expansion, ""));
                matchList.addAll(matchingExpansions);
            }
        } else if (expansion instanceof Tag) {
            Tag tag = (Tag) expansion;
            List<MatchInfo> matchingExpansions = this.getMatchingExpansions(
                    grammar, tag.getChildExpansion(), words, wordPosition);
            if (matchingExpansions.size() != 0) {
                matchList.add(new MatchInfo(expansion,""));
                matchList.addAll(matchingExpansions); // Found a match! Add it to the list
            }
        } else if (expansion instanceof AlternativeSet) {
            AlternativeSet as = (AlternativeSet) expansion;
            for (Expansion expansionTmp : as.getChildExpansions()) {
                List<MatchInfo> matchingExpansions = this.getMatchingExpansions(
                        grammar, expansionTmp, words, wordPosition);

                if ((expansionTmp instanceof KleeneStar || expansionTmp instanceof OptionalGrouping) &&
                        matchingExpansions.size() == 0) { // Stupid OptionalGrouping
                    continue;
                }

                if (matchingExpansions.size() != 0) {
                    matchList.add(new MatchInfo(expansion,""));
                    matchList.addAll(matchingExpansions); // Found a match! Add it to the list
                    break;
                }
            }
        } else if (expansion instanceof Sequence) {
            Sequence seq = (Sequence) expansion;
            List<MatchInfo> localMatchList = new ArrayList<>();
            int matchedCount = 0;
            for (Object object : seq) {
                Expansion expansionTemp = (Expansion) object;
                List<MatchInfo> m1 = this.getMatchingExpansions(grammar, expansionTemp, words, wordPosition);
                if (m1.size() == 0 && (expansionTemp instanceof KleeneStar || expansionTemp instanceof OptionalGrouping)) {
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
                matchList.add(new MatchInfo(expansion, ""));
                matchList.addAll(localMatchList);
            }
        } else if (expansion instanceof KleeneStar) {
            KleeneStar kleeneStar = (KleeneStar) expansion;
            boolean done = false;
            List<MatchInfo> matchInfoList;
            matchList.add(new MatchInfo(expansion, ""));
            while(!done) {
                if (wordPosition > words.length - 1) {
                    break;
                }
                matchInfoList = this.getMatchingExpansions(grammar, kleeneStar.getChildExpansion(), words, wordPosition);
                if (matchInfoList.size() == 0) {
                    // No matches
                    done = true;
                } else {
                    //Matches
                    for (MatchInfo mi2 : matchInfoList) {
                        if(!mi2.getMatchingStringSection().equals("")) {
                            wordPosition += mi2.getMatchingStringSection().split(" ").length;
                        }
                    }
                    matchList.addAll(matchInfoList);
                    matchList.add(new MatchInfo(expansion, ""));
                }
            }
        } else if (expansion instanceof PlusOperator) {
            PlusOperator plusOperator = (PlusOperator) expansion;
            boolean done = false;
            List<MatchInfo> matchInfoList;
            while(!done) {
                if (wordPosition > words.length-1) {
                    break;
                }
                matchInfoList = this.getMatchingExpansions(grammar, plusOperator.getChildExpansion(), words, wordPosition);
                if (matchInfoList.size() == 0) {
                    // No matches
                    done = true;
                } else {
                    //Matches
                    matchList.add(new MatchInfo(expansion, ""));
                    for (MatchInfo matchInfo : matchInfoList) {
                        if(!matchInfo.getMatchingStringSection().equals("")) {
                            wordPosition += matchInfo.getMatchingStringSection().split(" ").length;
                        }
                    }
                    matchList.addAll(matchInfoList);
                }
            }
        }

        return matchList;
    }

    private Rule getRule(Grammar grammar, String ruleName) {
        for (Rule rule : grammar.getRules()) {
            if (rule.name.equals(ruleName)) {
                return rule;
            }
        }
        // Can find rule in grammar, may be in import rule.
        return null;
    }

    private Grammar findImportGrammarByRule(Grammar grammar, String ruleName) {
        for (Import importRule : grammar.getImports()) {
            String[] items = importRule.getImportName().split("\\.");
            if (items.length == 0) {
                continue;
            }
            String importRuleName = items[items.length-1].trim();
            if (importRuleName.equals(ruleName) || importRuleName.equals("*")) {
                StringJoiner stringJoiner = new StringJoiner(".");
                for (int i = 0; i < items.length-1; ++i) {
                    stringJoiner.add(items[i]);
                }
                String grammarName = stringJoiner.toString();
                Grammar importGrammar = grammarMap.get(grammarName);
                if (importGrammar == null) {
                    continue;
                }
                // import * need find match rule
                if (importRuleName.equals("*")) {
                    for (Rule rule : importGrammar.getRules()) {
                        if (rule.isvisible && rule.name.equals(ruleName)) {
                            return importGrammar;
                        }
                    }
                } else {
                    // import real rule name
                    Rule rule = importGrammar.getRule(importRuleName);
                    if (rule != null && rule.isvisible) {
                        return importGrammar;
                    }
                }
            }
        }
        return null;
    }
}
