package com.example.demo;

import com.example.demo.farm.*;

public class ConsoleSimulation {
    public static void main(String[] args) throws Exception {
        Farm farm = new Farm();
        GameClock clock = new GameClock();

        // startowe zasoby
        farm.getWallet().depositCents(20_000); // 200.00
        farm.getInventory().addFeed(60);
        farm.getInventory().addWater(20);
        farm.getInventory().addFertilizer(10);

        // startowe obiekty
        farm.hireWorker(new Worker("Jan", 1_500, 5)); // 15.00 co 5 ticków
        farm.addAnimal(new Cow("Krowa#1"));
        farm.addAnimal(new Chicken("Kura#1"));

        farm.addField(new Field(1));
        farm.getFieldById(1).plant(new Wheat());

        // symulacja
        for (int i = 0; i < 40; i++) {
            long now = clock.nextTick();
            farm.tick(now);

            if (now % 5 == 0) {
                System.out.println("TICK=" + now
                        + " | wallet=" + farm.getWallet().getFormatted()
                        + " | feed=" + farm.getInventory().getFeed()
                        + " | workers=" + farm.getWorkers().size()
                        + " | milk=" + farm.getWarehouse().getQty(ProductType.MILK)
                        + " | eggs=" + farm.getWarehouse().getQty(ProductType.EGGS)
                        + " | wheat=" + farm.getWarehouse().getQty(ProductType.WHEAT)
                );
                Crop c = farm.getFieldById(1).getCrop();
                System.out.println("  Field#1 stage=" + (c == null ? "EMPTY" : c.getStage()));
            }

            Thread.sleep(150);
        }

        // spróbuj zebrać jeśli dojrzałe
        boolean harvested = farm.getFieldById(1).harvest(farm.getWarehouse());
        System.out.println("Harvested? " + harvested + " | wheat=" + farm.getWarehouse().getQty(ProductType.WHEAT));
    }
}
