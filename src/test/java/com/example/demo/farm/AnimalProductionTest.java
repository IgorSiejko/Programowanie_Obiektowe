package com.example.demo.farm;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AnimalProductionTest {

    @Test
    void animalProducesWhenFed() {
        Farm farm = new Farm();
        farm.getInventory().addFeed(100);
        farm.addAnimal(new Chicken("Kura#1"));

        // Chicken: productInterval = 6, więc na tick=6 powinna dać 1 EGGS
        for (int t = 1; t <= 6; t++) {
            farm.tick(t);
        }

        assertEquals(1, farm.getWarehouse().getQty(ProductType.EGGS));
    }

    @Test
    void diesWithoutFeed() {
        Farm farm = new Farm();
        Cow cow = new Cow("Krowa#1");
        farm.addAnimal(cow); // brak paszy

        boolean stopped = false;
        int milkAtStop = -1;

        for (int t = 1; t <= 30; t++) {
            farm.tick(t);

            if (!stopped && !cow.isProducing()) {
                stopped = true;
                milkAtStop = farm.getWarehouse().getQty(ProductType.MILK);
            }

            if (stopped) {
                assertEquals(milkAtStop, farm.getWarehouse().getQty(ProductType.MILK));
            }

            if (!cow.isAlive()) break;
        }

        assertTrue(stopped);
        assertFalse(cow.isAlive());
    }

}
