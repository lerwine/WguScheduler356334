package Erwine.Leonard.T.wguscheduler356334.util.validation;

import android.content.res.Resources;

import androidx.annotation.NonNull;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import Erwine.Leonard.T.wguscheduler356334.R;

public class ResourceMessageResult {

    private final Stream<ResourceMessageFactory> stream;
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private final Optional<MessageLevel> level;
    private final boolean error;
    private final boolean warning;
    private final boolean succeeded;

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    ResourceMessageResult(@NonNull Stream<ResourceMessageFactory> stream, Optional<MessageLevel> level) {
        this.stream = stream;
        this.level = level;
        switch (level.orElse(MessageLevel.INFO)) {
            case INFO:
                succeeded = true;
                warning = error = false;
                break;
            case WARNING:
                warning = true;
                succeeded = error = false;
                break;
            default:
                error = true;
                succeeded = warning = false;
                break;
        }
    }

    public Stream<ResourceMessageFactory> getStream() {
        return stream;
    }

    public Optional<MessageLevel> getLevel() {
        return level;
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
            switch (t.getLevel()) {
                case INFO:
                    return resources.getString(R.string.format_info, m);
                case WARNING:
                    return resources.getString(R.string.format_warning, m);
                default:
                    return resources.getString(R.string.format_error, m);
            }
        }).collect(Collectors.joining(delimiter));
    }
}
