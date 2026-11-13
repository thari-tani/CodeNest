package econest.client.ui;

import econest.client.model.Client;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

// feeds the list of clients into the JTable
public class ClientTableModel extends AbstractTableModel {

    private static final String[] COLUMNS = {"ID", "Type", "Name", "Email", "Phone", "Address", "Details"};
    private List<Client> clients = new ArrayList<>();

    public void setClients(List<Client> clients) {
        this.clients = new ArrayList<>(clients);
        fireTableDataChanged();
    }

    public Client getClientAt(int row) { return clients.get(row); }

    @Override public int getRowCount() { return clients.size(); }
    @Override public int getColumnCount() { return COLUMNS.length; }
    @Override public String getColumnName(int col) { return COLUMNS[col]; }

    @Override
    public Class<?> getColumnClass(int col) { return col == 0 ? Integer.class : String.class; }

    @Override
    public Object getValueAt(int row, int col) {
        Client c = clients.get(row);
        switch (col) {
            case 0: return c.getClientId();
            case 1: return c.getClientType().getLabel();
            case 2: return c.getName();
            case 3: return c.getEmail();
            case 4: return c.getPhone();
            case 5: return c.getAddress();
            default: return c.getTypeSpecificDetails();
        }
    }
}
