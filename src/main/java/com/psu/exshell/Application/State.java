package com.psu.exshell.Application;

import com.psu.exshell.Knowledge.Domain;
import com.psu.exshell.Knowledge.Fact;
import com.psu.exshell.Knowledge.Rule;
import com.psu.exshell.Knowledge.Value;
import com.psu.exshell.Knowledge.Variable;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import com.thoughtworks.xstream.XStream;

public class State {

    private final ArrayList<Domain> domains;
    private final ArrayList<Variable> variables;
    private final ArrayList<Rule> rules;

    public State() {
        domains = new ArrayList<>();
        variables = new ArrayList<>();
        rules = new ArrayList<>();
    }

    public State(State other) {
        this.domains = new ArrayList<>(other.domains);
        this.variables = new ArrayList<>(other.variables);
        this.rules = new ArrayList<>(other.rules);
    }

    public ArrayList<Domain> getDomains() {
        return domains;
    }

    public ArrayList<Variable> getVariables() {
        return variables;
    }

    public ArrayList<Rule> getRules() {
        return rules;
    }

    public void add(Domain domain) {
        domains.add(domain);
    }

    public void add(Variable variable) {
        variables.add(variable);
    }

    public void add(Rule rule) {
        rules.add(rule);
    }

    public void removeDomain(int i) {
        if (i < domains.size()) {
            domains.remove(i);
        }
    }

    public void removeVariable(int i) {
        if (i < variables.size()) {
            variables.remove(i);
        }
    }

    public void removeRule(int i) {
        if (i < rules.size()) {
            rules.remove(i);
        }
    }

    public void save(String path) {
        var xstream = new XStream();
        xstream.alias("state", State.class);
        xstream.alias("value", Value.class);
        xstream.alias("domain", Domain.class);
        xstream.alias("variable", Variable.class);
        xstream.alias("fact", Fact.class);
        xstream.alias(("rule"), Rule.class);
        var xmlState = xstream.toXML(this);
        try {
            Files.writeString(Paths.get(path), xmlState);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void load(String path) {
        String xmlState = "<state></state>";
        try {
            xmlState = Files.readString(Paths.get(path), StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        var xstream = new XStream();
        xstream.alias("state", State.class);
        xstream.alias("value", Value.class);
        xstream.alias("domain", Domain.class);
        xstream.alias("variable", Variable.class);
        xstream.alias("fact", Fact.class);
        xstream.alias(("rule"), Rule.class);
        var newState = (State) xstream.fromXML(xmlState);
        clear();
        domains.addAll(newState.getDomains());
        variables.addAll(newState.getVariables());
        rules.addAll(newState.getRules());
    }

    public void clear() {
        domains.clear();
        variables.clear();
        rules.clear();
    }
}
