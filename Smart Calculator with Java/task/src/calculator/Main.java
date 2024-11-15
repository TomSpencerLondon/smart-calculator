package calculator;

import java.math.BigInteger;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        // Create a Scanner object to read input
        Scanner scanner = new Scanner(System.in);

        // Print help message once at the start
        printHelp();

        Context context = new Context();
        Interpreter interpreter = new Interpreter(context);

        // Loop to continuously read input or commands
        while (true) {
            // Prompt user for input
            String input = normalizeExpression(scanner.nextLine().trim()); // Read the entire line and trim any surrounding whitespace

            // If the user enters "/exit", print "Bye!" and break the loop
            if (input.equals("/exit")) {
                System.out.println("Bye!");
                break;
            }

            // If the user enters "/help", print help message
            if (input.equals("/help")) {
                printHelp();
                continue;
            }

            // If the input is empty, ignore it
            if (input.isEmpty()) {
                continue;
            }

            if (!isValidCommand(input)) {
                System.out.println("Unknown command");
                continue;
            }

            if (isValidIdentifier(input) && !context.contains(input)) {
                System.out.println("Unknown variable");
                continue;
            }

            // Validate the expression
            if (!isValidExpression(input, context) && !isPreviousAssignment(input, context)) {
                System.out.println("Invalid expression");
                continue;
            }

            if (input.contains("=")) {
                String[] parts = input.split("\\s*=\\s*");
                if (parts.length != 2) {
                    System.out.println("Invalid assignment");
                    continue;
                }
                String left = parts[0].trim();
                String right = parts[1].trim();

                // Validate the assignment and print appropriate messages
                if (!isValidIdentifier(left)) {
                    System.out.println("Invalid identifier");
                    continue;
                } else if (isUnknownVariable(context, right)) {
                    System.out.println("Unknown variable");
                    continue;
                } else if (!isValidAssignment(left, right, context)) {
                    System.out.println("Invalid assignment");
                    continue;
                } else {
                    try {
                        if (context.contains(right)) {
                            context.put(left, context.get(right));
                        } else {
                            context.put(left, new BigInteger(right));
                        }

                        continue;
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid assignment");
                        continue;
                    }
                }
            }


            // Process and evaluate the expression
            try {
                // Interpret expression
                BigInteger result = interpreter.interpret(input);
                System.out.println(result);
            } catch (Exception e) {
                System.out.println("Invalid expression. Please enter a valid expression.");
            }
        }

        // Close the scanner to avoid resource leak
        scanner.close();
    }

    private static String normalizeExpression(String input) {
        StringBuilder result = new StringBuilder();
        int length = input.length();
        int i = 0;

        while (i < length) {
            char currentChar = input.charAt(i);

            // Handle consecutive '+' and '-' signs
            if (currentChar == '+' || currentChar == '-') {
                int minusCount = 0;

                // Count consecutive signs and simplify them
                while (i < length && (input.charAt(i) == '+' || input.charAt(i) == '-')) {
                    if (input.charAt(i) == '-') {
                        minusCount++;
                    }
                    i++;
                }

                // Append simplified sign: '+' if even count of '-', '-' if odd
                result.append(minusCount % 2 == 0 ? "+" : "-");
            } else {
                // Directly append variables, numbers, and non-sign characters
                result.append(currentChar);
                i++;
            }
        }

        // Replace multiple spaces with a single space (if needed) and trim the result
        return result.toString().replaceAll("\\s+", " ").trim();
    }

    private static boolean isPreviousAssignment(String input, Context context) {
        return context.contains(input);
    }

    public static boolean isValidIdentifier(String s) {
        // Check if the string is empty
        if (s.isEmpty()) {
            return false;
        }

        // The first character must be a letter (A-Z or a-z)
        char firstChar = s.charAt(0);
        if (!Character.isLetter(firstChar)) {
            return false;
        }

        if (!isLatinLettersOnly(s)) {
            return false;
        }

        // Check that all characters are valid (only letters A-Z, a-z)
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (!Character.isLetter(c)) {
                return false;
            }
        }

        return true;
    }

    public static boolean isLatinLettersOnly(String input) {
        return input.chars().allMatch(c -> (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z'));
    }

    // Method to check if a string is an unknown variable
    public static boolean isUnknownVariable(Context context, String s) {
        // Check if the string is a valid identifier and is not defined yet
        return isValidIdentifier(s) && !context.contains(s);
    }

    // Method to check if the right-hand side is a valid assignment
    public static boolean isValidAssignment(String left, String right, Context context) {
        // Right side must be either a valid identifier or numeric value
        return isValidIdentifier(right) || isNumeric(right, context);
    }

    // Method to check if a string is a valid numeric value
    public static boolean isNumeric(String s, Context context) {
        // Check for an empty string
        if (s.isEmpty()) {
            return false;
        }

        if (context.contains(s)) {
            return true;
        }

        // Check if each character is a digit (allow negative sign at the start)
        int start = 0;
        if (s.charAt(0) == '-') {
            if (s.length() == 1) {
                return false; // "-" is not a valid number
            }
            start = 1; // Start checking digits from index 1
        }

        for (int i = start; i < s.length(); i++) {
            if (!Character.isDigit(s.charAt(i))) {
                return false;
            }
        }

        return true;
    }


    private static boolean isValidCommand(String expression) {
        return !(expression.startsWith("/") &&
                doesNotContainCommand(expression));
    }

    private static boolean doesNotContainCommand(String expression) {
        return !(expression.contains("help") || expression.contains("exit"));
    }

    // Function to check if the expression is valid
    private static boolean isValidExpression(String expression, Context context) {
        // Trim the input to remove leading and trailing spaces
        expression = expression.trim();

        // Empty input is not a valid expression
        if (expression.isEmpty()) {
            return false;
        }

        // Check if the expression is a valid assignment
        if (isAssignment(expression)) {
            return true;
        }

        // Split the expression by whitespace and keep operators as separate elements
        // Example: "7 + 5 - 3" -> ["7", "+", "5", "-", "3"]
        String[] parts = expression.split("(?=[+-,*/()])|(?<=[+-,*/()])|\\s+");

        boolean expectingNumber = true; // Initially, we expect a number
        int openBracketsCount = 0; // To count open brackets

        for (String part : parts) {
            // Trim each part to ensure no trailing spaces affect the check
            part = part.trim();
            if (part.isEmpty()) continue; // Ignore empty parts from splitting

            // Check for an opening bracket '('
            if (part.equals("(")) {
                if (!expectingNumber) {
                    return false; // Invalid if we expect an operator or another bracket before '('
                }
                openBracketsCount++; // Increment open brackets count
                expectingNumber = true; // After an opening bracket, expect a number or subexpression
            }
            // Check for a closing bracket ')'
            else if (part.equals(")")) {
                if (expectingNumber) {
                    return false; // Invalid if we expect a number and encounter a ')'
                }
                openBracketsCount--; // Decrement open brackets count
                if (openBracketsCount < 0) {
                    return false; // More closing brackets than opening brackets
                }
                expectingNumber = false; // After a closing bracket, expect an operator or end of expression
            }
            // Check for a number
            else if (expectingNumber) {
                if (isNumeric(part, context)) {
                    expectingNumber = false; // After a number, we expect an operator
                } else {
                    return false; // If it's not a valid number, the expression is invalid
                }
            }
            // If we don't expect a number, we must expect an operator
            else {
                if (part.equals("+") || part.equals("-") || part.equals("*") || part.equals("/")) {
                    expectingNumber = true; // After an operator, we expect a number next
                } else {
                    return false; // Invalid part, should be an operator or number
                }
            }
        }

        // If we end expecting a number, but no number was provided, the expression is invalid
        return !expectingNumber && openBracketsCount == 0; // Expression is valid if brackets match
    }


    private static boolean isAssignment(String expression) {
        return expression.contains("=");
    }


    // Function to print the help message
    private static void printHelp() {
        System.out.println("The program calculates the sum of numbers.");
        System.out.println("You can enter an expression with '+' and '-' operators.");
        System.out.println("Example: 4 + 6 - 8, 2 - 3 - 4");
        System.out.println("Use '/exit' to quit the program.");
    }
}
