package Erwine.Leonard.T.wguscheduler356334.db;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.preference.PreferenceManager;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import Erwine.Leonard.T.wguscheduler356334.MainActivity;
import Erwine.Leonard.T.wguscheduler356334.R;
import Erwine.Leonard.T.wguscheduler356334.TimePreference;
import Erwine.Leonard.T.wguscheduler356334.entity.AbstractEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.alert.AlertEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.alert.AlertLink;
import Erwine.Leonard.T.wguscheduler356334.entity.alert.AlertListItem;
import Erwine.Leonard.T.wguscheduler356334.entity.assessment.Assessment;
import Erwine.Leonard.T.wguscheduler356334.entity.assessment.AssessmentAlert;
import Erwine.Leonard.T.wguscheduler356334.entity.assessment.AssessmentAlertDetails;
import Erwine.Leonard.T.wguscheduler356334.entity.assessment.AssessmentAlertLink;
import Erwine.Leonard.T.wguscheduler356334.entity.assessment.AssessmentDetails;
import Erwine.Leonard.T.wguscheduler356334.entity.assessment.AssessmentEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.course.Course;
import Erwine.Leonard.T.wguscheduler356334.entity.course.CourseAlert;
import Erwine.Leonard.T.wguscheduler356334.entity.course.CourseAlertDetails;
import Erwine.Leonard.T.wguscheduler356334.entity.course.CourseAlertLink;
import Erwine.Leonard.T.wguscheduler356334.entity.course.CourseDetails;
import Erwine.Leonard.T.wguscheduler356334.entity.course.CourseEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.course.CourseStatus;
import Erwine.Leonard.T.wguscheduler356334.entity.course.MentorCourseListItem;
import Erwine.Leonard.T.wguscheduler356334.entity.course.TermCourseListItem;
import Erwine.Leonard.T.wguscheduler356334.entity.mentor.Mentor;
import Erwine.Leonard.T.wguscheduler356334.entity.mentor.MentorEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.mentor.MentorListItem;
import Erwine.Leonard.T.wguscheduler356334.entity.term.Term;
import Erwine.Leonard.T.wguscheduler356334.entity.term.TermEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.term.TermListItem;
import Erwine.Leonard.T.wguscheduler356334.util.ComparisonHelper;
import Erwine.Leonard.T.wguscheduler356334.util.ToStringBuilder;
import Erwine.Leonard.T.wguscheduler356334.util.validation.ResourceMessageBuilder;
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

import static Erwine.Leonard.T.wguscheduler356334.entity.IdIndexedEntity.ID_NEW;

/**
 * A singleton helper object for database I/O.
 */
public class DbLoader {

    private static final String LOG_TAG = MainActivity.getLogTag(DbLoader.class);

    private final BehaviorSubject<Boolean> preferEmailSubject;
    private final MutableLiveData<Boolean> preferEmail;
    private final BehaviorSubject<LocalTime> preferAlertTimeSubject;
    private final MutableLiveData<LocalTime> preferAlertTime;
    private final BehaviorSubject<Integer> preferNextNotificationIdSubject;
    private final MutableLiveData<Integer> preferNextNotificationId;
    private static DbLoader instance;
    @SuppressWarnings("FieldCanBeLocal")
    private final CompositeDisposable subscriptionCompositeDisposable;
    private final AppDb appDb;
    private final Scheduler scheduler;
    @SuppressWarnings("FieldCanBeLocal")
    private final Executor dataExecutor;
    private LiveData<List<TermListItem>> allTerms;
    private LiveData<List<MentorListItem>> allMentors;

    /**
     * Gets the singleton {@code DbLoader} instance.
     *
     * @param context The {@link Context} to use for creating the underling {@link AppDb} instance if it was not yet already created.
     * @return The singleton {@code DbLoader} instance.
     */
    public static DbLoader getInstance(Context context) {
        if (null == instance) {
            instance = new DbLoader(context);
        }

        return instance;
    }

    private DbLoader(Context context) {
        this(context, AppDb.getInstance(context));
    }

    protected DbLoader(Context context, AppDb appDb) {
        subscriptionCompositeDisposable = new CompositeDisposable();
        this.appDb = appDb;
        dataExecutor = Executors.newSingleThreadExecutor();
        scheduler = Schedulers.from(dataExecutor);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        final String preference_prefer_email = context.getResources().getString(R.string.preference_prefer_email);
        preferEmailSubject = BehaviorSubject.createDefault(sharedPreferences.getBoolean(preference_prefer_email, false));
        final String preference_alert_time = context.getResources().getString(R.string.preference_alert_time);
        int minutes = sharedPreferences.getInt(preference_alert_time, TimePreference.DEFAULT_VALUE);
        preferAlertTimeSubject = BehaviorSubject.createDefault(LocalTime.of(minutes / 60, minutes % 60));
        final String preference_next_notification_id = context.getResources().getString(R.string.preference_next_notification_id);
        preferNextNotificationIdSubject = BehaviorSubject.createDefault(sharedPreferences.getInt(preference_next_notification_id, 1));

        preferEmail = new MutableLiveData<>(preferEmailSubject.getValue());
        preferAlertTime = new MutableLiveData<>(preferAlertTimeSubject.getValue());
        preferNextNotificationId = new MutableLiveData<>(preferNextNotificationIdSubject.getValue());
        subscriptionCompositeDisposable.add(preferEmailSubject.subscribe(preferEmail::postValue));
        subscriptionCompositeDisposable.add(preferAlertTimeSubject.subscribe(preferAlertTime::postValue));
        subscriptionCompositeDisposable.add(preferNextNotificationIdSubject.subscribe(id -> {
            preferNextNotificationId.postValue(id);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            try {
                editor.putInt(preference_next_notification_id, id);
            } finally {
                editor.apply();
            }
        }));
        sharedPreferences.registerOnSharedPreferenceChangeListener((sp, key) -> {
            if (preference_alert_time.equals(key)) {
                int m = sp.getInt(key, TimePreference.DEFAULT_VALUE);
                preferAlertTimeSubject.onNext(LocalTime.of(m / 60, m % 60));
            } else if (preference_prefer_email.equals(key)) {
                preferEmailSubject.onNext(sp.getBoolean(key, false));
            }
        });
    }

