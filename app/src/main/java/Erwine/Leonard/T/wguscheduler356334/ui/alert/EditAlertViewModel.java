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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

import Erwine.Leonard.T.wguscheduler356334.R;
import Erwine.Leonard.T.wguscheduler356334.db.DbLoader;
import Erwine.Leonard.T.wguscheduler356334.db.LocalDateConverter;
import Erwine.Leonard.T.wguscheduler356334.db.LocalTimeConverter;
import Erwine.Leonard.T.wguscheduler356334.entity.AbstractEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.alert.AlertEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.alert.AlertLink;
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
import Erwine.Leonard.T.wguscheduler356334.ui.course.EditCourseFragment;
import Erwine.Leonard.T.wguscheduler356334.util.BinaryAlternate;
import Erwine.Leonard.T.wguscheduler356334.util.BinaryOptional;
import Erwine.Leonard.T.wguscheduler356334.util.OneTimeObservers;
import Erwine.Leonard.T.wguscheduler356334.util.validation.ResourceMessageFactory;
import Erwine.Leonard.T.wguscheduler356334.util.validation.ResourceMessageResult;
import Erwine.Leonard.T.wguscheduler356334.util.validation.ValidationMessage;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.subjects.BehaviorSubject;

public class EditAlertViewModel extends AndroidViewModel {

    private static final String LOG_TAG = EditCourseFragment.class.getName();
    @StringRes
    public static final int TYPE_VALUE_COURSE = R.string.label_course;
    public static final NumberFormat NUMBER_FORMATTER = NumberFormat.getIntegerInstance();

    static final String ARG_KEY_ALERT_ID = "alert_id";
    static final String ARG_KEY_COURSE_ID = "course_id";
    static final String ARG_KEY_ASSESSMENT_ID = "assessment_id";
    private static final String STATE_KEY_STATE_INITIALIZED = "state_initialized";
    private static final String STATE_KEY_TYPE = "type";
    private static final String STATE_KEY_RELATIVITY = "relativity";
    private static final String STATE_KEY_MESSAGE = "message";
    private static final String STATE_KEY_DAYS_TEXT = "days_text";
    private static final String STATE_KEY_SELECTED_DATE = "selected_date";
    private static final String STATE_KEY_SELECTED_TIME = "selected_time";

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

    private final DbLoader dbLoader;
    private final BehaviorSubject<AlertEntity> alertEntitySubject;
    private final BehaviorSubject<Integer> typeResourceIdSubject;
    private final BehaviorSubject<Optional<LocalDate>> eventStartSubject;
    private final BehaviorSubject<Optional<LocalDate>> eventEndSubject;
    private final BehaviorSubject<Boolean> beforeEndAllowedSubject;
    private final BehaviorSubject<String> daysTextSubject;
    private final BehaviorSubject<Optional<LocalDate>> selectedDateSubject;
    private final BehaviorSubject<AlertDateOption> selectedOptionSubject;
    private final BehaviorSubject<Boolean> explicitTimeSubject;
    private final BehaviorSubject<Optional<LocalTime>> selectedTimeSubject;
    private final BehaviorSubject<String> customMessageTextSubject;
    @SuppressWarnings("FieldCanBeLocal") // Needs to be a field so it doesn't get garbage-collected
    private final CompositeDisposable compositeDisposable;
    private final MutableLiveData<ResourceMessageResult> initializationFailureLiveData;
    private final MutableLiveData<Boolean> validLiveData;
    private final MutableLiveData<Boolean> canSaveLiveData;
    private final MutableLiveData<Boolean> hasChangesLiveData;
    private final MutableLiveData<String> effectiveAlertDateStringLiveData;
    private final MutableLiveData<Optional<LocalDateTime>> effectiveAlertDateTimeLiveData;
    private final MutableLiveData<Optional<ResourceMessageFactory>> daysValidationMessageLiveData;
    private final MutableLiveData<Optional<ResourceMessageFactory>> selectedDateValidationMessageLiveData;
    private final MutableLiveData<AlertEntity> alertEntityLiveData;
    private BinaryAlternate<? extends CourseAlertDetails, ? extends AssessmentAlertDetails> target;
    @StringRes
    private int startLabelTextResourceId;
    @StringRes
    private int endLabelTextResourceId;
    @Nullable
    private Long timeSpec;

