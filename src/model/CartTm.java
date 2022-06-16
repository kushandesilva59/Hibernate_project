package model;

import javafx.scene.control.Button;

public class CartTm {
    private String productId;
    private int qty;
    private double unitPrice;
    private double totalCost;
    private Button btn;

    public CartTm() {
    }

    public CartTm(String productId, int qty, double unitPrice, double totalCost, Button btn) {
        this.productId = productId;
        this.qty = qty;
        this.unitPrice = unitPrice;
        this.totalCost = totalCost;
        this.btn = btn;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }

    public Button getBtn() {
        return btn;
    }

    public void setBtn(Button btn) {
        this.btn = btn;
    }

    @Override
    public String toString() {
        return "CartTm{" +
                "productId='" + productId + '\'' +
                ", qty=" + qty +
                ", unitPrice=" + unitPrice +
                ", totalCost=" + totalCost +
                ", btn=" + btn +
                '}';
    }
}
