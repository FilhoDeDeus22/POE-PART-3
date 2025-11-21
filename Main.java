import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;

public class Main {
    private static Login currentUser = null;
    private static List<Message> currentSessionMessages = new ArrayList<>();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("=== Welcome to QuickChat ===");

        boolean running = true;

        while (running) {
            displayMainMenu();
            int mainChoice = getIntInput("Enter your choice (1-3): ");

            switch (mainChoice) {
                case 1:
                    registerAndLogin();
                    break;
                case 2:
                    if (currentUser != null) {
                        quickChatApplication();
                    } else {
                        System.out.println("Please login first.");
                    }
                    break;
                case 3:
                    running = false;
                    System.out.println("Thank you for using our application. Goodbye!");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
        scanner.close();
    }

    private static void displayMainMenu() {
        System.out.println("\n=== Main Menu ===");
        System.out.println("1. Register & Login");
        System.out.println("2. QuickChat Messaging");
        System.out.println("3. Exit");
    }

    private static void registerAndLogin() {
        System.out.println("\n=== Registration ===");
        System.out.print("Enter your first name: ");
        String firstName = scanner.nextLine();

        System.out.print("Enter your last name: ");
        String lastName = scanner.nextLine();

        currentUser = new Login(firstName, lastName);

        System.out.print("Enter username (must contain '_' and be ≤5 characters): ");
        String username = scanner.nextLine();

        System.out.print("Enter password (≥8 chars, capital, number, special char): ");
        String password = scanner.nextLine();

        System.out.print("Enter cellphone (+27 format, e.g., +27123456789): ");
        String cellphone = scanner.nextLine();

        String result = currentUser.registerUser(username, password, cellphone);
        System.out.println(result);

        if (result.contains("successfully")) {
            // Auto-login after successful registration
            System.out.println(currentUser.getWelcomeMessage());
        } else {
            currentUser = null; // Reset if registration failed
        }
    }

    private static void quickChatApplication() {
        System.out.println("\n=== Welcome to QuickChat ===");

        boolean inQuickChat = true;
        while (inQuickChat) {
            displayQuickChatMenu();
            int choice = getIntInput("Enter your choice (1-4): ");

            switch (choice) {
                case 1:
                    sendMessages();
                    break;
                case 2:
                    showRecentMessages();
                    break;
                case 3:
                    showStoredMessages();
                    break;
                case 4:
                    inQuickChat = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void displayQuickChatMenu() {
        System.out.println("\n=== QuickChat Menu ===");
        System.out.println("1. Send Messages");
        System.out.println("2. Show recently sent messages");
        System.out.println("3. Show stored messages");
        System.out.println("4. Back to Main Menu");
    }

    private static void sendMessages() {
        int numMessages = getIntInput("How many messages do you wish to enter? ");
        if (numMessages <= 0) {
            System.out.println("Please enter a positive number.");
            return;
        }

        for (int i = 0; i < numMessages; i++) {
            System.out.println("\nCreating Message " + (i + 1) + " of " + numMessages);

            // Get recipient number
            System.out.print("Enter recipient cell number (with international code, e.g., +27123456789): ");
            String recipient = scanner.nextLine();

            // Get message content
            System.out.print("Enter your message (max 250 characters): ");
            String messageContent = scanner.nextLine();

            // Create and validate message
            processMessage(recipient, messageContent, i);
        }

        // Display total messages sent
        System.out.println("Total messages sent in this session: " + Message.returnTotalMessages());
    }

    private static void processMessage(String recipient, String messageContent, int messageIndex) {
        Message message = new Message(recipient, messageContent);

        // Validate message ID
        if (!message.checkMessageID()) {
            System.out.println("Invalid Message ID generated.");
            return;
        }

        // Validate recipient number
        int recipientCheck = message.checkRecipientCell();
        if (recipientCheck != 0) {
            String errorMsg = getRecipientErrorMessage(recipientCheck);
            System.out.println(errorMsg);
            return;
        }

        // Check message length
        String lengthCheck = message.checkMessageLength();
        if (!lengthCheck.equals("Message ready to send.")) {
            System.out.println(lengthCheck);
            return;
        }

        // Create message hash
        String hash = message.createMessageHash();
        System.out.println("Message Hash: " + hash);
        System.out.println("Message ID generated: " + message.getMessageID());

        // Send message options
        int sendChoice = getSendMessageChoice();
        String result = message.SentMessage(sendChoice);
        System.out.println(result);

        // If message was sent, display details and add to session
        if (sendChoice == 1) {
            currentSessionMessages.add(message);
            displayMessageDetails(message);
        }

        // If user chose to disregard and confirmed deletion
        if (sendChoice == 2) {
            System.out.print("Press 0 to delete message, any other key to cancel: ");
            String deleteConfirm = scanner.nextLine();
            if ("0".equals(deleteConfirm)) {
                System.out.println("Message deleted.");
            }
        }
    }

    private static int getSendMessageChoice() {
        System.out.println("\nChoose an option:");
        System.out.println("1. Send Message");
        System.out.println("2. Disregard Message");
        System.out.println("3. Store Message to send later");
        return getIntInput("Enter your choice (1-3): ");
    }

    private static String getRecipientErrorMessage(int errorCode) {
        switch (errorCode) {
            case -1: return "Cell phone number is empty.";
            case -2: return "Cell phone number is incorrectly formatted or does not contain an international code. Please correct the number and try again.";
            case -3: return "Cell phone number is too long.";
            case -4: return "Cell phone number is too short.";
            case -5: return "Cell phone number contains invalid characters.";
            default: return "Cell phone number is invalid.";
        }
    }

    private static void displayMessageDetails(Message message) {
        System.out.println("\n=== Message Details ===");
        System.out.println("MessageID: " + message.getMessageID());
        System.out.println("Message Hash: " + message.getMessageHash());
        System.out.println("Recipient: " + message.getRecipient());
        System.out.println("Message: " + message.getMessageContent());
        System.out.println("Message successfully sent!");
    }

    private static void showRecentMessages() {
        String recentMessages = Message.printMessages();
        System.out.println("\n" + recentMessages);
    }

    private static void showStoredMessages() {
        String storedMessages = Message.getStoredMessages();
        System.out.println("\n" + storedMessages);
    }

    private static int getIntInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }
}