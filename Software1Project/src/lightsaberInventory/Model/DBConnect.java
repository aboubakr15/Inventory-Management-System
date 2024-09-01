package lightsaberInventory.Model;

import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.SQLException;

public interface DBConnect<T> {

    Connection connect() throws SQLException;

    // Method to insert a new record into the database using a generic object
    boolean insertRecord(Object obj) throws SQLException;

    boolean updateRecord(Object obj) throws SQLException;

    void deleteRecord(int id) throws SQLException;

    Object getRecord(int id) throws SQLException;

    ObservableList<T> getAllRecords() throws SQLException;
}
