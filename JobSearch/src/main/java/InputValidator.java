import java.text.ParseException;
import java.text.SimpleDateFormat;

public class InputValidator {
    public static boolean isValidDouble(String input) {
        try {
            Double.parseDouble(input);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isValidInteger(String input) {
        try {
            Integer.parseInt(input);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isValidDate(String input, String pattern) {
        try {
            new SimpleDateFormat(pattern).parse(input);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }
}
