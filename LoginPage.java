import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;

public class LoginPage extends JFrame {

    private JTextField tfUsername;
    private JPasswordField tfPassword;
    private JLabel lblError;

    // Animated background colors
    private float hue = 0.55f;
    private Timer animTimer;

    private static final String[][] USERS = {
        {"owner",     "owner123",  "owner"},
        {"doctor",    "doctor123", "doctor"},
        {"assistant", "asst123",   "assistant"}
    };

    public LoginPage() {
        setTitle("Medical Clinic — Login");
        setSize(480, 580);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setUndecorated(true); // Remove default title bar for modern look
        setShape(new RoundRectangle2D.Double(0, 0, 480, 580, 20, 20));

        JPanel root = buildPanel();
        setContentPane(root);

        // Drag window since undecorated
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

        // Animate background gradient
        animTimer = new Timer(50, e -> { hue += 0.002f; if (hue > 1f) hue = 0f; root.repaint(); });
        animTimer.start();

        setVisible(true);
    }

    private JPanel buildPanel() {
        JPanel panel = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Animated mesh gradient background
                Color c1 = Color.getHSBColor(hue, 0.7f, 0.25f);
                Color c2 = Color.getHSBColor(hue + 0.1f, 0.8f, 0.35f);
                Color c3 = Color.getHSBColor(hue + 0.2f, 0.6f, 0.20f);
                GradientPaint gp = new GradientPaint(0, 0, c1, getWidth(), getHeight(), c2);
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);

                // Decorative circles
                g2.setColor(new Color(255, 255, 255, 18));
                g2.fillOval(-60, -60, 220, 220);
                g2.fillOval(getWidth() - 100, getHeight() - 100, 200, 200);
                g2.setColor(new Color(255, 255, 255, 10));
                g2.fillOval(getWidth() / 2 - 80, -40, 160, 160);

                // Subtle grid pattern
                g2.setColor(new Color(255, 255, 255, 8));
                g2.setStroke(new BasicStroke(1));
                for (int x = 0; x < getWidth(); x += 30) g2.drawLine(x, 0, x, getHeight());
                for (int y = 0; y < getHeight(); y += 30) g2.drawLine(0, y, getWidth(), y);
            }
        };

        // ── Close button (top right) ──────────────────────────────────────────
        JButton btnClose = new JButton("✕");
        btnClose.setBounds(440, 10, 30, 30);
        btnClose.setForeground(Color.WHITE);
        btnClose.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnClose.setContentAreaFilled(false);
        btnClose.setBorderPainted(false);
        btnClose.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnClose.addActionListener(e -> System.exit(0));
        panel.add(btnClose);

        // ── Glassmorphism card ────────────────────────────────────────────────
        JPanel card = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Frosted glass effect
                g2.setColor(new Color(255, 255, 255, 35));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 24, 24);
                // Border
                g2.setColor(new Color(255, 255, 255, 60));
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 24, 24);
            }
        };
        card.setOpaque(false);
        card.setBounds(60, 60, 360, 460);
        panel.add(card);

        // ── Hospital icon ─────────────────────────────────────────────────────
        JLabel icon = new JLabel("🧑🏻‍⚕️", SwingConstants.CENTER);
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 52));
        icon.setBounds(0, 25, 360, 65);
        card.add(icon);

        // ── Title ─────────────────────────────────────────────────────────────
        JLabel title = new JLabel("Medical Clinic", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(Color.WHITE);
        title.setBounds(0, 95, 360, 35);
        card.add(title);

        JLabel subtitle = new JLabel("Secure Access Portal", SwingConstants.CENTER);
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitle.setForeground(new Color(200, 220, 255, 200));
        subtitle.setBounds(0, 130, 360, 22);
        card.add(subtitle);

        // ── Divider ───────────────────────────────────────────────────────────
        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(255, 255, 255, 40));
        sep.setBounds(40, 162, 280, 2);
        card.add(sep);

        // ── Username label + field ────────────────────────────────────────────
        JLabel lblUser = new JLabel("👤  Username");
        lblUser.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblUser.setForeground(new Color(200, 220, 255));
        lblUser.setBounds(40, 178, 280, 20);
        card.add(lblUser);

        tfUsername = new JTextField();
        styleModernField(tfUsername, "Enter your username");
        tfUsername.setBounds(40, 202, 280, 42);
        card.add(tfUsername);

        // ── Password label + field ────────────────────────────────────────────
        JLabel lblPass = new JLabel("🔒  Password");
        lblPass.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblPass.setForeground(new Color(200, 220, 255));
        lblPass.setBounds(40, 256, 280, 20);
        card.add(lblPass);

        tfPassword = new JPasswordField();
        styleModernField(tfPassword, "Enter your password");
        tfPassword.setBounds(40, 280, 280, 42);
        card.add(tfPassword);

        // ── Error label ───────────────────────────────────────────────────────
        lblError = new JLabel("", SwingConstants.CENTER);
        lblError.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblError.setForeground(new Color(255, 100, 100));
        lblError.setBounds(40, 328, 280, 20);
        card.add(lblError);

        // ── Login button ──────────────────────────────────────────────────────
        JButton btnLogin = new JButton("SIGN IN  →") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color top = getModel().isRollover() ? new Color(0x56CCF2) : new Color(0x2F80ED);
                Color bot = getModel().isRollover() ? new Color(0x2F80ED) : new Color(0x1A6CC7);
                GradientPaint gp = new GradientPaint(0, 0, top, 0, getHeight(), bot);
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 22, 22);
                if (getModel().isPressed()) {
                    g2.setColor(new Color(0, 0, 0, 40));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 22, 22);
                }
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 14));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(), (getWidth() - fm.stringWidth(getText())) / 2,
                              (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
            }
        };
        btnLogin.setBounds(40, 355, 280, 46);
        btnLogin.setContentAreaFilled(false);
        btnLogin.setBorderPainted(false);
        btnLogin.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnLogin.addActionListener(e -> doLogin());
        tfPassword.addActionListener(e -> doLogin());
        card.add(btnLogin);

        // ── Hint labels ───────────────────────────────────────────────────────
        JLabel hint = new JLabel("owner • doctor • assistant", SwingConstants.CENTER);
        hint.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        hint.setForeground(new Color(180, 200, 255, 150));
        hint.setBounds(0, 415, 360, 18);
        card.add(hint);

        panel.setPreferredSize(new Dimension(480, 580));
        return panel;
    }

    private void styleModernField(JTextField field, String placeholder) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setForeground(Color.WHITE);
        field.setCaretColor(Color.WHITE);
        field.setOpaque(false);
        field.setBorder(BorderFactory.createCompoundBorder(
            new RoundBorder(new Color(255, 255, 255, 70), 11),
            BorderFactory.createEmptyBorder(8, 14, 8, 14)
        ));
        field.setBackground(new Color(255, 255, 255, 0));
        // Paint translucent background manually
        field.setUI(new javax.swing.plaf.basic.BasicTextFieldUI() {
            @Override
            protected void paintBackground(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 255, 255, 30));
                g2.fillRoundRect(0, 0, field.getWidth(), field.getHeight(), 11, 11);
            }
        });
    }

    // Rounded border helper
    static class RoundBorder extends AbstractBorder {
        Color color; int radius;
        RoundBorder(Color c, int r) { color = c; radius = r; }
        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawRoundRect(x, y, w - 1, h - 1, radius, radius);
        }
        @Override public Insets getBorderInsets(Component c) { return new Insets(4, 4, 4, 4); }
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
        lblError.setText("❌  Invalid username or password. try again!");
        tfPassword.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginPage());
    }
}