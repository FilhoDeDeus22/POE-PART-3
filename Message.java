import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Message {
    private final String messageID;
    private final String recipient;
    private final String messageContent;
    private String messageHash;
    private boolean isSent;
    private boolean isStored;
    private static int totalMessagesSent = 0;
    private static int messageCounter = 0;
    private static final List<Message> allMessages = new ArrayList<>();
    private static final String STORAGE_FILE = "messages.txt";

    public Message(String recipient, String messageContent) {
        this.messageID = generateMessageID();
        this.recipient = recipient;
        this.messageContent = messageContent;
        this.messageHash = "";
        this.isSent = false;
        this.isStored = false;
        messageCounter++;
    }

    // Method to generate random 10-digit message ID
    private String generateMessageID() {
        Random rand = new Random();
        long id = 1000000000L + (long)(rand.nextDouble() * 9000000000L);
        return String.valueOf(id);
    }

    // Check if message ID is valid (not more than 10 characters)
    public boolean checkMessageID() {
        return this.messageID != null && this.messageID.length() <= 10;
    }

    // Check recipient cell number format
    public int checkRecipientCell() {
        if (this.recipient == null || this.recipient.isEmpty()) {
            return -1; // Invalid - empty
        }

        // Remove any non-digit characters except leading +
        String cleanNumber = this.recipient.replaceAll("[^+0-9]", "");

        // Check if starts with international code (+)
        if (!cleanNumber.startsWith("+")) {
            return -2; // Missing international code
        }

        // Remove + for digit count check
        String digitsOnly = cleanNumber.substring(1);

        if (digitsOnly.length() > 10) {
            return -3; // Too long
        }

        if (digitsOnly.length() < 10) {
            return -4; // Too short
        }

        // Check if all characters after + are digits
        if (!digitsOnly.matches("\\d+")) {
            return -5; // Contains non-digit characters
        }

        return 0; // Success
    }

    // Create message hash
    public String createMessageHash() {
        if (this.messageContent == null || this.messageContent.isEmpty()) {
            return "00:0:EMPTY";
        }

        String[] words = this.messageContent.trim().split("\\s+");
        String firstWord = words.length > 0 ? words[0].replaceAll("[^a-zA-Z]", "").toUpperCase() : "";
        String lastWord = words.length > 1 ? words[words.length - 1].replaceAll("[^a-zA-Z]", "").toUpperCase() : firstWord;

        // Get first two numbers of message ID
        String firstTwoNumbers = this.messageID.length() >= 2 ? this.messageID.substring(0, 2) : "00";

        this.messageHash = firstTwoNumbers + ":" + (messageCounter - 1) + ":" + firstWord + lastWord;
        return this.messageHash;
    }

    // Handle send message options
    public String SentMessage(int choice) {
        switch (choice) {
            case 1: // Send Message
                this.isSent = true;
                totalMessagesSent++;
                allMessages.add(this);
                return "Message successfully sent.";

            case 2: // Disregard Message
                return "Press 0 to delete message.";

            case 3: // Store Message
                this.isStored = true;
                storeMessage();
                return "Message successfully stored.";

            default:
                return "Invalid choice.";
        }
    }

    // Store message in simple text format (no JSON)
    public void storeMessage() {
        try {
            FileWriter writer = new FileWriter(STORAGE_FILE, true); // append mode
            writer.write("MessageID: " + this.messageID + "\n");
            writer.write("Recipient: " + this.recipient + "\n");
            writer.write("Message: " + this.messageContent + "\n");
            writer.write("Hash: " + this.messageHash + "\n");
            writer.write("Timestamp: " + System.currentTimeMillis() + "\n");
            writer.write("Status: stored\n");
            writer.write("--------------------\n");
            writer.close();

            System.out.println("Message stored in file: " + STORAGE_FILE);

        } catch (Exception e) {
            System.out.println("Error storing message: " + e.getMessage());
        }
    }

    // Check message length
    public String checkMessageLength() {
        if (this.messageContent == null) {
            return "Message is empty.";
        }

        int length = this.messageContent.length();
        if (length <= 250) {
            return "Message ready to send.";
        } else {
            int excess = length - 250;
            return "Message exceeds 250 characters by " + excess + ", please reduce size.";
        }
    }

    // Getters
    public String getMessageID() { return messageID; }
    public String getRecipient() { return recipient; }
    public String getMessageContent() { return messageContent; }
    public String getMessageHash() { return messageHash; }
    public boolean isSent() {
        return isSent; }

    // Static methods
    public static String printMessages() {
        if (allMessages.isEmpty()) {
            return "No messages sent yet.";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("All Sent Messages:\n");
        for (int i = 0; i < allMessages.size(); i++) {
            Message msg = allMessages.get(i);
            if (msg.isSent) {
                sb.append(i + 1).append(". MessageID: ").append(msg.getMessageID())
                        .append(", Hash: ").append(msg.getMessageHash())
                        .append(", Recipient: ").append(msg.getRecipient())
                        .append(", Message: ").append(msg.getMessageContent())
                        .append("\n");
            }
        }
        return sb.toString();
    }

    public static int returnTotalMessages() {
        return totalMessagesSent;
    }

    public static void clearMessages() {
        allMessages.clear();
        totalMessagesSent = 0;
        messageCounter = 0;
    }

    // Method to get stored messages from file
    public static String getStoredMessages() {
        try {
            File file = new File(STORAGE_FILE);
            if (!file.exists()) {
                return "No stored messages found.";
            }

            BufferedReader reader = new BufferedReader(new FileReader(file));
            StringBuilder content = new StringBuilder();
            String line;
            content.append("Stored Messages:\n");
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
            reader.close();
            return content.toString();
        } catch (Exception e) {
            return "Error retrieving stored messages: " + e.getMessage();
        }
    }
}