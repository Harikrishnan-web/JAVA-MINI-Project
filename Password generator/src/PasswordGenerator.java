// PasswordGenerator.java
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

public class PasswordGenerator {
    private static final SecureRandom secureRandom = new SecureRandom();

    private static final String LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String DIGITS = "0123456789";
    private static final String SYMBOLS = "!@#$%^&*()-_=+[]{}|;:,.<>?/";

    /**
     * Generate a cryptographically strong password.
     *
     * @param length     Desired password length (> 0)
     * @param useLower   include lowercase letters
     * @param useUpper   include uppercase letters
     * @param useDigits  include digits
     * @param useSymbols include symbols
     * @return generated password
     */
    public static String generate(int length, boolean useLower, boolean useUpper,
                                  boolean useDigits, boolean useSymbols) {
        if (length <= 0) throw new IllegalArgumentException("Length must be > 0");

        StringBuilder pool = new StringBuilder();
        List<String> categories = new ArrayList<>();

        if (useLower) { pool.append(LOWER); categories.add(LOWER); }
        if (useUpper) { pool.append(UPPER); categories.add(UPPER); }
        if (useDigits) { pool.append(DIGITS); categories.add(DIGITS); }
        if (useSymbols) { pool.append(SYMBOLS); categories.add(SYMBOLS); }

        if (pool.length() == 0) {
            throw new IllegalArgumentException("At least one character category must be selected.");
        }

        if (length < categories.size()) {
            throw new IllegalArgumentException("Length too short to include all selected categories. Minimum: " + categories.size());
        }

        char[] pw = new char[length];

        // Ensure at least one char from each selected category placed at random positions
        for (String cat : categories) {
            int pos;
            do {
                pos = secureRandom.nextInt(length);
            } while (pw[pos] != '\u0000');
            pw[pos] = cat.charAt(secureRandom.nextInt(cat.length()));
        }

        // Fill remaining slots from pool
        for (int i = 0; i < length; i++) {
            if (pw[i] == '\u0000') {
                pw[i] = pool.charAt(secureRandom.nextInt(pool.length()));
            }
        }

        return new String(pw);
    }
}
