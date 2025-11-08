// PasswordGeneratorGUI.java
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;

public class PasswordGeneratorGUI extends JFrame {
    private final JTextField usernameField = new JTextField(20);
    private final JSpinner lengthSpinner = new JSpinner(new SpinnerNumberModel(16, 4, 128, 1));
    private final JCheckBox lowerBox = new JCheckBox("Lowercase", true);
    private final JCheckBox upperBox = new JCheckBox("Uppercase", true);
    private final JCheckBox digitsBox = new JCheckBox("Digits", true);
    private final JCheckBox symbolsBox = new JCheckBox("Symbols", true);
    private final JTextArea resultArea = new JTextArea(3, 30);
    private final JButton generateBtn = new JButton("Generate");
    private final JButton saveBtn = new JButton("Save to DB");
    private static final String DB_URL = "jdbc:sqlite:passwords.db";

    public PasswordGeneratorGUI() {
        super("Password Generator");
        setupUI();
        ensureDatabase();
        attachHandlers();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
    }

    private void setupUI() {
        JPanel top = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6,6,6,6);
        c.gridx = 0; c.gridy = 0; c.anchor = GridBagConstraints.LINE_END;
        top.add(new JLabel("Username:"), c);
        c.gridx = 1; c.anchor = GridBagConstraints.LINE_START;
        top.add(usernameField, c);

        c.gridx = 0; c.gridy = 1; c.anchor = GridBagConstraints.LINE_END;
        top.add(new JLabel("Length:"), c);
        c.gridx = 1; c.anchor = GridBagConstraints.LINE_START;
        top.add(lengthSpinner, c);

        JPanel boxes = new JPanel(new FlowLayout(FlowLayout.LEFT));
        boxes.add(lowerBox); boxes.add(upperBox); boxes.add(digitsBox); boxes.add(symbolsBox);
        c.gridx = 0; c.gridy = 2; c.gridwidth = 2;
        top.add(boxes, c);

        c.gridy = 3;
        top.add(generateBtn, c);

        c.gridy = 4;
        resultArea.setLineWrap(true);
        resultArea.setWrapStyleWord(true);
        resultArea.setEditable(false);
        top.add(new JScrollPane(resultArea), c);

        c.gridy = 5;
        top.add(saveBtn, c);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton openViewer = new JButton("Open Viewer");
        openViewer.addActionListener(e -> {
            SwingUtilities.invokeLater(() -> new PasswordViewer().setVisible(true));
        });
        bottom.add(openViewer);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(top, BorderLayout.CENTER);
        getContentPane().add(bottom, BorderLayout.SOUTH);
    }

    private void attachHandlers() {
        generateBtn.addActionListener((ActionEvent e) -> {
            try {
                int len = (Integer) lengthSpinner.getValue();
                boolean useLower = lowerBox.isSelected();
                boolean useUpper = upperBox.isSelected();
                boolean useDigits = digitsBox.isSelected();
                boolean useSymbols = symbolsBox.isSelected();

                String pw = PasswordGenerator.generate(len, useLower, useUpper, useDigits, useSymbols);
                resultArea.setText(pw);
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Unexpected error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        saveBtn.addActionListener((ActionEvent e) -> {
            String username = usernameField.getText().trim();
            String password = resultArea.getText().trim();

            if (username.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Username cannot be empty.", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Generate a password first.", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // attempt to save ensuring uniqueness
            try (Connection conn = DriverManager.getConnection(DB_URL)) {
                // check if username exists
                PreparedStatement check = conn.prepareStatement("SELECT COUNT(*) FROM credentials WHERE username = ?");
                check.setString(1, username);
                ResultSet rs = check.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) {
                    JOptionPane.showMessageDialog(this, "Username already exists. Choose a different username.", "Duplicate", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                rs.close();
                check.close();

                PreparedStatement ins = conn.prepareStatement(
                        "INSERT INTO credentials (username, password, length, lowercase, uppercase, digits, symbols, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, datetime('now'))");
                ins.setString(1, username);
                ins.setString(2, password);
                ins.setInt(3, Integer.parseInt(lengthSpinner.getValue().toString()));
                ins.setBoolean(4, lowerBox.isSelected());
                ins.setBoolean(5, upperBox.isSelected());
                ins.setBoolean(6, digitsBox.isSelected());
                ins.setBoolean(7, symbolsBox.isSelected());
                ins.executeUpdate();
                ins.close();

                JOptionPane.showMessageDialog(this, "Saved successfully.", "Saved", JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "DB error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private void ensureDatabase() {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            Statement st = conn.createStatement();
            st.execute("CREATE TABLE IF NOT EXISTS credentials (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "username TEXT UNIQUE NOT NULL," +
                    "password TEXT NOT NULL," +
                    "length INTEGER," +
                    "lowercase BOOLEAN," +
                    "uppercase BOOLEAN," +
                    "digits BOOLEAN," +
                    "symbols BOOLEAN," +
                    "created_at TEXT" +
                    ")");
            st.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Failed to initialize DB: " + ex.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        // Load SQLite JDBC (the driver registers itself)
        SwingUtilities.invokeLater(() -> {
            new PasswordGeneratorGUI().setVisible(true);
        });
    }
}
