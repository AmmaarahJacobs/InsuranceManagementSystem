//Question One 
package digitalinsurancemanagementsystem;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import java.util.Locale;

public class DigitalInsuranceManagementSystem extends Application {
    //Observable List to hold table data
    private final ObservableList<Policies> data = FXCollections.observableArrayList();
    private TableView<Policies> tableView = new TableView<>();
    
    private void initializeTableView() {
        //tableview
        tableView.setItems(data);
         
        //create columns with proper property bindings
        TableColumn<Policies, String> idCol = new TableColumn<>("ID Number");
        idCol.setCellValueFactory(new PropertyValueFactory("idNo"));
        
        TableColumn<Policies, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory("custName"));
        
        TableColumn<Policies, String> surnameCol = new TableColumn<>("Surname");
        surnameCol.setCellValueFactory(new PropertyValueFactory("custSurname"));

        TableColumn<Policies, String> addressCol = new TableColumn<>("Address");
        addressCol.setCellValueFactory(new PropertyValueFactory("address"));
        
        TableColumn<Policies, String> ageCol = new TableColumn<>("Age");
        ageCol.setCellValueFactory(new PropertyValueFactory("age"));
        
        TableColumn<Policies, String> typeCol = new TableColumn<>("Policy Type");
        typeCol.setCellValueFactory(new PropertyValueFactory("policyType"));
        
        TableColumn<Policies, String> sumCol = new TableColumn<>("Sum Insured");
        sumCol.setCellValueFactory(new PropertyValueFactory("sumInsured"));
        
        TableColumn<Policies, String> coverageCol = new TableColumn<>("Coverage Amount");
        coverageCol.setCellValueFactory(new PropertyValueFactory("coverageAmount"));
        
        TableColumn<Policies, String> premiumCol = new TableColumn<>("Premium Amount");
        premiumCol.setCellValueFactory(new PropertyValueFactory("premiumAmount"));
        
