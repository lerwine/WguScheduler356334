package Erwine.Leonard.T.wguscheduler356334.util;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import Erwine.Leonard.T.wguscheduler356334.entity.AbstractEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.course.AbstractCourseEntity;

public class EntityHelper {

    public static <T extends AbstractEntity<T>> Map<Long, T> mapById(@NonNull Collection<T> source) {
        return mapById(source, AbstractEntity::getId);
    }

    public static <T extends AbstractEntity<T>> Map<Long, T> mapById(@NonNull Stream<T> source) {
        return mapById(source, AbstractEntity::getId);
    }

    public static <T> Map<Long, T> mapById(@NonNull Collection<T> source, @NonNull Function<T, Long> mapper) {
        return mapById(source.stream(), mapper);
    }

    public static <T> Map<Long, T> mapById(@NonNull Stream<T> source, @NonNull Function<T, Long> mapper) {
        return source.collect(Collectors.toMap(mapper, t -> t));
    }

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
