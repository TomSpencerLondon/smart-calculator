package calculator;


import java.math.BigInteger;

class NumberExpression implements Expression {
    private BigInteger number;

    public NumberExpression(BigInteger number) {
        this.number = number;
    }

    @Override
    public BigInteger interpret(Context context) {
        return number;
    }
}