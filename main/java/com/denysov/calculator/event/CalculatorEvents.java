package com.denysov.calculator.event;

import com.denysov.calculator.model.HistoryEntry;
import com.denysov.calculator.service.ExpressionEvaluator;
import com.denysov.calculator.view.CalculatorView;
import com.denysov.calculator.model.AngleMode;
import javafx.scene.control.Button;
import javafx.scene.control.TextInputControl;
import javafx.scene.input.KeyEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import com.denysov.calculator.service.SoundService;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.nio.file.Files;
import java.util.List;

public class CalculatorEvents {

    private static final MathContext MATH_CONTEXT =
            MathContext.DECIMAL128;

    private final CalculatorView view;
    private final SoundService soundService;
    private final ExpressionEvaluator expressionEvaluator;
    private AngleMode angleMode;
    private final StringBuilder expression;

    private boolean resultShown;

    private double dragOffsetX;
    private double dragOffsetY;
    private enum UnaryFunction {

        SQUARE_ROOT,
        SQUARE,
        RECIPROCAL,

        SIN,
        COS,
        TAN,

        LN,
        LOG,
        ABSOLUTE
    }
    private void applyUnaryFunction(
            UnaryFunction function
    ) {

        if (expression.isEmpty()) {

            return;
        }

        int operandStart =
                findCurrentOperandStart();

        if (operandStart < 0
                || operandStart
                >= expression.length()) {

            return;
        }

        String operand =
                expression.substring(
                        operandStart
                );

        if (operand.isBlank()) {

            return;
        }

        boolean standalone =
                operandStart == 0;

        String transformed =
                switch (function) {

                    case SQUARE_ROOT ->
                            "sqrt("
                                    + operand
                                    + ")";

                    case SQUARE ->
                            "("
                                    + operand
                                    + ")^2";

                    case RECIPROCAL ->
                            "1/("
                                    + operand
                                    + ")";

                    case SIN ->
                            "sin("
                                    + operand
                                    + ")";

                    case COS ->
                            "cos("
                                    + operand
                                    + ")";

                    case TAN ->
                            "tan("
                                    + operand
                                    + ")";

                    case LN ->
                            "ln("
                                    + operand
                                    + ")";

                    case LOG ->
                            "log("
                                    + operand
                                    + ")";

                    case ABSOLUTE ->
                            "abs("
                                    + operand
                                    + ")";
                };

        try {

            BigDecimal result =
                    expressionEvaluator.evaluate(
                            transformed,
                            angleMode
                    );

            expression.replace(
                    operandStart,
                    expression.length(),
                    transformed
            );

            String formattedResult =
                    formatNumber(
                            result
                    );

            view.getDisplay()
                    .setText(
                            formattedResult
                    );

            updateExpressionDisplay();

            if (standalone) {

                addExpressionHistory(
                        transformed,
                        result
                );

                resultShown =
                        true;

            } else {

                resultShown =
                        false;
            }

        } catch (
                ArithmeticException
                | IllegalArgumentException exception
        ) {

            view.getExpressionDisplay()
                    .setText(
                            exception.getMessage()
                    );
        }
    }

