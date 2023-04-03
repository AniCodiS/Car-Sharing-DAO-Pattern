package carsharing;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Scanner;

public interface CustomerDao {
    void listCustomers(Scanner scanner, Connection con) throws SQLException;
    void createCustomer(Scanner scanner, Connection con);
}
