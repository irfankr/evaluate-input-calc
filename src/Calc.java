import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Stack;
import java.util.TreeMap;
import java.util.Map;

/**
 * A simple calculator program reading arithmetic expressions from the standard
 * input, evaluating them, and printing the results on the standard output.
 */
public class Calc {
    /**
     * Evaluates an arithmetic expression. The grammar of accepted expressions
     * is the following:
     *
     * <code>
     *
     *   expr ::= factor | expr ('+' | '-') expr
     *   factor ::= term | factor ('*' | '/') factor
     *   term ::= '-' term | '(' expr ')' | number | id | function | binding
     *   number ::= int | decimal
     *   int ::= '0' | posint
     *   posint ::= ('1' - '9') | posint ('0' - '9')
     *   decimal ::= int '.' ('0' - '9') | '.' ('0' - '9')
     *   id ::= ('a' - 'z' | 'A' - 'Z' | '_') | id ('a' - 'z' | 'A' - 'Z' | '_' | '0' - '9')
     *   function ::= ('sqrt' | 'log' | 'sin' | 'cos') '(' expr ')'
     *   binding ::= id '=' expr
     *
     * </code>
     *
     * The binary operators are left-associative, with multiplication and division
     * taking precedence over addition and subtraction.
     *
     * Functions are implemented in terms of the respective static methods of
     * the class java.lang.Math.
     *
     * The bindings produced during the evaluation of the given expression
     * are stored in a map, where they remain available for the evaluation
     * of subsequent expressions.
     *
     * Before leaving this method, the value of the given expression is bound
     * to the special variable named "_".
     *
     * @param expr well-formed arithmetic expression
     * @return the value of the given expression
     */
    private final Map<String, Double> bindings = new TreeMap<>();

    private final static String[] availableFunctions = {"sin", "cos", "sqrt", "log"};

    public double eval(String expr) {
        Stack<Double> operands = new Stack<>();
        Stack<Character> operators = new Stack<>();
        StringBuilder unprocessedString = new StringBuilder();
        String binding = null;

        for (int i = 0; i < expr.length(); i++) {
            Character symbol = expr.charAt(i);

            unprocessedString.append(symbol);

            if (symbol >= '0' && symbol <= '9') {
                StringBuilder number = new StringBuilder(Character.toString(symbol));

                while (i+1 < expr.length() && ((expr.charAt(i+1) >= '0' && expr.charAt(i+1) <= '9') ||
                    expr.charAt(i+1) == '.')) {
                    i++;

                    if (i < expr.length()) {
                        number.append(expr.charAt(i));
                    }
                }

                operands.push(Double.parseDouble(number.toString()));
            } else if (symbol == '+' || symbol == '-' || symbol == '*' || symbol == '/') {

                while (!operators.empty() && !((symbol == '*' || symbol == '/') && (operators.peek() == '+' ||
                    operators.peek() == '-'))) {

                    if (operators.peek() == '(' || operators.peek() == ')') {
                        break;
                    }
                    operands.push(calculate(operators.pop(), operands.pop(), operands.pop()));
                }

                operators.push(symbol);
                unprocessedString.setLength(0);
            } else if (symbol == '=') {
                if (binding == null) {
                    binding = unprocessedString.substring(0, unprocessedString.length() - 1);
                } else {
                    throw new UnsupportedOperationException("Unsupported Operation");
                }
            } else if (Arrays.asList(availableFunctions).contains(unprocessedString.toString())) {
                int unclosedBrackets = 1;
                StringBuilder functionExp = new StringBuilder();
                i++;

                while (unclosedBrackets > 0) {
                    i++;

                    if (expr.charAt(i) == '(') {
                        unclosedBrackets++;
                    } else if (expr.charAt(i) == ')') {
                        unclosedBrackets--;
                    }

                    if (unclosedBrackets > 0) {
                        functionExp.append(expr.charAt(i));
                    }
                }

                Double functionResult = eval(functionExp.toString());
                operands.push(evalFunctionExpression(unprocessedString.toString(), functionResult));
                unprocessedString.setLength(0);
            } else if (bindings.get(unprocessedString.toString()) != null) {
                operands.push(bindings.get(unprocessedString.toString()));
            } else if (symbol == '(') {
                operators.push('(');
                unprocessedString.setLength(0);
            } else if (symbol == ')') {
                while (operators.peek() != '(') {
                    operands.push(calculate(operators.pop(), operands.pop(), operands.pop()));
                }

                operators.pop();
                unprocessedString.setLength(0);
            }
        }

        while (!operators.empty()) {
            if (operands.size() < 2) {
                throw new UnsupportedOperationException("Unsupported Operation");
            }
            operands.push(calculate(operators.pop(), operands.pop(), operands.pop()));
        }
        if (operands.size() == 0) {
            throw new UnsupportedOperationException("Unsupported Operation");
        }

        Double result = operands.pop();
        if (binding != null) {
            bindings.put(binding, result);
        }
        bindings.put("_", result);

        return result;
    }

    private Double evalFunctionExpression(String function, Double operand) {
        switch (function) {
            case "sin":
                return Math.sin(operand);
            case "cos":
                return Math.cos(operand);
            case "sqrt":
                return Math.sqrt(operand);
            case "log":
                return Math.log(operand);
            default:
                return null;
        }
    }

    private Double calculate(Character operator, Double operand1, Double operand2) {
        Double baseOperand = operand2;

        switch (operator) {
            case '+':
                baseOperand += operand1;
                break;
            case '-':
                baseOperand -= operand1;
                break;
            case '*':
                baseOperand *= operand1;
                break;
            case '/':
                if (operand1 == 0) {
                    throw new UnsupportedOperationException("Division by zero is not allowed!");
                }
                baseOperand /= operand1;
                break;
            default:
                return null;
        }

        return baseOperand;
    }

    public Map<String,Double> bindings() {
        return bindings;
    }

    public static void main(String[] args) throws IOException {
        Calc calc = new Calc();
        try (BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
             PrintWriter out = new PrintWriter(System.out, true)) {
            while (true) {
                String line = in.readLine();
                if (line == null) {
                    break;
                }
                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }
                try {
                    if (!line.startsWith(":")) {
                        // handle expression
                        out.println(calc.eval(line));
                    } else {
                        // handle command
                        String[] command = line.split("\\s+", 2);
                        switch (command[0]) {
                            case ":vars":
                                calc.bindings().forEach((name, value) ->
                                        out.println(name + " = " + value));
                                break;
                            case ":clear":
                                if (command.length == 1) {
                                    // clear all
                                    calc.bindings().clear();
                                } else {
                                    // clear requested
                                    calc.bindings().keySet().removeAll(Arrays.asList(command[1].split("\\s+")));
                                }
                                break;
                            case ":exit":
                            case ":quit":
                                System.exit(0);
                                break;
                            default:
                                throw new RuntimeException("unrecognized command: " + line);
                        }
                    }
                } catch (Exception ex) {
                    System.err.println("*** ERROR: " + ex.getMessage());
                }
            }
        }
    }
}