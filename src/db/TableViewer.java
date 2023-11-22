package db;

import java.sql.*;

public class TableViewer {

    public void displayTable(Connection conn, String tableName) {
        try (Statement stmt = conn.createStatement()) {
            String query = "SELECT * FROM " + tableName;
            ResultSet rs = stmt.executeQuery(query);
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();

            // Display column headers
            for (int i = 1; i <= columnCount; i++) {
                System.out.print(rsmd.getColumnName(i) + "\t\t");
            }
            System.out.println();

            // Display data rows
            while (rs.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    System.out.print(rs.getString(i) + "\t\t");
                }
                System.out.println();
            }
        } catch (SQLException e) {
            System.err.println("Error displaying table " + tableName + ": " + e.getMessage());
        }
    }
}
