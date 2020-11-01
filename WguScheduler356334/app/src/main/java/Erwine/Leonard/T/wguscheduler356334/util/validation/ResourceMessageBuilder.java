package Erwine.Leonard.T.wguscheduler356334.util.validation;

import android.content.res.Resources;

import androidx.annotation.StringRes;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

public class ResourceMessageBuilder implements Consumer<ResourceMessageFactory> {

    private final Stream.Builder<ResourceMessageFactory> builder = Stream.builder();
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private Optional<MessageLevel> level = Optional.empty();

    public Optional<MessageLevel> getLevel() {
        return level;
    }

    public ResourceMessageResult build() {
        return new ResourceMessageResult(builder.build(), level);
    }

    public void acceptError(@StringRes int id) {
        level = Optional.of(MessageLevel.ERROR);
        builder.accept(ResourceMessageFactory.ofError(id));
    }

    public void acceptError(Function<Resources, String> factory) {
        level = Optional.of(MessageLevel.ERROR);
        builder.accept(ResourceMessageFactory.ofError(factory));
    }

    public void acceptError(@StringRes int id, Object... formatArgs) {
        level = Optional.of(MessageLevel.ERROR);
        builder.accept(ResourceMessageFactory.ofError(id, formatArgs));
    }

    public void acceptWarning(@StringRes int id) {
        if (level.map(l -> l == MessageLevel.INFO).orElse(true)) {
            level = Optional.of(MessageLevel.WARNING);
        }
        builder.accept(ResourceMessageFactory.ofWarning(id));
    }

    public void acceptWarning(Function<Resources, String> factory) {
        if (level.map(l -> l == MessageLevel.INFO).orElse(true)) {
            level = Optional.of(MessageLevel.WARNING);
        }
        builder.accept(ResourceMessageFactory.ofWarning(factory));
    }

    public void acceptWarning(@StringRes int id, Object... formatArgs) {
        if (level.map(l -> l == MessageLevel.INFO).orElse(true)) {
            level = Optional.of(MessageLevel.WARNING);
        }
        builder.accept(ResourceMessageFactory.ofWarning(id, formatArgs));
    }

    public void acceptInfo(@StringRes int id) {
        if (!level.isPresent()) {
            level = Optional.of(MessageLevel.WARNING);
        }
        builder.accept(ResourceMessageFactory.ofInfo(id));
    }

    public void acceptInfo(Function<Resources, String> factory) {
        if (!level.isPresent()) {
            level = Optional.of(MessageLevel.WARNING);
        }
        builder.accept(ResourceMessageFactory.ofInfo(factory));
    }

    public void acceptInfo(@StringRes int id, Object... formatArgs) {
        if (!level.isPresent()) {
            level = Optional.of(MessageLevel.WARNING);
        }
        builder.accept(ResourceMessageFactory.ofInfo(id, formatArgs));
    }

    @Override
    public void accept(ResourceMessageFactory resourceMessageFactory) {
        if (null != resourceMessageFactory) {
            if (level.isPresent()) {
                level = level.flatMap(l -> {
                    switch (resourceMessageFactory.getLevel()) {
                        case ERROR:
                            if (l != MessageLevel.ERROR) {
                                return Optional.of(MessageLevel.ERROR);
                            }
                            break;
                        case WARNING:
                            if (l == MessageLevel.INFO) {
                                return Optional.of(MessageLevel.WARNING);
                            }
                            break;
                        default:
                            break;
                    }
                    return level;
                });
            } else {
                level = Optional.of(resourceMessageFactory.getLevel());
            }
            builder.accept(resourceMessageFactory);
        }
    }

}
