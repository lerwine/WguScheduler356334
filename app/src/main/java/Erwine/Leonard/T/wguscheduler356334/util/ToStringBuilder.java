package Erwine.Leonard.T.wguscheduler356334.util;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Optional;
import java.util.stream.Stream;

public class ToStringBuilder implements Appendable, CharSequence {

    public static final String NULL_STRING = "null";

    @NonNull
    public static String toEscapedString(@Nullable ToStringBuildable obj) {
        return toEscapedString(obj, false);
    }

    @NonNull
    public static String toEscapedString(@Nullable ToStringBuildable obj, boolean omitTypeName) {
        if (null == obj) {
            return NULL_STRING;
        }
        return new ToStringBuilder().appendImpl(obj, omitTypeName).toString();
    }

    @NonNull
    public static String toEscapedString(@Nullable String str) {
        if (null == str) {
            return NULL_STRING;
        }
        if (str.isEmpty()) {
            return "\"\"";
        }
        ToStringBuilder builder = new ToStringBuilder();
        builder.backingBuilder.append("\"");
        for (char c : str.toCharArray()) {
            builder.appendUnquoted(c);
        }
        return builder.backingBuilder.append("\"").toString();
    }

    @NonNull
    public static String toEscapedString(@Nullable CharSequence seq) {
        if (null == seq) {
            return NULL_STRING;
        }
        if (seq.length() == 0) {
            return seq.getClass().getName() + "\"\"";
        }
        ToStringBuilder builder = new ToStringBuilder();
        builder.backingBuilder.append(seq.getClass().getName()).append("\"");
        for (int i = 0; i < seq.length(); i++) {
            builder.appendUnquoted(seq.charAt(i));
        }
        return builder.backingBuilder.append("\"").toString();
    }

    @NonNull
    public static String toEscapedString(@Nullable CharSequence seq, boolean omitTypeName) {
        if (null == seq) {
            return NULL_STRING;
        }
        if (seq.length() == 0) {
            return (omitTypeName) ? "\"\"" : seq.getClass().getName() + "\"\"";
        }
        ToStringBuilder builder = new ToStringBuilder();
        if (!omitTypeName) {
            builder.backingBuilder.append(seq.getClass().getName());
        }
        builder.backingBuilder.append("\"");
        for (int i = 0; i < seq.length(); i++) {
            builder.appendUnquoted(seq.charAt(i));
        }
        return builder.backingBuilder.append("\"").toString();
    }

    @NonNull
    public static String toEscapedString(boolean b) {
        return Boolean.toString(b);
    }

    @NonNull
    public static String toEscapedString(char c) {
        return Character.toString(c);
    }

    @NonNull
    public static String toEscapedString(int i) {
        return Integer.toString(i);
    }

    @NonNull
    public static String toEscapedString(long lng) {
        return Long.toString(lng);
    }

    @NonNull
    public static String toEscapedString(float f) {
        return Float.toString(f);
    }

    @NonNull
    public static String toEscapedString(double d) {
        return Double.toString(d);
    }

    @NonNull
    public static String toEscapedString(@Nullable Boolean b) {
        return (null == b) ? NULL_STRING : Boolean.toString(b);
    }

    @NonNull
    public static String toEscapedString(@Nullable Character c) {
        return (null == c) ? NULL_STRING : Character.toString(c);
    }

    @NonNull
    public static String toEscapedString(@Nullable Integer i) {
        return (null == i) ? NULL_STRING : Integer.toString(i);
    }

    @NonNull
    public static String toEscapedString(@Nullable Long lng) {
        return (null == lng) ? NULL_STRING : Long.toString(lng);
    }

    @NonNull
    public static String toEscapedString(@Nullable Float f) {
        return (null == f) ? NULL_STRING : Float.toString(f);
    }

    @NonNull
    public static String toEscapedString(@Nullable Double d) {
        return (null == d) ? NULL_STRING : Double.toString(d);
    }

    @SuppressWarnings({"OptionalUsedAsFieldOrParameterType", "OptionalAssignedToNull"})
    @NonNull
    public static String toEscapedString(@Nullable Optional<?> o, boolean omitTypeName, boolean omitElementType) {
        if (null == o) {
            return NULL_STRING;
        }
        return new ToStringBuilder().appendImpl(o, omitTypeName, omitElementType).toString();
    }

    @NonNull
    public static String toEscapedString(Enum<?> e, boolean omitTypeName) {
        if (null == e) {
            return NULL_STRING;
        }
        if (omitTypeName) {
            return e.name();
        }
        return e.getClass().getName() + '.' + e.name();
    }

