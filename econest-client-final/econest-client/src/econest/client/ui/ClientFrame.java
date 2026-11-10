package econest.client.ui;

import econest.client.exception.ClientException;
import econest.client.exception.ValidationException;
import econest.client.model.Client;
import econest.client.model.ClientType;
import econest.client.service.ClientService;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;

// main window. No business rules in here, everything goes through ClientService
public class ClientFrame extends JFrame {

    private static final String ALL_TYPES = "All clients";

    private final ClientService service;
    private final ClientTableModel tableModel = new ClientTableModel();
    private final JTable table = new JTable(tableModel);
    private final ClientForm form = new ClientForm();
    private final JTextField searchField = Theme.textField();
    private final JComboBox<Object> filterBox = new JComboBox<>();
    private final JLabel statusLabel = new JLabel(" ");
    private final JLabel formTitle = new JLabel("New client");

    private final JButton addBtn = Theme.button("Add client", Theme.MOSS);
    private final JButton updateBtn = Theme.button("Save changes", Theme.MOSS);
    private final JButton deleteBtn = Theme.button("Remove", Theme.CLAY);
    private final JButton clearBtn = Theme.ghostButton("Clear");

    private Client selected;   // null means we are adding a new client

    public ClientFrame(ClientService service) {
        super("EcoNest - Client Management");
        this.service = service;

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1180, 700);
        setMinimumSize(new Dimension(980, 560));
        setLocationRelativeTo(null);
        getContentPane().setBackground(Theme.PAPER);
        setLayout(new BorderLayout());

        add(buildHeader(), BorderLayout.NORTH);
        add(buildCenter(), BorderLayout.CENTER);
        add(buildStatusBar(), BorderLayout.SOUTH);

