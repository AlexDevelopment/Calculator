package com.testtask.calculator;


import java.util.Stack;

public class ReversePolishNotation {

    private ShowErrorListener mShowErrorListener;
    private CleanExpressionListener mCleanExpressionListener;

    private final String ERROR = "error";

    public ReversePolishNotation(ShowErrorListener showErrorListener, CleanExpressionListener cleanExpressionListener) {
        this.mShowErrorListener = showErrorListener;
        this.mCleanExpressionListener = cleanExpressionListener;
    }

    public String calculate(String input) {
        if (getExpression(input) != ERROR) {
            mCleanExpressionListener.cleanExpression();
            return Double.toString(count(getExpression(input)));
        }
        return ERROR;
    }

    private String getExpression(String input) {
        String output = "";
        Stack<Character> operStack = new Stack<>();

        input = changeMinus(input);

        if (catchError(input)) {
            return ERROR;
        }
        for (int i = 0; i < input.length(); i++) {
            if (Character.isDigit(input.charAt(i)) || input.charAt(i) == '#') {
                while (!isOperator(input.charAt(i))) {
                    output += input.charAt(i);
                    i++;
                    if (i == input.length()) break;
                }
                output += " ";
                i--;
            }
            if (isOperator(input.charAt(i))) {
                if (input.charAt(i) == '(')
                    operStack.push(input.charAt(i));
                else if (input.charAt(i) == ')') {
                    if (operStack.size() != 0) {
                        char s = operStack.pop();
                        while (s != '(') {
                            output += String.valueOf(s) + ' ';
                            s = operStack.pop();
                        }
                    } else {
                        mShowErrorListener.showError("Возможно не хватает (");
                        return ERROR;
                    }
                } else {
                    if (operStack.size() > 0) {
                        if (getPriority(input.charAt(i)) <= getPriority(operStack.peek())) {
                            output += String.valueOf(operStack.pop()) + " ";
                        }
                    }
                    operStack.push(input.charAt(i));
                }
            }
        }
        while (operStack.size() > 0) {
            output += operStack.pop() + " ";
        }
        return output;
    }

    private String changeMinus(String input) {
        StringBuffer inputBuffer = new StringBuffer(input);
        for (int i = 0; i < inputBuffer.length(); i++) {
            if (i == 0) {
                if (inputBuffer.charAt(0) == '-') {
                    inputBuffer = inputBuffer.insert(0, '0');
                }
            } else if (inputBuffer.charAt(i) == '-' && ("+-/*(".indexOf(inputBuffer.charAt(i - 1)) != -1)) {
                inputBuffer.setCharAt(i, '#');
            }
        }
        return inputBuffer.toString();
    }

    private boolean catchError(String input) {
        if (getAmountOfOpeningParentheses(input) > getAmountOfClosingParentheses(input)) {
            mShowErrorListener.showError("Не хватает  )");
            return true;
        }
        if (getAmountOfClosingParentheses(input) > getAmountOfOpeningParentheses(input)) {
            mShowErrorListener.showError("Не хватает  (");
            return true;
        }
        return false;
    }

    private int getAmountOfOpeningParentheses(String input) {
        int amount = 0;
        for (int i = 0; i < input.length(); i++) {
            if (input.charAt(i) == '(') {
                amount++;
            }
        }
        return amount;
    }

    private int getAmountOfClosingParentheses(String input) {
        int amount = 0;
        for (int i = 0; i < input.length(); i++) {
            if (input.charAt(i) == ')') {
                amount++;
            }
        }
        return amount;
    }

    private boolean isOperator(char с) {
        if (("+-/*()".indexOf(с) != -1))
            return true;
        return false;
    }

    private int getPriority(char s) {
        switch (s) {
            case '(':
                return 0;
            case ')':
                return 1;
            case '-':
                return 2;
            case '+':
                return 2;
            case '*':
                return 3;
            case '/':
                return 3;
            default:
                return 5;
        }
    }

    private double count(String input) {
        double result = 0;
        Stack<Double> stackResult = new Stack<>();

        for (int i = 0; i < input.length(); i++) {
            if (Character.isDigit(input.charAt(i)) || input.charAt(i) == '#') {
                String a = "";
                while (!isDelimeter(input.charAt(i)) && !isOperator(input.charAt(i))) {
                    a += input.charAt(i);
                    i++;
                    if (i == input.length()) break;
                }
                a = a.replace('#', '-');
                stackResult.push(Double.parseDouble(a));
                i--;
            } else if (isOperator(input.charAt(i))) {
                if (stackResult.size() != 0) {

                }
                double a = stackResult.pop();
                double b = stackResult.pop();

                switch (input.charAt(i)) {
                    case '+':
                        result = b + a;
                        break;
                    case '-':
                        result = b - a;
                        break;
                    case '*':
                        result = b * a;
                        break;
                    case '/':
                        result = b / a;
                        break;
                }
                stackResult.push(result);
            }
        }
        return stackResult.peek();
    }

    private boolean isDelimeter(char c) {
        if ((" =".indexOf(c) != -1))
            return true;
        return false;
    }
}