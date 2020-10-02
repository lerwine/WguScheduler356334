package Erwine.Leonard.T.wguscheduler356334.ui.alert;

import android.app.Application;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.function.Supplier;

import Erwine.Leonard.T.wguscheduler356334.R;
import Erwine.Leonard.T.wguscheduler356334.db.DbLoader;
import Erwine.Leonard.T.wguscheduler356334.entity.AbstractEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.alert.AlertEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.assessment.AssessmentAlertDetails;
import Erwine.Leonard.T.wguscheduler356334.entity.assessment.AssessmentDetails;
import Erwine.Leonard.T.wguscheduler356334.entity.assessment.AssessmentEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.course.CourseAlertDetails;
import Erwine.Leonard.T.wguscheduler356334.entity.course.CourseDetails;
import Erwine.Leonard.T.wguscheduler356334.entity.course.CourseEntity;
import Erwine.Leonard.T.wguscheduler356334.ui.course.CoursePropertiesFragment;
import Erwine.Leonard.T.wguscheduler356334.util.ComparisonHelper;
import Erwine.Leonard.T.wguscheduler356334.util.ValidationMessage;
import Erwine.Leonard.T.wguscheduler356334.util.live.BooleanAndLiveData;
import io.reactivex.disposables.CompositeDisposable;

public class EditAlertViewModel extends AndroidViewModel {

    private static final String LOG_TAG = CoursePropertiesFragment.class.getName();
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("eee, MMM d, YYYY").withZone(ZoneId.systemDefault());
    public static final NumberFormat NUMBER_FORMATTER = NumberFormat.getIntegerInstance();
    static final String ARG_KEY_ALERT_ID = "alert_id";
    static final String ARG_KEY_COURSE_ID = "course_id";
    static final String ARG_KEY_ASSESSMENT_ID = "assessment_id";
    private static final String STATE_KEY_STATE_INITIALIZED = "state_initialized";
    private static final String STATE_KEY_RELATIVITY = "relativity";
    private static final String STATE_KEY_MESSAGE = "message";
    private static final String STATE_KEY_BEFORE_END_TEXT = "before_end_text";
    private static final String STATE_KEY_BEFORE_START_TEXT = "before_start_text";
    private static final String STATE_KEY_SELECTED_DATE = "selected_date";
    private static final String STATE_KEY_EVENT_START = "event_start";
    private static final String STATE_KEY_EVENT_END = "event_end";
    private static final String STATE_KEY_TYPE = "type";
    @StringRes
    private static final int TYPE_VALUE_COURSE = R.string.label_course;

    public static EditAlertFragment existingCourseAlertEditor(long alertId, long courseId) {
        Bundle args = new Bundle();
        args.putLong(ARG_KEY_ALERT_ID, alertId);
        args.putLong(ARG_KEY_COURSE_ID, courseId);
        EditAlertFragment dialog = new EditAlertFragment();
        dialog.setArguments(args);
        return dialog;
    }

    public static EditAlertFragment existingAssessmentAlertEditor(long alertId, long assessmentId) {
        Bundle args = new Bundle();
        args.putLong(ARG_KEY_ALERT_ID, alertId);
        args.putLong(ARG_KEY_ASSESSMENT_ID, assessmentId);
        EditAlertFragment dialog = new EditAlertFragment();
        dialog.setArguments(args);
        return dialog;
    }

    public static EditAlertFragment newCourseAlert(long courseId) {
        Bundle args = new Bundle();
        args.putLong(ARG_KEY_COURSE_ID, courseId);
        EditAlertFragment dialog = new EditAlertFragment();
        dialog.setArguments(args);
        return dialog;
    }

    public static EditAlertFragment newAssessmentAlert(long assessmentId) {
        Bundle args = new Bundle();
        args.putLong(ARG_KEY_ASSESSMENT_ID, assessmentId);
        EditAlertFragment dialog = new EditAlertFragment();
        dialog.setArguments(args);
        return dialog;
    }

