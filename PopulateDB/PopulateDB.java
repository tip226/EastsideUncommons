import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.sql.Types;

import java.util.Scanner;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class PopulateDB {
    static final String DB_URL = "jdbc:oracle:thin:@edgar1.cse.lehigh.edu:1521:cse241";

    public static void main(String[] args) {
        Connection conn = null;
        Scanner in = new Scanner(System.in);
        do {
            try {
                // getting user input for user and password
                System.out.print("Enter Oracle user id: ");
                String user = in.nextLine();
                System.out.print("Enter Oracle password for " + user + ": ");
                String pass = in.nextLine();
                // initialize connection to db
                conn = DriverManager.getConnection(DB_URL, user, pass);
                System.out.println("Super! I'm connected.");

                while(true) {
                    System.out.println("Choose an option:");
                    System.out.println("1: Insert data into Property table from a file");
                    System.out.println("2: Insert data into CommonAmenities table from a file");
                    System.out.println("3: Insert data into Apartments table from a file");
                    System.out.println("4: Insert data into Tenants table from a file");
                    System.out.println("5: Insert data into Payments table from a file");
                    System.out.println("6: Insert data into Pets table from a file");
                    System.out.println("7: Insert data into Property_CommonAmenities table from a file");
                    System.out.println("8: Insert data into PrivateAmenities table from a file");
                    System.out.println("9: Insert data into Apartment_PrivateAmenities table from a file");
                    System.out.println("10: Insert data into Lease table from a file");
                    System.out.println("11: Insert data into ProspectiveTenant table from a file");
                    System.out.println("12: Insert data into FinancialReport table from a file");
                    System.out.println("13: Exit");
                    String choice = in.nextLine();
                    switch (choice) {
                        case "1":
                            System.out.print("Enter the file path for Property data: ");
                            String filePath = in.nextLine();
                            insertIntoPropertyFromFile(conn, filePath);
                            break;
                        case "2":
                            System.out.print("Enter the file path for CommonAmenities data: ");
                            String amenitiesFilePath = in.nextLine();
                            insertIntoCommonAmenitiesFromFile(conn, amenitiesFilePath);
                            break;
                        case "3":
                            System.out.print("Enter the file path for Apartments data: ");
                            String apartmentsFilePath = in.nextLine();
                            insertIntoApartmentsFromFile(conn, apartmentsFilePath);
                            break;
                        case "4":
                            System.out.print("Enter the file path for Tenants data: ");
                            String tenantsFilePath = in.nextLine();
                            insertIntoTenantsFromFile(conn, tenantsFilePath);
                            break;
                        case "5":
                            System.out.print("Enter the file path for Payments data: ");
                            String paymentsFilePath = in.nextLine();
                            insertIntoPaymentsFromFile(conn, paymentsFilePath);
                            break;
                        case "6":
                            System.out.print("Enter the file path for Pets data: ");
                            String petsFilePath = in.nextLine();
                            insertIntoPetsFromFile(conn, petsFilePath);
                            break;
                        case "7":
                            System.out.print("Enter the file path for Property_CommonAmenities data: ");
                            String pcaFilePath = in.nextLine();
                            insertIntoPropertyCommonAmenitiesFromFile(conn, pcaFilePath);
                            break;
                        case "8":
                            System.out.print("Enter the file path for PrivateAmenities data: ");
                            String paFilePath = in.nextLine();
                            insertIntoPrivateAmenitiesFromFile(conn, paFilePath);
                            break;
                        case "9":
                            System.out.print("Enter the file path for Apartment_PrivateAmenities data: ");
                            String apaFilePath = in.nextLine();
                            insertIntoApartmentPrivateAmenitiesFromFile(conn, apaFilePath);
                            break;
                        case "10":
                            System.out.print("Enter the file path for Lease data: ");
                            String leaseFilePath = in.nextLine();
                            insertIntoLeaseFromFile(conn, leaseFilePath);
                            break;
                        case "11":
                            populateProspectiveTenant(conn);
                            break;
                        case "12":
                            populateFinancialReport(conn);
                            break;
                        case "13":
                            conn.close();
                            return;
                        default:
                            System.out.println("Invalid choice");
                            break;
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("[Error]: Connect error. Re-enter login data:");
            }
        } while (conn == null);
    }

    public static void insertIntoPropertyFromFile(Connection conn, String filePath) {
        String sql = "INSERT INTO Property (PropertyID, Street, City, State, ZIPCode) VALUES (?, ?, ?, ?, ?)";
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath));
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            String line;
            // Skip the header line
            reader.readLine();

            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split(", ");
                
                int propertyID = Integer.parseInt(tokens[0].trim());
                String street = tokens[1].trim();
                String city = tokens[2].trim();
                String state = tokens[3].trim();
                String zipCode = tokens[4].trim();

                stmt.setInt(1, propertyID);
                stmt.setString(2, street);
                stmt.setString(3, city);
                stmt.setString(4, state);
                stmt.setString(5, zipCode);

                stmt.executeUpdate();
            }
            System.out.println("Data inserted successfully.");

        } catch (IOException e) {
            System.out.println("Failed to read from the file.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("Failed to insert data.");
            e.printStackTrace();
        }
    }

    public static void insertIntoCommonAmenitiesFromFile(Connection conn, String filePath) {
        String sql = "INSERT INTO CommonAmenities (AmenityID, AmenityName, Cost) VALUES (?, ?, ?)";
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath));
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            String line;
            // Skip the header line
            reader.readLine();

            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split(", ");
                
                int amenityID = Integer.parseInt(tokens[0].trim());
                String amenityName = tokens[1].trim();
                double cost = Double.parseDouble(tokens[2].trim());

                stmt.setInt(1, amenityID);
                stmt.setString(2, amenityName);
                stmt.setDouble(3, cost);

                stmt.executeUpdate();
            }
            System.out.println("Data for CommonAmenities inserted successfully.");

        } catch (IOException e) {
            System.out.println("Failed to read from the file.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("Failed to insert data into CommonAmenities.");
            e.printStackTrace();
        }
    }

    public static void insertIntoApartmentsFromFile(Connection conn, String filePath) {
        String sql = "INSERT INTO Apartments (AptNumber, AptSize, Bedrooms, Bathrooms, MonthlyRent, SecurityDeposit, PropertyID_Ref) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath));
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            String line;
            // Skip the header line
            reader.readLine();

            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split(", ");
                
                int aptNumber = Integer.parseInt(tokens[0].trim());
                double aptSize = Double.parseDouble(tokens[1].trim());
                int bedrooms = Integer.parseInt(tokens[2].trim());
                int bathrooms = Integer.parseInt(tokens[3].trim());
                double monthlyRent = Double.parseDouble(tokens[4].trim());
                double securityDeposit = Double.parseDouble(tokens[5].trim());
                int propertyIDRef = Integer.parseInt(tokens[6].trim());

                stmt.setInt(1, aptNumber);
                stmt.setDouble(2, aptSize);
                stmt.setInt(3, bedrooms);
                stmt.setInt(4, bathrooms);
                stmt.setDouble(5, monthlyRent);
                stmt.setDouble(6, securityDeposit);
                stmt.setInt(7, propertyIDRef);

                stmt.executeUpdate();
            }
            System.out.println("Data for Apartments inserted successfully.");

        } catch (IOException e) {
            System.out.println("Failed to read from the file.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("Failed to insert data into Apartments.");
            e.printStackTrace();
        }
    }

    public static void insertIntoTenantsFromFile(Connection conn, String filePath) {
        String sql = "INSERT INTO Tenants (TenantID, TenantName, Email, PhoneNumber) VALUES (?, ?, ?, ?)";
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath));
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            String line;
            // Skip the header line
            reader.readLine();

            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split(", ");
                
                int tenantID = Integer.parseInt(tokens[0].trim());
                String tenantName = tokens[1].trim();
                String email = tokens[2].trim();
                String phoneNumber = tokens[3].trim();

                stmt.setInt(1, tenantID);
                stmt.setString(2, tenantName);
                stmt.setString(3, email);
                stmt.setString(4, phoneNumber);

                stmt.executeUpdate();
            }
            System.out.println("Data for Tenants inserted successfully.");

        } catch (IOException e) {
            System.out.println("Failed to read from the file.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("Failed to insert data into Tenants.");
            e.printStackTrace();
        }
    }

    public static void insertIntoPaymentsFromFile(Connection conn, String filePath) {
        String sql = "INSERT INTO Payments (PaymentID, Amount, PaymentDate, PaymentMethod, TenantID) VALUES (?, ?, TO_DATE(?, 'YYYY-MM-DD'), ?, ?)";
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath));
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            String line;
            // Skip the header line
            reader.readLine();

            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split(", ");
                
                int paymentID = Integer.parseInt(tokens[0].trim());
                double amount = Double.parseDouble(tokens[1].trim());
                String paymentDate = tokens[2].trim();
                String paymentMethod = tokens[3].trim();
                int tenantID = Integer.parseInt(tokens[4].trim());

                stmt.setInt(1, paymentID);
                stmt.setDouble(2, amount);
                stmt.setString(3, paymentDate);
                stmt.setString(4, paymentMethod);
                stmt.setInt(5, tenantID);

                stmt.executeUpdate();
            }
            System.out.println("Data for Payments inserted successfully.");

        } catch (IOException e) {
            System.out.println("Failed to read from the file.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("Failed to insert data into Payments.");
            e.printStackTrace();
        }
    }

    public static void insertIntoPetsFromFile(Connection conn, String filePath) {
        String sql = "INSERT INTO Pets (PetID, PetName, PetType, TenantID) VALUES (?, ?, ?, ?)";
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath));
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            String line;
            // Skip the header line
            reader.readLine();

            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split(", ");
                
                int petID = Integer.parseInt(tokens[0].trim());
                String petName = tokens[1].trim();
                String petType = tokens[2].trim();
                int tenantID = Integer.parseInt(tokens[3].trim());

                stmt.setInt(1, petID);
                stmt.setString(2, petName);
                stmt.setString(3, petType);
                stmt.setInt(4, tenantID);

                stmt.executeUpdate();
            }
            System.out.println("Data for Pets inserted successfully.");

        } catch (IOException e) {
            System.out.println("Failed to read from the file.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("Failed to insert data into Pets.");
            e.printStackTrace();
        }
    }

    public static void insertIntoPropertyCommonAmenitiesFromFile(Connection conn, String filePath) {
        String sql = "INSERT INTO Property_CommonAmenities (PropertyID, AmenityID) VALUES (?, ?)";
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath));
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            String line;
            // Skip the header line
            reader.readLine();

            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split(", ");
                
                int propertyID = Integer.parseInt(tokens[0].trim());
                int amenityID = Integer.parseInt(tokens[1].trim());

                stmt.setInt(1, propertyID);
                stmt.setInt(2, amenityID);

                stmt.executeUpdate();
            }
            System.out.println("Data for Property_CommonAmenities inserted successfully.");

        } catch (IOException e) {
            System.out.println("Failed to read from the file.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("Failed to insert data into Property_CommonAmenities.");
            e.printStackTrace();
        }
    }

    public static void insertIntoPrivateAmenitiesFromFile(Connection conn, String filePath) {
        String sql = "INSERT INTO PrivateAmenities (PrivateAmenityID, AmenityName, Cost) VALUES (?, ?, ?)";
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath));
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            String line;
            // Skip the header line
            reader.readLine();

            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split(", ");
                
                int privateAmenityID = Integer.parseInt(tokens[0].trim());
                String amenityName = tokens[1].replace("\"", "").trim(); // Removing quotes around the amenity name
                double cost = Double.parseDouble(tokens[2].trim());

                stmt.setInt(1, privateAmenityID);
                stmt.setString(2, amenityName);
                stmt.setDouble(3, cost);

                stmt.executeUpdate();
            }
            System.out.println("Data for PrivateAmenities inserted successfully.");

        } catch (IOException e) {
            System.out.println("Failed to read from the file.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("Failed to insert data into PrivateAmenities.");
            e.printStackTrace();
        }
    }
    public static void insertIntoApartmentPrivateAmenitiesFromFile(Connection conn, String filePath) {
        String sql = "INSERT INTO Apartment_PrivateAmenities (AptNumber, PrivateAmenityID) VALUES (?, ?)";
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath));
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            String line;
            // Skip the header line
            reader.readLine();

            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split(", ");
                
                int aptNumber = Integer.parseInt(tokens[0].trim());
                int privateAmenityID = Integer.parseInt(tokens[1].trim());

                stmt.setInt(1, aptNumber);
                stmt.setInt(2, privateAmenityID);

                stmt.executeUpdate();
            }
            System.out.println("Data for Apartment_PrivateAmenities inserted successfully.");

        } catch (IOException e) {
            System.out.println("Failed to read from the file.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("Failed to insert data into Apartment_PrivateAmenities.");
            e.printStackTrace();
        }
    }

    public static void insertIntoLeaseFromFile(Connection conn, String filePath) {
        String sql = "INSERT INTO Lease (LeaseID, TenantID, AptNumber, LeaseStartDate, LeaseEndDate, MonthlyRent, SecurityDeposit) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath));
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            String line;
            // Skip the header line
            reader.readLine();

            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split(", ");

                int leaseID = Integer.parseInt(tokens[0].trim());
                Integer tenantID = (tokens[1].trim().equalsIgnoreCase("null") || tokens[1].trim().isEmpty()) ? null : Integer.parseInt(tokens[1].trim());
                int aptNumber = Integer.parseInt(tokens[2].trim());
                Date leaseStartDate = Date.valueOf(tokens[3].trim());
                Date leaseEndDate = Date.valueOf(tokens[4].trim());
                double monthlyRent = Double.parseDouble(tokens[5].trim());
                double securityDeposit = Double.parseDouble(tokens[6].trim());

                stmt.setInt(1, leaseID);
                if (tenantID == null) {
                    stmt.setNull(2, Types.INTEGER);
                } else {
                    stmt.setInt(2, tenantID);
                }
                stmt.setInt(3, aptNumber);
                stmt.setDate(4, leaseStartDate);
                stmt.setDate(5, leaseEndDate);
                stmt.setDouble(6, monthlyRent);
                stmt.setDouble(7, securityDeposit);

                stmt.executeUpdate();
            }
            System.out.println("Data for Lease inserted successfully.");

        } catch (IOException e) {
            System.out.println("Failed to read from the file.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("Failed to insert data into Lease.");
            e.printStackTrace();
        }
    }

    private static void populateProspectiveTenant(Connection conn) {
        String filename = "prospectiveTenant.txt";
        String query = "INSERT INTO ProspectiveTenant (ProspectiveTenantID, Name, VisitDate, EligibilityInfo) VALUES (?, ?, ?, ?)";
        
        try (BufferedReader br = new BufferedReader(new FileReader(filename));
            PreparedStatement stmt = conn.prepareStatement(query)) {

            // Skip the header line
            br.readLine();

            String line;
            while ((line = br.readLine()) != null) {
                String[] tokens = line.split("\\|");

                int prospectiveTenantID = Integer.parseInt(tokens[0].trim());
                String name = tokens[1].trim();
                Date visitDate = Date.valueOf(tokens[2].trim());
                String eligibilityInfo = tokens[3].trim();

                stmt.setInt(1, prospectiveTenantID);
                stmt.setString(2, name);
                stmt.setDate(3, visitDate);
                stmt.setString(4, eligibilityInfo);

                stmt.executeUpdate();
            }
            System.out.println("Data for ProspectiveTenant inserted successfully.");
        } catch (IOException e) {
            System.out.println("Failed to read from the file.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("Failed to insert data into Lease.");
            e.printStackTrace();
        }
    }
    private static void populateFinancialReport(Connection conn) {
        String filename = "financialReport.txt";
        String query = "INSERT INTO FinancialReport (ReportID, Year, Revenue, ManagerID) VALUES (?, ?, ?, ?)";
        
        try (BufferedReader br = new BufferedReader(new FileReader(filename));
            PreparedStatement stmt = conn.prepareStatement(query)) {

            // Skip the header line
            br.readLine();

            String line;
            while ((line = br.readLine()) != null) {
                String[] tokens = line.split("\\|");

                int reportID = Integer.parseInt(tokens[0].trim());
                int year = Integer.parseInt(tokens[1].trim());
                double revenue = Double.parseDouble(tokens[2].trim());
                int managerID = Integer.parseInt(tokens[3].trim());

                stmt.setInt(1, reportID);
                stmt.setInt(2, year);
                stmt.setDouble(3, revenue);
                stmt.setInt(4, managerID);

                stmt.executeUpdate();
            }
            System.out.println("Data for FinancialReport inserted successfully.");
        } catch (IOException e) {
            System.out.println("Failed to read from the file.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("Failed to insert data into Lease.");
            e.printStackTrace();
        }
    }
}   