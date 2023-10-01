package util;

public class Name {
    private static int beginNumberSuffix(String s) {
        if (s.isEmpty())
            return 0;
        int k = s.length() - 1;
        while (k >= 0 && Character.isDigit(s.charAt(k)))
            k--;
        return k + 1;
    }

    public static boolean hasNameSuffixNumber(String s) {
        return beginNumberSuffix(s) < s.length();
    }

    public static int suffixNumber(String s) {
        int n = beginNumberSuffix(s);
        assert (n <= s.length());
        if (n == s.length())
            return -1;
        String numberStr = s.substring(n);
        return Integer.parseInt(numberStr);
    }

    public static String nameWithoutNumber(String s) {
        int n = beginNumberSuffix(s);
        assert (n <= s.length());
        return s.substring(0, n);
    }
}
