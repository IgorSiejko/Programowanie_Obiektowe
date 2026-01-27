package com.example.demo;

import com.example.demo.farm.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;


public class HelloApplication extends Application {

    // --- Core Logic ---
    private Farm farm;
    private GameClock clock;
    private Stage primaryStage;

    // --- UI Updates ---
    private Label statsLabel;
    private Label messageLabel;
    private Label inventoryLabel;

    // --- Containers ---
    private GridPane fieldsGrid;
    private VBox animalsListParam;
    private VBox workersListParam;
    private ComboBox<String> animalSelector;

    // --- Theme ---
    private final String COLOR_BG = "#eceff1";
    private final String COLOR_HEADER = "#2e7d32";
    private final String COLOR_SIDEBAR = "#ffffff";
    private final String COLOR_ACCENT = "#43a047";
    private final String COLOR_LOCKED = "#cfd8dc";
    private final String COLOR_WHEAT = "#fdd835";
    private final String COLOR_CORN = "#fff176";

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;


        farm = new Farm();
        clock = new GameClock();

        // Seed resources
        farm.getWallet().depositCents(30000);
        farm.getInventory().addFeed(50);
        farm.getInventory().addWater(50);
        farm.getInventory().addFertilizer(10);

        for (int i = 1; i <= 6; i++) farm.addField(new Field(i));

