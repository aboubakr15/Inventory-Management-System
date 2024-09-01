package lightsaberInventory.View;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lightsaberInventory.Model.DBReceipt;
import lightsaberInventory.Model.Receipt;
import lightsaberInventory.Model.ReceiptItem;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class ReceiptItemsController implements Initializable {
    @FXML
    Button goBackButton;
    @FXML
    TableView<ReceiptItem> receiptItemsTable;
    @FXML
    TableColumn<ReceiptItem, String> productNameCol;
    @FXML
    TableColumn<ReceiptItem, Integer> productQuantityCol;
    @FXML
    TableColumn<ReceiptItem, Double> totalPriceCol;
    private Receipt receipt;

    public void setReceipt(Receipt receipt) {
        this.receipt = receipt;
    }

    public void setReceipt(int receipt_id) throws SQLException {
        DBReceipt dbReceipt = new DBReceipt();
        this.receipt = (Receipt) dbReceipt.getRecord(receipt_id);
    }

    @FXML
    public void goBack(ActionEvent event){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Go Back?");
        alert.setHeaderText("Are you sure you want to exit?");
        alert.setContentText("Press OK to exit the program. \nPress Cancel to stay on this screen.");
        alert.showAndWait();
        if (alert.getResult() == ButtonType.OK) {
            Node source = (Node) event.getSource();
            // Get the stage (window) associated with the button
            Stage stage = (Stage) source.getScene().getWindow();
            // Close the stage
            stage.close();
        }
        else {
            alert.close();
        }
    }

    public void updateTable() {
        DBReceipt dbReceipt = new DBReceipt();
        receiptItemsTable.setItems(dbReceipt.getReceiptDetails(receipt.getId()));
    }

    public void returnItem () throws SQLException {
        if (receiptItemsTable.getSelectionModel().getSelectedItem() == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("No Selection");
            alert.setHeaderText("Please select a receipt");
            alert.showAndWait();
        }else {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("ReturnItems.fxml"));
                Parent returnItems = loader.load();
                ReturnItemsController returnItemsController = loader.getController();
                returnItemsController.set_items(receiptItemsTable.getSelectionModel().getSelectedItem().getReceiptID()
                , receiptItemsTable.getSelectionModel().getSelectedItem().getProductID()
                , receiptItemsTable.getSelectionModel().getSelectedItem().getProductQuantity()
                );
                Scene returnItemsScene = new Scene(returnItems);
                Stage winAddPart = new Stage();
                winAddPart.setTitle("Return Item");
                winAddPart.initModality(Modality.APPLICATION_MODAL);
                winAddPart.setScene(returnItemsScene);
                winAddPart.showAndWait();
            } catch (IOException e) {
                System.out.println(e.getLocalizedMessage());
            }
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        productNameCol.setCellValueFactory(new PropertyValueFactory<>("productName"));
        productQuantityCol.setCellValueFactory(new PropertyValueFactory<>("productQuantity"));
        totalPriceCol.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
        // Call the method to populate the table
        Platform.runLater(this::updateTable);
    }
}
