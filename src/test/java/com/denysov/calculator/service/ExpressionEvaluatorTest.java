package com.denysov.calculator.service;

import com.denysov.calculator.model.AngleMode;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class ExpressionEvaluatorTest {

    private final ExpressionEvaluator evaluator =
            new ExpressionEvaluator();

    @Test
    void shouldRespectMultiplicationPrecedence() {

        BigDecimal result =
                evaluator.evaluate("4 + 5 * 6");

        assertEquals(
                new BigDecimal("34"),
                result
        );
    }

    @Test
    void shouldRespectBrackets() {

        BigDecimal result =
                evaluator.evaluate("(4 + 5) * 6");

        assertEquals(
                new BigDecimal("54"),
                result
        );
    }

    @Test
    void shouldSupportNestedBrackets() {

        BigDecimal result =
                evaluator.evaluate(
                        "2 * (3 + (4 * 5))"
                );

        assertEquals(
                new BigDecimal("46"),
                result
        );
    }

    @Test
    void shouldEvaluatePower() {

        BigDecimal result =
                evaluator.evaluate("2 ^ 8");

        assertEquals(
                new BigDecimal("256"),
                result
        );
    }

    @Test
    void powerShouldBeRightAssociative() {

        BigDecimal result =
                evaluator.evaluate("2 ^ 3 ^ 2");

        assertEquals(
                new BigDecimal("512"),
                result
        );
    }

    @Test
    void shouldSupportNegativeExponent() {

        BigDecimal result =
                evaluator.evaluate("2 ^ -3");

        assertEquals(
                new BigDecimal("0.125"),
                result
        );
    }

    @Test
    void shouldSupportNegativeNumber() {

        BigDecimal result =
                evaluator.evaluate("-5 + 2");

        assertEquals(
                new BigDecimal("-3"),
                result
        );
    }

    @Test
    void shouldSupportNegativeBracketExpression() {

        BigDecimal result =
                evaluator.evaluate("-(4 + 5)");

        assertEquals(
                new BigDecimal("-9"),
                result
        );
    }

    @Test
    void shouldThrowForDivisionByZero() {

        assertThrows(
                ArithmeticException.class,
                () -> evaluator.evaluate(
                        "10 / (5 - 5)"
                )
        );
    }

    @Test
    void shouldThrowForMissingClosingBracket() {

        assertThrows(
                IllegalArgumentException.class,
                () -> evaluator.evaluate(
                        "(4 + 5"
                )
        );
    }

    @Test
    void shouldEvaluateFractionalExponent() {

        BigDecimal result =
                evaluator.evaluate(
                        "9 ^ 0.5"
                );

        assertEquals(
                0,
                result.compareTo(
                        new BigDecimal("3")
                )
        );
    }

    @Test
    void shouldEvaluateSinInDegrees() {

        BigDecimal result =
                evaluator.evaluate(
                        "sin(30)",
                        AngleMode.DEG
                );

        assertEquals(
                0,
                result.compareTo(
                        new BigDecimal("0.5")
                )
        );
    }
    @Test
    void shouldEvaluateCosInDegrees() {

        BigDecimal result =
                evaluator.evaluate(
                        "cos(180)",
                        AngleMode.DEG
                );

        assertEquals(
                0,
                result.compareTo(
                        new BigDecimal("-1")
                )
        );
    }
    @Test
    void shouldEvaluateTanInDegrees() {

        BigDecimal result =
                evaluator.evaluate(
                        "tan(45)",
                        AngleMode.DEG
                );

        assertEquals(
                0,
                result.compareTo(
                        new BigDecimal("1")
                )
        );
    }
    @Test
    void shouldEvaluatePiConstant() {

        BigDecimal result =
                evaluator.evaluate(
                        "pi"
                );

        assertEquals(
                Math.PI,
                result.doubleValue(),
                1.0E-15
        );
    }
    @Test
    void shouldEvaluateEulerConstant() {

        BigDecimal result =
                evaluator.evaluate(
                        "e"
                );

        assertEquals(
                Math.E,
                result.doubleValue(),
                1.0E-15
        );
    }
    @Test
    void shouldEvaluateSinWithPiInRadians() {

        BigDecimal result =
                evaluator.evaluate(
                        "sin(pi / 2)",
                        AngleMode.RAD
                );

        assertEquals(
                0,
                result.compareTo(
                        BigDecimal.ONE
                )
        );
    }
    @Test
    void shouldRejectUndefinedTangent() {

        assertThrows(
                ArithmeticException.class,
                () ->
                        evaluator.evaluate(
                                "tan(90)",
                                AngleMode.DEG
                        )
        );
    }


    @Test
    void shouldEvaluateFractionalExponentExpression() {

        BigDecimal result =
                evaluator.evaluate(
                        "27 ^ (1 / 3)"
                );

        assertEquals(
                3.0,
                result.doubleValue(),
                1.0E-12
        );
    }
    @Test
    void shouldEvaluateNaturalLogarithm() {

        BigDecimal result =
                evaluator.evaluate(
                        "ln(e)"
                );

        assertEquals(
                1.0,
                result.doubleValue(),
                1.0E-14
        );
    }

    @Test
    void shouldEvaluateBase10Logarithm() {

        BigDecimal result =
                evaluator.evaluate(
                        "log(1000)"
                );

        assertEquals(
                3.0,
                result.doubleValue(),
                1.0E-14
        );
    }

    @Test
    void shouldRejectLogarithmOfZero() {

        assertThrows(
                ArithmeticException.class,
                () ->
                        evaluator.evaluate(
                                "log(0)"
                        )
        );
    }

    @Test
    void shouldEvaluateAbsoluteValue() {

        BigDecimal result =
                evaluator.evaluate(
                        "abs(-25)"
                );

        assertEquals(
                0,
                result.compareTo(
                        new BigDecimal(
                                "25"
                        )
                )
        );
    }

    @Test
    void shouldEvaluateFactorial() {

        BigDecimal result =
                evaluator.evaluate(
                        "5!"
                );

        assertEquals(
                0,
                result.compareTo(
                        new BigDecimal(
                                "120"
                        )
                )
        );
    }

    @Test
    void shouldEvaluateZeroFactorial() {

        BigDecimal result =
                evaluator.evaluate(
                        "0!"
                );

        assertEquals(
                0,
                result.compareTo(
                        BigDecimal.ONE
                )
        );
    }

    @Test
    void shouldRejectFractionalFactorial() {

        assertThrows(
                ArithmeticException.class,
                () ->
                        evaluator.evaluate(
                                "2.5!"
                        )
        );
    }

    @Test
    void shouldApplyFactorialBeforePower() {

        BigDecimal result =
                evaluator.evaluate(
                        "2 ^ 3!"
                );

        assertEquals(
                0,
                result.compareTo(
                        new BigDecimal(
                                "64"
                        )
                )
        );
    }

}