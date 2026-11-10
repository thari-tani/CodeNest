package econest.client;

import econest.client.dao.ClientDAO;
import econest.client.dao.InMemoryClientDAO;
import econest.client.dao.JdbcClientDAO;
import econest.client.exception.ClientException;
import econest.client.model.*;
import econest.client.service.ClientService;

// quick console test of the backend. --sample = in-memory, no args = MySQL
public class Main {

    private static int passed = 0, failed = 0;

    public static void main(String[] args) {
        boolean useDb = args.length > 0 && args[0].equals("--sample");
        ClientDAO dao = !useDb ? new JdbcClientDAO() : new InMemoryClientDAO();
        ClientService service = new ClientService(dao);
        System.out.println("Storage: " + (!useDb ? "MySQL (JDBC)" : "In-memory") + "\n");

        try {
            section("ADD");
            Client h = service.addClient(new HouseholdClient("Nimal Perera", "Nimal@Mail.com ", "0771234567", "12 Lake Rd, Kilinochchi", 4));
            Client o = service.addClient(new OfficeClient("Green Tech Pvt Ltd", "info@greentech.lk", "+94112345678",
                    "45 Galle Rd, Colombo 03", "Green Tech", "Kamala Silva", "PV12345"));
            check("household id assigned", h.getClientId() > 0);
            check("email normalised to lower case", h.getEmail().equals("nimal@mail.com"));
            check("office client has 5% discount", o.getDiscountRate() == 0.05);

            section("VALIDATION");
            expectFail("bad email / phone / size", () ->
                    service.addClient(new HouseholdClient("A", "not-an-email", "123", "x", 0)));
            expectFail("duplicate email", () ->
                    service.addClient(new HouseholdClient("Another Nimal", "nimal@mail.com", "0771111111", "99 Hill St, Jaffna", 2)));
            expectFail("duplicate business reg no", () ->
                    service.addClient(new OfficeClient("Other Co", "other@co.lk", "0112223334", "7 Main St, Kandy", "Other", "Sam", "PV12345")));

            section("UPDATE");
            h.setPhone("0779998888");
            ((HouseholdClient) h).setHouseholdSize(5);
            service.updateClient(h);
            check("phone updated", service.getClient(h.getClientId()).getPhone().equals("0779998888"));
            h.setEmail("info@greentech.lk");
            expectFail("update to another client's email", () -> service.updateClient(h));
            h.setEmail("nimal@mail.com");

            section("SEARCH / LIST");
            check("2 clients total", service.getAllClients().size() == 2);
            check("1 office client", service.getClientsByType(ClientType.OFFICE).size() == 1);
            check("search 'nim' finds Nimal", service.searchClients("nim").size() == 1);
            service.getAllClients().forEach(System.out::println);

            section("REMOVE");
            service.removeClient(h.getClientId());
            check("1 client left", service.getAllClients().size() == 1);
            expectFail("remove missing client", () -> service.removeClient(999));
            service.removeClient(o.getClientId());   // clean up so the demo can be re-run on MySQL

        } catch (ClientException e) {
            System.out.println("UNEXPECTED: " + e.getMessage());
            failed++;
        }
        System.out.println("\nResult: " + passed + " passed, " + failed + " failed");
        System.exit(failed == 0 ? 0 : 1);
    }

    private interface Action { void run() throws ClientException; }

    private static void section(String s) { System.out.println("--- " + s + " ---"); }

    private static void check(String name, boolean ok) {
        System.out.println((ok ? "  PASS  " : "  FAIL  ") + name);
        if (ok) passed++; else failed++;
    }

    private static void expectFail(String name, Action a) {
        try { a.run(); check(name + " (should have been rejected)", false); }
        catch (ClientException e) { check(name + " -> rejected: " + e.getClass().getSimpleName(), true); }
    }
}
