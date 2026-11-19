package bank.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
    private static final String URL = "jdbc:h2:./data/bankdb";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, "sa", "");
    }

    public static void initSchema() {
        try (Connection con = getConnection(); Statement st = con.createStatement()) {
            st.execute("""
                CREATE TABLE IF NOT EXISTS customers (
                    customer_id      VARCHAR(20)  PRIMARY KEY,
                    customer_type    VARCHAR(12)  NOT NULL,
                    address          VARCHAR(100),
                    user_password    VARCHAR(50)  NOT NULL,
                    first_name       VARCHAR(50),
                    surname          VARCHAR(50),
                    id_number        VARCHAR(20),
                    age              INT,
                    employed         BOOLEAN,
                    company_name     VARCHAR(100),
                    company_registry VARCHAR(30)
                )""");
            st.execute("""
                CREATE TABLE IF NOT EXISTS accounts (
                    account_number   VARCHAR(20)   PRIMARY KEY,
                    account_type     VARCHAR(12)   NOT NULL,
                    branch           VARCHAR(50),
                    balance          DECIMAL(14,2) NOT NULL,
                    customer_id      VARCHAR(20)   NOT NULL,
                    employer_name    VARCHAR(100),
                    employer_address VARCHAR(150),
                    FOREIGN KEY (customer_id) REFERENCES customers(customer_id),
                    UNIQUE (customer_id, account_type)
                )""");
            st.execute("""
                CREATE TABLE IF NOT EXISTS transactions (
                    transaction_id VARCHAR(20)   PRIMARY KEY,
                    account_number VARCHAR(20)   NOT NULL,
                    tx_type        VARCHAR(30),
                    amount         DECIMAL(14,2),
                    tx_date        TIMESTAMP,
                    balance_after  DECIMAL(14,2),
                    FOREIGN KEY (account_number) REFERENCES accounts(account_number)
                )""");
        } catch (SQLException e) {
            throw new RuntimeException("Could not create schema", e);
        }
    }
}