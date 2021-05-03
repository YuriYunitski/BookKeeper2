package com.yunitski.bookkeeper2;

public class Element {
    String value;
    String totalValue;
    String date;
    int operation;

    public Element(String value, String totalValue, String date, int operation) {
        this.value = value;
        this.totalValue = totalValue;
        this.date = date;
        this.operation = operation;
    }

    public int getOperation() {
        return operation;
    }

    public void setOperation(int operation) {
        this.operation = operation;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getTotalValue() {
        return totalValue;
    }

    public void setTotalValue(String totalValue) {
        this.totalValue = totalValue;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
