package calculator;

import java.math.BigInteger;

public class DivisionExpression implements Expression {
    private Expression left;
    private Expression right;

    public DivisionExpression(Expression left, Expression right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public BigInteger interpret(Context context) {
        return left.interpret(context).divide(right.interpret(context));
    }
}
