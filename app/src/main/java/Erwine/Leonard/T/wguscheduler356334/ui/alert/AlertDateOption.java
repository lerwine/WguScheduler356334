package Erwine.Leonard.T.wguscheduler356334.ui.alert;

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
