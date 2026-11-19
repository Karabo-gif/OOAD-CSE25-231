package bank.controller;

import bank.dao.CustomerDAO;
import bank.model.Customer;

public class LoginController {
    private final CustomerDAO dao;

    public LoginController(CustomerDAO dao) {
        this.dao = dao;
    }

    public Customer login(String id, String password) {
        Customer c = dao.findById(id);
        if (c == null || !c.checkPassword(password)) {
            throw new IllegalArgumentException("Invalid customer ID or password");
        }
        return c;
    }
}