    private int findCurrentOperandStart() {

        if (expression.isEmpty()) {
            return -1;
        }

        int end =
                expression.length() - 1;

        char last =
                expression.charAt(
                        end
                );

        /*
         * Current operand ends with ):
         *
         * (4+5)
         *
         * Find the matching opening bracket.
         */
        if (last == ')') {

            int depth =
                    0;

            for (int i = end;
                 i >= 0;
                 i--) {

                char current =
                        expression.charAt(
                                i
                        );

                if (current == ')') {

                    depth++;

                } else if (current == '(') {

                    depth--;

                    if (depth == 0) {

                        int start =
                                i;

                        /*
                         * Include sqrt before:
                         *
                         * sqrt(...)
                         */
                        String prefix =
                                "sqrt";

                        if (start
                                >= prefix.length()) {

                            int prefixStart =
                                    start
                                            - prefix.length();

                            String possibleFunction =
                                    expression.substring(
                                            prefixStart,
                                            start
                                    );

                            if (possibleFunction.equals(
                                    prefix
                            )) {

                                start =
                                        prefixStart;
                            }
                        }

                        /*
                         * Include unary minus:
                         *
                         * -(4+5)
                         */
                        if (start > 0
                                && expression.charAt(
                                start - 1
                        ) == '-'
                                && isUnaryMinus(
                                start - 1
                        )) {

                            start--;
                        }

                        return start;
                    }
                }
            }

            return -1;
        }

        /*
         * Normal number:
         *
         * 12
         * -12
         * 12.5
         */
        int index =
                end;

        while (index >= 0) {

            char current =
                    expression.charAt(
                            index
                    );

            if (Character.isDigit(
                    current
            )
                    || current == '.') {

                index--;

            } else {

                break;
            }
        }

        int start =
                index + 1;

        /*
         * Include unary minus:
         *
         * -12
         *
         * but don't include subtraction:
         *
         * 5-12
         */
        if (start > 0
                && expression.charAt(
                start - 1
        ) == '-'
                && isUnaryMinus(
                start - 1
        )) {

            start--;
        }

        return start;
    }
    ////////////////////Constructor////////////////////
    public CalculatorEvents(
            CalculatorView view
    ) {
        this.soundService =
                new SoundService();

        this.angleMode =
                AngleMode.DEG;
        this.view =
                view;

        this.expressionEvaluator =
                new ExpressionEvaluator();

        this.expression =
                new StringBuilder();

        this.resultShown =
                false;
    }
    private void registerButtonSounds() {

        List<Button> normalButtons =
                List.of(
                        view.getButton0(),
                        view.getButton1(),
                        view.getButton2(),
                        view.getButton3(),
                        view.getButton4(),
                        view.getButton5(),
                        view.getButton6(),
                        view.getButton7(),
                        view.getButton8(),
                        view.getButton9(),

                        view.getDecimalButton(),

                        view.getAddButton(),
                        view.getSubtractButton(),
                        view.getMultiplyButton(),
                        view.getDivideButton(),

                        view.getClearButton(),
                        view.getBackspaceButton(),
                        view.getSignButton(),

                        view.getOpenBracketButton(),
                        view.getCloseBracketButton(),

                        view.getReciprocalButton(),
                        view.getSqrtButton(),
                        view.getSquareButton(),
                        view.getPowerButton(),

                        view.getSinButton(),
                        view.getCosButton(),
                        view.getTanButton(),

                        view.getPiButton(),
                        view.getEButton(),
                        view.getAngleModeButton(),

                        view.getLnButton(),
                        view.getLogButton(),
                        view.getAbsoluteButton(),
                        view.getFactorialButton(),

                        view.getClearHistoryButton(),
                        view.getExportHistoryButton()
                );

        for (Button button :
                normalButtons) {

            button.addEventHandler(
                    javafx.event.ActionEvent.ACTION,
                    event ->
                            soundService.playClick()
            );
        }

        view.getEqualsButton()
                .addEventHandler(
                        javafx.event.ActionEvent.ACTION,
                        event ->
                                soundService.playEquals()
                );
    }
    public void registerEvents() {
        registerTrigonometricButtons();

        registerConstantButtons();

        registerAngleModeButton();
        registerNumberButtons();
        registerAdditionalScientificButtons();
        registerDecimalButton();
        registerButtonSounds();
        registerOperatorButton(
                view.getAddButton(),
                "+"
        );

        registerOperatorButton(
                view.getSubtractButton(),
                "-"
        );

        registerOperatorButton(
                view.getMultiplyButton(),
                "*"
        );

        registerOperatorButton(
                view.getDivideButton(),
                "/"
        );

        registerOperatorButton(
                view.getPowerButton(),
                "^"
        );

        registerEqualsButton();

        registerClearButton();

        registerBackspaceButton();

        registerSignButton();

        registerBracketButtons();

        registerReciprocalButton();

        registerSquareRootButton();

        registerSquareButton();

        registerClearHistoryButton();

        registerExportHistoryButton();
    }

