package interfaces;

import db.DBTablePrinter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
            int option = getInputInteger();
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

    // Method to get integer input from user
    private int getInputInteger() {
        while (!scanner.hasNextInt()) {
            System.out.println("Invalid input. Please enter a number:");
            scanner.next(); // consume the invalid input
        }
        int number = scanner.nextInt();
        scanner.nextLine(); // consume the newline character
        return number;
    }

    public void viewPropertyData() {
        DBTablePrinter.printTable(conn, "Property");
        System.out.println("Enter Property ID (enter '0' for all properties):");
        int propertyId = getInputInteger();

        try {
            if (propertyId == 0) {
                // Query for all properties
                String queryAll = "SELECT p.PropertyID, SUM(l.MonthlyRent) AS TotalRent, COUNT(l.AptNumber) AS NumberOfLeases "
                                + "FROM Property p LEFT JOIN Apartments a ON p.PropertyID = a.PropertyID_Ref "
                                + "LEFT JOIN Lease l ON a.AptNumber = l.AptNumber GROUP BY p.PropertyID";
                PreparedStatement stmtAll = conn.prepareStatement(queryAll);
                ResultSet rsAll = stmtAll.executeQuery();

                // Print results in a table format
                DBTablePrinter.printResultSet(rsAll);

            } else {
                // Query for a specific property
                String querySpecific = "SELECT SUM(l.MonthlyRent) AS TotalRent, COUNT(l.AptNumber) AS NumberOfLeases "
                                    + "FROM Apartments a LEFT JOIN Lease l ON a.AptNumber = l.AptNumber "
                                    + "WHERE a.PropertyID_Ref = ?";
                PreparedStatement stmtSpecific = conn.prepareStatement(querySpecific);
                stmtSpecific.setInt(1, propertyId);
                ResultSet rsSpecific = stmtSpecific.executeQuery();

                // Print results in a table format
                DBTablePrinter.printResultSet(rsSpecific);
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