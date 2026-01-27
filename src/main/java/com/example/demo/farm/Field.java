package com.example.demo.farm;

public class Field {
    private final int id;

    private Crop crop; // null = wolne pole
    private int waterLevel = 0;
    private int fertilizerLevel = 0;

    public Field(int id) {
        this.id = id;
    }

    public int getId() { return id; }
    public Crop getCrop() { return crop; }
    public boolean isFree() { return crop == null; }

    public void plant(Crop crop) {
        if (this.crop != null) throw new IllegalStateException("Pole zajęte");
        this.crop = crop;
    }

    public void water(Inventory inv) {
        if (inv.consumeWater(1)) waterLevel += 3;
    }

    public void fertilize(Inventory inv) {
        if (inv.consumeFertilizer(1)) fertilizerLevel += 3;
    }

    public void tick(Farm farm, long nowTick) {
        if (crop == null) return;

        int bonus = 0;
        if (waterLevel > 0) { waterLevel--; bonus++; }
        if (fertilizerLevel > 0) { fertilizerLevel--; bonus++; }

        crop.grow(1 + bonus);
    }

    public boolean harvest(Warehouse wh) {
        if (crop == null) return false;
        if (!crop.isMature()) return false;

        wh.add(crop.getType(), crop.getYield());
        crop = null;
        waterLevel = 0;
        fertilizerLevel = 0;
        return true;
    }
}
