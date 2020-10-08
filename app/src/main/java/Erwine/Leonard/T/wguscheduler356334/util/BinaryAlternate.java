package Erwine.Leonard.T.wguscheduler356334.util;

import androidx.annotation.NonNull;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

@SuppressWarnings("unchecked")
public abstract class BinaryAlternate<T, U> {

    public static <T, U> BinaryAlternate<T, U> ofPrimary(@NonNull T value) {
        return new BinaryAlternate<T, U>() {
            @Override
            public T getPrimary() {
                return value;
            }

            @Override
            public U getSecondary() {
                throw new NoSuchElementException();
            }

            @Override
            public boolean isPrimary() {
                return true;
            }

            @Override
            public void ifPrimary(@NonNull Consumer<T> consumer) {
                consumer.accept(value);
            }

            @Override
            public void ifSecondary(@NonNull Consumer<U> consumer) {
            }

            @Override
            public void switchPresence(@NonNull Consumer<T> ifPrimary, @NonNull Consumer<U> ifSecondary) {
                ifPrimary.accept(value);
            }

            @Override
            public Optional<T> extractPrimary() {
                return Optional.of(value);
            }

            @Override
            public Optional<U> extractSecondary() {
                return Optional.empty();
            }

            @Override
            public <R> BinaryAlternate<R, U> mapPrimary(@NonNull Function<T, R> mapper) {
                return ofPrimary(mapper.apply(value));
            }

            @Override
            public <R> BinaryAlternate<T, R> mapSecondary(@NonNull Function<U, R> mapper) {
                return (BinaryAlternate<T, R>) this;
            }

            @Override
            public <R, S> BinaryAlternate<R, S> map(@NonNull Function<T, R> primaryMapper, @NonNull Function<U, S> secondaryMapper) {
                return ofPrimary(primaryMapper.apply(value));
            }

            @Override
            public <R> R flatMap(@NonNull Function<T, R> primaryMapper, @NonNull Function<U, R> secondaryMapper) {
                return primaryMapper.apply(value);
            }
        };
    }

    public static <T, U> BinaryAlternate<T, U> ofSecondary(@NonNull U value) {
        return new BinaryAlternate<T, U>() {
            @Override
            public T getPrimary() {
                throw new NoSuchElementException();
            }

            @Override
            public U getSecondary() {
                return value;
            }

            @Override
            public boolean isPrimary() {
                return false;
            }

            @Override
            public void ifPrimary(@NonNull Consumer<T> consumer) {

            }

            @Override
            public void ifSecondary(@NonNull Consumer<U> consumer) {
                consumer.accept(value);
            }

            @Override
            public void switchPresence(@NonNull Consumer<T> ifPrimary, @NonNull Consumer<U> ifSecondary) {
                ifSecondary.accept(value);
            }

            @Override
            public Optional<T> extractPrimary() {
                return Optional.empty();
            }

            @Override
            public Optional<U> extractSecondary() {
                return Optional.of(value);
            }

            @Override
            public <R> BinaryAlternate<R, U> mapPrimary(@NonNull Function<T, R> mapper) {
                return (BinaryAlternate<R, U>) this;
            }

            @Override
            public <R> BinaryAlternate<T, R> mapSecondary(@NonNull Function<U, R> mapper) {
                return ofSecondary(mapper.apply(value));
            }

            @Override
            public <R, S> BinaryAlternate<R, S> map(@NonNull Function<T, R> primaryMapper, @NonNull Function<U, S> secondaryMapper) {
                return ofSecondary(secondaryMapper.apply(value));
            }

            @Override
            public <R> R flatMap(@NonNull Function<T, R> primaryMapper, @NonNull Function<U, R> secondaryMapper) {
                return secondaryMapper.apply(value);
            }
        };
    }

    public abstract T getPrimary();

    public abstract U getSecondary();

    public abstract boolean isPrimary();

    public abstract void ifPrimary(@NonNull Consumer<T> consumer);

    public abstract void ifSecondary(@NonNull Consumer<U> consumer);

    public abstract void switchPresence(@NonNull Consumer<T> ifPrimary, @NonNull Consumer<U> ifSecondary);

    public abstract Optional<T> extractPrimary();

    public abstract Optional<U> extractSecondary();

    public abstract <R> BinaryAlternate<R, U> mapPrimary(@NonNull Function<T, R> mapper);

    public abstract <R> BinaryAlternate<T, R> mapSecondary(@NonNull Function<U, R> mapper);

    public abstract <R, S> BinaryAlternate<R, S> map(@NonNull Function<T, R> primaryMapper, @NonNull Function<U, S> secondaryMapper);

    public abstract <R> R flatMap(@NonNull Function<T, R> primaryMapper, @NonNull Function<U, R> secondaryMapper);

}
