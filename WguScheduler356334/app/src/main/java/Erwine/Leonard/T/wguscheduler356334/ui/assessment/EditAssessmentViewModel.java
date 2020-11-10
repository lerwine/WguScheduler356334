package Erwine.Leonard.T.wguscheduler356334.ui.assessment;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.text.HtmlCompat;
import androidx.lifecycle.LiveData;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import Erwine.Leonard.T.wguscheduler356334.AddAssessmentActivity;
import Erwine.Leonard.T.wguscheduler356334.MainActivity;
import Erwine.Leonard.T.wguscheduler356334.R;
import Erwine.Leonard.T.wguscheduler356334.ViewAssessmentActivity;
import Erwine.Leonard.T.wguscheduler356334.db.AppDb;
import Erwine.Leonard.T.wguscheduler356334.db.AssessmentStatusConverter;
import Erwine.Leonard.T.wguscheduler356334.db.DbLoader;
import Erwine.Leonard.T.wguscheduler356334.db.LocalDateConverter;
import Erwine.Leonard.T.wguscheduler356334.entity.AbstractEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.AbstractNotedEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.IdIndexedEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.assessment.AbstractAssessmentEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.assessment.Assessment;
import Erwine.Leonard.T.wguscheduler356334.entity.assessment.AssessmentAlert;
import Erwine.Leonard.T.wguscheduler356334.entity.assessment.AssessmentDetails;
import Erwine.Leonard.T.wguscheduler356334.entity.assessment.AssessmentEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.assessment.AssessmentStatus;
import Erwine.Leonard.T.wguscheduler356334.entity.assessment.AssessmentType;
import Erwine.Leonard.T.wguscheduler356334.entity.course.AbstractCourseEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.course.Course;
import Erwine.Leonard.T.wguscheduler356334.entity.course.CourseEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.course.TermCourseListItem;
import Erwine.Leonard.T.wguscheduler356334.entity.mentor.MentorEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.term.TermEntity;
import Erwine.Leonard.T.wguscheduler356334.util.BehaviorComputationSource;
import Erwine.Leonard.T.wguscheduler356334.util.LiveDataWrapper;
import Erwine.Leonard.T.wguscheduler356334.util.SubscribingLiveDataWrapper;
import Erwine.Leonard.T.wguscheduler356334.util.ToStringBuilder;
import Erwine.Leonard.T.wguscheduler356334.util.WguSchedulerViewModel;
import Erwine.Leonard.T.wguscheduler356334.util.Workers;
import Erwine.Leonard.T.wguscheduler356334.util.validation.ResourceMessageResult;
import Erwine.Leonard.T.wguscheduler356334.util.validation.ValidationMessage;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.CompletableSubject;

import static Erwine.Leonard.T.wguscheduler356334.db.LocalDateConverter.FULL_FORMATTER;
import static Erwine.Leonard.T.wguscheduler356334.entity.IdIndexedEntity.ID_NEW;

public class EditAssessmentViewModel extends WguSchedulerViewModel {
    private static final String LOG_TAG = MainActivity.getLogTag(EditAssessmentViewModel.class);
    static final String STATE_KEY_STATE_INITIALIZED = "state_initialized";
    public static final String EXTRA_KEY_ASSESSMENT_ID = IdIndexedEntity.stateKey(AppDb.TABLE_NAME_ASSESSMENTS, Assessment.COLNAME_ID, false);

