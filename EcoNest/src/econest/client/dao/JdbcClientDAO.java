package econest.client.dao;

import econest.client.db.DBConnection;
import econest.client.exception.DatabaseException;
import econest.client.model.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// talks to MySQL. PreparedStatements everywhere so no SQL injection
public class JdbcClientDAO implements ClientDAO {

    private static final String COLS =
        "client_id, client_type, name, email, phone, address, registered_date, " +
        "household_size, company_name, contact_person, business_reg_no";

    private final DBConnection db = DBConnection.getInstance();

    @Override
    public Client insert(Client c) throws DatabaseException {
        String sql = "INSERT INTO clients (client_type, name, email, phone, address, registered_date, " +
                     "household_size, company_name, contact_person, business_reg_no) VALUES (?,?,?,?,?,?,?,?,?,?)";
        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            bind(ps, c, false);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) c.setClientId(keys.getInt(1));
            }
            return c;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to insert client: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean update(Client c) throws DatabaseException {
        String sql = "UPDATE clients SET client_type=?, name=?, email=?, phone=?, address=?, registered_date=?, " +
                     "household_size=?, company_name=?, contact_person=?, business_reg_no=? WHERE client_id=?";
        try (Connection con = db.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            bind(ps, c, true);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to update client: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean delete(int clientId) throws DatabaseException {
        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement("DELETE FROM clients WHERE client_id=?")) {
            ps.setInt(1, clientId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to delete client (it may have existing orders): " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Client> findById(int id) throws DatabaseException {
        return queryOne("SELECT " + COLS + " FROM clients WHERE client_id=?", id);
    }

    @Override
    public Optional<Client> findByEmail(String email) throws DatabaseException {
        return queryOne("SELECT " + COLS + " FROM clients WHERE LOWER(email)=LOWER(?)", email);
    }

    @Override
    public Optional<Client> findByBusinessRegNo(String regNo) throws DatabaseException {
        return queryOne("SELECT " + COLS + " FROM clients WHERE business_reg_no=?", regNo);
    }

    @Override
    public List<Client> findAll() throws DatabaseException {
        return queryList("SELECT " + COLS + " FROM clients ORDER BY client_id");
    }

    @Override
    public List<Client> findByType(ClientType type) throws DatabaseException {
        return queryList("SELECT " + COLS + " FROM clients WHERE client_type=? ORDER BY client_id", type.name());
    }

    @Override
    public List<Client> searchByName(String keyword) throws DatabaseException {
        return queryList("SELECT " + COLS + " FROM clients WHERE name LIKE ? ORDER BY name", "%" + keyword + "%");
    }

    // fills the ? placeholders, household and office use different columns
    private void bind(PreparedStatement ps, Client c, boolean withId) throws SQLException {
        ps.setString(1, c.getClientType().name());
        ps.setString(2, c.getName());
        ps.setString(3, c.getEmail());
        ps.setString(4, c.getPhone());
        ps.setString(5, c.getAddress());
        ps.setDate(6, Date.valueOf(c.getRegisteredDate()));

        if (c instanceof HouseholdClient) {
            ps.setInt(7, ((HouseholdClient) c).getHouseholdSize());
            ps.setNull(8, Types.VARCHAR);
            ps.setNull(9, Types.VARCHAR);
            ps.setNull(10, Types.VARCHAR);
        } else if (c instanceof OfficeClient) {
            OfficeClient o = (OfficeClient) c;
            ps.setNull(7, Types.INTEGER);
            ps.setString(8, o.getCompanyName());
            ps.setString(9, o.getContactPerson());
            ps.setString(10, o.getBusinessRegNo());
        }
        if (withId) ps.setInt(11, c.getClientId());
    }

    private Optional<Client> queryOne(String sql, Object param) throws DatabaseException {
        List<Client> list = queryList(sql, param);
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }

    private List<Client> queryList(String sql, Object... params) throws DatabaseException {
        List<Client> result = new ArrayList<>();
        try (Connection con = db.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) ps.setObject(i + 1, params[i]);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) result.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Query failed: " + e.getMessage(), e);
        }
        return result;
    }

    // turns one DB row into the right object (household or office)
    private Client mapRow(ResultSet rs) throws SQLException {
        ClientType type = ClientType.valueOf(rs.getString("client_type"));
        Client c;
        if (type == ClientType.HOUSEHOLD) {
            c = new HouseholdClient(rs.getString("name"), rs.getString("email"), rs.getString("phone"),
                    rs.getString("address"), rs.getInt("household_size"));
        } else {
            c = new OfficeClient(rs.getString("name"), rs.getString("email"), rs.getString("phone"),
                    rs.getString("address"), rs.getString("company_name"),
                    rs.getString("contact_person"), rs.getString("business_reg_no"));
        }
        c.setClientId(rs.getInt("client_id"));
        c.setRegisteredDate(rs.getDate("registered_date").toLocalDate());
        return c;
    }
}
