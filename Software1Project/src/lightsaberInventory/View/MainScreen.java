package lightsaberInventory.View;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lightsaberInventory.Model.*;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/** MainScreen of LightSaber Inventory application. */

public class MainScreen implements Initializable {
    @FXML
    AnchorPane mainPane;

    @FXML
    TextField partSearchBox, productSearchBox, PercentChange;

    @FXML
    TableView<Client> partsTable = new TableView<>(Inventory.getAllClients());

    @FXML
    TableColumn<Client, Integer> partIdCol, partNameCol, partInventoryCol, partPriceCol;

    @FXML
    TableColumn<Product, String> ModelCol;

    @FXML
    TableView<Product> productTable;

    @FXML
    TableColumn<Product, Integer> productIdCol, productInStockCol, productBuyingPriceCol, productSellingPriceCol;

    @FXML
    TableColumn<Product, String> productNameCol;


    @FXML
    public void searchParts() throws SQLException {
        // Get the search text
        String searchText = partSearchBox.getText().trim().toLowerCase();

        // Clear the table if the search text is empty
        if (searchText.isEmpty()) {
            DBClient dbClient = new DBClient();
            partsTable.setItems(dbClient.getAllRecords());
        } else {
            try {
                DBClient dbClient = new DBClient();
                // Attempt to parse the input as an integer (product ID)
                int clientID = Integer.parseInt(searchText);

                // Lookup the product by ID
                Client returnedClient = (Client) dbClient.getRecord(clientID);

                if (returnedClient == null) {
                    displayNotFoundError();
                } else {
                    // Display the found product
                    partsTable.setItems(FXCollections.observableArrayList(returnedClient));
                }
            } catch (NumberFormatException e) {
                // If parsing as an integer fails, search by product name
                searchByNameClient(searchText);
            }
        }
    }

    @FXML
    public void searchProducts() throws SQLException {
        // Get the search text
        String searchText = productSearchBox.getText().trim().toLowerCase();

        // Clear the table if the search text is empty
        if (searchText.isEmpty()) {
            DBProduct dbProduct = new DBProduct();
            productTable.setItems(dbProduct.getAllRecords());
        } else {
            try {
                DBProduct dbProduct = new DBProduct();
                java.util.List<Product> filteredProducts = dbProduct.getAllRecords().stream()
                        .filter(product -> product.getModel().toLowerCase().contains(searchText))
                        .collect(Collectors.toList());

                if (filteredProducts.isEmpty()) {
                    searchByNameProduct(searchText);
                    //displayNotFoundError();
                } else {
                    // Display the filtered products
                    productTable.setItems(FXCollections.observableArrayList(filteredProducts));
                }
            } catch (NumberFormatException e) {
                // If parsing as an integer fails, search by product name
                searchByNameProduct(searchText);
            }
        }
    }

    private void searchByNameProduct(String searchText) throws SQLException {
        // Filter products by name
        DBProduct dbProduct = new DBProduct();
        java.util.List<Product> filteredProducts = dbProduct.getAllRecords().stream()
                .filter(product -> product.getName().toLowerCase().contains(searchText))
                .collect(Collectors.toList());

        if (filteredProducts.isEmpty()) {
            displayNotFoundError();
        } else {
            // Display the filtered products
            productTable.setItems(FXCollections.observableArrayList(filteredProducts));
        }
    }

    private void searchByNameClient(String searchText) throws SQLException {
        // Filter products by name
        DBClient dbClient = new DBClient();
        java.util.List<Client> filteredClients = dbClient.getAllRecords().stream()
                .filter(client -> client.getName().toLowerCase().contains(searchText))
                .collect(Collectors.toList());

        if (filteredClients.isEmpty()) {
            displayNotFoundError();
        } else {
            // Display the filtered products
            partsTable.setItems(FXCollections.observableArrayList(filteredClients));
        }
    }

    private void displayNotFoundError() {
        // Display an error alert for not finding products
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Nothing Found");
        alert.setHeaderText("Please Search Again");
        alert.showAndWait();
    }


    @FXML
    Button partAddButton, partModifyButton, partDeleteButton, partStatementButton,
            productStatementButton, addQuantity, percentChange, productAddButton;