        tableView.getColumns().addAll(idCol, nameCol, surnameCol, addressCol, ageCol, typeCol, sumCol, coverageCol, premiumCol);
        tableView.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
    }
    
    @Override
    public void start(Stage primaryStage) {
        //Title
        Label title = new Label("Insurance Management System");
        title.setFont(new Font("Arial", 24));
        title.setStyle("-fx-font-weight: bold;");
        title.setAlignment(Pos.CENTER);
        
        //Create form labels and fields
        Label custName = new Label("Customer Name:");
        TextField custField = new TextField();
        
        Label custSurname = new Label("Customer Surname:");
        TextField surField = new TextField();
        
        Label idNo = new Label("ID Number:");
        TextField idField = new TextField();
        
        Label age = new Label("Age:");
        TextField ageField = new TextField();
        
        Label address = new Label("Address:");
        TextField addressField = new TextField();
        
        ComboBox<String> polType = new ComboBox();
        polType.getItems().addAll("Health", "Life", "Auto");
        polType.setPromptText("Policy Type");
        
        polType.setOnAction(e -> {
            String selected = polType.getValue();
            System.out.println("Selected: " + selected);
        });
        
        Label sumInsuredLabel = new Label("Sum Insured");
        ComboBox<String> sumInsured = new ComboBox();
        sumInsured.getItems().addAll("100000", "95000", "90000", "85000", "80000", "75000", "70000", "65000", "60000", "55000", "50000", "45000", "40000", "35000", "30000", "25000", "20000", "15000", "10000", "5000");
        sumInsured.setPromptText("Sum Insured");
        
        sumInsured.setOnAction(e -> {
            String selected = sumInsured.getValue();
            System.out.println("Selected: " + selected);
        });
        
        Label covAmount = new Label("Coverage Amount:");
        TextField covField = new TextField();
        
        Label premAmount = new Label("Premium Amount:");
        TextField premField = new TextField();
        premField.setEditable(false);
        
        //Buttons and button label
        Button calcPremiumBtn = new Button("Calculate Premium");
        calcPremiumBtn.setOnAction(e -> {
           try {
               if(sumInsured.getValue() == null || covField.getText().isEmpty()) {
                   throw new IllegalArgumentException("Sum Insured and Coverage Amount are required");
               }
               
               double sum = Double.parseDouble(sumInsured.getValue());
               double coverage = Double.parseDouble(covField.getText());
               double premium = coverage * 0.8;
               premField.setText(String.format(Locale.US,"%.1f", premium));
           } catch (Exception ex){
               Alert alert = new Alert(Alert.AlertType.ERROR);
               alert.setTitle("Calculation Error");
               alert.setContentText(ex.getMessage());
               alert.showAndWait();
           }
        });
        
        //Buttons and button label
        Button submitBtn = new Button("Submit");
        submitBtn.setOnAction(e -> {
            try {
                String idno = idField.getText();
                String name = custField.getText();
                String surname = surField.getText();
                String address_tbl = addressField.getText();
                String age_tbl = ageField.getText();
                String pol_tbl = polType.getValue();
                String sum_tbl = sumInsured.getValue();
                String coverage_tbl = String.format("%.2f", Double.parseDouble(covField.getText()));
                String premiumText = premField.getText().trim();
                if (premiumText.isEmpty() || !premiumText.matches("\\d+(\\.\\d+)?")) {
                    throw new IllegalArgumentException("Premium Amount must be a valid number");
                }
                String premium_tbl = String.format("%.2f", Double.parseDouble(premiumText));

                if(idno.isEmpty() || name.isEmpty() ||  surname.isEmpty() || address_tbl.isEmpty() ||  age_tbl.isEmpty() || pol_tbl == null || sum_tbl == null || coverage_tbl.isEmpty() || premium_tbl.isEmpty()) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Validation Error");
                    alert.setContentText("Please fill in all fields");
                    alert.showAndWait();
                } else {
                    data.add(new Policies(idno, name, surname, age_tbl, address_tbl, pol_tbl, sum_tbl, coverage_tbl, premium_tbl));
                    System.out.println("Current data: " + data);
                    
                    idField.clear();
                    custField.clear();
                    surField.clear();
                    addressField.clear();
                    ageField.clear();
                    polType.getSelectionModel().clearSelection();
                    sumInsured.getSelectionModel().clearSelection();
                    covField.clear();
                    premField.clear();

                    Alert success = new Alert(Alert.AlertType.INFORMATION);
                    success.setTitle("Success");
                    success.setContentText("Policy added successfully!");
                    success.showAndWait();
            }
            } catch (Exception ex){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setContentText(ex.getMessage());
                alert.showAndWait();
            }
        });
        
        //Intialise tableView
        initializeTableView();
        tableView.setVisible(false);
        tableView.setMinHeight(200);
        
        Button viewPoliciesBtn = new Button("View Policies");
        viewPoliciesBtn.setOnAction(e -> {
           tableView.setVisible(!tableView.isVisible());
           viewPoliciesBtn.setText(tableView.isVisible() ? "Hide Policies" : "View Policies");
        });
        
        //Set stage 
        GridPane formGrid = new GridPane();
        formGrid.setPadding(new Insets(20));
        formGrid.setVgap(10);
        formGrid.setHgap(10);
        formGrid.setAlignment(Pos.TOP_LEFT);
        
        formGrid.add(custName, 0, 0);
        formGrid.add(custField, 1, 0);
        formGrid.add(custSurname, 0, 1);
        formGrid.add(surField, 1, 1);
        formGrid.add(idNo, 0, 2);
        formGrid.add(idField, 1, 2);
        formGrid.add(age, 0, 3);
        formGrid.add(ageField, 1, 3);
        formGrid.add(address, 0, 4);
        formGrid.add(addressField, 1, 4);
        formGrid.add(polType, 0, 5);
        formGrid.add(sumInsuredLabel, 0, 6);
        formGrid.add(sumInsured, 1, 6);
        formGrid.add(covAmount, 0, 7);
        formGrid.add(covField, 1, 7);
        formGrid.add(premAmount, 0, 8);
        formGrid.add(premField, 1, 8);
        formGrid.add(calcPremiumBtn, 1, 9);
        formGrid.add(submitBtn, 0, 10);
        formGrid.add(viewPoliciesBtn, 1, 10);
        formGrid.add(tableView, 0, 11, 2, 1);
        
        
        //VBox
        VBox root = new VBox(10, title, formGrid);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #e6f2ff;");
        root.setAlignment(Pos.TOP_CENTER);
        
        //Scene
        Scene scene = new Scene(root, 1000, 700);
        primaryStage.setTitle("Digital Insurance Management System");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    private void clearFields(TextField id, TextField name, TextField surname, TextField address, TextField age, ComboBox<String> policy, ComboBox<String> sum, TextField coverage, TextField premium) {
        id.clear();
        name.clear();
        surname.clear();
        address.clear();
        age.clear();
        policy.getSelectionModel().clearSelection();
        sum.getSelectionModel().clearSelection();
        coverage.clear();
        premium.clear();
    }
    
    //Model class
    public static class Policies {
        private final String idNo;
        private final String custName;
        private final String custSurname;
        private final String age;
        private final String address;
        private final String polType;
        private final String sumInsured;
        private final String covAmount;
        private final String premAmount;
        
        public Policies(String idNo, String custName, String custSurname, String age, String address, String polType, String sumInsured, String covAmount, String premAmount) {
            this.idNo = idNo;
            this.custName = custName;
            this.custSurname = custSurname;
            this.age = age;
            this.address = address;
            this.polType = polType;
            this.sumInsured = sumInsured;
            this.covAmount = covAmount;
            this.premAmount = premAmount;
        }
        
        public String getIdNo() {return idNo;}       
        public String getCustName() {return custName;}       
        public String getCustSurname() {return custSurname;}        
        public String getAge() {return age;}        
        public String getAddress() {return address;}        
        public String getPolicyType() {return polType;}        
        public String getSumInsured() {return sumInsured;} 
        public String getCoverageAmount() {return covAmount;}
        public String getPremiumAmount() {return premAmount;}
    }
    
    public static void main(String[] args) {
        launch(args);
    } 
}