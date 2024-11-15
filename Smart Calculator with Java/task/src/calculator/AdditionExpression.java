package calculator;


import java.math.BigInteger;

public class AdditionExpression implements Expression {
    private Expression left;
    private Expression right;

    public AdditionExpression(Expression left, Expression right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public BigInteger interpret(Context context) {
        return left.interpret(context).add(right.interpret(context));
    }
}
