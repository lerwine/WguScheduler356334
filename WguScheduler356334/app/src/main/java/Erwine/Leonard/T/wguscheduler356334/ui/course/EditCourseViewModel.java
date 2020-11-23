package Erwine.Leonard.T.wguscheduler356334.ui.course;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import Erwine.Leonard.T.wguscheduler356334.AddCourseActivity;
import Erwine.Leonard.T.wguscheduler356334.MainActivity;
import Erwine.Leonard.T.wguscheduler356334.R;
import Erwine.Leonard.T.wguscheduler356334.ViewCourseActivity;
import Erwine.Leonard.T.wguscheduler356334.db.AppDb;
import Erwine.Leonard.T.wguscheduler356334.db.DbLoader;
import Erwine.Leonard.T.wguscheduler356334.db.LocalDateConverter;
import Erwine.Leonard.T.wguscheduler356334.entity.AbstractEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.AbstractNotedEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.IdIndexedEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.alert.AlertListItem;
import Erwine.Leonard.T.wguscheduler356334.entity.assessment.AbstractAssessmentEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.assessment.AssessmentEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.course.Course;
import Erwine.Leonard.T.wguscheduler356334.entity.course.CourseAlert;
import Erwine.Leonard.T.wguscheduler356334.entity.course.CourseDetails;
import Erwine.Leonard.T.wguscheduler356334.entity.course.CourseEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.course.CourseStatus;
import Erwine.Leonard.T.wguscheduler356334.entity.course.TermCourseListItem;
import Erwine.Leonard.T.wguscheduler356334.entity.mentor.AbstractMentorEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.mentor.Mentor;
import Erwine.Leonard.T.wguscheduler356334.entity.mentor.MentorEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.mentor.MentorListItem;
import Erwine.Leonard.T.wguscheduler356334.entity.term.AbstractTermEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.term.Term;
import Erwine.Leonard.T.wguscheduler356334.entity.term.TermEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.term.TermListItem;
import Erwine.Leonard.T.wguscheduler356334.util.BehaviorComputationSource;
import Erwine.Leonard.T.wguscheduler356334.util.BinaryAlternate;
import Erwine.Leonard.T.wguscheduler356334.util.SubscribingLiveDataWrapper;
import Erwine.Leonard.T.wguscheduler356334.util.ToStringBuilder;
import Erwine.Leonard.T.wguscheduler356334.util.WguSchedulerViewModel;
import Erwine.Leonard.T.wguscheduler356334.util.Workers;
import Erwine.Leonard.T.wguscheduler356334.util.validation.MessageLevel;
import Erwine.Leonard.T.wguscheduler356334.util.validation.ResourceMessageFactory;
import Erwine.Leonard.T.wguscheduler356334.util.validation.ResourceMessageResult;
import Erwine.Leonard.T.wguscheduler356334.util.validation.ValidationMessage;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.CompletableSubject;

import static Erwine.Leonard.T.wguscheduler356334.db.LocalDateConverter.LONG_FORMATTER;
import static Erwine.Leonard.T.wguscheduler356334.entity.IdIndexedEntity.ID_NEW;
import static Erwine.Leonard.T.wguscheduler356334.ui.course.EditCourseFragment.NUMBER_FORMATTER;
import static android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE;

/**
 * View model shared by {@link Erwine.Leonard.T.wguscheduler356334.ViewCourseActivity}, {@link Erwine.Leonard.T.wguscheduler356334.AddCourseActivity},
 * {@link Erwine.Leonard.T.wguscheduler356334.ui.assessment.AssessmentListFragment} and {@link EditCourseFragment}
 */
public class EditCourseViewModel extends WguSchedulerViewModel {
    private static final String LOG_TAG = MainActivity.getLogTag(EditCourseViewModel.class);
    static final String STATE_KEY_STATE_INITIALIZED = "state_initialized";
    public static final String EXTRA_KEY_COURSE_ID = IdIndexedEntity.stateKey(AppDb.TABLE_NAME_COURSES, Course.COLNAME_ID, false);
    public static final String EXTRA_KEY_TERM_ID = IdIndexedEntity.stateKey(AppDb.TABLE_NAME_TERMS, Term.COLNAME_ID, false);
    public static final String EXTRA_KEY_EXPECTED_START = IdIndexedEntity.stateKey(AppDb.TABLE_NAME_COURSES, Course.COLNAME_EXPECTED_START, false);
    public static final String STATE_KEY_COMPETENCY_UNITS_TEXT = "t:" + IdIndexedEntity.stateKey(AppDb.TABLE_NAME_COURSES, Course.COLNAME_COMPETENCY_UNITS, false);
    private final SubscribingLiveDataWrapper<CourseDetails> originalValuesLiveData;

    public static void startAddCourseActivity(@NonNull Activity activity, int requestCode, long termId, @NonNull LocalDate expectedStart) {
        Log.d(LOG_TAG, String.format("Enter startAddCourseActivity(context, %d, %s)", termId, expectedStart));
        Intent intent = new Intent(activity, AddCourseActivity.class);
        intent.putExtra(EXTRA_KEY_TERM_ID, termId);
        intent.putExtra(EXTRA_KEY_EXPECTED_START, LocalDateConverter.fromLocalDate(expectedStart));
        activity.startActivityForResult(intent, requestCode);
    }

    public static void startViewCourseActivity(@NonNull Context context, long courseId) {
        Log.d(LOG_TAG, String.format("Enter startViewCourseActivity(context, %d)", courseId));
        Intent intent = new Intent(context, ViewCourseActivity.class);
        intent.putExtra(EXTRA_KEY_COURSE_ID, courseId);
        context.startActivity(intent);
    }

    private final DbLoader dbLoader;
    private final CompositeDisposable compositeDisposable;
    private final BehaviorComputationSource<CourseDetails> originalValuesSource;
    private final BehaviorComputationSource<String> numberSource;
    private final BehaviorComputationSource<String> titleSource;
    private final BehaviorComputationSource<CourseStatus> statusSource;
    private final BehaviorComputationSource<String> competencyUnitsTextSource;
    private final BehaviorComputationSource<Optional<LocalDate>> expectedStartSource;
    private final BehaviorComputationSource<Optional<LocalDate>> actualStartSource;
    private final BehaviorComputationSource<Optional<LocalDate>> expectedEndSource;
    private final BehaviorComputationSource<Optional<LocalDate>> actualEndSource;
    private final BehaviorComputationSource<Optional<AbstractMentorEntity<?>>> selectedMentorSource;
    private final BehaviorComputationSource<Optional<AbstractTermEntity<?>>> selectedTermSource;
    private final BehaviorComputationSource<String> notesSource;
    private final BehaviorComputationSource<List<TermCourseListItem>> coursesForTermSource;
    private final BehaviorComputationSource<List<AssessmentEntity>> assessmentsSource;
    private final BehaviorComputationSource<List<CourseAlert>> courseAlertsSource;
    private final CompletableSubject initializedSubject;
    private final Completable initializedCompletable;

