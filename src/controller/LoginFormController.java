package controller;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import java.io.IOException;

public class LoginFormController {
    public AnchorPane loginFormContext;
    public JFXPasswordField pwdPassword;
    public JFXTextField txtUsername;

    public void loginOnAction(ActionEvent event) throws IOException {
        if(txtUsername.getText().equals("1234") && pwdPassword.getText().equals("1234")){
            setUi("cashierForm");
        }else if (txtUsername.getText().equals("789") && pwdPassword.getText().equals("789")){
            setUi("adminForm");
        }else{
            new Alert(Alert.AlertType.WARNING,"Incorrect username or password!...").show();
        }
    }

    private void setUi(String location) throws IOException {
        Stage stage = (Stage) loginFormContext.getScene().getWindow();
        stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("../view/"+location+".fxml"))));
    }
}
