package com.psu.exshell.Knowledge;

import java.util.ArrayList;

public class Rule {

    private String name;
    private ArrayList<Fact> premise;
    private Fact conclusion;
    private String explanation;

    public Rule(String name, ArrayList<Fact> premise, Fact conclusion, String explanation) {
        this.name = name;
        this.premise = premise;
        this.conclusion = conclusion;
        this.explanation = explanation;
    }

    public Rule(Rule other) {
        name = other.name;
        premise = new ArrayList<>(other.premise);
        conclusion = new Fact(other.conclusion);
        explanation = other.explanation;
    }

    public Rule() {
        name = "";
        premise = new ArrayList<>();
        conclusion = new Fact();
        explanation = "";
    }

    public String getName() {
        return name;
    }

    public ArrayList<Fact> getPremise() {
        return premise;
    }

    public Fact getConclusion() {
        return conclusion;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPremise(ArrayList<Fact> premise) {
        this.premise = premise;
    }

    public void setConclusion(Fact conclusion) {
        this.conclusion = conclusion;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    @Override
    public String toString() {
        return name;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Rule)) {
            return false;
        }
        var otherRule = (Rule) obj;
        return name.equals(otherRule.name);
    }
}
