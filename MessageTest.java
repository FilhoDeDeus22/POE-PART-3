import java.io.File;

public class MessageTest {

    public static void main(String[] args) {
        System.out.println("=== Running Message Tests ===\n");

        // Clear any previous test data
        Message.clearMessages();

        // Run all tests
        testCheckMessageID();
        testCheckRecipientCell();
        testCreateMessageHash();
        testCheckMessageLength();
        testSentMessage();
        testStoreMessage();
        testPrintMessages();
        testReturnTotalMessages();

        // Test specific cases from requirements
        testRequirementTestCases();

        System.out.println("\n=== All Message tests completed! ===");
    }

    public static void testCheckMessageID() {
        System.out.println("1. Testing checkMessageID():");
        Message message = new Message("+27123456789", "Test message");
        boolean result = message.checkMessageID();
        System.out.println("   ✓ Message ID validation: " + (result ? "PASS" : "FAIL"));
        System.out.println("   Generated Message ID: " + message.getMessageID());
    }

    public static void testCheckRecipientCell() {
        System.out.println("\n2. Testing checkRecipientCell():");

        // Test valid number
        Message validMessage = new Message("+27123456789", "Test message");
        int validResult = validMessage.checkRecipientCell();
        System.out.println("   ✓ Valid number (+27123456789): " + (validResult == 0 ? "PASS" : "FAIL"));

        // Test missing international code
        Message invalidMessage1 = new Message("0712345678", "Test message");
        int invalidResult1 = invalidMessage1.checkRecipientCell();
        System.out.println("   ✓ Missing international code (0712345678): " + (invalidResult1 == -2 ? "PASS" : "FAIL"));

        // Test too short number
        Message invalidMessage2 = new Message("+27123456", "Test message");
        int invalidResult2 = invalidMessage2.checkRecipientCell();
        System.out.println("   ✓ Too short number (+27123456): " + (invalidResult2 == -4 ? "PASS" : "FAIL"));

        // Test too long number
        Message invalidMessage3 = new Message("+271234567890", "Test message");
        int invalidResult3 = invalidMessage3.checkRecipientCell();
        System.out.println("   ✓ Too long number (+271234567890): " + (invalidResult3 == -3 ? "PASS" : "FAIL"));
    }

    public static void testCreateMessageHash() {
        System.out.println("\n3. Testing createMessageHash():");

        // Test case 1: Normal message
        Message message1 = new Message("+27123456789", "Hi Mike join us tonight");
        String hash1 = message1.createMessageHash();
        System.out.println("   ✓ Normal message hash: " + hash1);

        // Test case 2: Single word message
        Message message2 = new Message("+27123456789", "Hello");
        String hash2 = message2.createMessageHash();
        System.out.println("   ✓ Single word hash: " + hash2);

        // Test case 3: Empty message
        Message message3 = new Message("+27123456789", "");
        String hash3 = message3.createMessageHash();
        System.out.println("   ✓ Empty message hash: " + hash3);
    }

    public static void testCheckMessageLength() {
        System.out.println("\n4. Testing checkMessageLength():");

        // Test short message
        Message shortMsg = new Message("+27123456789", "Short message");
        String shortResult = shortMsg.checkMessageLength();
        boolean shortPass = shortResult.equals("Message ready to send.");
        System.out.println("   ✓ Short message (under 250): " + (shortPass ? "PASS" : "FAIL"));

        // Test long message
        String longText = "This is a very long message that exceeds the 250 character limit. "
                + "This is a very long message that exceeds the 250 character limit. "
                + "This is a very long message that exceeds the 250 character limit. "
                + "This should be over 250 characters for sure.";
        Message longMsg = new Message("+27123456789", longText);
        String longResult = longMsg.checkMessageLength();
        boolean longPass = longResult.contains("exceeds 250 characters");
        System.out.println("   ✓ Long message (over 250): " + (longPass ? "PASS" : "FAIL"));
        System.out.println("   Long message result: " + longResult);
    }

    public static void testSentMessage() {
        System.out.println("\n5. Testing SentMessage():");

        Message.clearMessages(); // Reset counter

        Message message = new Message("+27123456789", "Test message for sending");

        // Test Send Message
        String sendResult = message.SentMessage(1);
        boolean sendPass = sendResult.equals("Message successfully sent.");
        System.out.println("   ✓ Send Message: " + (sendPass ? "PASS" : "FAIL"));

        // Test Store Message
        Message storeMessage = new Message("+27123456789", "Test message for storage");
        String storeResult = storeMessage.SentMessage(3);
        boolean storePass = storeResult.equals("Message successfully stored.");
        System.out.println("   ✓ Store Message: " + (storePass ? "PASS" : "FAIL"));

        // Test Disregard Message
        Message disregardMessage = new Message("+27123456789", "Test message for disregard");
        String disregardResult = disregardMessage.SentMessage(2);
        boolean disregardPass = disregardResult.equals("Press 0 to delete message.");
        System.out.println("   ✓ Disregard Message: " + (disregardPass ? "PASS" : "FAIL"));

        // Test Invalid Choice
        Message invalidMessage = new Message("+27123456789", "Test invalid choice");
        String invalidResult = invalidMessage.SentMessage(99);
        boolean invalidPass = invalidResult.equals("Invalid choice.");
        System.out.println("   ✓ Invalid Choice: " + (invalidPass ? "PASS" : "FAIL"));
    }

