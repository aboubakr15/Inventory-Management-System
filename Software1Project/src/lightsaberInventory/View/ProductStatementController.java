package lightsaberInventory.View;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import lightsaberInventory.Model.*;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class ProductStatementController implements Initializable {
    @FXML
    Label productName, productModel, productUses, totalSoldQuantity, totalBoughtQuantity;
    @FXML
    TableView<ProductStatement> statementSoldTable = new TableView<>();
    @FXML
    TableView<ProductIncome> statementBoughtTable = new TableView<>();
    @FXML
    Button closeButton;
    @FXML
    TableColumn<ProductStatement, String> dateSoldCol, clientNameSoldCol, quantitySoldCol, priceCol;
    @FXML
    TableColumn<ProductIncome, String> dateBoughtCol, quantityBoughtCol, priceIncomeCol;
    private Product product;
    @FXML
    private DatePicker startDatePicker;
    @FXML
    private DatePicker endDatePicker;

    DBReceipt dbReceipt = new DBReceipt();
    DBProduct_Income dbProductIncome = new DBProduct_Income();

    public void setProduct(Product product) {
        this.product = product;
        productName.setText("Part: " + product.getName());
        productModel.setText("Model: " + product.getModel());
        productUses.setText("Uses: " + product.getUses());
        totalBoughtQuantity.setText("Total Bought Quantity: " + product.getBoughtQuantity());
        totalSoldQuantity.setText("Total Sold Quantity: " + product.getSoldQuantity());
    }

    @FXML
    private void handleFilterButtonActionForProduct() {
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();

        if (endDate == null) {
            endDate = LocalDate.now();
        }
        // Update your UI with the filtered products
        ObservableList<ProductStatement> filteredProducts = dbReceipt.getReceiptItemsForProductInDateRange(product.getId(), startDate, endDate);
        ObservableList<ProductIncome>  filteredIncomeProducts = dbProductIncome.getRecordsInDateRange(product.getId(), startDate,endDate);
        statementSoldTable.setItems(filteredProducts);
        statementBoughtTable.setItems(filteredIncomeProducts);
    }

    @FXML
    public void closeWindow() throws IOException {
        // Close and head to the main menu.
        FXMLLoader loader = new FXMLLoader(getClass().getResource("MainScreen.fxml"));
        Parent mainScreen = loader.load();
        Scene mainScreenScene = new Scene(mainScreen);
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.setTitle("Sabtan");
        stage.setScene(mainScreenScene);
        stage.show();
    }

    public void updateTable() {
        statementSoldTable.setItems(dbReceipt.getReceiptItemsForProduct(product.getId()));
        statementBoughtTable.setItems(dbProductIncome.getRecords(product.getId()));
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Adjust Statement Sold Table
        dateSoldCol.setCellValueFactory(new PropertyValueFactory<>("receipt_date"));
        clientNameSoldCol.setCellValueFactory(new PropertyValueFactory<>("client_name"));
        quantitySoldCol.setCellValueFactory(new PropertyValueFactory<>("receipt_items_product_quantity"));
        priceCol.setCellValueFactory(new PropertyValueFactory<>("product_total_bought_quantity"));

        // Adjust Statement Bought Table
        dateBoughtCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        quantityBoughtCol.setCellValueFactory(new PropertyValueFactory<>("addedQuantity"));
        priceIncomeCol.setCellValueFactory(new PropertyValueFactory<>("productIncomePrice"));

        // Call the method to populate the table
        Platform.runLater(this::updateTable);
    }
}
