package calculator;

import java.math.BigInteger;

public class SubtractionExpression implements Expression {
    private Expression left;
    private Expression right;

    public SubtractionExpression(Expression left, Expression right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public BigInteger interpret(Context context) {
        return left.interpret(context).subtract(right.interpret(context));
    }
}