    private final MutableLiveData<AlertEntity> alertLiveData;
    private final MutableLiveData<ValidationMessage.ResourceMessageResult> initializationFailureLiveData;
    private final MutableLiveData<ValidationMessage.ResourceMessageFactory> beforeStartValidationLiveData;
    private final MutableLiveData<ValidationMessage.ResourceMessageFactory> beforeEndValidationLiveData;
    private final MutableLiveData<ValidationMessage.ResourceMessageFactory> selectedDateValidationLiveData;
    private final MutableLiveData<LocalDate> alertDateLiveData;
    private final BooleanAndLiveData validLiveData;
    private final CompositeDisposable compositeDisposable;
    private final DbLoader dbLoader;
    private CourseEntity courseEntity;
    private AssessmentEntity assessmentEntity;
    private AlertEntity alertEntity;
    private int type;
    private AlertDateOption dateSpecOption;
    private LocalDate eventStart;
    private LocalDate eventEnd;
    private String message;
    private Integer beforeStartValue;
    private String beforeStartText;
    private Integer beforeEndValue;
    private String beforeEndText;
    private LocalDate selectedDate;

    public EditAlertViewModel(@NonNull Application application) {
        super(application);
        compositeDisposable = new CompositeDisposable();
        dbLoader = DbLoader.getInstance(getApplication());
        beforeStartValidationLiveData = new MutableLiveData<>();
        beforeEndValidationLiveData = new MutableLiveData<>();
        selectedDateValidationLiveData = new MutableLiveData<>();
        alertDateLiveData = new MutableLiveData<>();
        validLiveData = new BooleanAndLiveData();
        validLiveData.addSource(beforeStartValidationLiveData, input -> null == input || input.isWarning());
        validLiveData.addSource(beforeEndValidationLiveData, input -> null == input || input.isWarning());
        validLiveData.addSource(selectedDateValidationLiveData, input -> null == input || input.isWarning());
        alertLiveData = new MutableLiveData<>();
        initializationFailureLiveData = new MutableLiveData<>();
    }

    public AlertEntity getAlertEntity() {
        return alertEntity;
    }

    public CourseEntity getCourseEntity() {
        return courseEntity;
    }

    public AssessmentEntity getAssessmentEntity() {
        return assessmentEntity;
    }

    public int getType() {
        return type;
    }

    public AlertDateOption getDateSpecOption() {
        return dateSpecOption;
    }

    public synchronized void setDateSpecOption(AlertDateOption dateSpecOption) {
        this.dateSpecOption = (null == dateSpecOption) ? AlertDateOption.EXPLICIT : dateSpecOption;
        switch (this.dateSpecOption) {
            case START_DATE:
                if (null == beforeStartValue) {
                    alertDateLiveData.postValue(null);
                    beforeStartValidationLiveData.postValue(ValidationMessage.ResourceMessageFactory.ofError((beforeStartText.isEmpty()) ? R.string.message_required : R.string.message_invalid_number));
                } else if (null != eventStart) {
                    alertDateLiveData.postValue(eventStart.minusDays(beforeStartValue));
                    beforeStartValidationLiveData.postValue(null);
                } else {
                    alertDateLiveData.postValue(null);
                    if (type == TYPE_VALUE_COURSE) {
                        beforeStartValidationLiveData.postValue(ValidationMessage.ResourceMessageFactory.ofWarning(R.string.message_course_has_no_start_date));
                    } else {
                        beforeStartValidationLiveData.postValue(ValidationMessage.ResourceMessageFactory.ofWarning(R.string.message_assessment_has_no_date));
                    }
                }
                beforeEndValidationLiveData.postValue(null);
                selectedDateValidationLiveData.postValue(null);
                break;
            case END_DATE:
                if (null == beforeEndValue) {
                    alertDateLiveData.postValue(null);
                    beforeEndValidationLiveData.postValue(ValidationMessage.ResourceMessageFactory.ofError((beforeEndText.isEmpty()) ? R.string.message_required : R.string.message_invalid_number));
                } else if (null != eventEnd) {
                    alertDateLiveData.postValue(eventEnd.minusDays(beforeEndValue));
                    beforeEndValidationLiveData.postValue(null);
                } else {
                    alertDateLiveData.postValue(null);
                    if (type == TYPE_VALUE_COURSE) {
                        beforeEndValidationLiveData.postValue(ValidationMessage.ResourceMessageFactory.ofWarning(R.string.message_course_has_no_end_date));
                    } else {
                        beforeEndValidationLiveData.postValue(ValidationMessage.ResourceMessageFactory.ofWarning(R.string.message_assessment_has_no_date));
                    }
                }
                beforeStartValidationLiveData.postValue(null);
                selectedDateValidationLiveData.postValue(null);
                break;
            default:
                alertDateLiveData.postValue(selectedDate);
                if (null == selectedDate) {
                    selectedDateValidationLiveData.postValue(ValidationMessage.ResourceMessageFactory.ofError(R.string.message_required));
                } else {
                    selectedDateValidationLiveData.postValue(null);
                }
                beforeStartValidationLiveData.postValue(null);
                beforeEndValidationLiveData.postValue(null);
                break;
        }
    }

