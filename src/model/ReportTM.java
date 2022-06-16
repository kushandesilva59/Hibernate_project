package model;

public class ReportTM {
    private String orderId;
    private String date;
    private double totalCost;

    public ReportTM() {
    }

    public ReportTM(String orderId, String date, double totalCost) {
        this.orderId = orderId;
        this.date = date;
        this.totalCost = totalCost;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }

    @Override
    public String toString() {
        return "ReportTM{" +
                "orderId='" + orderId + '\'' +
                ", date='" + date + '\'' +
                ", totalCost=" + totalCost +
                '}';
    }
}