    public static LiveData<Boolean> getPreferEmail() {
        return instance.preferEmail;
    }

    public static LiveData<LocalTime> getPreferAlertTime() {
        return instance.preferAlertTime;
    }

    AppDb getAppDb() {
        return appDb;
    }

    /**
     * Asynchronously gets a {@link TermEntity} from the {@link AppDb#TABLE_NAME_TERMS "terms"} data table within the underlying {@link AppDb} by its unique identifier.
     *
     * @param id The unique identifier of the  {@link TermEntity} to retrieve.
     * @return The {@link Single} object that can be used to observe the result {@link TermEntity} object.
     */
    @NonNull
    public Single<TermEntity> getTermById(long id) {
        Log.d(LOG_TAG, "Enter getTermById(" + id + ")");
        return appDb.termDAO().getById(id).subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread());
    }

    @NonNull
    public Single<TermEntity> getTermByIdForComputation(long id) {
        Log.d(LOG_TAG, "Enter getTermByIdForComputation(" + id + ")");
        return appDb.termDAO().getById(id).subscribeOn(scheduler).observeOn(Schedulers.computation());
    }

    /**
     * Gets all rows from the {@link AppDb#TABLE_NAME_TERMS "terms"} data table within the underlying {@link AppDb}.
     *
     * @return A {@link LiveData} object that will contain the list of {@link TermEntity} objects retrieved from the underlying {@link AppDb}.
     */
    @NonNull
    public LiveData<List<TermListItem>> getAllTerms() {
        Log.d(LOG_TAG, "Enter getAllTerms()");
        if (null == allTerms) {
            allTerms = appDb.termDAO().getAll();
        }
        return allTerms;
    }

    public LiveData<List<TermListItem>> getTermsOnOrAfter(LocalDate date) {
        return appDb.termDAO().getOnOrAfterDate(date);
    }

    @NonNull
    public Single<ResourceMessageResult> saveTerm(TermEntity entity, boolean ignoreWarnings) {
        Log.d(LOG_TAG, "Enter saveTerm(" + ToStringBuilder.toEscapedString(entity) + ", " + ignoreWarnings + ")");
        return Single.fromCallable(() -> {
            final ResourceMessageBuilder builder = new ResourceMessageBuilder();
            Term.validate(builder, entity);
            TermDAO dao = appDb.termDAO();
            List<TermListItem> list = dao.getAllSynchronous();
            String name = entity.getName();
            final long id = entity.getId();
            if (id == ID_NEW) {
                if (list.stream().anyMatch(t -> t.getName().equals(name))) {
                    builder.acceptError(R.string.message_term_duplicate_name);
                }
                if (builder.getLevel().map(l -> {
                    switch (l) {
                        case ERROR:
                            return true;
                        case WARNING:
                            return !ignoreWarnings;
                        default:
                            return false;
                    }
                }).orElse(false)) {
                    return builder.build();
                }
                entity.setId(dao.insertSynchronous(entity));
                return builder.build();
            }
            if (list.stream().anyMatch(t -> t.getName().equals(name) && id != t.getId())) {
                builder.acceptError(R.string.message_term_duplicate_name);
            }
            final LocalDate start = entity.getStart();
            final LocalDate end = entity.getEnd();
            if (null != start) {
                List<TermCourseListItem> courses = appDb.courseDAO().getByTermIdSynchronous(id);
                if (courses.stream().anyMatch(c -> ComparisonHelper.firstNonNull(c.getActualStart(), c.getExpectedStart()).filter(t -> t.compareTo(start) < 0).isPresent())) {
                    builder.acceptWarning(R.string.message_term_start_past_course_start);
                }
                if (null != end) {
                    if (courses.stream().anyMatch(c -> ComparisonHelper.firstNonNull(c.getActualEnd(), c.getExpectedEnd()).filter(t -> t.compareTo(end) < 0).isPresent())) {
                        builder.acceptWarning(R.string.message_term_end_before_course_end);
                    }
                }
            } else if (null != end) {
                if (appDb.courseDAO().getByTermIdSynchronous(id).stream().anyMatch(c -> ComparisonHelper.firstNonNull(c.getActualEnd(), c.getExpectedEnd()).filter(t -> t.compareTo(end) < 0).isPresent())) {
                    builder.acceptWarning(R.string.message_term_end_before_course_end);
                }
            }
            if (builder.getLevel().map(l -> {
                switch (l) {
                    case ERROR:
                        return true;
                    case WARNING:
                        return !ignoreWarnings;
                    default:
                        return false;
                }
            }).orElse(false)) {
                return builder.build();
            }
            dao.updateSynchronous(entity);
            return builder.build();
        }).subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * Asynchronously deletes a {@link TermEntity} from the {@link AppDb#TABLE_NAME_TERMS "terms"} data table of the underlying {@link AppDb}.
     *
     * @param entity The {@link TermEntity} to delete.
     * @return The {@link Completable} that can be observed for DB operation completion status.
     */
    @NonNull
    public Single<ResourceMessageResult> deleteTerm(TermEntity entity, boolean ignoreWarnings) {
        Log.d(LOG_TAG, "Enter deleteTerm(" + ToStringBuilder.toEscapedString(entity) + ", " + ignoreWarnings + ")");
        return Single.fromCallable(() -> {
            long id = entity.getId();
            if (ID_NEW == id) {
                return ValidationMessage.ofSingleWarning(R.string.message_item_never_saved);
            }
            if (!(ignoreWarnings || appDb.courseDAO().getCountByTermIdSynchronous(entity.getId()) == 0)) {
                return ValidationMessage.ofSingleWarning(R.string.message_term_has_courses);
            }
            if (appDb.termDAO().deleteSynchronous(entity) < 1) {
                return ValidationMessage.ofSingleError(R.string.message_delete_term_fail);
            }
            return ValidationMessage.success();
        }).subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * Gets all rows from the {@link AppDb#TABLE_NAME_MENTORS "mentors"} data table within the underlying {@link AppDb}.
     *
     * @return A {@link LiveData} object that will contain the list of {@link MentorEntity} objects retrieved from the underlying {@link AppDb}.
     */
    @NonNull
    public LiveData<List<MentorListItem>> getAllMentors() {
        Log.d(LOG_TAG, "Enter getAllMentors()");
        if (null == allMentors) {
            allMentors = appDb.mentorDAO().getAll();
        }
        return allMentors;
    }

    /**
     * Asynchronously saves the specified {@link MentorEntity} object into the {@link AppDb#TABLE_NAME_MENTORS "mentors"} data table of the underlying {@link AppDb}.
     * If {@link MentorEntity#getId()} is null, then it will be inserted into the {@link AppDb#TABLE_NAME_MENTORS "mentors"} data table; otherwise, the corresponding table row will be
     * updated. After a new {@link MentorEntity} has been successfully inserted, the value returned by {@link MentorEntity#getId()} will contain the unique identifier of the
     * newly added row.
     *
     * @param entity The {@link MentorEntity} to be saved.
     * @return The {@link Completable} that can be observed for DB operation completion status.
     */
    @NonNull
    public Single<ResourceMessageResult> saveMentor(MentorEntity entity, boolean ignoreWarnings) {
        Log.d(LOG_TAG, "Enter saveMentor(" + ToStringBuilder.toEscapedString(entity) + ", " + ignoreWarnings + ")");
        return Single.fromCallable(() -> {
            final ResourceMessageBuilder builder = new ResourceMessageBuilder();
            Mentor.validate(builder, entity);
            MentorDAO dao = appDb.mentorDAO();
            List<MentorListItem> list = dao.getAllSynchronous();
            String name = entity.getName();
            final long id = entity.getId();
            if (ID_NEW == id) {
                if (list.stream().anyMatch(t -> t.getName().equals(name))) {
                    builder.acceptError(R.string.message_mentor_duplicate_name);
                }
                if (builder.getLevel().map(l -> {
                    switch (l) {
                        case ERROR:
                            return true;
                        case WARNING:
                            return !ignoreWarnings;
                        default:
                            return false;
                    }
                }).orElse(false)) {
                    return builder.build();
                }
                entity.setId(dao.insertSynchronous(entity));
                return builder.build();
            }
            if (list.stream().anyMatch(t -> t.getName().equals(name) && id != t.getId())) {
                builder.acceptError(R.string.message_mentor_duplicate_name);
            }


            if (builder.getLevel().map(l -> {
                switch (l) {
                    case ERROR:
                        return true;
                    case WARNING:
                        return !ignoreWarnings;
                    default:
                        return false;
                }
            }).orElse(false)) {
                return builder.build();
            }
            dao.updateSynchronous(entity);
            return builder.build();
        }).subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * Asynchronously deletes a {@link MentorEntity} from the {@link AppDb#TABLE_NAME_MENTORS "mentors"} data table of the underlying {@link AppDb}.
     *
     * @param entity The {@link MentorEntity} to delete.
     * @return The {@link Completable} that can be observed for DB operation completion status.
     */
    @NonNull
    public Single<ResourceMessageResult> deleteMentor(MentorEntity entity, boolean ignoreWarnings) {
        Log.d(LOG_TAG, "Enter deleteMentor(" + ToStringBuilder.toEscapedString(entity) + ", " + ignoreWarnings + ")");
        return Single.fromCallable(() -> {
            long id = entity.getId();
            if (ID_NEW == id) {
                return ValidationMessage.ofSingleWarning(R.string.message_item_never_saved);
            }
            if (!(ignoreWarnings || appDb.courseDAO().getCountByMentorIdSynchronous(id) == 0)) {
                return ValidationMessage.ofSingleWarning(R.string.message_mentor_has_courses);
            }
            if (appDb.mentorDAO().deleteSynchronous(entity) < 0) {
                return ValidationMessage.ofSingleError(R.string.message_delete_mentor_fail);
            }
            return ValidationMessage.success();
        }).subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * Asynchronously gets a {@link MentorEntity} from the {@link AppDb#TABLE_NAME_MENTORS "mentors"} data table within the underlying {@link AppDb} by its unique identifier.
     *
     * @param id The unique identifier of the  {@link MentorEntity} to retrieve.
     * @return The {@link Single} object that can be used to observe the result {@link MentorEntity} object.
     */
    @NonNull
    public Single<MentorEntity> getMentorById(long id) {
        Log.d(LOG_TAG, "Enter getMentorById(" + id + ")");
        return appDb.mentorDAO().getById(id).subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread());
    }

    @NonNull
    public Single<MentorEntity> getMentorByIdForComputation(long id) {
        Log.d(LOG_TAG, "Enter getMentorByIdForComputation(" + id + ")");
        return appDb.mentorDAO().getById(id).subscribeOn(scheduler).observeOn(Schedulers.computation());
    }

    /**
     * Asynchronously deletes a {@link CourseEntity} from the {@link AppDb#TABLE_NAME_COURSES "courses"} data table of the underlying {@link AppDb}.
     *
     * @param entity The {@link CourseEntity} to delete.
     * @return The {@link Completable} that can be observed for DB operation completion status.
     */
    @NonNull
    public Single<ResourceMessageResult> deleteCourse(CourseEntity entity, boolean ignoreWarnings) {
        Log.d(LOG_TAG, "Enter deleteCourse(" + ToStringBuilder.toEscapedString(entity) + ", " + ignoreWarnings + ")");
        return Single.fromCallable(() -> {
            long id = entity.getId();
            if (ID_NEW == id) {
                return ValidationMessage.ofSingleWarning(R.string.message_item_never_saved);
            }
            if (!(ignoreWarnings || appDb.assessmentDAO().getCountByCourseIdSynchronous(id) > 0)) {
                return ValidationMessage.ofSingleWarning(R.string.message_course_has_assessments);
            }
            if (appDb.courseDAO().deleteSynchronous(entity) < 1) {
                return ValidationMessage.ofSingleError(R.string.message_delete_course_fail);
            }
            return ValidationMessage.success();
        }).subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * Asynchronously gets a {@link CourseDetails} from the {@link AppDb#TABLE_NAME_COURSES "courses"} data table within the underlying {@link AppDb} by its unique identifier.
     *
     * @param id The unique identifier of the  {@link CourseDetails} to retrieve.
     * @return The {@link Single} object that can be used to observe the result {@link CourseDetails} object.
     */
    @NonNull
    public Single<CourseDetails> getCourseById(long id) {
        Log.d(LOG_TAG, "Enter getCourseById(" + id + ")");
        return appDb.courseDAO().getById(id).subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * Gets rows from the {@link AppDb#TABLE_NAME_COURSES "courses"} data table within the underlying {@link AppDb} that are associated with a specific
     * row in the {@link AppDb#TABLE_NAME_TERMS "terms"} data table.
     *
     * @param termId The unique identifier of a {@link TermEntity}.
     * @return A {@link LiveData} object that will contain the list of {@link CourseEntity} objects retrieved from the underlying {@link AppDb} that are associated with a
     * specific {@link TermEntity}.
     */
    @NonNull
    public LiveData<List<TermCourseListItem>> getCoursesLiveDataByTermId(long termId) {
        Log.d(LOG_TAG, "Enter getCoursesByTermId(" + termId + ")");
        return appDb.courseDAO().getLiveDataByTermId(termId);
    }

    @NonNull
    public Observable<List<TermCourseListItem>> getCoursesObservableByTermId(long termId) {
        Log.d(LOG_TAG, "Enter getCoursesByTermId(" + termId + ")");
        return appDb.courseDAO().getObservableByTermId(termId).subscribeOn(scheduler);
    }

    /**
     * Gets rows from the {@link AppDb#TABLE_NAME_COURSES "courses"} data table within the underlying {@link AppDb} that are associated with a specific
     * row in the {@link AppDb#TABLE_NAME_MENTORS "mentors"} data table.
     *
     * @param mentorId The unique identifier of a {@link MentorEntity}.
     * @return A {@link LiveData} object that will contain the list of {@link CourseEntity} objects retrieved from the underlying {@link AppDb} that are associated with a
     * specific {@link MentorEntity}.
     */
    @NonNull
    public LiveData<List<MentorCourseListItem>> getCoursesByMentorId(long mentorId) {
        Log.d(LOG_TAG, "Enter getCoursesByMentorId(" + mentorId + ")");
        return appDb.courseDAO().getByMentorId(mentorId);
    }

    /**
     * Gets rows from the {@link AppDb#TABLE_NAME_COURSES "courses"} data table within the underlying {@link AppDb} that have not been completed and are expected to start
     * on or before a specified date.
     *
     * @param date The {@link LocalDate} value representing the inclusive end range of the expected start date.
     * @return A {@link LiveData} object that will contain the list of {@link CourseEntity} objects retrieved from the underlying {@link AppDb} that have not been completed and
     * are expected to start on or before a specified date.
     */
    @NonNull
    public LiveData<List<CourseEntity>> getUnterminatedCoursesOnOrBefore(LocalDate date) {
        Log.d(LOG_TAG, "Enter getUnterminatedCoursesOnOrBefore(" + ToStringBuilder.toEscapedString(date, true) + ")");
        return appDb.courseDAO().getUnterminatedOnOrBefore(date);
    }

    @NonNull
    public LiveData<List<CourseDetails>> getInProgressAndPlannedCourses() {
        Log.d(LOG_TAG, "Enter getInProgressAndPlannedCourses()");
        return appDb.courseDAO().getByStatus(CourseStatus.IN_PROGRESS, CourseStatus.PLANNED);
    }

    /**
     * Asynchronously save the specified {@link CourseEntity} object into the {@link AppDb#TABLE_NAME_COURSES "courses"} data table of the underlying {@link AppDb}.
     * If {@link CourseEntity#getId()} is null, then it will be inserted into the {@link AppDb#TABLE_NAME_COURSES "courses"} data table; otherwise, the corresponding table row will be
     * updated. After a new {@link CourseEntity} has been successfully inserted, the value returned by {@link CourseEntity#getId()} will contain the unique identifier of the
     * newly added row.
     *
     * @param entity The {@link CourseEntity} to be saved.
     * @return The {@link Completable} that can be observed for DB operation completion status.
     */
    @NonNull
    public Single<ResourceMessageResult> saveCourse(CourseEntity entity, boolean ignoreWarnings) {
        Log.d(LOG_TAG, "Enter saveCourse(" + ToStringBuilder.toEscapedString(entity) + ", " + ignoreWarnings + ")");
        return Single.fromCallable(() -> {
            final ResourceMessageBuilder builder = new ResourceMessageBuilder();
            Course.validate(builder, entity);
            CourseDAO dao = appDb.courseDAO();
            long termId = entity.getTermId();
            Optional<LocalDate> start = ComparisonHelper.firstNonNull(entity.getActualStart(), entity.getExpectedStart());
            Optional<LocalDate> end = ComparisonHelper.firstNonNull(entity.getActualEnd(), entity.getExpectedEnd());
            if (ID_NEW != termId) {
                TermEntity term = appDb.termDAO().getByIdSynchronous(termId);
                start.ifPresent(s -> Optional.ofNullable(term.getStart()).ifPresent(t -> {
                    if (s.compareTo(t) < 0) {
                        builder.acceptWarning(R.string.message_course_start_date_before_term_start);
                    }
                }));
                end.ifPresent(e -> Optional.ofNullable(term.getEnd()).ifPresent(t -> {
                    if (e.compareTo(t) > 0) {
                        builder.acceptWarning(R.string.message_course_end_date_after_term_end);
                    }
                }));
            }
            List<CourseEntity> list = dao.getAllSynchronous();
            String number = entity.getNumber();
            final long id = entity.getId();
            if (ID_NEW == id) {
                if (list.stream().anyMatch(t -> t.getNumber().equals(number))) {
                    builder.acceptWarning(R.string.message_course_duplicate_number);
                }
                if (builder.getLevel().map(l -> {
                    switch (l) {
                        case ERROR:
                            return true;
                        case WARNING:
                            return !ignoreWarnings;
                        default:
                            return false;
                    }
                }).orElse(false)) {
                    return builder.build();
                }
                entity.setId(dao.insertSynchronous(entity));
                return builder.build();
            }
            if (list.stream().anyMatch(t -> t.getNumber().equals(number) && id != t.getId())) {
                builder.acceptWarning(R.string.message_course_duplicate_number);
            }
            List<AssessmentEntity> assessments = appDb.assessmentDAO().getByCourseIdSynchronous(id);
            start.ifPresent(s -> {
                if (assessments.stream().anyMatch(a -> ComparisonHelper.firstNonNull(a.getCompletionDate(), a.getGoalDate()).map(d -> d.compareTo(s) < 0).orElse(false))) {
                    builder.acceptWarning(R.string.message_course_start_date_after_assessment);
                }
            });
            end.ifPresent(s -> {
                if (assessments.stream().anyMatch(a -> ComparisonHelper.firstNonNull(a.getCompletionDate(), a.getGoalDate()).map(d -> d.compareTo(s) > 0).orElse(false))) {
                    builder.acceptWarning(R.string.message_course_end_date_before_assessment);
                }
            });

            if (builder.getLevel().map(l -> {
                switch (l) {
                    case ERROR:
                        return true;
                    case WARNING:
                        return !ignoreWarnings;
                    default:
                        return false;
                }
            }).orElse(false)) {
                return builder.build();
            }
            dao.updateSynchronous(entity);
            return builder.build();
        }).subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * Asynchronously deletes a {@link AssessmentEntity} from the {@link AppDb#TABLE_NAME_ASSESSMENTS "assessments"} data table of the underlying {@link AppDb}.
     *
     * @param entity The {@link AssessmentEntity} to delete.
     * @return The {@link Completable} that can be observed for DB operation completion status.
     */
    @NonNull
    public Single<Integer> deleteAssessment(AssessmentEntity entity) {
        Log.d(LOG_TAG, "Enter deleteAssessment(" + ToStringBuilder.toEscapedString(entity) + ")");
        return appDb.assessmentDAO().delete(entity).subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * Asynchronously gets a {@link AssessmentEntity} from the {@link AppDb#TABLE_NAME_ASSESSMENTS "assessments"} data table within the underlying {@link AppDb} by its unique identifier.
     *
     * @param id The unique identifier of the  {@link AssessmentEntity} to retrieve.
     * @return The {@link Single} object that can be used to observe the result {@link AssessmentEntity} object.
     */
    @NonNull
    public Single<AssessmentDetails> getAssessmentById(long id) {
        Log.d(LOG_TAG, "Enter getAssessmentById(" + id + ")");
        return appDb.assessmentDAO().getById(id).subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread());
    }

    @NonNull
    public Single<AssessmentDetails> createAssessmentForCourse(long courseId, @Nullable LocalDate goalDate) {
        Log.d(LOG_TAG, "Enter createAssessmentForCourse(" + courseId + ", " + ToStringBuilder.toEscapedString(goalDate, true) + ")");
        return Single.fromCallable(() -> {
            AssessmentDetails assessmentDetails = new AssessmentDetails(appDb.courseDAO().getByIdSynchronous(courseId));
            assessmentDetails.setGoalDate(goalDate);
            return assessmentDetails;
        }).subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * Gets rows from the {@link AppDb#TABLE_NAME_ASSESSMENTS "assessments"} data table within the underlying {@link AppDb} that are associated with a specific
     * row in the {@link AppDb#TABLE_NAME_COURSES "courses"} data table.
     *
     * @param courseId The unique identifier of a {@link CourseEntity}.
     * @return A {@link LiveData} object that will contain the list of {@link AssessmentEntity} objects retrieved from the underlying {@link AppDb} that are associated with a
     * specific {@link CourseEntity}.
     */
    @NonNull
    public LiveData<List<AssessmentEntity>> getAssessmentsLiveDataByCourseId(long courseId) {
        Log.d(LOG_TAG, "Enter getAssessmentsLiveDataByCourseId(" + courseId + ")");
        return appDb.assessmentDAO().getLiveDataByCourseId(courseId);
    }

    @NonNull
    public Observable<List<AssessmentEntity>> getAssessmentsObservableByCourseId(long courseId) {
        Log.d(LOG_TAG, "Enter getAssessmentsObservableByCourseId(" + courseId + ")");
        return appDb.assessmentDAO().getObservableByCourseId(courseId);
    }

    @NonNull
    public Single<List<AssessmentEntity>> loadAssessmentsByCourseId(long courseId) {
        Log.d(LOG_TAG, "Enter loadAssessmentsByCourseId(" + courseId + ")");
        return appDb.assessmentDAO().loadByCourseId(courseId).subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread());
    }

    @NonNull
    public Single<List<TermCourseListItem>> loadCoursesByTermId(long termId) {
        Log.d(LOG_TAG, "Enter loadCoursesByTermId(" + termId + ")");
        return appDb.courseDAO().loadByTermId(termId).subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * Asynchronously save the specified {@link AssessmentEntity} object into the {@link AppDb#TABLE_NAME_ASSESSMENTS "assessments"} data table of the underlying {@link AppDb}.
     * If {@link AssessmentEntity#getId()} is null, then it will be inserted into the {@link AppDb#TABLE_NAME_ASSESSMENTS "assessments"} data table; otherwise, the corresponding table row will be
     * updated. After a new {@link AssessmentEntity} has been successfully inserted, the value returned by {@link AssessmentEntity#getId()} will contain the unique identifier of the
     * newly added row.
     *
     * @param entity The {@link AssessmentEntity} to be saved.
     * @return The {@link Completable} that can be observed for DB operation completion status.
     */
    @NonNull
    public Single<ResourceMessageResult> saveAssessment(AssessmentEntity entity, boolean ignoreWarnings) {
        Log.d(LOG_TAG, "Enter saveAssessment(" + ToStringBuilder.toEscapedString(entity) + ", " + ignoreWarnings + ")");
        return Single.fromCallable(() -> {
            final ResourceMessageBuilder builder = new ResourceMessageBuilder();
            Assessment.validate(builder, entity);
            AssessmentDAO dao = appDb.assessmentDAO();
            LocalDate date = entity.getCompletionDate();
            if (null != date || null != (date = entity.getGoalDate())) {
                final long courseId = entity.getCourseId();
                CourseEntity course = appDb.courseDAO().getByIdSynchronous(courseId);
                LocalDate d = course.getActualStart();
                if (null != d || null != (d = course.getExpectedStart()) && d.compareTo(date) > 0) {
                    builder.acceptWarning(R.string.message_assessment_date_before_course_start);
                } else if ((null != (d = course.getActualEnd()) || null != (d = course.getExpectedEnd())) && d.compareTo(date) < 0) {
                    builder.acceptWarning(R.string.message_assessment_date_after_course_end);
                }
            }
            List<AssessmentEntity> list = dao.getAllSynchronous();
            String code = entity.getCode();
            final long id = entity.getId();
            if (ID_NEW == id) {
                if (list.stream().anyMatch(t -> t.getCode().equals(code))) {
                    builder.acceptWarning(R.string.message_assessment_duplicate_code);
                }
                if (builder.getLevel().map(l -> {
                    switch (l) {
                        case ERROR:
                            return true;
                        case WARNING:
                            return !ignoreWarnings;
                        default:
                            return false;
                    }
                }).orElse(false)) {
                    return builder.build();
                }
                entity.setId(dao.insertSynchronous(entity));
                return builder.build();
            }
            if (list.stream().anyMatch(t -> t.getCode().equals(code) && id != t.getId())) {
                builder.acceptWarning(R.string.message_assessment_duplicate_code);
            }

            if (builder.getLevel().map(l -> {
                switch (l) {
                    case ERROR:
                        return true;
                    case WARNING:
                        return !ignoreWarnings;
                    default:
                        return false;
                }
            }).orElse(false)) {
                return builder.build();
            }
            dao.updateSynchronous(entity);
            return builder.build();
        }).subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread());
    }

    public Completable deleteCourseAlert(CourseAlert entity) {
        Log.d(LOG_TAG, "Enter deleteCourseAlert(" + ToStringBuilder.toEscapedString(entity) + ")");
        return Completable.fromAction(() -> appDb.runInTransaction(() -> {
            if (appDb.courseAlertDAO().deleteSynchronous(entity.getLink()) < 1) {
                throw new RuntimeException("Failed to delete associative entry");
            }
            if (appDb.alertDAO().deleteSynchronous(entity.getAlert()) < 1) {
                throw new RuntimeException("Failed to delete alert");
            }
        })).subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread());
    }

    public Completable deleteAssessmentAlert(AssessmentAlert entity) {
        Log.d(LOG_TAG, "Enter deleteAssessmentAlert(" + ToStringBuilder.toEscapedString(entity) + ")");
        return Completable.fromAction(() -> appDb.runInTransaction(() -> {
            if (appDb.assessmentAlertDAO().deleteSynchronous(entity.getLink()) < 1) {
                throw new RuntimeException("Failed to delete associative entry");
            }
            if (appDb.alertDAO().deleteSynchronous(entity.getAlert()) < 1) {
                throw new RuntimeException("Failed to delete alert");
            }
        })).subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread());
    }

    private int getNextNotificationId() {
        int id = Objects.requireNonNull(preferNextNotificationIdSubject.getValue());
        AlertDAO alertDao = appDb.alertDAO();
        while (alertDao.countByNotificationId(id) > 0) {
            if (id == Integer.MAX_VALUE) {
                id = 1;
            } else {
                id++;
            }
        }
        return id;
    }

    void insertCourseAlertSynchronous(CourseAlert entity) {
        appDb.runInTransaction(() -> {
            AlertEntity alert = entity.getAlert();
            CourseAlertLink link = entity.getLink();
            if (alert.getNotificationId() == 0) {
                alert.setNotificationId(getNextNotificationId());
            }
            long id = appDb.alertDAO().insertSynchronous(alert);
            link.setAlertIdAndRun(id, () -> {
                appDb.courseAlertDAO().insertSynchronous(link);
                preferNextNotificationIdSubject.onNext(alert.getNotificationId() + 1);
            });
            alert.setId(id);
        });
    }

    public Single<ResourceMessageResult> saveCourseAlert(CourseAlert entity, boolean ignoreWarnings) {
        Log.d(LOG_TAG, "Enter saveCourseAlert(" + ToStringBuilder.toEscapedString(entity) + ", " + ignoreWarnings + ")");
        return Single.fromCallable(() -> {
            final ResourceMessageBuilder builder = new ResourceMessageBuilder();
            AlertEntity alert = entity.getAlert();
            AlertLink.validate(builder, entity);
            if (builder.getLevel().map(l -> {
                switch (l) {
                    case ERROR:
                        return true;
                    case WARNING:
                        return !ignoreWarnings;
                    default:
                        return false;
                }
            }).orElse(false)) {
                return builder.build();
            }

            appDb.runInTransaction(() -> {
                CourseAlertLink link = entity.getLink();
                if (alert.getTimeSpec() < 0L) {
                    Boolean subsequent = alert.isSubsequent();
                    // Need to be sure that course is saved before this is invoked or validation message may not be as expected
                    if (null != subsequent && !subsequent && null != appDb.courseDAO().getByIdSynchronous(link.getTargetId()).getActualEnd()) {
                        builder.acceptError(R.string.message_alert_relative_before_end);
                        return;
                    }
                }
                if (alert.getId() == ID_NEW) {
                    if (alert.getNotificationId() == 0) {
                        alert.setNotificationId(getNextNotificationId());
                    }
                    long id = appDb.alertDAO().insertSynchronous(alert);
                    link.setAlertIdAndRun(id, () -> {
                        appDb.courseAlertDAO().insertSynchronous(link);
                        preferNextNotificationIdSubject.onNext(alert.getNotificationId() + 1);
                    });
                    alert.setId(id);
                } else {
                    appDb.courseAlertDAO().updateSynchronous(entity.getLink());
                    appDb.alertDAO().updateSynchronous(alert);
                }
            });
            return builder.build();
        }).subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread());
    }

    void insertAssessmentAlertSynchronous(AssessmentAlert entity) {
        appDb.runInTransaction(() -> {
            AlertEntity alert = entity.getAlert();
            AssessmentAlertLink link = entity.getLink();
            if (alert.getNotificationId() == 0) {
                alert.setNotificationId(getNextNotificationId());
            }
            long id = appDb.alertDAO().insertSynchronous(alert);
            link.setAlertIdAndRun(id, () -> {
                appDb.assessmentAlertDAO().insertSynchronous(link);
                preferNextNotificationIdSubject.onNext(alert.getNotificationId() + 1);
            });
            alert.setId(id);
        });
    }

    public Single<ResourceMessageResult> saveAssessmentAlert(AssessmentAlert entity, boolean ignoreWarnings) {
        Log.d(LOG_TAG, "Enter saveAssessmentAlert(" + ToStringBuilder.toEscapedString(entity) + ", " + ignoreWarnings + ")");
        return Single.fromCallable(() -> {
            final ResourceMessageBuilder builder = new ResourceMessageBuilder();
            entity.validate(builder);
            if (builder.getLevel().map(l -> {
                switch (l) {
                    case ERROR:
                        return true;
                    case WARNING:
                        return !ignoreWarnings;
                    default:
                        return false;
                }
            }).orElse(false)) {
                return builder.build();
            }
            appDb.runInTransaction(() -> {
                AlertEntity alert = entity.getAlert();
                AssessmentAlertLink link = entity.getLink();
                if (alert.getId() == ID_NEW) {
                    if (alert.getNotificationId() == 0) {
                        alert.setNotificationId(getNextNotificationId());
                    }
                    long id = appDb.alertDAO().insertSynchronous(alert);
                    link.setAlertIdAndRun(id, () -> {
                        appDb.assessmentAlertDAO().insertSynchronous(link);
                        preferNextNotificationIdSubject.onNext(alert.getNotificationId() + 1);
                    });
                    alert.setId(id);
                } else {
                    appDb.assessmentAlertDAO().updateSynchronous(link);
                    appDb.alertDAO().updateSynchronous(alert);
                }
            });
            return builder.build();
        }).subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread());
    }

    @NonNull
    public LiveData<List<CourseAlert>> getAlertsLiveDataByCourseId(long id) {
        return appDb.courseAlertDAO().getLiveDataByCourseId(id);
    }

    @NonNull
    public Observable<List<CourseAlert>> getAlertsObservableByCourseId(long id) {
        return appDb.courseAlertDAO().getObservableByCourseId(id);
    }

    @NonNull
    public LiveData<List<AssessmentAlert>> getAlertsLiveDataByAssessmentId(long id) {
        return appDb.assessmentAlertDAO().getLiveDataByAssessmentId(id);
    }

    @NonNull
    public Observable<List<AssessmentAlert>> getAlertsObservableByAssessmentId(long id) {
        return appDb.assessmentAlertDAO().getObservableByAssessmentId(id);
    }

    @NonNull
    public LiveData<List<AlertListItem>> getActiveAlertsOnDate(LocalDate date) {
        Log.d(LOG_TAG, "Enter getActiveAlertsOnDate(position: " + ToStringBuilder.toEscapedString(date, true) + ")");
        return appDb.alertDAO().getActiveOnDate(date);
    }

    @NonNull
    public LiveData<List<AlertListItem>> getActiveAlertsAfterDate(LocalDate date) {
        Log.d(LOG_TAG, "Enter getActiveAlertsAfterDate(position: " + ToStringBuilder.toEscapedString(date, true) + ")");
        return appDb.alertDAO().getActiveAfterDate(date);
    }

    @NonNull
    public LiveData<List<AlertListItem>> getActiveAlertsBeforeDate(LocalDate date) {
        Log.d(LOG_TAG, "Enter getActiveAlertsBeforeDate(position: " + ToStringBuilder.toEscapedString(date, true) + ")");
        return appDb.alertDAO().getActiveBeforeDate(date);
    }

    @NonNull
    public Single<List<AlertListItem>> getAllAlertsByCourseId(long courseId) {
        return appDb.alertDAO().getAllByCourseId(courseId);
    }

    @NonNull
    public Single<List<AlertListItem>> getAllAlertsByTermId(long termId) {
        return appDb.alertDAO().getAllByTermId(termId);
    }

    @NonNull
    public Single<List<AlertListItem>> getAllAlertsByMentorId(long mentorId) {
        return appDb.alertDAO().getAllByMentorId(mentorId);
    }

    @NonNull
    public Single<CourseAlertDetails> getCourseAlertDetailsById(long alertId, long courseId) {
        return appDb.courseAlertDAO().getDetailsAlertId(alertId, courseId).subscribeOn(this.scheduler).observeOn(AndroidSchedulers.mainThread());
    }

    @NonNull
    public Single<AssessmentAlertDetails> getAssessmentAlertDetailsById(long alertId, long assessmentId) {
        return appDb.assessmentAlertDAO().getByDetailByAlertId(alertId, assessmentId).subscribeOn(this.scheduler).observeOn(AndroidSchedulers.mainThread());
    }

    @NonNull
    public Single<String> checkDbIntegrity() {
        return Single.fromCallable(() -> {
            List<Long> termIds;
            List<Long> mentorIds;

            try {
                termIds = appDb.termDAO().getAllSynchronous().stream().map(AbstractEntity::getId).collect(Collectors.toList());
            } catch (Exception e) {
                throw new RuntimeException("Error accessing terms table", e);
            }
            try {
                mentorIds = appDb.mentorDAO().getAllSynchronous().stream().map(AbstractEntity::getId).collect(Collectors.toList());
            } catch (Exception e) {
                throw new RuntimeException("Error accessing mentors table", e);
            }
            List<Long> courseIds;
            ArrayList<String> messages = new ArrayList<>();
            try {
                courseIds = appDb.courseDAO().getAllSynchronous().stream().map(t -> {
                    Long id = t.getMentorId();
                    if (null != id && !mentorIds.contains(id)) {
                        messages.add(String.format(Locale.getDefault(), "%s: Row with primary key %d is using a non-existent mentorId of %d.", AppDb.TABLE_NAME_COURSES, t.getId(), id));
                    }
                    id = t.getTermId();
                    if (!termIds.contains(id)) {
                        messages.add(String.format(Locale.getDefault(), "%s: Row with primary key %d is using a non-existent termId of %d.", AppDb.TABLE_NAME_COURSES, t.getId(), id));
                    }
                    return t.getId();
                }).collect(Collectors.toList());
            } catch (Exception e) {
                throw new RuntimeException("Error accessing courses table", e);
            }
            List<AssessmentEntity> assessments;
            try {
                assessments = appDb.assessmentDAO().getAllSynchronous();
                assessments.forEach(t -> {
                    Long id = t.getCourseId();
                    if (!courseIds.contains(id)) {
                        messages.add(String.format(Locale.getDefault(), "%s: Row with primary key %d is using a non-existent courseId of %d.", AppDb.TABLE_NAME_ASSESSMENTS, t.getId(), id));
                    }
                });
            } catch (Exception e) {
                throw new RuntimeException("Error accessing assessments table", e);
            }
            return (messages.isEmpty()) ? "" : String.join("\n", messages);
        }).subscribeOn(this.scheduler).observeOn(AndroidSchedulers.mainThread());
    }

    void resetDb() {
        Log.d(LOG_TAG, "Clearing all tables");
        appDb.clearAllTables();
        for (String t : new String[]{
                AppDb.TABLE_NAME_ASSESSMENT_ALERTS,
                AppDb.TABLE_NAME_ASSESSMENTS,
                AppDb.TABLE_NAME_COURSE_ALERTS,
                AppDb.TABLE_NAME_COURSES,
                AppDb.TABLE_NAME_TERMS,
                AppDb.TABLE_NAME_MENTORS
        }) {
            Log.d(LOG_TAG, String.format("Resetting default sequence of %s", t));
            appDb.query(String.format("UPDATE sqlite_sequence SET seq = 1 WHERE name = '%s'", t), null).close();
        }
    }

    @NonNull
    public Completable resetDatabase() {
        return Completable.fromAction(this::resetDb).subscribeOn(this.scheduler).observeOn(AndroidSchedulers.mainThread());
    }

    @NonNull
    public Completable populateSampleData(Resources resources) {
        return Completable.fromAction(new SampleDataLoader(this, resources)).subscribeOn(this.scheduler).observeOn(AndroidSchedulers.mainThread());
    }

}
