import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * InputValidator is a utility class that provides methods to validate user input.
 * It supports validation for numeric types (double and integer) and date formats.
 * Useful for ensuring correct data entry in forms or user interfaces.
 */
public class InputValidator {

    // Checks if the input string can be parsed as a double.
    public static boolean isValidDouble(String input) {
        try {
            Double.parseDouble(input);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // Checks if the input string can be parsed as an integer.
    public static boolean isValidInteger(String input) {
        try {
            Integer.parseInt(input);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // Checks if the input string is a valid date according to the provided pattern.
    public static boolean isValidDate(String input, String pattern) {
        try {
            new SimpleDateFormat(pattern).parse(input);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }
}
