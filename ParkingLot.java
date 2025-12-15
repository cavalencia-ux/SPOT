import java.util.*;

public class ParkingLot {
    private static final int TOTAL_SPOTS = 10;
    
    // HashMap for quick lookup of parking assignments
    private HashMap<Integer, ParkingTicket> parkedVehicles; // spotNumber -> ParkingTicket
    private HashMap<String, Integer> vehicleToSpot; // vehiclePlate -> spotNumber
    
    // Queue for waiting list when lot is full
    private Queue<String> waitingList;
    
    // ArrayList for sorted display
    private ArrayList<Integer> spotNumbers;
    
    public ParkingLot() {
        this.parkedVehicles = new HashMap<>();
        this.vehicleToSpot = new HashMap<>();
        this.waitingList = new LinkedList<>();
        this.spotNumbers = new ArrayList<>();
        
        // Initialize spot numbers
        for (int i = 1; i <= TOTAL_SPOTS; i++) {
            spotNumbers.add(i);
        }
    }
    
    /**
     * Find the first available parking spot
     */
    public int findAvailableSpot() {
        for (Integer spotNum : spotNumbers) {
            if (!parkedVehicles.containsKey(spotNum)) {
                return spotNum;
            }
        }
        return -1; // No available spot
    }
    
    /**
     * Check if a vehicle is already parked
     */
    public boolean isVehicleParked(String vehiclePlate) {
        return vehicleToSpot.containsKey(vehiclePlate);
    }
    
    /**
     * Check in a vehicle (Insert operation)
     */
    public boolean checkInVehicle(String tenantName, String vehiclePlate) {
        // Check if vehicle already parked
        if (isVehicleParked(vehiclePlate)) {
            return false;
        }
        
        int availableSpot = findAvailableSpot();
        
        if (availableSpot == -1) {
            // Add to waiting queue
            waitingList.offer(tenantName + " (" + vehiclePlate + ")");
            return false;
        }
        
        ParkingTicket ticket = new ParkingTicket(tenantName, vehiclePlate, availableSpot);
        parkedVehicles.put(availableSpot, ticket);
        vehicleToSpot.put(vehiclePlate, availableSpot);
        
        return true;
    }
    
    /**
     * Check out a vehicle (Delete operation)
     */
    public ParkingTicket checkOutVehicle(int spotNumber) {
        if (!parkedVehicles.containsKey(spotNumber)) {
            return null; // Spot not occupied
        }
        
        ParkingTicket ticket = parkedVehicles.get(spotNumber);
        ticket.setCheckOutTime();
        ticket.setActive(false);
        
        // Remove from maps
        parkedVehicles.remove(spotNumber);
        vehicleToSpot.remove(ticket.getVehiclePlate());
        
        return ticket;
    }
    
    /**
     * Search for a vehicle (Search operation)
     */
    public ParkingTicket searchVehicle(String vehiclePlate) {
        if (!vehicleToSpot.containsKey(vehiclePlate)) {
            return null;
        }
        
        int spotNumber = vehicleToSpot.get(vehiclePlate);
        return parkedVehicles.get(spotNumber);
    }
    
    /**
     * Get parking ticket for a specific spot
     */
    public ParkingTicket getTicketBySpot(int spotNumber) {
        return parkedVehicles.get(spotNumber);
    }
    
    /**
     * Get all parked vehicles (Traverse operation)
     */
    public ArrayList<ParkingTicket> getAllParkedVehicles() {
        ArrayList<ParkingTicket> tickets = new ArrayList<>(parkedVehicles.values());
        // Sort by spot number
        tickets.sort((t1, t2) -> Integer.compare(t1.getSpotNumber(), t2.getSpotNumber()));
        return tickets;
    }
    
    /**
     * Get available spots count
     */
    public int getAvailableSpotsCount() {
        return TOTAL_SPOTS - parkedVehicles.size();
    }
    
    /**
     * Get occupied spots count
     */
    public int getOccupiedSpotsCount() {
        return parkedVehicles.size();
    }
    
    /**
     * Get total spots
     */
    public int getTotalSpots() {
        return TOTAL_SPOTS;
    }
    
    /**
     * Get waiting queue info
     */
    public int getWaitingListSize() {
        return waitingList.size();
    }
    
    /**
     * Get first person in waiting queue
     */
    public String getNextWaiting() {
        return waitingList.peek();
    }
    
    /**
     * Remove from waiting queue
     */
    public void processWaiting() {
        waitingList.poll();
    }
    
    /**
     * Check if lot is full
     */
    public boolean isLotFull() {
        return parkedVehicles.size() >= TOTAL_SPOTS;
    }
    
    /**
     * Get all waiting list entries
     */
    public ArrayList<String> getWaitingList() {
        return new ArrayList<>(waitingList);
    }
}