    @NonNull
    public static String toEscapedString(@Nullable Iterable<?> iterable, boolean omitTypeName, boolean omitElementType) {
        if (null == iterable) {
            return NULL_STRING;
        }
        return new ToStringBuilder().appendImpl(iterable, omitTypeName, omitElementType).toString();
    }

    @NonNull
    public static String toEscapedString(@Nullable Stream<?> stream, boolean omitTypeName, boolean omitElementType) {
        if (null == stream) {
            return NULL_STRING;
        }
        return new ToStringBuilder().appendImpl(stream, omitTypeName, omitElementType).toString();
    }

    @NonNull
    public static String toEscapedString(@Nullable boolean[] array, boolean omitTypeName) {
        if (null == array) {
            return NULL_STRING;
        }
        return new ToStringBuilder().appendImpl(array, omitTypeName).toString();
    }

    @NonNull
    public static String toEscapedString(@Nullable byte[] array, boolean omitTypeName) {
        if (null == array) {
            return NULL_STRING;
        }
        return new ToStringBuilder().appendImpl(array, omitTypeName).toString();
    }

    @NonNull
    public static String toEscapedString(@Nullable short[] array, boolean omitTypeName) {
        if (null == array) {
            return NULL_STRING;
        }
        return new ToStringBuilder().appendImpl(array, omitTypeName).toString();
    }

    @NonNull
    public static String toEscapedString(@Nullable char[] array, boolean omitTypeName) {
        if (null == array) {
            return NULL_STRING;
        }
        return new ToStringBuilder().appendImpl(array, omitTypeName).toString();
    }

    @NonNull
    public static String toEscapedString(@Nullable int[] array, boolean omitTypeName) {
        if (null == array) {
            return NULL_STRING;
        }
        return new ToStringBuilder().appendImpl(array, omitTypeName).toString();
    }

    @NonNull
    public static String toEscapedString(@Nullable long[] array, boolean omitTypeName) {
        if (null == array) {
            return NULL_STRING;
        }
        return new ToStringBuilder().appendImpl(array, omitTypeName).toString();
    }

    @NonNull
    public static String toEscapedString(@Nullable float[] array, boolean omitTypeName) {
        if (null == array) {
            return NULL_STRING;
        }
        return new ToStringBuilder().appendImpl(array, omitTypeName).toString();
    }

    @NonNull
    public static String toEscapedString(@Nullable double[] array, boolean omitTypeName) {
        if (null == array) {
            return NULL_STRING;
        }
        return new ToStringBuilder().appendImpl(array, omitTypeName).toString();
    }

    @NonNull
    public static <T> String toEscapedString(@Nullable T[] array, boolean omitTypeName, boolean omitElementType) {
        if (null == array) {
            return NULL_STRING;
        }
        return new ToStringBuilder().appendImpl(array, omitTypeName, omitElementType).toString();
    }

    @NonNull
    public static String toEscapedString(@Nullable LocalDate obj, boolean omitTypeName) {
        if (null == obj) {
            return NULL_STRING;
        }
        return new ToStringBuilder().appendImpl(obj, omitTypeName).toString();
    }

    @NonNull
    public static String toEscapedString(@Nullable LocalTime obj, boolean omitTypeName) {
        if (null == obj) {
            return NULL_STRING;
        }
        return new ToStringBuilder().appendImpl(obj, omitTypeName).toString();
    }

    @NonNull
    public static String toEscapedString(@Nullable Object obj, boolean omitTypeName, boolean omitElementType) {
        if (null == obj) {
            return NULL_STRING;
        }
        return new ToStringBuilder().appendImpl(obj, omitTypeName, omitElementType).toString();
    }

    private final StringBuilder backingBuilder;

    public ToStringBuilder() {
        backingBuilder = new StringBuilder();
    }

    @NonNull
    public ToStringBuilder appendRaw(@NonNull String str) {
        backingBuilder.append(str);
        return this;
    }

    @NonNull
    public ToStringBuilder append(@Nullable String str) {
        if (null != str) {
            return appendImpl(str);
        }
        backingBuilder.append(NULL_STRING);
        return this;
    }

    @NonNull
    @Override
    public ToStringBuilder append(@Nullable CharSequence csq) {
        if (null != csq) {
            return appendImpl(csq, false);
        }
        backingBuilder.append(NULL_STRING);
        return this;
    }

    @NonNull
    public ToStringBuilder append(@Nullable CharSequence csq, boolean omitTypeName) {
        if (null != csq) {
            return appendImpl(csq, omitTypeName);
        }
        backingBuilder.append(NULL_STRING);
        return this;
    }