    /*
     * =========================================================
     * NUMBERS
     * =========================================================
     */
    private void registerAdditionalScientificButtons() {

        view.getLnButton()
                .setOnAction(
                        event ->
                                applyUnaryFunction(
                                        UnaryFunction.LN
                                )
                );

        view.getLogButton()
                .setOnAction(
                        event ->
                                applyUnaryFunction(
                                        UnaryFunction.LOG
                                )
                );

        view.getAbsoluteButton()
                .setOnAction(
                        event ->
                                applyUnaryFunction(
                                        UnaryFunction.ABSOLUTE
                                )
                );

        view.getFactorialButton()
                .setOnAction(
                        event ->
                                applyFactorial()
                );
    }

    private void applyFactorial() {

        if (expression.isEmpty()) {

            return;
        }

        int operandStart =
                findCurrentOperandStart();

        if (operandStart < 0
                || operandStart
                >= expression.length()) {

            return;
        }

        String operand =
                expression.substring(
                        operandStart
                );

        if (operand.isBlank()) {

            return;
        }

        boolean standalone =
                operandStart == 0;

        String transformed =
                "("
                        + operand
                        + ")!";

        try {

            BigDecimal result =
                    expressionEvaluator.evaluate(
                            transformed,
                            angleMode
                    );

            expression.replace(
                    operandStart,
                    expression.length(),
                    transformed
            );

            String formattedResult =
                    formatNumber(
                            result
                    );

            view.getDisplay()
                    .setText(
                            formattedResult
                    );

            updateExpressionDisplay();

            if (standalone) {

                addExpressionHistory(
                        transformed,
                        result
                );

                resultShown =
                        true;

            } else {

                resultShown =
                        false;
            }

        } catch (
                ArithmeticException
                | IllegalArgumentException exception
        ) {

            view.getExpressionDisplay()
                    .setText(
                            exception.getMessage()
                    );
        }
    }
    private void registerTrigonometricButtons() {

        view.getSinButton()
                .setOnAction(
                        event ->
                                applyUnaryFunction(
                                        UnaryFunction.SIN
                                )
                );

        view.getCosButton()
                .setOnAction(
                        event ->
                                applyUnaryFunction(
                                        UnaryFunction.COS
                                )
                );

        view.getTanButton()
                .setOnAction(
                        event ->
                                applyUnaryFunction(
                                        UnaryFunction.TAN
                                )
                );
    }
    private void registerNumberButtons() {

        registerNumberButton(
                view.getButton0(),
                "0"
        );

        registerNumberButton(
                view.getButton1(),
                "1"
        );

        registerNumberButton(
                view.getButton2(),
                "2"
        );

        registerNumberButton(
                view.getButton3(),
                "3"
        );

        registerNumberButton(
                view.getButton4(),
                "4"
        );

        registerNumberButton(
                view.getButton5(),
                "5"
        );

        registerNumberButton(
                view.getButton6(),
                "6"
        );

        registerNumberButton(
                view.getButton7(),
                "7"
        );

        registerNumberButton(
                view.getButton8(),
                "8"
        );

        registerNumberButton(
                view.getButton9(),
                "9"
        );
    }

    private void registerNumberButton(
            Button button,
            String number
    ) {

        button.setOnAction(
                event ->
                        numberPressed(
                                number
                        )
        );
    }

    private void numberPressed(
            String number
    ) {

        if (resultShown) {

            expression.setLength(0);

            resultShown =
                    false;
        }

        if (!expression.isEmpty()) {

            char last =
                    lastCharacter();

            if (last == ')') {

                expression.append(
                        '*'
                );
            }
        }

        expression.append(
                number
        );

        updateDisplayFromCurrentEntry();

        updateExpressionDisplay();
    }

    /*
     * =========================================================
     * DECIMAL
     * =========================================================
     */

    private void registerDecimalButton() {

        view.getDecimalButton()
                .setOnAction(
                        event ->
                                decimalPressed()
                );
    }