    public EditAlertViewModel(@NonNull Application application) {
        super(application);
        dbLoader = DbLoader.getInstance(getApplication());
        alertEntitySubject = BehaviorSubject.create();
        typeResourceIdSubject = BehaviorSubject.create();
        eventStartSubject = BehaviorSubject.create();
        eventEndSubject = BehaviorSubject.create();
        beforeEndAllowedSubject = BehaviorSubject.create();
        daysTextSubject = BehaviorSubject.create();
        selectedDateSubject = BehaviorSubject.create();
        selectedOptionSubject = BehaviorSubject.create();
        explicitTimeSubject = BehaviorSubject.create();
        selectedTimeSubject = BehaviorSubject.create();
        customMessageTextSubject = BehaviorSubject.create();
        BehaviorSubject<LocalTime> defaultEventTimeSubject = BehaviorSubject.create();

        typeResourceIdSubject.onNext(TYPE_VALUE_COURSE);
        eventStartSubject.onNext(Optional.empty());
        eventEndSubject.onNext(Optional.empty());
        defaultEventTimeSubject.onNext(LocalTime.MIDNIGHT);
        OneTimeObservers.observeOnce(DbLoader.getPreferAlertTime(), defaultEventTimeSubject::onNext);
        daysTextSubject.onNext("");
        selectedDateSubject.onNext(Optional.empty());
        selectedOptionSubject.onNext(AlertDateOption.EXPLICIT);
        explicitTimeSubject.onNext(false);
        selectedTimeSubject.onNext(Optional.empty());
        customMessageTextSubject.onNext("");

        initializationFailureLiveData = new MutableLiveData<>();
        validLiveData = new MutableLiveData<>(false);
        canSaveLiveData = new MutableLiveData<>(false);
        hasChangesLiveData = new MutableLiveData<>(false);
        daysValidationMessageLiveData = new MutableLiveData<>(Optional.empty());
        selectedDateValidationMessageLiveData = new MutableLiveData<>(Optional.empty());
        effectiveAlertDateStringLiveData = new MutableLiveData<>("");
        effectiveAlertDateTimeLiveData = new MutableLiveData<>(Optional.empty());
        alertEntityLiveData = new MutableLiveData<>();

        Observable<String> normalizedMessageObservable = customMessageTextSubject.map(AbstractEntity.SINGLE_LINE_NORMALIZER::apply);
        Observable<BinaryOptional<Integer, ResourceMessageFactory>> daysEditTextParseResultObservable = Observable.combineLatest(daysTextSubject, selectedOptionSubject,
                (text, selectedOption) -> {
                    if (selectedOption == AlertDateOption.EXPLICIT) {
                        return BinaryOptional.empty();
                    }
                    String d = AbstractEntity.SINGLE_LINE_NORMALIZER.apply(text);
                    if (d.isEmpty()) {
                        return BinaryOptional.ofSecondary(ResourceMessageFactory.ofError(R.string.message_required));
                    }
                    try {
                        int i = Integer.parseInt(d);
                        if (i < AlertEntity.MIN_VALUE_RELATIVE_DAYS || i > AlertEntity.MAX_VALUE_RELATIVE_DAYS) {
                            return BinaryOptional.ofSecondary(ResourceMessageFactory.ofError(R.string.message_relative_days_out_of_range));
                        }
                        return BinaryOptional.ofPrimary(i);
                    } catch (NumberFormatException ex) {
                        return BinaryOptional.ofSecondary(ResourceMessageFactory.ofError(R.string.format_days_parse_error, ex.getMessage()));
                    }
                });
        Observable<BinaryOptional<LocalDate, ResourceMessageFactory>> effectiveAlertDateObservable = Observable.combineLatest(daysEditTextParseResultObservable, selectedDateSubject, selectedOptionSubject,
                (daysEditTextParseResult, selectedDate, selectedOption) -> {
                    if (selectedOption == AlertDateOption.EXPLICIT) {
                        return selectedDate.<BinaryOptional<LocalDate, ResourceMessageFactory>>map(BinaryOptional::ofPrimary).orElseGet(() ->
                                BinaryOptional.ofSecondary(ResourceMessageFactory.ofError(R.string.message_required)));
                    }
                    if (daysEditTextParseResult.isPrimary()) {
                        switch (selectedOption) {
                            case BEFORE_START_DATE:
                                return BinaryOptional.ofPrimary(eventStartSubject.getValue().orElseThrow(IllegalStateException::new).minusDays(daysEditTextParseResult.getPrimary()));
                            case AFTER_START_DATE:
                                return BinaryOptional.ofPrimary(eventStartSubject.getValue().orElseThrow(IllegalStateException::new).plusDays(daysEditTextParseResult.getPrimary()));
                            case BEFORE_END_DATE:
                                if (!beforeEndAllowedSubject.getValue()) {
                                    return BinaryOptional.ofSecondary(ResourceMessageFactory.ofError(R.string.format_error, "Illegal option selection"));
                                }
                                return BinaryOptional.ofPrimary(eventEndSubject.getValue().orElseThrow(IllegalStateException::new).minusDays(daysEditTextParseResult.getPrimary()));
                            default:
                                return BinaryOptional.ofPrimary(eventEndSubject.getValue().orElseThrow(IllegalStateException::new).plusDays(daysEditTextParseResult.getPrimary()));
                        }
                    }
                    return BinaryOptional.empty();
                });
        Observable<Optional<LocalTime>> effectiveTimeObservable = Observable.combineLatest(defaultEventTimeSubject, explicitTimeSubject, selectedTimeSubject,
                (defaultEventTime, isExplicitTime, selectedTime) -> (isExplicitTime) ? selectedTime : Optional.of(defaultEventTime));
        Observable<Optional<Long>> timeSpecObservable = Observable.combineLatest(selectedOptionSubject, daysEditTextParseResultObservable, selectedDateSubject,
                (selectedOption, daysEditTextParseResult, selectedDate) -> {
                    Optional<Long> result = (selectedOption == AlertDateOption.EXPLICIT) ?
                            selectedDate.map(LocalDateConverter::fromLocalDate) :
                            daysEditTextParseResult.ofPrimary().map(i -> (selectedOption.isBefore()) ? -(long) i : (long) i);
                    timeSpec = result.orElse(null);
                    return result;
                });
        Observable<Boolean> changedObservable = Observable.combineLatest(selectedOptionSubject, timeSpecObservable, selectedDateSubject, selectedTimeSubject, normalizedMessageObservable, alertEntitySubject,
                (option, timeSpec, date, localTime, normalizedMessage, alertEntity) -> {
                    String customMessage = alertEntity.getCustomMessage();
                    return ((null == customMessage) ? normalizedMessage.isEmpty() : normalizedMessage.equals(customMessage)) ||
                            localTime.map(t -> !Objects.equals(t, alertEntity.getAlertTime())).orElse(true) ||
                            timeSpec.map(t -> alertEntity.getTimeSpec() != t || option != AlertDateOption.of(alertEntity.isSubsequent(), t)).orElse(true);
                });
        Observable<Boolean> validObservable = Observable.combineLatest(daysEditTextParseResultObservable, effectiveAlertDateObservable, effectiveTimeObservable,
                (daysEditTextParseResult, effectiveAlertDate, effectiveAlertTime) ->
                        !(daysEditTextParseResult.isSecondary() && effectiveAlertDate.isSecondary()) && effectiveAlertTime.isPresent());

        Observable<Boolean> canSaveObservable = Observable.combineLatest(changedObservable, validObservable, (c, v) -> c && v);
        compositeDisposable = new CompositeDisposable();
        compositeDisposable.add(daysEditTextParseResultObservable.subscribe(daysEditText -> daysEditText.switchPresence(
                days -> daysValidationMessageLiveData.postValue(Optional.empty()),
                resourceMessageFactory -> daysValidationMessageLiveData.postValue(Optional.of(resourceMessageFactory)),
                () -> daysValidationMessageLiveData.postValue(Optional.empty())
        ), throwable -> daysValidationMessageLiveData.postValue(Optional.of(ResourceMessageFactory.ofError(throwable)))));
        compositeDisposable.add(effectiveAlertDateObservable.subscribe(effectiveAlertDate -> effectiveAlertDate.switchPresence(
                localDate -> {
                    selectedDateValidationMessageLiveData.postValue(Optional.empty());
                    effectiveAlertDateStringLiveData.postValue(LocalDateConverter.MEDIUM_FORMATTER.format(localDate));
                },
                resourceMessageFactory -> {
                    selectedDateValidationMessageLiveData.postValue(Optional.of(resourceMessageFactory));
                    effectiveAlertDateStringLiveData.postValue("");
                },
                () -> {
                    selectedDateValidationMessageLiveData.postValue(Optional.empty());
                    effectiveAlertDateStringLiveData.postValue("");
                }
        ), throwable -> selectedDateValidationMessageLiveData.postValue(Optional.of(ResourceMessageFactory.ofError(throwable)))));
        compositeDisposable.add(effectiveTimeObservable.subscribe((Optional<LocalTime> localTime) -> {

        }, throwable -> {

        }));
        compositeDisposable.add(canSaveObservable.subscribe(canSaveLiveData::postValue));
        compositeDisposable.add(validObservable.subscribe(validLiveData::postValue));
        compositeDisposable.add(Observable.combineLatest(effectiveAlertDateObservable, effectiveTimeObservable, (effectiveAlertDate, effectiveTime) ->
                effectiveAlertDate.ofPrimary().flatMap(d -> effectiveTime.map(d::atTime))).subscribe(effectiveAlertDateTimeLiveData::postValue));
        compositeDisposable.add(alertEntitySubject.subscribe(alertEntity -> {
            alertEntityLiveData.postValue(alertEntity);
            timeSpec = alertEntity.getTimeSpec();
            AlertDateOption alertDateOption = AlertDateOption.of(alertEntity.isSubsequent(), timeSpec);
            setAlertDateOption(alertDateOption);
            if (alertDateOption == AlertDateOption.EXPLICIT) {
                selectedDateSubject.onNext(Optional.ofNullable(LocalDateConverter.toLocalDate(timeSpec)));
                daysTextSubject.onNext("");
            } else {
                daysTextSubject.onNext(NUMBER_FORMATTER.format(Math.abs(timeSpec)));
                selectedDateSubject.onNext(Optional.empty());
            }
            Optional<LocalTime> alertTime = Optional.ofNullable(alertEntity.getAlertTime());
            selectedTimeSubject.onNext(alertTime);
            explicitTimeSubject.onNext(alertTime.isPresent());
            String message = alertEntity.getCustomMessage();
            customMessageTextSubject.onNext((null == message) ? "" : message);
        }));
    }

