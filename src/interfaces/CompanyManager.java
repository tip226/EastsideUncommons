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

        System.out.print("Enter common apartment size (square feet): ");
        int size = scanner.nextInt();

        System.out.print("Enter number of bedrooms: ");
        int bedrooms;
        while (true) {
            if (scanner.hasNextInt()) {
                bedrooms = scanner.nextInt();
                if (bedrooms >= 0) break;
                else System.out.print("Invalid input. Please enter a non-negative integer: ");
            } else {
                System.out.print("Invalid input. Please enter an integer: ");
                scanner.next(); // clear the invalid input
            }
        }

        System.out.print("Enter number of bathrooms: ");
        double bathrooms;
        while (true) {
            if (scanner.hasNextDouble()) {
                bathrooms = scanner.nextDouble();
                if (bathrooms >= 0) break;
                else System.out.print("Invalid input. Please enter a non-negative number: ");
            } else {
                System.out.print("Invalid input. Please enter a number: ");
                scanner.next(); // clear the invalid input
            }
        }

        System.out.print("Enter monthly rent: $");
        int monthlyRent = scanner.nextInt();

        System.out.print("Enter security deposit: $");
        int securityDeposit = scanner.nextInt();

        // Insert apartments into the database
        String sql = "INSERT INTO Apartments (AptSize, Bedrooms, Bathrooms, MonthlyRent, SecurityDeposit, PropertyID_Ref) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            conn.setAutoCommit(false); // Use transaction to ensure all inserts are successful

            for (int i = 0; i < numberOfApartments; i++) {
                stmt.setInt(1, size);
                stmt.setInt(2, bedrooms);
                stmt.setDouble(3, bathrooms);
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
        // Ask the user if they want to manage amenities
        System.out.print("Do you want to manage amenities for these apartments? (y/n): ");
        String manageAmenities = scanner.nextLine();
        while (!manageAmenities.equalsIgnoreCase("y") && !manageAmenities.equalsIgnoreCase("n")) {
            System.out.println("Invalid input. Please enter 'y' or 'n': ");
            manageAmenities = scanner.nextLine();
        }

        if (manageAmenities.equalsIgnoreCase("y")) {
            manageAmenitiesForApartments(propertyId);
        }
    }

    public void manageAmenitiesForApartments(int propertyId) {
        while (true) {
            System.out.println("Amenity Management");
            System.out.println("1: Add New Common Amenity to Property");
            System.out.println("2: Add New Private Amenity to an Apartment");
            System.out.println("3: Assign Common Amenity to Property");
            System.out.println("4: Assign Private Amenity to an Apartment");
            System.out.println("5: Exit Amenity Management");

            System.out.print("Select an option: ");
            int option = scanner.nextInt();
            scanner.nextLine(); // consume the rest of the line

            switch (option) {
                case 1:
                    addNewCommonAmenity();
                    break;
                case 2:
                    addNewPrivateAmenity();
                    break;
                case 3:
                    assignCommonAmenityToProperty(propertyId);
                    break;
                case 4:
                    assignPrivateAmenityToApartment();
                    break;
                case 5:
                    return;
                default:
                    System.out.println("Invalid option selected. Please try again.");
                    break;
            }
        }
    }

    private void assignCommonAmenityToProperty(int propertyId) {
        try {
            // Display available common amenities
            DBTablePrinter.printTable(conn, "CommonAmenities");

            System.out.print("Enter the Amenity ID to assign to this property: ");
            int amenityId = scanner.nextInt();
            scanner.nextLine(); // consume the rest of the line

            // Insert into Property_CommonAmenities table
            String sql = "INSERT INTO Property_CommonAmenities (PropertyID, AmenityID) VALUES (?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, propertyId);
                stmt.setInt(2, amenityId);

                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Amenity assigned successfully to the property.");
                } else {
                    System.out.println("Failed to assign the amenity.");
                }
            }
        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
        }
    }

    private void assignPrivateAmenityToApartment() {
        try {
            // Display available private amenities
            DBTablePrinter.printTable(conn, "PrivateAmenities");

            System.out.print("Enter the Private Amenity ID to assign: ");
            int privateAmenityId = scanner.nextInt();

            // Display apartments
            DBTablePrinter.printTable(conn, "Apartments");

            System.out.print("Enter the Apartment Number to assign this amenity: ");
            int apartmentNumber = scanner.nextInt();
            scanner.nextLine(); // consume the rest of the line

            // Insert into Apartment_PrivateAmenities table
            String sql = "INSERT INTO Apartment_PrivateAmenities (AptNumber, PrivateAmenityID) VALUES (?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, apartmentNumber);
                stmt.setInt(2, privateAmenityId);

                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Amenity assigned successfully to the apartment.");
                } else {
                    System.out.println("Failed to assign the amenity.");
                }
            }
        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
        }
    }

    public void addNewCommonAmenity() {
        System.out.print("Enter amenity name: ");
        String amenityName = scanner.nextLine();
        if (amenityName.isEmpty()) {
            System.out.println("Amenity name cannot be empty.");
            return;
        }

        String isAvailableStr;
        System.out.print("Is the amenity available (true/false)? ");
        while (true) {
            if (scanner.hasNextBoolean()) {
                boolean isAvailable = scanner.nextBoolean();
                isAvailableStr = isAvailable ? "Y" : "N";
                break;
            } else {
                System.out.println("Invalid input. Please enter 'true' or 'false': ");
                scanner.next(); // clear the invalid input
            }
        }
        
        System.out.print("Enter cost: $");
        double cost;
        while (true) {
            if (scanner.hasNextDouble()) {
                cost = scanner.nextDouble();
                if (cost >= 0) break;
                else System.out.print("Invalid input. Please enter a non-negative number: ");
            } else {
                System.out.print("Invalid input. Please enter a number: ");
                scanner.next(); // clear the invalid input
            }
        }

        scanner.nextLine(); // Consume the rest of the line

        System.out.print("Enter cost type (One-time, Monthly, Included): ");
        String costType = scanner.nextLine();
        if (!costType.equals("One-time") && !costType.equals("Monthly") && !costType.equals("Included")) {
            System.out.println("Invalid cost type. Must be 'One-time', 'Monthly', or 'Included'.");
            return;
        }

        // Insert data into the database
        String sql = "INSERT INTO CommonAmenities (AmenityName, IsAvailable, Cost, CostType) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, amenityName);
            stmt.setString(2, isAvailableStr);
            stmt.setDouble(3, cost);
            stmt.setString(4, costType);
            stmt.executeUpdate();
            System.out.println("Amenity added successfully.");
        } catch (SQLException e) {
            System.out.println("Error adding amenity: " + e.getMessage());
        }
    }

    public void addNewPrivateAmenity() {
        System.out.print("Enter amenity name: ");
        String amenityName = scanner.nextLine();

        System.out.print("Is the amenity available (true/false)? ");
        boolean isAvailable = scanner.nextBoolean();

        System.out.print("Enter cost: ");
        double cost = scanner.nextDouble();

        scanner.nextLine(); // Consume the rest of the line

        System.out.print("Enter cost type (One-time, Monthly, Included): ");
        String costType = scanner.nextLine();

        // Convert isAvailable to 'Y' or 'N'
        String isAvailableStr = isAvailable ? "Y" : "N";

        // Validate costType
        if (!costType.equals("One-time") && !costType.equals("Monthly") && !costType.equals("Included")) {
            System.out.println("Invalid cost type. Must be 'One-time', 'Monthly', or 'Included'.");
            return;
        }

        // Insert data into the database
        String sql = "INSERT INTO PrivateAmenities (AmenityName, IsAvailable, Cost, CostType) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, amenityName);
            stmt.setString(2, isAvailableStr);
            stmt.setDouble(3, cost);
            stmt.setString(4, costType);
            stmt.executeUpdate();
            System.out.println("Amenity added successfully.");
        } catch (SQLException e) {
            System.out.println("Error adding amenity: " + e.getMessage());
        }
    }

}