    private final DbLoader dbLoader;
    private final BehaviorComputationSource<String> code;
    private final BehaviorComputationSource<String> name;
    private final BehaviorComputationSource<AssessmentStatus> status;
    private final BehaviorComputationSource<Optional<LocalDate>> goalDate;
    private final BehaviorComputationSource<Optional<LocalDate>> completionDate;
    private final BehaviorComputationSource<Optional<AbstractCourseEntity<?>>> selectedCourse;
    private final BehaviorComputationSource<AssessmentType> type;
    private final BehaviorComputationSource<String> notes;
    private final BehaviorComputationSource<AssessmentDetails> originalValues;
    private final SubscribingLiveDataWrapper<Function<Resources, CharSequence>> titleFactoryLiveData;
    private final SubscribingLiveDataWrapper<String> subTitleLiveData;
    private final SubscribingLiveDataWrapper<Function<Resources, CharSequence>> overviewFactoryLiveData;
    private final SubscribingLiveDataWrapper<Boolean> codeValidLiveData;
    private final SubscribingLiveDataWrapper<LocalDate> goalDateLiveData;
    private final SubscribingLiveDataWrapper<LocalDate> completionDateLiveData;
    private final SubscribingLiveDataWrapper<AbstractCourseEntity<?>> selectedCourseLiveData;
    private final SubscribingLiveDataWrapper<TermEntity> currentTermLiveData;
    private final SubscribingLiveDataWrapper<MentorEntity> currentMentorLiveData;
    private final SubscribingLiveDataWrapper<Boolean> canShareLiveData;
    private final SubscribingLiveDataWrapper<Function<Resources, CharSequence>> courseDisplayLiveData;
    private final SubscribingLiveDataWrapper<Boolean> canSaveLiveData;
    private final SubscribingLiveDataWrapper<Boolean> changedLiveData;
    private final SubscribingLiveDataWrapper<AssessmentDetails> originalValuesLiveData;
    private final SubscribingLiveDataWrapper<Integer> typeDisplayLiveData;
    private final SubscribingLiveDataWrapper<Integer> statusDisplayLiveData;
    private final SubscribingLiveDataWrapper<String> goalDateDisplayLiveData;
    private final SubscribingLiveDataWrapper<String> completionDateDisplayLiveData;
    private final SubscribingLiveDataWrapper<Boolean> showGoalDateCloseIconLiveData;
    private final SubscribingLiveDataWrapper<Boolean> showCompletionDateCloseIconLiveData;
    private final LiveDataWrapper<List<AssessmentAlert>> assessmentAlertsLiveData;
    private final SubscribingLiveDataWrapper<Boolean> courseValidLiveData;
    private final CompletableSubject initializedSubject;
    private final CompositeDisposable compositeDisposable;
    private final Completable initializedCompletable;
    private Pair<Long, Disposable> alertsObserving;
    private boolean fromInitializedState;

    public static void startAddAssessmentActivity(@NonNull Activity activity, int requestCode, long courseId, @Nullable LocalDate goalDate) {
        Intent intent = new Intent(activity, AddAssessmentActivity.class);
        intent.putExtra(IdIndexedEntity.stateKey(AppDb.TABLE_NAME_COURSES, Course.COLNAME_ID, false), courseId);
        Long d = LocalDateConverter.fromLocalDate(goalDate);
        if (null != d) {
            intent.putExtra(IdIndexedEntity.stateKey(AppDb.TABLE_NAME_ASSESSMENTS, Assessment.COLNAME_GOAL_DATE, false), d);
        }
        activity.startActivityForResult(intent, requestCode);
    }

    public static void startViewAssessmentActivity(@NonNull Context context, long assessmentId) {
        Intent intent = new Intent(context, ViewAssessmentActivity.class);
        intent.putExtra(EXTRA_KEY_ASSESSMENT_ID, assessmentId);
        context.startActivity(intent);
    }

