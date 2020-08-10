package Erwine.Leonard.T.wguscheduler356334.util;

import android.text.Editable;
import android.text.TextWatcher;

import java.util.Arrays;
import java.util.Iterator;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Values {

    public static final Pattern REGEX_NON_NORMAL_WHITESPACES = Pattern.compile(" \\s+|(?! )\\s+");
    public static final Pattern REGEX_LINEBREAKN = Pattern.compile("[\\r\\n]+");

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

    /**
     * Ensures a {@link String} value is not null and that all white space is normalized. Leading and trailing whitespace will be removed. Consecutive whitespace characters will be
     * replaced with a single space characters. Other whitespace characters will be replaced by a normal space character.
     *
     * @param value The source {@link String} value.
     * @return The {@code value} with white space normalized if not null; otherwise, an empty {@link String}.
     */
    public static String asNonNullAndWsNormalizedMultiLine(String value) {
        if (null == value || (value = value.trim()).isEmpty()) {
            return "";
        }

        String[] lines = REGEX_LINEBREAKN.split(value);
        if (lines.length < 2) {
            return asNonNullAndWsNormalized(value);
        }
        StringBuilder sb = new StringBuilder(asNonNullAndWsNormalized(lines[0]));
        for (int i = 1; i < lines.length; i++) {
            sb.append("\n");
            sb.append(asNonNullAndWsNormalized(lines[i]));
        }
        return sb.toString();
    }

    public static String asWsNormalizedStringLines(String value) {
        if (null == value || (value = value.trim()).isEmpty()) {
            return "";
        }

        String[] lines = REGEX_LINEBREAKN.split(value);
        if (lines.length < 2) {
            return asNonNullAndWsNormalized(value);
        }
        Iterator<String> iterator = Arrays.stream(lines).map(Values::asNonNullAndWsNormalized).filter((t) -> !t.isEmpty()).iterator();
        if (iterator.hasNext()) {
            StringBuilder sb = new StringBuilder(asNonNullAndWsNormalized(iterator.next()));
            while (iterator.hasNext()) {
                sb.append("\n");
                sb.append(asNonNullAndWsNormalized(iterator.next()));
            }
            return sb.toString();
        }
        return "";
    }

}
