import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class MedicalAppUI extends JFrame {

    // ── Fields ────────────────────────────────────────────────────────────────
    private String role;

    // Patients
    private JTable patientTable;
    private DefaultTableModel patientModel;
    private JTextField tfId, tfNom, tfPrenom, tfTel;
    private JButton btnDelete;

    // Doctors
    private JTable doctorTable;
    private DefaultTableModel doctorModel;
    private JTextField tfDoctorId, tfDoctorNom, tfDoctorPrenom, tfDoctorSpec, tfDoctorTel;
    private JButton btnDeleteDoctor;

    // Appointments
    private JTable rdvTable;
    private DefaultTableModel rdvModel;
    private JTextField tfRdvId, tfRdvPatient, tfRdvMedecin, tfRdvDate, tfRdvHeure, tfRdvStatut;
    private JButton btnDeleteRdv;

    // ── Constructor ───────────────────────────────────────────────────────────
    public MedicalAppUI(String role) {
        this.role = role;
        setTitle("Medical Clinic Management");
        setSize(950, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // ── Top bar showing role ──────────────────────────────────────────────
        String roleText;
        Color roleColor;
        switch (role) {
            case "owner":     roleText = "👑 Owner — Full Access";        roleColor = new Color(0x8E44AD); break;
            case "doctor":    roleText = "🩺 Doctor — View Only";          roleColor = new Color(0x2980B9); break;
            default:          roleText = "📋 Assistant — Limited Access";  roleColor = new Color(0xE67E22); break;
        }

        JLabel lblRole = new JLabel(roleText, SwingConstants.RIGHT);
        lblRole.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblRole.setForeground(roleColor);
        lblRole.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 15));
        add(lblRole, BorderLayout.NORTH);

        // ── Tabs ──────────────────────────────────────────────────────────────
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Patients",     buildPatientsPanel());
        tabs.addTab("Doctors",      buildDoctorsPanel());
        tabs.addTab("Appointments", buildAppointmentsPanel());
        add(tabs, BorderLayout.CENTER);

        setVisible(true);
    }

    // ══════════════════════════════════════════════════════════════════════════
    // PATIENTS TAB
    // ══════════════════════════════════════════════════════════════════════════
    private JPanel buildPatientsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        String[] columns = {"ID", "Last Name", "First Name", "Phone"};
        patientModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        patientTable = new JTable(patientModel);
        patientTable.setRowHeight(25);
        panel.add(new JScrollPane(patientTable), BorderLayout.CENTER);

        JPanel form = new JPanel(new GridLayout(2, 5, 5, 5));
        tfId     = new JTextField(); tfId.setToolTipText("Patient ID");
        tfNom    = new JTextField(); tfNom.setToolTipText("Last Name");
        tfPrenom = new JTextField(); tfPrenom.setToolTipText("First Name");
        tfTel    = new JTextField(); tfTel.setToolTipText("Phone");

        form.add(new JLabel("ID")); form.add(new JLabel("Last Name"));
        form.add(new JLabel("First Name")); form.add(new JLabel("Phone"));
        form.add(new JLabel(""));
        form.add(tfId); form.add(tfNom); form.add(tfPrenom);
        form.add(tfTel); form.add(new JLabel(""));

        JPanel buttons = new JPanel(new GridLayout(3, 1, 5, 5));
        JButton btnAdd    = new JButton("Add");
        JButton btnUpdate = new JButton("Update");
        btnDelete         = new JButton("Delete");

        btnAdd.addActionListener(e -> addPatient());
        btnUpdate.addActionListener(e -> updatePatient());
        btnDelete.addActionListener(e -> deletePatient());

        // ── ROLE PERMISSIONS ─────────────────────────────────────────────────
        if (role.equals("doctor")) {
            btnAdd.setEnabled(false);
            btnUpdate.setEnabled(false);
            btnDelete.setEnabled(false);
        }
        if (role.equals("assistant")) {
            btnDelete.setEnabled(false);
        }

        buttons.add(btnAdd); buttons.add(btnUpdate); buttons.add(btnDelete);

        patientTable.getSelectionModel().addListSelectionListener(e -> {
            int row = patientTable.getSelectedRow();
            if (row >= 0) {
                tfId.setText(patientModel.getValueAt(row, 0).toString());
                tfNom.setText(patientModel.getValueAt(row, 1).toString());
                tfPrenom.setText(patientModel.getValueAt(row, 2).toString());
                tfTel.setText(patientModel.getValueAt(row, 3).toString());
            }
        });

        JPanel south = new JPanel(new BorderLayout(5, 5));
        south.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        south.add(form, BorderLayout.CENTER);
        south.add(buttons, BorderLayout.EAST);
        panel.add(south, BorderLayout.SOUTH);

        loadPatients();
        return panel;
    }

    private void loadPatients() {
        patientModel.setRowCount(0);
        try (Connection con = Connexion.getConnexion();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(
                 "SELECT num_patient, nom, prenom, telephone FROM patient")) {
            while (rs.next()) {
                patientModel.addRow(new Object[]{
                    rs.getInt("num_patient"), rs.getString("nom"),
                    rs.getString("prenom"),   rs.getString("telephone")
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Load patients error: " + e.getMessage());
        }
    }

    private void addPatient() {
        String sql = "INSERT INTO patient (num_patient, nom, prenom, telephone) " +
                     "VALUES ((SELECT NVL(MAX(num_patient),0)+1 FROM patient), ?, ?, ?)";
        try (Connection con = Connexion.getConnexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, tfNom.getText());
            ps.setString(2, tfPrenom.getText());
            ps.setString(3, tfTel.getText());
            ps.executeUpdate();
            loadPatients(); clearPatientFields();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Add patient error: " + e.getMessage());
        }
    }

    private void updatePatient() {
        String sql = "UPDATE patient SET nom=?, prenom=?, telephone=? WHERE num_patient=?";
        try (Connection con = Connexion.getConnexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, tfNom.getText());
            ps.setString(2, tfPrenom.getText());
            ps.setString(3, tfTel.getText());
            ps.setInt(4, Integer.parseInt(tfId.getText()));
            ps.executeUpdate();
            loadPatients(); clearPatientFields();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Update patient error: " + e.getMessage());
        }
    }

    private void deletePatient() {
        try (Connection con = Connexion.getConnexion();
             PreparedStatement ps = con.prepareStatement(
                 "DELETE FROM patient WHERE num_patient=?")) {
            ps.setInt(1, Integer.parseInt(tfId.getText()));
            if (JOptionPane.showConfirmDialog(this, "Delete this patient?") == JOptionPane.YES_OPTION) {
                ps.executeUpdate();
                loadPatients(); clearPatientFields();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Delete patient error: " + e.getMessage());
        }
    }

    private void clearPatientFields() {
        tfId.setText(""); tfNom.setText(""); tfPrenom.setText(""); tfTel.setText("");
    }

    // ══════════════════════════════════════════════════════════════════════════
    // DOCTORS TAB
    // ══════════════════════════════════════════════════════════════════════════
    private JPanel buildDoctorsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        String[] columns = {"ID", "Last Name", "First Name", "Specialty", "Phone"};
        doctorModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        doctorTable = new JTable(doctorModel);
        doctorTable.setRowHeight(25);
        panel.add(new JScrollPane(doctorTable), BorderLayout.CENTER);

        JPanel form = new JPanel(new GridLayout(2, 6, 5, 5));
        tfDoctorId     = new JTextField(); tfDoctorId.setToolTipText("ID");
        tfDoctorNom    = new JTextField(); tfDoctorNom.setToolTipText("Last Name");
        tfDoctorPrenom = new JTextField(); tfDoctorPrenom.setToolTipText("First Name");
        tfDoctorSpec   = new JTextField(); tfDoctorSpec.setToolTipText("Specialty");
        tfDoctorTel    = new JTextField(); tfDoctorTel.setToolTipText("Phone");

        form.add(new JLabel("ID")); form.add(new JLabel("Last Name"));
        form.add(new JLabel("First Name")); form.add(new JLabel("Specialty"));
        form.add(new JLabel("Phone")); form.add(new JLabel(""));
        form.add(tfDoctorId); form.add(tfDoctorNom); form.add(tfDoctorPrenom);
        form.add(tfDoctorSpec); form.add(tfDoctorTel); form.add(new JLabel(""));

        JPanel buttons = new JPanel(new GridLayout(3, 1, 5, 5));
        JButton btnAddDoc    = new JButton("Add");
        JButton btnUpdateDoc = new JButton("Update");
        btnDeleteDoctor      = new JButton("Delete");

        btnAddDoc.addActionListener(e -> addDoctor());
        btnUpdateDoc.addActionListener(e -> updateDoctor());
        btnDeleteDoctor.addActionListener(e -> deleteDoctor());

        // ── ROLE PERMISSIONS ─────────────────────────────────────────────────
        // Only owner can manage doctors
        if (!role.equals("owner")) {
            btnAddDoc.setEnabled(false);
            btnUpdateDoc.setEnabled(false);
            btnDeleteDoctor.setEnabled(false);
        }

        buttons.add(btnAddDoc); buttons.add(btnUpdateDoc); buttons.add(btnDeleteDoctor);

        doctorTable.getSelectionModel().addListSelectionListener(e -> {
            int row = doctorTable.getSelectedRow();
            if (row >= 0) {
                tfDoctorId.setText(doctorModel.getValueAt(row, 0).toString());
                tfDoctorNom.setText(doctorModel.getValueAt(row, 1).toString());
                tfDoctorPrenom.setText(doctorModel.getValueAt(row, 2).toString());
                tfDoctorSpec.setText(doctorModel.getValueAt(row, 3).toString());
                tfDoctorTel.setText(doctorModel.getValueAt(row, 4).toString());
            }
        });

        JPanel south = new JPanel(new BorderLayout(5, 5));
        south.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        south.add(form, BorderLayout.CENTER);
        south.add(buttons, BorderLayout.EAST);
        panel.add(south, BorderLayout.SOUTH);

        loadDoctors();
        return panel;
    }

    private void loadDoctors() {
        doctorModel.setRowCount(0);
        try (Connection con = Connexion.getConnexion();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(
                 "SELECT num_medecin, nom, prenom, specialite, telephone FROM medecin")) {
            while (rs.next()) {
                doctorModel.addRow(new Object[]{
                    rs.getInt("num_medecin"), rs.getString("nom"),
                    rs.getString("prenom"),   rs.getString("specialite"),
                    rs.getString("telephone")
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Load doctors error: " + e.getMessage());
        }
    }

    private void addDoctor() {
        String sql = "INSERT INTO medecin (num_medecin, nom, prenom, specialite, telephone) " +
                     "VALUES ((SELECT NVL(MAX(num_medecin),0)+1 FROM medecin), ?, ?, ?, ?)";
        try (Connection con = Connexion.getConnexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, tfDoctorNom.getText());
            ps.setString(2, tfDoctorPrenom.getText());
            ps.setString(3, tfDoctorSpec.getText());
            ps.setString(4, tfDoctorTel.getText());
            ps.executeUpdate();
            loadDoctors(); clearDoctorFields();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Add doctor error: " + e.getMessage());
        }
    }

    private void updateDoctor() {
        String sql = "UPDATE medecin SET nom=?, prenom=?, specialite=?, telephone=? WHERE num_medecin=?";
        try (Connection con = Connexion.getConnexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, tfDoctorNom.getText());
            ps.setString(2, tfDoctorPrenom.getText());
            ps.setString(3, tfDoctorSpec.getText());
            ps.setString(4, tfDoctorTel.getText());
            ps.setInt(5, Integer.parseInt(tfDoctorId.getText()));
            ps.executeUpdate();
            loadDoctors(); clearDoctorFields();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Update doctor error: " + e.getMessage());
        }
    }

    private void deleteDoctor() {
        try (Connection con = Connexion.getConnexion();
             PreparedStatement ps = con.prepareStatement(
                 "DELETE FROM medecin WHERE num_medecin=?")) {
            ps.setInt(1, Integer.parseInt(tfDoctorId.getText()));
            if (JOptionPane.showConfirmDialog(this, "Delete this doctor?") == JOptionPane.YES_OPTION) {
                ps.executeUpdate();
                loadDoctors(); clearDoctorFields();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Delete doctor error: " + e.getMessage());
        }
    }

    private void clearDoctorFields() {
        tfDoctorId.setText(""); tfDoctorNom.setText(""); tfDoctorPrenom.setText("");
        tfDoctorSpec.setText(""); tfDoctorTel.setText("");
    }

    // ══════════════════════════════════════════════════════════════════════════
    // APPOINTMENTS TAB
    // ══════════════════════════════════════════════════════════════════════════
    private JPanel buildAppointmentsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        String[] columns = {"ID", "Patient ID", "Doctor ID", "Date", "Time", "Status"};
        rdvModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        rdvTable = new JTable(rdvModel);
        rdvTable.setRowHeight(25);
        panel.add(new JScrollPane(rdvTable), BorderLayout.CENTER);

        JPanel form = new JPanel(new GridLayout(2, 7, 5, 5));
        tfRdvId      = new JTextField(); tfRdvId.setToolTipText("ID");
        tfRdvPatient = new JTextField(); tfRdvPatient.setToolTipText("Patient ID");
        tfRdvMedecin = new JTextField(); tfRdvMedecin.setToolTipText("Doctor ID");
        tfRdvDate    = new JTextField(); tfRdvDate.setToolTipText("Date YYYY-MM-DD");
        tfRdvHeure   = new JTextField(); tfRdvHeure.setToolTipText("Time HH:MM");
        tfRdvStatut  = new JTextField(); tfRdvStatut.setToolTipText("Status");

        form.add(new JLabel("ID")); form.add(new JLabel("Patient ID"));
        form.add(new JLabel("Doctor ID")); form.add(new JLabel("Date"));
        form.add(new JLabel("Time")); form.add(new JLabel("Status"));
        form.add(new JLabel(""));
        form.add(tfRdvId); form.add(tfRdvPatient); form.add(tfRdvMedecin);
        form.add(tfRdvDate); form.add(tfRdvHeure); form.add(tfRdvStatut);
        form.add(new JLabel(""));

        JPanel buttons = new JPanel(new GridLayout(3, 1, 5, 5));
        JButton btnAddRdv    = new JButton("Add");
        JButton btnUpdateRdv = new JButton("Update");
        btnDeleteRdv         = new JButton("Delete");

        btnAddRdv.addActionListener(e -> addRdv());
        btnUpdateRdv.addActionListener(e -> updateRdv());
        btnDeleteRdv.addActionListener(e -> deleteRdv());

        // ── ROLE PERMISSIONS ─────────────────────────────────────────────────
        if (role.equals("doctor")) {
            btnAddRdv.setEnabled(false);
            btnUpdateRdv.setEnabled(false);
            btnDeleteRdv.setEnabled(false);
        }
        if (role.equals("assistant")) {
            btnDeleteRdv.setEnabled(false);
        }

        buttons.add(btnAddRdv); buttons.add(btnUpdateRdv); buttons.add(btnDeleteRdv);

        rdvTable.getSelectionModel().addListSelectionListener(e -> {
            int row = rdvTable.getSelectedRow();
            if (row >= 0) {
                tfRdvId.setText(rdvModel.getValueAt(row, 0).toString());
                tfRdvPatient.setText(rdvModel.getValueAt(row, 1).toString());
                tfRdvMedecin.setText(rdvModel.getValueAt(row, 2).toString());
                tfRdvDate.setText(rdvModel.getValueAt(row, 3).toString());
                tfRdvHeure.setText(rdvModel.getValueAt(row, 4).toString());
                tfRdvStatut.setText(rdvModel.getValueAt(row, 5).toString());
            }
        });

        JPanel south = new JPanel(new BorderLayout(5, 5));
        south.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        south.add(form, BorderLayout.CENTER);
        south.add(buttons, BorderLayout.EAST);
        panel.add(south, BorderLayout.SOUTH);

        loadRdv();
        return panel;
    }

    private void loadRdv() {
        rdvModel.setRowCount(0);
        try (Connection con = Connexion.getConnexion();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(
                 "SELECT num_rendezvous, num_patient, num_medecin, " +
                 "TO_CHAR(date_rdv,'YYYY-MM-DD'), heure_rdv, statut FROM rendezvous")) {
            while (rs.next()) {
                rdvModel.addRow(new Object[]{
                    rs.getInt(1), rs.getInt(2), rs.getInt(3),
                    rs.getString(4), rs.getString(5), rs.getString(6)
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Load appointments error: " + e.getMessage());
        }
    }

    private void addRdv() {
        String sql = "INSERT INTO rendezvous (num_rendezvous, num_patient, num_medecin, date_rdv, heure_rdv, statut) " +
                     "VALUES ((SELECT NVL(MAX(num_rendezvous),0)+1 FROM rendezvous), ?, ?, TO_DATE(?,'YYYY-MM-DD'), ?, ?)";
        try (Connection con = Connexion.getConnexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, Integer.parseInt(tfRdvPatient.getText()));
            ps.setInt(2, Integer.parseInt(tfRdvMedecin.getText()));
            ps.setString(3, tfRdvDate.getText());
            ps.setString(4, tfRdvHeure.getText());
            ps.setString(5, tfRdvStatut.getText().isEmpty() ? "Planifie" : tfRdvStatut.getText());
            ps.executeUpdate();
            loadRdv(); clearRdvFields();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Add appointment error: " + e.getMessage());
        }
    }

    private void updateRdv() {
        String sql = "UPDATE rendezvous SET num_patient=?, num_medecin=?, " +
                     "date_rdv=TO_DATE(?,'YYYY-MM-DD'), heure_rdv=?, statut=? WHERE num_rendezvous=?";
        try (Connection con = Connexion.getConnexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, Integer.parseInt(tfRdvPatient.getText()));
            ps.setInt(2, Integer.parseInt(tfRdvMedecin.getText()));
            ps.setString(3, tfRdvDate.getText());
            ps.setString(4, tfRdvHeure.getText());
            ps.setString(5, tfRdvStatut.getText());
            ps.setInt(6, Integer.parseInt(tfRdvId.getText()));
            ps.executeUpdate();
            loadRdv(); clearRdvFields();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Update appointment error: " + e.getMessage());
        }
    }

    private void deleteRdv() {
        try (Connection con = Connexion.getConnexion();
             PreparedStatement ps = con.prepareStatement(
                 "DELETE FROM rendezvous WHERE num_rendezvous=?")) {
            ps.setInt(1, Integer.parseInt(tfRdvId.getText()));
            if (JOptionPane.showConfirmDialog(this, "Delete this appointment?") == JOptionPane.YES_OPTION) {
                ps.executeUpdate();
                loadRdv(); clearRdvFields();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Delete appointment error: " + e.getMessage());
        }
    }

    private void clearRdvFields() {
        tfRdvId.setText(""); tfRdvPatient.setText(""); tfRdvMedecin.setText("");
        tfRdvDate.setText(""); tfRdvHeure.setText(""); tfRdvStatut.setText("");
    }

    // ── Main ──────────────────────────────────────────────────────────────────
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MedicalAppUI("owner"));
    }
}