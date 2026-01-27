package com.example.demo.farm;

public abstract class Crop {
    private final ProductType type;
    private final int ticksToMature;
    private final int yield;

    private int ageTicks = 0;
    private GrowthStage stage = GrowthStage.SEED;

    protected Crop(ProductType type, int ticksToMature, int yield) {
        this.type = type;
        this.ticksToMature = Math.max(1, ticksToMature);
        this.yield = Math.max(1, yield);
    }

    public ProductType getType() { return type; }
    public GrowthStage getStage() { return stage; }
    public boolean isMature() { return stage == GrowthStage.MATURE; }
    public int getYield() { return yield; }

    public void grow(int deltaTicks) {
        ageTicks += Math.max(1, deltaTicks);

        double p = ageTicks / (double) ticksToMature;
        if (p >= 1.0) stage = GrowthStage.MATURE;
        else if (p >= 0.66) stage = GrowthStage.GROWING;
        else if (p >= 0.33) stage = GrowthStage.SPROUT;
        else stage = GrowthStage.SEED;
    }
}
