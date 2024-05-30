package GUIBeneran;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.event.*;
import java.sql.*;

public class UserGUI extends JFrame {
    private JTextField nameField, emailField, ageField, cityField;
    private JButton addButton, displayButton, deleteData;
    private JTable table;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                UserGUI gui = new UserGUI();
                gui.createAndShowGUI();
            }
        });
    }

    public void createAndShowGUI() {

        JFrame frame = new JFrame("Data Entry Lucy In The Sky");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 500);
        frame.setLayout(null);

        JLabel nameLabel = new JLabel("Nama:");
        nameLabel.setBounds(20, 20, 80, 25);
        frame.add(nameLabel);

        nameField = new JTextField();
        nameField.setBounds(100, 20, 320, 25);
        frame.add(nameField);

        JLabel ageLabel = new JLabel("Umur:");
        ageLabel.setBounds(20, 50, 80, 25);
        frame.add(ageLabel);

        ageField = new JTextField();
        ageField.setBounds(100, 50, 320, 25);
        frame.add(ageField);

        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setBounds(20, 80, 80, 25);
        frame.add(emailLabel);

        emailField = new JTextField();
        emailField.setBounds(100, 80, 320, 25);
        frame.add(emailField);

        JLabel cityLabel = new JLabel("Kota Asal:");
        cityLabel.setBounds(20, 110, 80, 25);
        frame.add(cityLabel);

        cityField = new JTextField();
        cityField.setBounds(100, 110, 320, 25);
        frame.add(cityField);

        addButton = new JButton("Simpan");
        addButton.setBounds(100, 140, 80, 25);
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addUser();

            }

        });

        deleteData = new JButton("Hapus");
        deleteData.setBounds(200, 140, 80, 25);
        deleteData.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(UserGUI.this, "Pilih baris yang akan dihapus.", "Peringatan",
                            JOptionPane.WARNING_MESSAGE);
                } else {
                    int id = (int) table.getValueAt(selectedRow, 0);
                    deleteData(id);
                }
            }
        });
        frame.add(deleteData);

        frame.add(addButton);

        displayButton = new JButton("Display User");
        displayButton.setBounds(300, 140, 120, 25);
        displayButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                displayUsers();
            }
        });
        frame.add(displayButton);

        String[] columnNames = { "Nomor", "Nama", "Umur", "Email", "Kota Asal" };
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(20, 180, 740, 250);
        frame.add(scrollPane);

        frame.setVisible(true);
    }

    private void deleteData(int id) {
        int option = JOptionPane.showConfirmDialog(this, "Anda yakin ingin menghapus data ini?", "Konfirmasi",
                JOptionPane.YES_NO_OPTION);
        if (option == JOptionPane.YES_OPTION) {
            String sql = "DELETE FROM users WHERE id = ?";
            Connection conn = DatabaseConnection.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, id);
                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Data berhasil dihapus.");
                displayUsers();
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error deleting data.");
            }
        }
    }

    private void addUser() {
        String name = nameField.getText();
        String ageText = ageField.getText();
        String email = emailField.getText();
        String city = cityField.getText();

        int age;
        try {
            age = Integer.parseInt(ageText);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Umur harus berupa angka.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int option = JOptionPane
                .showConfirmDialog(
                        this, "Tambahkan pengguna dengan detail berikut?\nNama: " + name + "\nUmur: " + age
                                + "\nEmail: " + email + "\nKota Asal: " + city,
                        "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (option == JOptionPane.YES_OPTION) {
            Connection conn = DatabaseConnection.getConnection();
            if (conn != null) {
                try {
                    String query = "INSERT INTO users (name, age, email, city) VALUES (?, ?, ?, ?)";
                    PreparedStatement preparedStatement = conn.prepareStatement(query);
                    preparedStatement.setString(1, name);
                    preparedStatement.setInt(2, age);
                    preparedStatement.setString(3, email);
                    preparedStatement.setString(4, city);
                    preparedStatement.executeUpdate();
                    JOptionPane.showMessageDialog(this, "User added successfully.");
                } catch (SQLException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Error adding user.");
                }
            }
        }
    }

    private void displayUsers() {
        Connection conn = DatabaseConnection.getConnection();
        if (conn != null) {
            try {
                String query = "SELECT * FROM users";
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query);
                DefaultTableModel tableModel = (DefaultTableModel) table.getModel();
                tableModel.setRowCount(0);
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String name = rs.getString("name");
                    int age = rs.getInt("age");
                    String email = rs.getString("email");
                    String city = rs.getString("city");
                    tableModel.addRow(new Object[] { id, name, age, email, city });
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}