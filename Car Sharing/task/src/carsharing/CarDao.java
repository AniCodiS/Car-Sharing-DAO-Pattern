package carsharing;

import java.sql.SQLException;
import java.util.Scanner;

public interface CarDao {
    void rentCar(Scanner scanner, int customerID) throws SQLException;
    void returnCar(int customerID) throws SQLException;
    void listCar(int customerID) throws SQLException;
    void carOperations(Scanner scanner, int companyID);
}
