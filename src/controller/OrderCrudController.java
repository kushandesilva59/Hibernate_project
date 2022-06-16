package controller;

import Util.CrudUtil;
import model.Order;
import model.OrderDetail;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class OrderCrudController {
    public boolean saveOrder(Order order) throws SQLException, ClassNotFoundException {
       return CrudUtil.executeUpdate("INSERT INTO orders VALUES(?,?,?)",order.getId(),order.getDate(),order.getCustomerId());
    }

    public boolean saveOrderDetails(ArrayList <OrderDetail> details) throws SQLException, ClassNotFoundException {
        for(OrderDetail det : details){
            boolean isSave = CrudUtil.executeUpdate("INSERT INTO orderDetails VALUES(?,?,?,?)",det.getOrderId(),det.getProductId(),Integer.valueOf(det.getQty()),Double.valueOf(det.getUnitPrice()));
            if(isSave){
                if(!updateQty(det.getProductId(),det.getQty())){
                    return false;
                }
            }else{
                return false;
            }
        }
        return true;
    }

    private boolean updateQty(String productId, int qty) throws SQLException, ClassNotFoundException {
        return CrudUtil.executeUpdate("UPDATE products SET qtyOnHand = qtyOnHand - ? WHERE id = ?",qty,productId);
    }

    public static String getOrderId() throws SQLException, ClassNotFoundException {
        ResultSet resultSet = CrudUtil.executeQuery("SELECT id FROM orders ORDER BY id  DESC LIMIT 1 ");
        if(resultSet.next()){
            String label = resultSet.getString("id");
            String [] split = label.split("OR");
            Integer num = Integer.valueOf(split[1])+1;
            String id = String.format("OR%03d",num);
            return id;
        }else{
            return "OR001";
        }
    }
}
