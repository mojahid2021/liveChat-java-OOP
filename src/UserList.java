import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class UserList extends JFrame {
    public UserList(String currentUser) {
        setTitle("User List");
        setSize(300, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(0, 1));

        try (Connection connection = DBConnection.getConnection()) {
            String sql = "SELECT username FROM users WHERE username != '" + currentUser + "'";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);

            while (resultSet.next()) {
                String username = resultSet.getString("username");
                JButton userButton = new JButton(username);
                userButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        new ChatWindow(currentUser, username).setVisible(true);
                    }
                });
                add(userButton);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
