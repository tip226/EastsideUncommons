package interfaces;

import db.DBTablePrinter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class Tenant implements TenantInterface{
    private Connection conn;
    private Scanner scanner;
    private int tenantId;
    private java.sql.Date paymentDate;
    private int apartmentNumber;

    public Tenant(Connection conn) {
        this.conn = conn;
        this.scanner = new Scanner(System.in);
        this.tenantId = promptForTenantId();
        if (this.tenantId != -1) {
            promptForPaymentDate();
        }
        try {
            this.apartmentNumber = getApartmentNumberForTenant();
            System.out.println("Apartment Number: " + this.apartmentNumber);
        } catch (SQLException e) {
            System.out.println("Error retrieving apartment number: " + e.getMessage());
            // Handle the exception (e.g., set default value, log error, etc.)
            this.apartmentNumber = -1; // Example default value
        }
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

        int tenantId = -1;
        boolean validInput = false;

        while (!validInput) {
            System.out.print("Enter Tenant ID to select: ");
            if (scanner.hasNextInt()) {
                tenantId = scanner.nextInt();
                // Check if the Tenant ID exists in the database
                if (checkTenantExists(tenantId)) {
                    validInput = true; // Break out of the loop if the ID exists
                } else {
                    System.out.println("No tenant found with the given ID. Please enter a valid Tenant ID.");
                }
            } else {
                System.out.println("Invalid input. Please enter a valid Tenant ID.");
                scanner.next(); // Consume the invalid input
            }
        }

        return tenantId;

    } catch (SQLException e) {
        System.out.println("Error fetching tenants: " + e.getMessage());
        return -1; // Indicates an error
    }
}

