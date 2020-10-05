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
import Erwine.Leonard.T.wguscheduler356334.entity.assessment.AssessmentAlert;
import Erwine.Leonard.T.wguscheduler356334.entity.assessment.AssessmentAlertDetails;
import Erwine.Leonard.T.wguscheduler356334.entity.assessment.AssessmentAlertLink;
import Erwine.Leonard.T.wguscheduler356334.entity.assessment.AssessmentDetails;
import Erwine.Leonard.T.wguscheduler356334.entity.assessment.AssessmentEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.course.CourseAlert;
import Erwine.Leonard.T.wguscheduler356334.entity.course.CourseAlertDetails;
import Erwine.Leonard.T.wguscheduler356334.entity.course.CourseAlertLink;
import Erwine.Leonard.T.wguscheduler356334.entity.course.CourseDetails;
import Erwine.Leonard.T.wguscheduler356334.entity.course.CourseEntity;
import Erwine.Leonard.T.wguscheduler356334.ui.course.CoursePropertiesFragment;
import Erwine.Leonard.T.wguscheduler356334.util.ValidationMessage;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
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
    private static final String STATE_KEY_DAYS_TEXT = "days_text";
    private static final String STATE_KEY_SELECTED_DATE = "selected_date";
    private static final String STATE_KEY_EVENT_START = "event_start";
    private static final String STATE_KEY_EVENT_END = "event_end";
    private static final String STATE_KEY_TYPE = "type";
    @StringRes
    private static final int TYPE_VALUE_COURSE = R.string.label_course;
    private LocalDate calculatedDate;

    public static EditAlertDialog existingCourseAlertEditor(long alertId, long courseId) {
        Bundle args = new Bundle();
        args.putLong(ARG_KEY_ALERT_ID, alertId);
        args.putLong(ARG_KEY_COURSE_ID, courseId);
        EditAlertDialog dialog = new EditAlertDialog();
        dialog.setArguments(args);
        return dialog;
    }

    public static EditAlertDialog existingAssessmentAlertEditor(long alertId, long assessmentId) {
        Bundle args = new Bundle();
        args.putLong(ARG_KEY_ALERT_ID, alertId);
        args.putLong(ARG_KEY_ASSESSMENT_ID, assessmentId);
        EditAlertDialog dialog = new EditAlertDialog();
        dialog.setArguments(args);
        return dialog;
    }

    public static EditAlertDialog newCourseAlert(long courseId) {
        Bundle args = new Bundle();
        args.putLong(ARG_KEY_COURSE_ID, courseId);
        EditAlertDialog dialog = new EditAlertDialog();
        dialog.setArguments(args);
        return dialog;
    }

    public static EditAlertDialog newAssessmentAlert(long assessmentId) {
        Bundle args = new Bundle();
        args.putLong(ARG_KEY_ASSESSMENT_ID, assessmentId);
        EditAlertDialog dialog = new EditAlertDialog();
        dialog.setArguments(args);
        return dialog;
    }

    private final MutableLiveData<AlertEntity> alertEntityLiveData;
    private final MutableLiveData<ValidationMessage.ResourceMessageResult> initializationFailureLiveData;
    private final MutableLiveData<String> eventDateLiveData;
    private final MutableLiveData<String> calculatedDateLiveData;
    private final IsValid validLiveData;
    private final CompositeDisposable compositeDisposable;
    private final DbLoader dbLoader;
    private CourseEntity courseEntity;
    private AssessmentEntity assessmentEntity;
    private AlertEntity alertEntity;
    private int type;
    private AlertDateOption dateSpecOption = AlertDateOption.EXPLICIT;
    @StringRes
    private int startLabelTextResourceId;
    @StringRes
    private int endLabelTextResourceId;
    private boolean beforeEndEnabled;
    private LocalDate eventStart;
    private LocalDate eventEnd;
    @NonNull
    private String message = "";
    private Integer daysValue;
    @NonNull
    private String daysText = "";
    private LocalDate selectedDate;

    @StringRes
    public int getStartLabelTextResourceId() {
        return startLabelTextResourceId;
    }

    @StringRes
    public int getEndLabelTextResourceId() {
        return endLabelTextResourceId;
    }

    public EditAlertViewModel(@NonNull Application application) {
        super(application);
        compositeDisposable = new CompositeDisposable();
        dbLoader = DbLoader.getInstance(getApplication());
        eventDateLiveData = new MutableLiveData<>();
        calculatedDateLiveData = new MutableLiveData<>();
        validLiveData = new IsValid();
        alertEntityLiveData = new MutableLiveData<>();
        initializationFailureLiveData = new MutableLiveData<>();
    }

    public AlertDateOption getDateSpecOption() {
        return dateSpecOption;
    }

    private synchronized void calculateAlertDate() {
        Log.d(LOG_TAG, "Enter calculateAlertDate");
        if (dateSpecOption == AlertDateOption.EXPLICIT) {
            if (null == selectedDate) {
                Log.d(LOG_TAG, "calculateAlertDate: Posting message_required to selectedDateValidationLiveData");
                validLiveData.postSelectedDateMessage(R.string.message_required);
                Log.d(LOG_TAG, "calculateAlertDate: Posting null to calculatedDateLiveData");
                calculatedDateLiveData.postValue(null);
            } else {
                Log.d(LOG_TAG, "calculateAlertDate: Posting " + DATE_FORMATTER.format(selectedDate) + " to calculatedDateLiveData");
                calculatedDateLiveData.postValue(DATE_FORMATTER.format(selectedDate));
                Log.d(LOG_TAG, "calculateAlertDate: Posting null to selectedDateValidationLiveData");
                validLiveData.clearSelectedDateMessage();
            }
            Log.d(LOG_TAG, "calculateAlertDate: Posting null to daysValidationLiveData");
            validLiveData.clearDaysMessage();
        } else {
            if (null == daysValue) {
                Log.d(LOG_TAG, "calculateAlertDate: Posting null to calculatedDateLiveData");
                calculatedDateLiveData.postValue(null);
                calculatedDate = null;
                Log.d(LOG_TAG, "calculateAlertDate: Posting " + ((daysText.isEmpty()) ? "message_required" : "message_invalid_number") + " to daysValidationLiveData");
                validLiveData.postSelectedDateMessage((daysText.isEmpty()) ? R.string.message_required : R.string.message_invalid_number);
            } else if (daysValue < AlertEntity.MIN_VALUE_RELATIVE_DAYS || daysValue > AlertEntity.MAX_VALUE_RELATIVE_DAYS) {
                Log.d(LOG_TAG, "calculateAlertDate: Posting null to message_relative_days_out_of_range");
                validLiveData.postDaysMessage(R.string.message_relative_days_out_of_range);
                Log.d(LOG_TAG, "calculateAlertDate: Posting null to calculatedDateLiveData");
                calculatedDateLiveData.postValue(null);
                calculatedDate = null;
            } else {
                LocalDate date = (dateSpecOption.isStart()) ? eventStart : eventEnd;
                if (null != date) {
                    calculatedDate = (dateSpecOption.isBefore()) ? date.minusDays(daysValue) : date.plusDays(daysValue);
                    Log.d(LOG_TAG, "calculateAlertDate: Posting " + DATE_FORMATTER.format(date) + " to calculatedDateLiveData");
                    calculatedDateLiveData.postValue(DATE_FORMATTER.format(date));
                    Log.d(LOG_TAG, "calculateAlertDate: Posting null to daysValidationLiveData");
                    validLiveData.clearDaysMessage();
                } else {
                    calculatedDate = null;
                    Log.d(LOG_TAG, "calculateAlertDate: Posting null to calculatedDateLiveData");
                    calculatedDateLiveData.postValue(null);
                    if (type == TYPE_VALUE_COURSE) {
                        Log.d(LOG_TAG, "calculateAlertDate: Posting " + ((dateSpecOption.isStart()) ? "message_course_has_no_start_date" : "message_course_has_no_end_date") + " to daysValidationLiveData");
                        validLiveData.postDaysMessage((dateSpecOption.isStart()) ? R.string.message_course_has_no_start_date : R.string.message_course_has_no_end_date);
                    } else {
                        Log.d(LOG_TAG, "calculateAlertDate: Posting " + ((dateSpecOption.isStart()) ? "message_assessment_has_no_goal_date" : "message_assessment_has_no_completion_date") + " to daysValidationLiveData");
                        validLiveData.postDaysMessage((dateSpecOption.isStart()) ? R.string.message_assessment_has_no_goal_date : R.string.message_assessment_has_no_completion_date);
                    }
                }
            }
            Log.d(LOG_TAG, "calculateAlertDate: Posting null to selectedDateValidationLiveData");
            validLiveData.clearSelectedDateMessage();
        }
    }

    public synchronized void setDateSpecOption(AlertDateOption dateSpecOption) {
        AlertDateOption newValue = (null == dateSpecOption) ? AlertDateOption.EXPLICIT : dateSpecOption;
        if (this.dateSpecOption != newValue) {
            Log.d(LOG_TAG, "setDateSpecOption: Changing from " + this.dateSpecOption.name() + " to " + newValue.name());
            if (newValue == AlertDateOption.BEFORE_END_DATE && !beforeEndEnabled) {
                throw new IllegalStateException();
            }
            AlertDateOption oldValue = this.dateSpecOption;
            this.dateSpecOption = newValue;
            if (oldValue == AlertDateOption.EXPLICIT) {
                parseDaysText();
            } else {
                calculateAlertDate();
            }
        }
    }

    public boolean isBeforeEndEnabled() {
        return beforeEndEnabled;
    }

    @NonNull
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = AbstractEntity.SINGLE_LINE_NORMALIZER.apply(message);
    }

    @NonNull
    public String getDaysText() {
        return daysText;
    }

    public LocalDate getCalculatedDate() {
        return calculatedDate;
    }

    private void parseDaysText() {
        if (this.daysText.isEmpty()) {
            daysValue = null;
        } else {
            try {
                daysValue = Objects.requireNonNull(NUMBER_FORMATTER.parse(this.daysText)).intValue();
            } catch (ParseException | NullPointerException e) {
                daysValue = null;
            }
        }
        calculateAlertDate();
    }

    public synchronized void setDaysText(String daysText) {
        String olValue = this.daysText;
        this.daysText = AbstractEntity.SINGLE_LINE_NORMALIZER.apply(daysText);
        if (dateSpecOption != AlertDateOption.EXPLICIT && !this.daysText.equals(olValue)) {
            Log.d(LOG_TAG, "setDaysText: Changing from \"" + olValue + "\" to \"" + this.daysText + "\"");
            parseDaysText();
        }
    }

    public LocalDate getSelectedDate() {
        return selectedDate;
    }

    public synchronized void setSelectedDate(LocalDate selectedDate) {
        if (Objects.equals(this.selectedDate, selectedDate)) {
            return;
        }
        if (dateSpecOption == AlertDateOption.EXPLICIT) {
            this.selectedDate = selectedDate;
            return;
        }
        Log.d(LOG_TAG, "setDateSpecOption: Changing from " + this.selectedDate + " to " + selectedDate);
        this.selectedDate = selectedDate;
        if (null == selectedDate) {
            Log.d(LOG_TAG, "setSelectedDate: Posting message_required to selectedDateValidationLiveData");
            validLiveData.postSelectedDateMessage(R.string.message_required);
            Log.d(LOG_TAG, "setSelectedDate: Posting null to calculatedDateLiveData");
            calculatedDateLiveData.postValue(null);
        } else {
            Log.d(LOG_TAG, "setSelectedDate: Posting " + DATE_FORMATTER.format(selectedDate) + " to calculatedDateLiveData");
            calculatedDateLiveData.postValue(DATE_FORMATTER.format(selectedDate));
            Log.d(LOG_TAG, "setSelectedDate: Posting null to selectedDateValidationLiveData");
            validLiveData.clearSelectedDateMessage();
        }
        Log.d(LOG_TAG, "setSelectedDate: Posting null to daysValidationLiveData");
        validLiveData.clearDaysMessage();
    }

    public LiveData<AlertEntity> getAlertEntityLiveData() {
        return alertEntityLiveData;
    }

    public LiveData<String> getCalculatedDateLiveData() {
        return calculatedDateLiveData;
    }

    public LiveData<ValidationMessage.ResourceMessageResult> getInitializationFailureLiveData() {
        return initializationFailureLiveData;
    }

    public LiveData<ValidationMessage.ResourceMessageFactory> getDaysValidationLiveData() {
        return validLiveData.daysValidationLiveData;
    }

    public LiveData<ValidationMessage.ResourceMessageFactory> getSelectedDateValidationLiveData() {
        return validLiveData.selectedDateValidationLiveData;
    }

    public LiveData<String> getEventDateLiveData() {
        return eventDateLiveData;
    }

    public LiveData<Boolean> getValidLiveData() {
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
        outState.putString(STATE_KEY_DAYS_TEXT, daysText);
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
        setDaysText(state.getString(STATE_KEY_DAYS_TEXT, ""));
        if (state.containsKey(STATE_KEY_SELECTED_DATE)) {
            setSelectedDate(LocalDate.ofEpochDay(state.getLong(STATE_KEY_SELECTED_DATE)));
        }
        setMessage(state.getString(STATE_KEY_MESSAGE, ""));
        alertEntityLiveData.postValue(alertEntity);
    }

    private synchronized void onCourseAlertLoaded(CourseAlertDetails courseAlertDetails) {
        courseEntity = courseAlertDetails.getCourse();
        assessmentEntity = null;
        alertEntity = courseAlertDetails.getAlert();
        initializeCourseAlert();
    }

    private void onCourseAlertLoadFailed(Throwable throwable) {
        Log.e(LOG_TAG, "Error loading course alert", throwable);
        initializationFailureLiveData.postValue(ValidationMessage.ofSingleError(R.string.format_message_read_error, throwable.toString()));
    }

    private synchronized void onAssessmentAlertLoaded(AssessmentAlertDetails assessmentAlertDetails) {
        assessmentEntity = assessmentAlertDetails.getAssessment();
        alertEntity = assessmentAlertDetails.getAlert();
        courseEntity = null;
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
        eventStart = courseEntity.getActualStart();
        if (null == eventStart) {
            eventStart = courseEntity.getExpectedStart();
            startLabelTextResourceId = R.string.label_expected_start;
        } else {
            startLabelTextResourceId = R.string.label_actual_start;
        }
        eventEnd = courseEntity.getActualEnd();
        if (null == eventEnd) {
            beforeEndEnabled = true;
            eventEnd = courseEntity.getExpectedEnd();
            endLabelTextResourceId = R.string.label_expected_end;
        } else {
            beforeEndEnabled = false;
            endLabelTextResourceId = R.string.label_actual_end;
        }
        initializeAlert();
    }

    private void initializeAssessmentAlert() {
        type = assessmentEntity.getType().displayResourceId();
        startLabelTextResourceId = R.string.label_goal_date;
        endLabelTextResourceId = R.string.label_completion_date;
        beforeEndEnabled = false;
        eventStart = assessmentEntity.getGoalDate();
        eventEnd = assessmentEntity.getCompletionDate();
        initializeAlert();
    }

    private void initializeAlert() {
        Boolean b = alertEntity.isSubsequent();
        long days = alertEntity.getTimeSpec();
        if (null == b) {
            setDateSpecOption(AlertDateOption.EXPLICIT);
            setDaysText("");
            setSelectedDate(LocalDate.ofEpochDay(days));
        } else if (days < 0) {
            setDateSpecOption((b && beforeEndEnabled) ? AlertDateOption.BEFORE_END_DATE : AlertDateOption.BEFORE_START_DATE);
            setDaysText(NUMBER_FORMATTER.format(Math.abs(days)));
        } else {
            setDateSpecOption((b) ? AlertDateOption.AFTER_END_DATE : AlertDateOption.AFTER_START_DATE);
            setDaysText(NUMBER_FORMATTER.format(days));
        }
        setMessage(alertEntity.getCustomMessage());
        alertEntityLiveData.postValue(alertEntity);
    }

    public synchronized Single<ValidationMessage.ResourceMessageResult> save(boolean ignoreWarnings) {
        AlertEntity entity = new AlertEntity(alertEntity);
        if (dateSpecOption.isExplicit()) {
            entity.setSubsequent(null);
            if (null == selectedDate) {
                return Single.just(ValidationMessage.ofSingleError(R.string.message_alert_date_required)).observeOn(AndroidSchedulers.mainThread());
            }
            entity.setTimeSpec(selectedDate.toEpochDay());
        } else {
            entity.setSubsequent(dateSpecOption.isEnd());
            if (null == daysValue) {
                return Single.just(ValidationMessage.ofSingleError(R.string.message_alert_days_required)).observeOn(AndroidSchedulers.mainThread());
            }
            entity.setTimeSpec(daysValue);
        }
        entity.setCustomMessage((message.isEmpty()) ? null : message);
        if (null == assessmentEntity) {
            CourseAlert courseAlert = new CourseAlert(new CourseAlertLink(alertEntity.getId(), courseEntity.getId()), entity);
            return dbLoader.saveCourseAlert(courseAlert, ignoreWarnings);
        }
        AssessmentAlert assessmentAlert = new AssessmentAlert(new AssessmentAlertLink(alertEntity.getId(), courseEntity.getId()), entity);
        return dbLoader.saveAssessmentAlert(assessmentAlert, ignoreWarnings);
    }

    public synchronized Completable delete() {
        if (null == assessmentEntity) {
            CourseAlert courseAlert = new CourseAlert(new CourseAlertLink(alertEntity.getId(), courseEntity.getId()), new AlertEntity(alertEntity));
            return dbLoader.deleteCourseAlert(courseAlert);
        }
        AssessmentAlert assessmentAlert = new AssessmentAlert(new AssessmentAlertLink(alertEntity.getId(), courseEntity.getId()), new AlertEntity(alertEntity));
        return dbLoader.deleteAssessmentAlert(assessmentAlert);
    }

    private static class IsValid extends LiveData<Boolean> {

        private final MutableLiveData<ValidationMessage.ResourceMessageFactory> daysValidationLiveData;
        private final MutableLiveData<ValidationMessage.ResourceMessageFactory> selectedDateValidationLiveData;

        private IsValid() {
            super(true);
            daysValidationLiveData = new MutableLiveData<>();
            selectedDateValidationLiveData = new MutableLiveData<>();
        }

        void clearSelectedDateMessage() {
            selectedDateValidationLiveData.postValue(null);
            ValidationMessage.ResourceMessageFactory m = daysValidationLiveData.getValue();
            postValue(null == m || m.isWarning());
        }

        void postSelectedDateMessage(@StringRes int id) {
            selectedDateValidationLiveData.postValue(ValidationMessage.ResourceMessageFactory.ofError(id));
            postValue(false);
        }

        void clearDaysMessage() {
            daysValidationLiveData.postValue(null);
            ValidationMessage.ResourceMessageFactory m = selectedDateValidationLiveData.getValue();
            postValue(null == m || m.isWarning());
        }

        public void postDaysMessage(@StringRes int id) {
            daysValidationLiveData.postValue(ValidationMessage.ResourceMessageFactory.ofError(id));
            postValue(false);
        }
    }
}
