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
import lightsaberInventory.Model.Client;
import lightsaberInventory.Model.DBClient;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;


/** ModifyPart controller of LightSaber Inventory application. */
public class ModifyClientController implements Initializable{

    public Client displayedClient;

    @FXML
    Label clientName;

    @FXML
    TextField textName;

    @FXML
    TextField textPhoneNumber;

    @FXML
    TextField textAddress;

    @FXML
    Button saveButton;

    @FXML
    Button cancelButton;

    public void setClient(Client client) {
        this.displayedClient = client;
    }

    /** Saves modifications to part and displays MainScreen. The old part is replaced by new part at the index of old part .
     * Checks include, but are not limited to: Min is less than Inventory is less than Max, all textFields are filled in.
     * @param event An ActionEvent to help scene transition
     * @throws IOException failed to read the file
     * @throws NumberFormatException inputted a string in a field designed for integers */
    @FXML
    public void saveMod(ActionEvent event) throws IOException, NumberFormatException {

        if (textName.getText().isEmpty()
                || textPhoneNumber.getText().isEmpty()
                || textAddress.getText().isEmpty()) {

            System.out.println("Data Empty");
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Data Error");
            alert.setHeaderText("Please enter valid data for every field");
            alert.showAndWait();
        }else {
            try {
                String name = textName.getText();
                String phoneNumber = textPhoneNumber.getText();
                String address = textAddress.getText();

                displayedClient.setName(name);
                displayedClient.setPhoneNumber(phoneNumber);
                displayedClient.setAddress(address);

                DBClient dbClient = new DBClient();
                boolean success = dbClient.updateRecord(displayedClient);

                if (success) {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("MainScreen.fxml"));
                    Parent addPartScreen = loader.load();
                    Scene addPartScene = new Scene(addPartScreen);
                    Stage winAddPart = (Stage)((Node)event.getSource()).getScene().getWindow();
                    winAddPart.setTitle("Add Part");
                    winAddPart.setScene(addPartScene);
                    winAddPart.show();
                    }
                } catch (NumberFormatException E) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Type Error");
                alert.setHeaderText("Please format your inputs like the following:" +
                        "\nName: String" +
                        "\nPhone: Number" +
                        "\nAddress: String");
                alert.showAndWait();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @FXML
    void setCancelButton(ActionEvent event) {

        try {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Cancel?");
            alert.setHeaderText("Are you sure you want to exit?");
            alert.setContentText("Press OK to discard edits.");
            alert.showAndWait();
            if (alert.getResult() == ButtonType.OK) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("MainScreen.fxml"));
                Parent addPartScreen = loader.load();
                Scene addPartScene = new Scene(addPartScreen);
                Stage winAddPart = (Stage)((Node)event.getSource()).getScene().getWindow();
                winAddPart.setTitle("Add Part");
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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Platform.runLater(() -> {
            clientName.setText(displayedClient.getName());
            textName.setText(displayedClient.getName());  // Call the method to populate the table
            textAddress.setText(displayedClient.getAddress());  // Call the method to populate the table
            textPhoneNumber.setText(displayedClient.getPhoneNumber());  // Call the method to populate the table
        });
    }
}
