package Erwine.Leonard.T.wguscheduler356334.util;

import java.util.HashMap;
import java.util.Objects;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class StringHelper {
    /**
     * No special options.
     */
    public static final int NORMALIZE_FLAG_DEFAULT = 0x00;
    /**
     * Do not replace null with empty string.
     */
    public static final int NORMALIZE_FLAG_PASS_NULL_VALUE = 0x01;
    /**
     * Do not remove whitespace at beginning of line.
     */
    public static final int NORMALIZE_FLAG_NO_TRIM_START = 0x02;
    /**
     * Do not remove whitespace at end of line.
     */
    public static final int NORMALIZE_FLAG_NO_TRIM_END = 0x04;
    /**
     * Replace line breaks with a space. This option takes precedence over other options.
     */
    public static final int NORMALIZE_FLAG_SINGLE_LINE = 0x08;
    /**
     * Do not collapse multiple whitespace characters and other non-space whitespace characters into a single space character.
     */
    public static final int NORMALIZE_FLAG_LEAVE_WHITESPACE = 0x10;
    /**
     * Do not remove blank lines. Ignored when {@link #NORMALIZE_FLAG_SINGLE_LINE} is present.
     */
    public static final int NORMALIZE_FLAG_LEAVE_BLANK_LINES = 0x20;

    public static final Pattern PATTERN_LINEBREAK = Pattern.compile("\\r\\n?|[\\n\\f\\u0085\\u2028\\u2029]");

    public static final Pattern PATTERN_LINEBREAK_MULTIPLE = Pattern.compile("[\\r\\n\\f\\u0085\\u2028\\u2029]+([^\\S\\r\\n\\f\\u0085\\u2028\\u2029]+[\\r\\n\\f\\u0085\\u2028\\u2029]+)*");

    public static final Pattern NON_NORMAL_WHITESPACE = Pattern.compile(" [\\s\\u1680\\u0085\\u2028\\u2029]+|([\\u1680\\u0085\\u2028\\u2029]|[^ \\S])[\\s\\u1680\\u0085\\u2028\\u2029]*");

    public static final Pattern NEWLINE_SURROUNDING_WHITESPACE = Pattern.compile("((?:\\u1680+|[^\\S\\r\\n\\f\\u0085\\u2028\\u2029]+)+)?([\\r\\n\\f\\u0085\\u2028\\u2029]+((?:\\u1680+|[^\\S\\r\\n\\f\\u0085\\u2028\\u2029]+)+[\\r\\n\\f\\u0085\\u2028\\u2029]+)*)((?:\\u1680+|[^\\S\\r\\n\\f\\u0085\\u2028\\u2029]+)+)?");

    private static final HashMap<Integer, Function<String, String>> NORMALIZER_MAP = new HashMap<>();

    private static final HashMap<Integer, Function<String, String>> STRING_NORMALIZER_MAP = new HashMap<>();

    public static boolean isLineSeparator(char c) {
        switch (c) {
            case '\r':
            case '\n':
            case '\f':
            case '\u0085':
                return true;
            default:
                int type = Character.getType(c);
                return type == Character.LINE_SEPARATOR || type == Character.PARAGRAPH_SEPARATOR;
        }
    }

    public static boolean isWhiteSpaceOrLineSeparator(char c) {
        switch (c) {
            case '\u0085':
                return true;
            default:
                return Character.isWhitespace(c);
        }
    }

    private StringHelper() {
    }

    public static String normalizeString(String value, StringNormalizationOption... options) {
        return getNormalizer(options).apply(value);
    }

    public static String trimStart(String value) {
        if (null == value || value.isEmpty()) {
            return value;
        }

        if (isWhiteSpaceOrLineSeparator(value.charAt(0))) {
            for (int i = 1; i < value.length(); i++) {
                if (!isWhiteSpaceOrLineSeparator(value.charAt(i))) {
                    return value.substring(i);
                }
            }
            return "";
        }
        return value;
    }

    public static String trimEnd(String value) {
        if (null == value || value.isEmpty()) {
            return value;
        }
        int i = value.length() - 1;

        if (isWhiteSpaceOrLineSeparator(value.charAt(i))) {
            while (--i > -1) {
                if (!isWhiteSpaceOrLineSeparator(value.charAt(i))) {
                    return value.substring(0, i + 1);
                }
            }
            return "";
        }
        return value;
    }

    public static Function<String, String> getNormalizer(StringNormalizationOption... options) {
        int flags = StringNormalizationOption.toFlags(options);
        synchronized (NORMALIZER_MAP) {
            if (NORMALIZER_MAP.containsKey(flags)) {
                return NORMALIZER_MAP.get(flags);
            }

            Function<String, String> normalizer;
            boolean nullToEmpty = (flags & NORMALIZE_FLAG_PASS_NULL_VALUE) == 0;
            if ((flags & NORMALIZE_FLAG_SINGLE_LINE) != 0) {
                if ((flags & NORMALIZE_FLAG_NO_TRIM_START) == 0) {
                    if ((flags & NORMALIZE_FLAG_NO_TRIM_END) == 0) {
                        if ((flags & NORMALIZE_FLAG_LEAVE_WHITESPACE) != 0) {
                            // FIXME: String::trim does not work for \u0080
                            normalizer = matcherReplacer(NEWLINE_SURROUNDING_WHITESPACE, String::trim, matcher -> {
                                String s = matcher.group(2);
                                if (null != s || null != (s = matcher.group(1))) {
                                    return s;
                                }
                                return " ";
                            }, nullToEmpty);
                        } else {
                            // FIXME: String::trim does not work for \u0080
                            normalizer = staticReplacer(NON_NORMAL_WHITESPACE, String::trim, " ", nullToEmpty);
                        }
                    } else if ((flags & NORMALIZE_FLAG_LEAVE_WHITESPACE) != 0) {
                        normalizer = matcherReplacer(NEWLINE_SURROUNDING_WHITESPACE, StringHelper::trimStart, matcher -> {
                            String s = matcher.group(2);
                            if (null != s || null != (s = matcher.group(1))) {
                                return s;
                            }
                            return " ";
                        }, nullToEmpty);
                    } else {
                        normalizer = staticReplacer(NON_NORMAL_WHITESPACE, StringHelper::trimStart, " ", nullToEmpty);
                    }
                } else if ((flags & NORMALIZE_FLAG_NO_TRIM_END) == 0) {
                    if ((flags & NORMALIZE_FLAG_LEAVE_WHITESPACE) != 0) {
                        normalizer = matcherReplacer(NEWLINE_SURROUNDING_WHITESPACE, StringHelper::trimEnd, matcher -> {
                            String s = matcher.group(2);
                            if (null != s || null != (s = matcher.group(1))) {
                                return s;
                            }
                            return " ";
                        }, nullToEmpty);
                    } else {
                        normalizer = staticReplacer(NON_NORMAL_WHITESPACE, StringHelper::trimEnd, " ", nullToEmpty);
                    }
                } else if ((flags & NORMALIZE_FLAG_LEAVE_WHITESPACE) != 0) {
                    normalizer = matcherReplacer(NEWLINE_SURROUNDING_WHITESPACE, matcher -> {
                        String s = matcher.group(2);
                        if (null != s || null != (s = matcher.group(1))) {
                            return s;
                        }
                        return " ";
                    }, nullToEmpty);
                } else {
                    normalizer = staticReplacer(NON_NORMAL_WHITESPACE, " ", nullToEmpty);
                }
            } else if ((flags & NORMALIZE_FLAG_LEAVE_WHITESPACE) != 0) {
                normalizer = lineByLineReplacer(nullToEmpty, (flags & NORMALIZE_FLAG_LEAVE_BLANK_LINES) == 0, (flags & NORMALIZE_FLAG_NO_TRIM_START) == 0,
                        (flags & NORMALIZE_FLAG_NO_TRIM_END) == 0);
            } else {
                normalizer = lineByLineReplacer(line -> NON_NORMAL_WHITESPACE.matcher(line).replaceAll(" "), nullToEmpty,
                        (flags & NORMALIZE_FLAG_LEAVE_BLANK_LINES) == 0, (flags & NORMALIZE_FLAG_NO_TRIM_START) == 0,
                        (flags & NORMALIZE_FLAG_NO_TRIM_END) == 0);
            }
            NORMALIZER_MAP.put(flags, normalizer);
            return normalizer;
        }
    }

    private static Function<String, String> applyWhenNotNullOrEmpty(final Function<String, String> whenNotNullOrEmpty, boolean nullToEmpty) {
        if (nullToEmpty) {
            return s -> {
                if (null == s || s.isEmpty()) {
                    return "";
                }
                return whenNotNullOrEmpty.apply(s);
            };
        }
        return s -> {
            if (null == s || s.isEmpty()) {
                return s;
            }
            return whenNotNullOrEmpty.apply(s);
        };
    }

    private static Function<String, String> staticReplacer(final Pattern pattern, final Function<String, String> beforeMatch, final String replacement, boolean nullToEmpty) {
        Objects.requireNonNull(pattern);
        Objects.requireNonNull(replacement);
        return applyWhenNotNullOrEmpty(s -> {
            String v = beforeMatch.apply(s);
            if (!v.isEmpty()) {
                Matcher matcher = pattern.matcher(v);
                if (matcher.find()) {
                    StringBuffer sb = new StringBuffer();
                    do {
                        matcher.appendReplacement(sb, replacement);
                    } while (matcher.find());
                    matcher.appendTail(sb);
                    return sb.toString();
                }
            }
            return v;
        }, nullToEmpty);
    }

    private static Function<String, String> staticReplacer(final Pattern pattern, final String replacement, boolean nullToEmpty) {
        Objects.requireNonNull(pattern);
        Objects.requireNonNull(replacement);
        return applyWhenNotNullOrEmpty(s -> {
            Matcher matcher = pattern.matcher(s);
            if (matcher.find()) {
                StringBuffer sb = new StringBuffer();
                do {
                    matcher.appendReplacement(sb, replacement);
                } while (matcher.find());
                matcher.appendTail(sb);
                return sb.toString();
            }
            return s;
        }, nullToEmpty);
    }

    private static Function<String, String> matcherReplacer(final Pattern pattern, final Function<String, String> beforeMatch, final Function<Matcher, String> getReplacement, boolean nullToEmpty) {
        Objects.requireNonNull(pattern);
        Objects.requireNonNull(getReplacement);
        return applyWhenNotNullOrEmpty(s -> {
            String v = beforeMatch.apply(s);
            if (!v.isEmpty()) {
                Matcher matcher = pattern.matcher(v);
                if (matcher.find()) {
                    StringBuffer sb = new StringBuffer();
                    do {
                        matcher.appendReplacement(sb, getReplacement.apply(matcher));
                    } while (matcher.find());
                    matcher.appendTail(sb);
                    return sb.toString();
                }
            }
            return v;
        }, nullToEmpty);
    }

    private static Function<String, String> matcherReplacer(final Pattern pattern, final Function<Matcher, String> getReplacement, boolean nullToEmpty) {
        Objects.requireNonNull(pattern);
        Objects.requireNonNull(getReplacement);
        return applyWhenNotNullOrEmpty(s -> {
            Matcher matcher = pattern.matcher(s);
            if (matcher.find()) {
                StringBuffer sb = new StringBuffer();
                do {
                    matcher.appendReplacement(sb, getReplacement.apply(matcher));
                } while (matcher.find());
                matcher.appendTail(sb);
                return sb.toString();
            }
            return s;
        }, nullToEmpty);
    }

    private static Function<String, String> lineByLineReplacer(final Function<String, String> normalizeLine, boolean nullToEmpty, boolean omitEmptyLines, boolean trimStart, boolean trimEnd) {
        Objects.requireNonNull(normalizeLine);
        if (omitEmptyLines) {
            return applyWhenNotNullOrEmpty(s -> {
                StringLineIterator iterator = StringLineIterator.create(s, trimStart, trimEnd);
                String v = normalizeLine.apply(iterator.next());
                while (v.isEmpty()) {
                    if (!iterator.hasNext()) {
                        return "";
                    }
                    v = normalizeLine.apply(iterator.next());
                }
                if (!iterator.hasNext()) {
                    return v;
                }
                StringBuilder sb = new StringBuilder(v);
                do {
                    v = normalizeLine.apply(iterator.next());
                    if (!v.isEmpty()) {
                        sb.append("\n").append(v);
                    }
                } while (iterator.hasNext());
                return sb.toString();
            }, nullToEmpty);
        }
        return applyWhenNotNullOrEmpty(s -> {
            StringLineIterator iterator = StringLineIterator.create(s, trimStart, trimEnd);
            String v = normalizeLine.apply(iterator.next());
            if (!iterator.hasNext()) {
                return v;
            }
            StringBuilder sb = new StringBuilder(v);
            do {
                sb.append("\n").append(normalizeLine.apply(iterator.next()));
            } while (iterator.hasNext());
            return sb.toString();
        }, nullToEmpty);
    }

    private static Function<String, String> lineByLineReplacer(boolean nullToEmpty, boolean omitEmptyLines, boolean trimStart, boolean trimEnd) {
        if (omitEmptyLines) {
            return applyWhenNotNullOrEmpty(s -> {
                StringLineIterator iterator = StringLineIterator.create(s, trimStart, trimEnd);
                String v = iterator.next();
                while (v.isEmpty()) {
                    if (!iterator.hasNext()) {
                        return "";
                    }
                    v = iterator.next();
                }
                if (!iterator.hasNext()) {
                    return v;
                }
                StringBuilder sb = new StringBuilder(v);
                do {
                    v = iterator.next();
                    if (!v.isEmpty()) {
                        sb.append("\n").append(v);
                    }
                } while (iterator.hasNext());
                return sb.toString();
            }, nullToEmpty);
        }
        return applyWhenNotNullOrEmpty(s -> {
            StringLineIterator iterator = StringLineIterator.create(s, trimStart, trimEnd);
            String v = iterator.next();
            if (!iterator.hasNext()) {
                return v;
            }
            StringBuilder sb = new StringBuilder(v);
            do {
                sb.append("\n").append(iterator.next());
            } while (iterator.hasNext());
            return sb.toString();
        }, nullToEmpty);
    }
}
