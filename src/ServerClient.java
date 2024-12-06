import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ServerClient implements ActionListener {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/chat_app";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "";

    private JPanel loginPanel, chatPanel;
    private JTextField text;
    private JPanel messageArea;
    private static Box vertical = Box.createVerticalBox();
    private static JFrame f = new JFrame();
    private static DataOutputStream dout;
    private static DataInputStream din;
    private Point initialClick;

    ServerClient() {
        f.setLayout(new CardLayout());

        // Initialize Panels
        loginPanel = createLoginPanel();
        chatPanel = createChatPanel();

        // Add Panels to Frame
        f.add(loginPanel, "Login");
        f.add(chatPanel, "Chat");

        // Frame Settings
        f.setSize(450, 680);
        f.setLocation(200, 50);
        f.setUndecorated(true);
        f.setVisible(true);
    }

    private JPanel createLoginPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(null);

        JLabel loginLabel = new JLabel("Login to Chat");
        loginLabel.setBounds(140, 50, 200, 30);
        loginLabel.setFont(new Font("SAN_SERIF", Font.BOLD, 20));
        panel.add(loginLabel);

        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setBounds(50, 120, 100, 30);
        panel.add(usernameLabel);

        JTextField usernameField = new JTextField();
        usernameField.setBounds(150, 120, 200, 30);
        panel.add(usernameField);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setBounds(50, 170, 100, 30);
        panel.add(passwordLabel);

        JPasswordField passwordField = new JPasswordField();
        passwordField.setBounds(150, 170, 200, 30);
        panel.add(passwordField);

        JButton loginButton = new JButton("Login");
        loginButton.setBounds(150, 230, 200, 30);
        loginButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            if (login(username, password)) {
                JOptionPane.showMessageDialog(f, "Login Successful!");
                showChatPanel();
            } else {
                JOptionPane.showMessageDialog(f, "Invalid credentials!");
            }
        });
        panel.add(loginButton);

        JLabel registerText = new JLabel("Don't have an account? Click here to register");
        registerText.setBounds(120, 270, 250, 30);
        registerText.setForeground(Color.BLUE);
        registerText.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        registerText.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                showRegisterDialog();
            }
        });
        panel.add(registerText);

        return panel;
    }

    private JPanel createChatPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(null);

        JPanel header = new JPanel();
        header.setBackground(new Color(7, 94, 84));
        header.setBounds(0, 0, 450, 70);
        header.setLayout(null);
        panel.add(header);

        JLabel name = new JLabel("Chat Application");
        name.setBounds(140, 20, 200, 20);
        name.setForeground(Color.WHITE);
        name.setFont(new Font("SAN_SERIF", Font.BOLD, 18));
        header.add(name);

        messageArea = new JPanel();
        messageArea.setBounds(5, 75, 440, 450);
        messageArea.setLayout(new BorderLayout());
        panel.add(messageArea);

        text = new JTextField();
        text.setBounds(5, 530, 310, 40);
        text.setFont(new Font("SAN_SERIF", Font.PLAIN, 16));
        panel.add(text);

        JButton send = new JButton("Send");
        send.setBounds(320, 530, 123, 40);
        send.setBackground(new Color(7, 94, 84));
        send.setForeground(Color.WHITE);
        send.addActionListener(this);
        send.setFont(new Font("SAN_SERIF", Font.PLAIN, 16));
        panel.add(send);

        return panel;
    }

    private void showRegisterDialog() {
        JDialog registerDialog = new JDialog(f, "Register", true);
        registerDialog.setLayout(new GridLayout(3, 2));

        JLabel usernameLabel = new JLabel("Username:");
        JTextField usernameField = new JTextField();

        JLabel passwordLabel = new JLabel("Password:");
        JPasswordField passwordField = new JPasswordField();

        JButton registerButton = new JButton("Register");
        registerButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            if (register(username, password)) {
                JOptionPane.showMessageDialog(f, "Registration successful! Please log in.");
                registerDialog.dispose();
            } else {
                JOptionPane.showMessageDialog(f, "Username already exists!");
            }
        });

        registerDialog.add(usernameLabel);
        registerDialog.add(usernameField);
        registerDialog.add(passwordLabel);
        registerDialog.add(passwordField);
        registerDialog.add(new JLabel());
        registerDialog.add(registerButton);

        registerDialog.setSize(300, 150);
        registerDialog.setLocationRelativeTo(f);
        registerDialog.setVisible(true);
    }

    private boolean login(String username, String password) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement stmt = conn.prepareStatement("SELECT password FROM users WHERE username = ?")) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String storedPassword = rs.getString("password");
                return storedPassword.equals(password); // Use hashed password verification in production!
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean register(String username, String password) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO users (username, password) VALUES (?, ?)")) {
            stmt.setString(1, username);
            stmt.setString(2, password); // Use hashed password storage in production!
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void showChatPanel() {
        CardLayout cl = (CardLayout) f.getContentPane().getLayout();
        cl.show(f.getContentPane(), "Chat");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            String out = text.getText();
            JPanel p2 = formatLabel(out);
            messageArea.setLayout(new BorderLayout());
            JPanel right = new JPanel(new BorderLayout());
            right.add(p2, BorderLayout.LINE_END);
            vertical.add(right);
            vertical.add(Box.createVerticalStrut(15));
            messageArea.add(vertical, BorderLayout.PAGE_START);
            dout.writeUTF(out);
            text.setText("");
            f.repaint();
            f.invalidate();
            f.validate();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static JPanel formatLabel(String out) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        JLabel output = new JLabel("<html><p style=\"width: 150px\">" + out + "</p></html>");
        output.setFont(new Font("Tahoma", Font.PLAIN, 16));
        output.setBackground(new Color(37, 211, 102));
        output.setOpaque(true);
        output.setBorder(new EmptyBorder(15, 15, 15, 50));
        panel.add(output);

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        JLabel time = new JLabel();
        time.setText(sdf.format(cal.getTime()));
        panel.add(time);

        return panel;
    }
    public static void main(String[] args) {
        ServerClient serverClient = new ServerClient(); // Create an instance of ServerClient
        try {
            Socket s = new Socket("127.0.0.1", 6001);
            din = new DataInputStream(s.getInputStream());
            dout = new DataOutputStream(s.getOutputStream());

            // Start reading messages from the server
            while (true) {
                String msg = din.readUTF(); // Read message from server
                SwingUtilities.invokeLater(() -> {
                    JPanel panel = formatLabel(msg);
                    JPanel left = new JPanel(new BorderLayout());
                    left.add(panel, BorderLayout.LINE_START);
                    vertical.add(left);
                    vertical.add(Box.createVerticalStrut(15));
                    serverClient.messageArea.add(vertical, BorderLayout.PAGE_START); // Use the instance
                    f.validate();
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
