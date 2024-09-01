package lightsaberInventory.Model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;


import java.time.LocalDate;

import java.sql.*;
import java.time.format.DateTimeFormatter;

public class DBReceipt implements DBConnect {
    private String dburl = "jdbc:mysql://localhost:3306/sabtan";//3300 on client's device
    private String user = "root";
    private String pass = "Admin123";
    private Connection connection;  
    private Statement statement;
    private ResultSet resultSet;
    private ObservableList<Receipt> receipts = FXCollections.observableArrayList();
    private final ObservableList<ReceiptItem> receiptItems = FXCollections.observableArrayList();
    private final ObservableList<ProductStatement> productStatements = FXCollections.observableArrayList();

    @Override
    public Connection connect() throws SQLException {
        // Get a connection to database.
        connection = DriverManager.getConnection(dburl, user, pass);
        System.out.println("\nDatabase connection successfully!\n");
        return connection;
    }

    @Override
    public boolean insertRecord(Object obj) {
        if (obj instanceof Receipt) {
            return insertReceipt((Receipt) obj);
        } else {
            throw new IllegalArgumentException("Unsupported object type for ProductDatabase");
        }
    }

    @Override
    public boolean updateRecord(Object obj) {
        return false;
    }

    @Override
    public void deleteRecord(int id) {

    }

    @Override
    public Object getRecord(int id) {
        return null;
    }

    private boolean insertReceipt(Receipt receipt) {

        if (receipt.getTotalPrice() != 0) {
            try {
                // Get a connection to the database.
                connection = connect();
                // Create a statement.
                statement = connection.createStatement();
                System.out.println("Inserting new receipt");

                // Execute query to insert into receipt table
                PreparedStatement insertReceiptStatement = connection.prepareStatement(
                        "insert into sabtan.receipt (client_id, date, total_price) " +
                                "values(?, ?, ?)",
                        Statement.RETURN_GENERATED_KEYS);

                insertReceiptStatement.setInt(1, receipt.getClientID());
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                String dateWithoutTime = receipt.getDate().substring(0, 10);
                LocalDate parsedDate = LocalDate.parse(dateWithoutTime, formatter);
                insertReceiptStatement.setDate(2, Date.valueOf(parsedDate));
                insertReceiptStatement.setDouble(3, receipt.getTotalPrice());

                // Execute the insert statement
                int rowsAffected = insertReceiptStatement.executeUpdate();

                if (rowsAffected == 0) {
                    System.out.println("Inserting receipt failed, no rows affected.");
                    return false;
                }

                // Retrieve the generated ID of the recently added receipt
                try (ResultSet generatedKeys = insertReceiptStatement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int generatedId = generatedKeys.getInt(1);
                        System.out.println("Generated Receipt ID: " + generatedId);

                        // Now use generatedId in the receipt_items table
                        for (Product product : receipt.getProducts()) {
                            // Execute query to insert into receipt_items table
                            int insertToReceiptItemsTable = statement.executeUpdate(
                                    "insert into sabtan.receipt_items (receipt_id, product_id, quantity, product_price, product_buying_price) " +
                                            "values('"
                                            + generatedId
                                            + "', '" + product.getId()
                                            + "', '" + product.getQuantity()
                                            + "', '" + product.getSellingPrice()
                                            + "', '" + product.getBuyingPrice() + "')"
                            );
                        }
                        System.out.println("Receipt and Receipt Items inserted successfully");
                        return true;
                    } else {
                        System.out.println("Inserting receipt failed, no ID obtained.");
                        return false;
                    }
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Add Receipt");
                alert.setHeaderText(e.getLocalizedMessage());
                alert.setContentText("Something went wrong. Please try again.");
                alert.showAndWait();
                return false;
            }
        } else {
            return false;
        }
    }

