package calculator;


import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

class Context {
    // Any global information needed for interpretation

    private static Map<Object, BigInteger> assignments = new HashMap<>();

    public void put(String left, BigInteger right) {
        assignments.put(left, right);
    }

    public boolean contains(String variable) {
        return assignments.containsKey(variable);
    }

    public BigInteger get(String token) {
        return assignments.get(token);
    }
}
