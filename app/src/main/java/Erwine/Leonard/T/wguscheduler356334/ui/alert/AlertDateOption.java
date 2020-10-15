package Erwine.Leonard.T.wguscheduler356334.ui.alert;

import androidx.annotation.Nullable;

public enum AlertDateOption {
    EXPLICIT(false, false, true),
    BEFORE_START_DATE(false, false, false),
    AFTER_START_DATE(true, false, false),
    BEFORE_END_DATE(false, true, false),
    AFTER_END_DATE(true, true, false);

    private final boolean end;
    private final boolean after;
    private final boolean explicit;

    AlertDateOption(boolean after, boolean end, boolean explicit) {
        this.end = end;
        this.after = after;
        this.explicit = explicit;
    }

    public static AlertDateOption of(@Nullable Boolean subsequent, long days) {
        if (null == subsequent) {
            return AlertDateOption.EXPLICIT;
        }
        if (days < 0) {
            return (subsequent) ? AlertDateOption.BEFORE_END_DATE : AlertDateOption.BEFORE_START_DATE;
        }
        return (subsequent) ? AlertDateOption.AFTER_END_DATE : AlertDateOption.AFTER_START_DATE;
    }

    public boolean isStart() {
        return !end;
    }

    public boolean isEnd() {
        return end;
    }

    public boolean isBefore() {
        return !after;
    }

    public boolean isAfter() {
        return after;
    }

    public boolean isExplicit() {
        return explicit;
    }
}