        // Main Layout (BorderPane)
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + COLOR_BG + ";");

        // --- TOP ---
        root.setTop(createCustomHeader());

        // --- CENTER (TABS) ---
        TabPane centerTabs = new TabPane();
        centerTabs.setStyle("-fx-background-color: transparent; -fx-tab-min-height: 40px;");

        // Tab 1: Fields (Centered Grid)
        fieldsGrid = new GridPane();
        fieldsGrid.setHgap(20);
        fieldsGrid.setVgap(20);
        fieldsGrid.setPadding(new Insets(40));
        fieldsGrid.setAlignment(Pos.CENTER);

        ScrollPane fieldsScroll = new ScrollPane(fieldsGrid);
        fieldsScroll.setFitToWidth(true);
        fieldsScroll.setFitToHeight(true);
        fieldsScroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        Tab tabFields = new Tab("🌱 Fields", fieldsScroll);
        tabFields.setClosable(false);

        // Tab 2: Animals
        animalsListParam = new VBox(10);
        animalsListParam.setPadding(new Insets(15));
        ScrollPane animalsScroll = new ScrollPane(animalsListParam);
        animalsScroll.setFitToWidth(true);
        animalsScroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        Tab tabAnimals = new Tab("🐄 Livestock", animalsScroll);
        tabAnimals.setClosable(false);

        // Tab 3: Workers
        workersListParam = new VBox(10);
        workersListParam.setPadding(new Insets(15));
        ScrollPane workersScroll = new ScrollPane(workersListParam);
        workersScroll.setFitToWidth(true);
        workersScroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        Tab tabWorkers = new Tab("👷 Staff", workersScroll);
        tabWorkers.setClosable(false);

        centerTabs.getTabs().addAll(tabFields, tabAnimals, tabWorkers);
        root.setCenter(centerTabs);

        // --- RIGHT (SCROLLABLE SIDEBAR) ---
        ScrollPane sidebarScroll = new ScrollPane(createSideContent());
        sidebarScroll.setFitToWidth(true);
        sidebarScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        sidebarScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        sidebarScroll.setPrefWidth(320);
        sidebarScroll.setStyle("-fx-background-color: " + COLOR_SIDEBAR + "; -fx-border-color: #ddd; -fx-border-width: 0 0 0 1;");

        root.setRight(sidebarScroll);

        // --- BOTTOM ---
        messageLabel = new Label("Welcome! Hire workers to automate your farm.");
        messageLabel.setMaxWidth(Double.MAX_VALUE);
        messageLabel.setPadding(new Insets(10));
        messageLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        styleMessage("System Ready", false);
        root.setBottom(messageLabel);

        refreshApp();

        Scene scene = new Scene(root, 1200, 600);
        stage.setTitle("Farm Simulator");
        stage.setScene(scene);
        stage.show();
    }

    private HBox createCustomHeader() {
        HBox header = new HBox(20);
        header.setPadding(new Insets(15, 25, 15, 25));
        header.setAlignment(Pos.CENTER_LEFT);
        header.setStyle("-fx-background-color: " + COLOR_HEADER + "; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.4), 10, 0, 0, 2);");

        Label title = new Label("🚜 FARM MANAGER");
        title.setTextFill(Color.WHITE);
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));

        statsLabel = new Label("Day: 0 | Cash: $0.00");
        statsLabel.setTextFill(Color.web("#fff9c4"));
        statsLabel.setFont(Font.font("Monospaced", FontWeight.BOLD, 18));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button btnExit = new Button("Exit");
        btnExit.setStyle("-fx-background-color: #d32f2f; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
        btnExit.setOnAction(e -> Platform.exit());

        header.getChildren().addAll(title, statsLabel, spacer, btnExit);
        return header;
    }

    private VBox createSideContent() {
        VBox box = new VBox(15);
        box.setPadding(new Insets(20));
        box.setStyle("-fx-background-color: " + COLOR_SIDEBAR + ";");

        // --- Time Control ---
        Button btnNextTick = new Button("End Day (Next Turn) 🌙");
        btnNextTick.setMaxWidth(Double.MAX_VALUE);
        btnNextTick.setPrefHeight(50);
        btnNextTick.setStyle("-fx-background-color: #1565c0; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand; -fx-font-size: 14;");
        btnNextTick.setOnAction(e -> handleNextTurn());

        // --- Animal Market ---
        VBox shopBox = new VBox(8);
        Label lblShop = new Label("🐾 ANIMAL MARKET");
        lblShop.setFont(Font.font("System", FontWeight.BOLD, 12));

        animalSelector = new ComboBox<>();
        animalSelector.getItems().addAll("🐔 Chicken - $20.00", "🐑 Sheep - $40.00", "🐄 Cow - $50.00");
        animalSelector.getSelectionModel().selectFirst();
        animalSelector.setMaxWidth(Double.MAX_VALUE);

        Button btnBuyAnimal = new Button("Buy Animal");
        styleActionBtn(btnBuyAnimal);
        btnBuyAnimal.setOnAction(e -> handleBuyAnimal());
        shopBox.getChildren().addAll(lblShop, animalSelector, btnBuyAnimal);

        // --- HR Dept ---
        VBox workerBox = new VBox(8);
        Label lblWork = new Label("👷 HR DEPARTMENT");
        lblWork.setFont(Font.font("System", FontWeight.BOLD, 12));

        Label lblInfo = new Label("Assign workers to sectors\nto auto-feed or auto-farm.");
        lblInfo.setWrapText(true);
        lblInfo.setStyle("-fx-text-fill: #555; -fx-font-size: 11;");

        Button btnHire = new Button("Hire Worker ($2/day)");
        btnHire.setMaxWidth(Double.MAX_VALUE);
        btnHire.setStyle("-fx-background-color: #00897b; -fx-text-fill: white; -fx-cursor: hand;");
        btnHire.setOnAction(e -> handleHireWorker());
        workerBox.getChildren().addAll(lblWork, lblInfo, btnHire);

        // --- Resources ---
        VBox resBox = new VBox(8);
        Label lblRes = new Label("💧 FARM SUPPLIES");
        lblRes.setFont(Font.font("System", FontWeight.BOLD, 12));

        Button btnWater = new Button("Buy Water ($2.00)");
        styleSmallBtn(btnWater);
        btnWater.setOnAction(e -> handleBuyResource("WATER", 200, 10));

        Button btnFeed = new Button("Buy Feed ($5.00)");
        styleSmallBtn(btnFeed);
        btnFeed.setOnAction(e -> handleBuyResource("FEED", 500, 10));

        Button btnFert = new Button("Buy Fertilizer ($8.00)");
        styleSmallBtn(btnFert);
        btnFert.setOnAction(e -> handleBuyResource("FERT", 800, 2));

        resBox.getChildren().addAll(lblRes, btnWater, btnFeed, btnFert);

        // --- Warehouse ---
        VBox sellBox = new VBox(8);
        Label lblSell = new Label("💰 WAREHOUSE");
        lblSell.setFont(Font.font("System", FontWeight.BOLD, 12));

        Button btnSellAll = new Button("Sell All Crops");
        btnSellAll.setMaxWidth(Double.MAX_VALUE);
        btnSellAll.setPrefHeight(40);
        btnSellAll.setStyle("-fx-background-color: #ff8f00; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
        btnSellAll.setOnAction(e -> handleSellWarehouse());
        sellBox.getChildren().addAll(lblSell, btnSellAll);

        // --- Inventory Status ---
        inventoryLabel = new Label();
        inventoryLabel.setWrapText(true);
        inventoryLabel.setStyle("-fx-font-family: 'Monospaced'; -fx-font-size: 12; -fx-background-color: #eee; -fx-padding: 10; -fx-background-radius: 5;");

        box.getChildren().addAll(
                btnNextTick,
                new Separator(), shopBox,
                new Separator(), workerBox,
                new Separator(), resBox,
                new Separator(), sellBox,
                new Separator(), inventoryLabel
        );

        return box;
    }

    // ============================
    //      GAME LOGIC
    // ============================

    private void handleNextTurn() {
        farm.tick(clock.nextTick());

        // --- WORKER AUTOMATION ---
        for(Worker w : farm.getWorkers()) {

            // 1. Auto-Feed Animals
            if(w.getSector() == Sector.ANIMALS) {
                for(Animal a : farm.getAnimals()) {
                    if(a.getHunger() > 20) {
                        farm.getInventory().consumeFeed(1);
                        // Backend logic handles actual hunger reduction in a.tick()
                    }
                }
            }

            // 2. Auto-Farm Fields
            if(w.getSector() == Sector.FIELDS) {
                for(Field f : farm.getFields()) {
                    if(!f.isFree()) { // Only care about planted fields

                        // We use the helper to peek at levels since we can't edit Field.java
                        int currentWater = getPrivateFieldInt(f, "waterLevel");
                        int currentFert = getPrivateFieldInt(f, "fertilizerLevel");

                        // Water if dry (0)
                        if(currentWater == 0) {
                            f.water(farm.getInventory());
                        }
                        // Fertilize if low (0)
                        if(currentFert == 0) {
                            f.fertilize(farm.getInventory());
                        }
                    }
                }
            }
        }

        // Casualties check
        int animalsBefore = farm.getAnimals().size();
        farm.getAnimals().removeIf(a -> !a.isAlive());
        if (farm.getAnimals().size() < animalsBefore) {
            updateMessage("☠️ SAD NEWS: An animal has died!", true);
        } else {
            updateMessage("Day " + clock.now() + " ended. Workers paid.", false);
        }
        refreshApp();
    }

    // Helper to read private fields from Field.java without editing it
    private int getPrivateFieldInt(Object obj, String fieldName) {
        try {
            java.lang.reflect.Field f = obj.getClass().getDeclaredField(fieldName);
            f.setAccessible(true);
            return f.getInt(obj);
        } catch (Exception e) {
            return 0; // Default if fail
        }
    }

    private void handleBuyAnimal() {
        String sel = animalSelector.getValue();
        if(sel == null) return;

        TextInputDialog dialog = new TextInputDialog("Bessie");
        dialog.setTitle("New Animal");
        dialog.setHeaderText("Name your new friend!");
        dialog.setContentText("Enter name:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()){
            String customName = result.get();
            if(customName.trim().isEmpty()) customName = "Unnamed";

            Animal a = null; long cost = 0;
            if(sel.contains("Chicken")) { a = new Chicken(customName); cost=2000; }
            else if(sel.contains("Sheep")) { a = new Sheep(customName); cost=4000; }
            else if(sel.contains("Cow")) { a = new Cow(customName); cost=5000; }

            if(a!=null && farm.getWallet().withdrawCents(cost)) {
                farm.addAnimal(a);
                updateMessage("Purchased " + customName + "!", false);
                refreshApp();
            } else updateMessage("Not enough cash.", true);
        }
    }

    private void handleHireWorker() {
        TextInputDialog dialog = new TextInputDialog("Bob");
        dialog.setTitle("Hiring");
        dialog.setHeaderText("New Employee Contract");
        dialog.setContentText("Worker Name:");

        Optional<String> result = dialog.showAndWait();
        String name = result.orElse("Worker #" + (farm.getWorkers().size() + 1));
        if(name.trim().isEmpty()) name = "Worker";

        Worker w = new Worker(name, 200, 1);
        farm.hireWorker(w);
        updateMessage("Hired: " + name, false);
        refreshApp();
    }

    private void handleFireWorker(UUID id) {
        farm.fireWorker(id);
        updateMessage("Worker let go.", true);
        refreshApp();
    }

    private void handleWorkerAssign(Worker w, Sector s) {
        w.setSector(s);
        updateMessage(w.getName() + " is now working in: " + s, false);
        refreshApp();
    }

    private void handleBuyResource(String type, long cost, int amount) {
        if(farm.getWallet().withdrawCents(cost)) {
            if("WATER".equals(type)) farm.getInventory().addWater(amount);
            if("FEED".equals(type)) farm.getInventory().addFeed(amount);
            if("FERT".equals(type)) farm.getInventory().addFertilizer(amount);
            updateMessage("Supplies acquired.", false);
            refreshApp();
        } else {
            updateMessage("Too expensive!", true);
        }
    }

    private void handleSellWarehouse() {
        Warehouse wh = farm.getWarehouse();
        Map<ProductType, Integer> map = wh.snapshot();
        if(map.isEmpty()) { updateMessage("Warehouse is empty.", true); return; }

        long sum = 0;
        for(var e : map.entrySet()) sum += (getPrice(e.getKey()) * e.getValue());

        farm.getWallet().depositCents(sum);
        try { wh.getClass().getMethod("clear").invoke(wh); } catch(Exception ex){}
        updateMessage("Sold inventory for $" + (sum/100.0), false);
        refreshApp();
    }

    private long getPrice(ProductType pt) {
        switch(pt) {
            case MILK: return 150; case EGGS: return 50; case WOOL: return 300;
            case WHEAT: return 350; case CORN: return 450; default: return 0;
        }
    }

    // ============================
    //      RENDERING UI
    // ============================

    private void refreshApp() {
        statsLabel.setText(String.format("Day: %d  |  Cash: %s  |  Workers: %d",
                clock.now(), farm.getWallet().getFormatted(), farm.getWorkers().size()));

        StringBuilder sb = new StringBuilder("📦 STORAGE:\n");
        Inventory inv = farm.getInventory();
        sb.append(String.format("💧 Water: %d\n🍲 Feed:  %d\n🧪 Fert:  %d\n\n🌾 HARVEST:\n",
                inv.getWater(), inv.getFeed(), inv.getFertilizer()));
        var map = farm.getWarehouse().snapshot();
        if(map.isEmpty()) sb.append("(Empty)");
        else map.forEach((k,v)->sb.append("• ").append(k).append(": ").append(v).append("\n"));
        inventoryLabel.setText(sb.toString());

        fieldsGrid.getChildren().clear();
        int c=0, r=0;
        int activeFieldsCount = 1 + farm.getWorkers().size();

        for(Field f : farm.getFields()) {
            boolean isLocked = f.getId() > activeFieldsCount;
            fieldsGrid.add(createFancyField(f, isLocked), c, r);
            c++; if(c>2) { c=0; r++; }
        }

        animalsListParam.getChildren().clear();
        if(farm.getAnimals().isEmpty()) {
            Label empty = new Label("No animals yet.");
            empty.setStyle("-fx-text-fill: #757575;");
            animalsListParam.getChildren().add(empty);
        } else {
            for(Animal a : new ArrayList<>(farm.getAnimals())) {
                animalsListParam.getChildren().add(createAnimalRow(a));
            }
        }

        workersListParam.getChildren().clear();
        if(farm.getWorkers().isEmpty()) {
            Label empty = new Label("No staff hired.");
            empty.setStyle("-fx-text-fill: #757575;");
            workersListParam.getChildren().add(empty);
        } else {
            for(Worker w : new ArrayList<>(farm.getWorkers())) {
                workersListParam.getChildren().add(createWorkerRow(w));
            }
        }
    }

    private VBox createFancyField(Field f, boolean isLocked) {
        VBox box = new VBox(5);
        box.setPrefSize(180, 160);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(10));

        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.rgb(0,0,0,0.1));
        box.setEffect(shadow);
        String styleBase = "-fx-background-radius: 8; -fx-border-radius: 8; -fx-border-width: 1; ";

        if (isLocked) {
            box.setStyle(styleBase + "-fx-background-color: " + COLOR_LOCKED + "; -fx-border-color: #b0bec5;");
            Label icon = new Label("🔒"); icon.setFont(Font.font(40));
            Label txt = new Label("LOCKED"); txt.setStyle("-fx-text-fill: #757575; -fx-font-weight: bold;");
            box.getChildren().addAll(icon, txt);
            return box;
        }

        if (f.isFree()) {
            box.setStyle(styleBase + "-fx-background-color: #d7ccc8; -fx-border-color: #8d6e63;");
            Label icon = new Label("🟫"); icon.setFont(Font.font(40));
            Label txt = new Label("Empty Field"); txt.setStyle("-fx-text-fill: #3e2723; -fx-font-weight: bold;");

            MenuButton btnPlant = new MenuButton("Plant...");
            btnPlant.setStyle("-fx-base: #a1887f; -fx-font-size: 11px;");

            MenuItem w = new MenuItem("Wheat ($10)"); w.setOnAction(e->plant(f, new Wheat(), 1000));
            MenuItem corn = new MenuItem("Corn ($15)"); corn.setOnAction(e->plant(f, new Corn(), 1500));
            btnPlant.getItems().addAll(w, corn);

            box.getChildren().addAll(icon, txt, btnPlant);
        } else {
            Crop crop = f.getCrop();
            String cropColor = (crop.getType() == ProductType.CORN) ? COLOR_CORN : COLOR_WHEAT;
            String cropIcon = (crop.getType() == ProductType.CORN) ? "🌽" : "🌾";

            // --- FIELD STATS ---
            int wLevel = getPrivateFieldInt(f, "waterLevel");
            int fLevel = getPrivateFieldInt(f, "fertilizerLevel");

            Tooltip tp = new Tooltip("Water: " + wLevel + " | Fert: " + fLevel);
            Tooltip.install(box, tp);

            // Visual indicator for stats
            Label statsLbl = new Label("💧" + wLevel + "  🧪" + fLevel);
            statsLbl.setStyle("-fx-font-size: 10px; -fx-text-fill: #333; -fx-background-color: rgba(255,255,255,0.6); -fx-background-radius: 4; -fx-padding: 2;");

            if (crop.isMature()) {
                box.setStyle(styleBase + "-fx-background-color: " + cropColor + "; -fx-border-color: #f57f17; -fx-border-width: 3;");
                Label icon = new Label(cropIcon); icon.setFont(Font.font(48));
                Button btnHarv = new Button("HARVEST");
                btnHarv.setStyle("-fx-background-color: #2e7d32; -fx-text-fill: white; -fx-font-weight: bold;");
                btnHarv.setOnAction(e -> { f.harvest(farm.getWarehouse()); refreshApp(); });
                box.getChildren().addAll(icon, new Label("Ready!"), btnHarv);
            } else {
                box.setStyle(styleBase + "-fx-background-color: #fff9c4; -fx-border-color: #fbc02d;");
                Label icon = new Label("🌱"); icon.setFont(Font.font(32));

                Label status = new Label(crop.getType() + "\n" + crop.getStage());
                status.setAlignment(Pos.CENTER);
                status.setStyle("-fx-text-fill: #333; -fx-font-size: 10px;");

                HBox actions = new HBox(5); actions.setAlignment(Pos.CENTER);
                Button btnWater = new Button("💧"); btnWater.setOnAction(e -> { f.water(farm.getInventory()); refreshApp(); });
                Button btnFert = new Button("🧪"); btnFert.setOnAction(e -> { f.fertilize(farm.getInventory()); refreshApp(); });
                actions.getChildren().addAll(btnWater, btnFert);

                box.getChildren().addAll(statsLbl, icon, status, actions);
            }
        }
        return box;
    }

    private void plant(Field f, Crop c, long cost) {
        if(farm.getWallet().withdrawCents(cost)) { f.plant(c); refreshApp(); }
        else updateMessage("Need more cash!", true);
    }

    private HBox createAnimalRow(Animal a) {
        HBox row = new HBox(15);
        row.setPadding(new Insets(10));
        row.setStyle("-fx-background-color: white; -fx-background-radius: 8; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 3,0,0,1);");
        row.setAlignment(Pos.CENTER_LEFT);

        Label icon = new Label(a instanceof Cow ? "🐄" : (a instanceof Sheep ? "🐑" : "🐔"));
        icon.setFont(Font.font("Segoe UI Emoji", 32));
        icon.setStyle("-fx-text-fill: black;");

        VBox statsBox = new VBox(4);
        statsBox.setPrefWidth(250);

        HBox hpBox = new HBox(5);
        Label lblHp = new Label("❤ HP:");
        lblHp.setStyle("-fx-text-fill: #212121; -fx-font-weight: bold;");
        lblHp.setPrefWidth(50);
        ProgressBar barHp = new ProgressBar(a.getHealth() / 100.0);
        barHp.setPrefWidth(120);
        barHp.setStyle("-fx-accent: " + (a.getHealth() < 30 ? "red" : "green") + ";");
        hpBox.getChildren().addAll(lblHp, barHp);

        HBox hungerBox = new HBox(5);
        Label lblHunger = new Label("🍔 Food:");
        lblHunger.setStyle("-fx-text-fill: #212121; -fx-font-weight: bold;");
        lblHunger.setPrefWidth(50);
        ProgressBar barHunger = new ProgressBar(a.getHunger() / 100.0);
        barHunger.setPrefWidth(120);
        barHunger.setStyle("-fx-accent: " + (a.getHunger() > 70 ? "red" : "#8bc34a") + ";");
        hungerBox.getChildren().addAll(lblHunger, barHunger);

        Label nameLabel = new Label(a.getName());
        nameLabel.setStyle("-fx-text-fill: #212121; -fx-font-weight: bold; -fx-font-size: 14px;");

        // --- PRODUCTION INDICATOR ---
        Label prodLabel = new Label(a.isProducing() ? "✨ Producing Resource" : "💤 Resting (Needs Care)");
        prodLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: " + (a.isProducing() ? "#2e7d32" : "#757575") + ";");

        statsBox.getChildren().addAll(nameLabel, hpBox, hungerBox, prodLabel);

        Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS);

        Button btnSell = new Button("Sell (" + ((Sellable)a).getFormattedPrice() + ")");
        btnSell.setStyle("-fx-background-color: #ef5350; -fx-text-fill: white; -fx-font-weight: bold;");
        btnSell.setOnAction(e -> {
            farm.getWallet().sell((Sellable)a);
            farm.getAnimals().remove(a);
            refreshApp();
        });

        row.getChildren().addAll(icon, statsBox, spacer, btnSell);
        return row;
    }

    private HBox createWorkerRow(Worker w) {
        HBox row = new HBox(15);
        row.setPadding(new Insets(10));
        row.setStyle("-fx-background-color: #e3f2fd; -fx-background-radius: 8; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 3,0,0,1);");
        row.setAlignment(Pos.CENTER_LEFT);

        Label icon = new Label("👷");
        icon.setFont(Font.font(32));
        icon.setStyle("-fx-text-fill: black;");

        VBox info = new VBox(4);
        Label name = new Label(w.getName());
        name.setStyle("-fx-text-fill: #0d47a1; -fx-font-weight: bold; -fx-font-size: 14px;");

        Label salary = new Label("Salary: $" + (w.getSalaryCents()/100.0));
        salary.setStyle("-fx-text-fill: #455a64;");
        info.getChildren().addAll(name, salary);

        Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS);

        VBox assignBox = new VBox(2);
        Label lblAssign = new Label("Assignment:");
        lblAssign.setStyle("-fx-text-fill: #455a64; -fx-font-size: 10px;");

        ComboBox<Sector> sectorBox = new ComboBox<>();
        sectorBox.getItems().setAll(Sector.values());
        sectorBox.setValue(w.getSector());
        sectorBox.setStyle("-fx-font-size: 11px;");
        sectorBox.setOnAction(e -> handleWorkerAssign(w, sectorBox.getValue()));

        assignBox.getChildren().addAll(lblAssign, sectorBox);

        Button btnFire = new Button("Fire");
        btnFire.setStyle("-fx-background-color: #c62828; -fx-text-fill: white;");
        btnFire.setOnAction(e -> handleFireWorker(w.getId()));

        row.getChildren().addAll(icon, info, spacer, assignBox, btnFire);
        return row;
    }

    private void styleActionBtn(Button b) {
        b.setMaxWidth(Double.MAX_VALUE);
        b.setStyle("-fx-background-color: " + COLOR_ACCENT + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
    }
    private void styleSmallBtn(Button b) {
        b.setMaxWidth(Double.MAX_VALUE); b.setWrapText(true);
        b.setStyle("-fx-background-color: #90caf9; -fx-cursor: hand; -fx-font-size: 12; -fx-text-fill: #000;");
    }
    private void styleMessage(String msg, boolean err) {
        messageLabel.setText(msg);
        messageLabel.setStyle(err
                ? "-fx-background-color: #d32f2f; -fx-text-fill: white; -fx-padding: 8; -fx-font-weight: bold;"
                : "-fx-background-color: #37474f; -fx-text-fill: white; -fx-padding: 8; -fx-font-weight: bold;");
    }
    private void updateMessage(String msg, boolean err) { styleMessage(msg, err); }

    public static void main(String[] args) { launch(); }
}