package Erwine.Leonard.T.wguscheduler356334.ui.alert;

import android.app.Application;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
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

import Erwine.Leonard.T.wguscheduler356334.MainActivity;
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
import Erwine.Leonard.T.wguscheduler356334.util.BehaviorComputationSource;
import Erwine.Leonard.T.wguscheduler356334.util.BinaryAlternate;
import Erwine.Leonard.T.wguscheduler356334.util.BinaryOptional;
import Erwine.Leonard.T.wguscheduler356334.util.ComparisonHelper;
import Erwine.Leonard.T.wguscheduler356334.util.LiveDataWrapper;
import Erwine.Leonard.T.wguscheduler356334.util.ObserverHelper;
import Erwine.Leonard.T.wguscheduler356334.util.SubscribingLiveDataWrapper;
import Erwine.Leonard.T.wguscheduler356334.util.ToStringBuilder;
import Erwine.Leonard.T.wguscheduler356334.util.WguSchedulerViewModel;
import Erwine.Leonard.T.wguscheduler356334.util.Workers;
import Erwine.Leonard.T.wguscheduler356334.util.validation.ResourceMessageFactory;
import Erwine.Leonard.T.wguscheduler356334.util.validation.ResourceMessageResult;
import Erwine.Leonard.T.wguscheduler356334.util.validation.ValidationMessage;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.disposables.CompositeDisposable;

