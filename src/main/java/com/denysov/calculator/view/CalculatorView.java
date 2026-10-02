package com.denysov.calculator.view;

import com.denysov.calculator.model.HistoryEntry;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Separator;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class CalculatorView {

    private final BorderPane root;

    private final Label expressionDisplay;
    private final Label display;
    private final Button lnButton;
    private final Button logButton;
    private final Button absoluteButton;
    private final Button factorialButton;
    private final ListView<HistoryEntry> historyList;

    private final Button button0;
    private final Button button1;
    private final Button button2;
    private final Button button3;
    private final Button button4;
    private final Button button5;
    private final Button button6;
    private final Button button7;
    private final Button button8;
    private final Button button9;

    private final Button addButton;
    private final Button subtractButton;
    private final Button multiplyButton;
    private final Button divideButton;

    private final Button equalsButton;
    private final Button clearButton;
    private final Button backspaceButton;
    private final Button signButton;
    private final Button decimalButton;
    private final Button sinButton;
    private final Button cosButton;
    private final Button tanButton;

    private final Button piButton;
    private final Button eButton;

    private final Button angleModeButton;
    private final Button reciprocalButton;
    private final Button sqrtButton;
    private final Button squareButton;
    private final Button powerButton;

    private final Button openBracketButton;
    private final Button closeBracketButton;

    private final Button clearHistoryButton;
    private final Button exportHistoryButton;

    private final Button minimizeButton;
    private final Button closeButton;

    public CalculatorView() {

        root = new BorderPane();
        root.getStyleClass().add("root-pane");

        expressionDisplay =
                new Label("");

        display =
                new Label("0");

        historyList = new ListView<>();
        historyList.getStyleClass().add("history-list");
        historyList.setCellFactory(
                listView ->
                        new HistoryListCell()
        );
        lnButton =
                createButton(
                        "ln",
                        "secondary-button"
                );

        logButton =
                createButton(
                        "log",
                        "secondary-button"
                );

        absoluteButton =
                createButton(
                        "|x|",
                        "secondary-button"
                );

        factorialButton =
                createButton(
                        "n!",
                        "secondary-button"
                );
        button0 =
                createButton(
                        "0",
                        "number-button"
                );

        button1 =
                createButton(
                        "1",
                        "number-button"
                );

        button2 =
                createButton(
                        "2",
                        "number-button"
                );

        button3 =
                createButton(
                        "3",
                        "number-button"
                );

        button4 =
                createButton(
                        "4",
                        "number-button"
                );

        button5 =
                createButton(
                        "5",
                        "number-button"
                );

        button6 =
                createButton(
                        "6",
                        "number-button"
                );

        button7 =
                createButton(
                        "7",
                        "number-button"
                );

        button8 =
                createButton(
                        "8",
                        "number-button"
                );

        button9 =
                createButton(
                        "9",
                        "number-button"
                );

        addButton =
                createButton(
                        "+",
                        "operator-button"
                );

        subtractButton =
                createButton(
                        "−",
                        "operator-button"
                );

        multiplyButton =
                createButton(
                        "×",
                        "operator-button"
                );

        divideButton =
                createButton(
                        "÷",
                        "operator-button"
                );

        equalsButton =
                createButton(
                        "=",
                        "equals-button"
                );

        clearButton =
                createButton(
                        "C/E",
                        "utility-button"
                );

        backspaceButton =
                createButton(
                        "←",
                        "utility-button"
                );

        signButton =
                createButton(
                        "±",
                        "utility-button"
                );

        decimalButton =
                createButton(
                        ".",
                        "number-button"
                );

        reciprocalButton =
                createButton(
                        "1/x",
                        "secondary-button"
                );

        sqrtButton =
                createButton(
                        "√x",
                        "secondary-button"
                );

        squareButton =
                createButton(
                        "x²",
                        "secondary-button"
                );

        powerButton =
                createButton(
                        "xʸ",
                        "secondary-button"
                );

        openBracketButton =
                createButton(
                        "(",
                        "secondary-button"
                );

        closeBracketButton =
                createButton(
                        ")",
                        "secondary-button"
                );

        sinButton =
                createButton(
                        "sin",
                        "secondary-button"
                );

        cosButton =
                createButton(
                        "cos",
                        "secondary-button"
                );

        tanButton =
                createButton(
                        "tan",
                        "secondary-button"
                );

        piButton =
                createButton(
                        "π",
                        "secondary-button"
                );

        eButton =
                createButton(
                        "e",
                        "secondary-button"
                );

        angleModeButton =
                createButton(
                        "DEG",
                        "angle-mode-button"
                );
        clearHistoryButton =
                new Button("Clear");

        clearHistoryButton
                .getStyleClass()
                .add(
                        "history-action"
                );

        exportHistoryButton =
                new Button(
                        "Export History"
                );

        exportHistoryButton
                .getStyleClass()
                .add(
                        "export-button"
                );

        minimizeButton =
                new Button("−");

        minimizeButton
                .getStyleClass()
                .add(
                        "window-button"
                );

        closeButton =
                new Button("×");

        closeButton
                .getStyleClass()
                .addAll(
                        "window-button",
                        "close-button"
                );

        buildLayout();
    }

    private void buildLayout() {

        HBox mainContent =
                new HBox(8);

        mainContent.setPadding(
                new Insets(0)
        );

        VBox calculatorPanel =
                createCalculatorPanel();

        VBox historyPanel =
                createHistoryPanel();

        HBox.setHgrow(
                historyPanel,
                Priority.ALWAYS
        );

        mainContent
                .getChildren()
                .addAll(
                        calculatorPanel,
                        historyPanel
                );

        VBox appLayout =
                new VBox();

        HBox windowControls =
                createWindowControls();

        appLayout
                .getChildren()
                .addAll(
                        windowControls,
                        mainContent
                );

        VBox.setVgrow(
                mainContent,
                Priority.ALWAYS
        );

        root.setCenter(
                appLayout
        );
    }

    private HBox createWindowControls() {

        Region spacer =
                new Region();

        HBox.setHgrow(
                spacer,
                Priority.ALWAYS
        );

        HBox controls =
                new HBox(
                        spacer,
                        minimizeButton,
                        closeButton
                );

        controls.setAlignment(
                Pos.CENTER_RIGHT
        );

        controls.setSpacing(4);

        return controls;
    }

    private VBox createCalculatorPanel() {

        VBox calculatorPanel =
                new VBox(14);

        calculatorPanel.setPrefWidth(
                430
        );

        calculatorPanel
                .getStyleClass()
                .add(
                        "calculator-panel"
                );

        VBox displayPanel =
                createDisplayPanel();

        GridPane mainButtons =
                createMainButtons();

        Separator separator =
                new Separator();

        GridPane scientificButtons =
                createScientificButtons();

        calculatorPanel
                .getChildren()
                .addAll(
                        displayPanel,
                        mainButtons,
                        separator,
                        scientificButtons
                );

        return calculatorPanel;
    }

    private VBox createDisplayPanel() {

        VBox panel =
                new VBox(4);

        panel
                .getStyleClass()
                .add(
                        "display-panel"
                );

        expressionDisplay
                .getStyleClass()
                .add(
                        "expression-display"
                );

        expressionDisplay.setMaxWidth(
                Double.MAX_VALUE
        );

        expressionDisplay.setAlignment(
                Pos.CENTER_RIGHT
        );

        display
                .getStyleClass()
                .add(
                        "main-display"
                );

        display.setMaxWidth(
                Double.MAX_VALUE
        );

        display.setAlignment(
                Pos.CENTER_RIGHT
        );

        panel
                .getChildren()
                .addAll(
                        expressionDisplay,
                        display
                );

        return panel;
    }

    private GridPane createMainButtons() {

        GridPane grid =
                new GridPane();

        grid.setHgap(10);
        grid.setVgap(10);

        addFourEqualColumns(
                grid
        );

        grid.add(
                clearButton,
                0,
                0
        );

        grid.add(
                backspaceButton,
                1,
                0
        );

        grid.add(
                signButton,
                2,
                0
        );

        grid.add(
                divideButton,
                3,
                0
        );

        grid.add(
                button7,
                0,
                1
        );

        grid.add(
                button8,
                1,
                1
        );

        grid.add(
                button9,
                2,
                1
        );

        grid.add(
                multiplyButton,
                3,
                1
        );

        grid.add(
                button4,
                0,
                2
        );

        grid.add(
                button5,
                1,
                2
        );

        grid.add(
                button6,
                2,
                2
        );

        grid.add(
                subtractButton,
                3,
                2
        );

        grid.add(
                button1,
                0,
                3
        );

        grid.add(
                button2,
                1,
                3
        );

        grid.add(
                button3,
                2,
                3
        );

        grid.add(
                addButton,
                3,
                3
        );

        grid.add(
                button0,
                0,
                4,
                2,
                1
        );

        grid.add(
                decimalButton,
                2,
                4
        );

        grid.add(
                equalsButton,
                3,
                4
        );

        return grid;
    }

    private GridPane createScientificButtons() {

        GridPane grid =
                new GridPane();

        grid.setHgap(
                10
        );

        grid.setVgap(
                10
        );

        for (int i = 0;
             i < 6;
             i++) {

            ColumnConstraints constraints =
                    new ColumnConstraints();

            constraints.setPercentWidth(
                    100.0 / 6.0
            );

            constraints.setHgrow(
                    Priority.ALWAYS
            );

            grid
                    .getColumnConstraints()
                    .add(
                            constraints
                    );
        }

        /*
         * Row 1
         */

        grid.add(
                openBracketButton,
                0,
                0
        );

        grid.add(
                closeBracketButton,
                1,
                0
        );

        grid.add(
                reciprocalButton,
                2,
                0
        );

        grid.add(
                sqrtButton,
                3,
                0
        );

        grid.add(
                squareButton,
                4,
                0
        );

        grid.add(
                powerButton,
                5,
                0
        );

        /*
         * Row 2
         */

        grid.add(
                sinButton,
                0,
                1
        );

        grid.add(
                cosButton,
                1,
                1
        );

        grid.add(
                tanButton,
                2,
                1
        );

        grid.add(
                piButton,
                3,
                1
        );

        grid.add(
                eButton,
                4,
                1
        );

        grid.add(
                angleModeButton,
                5,
                1
        );

        /*
         * Row 3
         */

        grid.add(
                lnButton,
                0,
                2
        );

        grid.add(
                logButton,
                1,
                2
        );

        grid.add(
                absoluteButton,
                2,
                2
        );

        grid.add(
                factorialButton,
                3,
                2
        );

        return grid;
    }

    private VBox createHistoryPanel() {

        VBox panel =
                new VBox(12);

        panel
                .getStyleClass()
                .add(
                        "history-panel"
                );

        Label title =
                new Label(
                        "History"
                );

        title
                .getStyleClass()
                .add(
                        "history-title"
                );

        Region spacer =
                new Region();

        HBox.setHgrow(
                spacer,
                Priority.ALWAYS
        );

        HBox header =
                new HBox(
                        title,
                        spacer,
                        clearHistoryButton
                );

        header.setAlignment(
                Pos.CENTER_LEFT
        );

        historyList.setPlaceholder(
                new Label(
                        "Memory is empty"
                )
        );

        VBox.setVgrow(
                historyList,
                Priority.ALWAYS
        );

        /*
         * Give history useful room now.
         *
         * Later our custom HistoryListCell
         * can calculate/adapt its preferred width
         * for long expressions.
         */
        panel.setMinWidth(390);
        panel.setPrefWidth(460);

        exportHistoryButton.setMaxWidth(
                Double.MAX_VALUE
        );

        panel
                .getChildren()
                .addAll(
                        header,
                        historyList,
                        exportHistoryButton
                );

        return panel;
    }

    private Button createButton(
            String text,
            String styleClass
    ) {

        Button button =
                new Button(text);

        button.setMaxSize(
                Double.MAX_VALUE,
                Double.MAX_VALUE
        );

        button.setMinHeight(64);

        button
                .getStyleClass()
                .add(
                        styleClass
                );

        return button;
    }

    private void addFourEqualColumns(
            GridPane grid
    ) {

        for (int i = 0; i < 4; i++) {

            ColumnConstraints constraints =
                    new ColumnConstraints();

            constraints.setPercentWidth(
                    25
            );

            constraints.setHgrow(
                    Priority.ALWAYS
            );

            grid
                    .getColumnConstraints()
                    .add(
                            constraints
                    );
        }
    }
    public Button getLnButton() {

        return lnButton;
    }

    public Button getLogButton() {

        return logButton;
    }

    public Button getAbsoluteButton() {

        return absoluteButton;
    }

    public Button getFactorialButton() {

        return factorialButton;
    }
    public BorderPane getRoot() {
        return root;
    }

    public Label getExpressionDisplay() {
        return expressionDisplay;
    }

    public Label getDisplay() {
        return display;
    }

    public ListView<HistoryEntry> getHistoryList() {
        return historyList;
    }

    public Button getButton0() {
        return button0;
    }

    public Button getButton1() {
        return button1;
    }

    public Button getButton2() {
        return button2;
    }

    public Button getButton3() {
        return button3;
    }

    public Button getButton4() {
        return button4;
    }

    public Button getButton5() {
        return button5;
    }

    public Button getButton6() {
        return button6;
    }

    public Button getButton7() {
        return button7;
    }

    public Button getButton8() {
        return button8;
    }

    public Button getButton9() {
        return button9;
    }

    public Button getAddButton() {
        return addButton;
    }

    public Button getSubtractButton() {
        return subtractButton;
    }

    public Button getMultiplyButton() {
        return multiplyButton;
    }

    public Button getDivideButton() {
        return divideButton;
    }

    public Button getEqualsButton() {
        return equalsButton;
    }

    public Button getClearButton() {
        return clearButton;
    }

    public Button getBackspaceButton() {
        return backspaceButton;
    }

    public Button getSignButton() {
        return signButton;
    }

    public Button getDecimalButton() {
        return decimalButton;
    }

    public Button getReciprocalButton() {
        return reciprocalButton;
    }

    public Button getSqrtButton() {
        return sqrtButton;
    }

    public Button getSquareButton() {
        return squareButton;
    }

    public Button getPowerButton() {
        return powerButton;
    }

    public Button getOpenBracketButton() {
        return openBracketButton;
    }

    public Button getCloseBracketButton() {
        return closeBracketButton;
    }

    public Button getClearHistoryButton() {
        return clearHistoryButton;
    }

    public Button getExportHistoryButton() {
        return exportHistoryButton;
    }

    public Button getMinimizeButton() {
        return minimizeButton;
    }

    public Button getCloseButton() {
        return closeButton;
    }
    public Button getSinButton() {
        return sinButton;
    }

    public Button getCosButton() {
        return cosButton;
    }

    public Button getTanButton() {
        return tanButton;
    }

    public Button getPiButton() {
        return piButton;
    }

    public Button getEButton() {
        return eButton;
    }

    public Button getAngleModeButton() {
        return angleModeButton;
    }
}