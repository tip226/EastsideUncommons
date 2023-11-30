package interfaces;

import db.DBTablePrinter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;
import java.sql.Date;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.sql.Statement;

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
            System.out.println("1: Record Visit Data");
            System.out.println("2: Record Lease Data");
            System.out.println("3: Record Move-Out");
            System.out.println("4: Add Person to Lease");
            System.out.println("5: Add Pet to Lease");
            System.out.println("6: Manage Tenants");
            System.out.println("7: Exit");

            try {
                System.out.print("Select an option: ");
                int option = Integer.parseInt(scanner.nextLine()); // parse the input as integer
                int leaseID;

                switch (option) {
                    case 1:
                        recordVisitData();
                        break;
                    case 2:
                        recordLeaseData();
                        break;
                    case 3:
                        setMoveOutDate();
                        break;
                    case 4:
                        DBTablePrinter.printTable(conn, "Lease");
                        leaseID = validateLeaseID();
                        addPersonToLease(leaseID);
                        break;
                    case 5:
                        DBTablePrinter.printTable(conn, "Lease");
                        leaseID = validateLeaseID();
                        addPetToLease(leaseID);
                        break;
                    case 6:
                        manageTenantsMenu();
                        break;
                    case 7:
                        System.out.println("Exiting Property Manager Interface.");
                        return;
                    default:
                        System.out.println("Invalid option selected. Please try again.");
                        break;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            } catch (SQLException e) {
                System.out.println("SQL Error: " + e.getMessage());
            }
        }
            
    }

    private void manageTenantsMenu() {
        while (true) {
            System.out.println("Tenant Management");
            System.out.println("1: Add Tenant");
            System.out.println("2: Edit Tenant");
            System.out.println("3: Remove Tenant");
            System.out.println("4: Return to Main Menu");

            try {
                System.out.print("Select an option: ");
                int option = Integer.parseInt(scanner.nextLine()); // parse the input as integer

                switch (option) {
                    case 1:
                        addTenant();
                        break;
                    case 2:
                        editTenant();
                        break;
                    case 3:
                        removeTenant();
                        break;
                    case 4:
                        return;
                    default:
                        System.out.println("Invalid option selected. Please try again.");
                        break;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }

    private void addTenant() {
        System.out.println("Do you want to add a tenant manually (M) or select from prospective tenants (P)?");
        String choice = scanner.nextLine().trim().toUpperCase();

        try {
            if ("P".equals(choice)) {
                addTenantFromProspective();
            } else if ("M".equals(choice)) {
                addTenantManually();
            } else {
                System.out.println("Invalid choice. Please choose 'M' for manually or 'P' for prospective tenants.");
            }
        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
        }
    }

    private void addTenantFromProspective() {
        DBTablePrinter.printTable(conn, "ProspectiveTenant");
        int pTenantID = validateProspectiveTenantID();

        try {
            // Transfer prospective tenant to Tenants table
            transferProspectiveTenantToTenant(pTenantID);
        } catch (SQLException e) {
            System.out.println("Error transferring prospective tenant: " + e.getMessage());
        }
    }

    private int addTenantManually() throws SQLException {
        // Validate name
        String name = "";
        System.out.println("Enter Tenant's Full Name (First and Last Name):");
        while (true) {
            name = scanner.nextLine().trim();
            if (name.split("\\s+").length >= 2) { // Checks if there are at least two words
                break;
            }
            System.out.println("Invalid input. Please enter both first and last name:");
        }

        System.out.println("Enter Tenant's Email:");
        String email = scanner.nextLine().trim();

        System.out.println("Enter Tenant's Phone Number:");
        String phoneNumber = scanner.nextLine().trim();

        int tenantID = -1;
        String insertSql = "INSERT INTO Tenants (TenantName, Email, PhoneNumber) VALUES (?, ?, ?)";

        try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
            insertStmt.setString(1, name);
            insertStmt.setString(2, email);
            insertStmt.setString(3, phoneNumber);
            insertStmt.executeUpdate();
        }

        // Retrieve the ID of the newly inserted tenant
        String selectSql = "SELECT TenantID FROM Tenants WHERE TenantName = ? AND Email = ? AND PhoneNumber = ? ORDER BY TenantID DESC FETCH FIRST ROW ONLY";

        try (PreparedStatement selectStmt = conn.prepareStatement(selectSql)) {
            selectStmt.setString(1, name);
            selectStmt.setString(2, email);
            selectStmt.setString(3, phoneNumber);
            ResultSet rs = selectStmt.executeQuery();
            if (rs.next()) {
                tenantID = rs.getInt("TenantID");
            }
        }

        return tenantID;
    }

    private void editTenant() {
        System.out.println("Enter Tenant ID to Edit:");
        int tenantID = scanner.nextInt();
        scanner.nextLine(); // consume the rest of the line

        System.out.println("Enter Updated Tenant's Name:");
        String tenantName = scanner.nextLine().trim();

        System.out.println("Enter Updated Tenant's Email:");
        String email = scanner.nextLine().trim();

        System.out.println("Enter Updated Tenant's Phone Number:");
        String phoneNumber = scanner.nextLine().trim();

        String sql = "UPDATE Tenants SET TenantName = ?, Email = ?, PhoneNumber = ? WHERE TenantID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, tenantName);
            stmt.setString(2, email);
            stmt.setString(3, phoneNumber);
            stmt.setInt(4, tenantID);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Tenant updated successfully.");
            } else {
                System.out.println("No Tenant found with the provided ID.");
            }
        } catch (SQLException e) {
            System.out.println("Error updating tenant: " + e.getMessage());
        }
    }

    private void removeTenant() {
        System.out.println("Enter Tenant ID to Remove:");
        int tenantID = scanner.nextInt();
        scanner.nextLine(); // consume the rest of the line

        String sql = "DELETE FROM Tenants WHERE TenantID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, tenantID);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Tenant removed successfully.");
            } else {
                System.out.println("No Tenant found with the provided ID.");
            }
        } catch (SQLException e) {
            System.out.println("Error removing tenant: " + e.getMessage());
        }
    }

    public void recordVisitData() {
        // Print list of apartments
        DBTablePrinter.printTable(conn, "Apartments");

        System.out.println("Enter details for recording a visit");

        // Validate name
        String name = "";
        System.out.println("Enter Visitor's Full Name (First and Last Name):");
        while (true) {
            name = scanner.nextLine().trim();
            if (name.split("\\s+").length >= 2) { // Checks if there are at least two words
                break;
            }
            System.out.println("Invalid input. Please enter both first and last name:");
        }

        // Validate email
        String email = "";
        System.out.println("Enter Visitor's Email:");
        while (true) {
            email = scanner.nextLine().trim();
            if (email.contains("@") && email.contains(".")) { // Basic email validation
                break;
            }
            System.out.println("Invalid input. Please enter a valid email:");
        }

        // Validate phone number
        String phoneNumber = "";
        System.out.println("Enter Visitor's Phone Number:");
        while (true) {
            phoneNumber = scanner.nextLine().trim();
            if (phoneNumber.matches("\\d+")) { // Checks if the input is numeric
                break;
            }
            System.out.println("Invalid input. Please enter a valid phone number:");
        }

        System.out.println("Enter Visit Date (YYYY-MM-DD):");
        java.sql.Date visitDate = validateAndInputDate();

        // Validate apartment ID
        System.out.println("Enter Apartment ID for the visit:");
        int apartmentId = 0;
        boolean validApartmentId = false;
        while (!validApartmentId) {
            while (!scanner.hasNextInt()) {
                System.out.println("Invalid input. Please enter a positive integer:");
                scanner.next(); // clear the invalid input
            }
            apartmentId = scanner.nextInt();
            scanner.nextLine(); // consume the rest of the line

            if (apartmentId > 0 && checkApartmentExists(apartmentId)) {
                validApartmentId = true;
            } else {
                System.out.println("No apartment found with the given ID. Please enter a valid Apartment ID.");
            }
        }

        String employmentStatus = standardizeEmploymentStatus();

        // Annual income
        System.out.println("Enter Annual Income:");
        double annualIncome = 0;
        while (true) {
            try {
                annualIncome = Double.parseDouble(scanner.nextLine().trim());
                if (annualIncome >= 0) {
                    break;
                }
                System.out.println("Invalid input. Please enter a non-negative number:");
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number:");
            }
        }

        // Credit score
        System.out.println("Enter Credit Score:");
        int creditScore = 0;
        while (true) {
            try {
                creditScore = Integer.parseInt(scanner.nextLine().trim());
                if (creditScore >= 0) {
                    break;
                }
                System.out.println("Invalid input. Please enter a non-negative number:");
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number:");
            }
        }

        String sql = "INSERT INTO ProspectiveTenant (Name, Email, PhoneNumber, VisitDate, ApartmentVisited, EmploymentStatus, AnnualIncome, CreditScore) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.setString(2, email);
            stmt.setString(3, phoneNumber);
            stmt.setDate(4, visitDate);
            stmt.setInt(5, apartmentId);
            stmt.setString(6, employmentStatus);
            stmt.setDouble(7, annualIncome);
            stmt.setInt(8, creditScore);
            stmt.executeUpdate();
            System.out.println("Prospective tenant added successfully.");
        } catch (SQLException e) {
            System.out.println("Error adding prospective tenant: " + e.getMessage());
        }
    }

    public void recordLeaseData() {
        try {
            System.out.println("Enter details for recording a lease");

            DBTablePrinter.printTable(conn, "Apartments");
            int aptNumber = validateApartmentID();

            if (checkActiveLease(aptNumber)) {
                System.out.println("This apartment already has an active lease.");
                return;
            }

            System.out.println("Enter Lease Start Date (YYYY-MM-DD):");
            java.sql.Date leaseStartDate = validateAndInputDate();

            // Input and validate Lease End Date
            java.sql.Date leaseEndDate = null;
            while (leaseEndDate == null || leaseEndDate.before(leaseStartDate)) {
                System.out.println("Enter Lease End Date (YYYY-MM-DD):");
                leaseEndDate = validateAndInputDate();
                if (leaseEndDate.before(leaseStartDate)) {
                    System.out.println("Lease End Date must be after Lease Start Date.");
                }
            }

            double monthlyRent = validateMoneyInput("Enter Monthly Rent: $");
            double securityDeposit = validateMoneyInput("Enter Security Deposit: $");
            
            int totalOccupants = getTotalOccupants(aptNumber);
            System.out.println("Total Occupants: " + totalOccupants);

            int leaseID = insertLeaseData(aptNumber, leaseStartDate, leaseEndDate, monthlyRent, securityDeposit);
            System.out.println("Lease ID: " + leaseID);
            if (leaseID == -1) {
                System.out.println("Error creating lease.");
                return;
            }

            // Add additional tenants and pets
            for (int i = 0; i < totalOccupants; i++) {
                System.out.println("Add tenant (T) or pet (P)?");
                String occupantType = scanner.nextLine().trim().toUpperCase();
                if ("T".equals(occupantType)) {
                    addPersonToLease(leaseID);
                } else if ("P".equals(occupantType)) {
                    addPetToLease(leaseID);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error in lease processing: " + e.getMessage());
        }
    }

private int insertLeaseData(int aptNumber, java.sql.Date leaseStartDate, java.sql.Date leaseEndDate, double monthlyRent, double securityDeposit) {
    int leaseID = -1;
    try {
        // Insert the new lease data
        String insertSql = "INSERT INTO Lease (AptNumber, LeaseStartDate, LeaseEndDate, MonthlyRent, SecurityDeposit) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
            insertStmt.setInt(1, aptNumber);
            insertStmt.setDate(2, leaseStartDate);
            insertStmt.setDate(3, leaseEndDate);
            insertStmt.setDouble(4, monthlyRent);
            insertStmt.setDouble(5, securityDeposit);
            insertStmt.executeUpdate();
        }

        // Retrieve the ID of the newly inserted lease
        String selectSql = "SELECT LeaseID FROM Lease WHERE AptNumber = ? AND LeaseStartDate = ? AND LeaseEndDate = ? ORDER BY LeaseID DESC FETCH FIRST ROW ONLY";
        try (PreparedStatement selectStmt = conn.prepareStatement(selectSql)) {
            selectStmt.setInt(1, aptNumber);
            selectStmt.setDate(2, leaseStartDate);
            selectStmt.setDate(3, leaseEndDate);
            ResultSet rs = selectStmt.executeQuery();
            if (rs.next()) {
                leaseID = rs.getInt("LeaseID");
            }
        }

    } catch (SQLException e) {
        System.out.println("Error in insertLeaseData: " + e.getMessage());
        e.printStackTrace();
    }
    return leaseID;
}

private boolean checkActiveLease(int aptNumber) throws SQLException {
    String sql = "SELECT COUNT(*) FROM Lease WHERE AptNumber = ? AND LeaseEndDate > CURRENT_DATE";
    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setInt(1, aptNumber);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            return rs.getInt(1) > 0;
        }
    }
    return false;
}

    private int transferProspectiveTenantToTenant(int pTenantID) throws SQLException {
        // Transfer data from ProspectiveTenant to Tenant
        String transferSql = "INSERT INTO Tenants (TenantName, Email, PhoneNumber) " +
                            "SELECT Name, Email, PhoneNumber FROM ProspectiveTenant WHERE PTenantID = ?";
        String deleteSql = "DELETE FROM ProspectiveTenant WHERE PTenantID = ?";

        // First, get the prospective tenant's details
        String getDetailsSql = "SELECT Name, Email, PhoneNumber FROM ProspectiveTenant WHERE PTenantID = ?";
        String tenantName = "", email = "", phoneNumber = "";
        try (PreparedStatement detailsStmt = conn.prepareStatement(getDetailsSql)) {
            detailsStmt.setInt(1, pTenantID);
            ResultSet rs = detailsStmt.executeQuery();
            if (rs.next()) {
                tenantName = rs.getString("Name");
                email = rs.getString("Email");
                phoneNumber = rs.getString("PhoneNumber");
            }
        }

        // Now insert into Tenants table
        try (PreparedStatement transferStmt = conn.prepareStatement(transferSql)) {
            transferStmt.setInt(1, pTenantID);
            transferStmt.executeUpdate();
        }

        // Retrieve the new TenantID based on the unique data
        int tenantID = -1;
        String getNewTenantIDSql = "SELECT TenantID FROM Tenants WHERE TenantName = ? AND Email = ? AND PhoneNumber = ?";
        try (PreparedStatement newTenantStmt = conn.prepareStatement(getNewTenantIDSql)) {
            newTenantStmt.setString(1, tenantName);
            newTenantStmt.setString(2, email);
            newTenantStmt.setString(3, phoneNumber);
            ResultSet rs = newTenantStmt.executeQuery();
            if (rs.next()) {
                tenantID = rs.getInt("TenantID");
            }
        }

        // Finally, delete the prospective tenant
        try (PreparedStatement deleteStmt = conn.prepareStatement(deleteSql)) {
            deleteStmt.setInt(1, pTenantID);
            deleteStmt.executeUpdate();
        }

        return tenantID;
    }

    private int getTotalOccupants(int aptNumber) throws SQLException {
        int totalOccupants = 0;
        while (totalOccupants < 1) {
            System.out.println("Enter the number of tenants (including pets):");
            while (!scanner.hasNextInt()) {
                System.out.println("Invalid input. Please enter a positive integer:");
                scanner.next(); // Clear the invalid input
            }
            totalOccupants = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            if (totalOccupants < 1) {
                System.out.println("You must have at least one occupant.");
            } else {
                try {
                    checkCapacity(aptNumber, totalOccupants);
                } catch (SQLException e) {
                    System.out.println("Capacity check failed: " + e.getMessage());
                    totalOccupants = 0; // Reset to force re-entry
                }
            }
        }
        return totalOccupants;
    }

    private void checkCapacity(int aptNumber, int totalOccupants) throws SQLException {
        String sql = "SELECT Bedrooms FROM Apartments WHERE AptNumber = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, aptNumber);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int bedrooms = rs.getInt("Bedrooms");
                if (totalOccupants > bedrooms) {
                    throw new SQLException("Total number of occupants exceeds the number of bedrooms.");
                }
            }
        }
    }

    public void setMoveOutDate() {
        try {
            DBTablePrinter.printTable(conn, "Lease");
            int leaseID = validateLeaseID();

            System.out.println("Enter Move-Out Date (YYYY-MM-DD):");
            java.sql.Date moveOutDate = validateAndInputDate();

            // Ask about damages and update lease
            handleDamageAssessmentAndUpdateLease(leaseID, moveOutDate);
        } catch (SQLException e) {
            System.out.println("Error in updating move-out details: " + e.getMessage());
        }
    }

    private void handleDamageAssessmentAndUpdateLease(int leaseID, java.sql.Date moveOutDate) throws SQLException {
        // Ask about damages
        String damageResponse;
        boolean damageAssessed = false;
        do {
            System.out.println("Were there any damages? (Y/N):");
            damageResponse = scanner.nextLine().trim();
            if ("Y".equalsIgnoreCase(damageResponse)) {
                damageAssessed = true;
                break;
            } else if ("N".equalsIgnoreCase(damageResponse)) {
                break;
            } else {
                System.out.println("Invalid input. Please enter 'Y' for Yes or 'N' for No.");
            }
        } while (true);

        // Update Lease table with move-out date and damage assessment
        updateLeaseWithMoveOut(leaseID, moveOutDate, damageAssessed);
    }

    private void updateLeaseWithMoveOut(int leaseID, java.sql.Date moveOutDate, boolean damageAssessed) throws SQLException {
        String sql = "UPDATE Lease SET MoveOutDate = ?, DamageAssessed = ? WHERE LeaseID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, moveOutDate);
            stmt.setString(2, damageAssessed ? "Y" : "N");
            stmt.setInt(3, leaseID);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Move-Out recorded successfully.");
            } else {
                System.out.println("No lease found with the provided ID.");
            }
        }
    }

    public void addPersonToLease(int leaseID) throws SQLException {

        System.out.println("Do you want to add a tenant manually (M) or select from prospective tenants (P)?");
        String choice = scanner.nextLine().trim().toUpperCase();

        int tenantID;
        if ("P".equals(choice)) {
            tenantID = selectProspectiveTenant();
        } else if ("M".equals(choice)) {
            tenantID = addTenantManually();
        } else {
            System.out.println("Invalid choice. Please choose 'M' for manually or 'P' for prospective tenants.");
            return;
        }

        String sql = "INSERT INTO LeaseTenants (LeaseID, TenantID) VALUES (?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, leaseID);
            stmt.setInt(2, tenantID);
            stmt.executeUpdate();
            System.out.println("Tenant added to lease successfully.");
        }
    }

    private int selectProspectiveTenant() throws SQLException {
        DBTablePrinter.printTable(conn, "ProspectiveTenant");
        int pTenantID = validateProspectiveTenantID();

        int tenantID = -1;
        String getDetailsSql = "SELECT Name, Email, PhoneNumber FROM ProspectiveTenant WHERE PTenantID = ?";
        String name = "", email = "", phoneNumber = "";

        try (PreparedStatement detailsStmt = conn.prepareStatement(getDetailsSql)) {
            detailsStmt.setInt(1, pTenantID);
            ResultSet rs = detailsStmt.executeQuery();
            if (rs.next()) {
                name = rs.getString("Name");
                email = rs.getString("Email");
                phoneNumber = rs.getString("PhoneNumber");
            }
        }

        String insertSql = "INSERT INTO Tenants (TenantName, Email, PhoneNumber) VALUES (?, ?, ?)";
        try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
            insertStmt.setString(1, name);
            insertStmt.setString(2, email);
            insertStmt.setString(3, phoneNumber);
            insertStmt.executeUpdate();
        }

        // Retrieve the ID of the newly inserted tenant
        String selectSql = "SELECT TenantID FROM Tenants WHERE TenantName = ? AND Email = ? AND PhoneNumber = ? ORDER BY TenantID DESC FETCH FIRST ROW ONLY";

        try (PreparedStatement selectStmt = conn.prepareStatement(selectSql)) {
            selectStmt.setString(1, name);
            selectStmt.setString(2, email);
            selectStmt.setString(3, phoneNumber);
            ResultSet rs = selectStmt.executeQuery();
            if (rs.next()) {
                tenantID = rs.getInt("TenantID");
            }
        }

        String deleteSql = "DELETE FROM ProspectiveTenant WHERE PTenantID = ?";
        try (PreparedStatement deleteStmt = conn.prepareStatement(deleteSql)) {
            deleteStmt.setInt(1, pTenantID);
            deleteStmt.executeUpdate();
        }

        return tenantID;
    }

    public void addPetToLease(int leaseID) throws SQLException {
        System.out.println("Enter details to add a pet to a lease");

        System.out.println("Enter Pet's Name:");
        String petName = scanner.nextLine().trim();

        System.out.println("Enter Pet Type:");
        String petType = scanner.nextLine().trim();

        int tenantID;
        while (true) {
            // Display tenants in the current lease
            printTenantsInLease(leaseID);

            // Validate and input the Tenant ID to associate the pet
            System.out.println("Enter Tenant ID to associate with the pet:");
            tenantID = validateTenantID();

            try {
                if (isTenantInLease(leaseID, tenantID)) {
                    break;
                } else {
                    System.out.println("The selected tenant is not part of the lease. Please try again.");
                }
            } catch (SQLException e) {
                System.out.println("Error validating tenant in lease: " + e.getMessage());
                return;
            }
        }

        try {
            String sql = "INSERT INTO Pets (PetName, PetType, TenantID) VALUES (?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, petName);
                stmt.setString(2, petType);
                stmt.setInt(3, tenantID);
                stmt.executeUpdate();
                System.out.println("Pet added to lease successfully.");
            }
        } catch (SQLException e) {
            System.out.println("Error while adding pet to lease: " + e.getMessage());
        }
    }

    private void printTenantsInLease(int leaseID) {
        String sql = "SELECT t.TenantID, t.TenantName FROM Tenants t " +
                    "JOIN LeaseTenants lt ON t.TenantID = lt.TenantID " +
                    "WHERE lt.LeaseID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, leaseID);
            ResultSet rs = stmt.executeQuery();
            System.out.println("Tenants in Lease ID " + leaseID + ":");
            System.out.println("TenantID\tTenantName");
            while (rs.next()) {
                int tenantId = rs.getInt("TenantID");
                String tenantName = rs.getString("TenantName");
                System.out.println(tenantId + "\t\t" + tenantName);
            }
        } catch (SQLException e) {
            System.out.println("Error displaying tenants in lease: " + e.getMessage());
        }
    }

    private boolean isTenantInLease(int leaseID, int tenantID) throws SQLException {
        String sql = "SELECT * FROM LeaseTenants WHERE LeaseID = ? AND TenantID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, leaseID);
            stmt.setInt(2, tenantID);
            ResultSet rs = stmt.executeQuery();
            return rs.next(); // Returns true if the tenant is part of the lease
        }
    }

    private boolean checkApartmentExists(int apartmentId) {
        String sql = "SELECT COUNT(*) FROM Apartments WHERE AptNumber = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, apartmentId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }


    private String standardizeEmploymentStatus() {
        while (true) {
            System.out.print("Enter Employment Status (Employed, Student, Unemployed, Self-Employed, Retired, Other): ");
            String employmentStatus = scanner.nextLine();

            if (employmentStatus.equalsIgnoreCase("Employed")) {
                return "Employed";
            } else if (employmentStatus.equalsIgnoreCase("Student")) {
                return "Student";
            } else if (employmentStatus.equalsIgnoreCase("Unemployed")) {
                return "Unemployed";
            } else if (employmentStatus.equalsIgnoreCase("Self-Employed")) {
                return "Self-Employed";
            } else if (employmentStatus.equalsIgnoreCase("Retired")) {
                return "Retired";
            } else if (employmentStatus.equalsIgnoreCase("Other")) {
                return "Other";
            } else {
                System.out.println("Invalid input. Please enter 'Employed', 'Student', 'Unemployed', 'Self-Employed', 'Retired', or 'Other'.");
            }
        }
    }

    private java.sql.Date validateAndInputDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false);
        java.sql.Date sqlDate = null;
        while (sqlDate == null) {
            String dateString = scanner.nextLine();
            try {
                java.util.Date parsedDate = dateFormat.parse(dateString);
                sqlDate = new java.sql.Date(parsedDate.getTime()); // Convert to java.sql.Date
            } catch (Exception e) {
                System.out.println("Please use YYYY-MM-DD format.");
            }
        }
        return sqlDate;
    }

    private int validateApartmentID() {
        int apartmentId = 0;
        boolean validApartmentId = false;
        while (!validApartmentId) {
            System.out.print("Enter Apartment ID: ");
            while (!scanner.hasNextInt()) {
                System.out.println("Invalid input. Please enter a positive integer: ");
                scanner.next(); // Clear the invalid input
            }
            apartmentId = scanner.nextInt();
            scanner.nextLine(); // Consume the rest of the line

            if (apartmentId > 0 && checkApartmentExists(apartmentId)) {
                validApartmentId = true;
            } else {
                System.out.println("No apartment found with the given ID. Please enter a valid Apartment ID.");
            }
        }
        return apartmentId;
    }

    private double validateMoneyInput(String prompt) {
        String input;
        double value;
        while (true) {
            System.out.println(prompt);
            input = scanner.nextLine().trim();
            if (input.matches("\\d+(\\.\\d{1,2})?")) { // Updated regex here
                try {
                    value = Double.parseDouble(input);
                    break;
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Please enter a valid number.");
                }
            } else {
                System.out.println("Please enter a valid amount with up to two decimal places.");
            }
        }
        return value;
    }

    private int validateTenantID() {
        int tenantID = 0;
        boolean validTenantId = false;
        while (!validTenantId) {
            System.out.print("Enter Tenant ID: ");
            while (!scanner.hasNextInt()) {
                System.out.println("Invalid input. Please enter a positive integer: ");
                scanner.next(); // Clear the invalid input
            }
            tenantID = scanner.nextInt();
            scanner.nextLine(); // Consume the rest of the line

            if (tenantID > 0 && checkTenantExists(tenantID)) {
                validTenantId = true;
            } else {
                System.out.println("No tenant found with the given ID. Please enter a valid Tenant ID.");
            }
        }
        return tenantID;
    }

    private boolean checkTenantExists(int tenantId) {
        String sql = "SELECT COUNT(*) FROM Tenants WHERE TenantID = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, tenantId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    private int validateProspectiveTenantID() {
        int pTenantID = 0;
        boolean validProspectiveTenantId = false;
        while (!validProspectiveTenantId) {
            System.out.print("Enter Prospective Tenant ID: ");
            while (!scanner.hasNextInt()) {
                System.out.println("Invalid input. Please enter a positive integer: ");
                scanner.next(); // Clear the invalid input
            }
            pTenantID = scanner.nextInt();
            scanner.nextLine(); // Consume the rest of the line

            if (pTenantID > 0 && checkProspectiveTenantExists(pTenantID)) {
                validProspectiveTenantId = true;
            } else {
                System.out.println("No prospective tenant found with the given ID. Please enter a valid Prospective Tenant ID.");
            }
        }
        return pTenantID;
    }

    private boolean checkProspectiveTenantExists(int pTenantID) {
        String sql = "SELECT COUNT(*) FROM ProspectiveTenant WHERE PTenantID = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, pTenantID);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    private int validateLeaseID() {
        int leaseID = 0;
        boolean validLeaseId = false;
        while (!validLeaseId) {
            System.out.print("Enter Lease ID: ");
            while (!scanner.hasNextInt()) {
                System.out.println("Invalid input. Please enter a positive integer: ");
                scanner.next(); // Clear the invalid input
            }
            leaseID = scanner.nextInt();
            scanner.nextLine(); // Consume the rest of the line

            if (leaseID > 0 && checkLeaseExists(leaseID)) {
                validLeaseId = true;
            } else {
                System.out.println("No lease found with the given ID. Please enter a valid Lease ID.");
            }
        }
        return leaseID;
    }

    private boolean checkLeaseExists(int leaseId) {
        String sql = "SELECT COUNT(*) FROM Lease WHERE LeaseID = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, leaseId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

}
