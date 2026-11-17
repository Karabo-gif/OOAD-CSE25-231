package bank;

import bank.model.*;

public class App {
    public static void main(String[] args) {
        IndividualCustomer c = new IndividualCustomer("C001", "Gaborone", "pass123",
                "Neo", "Kgosi", "123456789", 30, true);

        SavingsAccount sav = c.openSavingsAccount("S001", "Main Mall");
        InvestmentAccount inv = c.openInvestmentAccount("I001", "Main Mall", 500);
        ChequeAccount chq = c.openChequeAccount("CH001", "Main Mall", "ABC Ltd", "Plot 1, Gaborone");

        sav.deposit(1000);
        inv.deposit(1000);
        chq.deposit(1000);
        chq.withdraw(100);
        inv.withdraw(200);

        sav.payInterest();
        inv.payInterest();

        c.viewAccountInformation();
    }
}