    public boolean deleteReceiptItem(int receiptId, int productId, int quantity_returned, int quantity_bought) {
        String DeleteQuery = "DELETE FROM sabtan.receipt_items WHERE receipt_id = ? AND product_id = ?";
        String UpdateQuery = "Update sabtan.receipt_items SET quantity = quantity - ? WHERE receipt_id = ? AND product_id = ?";
        String UpdateStockQuery = "Update sabtan.product SET quantity_in_stock = quantity_in_stock + ? WHERE id = ?";

        try {
            // Get a connection to database.
            connection = connect();
            // Create a statement.
            statement = connection.createStatement();
            // Create a PreparedStatement object to execute the query
            PreparedStatement preparedStatementDelete = connection.prepareStatement(DeleteQuery);
            PreparedStatement preparedStatementUpdate = connection.prepareStatement(UpdateQuery);
            PreparedStatement preparedStatementUpdateStock = connection.prepareStatement(UpdateStockQuery);

            // Set the parameters
            preparedStatementDelete.setInt(1, receiptId);
            preparedStatementDelete.setInt(2, productId);

            preparedStatementUpdate.setInt(1, quantity_returned);
            preparedStatementUpdate.setInt(2, receiptId);
            preparedStatementUpdate.setInt(3, productId);

            preparedStatementUpdateStock.setInt(1, quantity_returned);
            preparedStatementUpdateStock.setInt(2, productId);

            // select delete or update operation
            int rowsDeleted;
            if (quantity_returned == quantity_bought) {
                rowsDeleted = preparedStatementDelete.executeUpdate();
                preparedStatementUpdateStock.executeUpdate();
            }else {
                rowsDeleted = preparedStatementUpdate.executeUpdate();
                preparedStatementUpdateStock.executeUpdate();
            }

            // Check if the process was successful
            if (rowsDeleted > 0) {
                System.out.println("Item deleted successfully from receipt_items table.");
                return true;
            } else {
                System.out.println("No item found for deletion.");
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public ObservableList<Receipt> getAllRecords() throws SQLException {
        try {
            // Get a connection to database.
            connection = connect();
            // Create a statement.
            statement = connection.createStatement();
            System.out.println("Selecting all receipts");
            // Execute query.
            resultSet = statement.executeQuery("select * from sabtan.receipt");
            // Create clients object
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                double totalPrice = resultSet.getDouble("total_price");
                int clientID = resultSet.getInt("client_id");
                LocalDate date = resultSet.getDate("date").toLocalDate();
                Receipt receipt = new Receipt(id, totalPrice, clientID, date);
                receipts.add(receipt);
            }
            return receipts;
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Get Receipts");
            alert.setHeaderText(e.getLocalizedMessage());
            alert.setContentText("Something went wrong. Please try again.");
            alert.showAndWait();
            return null;
        }
    }

    public ObservableList<Receipt> getAllRecordsForClient(int clientID) throws SQLException {
        try {
            // Get a connection to database.
            connection = connect();
            // Create a statement.
            statement = connection.createStatement();
            System.out.println("Selecting all receipts");
            // Execute query.
            System.out.println("Executing SQL query: " + "select * from sabtan.receipt where client_id = " + clientID);
            resultSet = statement.executeQuery("select * from sabtan.receipt where client_id = " + clientID);

            // Create clients object
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                double totalPrice = resultSet.getDouble("total_price");
                LocalDate date = resultSet.getDate("date").toLocalDate();
                Receipt receipt = new Receipt(id, totalPrice, clientID, date);
                receipts.add(receipt);
            }
            return receipts;
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Get Receipts");
            alert.setHeaderText(e.getLocalizedMessage());
            alert.setContentText("Something went wrong. Please try again.");
            alert.showAndWait();
            return null;
        }
    }

    public ObservableList<ReceiptItem> getReceiptDetails(int receiptID) {
        String query = "SELECT " +
                "    product.name AS product_name, " +
                "    product.selling_price AS product_price, " +
                "    receipt_items.quantity AS quantity, " +
                "    receipt_items.product_id AS pID, " +
                "    (receipt_items.product_price * receipt_items.quantity) AS total_price\n" +
                "FROM receipt\n" +
                "JOIN receipt_items ON receipt.id = receipt_items.receipt_id\n" +
                "JOIN product ON product.id = receipt_items.product_id\n" +
                "WHERE receipt.id = " + receiptID;
        try {
            connection = connect();
            statement = connection.createStatement();
            System.out.println("Selecting all receipts");
            resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                String productName = resultSet.getString("product_name");
                double productPrice = resultSet.getDouble("product_price");
                int productQuantity = resultSet.getInt("quantity");
                int productID = resultSet.getInt("pID");
                double totalPrice = resultSet.getDouble("total_price");
                ReceiptItem receiptItem = new ReceiptItem(productName, productPrice, productQuantity, totalPrice, productID, receiptID);
                receiptItems.add(receiptItem);
            }
            return receiptItems;
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Get Receipt Info");
            alert.setHeaderText(e.getLocalizedMessage());
            alert.setContentText("Something went wrong. Please try again.");
            alert.showAndWait();
            return null;
        }
    }

