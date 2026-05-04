import javax.swing.*;
import javax.swing.table.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.sql.*;

public class MedicalAppUI extends JFrame {

    private String role;

    // Patients
    private JTable patientTable;
    private DefaultTableModel patientModel;
    private TableRowSorter<DefaultTableModel> patientSorter;
    private JTextField tfId, tfNom, tfPrenom, tfTel;
    private JButton btnDelete;

    // Doctors
    private JTable doctorTable;
    private DefaultTableModel doctorModel;
    private TableRowSorter<DefaultTableModel> doctorSorter;
    private JTextField tfDoctorId, tfDoctorNom, tfDoctorPrenom, tfDoctorSpec, tfDoctorTel;
    private JButton btnDeleteDoctor;

    // Appointments
    private JTable rdvTable;
    private DefaultTableModel rdvModel;
    private TableRowSorter<DefaultTableModel> rdvSorter;
    private JTextField tfRdvId, tfRdvPatient, tfRdvMedecin, tfRdvDate, tfRdvHeure, tfRdvStatut;
    private JButton btnDeleteRdv;

    // Color Palette
    static final Color BG_DARK      = new Color(0x0F1923);
    static final Color BG_CARD      = new Color(0x1A2636);
    static final Color BG_FIELD     = new Color(0x243447);
    static final Color ACCENT_BLUE  = new Color(0x2F80ED);
    static final Color ACCENT_GREEN = new Color(0x27AE60);
    static final Color ACCENT_RED   = new Color(0xE74C3C);
    static final Color ACCENT_ORANGE= new Color(0xE67E22);
    static final Color ACCENT_PURPLE= new Color(0x8E44AD);
    static final Color TEXT_MAIN    = new Color(0xECF0F1);
    static final Color TEXT_DIM     = new Color(0x8899AA);
    static final Color ROW_ODD      = new Color(0x1E2D3D);
    static final Color ROW_EVN      = new Color(0x172231);
    static final Color HEADER_BG    = new Color(0x0D1B2A);

    public MedicalAppUI(String role) {
        this.role = role;
        setTitle("Medical Clinic Management");
        setSize(1050, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(BG_DARK);

        add(buildTopBar(), BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setBackground(BG_CARD);
        tabs.setForeground(TEXT_MAIN);
        tabs.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tabs.addTab("[ Patients ]",     buildPatientsPanel());
        tabs.addTab("[ Doctors ]",      buildDoctorsPanel());
        tabs.addTab("[ Appointments ]", buildAppointmentsPanel());

        tabs.setBackgroundAt(0, ACCENT_BLUE);
        tabs.setForegroundAt(0, Color.WHITE);
        tabs.setBackgroundAt(1, BG_CARD);
        tabs.setForegroundAt(1, TEXT_MAIN);
        tabs.setBackgroundAt(2, BG_CARD);
        tabs.setForegroundAt(2, TEXT_MAIN);

        tabs.addChangeListener(e -> {
            for (int i = 0; i < tabs.getTabCount(); i++) {
                boolean sel = (i == tabs.getSelectedIndex());
                tabs.setBackgroundAt(i, sel ? ACCENT_BLUE : BG_CARD);
                tabs.setForegroundAt(i, Color.WHITE);
            }
        });

        add(tabs, BorderLayout.CENTER);
        setVisible(true);
    }

    // ── Top bar ───────────────────────────────────────────────────────────────
    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setPaint(new GradientPaint(0,0,new Color(0x0D1B2A),getWidth(),0,new Color(0x1A2F4A)));
                g2.fillRect(0,0,getWidth(),getHeight());
                g2.setColor(ACCENT_BLUE);
                g2.setStroke(new BasicStroke(2));
                g2.drawLine(0,getHeight()-1,getWidth(),getHeight()-1);
            }
        };
        bar.setPreferredSize(new Dimension(0, 60));
        bar.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
        bar.setOpaque(false);

        JLabel appName = new JLabel("+ Medical Clinic");
        appName.setFont(new Font("Segoe UI", Font.BOLD, 22));
        appName.setForeground(Color.WHITE);
        bar.add(appName, BorderLayout.WEST);

