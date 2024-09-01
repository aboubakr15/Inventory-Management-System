package lightsaberInventory.Model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;

import java.sql.*;
import java.time.LocalDate;

public class DBProduct_Income implements DBConnect<ProductIncome>{
    private Connection connection;
    private Statement statement;
    private ResultSet resultSet;
    private final ObservableList<ProductIncome> productIncomes = FXCollections.observableArrayList();
    @Override
    public Connection connect() throws SQLException {
        String dburl = "jdbc:mysql://localhost:3306/sabtan";//3300 on client's device
        String user = "root";
        String pass = "Admin123";
        // Get a connection to database.
        connection = DriverManager.getConnection(dburl, user, pass);
        System.out.println("\nDatabase connection successfully!\n");
        return connection;
    }

    @Override
    public boolean insertRecord(Object obj){
        if (obj instanceof ProductIncome) {
            return insertProduct_Income((ProductIncome) obj);
        } else {
            throw new IllegalArgumentException("Unsupported object type for ProductDatabase");
        }
    }

    private boolean insertProduct_Income(ProductIncome productIncome) {
        try {
            // Get a connection to database.
            connection = connect();
            // Create a statement.
            statement = connection.createStatement();
            System.out.println("Inserting new product");

            // Execute query.
            int rowsAffected = statement.executeUpdate(
                    "insert into sabtan.product_income (product_id_in, quantity_in, date, product_income_price) " +
                            "values('"
                            + productIncome.getProduct_id()
                            + "', '" + productIncome.getAddedQuantity()
                            + "', '" + productIncome.getDate()
                            + "', '" + productIncome.getProductIncomePrice() + "')"
            );
            if (rowsAffected > 0){
                System.out.println("ProductItem inserted successfully");
                return true;
            }
            else {
                System.out.println("Error: Couldn't Update the database");
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Add Product");
                alert.setHeaderText("Failed TO Update");
                alert.setContentText("Something went wrong. Please try again.");
                alert.showAndWait();
                return false;
            }
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
    public boolean updateRecord(Object obj) {
        return false;
    }

    @Override
    public void deleteRecord(int id) {}

    @Override
    public Object getRecord(int id){
        return null;
    }

    public ObservableList<ProductIncome> getRecords(int productId) {
        try {
            // Get a connection to database.
            connection = connect();
            // Create a statement.
            statement = connection.createStatement();
            System.out.println("Selecting all product incomes");
            // Execute query.
            resultSet = statement.executeQuery("select * from sabtan.product_income where product_id_in = " + productId);
            // Create product incomes object
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                int productID = resultSet.getInt("product_id_in");
                int quantityIn = resultSet.getInt("quantity_in");
                double productIncomePrice = resultSet.getDouble("product_income_price"); //* quantityIn
                LocalDate date = resultSet.getDate("date").toLocalDate();
                ProductIncome productIncome = new ProductIncome(id, productID, quantityIn, String.valueOf(date), productIncomePrice);
                productIncomes.add(productIncome);
            }
            return productIncomes;
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Get Product Incomes");
            alert.setHeaderText(e.getLocalizedMessage());
            alert.setContentText("Something went wrong. Please try again.");
            alert.showAndWait();
            return null;
        }
    }

    public ObservableList<ProductIncome> getRecordsInDateRange(int productId, LocalDate startDate, LocalDate endDate) {
        ObservableList<ProductIncome> productIncomes = FXCollections.observableArrayList();

        if (startDate == null){
            String query = "SELECT pi.* " +
                    "FROM sabtan.product_income pi " +
                    "JOIN product p ON pi.product_id_in = p.id " +
                    "WHERE pi.product_id_in = ? AND pi.date <= ?";
            try {
                connection = connect();
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setInt(1, productId);
                preparedStatement.setDate(2, Date.valueOf(endDate));
                resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    int productID = resultSet.getInt("product_id_in");
                    int quantityIn = resultSet.getInt("quantity_in");
                    double productIncomePrice = resultSet.getDouble("product_income_price");
                    LocalDate date = resultSet.getDate("date").toLocalDate();
                    ProductIncome productIncome = new ProductIncome(id, productID, quantityIn, String.valueOf(date), productIncomePrice);
                    productIncomes.add(productIncome);
                }
            } catch (SQLException e) {
                System.out.println("Error: " + e.getMessage());
                e.printStackTrace();
                // Handle the exception properly, e.g., show an error message
            }
        }else {
            String query = "SELECT pi.* " +
                    "FROM sabtan.product_income pi " +
                    "JOIN product p ON pi.product_id_in = p.id " +
                    "WHERE pi.product_id_in = ? AND date BETWEEN ? AND ?";
            try {
                connection = connect();
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setInt(1, productId);
                preparedStatement.setDate(2, Date.valueOf(startDate));
                preparedStatement.setDate(3, Date.valueOf(endDate));
                resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    int productID = resultSet.getInt("product_id_in");
                    int quantityIn = resultSet.getInt("quantity_in");
                    double productIncomePrice = resultSet.getDouble("product_income_price");
                    LocalDate date = resultSet.getDate("date").toLocalDate();
                    ProductIncome productIncome = new ProductIncome(id, productID, quantityIn, String.valueOf(date), productIncomePrice);
                    productIncomes.add(productIncome);
                }
            } catch (SQLException e) {
                System.out.println("Error: " + e.getMessage());
                e.printStackTrace();
                // Handle the exception properly, e.g., show an error message
            }
        }
        return productIncomes;
    }

    @Override
    public ObservableList<ProductIncome> getAllRecords() {
        try {
            // Get a connection to database.
            connection = connect();
            // Create a statement.
            statement = connection.createStatement();
            System.out.println("Selecting all product incomes");
            // Execute query.
            resultSet = statement.executeQuery("select * from sabtan.product_income");
            // Create product incomes object
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                int productID = resultSet.getInt("product_id_in");
                int quantityIn = resultSet.getInt("quantity_in");
                double productIncomePrice = resultSet.getDouble("product_income_price");
                LocalDate date = resultSet.getDate("date").toLocalDate();
                ProductIncome productIncome = new ProductIncome(id, productID, quantityIn, String.valueOf(date), productIncomePrice);
                productIncomes.add(productIncome);
            }
            return productIncomes;
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Get Product Incomes");
            alert.setHeaderText(e.getLocalizedMessage());
            alert.setContentText("Something went wrong. Please try again.");
            alert.showAndWait();
            return null;
        }
    }

    public double getProductPrice(int receiptId, int productId) throws SQLException {
        double productPrice = 0.0;
        String query = "SELECT product_price FROM sabtan.receipt_items WHERE receipt_id = ? AND product_id = ?";
        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, receiptId);
            statement.setInt(2, productId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    productPrice = resultSet.getDouble("product_price");
                }
            }
        }
        return productPrice;
    }
}