    private final LiveData<List<TermListItem>> termOptionsLiveData;
    private final LiveData<List<MentorListItem>> mentorOptionsLiveData;
    private final SubscribingLiveDataWrapper<List<AssessmentEntity>> assessmentsObserver;
    private final SubscribingLiveDataWrapper<List<CourseAlert>> courseAlertsObserver;
    private final SubscribingLiveDataWrapper<LocalDate> effectiveStartObserver;
    private final SubscribingLiveDataWrapper<LocalDate> effectiveEndObserver;
    private final SubscribingLiveDataWrapper<Boolean> termValidObserver;
    private final SubscribingLiveDataWrapper<Boolean> numberValidObserver;
    private final SubscribingLiveDataWrapper<Boolean> titleValidObserver;
    private final SubscribingLiveDataWrapper<Optional<ResourceMessageFactory>> expectedStartValidationMessageObserver;
    private final SubscribingLiveDataWrapper<Optional<ResourceMessageFactory>> expectedEndValidationMessageObserver;
    private final SubscribingLiveDataWrapper<Optional<ResourceMessageFactory>> actualStartValidationMessageObserver;
    private final SubscribingLiveDataWrapper<Optional<ResourceMessageFactory>> actualEndValidationMessageObserver;
    private final SubscribingLiveDataWrapper<Optional<ResourceMessageFactory>> competencyUnitsValidationMessageObserver;
    private final SubscribingLiveDataWrapper<Optional<Integer>> competencyUnitsValueObserver;
    private final SubscribingLiveDataWrapper<Function<Resources, CharSequence>> titleFactoryObserver;
    private final SubscribingLiveDataWrapper<String> subTitleObserver;
    private final SubscribingLiveDataWrapper<Function<Resources, CharSequence>> overviewFactoryObserver;
    private final SubscribingLiveDataWrapper<Boolean> canSaveObserver;
    private final SubscribingLiveDataWrapper<Boolean> canShareObserver;
    private final SubscribingLiveDataWrapper<Boolean> hasChangesObserver;
    private Pair<Long, Disposable> coursesForTermObserving;
    private Pair<Long, Disposable> courseAlertsObserving;
    private Pair<Long, Disposable> assessmentsObserving;
    private boolean fromInitializedState;

