import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.*;

/**
 * SPOTDashboardController - Main Controller for SPOT Parking Management System
 * Demonstrates:
 * - HashMap for O(1) slot lookup
 * - Queue for waiting list (FIFO)
 * - ArrayList for dynamic storage
 * - CRUD operations: Create, Read, Update, Delete
 */
public class SPOTDashboardController implements Initializable {
    
    // Data Structures
    // HashMap: Fast O(1) lookup of slots by ID
    private HashMap<String, ParkingSlot> slotsMap = new HashMap<>();
    
    // Queue: FIFO waiting list for booking requests when lot is full
    private Queue<String> waitingQueue = new LinkedList<>();
    
    // ArrayList: Dynamic storage for maintaining insertion order
    private ArrayList<ParkingSlot> slotsList = new ArrayList<>();
    
    // Constants
    private static final int TOTAL_SLOTS = 10;
    private static final String[] SLOT_IDS = {"A1", "A2", "A3", "A4", "A5", "B1", "B2", "B3", "B4", "B5"};
    
    // ==================== FXML Injections ====================
    
    // Header
    @FXML private Label activeSessionsLabel;
    
    // Sidebar
    @FXML private Button dashboardBtn;
    @FXML private Button bookSlotBtn;
    @FXML private Button releaseSlotBtn;
    @FXML private Button searchSlotBtn;
    @FXML private Label totalSlotsLabel;
    @FXML private Label availableSlotsLabel;
    @FXML private Label occupiedSlotsLabel;
    
    // Content Area
    @FXML private StackPane contentArea;
    @FXML private VBox dashboardView;
    @FXML private VBox bookSlotView;
    @FXML private VBox releaseSlotView;
    @FXML private VBox searchSlotView;
    
    // Dashboard Tab
    @FXML private GridPane slotsGrid;
    @FXML private ListView<String> waitingQueueView;
    
    // Book Slot Tab
    @FXML private TextField tenantNameField;
    @FXML private TextField vehiclePlateField;
    @FXML private ComboBox<String> availableSlotsCombo;
    @FXML private Label bookStatusLabel;
    
    // Release Slot Tab
    @FXML private ComboBox<String> occupiedSlotsCombo;
    @FXML private Label durationLabel;
    @FXML private TextArea tenantInfoArea;
    @FXML private Label releaseStatusLabel;
    
    // Search Slot Tab
    @FXML private TextField searchSlotIdField;
    @FXML private TextArea searchResultsArea;
    @FXML private VBox searchResultsBox;
    
    // Footer
    @FXML private Label footerStatusLabel;
    
    // ==================== Initialization ====================
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialize parking slots with dummy data
        initializeParkingSlots();
        
        // Setup UI components
        setupSlotsGrid();
        updateStatistics();
        updateAvailableSlotsCombo();
        updateOccupiedSlotsCombo();
        updateWaitingQueueView();
        
        // Set active view to dashboard
        showDashboard();
        
        // Add event listeners
        setupOccupiedSlotsComboListener();
        
