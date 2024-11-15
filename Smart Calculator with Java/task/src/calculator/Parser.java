package calculator;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {
    public static Expression parse(String expression, Context context) {
        String[] tokens = Arrays.stream(expression.split("(?<=[()*/+-])|(?=[()*/+-])|\\s+"))
                .filter(token -> !token.isEmpty())
                .map(String::trim)
                .toArray(String[]::new);

        Stack<Expression> operandStack = new Stack<>();
        Stack<String> operatorStack = new Stack<>();

        for (String token : tokens) {
            if (isNumber(token)) {
                operandStack.push(new NumberExpression(new BigInteger(token)));
            } else if ("(".equals(token)) {
                operatorStack.push(token);
            } else if (")".equals(token)) {
                while (!operatorStack.isEmpty() && !operatorStack.peek().equals("(")) {
                    Expression right = operandStack.pop();
                    Expression left = operandStack.pop();
                    operandStack.push(createExpression(operatorStack.pop(), right, left));
                }

                operatorStack.pop();
            }  else if (isOperator(token)) {
                while (!operatorStack.isEmpty() && precedence(operatorStack.peek()) >= precedence(token)) {
                    Expression right = operandStack.pop();
                    Expression left = operandStack.pop();
                    operandStack.push(createExpression(operatorStack.pop(), right, left));
                }
                operatorStack.push(token);
            } else if (isVariable(token, context)) {
                operandStack.push(new NumberExpression(context.get(token)));
            }
        }

        // Process remaining operators
        while (!operatorStack.isEmpty()) {
            Expression right = operandStack.pop();
            Expression left = operandStack.pop();
            operandStack.push(createExpression(operatorStack.pop(), right, left));
        }

        return operandStack.pop();
    }

    private static boolean isVariable(String token, Context context) {
        return context.contains(token);
    }

    private static boolean isNumber(String token) {
        try {
            new BigInteger(token);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static boolean isOperator(String token) {
        return token.equals("+") || token.equals("-") || token.equals("*") || token.equals("/");
    }

    private static int precedence(String operator) {
        switch (operator) {
            case "*": case "/": return 2;
            case "+": case "-": return 1;
            default: return 0;
        }
    }

    private static Expression createExpression(String operator, Expression right, Expression left) {
        switch (operator) {
            case "+": return new AdditionExpression(left, right);
            case "-": return new SubtractionExpression(left, right);
            case "*": return new MultiplicationExpression(left, right);
            case "/": return new DivisionExpression(left, right);
            default: throw new IllegalArgumentException("Unknown operator: " + operator);
        }
    }
}

