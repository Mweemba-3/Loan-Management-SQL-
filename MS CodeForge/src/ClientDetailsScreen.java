// ClientDetailsScreen.java
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.print.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class ClientDetailsScreen extends JPanel {
    private ClientsScreen.Client client;
    private int currentUserId;
    private String currentUserRole;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
    private SimpleDateFormat datetimeFormat = new SimpleDateFormat("dd-MMM-yyyy HH:mm");
    
    public ClientDetailsScreen(ClientsScreen.Client client, int userId, String userRole) {
        this.client = client;
        this.currentUserId = userId;
        this.currentUserRole = userRole;
        initUI();
        loadClientDetails();
    }
    
    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 245));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        JLabel titleLabel = new JLabel("CLIENT DETAILS");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(50, 50, 50));
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        JButton backButton = new JButton("← Back to Clients");
        styleButton(backButton, new Color(120, 120, 120), new Color(100, 100, 100));
        backButton.addActionListener(e -> goBack());
        headerPanel.add(backButton, BorderLayout.EAST);
        
        add(headerPanel, BorderLayout.NORTH);
        
        // Main Content
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        tabbedPane.addTab("Personal Info", createPersonalInfoPanel());
        tabbedPane.addTab("Next of Kin", createNextOfKinPanel());
        tabbedPane.addTab("Bank Details", createBankDetailsPanel());
        tabbedPane.addTab("Loan History", createLoanHistoryPanel());
        tabbedPane.addTab("Payment History", createPaymentHistoryPanel());
        
        add(tabbedPane, BorderLayout.CENTER);
    }
    
    private JPanel createPersonalInfoPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.gridx = 0;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                 "SELECT c.*, b.branch_name FROM clients c LEFT JOIN branches b ON c.branch_id = b.branch_id WHERE c.client_id = ?")) {
            
            stmt.setInt(1, client.getClientId());
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                int row = 0;
                
                // Personal Information
                addDetailRow(panel, gbc, "Client ID:", String.valueOf(client.getClientId()), row++);
                addDetailRow(panel, gbc, "Full Name:", 
                    rs.getString("title") + " " + rs.getString("first_name") + " " + 
                    (rs.getString("middle_name") != null ? rs.getString("middle_name") + " " : "") + 
                    rs.getString("last_name"), row++);
                addDetailRow(panel, gbc, "Date of Birth:", 
                    dateFormat.format(rs.getDate("date_of_birth")), row++);
                addDetailRow(panel, gbc, "Gender:", rs.getString("gender"), row++);
                addDetailRow(panel, gbc, "Marital Status:", rs.getString("marital_status"), row++);
                
                // Contact Information
                addDetailRow(panel, gbc, "Phone:", rs.getString("phone_number"), row++);
                addDetailRow(panel, gbc, "Email:", 
                    rs.getString("email") != null ? rs.getString("email") : "Not provided", row++);
                addDetailRow(panel, gbc, "Physical Address:", rs.getString("physical_address"), row++);
                addDetailRow(panel, gbc, "Province:", rs.getString("province"), row++);
                addDetailRow(panel, gbc, "Postal Address:", 
                    rs.getString("postal_address") != null ? rs.getString("postal_address") : "Not provided", row++);
                
                // Identification
                addDetailRow(panel, gbc, "ID Type:", rs.getString("id_type"), row++);
                addDetailRow(panel, gbc, "ID Number:", rs.getString("id_number"), row++);
                
                // Employment Information
                addDetailRow(panel, gbc, "Employment Status:", rs.getString("employment_status"), row++);
                addDetailRow(panel, gbc, "Employer:", 
                    rs.getString("employer_name") != null ? rs.getString("employer_name") : "Not provided", row++);
                addDetailRow(panel, gbc, "Job Title:", 
                    rs.getString("job_title") != null ? rs.getString("job_title") : "Not provided", row++);
                addDetailRow(panel, gbc, "Monthly Income:", 
                    String.format("ZMW %,.2f", rs.getDouble("monthly_income")), row++);
                
                // Branch
                addDetailRow(panel, gbc, "Branch:", 
                    rs.getString("branch_name") != null ? rs.getString("branch_name") : "Not assigned", row++);
                
                // Dates
                addDetailRow(panel, gbc, "Member Since:", 
                    dateFormat.format(rs.getTimestamp("created_at")), row++);
                addDetailRow(panel, gbc, "Last Updated:", 
                    dateFormat.format(rs.getTimestamp("updated_at")), row);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading client details: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
        
        return panel;
    }
    
    private JPanel createNextOfKinPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        String[] columnNames = {"Name", "Relationship", "Phone", "ID Number", "Address"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable table = new JTable(model);
        customizeTable(table);
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                 "SELECT * FROM next_of_kin WHERE client_id = ?")) {
            
            stmt.setInt(1, client.getClientId());
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("name"),
                    rs.getString("relationship"),
                    rs.getString("phone"),
                    rs.getString("id_number") != null ? rs.getString("id_number") : "Not provided",
                    rs.getString("address") != null ? rs.getString("address") : "Not provided"
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createBankDetailsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        String[] columnNames = {"Bank Name", "Account Number", "Account Name", "Branch", "Branch Code"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable table = new JTable(model);
        customizeTable(table);
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                 "SELECT * FROM bank_details WHERE client_id = ?")) {
            
            stmt.setInt(1, client.getClientId());
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("bank_name"),
                    rs.getString("account_number"),
                    rs.getString("account_name"),
                    rs.getString("branch_name") != null ? rs.getString("branch_name") : "Not provided",
                    rs.getString("branch_code") != null ? rs.getString("branch_code") : "Not provided"
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createLoanHistoryPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        String[] columnNames = {"Loan Number", "Amount", "Status", "Application Date", "Due Date", "Outstanding Balance"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable table = new JTable(model);
        customizeTable(table);
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                 "SELECT loan_number, amount, status, application_date, due_date, outstanding_balance " +
                 "FROM loans WHERE client_id = ? ORDER BY application_date DESC")) {
            
            stmt.setInt(1, client.getClientId());
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("loan_number"),
                    String.format("ZMW %,.2f", rs.getDouble("amount")),
                    rs.getString("status"),
                    dateFormat.format(rs.getTimestamp("application_date")),
                    rs.getDate("due_date") != null ? dateFormat.format(rs.getDate("due_date")) : "N/A",
                    String.format("ZMW %,.2f", rs.getDouble("outstanding_balance"))
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Summary Panel
        JPanel summaryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        summaryPanel.setBackground(new Color(240, 240, 240));
        summaryPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                 "SELECT COUNT(*) as total_loans, " +
                 "SUM(amount) as total_borrowed, " +
                 "SUM(outstanding_balance) as total_balance " +
                 "FROM loans WHERE client_id = ? AND status IN ('Active', 'Approved')")) {
            
            stmt.setInt(1, client.getClientId());
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                JLabel summaryLabel = new JLabel(String.format(
                    "Active Loans: %d | Total Borrowed: ZMW %,.2f | Outstanding Balance: ZMW %,.2f",
                    rs.getInt("total_loans"),
                    rs.getDouble("total_borrowed"),
                    rs.getDouble("total_balance")
                ));
                summaryLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
                summaryPanel.add(summaryLabel);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        panel.add(summaryPanel, BorderLayout.NORTH);
        
        return panel;
    }
    
    private JPanel createPaymentHistoryPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        String[] columnNames = {
            "Payment Date", "Loan Number", "Payment #", 
            "Amount Paid", "Principal", "Interest", "Penalty", 
            "Payment Mode", "Voucher #", "Status", "Received By"
        };
        
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable table = new JTable(model);
        customizePaymentTable(table);
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                 "SELECT lp.*, l.loan_number, e.name as received_by_name " +
                 "FROM loan_payments lp " +
                 "JOIN loans l ON lp.loan_id = l.loan_id " +
                 "LEFT JOIN employees e ON lp.received_by = e.employee_id " +
                 "WHERE l.client_id = ? " +
                 "ORDER BY lp.paid_date DESC, lp.scheduled_payment_date DESC")) {
            
            stmt.setInt(1, client.getClientId());
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                String paymentDate = rs.getTimestamp("paid_date") != null ? 
                    datetimeFormat.format(rs.getTimestamp("paid_date")) : "Not Paid";
                
                model.addRow(new Object[]{
                    paymentDate,
                    rs.getString("loan_number"),
                    rs.getInt("payment_number"),
                    String.format("ZMW %,.2f", rs.getDouble("paid_amount")),
                    String.format("ZMW %,.2f", rs.getDouble("principal_amount")),
                    String.format("ZMW %,.2f", rs.getDouble("interest_amount")),
                    String.format("ZMW %,.2f", rs.getDouble("penalty_amount")),
                    rs.getString("payment_mode"),
                    rs.getString("voucher_number"),
                    rs.getString("status"),
                    rs.getString("received_by_name") != null ? rs.getString("received_by_name") : "N/A"
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading payment history: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
        
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Payment Summary Panel with Print Button
        JPanel summaryPanel = createPaymentSummaryPanel();
        panel.add(summaryPanel, BorderLayout.NORTH);
        
        return panel;
    }
    
    private JPanel createPaymentSummaryPanel() {
        JPanel summaryPanel = new JPanel(new BorderLayout());
        summaryPanel.setBackground(new Color(240, 240, 240));
        summaryPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 10, 0));
        statsPanel.setBackground(new Color(240, 240, 240));
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(new Color(240, 240, 240));
        
        // Print Button
        JButton printButton = new JButton("🖨️ Print Statement");
        styleButton(printButton, new Color(70, 130, 180), new Color(60, 120, 170));
        printButton.addActionListener(e -> printPaymentStatement());
        buttonPanel.add(printButton);
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                 "SELECT " +
                 "COUNT(*) as total_payments, " +
                 "SUM(paid_amount) as total_paid, " +
                 "SUM(principal_amount) as total_principal, " +
                 "SUM(interest_amount) as total_interest, " +
                 "SUM(penalty_amount) as total_penalty " +
                 "FROM loan_payments lp " +
                 "JOIN loans l ON lp.loan_id = l.loan_id " +
                 "WHERE l.client_id = ? AND lp.status = 'Paid'")) {
            
            stmt.setInt(1, client.getClientId());
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                statsPanel.add(createSummaryCard("Total Payments", 
                    String.valueOf(rs.getInt("total_payments")), new Color(70, 130, 180)));
                statsPanel.add(createSummaryCard("Total Paid", 
                    String.format("ZMW %,.2f", rs.getDouble("total_paid")), new Color(60, 179, 113)));
                statsPanel.add(createSummaryCard("Total Principal", 
                    String.format("ZMW %,.2f", rs.getDouble("total_principal")), new Color(255, 165, 0)));
                statsPanel.add(createSummaryCard("Total Interest", 
                    String.format("ZMW %,.2f", rs.getDouble("total_interest")), new Color(220, 20, 60)));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        summaryPanel.add(statsPanel, BorderLayout.CENTER);
        summaryPanel.add(buttonPanel, BorderLayout.EAST);
        
        return summaryPanel;
    }
    
    private void printPaymentStatement() {
        try {
            PrinterJob job = PrinterJob.getPrinterJob();
            job.setJobName("Client Payment Statement - " + client.getClientId());
            
            // Create printable component
            Printable printable = new Printable() {
                @Override
                public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
                    if (pageIndex > 0) {
                        return NO_SUCH_PAGE;
                    }
                    
                    Graphics2D g2d = (Graphics2D) graphics;
                    g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
                    
                    // Set font and color
                    g2d.setFont(new Font("Arial", Font.BOLD, 16));
                    g2d.setColor(Color.BLACK);
                    
                    int y = 50;
                    int lineHeight = 20;
                    
                    // Header
                    g2d.drawString("MS CODEFORGE - CLIENT PAYMENT STATEMENT", 50, y);
                    y += lineHeight + 10;
                    
                    g2d.setFont(new Font("Arial", Font.PLAIN, 12));
                    g2d.drawString("Generated on: " + new SimpleDateFormat("dd-MMM-yyyy HH:mm").format(new java.util.Date()), 50, y);
                    y += lineHeight;
                    
                    // Client Information
                    String clientInfo = getClientInfo();
                    String[] clientLines = clientInfo.split("\n");
                    for (String line : clientLines) {
                        g2d.drawString(line, 50, y);
                        y += lineHeight;
                    }
                    
                    y += 10;
                    
                    // Payment Summary
                    g2d.setFont(new Font("Arial", Font.BOLD, 14));
                    g2d.drawString("PAYMENT SUMMARY", 50, y);
                    y += lineHeight + 5;
                    
                    g2d.setFont(new Font("Arial", Font.PLAIN, 12));
                    String paymentSummary = getPaymentSummary();
                    String[] summaryLines = paymentSummary.split("\n");
                    for (String line : summaryLines) {
                        g2d.drawString(line, 50, y);
                        y += lineHeight;
                    }
                    
                    y += 10;
                    
                    // Payment Details Header
                    g2d.setFont(new Font("Arial", Font.BOLD, 14));
                    g2d.drawString("PAYMENT DETAILS", 50, y);
                    y += lineHeight + 5;
                    
                    // Payment Details Table Headers
                    g2d.setFont(new Font("Arial", Font.BOLD, 10));
                    g2d.drawString("Date", 50, y);
                    g2d.drawString("Loan #", 120, y);
                    g2d.drawString("Amount", 200, y);
                    g2d.drawString("Principal", 280, y);
                    g2d.drawString("Interest", 360, y);
                    g2d.drawString("Mode", 440, y);
                    g2d.drawString("Status", 500, y);
                    y += lineHeight;
                    
                    // Payment Details
                    g2d.setFont(new Font("Arial", Font.PLAIN, 9));
                    List<String[]> paymentData = getPaymentData();
                    for (String[] payment : paymentData) {
                        if (y > pageFormat.getImageableHeight() - 50) {
                            // Start new page if needed
                            return PAGE_EXISTS;
                        }
                        
                        g2d.drawString(payment[0], 50, y); // Date
                        g2d.drawString(payment[1], 120, y); // Loan Number
                        g2d.drawString(payment[2], 200, y); // Amount
                        g2d.drawString(payment[3], 280, y); // Principal
                        g2d.drawString(payment[4], 360, y); // Interest
                        g2d.drawString(payment[5], 440, y); // Mode
                        g2d.drawString(payment[6], 500, y); // Status
                        y += lineHeight;
                    }
                    
                    // Footer
                    y = (int) pageFormat.getImageableHeight() - 30;
                    g2d.setFont(new Font("Arial", Font.ITALIC, 10));
                    g2d.drawString("Generated by MS CodeForge Loan Management System", 50, y);
                    
                    return PAGE_EXISTS;
                }
            };
            
            job.setPrintable(printable);
            
            if (job.printDialog()) {
                job.print();
                JOptionPane.showMessageDialog(this, 
                    "Payment statement sent to printer successfully!", 
                    "Print Success", 
                    JOptionPane.INFORMATION_MESSAGE);
            }
            
        } catch (PrinterException ex) {
            JOptionPane.showMessageDialog(this, 
                "Error printing statement: " + ex.getMessage(), 
                "Print Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private String getClientInfo() {
        StringBuilder info = new StringBuilder();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                 "SELECT title, first_name, last_name, id_number, phone_number " +
                 "FROM clients WHERE client_id = ?")) {
            
            stmt.setInt(1, client.getClientId());
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                info.append("Client: ").append(rs.getString("title"))
                    .append(" ").append(rs.getString("first_name"))
                    .append(" ").append(rs.getString("last_name")).append("\n");
                info.append("ID Number: ").append(rs.getString("id_number")).append("\n");
                info.append("Phone: ").append(rs.getString("phone_number")).append("\n");
                info.append("Client ID: ").append(client.getClientId());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return info.toString();
    }
    
    private String getPaymentSummary() {
        StringBuilder summary = new StringBuilder();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                 "SELECT " +
                 "COUNT(*) as total_payments, " +
                 "SUM(paid_amount) as total_paid, " +
                 "SUM(principal_amount) as total_principal, " +
                 "SUM(interest_amount) as total_interest, " +
                 "SUM(penalty_amount) as total_penalty " +
                 "FROM loan_payments lp " +
                 "JOIN loans l ON lp.loan_id = l.loan_id " +
                 "WHERE l.client_id = ? AND lp.status = 'Paid'")) {
            
            stmt.setInt(1, client.getClientId());
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                summary.append("Total Payments: ").append(rs.getInt("total_payments")).append("\n");
                summary.append("Total Amount Paid: ZMW ").append(String.format("%,.2f", rs.getDouble("total_paid"))).append("\n");
                summary.append("Total Principal: ZMW ").append(String.format("%,.2f", rs.getDouble("total_principal"))).append("\n");
                summary.append("Total Interest: ZMW ").append(String.format("%,.2f", rs.getDouble("total_interest"))).append("\n");
                summary.append("Total Penalty: ZMW ").append(String.format("%,.2f", rs.getDouble("total_penalty")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return summary.toString();
    }
    
    private List<String[]> getPaymentData() {
        List<String[]> paymentData = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                 "SELECT lp.paid_date, l.loan_number, lp.paid_amount, " +
                 "lp.principal_amount, lp.interest_amount, lp.payment_mode, lp.status " +
                 "FROM loan_payments lp " +
                 "JOIN loans l ON lp.loan_id = l.loan_id " +
                 "WHERE l.client_id = ? " +
                 "ORDER BY lp.paid_date DESC")) {
            
            stmt.setInt(1, client.getClientId());
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                String date = rs.getTimestamp("paid_date") != null ? 
                    new SimpleDateFormat("dd-MMM-yy").format(rs.getTimestamp("paid_date")) : "N/A";
                String amount = String.format("%,.2f", rs.getDouble("paid_amount"));
                String principal = String.format("%,.2f", rs.getDouble("principal_amount"));
                String interest = String.format("%,.2f", rs.getDouble("interest_amount"));
                
                paymentData.add(new String[]{
                    date,
                    rs.getString("loan_number"),
                    amount,
                    principal,
                    interest,
                    rs.getString("payment_mode"),
                    rs.getString("status")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return paymentData;
    }
    
    private JPanel createSummaryCard(String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color, 2),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        titleLabel.setForeground(color);
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        valueLabel.setForeground(Color.BLACK);
        
        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        
        return card;
    }
    
    private void customizePaymentTable(JTable table) {
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setRowHeight(30);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        
        // Set column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(120); // Payment Date
        table.getColumnModel().getColumn(1).setPreferredWidth(100); // Loan Number
        table.getColumnModel().getColumn(2).setPreferredWidth(80);  // Payment #
        table.getColumnModel().getColumn(3).setPreferredWidth(100); // Amount Paid
        table.getColumnModel().getColumn(4).setPreferredWidth(100); // Principal
        table.getColumnModel().getColumn(5).setPreferredWidth(100); // Interest
        table.getColumnModel().getColumn(6).setPreferredWidth(100); // Penalty
        table.getColumnModel().getColumn(7).setPreferredWidth(80);  // Payment Mode
        table.getColumnModel().getColumn(8).setPreferredWidth(100); // Voucher #
        table.getColumnModel().getColumn(9).setPreferredWidth(80);  // Status
        table.getColumnModel().getColumn(10).setPreferredWidth(120); // Received By
        
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setBackground(new Color(70, 130, 180));
        header.setForeground(Color.WHITE);
        header.setReorderingAllowed(false);
        
        // Center align all columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
    }
    
    private void addDetailRow(JPanel panel, GridBagConstraints gbc, String label, String value, int row) {
        gbc.gridy = row;
        gbc.gridx = 0;
        gbc.weightx = 0.3;
        
        JLabel labelComp = new JLabel(label);
        labelComp.setFont(new Font("Segoe UI", Font.BOLD, 14));
        labelComp.setForeground(new Color(70, 70, 70));
        panel.add(labelComp, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        
        JLabel valueComp = new JLabel(value);
        valueComp.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panel.add(valueComp, gbc);
    }
    
    private void customizeTable(JTable table) {
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(35);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(new Color(70, 130, 180));
        header.setForeground(Color.WHITE);
        header.setReorderingAllowed(false);
        
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
    }
    
    private void styleButton(JButton button, Color bgColor, Color hoverColor) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(hoverColor);
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });
    }
    
    private void goBack() {
        ScreenManager.getInstance().showScreen(new ClientsScreen(currentUserId, currentUserRole));
    }
    
    private void loadClientDetails() {
        // Method implementation if needed
    }
}