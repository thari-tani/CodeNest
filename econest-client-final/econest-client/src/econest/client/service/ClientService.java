package econest.client.service;

import econest.client.dao.ClientDAO;
import econest.client.exception.*;
import econest.client.model.*;

import java.util.List;
import java.util.Optional;

// the business logic for Task 2. The UI and the other members' modules only call this class
public class ClientService {

    private final ClientDAO dao;                 // passed in through the constructor
    private final ClientValidator validator = new ClientValidator();

    public ClientService(ClientDAO dao) { this.dao = dao; }

    public Client addClient(Client client) throws ClientException {
        normalise(client);
        validator.validate(client);
        checkDuplicates(client, 0);
        return dao.insert(client);
    }

    public void updateClient(Client client) throws ClientException {
        getClient(client.getClientId());         // throws if the id doesn't exist
        normalise(client);
        validator.validate(client);
        checkDuplicates(client, client.getClientId());
        if (!dao.update(client))
            throw new ClientNotFoundException("Client " + client.getClientId() + " could not be updated.");
    }

    public void removeClient(int clientId) throws ClientException {
        getClient(clientId);
        if (!dao.delete(clientId))
            throw new ClientNotFoundException("Client " + clientId + " could not be removed.");
    }

    public Client getClient(int clientId) throws ClientException {
        return dao.findById(clientId)
                .orElseThrow(() -> new ClientNotFoundException("No client found with id " + clientId));
    }

    // the email module needs this one
    public String getClientEmail(int clientId) throws ClientException {
        return getClient(clientId).getEmail();
    }

    public List<Client> getAllClients() throws ClientException { return dao.findAll(); }

    public List<Client> getClientsByType(ClientType type) throws ClientException { return dao.findByType(type); }

    public List<Client> searchClients(String keyword) throws ClientException {
        if (keyword == null || keyword.trim().isEmpty()) return dao.findAll();
        return dao.searchByName(keyword.trim());
    }

    // selfId lets a client keep its own email when it's being edited
    private void checkDuplicates(Client c, int selfId) throws ClientException {
        Optional<Client> byEmail = dao.findByEmail(c.getEmail());
        if (byEmail.isPresent() && byEmail.get().getClientId() != selfId)
            throw new DuplicateClientException("Email already registered: " + c.getEmail());

        if (c instanceof OfficeClient) {
            String brn = ((OfficeClient) c).getBusinessRegNo();
            Optional<Client> byBrn = dao.findByBusinessRegNo(brn);
            if (byBrn.isPresent() && byBrn.get().getClientId() != selfId)
                throw new DuplicateClientException("Business reg. no. already registered: " + brn);
        }
    }

    // trim spaces and lowercase the email so "A@x.com " and "a@x.com" are the same
    private void normalise(Client c) {
        if (c.getName() != null) c.setName(c.getName().trim());
        if (c.getEmail() != null) c.setEmail(c.getEmail().trim().toLowerCase());
        if (c.getPhone() != null) c.setPhone(c.getPhone().trim().replace(" ", ""));
        if (c.getAddress() != null) c.setAddress(c.getAddress().trim());
    }
}
