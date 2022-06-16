package controller;

import Util.CrudUtil;
import model.Product;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ProductCrudController {
    public static ArrayList<String> getProductIds() throws SQLException, ClassNotFoundException {
        ArrayList itemIds = new ArrayList();
        ResultSet resultSet = CrudUtil.executeQuery("SELECT * FROM products");
        while(resultSet.next()){
            itemIds.add(resultSet.getString("id"));
        }
        return itemIds;
    }

    public static Product getProduct(String id) throws SQLException, ClassNotFoundException {
        ResultSet resultSet = CrudUtil.executeQuery("SELECT * FROM products WHERE id = ?", id);
        if(resultSet.next()){
            Product p = new Product(
                    resultSet.getString(1),
                    resultSet.getString(2),
                    Integer.valueOf(resultSet.getString(3)),
                    resultSet.getString(4),
                    Double.valueOf(resultSet.getString(5))

                    );
            return p;
        }
        return null;
    }
}
