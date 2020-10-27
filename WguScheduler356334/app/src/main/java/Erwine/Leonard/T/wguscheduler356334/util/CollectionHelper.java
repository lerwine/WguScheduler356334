package Erwine.Leonard.T.wguscheduler356334.util;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class CollectionHelper {

    //<editor-fold defaultstate="collapsed" desc="toStream methods">

    //<editor-fold defaultstate="collapsed" desc="overloads:(source)">

    /**
     * Builds a {@link Stream} from the elements of an {@link Iterable} object.
     *
     * @param source The {@link Iterable} source for the {@link Stream}.
     * @param <T>    The element type.
     * @return A {@link Stream} containing elements from the source {@link Iterable} or {@code null} if {@code source} was {@code null}.
     */
    @Nullable
    public static <T> Stream<T> toStream(Iterable<T> source) {
        if (null == source)
            return null;
        if (source instanceof Collection)
            return ((Collection<T>) source).stream();
        return toStreamImpl(source.iterator());
    }

    /**
     * Builds a {@link Stream} from the elements of an {@link Iterator} object.
     *
     * @param source The {@link Iterator} source for the {@link Stream}.
     * @param <T>    The element type.
     * @return A {@link Stream} containing elements from the source {@link Iterator} or {@code null} if {@code source} was {@code null}.
     */
    @Nullable
    public static <T> Stream<T> toStream(Iterator<T> source) {
        if (null == source)
            return null;
        return toStreamImpl(source);
    }

    private static <T> Stream<T> toStreamImpl(@NonNull Iterator<T> source) {
        if (source.hasNext()) {
            T value = source.next();
            if (source.hasNext()) {
                Stream.Builder<T> builder = Stream.builder();
                builder.accept(value);
                source.forEachRemaining(builder);
                return builder.build();
            }
            return Stream.of(value);
        }
        return Stream.empty();
    }

    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="overloads:(source, filter)">

    /**
     * Builds a {@link Stream} from the filtered elements of an {@link Iterable} object.
     *
     * @param source The {@link Iterable} source for the {@link Stream}.
     * @param filter The {@link Predicate} which determines what elements are included in the result {@link Stream}.
     * @param <T>    The element type.
     * @return A {@link Stream} containing the filtered elements from the source {@link Iterable} or {@code null} if {@code source} was {@code null}.
     */
    @Nullable
    public static <T> Stream<T> toStream(Iterable<T> source, Predicate<T> filter) {
        if (null == source)
            return null;
        if (source instanceof Collection)
            return ((Collection<T>) source).stream().filter(filter);
        return toStream(filter, source.iterator());
    }

    /**
     * Builds a {@link Stream} from the filtered elements of an {@link Iterator} object.
     *
     * @param source The {@link Iterator} source for the {@link Stream}.
     * @param filter The {@link Predicate} which determines what elements are included in the result {@link Stream}.
     * @param <T>    The element type.
     * @return A {@link Stream} containing the filtered elements from the source {@link Iterator} or {@code null} if {@code source} was {@code null}.
     */
    @Nullable
    public static <T> Stream<T> toStream(Iterator<T> source, Predicate<T> filter) {
        if (null == source)
            return null;
        return toStream(filter, source);
    }

    private static <T> Stream<T> toStream(Predicate<T> filter, @NonNull Iterator<T> source) {
        Objects.requireNonNull(filter);
        if (source.hasNext()) {
            T value = source.next();
            if (source.hasNext()) {
                Stream.Builder<T> builder = Stream.builder();
                if (filter.test(value))
                    builder.accept(value);
                source.forEachRemaining(t -> {
                    if (filter.test(t))
                        builder.accept(t);
                });
                return builder.build();
            }
            if (filter.test(value))
                return Stream.of(value);
        }
        return Stream.empty();
    }

    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="overloads:(source, mapper)">

    /**
     * Builds a {@link Stream} from the mapped elements from an {@link Iterable} object.
     *
     * @param source The {@link Iterable} source for the {@link Stream}.
     * @param mapper The {@link Function} that converts the source element to the target element.
     * @param <T>    The source element type.
     * @param <R>    The result element type.
     * @return A {@link Stream} containing the mapped elements from the source {@link Iterable} or {@code null} if {@code source} was {@code null}.
     */
    @Nullable
    public static <T, R> Stream<R> toStream(Iterable<T> source, Function<T, R> mapper) {
        Objects.requireNonNull(mapper);
        if (null == source)
            return null;
        if (source instanceof Collection)
            return ((Collection<T>) source).stream().map(mapper);
        return toStream(mapper, source.iterator());
    }

    /**
     * Builds a {@link Stream} from the mapped elements from an {@link Iterator} object.
     *
     * @param source The {@link Iterator} source for the {@link Stream}.
     * @param mapper The {@link Function} that converts the source element to the target element.
     * @param <T>    The source element type.
     * @param <R>    The result element type.
     * @return A {@link Stream} containing the mapped elements from the source {@link Iterator} or {@code null} if {@code source} was {@code null}.
     */
    @Nullable
    public static <T, R> Stream<R> toStream(Iterator<T> source, Function<T, R> mapper) {
        if (null == source)
            return null;
        return toStream(mapper, source);
    }

    private static <T, R> Stream<R> toStream(Function<T, R> mapper, @NonNull Iterator<T> source) {
        Objects.requireNonNull(mapper);
        if (source.hasNext()) {
            R value = mapper.apply(source.next());
            if (source.hasNext()) {
                Stream.Builder<R> builder = Stream.builder();
                builder.accept(value);
                source.forEachRemaining(t -> builder.accept(mapper.apply(t)));
                return builder.build();
            }
            return Stream.of(value);
        }
        return Stream.empty();
    }

    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="overloads:(source, mapper, filter)">

    /**
     * Builds a {@link Stream} from the mapped and filtered elements of an {@link Iterable} object.
     *
     * @param source The {@link Iterable} source for the {@link Stream}.
     * @param mapper The {@link Function} that converts the source element to the target element.
     * @param filter The {@link Predicate} that determines what mapped elements are included in the result {@link Stream}.
     * @param <T>    The source element type.
     * @param <R>    The result element type.
     * @return A {@link Stream} containing the mapped and filtered elements from the source {@link Iterable} or {@code null} if {@code source} was {@code null}.
     */
    @Nullable
    public static <T, R> Stream<R> toStream(Iterable<T> source, Function<T, R> mapper, Predicate<R> filter) {
        Objects.requireNonNull(mapper);
        if (null == source)
            return null;
        if (source instanceof Collection)
            return ((Collection<T>) source).stream().map(mapper).filter(filter);
        return toStream(mapper, filter, source.iterator());
    }

    /**
     * Builds a {@link Stream} from the mapped and filtered elements of an {@link Iterator} object.
     *
     * @param source The {@link Iterator} source for the {@link Stream}.
     * @param mapper The {@link Function} that converts the source element to the target element.
     * @param filter The {@link Predicate} that determines what mapped elements are included in the result {@link Stream}.
     * @param <T>    The source element type.
     * @param <R>    The result element type.
     * @return A {@link Stream} containing the mapped and filtered elements from the source {@link Iterator} or {@code null} if {@code source} was {@code null}.
     */
    @Nullable
    public static <T, R> Stream<R> toStream(Iterator<T> source, Function<T, R> mapper, Predicate<R> filter) {
        if (null == source)
            return null;
        return toStream(mapper, filter, source);
    }

    private static <T, R> Stream<R> toStream(Function<T, R> mapper, Predicate<R> filter, @NonNull Iterator<T> source) {
        Objects.requireNonNull(mapper);
        Objects.requireNonNull(filter);
        if (source.hasNext()) {
            R value = mapper.apply(source.next());
            if (source.hasNext()) {
                Stream.Builder<R> builder = Stream.builder();
                if (filter.test(value))
                    builder.accept(value);
                source.forEachRemaining(t -> {
                    R v = mapper.apply(t);
                    if (filter.test(v))
                        builder.accept(v);
                });
                return builder.build();
            }
            if (filter.test(value))
                return Stream.of(value);
        }
        return Stream.empty();
    }

    //</editor-fold>

    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="ifAllElementsMatch methods">

    //<editor-fold defaultstate="collapsed" desc="overloads:(first, second, predicate)">

    /**
     * Gets an {@link Optional} {@link Stream} of elements from the second {@link Iterable} if adjacent elements from both {@link Iterable Iterables} do not all match in
     * sequential order, and both {@link Iterable Iterables} have the same number of elements.
     *
     * @param first     The first {@link Iterable} object.
     * @param second    The second {@link Iterable} object.
     * @param predicate The {@link BiPredicate} that determines which elements from each {@link Iterable} at the same position. When this returns {@code false},
     *                  no further tests will be done and a {@link Stream} of the elements from the second {@link Iterable} will be returned.
     * @param <T>       The element type for the first {@link Iterable} object.
     * @param <U>       The result element type as well as the element type for the second {@link Iterable} object.
     * @return An empty {@link Optional} if both {@link Iterable Iterables} had the same number of elements and the {@code predicate} returned true for each adjacent element;
     * Otherwise, the {@link Optional} value will contain a {@link Stream} of elements from the second {@link Iterable}.
     */
    public static <T, U> Optional<Stream<U>> ifAnyElementDiffers(Iterable<T> first, Iterable<U> second, BiPredicate<T, U> predicate) {
        if (null == first) {
            return Optional.ofNullable(toStream(second));
        }
        if (null == second || first == second)
            return Optional.of(Stream.empty());
        if (first instanceof Collection && second instanceof Collection) {
            Collection<T> x = (Collection<T>) first;
            Collection<U> y = (Collection<U>) second;
            if (x.size() != y.size())
                return Optional.of(y.stream());
            if (x.isEmpty())
                return Optional.empty();
        }
        return ifAnyElementDiffers(predicate, first.iterator(), second.iterator());
    }

    /**
     * Gets an {@link Optional} {@link Stream} of elements from the second {@link Stream} if adjacent elements from both {@link Stream Streams} do not all match in sequential
     * order, and both {@link Stream Streams} have the same number of elements.
     *
     * @param first     The first {@link Stream}.
     * @param second    The second {@link Stream}.
     * @param predicate The {@link BiPredicate} that determines which elements from each {@link Stream} at the same position. When this returns {@code false},
     *                  no further tests will be done and a {@link Stream} of the elements from the second {@link Stream} will be returned.
     * @param <T>       The element type for the first {@link Stream}.
     * @param <U>       The element type for the result {@link Stream} as well as for the second {@link Stream}.
     * @return An empty {@link Optional} if both {@link Stream Streams} iterables had the same number of elements and the {@code predicate} returned true for each adjacent
     * element; Otherwise, the {@link Optional} value will contain a {@link Stream} of elements from the second {@link Stream}.
     */
    public static <T, U> Optional<Stream<U>> ifAnyElementDiffers(Stream<T> first, Stream<U> second, BiPredicate<T, U> predicate) {
        if (null == first) {
            return Optional.ofNullable(second);
        }
        if (null == second || first == second)
            return Optional.of(Stream.empty());

        return ifAnyElementDiffers(predicate, first.iterator(), second.iterator());
    }

    /**
     * Gets an {@link Optional} {@link Stream} of elements from the second {@link Iterator} if adjacent elements from both {@link Iterator Iterators} do not all match in
     * sequential order, and both {@link Iterator Iterators} have the same number of elements.
     *
     * @param first     The first {@link Iterator} object.
     * @param second    The second {@link Iterator} object.
     * @param predicate The {@link BiPredicate} that determines which elements from each {@link Iterator} at the same position. When this returns {@code false},
     *                  no further tests will be done and a {@link Stream} of the elements from the second {@link Iterator} will be returned.
     * @param <T>       The element type for the first {@link Iterator} object.
     * @param <U>       The result element type as well as the element type for the second {@link Iterator} object.
     * @return An empty {@link Optional} if both {@link Iterator Iterators} had the same number of elements and the {@code predicate} returned true for each adjacent element;
     * Otherwise, the {@link Optional} value will contain a {@link Stream} of elements from the second {@link Iterator}.
     */
    public static <T, U> Optional<Stream<U>> ifAnyElementDiffers(Iterator<T> first, Iterator<U> second, BiPredicate<T, U> predicate) {
        if (null == first) {
            return Optional.ofNullable(toStream(second));
        }
        if (null == second || first == second)
            return Optional.of(Stream.empty());

        return ifAnyElementDiffers(predicate, first, second);
    }

    private static <T, U> Optional<Stream<U>> ifAnyElementDiffers(BiPredicate<T, U> predicate, @NonNull Iterator<T> a, Iterator<U> b) {
        Stream.Builder<U> builder = Stream.builder();
        Objects.requireNonNull(predicate);
        while (b.hasNext()) {
            U value = b.next();
            if (!a.hasNext() && predicate.test(a.next(), value)) {
                builder.accept(value);
                b.forEachRemaining(builder);
                return Optional.of(builder.build());
            }
            builder.accept(value);
        }
        if (a.hasNext()) {
            return Optional.of(builder.build());
        }
        return Optional.empty();
    }

    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="overloads:(first, second, filter, predicate)">

    /**
     * Gets an {@link Optional} {@link Stream} of filtered elements from the second {@link Iterable} if adjacent elements from the first {@link Iterable} do not all match
     * the filtered elements from the second {@link Iterable} in sequential order, and the first {@link Iterable} has the same number of elements as the filtered
     * elements from the second {@link Iterable}.
     *
     * @param first     The first {@link Iterable} object.
     * @param second    The second {@link Iterable} object.
     * @param filter    The {@link Predicate} that is used to determine which elements from the second {@link Iterable} are included.
     * @param predicate The {@link BiPredicate} that tests elements from the first {@link Iterable} against filtered elements from the second {@link Iterable} at the same
     *                  respective positions. When this returns {@code false}, no further tests will be done and a {@link Stream} of the filtered elements from the second
     *                  {@link Iterable} will be returned.
     * @param <T>       The element type for the first {@link Iterable} object.
     * @param <U>       The result element type as well as the element type for the second {@link Iterable} object.
     * @return An empty {@link Optional} if the first {@link Iterable} had the same number of filtered elements as the second {@link Iterable}, and the
     * {@code predicate} returned true for each adjacent element; Otherwise, the {@link Optional} value will contain a {@link Stream} of filtered elements from the second
     * {@link Iterable}.
     */
    public static <T, U> Optional<Stream<U>> ifAnyElementDiffers(Iterable<T> first, Iterable<U> second, Predicate<U> filter, BiPredicate<T, U> predicate) {
        if (null == first) {
            return Optional.ofNullable(toStream(second, filter));
        }
        if (null == second || first == second)
            return Optional.of(Stream.empty());
        if (first instanceof Collection && second instanceof Collection) {
            Collection<T> x = (Collection<T>) first;
            Collection<U> y = (Collection<U>) second;
            if (x.size() != y.size())
                return Optional.of(y.stream().filter(filter));
            if (x.isEmpty())
                return Optional.empty();
        }
        return ifAnyElementDiffers(filter, predicate, first.iterator(), second.iterator());
    }

    /**
     * Gets an {@link Optional} {@link Stream} of filtered elements from the second {@link Iterator} if adjacent elements from the first {@link Iterator} do not all match
     * the filtered elements from the second {@link Iterator} in sequential order, and the first {@link Iterator} has the same number of elements as the filtered
     * elements from the second {@link Iterator}.
     *
     * @param first     The first {@link Iterator} object.
     * @param second    The second {@link Iterator} object.
     * @param filter    The {@link Predicate} that is used to determine which elements from the second {@link Iterator} are included.
     * @param predicate The {@link BiPredicate} that tests elements from the first {@link Iterator} against filtered elements from the second {@link Iterator} at the same
     *                  respective position. When this returns {@code false}, no further tests will be done and a {@link Stream} of the filtered elements from the second
     *                  {@link Iterator} will be returned.
     * @param <T>       The element type for the first {@link Iterator} object.
     * @param <U>       The result element type as well as the element type for the second {@link Iterator} object.
     * @return An empty {@link Optional} if the first {@link Iterator} had the same number of filtered elements as the second {@link Iterator}, and the
     * {@code predicate} returned true for each adjacent element; Otherwise, the {@link Optional} value will contain a {@link Stream} of filtered elements from the second
     * {@link Iterator}.
     */
    public static <T, U> Optional<Stream<U>> ifAnyElementDiffers(Iterator<T> first, Iterator<U> second, Predicate<U> filter, BiPredicate<T, U> predicate) {
        if (null == first) {
            return Optional.ofNullable(toStream(second, filter));
        }
        if (null == second || first == second)
            return Optional.of(Stream.empty());

        return ifAnyElementDiffers(filter, predicate, first, second);
    }

    private static <T, U> Optional<Stream<U>> ifAnyElementDiffers(Predicate<U> filter, BiPredicate<T, U> predicate, @NonNull Iterator<T> a, Iterator<U> b) {
        Objects.requireNonNull(filter);
        Objects.requireNonNull(predicate);
        if (!b.hasNext()) {
            // 'b' has no element. Iterators are not equal if 'a' has an element;
            if (a.hasNext())
                return Optional.of(Stream.empty());
            return Optional.empty();
        }
        // Iterate through 'b' until we get a value that is not filtered out.
        U firstValue = b.next();
        while (!filter.test(firstValue)) {
            if (!b.hasNext()) {
                // All elements in 'b' were filtered out.
                if (a.hasNext()) {
                    // 'a' has at least one element, so iterators are not equal.
                    return Optional.of(Stream.empty());
                }
                // 'a' has no elements, so iterators are equal.
                return Optional.empty();
            }
            firstValue = b.next();
        }

        if (!b.hasNext()) {
            // 'b' has only one element.
            if (!(a.hasNext() && predicate.test(a.next(), firstValue) && !a.hasNext())) {
                // 'a' either has no elements, the first element is not a match, or 'a' has a second element.
                return Optional.of(Stream.of(firstValue));
            }
            // 'a' has only one element and it is a match, so iterators are equal.
            return Optional.empty();
        }

        U secondValue = b.next();
        if (!(a.hasNext() && predicate.test(a.next(), firstValue))) {
            // 'a' has only one element or its first element is not a match.
            if (b.hasNext()) {
                // 'b' has at least one more element, so we'll create a builder to get all remaining elements.
                Stream.Builder<U> builder = Stream.builder();
                builder.accept(firstValue);
                // Add second value only if it is not filtered out.
                if (filter.test(secondValue))
                    builder.accept(secondValue);
                // Add remaining elements that are not filtered out.
                b.forEachRemaining(t -> {
                    if (filter.test(t))
                        builder.accept(t);
                });
                return Optional.of(builder.build());
            }
            // Return a stream with first element and second element (if it is not filtered out).
            return Optional.of((filter.test(secondValue)) ? Stream.of(firstValue, secondValue) : Stream.of(firstValue));
        }
        // Iterate until we get second element that is not filtered out.
        while (!filter.test(secondValue)) {
            if (!b.hasNext()) {
                // 'b' contains only one element that was not filtered out.
                if (a.hasNext()) {
                    // 'a' contains at least 2 elements, so iterators are different.
                    return Optional.of(Stream.of(firstValue));
                }
                // 'a' contains only one element (which was already determined to be a match), so iterators are the same.
                return Optional.empty();
            }
            secondValue = b.next();
        }
        if (a.hasNext() && predicate.test(a.next(), secondValue)) {
            // 'a' contains at least 2 elements and they are a match. Create a builder to store elements while we test the remainder of the iterator values.
            Stream.Builder<U> builder = Stream.builder();
            builder.accept(firstValue);
            builder.accept(secondValue);
            while (b.hasNext()) {
                firstValue = b.next();
                if (filter.test(firstValue)) {
                    builder.accept(firstValue);
                    if (!(a.hasNext() && predicate.test(a.next(), firstValue))) {
                        // Found elements that were not a match, so iterators are not equal. Add remaining filtered elements from 'b'.
                        if (b.hasNext())
                            b.forEachRemaining(t -> {
                                if (filter.test(t))
                                    builder.accept(t);
                            });
                        return Optional.of(builder.build());
                    }
                }
            }
            if (a.hasNext())
                // 'a' has at least one more element than the filtered elements of 'b', so iterators are not equal.
                return Optional.of(builder.build());
            // All elements of 'a' match the filtered elements of 'b';
            return Optional.empty();
        }
        return Optional.of(Stream.of(firstValue, secondValue));
    }

    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="overloads:(first, second, mapper, predicate)">

    /**
     * Gets an {@link Optional} {@link Stream} of mapped elements from the second {@link Iterable} if adjacent elements from the first {@link Iterable} do not all match
     * the mapped elements from the second {@link Iterable} in sequential order, and both {@link Iterable Iterables} have the same number of elements.
     *
     * @param second    The second {@link Iterable} object.
     * @param mapper    The {@link Function} that converts elements from the second {@link Iterable} to result element values.
     * @param predicate The {@link BiPredicate} which tests elements from the first {@link Iterable} against mapped elements from the second
     *                  {@link Iterable} at the same respective position. When this returns {@code false}, no further tests will be done and a {@link Stream} of the mapped
     *                  elements from the second {@link Iterable} will be returned.
     * @param <T>       The element type for the first {@link Iterable} object.
     * @param <U>       The element type for the second {@link Iterable} object.
     * @param <R>       The result element type.
     * @return An empty {@link Optional} if both {@link Iterable Iterables} didn't have the same number of elements, or the {@code predicate} returned true for the elements
     * of the first {@link Iterable} and the mapped element from the second {@link Iterable} at their respective positions; Otherwise, the {@link Optional} value will contain
     * a {@link Stream} of mapped elements from the second {@link Iterable}.
     */
    public static <T, U, R> Optional<Stream<R>> ifAnyElementDiffers(Iterable<T> first, Iterable<U> second, Function<U, R> mapper, BiPredicate<T, R> predicate) {
        if (null == first) {
            return Optional.ofNullable(toStream(second, mapper));
        }
        if (null == second || first == second)
            return Optional.of(Stream.empty());
        if (first instanceof Collection && second instanceof Collection) {
            Collection<T> x = (Collection<T>) first;
            Collection<U> y = (Collection<U>) second;
            if (x.size() != y.size())
                return Optional.of(y.stream().map(mapper));
            if (x.isEmpty())
                return Optional.empty();
        }
        return ifAnyElementDiffers(mapper, predicate, first.iterator(), second.iterator());
    }

    /**
     * Gets an {@link Optional} {@link Stream} of mapped elements from the second {@link Iterator} if adjacent elements from the first {@link Iterator} do not all match
     * the mapped elements from the second {@link Iterator} in sequential order, and both {@link Iterator Iterables} have the same number of elements.
     *
     * @param second    The second {@link Iterator} object.
     * @param mapper    The {@link Function} that converts elements from the second {@link Iterator} to result element values.
     * @param predicate The {@link BiPredicate} which tests elements from the first {@link Iterator} against mapped elements from the second
     *                  {@link Iterator} at the same respective position. When this returns {@code false}, no further tests will be done and a {@link Stream} of the mapped
     *                  elements from the second {@link Iterator} will be returned.
     * @param <T>       The element type for the first {@link Iterator} object.
     * @param <U>       The element type for the second {@link Iterator} object.
     * @param <R>       The result element type.
     * @return An empty {@link Optional} if both {@link Iterator Iterables} didn't have the same number of elements, or the {@code predicate} returned true for the elements
     * of the first {@link Iterator} and the mapped element from the second {@link Iterator} at their respective positions; Otherwise, the {@link Optional} value will contain
     * a {@link Stream} of mapped elements from the second {@link Iterator}.
     */
    public static <T, U, R> Optional<Stream<R>> ifAnyElementDiffers(Iterator<T> first, Iterator<U> second, Function<U, R> mapper, BiPredicate<T, R> predicate) {
        if (null == first) {
            return Optional.ofNullable(toStream(second, mapper));
        }
        if (null == second || first == second)
            return Optional.of(Stream.empty());

        return ifAnyElementDiffers(mapper, predicate, first, second);
    }

    private static <T, U, R> Optional<Stream<R>> ifAnyElementDiffers(Function<U, R> mapper, BiPredicate<T, R> predicate, @NonNull Iterator<T> a, @NonNull Iterator<U> b) {
        Stream.Builder<R> builder = Stream.builder();
        Objects.requireNonNull(predicate);
        Objects.requireNonNull(mapper);
        while (b.hasNext()) {
            R value = mapper.apply(b.next());
            if (!a.hasNext() && predicate.test(a.next(), value)) {
                builder.accept(value);
                b.forEachRemaining(t -> builder.accept(mapper.apply(t)));
                return Optional.of(builder.build());
            }
            builder.accept(value);
        }
        if (a.hasNext()) {
            return Optional.of(builder.build());
        }
        return Optional.empty();
    }

    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="overloads:(first, second, mapper, filter, predicate)">

    /**
     * Gets an {@link Optional} {@link Stream} of mapped and filtered elements from the second {@link Iterable} if adjacent elements from the first {@link Iterable} do not all
     * match the mapped and filtered elements from the second {@link Iterable} in sequential order, and the first {@link Iterable} has the same number of elements as the
     * filtered elements from the second {@link Iterable}.
     *
     * @param first     The first {@link Iterable} object.
     * @param second    The second {@link Iterable} object.
     * @param mapper    The {@link Function} that converts elements from the second {@link Iterable} to result element values.
     * @param filter    The {@link Predicate} that is used to determine which mapped elements from the second {@link Iterable} are included.
     * @param predicate The {@link BiPredicate} that tests elements from the first {@link Iterable} against mapped and filtered elements from the second {@link Iterable} at
     *                  the same respective positions. When this returns {@code false}, no further tests will be done and a {@link Stream} of the mapped and filtered elements
     *                  from the second {@link Iterable} will be returned.
     * @param <T>       The element type for the first {@link Iterable} object.
     * @param <U>       The element type for the second {@link Iterable} object.
     * @param <R>       The result element type.
     * @return An empty {@link Optional} if the first {@link Iterable} had the same number of filtered elements as the second {@link Iterable}, and the
     * {@code predicate} returned true for each adjacent mapped and filtered element; Otherwise, the {@link Optional} value will contain a {@link Stream} of mapped and
     * filtered elements from the second {@link Iterable}.
     */
    public static <T, U, R> Optional<Stream<R>> ifAnyElementDiffers(Iterable<T> first, Iterable<U> second, Function<U, R> mapper, Predicate<R> filter, BiPredicate<T, R> predicate) {
        if (null == first) {
            return Optional.ofNullable(toStream(second, mapper, filter));
        }
        if (null == second || first == second)
            return Optional.of(Stream.empty());
        if (first instanceof Collection && second instanceof Collection) {
            Collection<T> x = (Collection<T>) first;
            Collection<U> y = (Collection<U>) second;
            if (x.size() != y.size())
                return Optional.of(y.stream().map(mapper).filter(filter));
            if (x.isEmpty())
                return Optional.empty();
        }
        return ifAnyElementDiffers(mapper, filter, predicate, first.iterator(), second.iterator());
    }

    public static <T, U, R> Optional<Stream<R>> ifAnyElementDiffers(Iterator<T> first, Iterator<U> second, Function<U, R> mapper, Predicate<R> filter, BiPredicate<T, R> predicate) {
        if (null == first) {
            if (null == second)
                return Optional.empty();
            return Optional.ofNullable(toStream(second, mapper, filter));
        }
        if (null == second || first == second)
            return Optional.of(Stream.empty());

        return ifAnyElementDiffers(mapper, filter, predicate, first, second);
    }

    private static <T, U, R> Optional<Stream<R>> ifAnyElementDiffers(Function<U, R> mapper, Predicate<R> filter, BiPredicate<T, R> predicate, @NonNull Iterator<T> a, @NonNull Iterator<U> b) {
        Objects.requireNonNull(mapper);
        Objects.requireNonNull(filter);
        Objects.requireNonNull(predicate);
        if (!b.hasNext()) {
            // 'b' has no element. Iterators are not equal if 'a' has an element;
            if (a.hasNext())
                return Optional.of(Stream.empty());
            return Optional.empty();
        }
        // Iterate through 'b' until we get a value that is not filtered out.
        R firstValue = mapper.apply(b.next());
        while (!filter.test(firstValue)) {
            if (!b.hasNext()) {
                // All elements in 'b' were filtered out.
                if (a.hasNext()) {
                    // 'a' has at least one element, so iterators are not equal.
                    return Optional.of(Stream.empty());
                }
                // 'a' has no elements, so iterators are equal.
                return Optional.empty();
            }
            firstValue = mapper.apply(b.next());
        }

        if (!b.hasNext()) {
            // 'b' has only one element.
            if (!(a.hasNext() && predicate.test(a.next(), firstValue) && !a.hasNext())) {
                // 'a' either has no elements, the first element is not a match, or 'a' has a second element.
                return Optional.of(Stream.of(firstValue));
            }
            // 'a' has only one element and it is a match, so iterators are equal.
            return Optional.empty();
        }

        R secondValue = mapper.apply(b.next());
        if (!(a.hasNext() && predicate.test(a.next(), firstValue))) {
            // 'a' has only one element or its first element is not a match.
            if (b.hasNext()) {
                // 'b' has at least one more element, so we'll create a builder to get all remaining elements.
                Stream.Builder<R> builder = Stream.builder();
                builder.accept(firstValue);
                // Add second value only if it is not filtered out.
                if (filter.test(secondValue))
                    builder.accept(secondValue);
                // Add remaining elements that are not filtered out.
                b.forEachRemaining(t -> {
                    R v = mapper.apply(t);
                    if (filter.test(v))
                        builder.accept(v);
                });
                return Optional.of(builder.build());
            }
            // Return a stream with first element and second element (if it is not filtered out).
            return Optional.of((filter.test(secondValue)) ? Stream.of(firstValue, secondValue) : Stream.of(firstValue));
        }
        // Iterate until we get second element that is not filtered out.
        while (!filter.test(secondValue)) {
            if (!b.hasNext()) {
                // 'b' contains only one element that was not filtered out.
                if (a.hasNext()) {
                    // 'a' contains at least 2 elements, so iterators are different.
                    return Optional.of(Stream.of(firstValue));
                }
                // 'a' contains only one element (which was already determined to be a match), so iterators are the same.
                return Optional.empty();
            }
            secondValue = mapper.apply(b.next());
        }
        if (a.hasNext() && predicate.test(a.next(), secondValue)) {
            // 'a' contains at least 2 elements and they are a match. Create a builder to store elements while we test the remainder of the iterator values.
            Stream.Builder<R> builder = Stream.builder();
            builder.accept(firstValue);
            builder.accept(secondValue);
            while (b.hasNext()) {
                firstValue = mapper.apply(b.next());
                if (filter.test(firstValue)) {
                    builder.accept(firstValue);
                    if (!(a.hasNext() && predicate.test(a.next(), firstValue))) {
                        // Found elements that were not a match, so iterators are not equal. Add remaining filtered elements from 'b'.
                        if (b.hasNext())
                            b.forEachRemaining(t -> {
                                R v = mapper.apply(t);
                                if (filter.test(v))
                                    builder.accept(v);
                            });
                        return Optional.of(builder.build());
                    }
                }
            }
            if (a.hasNext())
                // 'a' has at least one more element than the filtered elements of 'b', so iterators are not equal.
                return Optional.of(builder.build());
            // All elements of 'a' match the filtered elements of 'b';
            return Optional.empty();
        }
        return Optional.of(Stream.of(firstValue, secondValue));
    }

    //</editor-fold>

    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="allElementsMatch methods">

    /**
     * Determines whether two {@link Iterable} objects have the same number of elements and each element matches in respective order.
     *
     * @param first     The first {@link Iterable} object.
     * @param second    The second {@link Iterable} object.
     * @param predicate The {@link BiPredicate} that determines whether an element from each {@link Iterable} matches.
     * @param <T>       The element type for the first {@link Iterable} object.
     * @param <U>       The element type for the second {@link Iterable} object.
     * @return {@code true} if both {@link Iterable} objects have the same number of elements and each sequential element matches in respective order; otherwise, {@code false}.
     */
    public static <T, U> boolean allElementsMatch(Iterable<T> first, Iterable<U> second, BiPredicate<T, U> predicate) {
        if (null == first)
            return null == second;
        if (null == second)
            return false;
        if (first == second)
            return true;
        if (first instanceof Collection && second instanceof Collection) {
            Collection<T> x = (Collection<T>) first;
            Collection<U> y = (Collection<U>) second;
            if (x.size() != y.size())
                return false;
            if (x.isEmpty())
                return true;
        }
        return allElementsMatch(predicate, first.iterator(), second.iterator());
    }

    /**
     * Determines whether two {@link Stream Streams} have the same number of elements and each element matches in respective order.
     *
     * @param first     The first {@link Stream}.
     * @param second    The second {@link Stream}.
     * @param predicate The {@link BiPredicate} that determines whether an element from each {@link Stream} matches.
     * @param <T>       The element type for the first {@link Stream}.
     * @param <U>       The element type for the second {@link Stream}.
     * @return {@code true} if both {@link Stream Streams} have the same number of elements and each sequential element matches in respective order; otherwise, {@code false}.
     */
    public static <T, U> boolean allElementsMatch(Stream<T> first, Stream<U> second, BiPredicate<T, U> predicate) {
        if (null == first)
            return null == second;
        if (null == second)
            return false;
        if (first == second)
            return true;
        return allElementsMatch(predicate, first.iterator(), second.iterator());
    }

    /**
     * Determines whether two {@link Iterator Iterators} have the same number of elements and each element matches in respective order.
     *
     * @param first     The first {@link Iterator}.
     * @param second    The second {@link Iterator}.
     * @param predicate The {@link BiPredicate} that determines whether an element from each {@link Iterator} matches.
     * @param <T>       The element type for the first {@link Iterator}.
     * @param <U>       The element type for the second {@link Iterator}.
     * @return {@code true} if both {@link Iterator Iterators} have the same number of elements and each sequential element matches in respective order; otherwise, {@code false}.
     */
    public static <T, U> boolean allElementsMatch(Iterator<T> first, Iterator<U> second, BiPredicate<T, U> predicate) {
        if (null == first)
            return null == second;
        if (null == second)
            return false;
        if (first == second)
            return true;
        return allElementsMatch(predicate, first, second);
    }

    private static <T, U> boolean allElementsMatch(BiPredicate<T, U> predicate, @NonNull Iterator<T> a, @NonNull Iterator<U> b) {
        Objects.requireNonNull(predicate);
        while (a.hasNext()) {
            if (!(b.hasNext() && predicate.test(a.next(), b.next())))
                return false;
        }
        return !b.hasNext();
    }

    //</editor-fold>
}
