package Erwine.Leonard.T.wguscheduler356334.util.validation;

import android.content.res.Resources;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import java.util.function.Function;

import Erwine.Leonard.T.wguscheduler356334.R;

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

    static ResourceMessageFactory ofWarning(@NonNull Throwable throwable) {
        return new ResourceMessageFactory() {
            @Override
            public boolean isWarning() {
                return true;
            }

            @Override
            public String apply(Resources resources) {
                String message = throwable.getLocalizedMessage();
                if ((null == message || message.trim().isEmpty()) && (null == (message = throwable.getMessage()) || message.trim().isEmpty())) {
                    message = throwable.getClass().getSimpleName();
                }
                return resources.getString(R.string.format_error, message);
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

    static ResourceMessageFactory ofError(@NonNull Throwable throwable) {
        return r -> {
            String message = throwable.getLocalizedMessage();
            if ((null == message || message.trim().isEmpty()) && (null == (message = throwable.getMessage()) || message.trim().isEmpty())) {
                message = throwable.getClass().getSimpleName();
            }
            return r.getString(R.string.format_error, message);
        };
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
