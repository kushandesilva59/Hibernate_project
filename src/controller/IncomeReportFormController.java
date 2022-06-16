package controller;

import Util.CrudUtil;
import com.jfoenix.controls.JFXDatePicker;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import model.ReportTM;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

public class IncomeReportFormController {

    public AnchorPane incomeReportContext;
    public JFXDatePicker dateFrom;
    public JFXDatePicker dateTo;
    public Button btnSearch;
    public TableView <ReportTM> tblReportTable;
    public TableColumn colId;
    public TableColumn colDate;
    public TableColumn colTotalCost;
    public Label lblFullAmount;

    public void initialize(){
        colId.setCellValueFactory(new PropertyValueFactory<>("orderId"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colTotalCost.setCellValueFactory(new PropertyValueFactory<>("totalCost"));
    }

    public void searchOnAction(ActionEvent event) throws SQLException, ClassNotFoundException, IOException {
        ObservableList<ReportTM> reportTMS = FXCollections.observableArrayList();
        ResultSet orders = CrudUtil.executeQuery(
                "SELECT orderdetails.orderId,orders.date,SUM(orderdetails.qty*orderdetails.unitPrice) AS amount FROM orders INNER JOIN orderdetails ON orders.id = orderdetails.orderId " +
                        "GROUP BY orderId,productId HAVING orders.date BETWEEN ? AND ?",(dateFrom.getValue().toString()),(dateTo.getValue().toString()));

        if(orders.next()){
            double fullAmount = 0;
            reportTMS.add(new ReportTM(orders.getString("orderId"),orders.getString("date"),Double.valueOf(orders.getString("amount"))));
            fullAmount += Double.valueOf(orders.getString("amount"));
            while (orders.next()){
                reportTMS.add(new ReportTM(orders.getString("orderId"),orders.getString("date"),Double.valueOf(orders.getString("amount"))));
                fullAmount += Double.valueOf(orders.getString("amount"));
            }
            tblReportTable.setItems(reportTMS);
            lblFullAmount.setText(String.valueOf(fullAmount));
        }else{
            new Alert(Alert.AlertType.WARNING,"Empty!..").show();
        }
    }

    public void backOnAction(ActionEvent event) throws IOException {
        Stage stage = (Stage)incomeReportContext.getScene().getWindow();
        stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("../view/adminForm.fxml"))));
    }

}
