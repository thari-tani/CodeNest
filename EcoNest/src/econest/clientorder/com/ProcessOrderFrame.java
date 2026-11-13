import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class ProcessOrderFrame extends JFrame {

    private JComboBox<String> clientCombo;

    private JComboBox<String> productCombo;

    private JTextField quantityField;

    private JTable orderTable;

    private DefaultTableModel tableModel;

    private JLabel totalLabel;

    private List<OrderItem> orderItems =
            new ArrayList<>();

    private OrderDAO orderDAO = new OrderDAO();

    public ProcessOrderFrame() {

        setTitle("EcoNest - Process Client Orders");

        setSize(900, 600);

        setLocationRelativeTo(null);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        setLayout(new BorderLayout(10, 10));

        createInterface();

        loadClients();

        loadProducts();

    }

    private void createInterface() {

        JPanel mainPanel = new JPanel(
                new BorderLayout(10, 10)
        );

        mainPanel.setBorder(
                BorderFactory.createEmptyBorder(
                        20, 20, 20, 20
                )
        );

        JLabel heading = new JLabel(
                "PROCESS CLIENT ORDERS",
                JLabel.CENTER
        );

        heading.setFont(
                new Font("Arial", Font.BOLD, 24)
        );

        mainPanel.add(
                heading,
                BorderLayout.NORTH
        );

        // Client selection

        JPanel selectionPanel = new JPanel(
                new GridLayout(3, 2, 10, 10)
        );

        selectionPanel.setBorder(
                BorderFactory.createTitledBorder(
                        "Order Information"
                )
        );

        clientCombo = new JComboBox<>();

        productCombo = new JComboBox<>();

        quantityField = new JTextField();

        selectionPanel.add(
                new JLabel("Select Client:")
        );

        selectionPanel.add(clientCombo);

        selectionPanel.add(
                new JLabel("Select Product:")
        );

        selectionPanel.add(productCombo);

        selectionPanel.add(
                new JLabel("Quantity:")
        );

        selectionPanel.add(quantityField);

        // Buttons

        JButton addButton =
                new JButton("Add Product");

        JButton removeButton =
                new JButton("Remove Product");

        JButton clearButton =
                new JButton("Clear Order");

        JButton placeButton =
                new JButton("Place Order");

        JPanel buttonPanel = new JPanel();

        buttonPanel.add(addButton);

        buttonPanel.add(removeButton);

        buttonPanel.add(clearButton);

        // Order table

        String[] columns = {
            "Product ID",
            "Product Name",
            "Quantity",
            "Unit Price",
            "Subtotal"
        };

        tableModel = new DefaultTableModel(
                columns, 0
        ) {

            @Override
            public boolean isCellEditable(
                    int row, int column) {

                return false;

            }

        };

        orderTable = new JTable(tableModel);

        JScrollPane scrollPane =
                new JScrollPane(orderTable);

        // Total

        totalLabel = new JLabel(
                "Total: Rs. 0.00"
        );

        totalLabel.setFont(
                new Font("Arial", Font.BOLD, 20)
        );

        JPanel bottomPanel = new JPanel(
                new BorderLayout()
        );

        bottomPanel.add(
                totalLabel,
                BorderLayout.WEST
        );

        bottomPanel.add(
                placeButton,
                BorderLayout.EAST
        );

        // Layout

        JPanel centerPanel = new JPanel(
                new BorderLayout(10, 10)
        );

        centerPanel.add(
                selectionPanel,
                BorderLayout.NORTH
        );

        centerPanel.add(
                scrollPane,
                BorderLayout.CENTER
        );

        centerPanel.add(
                buttonPanel,
                BorderLayout.SOUTH
        );

        mainPanel.add(
                centerPanel,
                BorderLayout.CENTER
        );

        mainPanel.add(
                bottomPanel,
                BorderLayout.SOUTH
        );

        add(mainPanel);

        // Button actions

        addButton.addActionListener(
                e -> addProduct()
        );

        removeButton.addActionListener(
                e -> removeProduct()
        );

        clearButton.addActionListener(
                e -> clearOrder()
        );

        placeButton.addActionListener(
                e -> placeOrder()
        );

    }

    // Load clients from database

    private void loadClients() {

        String sql =
                "SELECT client_id, client_name "
                + "FROM clients";

        try (Connection con =
                     DBConnection.getConnection();

             PreparedStatement ps =
                     con.prepareStatement(sql);

             ResultSet rs = ps.executeQuery()) {

            clientCombo.removeAllItems();

            while (rs.next()) {

                clientCombo.addItem(
                        rs.getInt("client_id")
                        + " - "
                        + rs.getString("client_name")
                );

            }

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Unable to load clients: "
                    + e.getMessage()
            );

        }

    }

    // Load products from database

    private void loadProducts() {

        String sql =
                "SELECT product_id, product_name, "
                + "price, quantity "
                + "FROM products";

        try (Connection con =
                     DBConnection.getConnection();

             PreparedStatement ps =
                     con.prepareStatement(sql);

             ResultSet rs = ps.executeQuery()) {

            productCombo.removeAllItems();

            while (rs.next()) {

                productCombo.addItem(
                        rs.getInt("product_id")
                        + " - "
                        + rs.getString("product_name")
                        + " - Rs. "
                        + rs.getDouble("price")
                        + " - Stock: "
                        + rs.getInt("quantity")
                );

            }

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Unable to load products: "
                    + e.getMessage()
            );

        }

    }

    // Add product to order

    private void addProduct() {

        try {

            if (productCombo.getSelectedItem() == null) {

                JOptionPane.showMessageDialog(
                        this,
                        "Please select a product."
                );

                return;

            }

            int quantity = Integer.parseInt(
                    quantityField.getText().trim()
            );

            if (quantity <= 0) {

                JOptionPane.showMessageDialog(
                        this,
                        "Quantity must be greater than zero."
                );

                return;

            }

            String selected =
                    productCombo.getSelectedItem().toString();

            String[] parts = selected.split(" - ");

            int productId =
                    Integer.parseInt(parts[0]);

            String productName = parts[1];

            double price =
                    Double.parseDouble(
                            parts[3].replace("Rs. ", "")
                    );

            int availableStock =
                    Integer.parseInt(
                            parts[4].replace("Stock: ", "")
                    );

            int alreadyAdded = 0;

            for (OrderItem item : orderItems) {

                if (item.getProductId() == productId) {

                    alreadyAdded += item.getQuantity();

                }

            }

            if (quantity + alreadyAdded > availableStock) {

                JOptionPane.showMessageDialog(
                        this,
                        "Insufficient stock!"
                );

                return;

            }

            OrderItem item = new OrderItem(
                    productId,
                    productName,
                    quantity,
                    price
            );

            orderItems.add(item);

            tableModel.addRow(
                    new Object[]{
                        productId,
                        productName,
                        quantity,
                        price,
                        item.getSubtotal()
                    }
            );

            calculateTotal();

            quantityField.setText("");

        } catch (NumberFormatException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please enter a valid quantity."
            );

        }

    }

    // Remove selected product

    private void removeProduct() {

        int selectedRow =
                orderTable.getSelectedRow();

        if (selectedRow == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select a product to remove."
            );

            return;

        }

        orderItems.remove(selectedRow);

        tableModel.removeRow(selectedRow);

        calculateTotal();

    }

    // Calculate total

    private void calculateTotal() {

        double total = 0;

        for (OrderItem item : orderItems) {

            total += item.getSubtotal();

        }

        totalLabel.setText(
                String.format(
                        "Total: Rs. %.2f",
                        total
                )
        );

    }

    // Clear order

    private void clearOrder() {

        orderItems.clear();

        tableModel.setRowCount(0);

        totalLabel.setText(
                "Total: Rs. 0.00"
        );

    }

    // Place order

    private void placeOrder() {

        if (clientCombo.getSelectedItem() == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select a client."
            );

            return;

        }

        if (orderItems.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please add at least one product."
            );

            return;

        }

        try {

            String selected =
                    clientCombo.getSelectedItem().toString();

            int clientId = Integer.parseInt(
                    selected.split(" - ")[0]
            );

            int orderId = orderDAO.saveOrder(
                    clientId,
                    orderItems
            );

            JOptionPane.showMessageDialog(
                    this,
                    "Order placed successfully!\n"
                    + "Order ID: "
                    + orderId
            );

            clearOrder();

            loadProducts();

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Unable to place order:\n"
                    + e.getMessage()
            );

        }

    }

}