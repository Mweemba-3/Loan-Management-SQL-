import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Arc2D;
import java.io.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AdminDashboard extends JPanel {
    private int employeeId;
    private String employeeName;
    private DoughnutChart doughnutChart;
    private JLabel welcomeLabel;
    private Timer animationTimer;
    private float animationProgress = 0f;

    public AdminDashboard(int employeeId, String employeeName) {
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        initUI();
        loadDashboardStats();
        startAnimation();
        logActivity("Dashboard Access", "Accessed admin dashboard");
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(new Color(34, 40, 49));
        
        // Create sidebar
        JPanel sidebar = createSidebar();
        add(sidebar, BorderLayout.WEST);
        
        // Main content area
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(new Color(45, 52, 64));
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Header
        JPanel headerPanel = createHeaderPanel();
        contentPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Center Panel with Doughnut Chart
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(new Color(45, 52, 64));
        
        // Welcome and refresh panel
        JPanel welcomePanel = new JPanel(new BorderLayout());
        welcomePanel.setBackground(new Color(45, 52, 64));
        welcomePanel.setBorder(new EmptyBorder(0, 0, 20, 0));
        
        welcomeLabel = new JLabel("Welcome back, " + employeeName + "!");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        welcomeLabel.setForeground(new Color(200, 200, 200));
        
        JButton refreshBtn = new JButton("Refresh Dashboard");
        styleButton(refreshBtn, new Color(70, 130, 180), new Color(60, 120, 170));
        refreshBtn.addActionListener(e -> refreshDashboard());
        
        welcomePanel.add(welcomeLabel, BorderLayout.WEST);
        welcomePanel.add(refreshBtn, BorderLayout.EAST);
        
        // Doughnut Chart Panel
        JPanel chartPanel = new JPanel(new BorderLayout());
        chartPanel.setBackground(new Color(57, 62, 70));
        chartPanel.setBorder(new CompoundBorder(
            new LineBorder(new Color(70, 70, 70), 2),
            new EmptyBorder(20, 20, 20, 20)
        ));
        
        doughnutChart = new DoughnutChart();
        chartPanel.add(doughnutChart, BorderLayout.CENTER);
        
        // Chart legend
        JPanel legendPanel = createLegendPanel();
        chartPanel.add(legendPanel, BorderLayout.SOUTH);
        
        centerPanel.add(welcomePanel, BorderLayout.NORTH);
        centerPanel.add(chartPanel, BorderLayout.CENTER);
        
        contentPanel.add(centerPanel, BorderLayout.CENTER);
        add(contentPanel, BorderLayout.CENTER);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(45, 52, 64));
        headerPanel.setBorder(new MatteBorder(0, 0, 1, 0, new Color(70, 70, 70)));
        
        JLabel titleLabel = new JLabel("ADMIN DASHBOARD");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(new Color(0, 173, 181));
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        JLabel roleLabel = new JLabel("Role: Administrator | ID: " + employeeId);
        roleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        roleLabel.setForeground(Color.WHITE);
        headerPanel.add(roleLabel, BorderLayout.EAST);
        
        return headerPanel;
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(45, 52, 64));
        sidebar.setPreferredSize(new Dimension(250, getHeight()));
        sidebar.setBorder(new EmptyBorder(20, 15, 20, 15));
        
        addLogo(sidebar);
        addMenuItems(sidebar);
        addBackupButton(sidebar);
        addLogoutButton(sidebar);
        
        return sidebar;
    }

    private void addLogo(JPanel sidebar) {
        JLabel logoLabel = new JLabel("MS CODEFORGE");
        logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        logoLabel.setForeground(new Color(0, 173, 181));
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoLabel.setBorder(new EmptyBorder(0, 0, 30, 0));
        sidebar.add(logoLabel);
    }

    private void addMenuItems(JPanel sidebar) {
        String[] menuItems = {"Clients", "Loans", "Payments", "Activities", "Employees", "Change Password"};
        for (String item : menuItems) {
            JButton menuButton = createMenuButton(item);
            sidebar.add(menuButton);
            sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        }
    }

    private void addBackupButton(JPanel sidebar) {
        sidebar.add(Box.createRigidArea(new Dimension(0, 20)));
        
        JButton backupBtn = createMenuButton("Backup Today");
        backupBtn.addActionListener(e -> generateBackup());
        sidebar.add(backupBtn);
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
    }

    private void addLogoutButton(JPanel sidebar) {
        sidebar.add(Box.createVerticalGlue());
        JButton logoutButton = createLogoutButton();
        sidebar.add(logoutButton);
    }

    private JButton createMenuButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(57, 62, 70));
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(12, 25, 12, 25));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        
        button.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { 
                button.setBackground(new Color(70, 76, 85)); 
            }
            @Override public void mouseExited(MouseEvent e) { 
                button.setBackground(new Color(57, 62, 70)); 
            }
        });
        
        button.addActionListener(e -> handleMenuClick(text));
        return button;
    }

    private JButton createLogoutButton() {
        JButton button = new JButton("LOGOUT");
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(255, 107, 107));
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(12, 25, 12, 25));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { 
                button.setBackground(new Color(255, 77, 77)); 
            }
            @Override public void mouseExited(MouseEvent e) { 
                button.setBackground(new Color(255, 107, 107)); 
            }
        });
        
        button.addActionListener(e -> {
            logActivity("Logout", "User logged out from system");
            ScreenManager.getInstance().showScreen(new LoginScreen());
        });
        return button;
    }

    private void styleButton(JButton button, Color bgColor, Color hoverColor) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(8, 15, 8, 15));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { button.setBackground(hoverColor); }
            public void mouseExited(MouseEvent e) { button.setBackground(bgColor); }
        });
    }

    private JPanel createLegendPanel() {
        JPanel legendPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        legendPanel.setBackground(new Color(57, 62, 70));
        legendPanel.setBorder(new EmptyBorder(15, 0, 0, 0));
        
        Color[] colors = {
            new Color(0, 173, 181),    // Total Clients - Teal
            new Color(97, 218, 121),   // Active Clients - Green
            new Color(255, 107, 107),  // Due Clients - Red
            new Color(255, 159, 67)    // Pending - Orange
        };
        
        String[] labels = {"Total Clients", "Active Clients", "Due Clients", "Pending Disbursement"};
        
        for (int i = 0; i < labels.length; i++) {
            JPanel legendItem = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
            legendItem.setBackground(new Color(57, 62, 70));
            
            JLabel colorLabel = new JLabel("■");
            colorLabel.setForeground(colors[i]);
            colorLabel.setFont(new Font("Arial", Font.BOLD, 16));
            
            JLabel textLabel = new JLabel(labels[i]);
            textLabel.setForeground(Color.WHITE);
            textLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            
            legendItem.add(colorLabel);
            legendItem.add(textLabel);
            legendPanel.add(legendItem);
        }
        
        return legendPanel;
    }

    private void handleMenuClick(String menuItem) {
        String menuText = menuItem.substring(menuItem.indexOf(" ") + 1).trim();
        
        switch (menuText) {
            case "Clients":
                ScreenManager.getInstance().showScreen(new ClientsScreen(employeeId, "admin"));
                break;
            case "Loans":
                ScreenManager.getInstance().showScreen(new LoansScreen(employeeId, "admin"));
                break;
            case "Payments":
                ScreenManager.getInstance().showScreen(new PaymentsScreen(employeeId, "admin"));
                break;
            case "Activities":
                ScreenManager.getInstance().showScreen(new ActivitiesScreen(employeeId, "admin"));
                break;
            case "Employees":
                ScreenManager.getInstance().showScreen(new EmployeesScreen(employeeId, "admin"));
                break;
            case "Change Password":
                ScreenManager.getInstance().showScreen(new ChangePasswordScreen(employeeId, "admin", employeeName));
                break;
        }
        logActivity("Navigation", "Accessed " + menuText + " section");
    }

    private void loadDashboardStats() {
        SwingWorker<Map<String, Integer>, Void> worker = new SwingWorker<Map<String, Integer>, Void>() {
            @Override
            protected Map<String, Integer> doInBackground() throws Exception {
                return fetchDashboardStats();
            }

            @Override
            protected void done() {
                try {
                    Map<String, Integer> stats = get();
                    updateDoughnutChart(stats);
                } catch (Exception ex) {
                    showDatabaseError("Error loading dashboard statistics");
                }
            }
        };
        worker.execute();
    }

    private Map<String, Integer> fetchDashboardStats() {
        Map<String, Integer> stats = new HashMap<>();
        Connection conn = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            if (conn == null || conn.isClosed()) {
                System.err.println("Database connection is closed or null");
                return stats;
            }

            // Total Clients
            stats.put("totalClients", getCount(conn, "SELECT COUNT(*) FROM clients"));
            
            // Active Clients (clients with approved/active loans)
            stats.put("activeClients", getCount(conn, 
                "SELECT COUNT(DISTINCT client_id) FROM loans WHERE status IN ('Active', 'Approved')"));
            
            // Due Clients (clients with overdue payments)
            stats.put("dueClients", getCount(conn,
                "SELECT COUNT(DISTINCT l.client_id) FROM loans l " +
                "JOIN loan_payments p ON l.loan_id = p.loan_id " +
                "WHERE p.status = 'Overdue' AND p.scheduled_payment_date < CURDATE()"));
            
            // Pending Disbursement (pending loans)
            stats.put("pendingDisbursement", getCount(conn, 
                "SELECT COUNT(*) FROM loans WHERE status = 'Pending'"));
                
        } catch (SQLException ex) {
            System.err.println("SQL Error in fetchDashboardStats: " + ex.getMessage());
            ex.printStackTrace();
        }
        
        return stats;
    }

    private int getCount(Connection conn, String sql) throws SQLException {
        if (conn == null || conn.isClosed()) {
            System.err.println("Connection is closed in getCount");
            return 0;
        }
        
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) {
            System.err.println("Error in getCount: " + e.getMessage());
            throw e;
        }
    }

    private void updateDoughnutChart(Map<String, Integer> stats) {
        int totalClients = stats.getOrDefault("totalClients", 0);
        int activeClients = stats.getOrDefault("activeClients", 0);
        int dueClients = stats.getOrDefault("dueClients", 0);
        int pendingDisbursement = stats.getOrDefault("pendingDisbursement", 0);
        
        doughnutChart.setData(new int[]{totalClients, activeClients, dueClients, pendingDisbursement});
    }

    private void startAnimation() {
        animationTimer = new Timer(16, e -> { // ~60 FPS
            animationProgress += 0.05f;
            if (animationProgress >= 1f) {
                animationProgress = 1f;
                animationTimer.stop();
            }
            doughnutChart.setAnimationProgress(animationProgress);
            doughnutChart.repaint();
        });
        animationTimer.start();
    }

    private void refreshDashboard() {
        animationProgress = 0f;
        startAnimation();
        loadDashboardStats();
        logActivity("Dashboard Refresh", "Refreshed dashboard statistics");
    }

    private void generateBackup() {
        SwingWorker<File, Void> worker = new SwingWorker<File, Void>() {
            @Override
            protected File doInBackground() throws Exception {
                return createDailyBackup();
            }

            @Override
            protected void done() {
                try {
                    File backupFile = get();
                    if (backupFile != null && backupFile.exists()) {
                        // Ask user if they want to open the file location
                        int option = JOptionPane.showConfirmDialog(
                            AdminDashboard.this,
                            "Backup created successfully!\n\n" +
                            "File: " + backupFile.getName() + "\n" +
                            "Location: " + backupFile.getParent() + "\n\n" +
                            "Would you like to open the backup location?",
                            "Backup Complete",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.INFORMATION_MESSAGE
                        );
                        
                        if (option == JOptionPane.YES_OPTION) {
                            // Open file explorer to show the backup file
                            Desktop.getDesktop().open(backupFile.getParentFile());
                        }
                    } else {
                        showError("Failed to create backup file");
                    }
                } catch (Exception ex) {
                    showError("Error creating backup: " + ex.getMessage());
                }
            }
        };
        worker.execute();
    }

    private File createDailyBackup() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String timestamp = dateFormat.format(new Date());
        String fileName = "MSCODEFORGE_BACKUP_" + timestamp + ".txt";
        
        // Create backup directory on desktop
        String userHome = System.getProperty("user.home");
        File backupDir = new File(userHome + "/Desktop/MSCODEFORGE_Backups");
        if (!backupDir.exists()) {
            backupDir.mkdirs();
        }
        
        File backupFile = new File(backupDir, fileName);
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(backupFile));
             Connection conn = DatabaseConnection.getConnection()) {
            
            if (conn == null || conn.isClosed()) {
                return null;
            }
            
            // Write header
            writer.println("MS CODEFORGE - DAILY ACTIVITIES BACKUP");
            writer.println("Generated on: " + new Date());
            writer.println("Generated by: " + employeeName + " (ID: " + employeeId + ")");
            writer.println("==============================================");
            writer.println();
            
            // Backup Today's Activities
            writer.println("TODAY'S ACTIVITIES:");
            writer.println("===================");
            backupTodaysActivities(writer, conn);
            writer.println();
            
            // Backup Client Summary
            writer.println("CLIENT SUMMARY:");
            writer.println("===============");
            backupClientSummary(writer, conn);
            writer.println();
            
            // Backup Loan Summary
            writer.println("LOAN SUMMARY:");
            writer.println("=============");
            backupLoanSummary(writer, conn);
            writer.println();
            
            // Backup Payment Summary
            writer.println("PAYMENT SUMMARY:");
            writer.println("================");
            backupPaymentSummary(writer, conn);
            
            // Log the backup creation
            logActivity("Backup Created", "Created daily backup: " + fileName);
            
            return backupFile;
            
        } catch (Exception ex) {
            System.err.println("Error creating backup: " + ex.getMessage());
            return null;
        }
    }

    private void backupTodaysActivities(PrintWriter writer, Connection conn) throws SQLException {
        String sql = "SELECT employee_name, action, details, action_date " +
                    "FROM audit_logs " +
                    "WHERE DATE(action_date) = CURDATE() " +
                    "ORDER BY action_date DESC";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            int count = 0;
            while (rs.next()) {
                count++;
                writer.printf("%s - %s: %s%n", 
                    rs.getTimestamp("action_date"),
                    rs.getString("employee_name"),
                    rs.getString("action") + " - " + rs.getString("details")
                );
            }
            
            if (count == 0) {
                writer.println("No activities recorded today.");
            } else {
                writer.println("Total activities today: " + count);
            }
        }
    }

    private void backupClientSummary(PrintWriter writer, Connection conn) throws SQLException {
        String sql = "SELECT COUNT(*) as total_clients, " +
                    "COUNT(DISTINCT created_by) as created_by_employees, " +
                    "MAX(created_at) as latest_client " +
                    "FROM clients";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                writer.println("Total Clients: " + rs.getInt("total_clients"));
                writer.println("Registered by: " + rs.getInt("created_by_employees") + " employees");
                writer.println("Latest Client Added: " + rs.getTimestamp("latest_client"));
            }
        }
    }

    private void backupLoanSummary(PrintWriter writer, Connection conn) throws SQLException {
        String sql = "SELECT " +
                    "COUNT(*) as total_loans, " +
                    "SUM(CASE WHEN status = 'Pending' THEN 1 ELSE 0 END) as pending_loans, " +
                    "SUM(CASE WHEN status IN ('Approved', 'Active') THEN 1 ELSE 0 END) as active_loans, " +
                    "SUM(CASE WHEN status = 'Rejected' THEN 1 ELSE 0 END) as rejected_loans, " +
                    "SUM(CASE WHEN status = 'Closed' THEN 1 ELSE 0 END) as closed_loans, " +
                    "SUM(amount) as total_amount, " +
                    "SUM(outstanding_balance) as total_outstanding " +
                    "FROM loans";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                writer.println("Total Loans: " + rs.getInt("total_loans"));
                writer.println("Pending: " + rs.getInt("pending_loans"));
                writer.println("Active: " + rs.getInt("active_loans"));
                writer.println("Rejected: " + rs.getInt("rejected_loans"));
                writer.println("Closed: " + rs.getInt("closed_loans"));
                writer.printf("Total Amount: ZMW %,.2f%n", rs.getDouble("total_amount"));
                writer.printf("Outstanding: ZMW %,.2f%n", rs.getDouble("total_outstanding"));
            }
        }
    }

    private void backupPaymentSummary(PrintWriter writer, Connection conn) throws SQLException {
        String sql = "SELECT " +
                    "COUNT(*) as total_payments, " +
                    "SUM(amount) as total_amount, " +
                    "SUM(CASE WHEN status = 'Pending' THEN 1 ELSE 0 END) as pending_payments, " +
                    "SUM(CASE WHEN status = 'Approved' THEN 1 ELSE 0 END) as approved_payments, " +
                    "SUM(CASE WHEN status = 'Rejected' THEN 1 ELSE 0 END) as rejected_payments " +
                    "FROM payment_receipts " +
                    "WHERE DATE(created_at) = CURDATE()";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                writer.println("Today's Payments: " + rs.getInt("total_payments"));
                writer.printf("Today's Amount: ZMW %,.2f%n", rs.getDouble("total_amount"));
                writer.println("Pending: " + rs.getInt("pending_payments"));
                writer.println("Approved: " + rs.getInt("approved_payments"));
                writer.println("Rejected: " + rs.getInt("rejected_payments"));
            }
        }
    }

    // ANIMATED DOUGHNUT CHART CLASS
    private class DoughnutChart extends JPanel {
        private int[] data = new int[4];
        private float animationProgress = 0f;
        private final Color[] colors = {
            new Color(0, 173, 181),    // Teal
            new Color(97, 218, 121),   // Green
            new Color(255, 107, 107),  // Red
            new Color(255, 159, 67)    // Orange
        };

        public DoughnutChart() {
            setPreferredSize(new Dimension(400, 300));
            setBackground(new Color(57, 62, 70));
        }

        public void setData(int[] newData) {
            this.data = newData;
            repaint();
        }

        public void setAnimationProgress(float progress) {
            this.animationProgress = progress;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int width = getWidth();
            int height = getHeight();
            int diameter = Math.min(width, height) - 40;
            int x = (width - diameter) / 2;
            int y = (height - diameter) / 2;

            // Calculate total for percentages
            int total = 0;
            for (int value : data) {
                total += value;
            }

            if (total == 0) {
                // Draw empty state
                g2d.setColor(new Color(100, 100, 100));
                g2d.fillOval(x, y, diameter, diameter);
                
                g2d.setColor(new Color(57, 62, 70));
                g2d.fillOval(x + 20, y + 20, diameter - 40, diameter - 40);
                
                // No data text
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Segoe UI", Font.BOLD, 16));
                String noData = "No Data Available";
                FontMetrics fm = g2d.getFontMetrics();
                int textWidth = fm.stringWidth(noData);
                g2d.drawString(noData, (width - textWidth) / 2, height / 2);
                return;
            }

            // Draw animated segments
            float startAngle = 90; // Start from top
            for (int i = 0; i < data.length; i++) {
                float extent = (360 * data[i] / total) * animationProgress;
                
                g2d.setColor(colors[i]);
                g2d.fill(new Arc2D.Float(x, y, diameter, diameter, startAngle, extent, Arc2D.PIE));
                
                startAngle += extent;
            }

            // Draw inner circle for doughnut effect
            g2d.setColor(new Color(57, 62, 70));
            int innerDiameter = diameter / 2;
            int innerX = (width - innerDiameter) / 2;
            int innerY = (height - innerDiameter) / 2;
            g2d.fillOval(innerX, innerY, innerDiameter, innerDiameter);

            // Draw center text
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Segoe UI", Font.BOLD, 18));
            String totalText = String.format("%,d", total);
            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(totalText);
            g2d.drawString(totalText, (width - textWidth) / 2, height / 2 - 10);
            
            g2d.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            String label = "Total Records";
            textWidth = fm.stringWidth(label);
            g2d.drawString(label, (width - textWidth) / 2, height / 2 + 15);
        }
    }

    private void logActivity(String action, String details) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            if (conn == null || conn.isClosed()) {
                System.err.println("Connection closed in logActivity");
                return;
            }
            
            String sql = "INSERT INTO audit_logs (employee_id, employee_name, action, details) " +
                        "SELECT ?, name, ?, ? FROM employees WHERE employee_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, employeeId);
            stmt.setString(2, action);
            stmt.setString(3, details);
            stmt.setInt(4, employeeId);
            stmt.executeUpdate();
        } catch (SQLException ex) {
            System.err.println("Audit Log Error: " + ex.getMessage());
        }
    }

    private void showDatabaseError(String message) {
        JOptionPane.showMessageDialog(this, message, "Database Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}    