    @FXML
    public void addPartsAction() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("AddClient.fxml"));
            Parent addPartScreen = loader.load();
            Scene addPartScene = new Scene(addPartScreen);
            Stage winAddPart = new Stage();
            winAddPart.setTitle("Add Client");
            winAddPart.initModality(Modality.APPLICATION_MODAL);
            winAddPart.setScene(addPartScene);
            winAddPart.showAndWait();
            updateLightsaberParts();
        } catch (IOException | SQLException e) {
            System.out.println(e.getLocalizedMessage());
        }
    }

    @FXML
    public void modifyPartAction(ActionEvent event) {
        if (partsTable.getSelectionModel().getSelectedItem() == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("No Selection");
            alert.setHeaderText("Please select a part to modify");
            alert.showAndWait();
        } else {
            Client selectedItem = partsTable.getSelectionModel().getSelectedItem();
            System.out.println(selectedItem.getName());
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("ModifyClient.fxml"));
                Parent addPartScreen = loader.load();
                ModifyClientController modifyClientController = loader.getController();
                modifyClientController.setClient(selectedItem);
                Scene addPartScene = new Scene(addPartScreen);
                Stage winAddPart = (Stage) ((Node) event.getSource()).getScene().getWindow();
                winAddPart.setTitle("Modify Client");
                winAddPart.setScene(addPartScene);
                winAddPart.show();
            } catch (IOException e) {
                System.out.println(e.getLocalizedMessage());
                System.out.println(e.getMessage());
                System.out.println("Error: Can not open the fxml file");
            }
        }
    }

    @FXML
    public void deletePartsAction() throws SQLException {
        if (partsTable.getSelectionModel().getSelectedItem() == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("No Selection");
            alert.setHeaderText("Please select a client to modify");
            alert.showAndWait();
        } else {
            Client selectedClient = partsTable.getSelectionModel().getSelectedItem();
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete Client?");
            alert.setHeaderText("Delete " + selectedClient.getName() + "?");
            alert.showAndWait();

            if (alert.getResult() == ButtonType.OK) {
                DBClient dbClient = new DBClient();
                dbClient.deleteRecord(selectedClient.getId());
                updateLightsaberParts();
            } else {
                alert.close();
            }
        }
    }




    @FXML
    public void addProductAction() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("AddProduct.fxml"));
            Parent addPartScreen = loader.load();
            Scene addPartScene = new Scene(addPartScreen);
            Stage winAddPart = new Stage();
            winAddPart.setTitle("Add Product");
            winAddPart.initModality(Modality.APPLICATION_MODAL);
            winAddPart.setScene(addPartScene);
            winAddPart.showAndWait();
            updateLightsaberProducts();
        } catch (IOException | SQLException e) {
            System.out.println(e.getLocalizedMessage());
        }
    }

    @FXML
    public void modifyProductAction(ActionEvent event) {
        if (productTable.getSelectionModel().getSelectedItem() == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("No Selection");
            alert.setHeaderText("Please select a product to modify");
            alert.showAndWait();
        } else {
            Product selectedItem = productTable.getSelectionModel().getSelectedItem();
            System.out.println(selectedItem.getName());

            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("ModifyProduct.fxml"));
                Parent addPartScreen = loader.load();
                ModifyProductController modifyProductController = loader.getController(); // This line
                modifyProductController.setDisplayedProduct(selectedItem); // This line
                Scene addPartScene = new Scene(addPartScreen);
                Stage winAddPart = (Stage) ((Node) event.getSource()).getScene().getWindow();
                winAddPart.setTitle("Modify Product");
                winAddPart.setScene(addPartScene);
                winAddPart.show();
            } catch (IOException e) {
                System.out.println(e.getLocalizedMessage());
            }
        }

    }

    @FXML
    public void deleteProductAction() throws SQLException {
        if (productTable.getSelectionModel().getSelectedItem() == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("No Selection");
            alert.setHeaderText("Please select a part to modify");
            alert.showAndWait();

        } else {
            Product selectedProduct = productTable.getSelectionModel().getSelectedItem();

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete Product?");
            alert.setHeaderText("Delete " + selectedProduct.getName() + "?");
            alert.showAndWait();

            if (alert.getResult() == ButtonType.OK) {
                DBProduct dbProduct = new DBProduct();
                dbProduct.deleteRecord(selectedProduct.getId());
                updateLightsaberProducts();
            } else {
                alert.close();
            }

        }
    }

    @FXML
    public void addQuantity(ActionEvent event) {
        // Check if product selected.
        if (productTable.getSelectionModel().getSelectedItem() == null) {
            // Alert if no product selected.
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("No Selection");
            alert.setHeaderText("Please select a product to increase quantity");
            alert.showAndWait();
        } else {
            // Get the selected product.
            Product selectedProduct = productTable.getSelectionModel().getSelectedItem();

            // Open the scene.
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("AddQuantity.fxml"));
                Parent addQuantityScreen = loader.load();

                // Load the controller to send the selected product.
                AddQuantityController addQuantityController = loader.getController();
                addQuantityController.setSelectedProduct(selectedProduct);

                // Show the scene
                Scene addQuantityScene = new Scene(addQuantityScreen);
                Stage winAddQuantity = (Stage) ((Node) event.getSource()).getScene().getWindow();
                winAddQuantity.setTitle("Add Quantity");
                winAddQuantity.setScene(addQuantityScene);
                winAddQuantity.show();
            } catch (IOException e) {
                System.out.println(e.getLocalizedMessage());
            }
        }
    }

    @FXML
    public void updateLightsaberParts() throws SQLException {
        DBClient dbClient = new DBClient();
        partsTable.setItems(dbClient.getAllRecords());
    }

    @FXML
    public void updateLightsaberProducts() throws SQLException {
        DBProduct dbProduct = new DBProduct();
        productTable.setItems(dbProduct.getAllRecords());
    }

    @FXML
    public void exitButtonAction(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Exit Lightsaber Inventory");
        alert.setHeaderText("Are you sure you want to exit?");
        alert.setContentText("Press OK to exit the program. \nPress Cancel to stay on this screen.");
        alert.showAndWait();
        if (alert.getResult() == ButtonType.OK) {
            Stage winMainScreen = (Stage) ((Node) event.getSource()).getScene().getWindow();
            winMainScreen.close();
        } else {
            alert.close();
        }
    }

    @FXML
    public void printReceiptButtonAction(ActionEvent event) {
        if (partsTable.getSelectionModel().getSelectedItem() == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("No Selection");
            alert.setHeaderText("Please select a client");
            alert.showAndWait();
        } else {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("PrintReceipt.fxml"));
                Parent addPartScreen = loader.load();
                PrintReceiptController printReceiptController = loader.getController();
                printReceiptController.setClientID(partsTable.getSelectionModel().getSelectedItem().getId());
                Scene addPartScene = new Scene(addPartScreen);
                Stage winAddPart = (Stage) ((Node) event.getSource()).getScene().getWindow();
                winAddPart.setTitle("Receipt");
                winAddPart.setScene(addPartScene);
                winAddPart.show();

            } catch (IOException e) {
                System.out.println(e.getLocalizedMessage()); // Handle the exception based on your needs
            }
        }
    }

    @FXML
    public void statementProductAction(ActionEvent event) {
        if (productTable.getSelectionModel().getSelectedItem() == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("No Selection");
            alert.setHeaderText("Please select a product");
            alert.showAndWait();
        } else {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("ProductStatement.fxml"));
                Parent addPartScreen = loader.load();
                ProductStatementController productStatementController = loader.getController();
                productStatementController.setProduct(productTable.getSelectionModel().getSelectedItem());
                Scene addPartScene = new Scene(addPartScreen);
                Stage winAddPart = (Stage) ((Node) event.getSource()).getScene().getWindow();
                winAddPart.setTitle("Client Statement");
                winAddPart.setScene(addPartScene);
                winAddPart.show();

            } catch (IOException e) {
                System.out.println(e.getLocalizedMessage()); // Handle the exception based on your needs
            }
        }
    }

    @FXML
    public void statementPartsAction(ActionEvent event) throws IOException {
        if (partsTable.getSelectionModel().getSelectedItem() == null) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("MonthlySales.fxml"));
            Parent addPartScreen = loader.load();
            ClientStatementController clientStatementController = loader.getController();
            clientStatementController.setSelection(false);
            Scene addPartScene = new Scene(addPartScreen);
            Stage winAddPart = (Stage) ((Node) event.getSource()).getScene().getWindow();
            winAddPart.setTitle("Monthly Sales");
            winAddPart.setScene(addPartScene);
            winAddPart.show();
        } else {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("ClientStatement.fxml"));
                Parent addPartScreen = loader.load();
                ClientStatementController clientStatementController = loader.getController();
                clientStatementController.setClient(partsTable.getSelectionModel().getSelectedItem());
                Scene addPartScene = new Scene(addPartScreen);
                Stage winAddPart = (Stage) ((Node) event.getSource()).getScene().getWindow();
                winAddPart.setTitle("Client Statement");
                winAddPart.setScene(addPartScene);
                winAddPart.show();

            } catch (IOException e) {
                System.out.println(e.getLocalizedMessage()); // Handle the exception based on your needs
            }
        }
    }

    public void percentChanger () throws SQLException {
        DBReceipt dbReceipt = new DBReceipt();
        double percentageIncrease = Double.parseDouble(PercentChange.getText());
        if(dbReceipt.raisePricesByPercent(percentageIncrease)){
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Percent Change");
            alert.setHeaderText("Prices Changed Successfully");
            alert.showAndWait();
            DBProduct dbProduct = new DBProduct();
            productTable.setItems(dbProduct.getAllRecords());
        }else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Percent NOT Changed ×");
            alert.setHeaderText("Something went wrong ×××");
            alert.showAndWait();
        }
    }

    // Method to export the entire database using MySQLDump
    public boolean exportEntireDatabase(String exportFilePath) {
        try {
            String username = System.getProperty("user.name");
            ProcessBuilder processBuilder = new ProcessBuilder(
                    "C:\\Users\\" + username + "\\Desktop\\BackUp\\mysqldump",
                    "-h", "localhost",
                    "-u", "root",
                    "-pAdmin123", // Replace with your actual password
                    "sabtan"  // Replace with your actual database name
            );

            // Redirect the output of mysqldump to the specified file
            processBuilder.redirectOutput(new File(exportFilePath));

            // Start the process
            Process process = processBuilder.start();

            // Wait for the process to complete
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                System.out.println("Export completed successfully!");
                return true;
            } else {
                System.out.println("Export failed with error code: " + exitCode);
                return false;
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Export Database");
            alert.setHeaderText(e.getMessage());
            alert.setContentText("Export failed. Please try again.");
            alert.showAndWait();
            return false;
        }
    }

    // This method is triggered when the user clicks on an "Export" button in your UI
    public void exportButtonClicked() {
        DBClient dbClient = new DBClient();
        String username = System.getProperty("user.name");
        boolean success = exportEntireDatabase("C:\\Users\\" + username + "\\Desktop\\BackUp\\DatabaseBackUp.sql");
        Alert alert;
        if (success) {
            // Notify the user about successful export
            alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("BackUp");
            alert.setHeaderText("Data Export");
            alert.setContentText("Data Exported Successfully.");
        } else {
            // Notify the user about export failure
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("BackUp");
            alert.setHeaderText("Data Export");
            alert.setContentText("Something went wrong.");
        }
        alert.showAndWait();
    }


    /** Loads the partsTable and productTable with columns names.  Helper function do to any necessary data population after scene is loaded. */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        partNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        partPriceCol.setCellValueFactory(new PropertyValueFactory<>("address"));
        partInventoryCol.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        try {
            updateLightsaberParts();
        } catch (SQLException e) {
            System.out.println(e.getLocalizedMessage());
        }

        productNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        ModelCol.setCellValueFactory(new PropertyValueFactory<>("model"));
        productBuyingPriceCol.setCellValueFactory(new PropertyValueFactory<>("buyingPrice"));
        productSellingPriceCol.setCellValueFactory(new PropertyValueFactory<>("sellingPrice"));
        productInStockCol.setCellValueFactory(new PropertyValueFactory<>("quantityInStock"));
        try {
            updateLightsaberProducts();
        } catch (SQLException e) {
            System.out.println(e.getLocalizedMessage());
        }

        productSearchBox.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                searchProducts();
            } catch (SQLException e) {
                System.out.println(e.getLocalizedMessage());
            }
        });

        partSearchBox.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                searchParts();
            } catch (SQLException e) {
                System.out.println(e.getLocalizedMessage());
            }
        });

    }
}