    public LocalDate getEventStart() {
        return eventStart;
    }

    public LocalDate getEventEnd() {
        return eventEnd;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = AbstractEntity.SINGLE_LINE_NORMALIZER.apply(message);
    }

    public Integer getBeforeStartValue() {
        return beforeStartValue;
    }

    public String getBeforeStartText() {
        return beforeStartText;
    }

    public synchronized void setBeforeStartText(String beforeStartText) {
        this.beforeStartText = AbstractEntity.SINGLE_LINE_NORMALIZER.apply(beforeStartText);
        if (dateSpecOption != AlertDateOption.START_DATE) {
            return;
        }
        if (this.beforeStartText.isEmpty()) {
            beforeStartValue = null;
            alertDateLiveData.postValue(null);
            beforeStartValidationLiveData.postValue(ValidationMessage.ResourceMessageFactory.ofError(R.string.message_required));
        } else {
            try {
                int value = Objects.requireNonNull(NUMBER_FORMATTER.parse(this.beforeStartText)).intValue();
                if (value < AlertEntity.MIN_VALUE_RELATIVE_DAYS || value > AlertEntity.MAX_VALUE_RELATIVE_DAYS) {
                    beforeStartValidationLiveData.postValue(ValidationMessage.ResourceMessageFactory.ofError(R.string.message_relative_days_out_of_range));
                    beforeStartValue = null;
                } else {
                    beforeStartValue = value;
                }
            } catch (ParseException | NullPointerException e) {
                beforeStartValidationLiveData.postValue(ValidationMessage.ResourceMessageFactory.ofError(R.string.message_invalid_number));
                beforeStartValue = null;
            }
            if (null != beforeStartValue) {
                if (null != eventStart) {
                    alertDateLiveData.postValue(eventStart.minusDays(beforeStartValue));
                    beforeStartValidationLiveData.postValue(null);
                } else {
                    alertDateLiveData.postValue(null);
                    if (type == TYPE_VALUE_COURSE) {
                        beforeStartValidationLiveData.postValue(ValidationMessage.ResourceMessageFactory.ofWarning(R.string.message_course_has_no_start_date));
                    } else {
                        beforeStartValidationLiveData.postValue(ValidationMessage.ResourceMessageFactory.ofWarning(R.string.message_assessment_has_no_date));
                    }
                }
            } else {
                alertDateLiveData.postValue(null);
            }
        }
    }

    public Integer getBeforeEndValue() {
        return beforeEndValue;
    }

    public String getBeforeEndText() {
        return beforeEndText;
    }

