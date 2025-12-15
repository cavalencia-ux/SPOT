import java.util.ArrayList;
import java.util.Scanner;

public class SPOTSystem {
    private ParkingLot parkingLot;
    private Scanner scanner;
    
    public SPOTSystem() {
        this.parkingLot = new ParkingLot();
        this.scanner = new Scanner(System.in);
    }
    
    /**
     * Main menu display
     */
    private void displayMainMenu() {
        System.out.println("\n==============================");
        System.out.println("   SPOT MANAGEMENT SYSTEM   ");
        System.out.println("==============================");
        System.out.println("1. Check In Vehicle (Park)");
        System.out.println("2. Check Out Vehicle (Leave)");
        System.out.println("3. View Parking Status");
        System.out.println("4. Search Vehicle");
        System.out.println("5. View Waiting List");
        System.out.println("6. Generate Report");
        System.out.println("7. Exit System");
        System.out.println("==============================");
        System.out.print("Enter your choice (1-7): ");
    }
    
    /**
     * Check in a vehicle
     */
    private void checkInVehicle() {
        System.out.println("\n--- Check In Vehicle ---");
        
        if (parkingLot.isLotFull()) {
            System.out.println("Parking lot is full! Vehicle will be added to waiting list.");
        }
        
        System.out.print("Enter Tenant Name: ");
        String tenantName = scanner.nextLine().trim();
        
        if (tenantName.isEmpty()) {
            System.out.println("Error: Tenant name cannot be empty.");
            return;
        }
        
        System.out.print("Enter Vehicle Plate: ");
        String vehiclePlate = scanner.nextLine().trim().toUpperCase();
        
        if (vehiclePlate.isEmpty()) {
            System.out.println("Error: Vehicle plate cannot be empty.");
            return;
        }
        
        // Check if vehicle already parked
        if (parkingLot.searchVehicle(vehiclePlate) != null) {
            System.out.println("Error: Vehicle with plate " + vehiclePlate + " is already parked.");
            return;
        }
        
        // Attempt check-in
        boolean success = parkingLot.checkInVehicle(tenantName, vehiclePlate);
        
        if (success) {
            ParkingTicket ticket = parkingLot.searchVehicle(vehiclePlate);
            System.out.println("\n✓ Check-In Successful!");
            System.out.println("Tenant: " + tenantName);
            System.out.println("Plate: " + vehiclePlate);
            System.out.println("Assigned Spot: #" + ticket.getSpotNumber());
            System.out.println("Check-in Time: " + ticket.getCheckInTimeString());
        } else {
            System.out.println("\n✗ Check-In Failed!");
            System.out.println("Tenant " + tenantName + " added to waiting list.");
            System.out.println("Current waiting list position: " + parkingLot.getWaitingListSize());
        }
    }
    
    /**
     * Check out a vehicle
     */
    private void checkOutVehicle() {
        System.out.println("\n--- Check Out Vehicle ---");
        
        if (parkingLot.getOccupiedSpotsCount() == 0) {
            System.out.println("No vehicles currently parked.");
            return;
        }
        
        System.out.print("Enter Spot Number (1-" + parkingLot.getTotalSpots() + "): ");
        
        try {
            int spotNumber = Integer.parseInt(scanner.nextLine().trim());
            
            if (spotNumber < 1 || spotNumber > parkingLot.getTotalSpots()) {
                System.out.println("Error: Invalid spot number.");
                return;
            }
            
            ParkingTicket ticket = parkingLot.checkOutVehicle(spotNumber);
            
            if (ticket == null) {
                System.out.println("Error: Spot #" + spotNumber + " is currently vacant or does not exist.");
                return;
            }
            
            long durationMinutes = ticket.getParkingDurationMinutes();
            int hours = (int) (durationMinutes / 60);
            int minutes = (int) (durationMinutes % 60);
            
            System.out.println("\n--- Checkout Report for Spot #" + spotNumber + " ---");
            System.out.println("Tenant: " + ticket.getTenantName());
            System.out.println("Plate: " + ticket.getVehiclePlate());
            System.out.println("Check-in Time: " + ticket.getCheckInTimeString());
            System.out.println("Check-out Time: " + ticket.getCheckOutTimeString());
            System.out.println("Parking Duration: " + hours + "h " + minutes + "m");
            System.out.println("------------------------------------------");
            System.out.println("✓ Vehicle checked out successfully.");
            
            // Check waiting list
            if (parkingLot.getWaitingListSize() > 0) {
                System.out.println("\nNote: There are " + parkingLot.getWaitingListSize() + " vehicles waiting.");
            }
            
        } catch (NumberFormatException e) {
            System.out.println("Error: Invalid input. Please enter a valid number.");
        }
    }
    
