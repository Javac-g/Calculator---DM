package com.denysov.calculator.controller;

import com.denysov.calculator.model.Solution;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.List;

public class SolutionController {

    private final List<Solution> solutions = new ArrayList<>();

    private static final MathContext MATH_CONTEXT =
            MathContext.DECIMAL128;

    public BigDecimal add(
            BigDecimal a,
            BigDecimal b
    ) {
        return a.add(b);
    }

    public BigDecimal subtract(
            BigDecimal a,
            BigDecimal b
    ) {
        return a.subtract(b);
    }

    public BigDecimal multiply(
            BigDecimal a,
            BigDecimal b
    ) {
        return a.multiply(b);
    }

    public BigDecimal divide(
            BigDecimal a,
            BigDecimal b
    ) {

        if (b.compareTo(BigDecimal.ZERO) == 0) {
            throw new ArithmeticException(
                    "Division by zero"
            );
        }

        return a.divide(
                b,
                MATH_CONTEXT
        );
    }

    public BigDecimal power(
            BigDecimal a,
            BigDecimal b
    ) {

        int exponent = b.intValueExact();

        return a.pow(
                exponent,
                MATH_CONTEXT
        );
    }

    public Solution calculate(
            BigDecimal a,
            String operand,
            BigDecimal b
    ) {

        if (a == null) {
            throw new IllegalArgumentException(
                    "A cannot be null"
            );
        }

        if (b == null) {
            throw new IllegalArgumentException(
                    "B cannot be null"
            );
        }

        if (operand == null) {
            throw new IllegalArgumentException(
                    "Operand cannot be null"
            );
        }

        Solution solution =
                new Solution();

        solution.setA(a);
        solution.setB(b);
        solution.setOperand(operand);

        BigDecimal value =
                switch (operand) {

                    case "+" ->
                            add(a, b);

                    case "-" ->
                            subtract(a, b);

                    case "*" ->
                            multiply(a, b);

                    case "/" ->
                            divide(a, b);

                    case "^" ->
                            power(a, b);

                    default ->
                            throw new IllegalArgumentException(
                                    "Unknown operand: "
                                            + operand
                            );
                };

        solution.setValue(value);

        solutions.add(solution);

        return solution;
    }

    public List<Solution> getSolutions() {
        return solutions;
    }
}