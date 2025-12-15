import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.*;

/**
 * SPOTDashboardProController - Professional Controller for SPOT Parking Management
 * 
 * Data Structures:
 * - HashMap<String, ParkingSlot>: O(1) fast slot lookup by ID
 * - Queue<String>: FIFO waiting list for booking requests
 * - ArrayList<ParkingSlot>: Dynamic storage with insertion order preservation
 * 
 * Demonstrates all CRUD operations:
 * - Create (Insert): Book a parking slot
 * - Read (Search): Find slot by ID
 * - Update: Modify slot status
 * - Delete: Release a parking slot
 */
public class SPOTDashboardProController implements Initializable {
    
    // ==================== DATA STRUCTURES ====================
    
    private HashMap<String, ParkingSlot> slotsMap = new HashMap<>();
    private Queue<String> waitingQueue = new LinkedList<>();
    private ArrayList<ParkingSlot> slotsList = new ArrayList<>();
    
    private static final int TOTAL_SLOTS = 10;
    private static final String[] SLOT_IDS = {"A1", "A2", "A3", "A4", "A5", "B1", "B2", "B3", "B4", "B5"};
    
    // ==================== FXML INJECTIONS - NAVBAR ==================== 
    
    @FXML private ImageView logoImageView;
    @FXML private Label activeSessionsLabel;
    @FXML private Label occupancyRateLabel;
    
    // ==================== FXML INJECTIONS - SIDEBAR ====================
    
    @FXML private Button navDashboard;
    @FXML private Button navBook;
    @FXML private Button navRelease;
    @FXML private Button navSearch;
    
    @FXML private Label totalSlotsLabel;
    @FXML private Label availableSlotsLabel;
    @FXML private Label occupiedSlotsLabel;
    
    // ==================== FXML INJECTIONS - CONTENT AREA ====================
    
    @FXML private StackPane contentArea;
    @FXML private VBox dashboardView;
    @FXML private VBox bookSlotView;
    @FXML private VBox releaseSlotView;
    @FXML private VBox searchSlotView;
    
    // Dashboard
    @FXML private GridPane slotsGrid;
    @FXML private ListView<String> waitingQueueView;
    
    // Book Slot
    @FXML private TextField tenantNameField;
    @FXML private TextField vehiclePlateField;
    @FXML private ComboBox<String> availableSlotsCombo;
    @FXML private Label bookStatusLabel;
    
    // Release Slot
    @FXML private ComboBox<String> occupiedSlotsCombo;
    @FXML private Label durationLabel;
    @FXML private TextArea tenantInfoArea;
    @FXML private Label releaseStatusLabel;
    
    // Search Slot
    @FXML private TextField searchSlotIdField;
    @FXML private TextArea searchResultsArea;
    @FXML private VBox searchResultsBox;
    
    // Footer
    @FXML private Label footerStatusLabel;
    
