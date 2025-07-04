//Question Three

package insurancemanagementsystemjdbc;

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
import java.sql.*;
import java.util.Locale;

public class InsuranceManagementSystemJDBC extends Application {
    //Database Connection
    private static final String DB_URL = "jdbc:mysql://localhost:3306/insurance_db";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Ammaarah2125*";
    
    private final ObservableList<Policy> data = FXCollections.observableArrayList();
    private TableView<Policy> tableView = new TableView<>();
    private Connection connection;
    
    //Form fields
    private TextField custField = new TextField();
    private TextField surField = new TextField();
    private TextField idField = new TextField();
    private TextField ageField = new TextField();
    private TextField addressField = new TextField();
    private ComboBox<String> polType = new ComboBox();
    private ComboBox<String> sumInsured = new ComboBox();
    private TextField covField = new TextField();
    private TextField premField = new TextField();
    
    @Override
    public void init() throws Exception {
        super.init();
        
        //Initialize database connection
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            System.out.println("Database connection established.");
        } catch (Exception e) {
            System.err.println("Database connection failed: " + e.getMessage());
            throw e;
        }
    }
    
    @Override
    public void stop() throws Exception {
        super.stop();
        
        //Close database connection when application stops 
        if (connection != null && !connection.isClosed()) {
            connection.close();
            System.out.println("Database connection closed successfully.");
        }
    }
    
    private void initializeTableView() {
        //tableview
        tableView.setItems(data);
         
        //create columns with proper property bindings
        TableColumn<Policy, String> idCol = new TableColumn<>("ID Number");
        idCol.setCellValueFactory(new PropertyValueFactory("idNo"));
        
        TableColumn<Policy, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory("customerName"));
        
        TableColumn<Policy, String> surnameCol = new TableColumn<>("Surname");
        surnameCol.setCellValueFactory(new PropertyValueFactory("customerSurname"));

        TableColumn<Policy, String> addressCol = new TableColumn<>("Address");
        addressCol.setCellValueFactory(new PropertyValueFactory("address"));
        
        TableColumn<Policy, String> ageCol = new TableColumn<>("Age");
        ageCol.setCellValueFactory(new PropertyValueFactory("age"));
        
        TableColumn<Policy, String> typeCol = new TableColumn<>("Policy Type");
        typeCol.setCellValueFactory(new PropertyValueFactory("policyType"));
        
        TableColumn<Policy, String> sumCol = new TableColumn<>("Sum Insured");
        sumCol.setCellValueFactory(new PropertyValueFactory("sumInsured"));
        
        TableColumn<Policy, String> coverageCol = new TableColumn<>("Coverage Amount");
        coverageCol.setCellValueFactory(new PropertyValueFactory("coverageAmount"));
        
        TableColumn<Policy, String> premiumCol = new TableColumn<>("Premium Amount");
        premiumCol.setCellValueFactory(new PropertyValueFactory("premiumAmount"));
        
        tableView.getColumns().addAll(idCol, nameCol, surnameCol, addressCol, ageCol, typeCol, sumCol, coverageCol, premiumCol);
        tableView.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
    }
    
    private void savePolicyToDatabase(Policy policy) throws SQLException {
        String customerSql = "INSERT INTO customers (name, surname, id_number, age, address) VALUES (?, ?, ?, ?, ?)";
        String policySql = "INSERT INTO policies (customer_id, policy_type, sum_insured, coverage_amount, premium_amount) VALUES (?, ?, ?, ?, ?)";
        
        try (PreparedStatement customerStmt = connection.prepareStatement(customerSql, Statement.RETURN_GENERATED_KEYS);
             PreparedStatement policyStmt = connection.prepareStatement(policySql)) {
            
            //Insert customer
            customerStmt.setString(1, policy.getCustomerName());
            customerStmt.setString(2, policy.getCustomerSurname());
            customerStmt.setString(3, policy.getIdNumber());
            customerStmt.setInt(4, policy.getAge());
            customerStmt.setString(5, policy.getAddress());
            
            int affectedRows = customerStmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating customer failed, no rows affected");
            }
            
            //Get generated customer ID
            int customerId;
            try (ResultSet generatedKeys = customerStmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    customerId = generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Creating customer failed, no ID obtained");
                }
            }
            
            //insert policy
            policyStmt.setInt(1, customerId);
            policyStmt.setString(2, policy.getPolicyType());
            policyStmt.setDouble(3, policy.getSumInsured());
            policyStmt.setDouble(4, policy.getCoverageAmount());
            policyStmt.setDouble(5, policy.getPremiumAmount());
            
            policyStmt.executeUpdate();
        }
    }
    
    private void loadPoliciesFromDatabase() throws SQLException {
        data.clear();
        String sql = "SELECT c.id_number, c.name, c.surname, c.age, c.address, " + "p.policy_type, p.sum_insured, p.coverage_amount, p.premium_amount " + "FROM customers c JOIN policies p ON c.id = p.customer_id";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Policy policy = new Policy(
                  rs.getString("id_Number"), 
                  rs.getString("name"), 
                  rs.getString("surname"), 
                  rs.getInt("age"), 
                  rs.getString("address"), 
                  rs.getString("policy_type"), 
                  rs.getDouble("sum_insured"), 
                  rs.getDouble("coverage_amount"),
                  rs.getDouble("premium_amount")
                );
                data.add(policy);
            }
        }
    }
    
    private double calculatePremium(String policyType, double coverageAmount) {
        switch (policyType) {
            case "Life" : return coverageAmount * 0.05;
            case "Health": return coverageAmount * 0.08;
            case "Auto": return coverageAmount * 0.10;
            default: throw new IllegalArgumentException("Invalid policy type");
        }
    }
    
    private boolean validateInputs() {
        if (idField.getText().trim().isEmpty() || custField.getText().trim().isEmpty() ||surField.getText().trim().isEmpty() || ageField.getText().trim().isEmpty() || addressField.getText().trim().isEmpty() || polType.getValue().trim().isEmpty() || sumInsured.getValue().trim().isEmpty() || covField.getText().trim().isEmpty() || premField.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please fill n all the fields");
            return false;
        }
        
        try {
            Integer.parseInt(ageField.getText().trim());
            Double.parseDouble(sumInsured.getValue().trim());
            Double.parseDouble(covField.getText().trim());
            Double.parseDouble(premField.getText().trim());
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Input Error", "Please enter valid numbers");
            return false;
        }
        return true;
    }
    
    private void clearForm() {
        idField.clear();
        custField.clear();
        surField.clear();
        addressField.clear();
        ageField.clear();
        polType.getSelectionModel().clearSelection();
        sumInsured.getSelectionModel().clearSelection();
        covField.clear();
        premField.clear();
    }
    
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    @Override
    public void start(Stage primaryStage) {
        //Initialize UI components
        initializeTableView();
        tableView.setVisible(false);
        
        //title
        Label title = new Label("Insurance Management System");
        title.setFont(new Font("Arial", 24));
        title.setStyle("-fx-font-weight: bold;");
        title.setAlignment(Pos.CENTER);
        
        Label custNameLabel = new Label("Customer Name:");
        Label custSurnameLabel = new Label("Customer Surname:");
        Label idNoLabel = new Label("ID Number:");
        Label ageLabel = new Label("Age:");
        Label addressLabel = new Label("Address:");
        Label sumInsuredLabel = new Label("Sum Insured:");
        Label covAmountLabel = new Label("Coverage Amount:");
        Label premAmountLabel = new Label("Premium Amount:");
        
        //ComboBox Setup
        polType.getItems().addAll("Health", "Life", "Auto");
        polType.setPromptText("Policy Type");
        
        sumInsured.getItems().addAll("100000", "95000", "90000", "85000", "80000", "75000", "70000", "65000", "60000", "55000", "50000", "45000", "40000", "35000", "30000", "25000", "20000", "15000", "10000", "5000");
        sumInsured.setPromptText("Sum Insured");
        
        premField.setEditable(false);
        
        //Buttons
        Button calcPremiumBtn = new Button("Calculate Premium");
        calcPremiumBtn.setOnAction(e -> {
           try {
               if(polType.getValue() == null) {
                   throw new IllegalArgumentException("Policy type is required");
               }
               if (sumInsured.getValue() == null || covField.getText().isEmpty()) {
                   throw new IllegalArgumentException("Sum Insured and Coverage Amount are required");
               }
               
               double coverage = Double.parseDouble(covField.getText());
               double premium = calculatePremium(polType.getValue(), coverage);
               premField.setText(String.format(Locale.US, "%.2f", premium));
           } catch(Exception ex) {
               showAlert(Alert.AlertType.ERROR, "Calculation Error", ex.getMessage());
           }
        });
        
        Button submitBtn = new Button("Submit");
        submitBtn.setOnAction(e -> {
            try {
                if (!validateInputs()) return;
                
                Policy policy = new Policy(
                    idField.getText().trim(),
                    custField.getText().trim(),
                    surField.getText().trim(),
                    Integer.parseInt(ageField.getText().trim()),
                    addressField.getText().trim(),
                    polType.getValue(),
                    Double.parseDouble(sumInsured.getValue()),
                    Double.parseDouble(covField.getText()),
                    Double.parseDouble(premField.getText())
                );
                
                savePolicyToDatabase(policy);
                clearForm();
                showAlert(Alert.AlertType.INFORMATION, "Success", "Policy saved successfully!");
                
            } catch (SQLException ex) {
                showAlert(Alert.AlertType.ERROR, "Database Error", "Error saving policy: " + ex.getMessage());
            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, "Error", ex.getMessage());
            }
        });
        
        Button viewPoliciesBtn = new Button("View Policies");
        viewPoliciesBtn.setOnAction(e -> {
           try {
           loadPoliciesFromDatabase();
           tableView.setVisible(!tableView.isVisible());
           viewPoliciesBtn.setText(tableView.isVisible() ? "Hide Policies" : "View Policies");
           } catch (SQLException ex) {
               showAlert(Alert.AlertType.ERROR, "Database Error", "Error Loading policies: " + ex.getMessage());
           }
        });
        
        //Set stage 
        GridPane formGrid = new GridPane();
        formGrid.setPadding(new Insets(20));
        formGrid.setVgap(10);
        formGrid.setHgap(10);
        formGrid.setAlignment(Pos.TOP_LEFT);
        
        formGrid.add(custNameLabel, 0,0);
        formGrid.add(custField, 1,0);
        formGrid.add(custSurnameLabel, 0,1);
        formGrid.add(surField, 1,1);
        formGrid.add(idNoLabel, 0,2);
        formGrid.add(idField, 1,2);
        formGrid.add(ageLabel, 0,3);
        formGrid.add(ageField, 1,3);
        formGrid.add(addressLabel, 0,4);
        formGrid.add(addressField, 1,4);
        formGrid.add(polType, 0, 5, 2, 1);
        formGrid.add(sumInsuredLabel, 0,6);
        formGrid.add(sumInsured, 1,6);
        formGrid.add(covAmountLabel, 0,7);
        formGrid.add(covField, 1,7);
        formGrid.add(premAmountLabel, 0,8);
        formGrid.add(premField, 1,8);
        formGrid.add(calcPremiumBtn, 1,9);
        formGrid.add(submitBtn, 0,10);
        formGrid.add(viewPoliciesBtn, 1,10);
        formGrid.add(tableView, 0,11,2,1);
        
        //VBox
        VBox root = new VBox(10, title, formGrid);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #e6f2ff;");
        root.setAlignment(Pos.TOP_CENTER);
        
        primaryStage.setScene(new Scene(root, 1000, 700));
        primaryStage.setTitle("Digital Insurance Management System");
        primaryStage.show();
    }

    public static class Policy {
        private final String idNumber; 
        private final String customerName; 
        private final String customerSurname; 
        private final int age;
        private final String address;
        private final String policyType;
        private final double sumInsured;
        private final double coverageAmount;
        private final double premiumAmount;
        
        public Policy(String idNumber, String customerName, String customerSurname, int age, String address, String policyType, double sumInsured, double coverageAmount, double premiumAmount) {
            this.idNumber = idNumber;
            this.customerName = customerName;
            this.customerSurname = customerSurname;
            this.age = age;
            this.address = address;
            this.policyType = policyType;
            this.sumInsured = sumInsured;
            this.coverageAmount = coverageAmount;
            this.premiumAmount = premiumAmount;

        }
        
        //Getters
        public String getIdNumber() {return idNumber;}
        public String getCustomerName() {return customerName;}
        public String getCustomerSurname() {return customerSurname;}
        public int getAge() {return age;}
        public String getAddress() {return address;}
        public String getPolicyType() {return policyType;}
        public double getSumInsured() {return sumInsured;}
        public double getCoverageAmount() {return coverageAmount;}
        public double getPremiumAmount() {return premiumAmount;}
    }
    
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}