    public EditAssessmentViewModel(@NonNull Application application) {
        super(application);
        dbLoader = DbLoader.getInstance(getApplication());
        selectedCourse = BehaviorComputationSource.createDefault(Optional.empty());
        notes = BehaviorComputationSource.createDefault("");
        type = BehaviorComputationSource.createDefault(AssessmentType.OBJECTIVE_ASSESSMENT);
        completionDate = BehaviorComputationSource.createDefault(Optional.empty());
        goalDate = BehaviorComputationSource.createDefault(Optional.empty());
        status = BehaviorComputationSource.createDefault(AssessmentStatus.NOT_STARTED);
        name = BehaviorComputationSource.createDefault("");
        code = BehaviorComputationSource.createDefault("");
        originalValues = BehaviorComputationSource.createDefault(new AssessmentDetails((AbstractCourseEntity<?>) null));
        assessmentAlertsLiveData = new LiveDataWrapper<>(Collections.emptyList());
        initializedSubject = CompletableSubject.create();
        initializedCompletable = initializedSubject.observeOn(AndroidSchedulers.mainThread());

        Observable<String> codeObservable = code.getObservable().map(Workers.asCached(AbstractAssessmentEntity.SINGLE_LINE_NORMALIZER::apply));
        Observable<String> nameObservable = name.getObservable().map(Workers.asCached(AbstractAssessmentEntity.SINGLE_LINE_NORMALIZER::apply));

        Observable<Boolean> codeValidObservable = codeObservable.map(c -> !c.isEmpty());
        Observable<Boolean> courseValidObservable = selectedCourse.getObservable().map(Optional::isPresent);

        titleFactoryLiveData = SubscribingLiveDataWrapper.of(c -> c.getString(R.string.title_activity_view_assessment), Observable.combineLatest(type.getObservable(), codeObservable,
                Workers.asCached(
                        (type, code) -> resources -> resources.getString(R.string.format_value_colon_value, resources.getString(type.displayResourceId()), code)
                )
        ));
        subTitleLiveData = SubscribingLiveDataWrapper.of("", nameObservable);
        overviewFactoryLiveData = SubscribingLiveDataWrapper.of(r -> "", Observable.combineLatest(goalDate.getObservable(), completionDate.getObservable(),
                status.getObservable(),
                Workers.asCached(EditAssessmentViewModel::calculateOverview)
        ));
        originalValuesLiveData = SubscribingLiveDataWrapper.of(originalValues.getValue(), originalValues.getObservable());
        codeValidLiveData = SubscribingLiveDataWrapper.of(false, codeValidObservable);
        courseValidLiveData = SubscribingLiveDataWrapper.of(false, courseValidObservable);
        goalDateLiveData = SubscribingLiveDataWrapper.ofOptional(goalDate.getObservable().doOnNext(d -> {
            Log.d(LOG_TAG, "Calculating goalDate: goalDate = " + ToStringBuilder.toEscapedString(d.orElse(null), false));
            recalculateAlerts(d.orElse(null), completionDate.getValue().filter(c -> {
                switch (status.getValue()) {
                    case NOT_PASSED:
                    case PASSED:
                        return true;
                    default:
                        return false;
                }
            }).orElse(null));
        }));
        completionDateLiveData = SubscribingLiveDataWrapper.ofOptional(Observable.combineLatest(status.getObservable(), completionDate.getObservable(),
                Workers.asCached((status, completionDate) -> {
                    Log.d(LOG_TAG, "Calculating completionDate: status = " + status.name() +
                            ", completionDate = " + ToStringBuilder.toEscapedString(completionDate.orElse(null), false));
                    switch (status) {
                        case NOT_PASSED:
                        case PASSED:
                            recalculateAlerts(goalDate.getValue().orElse(null), completionDate.orElse(null));
                            return completionDate;
                        default:
                            recalculateAlerts(goalDate.getValue().orElse(null), null);
                            return Optional.empty();
                    }
                })));
        selectedCourseLiveData = SubscribingLiveDataWrapper.ofOptional(selectedCourse.getObservable());
        currentTermLiveData = SubscribingLiveDataWrapper.ofOptional(selectedCourse.getObservable().flatMap(o -> {
                    Log.d(LOG_TAG, "Calculating currentTerm: selectedCourse = " + ToStringBuilder.toEscapedString(o.orElse(null)));
                    return o.map(t -> dbLoader.getTermByIdForComputation(t.getTermId()).doOnError(throwable ->
                            Log.e(LOG_TAG, "Error loading term", throwable)).map(Optional::of)).orElseGet(() -> Single.just(Optional.empty())).toObservable();
                }
        ));
        currentMentorLiveData = SubscribingLiveDataWrapper.ofOptional(selectedCourse.getObservable().flatMap(o -> {
                    Log.d(LOG_TAG, "Calculating currentMentor: selectedCourse = " + ToStringBuilder.toEscapedString(o.orElse(null)));
                    return o.<Single<Optional<MentorEntity>>>map(t -> {
                        Long id = t.getMentorId();
                        if (null == id) {
                            return Single.just(Optional.empty());
                        }
                        return dbLoader.getMentorByIdForComputation(id).doOnError(throwable ->
                                Log.e(LOG_TAG, "Error loading mentor", throwable)).map(Optional::of);
                    }).orElseGet(() -> Single.just(Optional.empty())).toObservable();
                }
        ));
        courseDisplayLiveData = SubscribingLiveDataWrapper.of(selectedCourse.getObservable().map(o -> o.<Function<Resources, CharSequence>>map(course -> r ->
                r.getString(R.string.format_value_colon_value, course.getNumber(), course.getTitle())
        ).orElseGet(() -> r -> HtmlCompat.fromHtml(r.getString(R.string.html_none), HtmlCompat.FROM_HTML_SEPARATOR_LINE_BREAK_DIV))));
        typeDisplayLiveData = SubscribingLiveDataWrapper.of(AssessmentType.OBJECTIVE_ASSESSMENT.displayResourceId(), type.getObservable().map(AssessmentType::displayResourceId));
        statusDisplayLiveData = SubscribingLiveDataWrapper.of(AssessmentStatus.NOT_STARTED.displayResourceId(), status.getObservable().map(AssessmentStatus::displayResourceId));
        goalDateDisplayLiveData = SubscribingLiveDataWrapper.of("", goalDate.getObservable().map(o -> o.map(FULL_FORMATTER::format).orElse("")));
        completionDateDisplayLiveData = SubscribingLiveDataWrapper.of("", completionDate.getObservable().map(o -> o.map(FULL_FORMATTER::format).orElse("")));
        showGoalDateCloseIconLiveData = SubscribingLiveDataWrapper.of(false, goalDate.getObservable().map(Optional::isPresent));
        showCompletionDateCloseIconLiveData = SubscribingLiveDataWrapper.of(false, completionDate.getObservable().map(Optional::isPresent));
        Observable<Boolean> changedObservable = Observable.combineLatest(originalValues.getObservable(),
                codeObservable, nameObservable, status.getObservable(), goalDate.getObservable(), completionDate.getObservable(),
                type.getObservable(), notes.getObservable().map(Workers.asCached(AbstractNotedEntity.MULTI_LINE_NORMALIZER::apply)),
                selectedCourse.getObservable(), EditAssessmentViewModel::calculateChanged);
        Observable<Boolean> validObservable = Observable.combineLatest(codeValidObservable, courseValidObservable, (code, course) -> code && course);
        changedLiveData = SubscribingLiveDataWrapper.of(false, changedObservable);
        canShareLiveData = SubscribingLiveDataWrapper.of(false, Observable.combineLatest(validObservable, changedObservable, (v, c) -> {
            Log.d(LOG_TAG, "Calculating canShare: valid = " + v + "; changed =  " + c);
            return v && !c;
        }));
        canSaveLiveData = SubscribingLiveDataWrapper.of(false, Observable.combineLatest(validObservable, changedObservable, (v, c) -> {
            Log.d(LOG_TAG, "Calculating canSave: valid = " + v + "; changed = " + c);
            return v && c;
        }));
        compositeDisposable = new CompositeDisposable(titleFactoryLiveData, subTitleLiveData, overviewFactoryLiveData, originalValuesLiveData, codeValidLiveData, courseValidLiveData,
                goalDateLiveData, completionDateLiveData, selectedCourseLiveData, currentTermLiveData, currentMentorLiveData, courseDisplayLiveData, typeDisplayLiveData,
                statusDisplayLiveData, goalDateDisplayLiveData, completionDateDisplayLiveData, showGoalDateCloseIconLiveData, showCompletionDateCloseIconLiveData,
                changedLiveData, canShareLiveData, canSaveLiveData,
                originalValues.getObservable().map(AbstractEntity::getId).subscribe(this::observeAlertsByAssessmentId));
    }

