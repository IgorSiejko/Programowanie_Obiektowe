package com.example.demo.farm;

public class Wheat extends Crop implements Sellable {
    public Wheat() {
        super(ProductType.WHEAT, 18, 6);
    }

    @Override
    public long getPriceInCents() {
        return 350;
    }
}