import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

public class ChatWindow extends JFrame {
    private JTextArea chatArea;
    private JTextField inputField;
    private JButton sendButton;
    private String chatFilePath;

    public ChatWindow(String currentUser, String chatWithUser) {
        chatFilePath = "chats/chat_" + currentUser + "_" + chatWithUser + ".txt";

        setTitle("Chat with " + chatWithUser);
        setSize(400, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        add(new JScrollPane(chatArea), BorderLayout.CENTER);

        inputField = new JTextField();
        sendButton = new JButton("Send");

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        add(inputPanel, BorderLayout.SOUTH);

        loadOldChats();

        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String message = currentUser + ": " + inputField.getText().trim() + "\n";
                chatArea.append(message);
                saveChat(message);
                inputField.setText("");
            }
        });
    }

    private void loadOldChats() {
        File chatFile = new File(chatFilePath);
        if (chatFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(chatFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    chatArea.append(line + "\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveChat(String message) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(chatFilePath, true))) {
            writer.write(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
