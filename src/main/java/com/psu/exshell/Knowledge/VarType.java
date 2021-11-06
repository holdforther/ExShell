package com.psu.exshell.Knowledge;

public enum VarType {
    REQUESTED("Requested"),
    INFERRED("Inferred"),
    INFERRED_REQUESTED("Infer-Requested");

    private String str;

    VarType(String str) {
        this.str = str;
    }

    @Override
    public String toString() {
        return str;
    }
}