    public EditCourseViewModel(@NonNull Application application) {
        super(application);
        dbLoader = DbLoader.getInstance(getApplication());
        numberSource = BehaviorComputationSource.createDefault("");
        titleSource = BehaviorComputationSource.createDefault("");
        statusSource = BehaviorComputationSource.createDefault(CourseStatus.UNPLANNED);
        competencyUnitsTextSource = BehaviorComputationSource.createDefault("");
        expectedStartSource = BehaviorComputationSource.createDefault(Optional.empty());
        actualStartSource = BehaviorComputationSource.createDefault(Optional.empty());
        expectedEndSource = BehaviorComputationSource.createDefault(Optional.empty());
        actualEndSource = BehaviorComputationSource.createDefault(Optional.empty());
        selectedMentorSource = BehaviorComputationSource.createDefault(Optional.empty());
        selectedTermSource = BehaviorComputationSource.createDefault(Optional.empty());
        notesSource = BehaviorComputationSource.createDefault("");
        originalValuesSource = BehaviorComputationSource.createDefault(new CourseDetails(null));
        coursesForTermSource = BehaviorComputationSource.createDefault(Collections.emptyList());
        assessmentsSource = BehaviorComputationSource.createDefault(Collections.emptyList());
        courseAlertsSource = BehaviorComputationSource.createDefault(Collections.emptyList());
        initializedSubject = CompletableSubject.create();
        initializedCompletable = initializedSubject.observeOn(AndroidSchedulers.mainThread());
        termOptionsLiveData = dbLoader.getAllTerms();
        mentorOptionsLiveData = dbLoader.getAllMentors();
        courseAlertsObserver = SubscribingLiveDataWrapper.of(courseAlertsSource.getValue(), courseAlertsSource.getObservable());
        assessmentsObserver = SubscribingLiveDataWrapper.of(assessmentsSource.getValue(), assessmentsSource.getObservable());

        Observable<String> numberObservable = numberSource.getObservable().map(Workers.asCached(AbstractAssessmentEntity.SINGLE_LINE_NORMALIZER::apply));
        Observable<String> titleObservable = titleSource.getObservable().map(Workers.asCached(AbstractAssessmentEntity.SINGLE_LINE_NORMALIZER::apply));
        Observable<BinaryAlternate<Integer, ResourceMessageFactory>> competencyUnitsParsedObservable = competencyUnitsTextSource.getObservable()
                .map(Workers.asCached(EditCourseViewModel::parseCompetencyUnits));
        Observable<Optional<Long>> selectedTermIdObservable = selectedTermSource.getObservable().map(t -> t.map(AbstractEntity::getId).filter(i -> ID_NEW != i));
        Observable<Optional<ResourceMessageFactory>> expectedStartMessage = Observable.combineLatest(statusSource.getObservable(), expectedStartSource.getObservable(),
                expectedEndSource.getObservable(), selectedTermSource.getObservable(), EditCourseViewModel::validateExpectedStart);
        Observable<Optional<ResourceMessageFactory>> expectedEndMessage = Observable.combineLatest(statusSource.getObservable(), expectedStartSource.getObservable(),
                expectedEndSource.getObservable(), selectedTermSource.getObservable(), EditCourseViewModel::validateExpectedEnd);
        Observable<Optional<ResourceMessageFactory>> actualStartMessage = Observable.combineLatest(statusSource.getObservable(), actualStartSource.getObservable(),
                actualEndSource.getObservable(), EditCourseViewModel::validateActualStart);
        Observable<Optional<ResourceMessageFactory>> actualEndMessage = Observable.combineLatest(statusSource.getObservable(), actualStartSource.getObservable(),
                actualEndSource.getObservable(), EditCourseViewModel::validateActualEnd);
        Observable<Optional<Integer>> competencyUnitsObservable = competencyUnitsParsedObservable.map(BinaryAlternate::extractPrimary);
        Observable<Long> courseIdObservable = originalValuesSource.getObservable().map(AbstractEntity::getId);
        Observable<DateValues> dateValuesObservable = Observable.combineLatest(expectedStartSource.getObservable(), expectedEndSource.getObservable(),
                actualStartSource.getObservable(), actualEndSource.getObservable(), DateValues::new);
        Observable<ModifiedValues> modifiedValuesObservable = Observable.combineLatest(numberObservable, titleObservable, statusSource.getObservable(),
                competencyUnitsParsedObservable.map(BinaryAlternate::extractPrimary), selectedMentorSource.getObservable().map(o -> o.map(AbstractEntity::getId)),
                selectedTermIdObservable, notesSource.getObservable().map(Workers.asCached(AbstractNotedEntity.MULTI_LINE_NORMALIZER::apply)),
                ModifiedValues::new);
        Observable<Boolean> hasChangesObservable = Observable.combineLatest(
                originalValuesSource.getObservable(),
                dateValuesObservable,
                modifiedValuesObservable,
                (course, dateValues, modifiedValues) -> dateValues.isChanged(course) || modifiedValues.isChanged(course)
        );
        Observable<Boolean> termValidObservable = selectedTermIdObservable.map(Optional::isPresent);
        Observable<Boolean> numberValidObservable = numberObservable.map(s ->
                !s.isEmpty()
        );
        Observable<Boolean> titleValidObservable = titleObservable.map(s ->
                !s.isEmpty()
        );

        Observable<Boolean> validObservable = Observable.combineLatest(termValidObservable, numberValidObservable, titleValidObservable,
                expectedStartMessage.map(o -> o.map(m -> m.getLevel() == MessageLevel.INFO).orElse(true)),
                expectedEndMessage.map(o -> o.map(m -> m.getLevel() == MessageLevel.INFO).orElse(true)),
                actualStartMessage.map(o -> o.map(m -> m.getLevel() == MessageLevel.INFO).orElse(true)),
                actualEndMessage.map(o -> o.map(m -> m.getLevel() == MessageLevel.INFO).orElse(true)),
                (termValid, numberValid, titleValid, expectedStartValid, expectedEndValid, actualStartValid, actualEndValid) -> {
                    Log.d(LOG_TAG, "Calculating valid: termValid = " + termValid + "; numberValid = " + numberValid + "; titleValid = " + titleValid +
                            "; expectedStartValid = " + expectedStartValid + "; expectedEndValid = " + expectedEndValid + "; actualStartValid = " + actualStartValid +
                            "; actualEndValid = " + actualEndValid);
                    return termValid && numberValid && titleValid && expectedStartValid && expectedEndValid && actualStartValid && actualEndValid;
                });

        effectiveStartObserver = SubscribingLiveDataWrapper.ofOptional(Observable.combineLatest(expectedStartSource.getObservable(), actualStartSource.getObservable(),
                (e, a) -> (a.isPresent()) ? a : e).doAfterNext(d -> recalculateAlerts(Objects.requireNonNull(courseAlertsSource.getValue()), d.orElse(null), Objects.requireNonNull(actualStartSource.getValue()).orElseGet(() ->
                Objects.requireNonNull(expectedStartSource.getValue()).orElse(null)))));
        effectiveEndObserver = SubscribingLiveDataWrapper.ofOptional(Observable.combineLatest(expectedEndSource.getObservable(), actualEndSource.getObservable(),
                (e, a) -> (a.isPresent()) ? a : e).doAfterNext(d -> recalculateAlerts(Objects.requireNonNull(courseAlertsSource.getValue()), Objects.requireNonNull(actualEndSource.getValue()).orElseGet(() ->
                Objects.requireNonNull(expectedEndSource.getValue()).orElse(null)), d.orElse(null))));
        termValidObserver = SubscribingLiveDataWrapper.of(false, termValidObservable);
        numberValidObserver = SubscribingLiveDataWrapper.of(false, numberValidObservable);
        titleValidObserver = SubscribingLiveDataWrapper.of(false, titleValidObservable);
        expectedStartValidationMessageObserver = SubscribingLiveDataWrapper.of(Optional.empty(), expectedStartMessage);
        expectedEndValidationMessageObserver = SubscribingLiveDataWrapper.of(Optional.empty(), expectedEndMessage);
        actualStartValidationMessageObserver = SubscribingLiveDataWrapper.of(Optional.empty(), actualStartMessage);
        actualEndValidationMessageObserver = SubscribingLiveDataWrapper.of(Optional.empty(), actualEndMessage);
        originalValuesLiveData = SubscribingLiveDataWrapper.of(originalValuesSource.getObservable());
        competencyUnitsValidationMessageObserver = SubscribingLiveDataWrapper.of(Optional.empty(), competencyUnitsParsedObservable.map(BinaryAlternate::extractSecondary));
        competencyUnitsValueObserver = SubscribingLiveDataWrapper.of(Optional.empty(), competencyUnitsObservable);
        titleFactoryObserver = SubscribingLiveDataWrapper.of(c -> c.getString(R.string.title_activity_view_course), numberObservable.map(EditCourseViewModel::calculateViewTitleFactory));
        subTitleObserver = SubscribingLiveDataWrapper.of("", titleObservable);
        overviewFactoryObserver = SubscribingLiveDataWrapper.of(r -> "", Observable.combineLatest(statusSource.getObservable(),
                competencyUnitsParsedObservable.map(BinaryAlternate::extractPrimary), expectedStartSource.getObservable(), expectedEndSource.getObservable(),
                actualStartSource.getObservable(), actualEndSource.getObservable(), selectedTermSource.getObservable(), selectedMentorSource.getObservable(),
                EditCourseViewModel::calculateOverviewFactory));
        canSaveObserver = SubscribingLiveDataWrapper.of(false, Observable.combineLatest(validObservable, hasChangesObservable, (v, c) -> {
            Log.d(LOG_TAG, "Calculating canSave: valid = " + v + "; changed = " + c);
            return v && c;
        }));
        canShareObserver = SubscribingLiveDataWrapper.of(false, Observable.combineLatest(validObservable, hasChangesObservable, (v, c) -> {
            Log.d(LOG_TAG, "Calculating canShare: valid = " + v + "; changed = " + c);
            return v && !c;
        }));
        hasChangesObserver = SubscribingLiveDataWrapper.of(false, hasChangesObservable);
        compositeDisposable = new CompositeDisposable(effectiveStartObserver, effectiveEndObserver, termValidObserver, numberValidObserver, titleValidObserver,
                expectedStartValidationMessageObserver, expectedEndValidationMessageObserver, actualStartValidationMessageObserver, actualEndValidationMessageObserver,
                competencyUnitsValidationMessageObserver, titleFactoryObserver, subTitleObserver, overviewFactoryObserver, hasChangesObserver,
                canSaveObserver, canShareObserver, courseIdObservable.subscribe(this::observeCourseAlertsByCourseId),
                courseIdObservable.subscribe(this::observeAssessmentsByCourseId), selectedTermIdObservable.subscribe(this::observeCoursesByTermId));
    }

