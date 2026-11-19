package bank.view;

import bank.controller.AccountController;
import bank.controller.LoginController;
import bank.dao.AccountDAO;
import bank.dao.CustomerDAO;
import bank.dao.DataSeeder;
import bank.dao.DatabaseManager;
import bank.model.Customer;
import javafx.application.Application;
import javafx.stage.Stage;

public class MainApp extends Application {
    private final CustomerDAO dao;
    private final LoginController loginController;
    private final AccountController accountController;
    private Stage stage;

    public MainApp() {
        DatabaseManager.initSchema();
        dao = new CustomerDAO();
        DataSeeder.seedIfEmpty(dao);
        loginController = new LoginController(dao);
        accountController = new AccountController(new AccountDAO());
    }

    @Override
    public void start(Stage primaryStage) {
        this.stage = primaryStage;
        stage.setTitle("Banking System");
        showLogin();
        stage.show();
    }

    private void showLogin() {
        stage.setScene(new LoginView(loginController, this::showAccounts).build());
    }

    private void showAccounts(Customer customer) {
        stage.setScene(new AccountView(customer, accountController, this::showLogin).build());
    }

    public static void main(String[] args) {
        launch(args);
    }
}