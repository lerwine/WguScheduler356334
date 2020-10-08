package Erwine.Leonard.T.wguscheduler356334.util.observables;

import androidx.annotation.NonNull;
import androidx.core.util.Consumer;

import java.util.Optional;
import java.util.function.Function;

import Erwine.Leonard.T.wguscheduler356334.util.StringHelper;
import Erwine.Leonard.T.wguscheduler356334.util.StringNormalizationOption;
import Erwine.Leonard.T.wguscheduler356334.util.ValidationMessage;

public abstract class StringParsingObservables<R> extends MessageValidatedBehaviorObservables<String, R> {
    public static final Function<String, String> SINGLE_LINE_NORMALIZER = StringHelper.getNormalizer(StringNormalizationOption.SINGLE_LINE);
    public static final Function<String, String> MULTI_LINE_NORMALIZER = StringHelper.getNormalizer(StringNormalizationOption.NO_TRIM_START, StringNormalizationOption.LEAVE_BLANK_LINES);
    private final Function<String, String> normalizer;

    public StringParsingObservables(boolean isMultiLine) {
        this((isMultiLine) ? MULTI_LINE_NORMALIZER : SINGLE_LINE_NORMALIZER);
    }

    public StringParsingObservables(@NonNull Function<String, String> normalizer) {
        this.normalizer = normalizer;
    }

    @NonNull
    @Override
    protected Optional<R> mapNext(@NonNull String s, Consumer<ValidationMessage.ResourceMessageFactory> onMessage) {
        return tryParse(normalizer.apply(s), onMessage);
    }

    protected abstract Optional<R> tryParse(String apply, Consumer<ValidationMessage.ResourceMessageFactory> onMessage);
}
