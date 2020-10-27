package Erwine.Leonard.T.wguscheduler356334.util;

import androidx.annotation.NonNull;
import androidx.core.util.Supplier;

public interface NonNullSupplier<T> extends Supplier<T> {
    @Override
    @NonNull
    T get();
}
