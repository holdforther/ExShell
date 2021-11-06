package com.psu.exshell.Knowledge;

public class Value {

    private String value;

    public Value(String value) {
        this.value = value;
    }

    public Value(Value other) {
        value = other.value;
    }

    public Value() {
        value = "";
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Value)) {
            return false;
        }
        var otherValue = (Value) obj;
        return value.equals(otherValue.value);
    }
}
