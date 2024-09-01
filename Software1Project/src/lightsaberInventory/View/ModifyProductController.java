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
import javafx.stage.Stage;
import lightsaberInventory.Model.*;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;


/** ModifyProduct controller of LightSaber Inventory application. */
public class ModifyProductController implements Initializable {

    Product displayedProduct;

    public void setDisplayedProduct(Product displayedProduct) {
        this.displayedProduct = displayedProduct;
    }

    @FXML
    TextField textName;

    @FXML
    TextField textInventory;

    @FXML
    TextField textModel;

    @FXML
    TextField textUses;

    @FXML
    TextField textBuyingPrice;

    @FXML
    TextField textSellingPrice;

    @FXML
    Button saveButton;

    @FXML
    Button cancelButton;

    @FXML
    void setCancelButton(ActionEvent event){

        try {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Cancel?");
            alert.setHeaderText("Are you sure you want to exit?");
            alert.setContentText("Press OK to exit the program. \nPress Cancel to stay on this screen.");
            alert.showAndWait();
            if (alert.getResult() == ButtonType.OK) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("MainScreen.fxml"));
                Parent addPartScreen = loader.load();
                Scene addPartScene = new Scene(addPartScreen);
                Stage winAddPart = (Stage)((Node)event.getSource()).getScene().getWindow();
                winAddPart.setTitle("Add Client");
                winAddPart.setScene(addPartScene);
                winAddPart.show();
            }
            else {
                alert.close();
            }
        }
        catch (IOException E) {
            System.out.println(E.getLocalizedMessage());
        }
    }

    @FXML
    public void saveProductButtonPressed(ActionEvent event) {
        if (textName.getText().isEmpty()
                || textInventory.getText().isEmpty()
                || textBuyingPrice.getText().isEmpty()
                || textSellingPrice.getText().isEmpty()
                || textUses.getText().isEmpty()
                || textModel.getText().isEmpty()) {

            System.out.println("Data Empty");
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Data Error");
            alert.setHeaderText("Please enter valid data for every field");
            alert.showAndWait();
        }
        else if (!Inventory.isNumeric(textSellingPrice.getText())
                || !Inventory.isNumeric(textBuyingPrice.getText())
                || !Inventory.isNumeric(textInventory.getText())) {

            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Value Error");
            alert.setHeaderText("Quantity, Selling Price, and Buying Price should all be numeric");
            alert.showAndWait();
        }
        else {
            System.out.println("Data not empty");
            try {
                String name = textName.getText();
                String model = textModel.getText();
                String uses = textUses.getText();
                int quantityInStock = Integer.parseInt(textInventory.getText());
                double buyingPrice = Double.parseDouble(textBuyingPrice.getText());
                double sellingPrice = Double.parseDouble(textSellingPrice.getText());

                displayedProduct.setName(name);
                displayedProduct.setModel(model);
                displayedProduct.setUses(uses);
                displayedProduct.setQuantityInStock(quantityInStock);
                displayedProduct.setSellingPrice(sellingPrice);

                if (quantityInStock == 0){
                    displayedProduct.setBuyingPrice(buyingPrice);
                }else {
                    displayedProduct.setBuyingPrice(displayedProduct.getBuyingPrice());
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Type Error");
                    alert.setHeaderText("Can't change buying price because it's still in stock");
                    alert.showAndWait();
                }

                DBProduct dbProduct = new DBProduct();
                boolean success = dbProduct.updateRecord(displayedProduct);


                if (success) {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("MainScreen.fxml"));
                    Parent addPartScreen = loader.load();
                    Scene addPartScene = new Scene(addPartScreen);
                    Stage winAddPart = (Stage)((Node)event.getSource()).getScene().getWindow();
                    winAddPart.setTitle("Main Screen");
                    winAddPart.setScene(addPartScene);
                    winAddPart.show();
                }
            }
            catch (IOException E) {
                System.out.println(E.getLocalizedMessage());
            }
            catch ( NumberFormatException E) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Type Error");
                alert.setHeaderText("""
                        Please format your inputs like the following:
                        Name: String
                        Model: String
                        Use: String
                        SellingPrice: Double
                        BuyingPrice: String""");
                alert.showAndWait();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        Platform.runLater(() -> {
            textName.setText(displayedProduct.getName());
            textInventory.setText(String.valueOf(displayedProduct.getQuantityInStock()));
            textModel.setText(String.valueOf((displayedProduct.getModel())));
            textUses.setText(String.valueOf((displayedProduct.getUses())));
            textBuyingPrice.setText(String.valueOf(displayedProduct.getBuyingPrice()));
            textSellingPrice.setText(String.valueOf(displayedProduct.getSellingPrice()));
        });
    }
}
