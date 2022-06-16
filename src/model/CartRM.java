package model;

public class CartRM {
    private String productId;
    private int qty;
    private double unitPrice;
    private double totalCost;

    public CartRM(String productId, int qty, double unitPrice, double totalCost) {
        this.productId = productId;
        this.qty = qty;
        this.unitPrice = unitPrice;
        this.totalCost = totalCost;
    }

    public CartRM() {
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

    @Override
    public String toString() {
        return "CartRM{" +
                "productId='" + productId + '\'' +
                ", qty=" + qty +
                ", unitPrice=" + unitPrice +
                ", totalCost=" + totalCost +
                '}';
    }
}