    @NonNull
    @Override
    public ToStringBuilder append(@Nullable CharSequence csq, int start, int end) {
        throw new UnsupportedOperationException();
    }

    @NonNull
    public ToStringBuilder append(boolean b) {
        backingBuilder.append(b);
        return this;
    }

    @NonNull
    @Override
    public ToStringBuilder append(char c) {
        switch (c) {
            case ' ':
                backingBuilder.append("' '");
                break;
            case '\b':
                backingBuilder.append("'\\b'");
                break;
            case '\t':
                backingBuilder.append("'\\t'");
                break;
            case '\r':
                backingBuilder.append("'\\r'");
                break;
            case '\n':
                backingBuilder.append("'\\n'");
                break;
            case '\f':
                backingBuilder.append("'\\f'");
                break;
            case '\'':
                backingBuilder.append("'\\''");
                break;
            case '\\':
                backingBuilder.append("'\\\\'");
                break;
            default:
                if (Character.isISOControl(c) || Character.isWhitespace(c) || (int) c > 126)
                    backingBuilder.append(String.format("'\\u%04x'", (int) c));
                else
                    backingBuilder.append('\'').append(c).append('\'');
                break;
        }
        return this;
    }

    @NonNull
    public ToStringBuilder append(int i) {
        backingBuilder.append(i);
        return this;
    }

    @NonNull
    public ToStringBuilder append(long lng) {
        backingBuilder.append(lng);
        return this;
    }

    @NonNull
    public ToStringBuilder append(float f) {
        backingBuilder.append(f);
        return this;
    }

    @NonNull
    public ToStringBuilder append(double d) {
        backingBuilder.append(d);
        return this;
    }

    @NonNull
    public ToStringBuilder append(@Nullable Boolean b) {
        if (null == b) {
            backingBuilder.append(NULL_STRING);
        } else {
            backingBuilder.append((boolean) b);
        }
        return this;
    }

    @NonNull
    public ToStringBuilder append(@Nullable Character c) {
        if (null != c) {
            return append((char) c);
        }
        backingBuilder.append(NULL_STRING);
        return this;
    }

    @NonNull
    public ToStringBuilder append(@Nullable Integer i) {
        if (null == i) {
            backingBuilder.append(NULL_STRING);
        } else {
            backingBuilder.append((int) i);
        }
        return this;
    }

    @NonNull
    public ToStringBuilder append(@Nullable Long lng) {
        if (null == lng) {
            backingBuilder.append(NULL_STRING);
        } else {
            backingBuilder.append((long) lng);
        }
        return this;
    }

    @NonNull
    public ToStringBuilder append(@Nullable Float f) {
        if (null == f) {
            backingBuilder.append(NULL_STRING);
        } else {
            backingBuilder.append((float) f);
        }
        return this;
    }

    @NonNull
    public ToStringBuilder append(@Nullable Double d) {
        if (null == d) {
            backingBuilder.append(NULL_STRING);
        } else {
            backingBuilder.append((double) d);
        }
        return this;
    }

    @SuppressWarnings({"OptionalUsedAsFieldOrParameterType", "OptionalAssignedToNull"})
    @NonNull
    public ToStringBuilder append(@Nullable Optional<?> o, boolean omitTypeName, boolean omitElementType) {
        if (null != o) {
            return appendImpl(o, omitTypeName, omitElementType);
        }
        backingBuilder.append(NULL_STRING);
        return this;
    }

    @NonNull
    public ToStringBuilder append(ToStringBuildable obj) {
        return append(obj, false);
    }

    @NonNull
    public ToStringBuilder append(ToStringBuildable obj, boolean omitTypeName) {
        if (null != obj) {
            return appendImpl(obj, omitTypeName);
        }
        backingBuilder.append(NULL_STRING);
        return this;
    }

    @NonNull
    public ToStringBuilder append(Enum<?> e, boolean omitTypeName) {
        if (null == e) {
            backingBuilder.append(NULL_STRING);
        } else {
            if (!omitTypeName) {
                backingBuilder.append(e.getClass().getName()).append('.');
            }
            backingBuilder.append(e.name());
        }
        return this;
    }

    @NonNull
    public ToStringBuilder append(@Nullable Iterable<?> iterable, boolean omitTypeName, boolean omitElementType) {
        if (null != iterable) {
            return appendImpl(iterable.iterator(), (omitTypeName) ? "" : iterable.getClass().getName(), false, omitElementType);
        }
        backingBuilder.append(NULL_STRING);
        return this;
    }

