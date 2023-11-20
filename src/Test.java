import db.*;
import interfaces.*;

public class Test {
    public static void main(String[] args) {
        // Create a new instance of DatabaseConnector
        DatabaseConnector dbConnector = new DatabaseConnector();

        // Attempt to establish a connection to the database
        dbConnector.connect();

        // If connection is successful, show the main menu
        dbConnector.showMainMenu();

        // The showMainMenu method includes the logic to exit, so we don't need to call System.exit here
    }
}   