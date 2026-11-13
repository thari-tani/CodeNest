package econest.product.main;
import db.*;
import java.awt.Color;
import model.*;
import java.sql.*;
import java.util.*;
import javax.swing.*;
import javax.swing.text.*;

/*
 * @author P. U. Liyanage
 * @sanumber SA26610056
 * @group EcoNest
 */
public class ProductApp extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(ProductApp.class.getName());
    
    //Databse Access Manage
    private DBManager db;
    
    //Lists of products currently in the actual table
    private ArrayList<Product> products;
    //Products to add to the table in the database
    private ArrayList<Product> newProducts;
    //Products changed in the table in the database
    private ArrayList<Product> changedProducts;
    //Products to delete from the table in the database
    private ArrayList<Product> deletedProducts;
    //Result of MySQL queries performed
    private ResultSet result;
    //Flag to see whether the changes made have been saved to the database. By default true if no changes have been made yet.
    private boolean isSaved = true;
    //Flag to see if the user is notified changes made have not been saved yet
    private boolean noticeAdded = false;
    //Max character limit of descriptionTextArea, enforced by a DocumentListener
    private final int DESC_MAX_CHARS = 120;
    /**
     * Creates new form ProductApp
     */
    public ProductApp() {
        //Initialize the app's components
        initComponents();
        
        //Set window title
        this.setTitle("EcoNest | Product");
        
        //Make sure the productTables fill the JScrollPane it resides in
        productTable.setFillsViewportHeight(true);
        
        //Create a DBManager using the given parameters
        db = new DBManager(
                System.getenv().getOrDefault("ECONEST_DB_URL","jdbc:mysql://localhost:3306/econest_db?useSSL=false&serverTimezone=UTC&allowMultiQueries=true"),
                System.getenv().getOrDefault("ECONEST_DB_USER", "root"),
                System.getenv().getOrDefault("ECONEST_DB_PASS", "$my5qlP455W0RD!")
        );
        
        //Intialise the product lists
        products = new ArrayList<Product>(4);
        newProducts = new ArrayList<Product>(4);
        changedProducts = new ArrayList<Product>(4);
        deletedProducts = new ArrayList<Product>(4);
        //Connect to the database
        db.connect();
        //Load the table with data from the database
        loadTable();
        
        //Get the Document Object of the descriptionTextArea
        AbstractDocument doc = (AbstractDocument) descriptionTextArea.getDocument();
       //Set a DocumentFilter for the descriptionTextArea's Document
        doc.setDocumentFilter(new DocumentFilter() {
           //Override the insertString function of the DocumentFilter
            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) 
                    throws BadLocationException {
                //Do nothing in the function if the descriptionTextArea is empty
                if (string == null) return;
                /*
                If the length of the text already in the descriptionTextArea
                plus the length of the newly added text is less or equal to
                DESC_MAX_CHAR...
               */
                if ((fb.getDocument().getLength() + string.length()) <= DESC_MAX_CHARS) {
                    /*
                    Calls the FilterBypass' version of insertString internally,
                    putting the pasted text in the descriptionTextArea
                    */
                    super.insertString(fb, offset, string, attr);
                } else {
                    //Truncate the pasted string to fit the remaining limit
                    int remaining = DESC_MAX_CHARS - fb.getDocument().getLength();
                    if (remaining > 0) {
                        super.insertString(fb, offset, string.substring(0, remaining), attr);
                    }
                }
            }

            //Override the replace function of the DocumentFilter
            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) 
                    throws BadLocationException {
                //Do nothing in the function if the descriptionTextArea is empty
                if (text == null) return;
                //Get the current length of the text in the descriptionTextArea
                int currentLength = fb.getDocument().getLength();
                /*
                Calculate the new length of text after characters from
                the descriptionTextArea are deleted and replaced with the new text
                */
                int nextLength = currentLength - length + text.length();
                
                //If the new length is less than DESC_MAX_CHARS (120)
                if (nextLength <= DESC_MAX_CHARS) {
                    //
                    super.replace(fb, offset, length, text, attrs);
                } else {
                    int remaining = DESC_MAX_CHARS - (currentLength - length);
                    if (remaining > 0) {
                        super.replace(fb, offset, length, text.substring(0, remaining), attrs);
                    }
                }
            }
        });
        
        //Add a DocumentListener to outputTextArea
        outputTextArea.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            
            //Performs actions when a mesage is printed in the outputTextArea
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                SwingUtilities.invokeLater(() -> {
                    /*
                    If the changes  that were made were not saved
                    and the user has not been notified yet...
                    */
                    if(!isSaved && !noticeAdded){
                        //Add a message saying that changes are unsaved
                        outputTextArea.setText(outputTextArea.getText().concat("\n(Changes are unsaved)"));
                        //Set noticeAdded flag to true
                        noticeAdded = true;
                       
                    }
                });
            } 
            
            //Does nothing if text is removed
            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
               
            }

            //Does nothing if text is changed
            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
            }
            
            

        });
        
        productTable.getSelectionModel().addListSelectionListener(e -> {
        //Ensure the event is stable and not midway through a change
            if (!e.getValueIsAdjusting()) {
                //Gets the row currently selected by the user
                int selectedRow = productTable.getSelectedRow();
                //If a row is actually selected
                if (selectedRow != -1) {
                    
                    //Pull the data in that row
                    String id = productTable.getValueAt(selectedRow, 0).toString();
                    String name = productTable.getValueAt(selectedRow, 1).toString();
                    String desc = productTable.getValueAt(selectedRow, 2).toString();
                    String price = productTable.getValueAt(selectedRow, 3).toString().substring(1);
                    Double rating = Double.parseDouble(productTable.getValueAt(selectedRow, 4).toString());
                    
                    //Put those values in the typeable fields
                    productCodeTextField.setText(id);
                    nameTextField.setText(name);
                    descriptionTextArea.setText(desc);
                    priceTextField.setText(price);
                    ecoRatingTextField.setText(rating.toString());
                    ecoRatingSlider.setValue((int)(rating*10));
                }
            }
        });

        
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel3 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        productTable = new javax.swing.JTable() {
            @Override
            public java.awt.Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int row, int column) {
                java.awt.Component comp = super.prepareRenderer(renderer, row, column);

                // Keep the default highlight color when a user selects a row
                if (!comp.getBackground().equals(getSelectionBackground())) {
                    java.awt.Color firstColor = new java.awt.Color(187, 232, 195);
                    java.awt.Color secondColor = new java.awt.Color(169, 217, 165);
                    comp.setBackground(row % 2 == 0 ? firstColor : secondColor);
                }
                return comp;
            }
        };
        productTable.getTableHeader().setBackground(new java.awt.Color(192, 235, 190));
        productTable.getTableHeader().setForeground(java.awt.Color.BLACK);
        productTable.getTableHeader().setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 12));
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        nameTextField = new javax.swing.JTextField();
        productCodeTextField = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        ecoRatingSlider = new javax.swing.JSlider();
        jLabel4 = new javax.swing.JLabel();
        ecoRatingValueLabel = new javax.swing.JLabel();
        ecoRatingTextField = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        descriptionTextArea = new javax.swing.JTextArea();
        jLabel6 = new javax.swing.JLabel();
        priceTextField = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jLayeredPane1 = new javax.swing.JLayeredPane();
        exitButton = new javax.swing.JButton();
        deleteButton = new javax.swing.JButton();
        addButton = new javax.swing.JButton();
        updateButton = new javax.swing.JButton();
        saveButton = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        outputTextArea = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setBackground(new java.awt.Color(204, 255, 204));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
        });

        jPanel3.setBackground(new java.awt.Color(181, 215, 182));

        productTable.setAutoCreateRowSorter(true);
        productTable.setBackground(new java.awt.Color(187, 232, 195));
        productTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ProductCode", "Name", "Description", "Price", "Eco Rating"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Double.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        productTable.setFocusable(false);
        productTable.setGridColor(new java.awt.Color(204, 204, 204));
        productTable.setMaximumSize(new java.awt.Dimension(((int)0.75*this.getMaximumSize().width), Short.MAX_VALUE));
        productTable.setMinimumSize(new java.awt.Dimension(((int)0.75*this.getMinimumSize().width), 350));
        productTable.setPreferredSize(new java.awt.Dimension(((int)0.75*(this.getPreferredSize().width)), 350));
        productTable.setSelectionBackground(new java.awt.Color(0, 153, 153));
        productTable.setSelectionForeground(new java.awt.Color(255, 255, 255));
        productTable.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(productTable);
        if (productTable.getColumnModel().getColumnCount() > 0) {
            productTable.getColumnModel().getColumn(0).setResizable(false);
            productTable.getColumnModel().getColumn(0).setPreferredWidth(32);
            productTable.getColumnModel().getColumn(1).setResizable(false);
            productTable.getColumnModel().getColumn(1).setPreferredWidth(100);
            productTable.getColumnModel().getColumn(2).setResizable(false);
            productTable.getColumnModel().getColumn(2).setPreferredWidth(250);
            productTable.getColumnModel().getColumn(3).setResizable(false);
            productTable.getColumnModel().getColumn(4).setResizable(false);
            productTable.getColumnModel().getColumn(4).setPreferredWidth(20);
        }

        jPanel2.setBackground(new java.awt.Color(187, 232, 195));

        jLabel2.setText("Name");

        productCodeTextField.setToolTipText("ECO-XXXXXX");

        jLabel3.setText("Product Code");

        ecoRatingSlider.setMajorTickSpacing(10);
        ecoRatingSlider.setMaximum(50);
        ecoRatingSlider.setMinimum(10);
        ecoRatingSlider.setPaintTicks(true);
        ecoRatingSlider.setValue(30);
        ecoRatingSlider.addChangeListener(this::ecoRatingSliderStateChanged);

        jLabel4.setText("Eco Rating");

        ecoRatingValueLabel.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        ecoRatingValueLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        ecoRatingValueLabel.setText(Double.toString(ecoRatingSlider.getValue()*0.1));

        ecoRatingTextField.setText(Double.toString(ecoRatingSlider.getValue()*0.1));
        ecoRatingTextField.setToolTipText("Value between 1.0 and 5.0");
        ecoRatingTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                ecoRatingTextFieldKeyPressed(evt);
            }
        });

        jLabel5.setText("Description");

        descriptionTextArea.setColumns(20);
        descriptionTextArea.setLineWrap(true);
        descriptionTextArea.setRows(5);
        descriptionTextArea.setToolTipText("Short description of the product");
        jScrollPane2.setViewportView(descriptionTextArea);

        jLabel6.setText("Price");

        priceTextField.setToolTipText("Price in dollars (e.g. 8.99)");
        priceTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                priceTextFieldKeyPressed(evt);
            }
        });

        jLabel7.setText("$");

        exitButton.setBackground(new java.awt.Color(255, 102, 102));
        exitButton.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        exitButton.setForeground(new java.awt.Color(255, 255, 255));
        exitButton.setText("Exit");
        exitButton.addActionListener(this::exitButtonActionPerformed);

        deleteButton.setBackground(new java.awt.Color(255, 102, 102));
        deleteButton.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        deleteButton.setForeground(new java.awt.Color(255, 255, 255));
        deleteButton.setText("Delete");
        deleteButton.setToolTipText("Delete an item");
        deleteButton.addActionListener(this::deleteButtonActionPerformed);

        addButton.setBackground(new java.awt.Color(153, 255, 153));
        addButton.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        addButton.setText("Add");
        addButton.setToolTipText("Add a new product to the table");
        addButton.addActionListener(this::addButtonActionPerformed);

        updateButton.setBackground(new java.awt.Color(153, 255, 153));
        updateButton.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        updateButton.setText("Update");
        updateButton.setToolTipText("Update a selected product");
        updateButton.addActionListener(this::updateButtonActionPerformed);

        saveButton.setBackground(new java.awt.Color(153, 255, 153));
        saveButton.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        saveButton.setText("Save");
        saveButton.setToolTipText("Save all changes made");
        saveButton.addActionListener(this::saveButtonActionPerformed);

        jLayeredPane1.setLayer(exitButton, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane1.setLayer(deleteButton, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane1.setLayer(addButton, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane1.setLayer(updateButton, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane1.setLayer(saveButton, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout jLayeredPane1Layout = new javax.swing.GroupLayout(jLayeredPane1);
        jLayeredPane1.setLayout(jLayeredPane1Layout);
        jLayeredPane1Layout.setHorizontalGroup(
            jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jLayeredPane1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(deleteButton, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(addButton, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 14, Short.MAX_VALUE)
                .addGroup(jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(saveButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(updateButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(exitButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        jLayeredPane1Layout.setVerticalGroup(
            jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jLayeredPane1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(addButton)
                    .addComponent(saveButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(updateButton)
                    .addComponent(deleteButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(exitButton)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(ecoRatingSlider, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                .addComponent(ecoRatingTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ecoRatingValueLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 86, Short.MAX_VALUE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel7)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(productCodeTextField, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(nameTextField)
                            .addComponent(jScrollPane2)
                            .addComponent(priceTextField, javax.swing.GroupLayout.Alignment.TRAILING)))
                    .addComponent(jLayeredPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel2Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(239, Short.MAX_VALUE)))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(productCodeTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(nameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(priceTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, 27, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ecoRatingSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(ecoRatingTextField)
                    .addComponent(ecoRatingValueLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(31, 31, 31)
                .addComponent(jLayeredPane1)
                .addGap(26, 26, 26))
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel2Layout.createSequentialGroup()
                    .addGap(19, 19, 19)
                    .addComponent(jLabel3)
                    .addContainerGap(446, Short.MAX_VALUE)))
        );

        jPanel1.setBackground(new java.awt.Color(187, 232, 195));
        jPanel1.setMaximumSize(new java.awt.Dimension(this.getMaximumSize().width, 100));

        jLabel1.setFont(new java.awt.Font("Segoe UI Black", 0, 48)); // NOI18N
        jLabel1.setText("EcoNest | Products");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 550, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addComponent(jLabel1)
                .addContainerGap(24, Short.MAX_VALUE))
        );

        outputTextArea.setEditable(false);
        outputTextArea.setColumns(20);
        outputTextArea.setRows(4);
        outputTextArea.setToolTipText("Output area for results of operations done");
        jScrollPane3.setViewportView(outputTextArea);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 906, Short.MAX_VALUE)
                            .addComponent(jScrollPane3))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 416, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane3)))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void ecoRatingSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_ecoRatingSliderStateChanged
        //Set the values in ecoRatingTexField and ecoRatingValueLabel to match the value on the ecoRatingSlider
        ecoRatingTextField.setText(Double.toString( Math.round(ecoRatingSlider.getValue() * 10.0) / 100.0));
        ecoRatingValueLabel.setText(Double.toString(Math.round(ecoRatingSlider.getValue() * 10.0) / 100.0));
    }//GEN-LAST:event_ecoRatingSliderStateChanged

    private void ecoRatingTextFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_ecoRatingTextFieldKeyPressed
        
        
        try{
            //If the user enters the value and the field is not empty...
            if(evt.getKeyCode() ==  java.awt.event.KeyEvent.VK_ENTER && !ecoRatingTextField.getText().isEmpty()){
                //Set the slider value to the calculated value in the text field
                ecoRatingSlider.setValue((int)(Double.parseDouble(ecoRatingTextField.getText()) * 10.0));
        }} catch (NumberFormatException e){
            //Print an error message
            outputTextArea.setForeground(Color.red);
            outputTextArea.setText("Invalid Input!\n\n" + e.toString());
            ecoRatingTextField.setText(Double.toString( Math.round(ecoRatingSlider.getValue() * 10.0) / 100.0));
        }
    }//GEN-LAST:event_ecoRatingTextFieldKeyPressed

    private void priceTextFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_priceTextFieldKeyPressed
        try{
            //If the user enters the value and the field is not empty...
            if(evt.getKeyCode() ==  java.awt.event.KeyEvent.VK_ENTER && !priceTextField.getText().isEmpty()){
                //Try and check if the value is a Double by trying to perform parseDouble 
                Double.parseDouble(priceTextField.getText());
        }} catch (NumberFormatException e){
            //Print an error message if it is not a double
            outputTextArea.setForeground(Color.red);
            outputTextArea.setText("Invalid Input!\n\n" + e.toString());
            //Empty the field
            priceTextField.setText("");
        }
    }//GEN-LAST:event_priceTextFieldKeyPressed

    private void exitButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitButtonActionPerformed
        //Dispose the ProductApp Frame
        this.dispose();
    }//GEN-LAST:event_exitButtonActionPerformed

    private void updateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateButtonActionPerformed
       
       //Flag to signify if the product is in the table 
       boolean isInTable = false;
       //Flag to signify if the product's fields have been changed at all
       boolean isUnchanged = false;
       
       //Get the values from the input fields
       String id = productCodeTextField.getText();
       String name = nameTextField.getText();
       String desc = descriptionTextArea.getText();
       String price = "$".concat(priceTextField.getText());
       Double rating = Double.parseDouble(ecoRatingTextField.getText()); 
       
       //If none of the fields are empty...
       if (!(productCodeTextField.getText().isEmpty() || nameTextField.getText().isEmpty() || descriptionTextArea.getText().isEmpty() || priceTextField.getText().isEmpty() || ecoRatingTextField.getText().isEmpty())){
            
           //Go through the entire list of current products
            for (Product p : products){
                //If the product code of the current product matches the one in the text field...
                if (p.getProductCode().equals(productCodeTextField.getText())){
                    //Set the isInTable flag as true
                    isInTable = true;
                    
                    /*
                    ...then check if any of the other fields have changed.
                    If all of the values in the text field match the values
                    in of the product's fields...
                    
                    */
                    if(
                        p.getProductCode().equals(id) &&
                        p.getName().equals(name) &&
                        p.getDescription().equals(desc) &&
                        p.getPrice().equals(price) &&
                        Double.compare(p.getRating(), rating) == 0
                    ){
                        //Set the isUnchanged flag as true
                        isUnchanged = true;
                        
                    }
                   
                }
                
                
            }
            
            //Go through the entire list of NEW products
            for (Product p : newProducts){
                //If the product code of the current product matches the one in the text field...
                if (p.getProductCode().equals(productCodeTextField.getText())){
                    //Set the isInTable flag as true
                    isInTable = true;
                    
                    /*
                    ...then check if any of the other fields have changed.
                    If all of the values in the text field match the values
                    in of the product's fields...
                    
                    */
                    if(
                        p.getProductCode().equals(id) &&
                        p.getName().equals(name) &&
                        p.getDescription().equals(desc) &&
                        p.getPrice().equals(price) &&
                        Double.compare(p.getRating(), rating) == 0
                    ){
                        //Set the isUnchanged flag as true
                        isUnchanged = true;
                        
                    }
                   
                }
                
                
            }
            
            /*
            If the product is in the table and has at least one of it's values
            (besides Product Code) have changed...
            */
            if (isInTable && !isUnchanged ){
                
                
                //Checks if the enetered values are in the correct form
                if(!id.matches("ECO-[0-9]{6}")){
                    outputTextArea.setForeground(Color.red);
                    outputTextArea.setText("Error! Inavlid Product Code! Product Codes should be in the form \"ECO-XXXXXX\", where X is a numerical digit.");
                } else if(!price.matches("\\$[0-9]+\\.[0-9]{2}")){
                    outputTextArea.setForeground(Color.red);
                    outputTextArea.setText("Error! Inavlid Price! Prices should be in the form \"X(...).XX\", where X is a numerical digit (e.g. 149.49).");
                } else {
                    
                    //Set a default value for the index of the outdated product
                    int rowToDelete = -1;
                    //Flag to signify whether the product has already been updated once during the session
                    boolean isAlreadyChanged = false;

                    //Find the index of the item to delete
                    for(int i = 0; i < ((javax.swing.table.DefaultTableModel) productTable.getModel()).getRowCount(); i++){
                        if (((javax.swing.table.DefaultTableModel) productTable.getModel()).getValueAt(i, 0).equals(id)){
                            rowToDelete = i;
                        }
                    }

                    //If the index wasn't found...
                    if (rowToDelete == -1){

                        //Print an error message and end the process
                        outputTextArea.setForeground(Color.red);
                        outputTextArea.setText("Error! Item Doesn't Exist.");
                        return;
                    }


                    /*
                    Search the list and check if the product being updated
                    is already there                
                    */                
                    for (int i = 0; i <changedProducts.size(); i++){
                        //If the product is found...
                        if(changedProducts.get(i).getProductCode().equals(id)){
                            //Update the old details
                            changedProducts.get(i).setName(name);
                            changedProducts.get(i).setDescription(desc);
                            changedProducts.get(i).setPrice(price);
                            changedProducts.get(i).setRating(rating);
                            //Set the flag saying that the product has been updated more than once
                            isAlreadyChanged = true;
                            break;
                        }
                    }

                    //If it hasn't been updated at all...
                    if (!isAlreadyChanged){
                        //Update it for the first time and make note of it in the changedProducts list
                        changedProducts.add(new Product(id, name, desc, price, rating));
                    }
                    
                    
                    for (int i = 0; i < products.size(); i++){
                        //Find the product in the current list of products
                        if(products.get(i).getProductCode().equals(id)){
                       
                            //Update the product
                            products.get(i).setName(name);
                            products.get(i).setDescription(desc);
                            products.get(i).setPrice(price);
                            products.get(i).setRating(rating);
                            break;
                       }
                    
                    }
                    //Remove the outdated item from the table in the UI
                    ((javax.swing.table.DefaultTableModel) productTable.getModel()).removeRow(rowToDelete);
                    //Add the updated item
                    ((javax.swing.table.DefaultTableModel) productTable.getModel()).addRow(new Object[]{id, name, desc, price, rating});
                    //Refresh the table
                    ((javax.swing.table.DefaultTableModel) productTable.getModel()).fireTableDataChanged();
                    //Set the isSaved flag and noticeAdded flag to false
                    isSaved = noticeAdded = false;
                    //Output a message saying the item was updated successfully
                    outputTextArea.setForeground(Color.green);
                    outputTextArea.setText("Item updated sucessfully!");
                }
                
                
                
            } else {
                //Print an error message
                outputTextArea.setForeground(Color.red);
                outputTextArea.setText("Error! Item Unmodified; Unable to make new changes.");
            }
            
            
        }
    }//GEN-LAST:event_updateButtonActionPerformed

    private void deleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteButtonActionPerformed
       
        //Flag to signify if the product is in the table 
        boolean isInTable = false;
       
        //If none of the fields are empty...
        if (!(productCodeTextField.getText().isEmpty() || nameTextField.getText().isEmpty() || descriptionTextArea.getText().isEmpty() || priceTextField.getText().isEmpty() || ecoRatingTextField.getText().isEmpty())){
            
            //Go through the entire list of current products
            for (Product p : products){
                //If the product code of the current product matches the one in the text field...
                if (p.getProductCode().equals(productCodeTextField.getText())){
                    isInTable = true;
                }
            }
            
            //Go through the entire list of NEW products
            for (Product p : newProducts){
                //If the product code of the current product matches the one in the text field...
                if (p.getProductCode().equals(productCodeTextField.getText())){
                    isInTable = true;
                }
            }
            
            //If the product is in the table...
            if (isInTable){
                String id = productCodeTextField.getText();
                String name = nameTextField.getText();
                String desc = descriptionTextArea.getText();
                String price = "$".concat(priceTextField.getText());
                Double rating = Double.parseDouble(ecoRatingTextField.getText());
                
                //Set a default value for the index of the outdated product
                int rowToDelete = -1;
                
                //Find the index of the item to delete
                for(int i = 0; i < ((javax.swing.table.DefaultTableModel) productTable.getModel()).getRowCount(); i++){
                    if (((javax.swing.table.DefaultTableModel) productTable.getModel()).getValueAt(i, 0).equals(id)){
                        rowToDelete = i;
                    }
                }
            
            
                //If the index wasn't found...
                if (rowToDelete == -1){

                    //Print an error message and end the process
                    outputTextArea.setForeground(Color.red);
                    outputTextArea.setText("Error! Item Doesn't Exist.");
                    return;
                }
                
                for (int i = 0; i < products.size(); i++){
                    //Find the product in the current list of products
                    if(products.get(i).getProductCode().equals(id)){

                        products.remove(i);
                        //Add the product to be deleted to a list of items to be deleted
                        deletedProducts.add(new Product(id, name, desc, price, rating));
                        break;
                   }

                }
                
                for (int i = 0; i < newProducts.size(); i++){
                    //Find the product in the current list of products
                    if(newProducts.get(i).getProductCode().equals(id)){

                        newProducts.remove(i);
                        break;
                   }

                }
                
                
                //Delete the item off the table in the UI
                ((javax.swing.table.DefaultTableModel) productTable.getModel()).removeRow(rowToDelete);
                //Update the table
                ((javax.swing.table.DefaultTableModel) productTable.getModel()).fireTableDataChanged();
                
                //Set the isSaved flag and noticeAdded flag to false
                isSaved = noticeAdded = false;
                //Print a message saying the delete operation was successful
                outputTextArea.setForeground(Color.green);
                outputTextArea.setText("Item deleted sucessfully!");
                
            } else {
                //Print an error message
                outputTextArea.setForeground(Color.red);
                outputTextArea.setText("Error! Item Doesn't Exist.");
            }
            
            
        }
    }//GEN-LAST:event_deleteButtonActionPerformed

    private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addButtonActionPerformed
        
        //Flag to signify if the product is in the table 
        boolean isInTable = false;
       
        //If none of the fields are empty...
        if (!(productCodeTextField.getText().isEmpty() || nameTextField.getText().isEmpty() || descriptionTextArea.getText().isEmpty() || priceTextField.getText().isEmpty() || ecoRatingTextField.getText().isEmpty())){
            
            //Go through the entire list of products
            for (Product p : products){
                //If the product code of the current product matches the one in the text field...
                if (p.getProductCode().equals(productCodeTextField.getText())){
                    isInTable = true;
                }
            }
            //Check if the item has not been newly created
            for (Product p : newProducts){
                //If the product code of the current product matches the one in the text field...
                if (p.getProductCode().equals(productCodeTextField.getText())){
                    isInTable = true;
                }
            }
            
            //If the item does not exist yet...
            if (!isInTable){
                String id = productCodeTextField.getText();
                String name = nameTextField.getText();
                String desc = descriptionTextArea.getText();
                String price = "$".concat(priceTextField.getText());
                Double rating = Double.parseDouble(ecoRatingTextField.getText());
                
                //Checks if the enetered values are in the correct form
                if(!id.matches("ECO-[0-9]{6}")){
                    outputTextArea.setForeground(Color.red);
                    outputTextArea.setText("Error! Inavlid Product Code! Product Codes should be in the form \"ECO-XXXXXX\", where X is a numerical digit.");
                } else if(!price.matches("\\$[0-9]+\\.[0-9]{2}")){
                    outputTextArea.setForeground(Color.red);
                    outputTextArea.setText("Error! Inavlid Price! Prices should be in the form \"X(...).XX\", where X is a numerical digit (e.g. 149.49).");
                } else {
                    //Add the product to a list of new products
                    newProducts.add(new Product(id, name, desc, price, rating));
                    //Add the produuct to the list
                    ((javax.swing.table.DefaultTableModel) productTable.getModel()).addRow(new Object[]{id, name, desc, price, rating});
                    //Update the table in the UI
                    ((javax.swing.table.DefaultTableModel) productTable.getModel()).fireTableDataChanged();


                     //Set the isSaved flag and noticeAdded flag to false
                    isSaved = noticeAdded = false;
                    //Print a message saying the delete operation was successful
                    outputTextArea.setForeground(Color.green);
                    outputTextArea.setText("Item added sucessfully!");
                }
            } else {
                //Print an error message
                outputTextArea.setForeground(Color.red);
                outputTextArea.setText("Error! Item Already Exists");
            }
            
            
        }
    }//GEN-LAST:event_addButtonActionPerformed

    private void saveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveButtonActionPerformed
        String sql;
        String id;
        String name;
        String desc;
        String price;
        Double rating;
        boolean changesMade = false;
        
        try{
            //If the lists are not uninstantiated...
            if(!(newProducts == null && changedProducts == null && deletedProducts == null)){

                //If there are new products to add...
                if (!newProducts.isEmpty()){
                    //Pre the beginning of the SQL statement
                    sql = "INSERT INTO product (product_code, name, description, price, eco_rating) VALUES ";
                    for (int i = 0; i < newProducts.size(); i++){
                        //Get the details of each Product
                        id = newProducts.get(i).getProductCode();
                         name = newProducts.get(i).getName();
                         desc = newProducts.get(i).getDescription();
                         price = newProducts.get(i).getPrice();
                         rating = newProducts.get(i).getRating();
                         //If it's not the last product in the list...
                        if (i == newProducts.size()-1){
                            //Add the item to the list of values in the SQL statement this way
                            sql = sql.concat(String.format("('%s', '%s', '%s', '%s', %f);", id, name, desc, price, rating));
                        } else {
                            //Otherwise add the last item and close off the statement
                            sql = sql.concat(String.format("('%s', '%s', '%s', '%s', %f), ", id, name, desc, price, rating));
                        }

                            
                    }
                    //Output the created statement
                    System.out.println(sql);
                    //Add the items to the table in the database
                    db.updateQuery(sql, newProducts.size());
                }

                //If there are products to change...
                if (!changedProducts.isEmpty()){
                    sql = "";
                    for (int i = 0; i < changedProducts.size(); i++){
                        
                        //Get the product details
                        id = changedProducts.get(i).getProductCode();
                        name = changedProducts.get(i).getName();
                        desc = changedProducts.get(i).getDescription();
                        price = changedProducts.get(i).getPrice();
                        rating = changedProducts.get(i).getRating();
                        //Add the new UPDATE statement
                        sql = sql.concat(String.format("UPDATE product SET name = '%s', description = '%s', price = '%s', eco_rating = %f WHERE product_code = '%s'; ", name, desc, price, rating, id));
                        //Print out the UPDATE statement made
                        System.out.println(String.format("UPDATE product SET name = '%s', description = '%s', price = '%s', eco_rating = %f WHERE product_code = '%s'; ", name, desc, price, rating, id));

                    }   
                        //Update the rows in the table in the database
                        db.updateQuery(sql, changedProducts.size());
                }

                //If there are products to delete...
                if (!deletedProducts.isEmpty()){
                    sql = "";
                    for (int i = 0; i < deletedProducts.size(); i++){
                        id = deletedProducts.get(i).getProductCode();
                        sql = sql.concat(String.format("DELETE FROM product WHERE product_code = '%s'; ", id));
                        //Print out the DELETE statement made
                        System.out.println(String.format("DELETE FROM product WHERE product_code = '%s'; ", id));

                    }
                        //Delete the rows from the table in the database
                        db.updateQuery(sql, deletedProducts.size());
                }

                //Set the flag saying changes have been made
                changesMade = true;
                //Set the flag saying the data has been saved to the database
                isSaved = true;
            } 
        } catch (SQLException e){
            //Print an error message
            outputTextArea.setForeground(Color.red);
            outputTextArea.setText("Failed to save changes; SQLException occured.\n\n" + e);     
        } finally {
            //Empty the lists
            resetLists();
            //Reload the UI table with the updated table from the database
            loadTable();
            
            //If changes have been made and saved...
            if(changesMade){
                //Output a confirmation message
                outputTextArea.setForeground(Color.green);
                outputTextArea.setText("Changes saved!");
            }
        
        }
        
        
        
        
    }//GEN-LAST:event_saveButtonActionPerformed

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
        //Clost the database connection
        db.close();
    }//GEN-LAST:event_formWindowClosed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new ProductApp().setVisible(true));
    }

    private void loadTable(){
     
    ((javax.swing.table.DefaultTableModel) productTable.getModel()).getDataVector().removeAllElements();
    try{
        result = db.searchQuery("SELECT * FROM product;");
        //If there is data in the query...
        if (result != null){
            //Move to the first element
            result.next();
            //While the read head for the results is not past the last element
            while(!result.isAfterLast()){
                //Get the data from each field of the row
                String id = result.getString("product_code");
                String name = result.getString("name");
                String desc = result.getString("description");
                String price = result.getString("price");
                Double rating = result.getDouble("eco_rating");

                //Add the product to the product list
                products.add(new Product(id, name, desc, price, rating));
                //Add the product to the table
                ((javax.swing.table.DefaultTableModel) productTable.getModel()).addRow(new Object[]{id, name, desc, price, rating});
                //Refresh the table
                ((javax.swing.table.DefaultTableModel) productTable.getModel()).fireTableDataChanged();
                //Move to the next element
                result.next();
            }
            //Close the result
            result.close();

        }

        
        } catch (SQLException e){
            //Print an error message
            outputTextArea.setForeground(Color.red);
            outputTextArea.setText("Exception encountered:\n\n " + e);
        
        }
    }
    
    private void resetLists(){
        
        //IIf the lists are all instatiated
        if (!(products == null || newProducts == null || changedProducts == null || deletedProducts == null)){
           //Clear all the lists of elements
            products.clear();
            newProducts.clear();
            changedProducts.clear();
            deletedProducts.clear();
        }
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addButton;
    private javax.swing.JButton deleteButton;
    private javax.swing.JTextArea descriptionTextArea;
    private javax.swing.JSlider ecoRatingSlider;
    private javax.swing.JTextField ecoRatingTextField;
    private javax.swing.JLabel ecoRatingValueLabel;
    private javax.swing.JButton exitButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLayeredPane jLayeredPane1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTextField nameTextField;
    private javax.swing.JTextArea outputTextArea;
    private javax.swing.JTextField priceTextField;
    private javax.swing.JTextField productCodeTextField;
    private javax.swing.JTable productTable;

    private javax.swing.JButton saveButton;
    private javax.swing.JButton updateButton;
    // End of variables declaration//GEN-END:variables
}