import static Erwine.Leonard.T.wguscheduler356334.entity.IdIndexedEntity.ID_NEW;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class EditAlertViewModel extends WguSchedulerViewModel {

    private static final String LOG_TAG = MainActivity.getLogTag(EditAlertViewModel.class);
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
    private final BehaviorComputationSource<AlertEntity> originalValuesSubject;
    private final BehaviorComputationSource<Integer> typeResourceIdSubject;
    private final BehaviorComputationSource<Boolean> beforeEndAllowedSubject;
    private final BehaviorComputationSource<String> daysTextSubject;
    private final BehaviorComputationSource<Optional<LocalDate>> selectedDateSubject;
    private final BehaviorComputationSource<AlertDateOption> selectedOptionSubject;
    private final BehaviorComputationSource<Boolean> explicitTimeSubject;
    private final BehaviorComputationSource<Optional<LocalTime>> selectedTimeSubject;
    private final BehaviorComputationSource<String> customMessageTextSubject;
    @SuppressWarnings("FieldCanBeLocal") // Needs to be a field so it doesn't get garbage-collected
    private final BehaviorComputationSource<LocalTime> defaultEventTimeSubject;
    @SuppressWarnings("FieldCanBeLocal") // Needs to be a field so it doesn't get garbage-collected
    private final CompositeDisposable compositeDisposable;
    private final LiveDataWrapper<ResourceMessageResult> initializationFailureLiveData;
    private final SubscribingLiveDataWrapper<Boolean> validLiveData;
    private final SubscribingLiveDataWrapper<Boolean> canSaveLiveData;
    private final SubscribingLiveDataWrapper<String> eventDateStringLiveData;
    private final SubscribingLiveDataWrapper<String> selectedDateStringLiveData;
    private final SubscribingLiveDataWrapper<AlertDateOption> selectedOptionLiveData;
    private final SubscribingLiveDataWrapper<String> effectiveTimeStringLiveData;
    private final LiveDataWrapper<String> effectiveAlertDateTimeStringLiveData;
    private final LiveDataWrapper<Optional<LocalDateTime>> effectiveAlertDateTimeValueLiveData;
    private final LiveDataWrapper<Optional<ResourceMessageFactory>> daysValidationMessageLiveData;
    private final LiveDataWrapper<Optional<ResourceMessageFactory>> selectedDateValidationMessageLiveData;
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
        originalValuesSubject = BehaviorComputationSource.createDefault(new AlertEntity());
        typeResourceIdSubject = BehaviorComputationSource.createDefault(TYPE_VALUE_COURSE);
        beforeEndAllowedSubject = BehaviorComputationSource.createDefault(true);
        daysTextSubject = BehaviorComputationSource.createDefault("");
        selectedDateSubject = BehaviorComputationSource.createDefault(Optional.empty());
        selectedOptionSubject = BehaviorComputationSource.createDefault(AlertDateOption.EXPLICIT);
        explicitTimeSubject = BehaviorComputationSource.createDefault(false);
        selectedTimeSubject = BehaviorComputationSource.createDefault(Optional.empty());
        customMessageTextSubject = BehaviorComputationSource.createDefault("");
        defaultEventTimeSubject = BehaviorComputationSource.createDefault(LocalTime.MIDNIGHT);

        initializationFailureLiveData = new LiveDataWrapper<>();
        daysValidationMessageLiveData = new LiveDataWrapper<>();
        selectedDateValidationMessageLiveData = new LiveDataWrapper<>();
        effectiveAlertDateTimeStringLiveData = new LiveDataWrapper<>();
        effectiveAlertDateTimeValueLiveData = new LiveDataWrapper<>();

        ObserverHelper.observeOnce(DbLoader.getPreferAlertTime(), this, defaultEventTimeSubject::onNext);

        Observable<BinaryOptional<Integer, ResourceMessageFactory>> daysEditTextParseResultObservable = Observable.combineLatest(
                daysTextSubject.getObservable(),
                selectedOptionSubject.getObservable(),
                this::calculateDaysEditTextParseResult
        );
        Observable<Optional<LocalDate>> eventDateObservable = selectedOptionSubject.getObservable().map(Workers.asCached(this::calculateEventDate));
        Observable<BinaryOptional<LocalDate, ResourceMessageFactory>> effectiveAlertDateObservable = Observable.combineLatest(
                daysEditTextParseResultObservable,
                selectedDateSubject.getObservable(),
                selectedOptionSubject.getObservable(),
                eventDateObservable,
                beforeEndAllowedSubject.getObservable(),
                Workers.asCached(this::calculateEffectiveAlertDate)
        );
        Observable<Optional<LocalTime>> effectiveTimeObservable = Observable.combineLatest(
                defaultEventTimeSubject.getObservable(),
                explicitTimeSubject.getObservable(),
                selectedTimeSubject.getObservable(),
                Workers.asCached((defaultEventTime, isExplicitTime, selectedTime) -> (isExplicitTime) ? selectedTime : Optional.of(defaultEventTime))
        );
        Observable<Boolean> changedObservable = Observable.combineLatest(
                selectedOptionSubject.getObservable(),
                Observable.combineLatest(selectedOptionSubject.getObservable(), daysEditTextParseResultObservable, selectedDateSubject.getObservable(),
                        Workers.asCached(this::calculateTimeSpec)),
                selectedDateSubject.getObservable(),
                selectedTimeSubject.getObservable(),
                customMessageTextSubject.getObservable().map(Workers.asCached(AbstractEntity.SINGLE_LINE_NORMALIZER::apply)),
                originalValuesSubject.getObservable(),
                Workers.asCached(this::calculateChanged)
        );
        Observable<Boolean> validObservable = Observable.combineLatest(daysEditTextParseResultObservable, effectiveAlertDateObservable, effectiveTimeObservable,
                Workers.asCached((daysEditTextParseResult, effectiveAlertDate, effectiveAlertTime) ->
                        !(daysEditTextParseResult.isSecondary() && effectiveAlertDate.isSecondary()) && effectiveAlertTime.isPresent()));

        validLiveData = SubscribingLiveDataWrapper.of(false, validObservable);
        canSaveLiveData = SubscribingLiveDataWrapper.of(false, Observable.combineLatest(changedObservable, validObservable, (c, v) -> c && v));
        selectedOptionLiveData = SubscribingLiveDataWrapper.of(selectedOptionSubject.getValue(), selectedOptionSubject.getObservable());
        eventDateStringLiveData = SubscribingLiveDataWrapper.of("", eventDateObservable.map(e -> e.map(LocalDateConverter.MEDIUM_FORMATTER::format).orElse("")));
        selectedDateStringLiveData = SubscribingLiveDataWrapper.of("", Observable.combineLatest(selectedOptionSubject.getObservable(), selectedDateSubject.getObservable(),
                (o, d) -> (o == AlertDateOption.EXPLICIT) ? d : Optional.<LocalDate>empty())
                .map(e -> e.map(LocalDateConverter.SHORT_FORMATTER::format).orElse("")));
        effectiveTimeStringLiveData = SubscribingLiveDataWrapper.of("", effectiveTimeObservable.map(t -> t.map(LocalTimeConverter.MEDIUM_FORMATTER::format).orElse("")));

        compositeDisposable = new CompositeDisposable(validLiveData, canSaveLiveData, selectedOptionLiveData, eventDateStringLiveData, selectedDateStringLiveData,
                effectiveTimeStringLiveData,
                daysEditTextParseResultObservable.subscribe(this::onDaysEditTextChanged,
                        throwable -> daysValidationMessageLiveData.postValue(Optional.of(ResourceMessageFactory.ofError(throwable)))),
                effectiveAlertDateObservable.subscribe(this::onEffectiveAlertDateChanged,
                        throwable -> selectedDateValidationMessageLiveData.postValue(Optional.of(ResourceMessageFactory.ofError(throwable)))),
                Observable.combineLatest(effectiveAlertDateObservable, effectiveTimeObservable, Workers.asCached((effectiveAlertDate, effectiveTime) ->
                        effectiveAlertDate.ofPrimary().flatMap(d -> effectiveTime.map(d::atTime)))).subscribe(this::onEffectiveAlertDateTimeChanged),
                originalValuesSubject.getObservable().subscribe(this::onAlertEntityChanged)
        );
    }

    @Override
    protected void onCleared() {
        compositeDisposable.dispose();
        super.onCleared();
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

    @NonNull
    Optional<LocalDateTime> getOriginalEventDateTime(@NonNull LocalTime defaultEventTime) {
        Log.d(LOG_TAG, "Enter getOriginalEventDateTime(defaultEventTime: " + ToStringBuilder.toEscapedString(defaultEventTime, true) + ")");
        if (null == target) {
            return Optional.empty();
        }

        return target.flatMap(courseAlertDetails -> {
            Log.d(LOG_TAG, "Enter getOriginalEventDateTime#target.flatMap(courseAlertDetails: " + courseAlertDetails.toString() + ")");
            LocalDate d = courseAlertDetails.getAlertDate();
            if (null != d) {
                AlertEntity alert = courseAlertDetails.getAlert();
                if (alert.getId() != ID_NEW) {
                    LocalTime t = alert.getAlertTime();
                    return Optional.of(d.atTime((null == t) ? defaultEventTime : t));
                }
            }
            return Optional.empty();
        }, assessmentAlertDetails -> {
            Log.d(LOG_TAG, "Enter getOriginalEventDateTime#target.flatMap(assessmentAlertDetails: " + assessmentAlertDetails.toString() + ")");
            LocalDate d = assessmentAlertDetails.getAlertDate();
            if (null != d) {
                AlertEntity alert = assessmentAlertDetails.getAlert();
                if (alert.getId() != ID_NEW) {
                    LocalTime t = alert.getAlertTime();
                    return Optional.of(d.atTime((null == t) ? defaultEventTime : t));
                }
            }
            return Optional.empty();
        });
    }

    @NonNull
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
        if (daysEditTextParseResult.isPrimary() && eventDate.isPresent()) {
            if (selectedOption.isAfter()) {
                return BinaryOptional.ofPrimary(eventDate.get().plusDays(daysEditTextParseResult.getPrimary()));
            }
            if (selectedOption.isEnd() && !beforeEndAllowed) {
                return BinaryOptional.ofSecondary(ResourceMessageFactory.ofError(R.string.format_error, "Illegal option selection"));
            }
            return BinaryOptional.ofPrimary(eventDate.get().minusDays(daysEditTextParseResult.getPrimary()));
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
        return !((null == customMessage) ? normalizedMessage.isEmpty() : normalizedMessage.equals(customMessage)) ||
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
//        originalValuesLiveData.postValue(alertEntity);
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
        return ComparisonHelper.mapNonNullElse(originalValuesSubject.getValue(), AlertEntity::getNotificationId, 0);
    }

    public boolean isBeforeEndAllowed() {
        return Boolean.TRUE.equals(beforeEndAllowedSubject.getValue());
    }

    @NonNull
    public String getDaysText() {
        return ComparisonHelper.requireNonNullElse(daysTextSubject.getValue(), "");
    }

    public void setDaysText(String text) {
        String s = ComparisonHelper.requireNonNullElse(text, "");
        if (!s.isEmpty() || selectedOptionSubject.getValue() != AlertDateOption.EXPLICIT) {
            daysTextSubject.onNext(s);
        }
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
        return alertDateOption.isEnd();
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

    public LiveData<String> getEventDateStringLiveData() {
        return eventDateStringLiveData.getLiveData();
    }

    public LiveData<String> getSelectedDateStringLiveData() {
        return selectedDateStringLiveData.getLiveData();
    }

    public LiveData<String> getEffectiveAlertDateTimeStringLiveData() {
        return effectiveAlertDateTimeStringLiveData.getLiveData();
    }

    public LiveData<String> getEffectiveTimeStringLiveData() {
        return effectiveTimeStringLiveData.getLiveData();
    }

    public LiveData<Optional<LocalDateTime>> getEffectiveAlertDateTimeValueLiveData() {
        return effectiveAlertDateTimeValueLiveData.getLiveData();
    }

    public LiveData<Optional<ResourceMessageFactory>> getDaysValidationMessageLiveData() {
        return daysValidationMessageLiveData.getLiveData();
    }

    public LiveData<Optional<ResourceMessageFactory>> getSelectedDateValidationMessageLiveData() {
        return selectedDateValidationMessageLiveData.getLiveData();
    }

    public LiveData<AlertDateOption> getSelectedOptionLiveData() {
        return selectedOptionLiveData.getLiveData();
    }

    public LiveData<Boolean> getCanSaveLiveData() {
        return canSaveLiveData.getLiveData();
    }

//    public LiveData<Boolean> getHasChangesLiveData() {
//        return hasChangesLiveData;
//    }

    public LiveData<Boolean> getValidLiveData() {
        return validLiveData.getLiveData();
    }

    public LiveData<ResourceMessageResult> getInitializationFailureLiveData() {
        return initializationFailureLiveData.getLiveData();
    }

    public synchronized Single<AlertEntity> initializeViewModelState(@Nullable Bundle savedInstanceState, Supplier<Bundle> getArguments) {
        boolean fromInitializedState = null != savedInstanceState && savedInstanceState.getBoolean(STATE_KEY_STATE_INITIALIZED, false);
        Bundle state = (fromInitializedState) ? savedInstanceState : getArguments.get();
        if (null == state) {
            throw new IllegalStateException();
        }
        if (fromInitializedState) {
            return Single.just(restoreViewModelState(state));
        }
        if (state.containsKey(ARG_KEY_ALERT_ID)) {
            long alertId = state.getLong(ARG_KEY_ALERT_ID);
            if (state.containsKey(ARG_KEY_COURSE_ID)) {
                return loadCourseAlert(alertId, state.getLong(ARG_KEY_COURSE_ID));
            }
            if (state.containsKey(ARG_KEY_ASSESSMENT_ID)) {
                return loadAssessmentAlert(alertId, state.getLong(ARG_KEY_ASSESSMENT_ID));
            }
            throw new IllegalStateException("Missing ID of entity related to event");
        }
        if (state.containsKey(ARG_KEY_COURSE_ID)) {
            return initializeNewCourseAlert(state.getLong(ARG_KEY_COURSE_ID));
        }
        if (state.containsKey(ARG_KEY_ASSESSMENT_ID)) {
            return initializeNewAssessmentAlert(state.getLong(ARG_KEY_ASSESSMENT_ID));
        }
        throw new IllegalStateException("Missing ID of entity related to event");
    }

    private Single<AlertEntity> loadCourseAlert(long alertId, long courseId) {
        return dbLoader.getCourseAlertDetailsById(alertId, courseId).map(this::onCourseAlertLoaded).doOnError(this::onCourseAlertLoadFailed);
    }

    private Single<AlertEntity> loadAssessmentAlert(long alertId, long assessmentId) {
        return dbLoader.getAssessmentAlertDetailsById(alertId, assessmentId).map(this::onAssessmentAlertLoaded).doOnError(this::onAssessmentAlertLoadFailed);
    }

    private Single<AlertEntity> initializeNewCourseAlert(long courseId) {
        return dbLoader.getCourseById(courseId).map(this::onCourseLoaded).doOnError(this::onCourseLoadFailed);
    }

    private Single<AlertEntity> initializeNewAssessmentAlert(long assessmentId) {
        return dbLoader.getAssessmentById(assessmentId).map(this::onAssessmentLoaded).doOnError(this::onAssessmentLoadFailed);
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

    private synchronized AlertEntity restoreViewModelState(@NonNull Bundle state) {
        int type = state.getInt(STATE_KEY_TYPE, 0);
        AlertEntity entity;
        if (type == TYPE_VALUE_COURSE) {
            CourseAlertDetails courseAlertDetails = new CourseAlertDetails();
            courseAlertDetails.restoreState(state, true);
            entity = onCourseAlertLoaded(courseAlertDetails);
        } else {
            AssessmentAlertDetails assessmentAlertDetails = new AssessmentAlertDetails();
            assessmentAlertDetails.restoreState(state, true);
            entity = onAssessmentAlertLoaded(assessmentAlertDetails);
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
        return entity;
    }

    /**
     * Initializes validation for an existing course alert or when restoring from state or course has been saved.
     *
     * @param courseAlertDetails The {@link CourseAlertDetails} being edited.
     */
    synchronized AlertEntity onCourseAlertLoaded(@NonNull CourseAlertDetails courseAlertDetails) {
        return initializeAlert(BinaryAlternate.ofPrimary(courseAlertDetails));
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
    synchronized AlertEntity onCourseLoaded(@NonNull CourseDetails courseDetails) {
        AlertEntity entity = new AlertEntity();
        return initializeAlert(BinaryAlternate.ofPrimary(new CourseAlertDetails(entity, new CourseEntity(courseDetails))));
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
    synchronized AlertEntity onAssessmentAlertLoaded(@NonNull AssessmentAlertDetails assessmentAlertDetails) {
        return initializeAlert(BinaryAlternate.ofSecondary(assessmentAlertDetails));
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
    synchronized AlertEntity onAssessmentLoaded(@NonNull AssessmentDetails assessmentDetails) {
        AlertEntity entity = new AlertEntity();
        return initializeAlert(BinaryAlternate.ofSecondary(new AssessmentAlertDetails(entity, new AssessmentEntity(assessmentDetails))));
    }

    private void onAssessmentLoadFailed(Throwable throwable) {
        Log.e(LOG_TAG, "Error loading assessment", throwable);
        initializationFailureLiveData.postValue(ValidationMessage.ofSingleError(R.string.format_message_read_error, throwable.toString()));
    }

    private AlertEntity initializeAlert(@NonNull BinaryAlternate<? extends CourseAlertDetails, ? extends AssessmentAlertDetails> target) {
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
        originalValuesSubject.onNext(alertEntity);
        return alertEntity;
    }

    public synchronized Single<ResourceMessageResult> save(boolean ignoreWarnings) {
        return target.flatMap(courseAlertDetails -> {
            CourseAlertDetails courseAlert = new CourseAlertDetails(courseAlertDetails);
            ResourceMessageResult validationMessage = onSave(courseAlert.getAlert());
            if (null != validationMessage) {
                return Single.just(validationMessage);
            }
            return dbLoader.saveCourseAlert(courseAlert, ignoreWarnings).doOnSuccess(m -> {
                if (m.isSucceeded()) {
                    AlertEntity alertEntity = courseAlert.getAlert();
                    courseAlertDetails.setAlert(alertEntity);
                    originalValuesSubject.onNext(alertEntity);
                }
            });
        }, assessmentAlertDetails -> {
            AssessmentAlertDetails assessmentAlert = new AssessmentAlertDetails(assessmentAlertDetails);
            ResourceMessageResult validationMessage = onSave(assessmentAlert.getAlert());
            if (null != validationMessage) {
                return Single.just(validationMessage);
            }
            return dbLoader.saveAssessmentAlert(assessmentAlert, ignoreWarnings).doOnSuccess(m -> {
                if (m.isSucceeded()) {
                    AlertEntity alertEntity = assessmentAlert.getAlert();
                    assessmentAlertDetails.setAlert(alertEntity);
                    originalValuesSubject.onNext(alertEntity);
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

}
