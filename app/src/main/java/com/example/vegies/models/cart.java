package com.example.vegies.models;

public class cart {

     String productId,title,priceEach,price,quantity,id,timestamp;

    public cart() {
    }

    public cart(String productId, String title, String priceEach, String price, String quantity,String id,String timestamp) {
        this.id=id;
        this.productId = productId;
        this.title = title;
        this.priceEach = priceEach;
        this.price = price;
        this.quantity = quantity;
        this.timestamp = timestamp;
    }


    public  String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public  String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public  String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public  String getPriceEach() {
        return priceEach;
    }

    public void setPriceEach(String priceEach) {
        this.priceEach = priceEach;
    }

    public  String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public  String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public  String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
