package Erwine.Leonard.T.wguscheduler356334.ui.assessment;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.util.Log;

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
import Erwine.Leonard.T.wguscheduler356334.util.WguSchedulerViewModel;
import Erwine.Leonard.T.wguscheduler356334.util.Workers;
import Erwine.Leonard.T.wguscheduler356334.util.validation.ResourceMessageResult;
import Erwine.Leonard.T.wguscheduler356334.util.validation.ValidationMessage;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.CompletableSubject;

import static Erwine.Leonard.T.wguscheduler356334.db.LocalDateConverter.FULL_FORMATTER;
import static Erwine.Leonard.T.wguscheduler356334.entity.IdIndexedEntity.ID_NEW;

public class EditAssessmentViewModel extends WguSchedulerViewModel {
    private static final String LOG_TAG = MainActivity.getLogTag(EditAssessmentViewModel.class);
    static final String STATE_KEY_STATE_INITIALIZED = "state_initialized";
    public static final String EXTRA_KEY_ASSESSMENT_ID = IdIndexedEntity.stateKey(AppDb.TABLE_NAME_ASSESSMENTS, Assessment.COLNAME_ID, false);

    private final DbLoader dbLoader;
    private final BehaviorSubject<String> codeSubject;
    private final BehaviorSubject<String> nameSubject;
    private final BehaviorSubject<AssessmentStatus> statusSubject;
    private final BehaviorSubject<Optional<LocalDate>> goalDateSubject;
    private final BehaviorSubject<Optional<LocalDate>> completionDateSubject;
    private final BehaviorSubject<AssessmentType> typeSubject;
    private final BehaviorSubject<String> notesSubject;
    private final BehaviorSubject<Optional<AbstractCourseEntity<?>>> selectedCourseSubject;
    private final BehaviorSubject<AssessmentDetails> originalValuesSubject;
    private final CompletableSubject initializedSubject;
    private final PrivateLiveData<Function<Resources, String>> titleFactoryLiveData;
    private final PrivateLiveData<String> subTitleLiveData;
    private final PrivateLiveData<Function<Resources, Spanned>> overviewFactoryLiveData;
    private final PrivateLiveData<Boolean> codeValidLiveData;
    private final PrivateLiveData<LocalDate> goalDateLiveData;
    private final PrivateLiveData<LocalDate> completionDateLiveData;
    private final PrivateLiveData<AbstractCourseEntity<?>> selectedCourseLiveData;
    private final PrivateLiveData<TermEntity> currentTermLiveData;
    private final PrivateLiveData<MentorEntity> currentMentorLiveData;
    private final PrivateLiveData<Boolean> canShareLiveData;
    private final PrivateLiveData<Function<Resources, CharSequence>> courseDisplayLiveData;
    private final PrivateLiveData<Boolean> canSaveLiveData;
    private final PrivateLiveData<Boolean> changedLiveData;
    private final PrivateLiveData<AssessmentDetails> originalValuesLiveData;
    private final CompositeDisposable compositeDisposable;
    private final Completable initializedCompletable;
    private final PrivateLiveData<Integer> typeDisplayLiveData;
    private final PrivateLiveData<Integer> statusDisplayLiveData;
    private final PrivateLiveData<String> goalDateDisplayLiveData;
    private final PrivateLiveData<String> completionDateDisplayLiveData;
    private final PrivateLiveData<Boolean> showGoalDateCloseIconLiveData;
    private final PrivateLiveData<Boolean> showCompletionDateCloseIconLiveData;
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
        compositeDisposable = new CompositeDisposable();
        selectedCourseSubject = BehaviorSubject.createDefault(Optional.empty());
        notesSubject = BehaviorSubject.createDefault("");
        typeSubject = BehaviorSubject.createDefault(AssessmentType.OBJECTIVE_ASSESSMENT);
        completionDateSubject = BehaviorSubject.createDefault(Optional.empty());
        goalDateSubject = BehaviorSubject.createDefault(Optional.empty());
        statusSubject = BehaviorSubject.createDefault(AssessmentStatus.NOT_STARTED);
        nameSubject = BehaviorSubject.createDefault("");
        codeSubject = BehaviorSubject.createDefault("");
        originalValuesSubject = BehaviorSubject.createDefault(new AssessmentDetails((AbstractCourseEntity<?>) null));
        initializedSubject = CompletableSubject.create();
        initializedCompletable = initializedSubject.observeOn(AndroidSchedulers.mainThread());