    @NonNull
    public ToStringBuilder append(@Nullable Stream<?> stream, boolean omitTypeName, boolean omitElementType) {
        if (null != stream) {
            return appendImpl(stream.iterator(), (omitTypeName) ? "" : stream.getClass().getName(), false, omitElementType);
        }
        backingBuilder.append(NULL_STRING);
        return this;
    }

    @NonNull
    public ToStringBuilder append(@Nullable boolean[] array, boolean omitTypeName) {
        if (null != array) {
            return appendImpl(array, omitTypeName);
        }
        backingBuilder.append(NULL_STRING);
        return this;
    }

    @NonNull
    public ToStringBuilder append(@Nullable byte[] array, boolean omitTypeName) {
        if (null != array) {
            return appendImpl(array, omitTypeName);
        }
        backingBuilder.append(NULL_STRING);
        return this;
    }

    @NonNull
    public ToStringBuilder append(@Nullable short[] array, boolean omitTypeName) {
        if (null != array) {
            return appendImpl(array, omitTypeName);
        }
        backingBuilder.append(NULL_STRING);
        return this;
    }

    @NonNull
    public ToStringBuilder append(@Nullable char[] array, boolean omitTypeName) {
        if (null != array) {
            return appendImpl(array, omitTypeName);
        }
        backingBuilder.append(NULL_STRING);
        return this;
    }

    @NonNull
    public ToStringBuilder append(@Nullable int[] array, boolean omitTypeName) {
        if (null != array) {
            return appendImpl(array, omitTypeName);
        }
        backingBuilder.append(NULL_STRING);
        return this;
    }

    @NonNull
    public ToStringBuilder append(@Nullable long[] array, boolean omitTypeName) {
        if (null != array) {
            return appendImpl(array, omitTypeName);
        }
        backingBuilder.append(NULL_STRING);
        return this;
    }

    @NonNull
    public ToStringBuilder append(@Nullable float[] array, boolean omitTypeName) {
        if (null != array) {
            return appendImpl(array, omitTypeName);
        }
        backingBuilder.append(NULL_STRING);
        return this;
    }

    @NonNull
    public ToStringBuilder append(@Nullable double[] array, boolean omitTypeName) {
        if (null != array) {
            return appendImpl(array, omitTypeName);
        }
        backingBuilder.append(NULL_STRING);
        return this;
    }

    @NonNull
    public <T> ToStringBuilder append(@Nullable T[] array, boolean omitTypeName, boolean omitElementType) {
        if (null != array) {
            Class<?> t = array.getClass().getComponentType();
            return appendImpl(Arrays.stream(array).iterator(), (omitTypeName || null == t) ? "" : t.getName(), false, omitElementType);
        }
        backingBuilder.append(NULL_STRING);
        return this;
    }

    @NonNull
    public ToStringBuilder append(@Nullable LocalDate obj, boolean omitTypeName) {
        if (null != obj) {
            return appendImpl(obj, omitTypeName);
        }
        backingBuilder.append(NULL_STRING);
        return this;
    }

    @NonNull
    public ToStringBuilder append(@Nullable LocalTime obj, boolean omitTypeName) {
        if (null != obj) {
            return appendImpl(obj, omitTypeName);
        }
        backingBuilder.append(NULL_STRING);
        return this;
    }

    @NonNull
    public ToStringBuilder append(@Nullable Object obj, boolean omitTypeName, boolean omitElementType) {
        if (null != obj) {
            return appendImpl(obj, omitTypeName, omitElementType);
        }
        backingBuilder.append(NULL_STRING);
        return this;
    }

    @NonNull
    public ToStringBuilder append(@NonNull String name, @Nullable String str) {
        backingBuilder.append(", ").append(name).append("=");
        return append(str);
    }

    @NonNull
    public ToStringBuilder append(@NonNull String name, @Nullable CharSequence csq) {
        backingBuilder.append(", ").append(name).append("=");
        return append(csq);
    }

    @NonNull
    public ToStringBuilder append(@NonNull String name, @Nullable CharSequence csq, boolean omitTypeName) {
        backingBuilder.append(", ").append(name).append("=");
        return append(csq, omitTypeName);
    }

    @NonNull
    public ToStringBuilder append(@NonNull String name, boolean b) {
        backingBuilder.append(", ").append(name).append("=");
        return append(b);
    }

    @NonNull
    public ToStringBuilder append(@NonNull String name, char c) {
        backingBuilder.append(", ").append(name).append("=");
        return append(c);
    }