private boolean checkTenantExists(int tenantId) {
    String query = "SELECT COUNT(*) FROM Tenants WHERE TenantID = ?";
    try (PreparedStatement stmt = conn.prepareStatement(query)) {
        stmt.setInt(1, tenantId);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            return rs.getInt(1) > 0; // Returns true if the count is greater than 0
        }
    } catch (SQLException e) {
        System.out.println("Error checking tenant existence: " + e.getMessage());
    }
    return false; // Default to false if tenant does not exist or an error occurs
}

    private void promptForPaymentDate() {
        System.out.print("Enter the payment date (YYYY-MM-DD): ");
        this.paymentDate = validateAndInputDate();
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
            System.out.println("4: Add Tenant to Lease");
            System.out.println("5: Add Pet to Tenant");
            System.out.println("6: Exit");

            System.out.print("Select an option: ");

            int option = 0;
            boolean validInput = false;

            while (!validInput) {
                if (scanner.hasNextInt()) {
                    option = scanner.nextInt();
                    validInput = true; // Valid integer input
                } else {
                    System.out.println("Invalid input. Please enter a number.");
                    scanner.next(); // Consume the invalid input
                }
            }
            scanner.nextLine(); // Consume the rest of the line after integer input

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
                    addTenantToLease();
                    break;
                case 5:
                    addPetToLease();
                    break;
                case 6:
                    System.out.println("Exiting Tenant Interface.");
                    return;
                default:
                    System.out.println("Invalid option selected. Please try again.");
                    break;
            }
        }
    }

    public double checkPaymentStatus() {
        double totalDue = 0;
        try {
            if (isAfterLeaseEndDate(this.paymentDate)) {
                System.out.println("Lease has ended.");
                showSecurityDepositReturnStatus(apartmentNumber);
            } else if (isBeforeLeaseStartDate(this.paymentDate)) {
                if (hasPaidSecurityDeposit()) {
                    System.out.println("Security deposit has been paid.");
                } else {
                    System.out.println("Payment due for security deposit.");
                    totalDue += showSecurityDepositDetails();
                }
            } else if (hasPaidForMonth(this.paymentDate)) {
                System.out.println("All payments for the month are up to date.");
            } else {
                totalDue += showMonthlyRentAndAmenities();
            }
        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
        }
        return totalDue;
    }

    private boolean isAfterLeaseEndDate(java.sql.Date date) throws SQLException {
        String sql = "SELECT LeaseEndDate FROM Lease WHERE AptNumber = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, apartmentNumber);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                java.sql.Date leaseEndDate = rs.getDate("LeaseEndDate");
                return !date.before(leaseEndDate);
            }
        }
        return false;
    }

    private void showSecurityDepositReturnStatus(int apartmentNumber) throws SQLException {
        String sql = "SELECT SecurityDeposit, DamageAssessed FROM Lease WHERE AptNumber = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, apartmentNumber);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                double securityDeposit = rs.getDouble("SecurityDeposit");
                String damageAssessed = rs.getString("DamageAssessed");
                if ("Y".equals(damageAssessed)) {
                    System.out.println("Security deposit of $" + securityDeposit + " will not be returned due to assessed damages.");
                } else {
                    System.out.println("Security deposit of $" + securityDeposit + " will be returned.");
                }
            }
        }
    }

    private boolean isBeforeLeaseStartDate(java.sql.Date date) throws SQLException {
        String sql = "SELECT LeaseStartDate FROM Lease WHERE AptNumber = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, apartmentNumber);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                java.sql.Date leaseStartDate = rs.getDate("LeaseStartDate");
                return date.before(leaseStartDate);
            }
        }
        return false;
    }

    private boolean hasPaidForMonth(java.sql.Date date) throws SQLException {
        String sql = "SELECT PaymentDate FROM Payments WHERE TenantID = ? " +
                    "AND EXTRACT(MONTH FROM PaymentDate) = EXTRACT(MONTH FROM ?) " +
                    "AND EXTRACT(YEAR FROM PaymentDate) = EXTRACT(YEAR FROM ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, tenantId);
            stmt.setDate(2, date);
            stmt.setDate(3, date);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        }
    }

    private boolean hasPaidSecurityDeposit() throws SQLException {
    // Query to get the lease start date and security deposit amount
    String leaseInfoSql = "SELECT LeaseStartDate, SecurityDeposit FROM Lease WHERE AptNumber = ?";
    java.sql.Date leaseStartDate = null;
    double securityDepositAmount = 0.0;

    try (PreparedStatement leaseInfoStmt = conn.prepareStatement(leaseInfoSql)) {
        leaseInfoStmt.setInt(1, apartmentNumber);
        ResultSet leaseInfoRs = leaseInfoStmt.executeQuery();
        if (leaseInfoRs.next()) {
            leaseStartDate = leaseInfoRs.getDate("LeaseStartDate");
            securityDepositAmount = leaseInfoRs.getDouble("SecurityDeposit");

            // Debug print
            System.out.println("Lease Start Date: " + leaseStartDate);
            System.out.println("Security Deposit Amount: " + securityDepositAmount);
        }
    }

    // If lease start date or security deposit amount is not found, return false
    if (leaseStartDate == null || securityDepositAmount == 0.0) {
        return false;
    }

    // Query to check if there's a payment for the security deposit before the lease start date
    String paymentCheckSql = "SELECT 1 FROM Payments WHERE TenantID = ? AND PaymentDate <= ? AND Amount = ?";
    try (PreparedStatement paymentCheckStmt = conn.prepareStatement(paymentCheckSql)) {
        paymentCheckStmt.setInt(1, tenantId);
        paymentCheckStmt.setDate(2, leaseStartDate);
        paymentCheckStmt.setDouble(3, securityDepositAmount);
        ResultSet paymentCheckRs = paymentCheckStmt.executeQuery();

        // Debug print
        System.out.println("Query executed to check payment: " + paymentCheckSql);
        System.out.println("TenantID: " + tenantId);
        System.out.println("LeaseStartDate (for comparison): " + leaseStartDate);
        System.out.println("SecurityDepositAmount (for comparison): " + securityDepositAmount);

        return paymentCheckRs.next();
    }
}

    private double showSecurityDepositDetails() throws SQLException {
        String sql = "SELECT SecurityDeposit, AptNumber FROM Lease WHERE AptNumber = ?";
        double totalSecurityDeposit = 0;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, apartmentNumber);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                totalSecurityDeposit = rs.getDouble("SecurityDeposit");
            }
        }

        // Display breakdown
        System.out.println("Breakdown of Security Deposit:");
        System.out.println(String.format("%-30s %s", "Item", "Amount"));
        System.out.println("----------------------------------------");

        // Show base security deposit amount
        System.out.println(String.format("%-30s %.2f", "Base Security Deposit", totalSecurityDeposit));

        // Show one-time amenity costs and add them to the total security deposit
        double oneTimeAmenityCosts = getOneTimeAmenityCosts(apartmentNumber);
        totalSecurityDeposit += oneTimeAmenityCosts;

        System.out.println("----------------------------------------");
        System.out.println(String.format("%-30s %.2f", "Total Security Deposit Due", totalSecurityDeposit));

        return totalSecurityDeposit;
    }

    private double getOneTimeAmenityCosts(int apartmentNumber) throws SQLException {
        double oneTimeCosts = 0;
        String sql = "SELECT Cost, AmenityName FROM PrivateAmenities pa " +
                    "JOIN Apartment_PrivateAmenities apa ON pa.AmenityID = apa.PrivateAmenityID " +
                    "WHERE apa.AptNumber = ? AND pa.CostType = 'One-time'";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, apartmentNumber);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String amenityName = rs.getString("AmenityName");
                double cost = rs.getDouble("Cost");
                System.out.println(String.format("%-30s %.2f", amenityName, cost));
                oneTimeCosts += cost;
            }
        }
        return oneTimeCosts;
    }

    private double showMonthlyRentAndAmenities() throws SQLException {
        double totalDue = 0;
        System.out.println("Breakdown of Monthly Charges:");
        System.out.println(String.format("%-30s %s", "Item", "Amount"));
        System.out.println("----------------------------------------");

        // Show Monthly Rent
        double monthlyRent = getMonthlyRent();
        System.out.println(String.format("%-30s %.2f", "Monthly Rent", monthlyRent));
        totalDue += monthlyRent;

        // Show Common Amenity Costs
        totalDue += showAmenityCosts("CommonAmenities", "Property_CommonAmenities", "PropertyID", getPropertyIdForTenant(), "AmenityID");

        // Show Private Amenity Costs
        totalDue += showAmenityCosts("PrivateAmenities", "Apartment_PrivateAmenities", "AptNumber", getApartmentNumberForTenant(), "PrivateAmenityID");

        System.out.println("----------------------------------------");
        System.out.println(String.format("%-30s %.2f", "Total Monthly Due", totalDue));

        return totalDue;
    }

    private double showAmenityCosts(String amenityTable, String junctionTable, String joinColumn, int id, String amenityColumn) throws SQLException {
        double totalAmenityCost = 0;
        String sql = "SELECT pa.Cost, pa.AmenityName FROM " + amenityTable + " pa " +
                    "JOIN " + junctionTable + " jpa ON pa.AmenityID = jpa." + amenityColumn + " " +
                    "WHERE jpa." + joinColumn + " = ? AND pa.CostType = 'Monthly'";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String amenityName = rs.getString("AmenityName");
                double cost = rs.getDouble("Cost");
                System.out.println(String.format("%-30s %.2f", amenityName, cost));
                totalAmenityCost += cost;
            }
        }
        return totalAmenityCost;
    }

    private int getApartmentNumberForTenant() throws SQLException {
        String sql = "SELECT AptNumber FROM Lease WHERE LeaseID IN (SELECT LeaseID FROM LeaseTenants WHERE TenantID = ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, tenantId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("AptNumber");
            }
        }
        return -1; // Indicates no apartment number found for this tenant
    }

    private double getMonthlyRent() throws SQLException {
        String rentSql = "SELECT MonthlyRent FROM Lease WHERE AptNumber = ?";
        try (PreparedStatement rentStmt = conn.prepareStatement(rentSql)) {
            rentStmt.setInt(1, apartmentNumber);
            ResultSet rentRs = rentStmt.executeQuery();
            if (rentRs.next()) {
                return rentRs.getDouble("MonthlyRent");
            }
        }
        return 0.0;
    }

    private String getAmenityName(int amenityId) throws SQLException {
        String sql = "SELECT AmenityName FROM CommonAmenities WHERE AmenityID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, amenityId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("AmenityName");
            }
        }
        return "Unknown Amenity";
    }

    private double getAmenityCost(int amenityId) throws SQLException {
        String sql = "SELECT Cost FROM CommonAmenities WHERE AmenityID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, amenityId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("Cost");
            }
        }
        return 0;
    }

    // Method to charge tenant for all common amenities of their property
    public void chargeForAmenities() {
        int propertyId = getPropertyIdForTenant();

        if (propertyId != -1) {
            List<Integer> amenityIds = getAmenityIdsForProperty(propertyId);
            for (int amenityId : amenityIds) {
                addAmenityChargeToDues(amenityId, true);
            }
        } else {
            System.out.println("No property found for tenant.");
        }
    }

    private List<Integer> getAmenityIdsForProperty(int propertyId) {
        List<Integer> amenityIds = new ArrayList<>();
        String query = "SELECT AmenityID FROM Property_CommonAmenities WHERE PropertyID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, propertyId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                amenityIds.add(rs.getInt("AmenityID"));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching Amenity IDs: " + e.getMessage());
        }
        return amenityIds;
    }

    private int getPropertyIdForTenant() {
    String query = "SELECT p.PropertyID FROM Property p " +
                   "JOIN Apartments a ON p.PropertyID = a.PropertyID_Ref " +
                   "JOIN Lease l ON a.AptNumber = l.AptNumber " +
                   "JOIN LeaseTenants lt ON l.LeaseID = lt.LeaseID " +
                   "WHERE lt.TenantID = ?";
    try (PreparedStatement stmt = conn.prepareStatement(query)) {
        stmt.setInt(1, tenantId);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            return rs.getInt("PropertyID");
        }
    } catch (SQLException e) {
        System.out.println("Error fetching Property ID: " + e.getMessage());
    }
    return -1; // Indicates an error or not found
}

    public void addAmenityChargeToDues(int amenityId, boolean isCommonAmenity) {
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

    public void makeRentalPayment() {
    try {
        // First, check what payments are due and calculate the total
        double totalDue = checkPaymentStatus();
        if (totalDue > 0) {
            System.out.println("Total amount due: $" + totalDue);
            System.out.println("Do you want to proceed with the payment? (Y/N)");
            String confirmation = scanner.nextLine().trim().toUpperCase();
            if ("Y".equals(confirmation)) {
                System.out.println("Please select your payment method:");
        System.out.println("1: Credit/Debit");
        System.out.println("2: Cash");
        System.out.println("3: Check");
        System.out.println("4: Venmo");
        System.out.println("5: Zelle");
        System.out.println("6: Bank Transfer");
        System.out.print("Enter your choice (1-6): ");
        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        String paymentMethod;
        switch (choice) {
            case 1:
                paymentMethod = "Credit/Debit";
                break;
            case 2:
                paymentMethod = "Cash";
                break;
            case 3:
                paymentMethod = "Check";
                break;
            case 4:
                paymentMethod = "Venmo";
                break;
            case 5:
                paymentMethod = "Zelle";
                break;
            case 6:
                paymentMethod = "Bank Transfer";
                break;
            default:
                System.out.println("Invalid choice. Defaulting to Credit/Debit.");
                paymentMethod = "Credit/Debit";
        }

        processPayment(totalDue, paymentMethod);
        System.out.println("Payment of $" + totalDue + " made successfully via " + paymentMethod + ".");
            } else {
                System.out.println("Payment cancelled.");
            }
        } else {
            System.out.println("No payment is due at this time.");
        }
    } catch (SQLException e) {
        System.out.println("SQL Error: " + e.getMessage());
    }
}

private void processPayment(double amount, String paymentMethod) throws SQLException {
    String sql = "INSERT INTO Payments (Amount, PaymentDate, PaymentMethod, TenantID) VALUES (?, ?, ?, ?)";
    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setDouble(1, amount);
        stmt.setDate(2, paymentDate);
        stmt.setString(3, paymentMethod);
        stmt.setInt(4, tenantId);
        stmt.executeUpdate();
    }
}

    private double calculateTotalDuesUpToDate(int tenantId, java.sql.Date upToDate) {
        double totalDue = 0;
        String dueSql = 
            "SELECT SUM(pb.Amount) AS TotalDue " +
            "FROM PaymentBreakdown pb JOIN Payments p ON pb.PaymentID = p.PaymentID " +
            "WHERE p.TenantID = ? AND (p.PaymentDate IS NULL OR p.PaymentDate > ?)";

        try {
            PreparedStatement pstmtDue = conn.prepareStatement(dueSql);
            pstmtDue.setInt(1, tenantId);
            pstmtDue.setDate(2, upToDate);
            ResultSet rsDue = pstmtDue.executeQuery();

            if (rsDue.next()) {
                totalDue = rsDue.getDouble("TotalDue");
            }
        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
        }

        return totalDue;
    }

    private void applyPayment(int tenantId, double amount, Date paymentDate, String paymentMethod) {
        // This method applies the payment amount to the oldest dues first
        String getOldestDueSql = 
            "SELECT PaymentID, Amount FROM Payments " +
            "WHERE TenantID = ? AND (PaymentDate IS NULL OR PaymentMethod = 'Pending') " +
            "ORDER BY PaymentID ASC"; // Assuming PaymentID is sequential

        try {
            PreparedStatement pstmt = conn.prepareStatement(getOldestDueSql);
            pstmt.setInt(1, tenantId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next() && amount > 0) {
                int paymentId = rs.getInt("PaymentID");
                double dueAmount = rs.getDouble("Amount");

                // Determine the amount to be applied to this due
                double amountToApply = Math.min(dueAmount, amount);

                // Update the payment record
                updatePaymentRecord(paymentId, amountToApply, paymentDate, paymentMethod);

                // Subtract the applied amount from the total payment amount
                amount -= amountToApply;
            }

            if (amount > 0) {
                System.out.println("Excess amount paid: " + amount + ". Please contact the management for adjustment.");
            }
        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
        }
    }

    private void updatePaymentRecord(int paymentId, double amount, Date paymentDate, String paymentMethod) {
        // Update the payment record with the amount, date, and method
        String updateSql = 
            "UPDATE Payments SET Amount = Amount - ?, PaymentDate = ?, PaymentMethod = ? " +
            "WHERE PaymentID = ? AND TenantID = ?";

        try {
            PreparedStatement pstmtUpdate = conn.prepareStatement(updateSql);
            pstmtUpdate.setDouble(1, amount);
            pstmtUpdate.setDate(2, paymentDate);
            pstmtUpdate.setString(3, paymentMethod);
            pstmtUpdate.setInt(4, paymentId);
            pstmtUpdate.setInt(5, tenantId);

            int rowsAffected = pstmtUpdate.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Payment of " + amount + " applied to Payment ID " + paymentId);
            }
        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
        }
    }

    public void updatePersonalData() {
        // Validate name
        String name = "";
        System.out.println("Enter Updated Tenant's Full Name (First and Last Name):");
        while (true) {
            name = scanner.nextLine().trim();
            if (name.split("\\s+").length >= 2) { // Checks if there are at least two words
                break;
            }
            System.out.println("Invalid input. Please enter both first and last name:");
        }

        System.out.println("Enter Updated Tenant's Email:");
        String email = "";
        while (true) {
            email = scanner.nextLine().trim();
            if (email.contains("@") && email.contains(".")) { // Basic email validation
                break;
            }
            System.out.println("Invalid input. Please enter a valid email:");
        }

        String phoneNumber = "";
        System.out.println("Enter Updated Tenant's Phone Number (10 digits):");
        while (true) {
            phoneNumber = scanner.nextLine().trim();
            if (phoneNumber.matches("\\d{10}")) { // Checks if the input is numeric
                break;
            }
            System.out.println("Invalid input. Please enter a valid 10-digit phone number:");
        }

        String sql = "UPDATE Tenants SET TenantName = ?, Email = ?, PhoneNumber = ? WHERE TenantID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.setString(2, email);
            stmt.setString(3, phoneNumber);
            stmt.setInt(4, tenantId);
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

    public void addTenantToLease() {
        try {
            int leaseID = getLeaseIDForTenant();
            int aptNumber = getApartmentNumberForLease(leaseID);

            int currentOccupants = getCurrentOccupantsCount(leaseID);
            int maxOccupants = getMaxOccupants(aptNumber);

            if (currentOccupants < maxOccupants) {
                addPersonToLease(leaseID); // Method to add a person to the lease
            } else {
                System.out.println("Cannot add more tenants. The apartment is at full capacity.");
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void addPersonToLease(int leaseID) throws SQLException {
        int tenantID = -1;
        while (tenantID == -1) {
            System.out.println("Do you want to add a tenant manually (M) or select from prospective tenants (P)?");
            String choice = scanner.nextLine().trim().toUpperCase();

            if ("P".equals(choice)) {
                tenantID = selectProspectiveTenant();
            } else if ("M".equals(choice)) {
                tenantID = addTenantManually();
            } else {
                System.out.println("Invalid choice. Please choose 'M' for manually or 'P' for prospective tenants.");
                // Continue the loop if invalid choice
                continue;
            }

            String sql = "INSERT INTO LeaseTenants (LeaseID, TenantID) VALUES (?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, leaseID);
                stmt.setInt(2, tenantID);
                stmt.executeUpdate();
                System.out.println("Tenant added to lease successfully.");
            }
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
        String email = "";
        while (true) {
            email = scanner.nextLine().trim();
            if (email.contains("@") && email.contains(".")) { // Basic email validation
                break;
            }
            System.out.println("Invalid input. Please enter a valid email:");
        }

        String phoneNumber = "";
        System.out.println("Enter Tenant's Phone Number (10 digits):");
        while (true) {
            phoneNumber = scanner.nextLine().trim();
            if (phoneNumber.matches("\\d{10}")) { // Checks if the input is numeric
                break;
            }
            System.out.println("Invalid input. Please enter a valid 10-digit phone number:");
        }

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

    public int getApartmentNumberForLease(int leaseID) throws SQLException {
        String sql = "SELECT AptNumber FROM Lease WHERE LeaseID = ?";
        int aptNumber = -1;
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, leaseID);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                aptNumber = rs.getInt("AptNumber");
            }
        }
        return aptNumber; // Returns the apartment number or -1 if not found
    }

    public int getCurrentOccupantsCount(int leaseID) throws SQLException {
        String sql = "SELECT COUNT(*) as OccupantCount FROM LeaseTenants WHERE LeaseID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, leaseID);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("OccupantCount");
            }
        }
        return 0; // Default to 0 if no data is found
    }

    public int getMaxOccupants(int aptNumber) throws SQLException {
        String sql = "SELECT Bedrooms FROM Apartments WHERE AptNumber = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, aptNumber);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("Bedrooms");
            }
        }
        return 0; // Default to 0 if no data is found
    }

    private int getLeaseIDForTenant() throws SQLException {
        String sql = "SELECT LeaseID FROM LeaseTenants WHERE TenantID = ?";
        int leaseID = -1;
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, tenantId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                leaseID = rs.getInt("LeaseID");
            }
        } catch (SQLException e) {
            System.out.println("Error fetching Lease ID: " + e.getMessage());
        }
        return leaseID;
    }
    public void addPetToLease() {
        try {
            int leaseID = getLeaseIDForTenant();
            if (leaseID == -1) {
                System.out.println("Lease ID not found for the tenant.");
                return;
            }

            System.out.println("Enter Pet's Name:");
            String petName = scanner.nextLine().trim();

            System.out.println("Enter Pet Type:");
            String petType = scanner.nextLine().trim();

            String sql = "INSERT INTO Pets (PetName, PetType, TenantID) VALUES (?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, petName);
                stmt.setString(2, petType);
                stmt.setInt(3, tenantId);
                stmt.executeUpdate();
                System.out.println("Pet added to lease successfully.");
            }
        } catch (SQLException e) {
            System.out.println("Error adding pet to lease: " + e.getMessage());
        }
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
}