package lightsaberInventory.View;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;
import lightsaberInventory.Model.*;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;


/** AddProduct controller of LightSaber Inventory application. */
public class AddProductController implements Initializable {


    @FXML
    TextField textID, textName, textStock, textModel, textUses, textBuyingPrice, textSellingPrice;

    @FXML
    Button saveButton, cancelButton;

    @FXML
    Button addPartButton;


    /** Displays MainScreen and does not save new product. The new product instance is abandoned.
     * @throws IOException failed to read the file */
    @FXML
    void setCancelButton(ActionEvent event) throws IOException {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Cancel?");
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

    };


    /** Saves new product and displays MainScreen. The new product instance is added to allProducts if it passes error checks.
     * Checks include, but are not limited to: Min is less than Inventory is less than Max, all textFields are filled in.
     * @param event An ActionEvent to help scene transition
     * @throws IOException failed to read the file
     * @throws NumberFormatException inputted  a string in a field designed for integers */
    @FXML
    public void saveProductButtonPressed(ActionEvent event) throws IOException, NumberFormatException {
        if (textName.getText().isEmpty()
                || textStock.getText().isEmpty()
                || textBuyingPrice.getText().isEmpty()
                || textSellingPrice.getText().isEmpty()
                || textModel.getText().isEmpty()
                || textUses.getText().isEmpty()) {

            System.out.println("Data Empty");
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Data Error");
            alert.setHeaderText("Please enter valid data for every field");
            alert.showAndWait();
        }

        else if (!Inventory.isNumeric(textBuyingPrice.getText())
                || !Inventory.isNumeric(textSellingPrice.getText())
                || !Inventory.isNumeric(textStock.getText())) {


            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Value Error");
            alert.setHeaderText("Stock, buying price and selling price should all be numeric");
            alert.showAndWait();

        }

        else {
            System.out.println("Data not empty");
            try {
                String name = textName.getText();
                String model = textModel.getText();
                String uses = textUses.getText();
                int quantityInStock = Integer.parseInt(textStock.getText());
                double buyingPrice = Double.parseDouble(textBuyingPrice.getText());
                double sellingPrice = Double.parseDouble(textSellingPrice.getText());

                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Add Product");
                alert.setHeaderText("Would like to save this Product to the inventory? ");
                alert.showAndWait();

                if (alert.getResult() == ButtonType.OK)  {

                    //add to database is missing
                    Product product = new Product(name, model, uses, quantityInStock, buyingPrice, sellingPrice);
                    DBProduct dbProduct = new DBProduct();
                    boolean success = dbProduct.insertRecord(product);
                    if (success) {
                        System.out.println("\nProduct Added Successfully\n");
                        Node source = (Node) event.getSource();
                        // Get the stage (window) associated with the button
                        Stage stage = (Stage) source.getScene().getWindow();
                        // Close the stage
                        stage.close();
                    }
                }
                else {
                    alert.close();
                }
            }
            catch (SQLException E) {
                System.out.println(E.getLocalizedMessage());
            }
            catch ( NumberFormatException E) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Type Error");
                alert.setHeaderText("Please format your inputs like the following:" +
                        "\nName: String" +
                        "\nModel: String" +
                        "\nUse: String" +
                        "\nSellingPrice: Double" +
                        "\nBuyingPrice: String");
                alert.showAndWait();
            }
        }
    }

    /** Loads the allPartsTable and assocPartsTable with columns names. Helper function do to any necessary data population after scene is loaded. */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

//        Product lastProduct = Inventory.getAllProducts().get(Inventory.getAllProducts().size() - 1);
//        int lastID = lastProduct.getId();
//        textID.setText(String.valueOf(++lastID));

    }
}
