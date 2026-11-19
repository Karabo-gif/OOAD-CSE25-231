package bank.model;

import java.time.LocalDateTime;

public class Transaction {
    private final String transactionId;
    private final String type;
    private final double amount;
    private final LocalDateTime date;
    private final double balanceAfter;
    
    
    public Transaction(String transactionId, String type, double amount, double balanceAfter) {
    this.transactionId = transactionId;
    this.type = type;
    this.amount = amount;
    this.date = LocalDateTime.now();
    this.balanceAfter = balanceAfter;
}
    public Transaction(String transactionId, String type, double amount, LocalDateTime date, double balanceAfter) {
        this.transactionId = transactionId;
        this.type = type;
        this.amount = amount;
        this.date = LocalDateTime.now();
        this.balanceAfter = balanceAfter;
    }

    /*dont need setters bc this is read only in the brief */
    
    public String getTransactionId() { return transactionId; }

    public String getType() { return type; }

    public double getAmount() { return amount; }

    public LocalDateTime getDate() { return date; }

    public double getBalanceAfter() { return balanceAfter; }
}