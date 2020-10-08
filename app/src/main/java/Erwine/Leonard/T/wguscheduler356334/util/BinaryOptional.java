package Erwine.Leonard.T.wguscheduler356334.util;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.NoSuchElementException;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import io.reactivex.functions.Action;

@SuppressWarnings("unchecked")
public abstract class BinaryOptional<T, U> {

    private static final BinaryOptional<?, ?> EMPTY = new BinaryOptional<Object, Object>() {
        @Override
        public Object getPrimary() {
            throw new NoSuchElementException();
        }

        @Override
        public Object getSecondary() {
            throw new NoSuchElementException();
        }

        @Override
        public boolean isPrimary() {
            return false;
        }

        @Override
        public boolean isSecondary() {
            return false;
        }

        @Override
        public void ifPrimary(@NonNull Consumer<Object> consumer) {
        }

        @Override
        public void ifSecondary(@NonNull Consumer<Object> consumer) {
        }

        @Override
        public void ifPresent(@NonNull Consumer<Object> ifPrimary, @NonNull Consumer<Object> ifSecondary) {
        }

        @Override
        public void switchPresence(@NonNull Consumer<Object> ifPrimary, @NonNull Consumer<Object> ifSecondary, @NonNull Action ifNotPresent) {

        }

        @Override
        public <R> BinaryOptional<R, Object> mapPrimary(@NonNull Function<Object, R> mapper) {
            return (BinaryOptional<R, Object>) EMPTY;
        }

        @Override
        public <R> BinaryOptional<Object, R> mapSecondary(@NonNull Function<Object, R> mapper) {
            return (BinaryOptional<Object, R>) EMPTY;
        }

        @Override
        public <R, S> BinaryOptional<R, S> map(@NonNull Function<Object, R> primaryMapper, @NonNull Function<Object, S> secondaryMapper) {
            return (BinaryOptional<R, S>) EMPTY;
        }

        @Override
        public BinaryOptional<Object, Object> orElsePrimary(@NonNull Object value) {
            return ofPrimary(value);
        }

        @Override
        public BinaryOptional<Object, Object> orElseSecondary(@NonNull Object value) {
            return BinaryOptional.ofSecondary(value);
        }

        @Override
        public BinaryOptional<Object, Object> orElseGetPrimary(@NonNull Supplier<Object> value) {
            return ofPrimary(value.get());
        }

        @Override
        public BinaryOptional<Object, Object> orElseGetSecondary(@NonNull Supplier<Object> value) {
            return ofSecondary(value.get());
        }

        @Override
        public <R> R flatMap(@NonNull Function<Object, R> primaryMapper, @NonNull Function<Object, R> secondaryMapper, @NonNull Supplier<R> ifNotPresent) {
            return ifNotPresent.get();
        }

        @Override
        public boolean isPresent() {
            return false;
        }
    };

    public static <T, U> BinaryOptional<T, U> empty() {
        return (BinaryOptional<T, U>) EMPTY;
    }

    public static <T, U> BinaryOptional<T, U> ofPrimary(@NonNull T value) {
        return new BinaryOptional<T, U>() {
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
            public boolean isSecondary() {
                return false;
            }

            @Override
            public void ifPrimary(@NonNull Consumer<T> consumer) {
                consumer.accept(value);
            }

            @Override
            public void ifSecondary(@NonNull Consumer<U> consumer) {
            }

            @Override
            public void ifPresent(@NonNull Consumer<T> ifPrimary, @NonNull Consumer<U> ifSecondary) {
                ifPrimary.accept(value);
            }

            @Override
            public void switchPresence(@NonNull Consumer<T> ifPrimary, @NonNull Consumer<U> ifSecondary, @NonNull Action ifNotPresent) {
                ifPrimary.accept(value);
            }

            @Override
            public <R> BinaryOptional<R, U> mapPrimary(@NonNull Function<T, R> mapper) {
                return ofPrimary(mapper.apply(value));
            }

            @Override
            public <R> BinaryOptional<T, R> mapSecondary(@NonNull Function<U, R> mapper) {
                return (BinaryOptional<T, R>) this;
            }

            @Override
            public <R, S> BinaryOptional<R, S> map(@NonNull Function<T, R> primaryMapper, @NonNull Function<U, S> secondaryMapper) {
                return ofPrimary(primaryMapper.apply(value));
            }

            @Override
            public <R> R flatMap(@NonNull Function<T, R> primaryMapper, @NonNull Function<U, R> secondaryMapper, @NonNull Supplier<R> ifNotPresent) {
                return primaryMapper.apply(value);
            }
        };
    }

