package lightsaberInventory.Model;

import java.time.LocalDate;

public class ProductStatement {
    private LocalDate receipt_date;
    private String client_name;
    private int receipt_items_product_quantity;
    private double product_total_bought_quantity;

    public ProductStatement(LocalDate receipt_date, String client_name, int receipt_items_product_quantity, double product_total_bought_quantity) {
        this.receipt_date = receipt_date;
        this.client_name = client_name;
        this.receipt_items_product_quantity = receipt_items_product_quantity;
        this.product_total_bought_quantity = product_total_bought_quantity;
    }

    public LocalDate getReceipt_date() {
        return receipt_date;
    }

    public void setReceipt_date(LocalDate receipt_date) {
        this.receipt_date = receipt_date;
    }

    public String getClient_name() {
        return client_name;
    }

    public void setClient_name(String client_name) {
        this.client_name = client_name;
    }

    public int getReceipt_items_product_quantity() {
        return receipt_items_product_quantity;
    }

    public void setReceipt_items_product_quantity(int receipt_items_product_quantity) {
        this.receipt_items_product_quantity = receipt_items_product_quantity;
    }

    public double getProduct_total_bought_quantity() {
        return product_total_bought_quantity;
    }

    public void setProduct_total_bought_quantity(double product_total_bought_quantity) {
        this.product_total_bought_quantity = product_total_bought_quantity;
    }

}
