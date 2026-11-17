package bank.model;

public class ChequeAccount extends Account implements Withdrawable {
    private static final double DEPOSIT_FEE_RATE = 0.02;   // P2 per P100 deposited
    private static final double WITHDRAWAL_FEE_RATE = 0.01; // P1 per P100 withdrawn

    private final String employerName;
    private final String employerAddress;

    public ChequeAccount(String accountNumber, String accountBranch, IndividualCustomer owner,
                         String employerName, String employerAddress) {
        super(accountNumber, accountBranch, owner);
        if (!owner.isEmployed()) {
            throw new IllegalArgumentException("Only employed individual customers can open a cheque account");
        }
        this.employerName = employerName;
        this.employerAddress = employerAddress;
    }

    public String getEmployerName() { return employerName; }
    public String getEmployerAddress() { return employerAddress; }

    @Override
    public void deposit(double amount, String note) {
        super.deposit(amount, note);
        double fee = amount * DEPOSIT_FEE_RATE;
        decreaseBalance(fee);
        recordTransaction("Deposit fee", fee);
    }

    @Override
    public void withdraw(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Withdrawal must be greater than zero");
        }
        double fee = amount * WITHDRAWAL_FEE_RATE;
        if (amount + fee > getBalance()) {
            throw new IllegalStateException("Insufficient funds (including withdrawal fee)");
        }
        decreaseBalance(amount);
        recordTransaction("Withdrawal", amount);
        decreaseBalance(fee);
        recordTransaction("Withdrawal fee", fee);
    }

    @Override
    public String getAccountType() {
         return "Cheque"; 
        }
}
