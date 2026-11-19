package bank.view;

import bank.controller.AccountController;
import bank.model.Account;
import bank.model.Customer;
import bank.model.Transaction;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import java.util.function.BiConsumer;

public class AccountView {
    private final Customer customer;
    private final AccountController controller;
    private final Runnable onLogout;

    public AccountView(Customer customer, AccountController controller, Runnable onLogout) {
        this.customer = customer;
        this.controller = controller;
        this.onLogout = onLogout;
    }

    public Scene build() {
        Label welcome = new Label("Welcome, " + customer.getFullName());
        welcome.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        ComboBox<Account> accountBox = new ComboBox<>();
        accountBox.setPromptText("Select an account");
        accountBox.getItems().addAll(customer.getAccounts());
        accountBox.setConverter(new StringConverter<>() {
            @Override public String toString(Account a) {
                return a == null ? "" : a.getAccountType() + " - " + a.getAccountNumber();
            }
            @Override public Account fromString(String s) { return null; }
        });

        Label balanceLabel = new Label("Balance: -");
        TextField amountField = new TextField();
        amountField.setPromptText("Amount");
        Button depositButton = new Button("Deposit");
        Button withdrawButton = new Button("Withdraw");
        Button logoutButton = new Button("Log out");
        ListView<String> history = new ListView<>();
        Label message = new Label();

        Runnable refresh = () -> {
            Account a = accountBox.getValue();
            history.getItems().clear();
            if (a == null) {
                balanceLabel.setText("Balance: -");
                return;
            }
            balanceLabel.setText(String.format("Balance: BWP%.2f", a.getBalance()));
            for (Transaction t : a.getTransactions()) {
                history.getItems().add(String.format("%s  %-14s %8.2f  (balance %.2f)",
                        t.getDate().toLocalDate(), t.getType(), t.getAmount(), t.getBalanceAfter()));
            }
        };

        accountBox.setOnAction(e -> refresh.run());
        depositButton.setOnAction(e ->
                transact(accountBox.getValue(), amountField.getText(), message, controller::deposit, refresh));
        withdrawButton.setOnAction(e ->
                transact(accountBox.getValue(), amountField.getText(), message, controller::withdraw, refresh));
        logoutButton.setOnAction(e -> onLogout.run());

        HBox buttons = new HBox(10, depositButton, withdrawButton, logoutButton);
        VBox root = new VBox(10, welcome, accountBox, balanceLabel, amountField, buttons,
                message, new Label("Transaction history"), history);
        root.setPadding(new Insets(20));
        return new Scene(root, 520, 520);
    }

    private void transact(Account account, String text, Label message,
                          BiConsumer<Account, Double> action, Runnable refresh) {
        try {
            if (account == null) throw new IllegalArgumentException("Select an account first");
            action.accept(account, Double.parseDouble(text.trim()));
            message.setText("Done");
        } catch (NumberFormatException ex) {
            message.setText("Enter a valid amount");
        } catch (RuntimeException ex) {
            message.setText(ex.getMessage());
        }
        refresh.run();
    }
}