    private void recalculateAlerts(@Nullable LocalDate goalDate, @Nullable LocalDate completionDate) {
        Log.d(LOG_TAG, "Enter recalculateAlerts(goalDate = " + ToStringBuilder.toEscapedString(goalDate, false) +
                ", completionDate = " + ToStringBuilder.toEscapedString(completionDate, false));
        List<AssessmentAlert> list = getAllAlerts().getValue();
        if (null != list) {
            for (AssessmentAlert a : list) {
                a.reCalculate(goalDate, completionDate);
            }
        }
    }

    public LiveData<Function<Resources, CharSequence>> getCourseDisplayLiveData() {
        return courseDisplayLiveData.getLiveData();
    }

    public LiveData<Integer> getTypeDisplayLiveData() {
        return typeDisplayLiveData.getLiveData();
    }

    public LiveData<Integer> getStatusDisplayLiveData() {
        return statusDisplayLiveData.getLiveData();
    }

    public LiveData<String> getGoalDateDisplayLiveData() {
        return goalDateDisplayLiveData.getLiveData();
    }

    public LiveData<Boolean> getShowGoalDateCloseIconLiveData() {
        return showGoalDateCloseIconLiveData.getLiveData();
    }

    public LiveData<String> getCompletionDateDisplayLiveData() {
        return completionDateDisplayLiveData.getLiveData();
    }

