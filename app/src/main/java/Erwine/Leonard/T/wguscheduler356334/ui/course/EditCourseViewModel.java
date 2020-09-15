package Erwine.Leonard.T.wguscheduler356334.ui.course;

import androidx.lifecycle.ViewModel;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import Erwine.Leonard.T.wguscheduler356334.ui.term.TermViewModel;

public class EditCourseViewModel extends ViewModel {
    private static final String LOG_TAG = TermViewModel.class.getName();
    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("eee, MMM d, YYYY").withZone(ZoneId.systemDefault());
    static final String ARGUMENT_KEY_STATE_INITIALIZED = "state_initialized";
    public static final String ARGUMENT_KEY_COURSE_ID = "course_id";
    public static final String ARGUMENT_KEY_TERM_ID = "term_id";
    // TODO: Implement the ViewModel
}