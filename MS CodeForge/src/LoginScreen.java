import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class LoginScreen extends JPanel {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JLabel imageLabel;

    public LoginScreen() {
        setLayout(new BorderLayout());
        setBackground(new Color(30, 35, 40));
        initUI();
    }

    private void initUI() {
        // Main container with gradient
        JPanel mainPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                Color color1 = new Color(30, 35, 40);
                Color color2 = new Color(45, 50, 60);
                GradientPaint gp = new GradientPaint(0, 0, color1, getWidth(), getHeight(), color2);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setOpaque(false);

        // Left Panel with Image/Logo
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(new Color(25, 30, 35));
        leftPanel.setPreferredSize(new Dimension(400, 0));
        leftPanel.setBorder(new EmptyBorder(40, 40, 40, 40));

        // Logo/Image section
        try {
            // Load the image
            ImageIcon originalIcon = new ImageIcon("C:\\Users\\obvio\\Desktop\\My-JavaRoadMap.Mweemba\\MS CodeForge\\res\\MS CodeForge.png");
            Image originalImage = originalIcon.getImage();
            
            // Scale the image to fit the panel while maintaining aspect ratio
            int maxWidth = 300;
            int maxHeight = 300;
            int scaledWidth = originalIcon.getIconWidth();
            int scaledHeight = originalIcon.getIconHeight();
            
            if (scaledWidth > maxWidth) {
                scaledHeight = (scaledHeight * maxWidth) / scaledWidth;
                scaledWidth = maxWidth;
            }
            if (scaledHeight > maxHeight) {
                scaledWidth = (scaledWidth * maxHeight) / scaledHeight;
                scaledHeight = maxHeight;
            }
            
            Image scaledImage = originalImage.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH);
            ImageIcon scaledIcon = new ImageIcon(scaledImage);
            
            imageLabel = new JLabel(scaledIcon, SwingConstants.CENTER);
        } catch (Exception e) {
            // Fallback to text if image fails to load
            imageLabel = new JLabel("MS CODEFORGE", SwingConstants.CENTER);
            imageLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
            imageLabel.setForeground(new Color(0, 180, 180));
        }
        
        imageLabel.setBorder(new EmptyBorder(50, 0, 30, 0));
        
        JLabel subtitle = new JLabel("Loan Management System", SwingConstants.CENTER);
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subtitle.setForeground(new Color(180, 180, 180));
        
        JPanel imageContainer = new JPanel(new BorderLayout());
        imageContainer.setBackground(new Color(25, 30, 35));
        imageContainer.add(imageLabel, BorderLayout.CENTER);
        imageContainer.add(subtitle, BorderLayout.SOUTH);
        
        leftPanel.add(imageContainer, BorderLayout.CENTER);

        // Right Panel with Login Form
        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setBackground(new Color(45, 50, 60));
        rightPanel.setBorder(new EmptyBorder(80, 60, 80, 60));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 0, 15, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Login Title
        JLabel loginLabel = new JLabel("SIGN IN", SwingConstants.CENTER);
        loginLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        loginLabel.setForeground(new Color(0, 180, 180));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        rightPanel.add(loginLabel, gbc);

        // Username
        JLabel usernameLabel = new JLabel("Username:", SwingConstants.LEFT);
        usernameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        usernameLabel.setForeground(new Color(180, 180, 180));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        rightPanel.add(usernameLabel, gbc);

        usernameField = new JTextField();
        styleTextField(usernameField);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        rightPanel.add(usernameField, gbc);

        // Password
        JLabel passwordLabel = new JLabel("Password:", SwingConstants.LEFT);
        passwordLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordLabel.setForeground(new Color(180, 180, 180));
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        rightPanel.add(passwordLabel, gbc);

        passwordField = new JPasswordField();
        styleTextField(passwordField);
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        rightPanel.add(passwordField, gbc);

        // Login Button
        loginButton = new JButton("LOGIN");
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        loginButton.setPreferredSize(new Dimension(320, 50));
        loginButton.setBackground(new Color(0, 150, 150));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setBorder(BorderFactory.createEmptyBorder());
        loginButton.addActionListener(e -> performLogin());
        
        // Hover effects
        loginButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                loginButton.setBackground(new Color(0, 170, 170));
            }
            public void mouseExited(MouseEvent e) {
                loginButton.setBackground(new Color(0, 150, 150));
            }
            public void mousePressed(MouseEvent e) {
                loginButton.setBackground(new Color(0, 130, 130));
            }
        });

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        rightPanel.add(loginButton, gbc);

        // Add panels to main
        mainPanel.add(leftPanel, BorderLayout.WEST);
        mainPanel.add(rightPanel, BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);

        // Footer
        JLabel footerLabel = new JLabel("© 2025 MS CodeForge Ltd - Secure & Reliable Loan Solutions", SwingConstants.CENTER);
        footerLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        footerLabel.setForeground(new Color(120, 120, 120));
        footerLabel.setBorder(new EmptyBorder(0, 0, 20, 0));
        add(footerLabel, BorderLayout.SOUTH);
    }

    private void styleTextField(JComponent field) {
        field.setPreferredSize(new Dimension(320, 45));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBackground(new Color(60, 65, 70));
        field.setForeground(Color.WHITE);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(80, 85, 90)),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
    }

    private void performLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both fields", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Disable login button during authentication
        loginButton.setEnabled(false);
        loginButton.setText("AUTHENTICATING...");

        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                try (Connection conn = DatabaseConnection.getConnection()) {
                    String sql = "SELECT * FROM employees WHERE name = ? AND password = ? AND is_active = TRUE";
                    PreparedStatement stmt = conn.prepareStatement(sql);
                    stmt.setString(1, username);
                    stmt.setString(2, password);
                    
                    ResultSet rs = stmt.executeQuery();
                    if (rs.next()) {
                        int id = rs.getInt("employee_id");
                        String name = rs.getString("name");
                        String role = rs.getString("role");
                        
                        // Log login activity
                        logActivity(id, "Login", "User logged into system");
                        
                        SwingUtilities.invokeLater(() -> {
                            if ("admin".equals(role)) {
                                ScreenManager.getInstance().showScreen(new AdminDashboard(id, name));
                            } else {
                                ScreenManager.getInstance().showScreen(new EmployeeDashboard(id, name));
                            }
                        });
                        return true;
                    } else {
                        return false;
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    return false;
                }
            }

            @Override
            protected void done() {
                try {
                    if (!get()) {
                        JOptionPane.showMessageDialog(LoginScreen.this, 
                            "Invalid credentials or account inactive", 
                            "Login Failed", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(LoginScreen.this, 
                        "Database connection error", 
                        "System Error", JOptionPane.ERROR_MESSAGE);
                } finally {
                    loginButton.setEnabled(true);
                    loginButton.setText("LOGIN");
                }
            }
        };
        worker.execute();
    }

    private void logActivity(int employeeId, String action, String details) {
        try (Connection conn = DatabaseConnection.getConnection()) {
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
}