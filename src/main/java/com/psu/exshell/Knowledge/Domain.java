package com.psu.exshell.Knowledge;

import java.util.ArrayList;

public class Domain {

    private String name;
    private ArrayList<Value> values;

    public Domain(String name, ArrayList<Value> values) {
        this.name = name;
        this.values = values;
    }

    public Domain(Domain other) {
        name = other.name;
        values = new ArrayList<>(other.values);
    }

    public Domain() {
        name = "";
        values = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public ArrayList<Value> getValues() {
        return values;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setValues(ArrayList<Value> values) {
        this.values = values;
    }

    @Override
    public String toString() {
        var sb = new StringBuilder(name + ": ");
        for (var v : values) {
            sb.append(v);
            sb.append(", ");
        }
        sb.delete(sb.length() - 2, sb.length() - 1);
        return sb.toString();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Domain)) {
            return false;
        }
        var otherDomain = (Domain)obj;
        return name.equals(otherDomain.name);
    }
}
