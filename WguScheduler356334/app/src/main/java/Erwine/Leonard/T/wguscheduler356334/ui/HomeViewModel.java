package Erwine.Leonard.T.wguscheduler356334.ui;

import android.app.Application;
import android.content.res.Resources;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.text.HtmlCompat;
import androidx.lifecycle.LiveData;

import java.time.LocalDate;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

import Erwine.Leonard.T.wguscheduler356334.MainActivity;
import Erwine.Leonard.T.wguscheduler356334.R;
import Erwine.Leonard.T.wguscheduler356334.db.DbLoader;
import Erwine.Leonard.T.wguscheduler356334.entity.course.CourseDetails;
import Erwine.Leonard.T.wguscheduler356334.entity.mentor.AbstractMentorEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.term.AbstractTermEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.term.TermListItem;
import Erwine.Leonard.T.wguscheduler356334.util.BehaviorComputationSource;
import Erwine.Leonard.T.wguscheduler356334.util.ObserverHelper;
import Erwine.Leonard.T.wguscheduler356334.util.SubscribingLiveDataWrapper;
import Erwine.Leonard.T.wguscheduler356334.util.WguSchedulerViewModel;
import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;

import static Erwine.Leonard.T.wguscheduler356334.db.LocalDateConverter.FULL_FORMATTER;

public class HomeViewModel extends WguSchedulerViewModel {

    private static final String LOG_TAG = MainActivity.getLogTag(HomeViewModel.class);
    @SuppressWarnings("FieldCanBeLocal")
    private final DbLoader dbLoader;
    private final CompositeDisposable compositeDisposable;
    private final BehaviorComputationSource<Optional<CourseDetails>> currentCourse;
    private final BehaviorComputationSource<Optional<? extends AbstractTermEntity<?>>> currentTermByDate;
    private final BehaviorComputationSource<Optional<CourseDetails>> nextCourse;
    private final BehaviorComputationSource<Optional<? extends AbstractTermEntity<?>>> nextTermByDate;

