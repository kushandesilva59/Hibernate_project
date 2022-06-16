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
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import model.Supplier;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Optional;
import java.util.regex.Pattern;

public class ViewSuppliersFormController {

    public AnchorPane viewSuppliersContext;
    public TableView <Supplier>tblSuppliers;
    public TableColumn colId;
    public TableColumn colName;
    public TableColumn colAddress;
    public TableColumn colContact;
    public TableColumn colEdit;
    public Button btnSave;
    public Button btnDelete;
    public TextField txtid;
    public TextField txtName;
    public TextField txtContact;
    public TextField txtAddress;

    public void initialize() throws SQLException, ClassNotFoundException {
        colId.setCellValueFactory(new PropertyValueFactory("id"));
        colName.setCellValueFactory(new PropertyValueFactory("name"));
        colContact.setCellValueFactory(new PropertyValueFactory("contactNumber"));
        colAddress.setCellValueFactory(new PropertyValueFactory("address"));
        btnDelete.setDisable(true);

        tblSuppliers.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue != null){
                setData(newValue);
            }


                });
        loadAllSuppliers();

    }

    private void setData(Supplier supplier) {
        txtid.setText(supplier.getId());
        txtName.setText(supplier.getName());
        txtContact.setText(supplier.getContactNumber());
        txtAddress.setText(supplier.getAddress());
        btnDelete.setDisable(false);
        btnSave.setText("Update");
    }


    private void loadAllSuppliers() throws SQLException, ClassNotFoundException {
        ResultSet resultSet = CrudUtil.executeQuery("SELECT * FROM suppliers");
        ObservableList suppliers = FXCollections.observableArrayList();

        while(resultSet.next()){
            suppliers.add(new Supplier(
                    resultSet.getString(1),
                    resultSet.getString(2),
                    resultSet.getString(3),
                    resultSet.getString(4))
            );
        }
        tblSuppliers.setItems(suppliers);
    }

    public void saveOnAction(ActionEvent event) throws SQLException, ClassNotFoundException {
        if(btnSave.getText().equals("Save")){
            if(checkId(txtid.getText())){
                new Alert(Alert.AlertType.ERROR,"Can't duplicate supplier id!..").show();
            }else{
                boolean b = CrudUtil.executeUpdate("INSERT INTO suppliers VALUES(?,?,?,?)",
                        txtid.getText(),txtName.getText(),txtContact.getText(),txtAddress.getText());
                if(b){
                    loadAllSuppliers();
                    new Alert(Alert.AlertType.CONFIRMATION,"Saved!..").show();
                }else{
                    new Alert(Alert.AlertType.ERROR,"Something went wrong!..").show();
                }
            }
        }else{
            boolean isUpdated = CrudUtil.executeUpdate("UPDATE suppliers SET name = ? , contactNumber = ? , address = ? WHERE id = ?", txtName.getText(), txtContact.getText(), txtAddress.getText(), txtid.getText());
            if(isUpdated){
                loadAllSuppliers();
                new Alert(Alert.AlertType.CONFIRMATION,"Updated!..").show();
            }else {
                new Alert(Alert.AlertType.ERROR,"Something went wrong!..").show();

            }
        }

    }

    public boolean checkId(String id) throws SQLException, ClassNotFoundException {
        ResultSet resultSet = CrudUtil.executeQuery("SELECT * FROM suppliers");
        while(resultSet.next()){
            if(resultSet.getString("id").equals(id)){
                return true;
            }
        }
        return false;
    }

    public void newOnAction(ActionEvent event) {
        txtid.clear();
        txtName.clear();
        txtContact.clear();
        txtAddress.clear();
        btnSave.setText("Save");
    }

    public void backOnAction(ActionEvent event) throws IOException {
        loadUi("adminForm");
    }

    private void loadUi(String location) throws IOException {
        Stage stage = (Stage) viewSuppliersContext.getScene().getWindow();
        stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("../view/"+location+".fxml"))));
    }

    public void deleteOnAction(ActionEvent event) throws SQLException, ClassNotFoundException {
        Optional<ButtonType> buttonType = new Alert(Alert.AlertType.INFORMATION, "Are you sure?", ButtonType.YES, ButtonType.NO).showAndWait();
        if(buttonType.get().equals(ButtonType.YES)) {
            boolean b = CrudUtil.executeUpdate("DELETE FROM suppliers WHERE id = ?", txtid.getText());

            if (b) {
                txtid.clear();
                txtName.clear();
                txtContact.clear();
                txtAddress.clear();
                loadAllSuppliers();
                new Alert(Alert.AlertType.CONFIRMATION, "Deleted!..").show();

            } else {
                new Alert(Alert.AlertType.ERROR, "Something went wrong!..").show();
            }
        }
    }

    private Object validate() {
        LinkedHashMap<TextField, Pattern> map = new LinkedHashMap<>();

        Pattern idPattern = Pattern.compile("(S00-)[0-9]{3,5}$");
        Pattern namePattern = Pattern.compile("^[A-z ]{3,15}$");
        Pattern addressPattern = Pattern.compile("^[A-z0-9 ,/]{5,25}$");
        Pattern contactPattern = Pattern.compile("^[0-9]{3}-[0-9]{7}$");

        map.put(txtid,idPattern);
        map.put(txtName,namePattern);
        map.put(txtContact,contactPattern);
        map.put(txtAddress,addressPattern);

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

    public void keyReleased(KeyEvent keyEvent) {
        validate();
    }
}
