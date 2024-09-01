package lightsaberInventory.View;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import lightsaberInventory.Model.*;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class AddQuantityController implements Initializable {
    @FXML
    Label productName, currentQuantity, newQuantity;
    @FXML
    TextField quantityTextField;
    @FXML
    Button addButton, cancelButton;
    private Product selectedProduct;
    public void setSelectedProduct(Product product) {
        this.selectedProduct = product;

        // Update label fields.
        productName.setText(selectedProduct.getName());
        currentQuantity.setText("Current Quantity: " + selectedProduct.getQuantityInStock());
        newQuantity.setText("New Quantity: " + selectedProduct.getQuantityInStock());
    }
    @FXML
    public void addQuantity(ActionEvent event) {
        // Check if the input field is empty.
        if (quantityTextField.getText().isEmpty()) {
            // Alert that the field is empty.
            setWarningAlert("Data Error", "Please enter quantity");
        }
        // Check if the input field not numeric.
        else if (!Inventory.isNumeric(quantityTextField.getText())) {
            // Alert that the field not numeric.
            setWarningAlert("Data Error", "Please enter a number");
        }
        else {
            try {
                // Set confirmation alert.
                if (setConfirmationAlert("Add Quantity", "Would you like to save the added quantity?") == ButtonType.OK) {
                    // Get the added quantity.
                    int addedQuantity = getAddedQuantity();

                    // Update the quantity in product object.
                    selectedProduct.setBoughtQuantity(selectedProduct.getBoughtQuantity() + addedQuantity);
                    selectedProduct.setQuantityInStock(selectedProduct.getQuantityInStock() + addedQuantity);

                    // Update product quantity in the database.
                    DBProduct dbProduct = new DBProduct();
                    boolean success = dbProduct.updateRecord(selectedProduct);

                    // Check if the updated was completely made.
                    if (success) {
                        // Save the transaction in product_income table.
                        // Create product income object.
                        ProductIncome productIncome = new ProductIncome(selectedProduct.getId(), addedQuantity, selectedProduct.getBuyingPrice());
                        DBProduct_Income dbProductIncome = new DBProduct_Income();
                        dbProductIncome.insertRecord(productIncome);

                        // Close the scene and head back to the main scene.
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("MainScreen.fxml"));
                        Parent mainScreen = loader.load();
                        Scene mainScene = new Scene(mainScreen);
                        Stage winMain = (Stage)((Node)event.getSource()).getScene().getWindow();
                        winMain.setTitle("Main Screen");
                        winMain.setScene(mainScene);
                        winMain.show();
                    }
                }
            }
            catch (SQLException | IOException e) {
                System.out.println(e.getLocalizedMessage());
            }
        }
    }
    @FXML
    public void close(ActionEvent event) {
        try {
            // Close the scene and head back to the main scene.
            FXMLLoader loader = new FXMLLoader(getClass().getResource("MainScreen.fxml"));
            Parent mainScreen = loader.load();
            Scene mainScene = new Scene(mainScreen);
            Stage winMain = (Stage)((Node)event.getSource()).getScene().getWindow();
            winMain.setTitle("Main Screen");
            winMain.setScene(mainScene);
            winMain.show();
        } catch (IOException e) {
            System.out.println(e.getLocalizedMessage());
        }
    }
    private void setWarningAlert(String titleText, String headerText) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titleText);
        alert.setHeaderText(headerText);
        alert.showAndWait();
    }
    private ButtonType setConfirmationAlert(String titleText, String headerText) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(titleText);
        alert.setHeaderText(headerText);
        alert.showAndWait();
        return alert.getResult();
    }

    private int getAddedQuantity(){
        if (quantityTextField.getText().isEmpty()) return 0;
        return Integer.parseInt(quantityTextField.getText());
    }

    @FXML
    private void updateNewQuantityLabel() {
        newQuantity.setText("New Quantity: " + (selectedProduct.getQuantityInStock() + getAddedQuantity()));
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        quantityTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            updateNewQuantityLabel();
        });
    }
}