    @NonNull
    public ToStringBuilder append(@NonNull String name, int i) {
        backingBuilder.append(", ").append(name).append("=");
        return append(i);
    }

    @NonNull
    public ToStringBuilder append(@NonNull String name, long lng) {
        backingBuilder.append(", ").append(name).append("=");
        return append(lng);
    }

    @NonNull
    public ToStringBuilder append(@NonNull String name, float f) {
        backingBuilder.append(", ").append(name).append("=");
        return append(f);
    }

    @NonNull
    public ToStringBuilder append(@NonNull String name, double d) {
        backingBuilder.append(", ").append(name).append("=");
        return append(d);
    }

    @NonNull
    public ToStringBuilder append(@NonNull String name, @Nullable Boolean b) {
        backingBuilder.append(", ").append(name).append("=");
        return append(b);
    }

    @NonNull
    public ToStringBuilder append(@NonNull String name, @Nullable Character c) {
        backingBuilder.append(", ").append(name).append("=");
        return append(c);
    }

    @NonNull
    public ToStringBuilder append(@NonNull String name, @Nullable Integer i) {
        backingBuilder.append(", ").append(name).append("=");
        return append(i);
    }

    @NonNull
    public ToStringBuilder append(@NonNull String name, @Nullable Long lng) {
        backingBuilder.append(", ").append(name).append("=");
        return append(lng);
    }

    @NonNull
    public ToStringBuilder append(@NonNull String name, @Nullable Float f) {
        backingBuilder.append(", ").append(name).append("=");
        return append(f);
    }

