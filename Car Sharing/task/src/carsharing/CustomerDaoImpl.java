package carsharing;

import java.sql.*;
import java.util.Scanner;

import static carsharing.Main.loginOrExit;

public class CustomerDaoImpl implements CustomerDao {

    private static Connection con;
    private static CarDao car;

    public CustomerDaoImpl(Connection con, CarDao car) {
        this.con = con;
        this.car = car;
    }

    public void listCustomers(Scanner scanner, Connection con) throws SQLException {
        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT ID, NAME FROM CUSTOMER");

            if (rs.next()) {
                System.out.println("Customer list:");
                // Iterate through the result set and print the car names on new lines
                do {
                    int id = rs.getInt("ID");
                    String carName = rs.getString("NAME");
                    System.out.println(id + ". " + carName);
                } while (rs.next());
                System.out.println();
            } else {
                System.out.println("The customer list is empty!");
                loginOrExit();
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            // Handle any errors that might occur during the database query
            e.printStackTrace();
        }

        System.out.println("0. Back");
        scanner.nextLine();
        int choice = scanner.nextInt();
        if (choice == 0) {
            loginOrExit();
        } else {
            customerOperations(scanner, choice);
        }
    }

    protected static void customerOperations(Scanner scanner, int customerID) throws SQLException {
        // Get the customer and write:
        // 1. rent a car,
        // 2. return a rented car,
        // 3. my rented car
        // 4. back
        scanner.nextLine();
        int choice = -1;
        while (choice != 0) {
            System.out.println("1. Rent a car");
            System.out.println("2. Return a rented car");
            System.out.println("3. My rented car");
            System.out.println("0. Back");

            if (scanner.hasNextInt()) {
                choice = scanner.nextInt();
                switch (choice) {
                    case 1:
                        // Handle option 1
                        System.out.println("Redirecting to renting a car...");
                        car.rentCar(scanner, customerID);
                        break;
                    case 2:
                        // Handle option 2
                        System.out.println("Redirecting to returning a car...");
                        car.returnCar(customerID);
                        break;
                    case 3:
                        // Handle option 3
                        car.listCar(customerID);
                        break;
                    case 0:
                        // Handle option 0
                        System.out.println("Going back...");
                        loginOrExit();
                        break;
                    default:
                        System.out.println("Invalid option. Please try again.");
                        break;
                }
            } else {
                System.out.println("Invalid input. Please enter a number.");
                scanner.next(); // Consume the invalid input
            }
        }
    }

    public void createCustomer(Scanner scanner, Connection con) {
        scanner.nextLine();
        System.out.println("Enter the customer name:");
        String customerName = scanner.nextLine();
        try {
            // Prepare statement
            PreparedStatement stat = con.prepareStatement("INSERT INTO CUSTOMER (NAME, RENTED_CAR_ID) VALUES (?, ?)");

            // Set parameter values
            stat.setString(1, customerName);
            stat.setString(2, null);

            // Execute statement
            int rowsAffected = stat.executeUpdate();

            // Close connections
            stat.close();

            if (rowsAffected == 1) {
                System.out.println("New customer entry inserted successfully");
            } else {
                System.out.println("Failed to insert new customer entry");
            }
        } catch (SQLException e) {
            // Handle any errors that might occur during the database query
            e.printStackTrace();
        }
    }
}