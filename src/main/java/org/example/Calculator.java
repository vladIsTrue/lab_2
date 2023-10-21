/**
 * The Calculator class provides a method to calculate mathematical expressions
 * involving the operators +, -, *, /, as well as variables and parentheses.
 */
package org.example;

import java.util.ArrayList;
import java.util.List;

public class Calculator {

    /**
     * Calculates the result of a mathematical expression based on the input data.
     *
     * @param expressionText The string representing the mathematical expression.
     * @param variables A string containing variables and their values.
     * @return The result of evaluating the mathematical expression.
     */
    public static Integer calculate(String expressionText, String variables) {

        if (!variables.isEmpty())
        {
            String[] parts = variables.split(" ");

            for (int i = 0; i < parts.length; i += 2) {
                expressionText = expressionText.replace(parts[i], parts[i + 1]);
            }
        }

        try {
            List<Calculator.Token> tokens = analyzeTokens(expressionText);
            Calculator.TokenBuffer tokenBuffer = new Calculator.TokenBuffer(tokens);
            return evaluateExpression(tokenBuffer);
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    /**
     * Enum TokenType defines the types of tokens that can appear in a mathematical expression.
     */
    public enum TokenType {
        LEFT_BRACKET, RIGHT_BRACKET,
        OP_PLUS, OP_MINUS, OP_MUL, OP_DIV,
        NUMBER,
        EOF
    }

    /**
     * The Token class represents a token used for analyzing a mathematical expression.
     */
    public static class Token {
        Calculator.TokenType type;
        String value;

        public Token(Calculator.TokenType type, String value) {
            this.type = type;
            this.value = value;
        }

        public Token(Calculator.TokenType type, Character value) {
            this.type = type;
            this.value = value.toString();
        }

        @Override
        public String toString() {
            return "Token{" +
                    "type=" + type +
                    ", value='" + value + '\'' +
                    '}';
        }
    }

    /**
     * The TokenBuffer class represents a token buffer for expression analysis.
     */
    public static class TokenBuffer {
        private int position;

        public List<Calculator.Token> tokens;

        public TokenBuffer(List<Calculator.Token> tokens) {
            this.tokens = tokens;
        }

        public Calculator.Token next() {
            return tokens.get(position++);
        }

        public void back() {
            position--;
        }

        public int getPosition() {
            return position;
        }
    }

    /**
     * Analyzes the input string and breaks it into tokens for further processing.
     *
     * @param expText The string containing a mathematical expression.
     * @return A list of tokens obtained from analyzing the expression.
     */
    public static List<Calculator.Token> analyzeTokens(String expText) {
        ArrayList<Calculator.Token> tokens = new ArrayList<>();
        int pos = 0;
        while (pos < expText.length()) {
            char c = expText.charAt(pos);
            switch (c) {
                case '(':
                    tokens.add(new Calculator.Token(Calculator.TokenType.LEFT_BRACKET, c));
                    pos++;
                    continue;
                case ')':
                    tokens.add(new Calculator.Token(Calculator.TokenType.RIGHT_BRACKET, c));
                    pos++;
                    continue;
                case '+':
                    tokens.add(new Calculator.Token(Calculator.TokenType.OP_PLUS, c));
                    pos++;
                    continue;
                case '-':
                    tokens.add(new Calculator.Token(Calculator.TokenType.OP_MINUS, c));
                    pos++;
                    continue;
                case '*':
                    tokens.add(new Calculator.Token(Calculator.TokenType.OP_MUL, c));
                    pos++;
                    continue;
                case '/':
                    tokens.add(new Calculator.Token(Calculator.TokenType.OP_DIV, c));
                    pos++;
                    continue;
                default:
                    if (c <= '9' && c >= '0') {
                        StringBuilder sb = new StringBuilder();
                        do {
                            sb.append(c);
                            pos++;
                            if (pos >= expText.length()) {
                                break;
                            }
                            c = expText.charAt(pos);
                        } while (c <= '9' && c >= '0');
                        tokens.add(new Calculator.Token(Calculator.TokenType.NUMBER, sb.toString()));
                    } else {
                        if (c != ' ') {
                            throw new RuntimeException("Unexpected character: " + c);
                        }
                        pos++;
                    }
            }
        }
        tokens.add(new Calculator.Token(Calculator.TokenType.EOF, ""));
        return tokens;
    }

    /**
     * Evaluates the mathematical expression based on the token buffer.
     *
     * @param tokens The token buffer containing the tokens of the expression.
     * @return The result of evaluating the expression.
     */
    public static int evaluateExpression(Calculator.TokenBuffer tokens) {
        Calculator.Token token = tokens.next();
        if (token.type == Calculator.TokenType.EOF) {
            return 0;
        } else {
            tokens.back();
            return evaluatePlusMinus(tokens);
        }
    }

    /**
     * Evaluates addition and subtraction in the mathematical expression.
     *
     * @param tokens The token buffer containing the tokens of the expression.
     * @return The result of addition and subtraction.
     */
    public static int evaluatePlusMinus(Calculator.TokenBuffer tokens) {
        int value = evaluateMultDiv(tokens);
        while (true) {
            Calculator.Token token = tokens.next();
            switch (token.type) {
                case OP_PLUS:
                    value += evaluateMultDiv(tokens);
                    break;
                case OP_MINUS:
                    value -= evaluateMultDiv(tokens);
                    break;
                case EOF:
                case RIGHT_BRACKET:
                    tokens.back();
                    return value;
                default:
                    throw new RuntimeException("Unexpected token: " + token.value
                            + " at position: " + tokens.getPosition());
            }
        }
    }

    /**
     * Evaluates multiplication and division in the mathematical expression.
     *
     * @param tokens The token buffer containing the tokens of the expression.
     * @return The result of multiplication and division.
     */
    public static int evaluateMultDiv(Calculator.TokenBuffer tokens) {
        int value = evaluateFactor(tokens);
        while (true) {
            Calculator.Token token = tokens.next();
            switch (token.type) {
                case OP_MUL:
                    value *= evaluateFactor(tokens);
                    break;
                case OP_DIV:
                    value /= evaluateFactor(tokens);
                    break;
                case EOF:
                case RIGHT_BRACKET:
                case OP_PLUS:
                case OP_MINUS:
                    tokens.back();
                    return value;
                default:
                    throw new RuntimeException("Unexpected token: " + token.value
                            + " at position: " + tokens.getPosition());
            }
        }
    }

    /**
     * Evaluates factors, including numbers and parentheses in the mathematical expression.
     *
     * @param tokens The token buffer containing the tokens of the expression.
     * @return The result of evaluating a factor.
     */
    public static int evaluateFactor(Calculator.TokenBuffer tokens) {
        Calculator.Token token = tokens.next();
        switch (token.type) {
            case NUMBER:
                return Integer.parseInt(token.value);
            case LEFT_BRACKET:
                int value = evaluatePlusMinus(tokens);
                token = tokens.next();
                if (token.type != Calculator.TokenType.RIGHT_BRACKET) {
                    throw new RuntimeException("Unexpected token: " + token.value
                            + " at position: " + tokens.getPosition());
                }
                return value;
            default:
                throw new RuntimeException("Unexpected token: " + token.value
                        + " at position: " + tokens.getPosition());
        }
    }
}