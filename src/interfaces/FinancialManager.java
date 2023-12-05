package interfaces;

import db.DBTablePrinter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class FinancialManager implements FinancialManagerInterface {
    private Connection conn;
    private Scanner scanner;

    public FinancialManager(Connection conn) {
        this.conn = conn;
        this.scanner = new Scanner(System.in);
    }

    public void showMenu() {
        while(true) {
            System.out.println("Financial Manager Interface");
            System.out.println("1. View Property Data");
            System.out.println("2. View Enterprise Financial Report");
            System.out.println("3. Exit");

            System.out.print("Select an option: ");
            int option = scanner.nextInt();
            scanner.nextLine(); // consume the rest of the line

            switch (option) {
                case 1:
                    viewPropertyData();
                    break;
                case 2:
                    viewEnterpriseFinancialReport();
                    break;
                case 3:
                    System.out.println("Exiting Financial Manager Interface.");
                    return;
                default:
                    System.out.println("Invalid option selected. Please try again.");
                    break;
            }
        }
    }

    public void viewPropertyData() {
        DBTablePrinter.printTable(conn, "Property");
        System.out.println("Enter Property ID (enter '0' for all properties):");
        int propertyId = scanner.nextInt();
        scanner.nextLine(); // Consume the newline

        try {
            if (propertyId == 0) {
                // Query for all properties
                System.out.println("Property Financial Data (All Properties):");
                String queryAll = "SELECT p.PropertyID, SUM(l.MonthlyRent) AS TotalRent, COUNT(l.AptNumber) AS NumberOfLeases "
                                + "FROM Property p LEFT JOIN Apartments a ON p.PropertyID = a.PropertyID_Ref "
                                + "LEFT JOIN Lease l ON a.AptNumber = l.AptNumber GROUP BY p.PropertyID";
                PreparedStatement stmtAll = conn.prepareStatement(queryAll);
                ResultSet rsAll = stmtAll.executeQuery();
                while (rsAll.next()) {
                    System.out.println("Property ID: " + rsAll.getInt("PropertyID") 
                                    + ", Total Rent: " + rsAll.getDouble("TotalRent") 
                                    + ", Number of Leases: " + rsAll.getInt("NumberOfLeases"));
                }
            } else {
                // Query for a specific property
                System.out.println("Property Financial Data (Property ID: " + propertyId + "):");
                String querySpecific = "SELECT SUM(l.MonthlyRent) AS TotalRent, COUNT(l.AptNumber) AS NumberOfLeases "
                                    + "FROM Apartments a LEFT JOIN Lease l ON a.AptNumber = l.AptNumber "
                                    + "WHERE a.PropertyID_Ref = ?";
                PreparedStatement stmtSpecific = conn.prepareStatement(querySpecific);
                stmtSpecific.setInt(1, propertyId);
                ResultSet rsSpecific = stmtSpecific.executeQuery();
                if (rsSpecific.next()) {
                    System.out.println("Total Rent: " + rsSpecific.getDouble("TotalRent") 
                                    + ", Number of Leases: " + rsSpecific.getInt("NumberOfLeases"));
                }
            }
        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
        }
    }

    public void viewEnterpriseFinancialReport() {
        System.out.println("Enterprise Financial Report:");
        try {
            // Total revenue from rents
            String queryRent = "SELECT SUM(MonthlyRent) AS TotalRent FROM Lease";
            PreparedStatement stmtRent = conn.prepareStatement(queryRent);
            ResultSet rsRent = stmtRent.executeQuery();
            double totalRent = 0;
            if (rsRent.next()) {
                totalRent = rsRent.getDouble("TotalRent");
            }

            // Total revenue from amenities
            String queryAmenities = "SELECT SUM(Cost) AS TotalAmenities FROM CommonAmenities WHERE IsAvailable = 'Y'";
            PreparedStatement stmtAmenities = conn.prepareStatement(queryAmenities);
            ResultSet rsAmenities = stmtAmenities.executeQuery();
            double totalAmenities = 0;
            if (rsAmenities.next()) {
                totalAmenities = rsAmenities.getDouble("TotalAmenities");
            }

            // Display the report
            System.out.println("Total Revenue from Rents: $" + totalRent);
            System.out.println("Total Revenue from Amenities: $" + totalAmenities);
            System.out.println("Total Revenue: $" + (totalRent + totalAmenities));
        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
        }
    }

}