    public synchronized void setBeforeEndText(String beforeEndText) {
        this.beforeEndText = AbstractEntity.SINGLE_LINE_NORMALIZER.apply(beforeEndText);
        if (dateSpecOption != AlertDateOption.END_DATE) {
            return;
        }
        if (this.beforeEndText.isEmpty()) {
            beforeEndValue = null;
            alertDateLiveData.postValue(null);
            beforeEndValidationLiveData.postValue(ValidationMessage.ResourceMessageFactory.ofError(R.string.message_required));
        } else {
            try {
                int value = Objects.requireNonNull(NUMBER_FORMATTER.parse(this.beforeEndText)).intValue();
                if (value < AlertEntity.MIN_VALUE_RELATIVE_DAYS || value > AlertEntity.MAX_VALUE_RELATIVE_DAYS) {
                    beforeEndValidationLiveData.postValue(ValidationMessage.ResourceMessageFactory.ofError(R.string.message_relative_days_out_of_range));
                    beforeEndValue = null;
                } else {
                    beforeEndValue = value;
                }
            } catch (ParseException | NullPointerException e) {
                beforeEndValidationLiveData.postValue(ValidationMessage.ResourceMessageFactory.ofError(R.string.message_invalid_number));
                beforeEndValue = null;
            }
            if (null != beforeEndValue) {
                if (null != eventEnd) {
                    alertDateLiveData.postValue(eventEnd.minusDays(beforeEndValue));
                    beforeEndValidationLiveData.postValue(null);
                } else {
                    alertDateLiveData.postValue(null);
                    if (type == TYPE_VALUE_COURSE) {
                        beforeEndValidationLiveData.postValue(ValidationMessage.ResourceMessageFactory.ofWarning(R.string.message_course_has_no_end_date));
                    } else {
                        beforeEndValidationLiveData.postValue(ValidationMessage.ResourceMessageFactory.ofWarning(R.string.message_assessment_has_no_date));
                    }
                }
            } else {
                alertDateLiveData.postValue(null);
            }
        }
    }

    public LocalDate getSelectedDate() {
        return selectedDate;
    }

    public synchronized void setSelectedDate(LocalDate selectedDate) {
        this.selectedDate = selectedDate;
        if (dateSpecOption != AlertDateOption.EXPLICIT) {
            return;
        }
        alertDateLiveData.postValue(selectedDate);
        if (null == selectedDate) {
            selectedDateValidationLiveData.postValue(ValidationMessage.ResourceMessageFactory.ofError(R.string.message_required));
        } else {
            selectedDateValidationLiveData.postValue(null);
        }
    }

    public String getCode() {
        return (type == TYPE_VALUE_COURSE) ? courseEntity.getNumber() : assessmentEntity.getCode();
    }

    public String getTitle() {
        return (type == TYPE_VALUE_COURSE) ? courseEntity.getTitle() : assessmentEntity.getName();
    }

    public LiveData<AlertEntity> getAlertLiveData() {
        return alertLiveData;
    }

    public MutableLiveData<LocalDate> getAlertDateLiveData() {
        return alertDateLiveData;
    }

    public LiveData<ValidationMessage.ResourceMessageResult> getInitializationFailureLiveData() {
        return initializationFailureLiveData;
    }

    public MutableLiveData<ValidationMessage.ResourceMessageFactory> getBeforeStartValidationLiveData() {
        return beforeStartValidationLiveData;
    }

    public MutableLiveData<ValidationMessage.ResourceMessageFactory> getBeforeEndValidationLiveData() {
        return beforeEndValidationLiveData;
    }

    public MutableLiveData<ValidationMessage.ResourceMessageFactory> getSelectedDateValidationLiveData() {
        return selectedDateValidationLiveData;
    }

    public BooleanAndLiveData getValidLiveData() {
        return validLiveData;
    }

