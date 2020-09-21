package Erwine.Leonard.T.wguscheduler356334.util;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Collection;
import java.util.Optional;

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

    private EntityHelper() {
    }
}
