package Erwine.Leonard.T.wguscheduler356334.util;

import android.content.res.Resources;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import Erwine.Leonard.T.wguscheduler356334.R;

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

    public static class ResourceMessageBuilder implements Consumer<ResourceMessageFactory> {

        private final Stream.Builder<ResourceMessageFactory> builder = Stream.builder();
        private Boolean hasError;

        public boolean hasError() {
            return null != hasError && hasError;
        }

        public boolean hasWarning() {
            return null != hasError;
        }

        public ResourceMessageResult build() {
            return new ResourceMessageResult(this);
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

    public static class ResourceMessageResult {

        private final Stream<ResourceMessageFactory> stream;
        private final boolean error;
        private final boolean warning;
        private final boolean succeeded;

        private ResourceMessageResult(Stream<ResourceMessageFactory> stream, Boolean error) {
            this.stream = stream;
            succeeded = null == error;
            if (succeeded) {
                this.error = warning = false;
            } else {
                this.error = error;
                warning = !error;
            }
        }

        private ResourceMessageResult(ResourceMessageBuilder builder) {
            stream = builder.builder.build();
            error = builder.hasError();
            if (error) {
                succeeded = warning = false;
            } else {
                warning = builder.hasWarning();
                succeeded = !warning;
            }
        }

        public Stream<ResourceMessageFactory> getStream() {
            return stream;
        }

        public boolean isSucceeded() {
            return succeeded;
        }

        public boolean isWarning() {
            return warning;
        }

        public boolean isError() {
            return error;
        }

        public String join(String delimiter, Resources resources) {
            return stream.map(t -> {
                String m = t.apply(resources);
                if (t.isWarning()) {
                    return resources.getString(R.string.format_warning, m);
                }
                return resources.getString(R.string.format_error, m);
            }).collect(Collectors.joining(delimiter));
        }
    }
}
