package com.example.demo.farm;

/**
 * Interface for items that have a monetary value.
 * Implement this interface for any object that can be sold in the game.
 */
public interface Sellable {

    /**
     * Gets the price of the item in cents.
     * @return Price in cents (e.g., 100 cents = 1.00 currency unit).
     */
    long getPriceInCents();

    /**
     * Returns the price as a string.
     * @return Formatted price string.
     */
    default String getFormattedPrice() {
        return String.format("%.2f", getPriceInCents() / 100.0);
    }
}