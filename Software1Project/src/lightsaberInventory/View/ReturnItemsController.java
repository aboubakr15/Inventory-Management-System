package lightsaberInventory.View;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;
import lightsaberInventory.Model.*;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class ReturnItemsController implements Initializable {

    @FXML
    Label productName;
    @FXML
    TextField quantityTextField;
    @FXML
    Button addButton, cancelButton;

    private int receipt_id, product_id, quantity_bought;

    public void set_items (int receipt_id, int product_id, int quantity_bought){
        this.receipt_id = receipt_id;
        this.product_id = product_id;
        this.quantity_bought = quantity_bought;
    }

    @FXML
    public void returnQuantity(ActionEvent event) throws SQLException {

        // Check if the input field is empty.
        if (quantityTextField.getText().isEmpty()) {
            // Alert that the field is empty.
            setWarningAlert("Please enter quantity");
        }
        // Check if the input field not numeric.
        else if (!Inventory.isNumeric(quantityTextField.getText())) {
            // Alert that the field not numeric.
            setWarningAlert("Please enter a number");
        }
        //check quantity returned is less than quantity bought
        else if (quantity_bought < Integer.parseInt(quantityTextField.getText())) {
            // Alert that the field not numeric.
            setWarningAlert("Please enter a number less than or equal: " + quantity_bought);
        }
        else {
            DBReceipt dbReceipt = new DBReceipt();
            if(dbReceipt.returnItemDB(receipt_id, product_id, Integer.parseInt(quantityTextField.getText()), quantity_bought)){
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Done");
                alert.setHeaderText("Product is returned");
                alert.showAndWait();

                Node source = (Node) event.getSource();
                // Get the stage (window) associated with the button
                Stage stage = (Stage) source.getScene().getWindow();
                // Close the stage
                stage.close();
            }else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Something went wrong");
                alert.showAndWait();
            }
        }

    }

    @FXML
    public void close(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("cancel?");
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
    private void setWarningAlert(String headerText) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Data Error");
        alert.setHeaderText(headerText);
        alert.showAndWait();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}