    private void decimalPressed() {

        if (resultShown) {

            expression.setLength(0);

            resultShown =
                    false;
        }

        if (expression.isEmpty()) {

            expression.append(
                    "0."
            );

            view.getDisplay()
                    .setText(
                            "0."
                    );

            updateExpressionDisplay();

            return;
        }

        char last =
                lastCharacter();

        if (last == ')') {

            expression.append(
                    "*0."
            );

            view.getDisplay()
                    .setText(
                            "0."
                    );

            updateExpressionDisplay();

            return;
        }

        if (isOperator(last)
                || last == '(') {

            expression.append(
                    "0."
            );

            view.getDisplay()
                    .setText(
                            "0."
                    );

            updateExpressionDisplay();

            return;
        }

        String currentNumber =
                getCurrentNumber();

        if (currentNumber.contains(".")) {
            return;
        }

        expression.append('.');

        updateDisplayFromCurrentEntry();

        updateExpressionDisplay();
    }

    /*
     * =========================================================
     * OPERATORS
     * =========================================================
     */

    private void registerOperatorButton(
            Button button,
            String operator
    ) {

        button.setOnAction(
                event ->
                        operationPressed(
                                operator
                        )
        );
    }

    private void operationPressed(
            String operator
    ) {

        if (expression.isEmpty()) {

            if (operator.equals("-")) {

                expression.append('-');

                updateExpressionDisplay();
            }

            return;
        }

        if (resultShown) {

            resultShown =
                    false;
        }

        char last =
                lastCharacter();

        if (last == '(') {

            if (operator.equals("-")) {

                expression.append('-');

                updateExpressionDisplay();
            }

            return;
        }

        if (isOperator(last)) {

            /*
             * Preserve negative exponent syntax:
             *
             * 2^-3
             *
             * Otherwise normal repeated operators
             * replace one another.
             */
            if (operator.equals("-")
                    && last == '^') {

                expression.append('-');

            } else {

                expression.setCharAt(
                        expression.length() - 1,
                        operator.charAt(0)
                );
            }

            updateExpressionDisplay();

            return;
        }

        if (last == '.') {

            expression.append('0');
        }

        expression.append(
                operator
        );

        updateExpressionDisplay();
    }

    /*
     * =========================================================
     * BRACKETS
     * =========================================================
     */

    private void registerBracketButtons() {

        view.getOpenBracketButton()
                .setOnAction(
                        event ->
                                openBracket()
                );

        view.getCloseBracketButton()
                .setOnAction(
                        event ->
                                closeBracket()
                );
    }

    private void openBracket() {

        if (resultShown) {

            expression.setLength(0);

            resultShown =
                    false;
        }

        if (!expression.isEmpty()) {

            char last =
                    lastCharacter();

            if (Character.isDigit(last)
                    || last == '.'
                    || last == ')') {

                expression.append(
                        '*'
                );
            }
        }

        expression.append(
                '('
        );

        view.getDisplay()
                .setText(
                        "0"
                );

        updateExpressionDisplay();
    }

    private void closeBracket() {

        if (expression.isEmpty()) {
            return;
        }

        if (countOpenBrackets()
                <= countCloseBrackets()) {

            return;
        }

        char last =
                lastCharacter();

        if (last == '('
                || isOperator(last)) {

            return;
        }

        if (last == '.') {

            expression.append('0');
        }

        expression.append(
                ')'
        );

        updateExpressionDisplay();
    }

    /*
     * =========================================================
     * EQUALS
     * =========================================================
     */

    private void registerEqualsButton() {

        view.getEqualsButton()
                .setOnAction(
                        event ->
                                evaluateExpression()
                );
    }

    private void evaluateExpression() {

        if (expression.isEmpty()) {
            return;
        }

        char last =
                lastCharacter();

        if (isOperator(last)
                || last == '(') {

            return;
        }

        String originalExpression =
                expression.toString();

        try {

            BigDecimal result =
                    expressionEvaluator.evaluate(
                            originalExpression,
                            angleMode
                    );

            String formattedResult =
                    formatNumber(
                            result
                    );

            view.getExpressionDisplay()
                    .setText(
                            visualExpression(
                                    originalExpression
                            )
                                    + " ="
                    );

            view.getDisplay()
                    .setText(
                            formattedResult
                    );

            addExpressionHistory(
                    originalExpression,
                    result
            );

            expression.setLength(0);

            expression.append(
                    formattedResult
            );

            resultShown =
                    true;

        } catch (ArithmeticException
                 | IllegalArgumentException exception) {

            view.getExpressionDisplay()
                    .setText(
                            exception.getMessage()
                    );
        }
    }

