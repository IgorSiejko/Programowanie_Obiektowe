package com.example.demo.farm;

import java.util.UUID;

public abstract class Animal {
    private final UUID id = UUID.randomUUID();
    private final String name;

    private int hunger = 0;     // git 0..100
    private int health = 100;   // 0..100
    private boolean alive = true;
    private boolean producing = true;

    private final int feedPerTick;
    private final int productIntervalTicks;
    private long lastProductTick = 0;

    protected Animal(String name, int feedPerTick, int productIntervalTicks) {
        this.name = name;
        this.feedPerTick = Math.max(1, feedPerTick);
        this.productIntervalTicks = Math.max(1, productIntervalTicks);
    }

    public UUID getId() { return id; }
    public String getName() { return name; }
    public int getHunger() { return hunger; }
    public int getHealth() { return health; }
    public boolean isAlive() { return alive; }
    public boolean isProducing() { return producing; }

    protected abstract ProductType productType();

    public void tick(Farm farm, long nowTick) {
        if (!alive) return;

        boolean fed = farm.getInventory().consumeFeed(feedPerTick);
        if (fed) {
            hunger = Math.max(0, hunger - 3);
            health = Math.min(100, health + 1);
        } else {
            hunger = Math.min(100, hunger + 6);
            health = Math.max(0, health - (hunger >= 70 ? 2 : 1));
        }

        // dobrostan -> kiedy produkuje
        producing = (hunger < 80 && health > 20);

        // śmierć
        if (hunger >= 100 || health <= 0) {
            alive = false;
            producing = false;
            return;
        }

        // produkcja
        if (producing && (nowTick - lastProductTick >= productIntervalTicks)) {
            farm.getWarehouse().add(productType(), 1);
            lastProductTick = nowTick;
        }
    }
}
