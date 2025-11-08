// PasswordViewer.java
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.FileWriter;
import java.sql.*;
import java.util.Vector;

public class PasswordViewer extends JFrame {
    private final JTable table = new JTable();
    private final DefaultTableModel model = new DefaultTableModel();
    private static final String DB_URL = "jdbc:sqlite:passwords.db";

    public PasswordViewer() {
        super("Saved Passwords Viewer");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 400);
        setLocationRelativeTo(null);

        model.setColumnIdentifiers(new Object[]{"ID", "Username", "Password", "Length", "Lower", "Upper", "Digits", "Symbols", "Created At"});
        table.setModel(model);
        table.setAutoCreateRowSorter(true);

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton refresh = new JButton("Refresh");
        JButton delete = new JButton("Delete Selected");
        JButton export = new JButton("Export CSV");
        top.add(refresh);
        top.add(delete);
        top.add(export);

        refresh.addActionListener(e -> loadData());
        delete.addActionListener(e -> deleteSelected());
        export.addActionListener(e -> exportCSV());

        getContentPane().add(top, BorderLayout.NORTH);
        getContentPane().add(new JScrollPane(table), BorderLayout.CENTER);

        loadData();
    }

    private void loadData() {
        model.setRowCount(0);
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT id, username, password, length, lowercase, uppercase, digits, symbols, created_at FROM credentials ORDER BY id DESC");
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("id"));
                row.add(rs.getString("username"));
                row.add(rs.getString("password"));
                row.add(rs.getInt("length"));
                row.add(rs.getBoolean("lowercase"));
                row.add(rs.getBoolean("uppercase"));
                row.add(rs.getBoolean("digits"));
                row.add(rs.getBoolean("symbols"));
                row.add(rs.getString("created_at"));
                model.addRow(row);
            }
            rs.close();
            st.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "DB error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSelected() {
        int[] rows = table.getSelectedRows();
        if (rows.length == 0) {
            JOptionPane.showMessageDialog(this, "Select rows to delete.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Delete selected rows? This cannot be undone.", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            for (int viewRow : rows) {
                int modelRow = table.convertRowIndexToModel(viewRow);
                Object idObj = model.getValueAt(modelRow, 0);
                if (idObj != null) {
                    int id = (Integer) idObj;
                    PreparedStatement ps = conn.prepareStatement("DELETE FROM credentials WHERE id = ?");
                    ps.setInt(1, id);
                    ps.executeUpdate();
                    ps.close();
                }
            }
            loadData();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "DB delete error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void exportCSV() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Save CSV");
        int choice = chooser.showSaveDialog(this);
        if (choice != JFileChooser.APPROVE_OPTION) return;
        try (FileWriter fw = new FileWriter(chooser.getSelectedFile())) {
            // header
            for (int i = 0; i < model.getColumnCount(); i++) {
                fw.append(model.getColumnName(i));
                if (i < model.getColumnCount() - 1) fw.append(',');
            }
            fw.append('\n');

            for (int r = 0; r < model.getRowCount(); r++) {
                for (int c = 0; c < model.getColumnCount(); c++) {
                    Object val = model.getValueAt(r, c);
                    fw.append(val == null ? "" : val.toString().replaceAll("\"", "\"\""));
                    if (c < model.getColumnCount() - 1) fw.append(',');
                }
                fw.append('\n');
            }
            JOptionPane.showMessageDialog(this, "Exported successfully.", "Export", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Export error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // convenience main to run viewer stand-alone
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new PasswordViewer().setVisible(true);
        });
    }
}