    /*
     * =========================================================
     * CLEAR
     * =========================================================
     */

    private void registerClearButton() {

        view.getClearButton()
                .setOnAction(
                        event ->
                                clearCalculator()
                );
    }
    private void registerConstantButtons() {

        view.getPiButton()
                .setOnAction(
                        event ->
                                constantPressed(
                                        "pi"
                                )
                );

        view.getEButton()
                .setOnAction(
                        event ->
                                constantPressed(
                                        "e"
                                )
                );
    }
    private void constantPressed(
            String constant
    ) {

        if (resultShown) {

            expression.setLength(0);

            resultShown =
                    false;
        }

        if (!expression.isEmpty()) {

            char last =
                    lastCharacter();

            /*
             * Implicit multiplication:
             *
             * 2π
             *
             * internally becomes:
             *
             * 2*pi
             *
             * Also:
             *
             * (2+3)π
             */
            if (Character.isDigit(last)
                    || last == '.'
                    || last == ')'
                    || endsWithConstant()) {

                expression.append(
                        '*'
                );
            }
        }

        expression.append(
                constant
        );

        try {

            BigDecimal value =
                    expressionEvaluator.evaluate(
                            constant,
                            angleMode
                    );

            view.getDisplay()
                    .setText(
                            formatNumber(
                                    value
                            )
                    );

        } catch (
                ArithmeticException
                | IllegalArgumentException exception
        ) {

            view.getExpressionDisplay()
                    .setText(
                            exception.getMessage()
                    );

            return;
        }

        updateExpressionDisplay();
    }
    private boolean endsWithConstant() {

        String source =
                expression.toString();

        return source.endsWith("pi")
                || source.endsWith("e");
    }
    private void registerAngleModeButton() {

        view.getAngleModeButton()
                .setOnAction(
                        event ->
                                toggleAngleMode()
                );
    }
    private void toggleAngleMode() {

        angleMode =
                switch (angleMode) {

                    case DEG ->
                            AngleMode.RAD;

                    case RAD ->
                            AngleMode.DEG;
                };

        view.getAngleModeButton()
                .setText(
                        angleMode.name()
                );
    }
    private void clearCalculator() {

        expression.setLength(0);

        resultShown =
                false;

        view.getDisplay()
                .setText(
                        "0"
                );

        view.getExpressionDisplay()
                .setText(
                        ""
                );
    }

    /*
     * =========================================================
     * BACKSPACE
     * =========================================================
     */

    private void registerBackspaceButton() {

        view.getBackspaceButton()
                .setOnAction(
                        event ->
                                backspace()
                );
    }

    private void backspace() {

        if (expression.isEmpty()) {
            return;
        }

        resultShown =
                false;

        expression.deleteCharAt(
                expression.length() - 1
        );

        if (expression.isEmpty()) {

            view.getDisplay()
                    .setText(
                            "0"
                    );

            view.getExpressionDisplay()
                    .setText(
                            ""
                    );

            return;
        }

        char last =
                lastCharacter();

        if (Character.isDigit(last)
                || last == '.') {

            updateDisplayFromCurrentEntry();

        } else {

            view.getDisplay()
                    .setText(
                            "0"
                    );
        }

        updateExpressionDisplay();
    }

    /*
     * =========================================================
     * SIGN +/-
     * =========================================================
     */

    private void registerSignButton() {

        view.getSignButton()
                .setOnAction(
                        event ->
                                toggleSign()
                );
    }