    public ObservableList<Receipt> getReceiptsByDateRange(LocalDate startDate, LocalDate endDate) {
        ObservableList<Receipt> filteredReceipts = FXCollections.observableArrayList();

        try {
            // Get a connection to the database.
            connection = connect();
            PreparedStatement preparedStatement;
            if (startDate != null) {
                // Prepare the SQL statement
                preparedStatement = connection.prepareStatement(
                        "SELECT * FROM sabtan.receipt WHERE date BETWEEN ? AND ?"
                );
                preparedStatement.setDate(1, Date.valueOf(startDate));
                preparedStatement.setDate(2, Date.valueOf(endDate));
            } else {
                // Prepare the SQL statement
                preparedStatement = connection.prepareStatement(
                        "SELECT * FROM sabtan.receipt WHERE date <= ?"
                );
                preparedStatement.setDate(1, Date.valueOf(endDate));
            }

            // Execute the query
            resultSet = preparedStatement.executeQuery();

            // Process the result set and populate the filteredReceipts list
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                double totalPrice = resultSet.getDouble("total_price");
                int clientID = resultSet.getInt("client_id");
                LocalDate date = resultSet.getDate("date").toLocalDate();
                Receipt receipt = new Receipt(id, totalPrice, clientID, date);
                filteredReceipts.add(receipt);
            }

            return filteredReceipts;
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Get Receipts By Date Range");
            alert.setHeaderText(e.getLocalizedMessage());
            alert.setContentText("Something went wrong. Please try again.");
            alert.showAndWait();
            return null;
        }
    }

    public ObservableList<Receipt> getReceiptsByDateRangeForClient(int clientID, LocalDate startDate, LocalDate endDate) {
        ObservableList<Receipt> filteredReceipts = FXCollections.observableArrayList();

        try {
            // Get a connection to the database.
            connection = connect();
            PreparedStatement preparedStatement;

            if (startDate != null) {
                // Prepare the SQL statement
                preparedStatement = connection.prepareStatement(
                        "SELECT * FROM sabtan.receipt WHERE client_id = ? AND date BETWEEN ? AND ?"
                );
                preparedStatement.setInt(1, clientID);
                preparedStatement.setDate(2, Date.valueOf(startDate));
                preparedStatement.setDate(3, Date.valueOf(endDate));
            } else {
                // Prepare the SQL statement
                preparedStatement = connection.prepareStatement(
                        "SELECT * FROM sabtan.receipt WHERE client_id = ? AND date <= ?"
                );
                preparedStatement.setInt(1, clientID);
                preparedStatement.setDate(2, Date.valueOf(endDate));
            }

            // Execute the query
            resultSet = preparedStatement.executeQuery();

            // Process the result set and populate the filteredReceipts list
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                double totalPrice = resultSet.getDouble("total_price");
                LocalDate date = resultSet.getDate("date").toLocalDate();
                Receipt receipt = new Receipt(id, totalPrice, clientID, date);
                filteredReceipts.add(receipt);
            }

            return filteredReceipts;
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Get Receipts By Date Range For Client");
            alert.setHeaderText(e.getLocalizedMessage());
            alert.setContentText("Something went wrong. Please try again.");
            alert.showAndWait();
            return null;
        }
    }

    public double getSumOfBuyingPricesInDateRange(LocalDate startDate, LocalDate endDate) throws SQLException {
        double sum = 0.0;

        try {
            // Get a connection to the database.
            connection = connect();
            // Create a statement.
            statement = connection.createStatement();

            // Execute query.
            if (startDate != null) {
                PreparedStatement preparedStatement = connection.prepareStatement(
                        "SELECT sum(ri.product_buying_price * ri.quantity) AS total_buying_price " +
                                "FROM sabtan.receipt_items ri " +
                                "JOIN sabtan.receipt r ON ri.receipt_id = r.id " +
                                "WHERE r.date BETWEEN ? AND ?");
                preparedStatement.setDate(1, Date.valueOf(startDate));
                preparedStatement.setDate(2, Date.valueOf(endDate));
                resultSet = preparedStatement.executeQuery();

                // Retrieve the sum of buying prices.
                if (resultSet.next()) {
                    sum = resultSet.getDouble("total_buying_price");
                }
            } else {
                PreparedStatement preparedStatement = connection.prepareStatement(
                        "SELECT sum(ri.product_buying_price * ri.quantity) AS total_buying_price " +
                                "FROM sabtan.receipt_items ri " +
                                "JOIN sabtan.receipt r ON ri.receipt_id = r.id " +
                                "WHERE r.date <= ? ");
                preparedStatement.setDate(1, Date.valueOf(endDate));
                resultSet = preparedStatement.executeQuery();

                // Retrieve the sum of buying prices.
                if (resultSet.next()) {
                    sum = resultSet.getDouble("total_buying_price");
                }
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            // Handle exceptions or log them
            e.printStackTrace();
        }
        return sum;
    }

    public double getSumOfAllReceiptsPricesInDateRange(LocalDate startDate, LocalDate endDate) throws SQLException {
        double sum = 0.0;

        try {
            // Get a connection to the database.
            connection = connect();
            // Create a statement.
            statement = connection.createStatement();

            // Execute query.
            if (startDate != null) {
                PreparedStatement preparedStatement = connection.prepareStatement(
                        "SELECT SUM(total_price) AS total_receipts_price " +
                                "FROM sabtan.receipt " +
                                "WHERE date BETWEEN ? AND ?");
                preparedStatement.setDate(1, Date.valueOf(startDate));
                preparedStatement.setDate(2, Date.valueOf(endDate));
                resultSet = preparedStatement.executeQuery();

                // Retrieve the sum of all receipts prices.
                if (resultSet.next()) {
                    sum = resultSet.getDouble("total_receipts_price");
                }
            } else {
                PreparedStatement preparedStatement = connection.prepareStatement(
                        "SELECT SUM(total_price) AS total_receipts_price " +
                                "FROM sabtan.receipt " +
                                "WHERE date <= ?");
                preparedStatement.setDate(1, Date.valueOf(endDate));
                resultSet = preparedStatement.executeQuery();

                // Retrieve the sum of all receipts prices.
                if (resultSet.next()) {
                    sum = resultSet.getDouble("total_receipts_price");
                }
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            // Handle exceptions or log them
            e.printStackTrace();
        }
        return sum;
    }

    public double calculateProfitInDateRange(LocalDate startDate, LocalDate endDate) throws SQLException {
        double totalSellingPrice = getSumOfAllReceiptsPricesInDateRange(startDate, endDate);
        double totalBuyingPrice = getSumOfBuyingPricesInDateRange(startDate, endDate);
        return totalSellingPrice - totalBuyingPrice;
    }

    public ObservableList<ProductStatement> getReceiptItemsForProduct(int productID) {
        String query = "SELECT\n" +
                "    receipt.date AS receipt_date,\n" +
                "    client.name AS client_name,\n" +
                "    receipt_items.quantity AS product_quantity_sold,\n" +
                "    receipt_items.product_price\n" +
                "FROM\n" +
                "    receipt\n" +
                "JOIN receipt_items ON receipt.id = receipt_items.receipt_id\n" +
                "JOIN product ON product.id = receipt_items.product_id\n" +
                "JOIN client ON client.id = receipt.client_id\n" +
                "WHERE product.id = " + productID;
        try {
            connection = connect();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                LocalDate receipt_date = resultSet.getDate("receipt_date").toLocalDate();
                String client_name = resultSet.getString("client_name");
                int receipt_items_product_quantity = resultSet.getInt("product_quantity_sold");
                int product_price = resultSet.getInt("product_price");
                ProductStatement productStatement = new ProductStatement(receipt_date, client_name, receipt_items_product_quantity, product_price);
                productStatements.add(productStatement);
            }
            return productStatements;
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Get Receipt Info");
            alert.setHeaderText(e.getLocalizedMessage());
            alert.setContentText("Something went wrong. Please try again.");
            alert.showAndWait();
            return null;
        }
    }

    public ObservableList<ProductStatement> getReceiptItemsForProductInDateRange(int productID, LocalDate startDate, LocalDate endDate) {
        ObservableList<ProductStatement> productStatements = FXCollections.observableArrayList();
        String query;

        if (startDate != null) {
            query = "SELECT\n" +
                    "    receipt.date AS receipt_date,\n" +
                    "    client.name AS client_name,\n" +
                    "    receipt_items.quantity AS product_quantity_sold,\n" +
                    "    product.total_bought_quantity AS product_quantity_bought,\n" +
                    "    product.selling_price AS price\n" +
                    "FROM\n" +
                    "    receipt\n" +
                    "JOIN receipt_items ON receipt.id = receipt_items.receipt_id\n" +
                    "JOIN product ON product.id = receipt_items.product_id\n" +
                    "JOIN client ON client.id = receipt.client_id\n" +
                    "WHERE product.id = ? AND receipt.date BETWEEN ? AND ?";
            try {
                connection = connect();
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setInt(1, productID);
                preparedStatement.setDate(2, Date.valueOf(startDate));
                preparedStatement.setDate(3, Date.valueOf(endDate));
                resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    LocalDate receiptDate = resultSet.getDate("receipt_date").toLocalDate();
                    String clientName = resultSet.getString("client_name");
                    int productQuantitySold = resultSet.getInt("product_quantity_sold");
                    int productTotalBoughtQuantity = resultSet.getInt("product_quantity_bought");
                    ProductStatement productStatement = new ProductStatement(receiptDate, clientName, productQuantitySold, productTotalBoughtQuantity);
                    productStatements.add(productStatement);
                }
            } catch (SQLException e) {
                System.out.println("Error: " + e.getMessage());
                e.printStackTrace();
                // Handle the exception properly, e.g., show an error message
            }
        } else {
            query = "SELECT\n" +
                    "    receipt.date AS receipt_date,\n" +
                    "    client.name AS client_name,\n" +
                    "    receipt_items.quantity AS product_quantity_sold,\n" +
                    "    product.total_bought_quantity AS product_quantity_bought,\n" +
                    "    product.selling_price AS price\n" +
                    "FROM\n" +
                    "    receipt\n" +
                    "JOIN receipt_items ON receipt.id = receipt_items.receipt_id\n" +
                    "JOIN product ON product.id = receipt_items.product_id\n" +
                    "JOIN client ON client.id = receipt.client_id\n" +
                    "WHERE product.id = ? AND receipt.date <= ?";
            try {
                connection = connect();
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setInt(1, productID);
                preparedStatement.setDate(2, Date.valueOf(endDate));
                resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    LocalDate receiptDate = resultSet.getDate("receipt_date").toLocalDate();
                    String clientName = resultSet.getString("client_name");
                    int productQuantitySold = resultSet.getInt("product_quantity_sold");
                    int productTotalBoughtQuantity = resultSet.getInt("product_quantity_bought");
                    ProductStatement productStatement = new ProductStatement(receiptDate, clientName, productQuantitySold, productTotalBoughtQuantity);
                    productStatements.add(productStatement);
                }
            } catch (SQLException e) {
                System.out.println("Error: " + e.getMessage());
                e.printStackTrace();
                // Handle the exception properly, e.g., show an error message
            }
        }
        return productStatements;
    }

    public boolean raisePricesByPercent(double percentage) {
        String query = "UPDATE sabtan.product SET selling_price = selling_price + (selling_price * ? / 100)";

        try {
            connection = connect();
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setDouble(1, percentage);
            int rowsUpdated = preparedStatement.executeUpdate();
            System.out.println(rowsUpdated + " products prices changed successfully by " + percentage + "%.");
            return true;
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
            // Handle the exception properly, e.g., show an error message
            return false;
        }


    }

    // Method to update the total price after removing a product
    public void updateTotalPrice(int receiptId, double removedProductPrice) {
        String query = "UPDATE sabtan.receipt SET total_price = total_price - ? WHERE id = ?";

        try {
            // Get a connection to the database.
            connection = connect();
            // Create a statement.
            statement = connection.createStatement();
            // Create a statement.
            PreparedStatement preparedStatement = connection.prepareStatement(query);

            // Set the parameters
            preparedStatement.setDouble(1, removedProductPrice);
            preparedStatement.setInt(2, receiptId);

            // Execute the update
            int rowsUpdated = preparedStatement.executeUpdate();

            // Check if the update was successful
            if (rowsUpdated > 0) {
                System.out.println("Total price updated successfully.");
                System.out.println("Removed price -> " + removedProductPrice);
            } else {
                System.out.println("Failed to update total price.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to delete receipt item from the database
    public boolean returnItemDB (int receiptId, int productId, int quantity_returned, int quantity_bought) throws SQLException {

        //Update total price first because if u deleted the items first,
        // u will not be able to update the receipt using there prices, they won't be stored anymore.
        DBProduct_Income dbProduct_income = new DBProduct_Income();
        updateTotalPrice(receiptId
                , dbProduct_income.getProductPrice(receiptId, productId) * quantity_returned);

        if(deleteReceiptItem(receiptId, productId, quantity_returned, quantity_bought)){
            return true;
        }else {
            System.out.println("Failed to return");
            return false;
        }
    }

}