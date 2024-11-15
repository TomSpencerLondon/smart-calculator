package calculator;


import java.math.BigInteger;

public class MultiplicationExpression implements Expression {
    private Expression left;
    private Expression right;

    public MultiplicationExpression(Expression left, Expression right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public BigInteger interpret(Context context) {
        return left.interpret(context).multiply(right.interpret(context));
    }
}
