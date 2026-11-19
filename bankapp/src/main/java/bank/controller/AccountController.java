package bank.controller;

import bank.dao.AccountDAO;
import bank.model.Account;
import bank.model.Withdrawable;

public class AccountController {
    private final AccountDAO accountDAO;

    public AccountController(AccountDAO accountDAO) {
        this.accountDAO = accountDAO;
    }

    public void deposit(Account account, double amount) {
        account.deposit(amount);
        accountDAO.update(account);
    }

    public void withdraw(Account account, double amount) {
        if (account instanceof Withdrawable w) {
            w.withdraw(amount);
            accountDAO.update(account);
        } else {
            throw new IllegalStateException(
                "Withdrawals are not allowed on a " + account.getAccountType() + " account");
        }
    }
}
