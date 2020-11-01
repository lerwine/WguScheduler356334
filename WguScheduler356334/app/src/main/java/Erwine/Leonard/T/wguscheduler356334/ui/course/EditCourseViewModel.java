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
import Erwine.Leonard.T.wguscheduler356334.util.BinaryAlternate;
import Erwine.Leonard.T.wguscheduler356334.util.LiveDataWrapper;
import Erwine.Leonard.T.wguscheduler356334.util.SubscribingLiveDataWrapper;
import Erwine.Leonard.T.wguscheduler356334.util.WguSchedulerViewModel;
import Erwine.Leonard.T.wguscheduler356334.util.Workers;
import Erwine.Leonard.T.wguscheduler356334.util.validation.MessageLevel;
import Erwine.Leonard.T.wguscheduler356334.util.validation.ResourceMessageFactory;
import Erwine.Leonard.T.wguscheduler356334.util.validation.ResourceMessageResult;
import Erwine.Leonard.T.wguscheduler356334.util.validation.ValidationMessage;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.BehaviorSubject;
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
    private final BehaviorSubject<String> numberSubject;
    private final BehaviorSubject<String> titleSubject;
    private final BehaviorSubject<CourseStatus> statusSubject;
    private final BehaviorSubject<String> competencyUnitsTextSubject;
    private final BehaviorSubject<Optional<LocalDate>> expectedStartSubject;
    private final BehaviorSubject<Optional<LocalDate>> actualStartSubject;
    private final BehaviorSubject<Optional<LocalDate>> expectedEndSubject;
    private final BehaviorSubject<Optional<LocalDate>> actualEndSubject;
    private final BehaviorSubject<Optional<AbstractMentorEntity<?>>> selectedMentorSubject;
    private final BehaviorSubject<Optional<AbstractTermEntity<?>>> selectedTermSubject;
    private final BehaviorSubject<String> notesSubject;
    private final BehaviorSubject<CourseDetails> originalValuesSubject;
    private final BehaviorSubject<List<TermCourseListItem>> coursesForTermSubject;
    private final CompletableSubject initializedSubject;
    private final Completable initializedCompletable;

    private final LiveData<List<TermListItem>> termOptionsLiveData;
    private final LiveData<List<MentorListItem>> mentorOptionsLiveData;
    private LiveData<List<AssessmentEntity>> assessmentsLiveData;
    private final LiveDataWrapper<List<CourseAlert>> alertsLiveData;
    private final SubscribingLiveDataWrapper<LocalDate> effectiveStartLiveData;
    private final SubscribingLiveDataWrapper<LocalDate> effectiveEndLiveData;
    private final SubscribingLiveDataWrapper<Boolean> termValidLiveData;
    private final SubscribingLiveDataWrapper<Boolean> numberValidLiveData;
    private final SubscribingLiveDataWrapper<Boolean> titleValidLiveData;
    private final SubscribingLiveDataWrapper<Optional<ResourceMessageFactory>> expectedStartValidationMessageLiveData;
    private final SubscribingLiveDataWrapper<Optional<ResourceMessageFactory>> expectedEndValidationMessageLiveData;
    private final SubscribingLiveDataWrapper<Optional<ResourceMessageFactory>> actualStartValidationMessageLiveData;
    private final SubscribingLiveDataWrapper<Optional<ResourceMessageFactory>> actualEndValidationMessageLiveData;
    private final SubscribingLiveDataWrapper<Optional<ResourceMessageFactory>> competencyUnitsValidationMessageLiveData;
    private final SubscribingLiveDataWrapper<Optional<Integer>> competencyUnitsValueLiveData;
    private final SubscribingLiveDataWrapper<Function<Resources, CharSequence>> titleFactoryLiveData;
    private final SubscribingLiveDataWrapper<String> subTitleLiveData;
    private final SubscribingLiveDataWrapper<Function<Resources, CharSequence>> overviewFactoryLiveData;
    private final SubscribingLiveDataWrapper<Boolean> canSaveLiveData;
    private final SubscribingLiveDataWrapper<Boolean> canShareLiveData;
    private final SubscribingLiveDataWrapper<Boolean> hasChangesLiveData;
    private final SubscribingLiveDataWrapper<Boolean> isValidLiveData;
    //    private Observable<List<TermCourseListItem>> coursesForTermObservable;
    private Disposable coursesForTermObserving;
    //    private Observable<List<CourseAlert>> alertsObservable;
    private Disposable alertsObserving;
    private boolean fromInitializedState;

    public EditCourseViewModel(@NonNull Application application) {
        super(application);
        dbLoader = DbLoader.getInstance(getApplication());
        compositeDisposable = new CompositeDisposable();
        numberSubject = BehaviorSubject.createDefault("");
        titleSubject = BehaviorSubject.createDefault("");
        statusSubject = BehaviorSubject.createDefault(CourseStatus.UNPLANNED);
        competencyUnitsTextSubject = BehaviorSubject.createDefault("");
        expectedStartSubject = BehaviorSubject.createDefault(Optional.empty());
        actualStartSubject = BehaviorSubject.createDefault(Optional.empty());
        expectedEndSubject = BehaviorSubject.createDefault(Optional.empty());
        actualEndSubject = BehaviorSubject.createDefault(Optional.empty());
        selectedMentorSubject = BehaviorSubject.createDefault(Optional.empty());
        selectedTermSubject = BehaviorSubject.createDefault(Optional.empty());
        notesSubject = BehaviorSubject.createDefault("");
        originalValuesSubject = BehaviorSubject.createDefault(new CourseDetails(null));
        coursesForTermSubject = BehaviorSubject.createDefault(Collections.emptyList());
        initializedSubject = CompletableSubject.create();
        termOptionsLiveData = dbLoader.getAllTerms();
        mentorOptionsLiveData = dbLoader.getAllMentors();

        Scheduler computationScheduler = Schedulers.computation();
        initializedCompletable = initializedSubject.observeOn(computationScheduler);
        Observable<String> numberObservable = numberSubject.observeOn(computationScheduler).map(Workers.asCached(AbstractAssessmentEntity.SINGLE_LINE_NORMALIZER::apply));
        Observable<String> titleObservable = titleSubject.observeOn(computationScheduler).map(Workers.asCached(AbstractAssessmentEntity.SINGLE_LINE_NORMALIZER::apply));
        Observable<BinaryAlternate<Integer, ResourceMessageFactory>> competencyUnitsParsedObservable = competencyUnitsTextSubject.observeOn(computationScheduler)
                .map(Workers.asCached(EditCourseViewModel::parseCompetencyUnits));
        Observable<String> notesObservable = notesSubject.observeOn(computationScheduler).map(Workers.asCached(AbstractNotedEntity.MULTI_LINE_NORMALIZER::apply));
        Observable<CourseStatus> statusObservable = statusSubject.observeOn(computationScheduler);
        Observable<Optional<LocalDate>> expectedStartValueObservable = expectedStartSubject.observeOn(computationScheduler);
        Observable<Optional<LocalDate>> actualStartValueObservable = actualStartSubject.observeOn(computationScheduler);
        Observable<Optional<LocalDate>> expectedEndValueObservable = expectedEndSubject.observeOn(computationScheduler);
        Observable<Optional<LocalDate>> actualEndValueObservable = actualEndSubject.observeOn(computationScheduler);
        Observable<Optional<AbstractTermEntity<?>>> selectedTermObservable = selectedTermSubject.observeOn(computationScheduler);
        Observable<Optional<Long>> selectedTermIdObservable = selectedTermObservable.map(t -> t.map(AbstractEntity::getId).filter(i -> ID_NEW != i));
        Observable<Optional<AbstractMentorEntity<?>>> selectedMentorObservable = selectedMentorSubject.observeOn(computationScheduler);
        Observable<Optional<ResourceMessageFactory>> expectedStartMessage = Observable.combineLatest(statusObservable, expectedStartValueObservable,
                expectedEndValueObservable, selectedTermObservable, EditCourseViewModel::validateExpectedStart);
        Observable<Optional<ResourceMessageFactory>> expectedEndMessage = Observable.combineLatest(statusObservable, expectedStartValueObservable,
                expectedEndValueObservable, selectedTermObservable, EditCourseViewModel::validateExpectedEnd);
        Observable<Optional<ResourceMessageFactory>> actualStartMessage = Observable.combineLatest(statusObservable, expectedStartValueObservable,
                expectedEndValueObservable, EditCourseViewModel::validateActualStart);
        Observable<Optional<ResourceMessageFactory>> actualEndMessage = Observable.combineLatest(statusObservable, expectedStartValueObservable,
                expectedEndValueObservable, EditCourseViewModel::validateActualEnd);
        Observable<Optional<LocalDate>> effectiveStartObservable = Observable.combineLatest(expectedStartValueObservable, actualStartValueObservable,
                (e, a) -> (a.isPresent()) ? a : e);
        Observable<Optional<LocalDate>> effectiveEndObservable = Observable.combineLatest(expectedEndValueObservable, actualEndValueObservable,
                (e, a) -> (a.isPresent()) ? a : e);
        Observable<CourseDetails> originalValuesObservable = originalValuesSubject.observeOn(computationScheduler);
        Observable<Optional<Integer>> competencyUnitsObservable = competencyUnitsParsedObservable.map(BinaryAlternate::extractPrimary);
        Observable<Boolean> hasChangesObservable = Observable.combineLatest(
                originalValuesObservable,
                numberObservable,
                titleObservable,
                statusObservable,
                competencyUnitsParsedObservable.map(BinaryAlternate::extractPrimary),
                selectedMentorObservable.map(o -> o.map(AbstractEntity::getId)),
                selectedTermIdObservable,
                notesObservable,
                Observable.combineLatest(originalValuesObservable, expectedStartValueObservable, expectedEndValueObservable, actualStartValueObservable, actualEndValueObservable,
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
        Observable<Optional<ResourceMessageFactory>> competencyUnitsMessage = competencyUnitsParsedObservable.map(BinaryAlternate::extractSecondary);

        Observable<Boolean> validObservable = Observable.combineLatest(termValidObservable, numberValidObservable, titleValidObservable,
                expectedStartMessage.map(o -> o.map(m -> m.getLevel() == MessageLevel.INFO).orElse(true)),
                expectedEndMessage.map(o -> o.map(m -> m.getLevel() == MessageLevel.INFO).orElse(true)),
                actualStartMessage.map(o -> o.map(m -> m.getLevel() == MessageLevel.INFO).orElse(true)),
                actualEndMessage.map(o -> o.map(m -> m.getLevel() == MessageLevel.INFO).orElse(true)),
                (termValid, numberValid, titleValid, expectedStartValid, expectedEndValid, actualStartValid, actualEndValid) ->
                        termValid && numberValid && titleValid && expectedStartValid && expectedEndValid && actualStartValid && actualEndValid);

        effectiveStartLiveData = SubscribingLiveDataWrapper.ofOptional(effectiveStartObservable);
        effectiveEndLiveData = SubscribingLiveDataWrapper.ofOptional(effectiveEndObservable);
        alertsLiveData = new LiveDataWrapper<>(Collections.emptyList());
        termValidLiveData = SubscribingLiveDataWrapper.of(false, termValidObservable);
        numberValidLiveData = SubscribingLiveDataWrapper.of(false, numberValidObservable);
        titleValidLiveData = SubscribingLiveDataWrapper.of(false, titleValidObservable);
        expectedStartValidationMessageLiveData = SubscribingLiveDataWrapper.of(Optional.empty(), expectedStartMessage);
        expectedEndValidationMessageLiveData = SubscribingLiveDataWrapper.of(Optional.empty(), expectedEndMessage);
        actualStartValidationMessageLiveData = SubscribingLiveDataWrapper.of(Optional.empty(), actualStartMessage);
        actualEndValidationMessageLiveData = SubscribingLiveDataWrapper.of(Optional.empty(), actualEndMessage);
        originalValuesLiveData = SubscribingLiveDataWrapper.of(originalValuesObservable);
        competencyUnitsValidationMessageLiveData = SubscribingLiveDataWrapper.of(Optional.empty(), competencyUnitsMessage);
        competencyUnitsValueLiveData = SubscribingLiveDataWrapper.of(Optional.empty(), competencyUnitsObservable);
        titleFactoryLiveData = SubscribingLiveDataWrapper.of(c -> c.getString(R.string.title_activity_view_course), numberObservable.map(EditCourseViewModel::calculateViewTitleFactory));
        subTitleLiveData = SubscribingLiveDataWrapper.of("", titleObservable);
        overviewFactoryLiveData = SubscribingLiveDataWrapper.of(r -> "", Observable.combineLatest(statusObservable, competencyUnitsParsedObservable.map(BinaryAlternate::extractPrimary), expectedStartValueObservable,
                expectedEndValueObservable, actualStartValueObservable, actualEndValueObservable, selectedTermObservable, selectedMentorObservable,
                EditCourseViewModel::calculateOverviewFactory));
        canSaveLiveData = SubscribingLiveDataWrapper.of(false, Observable.combineLatest(validObservable, hasChangesObservable, (v, c) -> v && c));
        canShareLiveData = SubscribingLiveDataWrapper.of(false, Observable.combineLatest(validObservable, hasChangesObservable, (v, c) -> v && !c));
        hasChangesLiveData = SubscribingLiveDataWrapper.of(false, hasChangesObservable);
        isValidLiveData = SubscribingLiveDataWrapper.of(false, validObservable);
        compositeDisposable.addAll(effectiveStartLiveData, effectiveEndLiveData, termValidLiveData, numberValidLiveData, titleValidLiveData,
                expectedStartValidationMessageLiveData, expectedEndValidationMessageLiveData, actualStartValidationMessageLiveData, actualEndValidationMessageLiveData,
                competencyUnitsValidationMessageLiveData, titleFactoryLiveData, subTitleLiveData, overviewFactoryLiveData, hasChangesLiveData, isValidLiveData,
                canSaveLiveData, canShareLiveData);
        compositeDisposable.add(originalValuesObservable.subscribe(originalValues -> {
            try {
                if (null != alertsObserving) {
                    compositeDisposable.remove(alertsObserving);
                    alertsObserving = null;
                }
            } finally {
                long id = originalValues.getId();
                if (ID_NEW != id) {
                    alertsObserving = dbLoader.getAlertsObservableByCourseId(id).observeOn(computationScheduler).subscribe(alertsLiveData::postValue);
                    compositeDisposable.add(alertsObserving);
                } else {
                    coursesForTermSubject.onNext(Collections.emptyList());
                    alertsObserving = null;
                }
            }
        }));
        compositeDisposable.add(selectedTermIdObservable.subscribe(selectedTermId -> {
            try {
                if (null != coursesForTermObserving) {
                    compositeDisposable.remove(coursesForTermObserving);
                    coursesForTermObserving = null;
                }
            } finally {
                if (selectedTermId.isPresent()) {
                    coursesForTermObserving = dbLoader.getCoursesObservableByTermId(selectedTermId.get()).observeOn(computationScheduler)
                            .subscribe(list -> coursesForTermSubject.onNext((null == list) ? Collections.emptyList() : list));
                    compositeDisposable.add(coursesForTermObserving);
                } else {
                    coursesForTermSubject.onNext(Collections.emptyList());
                    coursesForTermObserving = null;
                }
            }
        }));
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
        return expectedEnd.map(e ->
                expectedStart.flatMap(s ->
                        (s.compareTo(e) > 0) ?
                                Optional.of(ResourceMessageFactory.ofError(R.string.message_start_after_end)) :
                                selectedTerm.flatMap(t -> {
                                    LocalDate d = t.getStart();
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
        return originalValuesSubject.getValue().getId();
    }

    @Nullable
    public AbstractTermEntity<?> getSelectedTerm() {
        return selectedTermSubject.getValue().orElse(null);
    }

    public void setSelectedTerm(AbstractTermEntity<?> selectedTerm) {
        selectedTermSubject.onNext(Optional.ofNullable(selectedTerm));
    }

    @Nullable
    public AbstractMentorEntity<?> getSelectedMentor() {
        return selectedMentorSubject.getValue().orElse(null);
    }

    public void setSelectedMentor(@Nullable AbstractMentorEntity<?> selectedMentor) {
        selectedMentorSubject.onNext(Optional.ofNullable(selectedMentor));
    }

    @NonNull
    public String getNumber() {
        return numberSubject.getValue();
    }

    public void setNumber(String value) {
        numberSubject.onNext((null == value) ? "" : value);
    }

    @NonNull
    public String getTitle() {
        return titleSubject.getValue();
    }

    public void setTitle(String value) {
        titleSubject.onNext((null == value) ? "" : value);
    }

    @Nullable
    public LocalDate getExpectedStart() {
        return expectedStartSubject.getValue().orElse(null);
    }

    public void setExpectedStart(LocalDate value) {
        expectedStartSubject.onNext(Optional.ofNullable(value));
    }

    @Nullable
    public LocalDate getActualStart() {
        return actualStartSubject.getValue().orElse(null);
    }

    public void setActualStart(LocalDate value) {
        actualStartSubject.onNext(Optional.ofNullable(value));
    }

    @Nullable
    public LocalDate getExpectedEnd() {
        return expectedEndSubject.getValue().orElse(null);
    }

    public void setExpectedEnd(LocalDate value) {
        expectedEndSubject.onNext(Optional.ofNullable(value));
    }

    @Nullable
    public LocalDate getActualEnd() {
        return actualEndSubject.getValue().orElse(null);
    }

    public void setActualEnd(LocalDate value) {
        actualEndSubject.onNext(Optional.ofNullable(value));
    }

    @NonNull
    public String getCompetencyUnitsText() {
        return competencyUnitsTextSubject.getValue();
    }

    public void setCompetencyUnitsText(String value) {
        competencyUnitsTextSubject.onNext((null == value) ? "" : value);
    }

    @NonNull
    public CourseStatus getStatus() {
        return statusSubject.getValue();
    }

    public synchronized void setStatus(CourseStatus status) {
        statusSubject.onNext((null == status) ? CourseStatus.UNPLANNED : status);
    }

    @NonNull
    public String getNotes() {
        return notesSubject.getValue();
    }

    public synchronized void setNotes(String value) {
        notesSubject.onNext((null == value) ? "" : value);
    }

    public List<TermCourseListItem> getCoursesForTerm() {
        return coursesForTermSubject.getValue();
    }

    public Completable getInitializedCompletable() {
        return initializedCompletable;
    }

    public LiveData<LocalDate> getEffectiveStartLiveData() {
        return effectiveStartLiveData.getLiveData();
    }

    public LiveData<LocalDate> getEffectiveEndLiveData() {
        return effectiveEndLiveData.getLiveData();
    }

    public LiveData<Boolean> getTermValidLiveData() {
        return termValidLiveData.getLiveData();
    }

    public LiveData<Boolean> getNumberValidLiveData() {
        return numberValidLiveData.getLiveData();
    }

    public LiveData<Boolean> getTitleValidLiveData() {
        return titleValidLiveData.getLiveData();
    }

    // TODO: Bind to this
    public LiveData<List<TermListItem>> getTermOptionsLiveData() {
        return termOptionsLiveData;
    }

    // TODO: Bind to this
    public LiveData<List<MentorListItem>> getMentorOptionsLiveData() {
        return mentorOptionsLiveData;
    }

    // TODO: Bind to this
    public LiveData<List<AssessmentEntity>> getAssessmentsLiveData() {
        return assessmentsLiveData;
    }

    // TODO: Bind to this
    public LiveData<Optional<ResourceMessageFactory>> getExpectedStartValidationMessageLiveData() {
        return expectedStartValidationMessageLiveData.getLiveData();
    }

    // TODO: Bind to this
    public LiveData<Optional<ResourceMessageFactory>> getExpectedEndValidationMessageLiveData() {
        return expectedEndValidationMessageLiveData.getLiveData();
    }

    // TODO: Bind to this
    public LiveData<Optional<ResourceMessageFactory>> getActualStartValidationMessageLiveData() {
        return actualStartValidationMessageLiveData.getLiveData();
    }

    // TODO: Bind to this
    public LiveData<Optional<ResourceMessageFactory>> getActualEndValidationMessageLiveData() {
        return actualEndValidationMessageLiveData.getLiveData();
    }

    // TODO: Bind to this
    public LiveData<Optional<ResourceMessageFactory>> getCompetencyUnitsValidationMessageLiveData() {
        return competencyUnitsValidationMessageLiveData.getLiveData();
    }

    // TODO: Bind to this
    public LiveData<Boolean> getCanSaveLiveData() {
        return canSaveLiveData.getLiveData();
    }

    // TODO: Bind to this
    public LiveData<Boolean> getCanShareLiveData() {
        return canShareLiveData.getLiveData();
    }

    // TODO: Bind to this
    public LiveData<Boolean> getHasChangesLiveData() {
        return hasChangesLiveData.getLiveData();
    }

    // TODO: Bind to this
    public LiveData<Boolean> getIsValidLiveData() {
        return isValidLiveData.getLiveData();
    }

    public LiveData<Function<Resources, CharSequence>> getTitleFactoryLiveData() {
        return titleFactoryLiveData.getLiveData();
    }

    public LiveData<String> getSubTitleLiveData() {
        return subTitleLiveData.getLiveData();
    }

    public LiveData<Function<Resources, CharSequence>> getOverviewFactoryLiveData() {
        return overviewFactoryLiveData.getLiveData();
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
                    selectedTermSubject.onNext(Optional.of(term));
                } else {
                    selectedTermSubject.onNext(Optional.empty());
                }
                key = IdIndexedEntity.stateKey(AppDb.TABLE_NAME_MENTORS, Mentor.COLNAME_ID, false);
                if (state.containsKey(key)) {
                    MentorEntity mentor = new MentorEntity();
                    mentor.restoreState(state, false);
                    selectedMentorSubject.onNext(Optional.of(mentor));
                } else {
                    selectedMentorSubject.onNext(Optional.empty());
                }
                numberSubject.onNext(state.getString(IdIndexedEntity.stateKey(AppDb.TABLE_NAME_COURSES, Course.COLNAME_NUMBER, false), ""));
                titleSubject.onNext(state.getString(IdIndexedEntity.stateKey(AppDb.TABLE_NAME_COURSES, Course.COLNAME_TITLE, false), ""));
                notesSubject.onNext(state.getString(IdIndexedEntity.stateKey(AppDb.TABLE_NAME_COURSES, Course.COLNAME_NOTES, false), ""));
                key = IdIndexedEntity.stateKey(AppDb.TABLE_NAME_COURSES, Course.COLNAME_STATUS, false);
                statusSubject.onNext((state.containsKey(key)) ? CourseStatus.valueOf(state.getString(key)) : CourseStatus.UNPLANNED);
                key = IdIndexedEntity.stateKey(AppDb.TABLE_NAME_COURSES, Course.COLNAME_EXPECTED_START, false);
                if (state.containsKey(key)) {
                    expectedStartSubject.onNext(Optional.of(LocalDateConverter.toLocalDate(state.getLong(key))));
                } else {
                    expectedStartSubject.onNext(Optional.empty());
                }
                key = IdIndexedEntity.stateKey(AppDb.TABLE_NAME_COURSES, Course.COLNAME_ACTUAL_START, false);
                if (state.containsKey(key)) {
                    actualStartSubject.onNext(Optional.of(LocalDateConverter.toLocalDate(state.getLong(key))));
                } else {
                    actualStartSubject.onNext(Optional.empty());
                }
                key = IdIndexedEntity.stateKey(AppDb.TABLE_NAME_COURSES, Course.COLNAME_EXPECTED_END, false);
                if (state.containsKey(key)) {
                    expectedEndSubject.onNext(Optional.of(LocalDateConverter.toLocalDate(state.getLong(key))));
                } else {
                    expectedEndSubject.onNext(Optional.empty());
                }
                key = IdIndexedEntity.stateKey(AppDb.TABLE_NAME_COURSES, Course.COLNAME_ACTUAL_END, false);
                if (state.containsKey(key)) {
                    actualEndSubject.onNext(Optional.of(LocalDateConverter.toLocalDate(state.getLong(key))));
                } else {
                    actualEndSubject.onNext(Optional.empty());
                }
                competencyUnitsTextSubject.onNext(state.getString(STATE_KEY_COMPETENCY_UNITS_TEXT, ""));
            } else {
                Log.d(LOG_TAG, "Loading courseEntity from database");
                return dbLoader.getCourseById(id).doOnSuccess(this::onEntityLoadedFromDb);
            }
        } else {
            Log.d(LOG_TAG, "No saved state or arguments");
            competencyUnitsTextSubject.onNext(NumberFormat.getIntegerInstance().format(entity.getCompetencyUnits()));
        }
        originalValuesSubject.onNext(entity);
        initializedSubject.onComplete();
        return Single.just(entity).observeOn(AndroidSchedulers.mainThread());
    }

    private void onEntityLoadedFromDb(CourseDetails entity) {
        Log.d(LOG_TAG, String.format("Loaded %s from database", entity));
        selectedTermSubject.onNext(Optional.ofNullable(entity.getTerm()));
        selectedMentorSubject.onNext(Optional.ofNullable(entity.getMentor()));
        numberSubject.onNext(entity.getNumber());
        titleSubject.onNext(entity.getTitle());
        statusSubject.onNext(entity.getStatus());
        expectedStartSubject.onNext(Optional.ofNullable(entity.getExpectedStart()));
        expectedEndSubject.onNext(Optional.ofNullable(entity.getExpectedEnd()));
        actualStartSubject.onNext(Optional.ofNullable(entity.getActualStart()));
        actualEndSubject.onNext(Optional.ofNullable(entity.getActualEnd()));
        competencyUnitsTextSubject.onNext(NumberFormat.getIntegerInstance().format(entity.getCompetencyUnits()));
        notesSubject.onNext(entity.getNotes());
        originalValuesSubject.onNext(entity);
        initializedSubject.onComplete();
    }

    public void saveViewModelState(@NonNull Bundle outState) {
        outState.putBoolean(STATE_KEY_STATE_INITIALIZED, true);
        originalValuesSubject.getValue().saveState(outState, true);
        selectedTermSubject.getValue().ifPresent(t -> t.saveState(outState, false));
        selectedMentorSubject.getValue().ifPresent(m -> m.saveState(outState, false));
        outState.putString(IdIndexedEntity.stateKey(AppDb.TABLE_NAME_COURSES, Course.COLNAME_NUMBER, false), numberSubject.getValue());
        outState.putString(IdIndexedEntity.stateKey(AppDb.TABLE_NAME_COURSES, Course.COLNAME_STATUS, false), statusSubject.getValue().name());
        outState.putString(IdIndexedEntity.stateKey(AppDb.TABLE_NAME_COURSES, Course.COLNAME_TITLE, false), titleSubject.getValue());
        outState.putString(IdIndexedEntity.stateKey(AppDb.TABLE_NAME_COURSES, Course.COLNAME_NOTES, false), notesSubject.getValue());
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
        outState.putString(STATE_KEY_COMPETENCY_UNITS_TEXT, competencyUnitsTextSubject.getValue());
    }

    public synchronized Single<ResourceMessageResult> save(boolean ignoreWarnings) {
        AbstractTermEntity<?> selectedTerm = selectedTermSubject.getValue().orElse(null);
        if (null == selectedTerm) {
            return Single.just(ValidationMessage.ofSingleError(R.string.message_term_not_selected)).observeOn(AndroidSchedulers.mainThread());
        }
        Integer cu = competencyUnitsValueLiveData.getLiveData().getValue().orElse(null);
        if (null == cu) {
            return Single.just(ValidationMessage.ofSingleError(R.string.message_required)).observeOn(AndroidSchedulers.mainThread());
        }
        CourseDetails originalValues = originalValuesSubject.getValue();
        CourseEntity entity = originalValues.toEntity();
        entity.setTermId(Objects.requireNonNull(selectedTerm).getId());
        entity.setMentorId(selectedMentorSubject.getValue().map(AbstractEntity::getId).orElse(null));
        entity.setNumber(numberSubject.getValue());
        entity.setTitle(titleSubject.getValue());
        entity.setStatus(statusSubject.getValue());
        entity.setExpectedStart(expectedStartSubject.getValue().orElse(null));
        entity.setExpectedEnd(expectedEndSubject.getValue().orElse(null));
        entity.setActualStart(actualStartSubject.getValue().orElse(null));
        entity.setActualEnd(actualEndSubject.getValue().orElse(null));
        entity.setCompetencyUnits(cu);
        entity.setNotes(notesSubject.getValue());
        Log.d(LOG_TAG, String.format("Saving %s to database", entity));
        return dbLoader.saveCourse(entity, ignoreWarnings).doOnSuccess(m -> {
            if (m.isSucceeded()) {
                originalValuesSubject.onNext(new CourseDetails(entity, selectedTerm, selectedMentorSubject.getValue().orElse(null)));
            }
        });
    }

    public Single<ResourceMessageResult> delete(boolean ignoreWarnings) {
        Log.d(LOG_TAG, "Enter delete(" + ignoreWarnings + ")");
        return dbLoader.deleteCourse(Objects.requireNonNull(originalValuesSubject.getValue()).toEntity(), ignoreWarnings);
    }

    public LiveData<List<CourseAlert>> getAllCourseAlerts() {
        // TODO: Implement this
        throw new UnsupportedOperationException();
    }

    public LiveData<List<AlertListItem>> getAllAlerts() {
        // TODO: Implement this
        throw new UnsupportedOperationException();
    }
}