    public static void testStoreMessage() {
        System.out.println("\n6. Testing storeMessage():");

        // Create a test message and store it
        Message message = new Message("+27831234567", "This is a test message for storage");
        message.createMessageHash();
        message.storeMessage();

        // Check if file was created
        File file = new File("messages.txt");
        boolean fileExists = file.exists();
        System.out.println("   ✓ Storage file created: " + (fileExists ? "PASS" : "FAIL"));

        if (fileExists) {
            String storedMessages = Message.getStoredMessages();
            boolean containsMessage = storedMessages.contains("This is a test message for storage");
            System.out.println("   ✓ Message stored correctly: " + (containsMessage ? "PASS" : "FAIL"));
        }
    }

    public static void testPrintMessages() {
        System.out.println("\n7. Testing printMessages():");

        Message.clearMessages(); // Reset for clean test

        // Send some messages
        Message msg1 = new Message("+27111111111", "First test message");
        msg1.SentMessage(1);

        Message msg2 = new Message("+27222222222", "Second test message");
        msg2.SentMessage(1);

        String printedMessages = Message.printMessages();
        boolean containsFirst = printedMessages.contains("First test message");
        boolean containsSecond = printedMessages.contains("Second test message");

        System.out.println("   ✓ Print messages contains first: " + (containsFirst ? "PASS" : "FAIL"));
        System.out.println("   ✓ Print messages contains second: " + (containsSecond ? "PASS" : "FAIL"));
        System.out.println("   Printed messages:\n" + printedMessages);
    }

    public static void testReturnTotalMessages() {
        System.out.println("\n8. Testing returnTotalMessages():");

        Message.clearMessages(); // Reset counter

        // Send 3 messages
        new Message("+27123456789", "Message 1").SentMessage(1);
        new Message("+27123456789", "Message 2").SentMessage(1);
        new Message("+27123456789", "Message 3").SentMessage(1);

        int total = Message.returnTotalMessages();
        boolean correctCount = (total == 3);
        System.out.println("   ✓ Total messages count (expected 3): " + (correctCount ? "PASS" : "FAIL"));
        System.out.println("   Actual count: " + total);
    }

    public static void testRequirementTestCases() {
        System.out.println("\n9. Testing Requirement Test Cases:");

        Message.clearMessages(); // Reset for clean test

        // Test Case 1 from requirements: +27718693002
        System.out.println("   Test Case 1 (from requirements):");
        Message test1 = new Message("+27718693002", "Hi Mike, can you join us for dinner tonight");
        test1.createMessageHash();
        String hash1 = test1.getMessageHash();
        System.out.println("     Recipient: +27718693002");
        System.out.println("     Message: Hi Mike, can you join us for dinner tonight");
        System.out.println("     Generated Hash: " + hash1);
        System.out.println("     Expected Format: XX:X:HITONIGHT");
        boolean hash1Valid = hash1.matches("\\d{2}:\\d+:HITONIGHT");
        System.out.println("     ✓ Hash format correct: " + (hash1Valid ? "PASS" : "FAIL"));

        // Test Case 2 from requirements: 08575975889 (should fail validation)
        System.out.println("\n   Test Case 2 (from requirements):");
        Message test2 = new Message("08575975889", "Hi Keegan, did you receive the payment?");
        int recipientCheck = test2.checkRecipientCell();
        System.out.println("     Recipient: 08575975889");
        System.out.println("     Message: Hi Keegan, did you receive the payment?");
        System.out.println("     Recipient validation result: " + recipientCheck);
        System.out.println("     Expected: Should fail (missing international code)");
        boolean recipient2Valid = (recipientCheck == -2); // Should be -2 (missing international code)
        System.out.println("     ✓ Correctly identified missing international code: " + (recipient2Valid ? "PASS" : "FAIL"));

        // Test message length validation
        System.out.println("\n   Test Case 3 (Message length validation):");
        String shortMessage = "Short message";
        Message test3 = new Message("+27123456789", shortMessage);
        String lengthCheck3 = test3.checkMessageLength();
        System.out.println("     Short message length check: " + lengthCheck3);
        System.out.println("     ✓ Short message validation: " +
                (lengthCheck3.equals("Message ready to send.") ? "PASS" : "FAIL"));

        // Test long message
        String longMessage = "A".repeat(300);
        Message test4 = new Message("+27123456789", longMessage);
        String lengthCheck4 = test4.checkMessageLength();
        System.out.println("     Long message length check: " + lengthCheck4);
        System.out.println("     ✓ Long message validation: " +
                (lengthCheck4.contains("exceeds 250 characters") ? "PASS" : "FAIL"));
    }
}
