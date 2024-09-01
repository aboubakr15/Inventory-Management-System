package lightsaberInventory.Model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;

import java.sql.*;

public class DBClient implements DBConnect {

    private String dburl = "jdbc:mysql://localhost:3306/sabtan";//3300 on client's device
    private String user = "root";
    private String pass = "Admin123";
    private Connection connection;
    private Statement statement;
    private ResultSet resultSet;
    private ObservableList<Client> clients = FXCollections.observableArrayList();

    @Override
    public Connection connect() throws SQLException {
        // Get a connection to database.
        connection = DriverManager.getConnection(dburl, user, pass);
        System.out.println("\nDatabase connection successfully!\n");
        return connection;
    }


    @Override
    public boolean insertRecord(Object obj) throws SQLException {
        if (obj instanceof Client) {
            return insertClient((Client) obj);
        } else {
            throw new IllegalArgumentException("Unsupported object type for ClientDatabase");
        }
    }

    private boolean insertClient(Client client) throws SQLException {
        try {
            // Get a connection to database.
            connection = connect();
            // Create a statement.
            statement = connection.createStatement();
            System.out.println("Inserting new client");

            // Execute query.
            int rowsAffected = statement.executeUpdate(
                    "insert into sabtan.client (name, address, phone_number) " +
                            "values('" + client.getName()  + "', '" + client.getAddress()  + "', '" + client.getPhoneNumber() + "')"
            );
            System.out.println("Client inserted successfully");
            return true;
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Add Client");
            alert.setHeaderText(e.getLocalizedMessage());
            alert.setContentText("Something went wrong. Please try again.");
            alert.showAndWait();
            return false;
        }
    }

    @Override
    public boolean updateRecord(Object obj) throws SQLException {
        if (obj instanceof Client) {
            return updateClient((Client) obj);
        } else {
            throw new IllegalArgumentException("Unsupported object type for ClientDatabase");
        }
    }

    private boolean updateClient(Client client) throws SQLException {
        try {
            // Get a connection to the database.
            connection = connect();
            // Create a statement.
            statement = connection.createStatement();
            System.out.println("Updating client");

            System.out.println("=========================");
            System.out.println(client.getId());
            System.out.println(client.getName());
            System.out.println("=========================");

            // Execute the update query.
            int rowsAffected = statement.executeUpdate(
                    "update sabtan.client " +
                            "set name = '" + client.getName() +
                            "', address = '" + client.getAddress() +
                            "', phone_number = '" + client.getPhoneNumber() +
                            "' where id = " + client.getId()
            );

            if (rowsAffected > 0) {
                System.out.println("Client updated successfully");
                return true;
            } else {
                System.out.println("No client with the specified ID found for update");
                return false;
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Update Client");
            alert.setHeaderText(e.getLocalizedMessage());
            alert.setContentText("Something went wrong. Please try again.");
            alert.showAndWait();
            return false;
        }
    }

    @Override
    public void deleteRecord(int id) throws SQLException {
        try {
            // Get a connection to database.
            connection = connect();
            // Create a statement.
            statement = connection.createStatement();
            System.out.println("Deleting client with ID: " + id);
            // Execute query.
            int rowsAffected = statement.executeUpdate("DELETE FROM sabtan.client WHERE id = " + id);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Add Client");
            alert.setHeaderText(e.getLocalizedMessage());
            alert.setContentText("Something went wrong. Please try again.");
            alert.showAndWait();
        }
    }

    @Override
    public Object getRecord(int id) throws SQLException {
        try {
            // Get a connection to database.
            connection = connect();
            // Create a statement.
            statement = connection.createStatement();
            System.out.println("Selecting client with ID");
            // Execute query.
            resultSet = statement.executeQuery("select * from sabtan.client where id = " + id);
            // Create client object
            if (resultSet.next()) {
                int client_id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String address = resultSet.getString("address");
                String phoneNumber = resultSet.getString("phone_number");
                Client client = new Client(client_id, name, phoneNumber, address);
                return client;
            }
            return null;
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Add Client");
            alert.setHeaderText(e.getLocalizedMessage());
            alert.setContentText("Something went wrong. Please try again.");
            alert.showAndWait();
            return null;
        }
    }

    @Override
    public ObservableList<Client> getAllRecords() throws SQLException {
        try {
            // Get a connection to database.
            connection = connect();
            // Create a statement.
            statement = connection.createStatement();
            System.out.println("Selecting all clients");
            // Execute query.
            resultSet = statement.executeQuery("select * from sabtan.client");
            // Create clients object
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String address = resultSet.getString("address");
                String phoneNumber = resultSet.getString("phone_number");
                Client client = new Client(id, name, phoneNumber, address);
                clients.add(client);
            }
            return clients;
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Add Client");
            alert.setHeaderText(e.getLocalizedMessage());
            alert.setContentText("Something went wrong. Please try again.");
            alert.showAndWait();
            return null;
        }
    }
}
