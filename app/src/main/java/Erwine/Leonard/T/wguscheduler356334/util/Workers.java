package Erwine.Leonard.T.wguscheduler356334.util;

import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import io.reactivex.Scheduler;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Function;
import io.reactivex.functions.Function3;
import io.reactivex.functions.Function4;
import io.reactivex.functions.Function5;
import io.reactivex.functions.Function6;
import io.reactivex.functions.Function7;
import io.reactivex.schedulers.Schedulers;

public class Workers {
    private static final Executor executor = Executors.newScheduledThreadPool(3);
    private static final Scheduler scheduler = Schedulers.from(executor);

    public static Executor getExecutor() {
        return executor;
    }

    public static Scheduler getScheduler() {
        return scheduler;
    }

    private Workers() {
    }

    public static <T, R> Function<T, R> asCached(@NonNull Function<T, R> source) {
        return (source instanceof CachedResultFunction) ? source : new CachedResultFunction<>(source);
    }

    public static class CachedResultFunction<T, R> implements Function<T, R> {
        private final Function<T, R> source;
        private T lastArg;
        private R lastResult;

        public CachedResultFunction(@NonNull Function<T, R> source) {
            this.source = source;
        }

        @Override
        public R apply(@NonNull T t) throws Exception {
            if (!Objects.equals(t, lastArg)) {
                lastArg = t;
                lastResult = source.apply(lastArg);
            }
            return lastResult;
        }
    }

    public static <T1, T2, R> BiFunction<T1, T2, R> asCached(@NonNull BiFunction<T1, T2, R> source) {
        return (source instanceof CachedResultBiFunction) ? source : new CachedResultBiFunction<>(source);
    }

    public static class CachedResultBiFunction<T1, T2, R> implements BiFunction<T1, T2, R> {
        private final BiFunction<T1, T2, R> source;
        private T1 lastArg1;
        private T2 lastArg2;
        private R lastResult;

        public CachedResultBiFunction(@NonNull BiFunction<T1, T2, R> source) {
            this.source = source;
        }

        @NonNull
        @Override
        public R apply(@NonNull T1 t1, @NonNull T2 t2) throws Exception {
            if (!(Objects.equals(t1, lastArg1) && Objects.equals(t2, lastArg2))) {
                lastArg1 = t1;
                lastArg2 = t2;
                lastResult = source.apply(lastArg1, lastArg2);
            }
            return lastResult;
        }
    }

    public static <T1, T2, T3, R> Function3<T1, T2, T3, R> asCached(@NonNull Function3<T1, T2, T3, R> source) {
        return (source instanceof CachedResultFunction3) ? source : new CachedResultFunction3<>(source);
    }

    public static class CachedResultFunction3<T1, T2, T3, R> implements Function3<T1, T2, T3, R> {
        private final Function3<T1, T2, T3, R> source;
        private T1 lastArg1;
        private T2 lastArg2;
        private T3 lastArg3;
        private R lastResult;

        public CachedResultFunction3(@NonNull Function3<T1, T2, T3, R> source) {
            this.source = source;
        }

        @NonNull
        @Override
        public R apply(@NonNull T1 t1, @NonNull T2 t2, @NonNull T3 t3) throws Exception {
            if (!(Objects.equals(t1, lastArg1) && Objects.equals(t2, lastArg2) && Objects.equals(t3, lastArg3))) {
                lastArg1 = t1;
                lastArg2 = t2;
                lastArg3 = t3;
                lastResult = source.apply(lastArg1, lastArg2, lastArg3);
            }
            return lastResult;
        }
    }

    public static <T1, T2, T3, T4, R> Function4<T1, T2, T3, T4, R> asCached(@NonNull Function4<T1, T2, T3, T4, R> source) {
        return (source instanceof CachedResultFunction4) ? source : new CachedResultFunction4<>(source);
    }

    public static class CachedResultFunction4<T1, T2, T3, T4, R> implements Function4<T1, T2, T3, T4, R> {
        private final Function4<T1, T2, T3, T4, R> source;
        private T1 lastArg1;
        private T2 lastArg2;
        private T3 lastArg3;
        private T4 lastArg4;
        private R lastResult;

        public CachedResultFunction4(@NonNull Function4<T1, T2, T3, T4, R> source) {
            this.source = source;
        }

        @NonNull
        @Override
        public R apply(@NonNull T1 t1, @NonNull T2 t2, @NonNull T3 t3, @NonNull T4 t4) throws Exception {
            if (!(Objects.equals(t1, lastArg1) && Objects.equals(t2, lastArg2) && Objects.equals(t3, lastArg3) && Objects.equals(t4, lastArg4))) {
                lastArg1 = t1;
                lastArg2 = t2;
                lastArg3 = t3;
                lastArg4 = t4;
                lastResult = source.apply(lastArg1, lastArg2, lastArg3, lastArg4);
            }
            return lastResult;
        }
    }

    public static <T1, T2, T3, T4, T5, R> Function5<T1, T2, T3, T4, T5, R> asCached(@NonNull Function5<T1, T2, T3, T4, T5, R> source) {
        return (source instanceof CachedResultFunction5) ? source : new CachedResultFunction5<>(source);
    }

