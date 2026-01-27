package com.example.demo.farm;

public class Cow extends Animal implements Sellable {
    public Cow(String name) {
        super(name, 2, 8); // zjada 2 paszy/tick, mleko co 8 ticków
    }

    @Override
    protected ProductType productType() {
        return ProductType.MILK;
    }

    @Override
    public long getPriceInCents() {
        return 5000;
    }
}