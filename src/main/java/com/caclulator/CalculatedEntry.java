package com.caclulator;

public record CalculatedEntry(double value, String expression,
                              String postfixExpression) {

    public double getValue() {
        return value;
    }

    public String getExpression() {
        return expression;
    }

    public String getPostfixExpression() {
        return postfixExpression;
    }

    @Override
    public String toString() {
        return "CalculatedEntry{" +
                "value=" + value +
                ", expression='" + expression + '\'' +
                '}';
    }
}
