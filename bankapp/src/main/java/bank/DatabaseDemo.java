package bank;

import bank.dao.*;
import bank.model.*;

public class DatabaseDemo {
    public static void main(String[] args) {
        DatabaseManager.initSchema();
        CustomerDAO customerDAO = new CustomerDAO();
        AccountDAO accountDAO = new AccountDAO();
        DataSeeder.seedIfEmpty(customerDAO);

        System.out.println("=== All customers in the database ===");
        for (Customer c : customerDAO.findAll()) {
            System.out.println(c.getCustomerId() + " - " + c.getFullName());
            c.viewAccountInformation();
        }

        System.out.println("\n=== Persistence test ===");
        Customer neo = customerDAO.findById("C001");
        Account savings = neo.getAccounts().stream()
                .filter(a -> a.getAccountType().equals("Savings")).findFirst().orElseThrow();
        System.out.printf("Savings before: BWP%.2f%n", savings.getBalance());
        savings.deposit(100);
        accountDAO.update(savings);

        Customer reloaded = customerDAO.findById("C001");
        Account again = reloaded.getAccounts().stream()
                .filter(a -> a.getAccountType().equals("Savings")).findFirst().orElseThrow();
        System.out.printf("Savings after reload from database: BWP%.2f%n", again.getBalance());
    }
}