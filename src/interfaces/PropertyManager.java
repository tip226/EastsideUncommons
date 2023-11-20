package interfaces;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;

public class PropertyManager implements PropertyManagerInterface{
    private Connection conn;
    private Scanner scanner;

    public PropertyManager(Connection conn) {
        this.conn = conn;
        this.scanner = new Scanner(System.in);
    }

    public void showMenu() {
        while (true) {
            System.out.println("Property Manager Interface");
            System.out.println("1: Record Visits Data");
            System.out.println("2: Record Lease Data");
            System.out.println("3: Record Move-Out");
            System.out.println("4: Add Person or Pet to Lease");
            System.out.println("5: Set Move-Out Date");
            System.out.println("6: Exit");

            System.out.print("Select an option: ");
            int option = scanner.nextInt();
            scanner.nextLine(); // consume the rest of the line

            switch (option) {
                case 1:
                    recordVisitsData();
                    break;
                case 2:
                    recordLeaseData();
                    break;
                case 3:
                    recordMoveOut();
                    break;
                case 4:
                    addPersonOrPetToLease();
                    break;
                case 5:
                    setMoveOutDate();
                    break;
                case 6:
                    System.out.println("Exiting Property Manager Interface.");
                    return;
                default:
                    System.out.println("Invalid option selected. Please try again.");
                    break;
            }
        }
    }

    public void recordVisitsData() {
        // Logic to record visits data
        System.out.println("Record Visits Data logic here.");
    }

    public void recordLeaseData() {
        // Logic to record lease data
        System.out.println("Record Lease Data logic here.");
    }

    public void recordMoveOut() {
        // Logic to record move-out
        System.out.println("Record Move-Out logic here.");
    }

    public void addPersonOrPetToLease() {
        // Logic to add a person or pet to a lease
        System.out.println("Add Person or Pet to Lease logic here.");
    }

    public void setMoveOutDate() {
        // Logic to set a move-out date
        System.out.println("Set Move-Out Date logic here.");
    }

    // Additional methods for database operations...
}
