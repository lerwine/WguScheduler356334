package Erwine.Leonard.T.wguscheduler356334.util.validation;

import android.content.res.Resources;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import java.util.function.Function;

import Erwine.Leonard.T.wguscheduler356334.R;

public interface ResourceMessageFactory extends Function<Resources, String> {
    @NonNull
    static ResourceMessageFactory of(@NonNull MessageLevel level, int id) {
        return new ResourceMessageFactory() {
            @Override
            public MessageLevel getLevel() {
                return level;
            }

            @Override
            public String apply(Resources resources) {
                return resources.getString(id);
            }
        };
    }

    @NonNull
    static ResourceMessageFactory of(@NonNull MessageLevel level, @NonNull Throwable throwable) {
        return new ResourceMessageFactory() {
            @Override
            public MessageLevel getLevel() {
                return level;
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

    @NonNull
    static ResourceMessageFactory of(@NonNull MessageLevel level, @NonNull Function<Resources, String> factory) {
        return new ResourceMessageFactory() {
            @Override
            public MessageLevel getLevel() {
                return level;
            }

            @Override
            public String apply(Resources resources) {
                return factory.apply(resources);
            }
        };
    }

    @NonNull
    static ResourceMessageFactory of(@NonNull MessageLevel level, @StringRes int id, Object... formatArgs) {
        return new ResourceMessageFactory() {
            @Override
            public MessageLevel getLevel() {
                return level;
            }

            @Override
            public String apply(Resources resources) {
                return resources.getString(id, formatArgs);
            }
        };
    }

    @NonNull
    static ResourceMessageFactory ofWarning(int id) {
        return of(MessageLevel.WARNING, id);
    }

    @NonNull
    static ResourceMessageFactory ofWarning(@NonNull Throwable throwable) {
        return of(MessageLevel.WARNING, throwable);
    }

    @NonNull
    static ResourceMessageFactory ofWarning(Function<Resources, String> factory) {
        return of(MessageLevel.WARNING, factory);
    }

    @NonNull
    static ResourceMessageFactory ofWarning(@StringRes int id, Object... formatArgs) {
        return of(MessageLevel.WARNING, id, formatArgs);
    }

    @NonNull
    static ResourceMessageFactory ofError(int id) {
        return of(MessageLevel.ERROR, id);
    }

    @NonNull
    static ResourceMessageFactory ofError(@NonNull Throwable throwable) {
        return of(MessageLevel.ERROR, throwable);
    }

    @NonNull
    static ResourceMessageFactory ofError(Function<Resources, String> factory) {
        return of(MessageLevel.ERROR, factory);
    }

    @NonNull
    static ResourceMessageFactory ofError(@StringRes int id, Object... formatArgs) {
        return of(MessageLevel.ERROR, id, formatArgs);
    }

    @NonNull
    static ResourceMessageFactory ofInfo(@StringRes int id) {
        return r -> r.getString(id);
    }

    @NonNull
    static ResourceMessageFactory ofInfo(@NonNull Throwable throwable) {
        return r -> {
            String message = throwable.getLocalizedMessage();
            if ((null == message || message.trim().isEmpty()) && (null == (message = throwable.getMessage()) || message.trim().isEmpty())) {
                message = throwable.getClass().getSimpleName();
            }
            return r.getString(R.string.format_error, message);
        };
    }

    @NonNull
    static ResourceMessageFactory ofInfo(@NonNull Function<Resources, String> factory) {
        return factory::apply;
    }

    @NonNull
    static ResourceMessageFactory ofInfo(@StringRes int id, Object... formatArgs) {
        return r -> r.getString(id, formatArgs);
    }

    default MessageLevel getLevel() {
        return MessageLevel.INFO;
    }
}