    private void toggleSign() {

        if (expression.isEmpty()) {

            expression.append(
                    "-0"
            );

            view.getDisplay()
                    .setText(
                            "-0"
                    );

            updateExpressionDisplay();

            return;
        }

        if (resultShown) {

            resultShown =
                    false;
        }

        char last =
                lastCharacter();

        if (!Character.isDigit(last)
                && last != '.') {

            return;
        }

        int numberStart =
                findCurrentNumberStart();

        int possibleMinusIndex =
                numberStart - 1;

        if (possibleMinusIndex >= 0
                && expression.charAt(
                possibleMinusIndex
        ) == '-'
                && isUnaryMinus(
                possibleMinusIndex
        )) {

            expression.deleteCharAt(
                    possibleMinusIndex
            );

        } else {

            expression.insert(
                    numberStart,
                    '-'
            );
        }

        updateDisplayFromCurrentEntry();

        updateExpressionDisplay();
    }

    /*
     * =========================================================
     * 1/x
     * =========================================================
     */

    private void registerReciprocalButton() {

        view.getReciprocalButton()
                .setOnAction(
                        event ->
                                applyReciprocal()
                );
    }

    private void applyReciprocal() {

        applyUnaryFunction(
                UnaryFunction.RECIPROCAL
        );
    }

    /*
     * =========================================================
     * SQRT
     * =========================================================
     */

    private void registerSquareRootButton() {

        view.getSqrtButton()
                .setOnAction(
                        event ->
                                applySquareRoot()
                );
    }

    private void applySquareRoot() {

        applyUnaryFunction(
                UnaryFunction.SQUARE_ROOT
        );
    }

    /*
     * =========================================================
     * x²
     * =========================================================
     */

    private void registerSquareButton() {

        view.getSquareButton()
                .setOnAction(
                        event ->
                                applySquare()
                );
    }

    private void applySquare() {

        applyUnaryFunction(
                UnaryFunction.SQUARE
        );
    }

    /*
     * =========================================================
     * UNARY RESULT REPLACEMENT
     * =========================================================
     */

    private void replaceCurrentNumber(
            BigDecimal result
    ) {

        String formatted =
                formatNumber(
                        result
                );

        if (expression.isEmpty()) {

            expression.append(
                    formatted
            );

        } else {

            char last =
                    lastCharacter();

            if (Character.isDigit(last)
                    || last == '.') {

                int start =
                        findSignedCurrentNumberStart();

                expression.replace(
                        start,
                        expression.length(),
                        formatted
                );

            } else {

                expression.append(
                        formatted
                );
            }
        }

        view.getDisplay()
                .setText(
                        formatted
                );

        updateExpressionDisplay();

        resultShown =
                false;
    }

    /*
     * =========================================================
     * HISTORY
     * =========================================================
     */

    private void addExpressionHistory(
            String sourceExpression,
            BigDecimal result
    ) {

        HistoryEntry historyEntry =
                new HistoryEntry(
                        visualExpression(
                                sourceExpression
                        ),
                        formatNumber(
                                result
                        )
                );

        view.getHistoryList()
                .getItems()
                .add(
                        0,
                        historyEntry
                );
    }

    private void registerClearHistoryButton() {

        view.getClearHistoryButton()
                .setOnAction(
                        event ->
                                view.getHistoryList()
                                        .getItems()
                                        .clear()
                );
    }

    /*
     * =========================================================
     * EXPORT
     * =========================================================
     */

