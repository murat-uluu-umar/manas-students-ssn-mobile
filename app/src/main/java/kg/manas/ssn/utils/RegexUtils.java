package kg.manas.ssn.utils;

import java.util.regex.Pattern;

public class RegexUtils {

    public static final String VALID_STUDENT_NUMBER = "(^[0-9]{4}\\.[0-9]{5}$)";

    public static boolean isValid(String regex, String value) {
        Pattern pattern = Pattern.compile(regex);
        return pattern.matcher(value).find();
    }

    public static boolean isValidStudentNumber(String studentNumber) {
        return isValid(VALID_STUDENT_NUMBER, studentNumber);
    }
}
