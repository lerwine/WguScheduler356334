package Erwine.Leonard.T.wguscheduler356334.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Values {

    public static final Pattern REGEX_NON_NORMAL_WHITESPACES = Pattern.compile(" \\s+|(?! )\\s+");

    /**
     * Ensures a {@link String} value is not null and does not contain extraneous white space characters.
     *
     * @param value The source {@link String} value.
     * @return The {@code value} with extraneous white space characters removed if not null; otherwise, an empty {@link String}.
     */
    public static String asNonNullAndTrimmed(String value) {
        return (null == value) ? "" : value.trim();
    }

    /**
     * Ensures a {@link String} value is not null and that all white space is normalized. Leading and trailing whitespace will be removed. Consecutive whitespace characters will be
     * replaced with a single space characters. Other whitespace characters will be replaced by a normal space character.
     *
     * @param value The source {@link String} value.
     * @return The {@code value} with white space normalized if not null; otherwise, an empty {@link String}.
     */
    public static String asNonNullAndWsNormalized(String value) {
        if (null == value || (value = value.trim()).isEmpty()) {
            return "";
        }

        StringBuffer sb = new StringBuffer();
        Matcher matcher = REGEX_NON_NORMAL_WHITESPACES.matcher(value);
        if (matcher.find()) {
            do {
                matcher.appendReplacement(sb, Matcher.quoteReplacement(" "));
            } while (matcher.find());
            matcher.appendTail(sb);
            return sb.toString();
        }
        return value;
    }

}
