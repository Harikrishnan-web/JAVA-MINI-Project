import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class PasswordGenerator {
    private static final SecureRandom secureRandom = new SecureRandom();

    private static final String LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String DIGITS = "0123456789";
    // Common printable symbols. You can edit this if you want other symbols.
    private static final String SYMBOLS = "!@#$%^&*()-_=+[]{}|;:,.<>?/";

    public static String generatePassword(int length, boolean useLower, boolean useUpper,
                                          boolean useDigits, boolean useSymbols) {
        if (length <= 0) {
            throw new IllegalArgumentException("Password length must be > 0");
        }

        // Build the pool and keep category strings in a list for guaranteed inclusion
        StringBuilder allChars = new StringBuilder();
        List<String> requiredCategories = new ArrayList<>();

        if (useLower) {
            allChars.append(LOWER);
            requiredCategories.add(LOWER);
        }
        if (useUpper) {
            allChars.append(UPPER);
            requiredCategories.add(UPPER);
        }
        if (useDigits) {
            allChars.append(DIGITS);
            requiredCategories.add(DIGITS);
        }
        if (useSymbols) {
            allChars.append(SYMBOLS);
            requiredCategories.add(SYMBOLS);
        }

        if (allChars.length() == 0) {
            throw new IllegalArgumentException("At least one character category must be enabled.");
        }

        if (length < requiredCategories.size()) {
            // If user asks length smaller than number of required categories, we cannot guarantee inclusion
            throw new IllegalArgumentException(
                    "Length too short to include at least one character from each selected category. " +
                            "Required minimum length: " + requiredCategories.size()
            );
        }

        char[] password = new char[length];

        // First, place one guaranteed character from each selected category at random positions
        for (int i = 0; i < requiredCategories.size(); i++) {
            String category = requiredCategories.get(i);
            int pos;
            // find a free position
            do {
                pos = secureRandom.nextInt(length);
            } while (password[pos] != '\u0000'); // '\u0000' indicates empty slot
            password[pos] = category.charAt(secureRandom.nextInt(category.length()));
        }

        // Fill remaining positions with random characters from the combined pool
        for (int i = 0; i < length; i++) {
            if (password[i] == '\u0000') {
                password[i] = allChars.charAt(secureRandom.nextInt(allChars.length()));
            }
        }

        return new String(password);
    }

    private static boolean parseBooleanOrDefault(String s, boolean defaultVal) {
        if (s == null) return defaultVal;
        s = s.trim().toLowerCase();
        if (s.equals("true") || s.equals("t") || s.equals("yes") || s.equals("y") || s.equals("1")) return true;
        if (s.equals("false") || s.equals("f") || s.equals("no") || s.equals("n") || s.equals("0")) return false;
        return defaultVal;
    }

    public static void main(String[] args) {
        int length = 16;
        boolean useLower = true;
        boolean useUpper = true;
        boolean useDigits = true;
        boolean useSymbols = true;

        if (args.length >= 1) {
            try {
                length = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.err.println("Invalid length argument. Using default length 16.");
                length = 16;
            }
        }
        if (args.length >= 5) {
            // args: length lower upper digits symbols
            useLower = parseBooleanOrDefault(args[1], true);
            useUpper = parseBooleanOrDefault(args[2], true);
            useDigits = parseBooleanOrDefault(args[3], true);
            useSymbols = parseBooleanOrDefault(args[4], true);
            try {
                String pw = generatePassword(length, useLower, useUpper, useDigits, useSymbols);
                System.out.println(pw);
                return;
            } catch (IllegalArgumentException ex) {
                System.err.println("Error: " + ex.getMessage());
                System.exit(1);
            }
        }

        // Interactive mode
        Scanner sc = new Scanner(System.in);
        System.out.println("=== Password Generator ===");
        System.out.print("Password length (default 16): ");
        String line = sc.nextLine().trim();
        if (!line.isEmpty()) {
            try {
                length = Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.println("Invalid number, using default 16.");
                length = 16;
            }
        }

        System.out.print("Include lowercase letters? (Y/n, default Y): ");
        line = sc.nextLine().trim();
        useLower = !line.equalsIgnoreCase("n") && !line.equalsIgnoreCase("no");

        System.out.print("Include uppercase letters? (Y/n, default Y): ");
        line = sc.nextLine().trim();
        useUpper = !line.equalsIgnoreCase("n") && !line.equalsIgnoreCase("no");

        System.out.print("Include digits? (Y/n, default Y): ");
        line = sc.nextLine().trim();
        useDigits = !line.equalsIgnoreCase("n") && !line.equalsIgnoreCase("no");

        System.out.print("Include symbols? (Y/n, default Y): ");
        line = sc.nextLine().trim();
        useSymbols = !line.equalsIgnoreCase("n") && !line.equalsIgnoreCase("no");

        try {
            String password = generatePassword(length, useLower, useUpper, useDigits, useSymbols);
            System.out.println("\nGenerated password:");
            System.out.println(password);
        } catch (IllegalArgumentException ex) {
            System.err.println("Error: " + ex.getMessage());
            System.exit(1);
        }
    }
}
