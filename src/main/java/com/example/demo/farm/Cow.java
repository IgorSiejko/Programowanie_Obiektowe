package com.example.demo.farm;

public class Cow extends Animal {
    public Cow(String name) {
        super(name, 2, 8); // zjada 2 paszy/tick, mleko co 8 ticków
    }

    @Override
    protected ProductType productType() {
        return ProductType.MILK;
    }
}