        // Update footer
        footerStatusLabel.setText("✓ System Ready");
    }
    
    /**
     * Initialize parking slots with dummy data
     * Demonstrates: Insertion into HashMap and ArrayList
     */
    private void initializeParkingSlots() {
        // Create all slots (initially available)
        for (String slotId : SLOT_IDS) {
            ParkingSlot slot = new ParkingSlot(slotId);
            slotsMap.put(slotId, slot);
            slotsList.add(slot);
        }
        
        // Add some dummy occupied slots for demonstration
        ParkingSlot slotA1 = slotsMap.get("A1");
        slotA1.occupy("John Doe", "ABC-1234");
        
        ParkingSlot slotB2 = slotsMap.get("B2");
        slotB2.occupy("Jane Smith", "XYZ-9876");
        
        // Add waiting customers
        waitingQueue.offer("Maria Garcia");
        waitingQueue.offer("Robert Johnson");
    }
    
    // ==================== Dashboard View Methods ====================
    
    /**
     * Setup the slots grid display with cards
     * Demonstrates: Traversal of HashMap
     */
    private void setupSlotsGrid() {
        slotsGrid.setHgap(15);
        slotsGrid.setVgap(15);
        slotsGrid.setStyle("-fx-padding: 10;");
        
        int col = 0, row = 0;
        // Traverse all slots in HashMap
        for (ParkingSlot slot : slotsMap.values()) {
            VBox slotCard = createSlotCard(slot);
            slotsGrid.add(slotCard, col, row);
            
            col++;
            if (col >= 5) {
                col = 0;
                row++;
            }
        }
    }
    
    /**
     * Create a visual card for a parking slot
     */
    private VBox createSlotCard(ParkingSlot slot) {
        VBox card = new VBox(8);
        card.setStyle(
            "-fx-border-color: #e8eef7; " +
            "-fx-border-width: 1; " +
            "-fx-border-radius: 10; " +
            "-fx-padding: 15; " +
            "-fx-background-color: #ffffff; " +
            "-fx-background-radius: 10; " +
            "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.08), 6, 0, 0, 2); " +
            "-fx-min-height: 130; " +
            "-fx-pref-width: 130;"
        );
        card.setAlignment(Pos.TOP_CENTER);
        
        // Slot ID
        Label slotIdLabel = new Label(slot.getSlotId());
        slotIdLabel.setStyle("-fx-font-size: 20; -fx-font-weight: bold; -fx-text-fill: #2d3748;");
        
        // Status badge
        Label statusLabel = new Label(slot.getStatus());
        if (slot.isAvailable()) {
            statusLabel.setStyle(
                "-fx-font-size: 10; " +
                "-fx-font-weight: bold; " +
                "-fx-text-fill: #38a169; " +
                "-fx-padding: 4 8 4 8; " +
                "-fx-background-color: #c6f6d5; " +
                "-fx-background-radius: 4;"
            );
        } else {
            statusLabel.setStyle(
                "-fx-font-size: 10; " +
                "-fx-font-weight: bold; " +
                "-fx-text-fill: #e53e3e; " +
                "-fx-padding: 4 8 4 8; " +
                "-fx-background-color: #fed7d7; " +
                "-fx-background-radius: 4;"
            );
        }
        
        // Tenant/vehicle info
        Label infoLabel = new Label();
        if (slot.isOccupied()) {
            infoLabel.setText(slot.getVehiclePlate());
            infoLabel.setStyle("-fx-font-size: 9; -fx-text-fill: #718096; -fx-wrap-text: true;");
        } else {
            infoLabel.setText("Available");
            infoLabel.setStyle("-fx-font-size: 9; -fx-text-fill: #38a169; -fx-font-weight: bold;");
        }
        
        card.getChildren().addAll(slotIdLabel, statusLabel, infoLabel);
        
        return card;
    }
    
    /**
     * Update waiting queue view
     * Demonstrates: Queue traversal
     */
    private void updateWaitingQueueView() {
        ObservableList<String> items = FXCollections.observableArrayList();
        
        // Traverse queue (copy elements to preserve queue state)
        Queue<String> tempQueue = new LinkedList<>(waitingQueue);
        int position = 1;
        while (!tempQueue.isEmpty()) {
            String customer = tempQueue.poll();
            items.add(position + ". " + customer);
            position++;
        }
        
        waitingQueueView.setItems(items);
    }
    
    /**
     * Update statistics labels
     * Demonstrates: Traversal and counting
     */
    private void updateStatistics() {
        int total = slotsMap.size();
        int occupied = 0;
        
        // Traverse HashMap to count occupied slots
        for (ParkingSlot slot : slotsMap.values()) {
            if (slot.isOccupied()) {
                occupied++;
            }
        }
        
        int available = total - occupied;
        
        totalSlotsLabel.setText(String.valueOf(total));
        occupiedSlotsLabel.setText(String.valueOf(occupied));
        availableSlotsLabel.setText(String.valueOf(available));
        activeSessionsLabel.setText(String.valueOf(occupied));
    }
    
    // ==================== Book Slot Methods ====================
    
    /**
     * Update available slots combo box
     * Demonstrates: Filtering HashMap values
     */
    private void updateAvailableSlotsCombo() {
        ObservableList<String> items = FXCollections.observableArrayList();
        
        for (ParkingSlot slot : slotsMap.values()) {
            if (slot.isAvailable()) {
                items.add(slot.getSlotId());
            }
        }
        
        availableSlotsCombo.setItems(items);
    }
    
    @FXML
    private void bookSlot() {
        String tenantName = tenantNameField.getText().trim();
        String vehiclePlate = vehiclePlateField.getText().trim();
        String selectedSlot = availableSlotsCombo.getValue();
        
        // Validation
        if (tenantName.isEmpty()) {
            showMessage(bookStatusLabel, "⚠ Enter tenant name", "error");
            return;
        }
        if (vehiclePlate.isEmpty()) {
            showMessage(bookStatusLabel, "⚠ Enter vehicle plate", "error");
            return;
        }
        if (selectedSlot == null) {
            showMessage(bookStatusLabel, "⚠ Select a parking slot", "error");
            return;
        }
        
        // Get slot from HashMap (O(1) lookup)
        ParkingSlot slot = slotsMap.get(selectedSlot);
        if (slot == null || !slot.isAvailable()) {
            // Add to waiting queue if slot not available
            waitingQueue.offer(tenantName + " (" + vehiclePlate + ")");
            showMessage(bookStatusLabel, "✓ Added to waiting queue", "info");
            updateWaitingQueueView();
            return;
        }
        
        // Occupy the slot
        slot.occupy(tenantName, vehiclePlate);
        showMessage(bookStatusLabel, "✓ Slot booked successfully!", "success");
        
        // Update UI
        updateStatistics();
        updateAvailableSlotsCombo();
        updateOccupiedSlotsCombo();
        setupSlotsGrid(); // Refresh grid
        clearBookForm();
    }
    
    @FXML
    private void clearBookForm() {
        tenantNameField.clear();
        vehiclePlateField.clear();
        availableSlotsCombo.setValue(null);
        bookStatusLabel.setText("");
    }
    
    // ==================== Release Slot Methods ====================
    
    /**
     * Update occupied slots combo box
     * Demonstrates: Filtering HashMap values
     */
    private void updateOccupiedSlotsCombo() {
        ObservableList<String> items = FXCollections.observableArrayList();
        
        for (ParkingSlot slot : slotsMap.values()) {
            if (slot.isOccupied()) {
                items.add(slot.getSlotId() + " (" + slot.getVehiclePlate() + ")");
            }
        }
        
        occupiedSlotsCombo.setItems(items);
    }
    
    /**
     * Setup listener for occupied slots combo
     */
    private void setupOccupiedSlotsComboListener() {
        occupiedSlotsCombo.setOnAction(event -> {
            String selectedItem = occupiedSlotsCombo.getValue();
            if (selectedItem != null) {
                String slotId = selectedItem.split(" ")[0];
                ParkingSlot slot = slotsMap.get(slotId);
                
                if (slot != null && slot.isOccupied()) {
                    durationLabel.setText(slot.getFormattedDuration());
                    tenantInfoArea.setText(
                        "Slot: " + slot.getSlotId() + "\n" +
                        "Tenant: " + slot.getTenantName() + "\n" +
                        "Vehicle: " + slot.getVehiclePlate() + "\n" +
                        "Check-in: " + slot.getFormattedCheckInTime() + "\n" +
                        "Duration: " + slot.getFormattedDuration()
                    );
                }
            }
        });
    }
    
    @FXML
    private void releaseSlot() {
        String selectedItem = occupiedSlotsCombo.getValue();
        
        if (selectedItem == null) {
            showMessage(releaseStatusLabel, "⚠ Select a parking slot", "error");
            return;
        }
        
        String slotId = selectedItem.split(" ")[0];
        ParkingSlot slot = slotsMap.get(slotId);
        
        if (slot == null || !slot.isOccupied()) {
            showMessage(releaseStatusLabel, "⚠ Slot is not occupied", "error");
            return;
        }
        
        // Release the slot
        slot.release();
        showMessage(releaseStatusLabel, "✓ Slot released successfully!", "success");
        
        // Check if there are waiting customers
        if (!waitingQueue.isEmpty()) {
            String nextCustomer = waitingQueue.poll();
            showMessage(releaseStatusLabel, "✓ Next customer: " + nextCustomer, "info");
            updateWaitingQueueView();
        }
        
        // Update UI
        updateStatistics();
        updateAvailableSlotsCombo();
        updateOccupiedSlotsCombo();
        setupSlotsGrid(); // Refresh grid
        clearReleaseForm();
    }
    
    @FXML
    private void clearReleaseForm() {
        occupiedSlotsCombo.setValue(null);
        durationLabel.setText("N/A");
        tenantInfoArea.clear();
        releaseStatusLabel.setText("");
    }
    
    // ==================== Search Slot Methods ====================
    
    @FXML
    private void searchSlot() {
        String searchId = searchSlotIdField.getText().trim().toUpperCase();
        
        if (searchId.isEmpty()) {
            showMessage(searchSlotIdField, "⚠ Enter a slot ID", "error");
            return;
        }
        
        // Search in HashMap (O(1) lookup)
        ParkingSlot slot = slotsMap.get(searchId);
        
        StringBuilder results = new StringBuilder();
        if (slot != null) {
            results.append("╔════════════════════════════════════╗\n");
            results.append("║         SLOT INFORMATION           ║\n");
            results.append("╚════════════════════════════════════╝\n\n");
            results.append("Slot ID: ").append(slot.getSlotId()).append("\n");
            results.append("Status: ").append(slot.getStatus()).append("\n");
            results.append("Tenant: ").append(slot.getTenantName().isEmpty() ? "N/A" : slot.getTenantName()).append("\n");
            results.append("Vehicle: ").append(slot.getVehiclePlate().isEmpty() ? "N/A" : slot.getVehiclePlate()).append("\n");
            
            if (slot.isOccupied()) {
                results.append("Check-in: ").append(slot.getFormattedCheckInTime()).append("\n");
                results.append("Duration: ").append(slot.getFormattedDuration()).append("\n");
            }
        } else {
            results.append("❌ Slot '").append(searchId).append("' not found!\n");
            results.append("Available slots: ").append(String.join(", ", availableSlotsCombo.getItems()));
        }
        
        searchResultsArea.setText(results.toString());
    }
    
    // ==================== Navigation Methods ====================
    
    @FXML
    private void showDashboard() {
        setActiveButton(dashboardBtn);
        contentArea.getChildren().clear();
        contentArea.getChildren().add(dashboardView);
        updateWaitingQueueView(); // Refresh waiting list
        setupSlotsGrid(); // Refresh slots grid
    }
    
    @FXML
    private void showBookSlot() {
        setActiveButton(bookSlotBtn);
        contentArea.getChildren().clear();
        contentArea.getChildren().add(bookSlotView);
        updateAvailableSlotsCombo();
    }
    
    @FXML
    private void showReleaseSlot() {
        setActiveButton(releaseSlotBtn);
        contentArea.getChildren().clear();
        contentArea.getChildren().add(releaseSlotView);
        updateOccupiedSlotsCombo();
    }
    
    @FXML
    private void showSearchSlot() {
        setActiveButton(searchSlotBtn);
        contentArea.getChildren().clear();
        contentArea.getChildren().add(searchSlotView);
        searchSlotIdField.clear();
        searchResultsArea.clear();
    }
    
    /**
     * Set the active navigation button
     */
    private void setActiveButton(Button activeBtn) {
        dashboardBtn.getStyleClass().remove("nav-button-active");
        bookSlotBtn.getStyleClass().remove("nav-button-active");
        releaseSlotBtn.getStyleClass().remove("nav-button-active");
        searchSlotBtn.getStyleClass().remove("nav-button-active");
        
        activeBtn.getStyleClass().add("nav-button-active");
    }
    
    // ==================== Utility Methods ====================
    
    /**
     * Show a status message with color coding
     */
    private void showMessage(Label label, String message, String type) {
        label.setText(message);
        label.getStyleClass().clear();
        label.getStyleClass().add("status-message");
        label.getStyleClass().add("status-" + type);
    }
    
    /**
     * Show a status message for text field
     */
    private void showMessage(TextField field, String message, String type) {
        // Shake effect on error
        if ("error".equals(type)) {
            field.setStyle("-fx-border-color: #f56565; -fx-border-width: 2;");
            field.setOnMouseExited(e -> field.setStyle(""));
        }
    }
}
