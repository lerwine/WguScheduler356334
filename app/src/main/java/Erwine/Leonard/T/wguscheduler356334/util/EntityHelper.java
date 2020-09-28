package Erwine.Leonard.T.wguscheduler356334.util;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;

import Erwine.Leonard.T.wguscheduler356334.entity.AbstractCourseEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.AbstractEntity;

public class EntityHelper {
    @NonNull
    public static <T extends AbstractEntity<T>> Optional<T> findById(@Nullable Long id, @NonNull Collection<T> source) {
        if (null == id) {
            return Optional.empty();
        }
        return source.stream().filter(t -> {
            Long i = t.getId();
            return id.equals(i);
        }).findFirst();
    }

    public static Optional<LocalDate> getLatestDate(Collection<? extends AbstractCourseEntity<?>> courses) {
        if (null == courses || courses.isEmpty()) {
            return Optional.empty();
        }
        return getLatestDate(courses.stream());
    }

    public static Optional<LocalDate> getLatestDate(LocalDate rangeStartInclusive, LocalDate rangeEndExclusive, Collection<? extends AbstractCourseEntity<?>> courses) {
        if (null == courses || courses.isEmpty()) {
            return Optional.empty();
        }
        return getLatestDate(rangeStartInclusive, rangeEndExclusive, courses.stream());
    }

    public static Optional<LocalDate> getLatestDate(Stream<? extends AbstractCourseEntity<?>> courses) {
        return courses.map(t -> ComparisonHelper.<LocalDate>maxValue(LocalDate::compareTo, t.getExpectedStart(), t.getExpectedEnd(), t.getActualStart(), t.getActualEnd()))
                .filter(Optional::isPresent).map(Optional::get).max(LocalDate::compareTo);
    }

    public static Optional<LocalDate> getLatestDate(LocalDate rangeStartInclusive, LocalDate rangeEndExclusive, Stream<? extends AbstractCourseEntity<?>> courses) {
        return ComparisonHelper.maxWithinRange(rangeStartInclusive, rangeEndExclusive, courses.map(t -> ComparisonHelper.<LocalDate>maxValue(LocalDate::compareTo, t.getExpectedStart(), t.getExpectedEnd(), t.getActualStart(), t.getActualEnd()))
                .filter(Optional::isPresent).map(Optional::get), LocalDate::compareTo);
    }

    @Nullable
    public static LocalDate parseDateString(@Nullable String value) {
        if (null == value || (value = value.trim()).isEmpty()) {
            return null;
        }
        return LocalDate.parse(value);
    }

    private EntityHelper() {
    }
}
