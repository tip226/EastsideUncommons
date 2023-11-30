package interfaces;

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

    public Tenant(Connection conn) {
        this.conn = conn;
        this.scanner = new Scanner(System.in);
        this.tenantId = promptForTenantId();
        if (this.tenantId != -1) {
            promptForPaymentDate();
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

            System.out.print("Enter Tenant ID to select: ");
            return scanner.nextInt();
        } catch (SQLException e) {
            System.out.println("Error fetching tenants: " + e.getMessage());
            return -1; // Indicates an error
        }
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
        chargeForAmenities();
        // Query to get detailed payment breakdown
        String paymentDetailsSql = 
            "SELECT p.PaymentID, p.PaymentDate, p.PaymentMethod, pb.Description, pb.Amount " +
            "FROM Payments p " +
            "JOIN PaymentBreakdown pb ON p.PaymentID = pb.PaymentID " +
            "WHERE p.TenantID = ? AND (p.PaymentDate IS NULL OR p.PaymentMethod = 'Pending') " +
            "ORDER BY p.PaymentID";

        try {
            PreparedStatement pstmt = conn.prepareStatement(paymentDetailsSql);
            pstmt.setInt(1, tenantId);
            ResultSet rs = pstmt.executeQuery();

            boolean hasPendingPayments = false;
            System.out.println("Detailed Payment Status for Tenant ID: " + tenantId);
            System.out.println("PaymentID | Amount | Payment Date | Payment Method | Description");
            while (rs.next()) {
                int paymentId = rs.getInt("PaymentID");
                double amount = rs.getDouble("Amount");
                Date paymentDate = rs.getDate("PaymentDate");
                String paymentMethod = rs.getString("PaymentMethod");
                String description = rs.getString("Description");

                System.out.printf("%d | %s | %s | %s | %s\n", paymentId, amount, paymentDate, paymentMethod, description);
                hasPendingPayments = true;
            }

            if (!hasPendingPayments) {
                System.out.println("No pending payments for this tenant.");
            }
        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
        }
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
                       "WHERE l.TenantID = ?";
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
        // Prompt for payment date
        System.out.print("Enter the payment date (YYYY-MM-DD): ");
        java.sql.Date paymentDate = validateAndInputDate();

        // Calculate total dues up to the entered date
        double totalDueAmount = calculateTotalDuesUpToDate(tenantId, paymentDate);
        System.out.println("Total due amount up to " + paymentDate + ": " + totalDueAmount);

        // Prompt for payment amount
        System.out.print("Enter the amount you want to pay: ");
        double paymentAmount = scanner.nextDouble();
        scanner.nextLine(); // consume the rest of the line

        // Display payment methods and prompt for selection
        System.out.println("Payment Methods: Credit/Debit, Cash, Check, Venmo, Zelle, Bank Transfer");
        System.out.print("Enter your payment method: ");
        String paymentMethod = scanner.nextLine();

        // Apply the payment
        applyPayment(tenantId, paymentAmount, paymentDate, paymentMethod);
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