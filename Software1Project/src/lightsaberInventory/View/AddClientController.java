package lightsaberInventory.View;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import lightsaberInventory.Model.Client;
import lightsaberInventory.Model.DBClient;
import lightsaberInventory.Model.Inventory;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

/** AddPart controller of LightSaber Inventory application. */
public class AddClientController implements Initializable {
    @FXML
    AnchorPane addClientPain;

    @FXML
    TextField textID, textName, textPhoneNumber, textAddress;

    @FXML
    TextField textDualPurpose;

    @FXML
    Button addButton, cancelButton;


    /** Displays MainScreen and does not save user changes. The new Client instance is abandoned.
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

    /** Saves new part and displays MainScreen. The new part instance is added to allParts if it passes error checks.
     * Checks include, but are not limited to: Min is less than Inventory is less than Max, all textFields are filled in.
     * @throws IOException failed to read the file
     * @throws NumberFormatException inputted a string in a field designed for integers
     * @param event An ActionEvent to help scene transition*/
    @FXML
    public void addClientButtonPressed(ActionEvent event) throws IOException, NumberFormatException {
        if (textName.getText().isEmpty()
                || textPhoneNumber.getText().isEmpty()
                || textAddress.getText().isEmpty()) {

            System.out.println("Data Empty");
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Data Error");
            alert.setHeaderText("Please enter valid data for every field");
            alert.showAndWait();
        }
        else {
            System.out.println("Data not empty");
            try {
                String name = textName.getText();
                String phoneNumber = textPhoneNumber.getText();
                String address = textAddress.getText();

                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Add Client");
                alert.setHeaderText("Would like to save this client to the inventory? ");
                alert.showAndWait();

                if (alert.getResult() == ButtonType.OK)  {
                    // Create client object.
                    Client client = new Client(name, phoneNumber, address);

                    // Add the client to database
                    DBClient dbClient = new DBClient();
                    boolean success = dbClient.insertRecord(client);
                    if (success) {
                        System.out.println("\nAdded Successfully\n");
                        // Go back to the main screen.
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
            } catch (NumberFormatException E) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Type Error");
                alert.setHeaderText("Please format your inputs like the following:" +
                        "\nName: Text" +
                        "\nPhone: 11 Numbers" +
                        "\nAddress: Text");
                alert.showAndWait();
            } catch (SQLException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("DB Error");
                alert.setHeaderText("Something went wrong");
                alert.showAndWait();
            }


        }
    }

    /** Sets InHouseRadio to selected. Initializes a part ID Helper function do to any necessary data population after scene is loaded. */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (!Inventory.getAllClients().isEmpty()) {
            Client lastClient = Inventory.getAllClients().get(Inventory.getAllClients().size() - 1);
            int lastID = lastClient.getId();
            textID.setText(String.valueOf(++lastID));
        }
    }
}