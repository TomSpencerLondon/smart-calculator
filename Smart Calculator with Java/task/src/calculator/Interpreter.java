package calculator;


import java.math.BigInteger;

class Interpreter {
    private Context context;

    public Interpreter(Context context) {
        this.context = context;
    }

    public BigInteger interpret(String expression) {
        // Parse expression and create expression tree
        Expression expressionTree = buildExpressionTree(expression, context);

        // Interpret expression tree
        return expressionTree.interpret(context);
    }

    private Expression buildExpressionTree(String expression, Context context) {
        return Parser.parse(expression, context);
    }
}
