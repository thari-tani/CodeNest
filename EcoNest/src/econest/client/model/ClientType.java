package econest.client.model;

// only two kinds of client for now
public enum ClientType {
    HOUSEHOLD("Household"),
    OFFICE("Office");

    private final String label;

    ClientType(String label) { this.label = label; }

    public String getLabel() { return label; }

    @Override
    public String toString() { return label; }
}
