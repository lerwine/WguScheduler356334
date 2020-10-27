package Erwine.Leonard.T.wguscheduler356334.util.validation;

import android.content.res.Resources;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import Erwine.Leonard.T.wguscheduler356334.R;

public class ResourceMessageResult {

    private final Stream<ResourceMessageFactory> stream;
    private final boolean error;
    private final boolean warning;
    private final boolean succeeded;

    ResourceMessageResult(Stream<ResourceMessageFactory> stream, Boolean error) {
        this.stream = stream;
        succeeded = null == error;
        if (succeeded) {
            this.error = warning = false;
        } else {
            this.error = error;
            warning = !error;
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
