package com.example.demo.farm;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FieldCropTest {

    @Test
    void cropGrowsAndCanBeHarvested() {
        Farm farm = new Farm();
        farm.getInventory().addWater(50);
        farm.getInventory().addFertilizer(50);

        Field field = new Field(1);
        farm.addField(field);

        field.plant(new Wheat());
        assertNotNull(field.getCrop());
        assertFalse(field.getCrop().isMature());

        // bonusy wzrostu: podlewanie + nawożenie
        for (int i = 0; i < 10; i++) {
            field.water(farm.getInventory());
            field.fertilize(farm.getInventory());
            farm.tick(i + 1);
        }

        //  (Wheat ma 18 ticksToMature, ale bonusy przyspieszają)
        for (int t = 11; t <= 25; t++) {
            farm.tick(t);
        }

        assertTrue(field.getCrop() == null || field.getCrop().isMature());

        boolean harvested = field.harvest(farm.getWarehouse());
        assertTrue(harvested);
        assertNull(field.getCrop());
        assertTrue(farm.getWarehouse().getQty(ProductType.WHEAT) > 0);
    }

    @Test
    void cannotHarvestIfNotMature() {
        Warehouse wh = new Warehouse();
        Field field = new Field(1);
        field.plant(new Corn());

        // Bez ticków nie dojrzeje
        assertFalse(field.harvest(wh));
        assertEquals(0, wh.getQty(ProductType.CORN));
    }
}
