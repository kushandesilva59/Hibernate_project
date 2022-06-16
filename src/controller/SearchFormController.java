package controller;

import Util.CrudUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Pattern;

public class SearchFormController {

    public AnchorPane searcProductsContext;
    public TextField txtId;
    public TextField txtName;
    public TextField txtQTYOnHand;
    public TextField txtUnitPrice;
    public TextField txtUnit;
    public Button btnSearch;


    public void searchOnAction(ActionEvent event) throws SQLException, ClassNotFoundException {
        ResultSet resultSet = CrudUtil.executeQuery("SELECT * FROM products WHERE id = ?", txtId.getText());
        if(resultSet.next()){
            txtName.setText(resultSet.getString("name"));
            txtQTYOnHand.setText(resultSet.getString("qtyOnHand"));
            txtUnit.setText(resultSet.getString("unit"));
            txtUnitPrice.setText(resultSet.getString("unitPrice"));
        }else{
            new Alert(Alert.AlertType.ERROR,"Empty results!").show();
        }
    }

    public void backOnAction(ActionEvent event) throws IOException {
        Stage stage = (Stage)searcProductsContext.getScene().getWindow();
        stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("../view/cashierForm.fxml"))));
    }


    public void onKeyReleased(KeyEvent keyEvent) {
        Pattern idPattern = Pattern.compile("(P00-)[0-9]{3,5}$");
        boolean isCorrect = idPattern.matcher(txtId.getText()).matches();

        if(isCorrect){
           setGreen(txtId);
        }else{
            setRed(txtId);
        }
    }

    private void setGreen(TextField textField) {
        if(textField.getLength() > 0) {
            textField.setStyle("-fx-border-color: #01ff00");
            btnSearch.setDisable(false);
        }
    }

    private void setRed(TextField textField) {
        if(textField.getLength() > 0){
            textField.setStyle("-fx-border-color: #ff001b");
            btnSearch.setDisable(true);
        }
    }
}