    private void registerExportHistoryButton() {

        view.getExportHistoryButton()
                .setOnAction(
                        event -> {

                            if (view.getHistoryList()
                                    .getItems()
                                    .isEmpty()) {

                                return;
                            }

                            FileChooser fileChooser =
                                    new FileChooser();

                            fileChooser.setTitle(
                                    "Export Calculator History"
                            );

                            fileChooser.setInitialFileName(
                                    "calculator-history.txt"
                            );

                            fileChooser
                                    .getExtensionFilters()
                                    .addAll(

                                            new FileChooser.ExtensionFilter(
                                                    "Text File (*.txt)",
                                                    "*.txt"
                                            ),

                                            new FileChooser.ExtensionFilter(
                                                    "Log File (*.log)",
                                                    "*.log"
                                            ),

                                            new FileChooser.ExtensionFilter(
                                                    "Data File (*.dat)",
                                                    "*.dat"
                                            )
                                    );

                            File file =
                                    fileChooser.showSaveDialog(
                                            view.getRoot()
                                                    .getScene()
                                                    .getWindow()
                                    );

                            if (file == null) {
                                return;
                            }

                            FileChooser.ExtensionFilter filter =
                                    fileChooser
                                            .getSelectedExtensionFilter();

                            String extension =
                                    filter
                                            .getExtensions()
                                            .get(0)
                                            .replace(
                                                    "*",
                                                    ""
                                            );

                            if (!file.getName()
                                    .toLowerCase()
                                    .endsWith(
                                            extension
                                    )) {

                                file =
                                        new File(
                                                file.getAbsolutePath()
                                                        + extension
                                        );
                            }

                            exportHistory(
                                    file
                            );
                        }
                );
    }

    private void exportHistory(
            File file
    ) {

        List<String> lines =
                view.getHistoryList()
                        .getItems()
                        .stream()
                        .map(
                                HistoryEntry::toString
                        )
                        .toList();

        try {

            Files.write(
                    file.toPath(),
                    lines
            );

        } catch (IOException exception) {

            throw new RuntimeException(
                    "Failed to export calculator history",
                    exception
            );
        }
    }

    /*
     * =========================================================
     * KEYBOARD
     * =========================================================
     */

    public void registerKeyboard() {

        view.getRoot()
                .getScene()
                .addEventFilter(
                        KeyEvent.KEY_TYPED,
                        event -> {

                            /*
                             * IMPORTANT:
                             *
                             * If the user is typing inside a TextField
                             * (for example a history tag), do not send
                             * those characters to the calculator.
                             */
                            if (event.getTarget()
                                    instanceof TextInputControl) {

                                return;
                            }

                            String character =
                                    event.getCharacter();

                            switch (character) {

                                case "0", "1", "2",
                                     "3", "4", "5",
                                     "6", "7", "8",
                                     "9" -> {

                                    numberPressed(
                                            character
                                    );

                                    event.consume();
                                }

                                case "." -> {

                                    decimalPressed();

                                    event.consume();
                                }

                                case "+" -> {

                                    operationPressed(
                                            "+"
                                    );

                                    event.consume();
                                }

                                case "-" -> {

                                    operationPressed(
                                            "-"
                                    );

                                    event.consume();
                                }

                                case "*" -> {

                                    operationPressed(
                                            "*"
                                    );

                                    event.consume();
                                }

                                case "/" -> {

                                    operationPressed(
                                            "/"
                                    );

                                    event.consume();
                                }

                                case "^" -> {

                                    operationPressed(
                                            "^"
                                    );

                                    event.consume();
                                }

                                case "(" -> {

                                    openBracket();

                                    event.consume();
                                }

                                case ")" -> {

                                    closeBracket();

                                    event.consume();
                                }

                                case "=" -> {

                                    evaluateExpression();

                                    event.consume();
                                }
                            }
                        }
                );

        view.getRoot()
                .getScene()
                .addEventFilter(
                        KeyEvent.KEY_PRESSED,
                        event -> {

                            /*
                             * Same protection for:
                             *
                             * Backspace
                             * Delete
                             * Enter
                             * etc.
                             *
                             * The TextField must receive them normally.
                             */
                            if (event.getTarget()
                                    instanceof TextInputControl) {

                                return;
                            }

                            switch (event.getCode()) {

                                case ENTER -> {

                                    evaluateExpression();

                                    event.consume();
                                }

                                case BACK_SPACE -> {

                                    backspace();

                                    event.consume();
                                }

                                case ESCAPE,
                                     DELETE -> {

                                    clearCalculator();

                                    event.consume();
                                }
                            }
                        }
                );
    }

    /*
     * =========================================================
     * WINDOW CONTROLS
     * =========================================================
     */

    public void registerWindowControls(
            Stage stage
    ) {

        view.getCloseButton()
                .setOnAction(
                        event ->
                                stage.close()
                );

        view.getMinimizeButton()
                .setOnAction(
                        event ->
                                stage.setIconified(
                                        true
                                )
                );
    }

