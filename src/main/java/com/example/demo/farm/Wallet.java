package com.example.demo.farm;

/**
 * Manages the player's money.
 * Stores value in cents to avoid floating-point errors.
 */
public class Wallet {
    private long cents = 0;

    public long getCents() {
        return cents;
    }

    /**
     * Returns the money formatted as a String (e.g. "10.50").
     */
    public String getFormatted() {
        return String.format("%.2f", cents / 100.0);
    }

    /**
     * Adds money to the wallet.
     * @param amount Amount in cents.
     */
    public void depositCents(long amount) {
        if (amount < 0) throw new IllegalArgumentException("amount < 0");
        cents += amount;
    }

    /**
     * Sells an item and adds its value to the wallet.
     * @param item The item to sell.
     */
    public void sell(Sellable item) {
        long value = item.getPriceInCents();
        this.depositCents(value);
        System.out.println("Item sold for: " + item.getFormattedPrice());
    }

    public boolean canAfford(long amount) {
        return cents >= amount;
    }

    /**
     * Attempts to withdraw money.
     * @param amount Amount in cents.
     * @return true if successful, false if insufficient funds.
     */
    public boolean withdrawCents(long amount) {
        if (amount < 0) throw new IllegalArgumentException("amount < 0");
        if (cents < amount) return false;
        cents -= amount;
        return true;
    }
}