        // Right side: role badge + logout button
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        rightPanel.setOpaque(false);

        String roleText; Color roleColor;
        switch (role) {
            case "owner":     roleText = "OWNER - Full Access";        roleColor = ACCENT_PURPLE; break;
            case "doctor":    roleText = "DOCTOR - View Only";         roleColor = ACCENT_BLUE;   break;
            default:          roleText = "ASSISTANT - Limited Access"; roleColor = ACCENT_ORANGE; break;
        }

        JLabel badge = new JLabel("  " + roleText + "  ") {
            Color bc = roleColor;
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bc.darker());
                g2.fillRoundRect(0,0,getWidth(),getHeight(),16,16);
                g2.setColor(bc);
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,16,16);
                super.paintComponent(g);
            }
        };
        badge.setFont(new Font("Segoe UI", Font.BOLD, 13));
        badge.setForeground(Color.WHITE);
        badge.setBorder(BorderFactory.createEmptyBorder(8,8,8,8));
        badge.setOpaque(false);

        // Logout button
        JButton btnLogout = new JButton("[ Logout ]") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? ACCENT_RED.brighter() : ACCENT_RED);
                g2.fillRoundRect(0,0,getWidth(),getHeight(),10,10);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI",Font.BOLD,12));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(),(getWidth()-fm.stringWidth(getText()))/2,
                             (getHeight()+fm.getAscent()-fm.getDescent())/2);
            }
        };
        btnLogout.setPreferredSize(new Dimension(100, 35));
        btnLogout.setContentAreaFilled(false);
        btnLogout.setBorderPainted(false);
        btnLogout.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnLogout.setFocusPainted(false);
        btnLogout.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to logout?", "Logout", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                dispose();
                new LoginPage();
            }
        });

        rightPanel.add(badge);
        rightPanel.add(btnLogout);
        bar.add(rightPanel, BorderLayout.EAST);
        return bar;
    }

    // ── Search bar builder ────────────────────────────────────────────────────
    private JPanel buildSearchBar(TableRowSorter<?> sorter, String placeholder) {
        JPanel searchPanel = new JPanel(new BorderLayout(8, 0));
        searchPanel.setBackground(BG_CARD);
        searchPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));

        // Search icon label
        JLabel searchIcon = new JLabel("  Search: ");
        searchIcon.setFont(new Font("Segoe UI", Font.BOLD, 13));
        searchIcon.setForeground(ACCENT_BLUE);
        searchPanel.add(searchIcon, BorderLayout.WEST);

        // Search field
        JTextField searchField = new JTextField();
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        searchField.setBackground(BG_FIELD);
        searchField.setForeground(TEXT_MAIN);
        searchField.setCaretColor(Color.WHITE);
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ACCENT_BLUE, 1),
            BorderFactory.createEmptyBorder(6, 12, 6, 12)
        ));
        searchField.setToolTipText(placeholder);

        // Placeholder text effect
        searchField.setText(placeholder);
        searchField.setForeground(TEXT_DIM);
        searchField.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) {
                if (searchField.getText().equals(placeholder)) {
                    searchField.setText("");
                    searchField.setForeground(TEXT_MAIN);
                }
            }
            @Override public void focusLost(FocusEvent e) {
                if (searchField.getText().isEmpty()) {
                    searchField.setText(placeholder);
                    searchField.setForeground(TEXT_DIM);
                }
            }
        });

        // Real-time filter as user types
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            void filter() {
                String text = searchField.getText();
                if (text.equals(placeholder) || text.isEmpty()) {
                    sorter.setRowFilter(null); // Show all rows
                } else {
                    // Search across ALL columns
                    sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
                }
            }
            @Override public void insertUpdate(javax.swing.event.DocumentEvent e) { filter(); }
            @Override public void removeUpdate(javax.swing.event.DocumentEvent e) { filter(); }
            @Override public void changedUpdate(javax.swing.event.DocumentEvent e) { filter(); }
        });

        // Clear button
        JButton clearBtn = new JButton("X") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? new Color(0x3A4A5A) : BG_FIELD);
                g2.fillRoundRect(0,0,getWidth(),getHeight(),8,8);
                g2.setColor(TEXT_DIM);
                g2.setFont(new Font("Segoe UI",Font.BOLD,11));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(),(getWidth()-fm.stringWidth(getText()))/2,
                             (getHeight()+fm.getAscent()-fm.getDescent())/2);
            }
        };
        clearBtn.setPreferredSize(new Dimension(40, 36));
        clearBtn.setContentAreaFilled(false);
        clearBtn.setBorderPainted(false);
        clearBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        clearBtn.setFocusPainted(false);
        clearBtn.setToolTipText("Clear search");
        clearBtn.addActionListener(e -> {
            searchField.setText(placeholder);
            searchField.setForeground(TEXT_DIM);
            sorter.setRowFilter(null);
        });

        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(clearBtn, BorderLayout.EAST);
        return searchPanel;
    }

    // ── Style table ───────────────────────────────────────────────────────────
    private void styleTable(JTable t) {
        t.setBackground(ROW_ODD);
        t.setForeground(TEXT_MAIN);
        t.setGridColor(new Color(0x243447));
        t.setRowHeight(32);
        t.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        t.setSelectionBackground(ACCENT_BLUE);
        t.setSelectionForeground(Color.WHITE);
        t.setShowVerticalLines(false);
        t.setIntercellSpacing(new Dimension(0,1));
        t.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable tbl,Object val,boolean sel,boolean foc,int row,int col) {
                super.getTableCellRendererComponent(tbl,val,sel,foc,row,col);
                if (!sel) { setBackground(row%2==0?ROW_ODD:ROW_EVN); setForeground(TEXT_MAIN); }
                setBorder(BorderFactory.createEmptyBorder(0,10,0,10));
                return this;
            }
        });
        JTableHeader h = t.getTableHeader();
        h.setBackground(HEADER_BG);
        h.setForeground(ACCENT_BLUE);
        h.setFont(new Font("Segoe UI", Font.BOLD, 13));
        h.setBorder(BorderFactory.createMatteBorder(0,0,2,0,ACCENT_BLUE));
        h.setReorderingAllowed(false);
        ((DefaultTableCellRenderer)h.getDefaultRenderer()).setHorizontalAlignment(SwingConstants.LEFT);
    }

    private JTextField sf(String tip) {
        JTextField f = new JTextField();
        f.setToolTipText(tip);
        f.setBackground(BG_FIELD);
        f.setForeground(TEXT_MAIN);
        f.setCaretColor(Color.WHITE);
        f.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0x2F4A6A),1),
            BorderFactory.createEmptyBorder(6,10,6,10)));
        return f;
    }

    private JLabel sl(String t) {
        JLabel l = new JLabel(t);
        l.setForeground(TEXT_DIM);
        l.setFont(new Font("Segoe UI", Font.BOLD, 11));
        return l;
    }

    private JButton cb(String text, Color color) {
        JButton b = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D)g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                Color base = isEnabled()?(getModel().isRollover()?color.brighter():color):new Color(0x3A4A5A);
                g2.setColor(base);
                g2.fillRoundRect(0,0,getWidth(),getHeight(),10,10);
                if(getModel().isPressed()){g2.setColor(new Color(0,0,0,50));g2.fillRoundRect(0,0,getWidth(),getHeight(),10,10);}
                g2.setColor(isEnabled()?Color.WHITE:TEXT_DIM);
                g2.setFont(new Font("Segoe UI",Font.BOLD,13));
                FontMetrics fm=g2.getFontMetrics();
                g2.drawString(getText(),(getWidth()-fm.stringWidth(getText()))/2,(getHeight()+fm.getAscent()-fm.getDescent())/2);
            }
        };
        b.setPreferredSize(new Dimension(130,38));
        b.setContentAreaFilled(false); b.setBorderPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setFocusPainted(false);
        return b;
    }

    // ══════════════════════════════════════════════════════════════════════════
    // PATIENTS TAB
    // ══════════════════════════════════════════════════════════════════════════
    private JPanel buildPatientsPanel() {
        JPanel panel = new JPanel(new BorderLayout(0,0));
        panel.setBackground(BG_CARD);

        patientModel = new DefaultTableModel(new String[]{"ID","Last Name","First Name","Phone"},0){
            @Override public boolean isCellEditable(int r,int c){return false;}
        };
        patientTable = new JTable(patientModel);
        styleTable(patientTable);

        // Attach sorter for search
        patientSorter = new TableRowSorter<>(patientModel);
        patientTable.setRowSorter(patientSorter);

        JScrollPane sp = new JScrollPane(patientTable);
        sp.getViewport().setBackground(BG_DARK);
        sp.setBorder(BorderFactory.createEmptyBorder(0,10,0,10));

        // Search bar + table together
        JPanel tableArea = new JPanel(new BorderLayout());
        tableArea.setBackground(BG_CARD);
        tableArea.add(buildSearchBar(patientSorter, "Type to search patients..."), BorderLayout.NORTH);
        tableArea.add(sp, BorderLayout.CENTER);
        panel.add(tableArea, BorderLayout.CENTER);

        // Form + buttons
        JPanel south = new JPanel(new BorderLayout(10,0));
        south.setBackground(BG_CARD);
        south.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        JPanel form = new JPanel(new GridLayout(2,4,8,4));
        form.setBackground(BG_CARD);
        tfId=sf("ID"); tfNom=sf("Last Name"); tfPrenom=sf("First Name"); tfTel=sf("Phone");
        form.add(sl("ID")); form.add(sl("LAST NAME")); form.add(sl("FIRST NAME")); form.add(sl("PHONE"));
        form.add(tfId); form.add(tfNom); form.add(tfPrenom); form.add(tfTel);

        JPanel bp = new JPanel(new GridLayout(3,1,0,8)); bp.setBackground(BG_CARD);
        JButton bA=cb("[ + ] Add",ACCENT_GREEN), bU=cb("[ / ] Update",ACCENT_BLUE);
        btnDelete=cb("[ x ] Delete",ACCENT_RED);
        bA.addActionListener(e->addPatient()); bU.addActionListener(e->updatePatient());
        btnDelete.addActionListener(e->deletePatient());
        if(role.equals("doctor")){bA.setEnabled(false);bU.setEnabled(false);btnDelete.setEnabled(false);}
        if(role.equals("assistant")){btnDelete.setEnabled(false);}
        bp.add(bA); bp.add(bU); bp.add(btnDelete);
        south.add(form,BorderLayout.CENTER); south.add(bp,BorderLayout.EAST);
        panel.add(south,BorderLayout.SOUTH);

        patientTable.getSelectionModel().addListSelectionListener(e->{
            int r=patientTable.getSelectedRow();
            if(r>=0){
                int modelRow = patientTable.convertRowIndexToModel(r);
                tfId.setText(patientModel.getValueAt(modelRow,0).toString());
                tfNom.setText(patientModel.getValueAt(modelRow,1).toString());
                tfPrenom.setText(patientModel.getValueAt(modelRow,2).toString());
                tfTel.setText(patientModel.getValueAt(modelRow,3).toString());
            }
        });
        loadPatients(); return panel;
    }

    private void loadPatients() {
        patientModel.setRowCount(0);
        try(Connection c=Connexion.getConnexion();Statement s=c.createStatement();
            ResultSet r=s.executeQuery("SELECT num_patient,nom,prenom,telephone FROM patient")){
            while(r.next()) patientModel.addRow(new Object[]{r.getInt(1),r.getString(2),r.getString(3),r.getString(4)});
        }catch(Exception e){JOptionPane.showMessageDialog(this,"Load error: "+e.getMessage());}
    }
    private void addPatient(){
        try(Connection c=Connexion.getConnexion();PreparedStatement p=c.prepareStatement(
            "INSERT INTO patient(num_patient,nom,prenom,telephone) VALUES((SELECT NVL(MAX(num_patient),0)+1 FROM patient),?,?,?)")){
            p.setString(1,tfNom.getText());p.setString(2,tfPrenom.getText());p.setString(3,tfTel.getText());
            p.executeUpdate();loadPatients();clearP();
        }catch(SQLException e){JOptionPane.showMessageDialog(this,"Add error: "+e.getMessage());}
    }
    private void updatePatient(){
        try(Connection c=Connexion.getConnexion();PreparedStatement p=c.prepareStatement(
            "UPDATE patient SET nom=?,prenom=?,telephone=? WHERE num_patient=?")){
            p.setString(1,tfNom.getText());p.setString(2,tfPrenom.getText());p.setString(3,tfTel.getText());
            p.setInt(4,Integer.parseInt(tfId.getText()));p.executeUpdate();loadPatients();clearP();
        }catch(SQLException e){JOptionPane.showMessageDialog(this,"Update error: "+e.getMessage());}
    }
    private void deletePatient(){
        try(Connection c=Connexion.getConnexion();PreparedStatement p=c.prepareStatement("DELETE FROM patient WHERE num_patient=?")){
            p.setInt(1,Integer.parseInt(tfId.getText()));
            if(JOptionPane.showConfirmDialog(this,"Delete this patient?")==JOptionPane.YES_OPTION){p.executeUpdate();loadPatients();clearP();}
        }catch(SQLException e){JOptionPane.showMessageDialog(this,"Delete error: "+e.getMessage());}
    }
    private void clearP(){tfId.setText("");tfNom.setText("");tfPrenom.setText("");tfTel.setText("");}

    // ══════════════════════════════════════════════════════════════════════════
    // DOCTORS TAB
    // ══════════════════════════════════════════════════════════════════════════
    private JPanel buildDoctorsPanel() {
        JPanel panel = new JPanel(new BorderLayout(0,0)); panel.setBackground(BG_CARD);
        doctorModel=new DefaultTableModel(new String[]{"ID","Last Name","First Name","Specialty","Phone"},0){
            @Override public boolean isCellEditable(int r,int c){return false;}
        };
        doctorTable=new JTable(doctorModel); styleTable(doctorTable);
        doctorSorter = new TableRowSorter<>(doctorModel);
        doctorTable.setRowSorter(doctorSorter);

        JScrollPane sp=new JScrollPane(doctorTable); sp.getViewport().setBackground(BG_DARK);
        sp.setBorder(BorderFactory.createEmptyBorder(0,10,0,10));

        JPanel tableArea = new JPanel(new BorderLayout());
        tableArea.setBackground(BG_CARD);
        tableArea.add(buildSearchBar(doctorSorter, "Type to search doctors..."), BorderLayout.NORTH);
        tableArea.add(sp, BorderLayout.CENTER);
        panel.add(tableArea, BorderLayout.CENTER);

        JPanel south=new JPanel(new BorderLayout(10,0)); south.setBackground(BG_CARD);
        south.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        JPanel form=new JPanel(new GridLayout(2,5,8,4)); form.setBackground(BG_CARD);
        tfDoctorId=sf("ID"); tfDoctorNom=sf("Last Name"); tfDoctorPrenom=sf("First Name");
        tfDoctorSpec=sf("Specialty"); tfDoctorTel=sf("Phone");
        form.add(sl("ID")); form.add(sl("LAST NAME")); form.add(sl("FIRST NAME")); form.add(sl("SPECIALTY")); form.add(sl("PHONE"));
        form.add(tfDoctorId); form.add(tfDoctorNom); form.add(tfDoctorPrenom); form.add(tfDoctorSpec); form.add(tfDoctorTel);

        JPanel bp=new JPanel(new GridLayout(3,1,0,8)); bp.setBackground(BG_CARD);
        JButton bA=cb("[ + ] Add",ACCENT_GREEN), bU=cb("[ / ] Update",ACCENT_BLUE);
        btnDeleteDoctor=cb("[ x ] Delete",ACCENT_RED);
        bA.addActionListener(e->addDoctor()); bU.addActionListener(e->updateDoctor());
        btnDeleteDoctor.addActionListener(e->deleteDoctor());
        if(!role.equals("owner")){bA.setEnabled(false);bU.setEnabled(false);btnDeleteDoctor.setEnabled(false);}
        bp.add(bA); bp.add(bU); bp.add(btnDeleteDoctor);
        south.add(form,BorderLayout.CENTER); south.add(bp,BorderLayout.EAST); panel.add(south,BorderLayout.SOUTH);

        doctorTable.getSelectionModel().addListSelectionListener(e->{
            int r=doctorTable.getSelectedRow();
            if(r>=0){
                int modelRow = doctorTable.convertRowIndexToModel(r);
                tfDoctorId.setText(doctorModel.getValueAt(modelRow,0).toString());
                tfDoctorNom.setText(doctorModel.getValueAt(modelRow,1).toString());
                tfDoctorPrenom.setText(doctorModel.getValueAt(modelRow,2).toString());
                tfDoctorSpec.setText(doctorModel.getValueAt(modelRow,3).toString());
                tfDoctorTel.setText(doctorModel.getValueAt(modelRow,4).toString());
            }
        });
        loadDoctors(); return panel;
    }

    private void loadDoctors(){
        doctorModel.setRowCount(0);
        try(Connection c=Connexion.getConnexion();Statement s=c.createStatement();
            ResultSet r=s.executeQuery("SELECT num_medecin,nom,prenom,specialite,telephone FROM medecin")){
            while(r.next()) doctorModel.addRow(new Object[]{r.getInt(1),r.getString(2),r.getString(3),r.getString(4),r.getString(5)});
        }catch(Exception e){JOptionPane.showMessageDialog(this,"Load doctors error: "+e.getMessage());}
    }
    private void addDoctor(){
        try(Connection c=Connexion.getConnexion();PreparedStatement p=c.prepareStatement(
            "INSERT INTO medecin(num_medecin,nom,prenom,specialite,telephone) VALUES((SELECT NVL(MAX(num_medecin),0)+1 FROM medecin),?,?,?,?)")){
            p.setString(1,tfDoctorNom.getText());p.setString(2,tfDoctorPrenom.getText());
            p.setString(3,tfDoctorSpec.getText());p.setString(4,tfDoctorTel.getText());
            p.executeUpdate();loadDoctors();clearD();
        }catch(SQLException e){JOptionPane.showMessageDialog(this,"Add doctor error: "+e.getMessage());}
    }
    private void updateDoctor(){
        try(Connection c=Connexion.getConnexion();PreparedStatement p=c.prepareStatement(
            "UPDATE medecin SET nom=?,prenom=?,specialite=?,telephone=? WHERE num_medecin=?")){
            p.setString(1,tfDoctorNom.getText());p.setString(2,tfDoctorPrenom.getText());
            p.setString(3,tfDoctorSpec.getText());p.setString(4,tfDoctorTel.getText());
            p.setInt(5,Integer.parseInt(tfDoctorId.getText()));p.executeUpdate();loadDoctors();clearD();
        }catch(SQLException e){JOptionPane.showMessageDialog(this,"Update doctor error: "+e.getMessage());}
    }
    private void deleteDoctor(){
        try(Connection c=Connexion.getConnexion();PreparedStatement p=c.prepareStatement("DELETE FROM medecin WHERE num_medecin=?")){
            p.setInt(1,Integer.parseInt(tfDoctorId.getText()));
            if(JOptionPane.showConfirmDialog(this,"Delete this doctor?")==JOptionPane.YES_OPTION){p.executeUpdate();loadDoctors();clearD();}
        }catch(SQLException e){JOptionPane.showMessageDialog(this,"Delete doctor error: "+e.getMessage());}
    }
    private void clearD(){tfDoctorId.setText("");tfDoctorNom.setText("");tfDoctorPrenom.setText("");tfDoctorSpec.setText("");tfDoctorTel.setText("");}

    // ══════════════════════════════════════════════════════════════════════════
    // APPOINTMENTS TAB
    // ══════════════════════════════════════════════════════════════════════════
    private JPanel buildAppointmentsPanel() {
        JPanel panel = new JPanel(new BorderLayout(0,0)); panel.setBackground(BG_CARD);
        rdvModel=new DefaultTableModel(new String[]{"ID","Patient ID","Doctor ID","Date","Time","Status"},0){
            @Override public boolean isCellEditable(int r,int c){return false;}
        };
        rdvTable=new JTable(rdvModel); styleTable(rdvTable);
        rdvSorter = new TableRowSorter<>(rdvModel);
        rdvTable.setRowSorter(rdvSorter);

        // Color-code status column
        rdvTable.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer(){
            @Override public Component getTableCellRendererComponent(JTable t,Object val,boolean sel,boolean foc,int row,int col){
                super.getTableCellRendererComponent(t,val,sel,foc,row,col);
                String s=val!=null?val.toString():"";
                if(!sel){
                    setBackground(row%2==0?ROW_ODD:ROW_EVN);
                    if(s.equals("Planifie")) setForeground(ACCENT_BLUE);
                    else if(s.equals("Annule")) setForeground(ACCENT_RED);
                    else setForeground(ACCENT_GREEN);
                }
                setBorder(BorderFactory.createEmptyBorder(0,10,0,10));
                setFont(new Font("Segoe UI",Font.BOLD,13));
                return this;
            }
        });

        JScrollPane sp=new JScrollPane(rdvTable); sp.getViewport().setBackground(BG_DARK);
        sp.setBorder(BorderFactory.createEmptyBorder(0,10,0,10));

        JPanel tableArea = new JPanel(new BorderLayout());
        tableArea.setBackground(BG_CARD);
        tableArea.add(buildSearchBar(rdvSorter, "Type to search appointments..."), BorderLayout.NORTH);
        tableArea.add(sp, BorderLayout.CENTER);
        panel.add(tableArea, BorderLayout.CENTER);

        JPanel south=new JPanel(new BorderLayout(10,0)); south.setBackground(BG_CARD);
        south.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        JPanel form=new JPanel(new GridLayout(2,6,8,4)); form.setBackground(BG_CARD);
        tfRdvId=sf("ID"); tfRdvPatient=sf("Patient ID"); tfRdvMedecin=sf("Doctor ID");
        tfRdvDate=sf("YYYY-MM-DD"); tfRdvHeure=sf("HH:MM"); tfRdvStatut=sf("Planifie/Annule");
        form.add(sl("ID")); form.add(sl("PATIENT ID")); form.add(sl("DOCTOR ID"));
        form.add(sl("DATE")); form.add(sl("TIME")); form.add(sl("STATUS"));
        form.add(tfRdvId); form.add(tfRdvPatient); form.add(tfRdvMedecin);
        form.add(tfRdvDate); form.add(tfRdvHeure); form.add(tfRdvStatut);

        JPanel bp=new JPanel(new GridLayout(3,1,0,8)); bp.setBackground(BG_CARD);
        JButton bA=cb("[ + ] Add",ACCENT_GREEN), bU=cb("[ / ] Update",ACCENT_BLUE);
        btnDeleteRdv=cb("[ x ] Delete",ACCENT_RED);
        bA.addActionListener(e->addRdv()); bU.addActionListener(e->updateRdv());
        btnDeleteRdv.addActionListener(e->deleteRdv());
        if(role.equals("doctor")){bA.setEnabled(false);bU.setEnabled(false);btnDeleteRdv.setEnabled(false);}
        if(role.equals("assistant")){btnDeleteRdv.setEnabled(false);}
        bp.add(bA); bp.add(bU); bp.add(btnDeleteRdv);
        south.add(form,BorderLayout.CENTER); south.add(bp,BorderLayout.EAST); panel.add(south,BorderLayout.SOUTH);

        rdvTable.getSelectionModel().addListSelectionListener(e->{
            int r=rdvTable.getSelectedRow();
            if(r>=0){
                int modelRow = rdvTable.convertRowIndexToModel(r);
                tfRdvId.setText(rdvModel.getValueAt(modelRow,0).toString());
                tfRdvPatient.setText(rdvModel.getValueAt(modelRow,1).toString());
                tfRdvMedecin.setText(rdvModel.getValueAt(modelRow,2).toString());
                tfRdvDate.setText(rdvModel.getValueAt(modelRow,3).toString());
                tfRdvHeure.setText(rdvModel.getValueAt(modelRow,4).toString());
                tfRdvStatut.setText(rdvModel.getValueAt(modelRow,5).toString());
            }
        });
        loadRdv(); return panel;
    }

    private void loadRdv(){
        rdvModel.setRowCount(0);
        try(Connection c=Connexion.getConnexion();Statement s=c.createStatement();
            ResultSet r=s.executeQuery("SELECT num_rendezvous,num_patient,num_medecin,TO_CHAR(date_rdv,'YYYY-MM-DD'),heure_rdv,statut FROM rendezvous")){
            while(r.next()) rdvModel.addRow(new Object[]{r.getInt(1),r.getInt(2),r.getInt(3),r.getString(4),r.getString(5),r.getString(6)});
        }catch(Exception e){JOptionPane.showMessageDialog(this,"Load appointments error: "+e.getMessage());}
    }
    private void addRdv(){
        try(Connection c=Connexion.getConnexion();PreparedStatement p=c.prepareStatement(
            "INSERT INTO rendezvous(num_rendezvous,num_patient,num_medecin,date_rdv,heure_rdv,statut) VALUES((SELECT NVL(MAX(num_rendezvous),0)+1 FROM rendezvous),?,?,TO_DATE(?,'YYYY-MM-DD'),?,?)")){
            p.setInt(1,Integer.parseInt(tfRdvPatient.getText()));p.setInt(2,Integer.parseInt(tfRdvMedecin.getText()));
            p.setString(3,tfRdvDate.getText());p.setString(4,tfRdvHeure.getText());
            p.setString(5,tfRdvStatut.getText().isEmpty()?"Planifie":tfRdvStatut.getText());
            p.executeUpdate();loadRdv();clearR();
        }catch(SQLException e){JOptionPane.showMessageDialog(this,"Add appointment error: "+e.getMessage());}
    }
    private void updateRdv(){
        try(Connection c=Connexion.getConnexion();PreparedStatement p=c.prepareStatement(
            "UPDATE rendezvous SET num_patient=?,num_medecin=?,date_rdv=TO_DATE(?,'YYYY-MM-DD'),heure_rdv=?,statut=? WHERE num_rendezvous=?")){
            p.setInt(1,Integer.parseInt(tfRdvPatient.getText()));p.setInt(2,Integer.parseInt(tfRdvMedecin.getText()));
            p.setString(3,tfRdvDate.getText());p.setString(4,tfRdvHeure.getText());
            p.setString(5,tfRdvStatut.getText());p.setInt(6,Integer.parseInt(tfRdvId.getText()));
            p.executeUpdate();loadRdv();clearR();
        }catch(SQLException e){JOptionPane.showMessageDialog(this,"Update appointment error: "+e.getMessage());}
    }
    private void deleteRdv(){
        try(Connection c=Connexion.getConnexion();PreparedStatement p=c.prepareStatement("DELETE FROM rendezvous WHERE num_rendezvous=?")){
            p.setInt(1,Integer.parseInt(tfRdvId.getText()));
            if(JOptionPane.showConfirmDialog(this,"Delete this appointment?")==JOptionPane.YES_OPTION){p.executeUpdate();loadRdv();clearR();}
        }catch(SQLException e){JOptionPane.showMessageDialog(this,"Delete appointment error: "+e.getMessage());}
    }
    private void clearR(){tfRdvId.setText("");tfRdvPatient.setText("");tfRdvMedecin.setText("");tfRdvDate.setText("");tfRdvHeure.setText("");tfRdvStatut.setText("");}

    public static void main(String[] args) {
        SwingUtilities.invokeLater(()->new MedicalAppUI("owner"));
    }
}