package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

public class CashierFormController {

    public AnchorPane cashierFormContext;

    public void placeOrderOnAction(ActionEvent event) throws IOException {
        loadUi("placeOrderForm");
    }

    public void logOutOnAction(ActionEvent event) throws IOException {
        Optional<ButtonType> buttonType = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure?", ButtonType.YES, ButtonType.NO).showAndWait();
        if(buttonType.get().equals(ButtonType.YES)){
            loadUi("loginForm");
        }
    }

    public void searchProductsOnAction(ActionEvent event) throws IOException {
      loadUi("searchForm");
    }

    private void loadUi(String location) throws IOException {
        Stage stage = (Stage) cashierFormContext.getScene().getWindow();
        stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("../view/"+location+".fxml"))));
    }
}