        Scheduler computationScheduler = Schedulers.computation();

        Observable<Optional<TermEntity>> currentTermObservable = selectedCourseSubject.flatMap(o ->
                o.map(t -> dbLoader.getTermByIdForComputation(t.getTermId()).doOnError(throwable ->
                        Log.e(LOG_TAG, "Error loading term", throwable)).map(Optional::of)).orElseGet(() -> Single.just(Optional.empty())).toObservable()
        );
        Observable<Optional<MentorEntity>> currentMentorObservable = selectedCourseSubject.flatMap(o ->
                o.<Single<Optional<MentorEntity>>>map(t -> {
                    Long id = t.getMentorId();
                    if (null == id) {
                        return Single.just(Optional.empty());
                    }
                    return dbLoader.getMentorByIdForComputation(t.getTermId()).doOnError(throwable ->
                            Log.e(LOG_TAG, "Error loading mentor", throwable)).map(Optional::of);
                }).orElseGet(() -> Single.just(Optional.empty())).toObservable()
        );
        Observable<String> normalizedCodeObservable = codeSubject.observeOn(computationScheduler).map(Workers.asCached(AbstractAssessmentEntity.SINGLE_LINE_NORMALIZER::apply));
        Observable<String> normalizedNameObservable = nameSubject.observeOn(computationScheduler).map(Workers.asCached(AbstractAssessmentEntity.SINGLE_LINE_NORMALIZER::apply));
        Observable<String> normalizedNotesObservable = notesSubject.observeOn(computationScheduler).map(Workers.asCached(AbstractNotedEntity.MULTI_LINE_NORMALIZER::apply));
        Observable<AssessmentType> typeObservable = typeSubject.observeOn(computationScheduler);
        Observable<Function<Resources, String>> viewTitleObservable = Observable.combineLatest(typeObservable, normalizedCodeObservable,
                Workers.asCached(
                        (type, code) -> resources -> resources.getString(R.string.format_value_colon_value, resources.getString(type.displayResourceId()), code)
                )
        );
        Observable<Optional<LocalDate>> goalDateObservable = goalDateSubject.observeOn(computationScheduler);
        Observable<Optional<LocalDate>> completionDateObservable = completionDateSubject.observeOn(computationScheduler);
        Observable<AssessmentStatus> statusObservable = statusSubject.observeOn(computationScheduler);
        Observable<Function<Resources, Spanned>> overviewObservable = Observable.combineLatest(goalDateObservable, completionDateObservable, statusObservable,
                Workers.asCached(this::calculateOverview)
        );
        Observable<Optional<LocalDate>> effectiveEndObservable = Observable.combineLatest(statusObservable, completionDateObservable,
                Workers.asCached(
                        (status, completionDate) -> {
                            switch (status) {
                                case NOT_PASSED:
                                case PASSED:
                                    return completionDate;
                                default:
                                    return Optional.empty();
                            }
                        }
                )
        );
        Observable<Optional<AbstractCourseEntity<?>>> selectedCourseObservable = selectedCourseSubject.observeOn(computationScheduler);
        Observable<AssessmentDetails> originalValuesObservable = originalValuesSubject.observeOn(computationScheduler);
        Observable<Boolean> changedObservable = Observable.combineLatest(originalValuesObservable,
                normalizedCodeObservable, normalizedNameObservable, statusObservable, goalDateObservable, completionDateObservable,
                typeObservable, normalizedNotesObservable, selectedCourseObservable, Workers.asCached(this::calculateChanged));

        Observable<Boolean> codeValidObservable = normalizedCodeObservable.map(c -> !c.isEmpty());
        Observable<Boolean> courseValidObservable = selectedCourseObservable.map(Optional::isPresent);
        Observable<Boolean> validObservable = Observable.combineLatest(codeValidObservable, courseValidObservable, (code, course) -> code && course);

