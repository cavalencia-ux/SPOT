import javafx.beans.property.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * ParkingSlot Model Class
 * Represents a single parking slot with status, tenant information, and check-in time
 * Uses JavaFX properties for automatic UI updates via bindings
 */
public class ParkingSlot {
    
    // Properties for JavaFX binding
    private StringProperty slotId;
    private StringProperty status; // "Available" or "Occupied"
    private StringProperty tenantName;
    private StringProperty vehiclePlate;
    private ObjectProperty<LocalDateTime> checkInTime;
    
    // Constructors
    /**
     * Create an available parking slot
     */
    public ParkingSlot(String slotId) {
        this.slotId = new SimpleStringProperty(slotId);
        this.status = new SimpleStringProperty("Available");
        this.tenantName = new SimpleStringProperty("");
        this.vehiclePlate = new SimpleStringProperty("");
        this.checkInTime = new SimpleObjectProperty<>(null);
    }
    
    /**
     * Create an occupied parking slot
     */
    public ParkingSlot(String slotId, String tenantName, String vehiclePlate, LocalDateTime checkInTime) {
        this.slotId = new SimpleStringProperty(slotId);
        this.status = new SimpleStringProperty("Occupied");
        this.tenantName = new SimpleStringProperty(tenantName);
        this.vehiclePlate = new SimpleStringProperty(vehiclePlate);
        this.checkInTime = new SimpleObjectProperty<>(checkInTime);
    }
    
    // Getters and Setters with Properties
    public String getSlotId() {
        return slotId.get();
    }
    
    public void setSlotId(String id) {
        slotId.set(id);
    }
    
    public StringProperty slotIdProperty() {
        return slotId;
    }
    
    public String getStatus() {
        return status.get();
    }
    
    public void setStatus(String status) {
        this.status.set(status);
    }
    
    public StringProperty statusProperty() {
        return status;
    }
    
    public String getTenantName() {
        return tenantName.get();
    }
    
    public void setTenantName(String name) {
        tenantName.set(name);
    }
    
    public StringProperty tenantNameProperty() {
        return tenantName;
    }
    
    public String getVehiclePlate() {
        return vehiclePlate.get();
    }
    
    public void setVehiclePlate(String plate) {
        vehiclePlate.set(plate);
    }
    
    public StringProperty vehiclePlateProperty() {
        return vehiclePlate;
    }
    
    public LocalDateTime getCheckInTime() {
        return checkInTime.get();
    }
    
    public void setCheckInTime(LocalDateTime time) {
        checkInTime.set(time);
    }
    
    public ObjectProperty<LocalDateTime> checkInTimeProperty() {
        return checkInTime;
    }
    
    // Utility Methods
    /**
     * Check if the slot is available
     */
    public boolean isAvailable() {
        return "Available".equals(getStatus());
    }
    
    /**
     * Check if the slot is occupied
     */
    public boolean isOccupied() {
        return "Occupied".equals(getStatus());
    }
    
    /**
     * Get formatted check-in time
     */
    public String getFormattedCheckInTime() {
        if (checkInTime.get() == null) {
            return "N/A";
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return checkInTime.get().format(formatter);
    }
    
    /**
     * Calculate parking duration in minutes
     */
    public long getParkingDurationMinutes() {
        if (checkInTime.get() == null) {
            return 0;
        }
        LocalDateTime now = LocalDateTime.now();
        return java.time.temporal.ChronoUnit.MINUTES.between(checkInTime.get(), now);
    }
    
    /**
     * Get parking duration as formatted string
     */
    public String getFormattedDuration() {
        long minutes = getParkingDurationMinutes();
        if (minutes <= 0) {
            return "0 min";
        }
        long hours = minutes / 60;
        long mins = minutes % 60;
        if (hours > 0) {
            return hours + "h " + mins + "m";
        }
        return mins + " min";
    }
    
    /**
     * Occupy the slot with tenant information
     */
    public void occupy(String tenantName, String vehiclePlate) {
        this.tenantName.set(tenantName);
        this.vehiclePlate.set(vehiclePlate);
        this.checkInTime.set(LocalDateTime.now());
        this.status.set("Occupied");
    }
    
    /**
     * Release the slot (make it available)
     */
    public void release() {
        this.tenantName.set("");
        this.vehiclePlate.set("");
        this.checkInTime.set(null);
        this.status.set("Available");
    }
    
    @Override
    public String toString() {
        return String.format("Slot %s [%s] - %s (%s)", 
            slotId.get(), status.get(), tenantName.get(), vehiclePlate.get());
    }
}
