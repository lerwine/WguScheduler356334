package Erwine.Leonard.T.wguscheduler356334.db;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.preference.PreferenceManager;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import Erwine.Leonard.T.wguscheduler356334.R;
import Erwine.Leonard.T.wguscheduler356334.entity.AbstractEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.alert.Alert;
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
import Erwine.Leonard.T.wguscheduler356334.entity.course.MentorCourseListItem;
import Erwine.Leonard.T.wguscheduler356334.entity.course.TermCourseListItem;
import Erwine.Leonard.T.wguscheduler356334.entity.mentor.Mentor;
import Erwine.Leonard.T.wguscheduler356334.entity.mentor.MentorEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.mentor.MentorListItem;
import Erwine.Leonard.T.wguscheduler356334.entity.term.Term;
import Erwine.Leonard.T.wguscheduler356334.entity.term.TermEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.term.TermListItem;
import Erwine.Leonard.T.wguscheduler356334.util.ComparisonHelper;
import Erwine.Leonard.T.wguscheduler356334.util.ValidationMessage;
import io.reactivex.Completable;
import io.reactivex.Scheduler;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

import static Erwine.Leonard.T.wguscheduler356334.entity.IdIndexedEntity.ID_NEW;

/**
 * A singleton helper object for database I/O.
 */
public class DbLoader {

    private static final String LOG_TAG = DbLoader.class.getName();

    /**
     * 1=title, 2=start, 3=end, 4=notes?
     */
    private static final Pattern PATTERN_SAMPLE_TERM_DATA = Pattern.compile("^\\s*([^,]+),([^,]+),([^,]+),(\\S+(?:\\s+\\S+)*)?\\s*$");

    /**
     * 1=name; 2=notes?; 3=phone_numbers?; 4=email_addresses?
     */
    private static final Pattern PATTERN_SAMPLE_MENTOR_DATA = Pattern.compile("^([^\\t]+)\\t([^\\t]+)?\\t([^\\t]+)?\\t([^\\t]+)?$");

    /**
     * 1=termId; 2=number; 3=title; 4=status; 5=expectedStart?; 6=actualStart?; 7=expectedEnd?; 8=actualEnd?; 9=competencyUnits?; 10=notes?; 11=mentorId?
     */
    private static final Pattern PATTERN_SAMPLE_COURSE_DATA = Pattern.compile("^(\\d+)\\t([^\\t]+)\\t([^\\t]+)\\t([^\\t]+)\\t([^\\t]+)?\\t([^\\t]+)?\\t([^\\t]+)?\\t([^\\t]+)?" +
            "\\t([^\\t]+)?\\t([^\\t]+)?\\t([^\\t]+)?$");
    /**
     * 1=courseNumber; 2=code; 3=status; (4=yyyy-mm-dd | 5=expectedEnd | 6=actualEnd)=goalDate?; 7=type; 8=notes?; (9=yyyy-mm-dd | 10=expectedEnd | 11=actualEnd)=evaluationDate?
     */
    private static final Pattern PATTERN_SAMPLE_ASSESSMENT_DATA = Pattern.compile("^([^\\t]+)\\t([^\\t]+)\\t([^\\t]+)\\t(?:(\\d{4}-\\d\\d-\\d\\d)|(expectedEnd)|(actualEnd))?" +
            "\\t([^\\t]+)\\t([^\\t]+)?\\t(?:(\\d{4}-\\d\\d-\\d\\d)|(expectedEnd)|(actualEnd))?$");

    private static final MutableLiveData<Boolean> preferEmailLiveData = new MutableLiveData<>(false);
    private static DbLoader instance;
    @SuppressWarnings("FieldCanBeLocal")
    private final CompositeDisposable compositeDisposable;
    private final AppDb appDb;
    private final Scheduler scheduler;
    @SuppressWarnings("FieldCanBeLocal")
    private final Executor dataExecutor;
    private LiveData<List<TermListItem>> allTerms;
    private LiveData<List<MentorListItem>> allMentors;
//    private LiveData<List<CourseEntity>> allCourses;
//    private LiveData<List<AssessmentEntity>> allAssessments;

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
        compositeDisposable = new CompositeDisposable();
        this.appDb = appDb;
        dataExecutor = Executors.newSingleThreadExecutor();
        scheduler = Schedulers.from(dataExecutor);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        preferEmailLiveData.postValue(sharedPreferences.getBoolean(context.getResources().getString(R.string.preference_prefer_email), false));
    }

    public static MutableLiveData<Boolean> getPreferEmailLiveData() {
        return preferEmailLiveData;
    }

    AppDb getAppDb() {
        return appDb;
    }

