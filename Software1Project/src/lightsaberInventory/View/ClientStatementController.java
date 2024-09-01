package lightsaberInventory.View;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lightsaberInventory.Model.*;
import javafx.scene.control.DatePicker;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class ClientStatementController implements Initializable {
    @FXML
    Label clientName;
    @FXML
    TableView<Receipt> statementTable = new TableView<>();
    @FXML
    Button showDetailsButton, closeButton;
    @FXML
    TableColumn<Receipt, String> dateCol, totalPriceCol;
    @FXML
    private DatePicker startDatePicker, endDatePicker;
    private Client client;
    private boolean selection = true;

    public void setSelection(boolean selection) {
        this.selection = selection;
    }

    //related to monthly sales
    double totalBuyingPrice,totalSellingPrice, totalProfit;
    @FXML
    Label totalbuyingPLabel;
    @FXML
    Label totalsellingPLabel;
    @FXML
    Label totalProfitLabel;
    DBReceipt dbreceipt = new DBReceipt();

    public void setClient(Client client) {
        this.client = client;
        clientName.setText(client.getName());
    }

    @FXML
    private void handleFilterButtonAction() throws SQLException {
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();
        // Get the selected start and end dates
        if (endDate == null){
            endDate = LocalDate.now();
        }

        totalBuyingPrice = (dbreceipt.getSumOfBuyingPricesInDateRange(startDate, endDate));
        totalSellingPrice = (dbreceipt.getSumOfAllReceiptsPricesInDateRange(startDate, endDate));
        totalProfit = (dbreceipt.calculateProfitInDateRange(startDate, endDate));

        DecimalFormat df = new DecimalFormat("###,###,###.# EGP");

        totalbuyingPLabel.setText(df.format(totalBuyingPrice));
        totalsellingPLabel.setText(df.format(totalSellingPrice));
        totalProfitLabel.setText(df.format(totalProfit));
        statementTable.setItems(dbreceipt.getReceiptsByDateRange(startDate, endDate));
    }
    @FXML
    private void handleFilterButtonActionForClient() {
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();
        // Get the selected start and end dates
        if (endDate == null){
            endDate = LocalDate.now();
        }
        statementTable.setItems(dbreceipt.getReceiptsByDateRangeForClient(client.getId(), startDate, endDate));
    }
    @FXML
    public void closeWindow() throws IOException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Cancel?");
        alert.setHeaderText("Are you sure you want to exit?");
        alert.setContentText("Press OK to exit the program. \nPress Cancel to stay on this screen.");
        alert.showAndWait();
        if (alert.getResult() == ButtonType.OK) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("MainScreen.fxml"));
            Parent mainScreen = loader.load();
            Scene mainScreenScene = new Scene(mainScreen);
            Stage stage = (Stage) closeButton.getScene().getWindow();
            stage.setTitle("Sabtan");
            stage.setScene(mainScreenScene);
            stage.show();
        }
        else {
            alert.close();
        }

    }
    @FXML
    public void showDetails() {
        if (statementTable.getSelectionModel().getSelectedItem() == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("No Selection");
            alert.setHeaderText("Please select a receipt");
            alert.showAndWait();
        }
        else {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("ReceiptItems.fxml"));
                Parent addPartScreen = loader.load();
                ReceiptItemsController receiptItemsController = loader.getController();
                receiptItemsController.setReceipt(statementTable.getSelectionModel().getSelectedItem());
                Scene addPartScene = new Scene(addPartScreen);
                Stage winAddPart = new Stage();
                winAddPart.setTitle("Show Details");
                winAddPart.initModality(Modality.APPLICATION_MODAL);
                winAddPart.setScene(addPartScene);
                winAddPart.showAndWait();
            } catch (IOException e) {
                System.out.println(e.getLocalizedMessage());
            }
        }
    }

    public void updateTable(boolean selection) {
        if(selection){
            try {
                DBReceipt dbReceipt = new DBReceipt();
                statementTable.setItems(dbReceipt.getAllRecordsForClient(client.getId()));
            } catch (SQLException e) {
                System.out.println("Error updating table: " + e.getMessage());
            }
        }else {
            try {
                DBReceipt dbReceipt = new DBReceipt();
                statementTable.setItems(dbReceipt.getAllRecords());
            } catch (SQLException e) {
                System.out.println("Error updating table for all clients: " + e.getMessage());
            }
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        totalPriceCol.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));

        Platform.runLater(() -> {
            updateTable(selection);  // Call the method to populate the table
            if (!selection){
                try {
                    handleFilterButtonAction();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
}