    public static class CachedResultFunction5<T1, T2, T3, T4, T5, R> implements Function5<T1, T2, T3, T4, T5, R> {
        private final Function5<T1, T2, T3, T4, T5, R> source;
        private T1 lastArg1;
        private T2 lastArg2;
        private T3 lastArg3;
        private T4 lastArg4;
        private T5 lastArg5;
        private R lastResult;

        public CachedResultFunction5(@NonNull Function5<T1, T2, T3, T4, T5, R> source) {
            this.source = source;
        }

        @NonNull
        @Override
        public R apply(@NonNull T1 t1, @NonNull T2 t2, @NonNull T3 t3, @NonNull T4 t4, @NonNull T5 t5) throws Exception {
            if (!(Objects.equals(t1, lastArg1) && Objects.equals(t2, lastArg2) && Objects.equals(t3, lastArg3) && Objects.equals(t4, lastArg4) && Objects.equals(t5, lastArg5))) {
                lastArg1 = t1;
                lastArg2 = t2;
                lastArg3 = t3;
                lastArg4 = t4;
                lastArg5 = t5;
                lastResult = source.apply(lastArg1, lastArg2, lastArg3, lastArg4, lastArg5);
            }
            return lastResult;
        }
    }

    public static <T1, T2, T3, T4, T5, T6, R> Function6<T1, T2, T3, T4, T5, T6, R> asCached(@NonNull Function6<T1, T2, T3, T4, T5, T6, R> source) {
        return (source instanceof CachedResultFunction6) ? source : new CachedResultFunction6<>(source);
    }

    public static class CachedResultFunction6<T1, T2, T3, T4, T5, T6, R> implements Function6<T1, T2, T3, T4, T5, T6, R> {
        private final Function6<T1, T2, T3, T4, T5, T6, R> source;
        private T1 lastArg1;
        private T2 lastArg2;
        private T3 lastArg3;
        private T4 lastArg4;
        private T5 lastArg5;
        private T6 lastArg6;
        private R lastResult;

        public CachedResultFunction6(@NonNull Function6<T1, T2, T3, T4, T5, T6, R> source) {
            this.source = source;
        }

        @NonNull
        @Override
        public R apply(@NonNull T1 t1, @NonNull T2 t2, @NonNull T3 t3, @NonNull T4 t4, @NonNull T5 t5, @NonNull T6 t6) throws Exception {
            if (!(Objects.equals(t1, lastArg1) && Objects.equals(t2, lastArg2) && Objects.equals(t3, lastArg3) && Objects.equals(t4, lastArg4) && Objects.equals(t5, lastArg5) && Objects.equals(t6, lastArg6))) {
                lastArg1 = t1;
                lastArg2 = t2;
                lastArg3 = t3;
                lastArg4 = t4;
                lastArg5 = t5;
                lastArg6 = t6;
                lastResult = source.apply(lastArg1, lastArg2, lastArg3, lastArg4, lastArg5, lastArg6);
            }
            return lastResult;
        }
    }

    public static <T1, T2, T3, T4, T5, T6, T7, R> Function7<T1, T2, T3, T4, T5, T6, T7, R> asCached(@NonNull Function7<T1, T2, T3, T4, T5, T6, T7, R> source) {
        return (source instanceof CachedResultFunction7) ? source : new CachedResultFunction7<>(source);
    }

    public static class CachedResultFunction7<T1, T2, T3, T4, T5, T6, T7, R> implements Function7<T1, T2, T3, T4, T5, T6, T7, R> {
        private final Function7<T1, T2, T3, T4, T5, T6, T7, R> source;
        private T1 lastArg1;
        private T2 lastArg2;
        private T3 lastArg3;
        private T4 lastArg4;
        private T5 lastArg5;
        private T6 lastArg6;
        private T7 lastArg7;
        private R lastResult;

        public CachedResultFunction7(@NonNull Function7<T1, T2, T3, T4, T5, T6, T7, R> source) {
            this.source = source;
        }

        @NonNull
        @Override
        public R apply(@NonNull T1 t1, @NonNull T2 t2, @NonNull T3 t3, @NonNull T4 t4, @NonNull T5 t5, @NonNull T6 t6, @NonNull T7 t7) throws Exception {
            if (!(Objects.equals(t1, lastArg1) && Objects.equals(t2, lastArg2) && Objects.equals(t3, lastArg3) && Objects.equals(t4, lastArg4) && Objects.equals(t5, lastArg5) && Objects.equals(t6, lastArg6) && Objects.equals(t7, lastArg7))) {
                lastArg1 = t1;
                lastArg2 = t2;
                lastArg3 = t3;
                lastArg4 = t4;
                lastArg5 = t5;
                lastArg6 = t6;
                lastArg7 = t7;
                lastResult = source.apply(lastArg1, lastArg2, lastArg3, lastArg4, lastArg5, lastArg6, lastArg7);
            }
            return lastResult;
        }
    }

}