    /**
     * View parking lot status
     */
    private void viewParkingStatus() {
        System.out.println("\n--- SPOT Parking Lot Status ---");
        System.out.println("Total Capacity: " + parkingLot.getTotalSpots());
        System.out.println("Occupied Spots: " + parkingLot.getOccupiedSpotsCount());
        System.out.println("Available Spots: " + parkingLot.getAvailableSpotsCount());
        
        ArrayList<ParkingTicket> vehicles = parkingLot.getAllParkedVehicles();
        
        if (vehicles.isEmpty()) {
            System.out.println("\nThe parking lot is currently empty.");
        } else {
            System.out.println("\nParked Vehicle Details:");
            System.out.println("-----------------------------------");
            for (ParkingTicket ticket : vehicles) {
                System.out.println(ticket);
            }
        }
        System.out.println("----------------------------------");
    }
    
    /**
     * Search for a vehicle
     */
    private void searchVehicle() {
        System.out.println("\n--- Search Vehicle ---");
        System.out.print("Enter Vehicle Plate: ");
        String vehiclePlate = scanner.nextLine().trim().toUpperCase();
        
        if (vehiclePlate.isEmpty()) {
            System.out.println("Error: Vehicle plate cannot be empty.");
            return;
        }
        
        ParkingTicket ticket = parkingLot.searchVehicle(vehiclePlate);
        
        if (ticket == null) {
            System.out.println("Vehicle not found in parking lot.");
            return;
        }
        
        System.out.println("\n--- Search Results ---");
        System.out.println("Tenant: " + ticket.getTenantName());
        System.out.println("Plate: " + ticket.getVehiclePlate());
        System.out.println("Spot: #" + ticket.getSpotNumber());
        System.out.println("Check-in Time: " + ticket.getCheckInTimeString());
        System.out.println("-------------------");
    }
    
    /**
     * View waiting list
     */
    private void viewWaitingList() {
        System.out.println("\n--- Waiting List ---");
        
        if (parkingLot.getWaitingListSize() == 0) {
            System.out.println("No vehicles waiting.");
            return;
        }
        
        ArrayList<String> waiting = parkingLot.getWaitingList();
        System.out.println("Total waiting: " + waiting.size());
        System.out.println("\nWaiting Vehicles:");
        for (int i = 0; i < waiting.size(); i++) {
            System.out.println((i + 1) + ". " + waiting.get(i));
        }
        System.out.println("-------------------");
    }
    
    /**
     * Generate parking report
     */
    private void generateReport() {
        System.out.println("\n--- SPOT System Report ---");
        System.out.println("Total Spots: " + parkingLot.getTotalSpots());
        System.out.println("Occupied Spots: " + parkingLot.getOccupiedSpotsCount());
        System.out.println("Available Spots: " + parkingLot.getAvailableSpotsCount());
        System.out.println("Occupancy Rate: " + String.format("%.2f%%", 
                (parkingLot.getOccupiedSpotsCount() * 100.0 / parkingLot.getTotalSpots())));
        System.out.println("Vehicles in Waiting List: " + parkingLot.getWaitingListSize());
        
        ArrayList<ParkingTicket> vehicles = parkingLot.getAllParkedVehicles();
        if (!vehicles.isEmpty()) {
            System.out.println("\nParked Vehicles List:");
            for (ParkingTicket ticket : vehicles) {
                System.out.println("  - Spot #" + ticket.getSpotNumber() + ": " + 
                                 ticket.getTenantName() + " (" + ticket.getVehiclePlate() + ")");
            }
        }
        System.out.println("------------------------");
    }
    
    /**
     * Run the main application loop
     */
    public void run() {
        System.out.println("================================================");
        System.out.println("SPOT (Smart Parking Optimization for Tenants)");
        System.out.println("================================================");
        System.out.println("System Initialized.");
        System.out.println("Total Parking Spots: " + parkingLot.getTotalSpots());
        System.out.println("================================================");
        
        int choice;
        
        do {
            displayMainMenu();
            
            try {
                choice = Integer.parseInt(scanner.nextLine().trim());
                
                switch (choice) {
                    case 1:
                        checkInVehicle();
                        break;
                    case 2:
                        checkOutVehicle();
                        break;
                    case 3:
                        viewParkingStatus();
                        break;
                    case 4:
                        searchVehicle();
                        break;
                    case 5:
                        viewWaitingList();
                        break;
                    case 6:
                        generateReport();
                        break;
                    case 7:
                        System.out.println("\nExiting SPOT System. Goodbye!");
                        break;
                    default:
                        System.out.println("Invalid choice. Please enter a number between 1 and 7.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Error: Invalid input. Please enter a valid number.");
                choice = -1;
            }
        } while (choice != 7);
        
        scanner.close();
    }
    
    public static void main(String[] args) {
        SPOTSystem system = new SPOTSystem();
        system.run();
    }
}
