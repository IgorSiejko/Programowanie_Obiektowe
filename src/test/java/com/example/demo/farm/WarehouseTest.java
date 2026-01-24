package com.example.demo.farm;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class WarehouseTest {

    @Test
    void addAndGetQtyWorks() {
        Warehouse wh = new Warehouse();
        assertEquals(0, wh.getQty(ProductType.MILK));

        wh.add(ProductType.MILK, 2);
        assertEquals(2, wh.getQty(ProductType.MILK));

        wh.add(ProductType.MILK, 3);
        assertEquals(5, wh.getQty(ProductType.MILK));
    }

    @Test
    void addIgnoresNonPositiveQty() {
        Warehouse wh = new Warehouse();
        wh.add(ProductType.EGGS, 0);
        wh.add(ProductType.EGGS, -10);
        assertEquals(0, wh.getQty(ProductType.EGGS));
    }

    @Test
    void snapshotIsCopyNotLiveMap() {
        Warehouse wh = new Warehouse();
        wh.add(ProductType.WOOL, 1);

        Map<ProductType, Integer> snap = wh.snapshot();
        assertEquals(1, snap.get(ProductType.WOOL));

        // Próba "zepsucia" snapshotu nie może zmienić magazynu
        snap.put(ProductType.WOOL, 999);
        assertEquals(1, wh.getQty(ProductType.WOOL));
    }
}
