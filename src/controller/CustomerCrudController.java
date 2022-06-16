package controller;

import Util.CrudUtil;
import model.Customer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class CustomerCrudController {
    public static ArrayList<String> getCustomerIds() throws SQLException, ClassNotFoundException {
        ArrayList ids = new ArrayList();
        ResultSet resultSet = CrudUtil.executeQuery("SELECT * FROM customers");
        while(resultSet.next()){
            ids.add(resultSet.getString("id"));
        }
        return ids;
    }

    public static Customer getCustomer(String id) throws SQLException, ClassNotFoundException {
        ResultSet resultSet = CrudUtil.executeQuery("SELECT * FROM customers WHERE id = ?",id);
        if(resultSet.next()){
            Customer c = new Customer(resultSet.getString(1),
                    resultSet.getString(2),
                    resultSet.getString(3),
                    resultSet.getString(4)
            );
            return c;
        }
        return null;
    }
}
