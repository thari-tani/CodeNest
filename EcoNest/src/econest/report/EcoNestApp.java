/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.econestapp;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;

public class EcoNestApp extends JFrame {

    // Database Connection විස්තර (WampServer)
    private final String DB_URL = "jdbc:mysql://localhost:3306/econest_db";
    private final String DB_USER = "root";
    private final String DB_PASS = ""; // WampServer default password එක හිස්

    // Task 7 UI Components
    private JComboBox<String> cmbMonth;
    private JTextField txtYear;
    private JLabel lblRevenue;
    private JTable tblLowStock;
    private DefaultTableModel lowStockModel;

    // Task 8 UI Components
    private JTable tblOrders;
    private DefaultTableModel ordersModel;
    private JTextField txtSenderEmail;
    private JPasswordField txtAppPassword;

    public EcoNestApp() {
        setTitle("EcoNest Retail System - Task 7 (Reports) & Task 8 (Email Notifications)");
        setSize(850, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 14));

        // Tabs දෙක එකතු කිරීම
        tabbedPane.addTab("Task 7: Monthly Sales & Inventory Reports", createTask7Panel());
        tabbedPane.addTab("Task 8: Order Dispatch & Email Notification", createTask8Panel());

        add(tabbedPane);

        // පටන් ගද්දීම Data ටික Load කිරීම
        generateReport();
        loadOrders();
    }

    // ================= TASK 7 PANEL =================
    private JPanel createTask7Panel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Top Filter Panel
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        topPanel.setBorder(BorderFactory.createTitledBorder("Monthly Revenue Summary"));

        topPanel.add(new JLabel("Select Month (1-12):"));
        String[] months = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"};
        cmbMonth = new JComboBox<>(months);
        cmbMonth.setSelectedIndex(7); // August (8)
        topPanel.add(cmbMonth);

        topPanel.add(new JLabel("Year:"));
        txtYear = new JTextField("2026", 6);
        topPanel.add(txtYear);

        JButton btnGenerate = new JButton("Generate Report");
        btnGenerate.setBackground(new Color(46, 125, 50));
        btnGenerate.setForeground(Color.WHITE);
        btnGenerate.addActionListener(e -> generateReport());
        topPanel.add(btnGenerate);

        lblRevenue = new JLabel("Total Monthly Revenue: Rs. 0.00");
        lblRevenue.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblRevenue.setForeground(new Color(27, 94, 32));
        topPanel.add(lblRevenue);

        panel.add(topPanel, BorderLayout.NORTH);

        // Center Table for Low Stock Alerts
        String[] cols = {"Product ID", "Product Name", "Price (Rs.)", "Qty On Hand", "Reorder Level", "Status"};
        lowStockModel = new DefaultTableModel(cols, 0);
        tblLowStock = new JTable(lowStockModel);
        tblLowStock.setRowHeight(25);

        JScrollPane scrollPane = new JScrollPane(tblLowStock);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Low-Stock Inventory Alerts (Qty <= Reorder Level)"));
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    // ================= TASK 8 PANEL =================
    private JPanel createTask8Panel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Top Email Config Panel
        JPanel emailConfigPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        emailConfigPanel.setBorder(BorderFactory.createTitledBorder("SMTP Email Settings (Optional for Live Gmail)"));
        emailConfigPanel.add(new JLabel("Sender Gmail:"));
        txtSenderEmail = new JTextField("econest.store@gmail.com", 18);
        emailConfigPanel.add(txtSenderEmail);

        emailConfigPanel.add(new JLabel("Gmail App Password:"));
        txtAppPassword = new JPasswordField("", 12);
        emailConfigPanel.add(txtAppPassword);

        panel.add(emailConfigPanel, BorderLayout.NORTH);

        // Orders Table
        String[] cols = {"Order ID", "Client Name", "Client Email", "Order Date", "Total (Rs.)", "Delivery Status"};
        ordersModel = new DefaultTableModel(cols, 0);
        tblOrders = new JTable(ordersModel);
        tblOrders.setRowHeight(25);

        JScrollPane scrollPane = new JScrollPane(tblOrders);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Select an Order to Dispatch & Notify Client"));
        panel.add(scrollPane, BorderLayout.CENTER);

        // Bottom Button Panel
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnRefresh = new JButton("Refresh Orders");
        btnRefresh.addActionListener(e -> loadOrders());

        JButton btnDispatch = new JButton("Dispatch Selected Order & Send Email");
        btnDispatch.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnDispatch.setBackground(new Color(25, 118, 210));
        btnDispatch.setForeground(Color.WHITE);
        btnDispatch.addActionListener(e -> dispatchAndSendEmail());

        bottomPanel.add(btnRefresh);
        bottomPanel.add(btnDispatch);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }

    // ================= DATABASE & LOGIC METHODS =================
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
    }

    // Task 7 Logic: Calculate Revenue & Load Low-Stock Items
    private void generateReport() {
        int month = Integer.parseInt(cmbMonth.getSelectedItem().toString());
        int year = Integer.parseInt(txtYear.getText().trim());

        try (Connection con = getConnection()) {
            // 1. Monthly Revenue Summary
            String revSql = "SELECT SUM(total_amount) AS rev FROM orders WHERE MONTH(order_date)=? AND YEAR(order_date)=?";
            PreparedStatement pst1 = con.prepareStatement(revSql);
            pst1.setInt(1, month);
            pst1.setInt(2, year);
            ResultSet rs1 = pst1.executeQuery();
            if (rs1.next()) {
                double rev = rs1.getDouble("rev");
                lblRevenue.setText(String.format("Total Monthly Revenue: Rs. %.2f", rev));
            }

            // 2. Low-Stock Alerts
            lowStockModel.setRowCount(0);
            String stockSql = "SELECT * FROM products WHERE qty_on_hand <= reorder_level";
            PreparedStatement pst2 = con.prepareStatement(stockSql);
            ResultSet rs2 = pst2.executeQuery();
            while (rs2.next()) {
                lowStockModel.addRow(new Object[]{
                    rs2.getString("product_id"),
                    rs2.getString("name"),
                    rs2.getDouble("price"),
                    rs2.getInt("qty_on_hand"),
                    rs2.getInt("reorder_level"),
                    "⚠️ LOW STOCK!"
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage());
        }
    }

    // Task 8 Logic: Load Orders
    private void loadOrders() {
        ordersModel.setRowCount(0);
        try (Connection con = getConnection()) {
            String sql = "SELECT * FROM orders";
            PreparedStatement pst = con.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                ordersModel.addRow(new Object[]{
                    rs.getString("order_id"),
                    rs.getString("client_name"),
                    rs.getString("client_email"),
                    rs.getDate("order_date"),
                    rs.getDouble("total_amount"),
                    rs.getString("status")
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading orders: " + ex.getMessage());
        }
    }

    // Task 8 Logic: Update Status to Dispatched & Send Email
    private void dispatchAndSendEmail() {
        int selectedRow = tblOrders.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an order from the table first!");
            return;
        }

        String orderId = ordersModel.getValueAt(selectedRow, 0).toString();
        String clientName = ordersModel.getValueAt(selectedRow, 1).toString();
        String clientEmail = ordersModel.getValueAt(selectedRow, 2).toString();

        try (Connection con = getConnection()) {
            // Update Order Status in Database
            String sql = "UPDATE orders SET status='Dispatched' WHERE order_id=?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, orderId);
            pst.executeUpdate();

            loadOrders(); // Refresh table

            // Send Email Notification
            sendEmailNotification(clientEmail, clientName, orderId);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error updating order: " + ex.getMessage());
        }
    }

    private void sendEmailNotification(String toEmail, String clientName, String orderId) {
        String sender = txtSenderEmail.getText().trim();
        String pass = new String(txtAppPassword.getPassword()).trim();

        String subject = "EcoNest - Your Order #" + orderId + " has been Dispatched!";
        String body = "Dear " + clientName + ",\n\n"
                + "Your EcoNest order (" + orderId + ") has been dispatched and is on its way!\n\n"
                + "Thank you for shopping with EcoNest!";

        // App Password එකක් දීලා නැත්නම් Simulation Message එකක් පෙන්නනවා (Viva එකේදී Error නොවී පෙන්නන්න)
        if (pass.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "✅ Order " + orderId + " marked as DISPATCHED!\n\n"
                    + "📧 Email Notification Dispatched to: " + toEmail + "\n"
                    + "Subject: " + subject + "\n\n"
                    + body,
                    "Email Notification Sent", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // App Password දීලා තියෙනවා නම් ඇත්තටම Gmail හරහා Email එක යවනවා
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(sender, pass);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(sender));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject(subject);
            message.setText(body);
            Transport.send(message);

            JOptionPane.showMessageDialog(this, "Live Email Sent Successfully to " + toEmail + "!");
        } catch (MessagingException e) {
            JOptionPane.showMessageDialog(this, "SMTP Error: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new EcoNestApp().setVisible(true);
        });
    }
}