    public void initializeViewModelState(@Nullable Bundle savedInstanceState, Supplier<Bundle> getArguments) {
        boolean fromInitializedState = null != savedInstanceState && savedInstanceState.getBoolean(STATE_KEY_STATE_INITIALIZED, false);
        Bundle state = (fromInitializedState) ? savedInstanceState : getArguments.get();
        if (null == state) {
            throw new IllegalStateException();
        }
        if (fromInitializedState) {
            restoreViewModelState(state);
        } else if (state.containsKey(ARG_KEY_ALERT_ID)) {
            long alertId = state.getLong(ARG_KEY_ALERT_ID);
            if (state.containsKey(ARG_KEY_COURSE_ID)) {
                loadCourseAlert(alertId, state.getLong(ARG_KEY_COURSE_ID));
            } else if (state.containsKey(ARG_KEY_ASSESSMENT_ID)) {
                loadAssessmentAlert(alertId, state.getLong(ARG_KEY_ASSESSMENT_ID));
            } else {
                throw new IllegalStateException("Missing ID of entity related to event");
            }
        } else if (state.containsKey(ARG_KEY_COURSE_ID)) {
            initializeNewCourseAlert(state.getLong(ARG_KEY_COURSE_ID));
        } else if (state.containsKey(ARG_KEY_ASSESSMENT_ID)) {
            initializeNewAssessmentAlert(state.getLong(ARG_KEY_ASSESSMENT_ID));
        } else {
            throw new IllegalStateException("Missing ID of entity related to event");
        }
    }

    private void loadCourseAlert(long alertId, long courseId) {
        compositeDisposable.clear();
        compositeDisposable.add(dbLoader.getCourseAlertDetailsId(alertId, courseId).subscribe(this::onCourseAlertLoaded, this::onCourseAlertLoadFailed));
    }

    private void loadAssessmentAlert(long alertId, long assessmentId) {
        compositeDisposable.clear();
        compositeDisposable.add(dbLoader.getAssessmentAlertDetailsId(alertId, assessmentId).subscribe(this::onAssessmentAlertLoaded, this::onAssessmentAlertLoadFailed));
    }

    private void initializeNewCourseAlert(long courseId) {
        compositeDisposable.clear();
        compositeDisposable.add(dbLoader.getCourseById(courseId).subscribe(this::onCourseLoaded, this::onCourseLoadFailed));
    }

    private void initializeNewAssessmentAlert(long assessmentId) {
        compositeDisposable.clear();
        compositeDisposable.add(dbLoader.getAssessmentById(assessmentId).subscribe(this::onAssessmentLoaded, this::onAssessmentLoadFailed));
    }

    public synchronized void saveViewModelState(Bundle outState) {
        outState.putBoolean(STATE_KEY_STATE_INITIALIZED, true);
        if (null != eventStart) {
            outState.putLong(STATE_KEY_EVENT_START, eventStart.toEpochDay());
        }
        if (null != eventEnd) {
            outState.putLong(STATE_KEY_EVENT_END, eventEnd.toEpochDay());
        }
        outState.putInt(STATE_KEY_TYPE, type);
        outState.putInt(STATE_KEY_RELATIVITY, dateSpecOption.ordinal());
        outState.putString(STATE_KEY_MESSAGE, message);
        outState.putString(STATE_KEY_BEFORE_START_TEXT, beforeStartText);
        outState.putString(STATE_KEY_BEFORE_END_TEXT, beforeEndText);
        if (null != selectedDate) {
            outState.putLong(STATE_KEY_SELECTED_DATE, selectedDate.toEpochDay());
        }
        if (null != courseEntity) {
            courseEntity.saveState(outState, true);
        } else {
            assessmentEntity.saveState(outState, true);
        }
        alertEntity.saveState(outState, true);
    }

    private synchronized void restoreViewModelState(Bundle state) {
        type = state.getInt(STATE_KEY_TYPE, 0);
        alertEntity = new AlertEntity();
        if (type == TYPE_VALUE_COURSE) {
            courseEntity = new CourseEntity();
            courseEntity.restoreState(state, true);
        } else {
            assessmentEntity = new AssessmentEntity();
            assessmentEntity.restoreState(state, true);
        }
        alertEntity.restoreState(state, true);
        setDateSpecOption(AlertDateOption.values()[state.getInt(STATE_KEY_RELATIVITY, 0)]);
        setBeforeStartText(state.getString(STATE_KEY_BEFORE_START_TEXT, ""));
        setBeforeEndText(state.getString(STATE_KEY_BEFORE_END_TEXT, ""));
        if (state.containsKey(STATE_KEY_SELECTED_DATE)) {
            setSelectedDate(LocalDate.ofEpochDay(state.getLong(STATE_KEY_SELECTED_DATE)));
        }
        setMessage(state.getString(STATE_KEY_MESSAGE, ""));
        alertLiveData.postValue(alertEntity);
    }