    public static <T, U> BinaryOptional<T, U> ofPrimaryNullable(@Nullable T value) {
        if (null == value) {
            return empty();
        }
        return ofPrimary(value);
    }

    public static <T, U> BinaryOptional<T, U> ofSecondary(@NonNull U value) {
        return new BinaryOptional<T, U>() {
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
            public boolean isSecondary() {
                return true;
            }

            @Override
            public void ifPrimary(@NonNull Consumer<T> consumer) {
            }

            @Override
            public void ifSecondary(@NonNull Consumer<U> consumer) {
                consumer.accept(value);
            }

            @Override
            public void ifPresent(@NonNull Consumer<T> ifPrimary, @NonNull Consumer<U> ifSecondary) {
                ifSecondary.accept(value);
            }

            @Override
            public void switchPresence(@NonNull Consumer<T> ifPrimary, @NonNull Consumer<U> ifSecondary, @NonNull Action ifNotPresent) {
                ifSecondary.accept(value);
            }

            @Override
            public <R> BinaryOptional<R, U> mapPrimary(@NonNull Function<T, R> mapper) {
                return (BinaryOptional<R, U>) this;
            }

            @Override
            public <R> BinaryOptional<T, R> mapSecondary(@NonNull Function<U, R> mapper) {
                return ofSecondary(mapper.apply(value));
            }

            @Override
            public <R, S> BinaryOptional<R, S> map(@NonNull Function<T, R> primaryMapper, @NonNull Function<U, S> secondaryMapper) {
                return ofSecondary(secondaryMapper.apply(value));
            }

            @Override
            public <R> R flatMap(@NonNull Function<T, R> primaryMapper, @NonNull Function<U, R> secondaryMapper, @NonNull Supplier<R> ifNotPresent) {
                return secondaryMapper.apply(value);
            }
        };
    }

    public static <T, U> BinaryOptional<T, U> ofSecondaryNullable(@Nullable U value) {
        if (null == value) {
            return empty();
        }
        return ofSecondary(value);
    }

    private BinaryOptional() {
    }

    public abstract T getPrimary();

    public abstract U getSecondary();

    public abstract boolean isPrimary();

    public abstract boolean isSecondary();

    public boolean isPresent() {
        return true;
    }

    public abstract void ifPrimary(@NonNull Consumer<T> consumer);

    public abstract void ifSecondary(@NonNull Consumer<U> consumer);

    public abstract void ifPresent(@NonNull Consumer<T> ifPrimary, @NonNull Consumer<U> ifSecondary);

    public abstract void switchPresence(@NonNull Consumer<T> ifPrimary, @NonNull Consumer<U> ifSecondary, @NonNull Action ifNotPresent);

    public abstract <R> BinaryOptional<R, U> mapPrimary(@NonNull Function<T, R> mapper);

    public abstract <R> BinaryOptional<T, R> mapSecondary(@NonNull Function<U, R> mapper);

    public abstract <R, S> BinaryOptional<R, S> map(@NonNull Function<T, R> primaryMapper, @NonNull Function<U, S> secondaryMapper);

    public BinaryOptional<T, U> orElsePrimary(@NonNull T value) {
        return this;
    }

    public BinaryOptional<T, U> orElseSecondary(@NonNull U value) {
        return this;
    }

    public BinaryOptional<T, U> orElseGetPrimary(@NonNull Supplier<T> value) {
        return this;
    }

    public BinaryOptional<T, U> orElseGetSecondary(@NonNull Supplier<U> value) {
        return this;
    }

    public abstract <R> R flatMap(@NonNull Function<T, R> primaryMapper, @NonNull Function<U, R> secondaryMapper, @NonNull Supplier<R> ifNotPresent);
}