    @StringRes
    public int getStartLabelTextResourceId() {
        return startLabelTextResourceId;
    }

    @StringRes
    public int getEndLabelTextResourceId() {
        return endLabelTextResourceId;
    }

    public AlertLink getAlertLink() {
        return target.flatMap(CourseAlert::getLink, AssessmentAlert::getLink);
    }

    public boolean isCourseAlert() {
        return target.isPrimary();
    }

    public int getNotificationId() {
        return alertEntitySubject.getValue().getNotificationId();
    }


    public boolean isBeforeEndAllowed() {
        return beforeEndAllowedSubject.getValue();
    }

    @NonNull
    public String getDaysText() {
        return daysTextSubject.getValue();
    }

    public void setDaysText(String text) {
        daysTextSubject.onNext((null == text) ? "" : text);
    }

    @NonNull
    public AlertDateOption getAlertDateOption() {
        return selectedOptionSubject.getValue();
    }

    void setAlertDateOption(@NonNull AlertDateOption alertDateOption) {
        selectedOptionSubject.onNext(alertDateOption);
    }

    @Nullable
    public Boolean isSubsequent() {
        AlertDateOption alertDateOption = selectedOptionSubject.getValue();
        if (alertDateOption.isExplicit()) {
            return null;
        }
        return alertDateOption.isAfter();
    }

