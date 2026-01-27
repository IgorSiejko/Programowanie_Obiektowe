package com.example.demo;

import com.example.demo.farm.*;

import java.math.BigDecimal;
import java.util.ArrayList; // <--- NOWE (potrzebne do listy zapisu)
import java.util.List;
import java.util.Scanner;

public class ConsoleSimulation2 {

    public static void main(String[] args) {
        Farm farm = new Farm();
        GameClock clock = new GameClock();
        Scanner sc = new Scanner(System.in);

        farm.getWallet().depositCents(20_000);
        farm.getInventory().addFeed(50);
        farm.getInventory().addWater(20);
        farm.getInventory().addFertilizer(10);
        for (int i = 1; i <= 5; i++) farm.addField(new Field(i));

        while (true) {
            System.out.println("""
                    
                    === FARM CONSOLE SIM 2 ===
                    1) Status
                    2) Tick
                    3) Lista pracowników
                    4) Zatrudnij pracownika
                    5) Zwolnij pracownika (po imieniu)
                    6) Przypisz sektor (po imieniu)
                    7) Lista zwierząt
                    8) Dodaj zwierzę
                    9) Lista pól
                    10) Posadź roślinę
                    11) Podlej pole
                    12) Nawóź pole
                    13) Zbierz plony
                    14) Sprzedaj zwierzę
                    0) Wyjście
                    """);

            String choice = ask(sc, "Wybierz opcję: ");

            try {
                switch (choice) {
                    case "1" -> {
                        System.out.println("TICK=" + clock.now());
                        System.out.println("Wallet=" + farm.getWallet().getFormatted());
                        System.out.println("Inventory: feed=" + farm.getInventory().getFeed()
                                + " water=" + farm.getInventory().getWater()
                                + " fert=" + farm.getInventory().getFertilizer());
                        System.out.println("Warehouse=" + farm.getWarehouse().snapshot());
                    }

                    case "2" -> {
                        int n = askInt(sc, "Ile ticków? ");
                        for (int i = 0; i < n; i++) farm.tick(clock.nextTick());
                        System.out.println("OK: tick x" + n);
                    }

                    case "3" -> {
                        if (farm.getWorkers().isEmpty()) System.out.println("(brak)");
                        for (Worker w : farm.getWorkers()) {
                            System.out.println(w.getName()
                                    + " | sector=" + w.getSector()
                                    + " | salary=" + (w.getSalaryCents() / 100.0)
                                    + " | every=" + w.getSalaryIntervalTicks());
                        }
                    }

                    case "4" -> {
                        String name = ask(sc, "Imię: ");
                        long salary = askMoney(sc, "Pensja (np. 15.00): ");
                        int interval = askInt(sc, "Interwał ticków (np. 5): ");
                        farm.hireWorker(new Worker(name, salary, interval));
                        System.out.println("OK: zatrudniono");
                    }

                    case "5" -> {
                        String name = ask(sc, "Imię pracownika do zwolnienia: ");
                        Worker w = findWorkerByName(farm, name);
                        if (w == null) System.out.println("Nie znaleziono pracownika: " + name);
                        else {
                            farm.fireWorker(w.getId());
                            System.out.println("OK: zwolniono " + name);
                        }
                    }

                    case "6" -> {
                        String name = ask(sc, "Imię pracownika: ");
                        Worker w = findWorkerByName(farm, name);
                        if (w == null) {
                            System.out.println("Nie znaleziono pracownika: " + name);
                        } else {
                            String s = ask(sc, "Sektor (IDLE/ANIMALS/FIELDS/WAREHOUSE): ").toUpperCase();
                            farm.assignWorker(w.getId(), Sector.valueOf(s));
                            System.out.println("OK: przypisano sektor");
                        }
                    }

                    case "7" -> {
                        if (farm.getAnimals().isEmpty()) System.out.println("(brak)");
                        for (Animal a : farm.getAnimals()) {
                            System.out.println(a.getClass().getSimpleName() + " | " + a.getName()
                                    + " | hunger=" + a.getHunger()
                                    + " | health=" + a.getHealth()
                                    + " | alive=" + a.isAlive()
                                    + " | prod=" + a.isProducing());
                        }
                    }

                    case "8" -> {
                        String type = ask(sc, "Typ (cow/chicken/sheep): ").toLowerCase();
                        String name = ask(sc, "Nazwa/imię zwierzęcia: ");
                        Animal a = switch (type) {
                            case "cow" -> new Cow(name);
                            case "chicken" -> new Chicken(name);
                            case "sheep" -> new Sheep(name);
                            default -> throw new IllegalArgumentException("Nieznany typ: " + type);
                        };
                        farm.addAnimal(a);
                        System.out.println("OK: dodano zwierzę");
                    }

                    case "9" -> {
                        for (Field f : farm.getFields()) {
                            Crop c = f.getCrop();
                            System.out.println("Field#" + f.getId() + " -> " +
                                    (c == null ? "EMPTY" : (c.getType() + " stage=" + c.getStage() + " mature=" + c.isMature())));
                        }
                    }

                    case "10" -> {
                        int id = askInt(sc, "ID pola: ");
                        String cropName = ask(sc, "Roślina (wheat/corn): ").toLowerCase();
                        Crop crop = switch (cropName) {
                            case "wheat" -> new Wheat();
                            // case "corn" -> new Corn(); // Odkomentuj jeśli masz klasę Corn
                            default -> throw new IllegalArgumentException("Nieznana roślina: " + cropName);
                        };
                        farm.getFieldById(id).plant(crop);
                        System.out.println("OK: posadzono");
                    }

                    case "11" -> {
                        int id = askInt(sc, "ID pola: ");
                        int times = askInt(sc, "Ile razy podlać? ");
                        Field f = farm.getFieldById(id);
                        for (int i = 0; i < times; i++) f.water(farm.getInventory());
                        System.out.println("OK: podlano");
                    }

                    case "12" -> {
                        int id = askInt(sc, "ID pola: ");
                        int times = askInt(sc, "Ile razy nawozić? ");
                        Field f = farm.getFieldById(id);
                        for (int i = 0; i < times; i++) f.fertilize(farm.getInventory());
                        System.out.println("OK: nawieziono");
                    }

                    case "13" -> {
                        int id = askInt(sc, "ID pola: ");
                        boolean ok = farm.getFieldById(id).harvest(farm.getWarehouse());
                        System.out.println(ok ? "OK: zebrano" : "NIE: puste/niedojrzałe");
                    }

                    case "14" -> {
                        String name = ask(sc, "Imię zwierzęcia do sprzedania: ");
                        Animal found = null;


                        for(Animal a : farm.getAnimals()) {
                            if(a.getName().equalsIgnoreCase(name)) {
                                found = a;
                                break;
                            }
                        }

                        if (found == null) {
                            System.out.println("Nie znaleziono zwierzęcia: " + name);
                        } else {
                            // Sprawdzamy, czy do sprzedania
                            if (found instanceof Sellable) {
                                farm.getWallet().sell((Sellable) found); // Sprzedajemy przez Wallet
                                farm.getAnimals().remove(found); // Usuwamy z farmy fizycznie
                                System.out.println("Zwierzę sprzedane i usunięte z farmy.");
                            } else {
                                System.out.println("Tego nie można sprzedać.");
                            }
                        }
                    }

                    case "0" -> {
                        System.out.println("Koniec.");
                        return;
                    }

                    default -> System.out.println("Nieznana opcja.");
                }
            } catch (Exception e) {
                System.out.println("Błąd: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private static Worker findWorkerByName(Farm farm, String name) {
        for (Worker w : farm.getWorkers()) {
            if (w.getName().equalsIgnoreCase(name)) return w;
        }
        return null;
    }

    private static String ask(Scanner sc, String prompt) {
        System.out.print(prompt);
        return sc.nextLine().trim();
    }

    private static int askInt(Scanner sc, String prompt) {
        while (true) {
            String s = ask(sc, prompt);
            try { return Integer.parseInt(s); }
            catch (NumberFormatException ignored) { System.out.println("Podaj liczbę całkowitą."); }
        }
    }

    private static long askMoney(Scanner sc, String prompt) {
        while (true) {
            String s = ask(sc, prompt);
            try {
                return new BigDecimal(s).multiply(BigDecimal.valueOf(100)).longValueExact();
            } catch (Exception ignored) {
                System.out.println("Podaj kwotę np. 15.00");
            }
        }
    }
}