package Erwine.Leonard.T.wguscheduler356334.util;

import androidx.annotation.NonNull;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public class ComparisonHelper {

    @SafeVarargs
    public static <T extends Comparable<T>> Optional<T> maxValue(T... values) {
        return maxValue(Comparable::compareTo, values);
    }

    @SafeVarargs
    public static <T> Optional<T> maxValue(@NonNull Comparator<T> comparator, T... values) {
        if (null == values || values.length == 0) {
            return Optional.empty();
        }
        if (values.length == 1) {
            return Optional.ofNullable(values[0]);
        }
        if (values.length == 2) {
            T a = values[0];
            if (null == a) {
                return Optional.ofNullable(values[1]);
            }
            T b = values[1];
            Optional.of((null != b && comparator.compare(a, b) < 0) ? b : a);
        }
        return Arrays.stream(values).filter(Objects::nonNull).max(comparator);
    }

    public static <T extends Comparable<T>> Optional<T> maxWithinRange(T rangeStartInclusive, T rangeEndExclusive, @NonNull Stream<T> values) {
        return maxWithinRange(rangeStartInclusive, rangeEndExclusive, values, Comparable::compareTo);
    }

    public static <T> Optional<T> maxWithinRange(T rangeStartInclusive, T rangeEndExclusive, @NonNull Stream<T> values, @NonNull Comparator<T> comparator) {
        Stream<T> filtered = values.filter(Objects::nonNull);
        if (null != rangeStartInclusive) {
            filtered = filtered.filter(t -> comparator.compare(rangeStartInclusive, t) <= 0);
        }
        if (null != rangeEndExclusive) {
            filtered = filtered.filter(t -> comparator.compare(rangeEndExclusive, t) > 0);
        }
        return filtered.max(comparator);
    }

    public static <T extends Comparable<T>> int compareRanges(T start1, T end1, T start2, T end2) {
        return compareRanges(start1, end1, start2, end2, Comparable::compareTo);
    }

    public static <T> int compareRanges(T start1, T end1, T start2, T end2, @NonNull Comparator<T> comparator) {
        if (null != start1) {
            if (null == start2) {
                return (null == end2) ? -1 : 1;
            }
            int result = comparator.compare(start1, start2);
            if (result != 0) {
                return result;
            }
            if (null == end1) {
                return (null == end2) ? 0 : 1;
            }
        } else if (null == end1) {
            return (null == start2 && null == end2) ? 0 : 1;
        }
        return (null == end2) ? -1 : comparator.compare(end1, end2);
    }

    private ComparisonHelper() {
    }
}
