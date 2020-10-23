package Erwine.Leonard.T.wguscheduler356334.util.observables;

import androidx.annotation.NonNull;

import java.util.Optional;
import java.util.function.Function;

import Erwine.Leonard.T.wguscheduler356334.util.StringHelper;
import Erwine.Leonard.T.wguscheduler356334.util.StringNormalizationOption;

public class StringNormalizerObservables extends ValidatedBehaviorObservables<String, String> {
    public static final Function<String, String> SINGLE_LINE_NORMALIZER = StringHelper.getNormalizer(StringNormalizationOption.SINGLE_LINE);
    public static final Function<String, String> MULTI_LINE_NORMALIZER = StringHelper.getNormalizer(StringNormalizationOption.NO_TRIM_START, StringNormalizationOption.LEAVE_BLANK_LINES);
    private final Function<String, String> normalizer;

    public StringNormalizerObservables(boolean isMultiLine) {
        this((isMultiLine) ? MULTI_LINE_NORMALIZER : SINGLE_LINE_NORMALIZER);
    }

    public StringNormalizerObservables(@NonNull Function<String, String> normalizer) {
        this.normalizer = normalizer;
    }

    @NonNull
    @Override
    public Optional<String> mapNext(@NonNull String s) {
        String n = normalizer.apply(s);
        return (null == n || n.isEmpty()) ? Optional.empty() : Optional.of(n);
    }
}
