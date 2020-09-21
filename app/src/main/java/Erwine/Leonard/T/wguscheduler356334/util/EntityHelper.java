package Erwine.Leonard.T.wguscheduler356334.util;

import java.util.Collection;
import java.util.Optional;

import Erwine.Leonard.T.wguscheduler356334.entity.AbstractEntity;

public class EntityHelper {
    public static <T extends AbstractEntity<T>> Optional<T> findById(long id, Collection<T> source) {
        return source.stream().filter(t -> {
            Long i = t.getId();
            return null != i && id == i;
        }).findFirst();
    }

    private EntityHelper() {
    }
}
