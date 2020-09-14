package Erwine.Leonard.T.wguscheduler356334.util;

import androidx.annotation.NonNull;

import java.util.Objects;
import java.util.function.Function;
import java.util.stream.IntStream;

public class NormalizingCharSequence implements CharSequence {

    public static String toString(CharSequence source) {
        if (null == source || source instanceof String) {
            return (String) source;
        }
        if (source instanceof NormalizingCharSequence) {
            return ((NormalizingCharSequence) source).asIs();
        }
        return source.toString();
    }

    private final Function<String, String> normalizer;
    private String backingString = "";
    private boolean rawIsNull = true;
    private String rawString = null;

    public NormalizingCharSequence(CharSequence initialValue, StringNormalizationOption... options) {
        this(initialValue, StringHelper.getNormalizer(options));
    }

    public NormalizingCharSequence(CharSequence initialValue, Function<String, String> normalizer) {
        this.normalizer = Objects.requireNonNull(normalizer);
        setValue(initialValue);
    }

    public synchronized String asIs() {
        return (null == backingString) ? rawString : backingString;
    }

    public String rawString() {
        if (rawIsNull) {
            return "";
        }
        return (null == rawString) ? backingString : rawString;
    }

    public synchronized String rawValue() {
        String s = rawString;
        return (rawIsNull || null != s) ? s : backingString;
    }

    private void applyNormalizer() {
        backingString = normalizer.apply(rawString);
        if (backingString.equals(rawString)) {
            rawString = null;
        }
    }

    public boolean isNormalized() {
        if (null == backingString) {
            applyNormalizer();
        }
        return null == rawString;
    }

    public boolean isEmpty() {
        return toString().isEmpty();
    }

    @Override
    public int length() {
        return toString().length();
    }

    @Override
    public char charAt(int index) {
        return toString().charAt(index);
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        return toString().substring(start, end);
    }

    @Override
    public IntStream chars() {
        return toString().chars();
    }

    @Override
    public IntStream codePoints() {
        return toString().codePoints();
    }

    public synchronized void setValue(CharSequence value) {
        if (value instanceof NormalizingCharSequence) {
            rawString = ((NormalizingCharSequence) value).rawString;
            backingString = ((NormalizingCharSequence) value).backingString;
            rawIsNull = ((NormalizingCharSequence) value).rawIsNull;
        } else {
            rawIsNull = null == value;
            rawString = (rawIsNull || value instanceof String) ? (String) value : value.toString();
            backingString = null;
        }
    }

    public static boolean equals(NormalizingCharSequence a, CharSequence b) {
        if (null == b) {
            return null == a;
        }
        return null != a && ((b instanceof NormalizingCharSequence) ? a.equals(b) : a.toString().equals((b instanceof String) ? (String) b : b.toString()));
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof NormalizingCharSequence && toString().equals(o);
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @NonNull
    @Override
    public synchronized String toString() {
        if (null == backingString) {
            if (rawIsNull) {
                return "";
            }
            applyNormalizer();
        }
        return backingString;
    }
}
