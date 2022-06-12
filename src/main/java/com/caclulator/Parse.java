package com.caclulator;

import java.util.*;

public class Parse {
    public ArrayList<String> getFormula() {
        return formula;
    }

    private final ArrayList<String> formula;

    /**
     * Class for parsing formula and variables into array and hashmap
     *
     * @param args arguments to parse
     */
    public Parse(String args) {
        formula = new ArrayList<>();
        parseFormula(args);
    }


    /**
     * Method to parse formula into array of strings
     *
     * @param args arguments to parse
     */
    private void parseFormula(String args) {
        ArrayList<String> splitExpression = parseSplitExpression(args);
        fillFormula(splitExpression);
    }

    /**
     * Method to split expression into arraylist
     *
     * @param args args to parse expression from
     * @return expression
     */
    private ArrayList<String> parseSplitExpression(String args) {
        ArrayList<String> formatted = formatSpaces(args);
        formatUnaryOperators(formatted);
        return formatted;
    }

    /**
     * Method to format unary minuses and pluses
     * -a -> ~a
     * a-b -> a-b
     * @param formatted formula to format
     */
    private void formatUnaryOperators(ArrayList<String> formatted) {
        for (int i = 0; i < formatted.size(); i++) {
            if (canBeUnary(formatted, i)) {
                formatted.set(i, getUnaryFor(formatted.get(i)));
            }
        }

    }

    /**
     * Method to get unary operator for normal operator
     * @param operator operator to check
     * @return unary operator
     */
    private String getUnaryFor(String operator) {
        if (operator.equals("+"))
            return "|";
        return "~";
    }

    /**
     * Method to check if operator can be unary
     * @param formatted formula
     * @param index index of an operator
     * @return bool
     */
    private boolean canBeUnary(ArrayList<String> formatted, int index) {
        String operator = formatted.get(index);
        if (!operator.equals("+") && !operator.equals("-"))
            return false;
        if (index == 0)
            return true;
        String before = formatted.get(index - 1);
        return isOperator(before) || before.equals("(") || isUnaryOperator(before);
    }


    /**
     * Method to format spaces in expression
     * a   +b -> a + b
     *
     * @param args expression
     * @return expression with formatted spaces
     */
    private ArrayList<String> formatSpaces(String args) {
        String expression = args.replaceAll(" ", "");
        expression = expression.replaceAll(",", ".");
        expression = expression.replaceAll("\\*", " * ");
        expression = expression.replaceAll("/", " / ");
        expression = expression.replaceAll("\\+", " + ");
        expression = expression.replaceAll("-", " - ");
        expression = expression.replaceAll("\\(", " ( ");
        expression = expression.replaceAll("\\)", " ) ");
        expression = expression.replaceAll("\\^", " ^ ");
        expression = expression.replaceAll(" {2}", " ");
        expression = expression.trim();
        return new ArrayList<>(List.of(expression.split(" ")));
    }

    /**
     * Method to fill postfix formula with stack algorithm
     *
     * @param splitExpression expression
     */
    private void fillFormula(ArrayList<String> splitExpression) {
        Stack<String> stack = new Stack<>();
        for (String infix : splitExpression) {
            if (isFunction(infix) || isUnaryOperator(infix)) {
                stack.push(infix);
            } else if (infix.equals("(")) {
                stack.push(infix);
            } else if (infix.equals(")")) {
                while (!stack.empty() && !stack.peek().equals("(")) {
                    if (stack.peek().equals(")"))
                        throw new NumberFormatException();
                    formula.add(stack.pop());
                }
                if (stack.empty())
                    throw new NumberFormatException();
                if (!stack.empty() && stack.peek().equals("("))
                    stack.pop();
                if (!stack.empty() && isFunction(stack.peek()))
                    formula.add(stack.pop());
            } else if (isOperator(infix)) {
                if (infix.equals("^")) {
                    while (!stack.empty() && priority(infix) <= priority(stack.peek()) && !stack.peek().equals("^"))
                        formula.add(stack.pop());
                } else {
                    while (!stack.empty() && (priority(infix) <= priority(stack.peek()))) {
                        formula.add(stack.pop());
                    }
                }
                stack.push(infix);
            } else {
                formula.add(infix);
            }
        }
        while (!stack.empty())
            formula.add(stack.pop());
    }

    /**
     * Method to check if string is unary operator
     * @param infix string to check
     * @return bool
     */
    public boolean isUnaryOperator(String infix) {
        return infix.equals("~") || infix.equals("|");
    }


    /**
     * Method to check if string is an operator
     *
     * @param s string
     * @return bool
     */
    public static boolean isOperator(String s) {
        return s.equals("+") || s.equals("-") || s.equals("*") || s.equals("/") || s.equals("^");
    }

    /**
     * Method to check if string is a function
     *
     * @param s string
     * @return bool
     */
    public static boolean isFunction(String s) {
        return s.equals("sin") || s.equals("cos") || s.equals("tan") || s.equals("atan") || s.equals("sqrt")
                || s.equals("log2") || s.equals("log10");
    }


    /**
     * Converter to string for formula
     *
     * @return formula
     */
    public String getStringFormula() {
        StringBuilder result = new StringBuilder();
        for (String s : formula) {
            result.append(s).append(" ");
        }
        return result.substring(0,result.length() - 1);
    }


    /**
     * Method to check if string is a double
     *
     * @param strNum string
     * @return bool
     */
    public static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    /**
     * Method to get priority of an operator
     *
     * @param str operator
     * @return priority
     */
    private int priority(String str) {
        if (str.equals("("))
            return 1;
        if (str.equals("+") || str.equals("-"))
            return 2;
        if (str.equals("*") || str.equals("/"))
            return 3;
        if (str.equals("^"))
            return 4;
        if (isUnaryOperator(str) || isFunction(str))
            return 5;
        return 6;
    }

}
