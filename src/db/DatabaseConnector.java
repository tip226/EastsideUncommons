package db;

import interfaces.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Scanner;
import java.io.Console;

public class DatabaseConnector {
    private static final String DB_URL = "jdbc:oracle:thin:@edgar1.cse.lehigh.edu:1521:cse241";
    private Connection conn;
    private Scanner scanner;

    public DatabaseConnector() {
        this.scanner = new Scanner(System.in);
    }

    public Connection connect() {
        Console console = System.console();
        if (console == null) {
            System.out.println("No console available");
            return null;
        }

        while (true) {
            try {
                System.out.print("Enter Oracle user id (or 'exit' to quit): ");
                String user = scanner.nextLine();

                // Allow user to exit the loop
                if ("exit".equalsIgnoreCase(user)) {
                    System.out.println("Exiting the connection setup.");
                    return null;
                }

                char[] passArray = console.readPassword("Enter Oracle password for " + user + ": ");
                String pass = new String(passArray);

                conn = DriverManager.getConnection(DB_URL, user, pass);
                System.out.println("Super! I'm connected.");
                return conn;
            } catch (SQLException e) {
                System.out.println("[Error]: Invalid username/password or connection error. Please try again.");
            }
        }
    }

    public void showMainMenu() {
        if (conn == null) {
            System.out.println("You must connect to the database first.");
            return;
        }

        try {
            while (true) {
                System.out.println("Choose an interface:");
                System.out.println("1: Property Manager Interface");
                System.out.println("2: Tenant Interface");
                System.out.println("3: Company Manager Interface");
                System.out.println("4: Financial Manager Interface");
                System.out.println("5: Developer Interface");
                System.out.println("6: Exit");

                String choice = scanner.nextLine();
                switch (choice) {
                    case "1":
                        PropertyManagerInterface propertyManager = new PropertyManager(conn);
                        propertyManager.showMenu();
                        break;
                    case "2":
                        TenantInterface tenantInterface = new Tenant(conn);
                        tenantInterface.showMenu();
                        break;
                    case "3":
                        CompanyManagerInterface companyManager = new CompanyManager(conn);
                        companyManager.showMenu();
                        break;
                    case "4":
                        FinancialManagerInterface financialManager = new FinancialManager(conn);
                        financialManager.showMenu();
                        break;
                    case "5":
                        DeveloperInterface developer = new Developer(conn);
                        developer.showMenu();
                        break;
                    case "6":
                        System.out.println("Exiting the application.");
                        conn.close(); // Close the connection before exiting
                        return;
                    default:
                        System.out.println("Invalid choice. Please enter a number between 1 and 6.");
                        break;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error closing connection: " + e.getMessage());
        }

        
    }

}