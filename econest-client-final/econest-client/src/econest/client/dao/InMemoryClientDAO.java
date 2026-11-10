package econest.client.dao;

import econest.client.model.*;

import java.util.*;
import java.util.stream.Collectors;

// fake database in a TreeMap, just for testing without MySQL.
// it stores copies so editing an object later doesn't secretly change the "saved" one
public class InMemoryClientDAO implements ClientDAO {

    private final Map<Integer, Client> store = new TreeMap<>();
    private int nextId = 1;

    @Override public Client insert(Client c) {
        c.setClientId(nextId++);
        store.put(c.getClientId(), copy(c));
        return c;
    }

    @Override public boolean update(Client c) {
        if (!store.containsKey(c.getClientId())) return false;
        store.put(c.getClientId(), copy(c));
        return true;
    }

    @Override public boolean delete(int id) { return store.remove(id) != null; }

    @Override public Optional<Client> findById(int id) { return Optional.ofNullable(store.get(id)).map(this::copy); }

    @Override public Optional<Client> findByEmail(String email) {
        return store.values().stream().filter(c -> c.getEmail().equalsIgnoreCase(email)).findFirst().map(this::copy);
    }

    @Override public Optional<Client> findByBusinessRegNo(String regNo) {
        return store.values().stream()
                .filter(c -> c instanceof OfficeClient && ((OfficeClient) c).getBusinessRegNo().equals(regNo))
                .findFirst().map(this::copy);
    }

    @Override public List<Client> findAll() {
        return store.values().stream().map(this::copy).collect(Collectors.toList());
    }

    @Override public List<Client> findByType(ClientType t) {
        return store.values().stream().filter(c -> c.getClientType() == t).map(this::copy).collect(Collectors.toList());
    }

    @Override public List<Client> searchByName(String k) {
        return store.values().stream()
                .filter(c -> c.getName().toLowerCase().contains(k.toLowerCase()))
                .map(this::copy).collect(Collectors.toList());
    }

    private Client copy(Client c) {
        Client n;
        if (c instanceof HouseholdClient) {
            n = new HouseholdClient(c.getName(), c.getEmail(), c.getPhone(), c.getAddress(),
                    ((HouseholdClient) c).getHouseholdSize());
        } else {
            OfficeClient o = (OfficeClient) c;
            n = new OfficeClient(c.getName(), c.getEmail(), c.getPhone(), c.getAddress(),
                    o.getCompanyName(), o.getContactPerson(), o.getBusinessRegNo());
        }
        n.setClientId(c.getClientId());
        n.setRegisteredDate(c.getRegisteredDate());
        return n;
    }
}