    public LiveData<Boolean> getShowCompletionDateCloseIconLiveData() {
        return showCompletionDateCloseIconLiveData.getLiveData();
    }

    @Override
    protected void onCleared() {
        compositeDisposable.dispose();
        super.onCleared();
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private static boolean calculateChanged(@NonNull AssessmentDetails originalValues, @NonNull String code, @NonNull String name, @NonNull AssessmentStatus status,
                                            Optional<LocalDate> goalDate, Optional<LocalDate> completionDate, @NonNull AssessmentType type, @NonNull String notes,
                                            Optional<AbstractCourseEntity<?>> selectedCourse) {
        Log.d(LOG_TAG, "Enter calculateChanged(originalValues = " + ToStringBuilder.toEscapedString(originalValues) + ",\ncode = " +
                ToStringBuilder.toEscapedString(code) + ", name = " + ToStringBuilder.toEscapedString(name) + ", status = " + status.name() +
                ", goalDate = " + ToStringBuilder.toEscapedString(goalDate.orElse(null), false) + ", completionDate = " +
                ToStringBuilder.toEscapedString(completionDate.orElse(null), false) + ", type = " + type.name() + ", notes = " +
                ToStringBuilder.toEscapedString(notes) + ", selectedCourse = " + ToStringBuilder.toEscapedString(selectedCourse.orElse(null)) + ")");
        String n = originalValues.getName();
        return !(code.equals(originalValues.getCode()) && name.equals((null == n) ? "" : n) && status == originalValues.getStatus() &&
                goalDate.map(d -> d.equals(originalValues.getGoalDate())).orElseGet(() -> null == originalValues.getGoalDate()) &&
                completionDate.map(d -> d.equals(originalValues.getCompletionDate())).orElseGet(() -> null == originalValues.getCompletionDate()) &&
                type == originalValues.getType() && notes.equals(originalValues.getNotes())) ||
                selectedCourse.map(c -> c.getId() != originalValues.getCourseId()).orElse(true);
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    @NonNull
    private static Function<Resources, CharSequence> calculateOverview(@NonNull Optional<LocalDate> goalDate, @NonNull Optional<LocalDate> completionDate, @NonNull AssessmentStatus status) {
        return completionDate.map(c ->
                goalDate.map(g -> (Function<Resources, CharSequence>) resources -> Html.fromHtml(resources.getString(R.string.html_format_assessment_overview_completed,
                        resources.getString(status.displayResourceId()), completionDate, goalDate), Html.TO_HTML_PARAGRAPH_LINES_CONSECUTIVE))
                        .orElseGet(() ->
                                resources -> Html.fromHtml(resources.getString(R.string.html_format_assessment_overview_completed_no_goal, resources.getString(status.displayResourceId()),
                                        completionDate), Html.TO_HTML_PARAGRAPH_LINES_CONSECUTIVE))
        ).orElseGet(() -> {
            if (status == AssessmentStatus.NOT_STARTED) {
                return goalDate.map(g -> (Function<Resources, CharSequence>) resources -> Html.fromHtml(resources.getString(R.string.html_format_assessment_overview_goal, resources.getString(status.displayResourceId()),
                        goalDate), Html.TO_HTML_PARAGRAPH_LINES_CONSECUTIVE))
                        .orElseGet(() -> resources -> Html.fromHtml(resources.getString(R.string.html_format_assessment_overview_no_goal, resources.getString(status.displayResourceId())), Html.TO_HTML_PARAGRAPH_LINES_CONSECUTIVE));
            }
            return goalDate.map(g -> (Function<Resources, CharSequence>) resources -> Html.fromHtml(resources.getString(R.string.html_format_assessment_overview_completed_required, resources.getString(status.displayResourceId()),
                    goalDate), Html.TO_HTML_PARAGRAPH_LINES_CONSECUTIVE))
                    .orElseGet(() -> resources -> Html.fromHtml(resources.getString(R.string.html_format_assessment_overview_completed_required_no_goal, resources.getString(status.displayResourceId())),
                            Html.TO_HTML_PARAGRAPH_LINES_CONSECUTIVE));
        });
    }

    public Completable getInitializedCompletable() {
        return initializedCompletable;
    }

    public LiveData<AssessmentDetails> getOriginalValuesLiveData() {
        return originalValuesLiveData.getLiveData();
    }

    public LiveData<Function<Resources, CharSequence>> getTitleFactoryLiveData() {
        return titleFactoryLiveData.getLiveData();
    }

    public LiveData<String> getSubTitleLiveData() {
        return subTitleLiveData.getLiveData();
    }

    public LiveData<AbstractCourseEntity<?>> getSelectedCourseLiveData() {
        return selectedCourseLiveData.getLiveData();
    }

    public LiveData<Boolean> getCanSaveLiveData() {
        return canSaveLiveData.getLiveData();
    }

    public LiveData<Boolean> getCanShareLiveData() {
        return canShareLiveData.getLiveData();
    }

    public LiveData<Boolean> getCodeValidLiveData() {
        return codeValidLiveData.getLiveData();
    }

    public LiveData<Boolean> getCourseValidLiveData() {
        return courseValidLiveData.getLiveData();
    }

    private void observeAlertsByAssessmentId(long assessmentId) {
        try {
            if (null != alertsObserving) {
                if (alertsObserving.first == assessmentId) {
                    return;
                }
                compositeDisposable.remove(alertsObserving.second);
                alertsObserving = null;
            }
        } finally {
            if (ID_NEW != assessmentId) {
                alertsObserving = new Pair<>(assessmentId, dbLoader.getAlertsObservableByAssessmentId(assessmentId).subscribe(this::onAlertsLoaded));
                compositeDisposable.add(alertsObserving.second);
            } else {
                assessmentAlertsLiveData.postValue(Collections.emptyList());
                alertsObserving = null;
            }
        }
    }

    private void onAlertsLoaded(List<AssessmentAlert> assessmentAlerts) {
        for (AssessmentAlert a : assessmentAlerts) {
            a.calculate(this);
        }
        assessmentAlertsLiveData.postValue(assessmentAlerts);
    }

    public LiveData<List<AssessmentAlert>> getAllAlerts() {
        return assessmentAlertsLiveData.getLiveData();
    }

    public LiveData<Function<Resources, CharSequence>> getOverviewFactoryLiveData() {
        return overviewFactoryLiveData.getLiveData();
    }

    @NonNull
    public LiveData<LocalDate> getGoalDateLiveData() {
        return goalDateLiveData.getLiveData();
    }

    @NonNull
    public LiveData<LocalDate> getCompletionDateLiveData() {
        return completionDateLiveData.getLiveData();
    }

    public long getId() {
        return Objects.requireNonNull(originalValues.getValue()).getId();
    }

    public synchronized void setSelectedCourse(AbstractCourseEntity<?> selectedCourse) {
        Log.d(LOG_TAG, "Enter setSelectedCourse(" + selectedCourse + ")");
        this.selectedCourse.onNext(Optional.ofNullable(selectedCourse));
    }

    @NonNull
    public String getCode() {
        return Objects.requireNonNull(code.getValue());
    }

    public void setCode(String code) {
        this.code.onNext((null == code) ? "" : code);
    }

    @NonNull
    public String getName() {
        return Objects.requireNonNull(name.getValue());
    }

    public void setName(String name) {
        this.name.onNext((null == name) ? "" : name);
    }

    @NonNull
    public AssessmentStatus getStatus() {
        return Objects.requireNonNull(status.getValue());
    }

    public void setStatus(AssessmentStatus status) {
        this.status.onNext((null == status) ? AssessmentStatus.NOT_STARTED : status);
    }

    @Nullable
    public LocalDate getGoalDate() {
        return Objects.requireNonNull(goalDate.getValue()).orElse(null);
    }

    public void setGoalDate(LocalDate goalDate) {
        this.goalDate.onNext(Optional.ofNullable(goalDate));
    }

    @Nullable
    public LocalDate getCompletionDate() {
        return Objects.requireNonNull(completionDate.getValue()).orElse(null);
    }

    public void setCompletionDate(LocalDate completionDate) {
        this.completionDate.onNext(Optional.ofNullable(completionDate));
    }

    @NonNull
    public AssessmentType getType() {
        return Objects.requireNonNull(type.getValue());
    }

    public void setType(AssessmentType type) {
        this.type.onNext((null == type) ? AssessmentType.OBJECTIVE_ASSESSMENT : type);
    }

    @NonNull
    public String getNotes() {
        return Objects.requireNonNull(notes.getValue());
    }

    public void setNotes(String notes) {
        this.notes.onNext((null == notes) ? "" : notes);
    }

    public boolean isFromInitializedState() {
        return fromInitializedState;
    }

    public LiveData<Boolean> getChangedLiveData() {
        return changedLiveData.getLiveData();
    }

    @NonNull
    public Single<List<TermCourseListItem>> loadCourses() {
        return dbLoader.loadCoursesByTermId(Objects.requireNonNull(originalValues.getValue()).getTermId());
    }

    public synchronized Single<AssessmentDetails> initializeViewModelState(@Nullable Bundle savedInstanceState, Supplier<Bundle> getArguments) {
        fromInitializedState = null != savedInstanceState && savedInstanceState.getBoolean(STATE_KEY_STATE_INITIALIZED, false);
        Bundle state = (fromInitializedState) ? savedInstanceState : getArguments.get();
        if (null != state) {
            Log.d(LOG_TAG, (fromInitializedState) ? "Restoring currentValues from saved state" : "Initializing currentValues from arguments");
            if (!fromInitializedState) {
                if (state.containsKey(EXTRA_KEY_ASSESSMENT_ID)) {
                    return dbLoader.getAssessmentById(state.getLong(EXTRA_KEY_ASSESSMENT_ID)).doOnSuccess(this::onEntityLoadedFromDb);
                }
                String key = IdIndexedEntity.stateKey(AppDb.TABLE_NAME_ASSESSMENTS, Assessment.COLNAME_GOAL_DATE, false);
                return dbLoader.createAssessmentForCourse(state.getLong(IdIndexedEntity.stateKey(AppDb.TABLE_NAME_COURSES, Course.COLNAME_ID, false)),
                        (state.containsKey(key)) ? LocalDateConverter.toLocalDate(state.getLong(key)) : null).doOnSuccess(this::onEntityLoadedFromDb);
            }
            AssessmentDetails originalValues = new AssessmentDetails((AbstractCourseEntity<?>) null);
            originalValues.restoreState(state, true);
            this.originalValues.onNext(originalValues);
            String key = IdIndexedEntity.stateKey(AppDb.TABLE_NAME_COURSES, Course.COLNAME_ID, false);
            if (state.containsKey(key)) {
                CourseEntity courseEntity = new CourseEntity();
                courseEntity.restoreState(state, false);
                selectedCourse.onNext(Optional.of(courseEntity));
            }
            code.onNext(state.getString(IdIndexedEntity.stateKey(AppDb.TABLE_NAME_ASSESSMENTS, Assessment.COLNAME_CODE, false), ""));
            name.onNext(state.getString(IdIndexedEntity.stateKey(AppDb.TABLE_NAME_ASSESSMENTS, Assessment.COLNAME_NAME, false), ""));
            key = IdIndexedEntity.stateKey(AppDb.TABLE_NAME_ASSESSMENTS, Assessment.COLNAME_STATUS, false);
            status.onNext((state.containsKey(key)) ? AssessmentStatus.valueOf(state.getString(key)) : AssessmentStatusConverter.DEFAULT);
            key = IdIndexedEntity.stateKey(AppDb.TABLE_NAME_ASSESSMENTS, Assessment.COLNAME_GOAL_DATE, false);
            goalDate.onNext((state.containsKey(key)) ? Optional.of(LocalDateConverter.toLocalDate(state.getLong(key))) : Optional.empty());
            key = IdIndexedEntity.stateKey(AppDb.TABLE_NAME_ASSESSMENTS, Assessment.COLNAME_COMPLETION_DATE, false);
            completionDate.onNext((state.containsKey(key)) ? Optional.of(LocalDateConverter.toLocalDate(state.getLong(key))) : Optional.empty());
            key = IdIndexedEntity.stateKey(AppDb.TABLE_NAME_ASSESSMENTS, Assessment.COLNAME_TYPE, false);
            type.onNext((state.containsKey(key)) ? AssessmentType.valueOf(state.getString(key)) : AssessmentType.OBJECTIVE_ASSESSMENT);
            notes.onNext(state.getString(IdIndexedEntity.stateKey(AppDb.TABLE_NAME_ASSESSMENTS, Assessment.COLNAME_NOTES, false), ""));
        }
        initializedSubject.onComplete();
        return Single.just(Objects.requireNonNull(originalValues.getValue())).observeOn(AndroidSchedulers.mainThread());
    }

    public void saveViewModelState(Bundle outState) {
        outState.putBoolean(STATE_KEY_STATE_INITIALIZED, true);
        Objects.requireNonNull(selectedCourse.getValue()).ifPresent(c -> c.saveState(outState, false));
        outState.putString(IdIndexedEntity.stateKey(AppDb.TABLE_NAME_ASSESSMENTS, Assessment.COLNAME_CODE, false), code.getValue());
        outState.putString(IdIndexedEntity.stateKey(AppDb.TABLE_NAME_ASSESSMENTS, Assessment.COLNAME_NAME, false), name.getValue());
        outState.putString(IdIndexedEntity.stateKey(AppDb.TABLE_NAME_ASSESSMENTS, Assessment.COLNAME_NOTES, false), notes.getValue());
        outState.putString(IdIndexedEntity.stateKey(AppDb.TABLE_NAME_ASSESSMENTS, Assessment.COLNAME_STATUS, false), Objects.requireNonNull(status.getValue()).name());
        outState.putString(IdIndexedEntity.stateKey(AppDb.TABLE_NAME_ASSESSMENTS, Assessment.COLNAME_TYPE, false), Objects.requireNonNull(type.getValue()).name());
        Objects.requireNonNull(goalDate.getValue()).ifPresent(d ->
                outState.putLong(IdIndexedEntity.stateKey(AppDb.TABLE_NAME_ASSESSMENTS, Assessment.COLNAME_GOAL_DATE, false), LocalDateConverter.fromLocalDate(d)));
        Objects.requireNonNull(completionDate.getValue()).ifPresent(d ->
                outState.putLong(IdIndexedEntity.stateKey(AppDb.TABLE_NAME_ASSESSMENTS, Assessment.COLNAME_COMPLETION_DATE, false), LocalDateConverter.fromLocalDate(d)));
        Objects.requireNonNull(originalValues.getValue()).saveState(outState, true);
    }

    private void onEntityLoadedFromDb(AssessmentDetails entity) {
        Log.d(LOG_TAG, String.format("Loaded %s from database", entity));
        originalValues.onNext(entity);
        code.onNext(entity.getCode());
        String s = entity.getName();
        name.onNext((null == s) ? "" : s);
        status.onNext(entity.getStatus());
        goalDate.onNext(Optional.ofNullable(entity.getGoalDate()));
        completionDate.onNext(Optional.ofNullable(entity.getCompletionDate()));
        type.onNext(entity.getType());
        notes.onNext(entity.getNotes());
        selectedCourse.onNext(Optional.ofNullable(entity.getCourse()));
        initializedSubject.onComplete();
    }

    public synchronized Single<ResourceMessageResult> save(boolean ignoreWarnings) {
        return Objects.requireNonNull(selectedCourse.getValue()).map(c -> {
            AssessmentEntity entity = new AssessmentEntity(originalValues.getValue());
            entity.setCode(code.getValue());
            entity.setCompletionDate(Objects.requireNonNull(completionDate.getValue()).orElse(null));
            entity.setCourseId(c.getId());
            entity.setGoalDate(Objects.requireNonNull(goalDate.getValue()).orElse(null));
            entity.setName(name.getValue());
            entity.setStatus(status.getValue());
            entity.setType(type.getValue());
            entity.setNotes(notes.getValue());
            return dbLoader.saveAssessment(entity, ignoreWarnings).doOnSuccess(m -> {
                if (m.isSucceeded()) {
                    AssessmentDetails originalValues = new AssessmentDetails((AbstractCourseEntity<?>) null);
                    originalValues.applyChanges(entity, c);
                    this.originalValues.onNext(originalValues);
                }
            });
        }).orElseGet(() -> Single.just(ValidationMessage.ofSingleError(R.string.message_course_not_selected)).observeOn(AndroidSchedulers.mainThread()));
    }

    public Single<Integer> delete() {
        Log.d(LOG_TAG, "Enter delete");
        AssessmentEntity entity = new AssessmentEntity(originalValues.getValue());
        return dbLoader.deleteAssessment(entity).doOnError(throwable -> Log.e(getClass().getName(),
                "Error deleting course", throwable));
    }

    public LiveData<TermEntity> getCurrentTermLiveData() {
        return currentTermLiveData.getLiveData();
    }

    public LiveData<MentorEntity> getCurrentMentorLiveData() {
        return currentMentorLiveData.getLiveData();
    }

}
