package interfaces;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class Tenant implements TenantInterface{
    private Connection conn;
    private Scanner scanner;
    private int tenantId;

    public Tenant(Connection conn) {
        this.conn = conn;
        this.scanner = new Scanner(System.in);
        this.tenantId = promptForTenantId();
    }

    public int promptForTenantId() {
        try {
            PreparedStatement stmt = conn.prepareStatement("SELECT TenantID, TenantName FROM Tenants");
            ResultSet rs = stmt.executeQuery();

            // Print header
            System.out.println("Tenant ID\tTenant Name");
            System.out.println("----------\t-----------");

            // Print rows
            while (rs.next()) {
                int id = rs.getInt("TenantID");
                String name = rs.getString("TenantName");
                System.out.println(id + "\t\t" + name);
            }

            System.out.print("Enter Tenant ID to select: ");
            return scanner.nextInt();
        } catch (SQLException e) {
            System.out.println("Error fetching tenants: " + e.getMessage());
            return -1; // Indicates an error
        }
    }

    public void showMenu() {
        while (true) {
            // Check if tenantId is valid
            if (tenantId == -1) {
                System.out.println("Invalid Tenant ID. Exiting Tenant Interface.");
                return;
            }
            System.out.println("Tenant Interface");
            System.out.println("1: Check Payment Status");
            System.out.println("2: Make a Rental Payment");
            System.out.println("3: Update Personal Data");
            System.out.println("4: Exit");

            System.out.print("Select an option: ");
            int option = scanner.nextInt();
            scanner.nextLine(); // consume the rest of the line

            switch (option) {
                case 1:
                    checkPaymentStatus();
                    break;
                case 2:
                    makeRentalPayment();
                    break;
                case 3:
                    updatePersonalData();
                    break;
                case 4:
                    System.out.println("Exiting Tenant Interface.");
                    return;
                default:
                    System.out.println("Invalid option selected. Please try again.");
                    break;
            }
        }
    }

    public void checkPaymentStatus() {
        // Logic to check payment status
        System.out.println("Checking payment status...");
        // Example: Query the database to find the amount due for this tenant
    }

    public void makeRentalPayment() {
        // Logic to make a rental payment
        System.out.println("Making a rental payment...");
        // Example: Insert a payment record into the database
    }

    public void updatePersonalData() {
        // Logic to update personal data
        System.out.println("Updating personal data...");
        // Example: Update tenant's personal information in the database
    }

    // Additional methods to interact with the database...
}