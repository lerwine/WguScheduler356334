package Erwine.Leonard.T.wguscheduler356334.util.validation;

import androidx.annotation.DrawableRes;

import Erwine.Leonard.T.wguscheduler356334.R;

public enum MessageLevel {
    INFO(R.drawable.dialog_success),
    WARNING(R.drawable.dialog_warning),
    ERROR(R.drawable.dialog_error);

    @DrawableRes
    private final int errorIcon;

    MessageLevel(@DrawableRes int errorIcon) {
        this.errorIcon = errorIcon;
    }

    public int getErrorIcon() {
        return errorIcon;
    }
}
