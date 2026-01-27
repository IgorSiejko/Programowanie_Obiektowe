package com.example.demo.farm;

import java.util.UUID;

public class Worker {
    private final UUID id = UUID.randomUUID();
    private final String name;

    private Sector sector = Sector.IDLE;

    private final long salaryCents;
    private final int salaryIntervalTicks;
    private long lastPaidTick = 0;

    public Worker(String name, long salaryCents, int salaryIntervalTicks) {
        this.name = name;
        this.salaryCents = salaryCents;
        this.salaryIntervalTicks = Math.max(1, salaryIntervalTicks);
    }

    public UUID getId() { return id; }
    public String getName() { return name; }
    public Sector getSector() { return sector; }
    public void setSector(Sector sector) { this.sector = sector; }

    public long getSalaryCents() { return salaryCents; }
    public int getSalaryIntervalTicks() { return salaryIntervalTicks; }

    /**
     * @return true jeśli pracownik dalej pracuje; false jeśli odchodzi (brak wypłaty)
     */
    public boolean tick(Farm farm, long nowTick) {
        if (nowTick - lastPaidTick >= salaryIntervalTicks) {
            boolean ok = farm.getWallet().withdrawCents(salaryCents);
            if (!ok) {
                // MVP: nie ma kasy -> pracownik odchodzi
                return false;
            }
            lastPaidTick = nowTick;
        }
        return true;
    }
}
