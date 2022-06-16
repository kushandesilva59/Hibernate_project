package controller;

import Util.CrudUtil;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import model.Customer;
import org.apache.commons.lang3.ObjectUtils;

import java.io.IOException;
import java.sql.*;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Optional;
import java.util.regex.Pattern;

public class ViewCustomersFormController {

    public AnchorPane viewCustomersContext;
    public TableColumn colId;
    public TableColumn colName;
    public TableColumn colAddress;
    public TableColumn colContact;
    public TableColumn colEdit;
    public TableView <Customer> tblCustomers;
    public  Button btn;
    public Button btnSave;
    public Button btnDelete;
    public TextField txtId;
    public TextField txtName;
    public TextField txtAddress;
    public TextField txtContact;

    public void initialize(){
        colId.setCellValueFactory(new PropertyValueFactory("id"));
        colName.setCellValueFactory(new PropertyValueFactory("name"));
        colContact.setCellValueFactory(new PropertyValueFactory("contactNumber"));
        colAddress.setCellValueFactory(new PropertyValueFactory("address"));
        btnDelete.setDisable(true);


        try {
            loadAllCustomers();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        tblCustomers.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if(newValue != null){
                        setData(newValue);
                    }
                });
    }

    private void setData(Customer c) {
        if(!txtId.getText().equals(null)){
            btnSave.setText("Update");
        }
            txtId.setText(c.getId());
            txtName.setText(c.getName());
            txtContact.setText(c.getContactNumber());
            txtAddress.setText(c.getAddress());
            btnDelete.setDisable(false);
    }


    public void saveOnAction(ActionEvent event) throws SQLException, ClassNotFoundException {
        if(btnSave.getText().equals("Save")){
            if(checkId(txtId.getText())){
                new Alert(Alert.AlertType.ERROR,"Can't duplicate customer id!..").show();
            }else{
                    boolean b = CrudUtil.executeUpdate("INSERT INTO customers VALUES(?,?,?,?)",txtId.getText(),txtName.getText(),txtContact.getText(),txtAddress.getText());
                    if(b){
                        loadAllCustomers();
                        new Alert(Alert.AlertType.CONFIRMATION,"Saved!..").show();
                    }else{
                        new Alert(Alert.AlertType.ERROR,"Something went wrong!..").show();
                    }
            }
        }else{
            boolean isUpdate = CrudUtil.executeUpdate("UPDATE customers set name = ? , contactNumber = ? ,address = ? WHERE id = ?",
                    txtName.getText(),txtContact.getText(),txtAddress.getText(),txtId.getText());
            if(isUpdate){
                loadAllCustomers();
                new Alert(Alert.AlertType.CONFIRMATION,"Updated!..").show();
            }else{
                new Alert(Alert.AlertType.ERROR,"Something went wrong!..").show();

            }
        }
    }

    public void newOnAction(ActionEvent event) {
        btnSave.setText("Save");
        txtId.clear();
        txtName.clear();
        txtContact.clear();
        txtAddress.clear();
    }
    
    public void backOnAction(ActionEvent event) throws IOException {
        Stage stage = (Stage)viewCustomersContext.getScene().getWindow();
        stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("../view/adminForm.fxml"))));
    }

    private void loadAllCustomers() throws SQLException, ClassNotFoundException {
        ResultSet resultSet = CrudUtil.executeQuery("SELECT * FROM customers");
        ObservableList<Customer> customers = FXCollections.observableArrayList();

        while(resultSet.next()){
            customers.add(new Customer(
                    resultSet.getString("id"),
                    resultSet.getString("name"),
                    resultSet.getString("contactNumber"),
                    resultSet.getString("address")
            ));
        }
        tblCustomers.setItems(customers);
    }

    public boolean checkId(String id) throws SQLException, ClassNotFoundException {
        ResultSet resultSet = CrudUtil.executeQuery("SELECT * FROM customers");
        while(resultSet.next()){
            if(resultSet.getString("id").equals(id)){
                return true;
            }
        }
        return false;
    }

    public void deleteOnAction(ActionEvent event) {
        try {
            Optional<ButtonType> buttonType = new Alert(Alert.AlertType.INFORMATION, "Are you sure?", ButtonType.YES, ButtonType.NO).showAndWait();
            if(buttonType.get().equals(ButtonType.YES)){
                boolean b = CrudUtil.executeUpdate("DELETE FROM customers WHERE id = ?",txtId.getText());
                if(b){
                    txtId.clear();
                    txtName.clear();
                    txtContact.clear();
                    txtAddress.clear();
                    loadAllCustomers();
                    new Alert(Alert.AlertType.CONFIRMATION,"Deleted!..").show();
                }else{
                    new Alert(Alert.AlertType.ERROR,"Something went wrong!..").show();
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void idKeyReleased(KeyEvent keyEvent) {
        validate();

    }

    private Object validate() {
        LinkedHashMap <TextField,Pattern> map = new LinkedHashMap<>();

        Pattern idPattern = Pattern.compile("(C00-)[0-9]{3,5}$");
        Pattern namePattern = Pattern.compile("^[A-z ]{3,15}$");
        Pattern addressPattern = Pattern.compile("^[A-z0-9 ,/]{5,25}$");
        Pattern contactPattern = Pattern.compile("^[0-9]{3}-[0-9]{7}$");

        map.put(txtId,idPattern);
        map.put(txtName,namePattern);
        map.put(txtAddress,addressPattern);
        map.put(txtContact,contactPattern);

        for(TextField key : map.keySet()){
          Pattern pattern =  map.get(key);
            if(!pattern.matcher(key.getText()).matches()){
                setRed(key);
                return key;
            }
            setGreen(key);
        }
        btnSave.setDisable(false);
        return true;
    }

    private void setGreen(TextField textField) {
        if(textField.getLength() > 0) {
            textField.setStyle("-fx-border-color: #01ff00");

        }
    }

    private void setRed(TextField textField) {
        if(textField.getLength() > 0){
            textField.setStyle("-fx-border-color: #ff001b");
            btnSave.setDisable(true);
        }
    }

    public void txtNameKeyReleased(KeyEvent keyEvent) {
        validate();
    }

    public void txtAddressKeyReleased(KeyEvent keyEvent) {
        validate();
    }

    public void txtContactKeyReleased(KeyEvent keyEvent) {
        validate();
    }

}
