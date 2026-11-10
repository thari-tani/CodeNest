package econest.client.dao;

import econest.client.exception.DatabaseException;
import econest.client.model.Client;
import econest.client.model.ClientType;

import java.util.List;
import java.util.Optional;

// the service only knows about this interface, so we can swap MySQL for the in-memory
// version when testing
public interface ClientDAO {
    Client insert(Client client) throws DatabaseException;      // gives back the client with its new id
    boolean update(Client client) throws DatabaseException;
    boolean delete(int clientId) throws DatabaseException;
    Optional<Client> findById(int clientId) throws DatabaseException;
    Optional<Client> findByEmail(String email) throws DatabaseException;
    Optional<Client> findByBusinessRegNo(String regNo) throws DatabaseException;
    List<Client> findAll() throws DatabaseException;
    List<Client> findByType(ClientType type) throws DatabaseException;
    List<Client> searchByName(String keyword) throws DatabaseException;
}
