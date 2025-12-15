import java.io.*;
import java.util.ArrayList;

/**
 * File I/O handler for persisting parking data
 */
public class FileHandler {
    private static final String DATA_FILE = "parking_data.dat";
    
    /**
     * Save parking lot data to file
     */
    public static boolean saveParkingData(ParkingLot parkingLot) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            // Save all parked vehicles
            ArrayList<ParkingTicket> vehicles = parkingLot.getAllParkedVehicles();
            oos.writeObject(vehicles);
            oos.writeInt(parkingLot.getTotalSpots());
            return true;
        } catch (IOException e) {
            System.err.println("Error saving parking data: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Load parking lot data from file
     */
    @SuppressWarnings("unchecked")
    public static ArrayList<ParkingTicket> loadParkingData() {
        File file = new File(DATA_FILE);
        
        if (!file.exists()) {
            return new ArrayList<>();
        }
        
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(DATA_FILE))) {
            ArrayList<ParkingTicket> vehicles = (ArrayList<ParkingTicket>) ois.readObject();
            return vehicles != null ? vehicles : new ArrayList<>();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading parking data: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    /**
     * Save transaction log
     */
    public static boolean logTransaction(ParkingTicket ticket, String action) {
        String logFile = "parking_transactions.log";
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFile, true))) {
            String logEntry = String.format("[%s] Action: %s | Tenant: %s | Plate: %s | Spot: %d | Check-in: %s%n",
                    java.time.LocalDateTime.now(),
                    action,
                    ticket.getTenantName(),
                    ticket.getVehiclePlate(),
                    ticket.getSpotNumber(),
                    ticket.getCheckInTimeString());
            
            writer.write(logEntry);
            return true;
        } catch (IOException e) {
            System.err.println("Error logging transaction: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Clear all data files
     */
    public static void clearDataFiles() {
        File dataFile = new File(DATA_FILE);
        if (dataFile.exists()) {
            dataFile.delete();
        }
    }
}
