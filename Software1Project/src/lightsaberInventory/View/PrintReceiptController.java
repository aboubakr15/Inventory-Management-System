package lightsaberInventory.View;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import lightsaberInventory.Model.*;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.DecimalFormat;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType0Font;


public class PrintReceiptController implements Initializable {
    @FXML
    TextField searchProductsBox, selectedQuantity;
    @FXML
    TableView<Product> productsTable, selectedProductsTable;
    @FXML
    TableColumn<Product, Integer> selectedProductQuantityCol, selectedProductPriceCol;

    @FXML
    TableColumn<Product, String> productNameCol, productModelCol ,productQuantityCol, productPriceCol, selectedProductNameCol, productBuyingPriceCol;

    @FXML
    Button printButton, addProductButton, closeButton, deleteProductButton;

    private Set<Integer> selectedProductIds = new HashSet<>();

    private ObservableList<Product> products = FXCollections.observableArrayList();
    private FilteredList<Product> filteredList = new FilteredList<>(products);
    private int clientID = 0;

    public void setClientID(int id) {
        this.clientID = id;
    }

    @FXML
    public void searchProducts() throws SQLException {
        // Get the search text
        String searchText = searchProductsBox.getText().trim().toLowerCase();

        // Clear the table if the search text is empty
        if (searchText.isEmpty()) {
            DBProduct dbProduct = new DBProduct();
            productsTable.setItems(dbProduct.getAllRecords());
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
                    productsTable.setItems(FXCollections.observableArrayList(filteredProducts));
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
            productsTable.setItems(FXCollections.observableArrayList(filteredProducts));
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
    public void selectQuantity() throws SQLException {
        Product selectedProduct = productsTable.getSelectionModel().getSelectedItem();
        addProductButton.requestFocus();
        selectedQuantity.setPromptText("(1 - " + selectedProduct.getQuantityInStock() + ")");
        addProductButtonAction();
    }
    @FXML
    public void addProductButtonAction() throws SQLException {
        if ( products.size() <= 31){
            Product selectedProduct = productsTable.getSelectionModel().getSelectedItem();
            if (selectedProduct == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("No Selection");
                alert.setHeaderText("Please select a product to add");
                alert.showAndWait();
                selectedQuantity.setText("");

            } else if (!Inventory.isNumeric(selectedQuantity.getText())){
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Value Error");
                alert.setHeaderText("Not a number!");
                alert.setContentText("Number must be between 1 and " + selectedProduct.getQuantityInStock());
                alert.showAndWait();
                selectedQuantity.setText("");

            } else if (Integer.parseInt(selectedQuantity.getText()) <= 0
                    || Integer.parseInt(selectedQuantity.getText()) > selectedProduct.getQuantityInStock()) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Value Error");
                alert.setHeaderText("Number must be between 1 and " + selectedProduct.getQuantityInStock());
                alert.showAndWait();
                selectedQuantity.setText("");

            } else if (selectedProductIds.contains(selectedProduct.getId())) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Duplicate");
                alert.setHeaderText("Duplicate !");
                alert.setContentText("Can't select same part twice.");
                alert.showAndWait();
                selectedQuantity.setText("");
            } else {
                selectedProductIds.add(selectedProduct.getId());
                int id = selectedProduct.getId();
                String name = selectedProduct.getName();
                String model = selectedProduct.getModel();
                int quantity = Integer.parseInt(selectedQuantity.getText());
                double price = selectedProduct.getSellingPrice();

                DBProduct dbProduct = new DBProduct();

                selectedProductNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
                selectedProductQuantityCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
                selectedProductPriceCol.setCellValueFactory(new PropertyValueFactory<>("sellingPrice"));

                Product product = (Product) dbProduct.getRecord(id);
                double buying_price = product.getBuyingPrice();
                Product newProduct = new Product(id, name, quantity, price, buying_price,model);

                products.add(newProduct);

                selectedProductsTable.setItems(filteredList);
                selectedQuantity.setText("");

                // Update Quantity
                selectedProduct.setQuantityInStock(selectedProduct.getQuantityInStock() - quantity);
                selectedProduct.setSoldQuantity(selectedProduct.getSoldQuantity() + quantity);
                // Update it in the database
                boolean success = dbProduct.updateRecord(selectedProduct);
                if (success) {
                    System.out.println("SUCCESS");
                    productsTable.refresh();
                }
            }
        }else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Receipt Limit");
            alert.setHeaderText("Receipt Limit!");
            alert.setContentText("Can't add more than 32 parts in one receipt");
            alert.showAndWait();
        }
    }

    @FXML
    public void deleteProductButtonAction() throws SQLException {
        Product selectedProduct = selectedProductsTable.getSelectionModel().getSelectedItem();
        if (selectedProduct == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("No Selection");
            alert.setHeaderText("Please select a product to delete");
            alert.showAndWait();
        } else {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete Product?");
            alert.setHeaderText("Delete " + selectedProduct.getName() + "?");
            alert.showAndWait();
            if (alert.getResult() == ButtonType.OK)  {
                products.remove(selectedProduct);
                selectedProductsTable.setItems(products);
                DBProduct dbProduct = new DBProduct();
                Product product = (Product) dbProduct.getRecord(selectedProduct.getId());
                product.setQuantityInStock(product.getQuantityInStock() + selectedProduct.getQuantity());
                boolean success = dbProduct.updateRecord(product);
                if (success) {
                    productsTable.setItems(dbProduct.getAllRecords());
                }
            }
            else {
                alert.close();
            }
        }
    }

    @FXML
    private void closeButtonAction() throws SQLException, IOException {
        DBProduct dbProduct = new DBProduct();
        for (Product product : products) {
            Product editProduct = (Product) dbProduct.getRecord(product.getId());
            editProduct.setQuantityInStock(editProduct.getQuantityInStock() + product.getQuantity());
            boolean success = dbProduct.updateRecord(editProduct);
        }
        FXMLLoader loader = new FXMLLoader(getClass().getResource("MainScreen.fxml"));
        Parent addPartScreen = loader.load();
        Scene addPartScene = new Scene(addPartScreen);
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.setTitle("Sabtan");
        stage.setScene(addPartScene);
        stage.show();
    }

    @FXML
    private void printButtonAction() throws SQLException {

        System.out.println("Saving receipt...");

        //Print PDF
        try (
            PDDocument document = new PDDocument()) {
            DBClient dbClient = new DBClient();
            Client Client = (lightsaberInventory.Model.Client) dbClient.getRecord(clientID);

            String username = System.getProperty("user.name");
            PDType0Font font = PDType0Font.load(document, new File("C:\\Users\\" + username + "\\Desktop\\BackUp\\Helvetica-Bold-Font.ttf"));

            //Page 1
            PDPage page = new PDPage();
            document.addPage(page);
            PDPageContentStream contentStream = new PDPageContentStream(document, page);
            float borderWidth = 2; // Adjust the border width as needed
            float[] dashPattern = {5, 5}; // Adjust the dash pattern as needed
            contentStream.setLineWidth(borderWidth);
            contentStream.setLineDashPattern(dashPattern, 0);
            // Draw the border
            contentStream.addRect(borderWidth / 2, borderWidth / 2, page.getMediaBox().getWidth() - borderWidth, page.getMediaBox().getHeight() - borderWidth);
            contentStream.stroke();

            contentStream.beginText();
            contentStream.setFont(font, 12);
            contentStream.newLineAtOffset(50, 750);
            contentStream.setLeading(14.5f);

            // Display the column headers
            contentStream.showText("Client Name :  " + Client.getName() + "  ( ________________________ )");
            contentStream.newLine();
            contentStream.newLine();
            contentStream.showText("-----------------------------------------------------------------------------------------------------------------");
            contentStream.newLine();
            contentStream.endText();

            DecimalFormat df = new DecimalFormat("###,###,###.##");
            contentStream.setFont(font, 10);

            ArrayList<Product> productsArray = new ArrayList<>();
            double GrandTotalPrice = 0;

            // Define initial y-coordinate position
            float startY = 700; // Adjust the starting y-coordinate as needed

            // Display the column headers
            contentStream.beginText();
            contentStream.setFont(font, 10);
            contentStream.newLineAtOffset(50, startY); // Starting x-coordinate for all columns
            contentStream.showText("Part");
            contentStream.newLineAtOffset(50, 0); // Adjust x-coordinate for the next column
            contentStream.showText("Model");
            contentStream.newLineAtOffset(150, 0); // Adjust x-coordinate for the next column
            contentStream.showText("Price (EGP)");
            contentStream.newLineAtOffset(100, 0); // Adjust x-coordinate for the next column
            contentStream.showText("Quantity");
            contentStream.newLineAtOffset(100, 0); // Adjust x-coordinate for the next column
            contentStream.showText("Total Price");
            contentStream.endText();

            // Loop through products and display them in the table format
            float currentY = startY - 25; // Adjusted y-coordinate for the next line
            for (Product product : products) {
                String part = product.getName();
                String model = product.getModel();
                String price = df.format(product.getSellingPrice());
                String quantity = String.valueOf(product.getQuantity());
                String Total = String.valueOf(product.getQuantity() * product.getSellingPrice());
                productsArray.add(product);


                contentStream.beginText();
                contentStream.setFont(font, 10);
                contentStream.newLineAtOffset(50, currentY); // Starting x-coordinate for all columns
                contentStream.showText(part);
                contentStream.newLineAtOffset(50, 0); // Adjust x-coordinate for the next column
                contentStream.showText(model);
                contentStream.newLineAtOffset(150, 0); // Adjust x-coordinate for the next column
                contentStream.showText(price);
                contentStream.newLineAtOffset(100, 0); // Adjust x-coordinate for the next column
                contentStream.showText(quantity);
                contentStream.newLineAtOffset(100, 0); // Adjust x-coordinate for the next column
                contentStream.showText(Total);
                contentStream.newLineAtOffset(50, 0); // Adjust x-coordinate for the next column
                contentStream.endText();

                // Draw a continuous line
                float startX = 50; // Starting x-coordinate for the line
                float currentX; // Starting x-coordinate for the line
                float endX = 500; // Ending x-coordinate for the line
                float lineY = currentY - 5; // Y-coordinate for the line

                contentStream.moveTo(startX, lineY); // Move to the starting point of the line

                // Draw short lines repeatedly to simulate a continuous line
                float dashLength = 3; // Length of each dash
                currentX = startX; // Current x-coordinate for drawing dashes
                while (currentX < endX) {
                    contentStream.lineTo(currentX + dashLength, lineY); // Draw a dash
                    contentStream.moveTo(currentX + dashLength, lineY); // Move to the next dash position
                    currentX += dashLength; // Move to the next dash position
                }
                contentStream.stroke(); // Stroke the path to draw the continuous line

                // Update the y-coordinate for the next line
                currentY -= 20;
                GrandTotalPrice += product.getSellingPrice() * product.getQuantity();
            }

            // Display the Grand Total
            Receipt receipt = new Receipt(productsArray, clientID, GrandTotalPrice);
            DBReceipt dbReceipt = new DBReceipt();
            String grandTotal = df.format(GrandTotalPrice);
            contentStream.beginText();
            contentStream.setFont(font, 10);
            contentStream.newLine();
            contentStream.newLine();
            contentStream.newLineAtOffset(50, currentY + 5); // Starting x-coordinate for Grand Total
            contentStream.showText("Grand Total: " + grandTotal + " EGP                                                                Date: " + receipt.getDate());
            contentStream.endText();
            contentStream.close();


            boolean savedSuccessfully = dbReceipt.insertRecord(receipt);
            String Filepath = "C:\\Users\\" + username + "\\Desktop\\الفواتير\\" + receipt.getDate().replace(':', '_') + " بتاريخ " + Client.getName() + " - فاتورة العميل" + ".pdf";

            if (savedSuccessfully) {
                // Display a success message
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Receipt Saved");
                alert.setHeaderText("Receipt saved successfully!");
                alert.showAndWait();
                document.save(Filepath);
                System.out.println("PDF created successfully");

                // Now navigate to the MainScreen.fxml
                FXMLLoader loader = new FXMLLoader(getClass().getResource("MainScreen.fxml"));
                Parent mainScreen = loader.load();
                Scene mainScreenScene = new Scene(mainScreen);
                Stage stage = (Stage) closeButton.getScene().getWindow();
                stage.setTitle("Sabtan");
                stage.setScene(mainScreenScene);
                stage.show();
            } else {
                // Display an error message if saving fails
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Failed to save receipt. Please try again.");
                alert.showAndWait();
            }
        } catch(IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        searchProductsBox.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                searchProducts();
            } catch (SQLException e) {
                System.out.println(e.getLocalizedMessage());
            }
        });

        // Add a listener to the selected item property of the TableView
        productsTable.getSelectionModel().selectedItemProperty().addListener((observable, oldSelection, newSelection) -> {
            if (newSelection != null) {
                // Update the prompt text in the TextField based on the selected product
                selectedQuantity.setPromptText("(1 - " + newSelection.getQuantityInStock() + ")");
            }
        });

        DBProduct dbProduct = new DBProduct();

//        productIDCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        productNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        productModelCol.setCellValueFactory(new PropertyValueFactory<>("model"));
        productBuyingPriceCol.setCellValueFactory(new PropertyValueFactory<>("buyingPrice"));
        productQuantityCol.setCellValueFactory(new PropertyValueFactory<>("quantityInStock"));
        productPriceCol.setCellValueFactory(new PropertyValueFactory<>("sellingPrice"));
        try {
            productsTable.setItems(dbProduct.getAllRecords());
        } catch (SQLException e) {
            System.out.println(e.getLocalizedMessage());
        }
    }
}

