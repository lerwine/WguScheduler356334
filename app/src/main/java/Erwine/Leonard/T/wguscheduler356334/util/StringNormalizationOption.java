package Erwine.Leonard.T.wguscheduler356334.util;

import static Erwine.Leonard.T.wguscheduler356334.util.StringHelper.NORMALIZE_FLAG_LEAVE_BLANK_LINES;
import static Erwine.Leonard.T.wguscheduler356334.util.StringHelper.NORMALIZE_FLAG_LEAVE_WHITESPACE;
import static Erwine.Leonard.T.wguscheduler356334.util.StringHelper.NORMALIZE_FLAG_NO_TRIM_END;
import static Erwine.Leonard.T.wguscheduler356334.util.StringHelper.NORMALIZE_FLAG_NO_TRIM_START;
import static Erwine.Leonard.T.wguscheduler356334.util.StringHelper.NORMALIZE_FLAG_PASS_NULL_VALUE;
import static Erwine.Leonard.T.wguscheduler356334.util.StringHelper.NORMALIZE_FLAG_SINGLE_LINE;

/**
 * Specifies options for {@link StringHelper#normalizeString(String, StringNormalizationOption...)} and {@link StringHelper#getNormalizer(StringNormalizationOption...)}
 * that deviate from the default behavior. The default behavior is as follows:
 * <ul>
 *     <li>{@code null} string values are converted to an empty string.</li>
 *     <li>Control characters, other than line separators, are normalized as space characters.</li>
 *     <li>Multiple contiguous whitespace characters are normalized as a single space character.</li>
 *     <li>All whitespace characters are normalized as a space character ({@code \u0020 }).</li>
 *     <li>Newline sequences are normalized as a single Line Feed character ({@code \u000A })</li>
 *     <li>Whitespace characters at the beginning and end of each line are removed.</li>
 *     <li>Blank lines are removed.</li>
 * </ul>
 */
public enum StringNormalizationOption {
    /**
     * Do not replace null with empty string - return null values as-is.
     */
    PASS_NULL_VALUE(NORMALIZE_FLAG_PASS_NULL_VALUE),
    /**
     * Do not remove whitespace from beginning or end of line. Equivalent to <code>{@link #NO_TRIM_START} | {@link #NO_TRIM_END}</code>.
     * By using this option, any lines that contain only whitespace characters will not be considered empty, and will not be removed.
     */
    NO_TRIM(NORMALIZE_FLAG_NO_TRIM_START | NORMALIZE_FLAG_NO_TRIM_END),
    /**
     * Do not remove whitespace from the beginning of the line.
     */
    NO_TRIM_START(NORMALIZE_FLAG_NO_TRIM_START),
    /**
     * Do not remove whitespace from end of the line.
     */
    NO_TRIM_END(NORMALIZE_FLAG_NO_TRIM_END),
    /**
     * Join multiple lines into a single line. This option takes precedence over other options.
     * When not combined with {@link #LEAVE_WHITESPACE}, line separator sequences are replaced with space characters.
     * When combined with {@link #LEAVE_WHITESPACE}, the line separator sequence is replaced with the leading whitespace of the following line and any trailing whitespace of the preceding line is ignored.
     * If the following line does not start with any whitespace characters, then the newline sequence is replaced with the trailing whitespace of the preceding line.
     * If the preceding line does not end with any whitespace characters and the following line does not start with any, then the newline is replaced by a single space character.
     */
    SINGLE_LINE(NORMALIZE_FLAG_SINGLE_LINE),
    /**
     * Do not normalize whitespace characters.
     */
    LEAVE_WHITESPACE(NORMALIZE_FLAG_LEAVE_WHITESPACE),
    /**
     * Do not remove blank lines. This option has no effect when {@link #SINGLE_LINE} is used.
     */
    LEAVE_BLANK_LINES(NORMALIZE_FLAG_LEAVE_BLANK_LINES);

    private final int flag;

    StringNormalizationOption(int flag) {
        this.flag = flag;
    }

    /**
     * Converts {@code StringNormalizationOption} values to an integer value whose bits represent the combined options.
     *
     * @param options The {@code StringNormalizationOption} value to combine.
     * @return An integer value whose bits represent the combined options.
     */
    public static int toFlags(StringNormalizationOption... options) {
        if (null == options || options.length == 0) {
            return 0;
        }
        int result = 0;
        for (StringNormalizationOption o : options) {
            result = result | o.flag;
        }
        if (SINGLE_LINE.isFlaggedIn(result)) {
            // Strip off REMOVE_BLANK_LINES bit
            return result & 0x1f;
        }
        return result;
    }

    /**
     * Gets the bit-wise integer value for the current {@code StringNormalizationOption}.
     *
     * @return The bit-wise integer value for the current {@code StringNormalizationOption}.
     */
    public int getFlag() {
        return flag;
    }

    /**
     * Determines whether the current {@code StringNormalizationOption} is represented by the bits of the specified integer value.
     *
     * @param flagBits The bit-wise integer value.
     * @return {@code true} if the bit value(s) for the current {@code StringNormalizationOption} are set in the specified integer value; otherwise {@code false}.
     */
    public boolean isFlaggedIn(int flagBits) {
        return (flagBits & flag) == flag;
    }

}