    @Nullable
    public LocalDate getSelectedDate() {
        return (explicitTimeSubject.getValue()) ? null : selectedDateSubject.getValue().orElse(null);
    }

    public void setSelectedDate(@Nullable LocalDate date) {
        selectedDateSubject.onNext(Optional.ofNullable(date));
    }

    public boolean isExplicitTime() {
        return explicitTimeSubject.getValue();
    }

    public void setExplicitTime(boolean value) {
        explicitTimeSubject.onNext(value);
    }

    @Nullable
    public LocalTime getSelectedTime() {
        return (explicitTimeSubject.getValue()) ? selectedTimeSubject.getValue().orElse(null) : null;
    }

    public void setSelectedTime(@Nullable LocalTime alertTime) {
        selectedTimeSubject.onNext(Optional.ofNullable(alertTime));
    }

    @NonNull
    public String getCustomMessage() {
        return customMessageTextSubject.getValue();
    }

    public void setCustomMessage(String customMessage) {
        customMessageTextSubject.onNext((null == customMessage) ? "" : customMessage);
    }

    public LiveData<AlertEntity> getAlertEntityLiveData() {
        return alertEntityLiveData;
    }

    public LiveData<String> getEffectiveAlertDateStringLiveData() {
        return effectiveAlertDateStringLiveData;
    }

