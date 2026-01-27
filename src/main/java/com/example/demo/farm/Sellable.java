package com.example.demo.farm;

public interface Sellable {
    long getPriceInCents();
    default String getFormattedPrice() {
        return String.format("%.2f", getPriceInCents() / 100.0);
    }
}