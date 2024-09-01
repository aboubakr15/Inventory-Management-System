package lightsaberInventory.View;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.prefs.Preferences;

public class LoginScreen {

    private static final String PASSWORD_KEY = "password";

    @FXML
    private PasswordField passwordField;

    @FXML
    private void LoginButton(ActionEvent event) throws IOException {
        String password = passwordField.getText();
        String savedPassword = getPasswordFromPreferences();

        if (savedPassword.equals(password)) {
            // Load the main screen
            FXMLLoader loader = new FXMLLoader(getClass().getResource("MainScreen.fxml"));
            Parent mainScreen = loader.load();
            Scene mainScene = new Scene(mainScreen);

            Stage primaryStage = (Stage)((Node)event.getSource()).getScene().getWindow();
            primaryStage.setTitle("Main Screen");
            primaryStage.setScene(mainScene);
            primaryStage.setMaximized(true);
            primaryStage.show();
        } else {
            // Display error message
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Incorrect Password");
            alert.setContentText("Please enter the correct password.");
            alert.showAndWait();
        }
    }

    @FXML
    public void close(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Exit Inventory");
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

    private String getPasswordFromPreferences() {
        Preferences preferences = Preferences.userNodeForPackage(LoginScreen.class);
        return preferences.get(PASSWORD_KEY, "");
    }

    public void changePassword(ActionEvent event) throws IOException {
        // Load the main screen
        FXMLLoader loader = new FXMLLoader(getClass().getResource("ChangePassword.fxml"));
        Parent mainScreen = loader.load();
        Scene mainScene = new Scene(mainScreen);
        Stage primaryStage = (Stage)((Node)event.getSource()).getScene().getWindow();
        primaryStage.setTitle("Change Password");
        primaryStage.setScene(mainScene);
        primaryStage.show();
    }
}
