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

public class HelloApplication extends Application {

    private Farm farm;
    private GameClock clock;
    private Stage primaryStage;

    // GUI Elements
    private Label statsLabel;
    private Label messageLabel;
    private Label inventoryLabel;
    private GridPane fieldsGrid;
    private VBox animalsListParam;
    private ComboBox<String> animalSelector;

    // --- KOLORYSTYKA ---
    private final String COLOR_BG = "#f4f7f6";
    private final String COLOR_HEADER = "#2e7d32";
    private final String COLOR_SIDEBAR = "#ffffff";
    private final String COLOR_ACCENT = "#66bb6a";
    private final String COLOR_EARTH = "#8d6e63";
    private final String COLOR_WHEAT = "#fdd835";
    private final String COLOR_CORN = "#fff176";

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;

        // 1. SETUP LOGIKI
        farm = new Farm();
        clock = new GameClock();
        farm.getWallet().depositCents(20000);
        farm.getInventory().addFeed(20);
        farm.getInventory().addWater(50);
        farm.getInventory().addFertilizer(5); // Startowy nawóz

        for (int i = 1; i <= 6; i++) farm.addField(new Field(i));

        // 2. LAYOUT GŁÓWNY
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + COLOR_BG + ";");

        root.setTop(createCustomHeader());

        // Centrum - Zakładki
        TabPane centerTabs = new TabPane();
        centerTabs.setStyle("-fx-background-color: transparent; -fx-tab-min-height: 40px;");

        fieldsGrid = new GridPane();
        fieldsGrid.setHgap(20);
        fieldsGrid.setVgap(20);
        fieldsGrid.setPadding(new Insets(30));
        fieldsGrid.setAlignment(Pos.CENTER);
        ScrollPane fieldsScroll = new ScrollPane(fieldsGrid);
        fieldsScroll.setFitToWidth(true);
        fieldsScroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        Tab tabFields = new Tab("🌱 Twoje Pola", fieldsScroll);
        tabFields.setClosable(false);

        animalsListParam = new VBox(15);
        animalsListParam.setPadding(new Insets(20));
        ScrollPane animalsScroll = new ScrollPane(animalsListParam);
        animalsScroll.setFitToWidth(true);
        animalsScroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        Tab tabAnimals = new Tab("🐄 Zagroda", animalsScroll);
        tabAnimals.setClosable(false);

        centerTabs.getTabs().addAll(tabFields, tabAnimals);
        root.setCenter(centerTabs);

        root.setRight(createSidePanel());

        messageLabel = new Label("Witaj na farmie! Rozpocznij pracę.");
        messageLabel.setMaxWidth(Double.MAX_VALUE);
        messageLabel.setPadding(new Insets(10));
        messageLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        styleMessage("Start gry", false);
        root.setBottom(messageLabel);

        refreshApp();

        Scene scene = new Scene(root, 1200, 800);
        stage.setTitle("Java Farm Simulator 2026");
        stage.setScene(scene);
        stage.show();
    }

    // --- HEADER ---
    private HBox createCustomHeader() {
        HBox header = new HBox(20);
        header.setPadding(new Insets(15, 20, 15, 20));
        header.setAlignment(Pos.CENTER_LEFT);
        header.setStyle("-fx-background-color: " + COLOR_HEADER + "; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 10, 0, 0, 5);");

        Label title = new Label("🚜 FARM MANAGER 2026");
        title.setTextFill(Color.WHITE);
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 24));

        statsLabel = new Label();
        statsLabel.setTextFill(Color.web("#e8f5e9"));
        statsLabel.setFont(Font.font("Monospaced", FontWeight.BOLD, 16));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button btnFull = new Button("⛶ Pełny Ekran");
        styleHeaderBtn(btnFull);
        btnFull.setOnAction(e -> primaryStage.setFullScreen(!primaryStage.isFullScreen()));

        Button btnExit = new Button("✖ Zamknij");
        styleHeaderBtn(btnExit);
        btnExit.setStyle("-fx-background-color: #c62828; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
        btnExit.setOnAction(e -> Platform.exit());

        header.getChildren().addAll(title, statsLabel, spacer, btnFull, btnExit);
        return header;
    }

    // --- PANEL BOCZNY ---
    private VBox createSidePanel() {
        VBox box = new VBox(20);
        box.setPadding(new Insets(20));
        box.setPrefWidth(320);
        box.setStyle("-fx-background-color: " + COLOR_SIDEBAR + "; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, -5, 0);");

        // Czas
        Button btnNextTick = new Button("Zakończ Dzień (Next Tick) 🌙");
        btnNextTick.setMaxWidth(Double.MAX_VALUE);
        btnNextTick.setPrefHeight(40);
        btnNextTick.setStyle("-fx-background-color: #3f51b5; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
        btnNextTick.setOnAction(e -> {
            farm.tick(clock.nextTick());
            updateMessage("Minął dzień " + clock.now(), false);
            refreshApp();
        });

        // Sklep Zwierzęta
        VBox shopBox = new VBox(10);
        Label lblShop = new Label("🐾 SKLEP ZOOLOGICZNY");
        lblShop.setFont(Font.font(null, FontWeight.BOLD, 12));

        animalSelector = new ComboBox<>();
        animalSelector.getItems().addAll("🐔 Kurczak - 20.00", "🐑 Owca - 40.00", "🐄 Krowa - 50.00");
        animalSelector.getSelectionModel().selectFirst();
        animalSelector.setMaxWidth(Double.MAX_VALUE);

        Button btnBuyAnimal = new Button("Kup Zwierzę");
        styleActionBtn(btnBuyAnimal);
        btnBuyAnimal.setOnAction(e -> handleBuyAnimal());
        shopBox.getChildren().addAll(lblShop, animalSelector, btnBuyAnimal);

        // Zasoby
        VBox resBox = new VBox(10);
        Label lblRes = new Label("💧 ZASOBY I PASZA");
        lblRes.setFont(Font.font(null, FontWeight.BOLD, 12));

        GridPane resGrid = new GridPane();
        resGrid.setHgap(10); resGrid.setVgap(10);

        Button btnWater = new Button("Woda (+10)\n2.00 zł");
        styleSmallBtn(btnWater);
        btnWater.setOnAction(e -> handleBuyResource("WATER", 200, 10));

        Button btnFeed = new Button("Pasza (+10)\n5.00 zł");
        styleSmallBtn(btnFeed);
        btnFeed.setOnAction(e -> handleBuyResource("FEED", 500, 10));

        Button btnFert = new Button("Nawóz (+2)\n8.00 zł");
        styleSmallBtn(btnFert);
        btnFert.setOnAction(e -> handleBuyResource("FERT", 800, 2));

        resGrid.add(btnWater, 0, 0);
        resGrid.add(btnFeed, 1, 0);
        resGrid.add(btnFert, 0, 1);
        resBox.getChildren().addAll(lblRes, resGrid);

        // Sprzedaż
        VBox sellBox = new VBox(10);
        Label lblSell = new Label("💰 RYNEK ZBYTU");
        lblSell.setFont(Font.font(null, FontWeight.BOLD, 12));

        Button btnSellAll = new Button("Sprzedaj plony z magazynu");
        btnSellAll.setMaxWidth(Double.MAX_VALUE);
        btnSellAll.setStyle("-fx-background-color: #ff9800; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
        btnSellAll.setOnAction(e -> handleSellWarehouse());
        sellBox.getChildren().addAll(lblSell, btnSellAll);

        // Magazyn
        inventoryLabel = new Label();
        inventoryLabel.setWrapText(true);
        inventoryLabel.setStyle("-fx-font-family: 'Monospaced'; -fx-background-color: #eee; -fx-padding: 10; -fx-background-radius: 5;");
        VBox.setVgrow(inventoryLabel, Priority.ALWAYS);

        box.getChildren().addAll(btnNextTick, new Separator(), shopBox, new Separator(), resBox, new Separator(), sellBox, new Separator(), inventoryLabel);
        return box;
    }

    // --- LOGIKA ---

    private void handleBuyResource(String type, long cost, int amount) {
        if(farm.getWallet().withdrawCents(cost)) {
            if("WATER".equals(type)) farm.getInventory().addWater(amount);
            if("FEED".equals(type)) farm.getInventory().addFeed(amount);
            if("FERT".equals(type)) farm.getInventory().addFertilizer(amount);
            updateMessage("Kupiono zasoby!", false);
            refreshApp();
        } else {
            updateMessage("Brak środków!", true);
        }
    }

    private void handleBuyAnimal() {
        String sel = animalSelector.getValue();
        if(sel == null) return;
        Animal a = null; long c = 0;
        if(sel.contains("Kurczak")) { a = new Chicken("Kura "+(farm.getAnimals().size()+1)); c=2000; }
        else if(sel.contains("Owca")) { a = new Sheep("Owca "+(farm.getAnimals().size()+1)); c=4000; }
        else if(sel.contains("Krowa")) { a = new Cow("Krowa "+(farm.getAnimals().size()+1)); c=5000; }

        if(a!=null && farm.getWallet().withdrawCents(c)) {
            farm.addAnimal(a);
            updateMessage("Nowe zwierzę w zagrodzie!", false);
            refreshApp();
        } else updateMessage("Za mało pieniędzy.", true);
    }

    private void handleSellWarehouse() {
        Warehouse wh = farm.getWarehouse();
        Map<ProductType, Integer> map = wh.snapshot();
        if(map.isEmpty()) { updateMessage("Magazyn pusty.", true); return; }

        long sum = 0;
        for(var e : map.entrySet()) sum += (getPrice(e.getKey()) * e.getValue());

        farm.getWallet().depositCents(sum);
        try { wh.getClass().getMethod("clear").invoke(wh); } catch(Exception ex){}
        updateMessage("Sprzedano za " + (sum/100.0), false);
        refreshApp();
    }

    private void handleSellAnimal(Animal a) {
        if(a instanceof Sellable) {
            farm.getWallet().sell((Sellable)a);
            farm.getAnimals().remove(a);
            refreshApp();
        }
    }

    private long getPrice(ProductType pt) {
        switch(pt) {
            case MILK: return 150; case EGGS: return 50; case WOOL: return 300;
            case WHEAT: return 350; case CORN: return 450; default: return 0;
        }
    }

    // --- RYSOWANIE GUI ---

    private void refreshApp() {
        statsLabel.setText(String.format("📅 Dzień: %d   |   💵 %s",
                clock.now(), farm.getWallet().getFormatted()));

        StringBuilder sb = new StringBuilder("📦 ZASOBY:\n");
        Inventory inv = farm.getInventory();
        sb.append(String.format("💧 Woda: %d\n🍲 Pasza: %d\n🧪 Nawóz: %d\n\n🌾 PLONY:\n",
                inv.getWater(), inv.getFeed(), inv.getFertilizer()));
        var map = farm.getWarehouse().snapshot();
        if(map.isEmpty()) sb.append("(pusto)");
        else map.forEach((k,v)->sb.append("• ").append(k).append(": ").append(v).append("\n"));
        inventoryLabel.setText(sb.toString());

        fieldsGrid.getChildren().clear();
        int c=0, r=0;
        for(Field f : farm.getFields()) {
            fieldsGrid.add(createFancyField(f), c, r);
            c++; if(c>2) { c=0; r++; }
        }

        animalsListParam.getChildren().clear();
        if(farm.getAnimals().isEmpty()) animalsListParam.getChildren().add(new Label("Zagroda pusta."));
        else for(Animal a : new ArrayList<>(farm.getAnimals())) animalsListParam.getChildren().add(createAnimalRow(a));
    }

    // --- ZAKTUALIZOWANA METODA POLA (Z NAWOŻENIEM) ---
    private VBox createFancyField(Field f) {
        VBox box = new VBox(5);
        box.setPrefSize(180, 160);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(10));

        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.rgb(0,0,0,0.2));
        box.setEffect(shadow);

        String styleBase = "-fx-background-radius: 10; -fx-border-radius: 10; -fx-border-width: 2; ";

        if (f.isFree()) {
            box.setStyle(styleBase + "-fx-background-color: " + COLOR_EARTH + "; -fx-border-color: #5d4037;");
            Label icon = new Label("🟫");
            icon.setFont(Font.font(40));
            Label txt = new Label("Ugór #" + f.getId());
            txt.setTextFill(Color.WHITE);
            txt.setFont(Font.font("System", FontWeight.BOLD, 14));

            MenuButton btnPlant = new MenuButton("Zasiej 🌱");
            btnPlant.setStyle("-fx-base: #a1887f; -fx-text-fill: black;");
            MenuItem w = new MenuItem("Pszenica (10.00)"); w.setOnAction(e->plant(f, new Wheat(), 1000));
            MenuItem corn = new MenuItem("Kukurydza (15.00)"); corn.setOnAction(e->plant(f, new Corn(), 1500));
            btnPlant.getItems().addAll(w, corn);

            box.getChildren().addAll(icon, txt, btnPlant);
        } else {
            Crop crop = f.getCrop();
            String cropColor = (crop.getType() == ProductType.CORN) ? COLOR_CORN : COLOR_WHEAT;
            String cropIcon = (crop.getType() == ProductType.CORN) ? "🌽" : "🌾";

            if (crop.isMature()) {
                box.setStyle(styleBase + "-fx-background-color: " + cropColor + "; -fx-border-color: #f57f17; -fx-border-width: 4;");
                Label icon = new Label(cropIcon);
                icon.setFont(Font.font(50));

                Button btnHarv = new Button("ZBIERZ!");
                btnHarv.setStyle("-fx-background-color: #2e7d32; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
                btnHarv.setOnAction(e -> { f.harvest(farm.getWarehouse()); refreshApp(); });

                box.getChildren().addAll(icon, new Label("Gotowe!"), btnHarv);
            } else {
                // Rośnie - TU ZMIANA (DODANO NAWÓZ)
                box.setStyle(styleBase + "-fx-background-color: #fff9c4; -fx-border-color: #fbc02d;");
                Label icon = new Label("🌱");
                icon.setFont(Font.font(30));

                Label status = new Label(crop.getType() + "\n" + crop.getStage());
                status.setAlignment(Pos.CENTER);

                // Kontener na przyciski akcji
                HBox actions = new HBox(5);
                actions.setAlignment(Pos.CENTER);

                Button btnWater = new Button("Podlej 💧");
                btnWater.setStyle("-fx-font-size: 10; -fx-base: #e3f2fd;");
                btnWater.setOnAction(e -> { f.water(farm.getInventory()); refreshApp(); });

                Button btnFert = new Button("Nawóz 🧪");
                btnFert.setStyle("-fx-font-size: 10; -fx-base: #d7ccc8;");
                btnFert.setOnAction(e -> { f.fertilize(farm.getInventory()); refreshApp(); });

                actions.getChildren().addAll(btnWater, btnFert);

                box.getChildren().addAll(icon, status, actions);
            }
        }
        return box;
    }

    private void plant(Field f, Crop c, long cost) {
        if(farm.getWallet().withdrawCents(cost)) { f.plant(c); refreshApp(); }
        else updateMessage("Brak pieniędzy!", true);
    }

    private HBox createAnimalRow(Animal a) {
        HBox row = new HBox(15);
        row.setPadding(new Insets(10));
        row.setStyle("-fx-background-color: white; -fx-background-radius: 8; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 3,0,0,1);");
        row.setAlignment(Pos.CENTER_LEFT);

        Label icon = new Label(a instanceof Cow ? "🐄" : (a instanceof Sheep ? "🐑" : "🐔"));
        icon.setFont(Font.font(24));

        VBox info = new VBox(2,
                new Label(a.getName()),
                new Label("♥ " + a.getHealth() + "%  🍔 " + a.getHunger() + "%")
        );
        ((Label)info.getChildren().get(1)).setTextFill(Color.GRAY);

        Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS);

        Button btnSell = new Button("Sprzedaj");
        if(a instanceof Sellable) {
            btnSell.setText("Sprzedaj ("+((Sellable)a).getFormattedPrice()+")");
            btnSell.setStyle("-fx-background-color: #ffccbc; -fx-text-fill: #d84315; -fx-cursor: hand;");
            btnSell.setOnAction(e->handleSellAnimal(a));
        }
        row.getChildren().addAll(icon, info, spacer, btnSell);
        return row;
    }

    private void styleHeaderBtn(Button b) {
        b.setStyle("-fx-background-color: rgba(255,255,255,0.2); -fx-text-fill: white; -fx-cursor: hand;");
    }
    private void styleActionBtn(Button b) {
        b.setMaxWidth(Double.MAX_VALUE);
        b.setStyle("-fx-background-color: " + COLOR_ACCENT + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
    }
    private void styleSmallBtn(Button b) {
        b.setMaxWidth(Double.MAX_VALUE);
        b.setWrapText(true);
        b.setStyle("-fx-background-color: #90caf9; -fx-cursor: hand; -fx-font-size: 11;");
    }
    private void styleMessage(String msg, boolean err) {
        messageLabel.setText(msg);
        messageLabel.setStyle(err
                ? "-fx-background-color: #e57373; -fx-text-fill: white; -fx-padding: 10;"
                : "-fx-background-color: #37474f; -fx-text-fill: white; -fx-padding: 10;");
    }
    private void updateMessage(String msg, boolean err) { styleMessage(msg, err); }

    public static void main(String[] args) { launch(); }
}