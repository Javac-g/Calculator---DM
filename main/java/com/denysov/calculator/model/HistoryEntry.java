package com.denysov.calculator.model;

import java.time.LocalDateTime;

public class HistoryEntry {

    private final String expression;
    private final String result;
    private final LocalDateTime createdAt;

    private String tag;

    public HistoryEntry(
            String expression,
            String result
    ) {
        this.expression = expression;
        this.result = result;
        this.createdAt = LocalDateTime.now();
        this.tag = null;
    }

    public String getExpression() {
        return expression;
    }

    public String getResult() {
        return result;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public String getTag() {
        return tag;
    }

    public boolean hasTag() {
        return tag != null
                && !tag.isBlank();
    }

    public void setTag(String tag) {

        if (tag == null
                || tag.isBlank()) {

            this.tag = null;
            return;
        }

        String normalized =
                tag.trim();

        if (normalized.startsWith("#")) {

            normalized =
                    normalized.substring(1);
        }

        normalized =
                normalized.trim();

        if (normalized.isBlank()) {

            this.tag = null;
            return;
        }

        this.tag =
                normalized;
    }
    public String getFormattedCalculation() {

        return expression
                + " = "
                + result;
    }

    public String getFormattedTag() {

        if (!hasTag()) {
            return "";
        }

        return "#" + tag;
    }

    @Override
    public String toString() {

        if (hasTag()) {
            return getFormattedCalculation()
                    + " "
                    + getFormattedTag();
        }

        return getFormattedCalculation();
    }
}