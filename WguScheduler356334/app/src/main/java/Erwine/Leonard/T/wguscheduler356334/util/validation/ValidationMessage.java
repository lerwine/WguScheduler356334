package Erwine.Leonard.T.wguscheduler356334.util.validation;

import android.content.res.Resources;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class ValidationMessage {

    public static Stream<ValidationMessage> accept(Stream<ResourceMessageFactory> stream, Resources resources) {
        return stream.map(f -> new ValidationMessage(f.apply(resources), f.getLevel()));
    }

    public static Optional<List<String>> map(Stream<ResourceMessageFactory> stream, Resources resources, Consumer<String> onWarningMessage) {
        Iterator<ResourceMessageFactory> iterator = stream.iterator();
        if (!iterator.hasNext()) {
            return Optional.empty();
        }
        ResourceMessageFactory factory = iterator.next();
        MessageLevel ml = factory.getLevel();
        while (ml != MessageLevel.ERROR) {
            if (ml == MessageLevel.WARNING) {
                onWarningMessage.accept(factory.apply(resources));
            }
            if (!iterator.hasNext()) {
                return Optional.empty();
            }
            ml = (factory = iterator.next()).getLevel();
        }
        ArrayList<String> result = new ArrayList<>();
        result.add(factory.apply(resources));
        while (iterator.hasNext()) {
            factory = iterator.next();
            String message = factory.apply(resources);
            switch (factory.getLevel()) {
                case ERROR:
                    result.add(message);
                    break;
                case WARNING:
                    onWarningMessage.accept(message);
                    break;
                default:
                    break;
            }
        }
        return Optional.of(result);
    }

    public static Stream<String> filter(Stream<ResourceMessageFactory> stream, Resources resources, Consumer<String> onWarningMessage) {
        return stream.filter(f -> {
            switch (f.getLevel()) {
                case ERROR:
                    return true;
                case WARNING:
                    onWarningMessage.accept(f.apply(resources));
                    return false;
                default:
                    return false;
            }
        }).map(f -> f.apply(resources));
    }

    public static boolean test(Stream<ResourceMessageFactory> stream, Resources resources, Consumer<List<ValidationMessage>> onHasAnyError, Consumer<List<ValidationMessage>> onNoErrors) {
        Iterator<ResourceMessageFactory> iterator = stream.iterator();
        ArrayList<ValidationMessage> messages = new ArrayList<>();
        if (!iterator.hasNext()) {
            onNoErrors.accept(messages);
            return true;
        }
        ResourceMessageFactory factory = iterator.next();
        MessageLevel ml = factory.getLevel();
        while (ml != MessageLevel.ERROR) {
            messages.add(new ValidationMessage(factory.apply(resources), ml));
            if (!iterator.hasNext()) {
                onNoErrors.accept(messages);
                return true;
            }
            ml = (factory = iterator.next()).getLevel();
        }
        messages.add(new ValidationMessage(factory.apply(resources), ml));
        while (iterator.hasNext()) {
            factory = iterator.next();
            messages.add(new ValidationMessage(factory.apply(resources), factory.getLevel()));
        }
        onHasAnyError.accept(messages);
        return false;
    }

    private final String message;
    private final MessageLevel level;

    public ValidationMessage(@NonNull String message, @NonNull MessageLevel level) {
        this.message = message;
        this.level = level;
    }

    public static ResourceMessageResult success() {
        return new ResourceMessageResult(Stream.empty(), Optional.empty());
    }

    public static ResourceMessageResult ofSingleWarning(@StringRes int id) {
        return new ResourceMessageResult(Stream.of(ResourceMessageFactory.ofWarning(id)), Optional.of(MessageLevel.WARNING));
    }

    public static ResourceMessageResult ofSingleWarning(@StringRes int id, Object... formatArgs) {
        return new ResourceMessageResult(Stream.of(ResourceMessageFactory.ofWarning(id, formatArgs)), Optional.of(MessageLevel.WARNING));
    }

    public static ResourceMessageResult ofSingleError(@StringRes int id) {
        return new ResourceMessageResult(Stream.of(ResourceMessageFactory.ofError(id)), Optional.of(MessageLevel.ERROR));
    }

    public static ResourceMessageResult ofSingleError(@StringRes int id, Object... formatArgs) {
        return new ResourceMessageResult(Stream.of(ResourceMessageFactory.ofError(id, formatArgs)), Optional.of(MessageLevel.ERROR));
    }

    public String getMessage() {
        return message;
    }

    public MessageLevel getLevel() {
        return level;
    }

}
