public class LoginTest {

    public static void main(String[] args) {
        System.out.println("=== Running Tests ===");

        testCheckUserNameCorrect();
        testCheckUserNameIncorrect();
        testCheckPasswordComplexityCorrect();
        testCheckPasswordComplexityIncorrect();
        testCheckCellPhoneNumberCorrect();
        testCheckCellPhoneNumberIncorrect();
        testRegisterUserSuccess();
        testLoginUserSuccess();
        testLoginUserFailure();

        System.out.println("=== All tests completed! ===");
    }

    public static void testCheckUserNameCorrect() {
        Login login = new Login("Test", "User");
        boolean result = login.checkUserName("kyl_1");
        System.out.println("✓ testCheckUserNameCorrect: " + (result ? "PASS" : "FAIL"));
    }

    public static void testCheckUserNameIncorrect() {
        Login login = new Login("Test", "User");
        boolean result = login.checkUserName("kyle!!!!!!");
        System.out.println("✓ testCheckUserNameIncorrect: " + (!result ? "PASS" : "FAIL"));
    }

    public static void testCheckPasswordComplexityCorrect() {
        Login login = new Login("Test", "User");
        boolean result = login.checkPasswordComplexity("Password1!");
        System.out.println("✓ testCheckPasswordComplexityCorrect: " + (result ? "PASS" : "FAIL"));
    }

    public static void testCheckPasswordComplexityIncorrect() {
        Login login = new Login("Test", "User");
        boolean result = login.checkPasswordComplexity("password");
        System.out.println("✓ testCheckPasswordComplexityIncorrect: " + (!result ? "PASS" : "FAIL"));
    }

    public static void testCheckCellPhoneNumberCorrect() {
        Login login = new Login("Test", "User");
        boolean result = login.checkCellPhoneNumber("+27123456789");
        System.out.println("✓ testCheckCellPhoneNumberCorrect: " + (result ? "PASS" : "FAIL"));
    }

    public static void testCheckCellPhoneNumberIncorrect() {
        Login login = new Login("Test", "User");
        boolean result = login.checkCellPhoneNumber("0712345678");
        System.out.println("✓ testCheckCellPhoneNumberIncorrect: " + (!result ? "PASS" : "FAIL"));
    }

    public static void testRegisterUserSuccess() {
        Login login = new Login("Kyle", "Smith");
        String result = login.registerUser("kyl_1", "Password1!", "+27123456789");
        boolean passed = !result.contains("not correctly");
        System.out.println("✓ testRegisterUserSuccess: " + (passed ? "PASS" : "FAIL"));
    }

    public static void testLoginUserSuccess() {
        Login login = new Login("Kyle", "Smith");
        login.registerUser("kyl_1", "Password1!", "+27123456789");
        boolean result = login.loginUser("kyl_1", "Password1!");
        System.out.println("✓ testLoginUserSuccess: " + (result ? "PASS" : "FAIL"));
    }

    public static void testLoginUserFailure() {
        Login login = new Login("Kyle", "Smith");
        login.registerUser("kyl_1", "Password1!", "+27123456789");
        boolean result = login.loginUser("wrong", "wrong");
        System.out.println("✓ testLoginUserFailure: " + (!result ? "PASS" : "FAIL"));
    }
}