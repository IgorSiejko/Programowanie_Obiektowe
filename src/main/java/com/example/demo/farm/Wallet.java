package com.example.demo.farm;

public class Wallet {
    private long cents = 0;

    public long getCents() {
        return cents;
    }

    public String getFormatted() {
        return String.format("%.2f", cents / 100.0);
    }

    public void depositCents(long amount) {
        if (amount < 0) throw new IllegalArgumentException("amount < 0");
        cents += amount;
    }

    public boolean canAfford(long amount) {
        return cents >= amount;
    }

    public boolean withdrawCents(long amount) {
        if (amount < 0) throw new IllegalArgumentException("amount < 0");
        if (cents < amount) return false;
        cents -= amount;
        return true;
    }
}
