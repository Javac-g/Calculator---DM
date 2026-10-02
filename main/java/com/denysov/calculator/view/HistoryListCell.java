package com.denysov.calculator.view;

import com.denysov.calculator.model.HistoryEntry;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.OverrunStyle;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class HistoryListCell
        extends ListCell<HistoryEntry> {

    private static final String[] TAG_COLOR_CLASSES = {
            "tag-green",
            "tag-blue",
            "tag-purple",
            "tag-yellow",
            "tag-orange",
            "tag-pink",
            "tag-cyan",
            "tag-gray"
    };

    private static final double TAG_WIDTH =
            190;

    private final VBox root;

    private final Label calculationLabel;

    private final HBox tagRow;

    private final Button tagButton;

    private final TextField tagField;

    private final Tooltip tagTooltip;

    private boolean editingTag;

    public HistoryListCell() {

        /*
         * =====================================================
         * CALCULATION
         * =====================================================
         */

        calculationLabel =
                new Label();

        calculationLabel
                .getStyleClass()
                .add(
                        "history-calculation"
                );

        calculationLabel.setWrapText(
                true
        );

        calculationLabel.setMinHeight(
                Region.USE_PREF_SIZE
        );


        /*
         * =====================================================
         * TAG BUTTON
         * =====================================================
         */

        tagButton =
                new Button(
                        "#AddTag"
                );

        tagButton
                .getStyleClass()
                .add(
                        "history-add-tag"
                );

        tagButton.setTextOverrun(
                OverrunStyle.CENTER_ELLIPSIS
        );

        tagButton.setMinWidth(
                TAG_WIDTH
        );

        tagButton.setPrefWidth(
                TAG_WIDTH
        );

        tagButton.setMaxWidth(
                TAG_WIDTH
        );


        /*
         * =====================================================
         * TOOLTIP
         * =====================================================
         */

        tagTooltip =
                new Tooltip();

        tagButton.setTooltip(
                tagTooltip
        );


        /*
         * =====================================================
         * TAG EDITOR
         * =====================================================
         */

        tagField =
                new TextField();

        tagField.setPromptText(
                "Tag"
        );

        tagField
                .getStyleClass()
                .add(
                        "history-tag-field"
                );

        tagField.setPrefColumnCount(
                1
        );

        tagField.setMinWidth(
                TAG_WIDTH
        );

        tagField.setPrefWidth(
                TAG_WIDTH
        );

        tagField.setMaxWidth(
                TAG_WIDTH
        );

        tagField.setVisible(
                false
        );

        tagField.setManaged(
                false
        );


        /*
         * =====================================================
         * TAG ROW
         * =====================================================
         */

        Region spacer =
                new Region();

        HBox.setHgrow(
                spacer,
                Priority.ALWAYS
        );

        tagRow =
                new HBox(
                        8,
                        spacer,
                        tagButton,
                        tagField
                );

        tagRow.setAlignment(
                Pos.CENTER_RIGHT
        );

        tagRow.setFillHeight(
                false
        );


        /*
         * =====================================================
         * ROOT
         * =====================================================
         */

        root =
                new VBox(
                        8,
                        calculationLabel,
                        tagRow
                );

        root
                .getStyleClass()
                .add(
                        "history-entry"
                );

        root.setFillWidth(
                true
        );

        root.setMinHeight(
                Region.USE_PREF_SIZE
        );

        root.setMaxHeight(
                Region.USE_PREF_SIZE
        );


        configureLayout();

        configureActions();
    }

    private void configureLayout() {

        /*
         * IMPORTANT:
         *
         * Bind wrapping width to the ListView itself,
         * not root.widthProperty().
         *
         * ListView has a stable width during cell
         * measurement. The cell graphic may not.
         */
        listViewProperty()
                .addListener(
                        (
                                observable,
                                oldListView,
                                newListView
                        ) -> {

                            if (oldListView != null) {

                                calculationLabel
                                        .prefWidthProperty()
                                        .unbind();

                                calculationLabel
                                        .maxWidthProperty()
                                        .unbind();
                            }

                            if (newListView != null) {

                                calculationLabel
                                        .prefWidthProperty()
                                        .bind(
                                                newListView
                                                        .widthProperty()
                                                        .subtract(55)
                                        );

                                calculationLabel
                                        .maxWidthProperty()
                                        .bind(
                                                newListView
                                                        .widthProperty()
                                                        .subtract(55)
                                        );
                            }
                        }
                );
    }

    private void configureActions() {

        tagButton.setOnAction(
                event ->
                        startTagEditing()
        );

        tagField.setOnAction(
                event ->
                        saveTag()
        );

        tagField.focusedProperty()
                .addListener(
                        (
                                observable,
                                oldValue,
                                newValue
                        ) -> {

                            if (!newValue
                                    && editingTag) {

                                saveTag();
                            }
                        }
                );
    }

    private void startTagEditing() {

        HistoryEntry entry =
                getItem();

        if (entry == null) {
            return;
        }

        editingTag =
                true;

        if (entry.hasTag()) {

            tagField.setText(
                    entry.getTag()
            );

        } else {

            tagField.clear();
        }

        tagButton.setVisible(
                false
        );

        tagButton.setManaged(
                false
        );

        tagField.setVisible(
                true
        );

        tagField.setManaged(
                true
        );

        Platform.runLater(
                () -> {

                    tagField.requestFocus();

                    tagField.selectAll();
                }
        );
    }

    private void saveTag() {

        HistoryEntry entry =
                getItem();

        if (entry == null) {
            return;
        }

        entry.setTag(
                tagField.getText()
        );

        editingTag =
                false;

        tagField.setVisible(
                false
        );

        tagField.setManaged(
                false
        );

        tagButton.setVisible(
                true
        );

        tagButton.setManaged(
                true
        );

        updateTagAppearance();

        /*
         * Recalculate this particular cell's
         * preferred height after changing controls.
         */
        root.autosize();

        requestLayout();

        if (getListView() != null) {

            getListView()
                    .requestLayout();
        }
    }

    private void updateTagAppearance() {

        HistoryEntry entry =
                getItem();

        if (entry == null) {
            return;
        }

        removeTagStyles();

        if (entry.hasTag()) {

            tagButton.setText(
                    entry.getFormattedTag()
            );

            tagButton
                    .getStyleClass()
                    .add(
                            "history-tag"
                    );

            tagButton
                    .getStyleClass()
                    .add(
                            getTagColorClass(
                                    entry.getTag()
                            )
                    );

            tagTooltip.setText(
                    entry.getFormattedTag()
            );

        } else {

            tagButton.setText(
                    "#AddTag"
            );

            tagButton
                    .getStyleClass()
                    .add(
                            "history-add-tag"
                    );

            tagTooltip.setText(
                    "Add tag"
            );
        }
    }

    private void removeTagStyles() {

        tagButton
                .getStyleClass()
                .remove(
                        "history-tag"
                );

        tagButton
                .getStyleClass()
                .remove(
                        "history-add-tag"
                );

        for (String colorClass
                : TAG_COLOR_CLASSES) {

            tagButton
                    .getStyleClass()
                    .remove(
                            colorClass
                    );
        }
    }

    private String getTagColorClass(
            String tag
    ) {

        int index =
                Math.floorMod(
                        tag
                                .toLowerCase()
                                .hashCode(),
                        TAG_COLOR_CLASSES.length
                );

        return TAG_COLOR_CLASSES[
                index
                ];
    }

    @Override
    protected void updateItem(
            HistoryEntry entry,
            boolean empty
    ) {

        super.updateItem(
                entry,
                empty
        );

        /*
         * Cells are reused by JavaFX.
         * Reset temporary editing state every time.
         */
        editingTag =
                false;

        tagField.setVisible(
                false
        );

        tagField.setManaged(
                false
        );

        tagButton.setVisible(
                true
        );

        tagButton.setManaged(
                true
        );

        if (empty
                || entry == null) {

            calculationLabel.setText(
                    ""
            );

            setText(
                    null
            );

            setGraphic(
                    null
            );

            return;
        }

        calculationLabel.setText(
                entry.getFormattedCalculation()
        );

        updateTagAppearance();

        setText(
                null
        );

        setGraphic(
                root
        );

        /*
         * Force correct preferred size after
         * virtualized cell reuse.
         */
        root.autosize();
    }
}