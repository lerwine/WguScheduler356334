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
        return stream.map(f -> new ValidationMessage(f.apply(resources), f.isWarning()));
    }

    public static Optional<List<String>> map(Stream<ResourceMessageFactory> stream, Resources resources, Consumer<String> onWarningMessage) {
        Iterator<ResourceMessageFactory> iterator = stream.iterator();
        if (!iterator.hasNext()) {
            return Optional.empty();
        }
        ResourceMessageFactory factory = iterator.next();
        while (factory.isWarning()) {
            onWarningMessage.accept(factory.apply(resources));
            if (!iterator.hasNext()) {
                return Optional.empty();
            }
            factory = iterator.next();
        }
        ArrayList<String> result = new ArrayList<>();
        result.add(factory.apply(resources));
        while (iterator.hasNext()) {
            factory = iterator.next();
            String message = factory.apply(resources);
            if (factory.isWarning()) {
                onWarningMessage.accept(message);
            } else {
                result.add(message);
            }
        }
        return Optional.of(result);
    }

    public static Stream<String> filter(Stream<ResourceMessageFactory> stream, Resources resources, Consumer<String> onWarningMessage) {
        return stream.filter(f -> {
            if (f.isWarning()) {
                onWarningMessage.accept(f.apply(resources));
                return false;
            }
            return true;
        }).map(f -> f.apply(resources));
    }

    public static boolean test(Stream<ResourceMessageFactory> stream, Resources resources, Consumer<List<ValidationMessage>> onHasAnyError, Consumer<List<String>> onNoErrors) {
        Iterator<ResourceMessageFactory> iterator = stream.iterator();
        ArrayList<String> messages = new ArrayList<>();
        if (!iterator.hasNext()) {
            onNoErrors.accept(messages);
            return true;
        }
        ResourceMessageFactory factory = iterator.next();
        while (factory.isWarning()) {
            messages.add(factory.apply(resources));
            if (!iterator.hasNext()) {
                onNoErrors.accept(messages);
                return true;
            }
            factory = iterator.next();
        }
        ArrayList<ValidationMessage> items = new ArrayList<>();
        messages.forEach(m -> items.add(new ValidationMessage(m, false)));
        items.add(new ValidationMessage(factory.apply(resources), true));
        while (iterator.hasNext()) {
            factory = iterator.next();
            items.add(new ValidationMessage(factory.apply(resources), factory.isWarning()));
        }
        onHasAnyError.accept(items);
        return false;
    }

    private final String message;
    private final boolean warning;

    public ValidationMessage(@NonNull String message, boolean warning) {
        this.message = message;
        this.warning = warning;
    }

    public static ResourceMessageResult success() {
        return new ResourceMessageResult(Stream.empty(), null);
    }

    public static ResourceMessageResult ofSingleWarning(@StringRes int id) {
        return new ResourceMessageResult(Stream.of(ResourceMessageFactory.ofWarning(id)), false);
    }

    public static ResourceMessageResult ofSingleWarning(@StringRes int id, Object... formatArgs) {
        return new ResourceMessageResult(Stream.of(ResourceMessageFactory.ofWarning(id, formatArgs)), false);
    }

    public static ResourceMessageResult ofSingleError(@StringRes int id) {
        return new ResourceMessageResult(Stream.of(ResourceMessageFactory.ofError(id)), false);
    }

    public static ResourceMessageResult ofSingleError(@StringRes int id, Object... formatArgs) {
        return new ResourceMessageResult(Stream.of(ResourceMessageFactory.ofError(id, formatArgs)), false);
    }

    public String getMessage() {
        return message;
    }

    public boolean isWarning() {
        return warning;
    }

}
