package com.example.demo.farm;

public class Chicken extends Animal implements Sellable {

    public Chicken(String name) {
        super(name, 1, 6); // jajka co 6 ticków
    }

    @Override
    protected ProductType productType() {
        return ProductType.EGGS;
    }

    @Override
    public long getPriceInCents() {
        return 2000;
    }
}