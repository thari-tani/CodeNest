package econest.client.model;

// normal home customer
public class HouseholdClient extends Client {

    private int householdSize;

    public HouseholdClient(String name, String email, String phone, String address, int householdSize) {
        super(name, email, phone, address);
        this.householdSize = householdSize;
    }

    public int getHouseholdSize() { return householdSize; }
    public void setHouseholdSize(int householdSize) { this.householdSize = householdSize; }

    @Override
    public ClientType getClientType() { return ClientType.HOUSEHOLD; }

    @Override
    public double getDiscountRate() { return 0.0; }

    @Override
    public String getTypeSpecificDetails() { return "Household size: " + householdSize; }
}
