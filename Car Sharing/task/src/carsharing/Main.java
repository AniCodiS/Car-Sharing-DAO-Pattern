package carsharing;

import java.sql.*;
import java.util.Scanner;

public class Main {

    private static Connection con;
    private static CarDao car;

    public static void main(String[] args) throws SQLException {
        // Getting command arguments
        String databaseName = null;
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-databaseFileName")) {
                if (args[i + 1] != null) {
                    databaseName = args[i + 1];
                }
            }
        }
        if (databaseName == null) {
            databaseName = "carsharing";
        }

        // Establishing connection
        String JDBCDriver = "org.h2.Driver";
        String DBUrl = "jdbc:h2:./src/carsharing/db/" + databaseName;
        MyConnection conn = new MyConnection(JDBCDriver, DBUrl);
        con = conn.getConnection();
        con.setAutoCommit(true);

        createCompanyCarsAndCustomersTables();

        loginOrExit();

        try {
            con.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static void createCompanyCarsAndCustomersTables() throws SQLException {
        Statement stmt;

        System.out.println("Creating tables in given database...");
        stmt = con.createStatement();
        String sql = "CREATE TABLE IF NOT EXISTS COMPANY (ID INT NOT NULL AUTO_INCREMENT, " +
                "NAME VARCHAR(255) NOT NULL UNIQUE, PRIMARY KEY (ID));";
        stmt.execute(sql);

        sql = "CREATE TABLE IF NOT EXISTS CAR (ID INT PRIMARY KEY AUTO_INCREMENT," +
                "NAME VARCHAR(255) NOT NULL UNIQUE," +
                "COMPANY_ID INT NOT NULL," +
                "FOREIGN KEY (COMPANY_ID) REFERENCES COMPANY(ID));";
        stmt.execute(sql);

        sql = "CREATE TABLE IF NOT EXISTS CUSTOMER (ID INT PRIMARY KEY AUTO_INCREMENT," +
                "NAME VARCHAR(255) NOT NULL UNIQUE," +
                "RENTED_CAR_ID INT," +
                "FOREIGN KEY (RENTED_CAR_ID) REFERENCES CAR(ID) ON DELETE SET NULL);";
        stmt.execute(sql);
        System.out.println("Created tables in given database...");

        stmt.close();
    }

    protected static void loginOrExit() throws SQLException {
        Scanner scanner = new Scanner(System.in);

        car = new CarDaoImpl(con, scanner);

        int choice = -1;
        while (choice != 0) {
            System.out.println("1. Log in as a manager");
            System.out.println("2. Log in as a customer");
            System.out.println("3. Create a customer");
            System.out.println("0. Exit");

            if (scanner.hasNextInt()) {
                choice = scanner.nextInt();
                switch (choice) {
                    case 1:
                        // Handle: 1. Log in as a manager
                        System.out.println("Logging in as a manager...");
                        ManagerDaoImpl newManager = new ManagerDaoImpl(car, con);
                        ManagerDaoImpl.managerActions(scanner);
                        //managerActivities(scanner);
                        break;
                    case 2:
                        // Handle: 2. Log in as a customer
                        CustomerDao newCustomer1 = new CustomerDaoImpl(con, car);
                        newCustomer1.listCustomers(scanner, con);
                        break;
                    case 3:
                        // Handle: 3. Create a customer
                        CustomerDao newCustomer2 = new CustomerDaoImpl(con, car);
                        newCustomer2.createCustomer(scanner, con);
                        break;
                    case 0:
                        // Handle: 0. Exit
                        System.out.println("Exiting...");
                        break;
                    default:
                        // Handle unexpected int input from the user
                        System.out.println("Invalid option. Please try again.");
                        break;
                }
            } else {
                System.out.println("Invalid input. Please enter a number.");
                scanner.next(); // Consume the invalid input
            }
        }
        scanner.close();
    }
}