    private static void recalculateAlerts(@NonNull List<CourseAlert> alerts, @Nullable LocalDate startDate, @Nullable LocalDate endDate) {
        for (CourseAlert a : alerts) {
            a.reCalculate(startDate, endDate);
        }
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private void observeCoursesByTermId(Optional<Long> selectedTermId) {
        try {
            if (null != coursesForTermObserving) {
                if (selectedTermId.map(i -> Objects.equals(coursesForTermObserving.first, i)).orElse(false)) {
                    return;
                }
                compositeDisposable.remove(coursesForTermObserving.second);
                coursesForTermObserving = null;
            }
        } finally {
            if (selectedTermId.isPresent()) {
                long id = selectedTermId.get();
                coursesForTermObserving = new Pair<>(id, dbLoader.getCoursesObservableByTermId(id)
                        .subscribe(list -> coursesForTermSource.onNext((null == list) ? Collections.emptyList() : list)));
                compositeDisposable.add(coursesForTermObserving.second);
            } else {
                coursesForTermSource.onNext(Collections.emptyList());
                coursesForTermObserving = null;
            }
        }
    }

    private void observeCourseAlertsByCourseId(long courseId) {
        try {
            if (null != courseAlertsObserving) {
                if (courseAlertsObserving.first == courseId) {
                    return;
                }
                compositeDisposable.remove(courseAlertsObserving.second);
                courseAlertsObserving = null;
            }
        } finally {
            if (ID_NEW != courseId) {
                courseAlertsObserving = new Pair<>(courseId, dbLoader.getAlertsObservableByCourseId(courseId).subscribe(this::onAlertsLoaded));
                compositeDisposable.add(courseAlertsObserving.second);
            } else {
                courseAlertsSource.onNext(Collections.emptyList());
                courseAlertsObserving = null;
            }
        }
    }

    private void onAlertsLoaded(List<CourseAlert> courseAlerts) {
        for (CourseAlert a : courseAlerts) {
            a.calculate(this);
        }
        this.courseAlertsSource.onNext(courseAlerts);
    }

    private void observeAssessmentsByCourseId(long courseId) {
        try {
            if (null != assessmentsObserving) {
                if (assessmentsObserving.first == courseId) {
                    return;
                }
                compositeDisposable.remove(courseAlertsObserving.second);
                assessmentsObserving = null;
            }
        } finally {
            if (ID_NEW != courseId) {
                assessmentsObserving = new Pair<>(courseId, dbLoader.getAssessmentsObservableByCourseId(courseId).subscribe(assessmentsSource::onNext));
                compositeDisposable.add(assessmentsObserving.second);
            } else {
                assessmentsSource.onNext(Collections.emptyList());
                assessmentsObserving = null;
            }
        }
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    @NonNull
    private static Function<Resources, CharSequence> calculateOverviewFactory(@NonNull CourseStatus status, Optional<Integer> competencyUnits,
                                                                              Optional<LocalDate> expectedStart, Optional<LocalDate> expectedEnd,
                                                                              Optional<LocalDate> actualStart, Optional<LocalDate> actualEnd,
                                                                              Optional<AbstractTermEntity<?>> selectedTerm, Optional<AbstractMentorEntity<?>> selectedMentor) {
        return resources -> {
            SpannableStringBuilder result = new SpannableStringBuilder("Status: ");
            result.setSpan(new StyleSpan(Typeface.BOLD), 0, result.length(), SPAN_EXCLUSIVE_EXCLUSIVE);
            result.append(resources.getString(status.displayResourceId())).append("; ")
                    .append("Competency Units: ", new StyleSpan(Typeface.BOLD), SPAN_EXCLUSIVE_EXCLUSIVE);
            if (competencyUnits.isPresent()) {
                int v = competencyUnits.get();
                if (v < 0) {
                    result.append(resources.getString(R.string.message_invalid_number),
                            new ForegroundColorSpan(resources.getColor(R.color.color_error, null)), SPAN_EXCLUSIVE_EXCLUSIVE);
                } else {
                    result.append(NUMBER_FORMATTER.format(v));
                }
            } else {
                result.append(resources.getString(R.string.message_invalid_number),
                        new ForegroundColorSpan(resources.getColor(R.color.color_error, null)), SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            int position = result.append("\n").length();
            if (actualStart.isPresent()) {
                result.append("Started on: ", new StyleSpan(Typeface.BOLD), SPAN_EXCLUSIVE_EXCLUSIVE).append(LONG_FORMATTER.format(actualStart.get()));
            } else if (expectedStart.isPresent()) {
                result.append("Expected Start: ", new StyleSpan(Typeface.BOLD), SPAN_EXCLUSIVE_EXCLUSIVE)
                        .append(LONG_FORMATTER.format(expectedStart.get()));
                switch (status) {
                    case IN_PROGRESS:
                    case NOT_PASSED:
                    case PASSED:
                        result.append(" (actual start date missing)",
                                new ForegroundColorSpan(resources.getColor(R.color.color_error, null)), SPAN_EXCLUSIVE_EXCLUSIVE);
                        break;
                    default:
                        break;
                }
            } else {
                switch (status) {
                    case IN_PROGRESS:
                    case NOT_PASSED:
                    case PASSED:
                        result.append("Started on: ", new StyleSpan(Typeface.BOLD), SPAN_EXCLUSIVE_EXCLUSIVE)
                                .append(resources.getString(R.string.message_required),
                                        new ForegroundColorSpan(resources.getColor(R.color.color_error, null)), SPAN_EXCLUSIVE_EXCLUSIVE);
                        break;
                    default:
                        break;
                }
            }
            if (actualEnd.isPresent()) {
                if (position < result.length()) {
                    result.append("; ");
                }
                result.append("Ended On: ", new StyleSpan(Typeface.BOLD), SPAN_EXCLUSIVE_EXCLUSIVE).append(LONG_FORMATTER.format(actualEnd.get()));
            } else if (expectedEnd.isPresent()) {
                if (position < result.length()) {
                    result.append("; ");
                }
                result.append("Expected End: ", new StyleSpan(Typeface.BOLD), SPAN_EXCLUSIVE_EXCLUSIVE)
                        .append(LONG_FORMATTER.format(expectedEnd.get()));
                switch (status) {
                    case NOT_PASSED:
                    case PASSED:
                        result.append(" (actual end date missing)",
                                new ForegroundColorSpan(resources.getColor(R.color.color_error, null)), SPAN_EXCLUSIVE_EXCLUSIVE);
                        break;
                    default:
                        break;
                }
            } else {
                switch (status) {
                    case NOT_PASSED:
                    case PASSED:
                        if (position < result.length()) {
                            result.append("; ");
                        }
                        result.append("Ended on: ", new StyleSpan(Typeface.BOLD), SPAN_EXCLUSIVE_EXCLUSIVE)
                                .append(resources.getString(R.string.message_required),
                                        new ForegroundColorSpan(resources.getColor(R.color.color_error, null)), SPAN_EXCLUSIVE_EXCLUSIVE);
                        break;
                    default:
                        break;
                }
            }
            if (position < result.length()) {
                result.append("\n");
            }
            selectedMentor.ifPresent(m -> result.append("Mentor: ", new StyleSpan(Typeface.BOLD), SPAN_EXCLUSIVE_EXCLUSIVE).append(m.getName()).append("; "));
            result.append("Term: ", new StyleSpan(Typeface.BOLD), SPAN_EXCLUSIVE_EXCLUSIVE);
            if (selectedTerm.isPresent()) {
                String n = selectedTerm.get().getName();
                String t = resources.getString(R.string.format_term, n);
                int i = t.indexOf(':');
                result.append((i > 0 && n.startsWith(t.substring(0, i))) ? n.substring(i).trim() : n);
            } else {
                result.append(resources.getString(R.string.message_required),
                        new ForegroundColorSpan(resources.getColor(R.color.color_error, null)), SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            return result;
        };
    }

    @NonNull
    private static Function<Resources, CharSequence> calculateViewTitleFactory(@NonNull String number) {
        if (number.isEmpty()) {
            return r -> r.getString(R.string.label_course);
        }
        return r -> r.getString(R.string.format_course, number);
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    @NonNull
    private static Optional<ResourceMessageFactory> validateExpectedStart(@NonNull CourseStatus status, Optional<LocalDate> expectedStart, Optional<LocalDate> expectedEnd,
                                                                          Optional<AbstractTermEntity<?>> selectedTerm) {
        Log.d(LOG_TAG, "Enter validateExpectedStart(status = " + status.name() + ", expectedStart = " +
                ToStringBuilder.toEscapedString(expectedStart.orElse(null), false) + ", expectedEnd = " +
                ToStringBuilder.toEscapedString(expectedEnd.orElse(null), false) + ", selectedTerm = " +
                ToStringBuilder.toEscapedString(selectedTerm.orElse(null)) + ")");
        return expectedStart.map(s ->
                expectedEnd.flatMap(e ->
                        (s.compareTo(e) > 0) ?
                                Optional.of(ResourceMessageFactory.ofError(R.string.message_start_after_end)) :
                                selectedTerm.flatMap(t -> {
                                    LocalDate d = t.getStart();
                                    return (null != d && s.compareTo(d) < 0) ?
                                            Optional.of(ResourceMessageFactory.ofWarning(R.string.message_before_term_start)) :
                                            Optional.empty();
                                })
                )
        ).orElseGet(() ->
                (status == CourseStatus.PLANNED) ? Optional.of(ResourceMessageFactory.ofError(R.string.message_required)) : Optional.empty()
        );
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    @NonNull
    private static Optional<ResourceMessageFactory> validateExpectedEnd(@NonNull CourseStatus status, Optional<LocalDate> expectedStart, Optional<LocalDate> expectedEnd,
                                                                        Optional<AbstractTermEntity<?>> selectedTerm) {
        Log.d(LOG_TAG, "Enter validateExpectedEnd(status = " + status.name() + ", expectedStart = " +
                ToStringBuilder.toEscapedString(expectedStart.orElse(null), false) + ", expectedEnd = " +
                ToStringBuilder.toEscapedString(expectedEnd.orElse(null), false) + ", selectedTerm = " +
                ToStringBuilder.toEscapedString(selectedTerm.orElse(null)) + ")");
        return expectedEnd.map(e ->
                expectedStart.flatMap(s ->
                        (s.compareTo(e) > 0) ?
                                Optional.of(ResourceMessageFactory.ofError(R.string.message_start_after_end)) :
                                selectedTerm.flatMap(t -> {
                                    LocalDate d = t.getEnd();
                                    return (null != d && e.compareTo(d) > 0) ?
                                            Optional.of(ResourceMessageFactory.ofWarning(R.string.message_after_term_end)) :
                                            Optional.empty();
                                })
                )
        ).orElseGet(() ->
                (status == CourseStatus.IN_PROGRESS) ? Optional.of(ResourceMessageFactory.ofError(R.string.message_required)) : Optional.empty()
        );
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    @NonNull
    private static Optional<ResourceMessageFactory> validateActualStart(@NonNull CourseStatus status, Optional<LocalDate> actualStart, Optional<LocalDate> actualEnd) {
        Log.d(LOG_TAG, "Enter validateActualStart(status = " + status.name() + ", actualStart = " +
                ToStringBuilder.toEscapedString(actualStart.orElse(null), false) + ", actualEnd = " +
                ToStringBuilder.toEscapedString(actualEnd.orElse(null), false) + ")");
        return actualStart.map(s ->
                actualEnd.flatMap(e ->
                        (s.compareTo(e) > 0) ?
                                Optional.of(ResourceMessageFactory.ofError(R.string.message_start_after_end)) : Optional.empty()
                )
        ).orElseGet(() -> {
            switch (status) {
                case IN_PROGRESS:
                case PASSED:
                case NOT_PASSED:
                    return Optional.of(ResourceMessageFactory.ofError(R.string.message_required));
                default:
                    return Optional.empty();
            }
        });
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    @NonNull
    private static Optional<ResourceMessageFactory> validateActualEnd(@NonNull CourseStatus status, Optional<LocalDate> actualStart, Optional<LocalDate> actualEnd) {
        Log.d(LOG_TAG, "Enter validateActualEnd(status = " + status.name() + ", actualStart = " +
                ToStringBuilder.toEscapedString(actualStart.orElse(null), false) + ", actualEnd = " +
                ToStringBuilder.toEscapedString(actualEnd.orElse(null), false) + ")");
        return actualEnd.map(e ->
                actualStart.flatMap(s ->
                        (s.compareTo(e) > 0) ?
                                Optional.of(ResourceMessageFactory.ofError(R.string.message_start_after_end)) : Optional.empty()
                )
        ).orElseGet(() -> {
            switch (status) {
                case PASSED:
                case NOT_PASSED:
                    return Optional.of(ResourceMessageFactory.ofError(R.string.message_required));
                default:
                    return Optional.empty();
            }
        });
    }

    @NonNull
    private static BinaryAlternate<Integer, ResourceMessageFactory> parseCompetencyUnits(String text) {
        if (null == text || (text = text.trim()).isEmpty()) {
            return BinaryAlternate.ofSecondary(ResourceMessageFactory.ofError(R.string.message_required));
        }
        double value;
        try {
            value = Double.parseDouble(text);
        } catch (NumberFormatException ex) {
            String m = ex.getMessage();
            return BinaryAlternate.ofSecondary((null == m || (m = m.trim()).isEmpty()) ? ResourceMessageFactory.ofError(R.string.message_number_parse_error) :
                    ResourceMessageFactory.ofError(R.string.format_days_parse_error, m));
        }
        if (value < 0.0 || value > (double) Integer.MAX_VALUE || Math.floor(value) != value) {
            return BinaryAlternate.ofSecondary(ResourceMessageFactory.ofError(R.string.message_invalid_number));
        }
        return BinaryAlternate.ofPrimary((int) value);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.dispose();
    }

    public long getId() {
        return Objects.requireNonNull(originalValuesSource.getValue()).getId();
    }

    @Nullable
    public AbstractTermEntity<?> getSelectedTerm() {
        return Objects.requireNonNull(selectedTermSource.getValue()).orElse(null);
    }

    public void setSelectedTerm(AbstractTermEntity<?> term) {
        selectedTermSource.onNext(Optional.ofNullable(term));
    }

    @Nullable
    public AbstractMentorEntity<?> getSelectedMentor() {
        return Objects.requireNonNull(selectedMentorSource.getValue()).orElse(null);
    }

    public void setSelectedMentor(@Nullable AbstractMentorEntity<?> mentor) {
        selectedMentorSource.onNext(Optional.ofNullable(mentor));
    }

    @NonNull
    public String getNumber() {
        return Objects.requireNonNull(numberSource.getValue());
    }

    public void setNumber(String value) {
        numberSource.onNext((null == value) ? "" : value);
    }

    @NonNull
    public String getTitle() {
        return Objects.requireNonNull(titleSource.getValue());
    }

    public void setTitle(String value) {
        titleSource.onNext((null == value) ? "" : value);
    }

    @Nullable
    public LocalDate getExpectedStart() {
        return Objects.requireNonNull(expectedStartSource.getValue()).orElse(null);
    }

    public void setExpectedStart(LocalDate value) {
        expectedStartSource.onNext(Optional.ofNullable(value));
    }

    @Nullable
    public LocalDate getActualStart() {
        return Objects.requireNonNull(actualStartSource.getValue()).orElse(null);
    }

    public void setActualStart(LocalDate value) {
        actualStartSource.onNext(Optional.ofNullable(value));
    }

    @Nullable
    public LocalDate getExpectedEnd() {
        return Objects.requireNonNull(expectedEndSource.getValue()).orElse(null);
    }

    public void setExpectedEnd(LocalDate value) {
        expectedEndSource.onNext(Optional.ofNullable(value));
    }

    @Nullable
    public LocalDate getActualEnd() {
        return Objects.requireNonNull(actualEndSource.getValue()).orElse(null);
    }

    public void setActualEnd(LocalDate value) {
        actualEndSource.onNext(Optional.ofNullable(value));
    }

    @NonNull
    public String getCompetencyUnitsText() {
        return Objects.requireNonNull(competencyUnitsTextSource.getValue());
    }

    public void setCompetencyUnitsText(String value) {
        competencyUnitsTextSource.onNext((null == value) ? "" : value);
    }

    @NonNull
    public CourseStatus getStatus() {
        return Objects.requireNonNull(statusSource.getValue());
    }

    public synchronized void setStatus(CourseStatus status) {
        this.statusSource.onNext((null == status) ? CourseStatus.UNPLANNED : status);
    }

    @NonNull
    public String getNotes() {
        return Objects.requireNonNull(notesSource.getValue());
    }

    public synchronized void setNotes(String value) {
        notesSource.onNext((null == value) ? "" : value);
    }

    public List<TermCourseListItem> getCoursesForTerm() {
        return coursesForTermSource.getValue();
    }

    public Completable getInitializedCompletable() {
        return initializedCompletable;
    }

    public LiveData<LocalDate> getEffectiveStartLiveData() {
        return effectiveStartObserver.getLiveData();
    }

    public LiveData<LocalDate> getEffectiveEndLiveData() {
        return effectiveEndObserver.getLiveData();
    }

    public LiveData<Boolean> getTermValidLiveData() {
        return termValidObserver.getLiveData();
    }

    public LiveData<Boolean> getNumberValidLiveData() {
        return numberValidObserver.getLiveData();
    }

    public LiveData<Boolean> getTitleValidLiveData() {
        return titleValidObserver.getLiveData();
    }

    public LiveData<List<TermListItem>> getTermOptionsLiveData() {
        return termOptionsLiveData;
    }

    public LiveData<List<MentorListItem>> getMentorOptionsLiveData() {
        return mentorOptionsLiveData;
    }

    public LiveData<List<AssessmentEntity>> getAssessmentsLiveData() {
        return assessmentsObserver.getLiveData();
    }

    public LiveData<Optional<ResourceMessageFactory>> getExpectedStartValidationMessageLiveData() {
        return expectedStartValidationMessageObserver.getLiveData();
    }

    public LiveData<Optional<ResourceMessageFactory>> getExpectedEndValidationMessageLiveData() {
        return expectedEndValidationMessageObserver.getLiveData();
    }

    public LiveData<Optional<ResourceMessageFactory>> getActualStartValidationMessageLiveData() {
        return actualStartValidationMessageObserver.getLiveData();
    }

    public LiveData<Optional<ResourceMessageFactory>> getActualEndValidationMessageLiveData() {
        return actualEndValidationMessageObserver.getLiveData();
    }

    public LiveData<Optional<ResourceMessageFactory>> getCompetencyUnitsValidationMessageLiveData() {
        return competencyUnitsValidationMessageObserver.getLiveData();
    }

    public LiveData<Boolean> getCanSaveLiveData() {
        return canSaveObserver.getLiveData();
    }

    public LiveData<Boolean> getCanShareLiveData() {
        return canShareObserver.getLiveData();
    }

    public LiveData<Boolean> getHasChangesLiveData() {
        return hasChangesObserver.getLiveData();
    }

    public LiveData<Function<Resources, CharSequence>> getTitleFactoryLiveData() {
        return titleFactoryObserver.getLiveData();
    }

    public LiveData<String> getSubTitleLiveData() {
        return subTitleObserver.getLiveData();
    }

    public LiveData<Function<Resources, CharSequence>> getOverviewFactoryLiveData() {
        return overviewFactoryObserver.getLiveData();
    }

    public LiveData<CourseDetails> getOriginalValuesLiveData() {
        return originalValuesLiveData.getLiveData();
    }

    public boolean isFromInitializedState() {
        return fromInitializedState;
    }

    public synchronized Single<CourseDetails> initializeViewModelState(@Nullable Bundle savedInstanceState, Supplier<Bundle> getArguments) {
        fromInitializedState = null != savedInstanceState && savedInstanceState.getBoolean(STATE_KEY_STATE_INITIALIZED, false);
        Bundle state = (fromInitializedState) ? savedInstanceState : getArguments.get();
        CourseDetails entity = new CourseDetails(null);
        if (null != state) {
            Log.d(LOG_TAG, (fromInitializedState) ? "Restoring currentValues from saved state" : "Initializing currentValues from arguments");
            if (!fromInitializedState) {
                if (state.containsKey(EXTRA_KEY_COURSE_ID)) {
                    return dbLoader.getCourseById(state.getLong(EXTRA_KEY_COURSE_ID)).doOnSuccess(this::onEntityLoadedFromDb);
                }
                if (state.containsKey(EXTRA_KEY_EXPECTED_START)) {
                    entity.setExpectedStart(LocalDateConverter.toLocalDate(state.getLong(EXTRA_KEY_EXPECTED_START)));
                }
                return dbLoader.getTermById(state.getLong(EXTRA_KEY_TERM_ID)).map(term -> {
                    entity.setTerm(term);
                    selectedTermSource.onNext(Optional.of(term));
                    originalValuesSource.onNext(entity);
                    initializedSubject.onComplete();
                    return entity;
                });
            }
            entity.restoreState(state, true);
            Log.d(LOG_TAG, "Restoring courseEntity from saved state");
            String key = IdIndexedEntity.stateKey(AppDb.TABLE_NAME_TERMS, Term.COLNAME_ID, false);
            if (state.containsKey(key)) {
                TermEntity term = new TermEntity();
                term.restoreState(state, false);
                selectedTermSource.onNext(Optional.of(term));
            } else {
                selectedTermSource.onNext(Optional.empty());
            }
            key = IdIndexedEntity.stateKey(AppDb.TABLE_NAME_MENTORS, Mentor.COLNAME_ID, false);
            if (state.containsKey(key)) {
                MentorEntity mentor = new MentorEntity();
                mentor.restoreState(state, false);
                selectedMentorSource.onNext(Optional.of(mentor));
            } else {
                selectedMentorSource.onNext(Optional.empty());
            }
            numberSource.onNext(state.getString(IdIndexedEntity.stateKey(AppDb.TABLE_NAME_COURSES, Course.COLNAME_NUMBER, false), ""));
            titleSource.onNext(state.getString(IdIndexedEntity.stateKey(AppDb.TABLE_NAME_COURSES, Course.COLNAME_TITLE, false), ""));
            notesSource.onNext(state.getString(IdIndexedEntity.stateKey(AppDb.TABLE_NAME_COURSES, Course.COLNAME_NOTES, false), ""));
            key = IdIndexedEntity.stateKey(AppDb.TABLE_NAME_COURSES, Course.COLNAME_STATUS, false);
            statusSource.onNext((state.containsKey(key)) ? CourseStatus.valueOf(state.getString(key)) : CourseStatus.UNPLANNED);
            key = IdIndexedEntity.stateKey(AppDb.TABLE_NAME_COURSES, Course.COLNAME_EXPECTED_START, false);
            if (state.containsKey(key)) {
                expectedStartSource.onNext(Optional.of(LocalDateConverter.toLocalDate(state.getLong(key))));
            } else {
                expectedStartSource.onNext(Optional.empty());
            }
            key = IdIndexedEntity.stateKey(AppDb.TABLE_NAME_COURSES, Course.COLNAME_ACTUAL_START, false);
            if (state.containsKey(key)) {
                actualStartSource.onNext(Optional.of(LocalDateConverter.toLocalDate(state.getLong(key))));
            } else {
                actualStartSource.onNext(Optional.empty());
            }
            key = IdIndexedEntity.stateKey(AppDb.TABLE_NAME_COURSES, Course.COLNAME_EXPECTED_END, false);
            if (state.containsKey(key)) {
                expectedEndSource.onNext(Optional.of(LocalDateConverter.toLocalDate(state.getLong(key))));
            } else {
                expectedEndSource.onNext(Optional.empty());
            }
            key = IdIndexedEntity.stateKey(AppDb.TABLE_NAME_COURSES, Course.COLNAME_ACTUAL_END, false);
            if (state.containsKey(key)) {
                actualEndSource.onNext(Optional.of(LocalDateConverter.toLocalDate(state.getLong(key))));
            } else {
                actualEndSource.onNext(Optional.empty());
            }
            competencyUnitsTextSource.onNext(state.getString(STATE_KEY_COMPETENCY_UNITS_TEXT, ""));
        } else {
            Log.d(LOG_TAG, "No saved state or arguments");
            competencyUnitsTextSource.onNext(NumberFormat.getIntegerInstance().format(entity.getCompetencyUnits()));
        }
        originalValuesSource.onNext(entity);
        initializedSubject.onComplete();
        return Single.just(entity).observeOn(AndroidSchedulers.mainThread());
    }

    private void onEntityLoadedFromDb(CourseDetails entity) {
        Log.d(LOG_TAG, String.format("Loaded %s from database", entity));
        selectedTermSource.onNext(Optional.ofNullable(entity.getTerm()));
        selectedMentorSource.onNext(Optional.ofNullable(entity.getMentor()));
        numberSource.onNext(entity.getNumber());
        titleSource.onNext(entity.getTitle());
        statusSource.onNext(entity.getStatus());
        expectedStartSource.onNext(Optional.ofNullable(entity.getExpectedStart()));
        expectedEndSource.onNext(Optional.ofNullable(entity.getExpectedEnd()));
        actualStartSource.onNext(Optional.ofNullable(entity.getActualStart()));
        actualEndSource.onNext(Optional.ofNullable(entity.getActualEnd()));
        competencyUnitsTextSource.onNext(NumberFormat.getIntegerInstance().format(entity.getCompetencyUnits()));
        notesSource.onNext(entity.getNotes());
        originalValuesSource.onNext(entity);
        initializedSubject.onComplete();
    }

    public void saveViewModelState(@NonNull Bundle outState) {
        outState.putBoolean(STATE_KEY_STATE_INITIALIZED, true);
        Objects.requireNonNull(originalValuesSource.getValue()).saveState(outState, true);
        Objects.requireNonNull(selectedTermSource.getValue()).ifPresent(t -> t.saveState(outState, false));
        Objects.requireNonNull(selectedMentorSource.getValue()).ifPresent(m -> m.saveState(outState, false));
        outState.putString(IdIndexedEntity.stateKey(AppDb.TABLE_NAME_COURSES, Course.COLNAME_NUMBER, false), numberSource.getValue());
        outState.putString(IdIndexedEntity.stateKey(AppDb.TABLE_NAME_COURSES, Course.COLNAME_STATUS, false), Objects.requireNonNull(statusSource.getValue()).name());
        outState.putString(IdIndexedEntity.stateKey(AppDb.TABLE_NAME_COURSES, Course.COLNAME_TITLE, false), titleSource.getValue());
        outState.putString(IdIndexedEntity.stateKey(AppDb.TABLE_NAME_COURSES, Course.COLNAME_NOTES, false), notesSource.getValue());
        Long d = LocalDateConverter.fromLocalDate(getExpectedStart());
        if (null != d) {
            outState.putLong(IdIndexedEntity.stateKey(AppDb.TABLE_NAME_COURSES, Course.COLNAME_EXPECTED_START, false), d);
        }
        d = LocalDateConverter.fromLocalDate(getActualStart());
        if (null != d) {
            outState.putLong(IdIndexedEntity.stateKey(AppDb.TABLE_NAME_COURSES, Course.COLNAME_ACTUAL_START, false), d);
        }
        d = LocalDateConverter.fromLocalDate(getExpectedEnd());
        if (null != d) {
            outState.putLong(IdIndexedEntity.stateKey(AppDb.TABLE_NAME_COURSES, Course.COLNAME_EXPECTED_END, false), d);
        }
        d = LocalDateConverter.fromLocalDate(getActualEnd());
        if (null != d) {
            outState.putLong(IdIndexedEntity.stateKey(AppDb.TABLE_NAME_COURSES, Course.COLNAME_ACTUAL_END, false), d);
        }
        outState.putString(STATE_KEY_COMPETENCY_UNITS_TEXT, competencyUnitsTextSource.getValue());
    }

    public synchronized Single<ResourceMessageResult> save(boolean ignoreWarnings) {
        AbstractTermEntity<?> term = Objects.requireNonNull(selectedTermSource.getValue()).orElse(null);
        if (null == term) {
            return Single.just(ValidationMessage.ofSingleError(R.string.message_term_not_selected)).observeOn(AndroidSchedulers.mainThread());
        }
        Integer cu = Objects.requireNonNull(competencyUnitsValueObserver.getLiveData().getValue()).orElse(null);
        if (null == cu) {
            return Single.just(ValidationMessage.ofSingleError(R.string.message_required)).observeOn(AndroidSchedulers.mainThread());
        }
        CourseDetails originalValues = this.originalValuesSource.getValue();
        CourseEntity entity = Objects.requireNonNull(originalValues).toEntity();
        entity.setTermId(Objects.requireNonNull(term).getId());
        entity.setMentorId(Objects.requireNonNull(selectedMentorSource.getValue()).map(AbstractEntity::getId).orElse(null));
        entity.setNumber(numberSource.getValue());
        entity.setTitle(titleSource.getValue());
        entity.setStatus(statusSource.getValue());
        entity.setExpectedStart(Objects.requireNonNull(expectedStartSource.getValue()).orElse(null));
        entity.setExpectedEnd(Objects.requireNonNull(expectedEndSource.getValue()).orElse(null));
        entity.setActualStart(Objects.requireNonNull(actualStartSource.getValue()).orElse(null));
        entity.setActualEnd(Objects.requireNonNull(actualEndSource.getValue()).orElse(null));
        entity.setCompetencyUnits(cu);
        entity.setNotes(notesSource.getValue());
        Log.d(LOG_TAG, String.format("Saving %s to database", entity));
        return dbLoader.saveCourse(entity, ignoreWarnings).doOnSuccess(m -> {
            if (m.isSucceeded()) {
                this.originalValuesSource.onNext(new CourseDetails(entity, term, selectedMentorSource.getValue().orElse(null)));
            }
        });
    }

    public Single<ResourceMessageResult> delete(boolean ignoreWarnings) {
        Log.d(LOG_TAG, "Enter delete(" + ignoreWarnings + ")");
        return dbLoader.deleteCourse(Objects.requireNonNull(originalValuesSource.getValue()).toEntity(), ignoreWarnings);
    }

    public LiveData<List<CourseAlert>> getCourseAlertsLiveData() {
        return courseAlertsObserver.getLiveData();
    }

    public synchronized Single<List<AlertListItem>> getCourseAndAssessmentAlerts() {
        return dbLoader.getAllAlertsByCourseId(getId());
    }

    public LocalDate getEffectiveStartDate() {
        return effectiveStartObserver.getLiveData().getValue();
    }

    public LocalDate getEffectiveEndDate() {
        return effectiveEndObserver.getLiveData().getValue();
    }

    private static class DateValues {
        private final LocalDate expectedStart;
        private final LocalDate expectedEnd;
        private final LocalDate actualStart;
        private final LocalDate actualEnd;

        @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
        DateValues(Optional<LocalDate> expectedStart, Optional<LocalDate> expectedEnd, Optional<LocalDate> actualStart, Optional<LocalDate> actualEnd) {
            this.expectedStart = expectedStart.orElse(null);
            this.expectedEnd = expectedEnd.orElse(null);
            this.actualStart = actualStart.orElse(null);
            this.actualEnd = actualEnd.orElse(null);
        }

        public Boolean isChanged(CourseDetails course) {
            return !(Objects.equals(course.getExpectedStart(), expectedStart) && Objects.equals(course.getExpectedEnd(), expectedEnd) &&
                    Objects.equals(course.getActualStart(), actualStart) && Objects.equals(course.getActualEnd(), actualEnd));
        }
    }

    private static class ModifiedValues {
        private final String number;
        private final String title;
        private final CourseStatus status;
        private final Integer competencyUnits;
        private final Long selectedMentorId;
        private final Long selectedTermId;
        private final String notes;

        @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
        public ModifiedValues(String number, String title, CourseStatus status, Optional<Integer> competencyUnits, Optional<Long> selectedMentorId,
                              Optional<Long> selectedTermId, String notes) {
            this.number = number;
            this.title = title;
            this.status = status;
            this.competencyUnits = competencyUnits.orElse(null);
            this.selectedMentorId = selectedMentorId.orElse(null);
            this.selectedTermId = selectedTermId.orElse(null);
            this.notes = notes;
        }

        public boolean isChanged(CourseDetails course) {
            return !(Objects.equals(course.getNumber(), number) && Objects.equals(course.getTitle(), title) &&
                    Objects.equals(course.getStatus(), status) && Objects.equals(course.getCompetencyUnits(), competencyUnits) &&
                    Objects.equals(course.getMentorId(), selectedMentorId) && Objects.equals(course.getTermId(), selectedTermId) &&
                    Objects.equals(course.getNotes(), notes));
        }
    }

}