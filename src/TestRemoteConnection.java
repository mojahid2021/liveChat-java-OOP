import java.sql.Connection;
import java.sql.SQLException;

public class TestRemoteConnection {
    public static void main(String[] args) {
        try (Connection connection = DBConnection.getConnection()) {
            System.out.println("Connection to remote MySQL server successful!");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to connect to remote MySQL server.");
        }
    }
}
