package bank.model;

public class InvestmentAccount extends Account implements InterestInterface, Withdrawable {
    private static final double INTEREST_RATE = 0.05; // 5% monthly
    private static final double MIN_OPENING_DEPOSIT = 500.00;

    public InvestmentAccount(String accountNumber, String accountBranch,
                             Customer owner, double initialDeposit) {
        super(accountNumber, accountBranch, owner);
        if (initialDeposit < MIN_OPENING_DEPOSIT) {
            throw new IllegalArgumentException("Investment account needs an opening deposit of at least BWP"
                    + MIN_OPENING_DEPOSIT);
        }
        deposit(initialDeposit, "Opening deposit");
    }

    @Override
    public void withdraw(double amount) {
        processWithdrawal(amount);
    }

    @Override
    public double calculateInterest() {
        return getBalance() * INTEREST_RATE;
    }

    @Override
    public void payInterest() {
        double interest = calculateInterest();
        if (interest > 0) {
            increaseBalance(interest);
            recordTransaction("Interest", interest);
        }
    }

    @Override
    public String getAccountType() { return "Investment"; }
}