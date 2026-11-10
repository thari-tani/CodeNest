package econest.client;

import econest.client.dao.ClientDAO;
import econest.client.dao.InMemoryClientDAO;
import econest.client.dao.JdbcClientDAO;
import econest.client.model.HouseholdClient;
import econest.client.model.OfficeClient;
import econest.client.service.ClientService;
import econest.client.ui.ClientFrame;

import javax.swing.*;

// run this one to open the window. --db uses MySQL, otherwise a few sample clients are loaded
public class ClientApp {

    public static void main(String[] args) {
        boolean useDb = args.length > 0 && args[0].equals("--example");

        SwingUtilities.invokeLater(() -> {
            try {
                for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                    if ("Nimbus".equals(info.getName())) { UIManager.setLookAndFeel(info.getClassName()); break; }
                }
            } catch (Exception ignored) { /* fall back to the default look and feel */ }

            ClientDAO dao = !useDb ? new JdbcClientDAO() : new InMemoryClientDAO();
            ClientService service = new ClientService(dao);
            if (useDb) seedSamples(service);
            new ClientFrame(service).setVisible(true);
        });
    }

    private static void seedSamples(ClientService s) {
        try {
            s.addClient(new HouseholdClient("Nimal Perera", "nimal@mail.com", "0771234567", "12 Lake Rd, Kilinochchi", 4));
            s.addClient(new OfficeClient("Green Tech Pvt Ltd", "info@greentech.lk", "0112345678",
                    "45 Galle Rd, Colombo 03", "Green Tech", "Kamala Silva", "PV12345"));
            s.addClient(new HouseholdClient("Anjali Fernando", "anjali@mail.com", "0714567890", "8 Temple Lane, Jaffna", 3));
        } catch (Exception e) { System.err.println("Could not seed samples: " + e.getMessage()); }
    }
}
