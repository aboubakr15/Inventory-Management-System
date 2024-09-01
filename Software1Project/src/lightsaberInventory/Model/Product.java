package lightsaberInventory.Model;

import javafx.collections.ObservableList;

import java.time.LocalDate;

/** The Product class defines methods and attributes for Products. */

public class Product {
    private int id;
    private String name, model, uses;
    private int quantity;
    private int quantityInStock, soldQuantity, boughtQuantity;
    private double buyingPrice, sellingPrice;
//    private double price;

    public Product(int id, String name,String model,String uses, int quantityInStock, double buyingPrice, double sellingPrice, int soldQuantity, int boughtQuantity) {
        this.id = id;
        this.name = name;
        this.model = model;
        this.uses = uses;
        this.quantityInStock = quantityInStock;
        this.buyingPrice = buyingPrice;
        this.sellingPrice = sellingPrice;
        this.soldQuantity = soldQuantity;
        this.boughtQuantity = boughtQuantity;
    }
    public Product(String name,String model,String uses, int quantityInStock, double buyingPrice, double sellingPrice) {
        this.name = name;
        this.model = model;
        this.uses = uses;
        this.quantityInStock = quantityInStock;
        this.buyingPrice = buyingPrice;
        this.sellingPrice = sellingPrice;
        this.soldQuantity = 0;
        this.boughtQuantity = quantityInStock;
    }

    public Product(int id, String name, int quantity, double sellingPrice, String model) {
        this.id = id;
        this.name = name;
        this.model = model;
        this.quantity = quantity;
        this.sellingPrice = sellingPrice;
    }

    public Product(int id, String name, int quantity, double sellingPrice, double buyingPrice, String model) {
        this.id = id;
        this.name = name;
        this.model = model;
        this.quantity = quantity;
        this.sellingPrice = sellingPrice;
        this.buyingPrice = buyingPrice;
    }



    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getSoldQuantity() {
        return soldQuantity;
    }

    public void setSoldQuantity(int soldQuantity) {
        this.soldQuantity = soldQuantity;
    }

    public int getBoughtQuantity() {
        return boughtQuantity;
    }

    public void setBoughtQuantity(int boughtQuantity) {
        this.boughtQuantity = boughtQuantity;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getUses() {
        return uses;
    }

    public void setUses(String model) {
        this.uses = model;
    }

    public int getQuantityInStock() {
        return quantityInStock;
    }

    public void setQuantityInStock(int quantityInStock) {
        this.quantityInStock = quantityInStock;
    }

    public double getBuyingPrice() {
        return buyingPrice;
    }

    public void setBuyingPrice(double buyingPrice) {
        this.buyingPrice = buyingPrice;
    }

    public double getSellingPrice() {
        return sellingPrice;
    }

    public void setSellingPrice(double sellingPrice) {
        this.sellingPrice = sellingPrice;
}
}
