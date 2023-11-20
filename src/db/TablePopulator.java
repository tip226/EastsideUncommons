package db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Date;
import java.sql.Types;

public class TablePopulator {
    public static void populatePropertyTable(Connection conn, String filePath) throws IOException, SQLException {
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
            System.out.println("Failed to insert data into Property.");
            e.printStackTrace();
        }
    }

    public static void populateCommonAmmenitiesTable(Connection conn, String filePath) throws IOException, SQLException {
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

    public static void populateApartmentsTable(Connection conn, String filePath) {
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

    public static void populateTenantsTable(Connection conn, String filePath) {
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

    public static void populatePaymentsTable(Connection conn, String filePath) {
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

    public static void populatePetsTable(Connection conn, String filePath) {
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

    public static void populatePropertyCommonAmenitiesTable(Connection conn, String filePath) {
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

    public static void populatePrivateAmenitiesTable(Connection conn, String filePath) {
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

    public static void populateApartmentPrivateAmenitiesTable(Connection conn, String filePath) {
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

    public static void populateLeaseTable(Connection conn, String filePath) {
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

    public static void populateProspectiveTenantTable(Connection conn, String filePath) {
        String sql = "INSERT INTO ProspectiveTenant (ProspectiveTenantID, Name, VisitDate, EligibilityInfo) VALUES (?, ?, ?, ?)";
        
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath));
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            String line;
            // Skip the header line
            reader.readLine();

            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split(", ");

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
    
    public static void populateFinancialReportTable(Connection conn, String filePath) {
        String sql = "INSERT INTO FinancialReport (ReportID, Year, Revenue, ManagerID) VALUES (?, ?, ?, ?)";
        
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath));
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            String line;
            // Skip the header line
            reader.readLine();

            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split(", ");

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