package Erwine.Leonard.T.wguscheduler356334.util;

import androidx.annotation.NonNull;

import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public class ComparisonHelper {

    public static <T extends Comparable<T>> Optional<T> maxWithinRange(T rangeStartInclusive, T rangeEndExclusive, Stream<T> values) {
        Stream<T> filtered = values.filter(Objects::nonNull);
        if (null != rangeStartInclusive) {
            filtered = filtered.filter(t -> rangeStartInclusive.compareTo(t) <= 0);
        }
        if (null != rangeEndExclusive) {
            filtered = filtered.filter(t -> rangeEndExclusive.compareTo(t) > 0);
        }
        return filtered.max(Comparable::compareTo);
    }

    public static <T> Optional<T> maxWithinRange(T rangeStartInclusive, T rangeEndExclusive, Stream<T> values, @NonNull Comparator<T> comparator) {
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
        if (null != start1) {
            if (null == start2) {
                return (null == end2) ? -1 : 1;
            }
            int result = start1.compareTo(start2);
            if (result != 0) {
                return result;
            }
            if (null == end1) {
                return (null == end2) ? 0 : 1;
            }
        } else if (null == end1) {
            return (null == start2 && null == end2) ? 0 : 1;
        }
        return (null == end2) ? -1 : end1.compareTo(end2);
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
