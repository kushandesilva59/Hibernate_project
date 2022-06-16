package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

public class AdminFormController {

    public AnchorPane adminFormContext;
    public Label labelContext;
    public AnchorPane anchorDashBoard;

    public void viewSuppliersOnAction(ActionEvent event) throws IOException {
        loadUi("viewSuppliersForm");
    }

    public void viewCustomersOnAction(ActionEvent event) throws IOException {
        loadUi("viewCustomersForm");
    }

    public void manageProductsOnAction(ActionEvent event) throws IOException {
        loadUi("manageProductsForm");
    }

    public void incomeReportOnAction(ActionEvent event) throws IOException {
        loadUi("incomeReportForm");
    }

    public void logOutOnAction(ActionEvent event) throws IOException {
        Optional<ButtonType> buttonType = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure?", ButtonType.YES, ButtonType.NO).showAndWait();
        if(buttonType.get().equals(ButtonType.YES)){
            loadUi("loginForm");
        }

    }

    private void loadUi(String location) throws IOException {
        Stage stage = (Stage) adminFormContext.getScene().getWindow();
        stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("../view/"+location+".fxml"))));

       /* anchorDashBoard.getChildren().clear();
        anchorDashBoard.getChildren().add(FXMLLoader.load(getClass().getResource("../view/"+location+".fxml")));*/
    }

}
