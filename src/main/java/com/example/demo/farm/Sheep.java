package com.example.demo.farm;

public class Sheep extends Animal implements Sellable {
    public Sheep(String name) {
        super(name, 2, 10); // wełna co 10 ticków
    }

    @Override
    protected ProductType productType() {
        return ProductType.WOOL;
    }

    @Override
    public long getPriceInCents() {
        return 4000;
    }
}