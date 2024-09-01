package lightsaberInventory.Model;

import javafx.collections.ObservableList;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class Receipt {
    private int id;
    private ArrayList<Product> products;
    private double totalPrice = 0;
    private int clientID;
    private String date = "";

    public Receipt(ArrayList<Product> products, int clientID, double totalPrice) {
        this.products = products;
        this.clientID = clientID;
        this.totalPrice = totalPrice;
        getCurrentTime();
    }

    public Receipt(int id, ArrayList<Product> products, double totalPrice, int clientID) {
        this.id = id;
        this.products = products;
        this.totalPrice = totalPrice;
        this.clientID = clientID;
    }

    public Receipt(int id, double totalPrice, int clientID, LocalDate date) {
        this.id = id;
        this.totalPrice = totalPrice;
        this.clientID = clientID;
        this.date = String.valueOf(date);
    }

    public Receipt(int id, ArrayList<Product> products, double totalPrice, int clientID, LocalDate date) {
        this.id = id;
        this.products = products;
        this.totalPrice = totalPrice;
        this.clientID = clientID;
        this.date = String.valueOf(date);
    }


    public void getCurrentTime() {
        DateTimeFormatter dtf;
        LocalDateTime now;
        dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd @ (HH:mm:ss)");
        now = LocalDateTime.now();
        this.date = dtf.format(now);
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getId() {
        // Get the previous ID from the database.
        // Generate new unique ID.
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ArrayList<Product> getProducts() {
        return products;
    }

    public void setProducts(ArrayList<Product> products) {
        this.products = products;
    }

    public int getClientID() {
        return clientID;
    }

    public void setClientID(int clientID) {
        this.clientID = clientID;
    }
}
