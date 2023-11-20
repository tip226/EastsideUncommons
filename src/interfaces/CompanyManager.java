package interfaces;

import db.TablePopulator;

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
        System.out.println("Company Manager Interface");
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
        // Logic to automatically generate apartments for a property
        System.out.println("Generating apartments for a property...");
        // This method will involve more complex logic to generate apartment data
        // based on user input and insert it into the database.
    }

}