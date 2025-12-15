#include <iostream>
#include <string>
#include <vector>
#include <limits>
#include <ctime>

using namespace std;

struct ParkingTicket {
    string tenantName;
    string vehiclePlate;
    int spotNumber;
    time_t checkInTime;

    string getCheckInTimeString() const {
        char buffer[80];
        tm* ltm = localtime(&checkInTime);
        strftime(buffer, sizeof(buffer), "%Y-%m-%d %H:%M:%S", ltm); 
        return string(buffer);
    }
};

// Global Configuration
const int TOTAL_SPOTS = 10; 

// Input Handling Functions

void handle_invalid_input() { 
    cout << "Error: Invalid input. Please enter a valid number.\n";
    cin.clear(); 
    cin.ignore(numeric_limits<streamsize>::max(), '\n'); 
}

void clear_input_buffer() {
    cin.ignore(numeric_limits<streamsize>::max(), '\n');
}

// Parking Lot Operations
int find_available_spot(const vector<ParkingTicket>& parkedVehicle) {
    for (int i = 1; i <= TOTAL_SPOTS; ++i) {
        bool isOccupied = false;
        // Traditional index loop
        for (size_t j = 0; j < parkedVehicle.size(); ++j) {
            if (parkedVehicle[j].spotNumber == i) {
                isOccupied = true;
                break;
            }
        }
        if (!isOccupied) {
            return i; // Found the first available spot
        }
    }
    return -1; // Lot if full
}

void check_in_car(vector<ParkingTicket>& parkedVehicle) {
    if (parkedVehicle.size() >= TOTAL_SPOTS) {
        cout << "Parking lot is full! Cannot check in a new vehicle.\n";
        return;
    }

    string name, plate;
    int spot = find_available_spot(parkedVehicle);
    if (spot == -1) {
         cout << "Parking lot is full! Spot finding failed.\n";
         return;
    }
    
    cout << "Enter Tenant Name: ";
    if (!getline(cin, name) || name.empty()) {
        cout << " Tenant name cannot be empty.\n";
        return;
    }
    
    cout << "Enter Vehicle Plate: ";
    if (!getline(cin, plate) || plate.empty()) {
        cout << "Vehicle plate cannot be empty.\n";
        return;
    }

    for (size_t i = 0; i < parkedVehicle.size(); ++i) {
        if (parkedVehicle[i].vehiclePlate == plate) {
            cout << "Vehicle with plate " << plate << " is already parked.\n";
            return;
        }
    }

    ParkingTicket newTicket = {name, plate, spot, time(0)};
    parkedVehicle.push_back(newTicket);
    
    cout << "\n Check-In Successful!\n";
    cout << "Tenant: " << name << ", Plate: " << plate << "\n";
    cout << "Assigned Spot: #" << spot << "\n";
    cout << "Time: " << newTicket.getCheckInTimeString() << "\n";
}

void check_out_car(vector<ParkingTicket>& parkedVehicle) {
    if (parkedVehicle.empty()) {
        cout << "No vehicles currently parked.\n";
        return;
    }

    int spotNumber;
    cout << "Enter the Spot Number to check out (1-" << TOTAL_SPOTS << "): ";
    
    if (!(cin >> spotNumber)) {
        handle_invalid_input();
        return;
    }
    clear_input_buffer(); 

    int index_to_remove = -1;
    for (size_t i = 0; i < parkedVehicle.size(); ++i) {
        if (parkedVehicle[i].spotNumber == spotNumber) {
            index_to_remove = (int)i;
            break;
        }
    }

    if (index_to_remove != -1) {
        const ParkingTicket& ticket = parkedVehicle[index_to_remove];
        time_t checkOutTime = time(0);
        double durationSeconds = difftime(checkOutTime, ticket.checkInTime);
        
        int hours = static_cast<int>(durationSeconds / 3600);
        int minutes = static_cast<int>((durationSeconds - hours * 3600) / 60);

        cout << "\n--- Checkout Report for Spot #" << ticket.spotNumber << " ---\n";
        cout << "Tenant: " << ticket.tenantName << "\n";
        cout << "Plate: " << ticket.vehiclePlate << "\n";
        cout << "Check-in Time: " << ticket.getCheckInTimeString() << "\n";
        cout << "Parking Duration: " << hours << "h " << minutes << "m\n";
        cout << "------------------------------------------\n";

        // Remove the ticket
        parkedVehicle.erase(parkedVehicle.begin() + index_to_remove);
        cout << "Vehicle checked out successfully.\n";

    } else {
        cout << "Error: Spot #" << spotNumber << " is currently vacant or does not exist.\n";
    }
}

void view_parking_status(const vector<ParkingTicket>& parkedVehicle) {
    cout << "\n--- SPOT Parking Lot Status ---\n";
    cout << "Total Capacity: " << TOTAL_SPOTS << "\n";
    cout << "Occupied Spots: " << parkedVehicle.size() << "\n";
    cout << "Available Spots: " << (TOTAL_SPOTS - parkedVehicle.size()) << "\n";

    if (parkedVehicle.empty()) {
        cout << "\nThe parking lot is currently empty.\n";
    } else {
        cout << "\nParked Vehicle Details:\n";
        // Traditional index for viewing
        for (size_t i = 0; i < parkedVehicle.size(); ++i) {
            const ParkingTicket& ticket = parkedVehicle[i];
            cout << "  Spot #" << ticket.spotNumber 
                 << " | Tenant: " << ticket.tenantName 
                 << " | Plate: " << ticket.vehiclePlate 
                 << " (In: " << ticket.getCheckInTimeString() << ")\n";
        }
    }
    cout << "----------------------------------\n";
}


// Main Program

int main() {
    vector<ParkingTicket> parkedVehicle; 
    int choice;

    cout << "SPOT (Smart Parking Optimization for Tenants) System Initialized.\n";
    cout << "Total Parking Spots: " << TOTAL_SPOTS << ".\n";

    do {
        cout << "\n==============================\n";
        cout << "   SPOT MANAGEMENT SYSTEM   \n";
        cout << "==============================\n";
        cout << "1. Check In Vehicle (Park)\n";
        cout << "2. Check Out Vehicle (Leave)\n";
        cout << "3. View Parking Status\n";
        cout << "4. Exit System\n";
        cout << "Enter your choice (1-4): ";
        
        if (!(cin >> choice)) {
            handle_invalid_input();
            continue; 
        }
        
        clear_input_buffer(); 

        if (choice == 1) {
            check_in_car(parkedVehicle);
        }
        else if (choice == 2) {
            check_out_car(parkedVehicle);
        }
        else if (choice == 3) {
            view_parking_status(parkedVehicle);
        }
        else if (choice == 4) {
            cout << "Exiting SPOT System. Goodbye!\n";
        }
        else {
            cout << "Invalid choice. Please enter a number between 1 and 4.\n";
        }

    } while (choice != 4);

    return 0;
}
