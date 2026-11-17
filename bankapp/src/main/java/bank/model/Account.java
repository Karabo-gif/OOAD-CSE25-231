package bank.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID; /*Universally Unique Identifier a long random ID that’s never repeated */

public abstract class Account {
    private final String accountNumber;
    private final String accountBranch;
    private final Customer owner;
    private double accountBalance;
    private final List<Transaction> transactions = new ArrayList<>();

    public Account(String accountNumber, String accountBranch, Customer owner) {
        this.accountNumber = accountNumber;
        this.accountBranch = accountBranch;
        this.owner = owner;
    }

    public String getAccountNumber() { return accountNumber; }
    public String getAccountBranch() { return accountBranch; }
    public Customer getOwner() { return owner; }
    public double getBalance() { return accountBalance; }
    public List<Transaction> getTransactions() { return Collections.unmodifiableList(transactions); }

    public abstract String getAccountType();

    // Overloaded deposit methods
    public void deposit(double amount) {
        deposit(amount, "Deposit");
    }

    public void deposit(double amount, String note) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Deposit must be greater than zero");
        }
        increaseBalance(amount);
        recordTransaction(note, amount);
    }

    protected void increaseBalance(double amount) {
        accountBalance += amount;
    }

    protected void decreaseBalance(double amount) {
        accountBalance -= amount;
    }

    protected void recordTransaction(String type, double amount) {
        String id = UUID.randomUUID().toString().substring(0, 8);
        transactions.add(new Transaction(id, type, amount, accountBalance));
    }

    // Shared withdrawal logic for accounts that allow withdrawals
    protected void processWithdrawal(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Withdrawal must be greater than zero");
        }
        if (amount > accountBalance) {
            throw new IllegalStateException("Insufficient funds");
        }
        decreaseBalance(amount);
        recordTransaction("Withdrawal", amount);
    }
}