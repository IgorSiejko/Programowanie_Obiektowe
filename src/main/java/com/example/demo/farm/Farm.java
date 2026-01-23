package com.example.demo.farm;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Farm {
    private final List<Worker> workers = new ArrayList<>();
    private final List<Animal> animals = new ArrayList<>();
    private final List<Field> fields = new ArrayList<>();

    private final Wallet wallet = new Wallet();
    private final Inventory inventory = new Inventory();
    private final Warehouse warehouse = new Warehouse();

    public List<Worker> getWorkers() { return workers; }
    public List<Animal> getAnimals() { return animals; }
    public List<Field> getFields() { return fields; }

    public Wallet getWallet() { return wallet; }
    public Inventory getInventory() { return inventory; }
    public Warehouse getWarehouse() { return warehouse; }

    public void hireWorker(Worker w) { workers.add(w); }
    public void fireWorker(UUID id) { workers.removeIf(w -> w.getId().equals(id)); }

    public void assignWorker(UUID id, Sector sector) {
        for (Worker w : workers) {
            if (w.getId().equals(id)) {
                w.setSector(sector);
                return;
            }
        }
    }

    public void addAnimal(Animal a) { animals.add(a); }
    public void addField(Field f) { fields.add(f); }

    public Field getFieldById(int id) {
        for (Field f : fields) if (f.getId() == id) return f;
        throw new IllegalArgumentException("Brak pola o id=" + id);
    }

    public void tick(long nowTick) {
        // 1) wypłaty
        List<Worker> toRemove = new ArrayList<>();
        for (Worker w : workers) {
            if (!w.tick(this, nowTick)) toRemove.add(w);
        }
        workers.removeAll(toRemove);

        // 2) zwierzęta
        for (Animal a : animals) {
            a.tick(this, nowTick);
        }

        // 3) pola
        for (Field f : fields) {
            f.tick(this, nowTick);
        }
    }
}
