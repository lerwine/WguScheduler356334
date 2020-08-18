package Erwine.Leonard.T.wguscheduler356334.util;

public class NormalizedCharSequence implements java.io.Serializable, Comparable<String>, CharSequence {

    private final String normalizedValue;

    private NormalizedCharSequence(String normalizedValue) {
        this.normalizedValue = normalizedValue;
    }

    public static NormalizedCharSequence of(CharSequence source) {
        if (null == source) {
            return new NormalizedCharSequence("") {
                @Override
                public String getOriginalValue() {
                    return null;
                }

                @Override
                public boolean isOriginalNormalized() {
                    return false;
                }
            };
        }
        String o = (source instanceof String) ? (String) source : source.toString();
        String n = Values.asNonNullAndWsNormalized(o);
        if (o.equals(n)) {
            return new NormalizedCharSequence(n);
        }
        return new NormalizedCharSequence(n) {
            private final String originalValue = o;

            @Override
            public String getOriginalValue() {
                return originalValue;
            }

            @Override
            public boolean isOriginalNormalized() {
                return false;
            }
        };
    }

    public String getNormalizedValue() {
        return normalizedValue;
    }

    public String getOriginalValue() {
        return normalizedValue;
    }

    public boolean isOriginalNormalized() {
        return true;
    }

    public boolean isEmpty() {
        return normalizedValue.isEmpty();
    }

    @Override
    public int length() {
        return normalizedValue.length();
    }

    @Override
    public char charAt(int index) {
        return normalizedValue.charAt(index);
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        return normalizedValue.subSequence(start, end);
    }

    @Override
    public int compareTo(String o) {
        return normalizedValue.compareTo(o);
    }

    @Override
    public int hashCode() {
        return normalizedValue.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        return o == this || (o instanceof NormalizedCharSequence && normalizedValue.equals(((NormalizedCharSequence) o).normalizedValue));
    }

}