        Observable<Function<Resources, CharSequence>> courseDisplayObservable = selectedCourseObservable.map(o -> o.<Function<Resources, CharSequence>>map(course -> r ->
                r.getString(R.string.format_value_colon_value, course.getNumber(), course.getTitle())
        ).orElseGet(() -> r -> r.getString(R.string.label_none)));

        titleFactoryLiveData = new PrivateLiveData<>(c -> c.getString(R.string.title_activity_view_assessment));
        subTitleLiveData = new PrivateLiveData<>("");
        overviewFactoryLiveData = new PrivateLiveData<>(r -> new SpannableString(" "));
        originalValuesLiveData = new PrivateLiveData<>();
        codeValidLiveData = new PrivateLiveData<>(false);
        goalDateLiveData = new PrivateLiveData<>();
        completionDateLiveData = new PrivateLiveData<>();
        selectedCourseLiveData = new PrivateLiveData<>();
        currentTermLiveData = new PrivateLiveData<>();
        currentMentorLiveData = new PrivateLiveData<>();
        courseDisplayLiveData = new PrivateLiveData<>(r -> HtmlCompat.fromHtml(r.getString(R.string.html_none), HtmlCompat.FROM_HTML_SEPARATOR_LINE_BREAK_DIV));
        typeDisplayLiveData = new PrivateLiveData<>(AssessmentType.OBJECTIVE_ASSESSMENT.displayResourceId());
        statusDisplayLiveData = new PrivateLiveData<>(AssessmentStatus.NOT_STARTED.displayResourceId());
        goalDateDisplayLiveData = new PrivateLiveData<>("");
        completionDateDisplayLiveData = new PrivateLiveData<>("");
        showGoalDateCloseIconLiveData = new PrivateLiveData<>(false);
        showCompletionDateCloseIconLiveData = new PrivateLiveData<>(false);
        changedLiveData = new PrivateLiveData<>(false);
        canShareLiveData = new PrivateLiveData<>(false);
        canSaveLiveData = new PrivateLiveData<>(false);

