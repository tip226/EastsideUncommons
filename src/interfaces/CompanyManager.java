package interfaces;

import db.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;

public class CompanyManager implements CompanyManagerInterface {
    private Connection conn;
    private Scanner scanner;

    public CompanyManager(Connection conn) {
        this.conn = conn;
        this.scanner = new Scanner(System.in);
    }

    public void showMenu() {
        while (true) {
            System.out.println("Company Manager Interface");
            System.out.println("1: Add New Property");
            System.out.println("2: Automatically Generate Apartments for a Property");
            System.out.println("3: Exit");

            System.out.print("Select an option: ");
            int option = scanner.nextInt();
            scanner.nextLine(); // consume the rest of the line

            switch (option) {
                case 1:
                    addNewProperty();
                    break;
                case 2:
                    generateApartmentsForProperty();
                    break;
                case 3:
                    System.out.println("Exiting Company Manager Interface.");
                    return;
                default:
                    System.out.println("Invalid option selected. Please try again.");
                    break;
            }
        }
    }

    public void addNewProperty() {
        // Prompt user for property details
        System.out.println("Enter property details...");
        System.out.print("Street: ");
        String street = scanner.nextLine();
        System.out.print("City: ");
        String city = scanner.nextLine();
        System.out.print("State: ");
        String state = scanner.nextLine();
        System.out.print("ZIP Code: ");
        String zipCode = scanner.nextLine();

        // Insert property data into database
        String sql = "INSERT INTO Property (Street, City, State, ZIPCode) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, street);
            stmt.setString(2, city);
            stmt.setString(3, state);
            stmt.setString(4, zipCode);
            stmt.executeUpdate();
            System.out.println("Property added successfully.");
        } catch (SQLException e) {
            System.out.println("Error adding property: " + e.getMessage());
        }
    }

    public void generateApartmentsForProperty() {
        // TableViewer tableViewer = new TableViewer();
        // tableViewer.displayTable(conn, "Property");
        DBTablePrinter.printTable(conn, "Property");
        
        System.out.print("Enter the Property ID for which to generate apartments: ");
        int propertyId = scanner.nextInt();

        System.out.print("Enter the number of apartments to generate: ");
        int numberOfApartments = scanner.nextInt();

        System.out.print("Enter common apartment size: ");
        int size = scanner.nextInt();

        System.out.print("Enter number of bedrooms: ");
        int bedrooms = scanner.nextInt();

        System.out.print("Enter number of bathrooms: ");
        int bathrooms = scanner.nextInt();

        System.out.print("Enter monthly rent: ");
        int monthlyRent = scanner.nextInt();

        System.out.print("Enter security deposit: ");
        int securityDeposit = scanner.nextInt();

        // Insert apartments into the database
        String sql = "INSERT INTO Apartments (AptSize, Bedrooms, Bathrooms, MonthlyRent, SecurityDeposit, PropertyID_Ref) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            conn.setAutoCommit(false); // Use transaction to ensure all inserts are successful

            for (int i = 0; i < numberOfApartments; i++) {
                stmt.setInt(1, size);
                stmt.setInt(2, bedrooms);
                stmt.setInt(3, bathrooms);
                stmt.setInt(4, monthlyRent);
                stmt.setInt(5, securityDeposit);
                stmt.setInt(6, propertyId);
                stmt.addBatch();
            }

            stmt.executeBatch();
            conn.commit();
            System.out.println(numberOfApartments + " apartments added successfully for Property ID " + propertyId);
        } catch (SQLException e) {
            System.out.println("Error generating apartments: " + e.getMessage());
            try {
                conn.rollback();
            } catch (SQLException ex) {
                System.out.println("Error on transaction rollback: " + ex.getMessage());
            }
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                System.out.println("Error resetting auto-commit: " + e.getMessage());
            }
        }
    }

}