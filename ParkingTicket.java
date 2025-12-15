import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class ParkingTicket {
    private String tenantName;
    private String vehiclePlate;
    private int spotNumber;
    private LocalDateTime checkInTime;
    private LocalDateTime checkOutTime;
    private boolean active;
    
    public ParkingTicket(String tenantName, String vehiclePlate, int spotNumber) {
        this.tenantName = tenantName;
        this.vehiclePlate = vehiclePlate;
        this.spotNumber = spotNumber;
        this.checkInTime = LocalDateTime.now();
        this.active = true;
    }
    
    public String getTenantName() {
        return tenantName;
    }
    
    public String getVehiclePlate() {
        return vehiclePlate;
    }
    
    public int getSpotNumber() {
        return spotNumber;
    }
    
    public LocalDateTime getCheckInTime() {
        return checkInTime;
    }
    
    public LocalDateTime getCheckOutTime() {
        return checkOutTime;
    }
    
    public void setCheckOutTime() {
        this.checkOutTime = LocalDateTime.now();
    }
    
    public boolean isActive() {
        return active;
    }
    
    public void setActive(boolean active) {
        this.active = active;
    }
    
    public String getCheckInTimeString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return checkInTime.format(formatter);
    }
    
    public String getCheckOutTimeString() {
        if (checkOutTime == null) {
            return "Not checked out";
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return checkOutTime.format(formatter);
    }
    
    public long getParkingDurationMinutes() {
        if (checkOutTime == null) {
            return ChronoUnit.MINUTES.between(checkInTime, LocalDateTime.now());
        }
        return ChronoUnit.MINUTES.between(checkInTime, checkOutTime);
    }
    
    public long getParkingDurationHours() {
        return getParkingDurationMinutes() / 60;
    }
    
    @Override
    public String toString() {
        return String.format("Spot #%d | Tenant: %s | Plate: %s | Check-in: %s",
                spotNumber, tenantName, vehiclePlate, getCheckInTimeString());
    }
}
