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
    void animalDiesWithoutFeedEventually() {
        Farm farm = new Farm();
        // brak paszy
        Animal cow = new Cow("Krowa#1");
        farm.addAnimal(cow);

        // przy braku paszy hunger rośnie o 6/tick, po ~17 tickach dobije do 100 i umrze
        for (int t = 1; t <= 25; t++) {
            farm.tick(t);
        }

        assertFalse(cow.isAlive());
        assertEquals(0, farm.getWarehouse().getQty(ProductType.MILK)); // nie produkowała
    }
}
