package Erwine.Leonard.T.wguscheduler356334.util.observables;

import androidx.annotation.NonNull;

import java.util.function.Function;

import Erwine.Leonard.T.wguscheduler356334.util.StringHelper;
import Erwine.Leonard.T.wguscheduler356334.util.StringNormalizationOption;

public abstract class StringValidatingObservables extends MessageValidatedBehaviorObservables<String, String> {
    public static final Function<String, String> SINGLE_LINE_NORMALIZER = StringHelper.getNormalizer(StringNormalizationOption.SINGLE_LINE);
    public static final Function<String, String> MULTI_LINE_NORMALIZER = StringHelper.getNormalizer(StringNormalizationOption.NO_TRIM_START, StringNormalizationOption.LEAVE_BLANK_LINES);
    private final Function<String, String> normalizer;

    public StringValidatingObservables(boolean isMultiLine) {
        this((isMultiLine) ? MULTI_LINE_NORMALIZER : SINGLE_LINE_NORMALIZER);
    }

    public StringValidatingObservables(@NonNull Function<String, String> normalizer) {
        this.normalizer = normalizer;
    }

}