//    /**
//     * Asynchronously gets a {@link TermEntity} from the {@link AppDb#TABLE_NAME_TERMS "terms"} data table within the underlying {@link AppDb} by its {@code ROWID}.
//     *
//     * @param rowId The {@code ROWID} of the {@link TermEntity} to retrieve.
//     * @return The {@link Single} object that can be used to observe the result {@link TermEntity} object.
//     */
//    @NonNull
//    public Single<TermEntity> getTermByRowId(int rowId) {
//        return appDb.termDAO().getByRowId(rowId).subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread());
//    }

    /**
     * Asynchronously gets a {@link TermEntity} from the {@link AppDb#TABLE_NAME_TERMS "terms"} data table within the underlying {@link AppDb} by its unique identifier.
     *
     * @param id The unique identifier of the  {@link TermEntity} to retrieve.
     * @return The {@link Single} object that can be used to observe the result {@link TermEntity} object.
     */
    @NonNull
    public Single<TermEntity> getTermById(long id) {
        Log.d(LOG_TAG, String.format("Called getTermById(%d)", id));
        return appDb.termDAO().getById(id).subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * Gets all rows from the {@link AppDb#TABLE_NAME_TERMS "terms"} data table within the underlying {@link AppDb}.
     *
     * @return A {@link LiveData} object that will contain the list of {@link TermEntity} objects retrieved from the underlying {@link AppDb}.
     */
    @NonNull
    public LiveData<List<TermListItem>> getAllTerms() {
        Log.d(LOG_TAG, "Called getAllTerms()");
        if (null == allTerms) {
            allTerms = appDb.termDAO().getAll();
        }
        return allTerms;
    }

//    /**
//     * Asynchronously gets the number of rows in the {@link AppDb#TABLE_NAME_TERMS "terms"} data table within the underlying {@link AppDb}.
//     *
//     * @return The {@link Single} object that can be used to observe the number of rows in the {@link AppDb#TABLE_NAME_TERMS "terms"} data table within the underlying {@link AppDb}.
//     */
//    @NonNull
//    public Single<Integer> getTermCount() {
//        Log.d(LOG_TAG, "Called getTermCount()");
//        return appDb.termDAO().getCount().subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread());
//    }

    @NonNull
    public Single<ValidationMessage.ResourceMessageResult> saveTerm(TermEntity entity, boolean ignoreWarnings) {
        Log.d(LOG_TAG, String.format("Called saveTerm(%s)", entity));
        return Single.fromCallable(() -> {
            final ValidationMessage.ResourceMessageBuilder builder = new ValidationMessage.ResourceMessageBuilder();
            Term.validate(builder, entity);
            TermDAO dao = appDb.termDAO();
            List<TermListItem> list = dao.getAllSynchronous();
            String name = entity.getName();
            final long id = entity.getId();
            if (id == ID_NEW) {
                if (list.stream().anyMatch(t -> t.getName().equals(name))) {
                    builder.acceptError(R.string.message_term_duplicate_name);
                }
                if (builder.hasError() || (!ignoreWarnings && builder.hasWarning())) {
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
            if (builder.hasError() || (!ignoreWarnings && builder.hasWarning())) {
                return builder.build();
            }
            dao.updateSynchronous(entity);
            return builder.build();
        }).subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread());
    }

//    /**
//     * Asynchronously inserts a {@link List} of @link TermEntity} objects into the {@link AppDb#TABLE_NAME_TERMS "terms"} data table of the underlying {@link AppDb}.
//     *
//     * @param list The {@link List} of @link TermEntity} objects to be inserted into the {@link AppDb#TABLE_NAME_TERMS "terms"} data table.
//     * @return The {@link Completable} that can be observed for DB operation completion status.
//     */
//    @NonNull
//    public Completable insertAllTerms(List<TermEntity> list) {
//        return Completable.fromSingle(appDb.termDAO().insertAll(list).subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread())
//                .doAfterSuccess(ids -> applyInsertedIds(ids, list, AbstractEntity::setId)));
//    }

    /**
     * Asynchronously deletes a {@link TermEntity} from the {@link AppDb#TABLE_NAME_TERMS "terms"} data table of the underlying {@link AppDb}.
     *
     * @param entity The {@link TermEntity} to delete.
     * @return The {@link Completable} that can be observed for DB operation completion status.
     */
    @NonNull
    public Single<ValidationMessage.ResourceMessageResult> deleteTerm(TermEntity entity, boolean ignoreWarnings) {
        Log.d(LOG_TAG, String.format("Called deleteTerm(%s)", entity));
        return Single.fromCallable(() -> {
            long id = entity.getId();
            if (ID_NEW == id) {
                return ValidationMessage.ofSingleWarning(R.string.message_item_never_saved);
            }
            if (!(ignoreWarnings || appDb.courseDAO().getCountByTermIdSynchronous(entity.getId()) == 0)) {
                return ValidationMessage.ofSingleWarning(R.string.message_term_has_courses);
            }
            appDb.termDAO().delete(entity);
            return ValidationMessage.success();
        }).subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread());
    }

//    /**
//     * Asynchronously gets a {@link MentorEntity} from the {@link AppDb#TABLE_NAME_MENTORS "mentors"} data table within the underlying {@link AppDb} by its {@code ROWID}.
//     *
//     * @param rowId The {@code ROWID} of the {@link MentorEntity} to retrieve.
//     * @return The {@link Single} object that can be used to observe the result {@link MentorEntity} object.
//     */
//    @NonNull
//    public Single<MentorEntity> getMentorByRowId(int rowId) {
//        return appDb.mentorDAO().getByRowId(rowId).subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread());
//    }

    /**
     * Gets all rows from the {@link AppDb#TABLE_NAME_MENTORS "mentors"} data table within the underlying {@link AppDb}.
     *
     * @return A {@link LiveData} object that will contain the list of {@link MentorEntity} objects retrieved from the underlying {@link AppDb}.
     */
    @NonNull
    public LiveData<List<MentorListItem>> getAllMentors() {
        Log.d(LOG_TAG, "Called getAllMentors()");
        if (null == allMentors) {
            allMentors = appDb.mentorDAO().getAll();
        }
        return allMentors;
    }

//    /**
//     * Asynchronously gets the number of rows in the {@link AppDb#TABLE_NAME_MENTORS "mentors"} data table within the underlying {@link AppDb}.
//     *
//     * @return The {@link Single} object that can be used to observe the number of rows in the {@link AppDb#TABLE_NAME_MENTORS "mentors"} data table within the underlying {@link AppDb}.
//     */
//    @NonNull
//    public Single<Integer> getMentorCount() {
//        Log.d(LOG_TAG, "Called getMentorCount()");
//        return appDb.mentorDAO().getCount().subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread());
//    }

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
    public Single<ValidationMessage.ResourceMessageResult> saveMentor(MentorEntity entity, boolean ignoreWarnings) {
        Log.d(LOG_TAG, String.format("Called saveMentor(%s)", entity));
        return Single.fromCallable(() -> {
            final ValidationMessage.ResourceMessageBuilder builder = new ValidationMessage.ResourceMessageBuilder();
            Mentor.validate(builder, entity);
            MentorDAO dao = appDb.mentorDAO();
            List<MentorListItem> list = dao.getAllSynchronous();
            String name = entity.getName();
            final long id = entity.getId();
            if (ID_NEW == id) {
                if (list.stream().anyMatch(t -> t.getName().equals(name))) {
                    builder.acceptError(R.string.message_mentor_duplicate_name);
                }
                if (builder.hasError() || (!ignoreWarnings && builder.hasWarning())) {
                    return builder.build();
                }
                entity.setId(dao.insertSynchronous(entity));
                return builder.build();
            }
            if (list.stream().anyMatch(t -> t.getName().equals(name) && id != t.getId())) {
                builder.acceptError(R.string.message_mentor_duplicate_name);
            }


            if (builder.hasError() || (!ignoreWarnings && builder.hasWarning())) {
                return builder.build();
            }
            dao.updateSynchronous(entity);
            return builder.build();
        }).subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread());
    }

//    /**
//     * Asynchronously inserts a {@link List} of @link MentorEntity} objects into the {@link AppDb#TABLE_NAME_MENTORS "mentors"} data table of the underlying {@link AppDb}.
//     *
//     * @param list The {@link List} of @link TermEntity} objects to be inserted into the {@link AppDb#TABLE_NAME_MENTORS "mentors"} data table.
//     * @return The {@link Completable} that can be observed for DB operation completion status.
//     */
//    @NonNull
//    public Completable insertAllMentors(List<MentorEntity> list) {
//        return Completable.fromSingle(appDb.mentorDAO().insertAll(list).subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread())
//                .doAfterSuccess(ids -> applyInsertedIds(ids, list, AbstractEntity::setId)));
//    }

    /**
     * Asynchronously deletes a {@link MentorEntity} from the {@link AppDb#TABLE_NAME_MENTORS "mentors"} data table of the underlying {@link AppDb}.
     *
     * @param entity The {@link MentorEntity} to delete.
     * @return The {@link Completable} that can be observed for DB operation completion status.
     */
    @NonNull
    public Single<ValidationMessage.ResourceMessageResult> deleteMentor(MentorEntity entity, boolean ignoreWarnings) {
        Log.d(LOG_TAG, String.format("Called deleteMentor(%s)", entity));
        return Single.fromCallable(() -> {
            long id = entity.getId();
            if (ID_NEW == id) {
                return ValidationMessage.ofSingleWarning(R.string.message_item_never_saved);
            }
            if (!(ignoreWarnings || appDb.courseDAO().getCountByMentorIdSynchronous(id) > 0)) {
                return ValidationMessage.ofSingleWarning(R.string.message_mentor_has_courses);
            }
            appDb.mentorDAO().deleteSynchronous(entity);
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
        Log.d(LOG_TAG, String.format("Called getMentorById(%d)", id));
        return appDb.mentorDAO().getById(id).subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * Asynchronously deletes a {@link CourseEntity} from the {@link AppDb#TABLE_NAME_COURSES "courses"} data table of the underlying {@link AppDb}.
     *
     * @param entity The {@link CourseEntity} to delete.
     * @return The {@link Completable} that can be observed for DB operation completion status.
     */
    @NonNull
    public Single<ValidationMessage.ResourceMessageResult> deleteCourse(CourseEntity entity, boolean ignoreWarnings) {
        Log.d(LOG_TAG, String.format("Called deleteCourse(%s)", entity));
        return Single.fromCallable(() -> {
            long id = entity.getId();
            if (ID_NEW == id) {
                return ValidationMessage.ofSingleWarning(R.string.message_item_never_saved);
            }
            if (!(ignoreWarnings || appDb.assessmentDAO().getCountByCourseIdSynchronous(id) > 0)) {
                return ValidationMessage.ofSingleWarning(R.string.message_course_has_assessments);
            }
            appDb.courseDAO().deleteSynchronous(entity);
            return ValidationMessage.success();
        }).subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread());
    }

//    /**
//     * Asynchronously gets a {@link CourseEntity} from the {@link AppDb#TABLE_NAME_COURSES "courses"} data table within the underlying {@link AppDb} by its {@code ROWID}.
//     *
//     * @param rowId The {@code ROWID} of the {@link CourseEntity} to retrieve.
//     * @return The {@link Single} object that can be used to observe the result {@link CourseEntity} object.
//     */
//    @NonNull
//    public Single<CourseEntity> getCourseByRowId(int rowId) {
//        return appDb.courseDAO().getByRowId(rowId).subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread());
//    }

    /**
     * Asynchronously gets a {@link CourseDetails} from the {@link AppDb#TABLE_NAME_COURSES "courses"} data table within the underlying {@link AppDb} by its unique identifier.
     *
     * @param id The unique identifier of the  {@link CourseDetails} to retrieve.
     * @return The {@link Single} object that can be used to observe the result {@link CourseDetails} object.
     */
    @NonNull
    public Single<CourseDetails> getCourseById(long id) {
        Log.d(LOG_TAG, String.format("Called getCourseById(%d)", id));
        return appDb.courseDAO().getById(id).subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread());
    }

//    /**
//     * Gets all rows from the {@link AppDb#TABLE_NAME_COURSES "courses"} data table within the underlying {@link AppDb}.
//     *
//     * @return A {@link LiveData} object that will contain the list of {@link CourseEntity} objects retrieved from the underlying {@link AppDb}.
//     */
//    @NonNull
//    public LiveData<List<CourseEntity>> getAllCourses() {
//        Log.d(LOG_TAG, "Called getAllCourses()");
//        if (null == allCourses) {
//            allCourses = appDb.courseDAO().getAll();
//        }
//        return allCourses;
//    }

    /**
     * Gets rows from the {@link AppDb#TABLE_NAME_COURSES "courses"} data table within the underlying {@link AppDb} that are associated with a specific
     * row in the {@link AppDb#TABLE_NAME_TERMS "terms"} data table.
     *
     * @param termId The unique identifier of a {@link TermEntity}.
     * @return A {@link LiveData} object that will contain the list of {@link CourseEntity} objects retrieved from the underlying {@link AppDb} that are associated with a
     * specific {@link TermEntity}.
     */
    @NonNull
    public LiveData<List<TermCourseListItem>> getCoursesByTermId(long termId) {
        Log.d(LOG_TAG, String.format("Called getCoursesByTermId(%d)", termId));
        return appDb.courseDAO().getByTermId(termId);
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
        Log.d(LOG_TAG, String.format("Called getCoursesByMentorId(%d)", mentorId));
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
        Log.d(LOG_TAG, String.format("Called getUnterminatedCoursesOnOrBefore(%s)", date));
        return appDb.courseDAO().getUnterminatedOnOrBefore(date);
    }

//    /**
//     * Asynchronously gets the number of rows in the {@link AppDb#TABLE_NAME_COURSES "courses"} data table within the underlying {@link AppDb}.
//     *
//     * @return The {@link Single} object that can be used to observe the number of rows in the {@link AppDb#TABLE_NAME_COURSES "courses"} data table within the underlying {@link AppDb}.
//     */
//    @NonNull
//    public Single<Integer> getCourseCount() {
//        Log.d(LOG_TAG, "Called getCourseCount()");
//        return appDb.courseDAO().getCount().subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread());
//    }

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
    public Single<ValidationMessage.ResourceMessageResult> saveCourse(CourseEntity entity, boolean ignoreWarnings) {
        Log.d(LOG_TAG, String.format("Called saveCourse(%s)", entity));
        return Single.fromCallable(() -> {
            final ValidationMessage.ResourceMessageBuilder builder = new ValidationMessage.ResourceMessageBuilder();
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
                if (builder.hasError() || (!ignoreWarnings && builder.hasWarning())) {
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

            if (builder.hasError() || (!ignoreWarnings && builder.hasWarning())) {
                return builder.build();
            }
            dao.updateSynchronous(entity);
            return builder.build();
        }).subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread());
    }

//    /**
//     * Asynchronously inserts a {@link List} of @link CourseEntity} objects into the {@link AppDb#TABLE_NAME_COURSES "courses"} data table of the underlying {@link AppDb}.
//     *
//     * @param list The {@link List} of @link CourseEntity} objects to be inserted into the {@link AppDb#TABLE_NAME_COURSES "courses"} data table.
//     * @return The {@link Completable} that can be observed for DB operation completion status.
//     */
//    @NonNull
//    public Completable insertAllCourses(List<CourseEntity> list) {
//        return Completable.fromSingle(appDb.courseDAO().insertAll(list).subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread())
//                .doAfterSuccess(ids -> applyInsertedIds(ids, list, AbstractEntity::setId)));
//    }

    /**
     * Asynchronously deletes a {@link AssessmentEntity} from the {@link AppDb#TABLE_NAME_ASSESSMENTS "assessments"} data table of the underlying {@link AppDb}.
     *
     * @param entity The {@link AssessmentEntity} to delete.
     * @return The {@link Completable} that can be observed for DB operation completion status.
     */
    @NonNull
    public Completable deleteAssessment(AssessmentEntity entity) {
        Log.d(LOG_TAG, String.format("Called deleteAssessment(%s)", entity));
        return appDb.assessmentDAO().delete(entity).subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread());
    }

//    /**
//     * Asynchronously gets a {@link AssessmentEntity} from the {@link AppDb#TABLE_NAME_ASSESSMENTS "assessments"} data table within the underlying {@link AppDb} by its {@code ROWID}.
//     *
//     * @param rowId The {@code ROWID} of the {@link AssessmentEntity} to retrieve.
//     * @return The {@link Single} object that can be used to observe the result {@link AssessmentEntity} object.
//     */
//    @NonNull
//    public Single<AssessmentEntity> getAssessmentByRowId(int rowId) {
//        return appDb.assessmentDAO().getByRowId(rowId).subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread());
//    }

    /**
     * Asynchronously gets a {@link AssessmentEntity} from the {@link AppDb#TABLE_NAME_ASSESSMENTS "assessments"} data table within the underlying {@link AppDb} by its unique identifier.
     *
     * @param id The unique identifier of the  {@link AssessmentEntity} to retrieve.
     * @return The {@link Single} object that can be used to observe the result {@link AssessmentEntity} object.
     */
    @NonNull
    public Single<AssessmentDetails> getAssessmentById(long id) {
        Log.d(LOG_TAG, String.format("Called getAssessmentById(%d)", id));
        return appDb.assessmentDAO().getById(id).subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread());
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
    public LiveData<List<AssessmentEntity>> getAssessmentsByCourseId(long courseId) {
        Log.d(LOG_TAG, String.format("Called getAssessmentsByCourseId(%d)", courseId));
        return appDb.assessmentDAO().getByCourseId(courseId);
    }

//    /**
//     * Gets all rows from the {@link AppDb#TABLE_NAME_ASSESSMENTS "assessments"} data table within the underlying {@link AppDb}.
//     *
//     * @return A {@link LiveData} object that will contain the list of {@link AssessmentEntity} objects retrieved from the underlying {@link AppDb}.
//     */
//    @NonNull
//    public LiveData<List<AssessmentEntity>> getAllAssessments() {
//        Log.d(LOG_TAG, "Called getAllAssessments()");
//        if (null == allAssessments) {
//            allAssessments = appDb.assessmentDAO().getAll();
//        }
//        return allAssessments;
//    }

//    /**
//     * Asynchronously gets the number of rows in the {@link AppDb#TABLE_NAME_ASSESSMENTS "assessments"} data table within the underlying {@link AppDb}.
//     *
//     * @return The {@link Single} object that can be used to observe the number of rows in the {@link AppDb#TABLE_NAME_ASSESSMENTS "assessments"} data table within the underlying {@link AppDb}.
//     */
//    @NonNull
//    public Single<Integer> getAssessmentCount() {
//        Log.d(LOG_TAG, "Called getAssessmentCount()");
//        return appDb.assessmentDAO().getCount().subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread());
//    }

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
    public Single<ValidationMessage.ResourceMessageResult> saveAssessment(AssessmentEntity entity, boolean ignoreWarnings) {
        Log.d(LOG_TAG, String.format("Called saveAssessment(%s)", entity));
        return Single.fromCallable(() -> {
            final ValidationMessage.ResourceMessageBuilder builder = new ValidationMessage.ResourceMessageBuilder();
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
                if (builder.hasError() || (!ignoreWarnings && builder.hasWarning())) {
                    return builder.build();
                }
                entity.setId(dao.insertSynchronous(entity));
                return builder.build();
            }
            if (list.stream().anyMatch(t -> t.getCode().equals(code) && id != t.getId())) {
                builder.acceptWarning(R.string.message_assessment_duplicate_code);
            }

            if (builder.hasError() || (!ignoreWarnings && builder.hasWarning())) {
                return builder.build();
            }
            dao.updateSynchronous(entity);
            return builder.build();
        }).subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread());
    }

    public Single<ValidationMessage.ResourceMessageResult> updateAlert(AlertEntity entity, boolean ignoreWarnings) {
        Log.d(LOG_TAG, String.format("Called saveAlert(%s)", entity));
        return Single.fromCallable(() -> {
            final ValidationMessage.ResourceMessageBuilder builder = new ValidationMessage.ResourceMessageBuilder();
            if (entity.getId() == ID_NEW) {
                builder.acceptError(R.string.message_alert_not_inserted);
                return builder.build();
            }
            Alert.validate(builder, entity);
            if (builder.hasError() || (!ignoreWarnings && builder.hasWarning())) {
                return builder.build();
            }
            appDb.alertDAO().update(entity);
            return builder.build();
        }).subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread());
    }

    public Single<ValidationMessage.ResourceMessageResult> insertCourseAlert(CourseAlert entity, boolean ignoreWarnings) {
        Log.d(LOG_TAG, String.format("Called insertCourseAlert(%s)", entity));
        return Single.fromCallable(() -> {
            final ValidationMessage.ResourceMessageBuilder builder = new ValidationMessage.ResourceMessageBuilder();
            AlertEntity alert = entity.getAlert();
            if (alert.getId() != ID_NEW) {
                builder.acceptError(R.string.message_alert_already_inserted);
                return builder.build();
            }
            AlertLink.validate(builder, entity);
            if (builder.hasError() || (!ignoreWarnings && builder.hasWarning())) {
                return builder.build();
            }
            appDb.runInTransaction(() -> {
                long id = appDb.alertDAO().insertSynchronous(alert);
                CourseAlertLink link = entity.getLink();
                link.setAlertId(id);
                appDb.courseAlertDAO().insertSynchronous(link);
                alert.setId(id);
            });
            return builder.build();
        }).doOnError(e -> entity.getLink().setAlertId(ID_NEW)).subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread());
    }

    public Single<ValidationMessage.ResourceMessageResult> insertAssessmentAlert(AssessmentAlert entity, boolean ignoreWarnings) {
        Log.d(LOG_TAG, String.format("Called insertAssessmentAlert(%s)", entity));
        return Single.fromCallable(() -> {
            final ValidationMessage.ResourceMessageBuilder builder = new ValidationMessage.ResourceMessageBuilder();
            AlertEntity alert = entity.getAlert();
            if (alert.getId() != ID_NEW) {
                builder.acceptError(R.string.message_alert_already_inserted);
                return builder.build();
            }
            AlertLink.validate(builder, entity);
            if (builder.hasError() || (!ignoreWarnings && builder.hasWarning())) {
                return builder.build();
            }
            appDb.runInTransaction(() -> {
                long id = appDb.alertDAO().insertSynchronous(alert);
                AssessmentAlertLink link = entity.getLink();
                link.setAlertId(id);
                appDb.assessmentAlertDAO().insertSynchronous(link);
                alert.setId(id);
            });
            return builder.build();
        }).doOnError(e -> entity.getLink().setAlertId(ID_NEW)).subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread());
    }

