package lightsaberInventory.Model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ProductIncome {
    private int id;
    private int product_id;
    private int addedQuantity;
    private double productIncomePrice;
    private String date;

    public ProductIncome(int id, int product_id, int addedQuantity, String date, double productIncomePrice) {
        this.id = id;
        this.product_id = product_id;
        this.addedQuantity = addedQuantity;
        this.date = date;
        this.productIncomePrice = productIncomePrice;
    }

    public ProductIncome(int product_id, int addedQuantity, double productIncomePrice) {
        this.product_id = product_id;
        this.addedQuantity = addedQuantity;
        this.productIncomePrice = productIncomePrice;
        getCurrentTime();
    }

    private void getCurrentTime() {
        DateTimeFormatter dtf;
        LocalDate now;
        dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        now = LocalDate.now();
        this.date = dtf.format(now);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public int getProduct_id() {
        return product_id;
    }

    public void setProduct_id(int product_id) {
        this.product_id = product_id;
    }

    public int getAddedQuantity() {
        return addedQuantity;
    }

    public void setAddedQuantity(int addedQuantity) {
        this.addedQuantity = addedQuantity;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getProductIncomePrice() {
        return productIncomePrice;
    }

    public void setProductIncomePrice(double productIncomePrice) {
        this.productIncomePrice = productIncomePrice;
    }
}
