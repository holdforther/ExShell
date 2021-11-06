package com.psu.exshell.Knowledge;

public class Fact {

    private Variable variable;
    private Value value;

    public Fact(Variable variable, Value value) {
        this.variable = variable;
        this.value = value;
    }

    public Fact(Fact other) {
        variable = new Variable(other.variable);
        value = new Value(other.value);
    }

    public Fact() {
        variable = new Variable();
        value = new Value();
    }

    public Variable getVariable() {
        return variable;
    }

    public Value getValue() {
        return value;
    }

    public void setVariable(Variable variable) {
        this.variable = variable;
    }

    public void setValue(Value value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return variable + " = " + value;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Fact)) {
            return false;
        }
        var otherFact = (Fact) obj;
        return variable.equals(otherFact.variable) && value.equals(otherFact.value);
    }
}
