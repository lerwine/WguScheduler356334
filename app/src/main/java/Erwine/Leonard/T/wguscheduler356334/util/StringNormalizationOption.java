package Erwine.Leonard.T.wguscheduler356334.util;

import static Erwine.Leonard.T.wguscheduler356334.util.StringNormalizer.NORMALIZE_FLAG_LEAVE_WHITESPACE;
import static Erwine.Leonard.T.wguscheduler356334.util.StringNormalizer.NORMALIZE_FLAG_PASS_NULL_VALUE;
import static Erwine.Leonard.T.wguscheduler356334.util.StringNormalizer.NORMALIZE_FLAG_REMOVE_BLANK_LINES;
import static Erwine.Leonard.T.wguscheduler356334.util.StringNormalizer.NORMALIZE_FLAG_SINGLE_LINE;
import static Erwine.Leonard.T.wguscheduler356334.util.StringNormalizer.NORMALIZE_FLAG_TRIM_END;
import static Erwine.Leonard.T.wguscheduler356334.util.StringNormalizer.NORMALIZE_FLAG_TRIM_START;

public enum StringNormalizationOption {
    /**
     * Do not replace null with empty string.
     */
    PASS_NULL_VALUE(NORMALIZE_FLAG_PASS_NULL_VALUE),
    /**
     * Remove whitespace from beginning and end of line. Equivalent to {@link #TRIM_START} and {@link #TRIM_END}.
     */
    TRIM(NORMALIZE_FLAG_TRIM_START | NORMALIZE_FLAG_TRIM_END),
    /**
     * Remove whitespace at beginning of line.
     */
    TRIM_START(NORMALIZE_FLAG_TRIM_START),
    /**
     * Remove whitespace at end of line.
     */
    TRIM_END(NORMALIZE_FLAG_TRIM_END),
    /**
     * Replace line breaks with a space. This option takes precedence over other options.
     */
    SINGLE_LINE(NORMALIZE_FLAG_SINGLE_LINE),
    /**
     * Do not collapse multiple whitespace characters and other non-space whitespace characters into a single space character.
     */
    LEAVE_WHITESPACE(NORMALIZE_FLAG_LEAVE_WHITESPACE),
    /**
     * Remove blank lines. Ignored when {@link #SINGLE_LINE} is present.
     */
    REMOVE_BLANK_LINES(NORMALIZE_FLAG_REMOVE_BLANK_LINES);

    private final int flag;

    StringNormalizationOption(int flag) {
        this.flag = flag;
    }

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

    public int getFlag() {
        return flag;
    }

    public boolean isFlaggedIn(int flagBits) {
        return (flagBits & flag) == flag;
    }

}
