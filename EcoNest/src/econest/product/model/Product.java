/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/*
 * @author P. U. Liyanage
 * @sanumber SA26610056
 * @group EcoNest
 */
public class Product {
    private String productCode, name, description, price;
    double rating;

    //Parameterized Constructor
    public Product(String productCode, String name, String description, String price, double rating) {
        this.productCode = productCode;
        this.name = name;
        this.description = description;
        this.price = price;
        this.rating = rating;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }
    
    
    
}
