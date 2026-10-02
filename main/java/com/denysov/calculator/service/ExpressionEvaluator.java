package com.denysov.calculator.service;

import com.denysov.calculator.model.AngleMode;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public class ExpressionEvaluator {

    private static final MathContext MATH_CONTEXT =
            MathContext.DECIMAL128;

    private static final MathContext TRANSCENDENTAL_CONTEXT =
            new MathContext(
                    15,
                    RoundingMode.HALF_EVEN
            );

    private static final BigDecimal PI =
            new BigDecimal(
                    Double.toString(Math.PI),
                    MATH_CONTEXT
            );

    private static final BigDecimal E =
            new BigDecimal(
                    Double.toString(Math.E),
                    MATH_CONTEXT
            );

    public BigDecimal evaluate(
            String expression
    ) {

        return evaluate(
                expression,
                AngleMode.RAD
        );
    }

    public BigDecimal evaluate(
            String expression,
            AngleMode angleMode
    ) {

        if (expression == null
                || expression.isBlank()) {

            throw new IllegalArgumentException(
                    "Expression cannot be empty"
            );
        }

        if (angleMode == null) {

            throw new IllegalArgumentException(
                    "Angle mode cannot be null"
            );
        }

        String normalized =
                expression
                        .replace("×", "*")
                        .replace("÷", "/")
                        .replace("−", "-")
                        .replace("√", "sqrt")
                        .replace("π", "pi")
                        .replaceAll("\\s+", "");

        Parser parser =
                new Parser(
                        normalized,
                        angleMode
                );

        return parser.parse();
    }

    private static class Parser {

        private final String expression;

        private final AngleMode angleMode;

        private int position;

        private Parser(
                String expression,
                AngleMode angleMode
        ) {

            this.expression =
                    expression;

            this.angleMode =
                    angleMode;

            this.position =
                    0;
        }

        private BigDecimal parse() {

            BigDecimal result =
                    parseExpression();

            if (position
                    != expression.length()) {

                throw new IllegalArgumentException(
                        "Unexpected character at position "
                                + position
                );
            }

            return result;
        }

        private BigDecimal parseExpression() {

            BigDecimal result =
                    parseTerm();

            while (position
                    < expression.length()) {

                char operator =
                        expression.charAt(
                                position
                        );

                if (operator != '+'
                        && operator != '-') {

                    break;
                }

                position++;

                BigDecimal right =
                        parseTerm();

                result =
                        switch (operator) {

                            case '+' ->
                                    result.add(
                                            right,
                                            MATH_CONTEXT
                                    );

                            case '-' ->
                                    result.subtract(
                                            right,
                                            MATH_CONTEXT
                                    );

                            default ->
                                    throw new IllegalStateException();
                        };
            }

            return result;
        }

        private BigDecimal parseTerm() {

            BigDecimal result =
                    parseUnary();

            while (position
                    < expression.length()) {

                char operator =
                        expression.charAt(
                                position
                        );

                if (operator != '*'
                        && operator != '/') {

                    break;
                }

                position++;

                BigDecimal right =
                        parseUnary();

                result =
                        switch (operator) {

                            case '*' ->
                                    result.multiply(
                                            right,
                                            MATH_CONTEXT
                                    );

                            case '/' ->
                                    divide(
                                            result,
                                            right
                                    );

                            default ->
                                    throw new IllegalStateException();
                        };
            }

            return result;
        }

        private BigDecimal parseUnary() {

            if (match('+')) {

                return parseUnary();
            }

            if (match('-')) {

                return parseUnary()
                        .negate(
                                MATH_CONTEXT
                        );
            }

            return parsePower();
        }

        private BigDecimal parsePower() {

            BigDecimal base =
                    parsePostfix();

            if (match('^')) {

                BigDecimal exponent =
                        parseUnary();

                return power(
                        base,
                        exponent
                );
            }

            return base;
        }

        private BigDecimal parsePostfix() {

            BigDecimal value =
                    parsePrimary();

            if (match('!')) {

                return factorial(
                        value
                );
            }

            return value;
        }

        private BigDecimal parsePrimary() {

            if (startsWith("sqrt")) {

                position +=
                        "sqrt".length();

                BigDecimal value =
                        parseFunctionArgument(
                                "sqrt"
                        );

                if (value.compareTo(
                        BigDecimal.ZERO
                ) < 0) {

                    throw new ArithmeticException(
                            "Cannot calculate square root of negative number"
                    );
                }

                return value.sqrt(
                        MATH_CONTEXT
                );
            }

            if (startsWith("sin")) {

                position +=
                        "sin".length();

                BigDecimal value =
                        parseFunctionArgument(
                                "sin"
                        );

                return calculateSin(
                        value
                );
            }

            if (startsWith("cos")) {

                position +=
                        "cos".length();

                BigDecimal value =
                        parseFunctionArgument(
                                "cos"
                        );

                return calculateCos(
                        value
                );
            }

            if (startsWith("tan")) {

                position +=
                        "tan".length();

                BigDecimal value =
                        parseFunctionArgument(
                                "tan"
                        );

                return calculateTan(
                        value
                );
            }

            if (startsWith("ln")) {

                position +=
                        "ln".length();

                BigDecimal value =
                        parseFunctionArgument(
                                "ln"
                        );

                return calculateLn(
                        value
                );
            }

            if (startsWith("log")) {

                position +=
                        "log".length();

                BigDecimal value =
                        parseFunctionArgument(
                                "log"
                        );

                return calculateLog10(
                        value
                );
            }

            if (startsWith("abs")) {

                position +=
                        "abs".length();

                BigDecimal value =
                        parseFunctionArgument(
                                "abs"
                        );

                return value.abs(
                        MATH_CONTEXT
                );
            }

            if (startsWith("pi")) {

                position += 2;

                return PI;
            }

            if (startsWith("e")) {

                position++;

                return E;
            }

            if (match('(')) {

                BigDecimal result =
                        parseExpression();

                if (!match(')')) {

                    throw new IllegalArgumentException(
                            "Missing closing bracket"
                    );
                }

                return result;
            }

            return parseNumber();
        }

        private BigDecimal parseFunctionArgument(
                String functionName
        ) {

            if (!match('(')) {

                throw new IllegalArgumentException(
                        "Expected '(' after "
                                + functionName
                );
            }

            BigDecimal value =
                    parseExpression();

            if (!match(')')) {

                throw new IllegalArgumentException(
                        "Missing closing bracket after "
                                + functionName
                );
            }

            return value;
        }

        private BigDecimal parseNumber() {

            int start =
                    position;

            boolean decimalFound =
                    false;

            while (position
                    < expression.length()) {

                char current =
                        expression.charAt(
                                position
                        );

                if (Character.isDigit(
                        current
                )) {

                    position++;

                    continue;
                }

                if (current == '.'
                        && !decimalFound) {

                    decimalFound =
                            true;

                    position++;

                    continue;
                }

                break;
            }

            if (start == position) {

                throw new IllegalArgumentException(
                        "Expected number at position "
                                + position
                );
            }

            String number =
                    expression.substring(
                            start,
                            position
                    );

            try {

                return new BigDecimal(
                        number
                );

            } catch (
                    NumberFormatException exception
            ) {

                throw new IllegalArgumentException(
                        "Invalid number: "
                                + number
                );
            }
        }

        private BigDecimal calculateSin(
                BigDecimal value
        ) {

            double radians =
                    toRadians(
                            value
                    );

            double result =
                    Math.sin(
                            radians
                    );

            return fromDouble(
                    normalizeTrigResult(
                            result
                    )
            );
        }

        private BigDecimal calculateCos(
                BigDecimal value
        ) {

            double radians =
                    toRadians(
                            value
                    );

            double result =
                    Math.cos(
                            radians
                    );

            return fromDouble(
                    normalizeTrigResult(
                            result
                    )
            );
        }

        private BigDecimal calculateTan(
                BigDecimal value
        ) {

            double radians =
                    toRadians(
                            value
                    );

            double cosine =
                    Math.cos(
                            radians
                    );

            if (Math.abs(cosine)
                    < 1.0E-14) {

                throw new ArithmeticException(
                        "Tangent is undefined for this angle"
                );
            }

            double result =
                    Math.tan(
                            radians
                    );

            if (!Double.isFinite(
                    result
            )) {

                throw new ArithmeticException(
                        "Invalid tangent result"
                );
            }

            return fromDouble(
                    normalizeTrigResult(
                            result
                    )
            );
        }

        private BigDecimal calculateLn(
                BigDecimal value
        ) {

            if (value.compareTo(
                    BigDecimal.ZERO
            ) <= 0) {

                throw new ArithmeticException(
                        "Natural logarithm requires a positive number"
                );
            }

            double result =
                    Math.log(
                            value.doubleValue()
                    );

            return fromDouble(
                    result
            );
        }

        private BigDecimal calculateLog10(
                BigDecimal value
        ) {

            if (value.compareTo(
                    BigDecimal.ZERO
            ) <= 0) {

                throw new ArithmeticException(
                        "Logarithm requires a positive number"
                );
            }

            double result =
                    Math.log10(
                            value.doubleValue()
                    );

            return fromDouble(
                    result
            );
        }

        private BigDecimal factorial(
                BigDecimal value
        ) {

            final int number;

            try {

                number =
                        value.intValueExact();

            } catch (
                    ArithmeticException exception
            ) {

                throw new ArithmeticException(
                        "Factorial requires an integer"
                );
            }

            if (number < 0) {

                throw new ArithmeticException(
                        "Factorial requires a non-negative number"
                );
            }

            if (number > 10_000) {

                throw new ArithmeticException(
                        "Factorial value is too large"
                );
            }

            BigDecimal result =
                    BigDecimal.ONE;

            for (int i = 2;
                 i <= number;
                 i++) {

                result =
                        result.multiply(
                                BigDecimal.valueOf(
                                        i
                                )
                        );
            }

            return result;
        }

        private double toRadians(
                BigDecimal value
        ) {

            double number =
                    value.doubleValue();

            if (angleMode
                    == AngleMode.DEG) {

                return Math.toRadians(
                        number
                );
            }

            return number;
        }

        private double normalizeTrigResult(
                double value
        ) {

            if (Math.abs(value)
                    < 1.0E-14) {

                return 0.0;
            }

            if (Math.abs(
                    value - 0.5
            ) < 1.0E-14) {

                return 0.5;
            }

            if (Math.abs(
                    value + 0.5
            ) < 1.0E-14) {

                return -0.5;
            }

            if (Math.abs(
                    value - 1.0
            ) < 1.0E-14) {

                return 1.0;
            }

            if (Math.abs(
                    value + 1.0
            ) < 1.0E-14) {

                return -1.0;
            }

            return value;
        }

        private BigDecimal fromDouble(
                double value
        ) {

            if (!Double.isFinite(
                    value
            )) {

                throw new ArithmeticException(
                        "Invalid mathematical result"
                );
            }

            BigDecimal result =
                    BigDecimal.valueOf(
                                    value
                            )
                            .round(
                                    TRANSCENDENTAL_CONTEXT
                            )
                            .stripTrailingZeros();

            if (result.compareTo(
                    BigDecimal.ZERO
            ) == 0) {

                return BigDecimal.ZERO;
            }

            return result;
        }

        private boolean startsWith(
                String value
        ) {

            return expression.startsWith(
                    value,
                    position
            );
        }

        private boolean match(
                char expected
        ) {

            if (position
                    >= expression.length()) {

                return false;
            }

            if (expression.charAt(
                    position
            ) != expected) {

                return false;
            }

            position++;

            return true;
        }

        private BigDecimal divide(
                BigDecimal a,
                BigDecimal b
        ) {

            if (b.compareTo(
                    BigDecimal.ZERO
            ) == 0) {

                throw new ArithmeticException(
                        "Division by zero"
                );
            }

            return a.divide(
                    b,
                    MATH_CONTEXT
            );
        }

        private BigDecimal power(
                BigDecimal base,
                BigDecimal exponent
        ) {

            try {

                int exponentValue =
                        exponent.intValueExact();

                if (exponentValue >= 0) {

                    return base.pow(
                            exponentValue,
                            MATH_CONTEXT
                    );
                }

                if (base.compareTo(
                        BigDecimal.ZERO
                ) == 0) {

                    throw new ArithmeticException(
                            "Zero cannot have a negative exponent"
                    );
                }

                if (exponentValue
                        == Integer.MIN_VALUE) {

                    throw new ArithmeticException(
                            "Exponent is too small"
                    );
                }

                BigDecimal positivePower =
                        base.pow(
                                Math.abs(
                                        exponentValue
                                ),
                                MATH_CONTEXT
                        );

                return BigDecimal.ONE.divide(
                        positivePower,
                        MATH_CONTEXT
                );

            } catch (
                    ArithmeticException exception
            ) {

                if (exponent.scale() <= 0
                        || exponent
                        .stripTrailingZeros()
                        .scale() <= 0) {

                    throw exception;
                }
            }

            double baseValue =
                    base.doubleValue();

            double exponentValue =
                    exponent.doubleValue();

            if (baseValue < 0) {

                throw new ArithmeticException(
                        "Fractional power of a negative number is not supported"
                );
            }

            double result =
                    Math.pow(
                            baseValue,
                            exponentValue
                    );

            if (!Double.isFinite(
                    result
            )) {

                throw new ArithmeticException(
                        "Invalid power result"
                );
            }

            return fromDouble(
                    result
            );
        }
    }
}