    public LiveData<Optional<LocalDateTime>> getEffectiveAlertDateTimeLiveData() {
        return effectiveAlertDateTimeLiveData;
    }

    public LiveData<Optional<ResourceMessageFactory>> getDaysValidationMessageLiveData() {
        return daysValidationMessageLiveData;
    }

    public LiveData<Optional<ResourceMessageFactory>> getSelectedDateValidationMessageLiveData() {
        return selectedDateValidationMessageLiveData;
    }

    public LiveData<Boolean> getCanSaveLiveData() {
        return canSaveLiveData;
    }

    public LiveData<Boolean> getHasChangesLiveData() {
        return hasChangesLiveData;
    }

    public LiveData<Boolean> getValidLiveData() {
        return validLiveData;
    }

    public LiveData<ResourceMessageResult> getInitializationFailureLiveData() {
        return initializationFailureLiveData;
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
        OneTimeObservers.subscribeOnce(dbLoader.getCourseAlertDetailsById(alertId, courseId), this::onCourseAlertLoaded, this::onCourseAlertLoadFailed);
    }

    private void loadAssessmentAlert(long alertId, long assessmentId) {
        OneTimeObservers.subscribeOnce(dbLoader.getAssessmentAlertDetailsById(alertId, assessmentId), this::onAssessmentAlertLoaded, this::onAssessmentAlertLoadFailed);
    }

    private void initializeNewCourseAlert(long courseId) {
        OneTimeObservers.subscribeOnce(dbLoader.getCourseById(courseId), this::onCourseLoaded, this::onCourseLoadFailed);
    }

    private void initializeNewAssessmentAlert(long assessmentId) {
        OneTimeObservers.subscribeOnce(dbLoader.getAssessmentById(assessmentId), this::onAssessmentLoaded, this::onAssessmentLoadFailed);
    }

    public synchronized void saveViewModelState(@NonNull Bundle outState) {
        outState.putBoolean(STATE_KEY_STATE_INITIALIZED, true);
        outState.putInt(STATE_KEY_RELATIVITY, selectedOptionSubject.getValue().ordinal());
        outState.putString(STATE_KEY_MESSAGE, customMessageTextSubject.getValue());
        outState.putString(STATE_KEY_DAYS_TEXT, daysTextSubject.getValue());
        selectedDateSubject.getValue().map(LocalDateConverter::fromLocalDate).ifPresent(d -> outState.putLong(STATE_KEY_SELECTED_DATE, d));
        selectedTimeSubject.getValue().map(LocalTimeConverter::fromLocalTime).ifPresent(t -> outState.putInt(STATE_KEY_SELECTED_TIME, t));
        target.switchPresence(courseAlertDetails -> courseAlertDetails.saveState(outState, true),
                assessmentAlertDetails -> assessmentAlertDetails.saveState(outState, true));
    }

