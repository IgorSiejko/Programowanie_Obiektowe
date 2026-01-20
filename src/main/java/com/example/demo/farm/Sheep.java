package com.example.demo.farm;

public class Sheep extends Animal {
    public Sheep(String name) {
        super(name, 2, 10); // wełna co 10 ticków
    }

    @Override
    protected ProductType productType() {
        return ProductType.WOOL;
    }
}

