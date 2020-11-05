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
import Erwine.Leonard.T.wguscheduler356334.ui.term.EditTermViewModel;
import Erwine.Leonard.T.wguscheduler356334.util.BehaviorComputationSource;
import Erwine.Leonard.T.wguscheduler356334.util.BinaryAlternate;
import Erwine.Leonard.T.wguscheduler356334.util.LiveDataWrapper;
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
    public static final String STATE_KEY_COMPETENCY_UNITS_TEXT = "t:" + IdIndexedEntity.stateKey(AppDb.TABLE_NAME_COURSES, Course.COLNAME_COMPETENCY_UNITS, false);
    private final SubscribingLiveDataWrapper<CourseDetails> originalValuesLiveData;

    public static void startAddCourseActivity(@NonNull Activity activity, int requestCode, long termId, @NonNull LocalDate expectedStart) {
        Log.d(LOG_TAG, String.format("Enter startAddCourseActivity(context, %d, %s)", termId, expectedStart));
        Intent intent = new Intent(activity, AddCourseActivity.class);
        intent.putExtra(EditTermViewModel.EXTRA_KEY_TERM_ID, termId);
        intent.putExtra(CourseDetails.COLNAME_EXPECTED_START, LocalDateConverter.fromLocalDate(expectedStart));
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
    private final BehaviorComputationSource<CourseDetails> originalValues;
    private final BehaviorComputationSource<String> number;
    private final BehaviorComputationSource<String> title;
    private final BehaviorComputationSource<CourseStatus> status;
    private final BehaviorComputationSource<String> competencyUnitsText;
    private final BehaviorComputationSource<Optional<LocalDate>> expectedStart;
    private final BehaviorComputationSource<Optional<LocalDate>> actualStart;
    private final BehaviorComputationSource<Optional<LocalDate>> expectedEnd;
    private final BehaviorComputationSource<Optional<LocalDate>> actualEnd;
    private final BehaviorComputationSource<Optional<AbstractMentorEntity<?>>> selectedMentor;
    private final BehaviorComputationSource<Optional<AbstractTermEntity<?>>> selectedTerm;
    private final BehaviorComputationSource<String> notes;
    private final BehaviorComputationSource<List<TermCourseListItem>> coursesForTerm;
    private final CompletableSubject initializedSubject;
    private final Completable initializedCompletable;

    private final LiveData<List<TermListItem>> termOptionsLiveData;
    private final LiveData<List<MentorListItem>> mentorOptionsLiveData;
    private final LiveDataWrapper<List<AssessmentEntity>> assessmentsLiveData;
    private final LiveDataWrapper<List<CourseAlert>> courseAlertsLiveData;
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
    //    private final SubscribingLiveDataWrapper<Boolean> isValidObserver;
    private Pair<Long, Disposable> coursesForTermObserving;
    private Pair<Long, Disposable> courseAlertsObserving;
    private Pair<Long, Disposable> assessmentsObserving;
    private boolean fromInitializedState;

    public EditCourseViewModel(@NonNull Application application) {
        super(application);
        dbLoader = DbLoader.getInstance(getApplication());
        number = BehaviorComputationSource.createDefault("");
        title = BehaviorComputationSource.createDefault("");
        status = BehaviorComputationSource.createDefault(CourseStatus.UNPLANNED);
        competencyUnitsText = BehaviorComputationSource.createDefault("");
        expectedStart = BehaviorComputationSource.createDefault(Optional.empty());
        actualStart = BehaviorComputationSource.createDefault(Optional.empty());
        expectedEnd = BehaviorComputationSource.createDefault(Optional.empty());
        actualEnd = BehaviorComputationSource.createDefault(Optional.empty());
        selectedMentor = BehaviorComputationSource.createDefault(Optional.empty());
        selectedTerm = BehaviorComputationSource.createDefault(Optional.empty());
        notes = BehaviorComputationSource.createDefault("");
        originalValues = BehaviorComputationSource.createDefault(new CourseDetails(null));
        coursesForTerm = BehaviorComputationSource.createDefault(Collections.emptyList());
        initializedSubject = CompletableSubject.create();
        initializedCompletable = initializedSubject.observeOn(AndroidSchedulers.mainThread());
        termOptionsLiveData = dbLoader.getAllTerms();
        mentorOptionsLiveData = dbLoader.getAllMentors();

        Observable<String> numberObservable = number.getObservable().map(Workers.asCached(AbstractAssessmentEntity.SINGLE_LINE_NORMALIZER::apply));
        Observable<String> titleObservable = title.getObservable().map(Workers.asCached(AbstractAssessmentEntity.SINGLE_LINE_NORMALIZER::apply));
        Observable<BinaryAlternate<Integer, ResourceMessageFactory>> competencyUnitsParsedObservable = competencyUnitsText.getObservable()
                .map(Workers.asCached(EditCourseViewModel::parseCompetencyUnits));
        Observable<Optional<Long>> selectedTermIdObservable = selectedTerm.getObservable().map(t -> t.map(AbstractEntity::getId).filter(i -> ID_NEW != i));
        Observable<Optional<ResourceMessageFactory>> expectedStartMessage = Observable.combineLatest(status.getObservable(), expectedStart.getObservable(),
                expectedEnd.getObservable(), selectedTerm.getObservable(), EditCourseViewModel::validateExpectedStart);
        Observable<Optional<ResourceMessageFactory>> expectedEndMessage = Observable.combineLatest(status.getObservable(), expectedStart.getObservable(),
                expectedEnd.getObservable(), selectedTerm.getObservable(), EditCourseViewModel::validateExpectedEnd);
        Observable<Optional<ResourceMessageFactory>> actualStartMessage = Observable.combineLatest(status.getObservable(), actualStart.getObservable(),
                actualEnd.getObservable(), EditCourseViewModel::validateActualStart);
        Observable<Optional<ResourceMessageFactory>> actualEndMessage = Observable.combineLatest(status.getObservable(), actualStart.getObservable(),
                actualEnd.getObservable(), EditCourseViewModel::validateActualEnd);
        Observable<Optional<Integer>> competencyUnitsObservable = competencyUnitsParsedObservable.map(BinaryAlternate::extractPrimary);
        Observable<Long> courseIdObservable = originalValues.getObservable().map(AbstractEntity::getId);
        Observable<Boolean> hasChangesObservable = Observable.combineLatest(
                originalValues.getObservable(),
                numberObservable,
                titleObservable,
                status.getObservable(),
                competencyUnitsParsedObservable.map(BinaryAlternate::extractPrimary),
                selectedMentor.getObservable().map(o -> o.map(AbstractEntity::getId)),
                selectedTermIdObservable,
                notes.getObservable().map(Workers.asCached(AbstractNotedEntity.MULTI_LINE_NORMALIZER::apply)),
                Observable.combineLatest(originalValues.getObservable(), expectedStart.getObservable(), expectedEnd.getObservable(),
                        actualStart.getObservable(), actualEnd.getObservable(),
                        (course, expectedStart, expectedEnd, actualStart, actualEnd) -> Objects.equals(expectedStart.orElse(null), course.getExpectedStart()) &&
                                Objects.equals(expectedEnd.orElse(null), course.getExpectedEnd()) &&
                                Objects.equals(actualStart.orElse(null), course.getExpectedStart()) &&
                                Objects.equals(actualEnd.orElse(null), course.getActualEnd())),
                (course, number, title, status, competencyUnits, selectedMentorId, selectedTermId, notes, datesUnchanged) ->
                        !(number.equals(course.getNumber()) && title.equals(course.getTitle()) && status == course.getStatus() &&
                                competencyUnits.map(i -> i == course.getCompetencyUnits()).orElse(false) &&
                                selectedMentorId.map(m -> Objects.equals(m, course.getMentorId())).orElseGet(() -> null == course.getMentorId()) &&
                                selectedTermId.map(m -> Objects.equals(m, course.getTermId())).orElse(false) &&
                                notes.equals(course.getNotes()) && datesUnchanged)
        );
        Observable<Boolean> termValidObservable = selectedTermIdObservable.map(Optional::isPresent);
        Observable<Boolean> numberValidObservable = numberObservable.map(s -> !s.isEmpty());
        Observable<Boolean> titleValidObservable = titleObservable.map(s -> !s.isEmpty());

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

        effectiveStartObserver = SubscribingLiveDataWrapper.ofOptional(Observable.combineLatest(expectedStart.getObservable(), actualStart.getObservable(),
                (e, a) -> (a.isPresent()) ? a : e).doAfterNext(d -> recalculateAlerts(d.orElse(null), actualStart.getValue().orElseGet(() ->
                expectedStart.getValue().orElse(null)))));
        effectiveEndObserver = SubscribingLiveDataWrapper.ofOptional(Observable.combineLatest(expectedEnd.getObservable(), actualEnd.getObservable(),
                (e, a) -> (a.isPresent()) ? a : e).doAfterNext(d -> recalculateAlerts(actualEnd.getValue().orElseGet(() -> expectedEnd.getValue().orElse(null)), d.orElse(null))));
        courseAlertsLiveData = new LiveDataWrapper<>(Collections.emptyList());
        assessmentsLiveData = new LiveDataWrapper<>(Collections.emptyList());
        termValidObserver = SubscribingLiveDataWrapper.of(false, termValidObservable);
        numberValidObserver = SubscribingLiveDataWrapper.of(false, numberValidObservable);
        titleValidObserver = SubscribingLiveDataWrapper.of(false, titleValidObservable);
        expectedStartValidationMessageObserver = SubscribingLiveDataWrapper.of(Optional.empty(), expectedStartMessage);
        expectedEndValidationMessageObserver = SubscribingLiveDataWrapper.of(Optional.empty(), expectedEndMessage);
        actualStartValidationMessageObserver = SubscribingLiveDataWrapper.of(Optional.empty(), actualStartMessage);
        actualEndValidationMessageObserver = SubscribingLiveDataWrapper.of(Optional.empty(), actualEndMessage);
        originalValuesLiveData = SubscribingLiveDataWrapper.of(originalValues.getObservable());
        competencyUnitsValidationMessageObserver = SubscribingLiveDataWrapper.of(Optional.empty(), competencyUnitsParsedObservable.map(BinaryAlternate::extractSecondary));
        competencyUnitsValueObserver = SubscribingLiveDataWrapper.of(Optional.empty(), competencyUnitsObservable);
        titleFactoryObserver = SubscribingLiveDataWrapper.of(c -> c.getString(R.string.title_activity_view_course), numberObservable.map(EditCourseViewModel::calculateViewTitleFactory));
        subTitleObserver = SubscribingLiveDataWrapper.of("", titleObservable);
        overviewFactoryObserver = SubscribingLiveDataWrapper.of(r -> "", Observable.combineLatest(status.getObservable(),
                competencyUnitsParsedObservable.map(BinaryAlternate::extractPrimary), expectedStart.getObservable(), expectedEnd.getObservable(),
                actualStart.getObservable(), actualEnd.getObservable(), selectedTerm.getObservable(), selectedMentor.getObservable(),
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
//        isValidObserver = SubscribingLiveDataWrapper.of(false, validObservable);
        compositeDisposable = new CompositeDisposable(effectiveStartObserver, effectiveEndObserver, termValidObserver, numberValidObserver, titleValidObserver,
                expectedStartValidationMessageObserver, expectedEndValidationMessageObserver, actualStartValidationMessageObserver, actualEndValidationMessageObserver,
                competencyUnitsValidationMessageObserver, titleFactoryObserver, subTitleObserver, overviewFactoryObserver, hasChangesObserver,
                canSaveObserver, canShareObserver, courseIdObservable.subscribe(this::observeCourseAlertsByCourseId),
                courseIdObservable.subscribe(this::observeAssessmentsByCourseId), selectedTermIdObservable.subscribe(this::observeCoursesByTermId));
    }

    void recalculateAlerts(@Nullable LocalDate startDate, @Nullable LocalDate endDate) {
        List<CourseAlert> list = getCourseAlertsLiveData().getValue();
        if (null != list) {
            for (CourseAlert a : list) {
                a.reCalculate(startDate, endDate);
            }
        }
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private void observeCoursesByTermId(Optional<Long> selectedTermId) {
        try {
            if (null != coursesForTermObserving) {
                if (selectedTermId.map(i -> Objects.equals(courseAlertsObserving.first, i)).orElse(false)) {
                    return;
                }
                compositeDisposable.remove(coursesForTermObserving.second);
                coursesForTermObserving = null;
            }
        } finally {
            if (selectedTermId.isPresent()) {
                long id = selectedTermId.get();
                coursesForTermObserving = new Pair<>(id, dbLoader.getCoursesObservableByTermId(id)
                        .subscribe(list -> coursesForTerm.onNext((null == list) ? Collections.emptyList() : list)));
                compositeDisposable.add(coursesForTermObserving.second);
            } else {
                coursesForTerm.onNext(Collections.emptyList());
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
                courseAlertsLiveData.postValue(Collections.emptyList());
                courseAlertsObserving = null;
            }
        }
    }

    private void onAlertsLoaded(List<CourseAlert> courseAlerts) {
        for (CourseAlert a : courseAlerts) {
            a.calculate(this);
        }
        courseAlertsLiveData.postValue(courseAlerts);
    }

    private void observeAssessmentsByCourseId(long courseId) {
        try {
            if (null != assessmentsObserving) {
                if (assessmentsObserving.first == courseId) {
                    return;
                }
                compositeDisposable.remove(courseAlertsObserving.second);
                courseAlertsObserving = null;
            }
        } finally {
            if (ID_NEW != courseId) {
                assessmentsObserving = new Pair<>(courseId, dbLoader.getAssessmentsObservableByCourseId(courseId).subscribe(assessmentsLiveData::postValue));
                compositeDisposable.add(courseAlertsObserving.second);
            } else {
                assessmentsLiveData.postValue(Collections.emptyList());
                courseAlertsObserving = null;
            }
        }
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    @NonNull
    public static Function<Resources, CharSequence> calculateOverviewFactory(@NonNull CourseStatus status, Optional<Integer> competencyUnits,
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
    public static Function<Resources, CharSequence> calculateViewTitleFactory(@NonNull String number) {
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
        return originalValues.getValue().getId();
    }

    @Nullable
    public AbstractTermEntity<?> getSelectedTerm() {
        return selectedTerm.getValue().orElse(null);
    }

    public void setSelectedTerm(AbstractTermEntity<?> term) {
        selectedTerm.onNext(Optional.ofNullable(term));
    }

    @Nullable
    public AbstractMentorEntity<?> getSelectedMentor() {
        return selectedMentor.getValue().orElse(null);
    }

    public void setSelectedMentor(@Nullable AbstractMentorEntity<?> mentor) {
        selectedMentor.onNext(Optional.ofNullable(mentor));
    }

    @NonNull
    public String getNumber() {
        return number.getValue();
    }

    public void setNumber(String value) {
        number.onNext((null == value) ? "" : value);
    }

    @NonNull
    public String getTitle() {
        return title.getValue();
    }

    public void setTitle(String value) {
        title.onNext((null == value) ? "" : value);
    }

    @Nullable
    public LocalDate getExpectedStart() {
        return expectedStart.getValue().orElse(null);
    }

    public void setExpectedStart(LocalDate value) {
        expectedStart.onNext(Optional.ofNullable(value));
    }

    @Nullable
    public LocalDate getActualStart() {
        return actualStart.getValue().orElse(null);
    }

    public void setActualStart(LocalDate value) {
        actualStart.onNext(Optional.ofNullable(value));
    }

    @Nullable
    public LocalDate getExpectedEnd() {
        return expectedEnd.getValue().orElse(null);
    }

    public void setExpectedEnd(LocalDate value) {
        expectedEnd.onNext(Optional.ofNullable(value));
    }

    @Nullable
    public LocalDate getActualEnd() {
        return actualEnd.getValue().orElse(null);
    }

    public void setActualEnd(LocalDate value) {
        actualEnd.onNext(Optional.ofNullable(value));
    }

    @NonNull
    public String getCompetencyUnitsText() {
        return competencyUnitsText.getValue();
    }

    public void setCompetencyUnitsText(String value) {
        competencyUnitsText.onNext((null == value) ? "" : value);
    }

    @NonNull
    public CourseStatus getStatus() {
        return status.getValue();
    }

    public synchronized void setStatus(CourseStatus status) {
        this.status.onNext((null == status) ? CourseStatus.UNPLANNED : status);
    }

    @NonNull
    public String getNotes() {
        return notes.getValue();
    }

    public synchronized void setNotes(String value) {
        notes.onNext((null == value) ? "" : value);
    }

    public List<TermCourseListItem> getCoursesForTerm() {
        return coursesForTerm.getValue();
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
        return assessmentsLiveData.getLiveData();
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
            entity.restoreState(state, true);
            long id = entity.getId();
            if (ID_NEW == id || fromInitializedState) {
                Log.d(LOG_TAG, "Restoring courseEntity from saved state");
                String key = IdIndexedEntity.stateKey(AppDb.TABLE_NAME_TERMS, Term.COLNAME_ID, false);
                if (state.containsKey(key)) {
                    TermEntity term = new TermEntity();
                    term.restoreState(state, false);
                    selectedTerm.onNext(Optional.of(term));
                } else {
                    selectedTerm.onNext(Optional.empty());
                }
                key = IdIndexedEntity.stateKey(AppDb.TABLE_NAME_MENTORS, Mentor.COLNAME_ID, false);
                if (state.containsKey(key)) {
                    MentorEntity mentor = new MentorEntity();
                    mentor.restoreState(state, false);
                    selectedMentor.onNext(Optional.of(mentor));
                } else {
                    selectedMentor.onNext(Optional.empty());
                }
                number.onNext(state.getString(IdIndexedEntity.stateKey(AppDb.TABLE_NAME_COURSES, Course.COLNAME_NUMBER, false), ""));
                title.onNext(state.getString(IdIndexedEntity.stateKey(AppDb.TABLE_NAME_COURSES, Course.COLNAME_TITLE, false), ""));
                notes.onNext(state.getString(IdIndexedEntity.stateKey(AppDb.TABLE_NAME_COURSES, Course.COLNAME_NOTES, false), ""));
                key = IdIndexedEntity.stateKey(AppDb.TABLE_NAME_COURSES, Course.COLNAME_STATUS, false);
                status.onNext((state.containsKey(key)) ? CourseStatus.valueOf(state.getString(key)) : CourseStatus.UNPLANNED);
                key = IdIndexedEntity.stateKey(AppDb.TABLE_NAME_COURSES, Course.COLNAME_EXPECTED_START, false);
                if (state.containsKey(key)) {
                    expectedStart.onNext(Optional.of(LocalDateConverter.toLocalDate(state.getLong(key))));
                } else {
                    expectedStart.onNext(Optional.empty());
                }
                key = IdIndexedEntity.stateKey(AppDb.TABLE_NAME_COURSES, Course.COLNAME_ACTUAL_START, false);
                if (state.containsKey(key)) {
                    actualStart.onNext(Optional.of(LocalDateConverter.toLocalDate(state.getLong(key))));
                } else {
                    actualStart.onNext(Optional.empty());
                }
                key = IdIndexedEntity.stateKey(AppDb.TABLE_NAME_COURSES, Course.COLNAME_EXPECTED_END, false);
                if (state.containsKey(key)) {
                    expectedEnd.onNext(Optional.of(LocalDateConverter.toLocalDate(state.getLong(key))));
                } else {
                    expectedEnd.onNext(Optional.empty());
                }
                key = IdIndexedEntity.stateKey(AppDb.TABLE_NAME_COURSES, Course.COLNAME_ACTUAL_END, false);
                if (state.containsKey(key)) {
                    actualEnd.onNext(Optional.of(LocalDateConverter.toLocalDate(state.getLong(key))));
                } else {
                    actualEnd.onNext(Optional.empty());
                }
                competencyUnitsText.onNext(state.getString(STATE_KEY_COMPETENCY_UNITS_TEXT, ""));
            } else {
                Log.d(LOG_TAG, "Loading courseEntity from database");
                return dbLoader.getCourseById(id).doOnSuccess(this::onEntityLoadedFromDb);
            }
        } else {
            Log.d(LOG_TAG, "No saved state or arguments");
            competencyUnitsText.onNext(NumberFormat.getIntegerInstance().format(entity.getCompetencyUnits()));
        }
        originalValues.onNext(entity);
        initializedSubject.onComplete();
        return Single.just(entity).observeOn(AndroidSchedulers.mainThread());
    }

    private void onEntityLoadedFromDb(CourseDetails entity) {
        Log.d(LOG_TAG, String.format("Loaded %s from database", entity));
        selectedTerm.onNext(Optional.ofNullable(entity.getTerm()));
        selectedMentor.onNext(Optional.ofNullable(entity.getMentor()));
        number.onNext(entity.getNumber());
        title.onNext(entity.getTitle());
        status.onNext(entity.getStatus());
        expectedStart.onNext(Optional.ofNullable(entity.getExpectedStart()));
        expectedEnd.onNext(Optional.ofNullable(entity.getExpectedEnd()));
        actualStart.onNext(Optional.ofNullable(entity.getActualStart()));
        actualEnd.onNext(Optional.ofNullable(entity.getActualEnd()));
        competencyUnitsText.onNext(NumberFormat.getIntegerInstance().format(entity.getCompetencyUnits()));
        notes.onNext(entity.getNotes());
        originalValues.onNext(entity);
        initializedSubject.onComplete();
    }

    public void saveViewModelState(@NonNull Bundle outState) {
        outState.putBoolean(STATE_KEY_STATE_INITIALIZED, true);
        originalValues.getValue().saveState(outState, true);
        selectedTerm.getValue().ifPresent(t -> t.saveState(outState, false));
        selectedMentor.getValue().ifPresent(m -> m.saveState(outState, false));
        outState.putString(IdIndexedEntity.stateKey(AppDb.TABLE_NAME_COURSES, Course.COLNAME_NUMBER, false), number.getValue());
        outState.putString(IdIndexedEntity.stateKey(AppDb.TABLE_NAME_COURSES, Course.COLNAME_STATUS, false), status.getValue().name());
        outState.putString(IdIndexedEntity.stateKey(AppDb.TABLE_NAME_COURSES, Course.COLNAME_TITLE, false), title.getValue());
        outState.putString(IdIndexedEntity.stateKey(AppDb.TABLE_NAME_COURSES, Course.COLNAME_NOTES, false), notes.getValue());
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
        outState.putString(STATE_KEY_COMPETENCY_UNITS_TEXT, competencyUnitsText.getValue());
    }

    public synchronized Single<ResourceMessageResult> save(boolean ignoreWarnings) {
        AbstractTermEntity<?> term = selectedTerm.getValue().orElse(null);
        if (null == term) {
            return Single.just(ValidationMessage.ofSingleError(R.string.message_term_not_selected)).observeOn(AndroidSchedulers.mainThread());
        }
        Integer cu = competencyUnitsValueObserver.getLiveData().getValue().orElse(null);
        if (null == cu) {
            return Single.just(ValidationMessage.ofSingleError(R.string.message_required)).observeOn(AndroidSchedulers.mainThread());
        }
        CourseDetails originalValues = this.originalValues.getValue();
        CourseEntity entity = originalValues.toEntity();
        entity.setTermId(Objects.requireNonNull(term).getId());
        entity.setMentorId(selectedMentor.getValue().map(AbstractEntity::getId).orElse(null));
        entity.setNumber(number.getValue());
        entity.setTitle(title.getValue());
        entity.setStatus(status.getValue());
        entity.setExpectedStart(expectedStart.getValue().orElse(null));
        entity.setExpectedEnd(expectedEnd.getValue().orElse(null));
        entity.setActualStart(actualStart.getValue().orElse(null));
        entity.setActualEnd(actualEnd.getValue().orElse(null));
        entity.setCompetencyUnits(cu);
        entity.setNotes(notes.getValue());
        Log.d(LOG_TAG, String.format("Saving %s to database", entity));
        return dbLoader.saveCourse(entity, ignoreWarnings).doOnSuccess(m -> {
            if (m.isSucceeded()) {
                this.originalValues.onNext(new CourseDetails(entity, term, selectedMentor.getValue().orElse(null)));
            }
        });
    }

    public Single<ResourceMessageResult> delete(boolean ignoreWarnings) {
        Log.d(LOG_TAG, "Enter delete(" + ignoreWarnings + ")");
        return dbLoader.deleteCourse(Objects.requireNonNull(originalValues.getValue()).toEntity(), ignoreWarnings);
    }

    public LiveData<List<CourseAlert>> getCourseAlertsLiveData() {
        return courseAlertsLiveData.getLiveData();
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
}