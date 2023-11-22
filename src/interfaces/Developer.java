package interfaces;

import db.TablePopulator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class Developer implements DeveloperInterface{
    private Connection conn;
    private Scanner scanner;

    // Predefined file paths for data population
    private static final String PROPERTY_FILE_PATH = "/home/tip226/cse241/EastsideUncommons/resources/textdata/Properties.txt";
    private static final String COMMON_AMENITIES_FILE_PATH = "/home/tip226/cse241/EastsideUncommons/resources/textdata/CommonAmenities.txt";
    private static final String APARTMENTS_FILE_PATH = "/home/tip226/cse241/EastsideUncommons/resources/textdata/Apartments.txt";
    private static final String TENANTS_FILE_PATH = "/home/tip226/cse241/EastsideUncommons/resources/textdata/Tenants.txt";
    private static final String PAYMENTS_FILE_PATH = "/home/tip226/cse241/EastsideUncommons/resources/textdata/Payments.txt";
    private static final String PETS_FILE_PATH = "/home/tip226/cse241/EastsideUncommons/resources/textdata/Pets.txt";
    private static final String PROPERTY_COMMON_AMENITIES_FILE_PATH = "/home/tip226/cse241/EastsideUncommons/resources/textdata/Property_CommonAmenities.txt";
    private static final String PRIVATE_AMENITIES_FILE_PATH = "/home/tip226/cse241/EastsideUncommons/resources/textdata/PrivateAmenities.txt";
    private static final String APARTMENT_PRIVATE_AMENITIES_FILE_PATH = "/home/tip226/cse241/EastsideUncommons/resources/textdata/ApartmentPrivateAmenities.txt";
    private static final String LEASE_FILE_PATH = "/home/tip226/cse241/EastsideUncommons/resources/textdata/LeaseData.txt";
    private static final String PROSPECTIVE_TENANT_FILE_PATH = "/home/tip226/cse241/EastsideUncommons/resources/textdata/ProspectiveTenant.txt";
    private static final String FINANCIAL_REPORT_FILE_PATH = "/home/tip226/cse241/EastsideUncommons/resources/textdata/FinancialReport.txt";

    public Developer(Connection conn) {
        this.conn = conn;
        this.scanner = new Scanner(System.in);
    }

    public void showMenu() {
        System.out.println("Developer Interface");
        while (true) {
            System.out.println("Developer Interface");
            System.out.println("1: Populate Data of A Table");
            System.out.println("2: Automatically Populate All Tables");
            System.out.println("3: Exit");

            System.out.print("Select an option: ");
            int option = scanner.nextInt();
            scanner.nextLine(); // consume the rest of the line

            switch (option) {
                case 1:
                    showPopulateMenu();
                    break;
                case 2:
                    populateAllTables();
                    break;
                case 3:
                    System.out.println("Exiting Developer Interface.");
                    return;
                default:
                    System.out.println("Invalid option selected. Please try again.");
                    break;
            }
        }
    }

    public void showPopulateMenu() {
        while (true) {
            System.out.println("Populate Data Menu");
            System.out.println("1: Populate Property Table");
            System.out.println("2: Populate Common Amenities Table");
            System.out.println("3: Populate Apartments Table");
            System.out.println("4: Populate Tenants Table");
            System.out.println("5: Populate Payments Table");
            System.out.println("6: Populate Pets Table");
            System.out.println("7: Populate Property_CommonAmenities Table");
            System.out.println("8: Populate PrivateAmenities Table");
            System.out.println("9: Populate Apartment_PrivateAmenities Table");
            System.out.println("10: Populate Lease Table");
            System.out.println("11: Populate ProspectiveTenant Table");
            System.out.println("12: Populate FinancialReport Table");
            System.out.println("13: Back to Main Menu");

            System.out.print("Select an option to populate data: ");
            int populateOption = scanner.nextInt();
            scanner.nextLine(); // consume the rest of the line

            if (populateOption == 13) {
                return; // Return to main menu
            }

            String tableName = "";
            switch (populateOption) {
                case 1:
                    tableName = "Property";
                    break;
                case 2:
                    tableName = "CommonAmenities";
                    break;
                case 3:
                    tableName = "Apartments";
                    break;
                case 4:
                    promptAndPopulate("Tenants");
                    break;
                case 5:
                    promptAndPopulate("Payments");
                    break;
                case 6:
                    promptAndPopulate("Pets");
                    break;
                case 7:
                    promptAndPopulate("Property_CommonAmenities");
                    break;
                case 8:
                    promptAndPopulate("PrivateAmenities");
                    break;
                case 9:
                    promptAndPopulate("Apartment_PrivateAmenities");
                    break;
                case 10:
                    promptAndPopulate("Lease");
                    break;
                case 11:
                    promptAndPopulate("ProspectiveTenant");
                    break;
                case 12:
                    promptAndPopulate("FinancialReport");
                    break;
                case 13:
                    System.out.println("Exiting Company Manager Interface.");
                    return;
                default:
                    System.out.println("Invalid option selected. Please try again.");
                    continue;
            }
            promptAndPopulate(tableName);
        }
    }

    public void promptAndPopulate(String tableName) {
        System.out.print("Enter the file path for " + tableName + " data: ");
        String filePath = scanner.nextLine();
        try {
            switch (tableName) {
                case "Property":
                    TablePopulator.populatePropertyTable(conn, filePath);
                    break;
                case "CommonAmenities":
                    TablePopulator.populateCommonAmmenitiesTable(conn, filePath);
                    break;
                case "Apartments":
                    TablePopulator.populateApartmentsTable(conn, filePath);
                    break;
                case "Tenants":
                    TablePopulator.populateTenantsTable(conn, filePath);
                    break;
                case "Payments":
                    TablePopulator.populatePaymentsTable(conn, filePath);
                    break;
                case "Pets":
                    TablePopulator.populatePetsTable(conn, filePath);
                    break;
                case "Property_CommonAmenities":
                    TablePopulator.populatePropertyCommonAmenitiesTable(conn, filePath);
                    break;
                case "PrivateAmenities":
                    TablePopulator.populatePrivateAmenitiesTable(conn, filePath);
                    break;
                case "Apartment_PrivateAmenities":
                    TablePopulator.populateApartmentPrivateAmenitiesTable(conn, filePath);
                    break;
                case "Lease":
                    TablePopulator.populateLeaseTable(conn, filePath);
                    break;
                case "ProspectiveTenant":
                    TablePopulator.populateProspectiveTenantTable(conn, filePath);
                    break;
                case "FinancialReport":
                    TablePopulator.populateFinancialReportTable(conn, filePath);
                    break;
                default:
                    System.out.println("Table name not recognized for population.");
                    break;
            }
        } catch (Exception e) {
            System.out.println("Error occurred while populating " + tableName + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void populateAllTables() {
        boolean success = true;
        try {
            System.out.println("Automatically populating all tables...");

            TablePopulator.populatePropertyTable(conn, PROPERTY_FILE_PATH);
            TablePopulator.populateCommonAmmenitiesTable(conn, COMMON_AMENITIES_FILE_PATH);
            TablePopulator.populateApartmentsTable(conn, APARTMENTS_FILE_PATH);
            TablePopulator.populateTenantsTable(conn, TENANTS_FILE_PATH);
            TablePopulator.populatePaymentsTable(conn, PAYMENTS_FILE_PATH);
            TablePopulator.populatePetsTable(conn, PETS_FILE_PATH);
            TablePopulator.populatePropertyCommonAmenitiesTable(conn, PROPERTY_COMMON_AMENITIES_FILE_PATH);
            TablePopulator.populatePrivateAmenitiesTable(conn, PRIVATE_AMENITIES_FILE_PATH);
            TablePopulator.populateApartmentPrivateAmenitiesTable(conn, APARTMENT_PRIVATE_AMENITIES_FILE_PATH);
            TablePopulator.populateLeaseTable(conn, LEASE_FILE_PATH);
            TablePopulator.populateProspectiveTenantTable(conn, PROSPECTIVE_TENANT_FILE_PATH);
            TablePopulator.populateFinancialReportTable(conn, FINANCIAL_REPORT_FILE_PATH);
        } catch (Exception e) {
            System.out.println("An error occurred while populating tables: " + e.getMessage());
            e.printStackTrace();
            success = false;
        }
        if (success) {
            System.out.println("Successfully populated all tables.");
        } else {
            System.out.println("Failed to populate all tables.");
        }
    }
}