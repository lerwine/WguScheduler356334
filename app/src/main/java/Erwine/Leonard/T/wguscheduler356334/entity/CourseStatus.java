package Erwine.Leonard.T.wguscheduler356334.entity;

import Erwine.Leonard.T.wguscheduler356334.R;

public enum CourseStatus {
    UNPLANNED(R.string.label_unplanned),
    PLANNED(R.string.label_planned),
    IN_PROGRESS(R.string.label_in_progress),
    PASSED(R.string.label_passed),
    NOT_PASSED(R.string.label_not_passed),
    DROPPED(R.string.label_dropped);

    private final int displayResourceId;

    CourseStatus(int displayResourceId) {
        this.displayResourceId = displayResourceId;
    }

    public int displayResourceId() {
        return displayResourceId;
    }
}
