package carsharing;

import java.sql.*;
import java.util.Scanner;

import static carsharing.Main.loginOrExit;

public class ManagerDaoImpl {

    private static CarDao car;
    private static Connection con;

    public ManagerDaoImpl(CarDao car, Connection con) {
        this.car = car;
        this.con = con;
    }

    public static void managerActions(Scanner scanner) throws SQLException {
        int choice = -1;
        while (choice != 0) {
            System.out.println("1. Company list");
            System.out.println("2. Create a company");
            System.out.println("0. Back");

            if (scanner.hasNextInt()) {
                choice = scanner.nextInt();
                switch (choice) {
                    case 1:
                        // Handle: 1. Company list
                        System.out.println("Listing companies...");
                        listCompanies(scanner);
                        break;
                    case 2:
                        // Handle: 2. Create a company
                        System.out.println("Redirecting to creating company...");
                        createCompany(scanner);
                        break;
                    case 0:
                        // Handle: 0. Back
                        System.out.println("Going back...");
                        loginOrExit();
                        break;
                    default:
                        // Handle invalid int input from the user
                        System.out.println("Invalid option. Please try again.");
                        break;
                }
            } else {
                System.out.println("Invalid input. Please enter a number.");
                scanner.next(); // Consume the invalid input
            }
        }
    }

    private static void listCompanies(Scanner scanner) throws SQLException {
        Statement stmt = con.createStatement();

        // Execute query
        ResultSet rs = stmt.executeQuery("SELECT ID, NAME FROM COMPANY ORDER BY ID");
        if (!rs.next()) {
            // ResultSet is empty
            System.out.println("The company list is empty");
            // Going back to the manager activities
            managerActions(scanner);
        } else {
            // ResultSet is not empty
            System.out.println("Choose a company:");
            do {
                int id = rs.getInt("ID");
                String name = rs.getString("NAME");
                System.out.println(id + ". " + name);
            } while (rs.next());
        }

        System.out.println("0. Back");
        scanner.nextLine();
        int choice = scanner.nextInt();
        if (choice == 0) {
            managerActions(scanner);
        } else {
            car.carOperations(scanner, choice);
        }
    }

    private static void createCompany(Scanner scanner) throws SQLException {
        scanner.nextLine(); // Clear any previously buffered input
        System.out.print("Enter the company name:");
        String companyName = scanner.nextLine();
        insertIntoTable(companyName);
    }

    private static void insertIntoTable(String companyName) throws SQLException {
        // Prepare statement
        PreparedStatement stat = con.prepareStatement("INSERT INTO COMPANY (NAME) VALUES (?)");

        // Set parameter values
        stat.setString(1, companyName);

        // Execute statement
        stat.executeUpdate();

        // Close connections
        stat.close();
    }
}