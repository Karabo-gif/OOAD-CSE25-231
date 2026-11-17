package bank.model;

public class SavingsAccount extends Account implements InterestInterface {
    private static final double INTEREST_RATE = 0.0005; // 0.05% monthly(ke the interest rate)

    public SavingsAccount(String accountNumber, String accountBranch, Customer owner) {
        super(accountNumber, accountBranch, owner);
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
    public String getAccountType() { return "Savings"; }
}