    private synchronized void restoreViewModelState(@NonNull Bundle state) {
        int type = state.getInt(STATE_KEY_TYPE, 0);
        if (type == TYPE_VALUE_COURSE) {
            CourseAlertDetails courseAlertDetails = new CourseAlertDetails(new CourseAlertLink(), new AlertEntity(), new CourseEntity());
            courseAlertDetails.restoreState(state, true);
            onCourseAlertLoaded(courseAlertDetails);
        } else {
            AssessmentAlertDetails assessmentAlertDetails = new AssessmentAlertDetails(new AssessmentAlertLink(), new AlertEntity(), new AssessmentEntity());
            assessmentAlertDetails.restoreState(state, true);
            onAssessmentAlertLoaded(assessmentAlertDetails);
        }
        setAlertDateOption(AlertDateOption.values()[state.getInt(STATE_KEY_RELATIVITY, 0)]);
        setDaysText(state.getString(STATE_KEY_DAYS_TEXT, ""));
        if (state.containsKey(STATE_KEY_SELECTED_DATE)) {
            setSelectedDate(LocalDateConverter.toLocalDate(state.getLong(STATE_KEY_SELECTED_DATE)));
        }
        if (state.containsKey(STATE_KEY_SELECTED_TIME)) {
            setSelectedTime(LocalTimeConverter.toLocalTime(state.getInt(STATE_KEY_SELECTED_TIME)));
        }
        setCustomMessage(state.getString(STATE_KEY_MESSAGE, ""));
    }


    /**
     * Initializes validation for an existing course alert or when restoring from state or course has been saved.
     *
     * @param courseAlertDetails The {@link CourseAlertDetails} being edited.
     */
    synchronized void onCourseAlertLoaded(@NonNull CourseAlertDetails courseAlertDetails) {
        initializeAlert(BinaryAlternate.ofPrimary(courseAlertDetails));
//        alertEntity = courseAlertDetails.getAlert();
    }

    private void onCourseAlertLoadFailed(Throwable throwable) {
        Log.e(LOG_TAG, "Error loading course alert", throwable);
        initializationFailureLiveData.postValue(ValidationMessage.ofSingleError(R.string.format_message_read_error, throwable.toString()));
    }

    /**
     * Initializes validation for a new course alert.
     *
     * @param courseDetails The target {@link CourseDetails} object.
     */
    synchronized void onCourseLoaded(@NonNull CourseDetails courseDetails) {
        AlertEntity entity = new AlertEntity();
        initializeAlert(BinaryAlternate.ofPrimary(new CourseAlertDetails(new CourseAlertLink(entity.getId(), courseDetails.getId()), entity, new CourseEntity(courseDetails))));
    }

    private void onCourseLoadFailed(Throwable throwable) {
        Log.e(LOG_TAG, "Error loading course", throwable);
        initializationFailureLiveData.postValue(ValidationMessage.ofSingleError(R.string.format_message_read_error, throwable.toString()));
    }

    /**
     * Initializes validation for an existing assessment alert or when restoring from state.
     *
     * @param assessmentAlertDetails The {@link AssessmentAlertDetails} being edited.
     */
    synchronized void onAssessmentAlertLoaded(@NonNull AssessmentAlertDetails assessmentAlertDetails) {
        initializeAlert(BinaryAlternate.ofSecondary(assessmentAlertDetails));
    }

    private void onAssessmentAlertLoadFailed(Throwable throwable) {
        Log.e(LOG_TAG, "Error loading assessment alert", throwable);
        initializationFailureLiveData.postValue(ValidationMessage.ofSingleError(R.string.format_message_read_error, throwable.toString()));
    }

    /**
     * Initializes validation for a new course alert.
     *
     * @param assessmentDetails The target {@link CourseDetails} object.
     */
    synchronized void onAssessmentLoaded(@NonNull AssessmentDetails assessmentDetails) {
        AlertEntity entity = new AlertEntity();
        initializeAlert(BinaryAlternate.ofSecondary(new AssessmentAlertDetails(new AssessmentAlertLink(entity.getId(), assessmentDetails.getId()), entity, new AssessmentEntity(assessmentDetails))));
    }

