package econest.client.model;

// company customer, gets 5% off because they buy in bulk
public class OfficeClient extends Client {

    private String companyName;
    private String contactPerson;
    private String businessRegNo;

    public OfficeClient(String name, String email, String phone, String address,
                        String companyName, String contactPerson, String businessRegNo) {
        super(name, email, phone, address);
        this.companyName = companyName;
        this.contactPerson = contactPerson;
        this.businessRegNo = businessRegNo;
    }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public String getContactPerson() { return contactPerson; }
    public void setContactPerson(String contactPerson) { this.contactPerson = contactPerson; }

    public String getBusinessRegNo() { return businessRegNo; }
    public void setBusinessRegNo(String businessRegNo) { this.businessRegNo = businessRegNo; }

    @Override
    public ClientType getClientType() { return ClientType.OFFICE; }

    @Override
    public double getDiscountRate() { return 0.05; }

    @Override
    public String getTypeSpecificDetails() {
        return "Company: " + companyName + ", Contact: " + contactPerson + ", BRN: " + businessRegNo;
    }
}
