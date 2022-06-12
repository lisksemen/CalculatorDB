package com.caclulator;

import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.Stack;

public class Calculator {
    public static final HashMap<String, IAction> ACTIONS = new HashMap<>() {{
        put("sin", new IAction.Sin());
        put("cos", new IAction.Cos());
        put("tan", new IAction.Tan());
        put("atan", new IAction.Atan());
        put("log10", new IAction.Log10());
        put("log2", new IAction.Log2());
        put("sqrt", new IAction.Sqrt());
        put("~", new IAction.UnaryMinus());
        put("|", new IAction.UnaryPlus());
    }};


    /**
     * Method to run calculator
     *
     * @param expression expression
     */
    public CalculatedEntry calculate(String expression) {
        if (expression.length() == 0) {
            return null;
        }
        try {
            Parse parse = new Parse(expression);
            return new CalculatedEntry(calculate(parse.getFormula()), expression, parse.getStringFormula());
        } catch (EmptyStackException | NumberFormatException | IndexOutOfBoundsException e) {
            return null;
        }

    }


    /**
     * Method to calculate with not default variables
     *
     * @param formula parsed expression
     * @return result
     */
    private double calculate(ArrayList<String> formula) {
        // Calculating postfix expression
        Stack<Double> stack = new Stack<>();
        for (String infix : formula) {
            if (Parse.isNumeric(infix)) {
                stack.push(Double.parseDouble(infix));
            } else if (Parse.isFunction(infix) || isUnaryOperator(infix)) {
                IAction action = ACTIONS.get(infix);
                stack.push(action.calculate(stack.pop()));
            } else if (Parse.isOperator(infix)) {
                if (stack.size() > 1) {
                    double second = stack.pop();
                    double first = stack.pop();
                    switch (infix) {
                        case "+" -> stack.push(first + second);
                        case "-" -> stack.push(first - second);
                        case "*" -> stack.push(first * second);
                        case "/" -> stack.push(first / second);
                        case "^" -> stack.push(Math.pow(first, second));
                    }
                } else throw new NumberFormatException();

            } else
                throw new NumberFormatException();
        }
        if (stack.empty() || stack.size() > 1)
            throw new NumberFormatException();
        return stack.peek();
    }


    /**
     * Method to parse variables
     *
     * @param args args to parse from
     * @return Hashmap of variables
     */
    private HashMap<String, Double> parseVariables(String[] args) {
        HashMap<String, Double> variables = new HashMap<>();
        for (int i = 1; i < args.length; i++) {
            variables.put(args[i].split("=")[0].replaceAll("\\s+", ""),
                    Double.parseDouble(args[i].split("=")[1].replaceAll("\\s+", "").replaceAll(",", ".")));
        }
        return variables;
    }

    /**
     * Method to define unary operators
     *
     * @param infix operator
     * @return bool
     */
    private boolean isUnaryOperator(String infix) {
        return infix.equals("~") || infix.equals("|");
    }

}

