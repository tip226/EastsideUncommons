package interfaces;

import db.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
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
            System.out.println("1: Manage Properties");
            System.out.println("2: Manage Apartments");
            System.out.println("3: Manage Amenities");
            System.out.println("4: Exit");

            System.out.print("Select an option: ");
            int option = scanner.nextInt();
            scanner.nextLine(); // consume the rest of the line

            switch (option) {
                case 1:
                    manageProperty();
                    break;
                case 2:
                    manageApartments();
                    break;
                case 3:
                    manageAmenities();
                    break;
                case 4:
                    System.out.println("Exiting Company Manager Interface.");
                    return;
                default:
                    System.out.println("Invalid option selected. Please try again.");
                    break;
            }
        }
    }

    private void manageProperty() {
        System.out.println("Property Management");
        System.out.println("1: Add New Property");
        System.out.println("2: Edit Property");
        System.out.println("3: Remove Property");
        System.out.println("4: Back to Main Menu");

        int choice = scanner.nextInt();
        scanner.nextLine(); // consume the rest of the line

        switch (choice) {
            case 1:
                addNewProperty();
                break;
            case 2:
                editProperty();
                break;
            case 3:
                removeProperty();
                break;
            case 4:
                return;
            default:
                System.out.println("Invalid option selected. Please try again.");
        }
    }

    private void manageApartments() {
        System.out.println("Apartment Management");
        System.out.println("1: Automatically Generate Apartments for a Property");
        System.out.println("2: Edit Apartment");
        System.out.println("3: Remove Apartment");
        System.out.println("4: Back to Main Menu");

        int choice = scanner.nextInt();
        scanner.nextLine(); // consume the rest of the line

        switch (choice) {
            case 1:
                generateApartmentsForProperty();
                break;
            case 2:
                editApartment();
                break;
            case 3:
                removeApartment();
                break;
            case 4:
                return;
            default:
                System.out.println("Invalid option selected. Please try again.");
        }
    }

    private void manageAmenities() {
        System.out.println("Amenity Management");
        System.out.println("1: Add New Common Amenity");
        System.out.println("2: Edit Common Amenity");
        System.out.println("3: Remove Common Amenity");
        System.out.println("4: Add New Private Amenity");
        System.out.println("5: Edit Private Amenity");
        System.out.println("6: Remove Private Amenity");
        System.out.println("7: Back to Main Menu");

        int choice = scanner.nextInt();
        scanner.nextLine(); // consume the rest of the line

        switch (choice) {
            case 1:
                addNewCommonAmenity();
                break;
            case 2:
                editCommonAmenity();
                break;
            case 3:
                removeCommonAmenity();
                break;
            case 4:
                addNewPrivateAmenity();
                break;
            case 5:
                editPrivateAmenity();
                break;
            case 6:
                removePrivateAmenity();
                break;
            case 7:
                return;
            default:
                System.out.println("Invalid option selected. Please try again.");
        }
    }

    public void addNewProperty() {
        // Prompt user for property details
        System.out.println("Enter property details...");

        System.out.print("Street: ");
        String street = scanner.nextLine();
        while (!isValidStreet(street)) {
            System.out.println("Invalid street. Please try again.");
            street = scanner.nextLine();
        }

        System.out.print("City: ");
        String city = scanner.nextLine();
        while (!isValidCity(city)) {
            System.out.println("Invalid city. Please try again.");
            city = scanner.nextLine();
        }

        System.out.print("State (2 characters): ");
        String state = scanner.nextLine();
        while (!isValidState(state)) {
            System.out.println("Invalid state. Should be 2 characters long. Please try again.");
            state = scanner.nextLine();
        }

        System.out.print("ZIP Code: ");
        String zipCode = scanner.nextLine();
        while (!isValidZipCode(zipCode)) {
            System.out.println("Invalid ZIP Code. Should be 5 digits. Please try again.");
            zipCode = scanner.nextLine();
        }

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

    // Validation Methods

    private boolean isValidStreet(String street) {
        // Example: Check if the street is not empty
        return street != null && !street.trim().isEmpty();
    }

    private boolean isValidCity(String city) {
        // Example: Check if the city is not empty
        return city != null && !city.trim().isEmpty();
    }

    private boolean isValidState(String state) {
        // Example: Check if the state is 2 characters long
        return state != null && state.length() == 2 && state.matches("^[a-zA-Z]{2}$");
    }

    private boolean isValidZipCode(String zipCode) {
        // Example: U.S. ZIP code validation (5 digits or 5+4 format)
        return zipCode != null && zipCode.matches("^\\d{5}(-\\d{4})?$");
    }

    public void editProperty() {
        DBTablePrinter.printTable(conn, "Property");

        System.out.print("Enter the Property ID to edit: ");
        int propertyId = 0;
        boolean validPropertyId = false;
        while (!validPropertyId) {
            System.out.print("Enter the Property ID for which to generate apartments: ");
            while (!scanner.hasNextInt()) {
                System.out.println("Invalid input. Please enter a positive integer: ");
                scanner.next(); // clear the invalid input
            }
            propertyId = scanner.nextInt();
            scanner.nextLine(); // consume the rest of the line

            if (propertyId > 0 && checkPropertyExists(propertyId)) {
                validPropertyId = true;
            } else {
                System.out.println("No property found with the given ID. Please enter a valid Property ID.");
            }
        }

        System.out.print("New Street: ");
        String street = scanner.nextLine();
        System.out.print("New City: ");
        String city = scanner.nextLine();
        System.out.print("New State: ");
        String state = scanner.nextLine();
        System.out.print("New ZIP Code: ");
        String zipCode = scanner.nextLine();

        // Update property data in database
        String sql = "UPDATE Property SET Street = ?, City = ?, State = ?, ZIPCode = ? WHERE PropertyID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, street);
            stmt.setString(2, city);
            stmt.setString(3, state);
            stmt.setString(4, zipCode);
            stmt.setInt(5, propertyId);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Property updated successfully.");
            } else {
                System.out.println("No property found with the given ID.");
            }
        } catch (SQLException e) {
            System.out.println("Error updating property: " + e.getMessage());
        }
    }

    private void removeProperty() {
        DBTablePrinter.printTable(conn, "Property");

        System.out.print("Enter the Property ID to remove: ");
        int propertyId = 0;
        boolean validPropertyId = false;
        while (!validPropertyId) {
            while (!scanner.hasNextInt()) {
                System.out.println("Invalid input. Please enter a positive integer: ");
                scanner.next(); // clear the invalid input
            }
            propertyId = scanner.nextInt();
            scanner.nextLine(); // consume the rest of the line

            if (propertyId > 0 && checkPropertyExists(propertyId)) {
                validPropertyId = true;
            } else {
                System.out.println("No property found with the given ID. Please enter a valid Property ID.");
            }
        }

        try {
            // Start transaction
            conn.setAutoCommit(false);

            // Delete dependencies in Apartment_PrivateAmenities
            String deleteApartmentPrivateAmenitiesSql = "DELETE FROM Apartment_PrivateAmenities WHERE AptNumber IN (SELECT AptNumber FROM Apartments WHERE PropertyID_Ref = ?)";
            try (PreparedStatement stmt = conn.prepareStatement(deleteApartmentPrivateAmenitiesSql)) {
                stmt.setInt(1, propertyId);
                stmt.executeUpdate();
            }

            // Delete dependencies in Property_CommonAmenities
            String deletePropertyCommonAmenitiesSql = "DELETE FROM Property_CommonAmenities WHERE PropertyID = ?";
            try (PreparedStatement stmt = conn.prepareStatement(deletePropertyCommonAmenitiesSql)) {
                stmt.setInt(1, propertyId);
                stmt.executeUpdate();
            }

            // Delete dependencies in Apartments
            String deleteApartmentsSql = "DELETE FROM Apartments WHERE PropertyID_Ref = ?";
            try (PreparedStatement stmt = conn.prepareStatement(deleteApartmentsSql)) {
                stmt.setInt(1, propertyId);
                stmt.executeUpdate();
            }

            // Delete the property
            String deletePropertySql = "DELETE FROM Property WHERE PropertyID = ?";
            try (PreparedStatement stmt = conn.prepareStatement(deletePropertySql)) {
                stmt.setInt(1, propertyId);
                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Property removed successfully.");
                } else {
                    System.out.println("No property found with the given ID.");
                }
            }

            // Commit transaction
            conn.commit();
        } catch (SQLException e) {
            System.out.println("Error removing property: " + e.getMessage());
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

    public void editApartment() {
        DBTablePrinter.printTable(conn, "Apartments");

        System.out.print("Enter the Apartment Number to edit: ");
        int aptNumber;
        while (!scanner.hasNextInt() || (aptNumber = scanner.nextInt()) <= 0) {
            System.out.println("Invalid input. Please enter a positive integer: ");
            scanner.nextLine(); // clear the invalid input
        }
        scanner.nextLine(); // consume the rest of the line

        System.out.print("New Apartment Size (square feet): ");
        double size = scanner.nextDouble();
        System.out.print("New Number of Bedrooms: ");
        int bedrooms = scanner.nextInt();
        System.out.print("New Number of Bathrooms: ");
        double bathrooms = scanner.nextDouble();
        System.out.print("New Monthly Rent: $");
        double monthlyRent = scanner.nextDouble();
        System.out.print("New Security Deposit: $");
        double securityDeposit = scanner.nextDouble();

        // Update apartment data in database
        String sql = "UPDATE Apartments SET AptSize = ?, Bedrooms = ?, Bathrooms = ?, MonthlyRent = ?, SecurityDeposit = ? WHERE AptNumber = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDouble(1, size);
            stmt.setInt(2, bedrooms);
            stmt.setDouble(3, bathrooms);
            stmt.setDouble(4, monthlyRent);
            stmt.setDouble(5, securityDeposit);
            stmt.setInt(6, aptNumber);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Apartment updated successfully.");
            } else {
                System.out.println("No apartment found with the given number.");
            }
        } catch (SQLException e) {
            System.out.println("Error updating apartment: " + e.getMessage());
        }
    }

    private void removeApartment() {
        DBTablePrinter.printTable(conn, "Apartments");

        System.out.print("Enter the Apartment Number to remove: ");
        int aptNumber = scanner.nextInt();
        scanner.nextLine(); // consume the rest of the line

        try {
            // Start transaction
            conn.setAutoCommit(false);

            // Delete dependencies in Apartment_PrivateAmenities
            String deleteApartmentPrivateAmenitiesSql = "DELETE FROM Apartment_PrivateAmenities WHERE AptNumber = ?";
            try (PreparedStatement stmt = conn.prepareStatement(deleteApartmentPrivateAmenitiesSql)) {
                stmt.setInt(1, aptNumber);
                stmt.executeUpdate();
            }

            // Delete the apartment
            String deleteApartmentSql = "DELETE FROM Apartments WHERE AptNumber = ?";
            try (PreparedStatement stmt = conn.prepareStatement(deleteApartmentSql)) {
                stmt.setInt(1, aptNumber);
                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Apartment removed successfully.");
                } else {
                    System.out.println("No apartment found with the given number.");
                }
            }

            // Commit transaction
            conn.commit();
        } catch (SQLException e) {
            System.out.println("Error removing apartment: " + e.getMessage());
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

    public void generateApartmentsForProperty() {
        DBTablePrinter.printTable(conn, "Property");

        int propertyId = 0;
        boolean validPropertyId = false;
        while (!validPropertyId) {
            System.out.print("Enter the Property ID for which to generate apartments: ");
            while (!scanner.hasNextInt()) {
                System.out.println("Invalid input. Please enter a positive integer: ");
                scanner.next(); // clear the invalid input
            }
            propertyId = scanner.nextInt();
            scanner.nextLine(); // consume the rest of the line

            if (propertyId > 0 && checkPropertyExists(propertyId)) {
                validPropertyId = true;
            } else {
                System.out.println("No property found with the given ID. Please enter a valid Property ID.");
            }
        }

        System.out.print("Enter the number of apartments to generate: ");
        int numberOfApartments;
        while (!scanner.hasNextInt() || (numberOfApartments = scanner.nextInt()) <= 0) {
            System.out.println("Invalid input. Please enter a positive integer: ");
            scanner.nextLine(); // clear the invalid input
        }
        System.out.print("Enter common apartment size (square feet): ");
        double size;
        while (!scanner.hasNextDouble() || (size = scanner.nextDouble()) <= 0) {
            System.out.println("Invalid input. Please enter a positive number for square feet: ");
            scanner.nextLine(); // clear the invalid input
        }

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
        double monthlyRent;
        while (!scanner.hasNextDouble() || (monthlyRent = scanner.nextDouble()) < 0) {
            System.out.println("Invalid input. Please enter a non-negative number: ");
            scanner.nextLine(); // clear the invalid input
        }

        System.out.print("Enter security deposit: $");
        double securityDeposit;
        while (!scanner.hasNextDouble() || (securityDeposit = scanner.nextDouble()) < 0) {
            System.out.println("Invalid input. Please enter a non-negative number: ");
            scanner.nextLine(); // clear the invalid input
        }

        // Insert apartments into the database
        String sql = "INSERT INTO Apartments (AptSize, Bedrooms, Bathrooms, MonthlyRent, SecurityDeposit, PropertyID_Ref) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            conn.setAutoCommit(false); // Use transaction to ensure all inserts are successful

            for (int i = 0; i < numberOfApartments; i++) {
                stmt.setDouble(1, size);
                stmt.setInt(2, bedrooms);
                stmt.setDouble(3, bathrooms);
                stmt.setDouble(4, monthlyRent);
                stmt.setDouble(5, securityDeposit);
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
            System.out.println("1: Add New Common Amenity");
            System.out.println("2: Add New Private Amenity");
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

            // Check if the amenity is already assigned to the property
            String checkSql = "SELECT COUNT(*) FROM Property_CommonAmenities WHERE PropertyID = ? AND AmenityID = ?";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setInt(1, propertyId);
                checkStmt.setInt(2, amenityId);
                ResultSet rs = checkStmt.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) {
                    System.out.println("This amenity is already assigned to the property.");
                    return;
                }
            }

            // Insert into Property_CommonAmenities table
            String sql = "INSERT INTO Property_CommonAmenities (PropertyID, AmenityID) VALUES (?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, propertyId);
                stmt.setInt(2, amenityId);

                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Amenity assigned successfully to the property.");
                    
                    // Automatically charge all tenants in the property
                    chargeTenantsForPropertyAmenity(propertyId, amenityId);
                } else {
                    System.out.println("Failed to assign the amenity.");
                }
            }
        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
        }
    }

    private void chargeTenantsForPropertyAmenity(int propertyId, int amenityId) {
        // SQL to find all tenants in the property and charge them
        String findTenantsSql = "SELECT TenantID FROM Lease WHERE AptNumber IN (SELECT AptNumber FROM Apartments WHERE PropertyID_Ref = ?)";
        try (PreparedStatement findTenantsStmt = conn.prepareStatement(findTenantsSql)) {
            findTenantsStmt.setInt(1, propertyId);
            ResultSet rs = findTenantsStmt.executeQuery();
            while (rs.next()) {
                int tenantId = rs.getInt("TenantID");
                addAmenityChargeToDues(tenantId, amenityId, true);
            }
        } catch (SQLException e) {
            System.out.println("Error charging tenants: " + e.getMessage());
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

            // Check if the amenity is already assigned to the apartment
            String checkSql = "SELECT COUNT(*) FROM Apartment_PrivateAmenities WHERE AptNumber = ? AND PrivateAmenityID = ?";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setInt(1, apartmentNumber);
                checkStmt.setInt(2, privateAmenityId);
                ResultSet rs = checkStmt.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) {
                    System.out.println("This amenity is already assigned to the apartment.");
                    return;
                }
            }

            // Insert into Apartment_PrivateAmenities table
            String sql = "INSERT INTO Apartment_PrivateAmenities (AptNumber, PrivateAmenityID) VALUES (?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, apartmentNumber);
                stmt.setInt(2, privateAmenityId);

                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println(" Private amenity assigned successfully to the apartment.");

                    // Automatically charge the tenant of the apartment
                    chargeTenantForApartmentAmenity(apartmentNumber, privateAmenityId);
                } else {
                    System.out.println("Failed to assign the amenity.");
                }
            }
        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
        }
    }

    private void chargeTenantForApartmentAmenity(int apartmentNumber, int amenityId) {
        // SQL to find the tenant of the apartment and charge them
        String findTenantSql = "SELECT TenantID FROM Lease WHERE AptNumber = ?";
        try (PreparedStatement findTenantStmt = conn.prepareStatement(findTenantSql)) {
            findTenantStmt.setInt(1, apartmentNumber);
            ResultSet rs = findTenantStmt.executeQuery();
            if (rs.next()) {
                int tenantId = rs.getInt("TenantID");
                addAmenityChargeToDues(tenantId, amenityId, false);
            }
        } catch (SQLException e) {
            System.out.println("Error charging tenant: " + e.getMessage());
        }
    }

    public void addAmenityChargeToDues(int tenantId, int amenityId, boolean isCommonAmenity) {
        // Determine which table to query based on amenity type
        String amenityTable = isCommonAmenity ? "CommonAmenities" : "PrivateAmenities";

        // SQL query to find the cost and name of the amenity
        String findAmenityCostSql = "SELECT Cost, AmenityName FROM " + amenityTable + " WHERE AmenityID = ?";

        // SQL query to add a charge to a tenant's dues with null PaymentDate
        String insertPaymentSql = "INSERT INTO Payments (Amount, PaymentDate, PaymentMethod, TenantID) VALUES (?, NULL, 'Pending', ?)";
        String insertPaymentBreakdownSql = "INSERT INTO PaymentBreakdown (PaymentID, Description, Amount) VALUES (?, ?, ?)";

        try {
            // Start a transaction
            conn.setAutoCommit(false);

            // Find the cost and name of the amenity
            PreparedStatement pstmtFindCost = conn.prepareStatement(findAmenityCostSql);
            pstmtFindCost.setInt(1, amenityId);
            ResultSet rs = pstmtFindCost.executeQuery();
            double amenityCost = 0;
            String amenityName = "";
            if (rs.next()) {
                amenityCost = rs.getDouble("Cost");
                amenityName = rs.getString("AmenityName");
            }

            // Insert a payment record with null PaymentDate
            PreparedStatement pstmtPayment = conn.prepareStatement(insertPaymentSql, new String[]{"PaymentID"});
            pstmtPayment.setDouble(1, amenityCost);
            pstmtPayment.setInt(2, tenantId);
            pstmtPayment.executeUpdate();

            // Retrieve the generated PaymentID
            ResultSet rsPayment = pstmtPayment.getGeneratedKeys();
            int paymentId = 0;
            if (rsPayment.next()) {
                paymentId = rsPayment.getInt(1);
            }

            // Insert into PaymentBreakdown
            if (paymentId != 0) {
                PreparedStatement pstmtBreakdown = conn.prepareStatement(insertPaymentBreakdownSql);
                pstmtBreakdown.setInt(1, paymentId);
                pstmtBreakdown.setString(2, "Amenity Charge - " + amenityName);
                pstmtBreakdown.setDouble(3, amenityCost);
                pstmtBreakdown.executeUpdate();
            }

            // Commit the transaction
            conn.commit();

        } catch (SQLException e) {
            try {
                // Rollback in case of error
                conn.rollback();
            } catch (SQLException ex) {
                System.out.println("Error during rollback: " + ex.getMessage());
            }
            System.out.println("SQL Error: " + e.getMessage());
        } finally {
            try {
                // Reset auto-commit to default
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                System.out.println("Error resetting auto-commit: " + e.getMessage());
            }
        }
    }

    public void addNewCommonAmenity() {
        System.out.print("Enter common amenity name: ");
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

        String costType = standardizeCostType();

        // Insert data into the database
        String sql = "INSERT INTO CommonAmenities (AmenityName, IsAvailable, Cost, CostType) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, amenityName);
            stmt.setString(2, isAvailableStr);
            stmt.setDouble(3, cost);
            stmt.setString(4, costType);
            stmt.executeUpdate();
            System.out.println("Common amenity added successfully.");
        } catch (SQLException e) {
            System.out.println("Error adding amenity: " + e.getMessage());
        }
    }

    private void editCommonAmenity() {
        DBTablePrinter.printTable(conn, "CommonAmenities");

        System.out.print("Enter the Common Amenity ID to edit: ");
        int amenityId = scanner.nextInt();
        scanner.nextLine(); // consume the rest of the line

        System.out.print("New Common Amenity Name: ");
        String amenityName = scanner.nextLine();

        System.out.print("Is the amenity available (y/n)? ");
        String isAvailableInput = scanner.nextLine();
        String isAvailableStr = "y".equalsIgnoreCase(isAvailableInput) ? "Y" : "N";

        System.out.print("New Cost: ");
        double cost = scanner.nextDouble();
        scanner.nextLine(); // consume the rest of the line

        String costType = standardizeCostType();

        String sql = "UPDATE CommonAmenities SET AmenityName = ?, IsAvailable = ?, Cost = ?, CostType = ? WHERE AmenityID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, amenityName);
            stmt.setString(2, isAvailableStr);
            stmt.setDouble(3, cost);
            stmt.setString(4, costType);
            stmt.setInt(5, amenityId);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Common amenity updated successfully.");
            } else {
                System.out.println("No common amenity found with the given ID.");
            }
        } catch (SQLException e) {
            System.out.println("Error updating common amenity: " + e.getMessage());
        }
    }

    private void removeCommonAmenity() {
        DBTablePrinter.printTable(conn, "CommonAmenities");

        System.out.print("Enter the Common Amenity ID to remove: ");
        int amenityId = scanner.nextInt();
        scanner.nextLine(); // consume the rest of the line

        String sql = "DELETE FROM CommonAmenities WHERE AmenityID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, amenityId);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Common amenity removed successfully.");
            } else {
                System.out.println("No common amenity found with the given ID.");
            }
        } catch (SQLException e) {
            System.out.println("Error removing common amenity: " + e.getMessage());
        }
    }

    public void addNewPrivateAmenity() {
        System.out.print("Enter private amenity name: ");
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

        String costType = standardizeCostType();

        // Insert data into the database
        String sql = "INSERT INTO PrivateAmenities (AmenityName, IsAvailable, Cost, CostType) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, amenityName);
            stmt.setString(2, isAvailableStr);
            stmt.setDouble(3, cost);
            stmt.setString(4, costType);
            stmt.executeUpdate();
            System.out.println("Private amenity added successfully.");
        } catch (SQLException e) {
            System.out.println("Error adding amenity: " + e.getMessage());
        }
    }

    private void editPrivateAmenity() {
        DBTablePrinter.printTable(conn, "PrivateAmenities");

        System.out.print("Enter the Private Amenity ID to edit: ");
        int amenityId = scanner.nextInt();
        scanner.nextLine(); // consume the rest of the line

        System.out.print("New Private Amenity Name: ");
        String amenityName = scanner.nextLine();

        System.out.print("Is the amenity available (y/n)? ");
        String isAvailableInput = scanner.nextLine();
        String isAvailableStr = "y".equalsIgnoreCase(isAvailableInput) ? "Y" : "N";

        System.out.print("New Cost: ");
        double cost = scanner.nextDouble();
        scanner.nextLine(); // consume the rest of the line

        String costType = standardizeCostType();

        String sql = "UPDATE PrivateAmenities SET AmenityName = ?, IsAvailable = ?, Cost = ?, CostType = ? WHERE PrivateAmenityID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, amenityName);
            stmt.setString(2, isAvailableStr);
            stmt.setDouble(3, cost);
            stmt.setString(4, costType);
            stmt.setInt(5, amenityId);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Private amenity updated successfully.");
            } else {
                System.out.println("No private amenity found with the given ID.");
            }
        } catch (SQLException e) {
            System.out.println("Error updating private amenity: " + e.getMessage());
        }
    }

    private void removePrivateAmenity() {
        DBTablePrinter.printTable(conn, "PrivateAmenities");

        System.out.print("Enter the Amenity ID to remove: ");
        int privateAmenityId = scanner.nextInt();
        scanner.nextLine(); // consume the rest of the line

        String sql = "DELETE FROM PrivateAmenities WHERE PrivateAmenityID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, privateAmenityId);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Private amenity removed successfully.");
            } else {
                System.out.println("No private amenity found with the given ID.");
            }
        } catch (SQLException e) {
            System.out.println("Error removing private amenity: " + e.getMessage());
        }
    }

    private boolean checkPropertyExists(int propertyId) {
        String sql = "SELECT COUNT(*) FROM Property WHERE PropertyID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, propertyId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.out.println("Error checking property existence: " + e.getMessage());
        }
        return false;
    }

    private String standardizeCostType() {
        while (true) {
            System.out.print("New Cost Type (One-time, Monthly, Included): ");
            String costTypeInput = scanner.nextLine();

            if (costTypeInput.equalsIgnoreCase("One-time")) {
                return "One-time";
            } else if (costTypeInput.equalsIgnoreCase("Monthly")) {
                return "Monthly";
            } else if (costTypeInput.equalsIgnoreCase("Included")) {
                return "Included";
            } else {
                System.out.println("Invalid input. Please enter 'One-time', 'Monthly', or 'Included'.");
            }
        }
    }
}