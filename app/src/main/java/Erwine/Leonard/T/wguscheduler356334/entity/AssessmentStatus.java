package Erwine.Leonard.T.wguscheduler356334.entity;

import androidx.annotation.StringRes;

import Erwine.Leonard.T.wguscheduler356334.R;

public enum AssessmentStatus {
    NOT_STARTED(R.string.label_not_started),
    IN_PROGRESS(R.string.label_in_progress),
    EVALUATING(R.string.label_evaluating),
    PASSED(R.string.label_passed),
    NOT_PASSED(R.string.label_not_passed);

    private final int displayResourceId;

    AssessmentStatus(@StringRes int displayResourceId) {
        this.displayResourceId = displayResourceId;
    }

    @StringRes
    public int displayResourceId() {
        return displayResourceId;
    }
}
