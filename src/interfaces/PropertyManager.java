package interfaces;

import db.DBTablePrinter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;
import java.sql.Date;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;

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
            System.out.println("6: Set Move-Out Date");
            System.out.println("7: Manage Tenants");
            System.out.println("8: Exit");

            System.out.print("Select an option: ");
            int option = scanner.nextInt();
            scanner.nextLine(); // consume the rest of the line

            switch (option) {
                case 1:
                    recordVisitData();
                    break;
                case 2:
                    recordLeaseData();
                    break;
                case 3:
                    recordMoveOut();
                    break;
                case 4:
                    addPersonToLease();
                    break;
                case 5:
                    addPetToLease();
                    break;
                case 6:
                    setMoveOutDate();
                    break;
                case 7:
                    manageTenantsMenu();
                    break;
                case 8:
                    System.out.println("Exiting Property Manager Interface.");
                    return;
                default:
                    System.out.println("Invalid option selected. Please try again.");
                    break;
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

            System.out.print("Select an option: ");
            int option = scanner.nextInt();
            scanner.nextLine(); // consume the rest of the line

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
        }
    }

    private void addTenant() {
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

        // Validate email
        String email = "";
        System.out.println("Enter Tenant's Email:");
        while (true) {
            email = scanner.nextLine().trim();
            if (email.contains("@") && email.contains(".")) { // Basic email validation
                break;
            }
            System.out.println("Invalid input. Please enter a valid email:");
        }

        // Validate phone number
        String phoneNumber = "";
        System.out.println("Enter Tenant's Phone Number:");
        while (true) {
            phoneNumber = scanner.nextLine().trim();
            if (phoneNumber.matches("\\d+")) { // Checks if the input is numeric
                break;
            }
            System.out.println("Invalid input. Please enter a valid phone number:");
        }
        
        String sql = "INSERT INTO Tenants (TenantName, Email, PhoneNumber) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.setString(2, email);
            stmt.setString(3, phoneNumber);
            stmt.executeUpdate();
            System.out.println("Person added to tenant successfully.");
        } catch (SQLException e) {
            System.out.println("Error adding person to lease: " + e.getMessage());
        }
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
        System.out.println("Enter details for recording a lease");

        DBTablePrinter.printTable(conn, "Tenants");
        int tenantID = validateTenantID();

        DBTablePrinter.printTable(conn, "Apartments");
        // Validate apartment ID
        System.out.println("Enter Apartment ID for the visit:");
        int aptNumber = 0;
        boolean validApartmentId = false;
        while (!validApartmentId) {
            while (!scanner.hasNextInt()) {
                System.out.println("Invalid input. Please enter a positive integer:");
                scanner.next(); // clear the invalid input
            }
            aptNumber = scanner.nextInt();
            scanner.nextLine(); // consume the rest of the line

            if (aptNumber > 0 && checkApartmentExists(aptNumber)) {
                validApartmentId = true;
            } else {
                System.out.println("No apartment found with the given ID. Please enter a valid Apartment ID.");
            }
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

        System.out.println("Enter Monthly Rent:");
        double monthlyRent = scanner.nextDouble();

        System.out.println("Enter Security Deposit:");
        double securityDeposit = scanner.nextDouble();

        System.out.println("Enter Move-Out Date (YYYY-MM-DD):");
        java.sql.Date moveOutDate = validateAndInputDate();

        String sql = "INSERT INTO Lease (TenantID, AptNumber, LeaseStartDate, LeaseEndDate, MonthlyRent, SecurityDeposit, MoveOutDate) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, tenantID);
            stmt.setInt(2, aptNumber);
            stmt.setDate(3, leaseStartDate);
            stmt.setDate(4, leaseEndDate);
            stmt.setDouble(5, monthlyRent);
            stmt.setDouble(6, securityDeposit);
            stmt.setDate(7, moveOutDate);
            stmt.executeUpdate();
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Lease added successfully.");

                // Add security deposit to dues
                double depositAmount = securityDeposit;
                addSecurityDepositToDues(tenantID, depositAmount);
                System.out.println("Security deposit added to tenant's dues.");
            }
        } catch (SQLException e) {
            System.out.println("Error adding lease: " + e.getMessage());
        }
    }

    public void addSecurityDepositToDues(int tenantId, double securityDeposit) {
        // SQL query to add a security deposit charge to a tenant's dues
        String sql = "INSERT INTO Payments (Amount, PaymentDate, PaymentMethod, TenantID) VALUES (?, ?, ?, ?)";
        String paymentBreakdownSql = "INSERT INTO PaymentBreakdown (PaymentID, Description, Amount) VALUES (?, ?, ?)";

        try {
            // Start a transaction
            conn.setAutoCommit(false);

            // Insert a payment record
            PreparedStatement pstmt = conn.prepareStatement(sql, new String[]{"PaymentID"});
            pstmt.setDouble(1, securityDeposit);
            pstmt.setDate(2, new java.sql.Date(System.currentTimeMillis())); // Use current date or a specific date
            pstmt.setString(3, "Pending"); // Placeholder for the payment method
            pstmt.setInt(4, tenantId);
            pstmt.executeUpdate();

            // Retrieve the generated PaymentID
            ResultSet rs = pstmt.getGeneratedKeys();
            int paymentId = 0;
            if (rs.next()) {
                paymentId = rs.getInt(1);
            }

            // Insert into PaymentBreakdown
            if (paymentId != 0) {
                PreparedStatement pstmtBreakdown = conn.prepareStatement(paymentBreakdownSql);
                pstmtBreakdown.setInt(1, paymentId);
                pstmtBreakdown.setString(2, "Security Deposit");
                pstmtBreakdown.setDouble(3, securityDeposit);
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

    public void setMoveOutDate() {
        DBTablePrinter.printTable(conn, "Lease");
        int leaseID = validateLeaseID();

        System.out.println("Enter Move-Out Date (YYYY-MM-DD):");
        java.sql.Date moveOutDate = validateAndInputDate();

        String sql = "UPDATE Lease SET LeaseEndDate = ? WHERE LeaseID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, moveOutDate);
            stmt.setInt(2, leaseID);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Move-Out recorded successfully.");
            } else {
                System.out.println("No lease found with the provided ID.");
            }
        } catch (SQLException e) {
            System.out.println("Error recording move-out: " + e.getMessage());
        }
    }

    public void addPersonToLease() {
        System.out.println("Enter details to add a person to a lease");

        // Validate and input the Lease ID
        DBTablePrinter.printTable(conn, "Lease");
        int leaseID = validateLeaseID();

        // Validate and input the Tenant ID to be added
        DBTablePrinter.printTable(conn, "Tenants");
        int tenantID = validateTenantID();

        String sql = "UPDATE Lease SET TenantID = ? WHERE LeaseID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, tenantID);
            stmt.setInt(2, leaseID);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Person added to lease successfully.");
            } else {
                System.out.println("No lease found with the provided ID.");
            }
        } catch (SQLException e) {
            System.out.println("Error adding person to lease: " + e.getMessage());
        }
    }

    public void addPetToLease() {
        System.out.println("Enter details to add a pet to a lease");

        System.out.println("Enter Pet's Name:");
        String petName = scanner.nextLine().trim();

        System.out.println("Enter Pet Type:");
        String petType = scanner.nextLine().trim();

        // Validate and input the Tenant ID to associate the pet
        DBTablePrinter.printTable(conn, "Tenants");
        int tenantID = validateTenantID();

        String sql = "INSERT INTO Pets (PetName, PetType, TenantID) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, petName);
            stmt.setString(2, petType);
            stmt.setInt(3, tenantID);
            stmt.executeUpdate();
            System.out.println("Pet added to lease successfully.");
        } catch (SQLException e) {
            System.out.println("Error adding pet to lease: " + e.getMessage());
        }
    }

    public void recordMoveOut() {
        DBTablePrinter.printTable(conn, "Lease");

        // Validate and input the Lease ID
        int leaseID = validateLeaseID();

        System.out.println("Enter Move-Out Date (YYYY-MM-DD):");
        java.sql.Date moveOutDate = validateAndInputDate();

        String sql = "UPDATE Lease SET MoveOutDate = ? WHERE LeaseID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, moveOutDate);
            stmt.setInt(2, leaseID);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Move-Out date recorded successfully.");
            } else {
                System.out.println("No lease found with the provided ID.");
            }
        } catch (SQLException e) {
            System.out.println("Error recording move-out date: " + e.getMessage());
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
        int prospectiveTenantID = 0;
        boolean validProspectiveTenantId = false;
        while (!validProspectiveTenantId) {
            System.out.print("Enter Prospective Tenant ID: ");
            while (!scanner.hasNextInt()) {
                System.out.println("Invalid input. Please enter a positive integer: ");
                scanner.next(); // Clear the invalid input
            }
            prospectiveTenantID = scanner.nextInt();
            scanner.nextLine(); // Consume the rest of the line

            if (prospectiveTenantID > 0 && checkProspectiveTenantExists(prospectiveTenantID)) {
                validProspectiveTenantId = true;
            } else {
                System.out.println("No prospective tenant found with the given ID. Please enter a valid Prospective Tenant ID.");
            }
        }
        return prospectiveTenantID;
    }

    private boolean checkProspectiveTenantExists(int prospectiveTenantId) {
        String sql = "SELECT COUNT(*) FROM ProspectiveTenant WHERE ProspectiveTenantID = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, prospectiveTenantId);
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
