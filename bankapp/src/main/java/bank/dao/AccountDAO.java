package bank.dao;

import bank.model.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AccountDAO {

    // CREATE
    public void insert(Account a) {
        String sql = "INSERT INTO accounts (account_number, account_type, branch, balance, "
                + "customer_id, employer_name, employer_address) VALUES (?,?,?,?,?,?,?)";
        try (Connection con = DatabaseManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, a.getAccountNumber());
            ps.setString(2, a.getAccountType());
            ps.setString(3, a.getAccountBranch());
            ps.setDouble(4, a.getBalance());
            ps.setString(5, a.getOwner().getCustomerId());
            if (a instanceof ChequeAccount chq) {
                ps.setString(6, chq.getEmployerName());
                ps.setString(7, chq.getEmployerAddress());
            } else {
                ps.setNull(6, Types.VARCHAR);
                ps.setNull(7, Types.VARCHAR);
            }
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Could not insert account", e);
        }
        saveTransactions(a);
    }

    // READ
    public List<Account> findByCustomer(Customer owner) {
        List<Account> accounts = new ArrayList<>();
        String sql = "SELECT * FROM accounts WHERE customer_id = ?";
        try (Connection con = DatabaseManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, owner.getCustomerId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Account a = build(rs, owner);
                    a.restore(rs.getDouble("balance"), loadTransactions(a.getAccountNumber()));
                    accounts.add(a);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Could not read accounts", e);
        }
        return accounts;
    }

    // UPDATE (balance, plus any new transactions)
    public void update(Account a) {
        String sql = "UPDATE accounts SET balance = ? WHERE account_number = ?";
        try (Connection con = DatabaseManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDouble(1, a.getBalance());
            ps.setString(2, a.getAccountNumber());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Could not update account", e);
        }
        saveTransactions(a);
    }

    // DELETE (all accounts and transactions of one customer)
    public void deleteByCustomer(String customerId) {
        try (Connection con = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = con.prepareStatement(
                    "DELETE FROM transactions WHERE account_number IN "
                    + "(SELECT account_number FROM accounts WHERE customer_id = ?)")) {
                ps.setString(1, customerId);
                ps.executeUpdate();
            }
            try (PreparedStatement ps = con.prepareStatement(
                    "DELETE FROM accounts WHERE customer_id = ?")) {
                ps.setString(1, customerId);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Could not delete accounts", e);
        }
    }

    private Account build(ResultSet rs, Customer owner) throws SQLException {
        String number = rs.getString("account_number");
        String branch = rs.getString("branch");
        return switch (rs.getString("account_type")) {
            case "Savings" -> new SavingsAccount(number, branch, owner);
            case "Investment" -> new InvestmentAccount(number, branch, owner);
            case "Cheque" -> new ChequeAccount(number, branch, (IndividualCustomer) owner,
                    rs.getString("employer_name"), rs.getString("employer_address"));
            default -> throw new IllegalStateException("Unknown account type");
        };
    }

    private List<Transaction> loadTransactions(String accountNumber) {
        List<Transaction> list = new ArrayList<>();
        String sql = "SELECT * FROM transactions WHERE account_number = ? ORDER BY tx_date";
        try (Connection con = DatabaseManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, accountNumber);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Transaction(rs.getString("transaction_id"), rs.getString("tx_type"),
                            rs.getDouble("amount"), rs.getTimestamp("tx_date").toLocalDateTime(),
                            rs.getDouble("balance_after")));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Could not read transactions", e);
        }
        return list;
    }

    // MERGE inserts a new transaction or ignores one that is already saved
    private void saveTransactions(Account a) {
        String sql = "MERGE INTO transactions (transaction_id, account_number, tx_type, amount, "
                + "tx_date, balance_after) KEY (transaction_id) VALUES (?,?,?,?,?,?)";
        try (Connection con = DatabaseManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            for (Transaction t : a.getTransactions()) {
                ps.setString(1, t.getTransactionId());
                ps.setString(2, a.getAccountNumber());
                ps.setString(3, t.getType());
                ps.setDouble(4, t.getAmount());
                ps.setTimestamp(5, Timestamp.valueOf(t.getDate()));
                ps.setDouble(6, t.getBalanceAfter());
                ps.addBatch();
            }
            ps.executeBatch();
        } catch (SQLException e) {
            throw new RuntimeException("Could not save transactions", e);
        }
    }
}