    private void onCourseAlertLoaded(CourseAlertDetails courseAlertDetails) {
        courseEntity = courseAlertDetails.getCourse();
        alertEntity = courseAlertDetails.getAlert();
        initializeCourseAlert();
    }

    private void onCourseAlertLoadFailed(Throwable throwable) {
        Log.e(LOG_TAG, "Error loading course alert", throwable);
        initializationFailureLiveData.postValue(ValidationMessage.ofSingleError(R.string.format_message_read_error, throwable.toString()));
    }

    private void onAssessmentAlertLoaded(AssessmentAlertDetails assessmentAlertDetails) {
        assessmentEntity = assessmentAlertDetails.getAssessment();
        alertEntity = assessmentAlertDetails.getAlert();
        initializeAssessmentAlert();
    }

    private void onAssessmentAlertLoadFailed(Throwable throwable) {
        Log.e(LOG_TAG, "Error loading assessment alert", throwable);
        initializationFailureLiveData.postValue(ValidationMessage.ofSingleError(R.string.format_message_read_error, throwable.toString()));
    }

    private void onCourseLoaded(CourseDetails courseDetails) {
        courseEntity = new CourseEntity(courseDetails);
        alertEntity = new AlertEntity();
        initializeCourseAlert();
    }

    private void onCourseLoadFailed(Throwable throwable) {
        Log.e(LOG_TAG, "Error loading course", throwable);
        initializationFailureLiveData.postValue(ValidationMessage.ofSingleError(R.string.format_message_read_error, throwable.toString()));
    }

    private void onAssessmentLoaded(AssessmentDetails assessmentDetails) {
        assessmentEntity = new AssessmentEntity(assessmentDetails);
        alertEntity = new AlertEntity();
        initializeAssessmentAlert();
    }

    private void onAssessmentLoadFailed(Throwable throwable) {
        Log.e(LOG_TAG, "Error loading assessment", throwable);
        initializationFailureLiveData.postValue(ValidationMessage.ofSingleError(R.string.format_message_read_error, throwable.toString()));
    }

    private void initializeCourseAlert() {
        type = TYPE_VALUE_COURSE;
        eventStart = ComparisonHelper.firstNonNull(courseEntity.getActualStart(), courseEntity.getExpectedStart()).orElse(null);
        eventEnd = ComparisonHelper.firstNonNull(courseEntity.getActualEnd(), courseEntity.getExpectedEnd()).orElse(null);
        initializeAlert();
    }

    private void initializeAssessmentAlert() {
        type = assessmentEntity.getType().displayResourceId();
        eventStart = ComparisonHelper.firstNonNull(assessmentEntity.getCompletionDate(), assessmentEntity.getGoalDate()).orElse(null);
        eventEnd = eventStart;
        initializeAlert();
    }

    private void initializeAlert() {
        Boolean b = alertEntity.isSubsequent();
        setDateSpecOption((null == b) ? AlertDateOption.EXPLICIT : ((b) ? AlertDateOption.END_DATE : AlertDateOption.START_DATE));
        long days = alertEntity.getTimeSpec();
        switch (dateSpecOption) {
            case START_DATE:
                alertDateLiveData.postValue((null == eventStart) ? null : eventStart.plusDays(days));
                setBeforeStartText(NUMBER_FORMATTER.format(days));
                setBeforeEndText("");
                break;
            case END_DATE:
                alertDateLiveData.postValue((null == eventEnd) ? null : eventEnd.plusDays(days));
                setBeforeStartText("");
                setBeforeEndText(NUMBER_FORMATTER.format(days));
                break;
            default:
                setBeforeStartText("");
                setBeforeEndText("");
                setSelectedDate(LocalDate.ofEpochDay(days));
                break;
        }
        setMessage(alertEntity.getCustomMessage());
        alertLiveData.postValue(alertEntity);
    }

}
