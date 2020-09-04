package Erwine.Leonard.T.wguscheduler356334;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class TestHelper {
    private TestHelper() {
    }


    public static <T> Supplier<T> createReIterator(Collection<T> source) {
        return new Supplier<T>() {
            private Iterator<T> iterator = source.iterator();

            @Override
            public T get() {
                T result = iterator.next();
                if (!iterator.hasNext())
                    iterator = source.iterator();
                return result;
            }
        };
    }

    public static <T> ArrayList<T> combineCollections(Collection<T> a, Collection<T> b) {
        ArrayList<T> result = new ArrayList<>();
        result.addAll(a);
        result.addAll(b);
        return result;
    }

    public static <T> ArrayList<T> combineCollections(Collection<T> a, T[] b) {
        ArrayList<T> result = new ArrayList<>(a);
        Collections.addAll(result, b);
        return result;
    }

    public static <T> ArrayList<T> combineCollections(T[] a, Collection<T> b) {
        return combineCollections(Arrays.asList(a), b);
    }

    public static <T> ArrayList<T> combineCollections(T[] a, T[] b) {
        return combineCollections(Arrays.asList(a), b);
    }

    public static <T> StringBuilder appendIterableDescription(StringBuilder target, Iterable<T> source, BiConsumer<StringBuilder, T> appendDescription) {
        if (null == source)
            return target.append("null");
        return appendIterableDescriptionImpl(target, source, appendDescription);
    }

    public static <T> StringBuilder appendIterableDescription(StringBuilder target, Iterable<T> source, Function<T, String> toDescription) {
        if (null == source)
            return target.append("null");
        return appendIterableDescriptionImpl(target, source, toDescription);
    }

    public static <T> StringBuilder appendIterableDescriptionImpl(StringBuilder target, Iterable<T> source, BiConsumer<StringBuilder, T> appendDescription) {
        target.append("{");
        Iterator<T> iterator = source.iterator();
        if (iterator.hasNext()) {
            T value = iterator.next();
            if (null == value)
                target.append("null");
            else
                appendDescription.accept(target, value);
            while (iterator.hasNext()) {
                value = iterator.next();
                if (null == value)
                    target.append(", null");
                else
                    appendDescription.accept(target.append(", "), value);
            }
        }
        return target.append("}");
    }

    public static <T> StringBuilder appendIterableDescriptionImpl(StringBuilder target, Iterable<T> source, Function<T, String> toDescription) {
        target.append("{");
        Iterator<T> iterator = source.iterator();
        if (iterator.hasNext()) {
            T value = iterator.next();
            if (null == value)
                target.append("null");
            else
                target.append(toDescription.apply(value));
            while (iterator.hasNext()) {
                value = iterator.next();
                if (null == value)
                    target.append(", null");
                else
                    target.append(", ").append(toDescription.apply(value));
            }
        }
        return target.append("}");
    }

    public static String toBooleanDescription(@Nullable Boolean value) {
        return (null == value) ? "null" : ((value) ? "true" : "false");
    }

    public static String toIterableBooleanDescription(@Nullable Iterable<Boolean> value) {
        if (null == value)
            return "null";
        return appendIterableDescriptionImpl(new StringBuilder(), value, t -> (t) ? "true" : "false").toString();
    }

    public static StringBuilder appendIterableBooleanDescription(StringBuilder target, @Nullable Iterable<Boolean> source) {
        if (null == source)
            return target.append("null");
        return appendIterableDescriptionImpl(target, source, t -> (t) ? "true" : "false");
    }

    public static String toLocalDateDescription(@Nullable LocalDate value) {
        return (null == value) ? "null" : appendLocalDateDescriptionImpl(new StringBuilder(), value).toString();
    }

    public static String toIterableLocalDateDescription(@Nullable Iterable<LocalDate> value) {
        if (null == value)
            return "null";
        return appendIterableDescriptionImpl(new StringBuilder(), value, TestHelper::appendLocalDateDescriptionImpl).toString();
    }

    public static StringBuilder appendIterableLocalDateDescription(StringBuilder target, @Nullable Iterable<LocalDate> source) {
        if (null == source)
            return target.append("null");
        return appendIterableDescriptionImpl(target, source, TestHelper::appendLocalDateDescriptionImpl);
    }

    public static String toLocalTimeDescription(@Nullable LocalTime value) {
        return (null == value) ? "null" : appendLocalTimeDescriptionImpl(new StringBuilder(), value).toString();
    }

    public static String toIterableLocalTimeDescription(@Nullable Iterable<LocalTime> value) {
        if (null == value)
            return "null";
        return appendIterableDescriptionImpl(new StringBuilder(), value, TestHelper::appendLocalTimeDescriptionImpl).toString();
    }

    public static StringBuilder appendIterableLocalTimeDescription(StringBuilder target, @Nullable Iterable<LocalTime> source) {
        if (null == source)
            return target.append("null");
        return appendIterableDescriptionImpl(target, source, TestHelper::appendLocalTimeDescriptionImpl);
    }

    public static String toLocalDateTimeDescription(@Nullable LocalDateTime value) {
        return (null == value) ? "null" : appendLocalDateTimeDescriptionImpl(new StringBuilder(), value).toString();
    }

    public static String toIterableLocalDateTimeDescription(@Nullable Iterable<LocalDateTime> value) {
        if (null == value)
            return "null";
        return appendIterableDescriptionImpl(new StringBuilder(), value, TestHelper::appendLocalDateTimeDescriptionImpl).toString();
    }

    public static StringBuilder appendIterableLocalDateTimeDescription(StringBuilder target, @Nullable Iterable<LocalDateTime> source) {
        if (null == source)
            return target.append("null");
        return appendIterableDescriptionImpl(target, source, TestHelper::appendLocalDateTimeDescriptionImpl);
    }

    public static String toNumberDescription(@Nullable Number value) {
        return (null == value) ? "null" : value.toString();
    }

    public static String toIterableNumberDescription(@Nullable Iterable<? extends Number> value) {
        if (null == value)
            return "null";
        return appendIterableDescriptionImpl(new StringBuilder(), value, TestHelper::toNumberDescription).toString();
    }

    public static StringBuilder appendIterableNumberDescription(StringBuilder target, @Nullable Iterable<? extends Number> source) {
        if (null == source)
            return target.append("null");
        return appendIterableDescriptionImpl(target, source, TestHelper::toNumberDescription);
    }

    public static String toStringDescription(String value) {
        if (null == value) {
            return "null";
        }
        if (value.isEmpty()) {
            return "\"\"";
        }
        StringBuilder sb = new StringBuilder("\"");
        appendStringDescriptionImpl(sb, value);
        return sb.append("\"").toString();
    }

    public static String toIterableStringDescription(@Nullable Iterable<String> value) {
        if (null == value)
            return "null";
        return appendIterableDescriptionImpl(new StringBuilder(), value, TestHelper::appendStringDescriptionImpl).toString();
    }

    public static StringBuilder appendIterableStringDescription(StringBuilder target, @Nullable Iterable<String> source) {
        if (null == source)
            return target.append("null");
        return appendIterableDescriptionImpl(target, source, TestHelper::appendStringDescriptionImpl);
    }

    public static String toStringDescription(StringBuilder value) {
        if (null == value) {
            return "null";
        }
        if (value.length() == 0) {
            return "\"\"";
        }
        StringBuilder sb = new StringBuilder("\"");
        appendStringDescriptionImpl(sb, value);
        return sb.append("\"").toString();
    }

    public static StringBuilder appendBooleanDescription(@NonNull StringBuilder target, @Nullable Boolean value) {
        if (null == value)
            return target.append("null");
        return appendBooleanDescriptionImpl(target, value);
    }

    private static StringBuilder appendBooleanDescriptionImpl(@NonNull StringBuilder target, boolean value) {
        return target.append((value) ? "true" : "false");
    }

    public static StringBuilder appendLocalDateDescription(@NonNull StringBuilder target, @Nullable LocalDate value) {
        if (null == value)
            return target.append("null");
        return appendLocalDateDescriptionImpl(target, value);
    }

    private static StringBuilder appendLocalDateDescriptionImpl(@NonNull StringBuilder target, @NonNull LocalDate value) {
        return target.append("LocalDate.of(").append(value.getYear()).append(", ").append(value.getMonthValue()).append(", ").append(value.getDayOfMonth()).append(")");
    }

    public static StringBuilder appendLocalTimeDescription(@NonNull StringBuilder target, @Nullable LocalTime value) {
        if (null == value)
            return target.append("null");
        return appendLocalTimeDescriptionImpl(target, value);
    }

    private static StringBuilder appendLocalTimeDescriptionImpl(@NonNull StringBuilder target, @NonNull LocalTime value) {
        int n = value.getNano();
        int s = value.getSecond();
        if (n == 0) {
            if (s == 0)
                return target.append("LocalTime.of(").append(value.getHour()).append(", ").append(value.getMinute()).append(")");
            return target.append("LocalTime.of(").append(value.getHour()).append(", ").append(value.getMinute()).append(", ").append(s).append(")");
        }
        return target.append("LocalTime.of(").append(value.getHour()).append(", ").append(value.getMinute()).append(", ").append(s).append(", ").append(n).append(")");
    }

    public static StringBuilder appendLocalDateTimeDescription(@NonNull StringBuilder target, @Nullable LocalDateTime value) {
        if (null == value)
            return target.append("null");
        return appendLocalDateTimeDescriptionImpl(target, value);
    }

    private static StringBuilder appendLocalDateTimeDescriptionImpl(@NonNull StringBuilder target, @NonNull LocalDateTime value) {
        return appendLocalTimeDescriptionImpl(appendLocalDateDescriptionImpl(target.append("LocalDateTime.of("), value.toLocalDate()), value.toLocalTime()).append(")");
    }

    public static StringBuilder appendStringDescription(@NonNull StringBuilder target, @Nullable CharSequence value) {
        if (null == value)
            return target.append("null");
        return appendStringDescriptionImpl(target, value);
    }

    private static StringBuilder appendStringDescriptionImpl(@NonNull StringBuilder target, @NonNull CharSequence value) {
        target.append("\"");
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            switch (c) {
                case '\b':
                    target.append("\\b");
                    break;
                case '\t':
                    target.append("\\t");
                    break;
                case '\r':
                    target.append("\\r");
                    break;
                case '\n':
                    target.append("\\n");
                    break;
                case '\f':
                    target.append("\\f");
                    break;
                case '"':
                    target.append("\\\"");
                    break;
                case '\\':
                    target.append("\\\\");
                    break;
                case '\u000B':
                    target.append("\\{VT}");
                    break;
                case '\u00A0':
                    target.append("\\{NBSP}");
                    break;
                case '\u0085':
                    target.append("\\{NEL}");
                    break;
                case '\u1680':
                    target.append("\\{OGHAM}");
                    break;
                case '\u2028':
                    target.append("\\{LS}");
                    break;
                case '\u2029':
                    target.append("\\{PS}");
                    break;
                default:
                    if (Character.isISOControl(c) || Character.isWhitespace(c))
                        target.append(String.format("\\u%04x", (int) c));
                    else
                        target.append(c);
                    break;
            }
        }
        return target.append("\"");
    }
}
