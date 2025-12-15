import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class SPOTApplication extends Application {
    private static final int TOTAL_SPOTS = 10;
    private Map<Integer, ParkingTicket> parkedVehicles = new HashMap<>();
    private Map<String, Integer> vehicleToSpot = new HashMap<>();
    private Queue<String> waitingList = new LinkedList<>();
    
    private Label totalSpotsLabel, occupiedLabel, availableLabel, occupancyLabel;
    private TextArea vehiclesDisplay;
    private TextArea waitingListDisplay;
    
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("SPOT - Smart Parking Optimization for Tenants");
        primaryStage.setWidth(1000);
        primaryStage.setHeight(700);
        
        // Create main layout
        BorderPane root = new BorderPane();
        root.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 12;");
        
        // Header
        VBox header = createHeader();
        root.setTop(header);
        
        // Tab Pane
        TabPane tabPane = createTabPane();
        root.setCenter(tabPane);
        
        // Footer
        Label footer = new Label("© 2025 SPOT Parking Management System");
        footer.setStyle("-fx-padding: 10; -fx-text-alignment: center; -fx-font-size: 10;");
        root.setBottom(footer);
        
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();
        
        // Update dashboard initially
        updateDashboard();
    }
    
    private VBox createHeader() {
        VBox header = new VBox();
        header.setStyle("-fx-background-color: linear-gradient(to right, #2563eb, #10b981); " +
                        "-fx-padding: 20; -fx-alignment: center;");
        header.setSpacing(5);
        
        Label title = new Label("🅿️ SPOT");
        title.setStyle("-fx-font-size: 36; -fx-text-fill: white; -fx-font-weight: bold;");
        
        Label subtitle = new Label("Smart Parking Optimization for Tenants");
        subtitle.setStyle("-fx-font-size: 14; -fx-text-fill: white;");
        
        header.getChildren().addAll(title, subtitle);
        return header;
    }
    
    private TabPane createTabPane() {
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        
        tabPane.getTabs().addAll(
            createDashboardTab(),
            createCheckInTab(),
            createCheckOutTab(),
            createSearchTab(),
            createWaitingListTab()
        );
        
        return tabPane;
    }
    
    private Tab createDashboardTab() {
        Tab tab = new Tab("Dashboard");
        
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        
        // Status cards
        HBox statusBox = new HBox(15);
        statusBox.setPrefHeight(120);
        
        statusBox.getChildren().addAll(
            createStatusCard("Total Spots", "10", "#2563eb"),
            createStatusCardWithLabel("Occupied", occupiedLabel = new Label("0"), "#ef4444"),
            createStatusCardWithLabel("Available", availableLabel = new Label("10"), "#10b981"),
            createStatusCardWithLabel("Occupancy Rate", occupancyLabel = new Label("0%"), "#2563eb")
        );
        
        // Vehicles list
        Label vehiclesTitle = new Label("Parked Vehicles");
        vehiclesTitle.setStyle("-fx-font-size: 14; -fx-font-weight: bold;");
        
        vehiclesDisplay = new TextArea();
        vehiclesDisplay.setEditable(false);
        vehiclesDisplay.setWrapText(true);
        vehiclesDisplay.setText("No vehicles parked");
        vehiclesDisplay.setPrefRowCount(15);
        
        content.getChildren().addAll(statusBox, vehiclesTitle, vehiclesDisplay);
        
        tab.setContent(new ScrollPane(content));
        return tab;
    }
    
    private Tab createCheckInTab() {
        Tab tab = new Tab("Check In");
        
        VBox content = new VBox(15);
        content.setPadding(new Insets(30));
        content.setMaxWidth(500);
        
        Label title = new Label("Check In Vehicle");
        title.setStyle("-fx-font-size: 16; -fx-font-weight: bold;");
        
        TextField tenantNameField = new TextField();
        tenantNameField.setPromptText("Enter tenant name");
        
        TextField vehiclePlateField = new TextField();
        vehiclePlateField.setPromptText("e.g., ABC-1234");
        
        Label messageLabel = new Label();
        messageLabel.setWrapText(true);
        
        Button checkInBtn = new Button("Check In");
        checkInBtn.setPrefWidth(Double.MAX_VALUE);
        checkInBtn.setStyle("-fx-padding: 10; -fx-font-size: 12;");
        checkInBtn.setOnAction(e -> {
            String tenantName = tenantNameField.getText().trim();
            String vehiclePlate = vehiclePlateField.getText().trim().toUpperCase();
            
            if (tenantName.isEmpty() || vehiclePlate.isEmpty()) {
                messageLabel.setText("Please fill in all fields");
                messageLabel.setStyle("-fx-text-fill: red;");
                return;
            }
            
            if (vehicleToSpot.containsKey(vehiclePlate)) {
                messageLabel.setText("Vehicle already parked");
                messageLabel.setStyle("-fx-text-fill: red;");
                return;
            }
            
            int spot = findAvailableSpot();
            if (spot == -1) {
                waitingList.offer(tenantName + " (" + vehiclePlate + ")");
                messageLabel.setText("✓ Added to waiting list (Position: " + waitingList.size() + ")");
                messageLabel.setStyle("-fx-text-fill: orange;");
            } else {
                ParkingTicket ticket = new ParkingTicket(tenantName, vehiclePlate, spot);
                parkedVehicles.put(spot, ticket);
                vehicleToSpot.put(vehiclePlate, spot);
                
                messageLabel.setText("✓ Check-in successful! Spot: #" + spot);
                messageLabel.setStyle("-fx-text-fill: green;");
                tenantNameField.clear();
                vehiclePlateField.clear();
                updateDashboard();
            }
        });
        
        content.getChildren().addAll(
            title,
            new Label("Tenant Name:"),
            tenantNameField,
            new Label("Vehicle Plate:"),
            vehiclePlateField,
            checkInBtn,
            messageLabel
        );
        
        tab.setContent(new ScrollPane(content));
        return tab;
    }
    
    private Tab createCheckOutTab() {
        Tab tab = new Tab("Check Out");
        
        VBox content = new VBox(15);
        content.setPadding(new Insets(30));
        content.setMaxWidth(500);
        
        Label title = new Label("Check Out Vehicle");
        title.setStyle("-fx-font-size: 16; -fx-font-weight: bold;");
        
        Spinner<Integer> spotSpinner = new Spinner<>(1, TOTAL_SPOTS, 1);
        spotSpinner.setPrefWidth(Double.MAX_VALUE);
        
        Label messageLabel = new Label();
        messageLabel.setWrapText(true);
        
        TextArea reportArea = new TextArea();
        reportArea.setEditable(false);
        reportArea.setWrapText(true);
        reportArea.setPrefRowCount(8);
        reportArea.setVisible(false);
        
        Button checkOutBtn = new Button("Check Out");
        checkOutBtn.setPrefWidth(Double.MAX_VALUE);
        checkOutBtn.setStyle("-fx-padding: 10; -fx-font-size: 12;");
        checkOutBtn.setOnAction(e -> {
            int spotNumber = spotSpinner.getValue();
            
            if (!parkedVehicles.containsKey(spotNumber)) {
                messageLabel.setText("Spot #" + spotNumber + " is vacant");
                messageLabel.setStyle("-fx-text-fill: red;");
                reportArea.setVisible(false);
                return;
            }
            
            ParkingTicket ticket = parkedVehicles.get(spotNumber);
            LocalDateTime checkOutTime = LocalDateTime.now();
            long durationMinutes = java.time.temporal.ChronoUnit.MINUTES.between(ticket.checkInTime, checkOutTime);
            long hours = durationMinutes / 60;
            long minutes = durationMinutes % 60;
            
            parkedVehicles.remove(spotNumber);
            vehicleToSpot.remove(ticket.vehiclePlate);
            
            messageLabel.setText("✓ Vehicle checked out successfully");
            messageLabel.setStyle("-fx-text-fill: green;");
            
            String report = String.format(
                "--- Checkout Report for Spot #%d ---\n" +
                "Tenant: %s\n" +
                "Plate: %s\n" +
                "Check-in: %s\n" +
                "Check-out: %s\n" +
                "Duration: %dh %dm\n" +
                "--------------------------------",
                spotNumber, ticket.tenantName, ticket.vehiclePlate,
                ticket.getCheckInTimeString(),
                checkOutTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                hours, minutes
            );
            
            reportArea.setText(report);
            reportArea.setVisible(true);
            updateDashboard();
        });
        
        content.getChildren().addAll(
            title,
            new Label("Spot Number (1-" + TOTAL_SPOTS + "):"),
            spotSpinner,
            checkOutBtn,
            messageLabel,
            reportArea
        );
        
        tab.setContent(new ScrollPane(content));
        return tab;
    }
    
    private Tab createSearchTab() {
        Tab tab = new Tab("Search");
        
        VBox content = new VBox(15);
        content.setPadding(new Insets(30));
        content.setMaxWidth(500);
        
        Label title = new Label("Search Vehicle");
        title.setStyle("-fx-font-size: 16; -fx-font-weight: bold;");
        
        TextField searchField = new TextField();
        searchField.setPromptText("Enter vehicle plate");
        
        TextArea resultArea = new TextArea();
        resultArea.setEditable(false);
        resultArea.setWrapText(true);
        resultArea.setPrefRowCount(10);
        resultArea.setVisible(false);
        
        Button searchBtn = new Button("Search");
        searchBtn.setPrefWidth(Double.MAX_VALUE);
        searchBtn.setStyle("-fx-padding: 10; -fx-font-size: 12;");
        searchBtn.setOnAction(e -> {
            String plate = searchField.getText().trim().toUpperCase();
            
            if (!vehicleToSpot.containsKey(plate)) {
                resultArea.setText("Vehicle not found");
                resultArea.setVisible(true);
                return;
            }
            
            int spotNum = vehicleToSpot.get(plate);
            ParkingTicket ticket = parkedVehicles.get(spotNum);
            
            String result = String.format(
                "--- Vehicle Found ---\n" +
                "Spot: #%d\n" +
                "Tenant: %s\n" +
                "Plate: %s\n" +
                "Check-in: %s\n" +
                "-------------------",
                spotNum, ticket.tenantName, ticket.vehiclePlate,
                ticket.getCheckInTimeString()
            );
            
            resultArea.setText(result);
            resultArea.setVisible(true);
        });
        
        content.getChildren().addAll(
            title,
            new Label("Vehicle Plate:"),
            searchField,
            searchBtn,
            resultArea
        );
        
        tab.setContent(new ScrollPane(content));
        return tab;
    }
    
    private Tab createWaitingListTab() {
        Tab tab = new Tab("Waiting List");
        
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        
        Label title = new Label("Waiting List");
        title.setStyle("-fx-font-size: 16; -fx-font-weight: bold;");
        
        waitingListDisplay = new TextArea();
        waitingListDisplay.setEditable(false);
        waitingListDisplay.setWrapText(true);
        waitingListDisplay.setText("No vehicles waiting");
        waitingListDisplay.setPrefRowCount(20);
        
        content.getChildren().addAll(title, waitingListDisplay);
        
        tab.setContent(new ScrollPane(content));
        return tab;
    }
    
    private VBox createStatusCard(String title, String value, String color) {
        VBox card = new VBox();
        card.setStyle("-fx-border-color: " + color + "; -fx-border-width: 0 0 0 3; " +
                      "-fx-background-color: white; -fx-padding: 15; -fx-border-radius: 5;");
        card.setPrefWidth(150);
        card.setSpacing(10);
        
        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 11; -fx-text-fill: gray;");
        
        Label valueLabel = new Label(value);
        valueLabel.setStyle("-fx-font-size: 28; -fx-font-weight: bold; -fx-text-fill: " + color + ";");
        
        card.getChildren().addAll(titleLabel, valueLabel);
        return card;
    }
    
    private VBox createStatusCardWithLabel(String title, Label valueLabel, String color) {
        VBox card = new VBox();
        card.setStyle("-fx-border-color: " + color + "; -fx-border-width: 0 0 0 3; " +
                      "-fx-background-color: white; -fx-padding: 15; -fx-border-radius: 5;");
        card.setPrefWidth(150);
        card.setSpacing(10);
        
        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 11; -fx-text-fill: gray;");
        
        valueLabel.setStyle("-fx-font-size: 28; -fx-font-weight: bold; -fx-text-fill: " + color + ";");
        
        card.getChildren().addAll(titleLabel, valueLabel);
        return card;
    }
    
    private int findAvailableSpot() {
        for (int i = 1; i <= TOTAL_SPOTS; i++) {
            if (!parkedVehicles.containsKey(i)) {
                return i;
            }
        }
        return -1;
    }
    
    private void updateDashboard() {
        occupiedLabel.setText(String.valueOf(parkedVehicles.size()));
        availableLabel.setText(String.valueOf(TOTAL_SPOTS - parkedVehicles.size()));
        double rate = (parkedVehicles.size() * 100.0) / TOTAL_SPOTS;
        occupancyLabel.setText(String.format("%.1f%%", rate));
        
        StringBuilder sb = new StringBuilder();
        if (parkedVehicles.isEmpty()) {
            sb.append("No vehicles parked");
        } else {
            parkedVehicles.entrySet().stream()
                .sorted((a, b) -> Integer.compare(a.getKey(), b.getKey()))
                .forEach(entry -> {
                    ParkingTicket ticket = entry.getValue();
                    sb.append("Spot #").append(entry.getKey())
                      .append(" | Tenant: ").append(ticket.tenantName)
                      .append(" | Plate: ").append(ticket.vehiclePlate)
                      .append(" | Check-in: ").append(ticket.getCheckInTimeString())
                      .append("\n\n");
                });
        }
        vehiclesDisplay.setText(sb.toString());
        
        // Update waiting list
        StringBuilder waitingSb = new StringBuilder();
        if (waitingList.isEmpty()) {
            waitingSb.append("No vehicles waiting");
        } else {
            int position = 1;
            for (String vehicle : waitingList) {
                waitingSb.append(position).append(". ").append(vehicle).append("\n");
                position++;
            }
        }
        waitingListDisplay.setText(waitingSb.toString());
    }
    
    public static void main(String[] args) {
        launch(args);
    }
    
    // ParkingTicket class
    private static class ParkingTicket {
        String tenantName;
        String vehiclePlate;
        int spotNumber;
        LocalDateTime checkInTime;
        
        ParkingTicket(String tenantName, String vehiclePlate, int spotNumber) {
            this.tenantName = tenantName;
            this.vehiclePlate = vehiclePlate;
            this.spotNumber = spotNumber;
            this.checkInTime = LocalDateTime.now();
        }
        
        String getCheckInTimeString() {
            return checkInTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }
    }
}