    public void registerWindowDragging(
            Stage stage
    ) {

        view.getRoot()
                .setOnMousePressed(
                        event -> {

                            dragOffsetX =
                                    event.getSceneX();

                            dragOffsetY =
                                    event.getSceneY();
                        }
                );

        view.getRoot()
                .setOnMouseDragged(
                        event -> {

                            stage.setX(
                                    event.getScreenX()
                                            - dragOffsetX
                            );

                            stage.setY(
                                    event.getScreenY()
                                            - dragOffsetY
                            );
                        }
                );
    }

    /*
     * =========================================================
     * HELPERS
     * =========================================================
     */

    private char lastCharacter() {

        return expression.charAt(
                expression.length() - 1
        );
    }

    private boolean isOperator(
            char character
    ) {

        return character == '+'
                || character == '-'
                || character == '*'
                || character == '/'
                || character == '^';
    }

    private int countOpenBrackets() {

        int count =
                0;

        for (int i = 0;
             i < expression.length();
             i++) {

            if (expression.charAt(i)
                    == '(') {

                count++;
            }
        }

        return count;
    }

    private int countCloseBrackets() {

        int count =
                0;

        for (int i = 0;
             i < expression.length();
             i++) {

            if (expression.charAt(i)
                    == ')') {

                count++;
            }
        }

        return count;
    }

    private int findCurrentNumberStart() {

        int index =
                expression.length() - 1;

        while (index >= 0) {

            char current =
                    expression.charAt(
                            index
                    );

            if (Character.isDigit(current)
                    || current == '.') {

                index--;

            } else {

                break;
            }
        }

        return index + 1;
    }

    private int findSignedCurrentNumberStart() {

        int start =
                findCurrentNumberStart();

        int possibleMinus =
                start - 1;

        if (possibleMinus >= 0
                && expression.charAt(
                possibleMinus
        ) == '-'
                && isUnaryMinus(
                possibleMinus
        )) {

            return possibleMinus;
        }

        return start;
    }

    private boolean isUnaryMinus(
            int index
    ) {

        if (index == 0) {
            return true;
        }

        char previous =
                expression.charAt(
                        index - 1
                );

        return previous == '('
                || isOperator(
                previous
        );
    }

    private String getCurrentNumber() {

        if (expression.isEmpty()) {
            return "";
        }

        int start =
                findSignedCurrentNumberStart();

        return expression.substring(
                start
        );
    }

    private BigDecimal getCurrentDisplayValue() {

        String text =
                view.getDisplay()
                        .getText();

        try {

            return new BigDecimal(
                    text
            );

        } catch (NumberFormatException exception) {

            return null;
        }
    }

    private void updateDisplayFromCurrentEntry() {

        String current =
                getCurrentNumber();

        if (current.isEmpty()
                || current.equals("-")) {

            view.getDisplay()
                    .setText(
                            "0"
                    );

            return;
        }

        view.getDisplay()
                .setText(
                        current
                );
    }

    private void updateExpressionDisplay() {

        view.getExpressionDisplay()
                .setText(
                        visualExpression(
                                expression.toString()
                        )
                );
    }

    private String visualExpression(
            String source
    ) {

        return source
                .replace(
                        "sqrt(",
                        "√("
                )
                .replace(
                        "pi",
                        "π"
                )
                .replace(
                        "*",
                        " × "
                )
                .replace(
                        "/",
                        " ÷ "
                )
                .replace(
                        "^2",
                        "²"
                )
                .replace(
                        "^",
                        " ^ "
                )
                .replace(
                        "+",
                        " + "
                )
                .replace(
                        "-",
                        " − "
                )
                .replaceAll(
                        "\\s+",
                        " "
                )
                .trim();
    }

    private String formatNumber(
            BigDecimal number
    ) {

        if (number.compareTo(
                BigDecimal.ZERO
        ) == 0) {

            return "0";
        }

        return number
                .stripTrailingZeros()
                .toPlainString();
    }
}