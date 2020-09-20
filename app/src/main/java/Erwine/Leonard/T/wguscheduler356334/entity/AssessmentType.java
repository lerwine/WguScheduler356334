package Erwine.Leonard.T.wguscheduler356334.entity;

import Erwine.Leonard.T.wguscheduler356334.R;

public enum AssessmentType {
    PRE_ASSESSMENT(R.string.label_pre_assessment),
    OBJECTIVE_ASSESSMENT(R.string.label_objective_assessment),
    PERFORMANCE_EVALUATION(R.string.label_performance_evaluation);

    private final int displayResourceId;

    AssessmentType(int displayResourceId) {
        this.displayResourceId = displayResourceId;
    }

    public int displayResourceId() {
        return displayResourceId;
    }
}