        compositeDisposable.add(viewTitleObservable.subscribe(titleFactoryLiveData::postValue));
        compositeDisposable.add(overviewObservable.subscribe(overviewFactoryLiveData::postValue));
        compositeDisposable.add(normalizedNameObservable.subscribe(subTitleLiveData::postValue));
        compositeDisposable.add(codeValidObservable.subscribe(codeValidLiveData::postValue));
        compositeDisposable.add(goalDateObservable.subscribe(d -> goalDateLiveData.postValue(d.orElse(null))));
        compositeDisposable.add(effectiveEndObservable.subscribe(d -> completionDateLiveData.postValue(d.orElse(null))));
        compositeDisposable.add(selectedCourseObservable.subscribe(c -> selectedCourseLiveData.postValue(c.orElse(null))));
        compositeDisposable.add(originalValuesObservable.subscribe(originalValuesLiveData::postValue));
        compositeDisposable.add(currentTermObservable.subscribe(t -> currentTermLiveData.postValue(t.orElse(null))));
        compositeDisposable.add(currentMentorObservable.subscribe(m -> currentMentorLiveData.postValue(m.orElse(null))));
        compositeDisposable.add(selectedCourseObservable.map(o -> o.<Function<Resources, CharSequence>>map(course -> r ->
                r.getString(R.string.format_value_colon_value, course.getNumber(), course.getTitle())
        ).orElseGet(() -> r -> HtmlCompat.fromHtml(r.getString(R.string.html_none), HtmlCompat.FROM_HTML_SEPARATOR_LINE_BREAK_DIV)))
                .subscribe(courseDisplayLiveData::postValue));
        compositeDisposable.add(typeObservable.map(AssessmentType::displayResourceId).subscribe(typeDisplayLiveData::postValue));
        compositeDisposable.add(statusObservable.map(AssessmentStatus::displayResourceId).subscribe(statusDisplayLiveData::postValue));
        compositeDisposable.add(goalDateObservable.map(o -> o.map(FULL_FORMATTER::format).orElse("")).subscribe(goalDateDisplayLiveData::postValue));
        compositeDisposable.add(goalDateObservable.map(Optional::isPresent).subscribe(showGoalDateCloseIconLiveData::postValue));
        compositeDisposable.add(completionDateObservable.map(o -> o.map(FULL_FORMATTER::format).orElse("")).subscribe(completionDateDisplayLiveData::postValue));
        compositeDisposable.add(completionDateObservable.map(Optional::isPresent).subscribe(showCompletionDateCloseIconLiveData::postValue));
        compositeDisposable.add(changedObservable.subscribe(changedLiveData::postValue));
        compositeDisposable.add(Observable.combineLatest(validObservable, changedObservable, (v, c) -> v && c).subscribe(canSaveLiveData::postValue));
        compositeDisposable.add(Observable.combineLatest(validObservable, changedObservable, (v, c) -> v && !c).subscribe(canShareLiveData::postValue));
    }

    public LiveData<Function<Resources, CharSequence>> getCourseDisplayLiveData() {
        return courseDisplayLiveData;
    }

    public LiveData<Integer> getTypeDisplayLiveData() {
        return typeDisplayLiveData;
    }

    public LiveData<Integer> getStatusDisplayLiveData() {
        return statusDisplayLiveData;
    }

    public LiveData<String> getGoalDateDisplayLiveData() {
        return goalDateDisplayLiveData;
    }

    public LiveData<Boolean> getShowGoalDateCloseIconLiveData() {
        return showGoalDateCloseIconLiveData;
    }

    public LiveData<String> getCompletionDateDisplayLiveData() {
        return completionDateDisplayLiveData;
    }

    public LiveData<Boolean> getShowCompletionDateCloseIconLiveData() {
        return showCompletionDateCloseIconLiveData;
    }

    @Override
    protected void onCleared() {
        compositeDisposable.dispose();
        super.onCleared();
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private boolean calculateChanged(@NonNull AssessmentDetails originalValues, @NonNull String code, @NonNull String name, @NonNull AssessmentStatus status,
                                     Optional<LocalDate> goalDate, Optional<LocalDate> completionDate, @NonNull AssessmentType type, @NonNull String notes,
                                     Optional<AbstractCourseEntity<?>> selectedCourse) {
        return !(code.equals(originalValues.getCode()) && Objects.equals(name, originalValues.getName()) && status == originalValues.getStatus() &&
                goalDate.map(d -> d.equals(originalValues.getGoalDate())).orElseGet(() -> null == originalValues.getGoalDate()) &&
                completionDate.map(d -> d.equals(originalValues.getCompletionDate())).orElseGet(() -> null == originalValues.getCompletionDate()) &&
                type == originalValues.getType() && notes.equals(originalValues.getNotes()) &&
                selectedCourse.map(c -> c.getId() == originalValues.getCourseId()).orElseGet(() -> originalValues.getCourseId() == ID_NEW));
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    @NonNull
    public synchronized Function<Resources, Spanned> calculateOverview(@NonNull Optional<LocalDate> goalDate, @NonNull Optional<LocalDate> completionDate, @NonNull AssessmentStatus status) {
        return completionDate.map(c ->
                goalDate.map(g -> (Function<Resources, Spanned>) resources -> Html.fromHtml(resources.getString(R.string.html_format_assessment_overview_completed,
                        resources.getString(status.displayResourceId()), completionDate, goalDate), Html.TO_HTML_PARAGRAPH_LINES_CONSECUTIVE))
                        .orElseGet(() ->
                                resources -> Html.fromHtml(resources.getString(R.string.html_format_assessment_overview_completed_no_goal, resources.getString(status.displayResourceId()),
                                        completionDate), Html.TO_HTML_PARAGRAPH_LINES_CONSECUTIVE))
        ).orElseGet(() -> {
            if (status == AssessmentStatus.NOT_STARTED) {
                return goalDate.map(g -> (Function<Resources, Spanned>) resources -> Html.fromHtml(resources.getString(R.string.html_format_assessment_overview_goal, resources.getString(status.displayResourceId()),
                        goalDate), Html.TO_HTML_PARAGRAPH_LINES_CONSECUTIVE))
                        .orElseGet(() -> resources -> Html.fromHtml(resources.getString(R.string.html_format_assessment_overview_no_goal, resources.getString(status.displayResourceId())), Html.TO_HTML_PARAGRAPH_LINES_CONSECUTIVE));
            }
            return goalDate.map(g -> (Function<Resources, Spanned>) resources -> Html.fromHtml(resources.getString(R.string.html_format_assessment_overview_completed_required, resources.getString(status.displayResourceId()),
                    goalDate), Html.TO_HTML_PARAGRAPH_LINES_CONSECUTIVE))
                    .orElseGet(() -> resources -> Html.fromHtml(resources.getString(R.string.html_format_assessment_overview_completed_required_no_goal, resources.getString(status.displayResourceId())),
                            Html.TO_HTML_PARAGRAPH_LINES_CONSECUTIVE));
        });
    }

    public Completable getInitializedCompletable() {
        return initializedCompletable;
    }

    public LiveData<AssessmentDetails> getOriginalValuesLiveData() {
        return originalValuesLiveData;
    }

    public LiveData<Function<Resources, String>> getTitleFactoryLiveData() {
        return titleFactoryLiveData;
    }

    public LiveData<String> getSubTitleLiveData() {
        return subTitleLiveData;
    }

    public LiveData<AbstractCourseEntity<?>> getSelectedCourseLiveData() {
        return selectedCourseLiveData;
    }

    public LiveData<Boolean> getCanSaveLiveData() {
        return canSaveLiveData;
    }

    public LiveData<Boolean> getCanShareLiveData() {
        return canShareLiveData;
    }

    public LiveData<Boolean> getCodeValidLiveData() {
        return codeValidLiveData;
    }

    public LiveData<List<AssessmentAlert>> getAllAlerts() {
        long id = Objects.requireNonNull(originalValuesSubject.getValue()).getId();
        if (id != ID_NEW) {
            return dbLoader.getAlertsByAssessmentId(id);
        }
        PrivateLiveData<List<AssessmentAlert>> result = new PrivateLiveData<>();
        result.postValue(Collections.emptyList());
        return result;
    }

    public LiveData<Function<Resources, Spanned>> getOverviewFactoryLiveData() {
        return overviewFactoryLiveData;
    }

    @NonNull
    public LiveData<LocalDate> getGoalDateLiveData() {
        return goalDateLiveData;
    }

    @NonNull
    public LiveData<LocalDate> getCompletionDateLiveData() {
        return completionDateLiveData;
    }

    public long getId() {
        return Objects.requireNonNull(originalValuesSubject.getValue()).getId();
    }

    public synchronized void setSelectedCourse(AbstractCourseEntity<?> selectedCourse) {
        Log.d(LOG_TAG, "Enter setSelectedCourse(" + selectedCourse + ")");
        selectedCourseSubject.onNext(Optional.ofNullable(selectedCourse));
    }

    @NonNull
    public String getCode() {
        return Objects.requireNonNull(codeSubject.getValue());
    }

    public void setCode(String code) {
        codeSubject.onNext((null == code) ? "" : code);
    }

    @NonNull
    public String getName() {
        return Objects.requireNonNull(nameSubject.getValue());
    }

    public void setName(String name) {
        nameSubject.onNext((null == name) ? "" : name);
    }

    @NonNull
    public AssessmentStatus getStatus() {
        return Objects.requireNonNull(statusSubject.getValue());
    }

    public void setStatus(AssessmentStatus status) {
        statusSubject.onNext((null == status) ? AssessmentStatus.NOT_STARTED : status);
    }

    @Nullable
    public LocalDate getGoalDate() {
        return Objects.requireNonNull(goalDateSubject.getValue()).orElse(null);
    }

    public void setGoalDate(LocalDate goalDate) {
        goalDateSubject.onNext(Optional.ofNullable(goalDate));
    }

    @Nullable
    public LocalDate getCompletionDate() {
        return Objects.requireNonNull(completionDateSubject.getValue()).orElse(null);
    }

    public void setCompletionDate(LocalDate completionDate) {
        completionDateSubject.onNext(Optional.ofNullable(completionDate));
    }

    @NonNull
    public AssessmentType getType() {
        return Objects.requireNonNull(typeSubject.getValue());
    }

    public void setType(AssessmentType type) {
        typeSubject.onNext((null == type) ? AssessmentType.OBJECTIVE_ASSESSMENT : type);
    }

    @NonNull
    public String getNotes() {
        return Objects.requireNonNull(notesSubject.getValue());
    }

    public void setNotes(String notes) {
        notesSubject.onNext((null == notes) ? "" : notes);
    }

    public boolean isFromInitializedState() {
        return fromInitializedState;
    }

    public LiveData<Boolean> getChangedLiveData() {
        return changedLiveData;
    }

    @NonNull
    public Single<List<TermCourseListItem>> loadCourses() {
        return dbLoader.loadCoursesByTermId(Objects.requireNonNull(originalValuesSubject.getValue()).getTermId());
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
            originalValuesSubject.onNext(originalValues);
            String key = IdIndexedEntity.stateKey(AppDb.TABLE_NAME_COURSES, Course.COLNAME_ID, false);
            if (state.containsKey(key)) {
                CourseEntity courseEntity = new CourseEntity();
                courseEntity.restoreState(state, false);
                selectedCourseSubject.onNext(Optional.of(courseEntity));
            }
            codeSubject.onNext(state.getString(IdIndexedEntity.stateKey(AppDb.TABLE_NAME_ASSESSMENTS, Assessment.COLNAME_CODE, false), ""));
            nameSubject.onNext(state.getString(IdIndexedEntity.stateKey(AppDb.TABLE_NAME_ASSESSMENTS, Assessment.COLNAME_NAME, false), ""));
            key = IdIndexedEntity.stateKey(AppDb.TABLE_NAME_ASSESSMENTS, Assessment.COLNAME_STATUS, false);
            statusSubject.onNext((state.containsKey(key)) ? AssessmentStatus.valueOf(state.getString(key)) : AssessmentStatusConverter.DEFAULT);
            key = IdIndexedEntity.stateKey(AppDb.TABLE_NAME_ASSESSMENTS, Assessment.COLNAME_GOAL_DATE, false);
            goalDateSubject.onNext((state.containsKey(key)) ? Optional.of(LocalDateConverter.toLocalDate(state.getLong(key))) : Optional.empty());
            key = IdIndexedEntity.stateKey(AppDb.TABLE_NAME_ASSESSMENTS, Assessment.COLNAME_COMPLETION_DATE, false);
            completionDateSubject.onNext((state.containsKey(key)) ? Optional.of(LocalDateConverter.toLocalDate(state.getLong(key))) : Optional.empty());
            key = IdIndexedEntity.stateKey(AppDb.TABLE_NAME_ASSESSMENTS, Assessment.COLNAME_TYPE, false);
            typeSubject.onNext((state.containsKey(key)) ? AssessmentType.valueOf(state.getString(key)) : AssessmentType.OBJECTIVE_ASSESSMENT);
            notesSubject.onNext(state.getString(IdIndexedEntity.stateKey(AppDb.TABLE_NAME_ASSESSMENTS, Assessment.COLNAME_NOTES, false), ""));
        }
        initializedSubject.onComplete();
        return Single.just(Objects.requireNonNull(originalValuesSubject.getValue())).observeOn(AndroidSchedulers.mainThread());
    }

    public void saveViewModelState(Bundle outState) {
        outState.putBoolean(STATE_KEY_STATE_INITIALIZED, true);
        Objects.requireNonNull(selectedCourseSubject.getValue()).ifPresent(c -> c.saveState(outState, false));
        outState.putString(IdIndexedEntity.stateKey(AppDb.TABLE_NAME_ASSESSMENTS, Assessment.COLNAME_CODE, false), codeSubject.getValue());
        outState.putString(IdIndexedEntity.stateKey(AppDb.TABLE_NAME_ASSESSMENTS, Assessment.COLNAME_NAME, false), nameSubject.getValue());
        outState.putString(IdIndexedEntity.stateKey(AppDb.TABLE_NAME_ASSESSMENTS, Assessment.COLNAME_NOTES, false), notesSubject.getValue());
        outState.putString(IdIndexedEntity.stateKey(AppDb.TABLE_NAME_ASSESSMENTS, Assessment.COLNAME_STATUS, false), Objects.requireNonNull(statusSubject.getValue()).name());
        outState.putString(IdIndexedEntity.stateKey(AppDb.TABLE_NAME_ASSESSMENTS, Assessment.COLNAME_TYPE, false), Objects.requireNonNull(typeSubject.getValue()).name());
        Objects.requireNonNull(goalDateSubject.getValue()).ifPresent(d ->
                outState.putLong(IdIndexedEntity.stateKey(AppDb.TABLE_NAME_ASSESSMENTS, Assessment.COLNAME_GOAL_DATE, false), LocalDateConverter.fromLocalDate(d)));
        Objects.requireNonNull(completionDateSubject.getValue()).ifPresent(d ->
                outState.putLong(IdIndexedEntity.stateKey(AppDb.TABLE_NAME_ASSESSMENTS, Assessment.COLNAME_COMPLETION_DATE, false), LocalDateConverter.fromLocalDate(d)));
        Objects.requireNonNull(originalValuesSubject.getValue()).saveState(outState, true);
    }

    private void onEntityLoadedFromDb(AssessmentDetails entity) {
        Log.d(LOG_TAG, String.format("Loaded %s from database", entity));
        originalValuesSubject.onNext(entity);
        codeSubject.onNext(entity.getCode());
        String s = entity.getName();
        nameSubject.onNext((null == s) ? "" : s);
        statusSubject.onNext(entity.getStatus());
        goalDateSubject.onNext(Optional.ofNullable(entity.getGoalDate()));
        completionDateSubject.onNext(Optional.ofNullable(entity.getCompletionDate()));
        typeSubject.onNext(entity.getType());
        notesSubject.onNext(entity.getNotes());
        selectedCourseSubject.onNext(Optional.ofNullable(entity.getCourse()));
        initializedSubject.onComplete();
    }

    public synchronized Single<ResourceMessageResult> save(boolean ignoreWarnings) {
        return Objects.requireNonNull(selectedCourseSubject.getValue()).map(c -> {
            AssessmentEntity entity = new AssessmentEntity(originalValuesSubject.getValue());
            entity.setCode(codeSubject.getValue());
            entity.setCompletionDate(Objects.requireNonNull(completionDateSubject.getValue()).orElse(null));
            entity.setCourseId(c.getId());
            entity.setGoalDate(Objects.requireNonNull(goalDateSubject.getValue()).orElse(null));
            entity.setName(nameSubject.getValue());
            entity.setStatus(statusSubject.getValue());
            entity.setType(typeSubject.getValue());
            entity.setNotes(notesSubject.getValue());
            return dbLoader.saveAssessment(entity, ignoreWarnings).doOnSuccess(m -> {
                if (m.isSucceeded()) {
                    AssessmentDetails originalValues = new AssessmentDetails((AbstractCourseEntity<?>) null);
                    originalValues.applyChanges(entity, c);
                    originalValuesSubject.onNext(originalValues);
                }
            });
        }).orElseGet(() -> Single.just(ValidationMessage.ofSingleError(R.string.message_course_not_selected)).observeOn(AndroidSchedulers.mainThread()));
    }

    public Single<Integer> delete() {
        Log.d(LOG_TAG, "Enter delete");
        AssessmentEntity entity = new AssessmentEntity(originalValuesSubject.getValue());
        return dbLoader.deleteAssessment(entity).doOnError(throwable -> Log.e(getClass().getName(),
                "Error deleting course", throwable));
    }

    public LiveData<TermEntity> getCurrentTermLiveData() {
        return currentTermLiveData;
    }

    public LiveData<MentorEntity> getCurrentMentorLiveData() {
        return currentMentorLiveData;
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