    private final SubscribingLiveDataWrapper<Integer> currentHeadingResourceObserver;
    private final SubscribingLiveDataWrapper<Boolean> showCurrentObserver;
    private final SubscribingLiveDataWrapper<CharSequence> currentTermNameObserver;
    private final SubscribingLiveDataWrapper<Integer> currentTermStartLabelObserver;
    private final SubscribingLiveDataWrapper<Function<Resources, CharSequence>> currentTermStartDateFactoryObserver;
    private final SubscribingLiveDataWrapper<Function<Resources, CharSequence>> currentTermEndDateFactoryObserver;
    private final SubscribingLiveDataWrapper<Boolean> showCurrentCourseObserver;
    private final SubscribingLiveDataWrapper<Function<Resources, CharSequence>> currentCourseNameFactoryObserver;
    private final SubscribingLiveDataWrapper<Boolean> showCurrentCourseDatesObserver;
    private final SubscribingLiveDataWrapper<Integer> currentCourseStartLabelObserver;
    private final SubscribingLiveDataWrapper<Function<Resources, CharSequence>> currentCourseStartDateFactoryObserver;
    private final SubscribingLiveDataWrapper<Function<Resources, CharSequence>> currentCourseEndDateFactoryObserver;
    private final SubscribingLiveDataWrapper<Boolean> showCurrentMentorObserver;
    private final SubscribingLiveDataWrapper<CharSequence> currentMentorNameObserver;
    private final SubscribingLiveDataWrapper<Function<Resources, CharSequence>> currentMentorPhoneNumberFactoryObserver;
    private final SubscribingLiveDataWrapper<Function<Resources, CharSequence>> currentMentorEmailAddressFactoryObserver;
    private final SubscribingLiveDataWrapper<Boolean> showNextObserver;
    private final SubscribingLiveDataWrapper<Boolean> showNextTermObserver;
    private final SubscribingLiveDataWrapper<Integer> nextTermLabelObserver;
    private final SubscribingLiveDataWrapper<CharSequence> nextTermNameTextObserver;
    private final SubscribingLiveDataWrapper<Integer> nextTermStartLabelObserver;
    private final SubscribingLiveDataWrapper<Function<Resources, CharSequence>> nextTermStartDateFactoryObserver;
    private final SubscribingLiveDataWrapper<Function<Resources, CharSequence>> nextTermEndDateFactoryObserver;
    private final SubscribingLiveDataWrapper<Boolean> showNextCourseHeadingObserver;
    private final SubscribingLiveDataWrapper<Boolean> showNextCourseObserver;
    private final SubscribingLiveDataWrapper<Function<Resources, CharSequence>> nextCourseNameFactoryObserver;
    private final SubscribingLiveDataWrapper<Boolean> showNextCourseDatesObserver;
    private final SubscribingLiveDataWrapper<Integer> nextCourseStartLabelObserver;
    private final SubscribingLiveDataWrapper<Function<Resources, CharSequence>> nextCourseStartDateFactoryObserver;
    private final SubscribingLiveDataWrapper<Function<Resources, CharSequence>> nextCourseEndDateFactoryObserver;
    private final SubscribingLiveDataWrapper<Boolean> showNextMentorObserver;
    private final SubscribingLiveDataWrapper<CharSequence> nextMentorNameObserver;
    private final SubscribingLiveDataWrapper<Function<Resources, CharSequence>> nextMentorPhoneNumberFactoryObserver;
    private final SubscribingLiveDataWrapper<Function<Resources, CharSequence>> nextMentorEmailAddressFactoryObserver;

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    @NonNull
    private static Optional<? extends AbstractTermEntity<?>> calculateCurrentTerm(Optional<CourseDetails> currentCourse, Optional<? extends AbstractTermEntity<?>> termByDate) {
        return currentCourse.<Optional<? extends AbstractTermEntity<?>>>map(c -> Optional.of(Objects.requireNonNull(c.getTerm())))
                .orElse(termByDate);
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    @NonNull
    private static Optional<? extends AbstractTermEntity<?>> calculateNextTerm(Optional<CourseDetails> currentCourse, Optional<CourseDetails> nextCourse,
                                                                               Optional<? extends AbstractTermEntity<?>> currentTermByDate,
                                                                               Optional<? extends AbstractTermEntity<?>> nextTermByDate) {
        if (nextCourse.isPresent()) {
            return nextCourse.flatMap(n -> (currentCourse.map(c -> c.getTermId() == n.getTermId()).orElse(true)) ? Optional.empty() : Optional.of(Objects.requireNonNull(n.getTerm())));
        }

        if (currentCourse.isPresent()) {
            Optional<? extends AbstractTermEntity<?>> nextTerm = currentTermByDate.filter(t -> currentCourse.get().getTermId() != t.getId());
            if (nextTerm.isPresent()) {
                return nextTerm;
            }
        }
        return nextTermByDate;
    }

    @NonNull
    private static Optional<Integer> calculateCurrentHeadingResource(Boolean showCurrent, Boolean showNext) {
        if (Boolean.TRUE.equals(showCurrent)) {
            return Optional.of(R.string.title_current_term);
        }
        return (Boolean.TRUE.equals(showNext)) ? Optional.empty() : Optional.of(R.string.html_no_upcoming_terms);
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    @NonNull
    private static Function<Resources, CharSequence> calculateDateTextFactory(Optional<LocalDate> date) {
        return date.<Function<Resources, CharSequence>>map(d -> r -> FULL_FORMATTER.format(d)).orElseGet(() -> r -> HtmlCompat.fromHtml(r.getString(R.string.html_none), HtmlCompat.FROM_HTML_SEPARATOR_LINE_BREAK_DIV));
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private static Function<Resources, CharSequence> calculateCourseNameFactory(Optional<CourseDetails> course) {
        return course.<Function<Resources, CharSequence>>map(c -> r -> r.getString(R.string.format_value_colon_value, c.getNumber(), c.getTitle()))
                .orElseGet(() -> r -> "");
    }

    private static Optional<LocalDate> calculateCourseStart(CourseDetails course) {
        LocalDate d = course.getActualStart();
        return (null == d) ? Optional.ofNullable(course.getExpectedStart()) : Optional.of(d);
    }

    private static Optional<LocalDate> calculateCourseEnd(CourseDetails course) {
        LocalDate d = course.getActualEnd();
        return (null == d) ? Optional.ofNullable(course.getExpectedEnd()) : Optional.of(d);
    }

    private void calculateCurrentAndNextTerms(List<TermListItem> courses) {
        Log.d(LOG_TAG, "Enter calculateCurrentAndNextTerms");
        Iterator<TermListItem> iterator = courses.iterator();
        if (iterator.hasNext()) {
            TermListItem term = iterator.next();
            Log.d(LOG_TAG, "calculateCurrentAndNextTerms: currentTermByDate.onNext(Optional.of(" + term + "))");
            currentTermByDate.onNext(Optional.of(term));
            if (iterator.hasNext()) {
                term = iterator.next();
                Log.d(LOG_TAG, "calculateCurrentAndNextTerms: nextTermByDate.onNext(Optional.of(" + term + "))");
                nextTermByDate.onNext(Optional.of(term));
            } else {
                Log.d(LOG_TAG, "calculateCurrentAndNextTerms: nextTermByDate.onNext(Optional.empty())");
                nextTermByDate.onNext(Optional.empty());
            }
        } else {
            Log.d(LOG_TAG, "calculateCurrentAndNextTerms: currentTermByDate.onNext(Optional.empty())");
            currentTermByDate.onNext(Optional.empty());
            Log.d(LOG_TAG, "calculateCurrentAndNextTerms: nextTermByDate.onNext(Optional.empty())");
            nextTermByDate.onNext(Optional.empty());
        }
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    @NonNull
    private static Integer calculateStartDateLabel(Optional<LocalDate> o) {
        return (o.map(d -> d.compareTo(LocalDate.now()) > 0).orElse(false)) ? R.string.label_starts_on : R.string.label_started_on;
    }

    private void calculateCurrentAndNextCourses(List<CourseDetails> courses) {
        Log.d(LOG_TAG, "Enter calculateCurrentAndNextCourses");
        Iterator<CourseDetails> iterator = courses.iterator();
        if (iterator.hasNext()) {
            CourseDetails course = iterator.next();
            Log.d(LOG_TAG, "calculateCurrentAndNextCourses: currentCourse.onNext(Optional.of(" + course + "))");
            currentCourse.onNext(Optional.of(course));
            if (iterator.hasNext()) {
                course = iterator.next();
                Log.d(LOG_TAG, "calculateCurrentAndNextCourses: nextCourse.onNext(Optional.of(" + course + "))");
                nextCourse.onNext(Optional.of(course));
            } else {
                Log.d(LOG_TAG, "calculateCurrentAndNextCourses: nextCourse.onNext(Optional.empty())");
                nextCourse.onNext(Optional.empty());
            }
        } else {
            Log.d(LOG_TAG, "calculateCurrentAndNextCourses: currentCourse.onNext(Optional.empty())");
            currentCourse.onNext(Optional.empty());
            Log.d(LOG_TAG, "calculateCurrentAndNextCourses: nextCourse.onNext(Optional.empty())");
            nextCourse.onNext(Optional.empty());
        }
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    @NonNull
    private static Function<Resources, CharSequence> calculateMentorPhoneNumberFactory(Optional<AbstractMentorEntity<?>> mentor) {
        return mentor.map(AbstractMentorEntity::getPhoneNumber).filter(p -> !p.isEmpty()).<Function<Resources, CharSequence>>map(p -> r -> p)
                .orElseGet(() -> r -> HtmlCompat.fromHtml(r.getString(R.string.html_none), HtmlCompat.FROM_HTML_SEPARATOR_LINE_BREAK_DIV));
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    @NonNull
    private static Function<Resources, CharSequence> calculateMentorEmailAddressFactory(Optional<AbstractMentorEntity<?>> mentor) {
        return mentor.map(AbstractMentorEntity::getEmailAddress).filter(p -> !p.isEmpty()).<Function<Resources, CharSequence>>map(p -> r -> p)
                .orElseGet(() -> r -> HtmlCompat.fromHtml(r.getString(R.string.html_none), HtmlCompat.FROM_HTML_SEPARATOR_LINE_BREAK_DIV));
    }

    public HomeViewModel(@NonNull Application application) {
        super(application);

        currentCourse = BehaviorComputationSource.createDefault(Optional.empty());
        currentTermByDate = BehaviorComputationSource.createDefault(Optional.empty());
        nextCourse = BehaviorComputationSource.createDefault(Optional.empty());
        nextTermByDate = BehaviorComputationSource.createDefault(Optional.empty());

        dbLoader = DbLoader.getInstance(application.getApplicationContext());
        ObserverHelper.observe(dbLoader.getInProgressAndPlannedCourses(), this, this::calculateCurrentAndNextCourses);
        ObserverHelper.observe(dbLoader.getTermsOnOrAfter(LocalDate.now()), this, this::calculateCurrentAndNextTerms);

        Observable<Optional<? extends AbstractTermEntity<?>>> currentTermObservable = Observable.combineLatest(currentCourse.getObservable(), currentTermByDate, HomeViewModel::calculateCurrentTerm);
        Observable<Boolean> showCurrentObservable = currentTermObservable.map(Optional::isPresent);
        showCurrentObserver = SubscribingLiveDataWrapper.of(showCurrentObservable);
        Observable<Boolean> showCurrentCourseObservable = currentCourse.getObservable().map(Optional::isPresent);
        showCurrentCourseObserver = SubscribingLiveDataWrapper.of(showCurrentCourseObservable);
        currentTermNameObserver = SubscribingLiveDataWrapper.of(currentTermObservable.map(c -> c.map(AbstractTermEntity::getName).orElse("")));
        Observable<Optional<? extends AbstractTermEntity<?>>> nextTermObservable = Observable.combineLatest(currentCourse.getObservable(), nextCourse.getObservable(), currentTermByDate.getObservable(), nextTermByDate.getObservable(), HomeViewModel::calculateNextTerm);
        Observable<Boolean> showNextTermObservable = nextTermObservable.map(Optional::isPresent);
        showNextTermObserver = SubscribingLiveDataWrapper.of(showNextTermObservable);
        Observable<Boolean> showNextCourseObservable = nextCourse.getObservable().map(Optional::isPresent);
        showNextCourseObserver = SubscribingLiveDataWrapper.of(showNextCourseObservable);
        Observable<Boolean> showNextObservable = Observable.combineLatest(showNextTermObservable, showNextCourseObservable, (t, c) -> t || c);
        showNextObserver = SubscribingLiveDataWrapper.of(showNextObservable);
        currentHeadingResourceObserver = SubscribingLiveDataWrapper.ofOptional(Observable.combineLatest(showCurrentObservable, showNextObservable, HomeViewModel::calculateCurrentHeadingResource));
        Observable<Optional<LocalDate>> currentTermStartDateObservable = currentTermObservable.map(o -> o.flatMap(t -> Optional.ofNullable(t.getStart())));
        currentTermStartLabelObserver = SubscribingLiveDataWrapper.of(currentTermStartDateObservable.map(HomeViewModel::calculateStartDateLabel));
        Observable<Optional<LocalDate>> currentTermEndDateObservable = currentTermObservable.map(o -> o.flatMap(t -> Optional.ofNullable(t.getEnd())));
        currentTermStartDateFactoryObserver = SubscribingLiveDataWrapper.of(currentTermStartDateObservable.map(HomeViewModel::calculateDateTextFactory));
        currentTermEndDateFactoryObserver = SubscribingLiveDataWrapper.of(currentTermEndDateObservable.map(HomeViewModel::calculateDateTextFactory));
        currentCourseNameFactoryObserver = SubscribingLiveDataWrapper.of(currentCourse.getObservable().map(HomeViewModel::calculateCourseNameFactory));
        Observable<Optional<LocalDate>> currentCourseStartDateObservable = currentCourse.getObservable().map(o -> o.flatMap(HomeViewModel::calculateCourseStart));
        currentCourseStartLabelObserver = SubscribingLiveDataWrapper.of(currentCourseStartDateObservable.map(HomeViewModel::calculateStartDateLabel));
        currentCourseStartDateFactoryObserver = SubscribingLiveDataWrapper.of(currentCourseStartDateObservable.map(HomeViewModel::calculateDateTextFactory));
        Observable<Optional<LocalDate>> currentCourseEndDateObservable = currentCourse.getObservable().map(o -> o.flatMap(HomeViewModel::calculateCourseEnd));
        currentCourseEndDateFactoryObserver = SubscribingLiveDataWrapper.of(currentCourseEndDateObservable.map(HomeViewModel::calculateDateTextFactory));
        showCurrentCourseDatesObserver = SubscribingLiveDataWrapper.of(Observable.combineLatest(currentCourseStartDateObservable.map(Optional::isPresent), currentCourseEndDateObservable.map(Optional::isPresent), (s, e) -> s || e));
        Observable<Optional<AbstractMentorEntity<?>>> currentMentorObservable = currentCourse.getObservable().map(o -> o.flatMap(c -> Optional.ofNullable(c.getMentor())));
        showCurrentMentorObserver = SubscribingLiveDataWrapper.of(currentMentorObservable.map(Optional::isPresent));
        currentMentorNameObserver = SubscribingLiveDataWrapper.of(currentMentorObservable.map(o -> o.map(AbstractMentorEntity::getName).orElse("")));
        currentMentorPhoneNumberFactoryObserver = SubscribingLiveDataWrapper.of(currentMentorObservable.map(HomeViewModel::calculateMentorPhoneNumberFactory));
        currentMentorEmailAddressFactoryObserver = SubscribingLiveDataWrapper.of(currentMentorObservable.map(HomeViewModel::calculateMentorEmailAddressFactory));
        nextTermLabelObserver = SubscribingLiveDataWrapper.of(showNextTermObservable.map(n -> (n) ? R.string.title_next_term : R.string.title_next_course));
        showNextCourseHeadingObserver = SubscribingLiveDataWrapper.of(Observable.combineLatest(showNextTermObservable, showNextCourseObservable, (t, c) -> t && c));
        nextTermNameTextObserver = SubscribingLiveDataWrapper.of(nextTermObservable.map(c -> c.map(AbstractTermEntity::getName).orElse("")));
        Observable<Optional<LocalDate>> nextTermStartDateObservable = nextTermObservable.map(o -> o.flatMap(t -> Optional.ofNullable(t.getStart())));
        nextTermStartLabelObserver = SubscribingLiveDataWrapper.of(nextTermStartDateObservable.map(HomeViewModel::calculateStartDateLabel));
        Observable<Optional<LocalDate>> nextTermEndDateObservable = nextTermObservable.map(o -> o.flatMap(t -> Optional.ofNullable(t.getEnd())));
        nextTermStartDateFactoryObserver = SubscribingLiveDataWrapper.of(nextTermStartDateObservable.map(HomeViewModel::calculateDateTextFactory));
        nextTermEndDateFactoryObserver = SubscribingLiveDataWrapper.of(nextTermEndDateObservable.map(HomeViewModel::calculateDateTextFactory));
        nextCourseNameFactoryObserver = SubscribingLiveDataWrapper.of(nextCourse.getObservable().map(HomeViewModel::calculateCourseNameFactory));
        Observable<Optional<LocalDate>> nextCourseStartDateObservable = nextCourse.getObservable().map(o -> o.flatMap(HomeViewModel::calculateCourseStart));
        nextCourseStartLabelObserver = SubscribingLiveDataWrapper.of(nextCourseStartDateObservable.map(HomeViewModel::calculateStartDateLabel));
        nextCourseStartDateFactoryObserver = SubscribingLiveDataWrapper.of(nextCourseStartDateObservable.map(HomeViewModel::calculateDateTextFactory));
        Observable<Optional<LocalDate>> nextCourseEndDateObservable = nextCourse.getObservable().map(o -> o.flatMap(HomeViewModel::calculateCourseEnd));
        nextCourseEndDateFactoryObserver = SubscribingLiveDataWrapper.of(nextCourseEndDateObservable.map(HomeViewModel::calculateDateTextFactory));
        showNextCourseDatesObserver = SubscribingLiveDataWrapper.of(Observable.combineLatest(nextCourseStartDateObservable.map(Optional::isPresent), nextCourseEndDateObservable.map(Optional::isPresent), (s, e) -> s || e));
        Observable<Optional<AbstractMentorEntity<?>>> nextMentorObservable = nextCourse.getObservable().map(o -> o.flatMap(c -> Optional.ofNullable(c.getMentor())));
        showNextMentorObserver = SubscribingLiveDataWrapper.of(nextMentorObservable.map(Optional::isPresent));
        nextMentorNameObserver = SubscribingLiveDataWrapper.of(nextMentorObservable.map(o -> o.map(AbstractMentorEntity::getName).orElse("")));
        nextMentorPhoneNumberFactoryObserver = SubscribingLiveDataWrapper.of(nextMentorObservable.map(HomeViewModel::calculateMentorPhoneNumberFactory));
        nextMentorEmailAddressFactoryObserver = SubscribingLiveDataWrapper.of(nextMentorObservable.map(HomeViewModel::calculateMentorEmailAddressFactory));
        compositeDisposable = new CompositeDisposable(currentHeadingResourceObserver, showCurrentObserver, currentTermNameObserver, currentTermStartLabelObserver,
                currentTermStartDateFactoryObserver, currentTermEndDateFactoryObserver, showCurrentCourseObserver, currentCourseNameFactoryObserver,
                showCurrentCourseDatesObserver, currentCourseStartLabelObserver, currentCourseStartDateFactoryObserver, currentCourseEndDateFactoryObserver,
                showCurrentMentorObserver, currentMentorNameObserver, currentMentorPhoneNumberFactoryObserver, currentMentorEmailAddressFactoryObserver,
                showNextObserver, showNextTermObserver, nextTermLabelObserver, nextTermNameTextObserver, nextTermStartLabelObserver, nextTermStartDateFactoryObserver,
                nextTermEndDateFactoryObserver, showNextCourseObserver, showNextCourseHeadingObserver, nextCourseNameFactoryObserver, showNextCourseDatesObserver, nextCourseStartLabelObserver,
                nextCourseStartDateFactoryObserver, nextCourseEndDateFactoryObserver, showNextMentorObserver, nextMentorNameObserver, nextMentorPhoneNumberFactoryObserver,
                nextMentorEmailAddressFactoryObserver);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.dispose();
    }

    @Nullable
    public CourseDetails getCurrentCourse() {
        return Objects.requireNonNull(currentCourse.getValue()).orElse(null);
    }

    @Nullable
    public AbstractTermEntity<?> getCurrentTerm() {
        return calculateCurrentTerm(Objects.requireNonNull(currentCourse.getValue()), currentTermByDate.getValue()).orElse(null);
    }

    @Nullable
    public CourseDetails getNextCourse() {
        return Objects.requireNonNull(nextCourse.getValue()).orElse(null);
    }

    @Nullable
    public AbstractTermEntity<?> getNextTerm() {
        return calculateNextTerm(currentCourse.getValue(), Objects.requireNonNull(nextCourse.getValue()), currentTermByDate.getValue(), nextTermByDate.getValue()).orElse(null);
    }

    public LiveData<Boolean> getShowCurrentLiveData() {
        return showCurrentObserver.getLiveData();
    }

    public LiveData<Boolean> getShowNextLiveData() {
        return showNextObserver.getLiveData();
    }

    public LiveData<Boolean> getShowCurrentCourseLiveData() {
        return showCurrentCourseObserver.getLiveData();
    }

    public LiveData<Integer> getCurrentHeadingResourceLiveData() {
        return currentHeadingResourceObserver.getLiveData();
    }

    public LiveData<CharSequence> getCurrentTermNameLiveData() {
        return currentTermNameObserver.getLiveData();
    }

    public LiveData<CharSequence> getCurrentMentorNameLiveData() {
        return currentMentorNameObserver.getLiveData();
    }

    public LiveData<Integer> getCurrentTermStartLabelLiveData() {
        return currentTermStartLabelObserver.getLiveData();
    }

    public LiveData<Function<Resources, CharSequence>> getCurrentTermStartDateFactoryLiveData() {
        return currentTermStartDateFactoryObserver.getLiveData();
    }

    public LiveData<Function<Resources, CharSequence>> getCurrentTermEndDateFactoryLiveData() {
        return currentTermEndDateFactoryObserver.getLiveData();
    }

    public LiveData<Function<Resources, CharSequence>> getCurrentCourseNameFactoryLiveData() {
        return currentCourseNameFactoryObserver.getLiveData();
    }

    public LiveData<Boolean> getShowCurrentCourseDatesLiveData() {
        return showCurrentCourseDatesObserver.getLiveData();
    }

    public LiveData<Integer> getCurrentCourseStartLabelLiveData() {
        return currentCourseStartLabelObserver.getLiveData();
    }

    public LiveData<Function<Resources, CharSequence>> getCurrentCourseStartDateFactoryLiveData() {
        return currentCourseStartDateFactoryObserver.getLiveData();
    }

    public LiveData<Function<Resources, CharSequence>> getCurrentCourseEndDateFactoryLiveData() {
        return currentCourseEndDateFactoryObserver.getLiveData();
    }

    public LiveData<Boolean> getShowCurrentMentorLiveData() {
        return showCurrentMentorObserver.getLiveData();
    }

    public LiveData<Function<Resources, CharSequence>> getCurrentMentorPhoneNumberFactoryLiveData() {
        return currentMentorPhoneNumberFactoryObserver.getLiveData();
    }

    public LiveData<Function<Resources, CharSequence>> getCurrentMentorEmailAddressFactoryLiveData() {
        return currentMentorEmailAddressFactoryObserver.getLiveData();
    }

    public LiveData<Boolean> getShowNextTermLiveData() {
        return showNextTermObserver.getLiveData();
    }

    public LiveData<Integer> getNextTermLabelLiveData() {
        return nextTermLabelObserver.getLiveData();
    }

    public LiveData<CharSequence> getNextTermNameTextLiveData() {
        return nextTermNameTextObserver.getLiveData();
    }

    public LiveData<Integer> getNextTermStartLabelLiveData() {
        return nextTermStartLabelObserver.getLiveData();
    }

    public LiveData<Function<Resources, CharSequence>> getNextTermStartDateFactoryLiveData() {
        return nextTermStartDateFactoryObserver.getLiveData();
    }

    public LiveData<Function<Resources, CharSequence>> getNextTermEndDateFactoryLiveData() {
        return nextTermEndDateFactoryObserver.getLiveData();
    }

    public LiveData<Boolean> getShowNextCourseHeadingLiveData() {
        return showNextCourseHeadingObserver.getLiveData();
    }

    public LiveData<Boolean> getShowNextCourseLiveData() {
        return showNextCourseObserver.getLiveData();
    }

    public LiveData<Function<Resources, CharSequence>> getNextCourseNameFactoryLiveData() {
        return nextCourseNameFactoryObserver.getLiveData();
    }

    public LiveData<Boolean> getShowNextCourseDatesLiveData() {
        return showNextCourseDatesObserver.getLiveData();
    }

    public LiveData<Integer> getNextCourseStartLabelLiveData() {
        return nextCourseStartLabelObserver.getLiveData();
    }

    public LiveData<Function<Resources, CharSequence>> getNextCourseStartDateFactoryLiveData() {
        return nextCourseStartDateFactoryObserver.getLiveData();
    }

    public LiveData<Function<Resources, CharSequence>> getNextCourseEndDateFactoryLiveData() {
        return nextCourseEndDateFactoryObserver.getLiveData();
    }

    public LiveData<Boolean> getShowNextMentorLiveData() {
        return showNextMentorObserver.getLiveData();
    }

    public LiveData<CharSequence> getNextMentorNameLiveData() {
        return nextMentorNameObserver.getLiveData();
    }

    public LiveData<Function<Resources, CharSequence>> getNextMentorPhoneNumberFactoryLiveData() {
        return nextMentorPhoneNumberFactoryObserver.getLiveData();
    }

    public LiveData<Function<Resources, CharSequence>> getNextMentorEmailAddressFactoryLiveData() {
        return nextMentorEmailAddressFactoryObserver.getLiveData();
    }

}