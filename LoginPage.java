import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

public class LoginPage extends JFrame {

    private JTextField tfUsername;
    private JPasswordField tfPassword;
    private JLabel lblError;
    private float hue = 0.60f;
    private Timer animTimer;

    private static final String[][] USERS = {
        {"owner",     "owner123",  "owner"},
        {"doctor",    "doctor123", "doctor"},
        {"assistant", "asst123",   "assistant"}
    };

    public LoginPage() {
        setTitle("Medical Clinic - Login");
        setSize(480, 580);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setUndecorated(true);
        setShape(new RoundRectangle2D.Double(0, 0, 480, 580, 20, 20));

        JPanel root = buildPanel();
        setContentPane(root);

        // Allow dragging the window
        Point[] drag = {new Point()};
        root.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) { drag[0] = e.getPoint(); }
        });
        root.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                Point loc = getLocation();
                setLocation(loc.x + e.getX() - drag[0].x, loc.y + e.getY() - drag[0].y);
            }
        });

        // Animate background
        animTimer = new Timer(50, e -> { hue += 0.002f; if (hue > 1f) hue = 0f; root.repaint(); });
        animTimer.start();

        setVisible(true);
    }

    private JPanel buildPanel() {
        JPanel panel = new JPanel(null) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Animated gradient background
                Color c1 = Color.getHSBColor(hue, 0.7f, 0.25f);
                Color c2 = Color.getHSBColor(hue + 0.12f, 0.8f, 0.35f);
                g2.setPaint(new GradientPaint(0, 0, c1, getWidth(), getHeight(), c2));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                // Decorative circles
                g2.setColor(new Color(255, 255, 255, 18));
                g2.fillOval(-60, -60, 220, 220);
                g2.fillOval(getWidth()-100, getHeight()-100, 200, 200);
                // Grid
                g2.setColor(new Color(255, 255, 255, 8));
                g2.setStroke(new BasicStroke(1));
                for (int x = 0; x < getWidth(); x += 30) g2.drawLine(x, 0, x, getHeight());
                for (int y = 0; y < getHeight(); y += 30) g2.drawLine(0, y, getWidth(), y);
            }
        };

        // Close button
        JButton btnClose = new JButton("X");
        btnClose.setBounds(440, 10, 30, 30);
        btnClose.setForeground(Color.WHITE);
        btnClose.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnClose.setContentAreaFilled(false);
        btnClose.setBorderPainted(false);
        btnClose.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnClose.addActionListener(e -> System.exit(0));
        panel.add(btnClose);

        // Glassmorphism card
        JPanel card = new JPanel(null) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 255, 255, 35));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 24, 24);
                g2.setColor(new Color(255, 255, 255, 60));
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 24, 24);
            }
        };
        card.setOpaque(false);
        card.setBounds(60, 60, 360, 460);
        panel.add(card);

        // Doctor icon — drawn with shapes (no emoji)
        JPanel doctorIcon = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Head circle
                g2.setColor(new Color(255,255,255,180));
                g2.fillOval(18, 5, 24, 24);
                // Body
                g2.fillRoundRect(10, 30, 40, 28, 8, 8);
                // Stethoscope cross
                g2.setColor(new Color(100,180,255,200));
                g2.setStroke(new BasicStroke(2.5f));
                g2.drawLine(30, 38, 30, 50);
                g2.drawLine(24, 44, 36, 44);
            }
        };
        doctorIcon.setOpaque(false);
        doctorIcon.setBounds(150, 18, 60, 60);
        card.add(doctorIcon);

        // Title
        JLabel title = new JLabel("Medical Clinic", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(Color.WHITE);
        title.setBounds(0, 85, 360, 35);
        card.add(title);

        JLabel subtitle = new JLabel("Secure Access Portal", SwingConstants.CENTER);
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitle.setForeground(new Color(200, 220, 255, 200));
        subtitle.setBounds(0, 122, 360, 22);
        card.add(subtitle);

        // Divider line
        JPanel divider = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                g.setColor(new Color(255,255,255,40));
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        divider.setOpaque(false);
        divider.setBounds(40, 155, 280, 1);
        card.add(divider);

        // Username label
        JLabel lblUser = new JLabel("Username");
        lblUser.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblUser.setForeground(new Color(200, 220, 255));
        lblUser.setBounds(40, 172, 280, 20);
        card.add(lblUser);

        // Username field
        tfUsername = new JTextField();
        styleField(tfUsername);
        tfUsername.setBounds(40, 196, 280, 42);
        card.add(tfUsername);

        // Password label
        JLabel lblPass = new JLabel("Password");
        lblPass.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblPass.setForeground(new Color(200, 220, 255));
        lblPass.setBounds(40, 250, 280, 20);
        card.add(lblPass);

        // Password field
        tfPassword = new JPasswordField();
        styleField(tfPassword);
        tfPassword.setBounds(40, 274, 280, 42);
        card.add(tfPassword);

        // Error label
        lblError = new JLabel("", SwingConstants.CENTER);
        lblError.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblError.setForeground(new Color(255, 100, 100));
        lblError.setBounds(40, 323, 280, 20);
        card.add(lblError);

        // Login button
        JButton btnLogin = new JButton("SIGN IN  ->") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color top = getModel().isRollover() ? new Color(0x56CCF2) : new Color(0x2F80ED);
                Color bot = getModel().isRollover() ? new Color(0x2F80ED) : new Color(0x1A6CC7);
                g2.setPaint(new GradientPaint(0, 0, top, 0, getHeight(), bot));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 22, 22);
                if (getModel().isPressed()) {
                    g2.setColor(new Color(0,0,0,40));
                    g2.fillRoundRect(0,0,getWidth(),getHeight(),22,22);
                }
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 14));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(), (getWidth()-fm.stringWidth(getText()))/2,
                              (getHeight()+fm.getAscent()-fm.getDescent())/2);
            }
        };
        btnLogin.setBounds(40, 350, 280, 46);
        btnLogin.setContentAreaFilled(false);
        btnLogin.setBorderPainted(false);
        btnLogin.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnLogin.addActionListener(e -> doLogin());
        tfPassword.addActionListener(e -> doLogin());
        card.add(btnLogin);

        // Hint
        JLabel hint = new JLabel("owner  *  doctor  *  assistant", SwingConstants.CENTER);
        hint.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        hint.setForeground(new Color(180, 200, 255, 150));
        hint.setBounds(0, 410, 360, 18);
        card.add(hint);

        return panel;
    }

    private void styleField(JTextField field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setForeground(Color.WHITE);
        field.setCaretColor(Color.WHITE);
        field.setOpaque(false);
        field.setBorder(BorderFactory.createCompoundBorder(
            new RoundBorder(new Color(255,255,255,70), 11),
            BorderFactory.createEmptyBorder(8, 14, 8, 14)
        ));
        field.setUI(new javax.swing.plaf.basic.BasicTextFieldUI() {
            @Override protected void paintBackground(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255,255,255,30));
                g2.fillRoundRect(0, 0, field.getWidth(), field.getHeight(), 11, 11);
            }
        });
    }

    static class RoundBorder extends AbstractBorder {
        Color color; int radius;
        RoundBorder(Color c, int r) { color = c; radius = r; }
        @Override public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2 = (Graphics2D)g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color); g2.setStroke(new BasicStroke(1.5f));
            g2.drawRoundRect(x, y, w-1, h-1, radius, radius);
        }
        @Override public Insets getBorderInsets(Component c) { return new Insets(4,4,4,4); }
    }

    private void doLogin() {
        String username = tfUsername.getText().trim();
        String password = new String(tfPassword.getPassword());
        for (String[] user : USERS) {
            if (user[0].equals(username) && user[1].equals(password)) {
                animTimer.stop();
                dispose();
                new MedicalAppUI(user[2]);
                return;
            }
        }
        lblError.setText("Invalid username or password");
        tfPassword.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginPage());
    }
}