public class Login {
    public String firstName;
    public String lastName;
    public String username;
    public String password;
    public String cellphone;

    public Login(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public boolean checkUserName(String username) {
        if (username.length() > 5) return false;
        if (!username.contains("_")) return false;
        return true;
    }

    public boolean checkPasswordComplexity(String password) {
        if (password.length() < 8) return false;

        boolean hasCapital = false;
        boolean hasNumber = false;
        boolean hasSpecial = false;

        for (int i = 0; i < password.length(); i++) {
            char c = password.charAt(i);
            if (Character.isUpperCase(c)) hasCapital = true;
            if (Character.isDigit(c)) hasNumber = true;
            if (!Character.isLetterOrDigit(c)) hasSpecial = true;
        }

        return hasCapital && hasNumber && hasSpecial;
    }

    public boolean checkCellPhoneNumber(String cellphone) {
        if (!cellphone.startsWith("+27")) return false;
        if (cellphone.length() != 12) return false;
        return true;
    }

    public String registerUser(String username, String password, String cellphone) {
        if (!checkUserName(username)) {
            return "Username is not correctly formatted, please ensure that your username contains an underscore and is no more than five characters in length.";
        }

        if (!checkPasswordComplexity(password)) {
            return "Password is not correctly formatted; please ensure that the password contains at least eight characters, a capital letter, a number, and a special character.";
        }

        if (!checkCellPhoneNumber(cellphone)) {
            return "Cell phone number incorrectly formatted or does not contain international code.";
        }

        this.username = username;
        this.password = password;
        this.cellphone = cellphone;

        return "Username successfully captured. Password successfully captured. Cell phone number successfully added.";
    }

    public boolean loginUser(String username, String password) {
        if (this.username == null) return false;
        if (this.password == null) return false;
        return this.username.equals(username) && this.password.equals(password);
    }

    public String getWelcomeMessage() {
        return "Welcome " + firstName + "," + lastName + " it is great to see you again.";
    }
}