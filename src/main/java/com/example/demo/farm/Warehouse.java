package com.example.demo.farm;

import java.util.EnumMap;
import java.util.Map;

public class Warehouse {
    private final Map<ProductType, Integer> products = new EnumMap<>(ProductType.class);

    public void add(ProductType type, int qty) {
        if (qty <= 0) return;
        products.put(type, getQty(type) + qty);
    }

    public int getQty(ProductType type) {
        return products.getOrDefault(type, 0);
    }

    public Map<ProductType, Integer> snapshot() {
        return new EnumMap<>(products);
    }
}
