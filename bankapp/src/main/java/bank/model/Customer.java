package bank.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class Customer {
    private final String customerId;
    private String address;
    private String password;
    private final List<Account> accounts = new ArrayList<>();

    public Customer(String customerId, String address, String password) {
        this.customerId = customerId;
        this.address = address;
        this.password = password;
    }

    public String getCustomerId() { return customerId; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getPassword() { return password; }
    public List<Account> getAccounts() { return Collections.unmodifiableList(accounts); }

    public boolean checkPassword(String attempt) {
        return password.equals(attempt);
    }

    public abstract String getFullName();

    // One account of each type per customer
    protected void addAccount(Account account) {
        for (Account existing : accounts) {
            if (existing.getAccountType().equals(account.getAccountType())) {
                throw new IllegalStateException(
                        "Customer already has a " + account.getAccountType() + " account");
            }
        }
        accounts.add(account);
    }

    public void attachAccount(Account account) {
        addAccount(account);
    }

    public SavingsAccount openSavingsAccount(String number, String branch) {
        SavingsAccount account = new SavingsAccount(number, branch, this);
        addAccount(account);
        return account;
    }

    public InvestmentAccount openInvestmentAccount(String number, String branch, double initialDeposit) {
        InvestmentAccount account = new InvestmentAccount(number, branch, this, initialDeposit);
        addAccount(account);
        return account;
    }

    public void viewAccountInformation() {
        System.out.println("Accounts for " + getFullName() + ":");
        for (Account a : accounts) {
            System.out.printf("  %s %s - balance BWP%.2f%n",
                    a.getAccountType(), a.getAccountNumber(), a.getBalance());
        }
    }
}