import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;

public class ScreenManager {
    private static ScreenManager instance;
    private JFrame mainFrame;
    private Map<String, JPanel> screens;
    private JPanel currentScreen;

    private ScreenManager() {
        initializeMainFrame();
        screens = new HashMap<>();
    }

    public static synchronized ScreenManager getInstance() {
        if (instance == null) {
            instance = new ScreenManager();
        }
        return instance;
    }

    private void initializeMainFrame() {
        mainFrame = new JFrame("MS CODEFORGE - Loan Management System");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(1200, 800);
        mainFrame.setMinimumSize(new Dimension(1024, 768));
        mainFrame.setLocationRelativeTo(null);
        
        mainFrame.getContentPane().setLayout(new BorderLayout());
        
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("C:\\\\Users\\\\obvio\\\\Desktop\\\\My-JavaRoadMap.Mweemba\\\\MS CodeForge\\\\res\\\\MS CodeForge.png"));
            mainFrame.setIconImage(icon.getImage());
        } catch (Exception e) {
            // Icon not found, continue without it
        }
        
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");
    }

    public void showScreen(JPanel screen) {
        if (currentScreen != null) {
            mainFrame.getContentPane().remove(currentScreen);
        }
        
        currentScreen = screen;
        mainFrame.getContentPane().add(currentScreen, BorderLayout.CENTER);
        
        mainFrame.revalidate();
        mainFrame.repaint();
        
        updateWindowTitle(screen);
        
        if (!mainFrame.isVisible()) {
            mainFrame.setVisible(true);
        }
    }

    private void updateWindowTitle(JPanel screen) {
        String title = "MS CODEFORGE - Loan Management System";
        
        if (screen instanceof AdminDashboard) {
            title += " - Admin Dashboard";
        } else if (screen instanceof EmployeeDashboard) {
            title += " - Employee Dashboard";
        } else if (screen instanceof ClientsScreen) {
            title += " - Client Management";
        } else if (screen instanceof LoansScreen) {
            title += " - Loan Management";
        } else if (screen instanceof PaymentsScreen) {
            title += " - Payment Management";
        } else if (screen instanceof ChangePasswordScreen) {
            title += " - Change Password";
        }
        
        mainFrame.setTitle(title);
    }

    public void addScreen(String name, JPanel screen) {
        screens.put(name, screen);
    }

    public JPanel getScreen(String name) {
        return screens.get(name);
    }

    public JFrame getMainFrame() {
        return mainFrame;
    }

    public JPanel getCurrentScreen() {
        return currentScreen;
    }

    public static void showErrorMessage(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void showSuccessMessage(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    public static boolean showConfirmationDialog(Component parent, String message) {
        int result = JOptionPane.showConfirmDialog(parent, message, "Confirmation", 
            JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        return result == JOptionPane.YES_OPTION;
    }

    public void refreshCurrentScreen() {
        if (currentScreen != null) {
            mainFrame.revalidate();
            mainFrame.repaint();
        }
    }

    public static void centerDialog(JDialog dialog) {
        dialog.setLocationRelativeTo(ScreenManager.getInstance().getMainFrame());
    }

    public static Dimension getScreenSize() {
        return Toolkit.getDefaultToolkit().getScreenSize();
    }

    public static JDialog createLoadingDialog(Component parent, String message) {
        JDialog loadingDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(parent), true);
        loadingDialog.setTitle("Please Wait");
        loadingDialog.setLayout(new BorderLayout());
        loadingDialog.setSize(300, 150);
        loadingDialog.setResizable(false);
        loadingDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel messageLabel = new JLabel(message, SwingConstants.CENTER);
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        
        contentPanel.add(messageLabel, BorderLayout.CENTER);
        contentPanel.add(progressBar, BorderLayout.SOUTH);
        
        loadingDialog.add(contentPanel);
        loadingDialog.setLocationRelativeTo(parent);
        
        return loadingDialog;
    }
}