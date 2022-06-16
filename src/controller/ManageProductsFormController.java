package controller;

import Util.CrudUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import model.Product;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ManageProductsFormController {
    public AnchorPane manageProductsContext;
    public TableColumn colId;
    public TableColumn colName;
    public TableColumn colQTYOnhand;
    public TableColumn colUnit;
    public TableColumn colUnitPrice;
    public Button btnDelete;
    public TableView <Product>tblProducts;
    public Button btnSave;
    public TextField txtId;
    public TextField txtName;
    public TextField txtQtyOnHand;
    public TextField txtUnit;
    public TextField txtUnitPrice;

    public void initialize() throws SQLException, ClassNotFoundException {
        colId.setCellValueFactory(new PropertyValueFactory("id"));
        colName.setCellValueFactory(new PropertyValueFactory("name"));
        colQTYOnhand.setCellValueFactory(new PropertyValueFactory("qtyOnHand"));
        colUnit.setCellValueFactory(new PropertyValueFactory("unit"));
        colUnitPrice.setCellValueFactory(new PropertyValueFactory("unitPrice"));
        btnDelete.setDisable(true);
        btnSave.setDisable(true);

        loadAllProducts();

        tblProducts.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue != null){
                    setData(newValue);
            }
        }
        );
    }

    private void setData(Product s) {
        if(!txtId.getText().equals(null)){
            txtId.setText(s.getId());
            txtName.setText(s.getName());
            txtQtyOnHand.setText(String.valueOf(s.getQtyOnHand()));
            txtUnit.setText(s.getUnit());
            txtUnitPrice.setText(String.valueOf(s.getUnitPrice()));
            btnDelete.setDisable(false);
            btnSave.setDisable(false);
            btnSave.setText("Update");
        }
    }

    private void loadAllProducts() throws SQLException, ClassNotFoundException {
        ResultSet resultSet = CrudUtil.executeQuery("SELECT * FROM products");
        ObservableList <Product> products = FXCollections.observableArrayList();

        while(resultSet.next()){
            products.add(new Product(
                    resultSet.getString("id"),
                    resultSet.getString("name"),
                    Integer.valueOf(resultSet.getString("qtyOnHand")),
                    resultSet.getString("unit"),
                    Double.valueOf(resultSet.getString("unitPrice"))
            ));
        }
        tblProducts.setItems(products);
    }

    public void saveOnAction(ActionEvent event) throws SQLException, ClassNotFoundException {
        if(btnSave.getText().equals("Save")){
            if(txtId.getStyle().equals("-fx-border-color: #01ff00")){
            }else{
                btnSave.setDisable(true);
            }
            if(checkId(txtId.getText())){
                new Alert(Alert.AlertType.ERROR,"Can't duplicate supplier id!..").show();
            }else{
                boolean b = CrudUtil.executeUpdate("INSERT INTO products VALUES(?,?,?,?,?)",
                        txtId.getText(), txtName.getText(), Integer.valueOf(txtQtyOnHand.getText()), txtUnit.getText(), Double.valueOf(txtUnitPrice.getText()));
                if(b){
                    loadAllProducts();
                    new Alert(Alert.AlertType.CONFIRMATION,"Saved!..").show();
                }else{
                    new Alert(Alert.AlertType.ERROR,"Something went wrong!..").show();
                }
            }
        }else{
            //update
            boolean isUpdated = CrudUtil.executeUpdate("UPDATE products SET name = ? , qtyOnHand = ? , unit = ? , unitPrice = ? WHERE id = ?", txtName.getText(), txtQtyOnHand.getText(), txtUnit.getText(), txtUnitPrice.getText(),txtId.getText());
            if(isUpdated){
                loadAllProducts();
                new Alert(Alert.AlertType.CONFIRMATION,"Updated!..").show();
            }else {
                new Alert(Alert.AlertType.ERROR,"Something went wrong!..").show();
            }
        }
    }

    public void newOnAction(ActionEvent event) {
        txtId.clear();
        txtName.clear();
        txtQtyOnHand.clear();
        txtUnit.clear();
        txtUnitPrice.clear();
        btnSave.setText("Save");
    }

    public boolean checkId(String id) throws SQLException, ClassNotFoundException {
        ResultSet resultSet = CrudUtil.executeQuery("SELECT * FROM products");
        while(resultSet.next()){
            if(resultSet.getString("id").equals(id)){
                return true;
            }
        }
        return false;
    }

    public void backOnAction(ActionEvent event) throws IOException {
        Stage stage = (Stage)manageProductsContext.getScene().getWindow();
        stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("../view/adminForm.fxml"))));
    }

    public void deleteOnAction(ActionEvent event) throws SQLException, ClassNotFoundException {
        Optional<ButtonType> buttonType = new Alert(Alert.AlertType.INFORMATION, "Are you sure?", ButtonType.YES, ButtonType.NO).showAndWait();
        if(buttonType.get().equals(ButtonType.YES)) {
            boolean b = CrudUtil.executeUpdate("DELETE FROM products WHERE id = ?", txtId.getText());

            if (b) {
                txtId.clear();
                txtName.clear();
                txtQtyOnHand.clear();
                txtUnit.clear();
                txtUnitPrice.clear();
                loadAllProducts();
                new Alert(Alert.AlertType.CONFIRMATION, "Deleted!..").show();

            } else {
                new Alert(Alert.AlertType.ERROR, "Something went wrong!..").show();
            }
        }
    }

    public void idKeyReleased(KeyEvent keyEvent) {
        /*Pattern compile = Pattern.compile("^(P00-)[0-9]{3,5}$");
        boolean matches = compile.matcher(txtId.getText()).matches();
        if(matches){
            txtId.setStyle("-fx-border-color: #01ff00");
            btnSave.setDisable(false);
        }else{
            txtId.setStyle("-fx-border-color: #ff001b");
            btnSave.setDisable(true);
        }*/

        validate();
    }

    private Object validate() {
        LinkedHashMap<TextField,Pattern> map = new LinkedHashMap<>();

        Pattern idPattern = Pattern.compile("(P00-)[0-9]{3,5}$");
        Pattern namePattern = Pattern.compile("^[A-z ]{3,15}$");
        Pattern qtyOnHandPattern = Pattern.compile("^[0-9]{2,4}$");
        Pattern unitPattern = Pattern.compile("^[A-z]{6}$");
        Pattern unitPricePattern = Pattern.compile("^[0-9]{2,5}$");


        map.put(txtId,idPattern);
        map.put(txtName,namePattern);
        map.put(txtQtyOnHand,qtyOnHandPattern);
        map.put(txtUnit,unitPattern);
        map.put(txtUnitPrice,unitPricePattern);

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

    public void nameKeyReleased(KeyEvent keyEvent) {

        validate();
    }

    public void qtyOnHandKeyReleased(KeyEvent keyEvent) {

        validate();
    }

    public void uniKeyReleased(KeyEvent keyEvent) {

        validate();
    }

    public void unitPriceKeyReleased(KeyEvent keyEvent) {

        validate();
    }
}
