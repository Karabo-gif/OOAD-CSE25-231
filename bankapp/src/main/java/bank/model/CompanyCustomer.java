package bank.model;

public class CompanyCustomer extends Customer {
    private final String companyName;
    private final String companyRegistry;

    public CompanyCustomer(String customerId, String address, String password,
                           String companyName, String companyRegistry) {
        super(customerId, address, password);
        this.companyName = companyName;
        this.companyRegistry = companyRegistry;
    }

    public String getCompanyRegistry() { return companyRegistry; }

    @Override
    public String getFullName() { return companyName; }
}