    @NonNull
    public ToStringBuilder append(@NonNull String name, @Nullable Double d) {
        backingBuilder.append(", ").append(name).append("=");
        return append(d);
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    @NonNull
    public ToStringBuilder append(@NonNull String name, @Nullable Optional<?> o, boolean omitTypeName, boolean omitElementType) {
        backingBuilder.append(", ").append(name).append("=");
        return append(o, omitTypeName, omitElementType);
    }

    @NonNull
    public ToStringBuilder append(@NonNull String name, ToStringBuildable obj) {
        return append(name, obj, false);
    }

    @NonNull
    public ToStringBuilder append(@NonNull String name, ToStringBuildable obj, boolean omitTypeName) {
        backingBuilder.append(", ").append(name).append("=");
        return append(obj, omitTypeName);
    }

    @NonNull
    public ToStringBuilder append(@NonNull String name, Enum<?> e) {
        backingBuilder.append(", ").append(name).append("=");
        return append(e, true);
    }

    @NonNull
    public ToStringBuilder append(@NonNull String name, Enum<?> e, boolean include) {
        backingBuilder.append(", ").append(name).append("=");
        return append(e, !include);
    }

    @NonNull
    public ToStringBuilder append(@NonNull String name, @Nullable Iterable<?> iterable, boolean omitTypeName, boolean omitElementType) {
        backingBuilder.append(", ").append(name).append("=");
        return append(iterable, omitTypeName, omitElementType);
    }

    @NonNull
    public ToStringBuilder append(@NonNull String name, @Nullable Stream<?> stream, boolean omitTypeName, boolean omitElementType) {
        backingBuilder.append(", ").append(name).append("=");
        return append(stream, omitTypeName, omitElementType);
    }

    @NonNull
    public ToStringBuilder append(@NonNull String name, @Nullable boolean[] array, boolean omitTypeName) {
        backingBuilder.append(", ").append(name).append("=");
        return append(array, omitTypeName);
    }

    @NonNull
    public ToStringBuilder append(@NonNull String name, @Nullable byte[] array, boolean omitTypeName) {
        backingBuilder.append(", ").append(name).append("=");
        return append(array, omitTypeName);
    }

    @NonNull
    public ToStringBuilder append(@NonNull String name, @Nullable short[] array, boolean omitTypeName) {
        backingBuilder.append(", ").append(name).append("=");
        return append(array, omitTypeName);
    }

    @NonNull
    public ToStringBuilder append(@NonNull String name, @Nullable char[] array, boolean omitTypeName) {
        backingBuilder.append(", ").append(name).append("=");
        return append(array, omitTypeName);
    }

    @NonNull
    public ToStringBuilder append(@NonNull String name, @Nullable int[] array, boolean omitTypeName) {
        backingBuilder.append(", ").append(name).append("=");
        return append(array, omitTypeName);
    }

    @NonNull
    public ToStringBuilder append(@NonNull String name, @Nullable long[] array, boolean omitTypeName) {
        backingBuilder.append(", ").append(name).append("=");
        return append(array, omitTypeName);
    }

    @NonNull
    public ToStringBuilder append(@NonNull String name, @Nullable float[] array, boolean omitTypeName) {
        backingBuilder.append(", ").append(name).append("=");
        return append(array, omitTypeName);
    }

    @NonNull
    public ToStringBuilder append(@NonNull String name, @Nullable double[] array, boolean omitTypeName) {
        backingBuilder.append(", ").append(name).append("=");
        return append(array, omitTypeName);
    }

    @NonNull
    public <T> ToStringBuilder append(@NonNull String name, @Nullable T[] array, boolean omitTypeName, boolean omitElementType) {
        backingBuilder.append(", ").append(name).append("=");
        return append(array, omitTypeName, omitElementType);
    }

    @NonNull
    public ToStringBuilder append(@NonNull String name, LocalDate obj) {
        backingBuilder.append(", ").append(name).append("=");
        return append(obj, false);
    }

    @NonNull
    public ToStringBuilder append(@NonNull String name, LocalDate obj, boolean includeTypeName) {
        backingBuilder.append(", ").append(name).append("=");
        return append(obj, !includeTypeName);
    }

    @NonNull
    public ToStringBuilder append(@NonNull String name, LocalTime obj) {
        backingBuilder.append(", ").append(name).append("=");
        return append(obj, false);
    }

    @NonNull
    public ToStringBuilder append(@NonNull String name, LocalTime obj, boolean includeTypeName) {
        backingBuilder.append(", ").append(name).append("=");
        return append(obj, !includeTypeName);
    }

    @NonNull
    public ToStringBuilder append(@NonNull String name, Object obj, boolean omitTypeName, boolean omitElementType) {
        backingBuilder.append(", ").append(name).append("=");
        return append(obj, omitTypeName, omitElementType);
    }

    @Override
    public int length() {
        return backingBuilder.length();
    }

    @Override
    public char charAt(int index) {
        return backingBuilder.charAt(index);
    }

    @NonNull
    @Override
    public CharSequence subSequence(int start, int end) {
        return backingBuilder.subSequence(start, end);
    }

    @NonNull
    private ToStringBuilder appendImpl(@NonNull LocalDate obj, boolean omitTypeName) {
        if (omitTypeName) {
            backingBuilder.append('{');
        } else {
            backingBuilder.append(obj.getClass().getName()).append('{');
        }
        backingBuilder.append(obj).append('}');
        return this;
    }

    @NonNull
    private ToStringBuilder appendImpl(@NonNull LocalTime obj, boolean omitTypeName) {
        if (omitTypeName) {
            backingBuilder.append('{');
        } else {
            backingBuilder.append(obj.getClass().getName()).append('{');
        }
        backingBuilder.append(obj).append('}');
        return this;
    }

    @NonNull
    private ToStringBuilder appendImpl(@NonNull Object obj, boolean omitTypeName, boolean omitElementType) {
        if (obj instanceof Enum) {
            if (!omitTypeName) {
                backingBuilder.append(obj.getClass().getName()).append(".");
            }
            backingBuilder.append(((Enum<?>) obj).name());
        } else {
            if (obj instanceof String) {
                return appendImpl((String) obj, omitTypeName);
            }
            if (obj instanceof ToStringBuildable) {
                return appendImpl((ToStringBuildable) obj, omitTypeName);
            }
            if (obj instanceof Optional) {
                return appendImpl((Optional<?>) obj, omitTypeName, omitElementType);
            }
            if (obj instanceof CharSequence) {
                return appendImpl((CharSequence) obj, omitTypeName);
            }
            if (obj instanceof boolean[]) {
                return appendImpl((boolean[]) obj, omitTypeName);
            }
            if (obj instanceof byte[]) {
                return appendImpl((byte[]) obj, omitTypeName);
            }
            if (obj instanceof short[]) {
                return appendImpl((short[]) obj, omitTypeName);
            }
            if (obj instanceof char[]) {
                return appendImpl((char[]) obj, omitTypeName);
            }
            if (obj instanceof int[]) {
                return appendImpl((int[]) obj, omitTypeName);
            }
            if (obj instanceof long[]) {
                return appendImpl((long[]) obj, omitTypeName);
            }
            if (obj instanceof float[]) {
                return appendImpl((float[]) obj, omitTypeName);
            }
            if (obj instanceof double[]) {
                return appendImpl((double[]) obj, omitTypeName);
            }
            if (obj instanceof Object[]) {
                Class<?> t = obj.getClass().getComponentType();
                return appendImpl(Arrays.stream((Object[]) obj).iterator(), (omitTypeName || null == t) ? "" : t.getName(), true, omitElementType);
            }
            if (obj instanceof Iterable<?>) {
                return appendImpl(((Iterable<?>) obj).iterator(), (omitTypeName) ? "" : obj.getClass().getName(), false, omitElementType);
            }
            if (obj instanceof Stream<?>) {
                return appendImpl(((Stream<?>) obj).iterator(), (omitTypeName) ? "" : obj.getClass().getName(), false, omitElementType);
            }
            Class<?> c = obj.getClass();
            if (c.isPrimitive()) {
                if (omitTypeName) {
                    if (c == Character.TYPE) {
                        return append((char) obj);
                    }
                    backingBuilder.append(obj);
                } else if (c == Character.TYPE) {
                    backingBuilder.append(c.getSimpleName()).append("{");
                    append((char) obj);
                    backingBuilder.append("}");
                } else {
                    backingBuilder.append(c.getSimpleName()).append("{").append(obj).append("}");
                }
            } else {
                backingBuilder.append(obj);
            }
        }
        return this;
    }

    @NonNull
    private ToStringBuilder appendImpl(@NonNull String str) {
        if (str.isEmpty()) {
            backingBuilder.append("\"\"");
        } else {
            backingBuilder.append("\"");
            for (char c : str.toCharArray()) {
                appendUnquoted(c);
            }
            backingBuilder.append("\"");
        }
        return this;
    }

    @NonNull
    private ToStringBuilder appendImpl(@NonNull CharSequence seq, boolean omitTypeName) {
        if (!omitTypeName) {
            backingBuilder.append(seq.getClass().getName());
        }
        if (seq.length() == 0) {
            backingBuilder.append("\"\"");
        } else {
            backingBuilder.append("\"");
            for (int i = 0; i < seq.length(); i++) {
                appendUnquoted(seq.charAt(i));
            }
            backingBuilder.append("\"");
        }
        return this;
    }

    private void appendUnquoted(char c) {
        switch (c) {
            case ' ':
                backingBuilder.append(' ');
                break;
            case '\b':
                backingBuilder.append("\\b");
                break;
            case '\t':
                backingBuilder.append("\\t");
                break;
            case '\r':
                backingBuilder.append("\\r");
                break;
            case '\n':
                backingBuilder.append("\\n");
                break;
            case '\f':
                backingBuilder.append("\\f");
                break;
            case '"':
                backingBuilder.append("\\\"");
                break;
            case '\\':
                backingBuilder.append("\\\\");
                break;
            default:
                if (Character.isISOControl(c) || Character.isWhitespace(c) || (int) c > 126)
                    backingBuilder.append(String.format("\\u%04x", (int) c));
                else
                    backingBuilder.append(c);
                break;
        }
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    @NonNull
    private ToStringBuilder appendImpl(@NonNull Optional<?> b, boolean omitTypeName, boolean omitElementType) {
        if (b.isPresent()) {
            backingBuilder.append((omitTypeName) ? "PRESENT{" : "Optional.present{");
            append(b.get(), omitElementType, false);
            backingBuilder.append("}");
        } else {
            backingBuilder.append((omitTypeName) ? "EMPTY" : "Optional.EMPTY");
        }
        return this;
    }

    @NonNull
    private ToStringBuilder appendImpl(@NonNull boolean[] array, boolean omitTypeName) {
        int len = array.length;
        if (omitTypeName) {
            if (len == 0) {
                backingBuilder.append("[]");
                return this;
            }
            backingBuilder.append("[").append(array[0]);
        } else {
            if (len == 0) {
                backingBuilder.append("boolean[]");
                return this;
            }
            backingBuilder.append("boolean[").append(array[0]);
        }
        for (int i = 1; i < len; i++) {
            backingBuilder.append(", ").append(array[i]);
        }
        return this;
    }

    @NonNull
    private ToStringBuilder appendImpl(@NonNull byte[] array, boolean omitTypeName) {
        int len = array.length;
        if (omitTypeName) {
            if (len == 0) {
                backingBuilder.append("[]");
                return this;
            }
            backingBuilder.append("[").append(array[0]);
        } else {
            if (len == 0) {
                backingBuilder.append("byte[]");
                return this;
            }
            backingBuilder.append("byte[").append(array[0]);
        }
        for (int i = 1; i < len; i++) {
            backingBuilder.append(", ").append(array[i]);
        }
        return this;
    }

    @NonNull
    private ToStringBuilder appendImpl(@NonNull short[] array, boolean omitTypeName) {
        int len = array.length;
        if (omitTypeName) {
            if (len == 0) {
                backingBuilder.append("[]");
                return this;
            }
            backingBuilder.append("[").append(array[0]);
        } else {
            if (len == 0) {
                backingBuilder.append("short[]");
                return this;
            }
            backingBuilder.append("short[").append(array[0]);
        }
        for (int i = 1; i < len; i++) {
            backingBuilder.append(", ").append(array[i]);
        }
        return this;
    }

    @NonNull
    private ToStringBuilder appendImpl(@NonNull char[] array, boolean omitTypeName) {
        int len = array.length;
        if (omitTypeName) {
            if (len == 0) {
                backingBuilder.append("[]");
                return this;
            }
            backingBuilder.append("[").append(array[0]);
        } else {
            if (len == 0) {
                backingBuilder.append("char[]");
                return this;
            }
            backingBuilder.append("char[").append(array[0]);
        }
        for (int i = 1; i < len; i++) {
            backingBuilder.append(", ").append(array[i]);
        }
        return this;
    }

    @NonNull
    private ToStringBuilder appendImpl(@NonNull int[] array, boolean omitTypeName) {
        int len = array.length;
        if (omitTypeName) {
            if (len == 0) {
                backingBuilder.append("[]");
                return this;
            }
            backingBuilder.append("[").append(array[0]);
        } else {
            if (len == 0) {
                backingBuilder.append("int[]");
                return this;
            }
            backingBuilder.append("int[").append(array[0]);
        }
        for (int i = 1; i < len; i++) {
            backingBuilder.append(", ").append(array[i]);
        }
        return this;
    }

    @NonNull
    private ToStringBuilder appendImpl(@NonNull long[] array, boolean omitTypeName) {
        int len = array.length;
        if (omitTypeName) {
            if (len == 0) {
                backingBuilder.append("[]");
                return this;
            }
            backingBuilder.append("[").append(array[0]);
        } else {
            if (len == 0) {
                backingBuilder.append("long[]");
                return this;
            }
            backingBuilder.append("long[").append(array[0]);
        }
        for (int i = 1; i < len; i++) {
            backingBuilder.append(", ").append(array[i]);
        }
        return this;
    }

    @NonNull
    private ToStringBuilder appendImpl(@NonNull float[] array, boolean omitTypeName) {
        int len = array.length;
        if (omitTypeName) {
            if (len == 0) {
                backingBuilder.append("[]");
                return this;
            }
            backingBuilder.append("[").append(array[0]);
        } else {
            if (len == 0) {
                backingBuilder.append("float[]");
                return this;
            }
            backingBuilder.append("float[").append(array[0]);
        }
        for (int i = 1; i < len; i++) {
            backingBuilder.append(", ").append(array[i]);
        }
        return this;
    }

    @NonNull
    private ToStringBuilder appendImpl(@NonNull double[] array, boolean omitTypeName) {
        int len = array.length;
        if (omitTypeName) {
            if (len == 0) {
                backingBuilder.append("[]");
                return this;
            }
            backingBuilder.append("[").append(array[0]);
        } else {
            if (len == 0) {
                backingBuilder.append("double[]");
                return this;
            }
            backingBuilder.append("double[").append(array[0]);
        }
        for (int i = 1; i < len; i++) {
            backingBuilder.append(", ").append(array[i]);
        }
        return this;
    }

    @NonNull
    private ToStringBuilder appendImpl(@NonNull Iterator<?> iterator, @Nullable String typeName, boolean isArray, boolean omitElementType) {
        if (null != typeName && !typeName.isEmpty()) {
            backingBuilder.append(typeName);
        }
        if (iterator.hasNext()) {
            if (isArray) {
                backingBuilder.append('[');
            } else {
                backingBuilder.append("{[");
            }
            append(iterator.next(), omitElementType, false);
            while (iterator.hasNext()) {
                backingBuilder.append(", ");
                append(iterator.next(), omitElementType, false);
            }
            if (isArray) {
                backingBuilder.append(']');
            } else {
                backingBuilder.append("]}");
            }
        } else {
            backingBuilder.append((isArray) ? "[]" : "{[]}");
        }
        return this;
    }

    @NonNull
    private ToStringBuilder appendImpl(@NonNull ToStringBuildable obj, boolean omitTypeName) {
        if (omitTypeName) {
            backingBuilder.append('{');
        } else {
            backingBuilder.append(obj.getClass().getName()).append('{');
        }
        obj.appendPropertiesAsStrings(this);
        backingBuilder.append('}');
        return this;
    }

    @NonNull
    @Override
    public String toString() {
        return backingBuilder.toString();
    }
}
