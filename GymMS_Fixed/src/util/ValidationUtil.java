package util;

public class ValidationUtil {
    public static boolean isEmpty(String v)         { return v == null || v.trim().isEmpty(); }
    public static boolean isValidEmail(String e)    { return !isEmpty(e) && e.matches("^[\\w.+\\-]+@[\\w\\-]+\\.[a-zA-Z]{2,}$"); }
    public static boolean isValidPhone(String p)    { return !isEmpty(p) && p.trim().matches("^[0-9]{10}$"); }
    public static boolean isValidPassword(String p) { return !isEmpty(p) && p.length() >= 4; }
    public static boolean isValidAge(String a) {
        try { int v = Integer.parseInt(a.trim()); return v >= 5 && v <= 120; }
        catch (NumberFormatException e) { return false; }
    }
    public static boolean isPositiveInteger(String v) {
        try { return Integer.parseInt(v.trim()) > 0; }
        catch (NumberFormatException e) { return false; }
    }
    public static boolean isPositiveNumber(String v) {
        try { return Double.parseDouble(v.trim()) > 0; }
        catch (NumberFormatException e) { return false; }
    }
}
