package lightsaberInventory.View;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.prefs.Preferences;

public class ChangePasswordController {

    @FXML
    private TextField oldPasswordField;
    @FXML
    private TextField newPasswordField;
    @FXML
    private TextField confirmPasswordField;

    private static final String PASSWORD_KEY = "password";

    @FXML
    private void changePassword(ActionEvent event) throws IOException {
        String oldPassword = oldPasswordField.getText();
        String newPassword = newPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
            showAlert("Error", "Empty data", "Please fill all the fields.");
        } else {
            Preferences preferences = Preferences.userNodeForPackage(ChangePasswordController.class);
            String savedPassword = preferences.get(PASSWORD_KEY, "");

            if (!savedPassword.equals(oldPassword)) {
                showAlert("Error", "Incorrect Old Password", "Please enter the correct old password.");
            } else if (!newPassword.equals(confirmPassword)) {
                showAlert("Error", "Password Mismatch", "New password and confirm password do not match.");
            } else {
                // Update the password
                preferences.put(PASSWORD_KEY, newPassword);

                showAlert("Success", "Password Changed", "Your password has been changed successfully.");

                // Load the main screen
                FXMLLoader loader = new FXMLLoader(getClass().getResource("LoginScreen.fxml"));
                Parent mainScreen = loader.load();
                Scene mainScene = new Scene(mainScreen);

                Stage primaryStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                primaryStage.setTitle("Login");
                primaryStage.setScene(mainScene);
                primaryStage.show();
            }
        }
    }


    private void showAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    public void close(ActionEvent event) {
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
        } else {
            alert.close();
        }
    }
}
