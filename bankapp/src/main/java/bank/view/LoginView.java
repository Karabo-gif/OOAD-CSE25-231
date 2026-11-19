package bank.view;

import bank.controller.LoginController;
import bank.model.Customer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import java.util.function.Consumer;

public class LoginView {
    private final LoginController controller;
    private final Consumer<Customer> onSuccess;

    public LoginView(LoginController controller, Consumer<Customer> onSuccess) {
        this.controller = controller;
        this.onSuccess = onSuccess;
    }

    public Scene build() {
        Label title = new Label("Banking System Login");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        TextField idField = new TextField();
        idField.setPromptText("Customer ID");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        Button loginButton = new Button("Log in");
        Label message = new Label();
        message.setStyle("-fx-text-fill: red;");

        loginButton.setOnAction(e -> {
            try {
                onSuccess.accept(controller.login(idField.getText().trim(), passwordField.getText()));
            } catch (IllegalArgumentException ex) {
                message.setText(ex.getMessage());
            }
        });

        VBox root = new VBox(10, title, idField, passwordField, loginButton, message);
        root.setPadding(new Insets(30));
        root.setAlignment(Pos.CENTER);
        return new Scene(root, 360, 300);
    }
}