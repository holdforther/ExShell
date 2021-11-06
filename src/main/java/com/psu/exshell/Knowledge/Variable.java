package com.psu.exshell.Knowledge;

public class Variable {

    private String name;
    private Domain domain;
    private VarType type;
    private String question;

    public Variable(String name, Domain domain, VarType type, String question) {
        this.name = name;
        this.domain = domain;
        this.type = type;
        this.question = question;
    }

    public Variable(Variable other) {
        name = other.name;
        domain = new Domain(other.domain);
        type = other.type;
        question = other.question;
    }

    public Variable() {
        name = "";
        domain = new Domain();
        type = VarType.REQUESTED;
        question = "?";
    }

    public String getName() {
        return name;
    }

    public Domain getDomain() {
        return domain;
    }

    public VarType getType() {
        return type;
    }

    public String getQuestion() {
        return question;
    }

    public void setName(String name) {
        this.name = name;
		this.question = name + "?";
    }

    public void setDomain(Domain domain) {
        this.domain = domain;
    }

    public void setType(VarType type) {
        this.type = type;
    }

    public void setQuestion(String question) {
        this.question = question;
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
        if (!(obj instanceof Variable)) {
            return false;
        }
        var otherVariable = (Variable) obj;
        return name.equals(otherVariable.name);
    }
}
