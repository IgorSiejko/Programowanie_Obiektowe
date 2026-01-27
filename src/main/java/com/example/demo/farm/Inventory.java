package com.example.demo.farm;

public class Inventory {
    private int feed = 0;
    private int water = 0;
    private int fertilizer = 0;

    public int getFeed() { return feed; }
    public int getWater() { return water; }
    public int getFertilizer() { return fertilizer; }

    public void addFeed(int amount) { feed += Math.max(0, amount); }
    public void addWater(int amount) { water += Math.max(0, amount); }
    public void addFertilizer(int amount) { fertilizer += Math.max(0, amount); }

    public boolean consumeFeed(int amount) {
        if (amount <= 0) return true;
        if (feed < amount) return false;
        feed -= amount;
        return true;
    }

    public boolean consumeWater(int amount) {
        if (amount <= 0) return true;
        if (water < amount) return false;
        water -= amount;
        return true;
    }

    public boolean consumeFertilizer(int amount) {
        if (amount <= 0) return true;
        if (fertilizer < amount) return false;
        fertilizer -= amount;
        return true;
    }
}
