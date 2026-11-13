package econest.client.ui;

import econest.client.model.*;

import javax.swing.*;
import java.awt.*;

// the add / edit form on the right side
public class ClientForm extends JPanel {

    private static final String HOUSEHOLD = "HOUSEHOLD";
    private static final String OFFICE = "OFFICE";

    private final JComboBox<ClientType> typeBox = new JComboBox<>(ClientType.values());
    private final JTextField nameField = Theme.textField();
    private final JTextField emailField = Theme.textField();
    private final JTextField phoneField = Theme.textField();
    private final JTextArea addressArea = new JTextArea(3, 20);
    private final JSpinner sizeSpinner = new JSpinner(new SpinnerNumberModel(3, 1, 30, 1));
    private final JTextField companyField = Theme.textField();
    private final JTextField contactField = Theme.textField();
    private final JTextField brnField = Theme.textField();

    private final CardLayout cards = new CardLayout();
    private final JPanel typeCards = new JPanel(cards);

    public ClientForm() {
        setLayout(new GridBagLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

        typeBox.setFont(Theme.BASE);
        typeBox.setBackground(Color.WHITE);
        addressArea.setFont(Theme.BASE);
        addressArea.setLineWrap(true);
        addressArea.setWrapStyleWord(true);
        addressArea.setBorder(Theme.fieldBorder());
        ((JSpinner.DefaultEditor) sizeSpinner.getEditor()).getTextField().setFont(Theme.BASE);

        typeCards.setOpaque(false);
        typeCards.add(householdCard(), HOUSEHOLD);
        typeCards.add(officeCard(), OFFICE);
        typeBox.addActionListener(e -> showCard());

        int row = 0;
        row = addRow(row, "Client type", typeBox);
        row = addRow(row, "Full / business name", nameField);
        row = addRow(row, "Email", emailField);
        row = addRow(row, "Phone", phoneField);
        row = addRow(row, "Address", new JScrollPane(addressArea) {{ setBorder(null); }});

        GridBagConstraints gc = new GridBagConstraints();
        gc.gridx = 0; gc.gridy = row; gc.fill = GridBagConstraints.HORIZONTAL; gc.weightx = 1;
        gc.insets = new Insets(8, 0, 0, 0);
        add(typeCards, gc);
        showCard();
    }

    private JPanel householdCard() {
        JPanel p = stackedPanel();
        p.add(Theme.label("Household size (people)"));
        p.add(Box.createVerticalStrut(4));
        sizeSpinner.setMaximumSize(new Dimension(120, 30));
        sizeSpinner.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(sizeSpinner);
        return p;
    }

    private JPanel officeCard() {
        JPanel p = stackedPanel();
        addStacked(p, "Company name", companyField);
        addStacked(p, "Contact person", contactField);
        addStacked(p, "Business registration no.", brnField);
        return p;
    }

    private JPanel stackedPanel() {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        return p;
    }

    private void addStacked(JPanel p, String label, JComponent field) {
        JLabel l = Theme.label(label);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, field.getPreferredSize().height));
        p.add(l);
        p.add(Box.createVerticalStrut(4));
        p.add(field);
        p.add(Box.createVerticalStrut(10));
    }

    private int addRow(int row, String label, JComponent field) {
        GridBagConstraints gc = new GridBagConstraints();
        gc.gridx = 0; gc.weightx = 1; gc.fill = GridBagConstraints.HORIZONTAL;
        gc.anchor = GridBagConstraints.WEST;
        gc.gridy = row;
        gc.insets = new Insets(row == 0 ? 0 : 8, 0, 0, 0);
        add(Theme.label(label), gc);
        gc.gridy = row + 1;
        gc.insets = new Insets(4, 0, 0, 0);
        add(field, gc);
        return row + 2;
    }

    private void showCard() {
        cards.show(typeCards, typeBox.getSelectedItem() == ClientType.OFFICE ? OFFICE : HOUSEHOLD);
    }

    // reads the fields and makes a HouseholdClient or OfficeClient
    public Client buildClient() {
        String name = nameField.getText(), email = emailField.getText();
        String phone = phoneField.getText(), address = addressArea.getText();
        if (typeBox.getSelectedItem() == ClientType.OFFICE) {
            return new OfficeClient(name, email, phone, address,
                    companyField.getText(), contactField.getText(), brnField.getText());
        }
        return new HouseholdClient(name, email, phone, address, (Integer) sizeSpinner.getValue());
    }

    public void show(Client c) {
        typeBox.setSelectedItem(c.getClientType());
        nameField.setText(c.getName());
        emailField.setText(c.getEmail());
        phoneField.setText(c.getPhone());
        addressArea.setText(c.getAddress());
        if (c instanceof HouseholdClient) {
            sizeSpinner.setValue(((HouseholdClient) c).getHouseholdSize());
        } else if (c instanceof OfficeClient) {
            OfficeClient o = (OfficeClient) c;
            companyField.setText(o.getCompanyName());
            contactField.setText(o.getContactPerson());
            brnField.setText(o.getBusinessRegNo());
        }
        typeBox.setEnabled(false);          // a saved client keeps its type
    }

    public void clear() {
        nameField.setText(""); emailField.setText(""); phoneField.setText(""); addressArea.setText("");
        sizeSpinner.setValue(3);
        companyField.setText(""); contactField.setText(""); brnField.setText("");
        typeBox.setSelectedItem(ClientType.HOUSEHOLD);   // back to the default type
        typeBox.setEnabled(true);
        nameField.requestFocusInWindow();
    }
}
