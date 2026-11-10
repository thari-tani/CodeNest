package econest.client.model;

import java.time.LocalDate;
import java.util.Objects;

// parent class for both client types, you can't do "new Client()" on it
public abstract class Client {

    private int clientId;            // stays 0 until the DB gives it an id
    private String name;
    private String email;
    private String phone;
    private String address;
    private LocalDate registeredDate;

    protected Client(String name, String email, String phone, String address) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.registeredDate = LocalDate.now();
    }

    // each child class has to fill these in
    public abstract ClientType getClientType();

    public abstract double getDiscountRate();   // the orders module will use this

    public abstract String getTypeSpecificDetails();

    // getters and setters
    public int getClientId() { return clientId; }
    public void setClientId(int clientId) { this.clientId = clientId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public LocalDate getRegisteredDate() { return registeredDate; }
    public void setRegisteredDate(LocalDate registeredDate) { this.registeredDate = registeredDate; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Client)) return false;
        Client other = (Client) o;
        return clientId != 0 && clientId == other.clientId;
    }

    @Override
    public int hashCode() { return Objects.hash(clientId); }

    @Override
    public String toString() {
        return String.format("[%d] %-9s | %-22s | %-26s | %-12s | %s | %s",
                clientId, getClientType(), name, email, phone, address, getTypeSpecificDetails());
    }
}
