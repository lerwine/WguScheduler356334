package Erwine.Leonard.T.wguscheduler356334.ui.alert;

import android.app.Application;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
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
import Erwine.Leonard.T.wguscheduler356334.entity.assessment.AssessmentDetails;
import Erwine.Leonard.T.wguscheduler356334.entity.assessment.AssessmentEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.course.CourseAlert;
import Erwine.Leonard.T.wguscheduler356334.entity.course.CourseAlertDetails;
import Erwine.Leonard.T.wguscheduler356334.entity.course.CourseDetails;
import Erwine.Leonard.T.wguscheduler356334.entity.course.CourseEntity;
import Erwine.Leonard.T.wguscheduler356334.ui.course.EditCourseFragment;
import Erwine.Leonard.T.wguscheduler356334.util.BinaryAlternate;
import Erwine.Leonard.T.wguscheduler356334.util.BinaryOptional;
import Erwine.Leonard.T.wguscheduler356334.util.ComparisonHelper;
import Erwine.Leonard.T.wguscheduler356334.util.OneTimeObservers;
import Erwine.Leonard.T.wguscheduler356334.util.Workers;
import Erwine.Leonard.T.wguscheduler356334.util.validation.ResourceMessageFactory;
import Erwine.Leonard.T.wguscheduler356334.util.validation.ResourceMessageResult;
import Erwine.Leonard.T.wguscheduler356334.util.validation.ValidationMessage;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.subjects.BehaviorSubject;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class EditAlertViewModel extends AndroidViewModel {

    private static final String LOG_TAG = EditCourseFragment.class.getName();
    @StringRes
    public static final int TYPE_VALUE_COURSE = R.string.label_course;
    public static final NumberFormat NUMBER_FORMATTER = NumberFormat.getIntegerInstance();
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.LONG).withZone(ZoneId.systemDefault());

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
    private final BehaviorSubject<Boolean> beforeEndAllowedSubject;
    private final BehaviorSubject<String> daysTextSubject;
    private final BehaviorSubject<Optional<LocalDate>> selectedDateSubject;
    private final BehaviorSubject<AlertDateOption> selectedOptionSubject;
    private final BehaviorSubject<Boolean> explicitTimeSubject;
    private final BehaviorSubject<Optional<LocalTime>> selectedTimeSubject;
    private final BehaviorSubject<String> customMessageTextSubject;
    @SuppressWarnings("FieldCanBeLocal") // Needs to be a field so it doesn't get garbage-collected
    private final BehaviorSubject<LocalTime> defaultEventTimeSubject;
    @SuppressWarnings("FieldCanBeLocal") // Needs to be a field so it doesn't get garbage-collected
    private final CompositeDisposable compositeDisposable;
    private final PrivateLiveData<ResourceMessageResult> initializationFailureLiveData;
    private final PrivateLiveData<Boolean> validLiveData;
    private final PrivateLiveData<Boolean> canSaveLiveData;
    private final PrivateLiveData<Boolean> hasChangesLiveData;
    private final PrivateLiveData<String> eventDateStringLiveData;
    private final PrivateLiveData<String> effectiveTimeStringLiveData;
    private final PrivateLiveData<String> effectiveAlertDateTimeStringLiveData;
    private final PrivateLiveData<Optional<LocalDateTime>> effectiveAlertDateTimeValueLiveData;
    private final PrivateLiveData<Optional<ResourceMessageFactory>> daysValidationMessageLiveData;
    private final PrivateLiveData<Optional<ResourceMessageFactory>> selectedDateValidationMessageLiveData;
    private final PrivateLiveData<AlertEntity> alertEntityLiveData;
    private BinaryAlternate<? extends CourseAlertDetails, ? extends AssessmentAlertDetails> target;
    @StringRes
    private volatile int startLabelTextResourceId;
    @StringRes
    private volatile int endLabelTextResourceId;
    @Nullable
    private volatile Long timeSpec;

    public EditAlertViewModel(@NonNull Application application) {
        super(application);
        dbLoader = DbLoader.getInstance(getApplication());
        alertEntitySubject = BehaviorSubject.create();
        typeResourceIdSubject = BehaviorSubject.createDefault(TYPE_VALUE_COURSE);
        beforeEndAllowedSubject = BehaviorSubject.createDefault(true);
        daysTextSubject = BehaviorSubject.createDefault("");
        selectedDateSubject = BehaviorSubject.createDefault(Optional.empty());
        selectedOptionSubject = BehaviorSubject.createDefault(AlertDateOption.EXPLICIT);
        explicitTimeSubject = BehaviorSubject.createDefault(false);
        selectedTimeSubject = BehaviorSubject.createDefault(Optional.empty());
        customMessageTextSubject = BehaviorSubject.createDefault("");
        defaultEventTimeSubject = BehaviorSubject.createDefault(LocalTime.MIDNIGHT);

        initializationFailureLiveData = new PrivateLiveData<>();
        validLiveData = new PrivateLiveData<>(false);
        canSaveLiveData = new PrivateLiveData<>(false);
        hasChangesLiveData = new PrivateLiveData<>(false);
        daysValidationMessageLiveData = new PrivateLiveData<>(Optional.empty());
        selectedDateValidationMessageLiveData = new PrivateLiveData<>(Optional.empty());
        eventDateStringLiveData = new PrivateLiveData<>("");
        effectiveTimeStringLiveData = new PrivateLiveData<>("");
        effectiveAlertDateTimeStringLiveData = new PrivateLiveData<>("");
        effectiveAlertDateTimeValueLiveData = new PrivateLiveData<>(Optional.empty());
        alertEntityLiveData = new PrivateLiveData<>();

        Observable<AlertDateOption> selectedOptionObservable = selectedOptionSubject.subscribeOn(Workers.getScheduler()).observeOn(Workers.getScheduler());
        Observable<String> normalizedMessageObservable = customMessageTextSubject.subscribeOn(Workers.getScheduler()).observeOn(Workers.getScheduler()).map(Workers.asCached(AbstractEntity.SINGLE_LINE_NORMALIZER::apply));
        Observable<BinaryOptional<Integer, ResourceMessageFactory>> daysEditTextParseResultObservable = Observable.combineLatest(
                daysTextSubject.subscribeOn(Workers.getScheduler()).observeOn(Workers.getScheduler()),
                selectedOptionObservable,
                this::calculateDaysEditTextParseResult
        );
        Observable<Optional<LocalDate>> eventDateObservable = selectedOptionObservable.map(Workers.asCached(this::calculateEventDate));
        Observable<Optional<LocalDate>> selectedDateObservable = selectedDateSubject.subscribeOn(Workers.getScheduler()).observeOn(Workers.getScheduler());
        Observable<Optional<LocalTime>> selectedTimeObservable = selectedTimeSubject.subscribeOn(Workers.getScheduler()).observeOn(Workers.getScheduler());
        Observable<BinaryOptional<LocalDate, ResourceMessageFactory>> effectiveAlertDateObservable = Observable.combineLatest(
                daysEditTextParseResultObservable,
                selectedDateObservable,
                selectedOptionObservable,
                eventDateObservable,
                beforeEndAllowedSubject.subscribeOn(Workers.getScheduler()).observeOn(Workers.getScheduler()),
                Workers.asCached(this::calculateEffectiveAlertDate)
        );
        Observable<Optional<LocalTime>> effectiveTimeObservable = Observable.combineLatest(
                defaultEventTimeSubject.subscribeOn(Workers.getScheduler()).observeOn(Workers.getScheduler()),
                explicitTimeSubject.subscribeOn(Workers.getScheduler()).observeOn(Workers.getScheduler()),
                selectedTimeObservable,
                Workers.asCached((defaultEventTime, isExplicitTime, selectedTime) -> (isExplicitTime) ? selectedTime : Optional.of(defaultEventTime))
        );
        Observable<Optional<Long>> timeSpecObservable = Observable.combineLatest(selectedOptionObservable, daysEditTextParseResultObservable, selectedDateObservable,
                Workers.asCached(this::calculateTimeSpec));
        Observable<AlertEntity> alertEntityObservable = alertEntitySubject.subscribeOn(Workers.getScheduler()).observeOn(Workers.getScheduler());
        Observable<Boolean> changedObservable = Observable.combineLatest(
                selectedOptionObservable,
                timeSpecObservable,
                selectedDateObservable,
                selectedTimeObservable,
                normalizedMessageObservable,
                alertEntityObservable,
                Workers.asCached(this::calculateChanged)
        );
        Observable<Boolean> validObservable = Observable.combineLatest(daysEditTextParseResultObservable, effectiveAlertDateObservable, effectiveTimeObservable,
                Workers.asCached((daysEditTextParseResult, effectiveAlertDate, effectiveAlertTime) ->
                        !(daysEditTextParseResult.isSecondary() && effectiveAlertDate.isSecondary()) && effectiveAlertTime.isPresent()));

        Observable<Boolean> canSaveObservable = Observable.combineLatest(changedObservable, validObservable, (c, v) -> c && v);
        compositeDisposable = new CompositeDisposable();
        compositeDisposable.add(daysEditTextParseResultObservable.subscribe(this::onDaysEditTextChanged, throwable -> daysValidationMessageLiveData.postValue(Optional.of(ResourceMessageFactory.ofError(throwable)))));
        compositeDisposable.add(effectiveAlertDateObservable.subscribe(this::onEffectiveAlertDateChanged,
                throwable -> selectedDateValidationMessageLiveData.postValue(Optional.of(ResourceMessageFactory.ofError(throwable)))));
        compositeDisposable.add(canSaveObservable.subscribe(canSaveLiveData::postValue));
        compositeDisposable.add(eventDateObservable.subscribe(e ->
                eventDateStringLiveData.postValue(e.map(LocalDateConverter.MEDIUM_FORMATTER::format).orElse(""))
        ));
        compositeDisposable.add(effectiveTimeObservable.subscribe(t ->
                effectiveTimeStringLiveData.postValue(t.map(LocalTimeConverter.MEDIUM_FORMATTER::format).orElse(""))));
        compositeDisposable.add(validObservable.subscribe(validLiveData::postValue));
        compositeDisposable.add(Observable.combineLatest(effectiveAlertDateObservable, effectiveTimeObservable, Workers.asCached((effectiveAlertDate, effectiveTime) ->
                effectiveAlertDate.ofPrimary().flatMap(d -> effectiveTime.map(d::atTime)))).subscribe(this::onEffectiveAlertDateTimeChanged));
        compositeDisposable.add(alertEntityObservable.subscribe(this::onAlertEntityChanged));
    }

    private void onEffectiveAlertDateTimeChanged(Optional<LocalDateTime> localDateTime) {
        effectiveAlertDateTimeValueLiveData.postValue(localDateTime);
        effectiveAlertDateTimeStringLiveData.postValue(localDateTime.map(DATE_TIME_FORMATTER::format).orElse(""));
    }

    @NonNull
    private BinaryOptional<Integer, ResourceMessageFactory> calculateDaysEditTextParseResult(String text, AlertDateOption selectedOption) {
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
    }

    private Optional<LocalDate> calculateEventDate(AlertDateOption selectedOption) {
        if (null == target) {
            return Optional.empty();
        }

        return target.flatMap(courseAlertDetails -> {
            LocalDate d;
            CourseEntity course;
            if (selectedOption.isExplicit()) {
                if (null == (d = (course = courseAlertDetails.getCourse()).getActualEnd()) && null == (d = course.getExpectedEnd()) &&
                        null == (d = course.getActualStart()))
                    return Optional.ofNullable(course.getExpectedStart());
                return Optional.of(d);
            }
            if (selectedOption.isStart()) {
                if (null == (d = (course = courseAlertDetails.getCourse()).getActualStart()))
                    return Optional.ofNullable(course.getExpectedStart());
                return Optional.of(d);
            }
            if (null == (d = (course = courseAlertDetails.getCourse()).getActualEnd()))
                return Optional.ofNullable(course.getExpectedEnd());
            return Optional.of(d);
        }, assessmentAlertDetails -> {
            if (selectedOption.isExplicit()) {
                AssessmentEntity assessment = assessmentAlertDetails.getAssessment();
                LocalDate d = assessment.getCompletionDate();
                return (null != d) ? Optional.of(d) : Optional.ofNullable(assessment.getGoalDate());
            }
            if (selectedOption.isStart()) {
                return Optional.ofNullable(assessmentAlertDetails.getAssessment().getGoalDate());
            }
            return Optional.ofNullable(assessmentAlertDetails.getAssessment().getCompletionDate());
        });
    }

    @NonNull
    private BinaryOptional<LocalDate, ResourceMessageFactory> calculateEffectiveAlertDate(BinaryOptional<Integer, ResourceMessageFactory> daysEditTextParseResult,
                                                                                          Optional<LocalDate> selectedDate, AlertDateOption selectedOption,
                                                                                          Optional<LocalDate> eventDate, Boolean beforeEndAllowed) {
        if (selectedOption == AlertDateOption.EXPLICIT) {
            return selectedDate.<BinaryOptional<LocalDate, ResourceMessageFactory>>map(BinaryOptional::ofPrimary).orElseGet(() ->
                    BinaryOptional.ofSecondary(ResourceMessageFactory.ofError(R.string.message_required)));
        }
        if (daysEditTextParseResult.isPrimary()) {
            if (selectedOption.isAfter()) {
                return BinaryOptional.ofPrimary(eventDate.orElseThrow(IllegalStateException::new).plusDays(daysEditTextParseResult.getPrimary()));
            }
            if (selectedOption.isEnd() && !beforeEndAllowed) {
                return BinaryOptional.ofSecondary(ResourceMessageFactory.ofError(R.string.format_error, "Illegal option selection"));
            }
            return BinaryOptional.ofPrimary(eventDate.orElseThrow(IllegalStateException::new).minusDays(daysEditTextParseResult.getPrimary()));
        }
        return BinaryOptional.empty();
    }

    @NonNull
    private Optional<Long> calculateTimeSpec(AlertDateOption selectedOption, BinaryOptional<Integer, ResourceMessageFactory> daysEditTextParseResult,
                                             Optional<LocalDate> selectedDate) {
        Optional<Long> result = (selectedOption == AlertDateOption.EXPLICIT) ?
                selectedDate.map(LocalDateConverter::fromLocalDate) :
                daysEditTextParseResult.ofPrimary().map(i -> (selectedOption.isBefore()) ? -(long) i : (long) i);
        timeSpec = result.orElse(null);
        return result;
    }

    @NonNull
    private Boolean calculateChanged(AlertDateOption option, Optional<Long> timeSpec, Optional<LocalDate> date, Optional<LocalTime> localTime, String normalizedMessage,
                                     AlertEntity alertEntity) {
        String customMessage = alertEntity.getCustomMessage();
        return ((null == customMessage) ? normalizedMessage.isEmpty() : normalizedMessage.equals(customMessage)) ||
                localTime.map(t -> !Objects.equals(t, alertEntity.getAlertTime())).orElse(true) ||
                timeSpec.map(t -> alertEntity.getTimeSpec() != t || option != AlertDateOption.of(alertEntity.isSubsequent(), t)).orElse(true);
    }

    private void onDaysEditTextChanged(BinaryOptional<Integer, ResourceMessageFactory> daysEditText) {
        daysEditText.switchPresence(
                days -> daysValidationMessageLiveData.postValue(Optional.empty()),
                resourceMessageFactory -> daysValidationMessageLiveData.postValue(Optional.of(resourceMessageFactory)),
                () -> daysValidationMessageLiveData.postValue(Optional.empty())
        );
    }

    private void onEffectiveAlertDateChanged(BinaryOptional<LocalDate, ResourceMessageFactory> effectiveAlertDate) {
        effectiveAlertDate.switchPresence(
                localDate -> {
                    selectedDateValidationMessageLiveData.postValue(Optional.empty());
                    effectiveAlertDateTimeStringLiveData.postValue(LocalDateConverter.MEDIUM_FORMATTER.format(localDate));
                },
                resourceMessageFactory -> {
                    selectedDateValidationMessageLiveData.postValue(Optional.of(resourceMessageFactory));
                    effectiveAlertDateTimeStringLiveData.postValue("");
                },
                () -> {
                    selectedDateValidationMessageLiveData.postValue(Optional.empty());
                    effectiveAlertDateTimeStringLiveData.postValue("");
                }
        );
    }

    private void onAlertEntityChanged(AlertEntity alertEntity) {
        alertEntityLiveData.postValue(alertEntity);
        long t = alertEntity.getTimeSpec();
        AlertDateOption alertDateOption = AlertDateOption.of(alertEntity.isSubsequent(), t);
        setAlertDateOption(alertDateOption);
        if (alertDateOption == AlertDateOption.EXPLICIT) {
            selectedDateSubject.onNext(Optional.ofNullable(LocalDateConverter.toLocalDate(t)));
            daysTextSubject.onNext("");
        } else {
            timeSpec = t;
            daysTextSubject.onNext(NUMBER_FORMATTER.format(Math.abs(t)));
            selectedDateSubject.onNext(Optional.empty());
        }
        Optional<LocalTime> alertTime = Optional.ofNullable(alertEntity.getAlertTime());
        selectedTimeSubject.onNext(alertTime);
        explicitTimeSubject.onNext(alertTime.isPresent());
        String message = alertEntity.getCustomMessage();
        customMessageTextSubject.onNext((null == message) ? "" : message);
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
        return ComparisonHelper.mapNonNullElse(alertEntitySubject.getValue(), AlertEntity::getNotificationId, 0);
    }

    public boolean isBeforeEndAllowed() {
        return Boolean.TRUE.equals(beforeEndAllowedSubject.getValue());
    }

    @NonNull
    public String getDaysText() {
        return ComparisonHelper.requireNonNullElse(daysTextSubject.getValue(), "");
    }

    public void setDaysText(String text) {
        daysTextSubject.onNext(ComparisonHelper.requireNonNullElse(text, ""));
    }

    @NonNull
    public AlertDateOption getAlertDateOption() {
        return ComparisonHelper.requireNonNullElse(selectedOptionSubject.getValue(), AlertDateOption.EXPLICIT);
    }

    void setAlertDateOption(@NonNull AlertDateOption alertDateOption) {
        selectedOptionSubject.onNext(alertDateOption);
    }

    @Nullable
    public Boolean isSubsequent() {
        AlertDateOption alertDateOption = selectedOptionSubject.getValue();
        if (null == alertDateOption || alertDateOption.isExplicit()) {
            return null;
        }
        return alertDateOption.isAfter();
    }

    @Nullable
    public LocalDate getSelectedDate() {
        AlertDateOption alertDateOption = selectedOptionSubject.getValue();
        if (null == alertDateOption || alertDateOption.isExplicit()) {
            return ComparisonHelper.requireNonNull(selectedDateSubject.getValue()).orElse(null);
        }
        return null;
    }

    public void setSelectedDate(@Nullable LocalDate date) {
        selectedDateSubject.onNext(Optional.ofNullable(date));
    }

    public boolean isExplicitTime() {
        return Boolean.TRUE.equals(explicitTimeSubject.getValue());
    }

    public void setExplicitTime(boolean value) {
        explicitTimeSubject.onNext(value);
    }

    @Nullable
    public LocalTime getSelectedTime() {
        if (Boolean.FALSE.equals(explicitTimeSubject.getValue())) {
            return ComparisonHelper.requireNonNull(selectedTimeSubject.getValue()).orElse(null);
        }
        return null;
    }

    public void setSelectedTime(@Nullable LocalTime alertTime) {
        selectedTimeSubject.onNext(Optional.ofNullable(alertTime));
    }

    @NonNull
    public String getCustomMessage() {
        return ComparisonHelper.requireNonNullElse(customMessageTextSubject.getValue(), "");
    }

    public void setCustomMessage(String customMessage) {
        customMessageTextSubject.onNext(ComparisonHelper.requireNonNullElse(customMessage, ""));
    }

    public LiveData<AlertEntity> getAlertEntityLiveData() {
        return alertEntityLiveData;
    }

    public LiveData<String> getEventDateStringLiveData() {
        return eventDateStringLiveData;
    }

    public LiveData<String> getEffectiveAlertDateTimeStringLiveData() {
        return effectiveAlertDateTimeStringLiveData;
    }

    public LiveData<String> getEffectiveTimeStringLiveData() {
        return effectiveTimeStringLiveData;
    }

    public LiveData<Optional<LocalDateTime>> getEffectiveAlertDateTimeValueLiveData() {
        return effectiveAlertDateTimeValueLiveData;
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
        outState.putInt(STATE_KEY_RELATIVITY, ComparisonHelper.mapNonNullElse(selectedOptionSubject.getValue(), AlertDateOption::ordinal, 0));
        outState.putString(STATE_KEY_MESSAGE, customMessageTextSubject.getValue());
        outState.putString(STATE_KEY_DAYS_TEXT, daysTextSubject.getValue());
        ComparisonHelper.requireNonNull(selectedDateSubject.getValue()).map(LocalDateConverter::fromLocalDate).ifPresent(d -> outState.putLong(STATE_KEY_SELECTED_DATE, d));
        ComparisonHelper.requireNonNull(selectedTimeSubject.getValue()).map(LocalTimeConverter::fromLocalTime).ifPresent(t -> outState.putInt(STATE_KEY_SELECTED_TIME, t));
        target.switchPresence(courseAlertDetails -> courseAlertDetails.saveState(outState, true),
                assessmentAlertDetails -> assessmentAlertDetails.saveState(outState, true));
    }

    private synchronized void restoreViewModelState(@NonNull Bundle state) {
        int type = state.getInt(STATE_KEY_TYPE, 0);
        if (type == TYPE_VALUE_COURSE) {
            CourseAlertDetails courseAlertDetails = new CourseAlertDetails();
            courseAlertDetails.restoreState(state, true);
            onCourseAlertLoaded(courseAlertDetails);
        } else {
            AssessmentAlertDetails assessmentAlertDetails = new AssessmentAlertDetails();
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
        initializeAlert(BinaryAlternate.ofPrimary(new CourseAlertDetails(entity, new CourseEntity(courseDetails))));
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
        initializeAlert(BinaryAlternate.ofSecondary(new AssessmentAlertDetails(entity, new AssessmentEntity(assessmentDetails))));
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
            if (date.isPresent()) {
                startLabelTextResourceId = R.string.label_actual_start;
            } else {
                startLabelTextResourceId = R.string.label_expected_start;
            }
            date = Optional.ofNullable(courseEntity.getActualEnd());
            if (date.isPresent()) {
                endLabelTextResourceId = R.string.label_actual_end;
                beforeEndAllowedSubject.onNext(false);
            } else {
                endLabelTextResourceId = R.string.label_expected_end;
                beforeEndAllowedSubject.onNext(true);
            }
            return courseAlertDetails.getAlert();
        }, assessmentAlertDetails -> {
            AssessmentEntity assessmentEntity = assessmentAlertDetails.getAssessment();
            typeResourceIdSubject.onNext(assessmentEntity.getType().displayResourceId());
            startLabelTextResourceId = R.string.label_goal_date;
            endLabelTextResourceId = R.string.label_completion_date;
            beforeEndAllowedSubject.onNext(false);
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
        Long t = timeSpec;
        if (null == t) {
            return ValidationMessage.ofSingleError((ComparisonHelper.mapNonNullElse(dateSpecOption, AlertDateOption::isExplicit, true)) ? R.string.message_alert_date_required : R.string.message_alert_days_required);
        }
        entity.setTimeSpec(t);
        String customMessageText = customMessageTextSubject.getValue();
        entity.setCustomMessage((ComparisonHelper.mapNonNullElse(customMessageText, String::isEmpty, true)) ? null : customMessageText);
        entity.setAlertTime(ComparisonHelper.requireNonNull(selectedTimeSubject.getValue()).orElse(null));
        return null;
    }

    public synchronized Completable delete() {
        return target.flatMap(dbLoader::deleteCourseAlert, dbLoader::deleteAssessmentAlert);
    }

    private static class PrivateLiveData<T> extends LiveData<T> {
        PrivateLiveData(T value) {
            super(value);
        }

        PrivateLiveData() {
        }

        @Override
        public void postValue(T value) {
            super.postValue(value);
        }

        @Override
        public void setValue(T value) {
            super.setValue(value);
        }
    }

}
