package com.example.demo.farm;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WorkerSalaryTest {

    @Test
    void salaryIsTakenEveryInterval() {
        Farm farm = new Farm();
        farm.getWallet().depositCents(10_00); // 10.00

        Worker worker = new Worker("Jan", 2_00, 5); // 2.00 co 5 ticków
        farm.hireWorker(worker);

        // tick 1..4 -> jeszcze bez wypłaty
        for (int t = 1; t <= 4; t++) farm.tick(t);
        assertEquals(10_00, farm.getWallet().getCents());
        assertEquals(1, farm.getWorkers().size());

        // tick 5 -> wypłata
        farm.tick(5);
        assertEquals(8_00, farm.getWallet().getCents());
        assertEquals(1, farm.getWorkers().size());

        // tick 10 -> kolejna wypłata
        farm.tick(10);
        assertEquals(6_00, farm.getWallet().getCents());
    }

    @Test
    void workerLeavesWhenCannotBePaid() {
        Farm farm = new Farm();
        farm.getWallet().depositCents(1_00); // 1.00

        Worker worker = new Worker("Jan", 2_00, 5); // chce 2.00
        farm.hireWorker(worker);

        // do tick 4 nic, tick 5 próba wypłaty -> brak kasy -> worker znika z listy
        for (int t = 1; t <= 5; t++) farm.tick(t);

        assertEquals(0, farm.getWorkers().size());
        assertEquals(1_00, farm.getWallet().getCents()); // nie zabiera, bo withdraw się nie udało
    }
}
