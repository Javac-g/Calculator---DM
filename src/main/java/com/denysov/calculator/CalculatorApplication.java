package com.denysov.calculator;

import com.denysov.calculator.event.CalculatorEvents;
import com.denysov.calculator.view.CalculatorView;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class CalculatorApplication
        extends Application {

    @Override
    public void start(
            Stage stage
    ) {

        CalculatorView view =
                new CalculatorView();

        CalculatorEvents events =
                new CalculatorEvents(
                        view
                );

        events.registerEvents();

        Scene scene =
                new Scene(
                        view.getRoot(),
                        960,
                        825
                );

        scene.getStylesheets()
                .add(
                        getClass()
                                .getResource(
                                        "/css/calculator.css"
                                )
                                .toExternalForm()
                );

        scene.setFill(
                Color.TRANSPARENT
        );

        stage.initStyle(
                StageStyle.TRANSPARENT
        );

        stage.setScene(
                scene
        );

        events.registerKeyboard();

        events.registerWindowControls(
                stage
        );

        events.registerWindowDragging(
                stage
        );

        stage.show();
    }

    public static void main(
            String[] args
    ) {

        launch(args);
    }
}