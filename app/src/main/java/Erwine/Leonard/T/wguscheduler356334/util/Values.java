package Erwine.Leonard.T.wguscheduler356334.util;

import android.text.Editable;
import android.text.TextWatcher;

import java.util.function.Consumer;

public class Values {


    public static TextWatcher textWatcherForTextChanged(final Consumer<String> onTextChanged) {
        return new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                onTextChanged.accept(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }

        };
    }

//    /**
//     * Ensures a {@link String} value is not null and does not contain extraneous white space characters.
//     *
//     * @param value The source {@link String} value.
//     * @return The {@code value} with extraneous white space characters removed if not null; otherwise, an empty {@link String}.
//     */
//    public static String asNonNullAndTrimmed(String value) {
//        return (null == value) ? "" : value.trim();
//    }

//    private static final Pattern PATTERN_MINUS_LEADING_WHITESPACE = Pattern.compile("^[\\s\\r\\n]+([^\\s\\r\\n].*)?$", Pattern.DOTALL);
//    private static final Pattern PATTERN_MINUS_TRAILING_WHITESPACE = Pattern.compile("^(([\\s\\r\\n]+[^\\s\\r\\n]+)+|[^\\s\\r\\n]+([\\s\\r\\n]+[^\\s\\r\\n]+)*)?[\\s\\r\\n]+$");
//
//    /**
//     * Group 1: Space (32) followed by one or more non-linebreak white space characters or 1 or more non-linebreak whitespace characters that doesn't start with a space (32);
//     * Non-capturing match: Newline sequence other than line feed (10).
//     */
//    private static final Pattern PATTERN_NBSPACES_OR_ALTNEWLINE = Pattern.compile("( [^\\S\\r\\n]+|[^ \\S\\r\\n][^\\S\\r\\n]*)|\\r\\n?");
//    private static final Pattern PATTERN_NBSPACES_OR_ALTNEWLINET = Pattern.compile("(?![^\\S\\r\\n]*[\r\n])[^\\S\\r\\n]*(?:\r\n?|\n)[^\\S\\r\\n]*|( [^\\S\\r\\n]+|[^ \\S\\r\\n][^\\S\\r\\n]*)|\\r\\n?[^\\S\\r\\n]*|\n[^\\S\\r\\n]*+");
//    private static final Pattern PATTERN_NBSPACES_OR_ALTNEWLINES = Pattern.compile("( [^\\S\\r\\n]+|[^ \\S\\r\\n][^\\S\\r\\n]*)|\\r[\\r\\n]*|\\n[\\r\\n]+");
//    private static final Pattern PATTERN_ALTWHITESPACES = Pattern.compile(" [\\s\\r\\n]+|[^ \\S][\\s\\r\\n]*");
//    private static final Pattern PATTERN_ALTSPACES = Pattern.compile(" \\s+|[^ \\S]\\s*");
//    private static final Pattern PATTERN_WHITESPACES_NEWANDBLANKLINES_WHITESPACES = Pattern.compile("([^\\S\\r\\n]+)?(?:[\\r\\n]+(?:[^\\S\\r\\n]+[\\r\\n]+)*)([^\\S\\r\\n]+)?");
//    private static final Pattern PATTERN_NEW_AND_BLANK_LINES = Pattern.compile("\\r[\\r\\n]*|\\n[\\r\\n]+");
//    private static final Pattern PATTERN_NEW_AND_WHITESPACE_LINES = Pattern.compile("\\r[\\r\\n]*([^\\S\\r\\n]+[\\r\\n]+)*|\\n([\\r\\n]+([^\\S\\r\\n]+[\\r\\n]+)*|([^\\S\\r\\n]+[\\r\\n]+)+)");
//    public static final Pattern REGEX_LINEBREAK = Pattern.compile("\\r\\n?|\\n");
//    public static final Pattern REGEX_LINEBREAK_AND_SURROUNDING_WS = Pattern.compile("[^\\S\\r\\n]*(\\r\\n?|\\n)[^\\S\\r\\n]*");
//    public static final Pattern REGEX_LINEBREAK_AND_FOLLOWING_WS = Pattern.compile("(\\r\\n?|\\n)[^\\S\\r\\n]*");
//    public static final Pattern REGEX_LINEBREAK_AND_PRECEDING_WS = Pattern.compile("[^\\S\\r\\n]*(\\r\\n?|\\n)");
//
//    public static final Pattern AFTER_LEADING_WHITESPACES = Pattern.compile("^\\s+(\\S.*)?$");
//    public static final Pattern BEFORE_TRAILING_WHITESPACES = Pattern.compile("^((\\s+\\S+)+|\\S+(\\s+\\S+)*)?\\s+$");
//    public static final Pattern REGEX_NON_NORMAL_WHITESPACES = Pattern.compile(" \\s+|(?! )\\s+");
//    public static final Pattern REGEX_LINEBREAKN = Pattern.compile("[\\r\\n]+");
//    public static final Pattern REGEX_LINEBREAKW = Pattern.compile("[\\r\\n]+([^\\S\\r\\n]+[\\r\\n]+)*");
//    public static final Pattern REGEX_LINEBREAKT = Pattern.compile("[^\\S\\r\\n]*(\\r\\n?|\\r)[^\\S\\r\\n]*");
//    public static final Pattern REGEX_LINEJOIN = Pattern.compile("[^\\S\\r\\n]*[\\r\\n]+([^\\S\\r\\n]+)|([^\\S\\r\\n]+)?[\\r\\n]+[^\\S\\r\\n]*");

//    /**
//     * Ensures a {@link String} value is not null and that all white space is normalized. Leading and trailing whitespace will be removed. Consecutive whitespace characters will be
//     * replaced with a single space characters. Other whitespace characters will be replaced by a normal space character.
//     *
//     * @param value The source {@link String} value.
//     * @return The {@code value} with white space normalized if not null; otherwise, an empty {@link String}.
//     */
//    public static String asNonNullAndWsNormalized(String value) {
//        return StringNormalizer.normalizeString(value, StringNormalizationOption.TRIM, StringNormalizationOption.SINGLE_LINE);
//    }

    /**
     * Ensures a {@link String} value is not null and that all white space is normalized. Leading and trailing whitespace will be removed. Consecutive whitespace characters will be
     * replaced with a single space characters. Other whitespace characters will be replaced by a normal space character.
     *
     * @param value The source {@link String} value.
     * @return The {@code value} with white space normalized if not null; otherwise, an empty {@link String}.
     */
    public static String asNonNullAndWsNormalizedMultiLine(String value) {
        return StringHelper.normalizeString(value);
    }

//    public static String asWsNormalizedStringLines(String value) {
//        if (null == value || (value = value.trim()).isEmpty()) {
//            return "";
//        }
//
//        String[] lines = REGEX_LINEBREAKN.split(value);
//        if (lines.length < 2) {
//            return asNonNullAndWsNormalized(value);
//        }
//        Iterator<String> iterator = Arrays.stream(lines).map(Values::asNonNullAndWsNormalized).filter((t) -> !t.isEmpty()).iterator();
//        if (iterator.hasNext()) {
//            StringBuilder sb = new StringBuilder(asNonNullAndWsNormalized(iterator.next()));
//            while (iterator.hasNext()) {
//                sb.append("\n");
//                sb.append(asNonNullAndWsNormalized(iterator.next()));
//            }
//            return sb.toString();
//        }
//        return "";
//    }

}
