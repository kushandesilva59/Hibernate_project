package controller;
import DAO.CustomerDAOImpl;
import DAO.ProductsDAOImpl;
import Util.CrudUtil;
import db.DbConnection;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.*;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.view.JasperViewer;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Optional;

public class PlaceOrderFormController {
    public AnchorPane placeOrderContext;
    public Label lblDate;
    public Label lblTime;
    public ComboBox <String>cmbCustomers;
    public ComboBox <String>cmbProducts;
    public TextField txtCustomerName;
    public TextField txtCustomerAddress;
    public TextField txtContactNumber;
    public TextField txtProductName;
    public TextField txtQtyOnHand;
    public TextField txtUnitPrice;
    public TextField txtProductUnit;
    public TableView <CartTm> tblOrderTable;
    public TableColumn colName;
    public TableColumn colQTY;
    public TableColumn colUnitPrice;
    public TableColumn colTotalCost;
    public TableColumn colEdit;
    public TextField txtQTY;
    public Label lblTotal;
    ObservableList <CartTm> tmList = FXCollections.observableArrayList();

    public void initialize() throws SQLException, ClassNotFoundException {
        colName.setCellValueFactory(new PropertyValueFactory("productId"));
        colQTY.setCellValueFactory(new PropertyValueFactory("qty"));
        colUnitPrice.setCellValueFactory(new PropertyValueFactory("unitPrice"));
        colTotalCost.setCellValueFactory(new PropertyValueFactory("totalCost"));
        colEdit.setCellValueFactory(new PropertyValueFactory("btn"));

        setTimeDate();
        loadCustomers();
        loadProducts();

        cmbCustomers.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            setCustomerDetails(newValue);
        });

        cmbProducts.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            try {
                setProductDetails(newValue);
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        });
    }

    private CartTm isExist(String id) {
        for(CartTm tm : tmList){
            if(tm.getProductId().equals(id)){
                return tm;
            }
        }
        return null;
    }

    private void setProductDetails(String selectedProductId) throws SQLException, ClassNotFoundException {
        Product product = ProductCrudController.getProduct(selectedProductId);
        if(product != null){
            txtProductName.setText(product.getName());
            txtQtyOnHand.setText(String.valueOf(product.getQtyOnHand()));
            txtUnitPrice.setText(String.valueOf(product.getUnitPrice()));
            txtProductUnit.setText(product.getUnit());
        }else{
            new Alert(Alert.AlertType.ERROR,"Empty results!..").show();
        }
    }

    private void loadProducts() throws SQLException, ClassNotFoundException {
        ObservableList ids = FXCollections.observableArrayList(ProductCrudController.getProductIds());
        cmbProducts.setItems(ids);
    }

    private void setCustomerDetails(String selectedCustomer)  {
        try {
            Customer c = CustomerCrudController.getCustomer(selectedCustomer);
            if(c != null){
                txtCustomerName.setText(c.getName());
                txtContactNumber.setText(c.getContactNumber());
                txtCustomerAddress.setText(c.getAddress());
            }else{
                new Alert(Alert.AlertType.ERROR,"Empty results!..").show();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void loadCustomers() throws SQLException, ClassNotFoundException {
        ObservableList <String> customerIds = FXCollections.observableArrayList();
        CustomerDAOImpl customerDAO = new CustomerDAOImpl();
        customerIds = customerDAO.getAllIds();
        cmbCustomers.setItems(customerIds);
    }

    public void backOnAction(ActionEvent event) throws IOException {
       Stage stage = (Stage)placeOrderContext.getScene().getWindow();
       stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("../view/cashierForm.fxml"))));
    }

    private void setTimeDate(){
        lblDate.setText(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));

        Timeline clock = new Timeline(new KeyFrame(Duration.ZERO, e ->{
            LocalTime time = LocalTime.now();
            lblTime.setText(time.getHour()+" : "+time.getMinute()+" : "+time.getSecond());
        }),new KeyFrame(Duration.seconds(1)));
        clock.setCycleCount(Animation.INDEFINITE);
        clock.play();
    }

    public void addToCartOnAction(ActionEvent event) throws SQLException, ClassNotFoundException {

            double unitPrice = Double.valueOf(txtUnitPrice.getText());
            int qty = Integer.valueOf(txtQTY.getText());
            double totalCost = qty*unitPrice;
            Button btn = new Button("Delete");

        boolean qtyIsOk = checkQty(qty, cmbProducts.getValue());

        if(qtyIsOk){
                CartTm exist = isExist(cmbProducts.getValue());
                if(exist != null){
                    for(CartTm temp : tmList){
                        if(temp.equals(exist)){
                            boolean isQtyOk = checkQty((exist.getQty() + qty), exist.getProductId());
                            if(isQtyOk){
                                temp.setQty(exist.getQty()+qty);
                                temp.setTotalCost(temp.getTotalCost()+totalCost);
                            }else{
                                new Alert(Alert.AlertType.WARNING,"Sorry!..Haven't QTY..").show();
                            }
                        }
                    }
                }else{
                    CartTm tm = new CartTm(cmbProducts.getValue(),qty,unitPrice,totalCost,btn);

                    btn.setOnAction(e -> {
                        Optional<ButtonType> buttonType = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure?", ButtonType.YES, ButtonType.NO).showAndWait();
                        if(buttonType.get().equals(ButtonType.YES)){
                            tmList.remove(tm);
                            calculateTotal();
                        }

                    });

                    tmList.add(tm);
                    tblOrderTable.setItems(tmList);
                }
                tblOrderTable.refresh();
                calculateTotal();
            }else{
                new Alert(Alert.AlertType.WARNING,"Sorry!..Haven't QTY..").show();
            }
    }

    private boolean checkQty(int qty,String productId) throws SQLException, ClassNotFoundException {
       // ResultSet resultSet = CrudUtil.executeQuery("SELECT * FROM products WHERE id = ?", productId);

        ProductsDAOImpl productsDAO = new ProductsDAOImpl();
        Product product = productsDAO.search(productId);
            if(qty > product.getQtyOnHand()){
                return false;
            }else {
                return true;
            }
    }

    private void calculateTotal(){
        double total = 0;
        for(CartTm tm : tmList){
            total += tm.getTotalCost();
        }
        lblTotal.setText(String.valueOf(total));
    }

    public void placeOrderOnAction(ActionEvent event) throws SQLException, ClassNotFoundException {

        Order order = new Order(OrderCrudController.getOrderId(),lblDate.getText(),cmbCustomers.getValue());

        ArrayList <OrderDetail> details = new ArrayList<>();
        ArrayList <CartRM> reportDetails = new ArrayList<>();

        for(CartTm tm :tmList){
            details.add(new OrderDetail(order.getId(),tm.getProductId(),tm.getQty(),tm.getUnitPrice()));
            reportDetails.add(new CartRM(tm.getProductId(),tm.getQty(),tm.getUnitPrice(),tm.getTotalCost()));
        }

        Connection connection = null;
        try {
            connection = DbConnection.getInstance().getConnection();
            connection.setAutoCommit(false);
            boolean isSaved = new OrderCrudController().saveOrder(order);
            if(isSaved){
                boolean isDetailsSaved = new OrderCrudController().saveOrderDetails(details);
                if(isDetailsSaved){
                    connection.commit();
                   Optional<ButtonType> buttonType = new Alert(Alert.AlertType.CONFIRMATION, "Saved!..").showAndWait();
                    if(buttonType.get().equals(ButtonType.OK)){
                        Stage stage = (Stage)placeOrderContext.getScene().getWindow();
                        stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("../view/placeOrderForm.fxml"))));
                    }
                    HashMap map = new HashMap();
                    map.put("orderId",OrderCrudController.getOrderId());
                    map.put("netAmount",Double.valueOf((lblTotal.getText())));
                    // JasperDesign load = JRXmlLoader.load(this.getClass().getResourceAsStream("../reports/farmshop.jrxml"));

                    JasperReport jasperReport  = (JasperReport) JRLoader.loadObject(this.getClass().getResource("/reports/farmshop.jasper"));

                    //   = JasperCompileManager.compileReport(load);

                    JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport,map, new JRBeanCollectionDataSource(reportDetails));

                    JasperViewer.viewReport(jasperPrint,false);
                }else{
                    connection.rollback();
                    new Alert(Alert.AlertType.ERROR,"Error!..").show();
                }
            }else{
                connection.rollback();
                new Alert(Alert.AlertType.ERROR,"Error!..").show();

            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (JRException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            connection.setAutoCommit(true);
        }
    }
}