    private void onAssessmentLoadFailed(Throwable throwable) {
        Log.e(LOG_TAG, "Error loading assessment", throwable);
        initializationFailureLiveData.postValue(ValidationMessage.ofSingleError(R.string.format_message_read_error, throwable.toString()));
    }

    private void initializeAlert(@NonNull BinaryAlternate<? extends CourseAlertDetails, ? extends AssessmentAlertDetails> target) {
        this.target = target;
        AlertEntity alertEntity = target.flatMap(courseAlertDetails -> {
            typeResourceIdSubject.onNext(TYPE_VALUE_COURSE);
            CourseEntity courseEntity = courseAlertDetails.getCourse();
            Optional<LocalDate> date = Optional.ofNullable(courseEntity.getActualStart());
            eventStartSubject.onNext((date.isPresent()) ? date : Optional.ofNullable(courseEntity.getExpectedStart()));
            date = Optional.ofNullable(courseEntity.getActualEnd());
            if (date.isPresent()) {
                startLabelTextResourceId = R.string.label_actual_start;
                eventEndSubject.onNext(date);
                beforeEndAllowedSubject.onNext(false);
            } else {
                startLabelTextResourceId = R.string.label_expected_start;
                eventEndSubject.onNext(Optional.ofNullable(courseEntity.getExpectedEnd()));
                beforeEndAllowedSubject.onNext(true);
            }
            return courseAlertDetails.getAlert();
        }, assessmentAlertDetails -> {
            AssessmentEntity assessmentEntity = assessmentAlertDetails.getAssessment();
            typeResourceIdSubject.onNext(assessmentEntity.getType().displayResourceId());
            startLabelTextResourceId = R.string.label_goal_date;
            endLabelTextResourceId = R.string.label_completion_date;
            beforeEndAllowedSubject.onNext(false);
            eventStartSubject.onNext(Optional.ofNullable(assessmentEntity.getGoalDate()));
            eventEndSubject.onNext(Optional.ofNullable(assessmentEntity.getCompletionDate()));
            return assessmentAlertDetails.getAlert();
        });
        alertEntitySubject.onNext(alertEntity);
    }

    public synchronized Single<ResourceMessageResult> save(boolean ignoreWarnings) {
        return target.flatMap(courseAlertDetails -> {
            CourseAlertDetails course = new CourseAlertDetails(courseAlertDetails);
            ResourceMessageResult validationMessage = onSave(course.getAlert());
            if (null != validationMessage) {
                return Single.just(validationMessage);
            }
            return dbLoader.saveCourseAlert(course, ignoreWarnings).doOnSuccess(m -> {
                if (m.isSucceeded()) {
                    onCourseAlertLoaded(course);
                }
            });
        }, assessmentAlertDetails -> {
            AssessmentAlertDetails assessment = new AssessmentAlertDetails(assessmentAlertDetails);
            ResourceMessageResult validationMessage = onSave(assessment.getAlert());
            if (null != validationMessage) {
                return Single.just(validationMessage);
            }
            return dbLoader.saveAssessmentAlert(assessment, ignoreWarnings).doOnSuccess(m -> {
                if (m.isSucceeded()) {
                    onAssessmentAlertLoaded(assessment);
                }
            });
        });
    }

    @Nullable
    private ResourceMessageResult onSave(@NonNull AlertEntity entity) {
        entity.setSubsequent(isSubsequent());
        AlertDateOption dateSpecOption = selectedOptionSubject.getValue();
        if (null == timeSpec) {
            return ValidationMessage.ofSingleError((dateSpecOption.isExplicit()) ? R.string.message_alert_date_required : R.string.message_alert_days_required);
        }
        entity.setTimeSpec(timeSpec);
        String customMessageText = customMessageTextSubject.getValue();
        entity.setCustomMessage((customMessageText.isEmpty()) ? null : customMessageText);
        entity.setAlertTime(selectedTimeSubject.getValue().orElse(null));
        return null;
    }

    public synchronized Completable delete() {
        return target.flatMap(dbLoader::deleteCourseAlert, dbLoader::deleteAssessmentAlert);
    }

}
