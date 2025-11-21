import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Message {
    private String messageID;
    private final String recipient;
    private final String messageContent;
    private String messageHash;
    private boolean isSent;
    private boolean isStored;
    private String timestamp;
    private static int totalMessagesSent = 0;
    private static int messageCounter = 0;
    private static final List<Message> allMessages = new ArrayList<>();
    private static final String STORAGE_FILE = "messages.json";

    public Message(String recipient, String messageContent) {
        this.messageID = generateMessageID();
        this.recipient = recipient;
        this.messageContent = messageContent;
        this.messageHash = "";
        this.isSent = false;
        this.isStored = false;
        this.timestamp = String.valueOf(System.currentTimeMillis());
        messageCounter++;
    }

    // Fixed: Added complete method implementation
    public static String getStoredMessagesFormatted() {
        try {
            List<Message> storedMessages = loadStoredMessages();
            if (storedMessages.isEmpty()) {
                return "No stored messages found.";
            }

            StringBuilder content = new StringBuilder();
            content.append("Stored Messages (Formatted):\n");
            for (int i = 0; i < storedMessages.size(); i++) {
                Message msg = storedMessages.get(i);
                content.append(i + 1).append(". MessageID: ").append(msg.getMessageID())
                        .append(", Recipient: ").append(msg.getRecipient())
                        .append(", Message: ").append(msg.getMessageContent())
                        .append(", Hash: ").append(msg.getMessageHash())
                        .append(", Timestamp: ").append(msg.getTimestamp())
                        .append("\n");
            }
            return content.toString();
        } catch (Exception e) {
            return "Error retrieving stored messages: " + e.getMessage();
        }
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

    // Store message in JSON format
    public void storeMessage() {
        try {
            // Load existing messages
            List<Message> storedMessages = loadStoredMessages();

            // Add current message
            storedMessages.add(this);

            // Save all messages back to file
            saveMessagesToFile(storedMessages);

            System.out.println("Message stored in JSON file: " + STORAGE_FILE);

        } catch (Exception e) {
            System.out.println("Error storing message: " + e.getMessage());
        }
    }

    // Load stored messages from JSON file
    private static List<Message> loadStoredMessages() {
        List<Message> messages = new ArrayList<>();
        try {
            File file = new File(STORAGE_FILE);
            if (!file.exists()) {
                return messages;
            }

            BufferedReader reader = new BufferedReader(new FileReader(file));
            StringBuilder jsonContent = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonContent.append(line);
            }
            reader.close();

            // Simple JSON parsing - extract message objects
            String json = jsonContent.toString().trim();
            if (json.startsWith("[") && json.endsWith("]")) {
                json = json.substring(1, json.length() - 1);
                String[] messageObjects = json.split("},\\{");

                for (String messageObj : messageObjects) {
                    Message msg = parseMessageFromJson(messageObj);
                    if (msg != null) {
                        messages.add(msg);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error loading stored messages: " + e.getMessage());
        }
        return messages;
    }

    // Parse a message from JSON string
    private static Message parseMessageFromJson(String json) {
        try {
            String recipient = extractJsonField(json, "recipient");
            String messageContent = extractJsonField(json, "messageContent");

            if (recipient != null && messageContent != null) {
                Message message = new Message(recipient, messageContent);
                message.messageHash = extractJsonField(json, "messageHash");
                message.messageID = extractJsonField(json, "messageID");
                message.timestamp = extractJsonField(json, "timestamp");
                message.isStored = true;
                return message;
            }
        } catch (Exception e) {
            System.out.println("Error parsing message from JSON: " + e.getMessage());
        }
        return null;
    }

    // Extract field from JSON string
    private static String extractJsonField(String json, String fieldName) {
        try {
            String search = "\"" + fieldName + "\":\"";
            int start = json.indexOf(search);
            if (start == -1) return null;
            start += search.length();
            int end = json.indexOf("\"", start);
            if (end == -1) return null;
            return json.substring(start, end);
        } catch (Exception e) {
            return null;
        }
    }

    // Save messages to JSON file
    private static void saveMessagesToFile(List<Message> messages) throws IOException {
        FileWriter writer = new FileWriter(STORAGE_FILE);
        writer.write("[\n");

        for (int i = 0; i < messages.size(); i++) {
            Message msg = messages.get(i);
            writer.write("  {\n");
            writer.write("    \"messageID\": \"" + escapeJson(msg.messageID) + "\",\n");
            writer.write("    \"recipient\": \"" + escapeJson(msg.recipient) + "\",\n");
            writer.write("    \"messageContent\": \"" + escapeJson(msg.messageContent) + "\",\n");
            writer.write("    \"messageHash\": \"" + escapeJson(msg.messageHash) + "\",\n");
            writer.write("    \"isSent\": " + msg.isSent + ",\n");
            writer.write("    \"isStored\": " + msg.isStored + ",\n");
            writer.write("    \"timestamp\": \"" + msg.timestamp + "\"\n");
            writer.write("  }");

            if (i < messages.size() - 1) {
                writer.write(",");
            }
            writer.write("\n");
        }

        writer.write("]");
        writer.close();
    }

    // Helper method to escape JSON special characters
    private static String escapeJson(String text) {
        if (text == null) return "";
        return text.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\b", "\\b")
                .replace("\f", "\\f")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
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

    public String getTimestamp() { return timestamp; }

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

    // Method to get stored messages from JSON file
    public static String getStoredMessages() {
        try {
            List<Message> storedMessages = loadStoredMessages();
            if (storedMessages.isEmpty()) {
                return "No stored messages found.";
            }

            StringBuilder content = new StringBuilder();
            content.append("Stored Messages (JSON):\n");
            for (int i = 0; i < storedMessages.size(); i++) {
                Message msg = storedMessages.get(i);
                content.append(i + 1).append(". MessageID: ").append(msg.getMessageID())
                        .append(", Recipient: ").append(msg.getRecipient())
                        .append(", Message: ").append(msg.getMessageContent())
                        .append(", Hash: ").append(msg.getMessageHash())
                        .append(", Timestamp: ").append(msg.getTimestamp())
                        .append("\n");
            }
            return content.toString();
        } catch (Exception e) {
            return "Error retrieving stored messages: " + e.getMessage();
        }
    }

    // Method to clear stored messages from JSON file
    public static void clearStoredMessages() {
        try {
            FileWriter writer = new FileWriter(STORAGE_FILE);
            writer.write("[]");
            writer.close();
            System.out.println("All stored messages cleared from JSON file.");
        } catch (Exception e) {
            System.out.println("Error clearing stored messages: " + e.getMessage());
        }
    }
}