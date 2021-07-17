package com.example.vegies.models;

public class ModelOrderSellerDetails {
    private String pId,title,cost,price,quantity;

    public ModelOrderSellerDetails() {
    }

    public ModelOrderSellerDetails(String pId, String title, String cost, String price, String quantity) {
        this.pId = pId;
        this.title = title;
        this.cost = cost;
        this.price = price;
        this.quantity = quantity;
    }

    public String getpId() {
        return pId;
    }

    public void setpId(String pId) {
        this.pId = pId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }
}