//    /**
//     * Asynchronously inserts a {@link List} of @link AssessmentEntity} objects into the {@link AppDb#TABLE_NAME_COURSES "courses"} data table of the underlying {@link AppDb}.
//     *
//     * @param list The {@link List} of @link AssessmentEntity} objects to be inserted into the {@link AppDb#TABLE_NAME_ASSESSMENTS "assessments"} data table.
//     * @return The {@link Completable} that can be observed for DB operation completion status.
//     */
//    @NonNull
//    public Completable insertAllAssessments(List<AssessmentEntity> list) {
//        return Completable.fromSingle(appDb.assessmentDAO().insertAll(list).subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread())
//                .doAfterSuccess(ids -> applyInsertedIds(ids, list, AbstractEntity::setId)));
//    }

    @NonNull
    public LiveData<List<AlertListItem>> getActiveAlertsOnDate(LocalDate date) {
        return appDb.alertDAO().getActiveOnDate(date);
    }

    @NonNull
    public LiveData<List<AlertListItem>> getActiveAlertsAfterDate(LocalDate date) {
        return appDb.alertDAO().getActiveAfterDate(date);
    }

    @NonNull
    public LiveData<List<AlertListItem>> getActiveAlertsBeforeDate(LocalDate date) {
        return appDb.alertDAO().getActiveBeforeDate(date);
    }

    @NonNull
    public Single<CourseAlertDetails> getCourseAlertDetailsId(long alertId, long courseId) {
        return appDb.courseAlertDAO().getDetailsAlertId(alertId, courseId);
    }

    @NonNull
    public Single<AssessmentAlertDetails> getAssessmentAlertDetailsId(long alertId, long courseId) {
        return appDb.assessmentAlertDAO().getByDetailByAlertId(alertId, courseId);
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
