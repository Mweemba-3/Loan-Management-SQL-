import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Arc2D;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class EmployeeDashboard extends JPanel {
    private int employeeId;
    private String employeeName;
    private DoughnutChart doughnutChart;
    private JLabel welcomeLabel;
    private Timer animationTimer;
    private float animationProgress = 0f;

    public EmployeeDashboard(int employeeId, String employeeName) {
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        initUI();
        loadDashboardStats();
        startAnimation();
        logActivity("Dashboard Access", "Accessed employee dashboard");
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(new Color(34, 40, 49));
        
        JPanel sidebar = createSidebar();
        add(sidebar, BorderLayout.WEST);
        
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(new Color(45, 52, 64));
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JPanel headerPanel = createHeaderPanel();
        contentPanel.add(headerPanel, BorderLayout.NORTH);
        
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(new Color(45, 52, 64));
        
        JPanel welcomePanel = new JPanel(new BorderLayout());
        welcomePanel.setBackground(new Color(45, 52, 64));
        welcomePanel.setBorder(new EmptyBorder(0, 0, 20, 0));
        
        welcomeLabel = new JLabel("Welcome, " + employeeName + "!");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        welcomeLabel.setForeground(new Color(200, 200, 200));
        
        JButton refreshBtn = new JButton("Refresh Dashboard");
        styleButton(refreshBtn, new Color(70, 130, 180), new Color(60, 120, 170));
        refreshBtn.addActionListener(e -> refreshDashboard());
        
        welcomePanel.add(welcomeLabel, BorderLayout.WEST);
        welcomePanel.add(refreshBtn, BorderLayout.EAST);
        
        JPanel chartPanel = new JPanel(new BorderLayout());
        chartPanel.setBackground(new Color(57, 62, 70));
        chartPanel.setBorder(new CompoundBorder(
            new LineBorder(new Color(70, 70, 70), 2),
            new EmptyBorder(20, 20, 20, 20)
        ));
        
        doughnutChart = new DoughnutChart();
        chartPanel.add(doughnutChart, BorderLayout.CENTER);
        
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
        
        JLabel titleLabel = new JLabel("EMPLOYEE DASHBOARD");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(new Color(0, 173, 181));
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        JLabel roleLabel = new JLabel("Role: Employee | ID: " + employeeId);
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
        String[] menuItems = {"Clients", "Loans", "Payments", "Change Password"};
        for (String item : menuItems) {
            JButton menuButton = createMenuButton(item);
            sidebar.add(menuButton);
            sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        }
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
            new Color(97, 218, 121),   // Active Loans - Green
            new Color(255, 107, 107),  // Due Payments - Red
            new Color(255, 159, 67)    // Pending - Orange
        };
        
        String[] labels = {"Total Clients", "Active Loans", "Due Payments", "Pending Loans"};
        
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
                ScreenManager.getInstance().showScreen(new ClientsScreen(employeeId, "employee"));
                break;
            case "Loans":
                ScreenManager.getInstance().showScreen(new LoansScreen(employeeId, "employee"));
                break;
            case "Payments":
                ScreenManager.getInstance().showScreen(new PaymentsScreen(employeeId, "employee"));
                break;
            case "Change Password":
                ScreenManager.getInstance().showScreen(new ChangePasswordScreen(employeeId, "employee", employeeName));
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
                    System.err.println("Error loading dashboard stats: " + ex.getMessage());
                    showDatabaseError("Error loading dashboard statistics: " + ex.getMessage());
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

            // Total Clients (ALL clients in the system)
            stats.put("totalClients", getCount(conn, "SELECT COUNT(*) FROM clients"));
            
            // Active Loans (ALL active loans in the system)
            stats.put("activeLoans", getCount(conn, 
                "SELECT COUNT(*) FROM loans WHERE status IN ('Active', 'Approved')"));
            
            // Due Payments (ALL due payments in the system)
            stats.put("duePayments", getCount(conn,
                "SELECT COUNT(*) FROM loan_payments " +
                "WHERE status = 'Overdue' AND scheduled_payment_date < CURDATE()"));
            
            // Pending Loans (ALL pending loans in the system)
            stats.put("pendingLoans", getCount(conn, 
                "SELECT COUNT(*) FROM loans WHERE status = 'Pending'"));
                
        } catch (SQLException ex) {
            System.err.println("SQL Error in fetchDashboardStats: " + ex.getMessage());
            ex.printStackTrace();
        } finally {
            // DON'T close the connection here - let the connection pool handle it
            // DatabaseConnection.closeConnection(conn);
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
        int activeLoans = stats.getOrDefault("activeLoans", 0);
        int duePayments = stats.getOrDefault("duePayments", 0);
        int pendingLoans = stats.getOrDefault("pendingLoans", 0);
        
        doughnutChart.setData(new int[]{totalClients, activeLoans, duePayments, pendingLoans});
    }

    private void startAnimation() {
        animationTimer = new Timer(16, e -> {
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

    // DOUGHNUT CHART CLASS - SAME AS ADMIN
    private class DoughnutChart extends JPanel {
        private int[] data = new int[4];
        private float animationProgress = 0f;
        private final Color[] colors = {
            new Color(0, 173, 181),    // Teal - Total Clients
            new Color(97, 218, 121),   // Green - Active Loans
            new Color(255, 107, 107),  // Red - Due Payments
            new Color(255, 159, 67)    // Orange - Pending Loans
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
        } finally {
            // DON'T close connection here
        }
    }

    private void showDatabaseError(String message) {
        JOptionPane.showMessageDialog(this, message, "Database Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}