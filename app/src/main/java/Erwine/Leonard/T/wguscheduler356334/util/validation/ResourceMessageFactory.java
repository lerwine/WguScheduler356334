package Erwine.Leonard.T.wguscheduler356334.util.validation;

import android.content.res.Resources;

import androidx.annotation.StringRes;

import java.util.function.Function;

public interface ResourceMessageFactory extends Function<Resources, String> {
    static ResourceMessageFactory ofWarning(int id) {
        return new ResourceMessageFactory() {
            @Override
            public boolean isWarning() {
                return true;
            }

            @Override
            public String apply(Resources resources) {
                return resources.getString(id);
            }
        };
    }

    static ResourceMessageFactory ofWarning(Function<Resources, String> factory) {
        return new ResourceMessageFactory() {
            @Override
            public boolean isWarning() {
                return true;
            }

            @Override
            public String apply(Resources resources) {
                return factory.apply(resources);
            }
        };
    }

    static ResourceMessageFactory ofWarning(@StringRes int id, Object... formatArgs) {
        return new ResourceMessageFactory() {
            @Override
            public boolean isWarning() {
                return true;
            }

            @Override
            public String apply(Resources resources) {
                return resources.getString(id, formatArgs);
            }
        };
    }

    static ResourceMessageFactory ofError(@StringRes int id) {
        return r -> r.getString(id);
    }

    static ResourceMessageFactory ofError(Function<Resources, String> factory) {
        return factory::apply;
    }

    static ResourceMessageFactory ofError(@StringRes int id, Object... formatArgs) {
        return r -> r.getString(id, formatArgs);
    }

    default boolean isWarning() {
        return false;
    }
}
