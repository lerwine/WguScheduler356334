package Erwine.Leonard.T.wguscheduler356334.util.validation;

import android.content.res.Resources;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

public class ResourceMessageBuilder implements Consumer<ResourceMessageFactory> {

    private final Stream.Builder<ResourceMessageFactory> builder = Stream.builder();
    @Nullable
    private Boolean hasError;

    public boolean hasError() {
        return null != hasError && hasError;
    }

    public boolean hasWarning() {
        return null != hasError;
    }

    public ResourceMessageResult build() {
        return new ResourceMessageResult(builder.build(), hasError);
    }

    public void acceptError(@StringRes int id) {
        hasError = true;
        builder.accept(ResourceMessageFactory.ofError(id));
    }

    public void acceptError(Function<Resources, String> factory) {
        hasError = true;
        builder.accept(ResourceMessageFactory.ofError(factory));
    }

    public void acceptError(@StringRes int id, Object... formatArgs) {
        hasError = true;
        builder.accept(ResourceMessageFactory.ofError(id, formatArgs));
    }

    public void acceptWarning(@StringRes int id) {
        if (null != hasError) {
            hasError = false;
        }
        builder.accept(ResourceMessageFactory.ofWarning(id));
    }

    public void acceptWarning(Function<Resources, String> factory) {
        if (null != hasError) {
            hasError = false;
        }
        builder.accept(ResourceMessageFactory.ofWarning(factory));
    }

    public void acceptWarning(@StringRes int id, Object... formatArgs) {
        if (null != hasError) {
            hasError = false;
        }
        builder.accept(ResourceMessageFactory.ofWarning(id, formatArgs));
    }

    @Override
    public void accept(ResourceMessageFactory resourceMessageFactory) {
        if (null != resourceMessageFactory) {
            if (!resourceMessageFactory.isWarning()) {
                hasError = true;
            } else if (null != hasError) {
                hasError = false;
            }
            builder.accept(resourceMessageFactory);
        }
    }

}