    // ==================== INITIALIZATION ====================
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadLogo();
        initializeParkingSlots();
        setupUI();
        showDashboard();
        updateAllStatistics();
    }
    
    /**
     * Load the SPOT logo from SVG file
     */
    private void loadLogo() {
        try {
            String logoPath = getClass().getResource("spot-logo.svg").toExternalForm();
            Image logoImage = new Image(logoPath);
            logoImageView.setImage(logoImage);
        } catch (Exception e) {
            System.out.println("Logo file not found. Proceeding without logo.");
        }
    }
    
    /**
     * Initialize parking system with dummy data
     * Demonstrates: HashMap insertion and ArrayList insertion
     */
    private void initializeParkingSlots() {
        // Create all parking slots
        for (String slotId : SLOT_IDS) {
            ParkingSlot slot = new ParkingSlot(slotId);
            slotsMap.put(slotId, slot);
            slotsList.add(slot);
        }
        
        // Add sample occupied slots
        ParkingSlot slotA1 = slotsMap.get("A1");
        slotA1.occupy("John Smith", "JXK-4521");
        
        ParkingSlot slotB3 = slotsMap.get("B3");
        slotB3.occupy("Maria Johnson", "LMN-8765");
        
        // Add sample waiting customers
        waitingQueue.offer("Alex Brown");
        waitingQueue.offer("Sophie Williams");
    }
    
    /**
     * Setup UI components and listeners
     */
    private void setupUI() {
        updateWaitingQueueView();
        updateAvailableSlotsCombo();
        updateOccupiedSlotsCombo();
        setupOccupiedSlotsListener();
    }
    
    // ==================== STATISTICS & UPDATES ====================
    
    /**
     * Update all statistics across the application
     * Demonstrates: HashMap traversal and counting
     */
    private void updateAllStatistics() {
        int total = slotsMap.size();
        int occupied = 0;
        
        // Traverse HashMap to count occupied slots
        for (ParkingSlot slot : slotsMap.values()) {
            if (slot.isOccupied()) {
                occupied++;
            }
        }
        
        int available = total - occupied;
        double occupancyRate = total > 0 ? (occupied * 100.0) / total : 0;
        
        // Update labels
        totalSlotsLabel.setText(String.valueOf(total));
        occupiedSlotsLabel.setText(String.valueOf(occupied));
        availableSlotsLabel.setText(String.valueOf(available));
        activeSessionsLabel.setText(String.valueOf(occupied));
        occupancyRateLabel.setText(String.format("%.1f%%", occupancyRate));
    }
    
    /**
     * Update waiting queue display
     * Demonstrates: Queue traversal without dequeuing
     */
    private void updateWaitingQueueView() {
        ObservableList<String> items = FXCollections.observableArrayList();
        
        Queue<String> tempQueue = new LinkedList<>(waitingQueue);
        int position = 1;
        while (!tempQueue.isEmpty()) {
            items.add(position + ". " + tempQueue.poll());
            position++;
        }
        
        waitingQueueView.setItems(items);
    }
    
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
     * Setup listener for occupied slots combo to display tenant info
     */
    private void setupOccupiedSlotsListener() {
        occupiedSlotsCombo.setOnAction(event -> {
            String selectedItem = occupiedSlotsCombo.getValue();
            if (selectedItem != null) {
                String slotId = selectedItem.split(" ")[0];
                ParkingSlot slot = slotsMap.get(slotId);
                
                if (slot != null && slot.isOccupied()) {
                    durationLabel.setText(slot.getFormattedDuration());
                    tenantInfoArea.setText(
                        "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
                        "Slot ID:        " + slot.getSlotId() + "\n" +
                        "Tenant Name:    " + slot.getTenantName() + "\n" +
                        "Vehicle Plate:  " + slot.getVehiclePlate() + "\n" +
                        "Check-in Time:  " + slot.getFormattedCheckInTime() + "\n" +
                        "Duration:       " + slot.getFormattedDuration() + "\n" +
                        "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
                    );
                }
            }
        });
    }
    
    // ==================== DASHBOARD VIEW ====================
    
    /**
     * Setup the parking slots grid with visual cards
     * Demonstrates: HashMap traversal
     */
    private void setupSlotsGrid() {
        slotsGrid.getChildren().clear();
        slotsGrid.setStyle("-fx-hgap: 16; -fx-vgap: 16; -fx-padding: 8;");
        
        int col = 0, row = 0;
        for (ParkingSlot slot : slotsMap.values()) {
            VBox card = createSlotCard(slot);
            slotsGrid.add(card, col, row);
            
            col++;
            if (col >= 5) {
                col = 0;
                row++;
            }
        }
    }
    
    /**
     * Create a professional slot card UI component
     */
    private VBox createSlotCard(ParkingSlot slot) {
        VBox card = new VBox(10);
        card.setStyle(
            "-fx-border-color: #30363D; " +
            "-fx-border-width: 1; " +
            "-fx-border-radius: 8; " +
            "-fx-padding: 16; " +
            "-fx-background-color: #1C1F26; " +
            "-fx-background-radius: 8; " +
            "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.2), 6, 0, 0, 1); " +
            "-fx-min-width: 140; " +
            "-fx-min-height: 140;"
        );
        card.setAlignment(Pos.TOP_CENTER);
        
        // Slot ID
        Label slotIdLabel = new Label(slot.getSlotId());
        slotIdLabel.setStyle(
            "-fx-font-size: 24; " +
            "-fx-font-weight: bold; " +
            "-fx-text-fill: #58A6FF;"
        );
        
        // Status badge
        Label statusLabel = new Label(slot.getStatus());
        if (slot.isAvailable()) {
            statusLabel.setStyle(
                "-fx-font-size: 10; " +
                "-fx-font-weight: bold; " +
                "-fx-text-fill: #ffffff; " +
                "-fx-padding: 6 10 6 10; " +
                "-fx-background-color: #3FB950; " +
                "-fx-background-radius: 4;"
            );
        } else {
            statusLabel.setStyle(
                "-fx-font-size: 10; " +
                "-fx-font-weight: bold; " +
                "-fx-text-fill: #ffffff; " +
                "-fx-padding: 6 10 6 10; " +
                "-fx-background-color: #F85149; " +
                "-fx-background-radius: 4;"
            );
        }
        
        // Vehicle info
        Label infoLabel = new Label();
        if (slot.isOccupied()) {
            infoLabel.setText(slot.getVehiclePlate());
            infoLabel.setStyle(
                "-fx-font-size: 10; " +
                "-fx-text-fill: #C9D1D9; " +
                "-fx-font-weight: 500; " +
                "-fx-wrap-text: true;"
            );
        } else {
            infoLabel.setText("Available");
            infoLabel.setStyle(
                "-fx-font-size: 10; " +
                "-fx-text-fill: #3FB950; " +
                "-fx-font-weight: bold;"
            );
        }
        
        card.getChildren().addAll(slotIdLabel, statusLabel, infoLabel);
        
        return card;
    }
    
    // ==================== BOOK SLOT OPERATIONS ====================
    
    @FXML
    private void bookSlot() {
        String tenantName = tenantNameField.getText().trim();
        String vehiclePlate = vehiclePlateField.getText().trim();
        String selectedSlot = availableSlotsCombo.getValue();
        
        // Validation
        if (tenantName.isEmpty()) {
            showMessage(bookStatusLabel, "⚠ Please enter tenant name", "error");
            return;
        }
        if (vehiclePlate.isEmpty()) {
            showMessage(bookStatusLabel, "⚠ Please enter vehicle plate", "error");
            return;
        }
        if (selectedSlot == null) {
            showMessage(bookStatusLabel, "⚠ Please select a parking slot", "error");
            return;
        }
        
        // Get slot from HashMap (O(1) operation)
        ParkingSlot slot = slotsMap.get(selectedSlot);
        if (slot == null || !slot.isAvailable()) {
            // Add to waiting queue if slot not available
            waitingQueue.offer(tenantName + " (" + vehiclePlate + ")");
            showMessage(bookStatusLabel, "✓ Added to waiting queue", "info");
            updateWaitingQueueView();
            clearBookForm();
            return;
        }
        
        // Book the slot
        slot.occupy(tenantName, vehiclePlate);
        showMessage(bookStatusLabel, "✓ Slot booked successfully!", "success");
        
        // Update UI
        updateAllStatistics();
        updateAvailableSlotsCombo();
        updateOccupiedSlotsCombo();
        setupSlotsGrid();
        clearBookForm();
    }
    
    @FXML
    private void clearBookForm() {
        tenantNameField.clear();
        vehiclePlateField.clear();
        availableSlotsCombo.setValue(null);
        bookStatusLabel.setText("");
    }
    
    // ==================== RELEASE SLOT OPERATIONS ====================
    
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
        
        // Process waiting queue
        if (!waitingQueue.isEmpty()) {
            String nextCustomer = waitingQueue.poll();
            showMessage(releaseStatusLabel, "✓ Next customer: " + nextCustomer, "info");
            updateWaitingQueueView();
        }
        
        // Update UI
        updateAllStatistics();
        updateAvailableSlotsCombo();
        updateOccupiedSlotsCombo();
        setupSlotsGrid();
        clearReleaseForm();
    }
    
    @FXML
    private void clearReleaseForm() {
        occupiedSlotsCombo.setValue(null);
        durationLabel.setText("N/A");
        tenantInfoArea.clear();
        releaseStatusLabel.setText("");
    }
    
    // ==================== SEARCH SLOT OPERATIONS ====================
    
    @FXML
    private void searchSlot() {
        String searchId = searchSlotIdField.getText().trim().toUpperCase();
        
        if (searchId.isEmpty()) {
            searchResultsArea.setText("Enter a slot ID to search (e.g., A1, B5)");
            return;
        }
        
        // Search in HashMap (O(1) operation)
        ParkingSlot slot = slotsMap.get(searchId);
        
        StringBuilder results = new StringBuilder();
        if (slot != null) {
            results.append("╔══════════════════════════════════╗\n");
            results.append("║      SLOT INFORMATION            ║\n");
            results.append("╚══════════════════════════════════╝\n\n");
            results.append("Slot ID:        ").append(slot.getSlotId()).append("\n");
            results.append("Status:         ").append(slot.getStatus()).append("\n");
            results.append("Tenant:         ").append(
                slot.getTenantName().isEmpty() ? "[Empty]" : slot.getTenantName()
            ).append("\n");
            results.append("Vehicle Plate:  ").append(
                slot.getVehiclePlate().isEmpty() ? "[None]" : slot.getVehiclePlate()
            ).append("\n");
            
            if (slot.isOccupied()) {
                results.append("Check-in:       ").append(slot.getFormattedCheckInTime()).append("\n");
                results.append("Duration:       ").append(slot.getFormattedDuration()).append("\n");
            }
        } else {
            results.append("❌ Slot '").append(searchId).append("' not found!\n\n");
            results.append("Available slots:\n");
            for (ParkingSlot s : slotsMap.values()) {
                if (s.isAvailable()) {
                    results.append("  • ").append(s.getSlotId()).append("\n");
                }
            }
        }
        
        searchResultsArea.setText(results.toString());
    }
    
    // ==================== NAVIGATION METHODS ====================
    
    @FXML
    private void showDashboard() {
        setActiveNavButton(navDashboard);
        contentArea.getChildren().clear();
        contentArea.getChildren().add(dashboardView);
        setupSlotsGrid();
        updateWaitingQueueView();
    }
    
    @FXML
    private void showBookSlot() {
        setActiveNavButton(navBook);
        contentArea.getChildren().clear();
        contentArea.getChildren().add(bookSlotView);
        updateAvailableSlotsCombo();
    }
    
    @FXML
    private void showReleaseSlot() {
        setActiveNavButton(navRelease);
        contentArea.getChildren().clear();
        contentArea.getChildren().add(releaseSlotView);
        updateOccupiedSlotsCombo();
    }
    
    @FXML
    private void showSearchSlot() {
        setActiveNavButton(navSearch);
        contentArea.getChildren().clear();
        contentArea.getChildren().add(searchSlotView);
        searchSlotIdField.clear();
        searchResultsArea.clear();
    }
    
    /**
     * Set active navigation button styling
     */
    private void setActiveNavButton(Button activeBtn) {
        navDashboard.getStyleClass().removeAll("nav-item-active");
        navBook.getStyleClass().removeAll("nav-item-active");
        navRelease.getStyleClass().removeAll("nav-item-active");
        navSearch.getStyleClass().removeAll("nav-item-active");
        
        activeBtn.getStyleClass().add("nav-item-active");
    }
    
    // ==================== UTILITY METHODS ====================
    
    /**
     * Display a status message with color coding
     */
    private void showMessage(Label label, String message, String type) {
        label.setText(message);
        label.getStyleClass().clear();
        label.getStyleClass().add("status-message");
        label.getStyleClass().add("status-" + type);
    }
}
