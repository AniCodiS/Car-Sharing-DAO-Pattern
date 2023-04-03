package carsharing;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import static carsharing.CustomerDaoImpl.customerOperations;
import static carsharing.ManagerDaoImpl.managerActions;

public class CarDaoImpl implements CarDao {

    private Connection con;
    private Scanner scanner;

    public CarDaoImpl(Connection con, Scanner scanner) {
        this.con = con;
        this.scanner = scanner;
    }

    public void carOperations(Scanner scanner, int companyID) {
        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT NAME FROM COMPANY WHERE ID = " + companyID);

            if (rs.next()) {
                // Getting name of the company with companyID
                String companyName = rs.getString("NAME");
                System.out.println(companyName + " company");
                handleCars(scanner, companyID);
            } else {
                // No company found with the given ID
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            // Handle any errors that might occur during the database query
            e.printStackTrace();
        }
    }

    private void handleCars(Scanner scanner, int companyID) {
        scanner.nextLine();

        int choice = -1;
        while (choice != 0) {
            System.out.println("1. Car list");
            System.out.println("2. Create a car");
            System.out.println("0. Back");

            if (scanner.hasNextInt()) {
                choice = scanner.nextInt();
                switch (choice) {
                    case 1:
                        // Handle: 1. Car list
                        System.out.println("Listing cars...");
                        listCars(companyID);
                        break;
                    case 2:
                        // Handle: 2. Create a car
                        System.out.println("Redirecting to creating car...");
                        createCar(scanner, companyID);
                        break;
                    case 0:
                        // Handle: 0. Back
                        System.out.println("Going back...");
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

    private void listCars(int companyID) {
        System.out.println("Car list:");

        int counter = 1;
        try {
            Statement stmt = con.createStatement();
            // Getting all the cars this company owns
            ResultSet rs = stmt.executeQuery("SELECT NAME FROM CAR WHERE COMPANY_ID = " + companyID);

            if (rs.next()) {
                // Iterate through the result set and print the car names on new lines
                do {
                    String carName = rs.getString("NAME");
                    System.out.println(counter + ". " + carName);
                    counter++;
                } while (rs.next());
                System.out.println();
            } else {
                System.out.println("The car list is empty!");
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            // Handle any errors that might occur during the database query
            e.printStackTrace();
        }
    }

    private void createCar(Scanner scanner, int companyID) {
        scanner.nextLine();
        System.out.println("Enter the car name:");
        String carName = scanner.nextLine();
        try {
            // Prepare statement
            PreparedStatement stat = con.prepareStatement("INSERT INTO CAR (NAME, COMPANY_ID) VALUES (?, ?)");

            // Set parameter values
            stat.setString(1, carName);
            stat.setString(2, Integer.toString(companyID));

            // Execute statement
            int rowsAffected = stat.executeUpdate();

            // Close connections
            stat.close();

            if (rowsAffected == 1) {
                // Checking if the insertion to the table was successful
                System.out.println("New car entry inserted successfully");
            } else {
                System.out.println("Failed to insert new car entry");
            }
        } catch (SQLException e) {
            // Handle any errors that might occur during the database query
            e.printStackTrace();
        }
    }

    public void rentCar(Scanner scanner, int customerID) throws SQLException {
        String sql = "SELECT RENTED_CAR_ID FROM CUSTOMER WHERE ID = ?";
        PreparedStatement statement = con.prepareStatement(sql);
        statement.setInt(1, customerID);
        ResultSet result = statement.executeQuery();
        if (result.next()) {
            int rentedCarID = result.getInt("RENTED_CAR_ID");
            if (rentedCarID != 0) {
                System.out.println("You've already rented a car!");
            } else {
                Statement stmt = con.createStatement();

                // Execute query
                ResultSet rs = stmt.executeQuery("SELECT ID, NAME FROM COMPANY ORDER BY ID");
                if (!rs.next()) {
                    // ResultSet is empty
                    System.out.println("The company list is empty");
                    managerActions(scanner);
                } else {
                    System.out.println("Choose a company:");
                    // ResultSet is not empty
                    do {
                        int id = rs.getInt("ID");
                        String name = rs.getString("NAME");
                        System.out.println(id + ". " + name);
                    } while (rs.next());
                }

                System.out.println("0. Back");
                scanner.nextLine();
                int choice = scanner.nextInt();
                chooseCar(scanner, choice, customerID);
            }
        }
    }

    private void chooseCar(Scanner scanner, int companyID, int customerID) throws SQLException {
        String companyName = null;
        String sql = "SELECT name FROM COMPANY WHERE id = ?";
        PreparedStatement statement1 = con.prepareStatement(sql);
        statement1.setInt(1, companyID);
        ResultSet result = statement1.executeQuery();
        if (result.next()) {
            companyName = result.getString("NAME");
        }

        Map<Integer, String> carToIndex = new HashMap<>();
        sql = "SELECT CAR.name AS car_name, COMPANY.name AS company_name " +
                "FROM CAR " +
                "JOIN COMPANY ON CAR.COMPANY_ID = COMPANY.ID " +
                "WHERE CAR.ID NOT IN (SELECT RENTED_CAR_ID FROM CUSTOMER WHERE RENTED_CAR_ID IS NOT NULL) " +
                "AND CAR.COMPANY_ID = ?";
        PreparedStatement statement2 = con.prepareStatement(sql);
        statement2.setInt(1, companyID);
        result = statement2.executeQuery();
        if (!result.next()) {
            System.out.println("No available cars in the " + companyName + " company");
            rentCar(scanner, customerID);
        } else {
            System.out.println("Choose a car:");
            int counter = 1;
            do {
                String carName = result.getString("NAME");
                carToIndex.put(counter, carName);
                System.out.println(counter + ". " + carName);
                counter++;
            } while (result.next());
        }

        int chosenCar = scanner.nextInt();
        String carName = carToIndex.get(chosenCar);
        sql = "SELECT ID FROM CAR WHERE name = ?";
        PreparedStatement statement3 = con.prepareStatement(sql);
        statement3.setString(1, carName);
        result = statement3.executeQuery();
        if (result.next()) {
            int carID = result.getInt("ID");
            sql = "UPDATE CUSTOMER SET RENTED_CAR_ID = ? WHERE ID = ?";
            PreparedStatement statement4 = con.prepareStatement(sql);
            statement4.setInt(1, carID);
            statement4.setInt(2, customerID);
            statement4.executeUpdate();
            System.out.println("You rented '" + carName + "'");
        }
        customerOperations(scanner, customerID);
    }

    public void returnCar(int customerID) throws SQLException {
        String sql = "SELECT RENTED_CAR_ID FROM CUSTOMER WHERE ID = ?";
        PreparedStatement statement = con.prepareStatement(sql);
        statement.setInt(1, customerID);
        ResultSet result = statement.executeQuery();

        if (result.next()) {
            int rentedCarID = result.getInt("RENTED_CAR_ID");
            if (rentedCarID != 0) {
                sql = "UPDATE CUSTOMER SET RENTED_CAR_ID = NULL WHERE ID = ?";
                statement = con.prepareStatement(sql);
                statement.setInt(1, customerID);
                statement.executeUpdate();
                System.out.println("You've returned a rented car!");
            } else {
                System.out.println("You didn't rent a car!");
            }
        }
    }

    public void listCar(int customerID) throws SQLException {
        String sql = "SELECT CAR.NAME AS car_name, COMPANY.name AS company_name " +
                "FROM CUSTOMER " +
                "JOIN CAR ON CUSTOMER.RENTED_CAR_ID = CAR.ID " +
                "JOIN COMPANY ON CAR.COMPANY_ID = COMPANY.ID " +
                "WHERE CUSTOMER.ID = ? AND CUSTOMER.RENTED_CAR_ID IS NOT NULL";
        PreparedStatement statement = con.prepareStatement(sql);
        statement.setInt(1, customerID);
        ResultSet result = statement.executeQuery();
        if (!result.next()) {
            System.out.println("You didn't rent a car!");
        } else {
            do {
                String carName = result.getString("car_name");
                String companyName = result.getString("company_name");
                System.out.println("Your Rented car:");
                System.out.println(carName);
                System.out.println("Company:");
                System.out.println(companyName);
            } while (result.next());
        }
    }
}
