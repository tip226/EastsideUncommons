package interfaces;

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
        System.out.println("Viewing Property Data");
    }

    public void viewEnterpriseFinancialReport() {
        System.out.println("Viewing Enterprise Financial Report");
    }
}