package lightsaberInventory.Model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;

import java.sql.*;
import java.time.LocalDate;

public class DBProduct implements DBConnect {
    private Connection connection;
    private Statement statement;
    private ResultSet resultSet;
    private ObservableList<Product> products = FXCollections.observableArrayList();
    @Override
    public Connection connect() throws SQLException {
        // Get a connection to database.
        String dburl = "jdbc:mysql://localhost:3306/sabtan";//3300 on client's device
        String user = "root";
        String pass = "Admin123";
        connection = DriverManager.getConnection(dburl, user, pass);
        System.out.println("\nDatabase connection successfully!\n");
        return connection;
    }

    @Override
    public boolean insertRecord(Object obj) throws SQLException {
        if (obj instanceof Product) {
            return insertProduct((Product) obj);
        } else {
            throw new IllegalArgumentException("Unsupported object type for ProductDatabase");
        }
    }

    private boolean insertProduct(Product product) throws SQLException {
        try {
            // Get a connection to database.
            connection = connect();
            // Create a statement.
            statement = connection.createStatement();
            System.out.println("Inserting new product");

            // Execute query.
            int rowsAffected = statement.executeUpdate(
                    "insert into sabtan.product (name, model, uses, quantity_in_stock, total_sold_quantity, total_bought_quantity, buying_price, selling_price) " +
                            "values('"
                            + product.getName()
                            + "', '" + product.getModel()
                            + "', '" + product.getUses()
                            + "', '" + product.getQuantityInStock()
                            + "', '" + product.getSoldQuantity()
                            + "', '" + product.getBoughtQuantity()
                            + "', '" + product.getBuyingPrice()
                            + "', '" + product.getSellingPrice() + "')"
            );
            System.out.println("Product inserted successfully");
            return true;
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Add Product");
            alert.setHeaderText(e.getLocalizedMessage());
            alert.setContentText("Something went wrong. Please try again.");
            alert.showAndWait();
            return false;
        }
    }

    @Override
    public boolean updateRecord(Object obj) throws SQLException {
        if (obj instanceof Product) {
            return updateProduct((Product) obj);
        } else {
            throw new IllegalArgumentException("Unsupported object type for ProductDatabase");
        }
    }

    private boolean updateProduct(Product product) throws SQLException {
        try {
            // Get a connection to the database.
            connection = connect();
            // Create a statement.
            statement = connection.createStatement();
            System.out.println("Updating product");

            System.out.println("=========================");
            System.out.println(product.getId());
            System.out.println(product.getName());
            System.out.println("=========================");

            // Execute the update query.
            int rowsAffected = statement.executeUpdate(
                    "update sabtan.product " +
                            "set name = '" + product.getName() +
                            "', model = '" + product.getModel() +
                            "', uses = '" + product.getUses() +
                            "', quantity_in_stock = '" + product.getQuantityInStock() +
                            "', total_sold_quantity = '" + product.getSoldQuantity() +
                            "', total_bought_quantity = '" + product.getBoughtQuantity() +
                            "', buying_price = '" + product.getBuyingPrice() +
                            "', selling_price = '" + product.getSellingPrice() +
                            "' where id = '" + product.getId() + "'"
            );
            if (rowsAffected > 0) {
                System.out.println("Product updated successfully");

                return true;
            } else {
                System.out.println("No product with the specified ID found for update");
                return false;
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Update Product");
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
            System.out.println("Deleting Product with ID: " + id);
            // Execute query.
            int rowsAffected = statement.executeUpdate("DELETE FROM sabtan.product WHERE id = " + id);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Add Product");
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
            // Execute query.
            resultSet = statement.executeQuery("select * from sabtan.product WHERE id = " + id);
            if (resultSet.next()) {
                int product_id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String model = resultSet.getString("model");
                String uses = resultSet.getString("uses");
                int quantityInStock = resultSet.getInt("quantity_in_stock");
                int soldQuantity = resultSet.getInt("total_sold_quantity");
                int boughtQuantity = resultSet.getInt("total_bought_quantity");
                double buyingPrice = resultSet.getDouble("buying_price");
                double sellingPrice = resultSet.getDouble("selling_price");
                Product product = new Product(product_id, name, model , uses, quantityInStock, buyingPrice, sellingPrice, soldQuantity, boughtQuantity);
                System.out.println(product.getName());
                return product;
            }
            return null;
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Add Product");
            alert.setHeaderText(e.getLocalizedMessage());
            alert.setContentText("Something went wrong. Please try again.");
            alert.showAndWait();
            return null;
        }
    }

    @Override
    public ObservableList<Product> getAllRecords() throws SQLException {
        try {
            // Get a connection to database.
            connection = connect();
            // Create a statement.
            statement = connection.createStatement();
            System.out.println("Selecting all products");
            // Execute query.
            resultSet = statement.executeQuery("select * from sabtan.product");
            // Create clients object
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String model = resultSet.getString("model");
                String uses = resultSet.getString("uses");
                int quantityInStock = resultSet.getInt("quantity_in_stock");
                int soldQuantity = resultSet.getInt("total_sold_quantity");
                int boughtQuantity = resultSet.getInt("total_bought_quantity");
                double buyingPrice = resultSet.getDouble("buying_price");
                double sellingPrice = resultSet.getDouble("selling_price");
                Product product = new Product(id, name, model , uses, quantityInStock, buyingPrice, sellingPrice, soldQuantity, boughtQuantity);
                products.add(product);
            }
            return products;
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

