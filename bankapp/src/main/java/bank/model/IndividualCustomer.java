package bank.model;

public class IndividualCustomer extends Customer {
    private final String firstName;
    private final String surname;
    private final String idNumber;
    private final int age;
    private boolean employed;

    public IndividualCustomer(String customerId, String address, String password,String firstName, String surname, String idNumber, int age, boolean employed) {
        super(customerId, address, password);
        if (age < 18) {
            throw new IllegalArgumentException("Customer must be at least 18");
        }
        this.firstName = firstName;
        this.surname = surname;
        this.idNumber = idNumber;
        this.age = age;
        this.employed = employed;
    }

    public boolean isEmployed() { return employed; }
    public void setEmployed(boolean employed) { this.employed = employed; }
    public String getIdNumber() { return idNumber; }
    public int getAge() { return age; }

    @Override
    public String getFullName() { return firstName + " " + surname; }

    public ChequeAccount openChequeAccount(String number, String branch,
                                           String employerName, String employerAddress) {
        ChequeAccount account = new ChequeAccount(number, branch, this, employerName, employerAddress);
        addAccount(account);
        return account;
    }

        public String getFirstName() { return firstName; }
    public String getSurname() { return surname; }
}