        wireEvents();
        setEditMode(false);
        reload();
    }

    private JComponent buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Theme.MOSS);
        header.setBorder(BorderFactory.createEmptyBorder(14, 22, 14, 22));

        JLabel title = new JLabel("EcoNest  |  Clients");
        title.setFont(Theme.TITLE);
        title.setForeground(Color.WHITE);
        JLabel sub = new JLabel("Household and office customers");
        sub.setFont(Theme.BASE);
        sub.setForeground(new Color(0xC9D8C3));

        JPanel text = new JPanel(new GridLayout(2, 1));
        text.setOpaque(false);
        text.add(title);
        text.add(sub);
        header.add(text, BorderLayout.WEST);
        return header;
    }

    private JComponent buildCenter() {
        JPanel left = new JPanel(new BorderLayout(0, 12));
        left.setOpaque(false);
        left.setBorder(BorderFactory.createEmptyBorder(16, 20, 12, 10));
        left.add(buildToolbar(), BorderLayout.NORTH);
        left.add(buildTable(), BorderLayout.CENTER);

        JPanel right = new JPanel(new BorderLayout());
        right.setOpaque(false);
        right.setBorder(BorderFactory.createEmptyBorder(16, 10, 12, 20));
        right.setPreferredSize(new Dimension(360, 0));
        right.add(buildFormCard(), BorderLayout.CENTER);

        JPanel center = new JPanel(new BorderLayout());
        center.setOpaque(false);
        center.add(left, BorderLayout.CENTER);
        center.add(right, BorderLayout.EAST);
        return center;
    }

    private JComponent buildToolbar() {
        filterBox.addItem(ALL_TYPES);
        for (ClientType t : ClientType.values()) filterBox.addItem(t);
        filterBox.setFont(Theme.BASE);
        filterBox.setBackground(Color.WHITE);
        searchField.putClientProperty("JTextField.placeholderText", "Search by name");

        JPanel bar = new JPanel(new BorderLayout(10, 0));
        bar.setOpaque(false);
        bar.add(searchField, BorderLayout.CENTER);
        bar.add(filterBox, BorderLayout.EAST);
        return bar;
    }

    private JComponent buildTable() {
        table.setFont(Theme.BASE);
        table.setRowHeight(30);
        table.setShowVerticalLines(false);
        table.setGridColor(new Color(0xE3E8DE));
        table.setSelectionBackground(Theme.SAGE);
        table.setSelectionForeground(Theme.INK);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setAutoCreateRowSorter(true);
        table.setFillsViewportHeight(true);

        JTableHeader h = table.getTableHeader();
        h.setFont(Theme.BOLD);
        h.setBackground(Color.WHITE);
        h.setForeground(Theme.MOSS);
        h.setReorderingAllowed(false);
        h.setPreferredSize(new Dimension(0, 34));

        DefaultTableCellRenderer pad = new DefaultTableCellRenderer();
        pad.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
        table.setDefaultRenderer(String.class, pad);
        table.setDefaultRenderer(Integer.class, pad);

        int[] widths = {45, 80, 170, 190, 110, 200, 300};
        for (int i = 0; i < widths.length; i++) table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        JScrollPane scroll = new JScrollPane(table, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(0xD5DDD0)));
        scroll.getViewport().setBackground(Color.WHITE);
        return scroll;
    }

    private JComponent buildFormCard() {
        formTitle.setFont(Theme.BOLD.deriveFont(16f));
        formTitle.setForeground(Theme.INK);
        formTitle.setBorder(BorderFactory.createEmptyBorder(16, 18, 0, 18));

        JPanel buttons = new JPanel(new GridLayout(2, 2, 8, 8));
        buttons.setOpaque(false);
        buttons.add(addBtn);
        buttons.add(updateBtn);
        buttons.add(deleteBtn);
        buttons.add(clearBtn);
        JPanel buttonWrap = new JPanel(new BorderLayout());
        buttonWrap.setBackground(Color.WHITE);
        buttonWrap.setBorder(BorderFactory.createEmptyBorder(0, 18, 18, 18));
        buttonWrap.add(buttons, BorderLayout.CENTER);

        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createLineBorder(new Color(0xD5DDD0)));
        card.add(formTitle, BorderLayout.NORTH);
        card.add(form, BorderLayout.CENTER);
        card.add(buttonWrap, BorderLayout.SOUTH);
        return card;
    }

    private JComponent buildStatusBar() {
        statusLabel.setFont(Theme.BASE);
        statusLabel.setForeground(Theme.MUTED);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(8, 22, 10, 22));
        return statusLabel;
    }

    private void wireEvents() {
        addBtn.addActionListener(e -> addClient());
        updateBtn.addActionListener(e -> updateClient());
        deleteBtn.addActionListener(e -> removeClient());
        clearBtn.addActionListener(e -> { table.clearSelection(); startNewClient(); });
        filterBox.addActionListener(e -> reload());
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { reload(); }
            public void removeUpdate(DocumentEvent e) { reload(); }
            public void changedUpdate(DocumentEvent e) { reload(); }
        });
        table.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            int viewRow = table.getSelectedRow();
            if (viewRow < 0) return;
            selected = tableModel.getClientAt(table.convertRowIndexToModel(viewRow));
            form.show(selected);
            setEditMode(true);
        });
    }

    private void addClient() {
        try {
            Client saved = service.addClient(form.buildClient());
            reload();
            startNewClient();
            status("Added " + saved.getName() + " (id " + saved.getClientId() + ").");
        } catch (ClientException ex) {
            showError(ex);
        }
    }

    private void updateClient() {
        if (selected == null) return;
        try {
            Client edited = form.buildClient();
            edited.setClientId(selected.getClientId());
            edited.setRegisteredDate(selected.getRegisteredDate());   // keep original registration date
            service.updateClient(edited);
            reload();
            status("Saved changes to " + edited.getName() + ".");
        } catch (ClientException ex) {
            showError(ex);
        }
    }

    private void removeClient() {
        if (selected == null) return;
        int choice = JOptionPane.showConfirmDialog(this,
                "Remove " + selected.getName() + "? This cannot be undone.",
                "Remove client", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (choice != JOptionPane.YES_OPTION) return;
        try {
            String name = selected.getName();
            service.removeClient(selected.getClientId());
            reload();
            startNewClient();
            status("Removed " + name + ".");
        } catch (ClientException ex) {
            showError(ex);
        }
    }

    private void reload() {
        try {
            List<Client> list;
            String keyword = searchField.getText();
            if (filterBox.getSelectedItem() instanceof ClientType) {
                ClientType type = (ClientType) filterBox.getSelectedItem();
                list = service.getClientsByType(type);
                if (!keyword.trim().isEmpty()) {
                    String k = keyword.trim().toLowerCase();
                    list.removeIf(c -> !c.getName().toLowerCase().contains(k));
                }
            } else {
                list = service.searchClients(keyword);
            }
            tableModel.setClients(list);
            status(list.size() + (list.size() == 1 ? " client" : " clients") + " shown.");
        } catch (ClientException ex) {
            showError(ex);
        }
    }

    private void startNewClient() {
        selected = null;
        form.clear();
        setEditMode(false);
    }

    private void setEditMode(boolean editing) {
        addBtn.setEnabled(!editing);
        updateBtn.setEnabled(editing);
        deleteBtn.setEnabled(editing);
        formTitle.setText(editing ? "Edit client #" + selected.getClientId() : "New client");
        for (JButton b : new JButton[]{addBtn, updateBtn, deleteBtn}) {
            b.setBackground(b.isEnabled() ? (b == deleteBtn ? Theme.CLAY : Theme.MOSS) : new Color(0xB9C4B3));
        }
    }

    private void status(String msg) { statusLabel.setText(msg); }

    private void showError(ClientException ex) {
        int type = (ex instanceof ValidationException) ? JOptionPane.WARNING_MESSAGE : JOptionPane.ERROR_MESSAGE;
        String title = (ex instanceof ValidationException) ? "Please fix the following"
                : ex.getClass().getSimpleName().replace("Exception", "");
        String msg = (ex instanceof ValidationException) ? " - " + ex.getMessage() : ex.getMessage();
        JOptionPane.showMessageDialog(this, msg, title, type);
    }
}
