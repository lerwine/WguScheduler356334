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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import Erwine.Leonard.T.wguscheduler356334.R;
import Erwine.Leonard.T.wguscheduler356334.entity.AbstractEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.AssessmentEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.AssessmentStatus;
import Erwine.Leonard.T.wguscheduler356334.entity.AssessmentType;
import Erwine.Leonard.T.wguscheduler356334.entity.CourseEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.CourseStatus;
import Erwine.Leonard.T.wguscheduler356334.entity.MentorCourseListItem;
import Erwine.Leonard.T.wguscheduler356334.entity.MentorEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.MentorListItem;
import Erwine.Leonard.T.wguscheduler356334.entity.TermCourseListItem;
import Erwine.Leonard.T.wguscheduler356334.entity.TermEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.TermListItem;
import Erwine.Leonard.T.wguscheduler356334.util.StringHelper;
import io.reactivex.Completable;
import io.reactivex.Scheduler;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

/**
 * A singleton helper object for database I/O.
 */
public class DbLoader {

    //<editor-fold defaultstate="collapsed" desc="Fields">

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
    private final CompositeDisposable compositeDisposable;
    private final AppDb appDb;
    private final Scheduler scheduler;
    private final Executor dataExecutor;
    private LiveData<List<TermListItem>> allTerms;
    private LiveData<List<MentorListItem>> allMentors;
    private LiveData<List<CourseEntity>> allCourses;
    private LiveData<List<AssessmentEntity>> allAssessments;

    //</editor-fold>

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

    private static <T> void applyInsertedIds(List<Long> ids, List<T> entities, BiConsumer<T, Long> setId) {
        for (int n = 0; n < ids.size() && n < entities.size(); n++) {
            Long i = ids.get(n);
            if (null != i) {
                setId.accept(entities.get(n), i);
            }
        }
    }

    private static ArrayList<String> parseSampleDataCells(String csvText, Integer expectedCellCount) {
        try {
            ArrayList<ArrayList<String>> rows = StringHelper.parseCsv(csvText.trim());
            if (rows.size() != 1)
                throw new RuntimeException(String.format("Expected 1 parsed CSV row; Actual: %d", rows.size()));
            ArrayList<String> r = rows.get(0);
            if (r.size() != expectedCellCount)
                throw new RuntimeException(String.format("Expected %d parsed CSV cells; Actual: %d", expectedCellCount, r.size()));
            return r;
        } catch (RuntimeException ex) {
            throw new RuntimeException(String.format("Error parsing sample data %s", csvText), ex);
        }
    }

    private static LocalDate sampleCellToLocalDate(String t, CourseEntity u) {
        switch (t) {
            case "expectedEnd":
                return u.getExpectedEnd();
            case "actualEnd":
                return u.getActualEnd();
            case "":
                return null;
            default:
                return LocalDate.parse(t);
        }
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

    /**
     * Asynchronously gets a {@link TermEntity} from the {@link AppDb#TABLE_NAME_TERMS "terms"} data table within the underlying {@link AppDb} by its {@code ROWID}.
     *
     * @param rowId The {@code ROWID} of the {@link TermEntity} to retrieve.
     * @return The {@link Single} object that can be used to observe the result {@link TermEntity} object.
     */
    @NonNull
    public Single<TermEntity> getTermByRowId(int rowId) {
        return appDb.termDAO().getByRowId(rowId).subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread());
    }

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

    /**
     * Asynchronously gets the number of rows in the {@link AppDb#TABLE_NAME_TERMS "terms"} data table within the underlying {@link AppDb}.
     *
     * @return The {@link Single} object that can be used to observe the number of rows in the {@link AppDb#TABLE_NAME_TERMS "terms"} data table within the underlying {@link AppDb}.
     */
    @NonNull
    public Single<Integer> getTermCount() {
        Log.d(LOG_TAG, "Called getTermCount()");
        return appDb.termDAO().getCount().subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * Asynchronously save the specified {@link TermEntity} object into the {@link AppDb#TABLE_NAME_TERMS "terms"} data table of the underlying {@link AppDb}.
     * If {@link TermEntity#getId()} is null, then it will be inserted into the {@link AppDb#TABLE_NAME_TERMS "terms"} data table; otherwise, the corresponding table row will be
     * updated. After a new {@link TermEntity} has been successfully inserted, the value returned by {@link TermEntity#getId()} will contain the unique identifier of the
     * newly added row.
     *
     * @param entity The {@link TermEntity} to be saved.
     * @return The {@link Completable} that can be observed for DB operation completion status.
     */
    @NonNull
    public Completable saveTerm(TermEntity entity) {
        Log.d(LOG_TAG, String.format("Called saveTerm(%s)", entity));
        if (null == entity.getId()) {
            return Completable.fromSingle(appDb.termDAO().insert(entity).subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread())
                    .doAfterSuccess(id -> TermEntity.applyInsertedId(entity, id)));
        }
        return appDb.termDAO().update(entity).subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * Asynchronously inserts a {@link List} of @link TermEntity} objects into the {@link AppDb#TABLE_NAME_TERMS "terms"} data table of the underlying {@link AppDb}.
     *
     * @param list The {@link List} of @link TermEntity} objects to be inserted into the {@link AppDb#TABLE_NAME_TERMS "terms"} data table.
     * @return The {@link Completable} that can be observed for DB operation completion status.
     */
    @NonNull
    public Completable insertAllTerms(List<TermEntity> list) {
        return Completable.fromSingle(appDb.termDAO().insertAll(list).subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread())
                .doAfterSuccess(ids -> applyInsertedIds(ids, list, TermEntity::applyInsertedId)));
    }

    /**
     * Asynchronously deletes a {@link TermEntity} from the {@link AppDb#TABLE_NAME_TERMS "terms"} data table of the underlying {@link AppDb}.
     *
     * @param entity The {@link TermEntity} to delete.
     * @return The {@link Completable} that can be observed for DB operation completion status.
     */
    @NonNull
    public Completable deleteTerm(TermEntity entity) {
        Log.d(LOG_TAG, String.format("Called deleteTerm(%s)", entity));
        return appDb.termDAO().delete(entity).subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * Asynchronously gets a {@link MentorEntity} from the {@link AppDb#TABLE_NAME_MENTORS "mentors"} data table within the underlying {@link AppDb} by its {@code ROWID}.
     *
     * @param rowId The {@code ROWID} of the {@link MentorEntity} to retrieve.
     * @return The {@link Single} object that can be used to observe the result {@link MentorEntity} object.
     */
    @NonNull
    public Single<MentorEntity> getMentorByRowId(int rowId) {
        return appDb.mentorDAO().getByRowId(rowId).subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread());
    }

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

    /**
     * Asynchronously gets the number of rows in the {@link AppDb#TABLE_NAME_MENTORS "mentors"} data table within the underlying {@link AppDb}.
     *
     * @return The {@link Single} object that can be used to observe the number of rows in the {@link AppDb#TABLE_NAME_MENTORS "mentors"} data table within the underlying {@link AppDb}.
     */
    @NonNull
    public Single<Integer> getMentorCount() {
        Log.d(LOG_TAG, "Called getMentorCount()");
        return appDb.mentorDAO().getCount().subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread());
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
    public Completable saveMentor(MentorEntity entity) {
        Log.d(LOG_TAG, String.format("Called saveMentor(%s)", entity));
        if (null == entity.getId()) {
            return Completable.fromSingle(appDb.mentorDAO().insert(entity).subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread())
                    .doAfterSuccess(id -> MentorEntity.applyInsertedId(entity, id)));
        }
        return appDb.mentorDAO().update(entity).subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * Asynchronously inserts a {@link List} of @link MentorEntity} objects into the {@link AppDb#TABLE_NAME_MENTORS "mentors"} data table of the underlying {@link AppDb}.
     *
     * @param list The {@link List} of @link TermEntity} objects to be inserted into the {@link AppDb#TABLE_NAME_MENTORS "mentors"} data table.
     * @return The {@link Completable} that can be observed for DB operation completion status.
     */
    @NonNull
    public Completable insertAllMentors(List<MentorEntity> list) {
        return Completable.fromSingle(appDb.mentorDAO().insertAll(list).subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread())
                .doAfterSuccess(ids -> applyInsertedIds(ids, list, MentorEntity::applyInsertedId)));
    }

    /**
     * Asynchronously deletes a {@link MentorEntity} from the {@link AppDb#TABLE_NAME_MENTORS "mentors"} data table of the underlying {@link AppDb}.
     *
     * @param entity The {@link MentorEntity} to delete.
     * @return The {@link Completable} that can be observed for DB operation completion status.
     */
    @NonNull
    public Completable deleteMentor(MentorEntity entity) {
        Log.d(LOG_TAG, String.format("Called deleteMentor(%s)", entity));
        return appDb.mentorDAO().delete(entity).subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread());
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
    public Completable deleteCourse(CourseEntity entity) {
        Log.d(LOG_TAG, String.format("Called deleteCourse(%s)", entity));
        return appDb.courseDAO().delete(entity).subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * Asynchronously gets a {@link CourseEntity} from the {@link AppDb#TABLE_NAME_COURSES "courses"} data table within the underlying {@link AppDb} by its {@code ROWID}.
     *
     * @param rowId The {@code ROWID} of the {@link CourseEntity} to retrieve.
     * @return The {@link Single} object that can be used to observe the result {@link CourseEntity} object.
     */
    @NonNull
    public Single<CourseEntity> getCourseByRowId(int rowId) {
        return appDb.courseDAO().getByRowId(rowId).subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * Asynchronously gets a {@link CourseEntity} from the {@link AppDb#TABLE_NAME_COURSES "courses"} data table within the underlying {@link AppDb} by its unique identifier.
     *
     * @param id The unique identifier of the  {@link CourseEntity} to retrieve.
     * @return The {@link Single} object that can be used to observe the result {@link CourseEntity} object.
     */
    @NonNull
    public Single<CourseEntity> getCourseById(long id) {
        Log.d(LOG_TAG, String.format("Called getCourseById(%d)", id));
        return appDb.courseDAO().getById(id).subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * Gets all rows from the {@link AppDb#TABLE_NAME_COURSES "courses"} data table within the underlying {@link AppDb}.
     *
     * @return A {@link LiveData} object that will contain the list of {@link CourseEntity} objects retrieved from the underlying {@link AppDb}.
     */
    @NonNull
    public LiveData<List<CourseEntity>> getAllCourses() {
        Log.d(LOG_TAG, "Called getAllCourses()");
        if (null == allCourses) {
            allCourses = appDb.courseDAO().getAll();
        }
        return allCourses;
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

    /**
     * Asynchronously gets the number of rows in the {@link AppDb#TABLE_NAME_COURSES "courses"} data table within the underlying {@link AppDb}.
     *
     * @return The {@link Single} object that can be used to observe the number of rows in the {@link AppDb#TABLE_NAME_COURSES "courses"} data table within the underlying {@link AppDb}.
     */
    @NonNull
    public Single<Integer> getCourseCount() {
        Log.d(LOG_TAG, "Called getCourseCount()");
        return appDb.courseDAO().getCount().subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread());
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
    public Completable saveCourse(CourseEntity entity) {
        Log.d(LOG_TAG, String.format("Called saveCourse(%s)", entity));
        if (null == entity.getId()) {
            return Completable.fromSingle(appDb.courseDAO().insert(entity).subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread())
                    .doAfterSuccess(id -> CourseEntity.applyInsertedId(entity, id)));
        }
        return appDb.courseDAO().update(entity).subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * Asynchronously inserts a {@link List} of @link CourseEntity} objects into the {@link AppDb#TABLE_NAME_COURSES "courses"} data table of the underlying {@link AppDb}.
     *
     * @param list The {@link List} of @link CourseEntity} objects to be inserted into the {@link AppDb#TABLE_NAME_COURSES "courses"} data table.
     * @return The {@link Completable} that can be observed for DB operation completion status.
     */
    @NonNull
    public Completable insertAllCourses(List<CourseEntity> list) {
        return Completable.fromSingle(appDb.courseDAO().insertAll(list).subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread())
                .doAfterSuccess(ids -> applyInsertedIds(ids, list, CourseEntity::applyInsertedId)));
    }

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

    /**
     * Asynchronously gets a {@link AssessmentEntity} from the {@link AppDb#TABLE_NAME_ASSESSMENTS "assessments"} data table within the underlying {@link AppDb} by its {@code ROWID}.
     *
     * @param rowId The {@code ROWID} of the {@link AssessmentEntity} to retrieve.
     * @return The {@link Single} object that can be used to observe the result {@link AssessmentEntity} object.
     */
    @NonNull
    public Single<AssessmentEntity> getAssessmentByRowId(int rowId) {
        return appDb.assessmentDAO().getByRowId(rowId).subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * Asynchronously gets a {@link AssessmentEntity} from the {@link AppDb#TABLE_NAME_ASSESSMENTS "assessments"} data table within the underlying {@link AppDb} by its unique identifier.
     *
     * @param id The unique identifier of the  {@link AssessmentEntity} to retrieve.
     * @return The {@link Single} object that can be used to observe the result {@link AssessmentEntity} object.
     */
    @NonNull
    public Single<AssessmentEntity> getAssessmentById(long id) {
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

    /**
     * Gets all rows from the {@link AppDb#TABLE_NAME_ASSESSMENTS "assessments"} data table within the underlying {@link AppDb}.
     *
     * @return A {@link LiveData} object that will contain the list of {@link AssessmentEntity} objects retrieved from the underlying {@link AppDb}.
     */
    @NonNull
    public LiveData<List<AssessmentEntity>> getAllAssessments() {
        Log.d(LOG_TAG, "Called getAllAssessments()");
        if (null == allAssessments) {
            allAssessments = appDb.assessmentDAO().getAll();
        }
        return allAssessments;
    }

    /**
     * Asynchronously gets the number of rows in the {@link AppDb#TABLE_NAME_ASSESSMENTS "assessments"} data table within the underlying {@link AppDb}.
     *
     * @return The {@link Single} object that can be used to observe the number of rows in the {@link AppDb#TABLE_NAME_ASSESSMENTS "assessments"} data table within the underlying {@link AppDb}.
     */
    @NonNull
    public Single<Integer> getAssessmentCount() {
        Log.d(LOG_TAG, "Called getAssessmentCount()");
        return appDb.assessmentDAO().getCount().subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread());
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
    public Completable saveAssessment(AssessmentEntity entity) {
        Log.d(LOG_TAG, String.format("Called saveAssessment(%s)", entity));
        if (null == entity.getId()) {
            return Completable.fromSingle(appDb.assessmentDAO().insert(entity).subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread())
                    .doAfterSuccess(id -> AssessmentEntity.applyInsertedId(entity, id)));
        }
        return appDb.assessmentDAO().update(entity).subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * Asynchronously inserts a {@link List} of @link AssessmentEntity} objects into the {@link AppDb#TABLE_NAME_COURSES "courses"} data table of the underlying {@link AppDb}.
     *
     * @param list The {@link List} of @link AssessmentEntity} objects to be inserted into the {@link AppDb#TABLE_NAME_ASSESSMENTS "assessments"} data table.
     * @return The {@link Completable} that can be observed for DB operation completion status.
     */
    @NonNull
    public Completable insertAllAssessments(List<AssessmentEntity> list) {
        return Completable.fromSingle(appDb.assessmentDAO().insertAll(list).subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread())
                .doAfterSuccess(ids -> applyInsertedIds(ids, list, AssessmentEntity::applyInsertedId)));
    }

    @NonNull
    public Single<String> checkDbIntegrity() {
        return Single.fromCallable(() -> {
            List<Long> termIds;
            List<Long> mentorIds;

            try {
                termIds = appDb.termDAO().getAllSynchronous().stream().map(AbstractEntity::getId).collect(Collectors.toList());
            } catch (Exception e) {
                return "Error accessing database";
            }
            try {
                mentorIds = appDb.mentorDAO().getAllSynchronous().stream().map(AbstractEntity::getId).collect(Collectors.toList());
            } catch (Exception e) {
                return "Error accessing database";
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
                messages.add(String.format(Locale.getDefault(), "%s: Error reading from table.", AppDb.TABLE_NAME_COURSES));
                return String.join("\n", messages);
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
                messages.add(String.format(Locale.getDefault(), "%s: Error reading from table.", AppDb.TABLE_NAME_ASSESSMENTS));
            }
            return (messages.isEmpty()) ? "" : String.join("\n", messages);
        }).subscribeOn(this.scheduler).observeOn(AndroidSchedulers.mainThread());
    }

    private void resetDb() {
        appDb.clearAllTables();
        for (String t : new String[]{
                AppDb.TABLE_NAME_ASSESSMENTS,
                AppDb.TABLE_NAME_COURSES,
                AppDb.TABLE_NAME_TERMS,
                AppDb.TABLE_NAME_MENTORS
        }) {
            appDb.query(String.format("UPDATE sqlite_sequence SET seq = 1 WHERE name = '%s'", t), null).close();
        }
    }

    @NonNull
    public Completable resetDatabase() {
        return Completable.fromAction(this::resetDb).subscribeOn(this.scheduler).observeOn(AndroidSchedulers.mainThread());
    }

    @NonNull
    private HashMap<Integer, Long> createSampleTerms(Resources resources) {
        List<TermEntity> entities = new ArrayList<>();
        for (String csv : resources.getStringArray(R.array.sample_terms)) {
            List<String> cells = parseSampleDataCells(csv, 4);
            TermEntity term = new TermEntity(cells.get(0), LocalDate.parse(cells.get(1)), LocalDate.parse(cells.get(2)), cells.get(3));
            Log.d(LOG_TAG, String.format("Creating: %s", term));
            entities.add(term);
        }
        List<Long> ids = appDb.termDAO().insertAllSynchronous(entities);
        HashMap<Integer, Long> result = new HashMap<>();
        int index = 0;
        while (index < ids.size()) {
            long id = ids.get(index);
            TermEntity term = entities.get(index);
            TermEntity.applyInsertedId(term, id);
            Log.d(LOG_TAG, String.format("ID %d applied to %s", id, term));
            result.put(++index, id);
        }
        return result;
    }

    @NonNull
    private HashMap<Integer, Long> createSampleMentors(Resources resources) {
        List<MentorEntity> entities = new ArrayList<>();
        for (String csv : resources.getStringArray(R.array.sample_mentors)) {
            List<String> cells = parseSampleDataCells(csv, 4);
            MentorEntity mentor = new MentorEntity(cells.get(0), cells.get(1), cells.get(2), cells.get(3));
            Log.d(LOG_TAG, String.format("Creating: %s", mentor));
            entities.add(mentor);
        }
        List<Long> ids = appDb.mentorDAO().insertAllSynchronous(entities);
        HashMap<Integer, Long> result = new HashMap<>();
        int index = 0;
        while (index < ids.size()) {
            long id = ids.get(index);
            MentorEntity mentor = entities.get(index);
            MentorEntity.applyInsertedId(mentor, id);
            Log.d(LOG_TAG, String.format("ID %d applied to %s", id, mentor));
            result.put(++index, id);
        }
        return result;
    }

    @NonNull
    private HashMap<Integer, CourseEntity> createSampleCourses(Resources resources, @NonNull HashMap<Integer, Long> sampleTerms, @NonNull HashMap<Integer, Long> sampleMentors) {
        List<CourseEntity> entities = new ArrayList<>();
        HashMap<String, CourseStatus> statusMap = new HashMap<>();
        for (CourseStatus cs : CourseStatus.values()) {
            statusMap.put(cs.name(), cs);
        }
        Function<String, LocalDate> parseDateCell = t -> (t.isEmpty()) ? null : LocalDate.parse(t);
        for (String csv : resources.getStringArray(R.array.sample_courses)) {
            List<String> cells = parseSampleDataCells(csv, 11);
            String c = cells.get(8);
            String m = cells.get(10);
            CourseEntity course = new CourseEntity(cells.get(1), cells.get(2), statusMap.get(cells.get(3)), parseDateCell.apply(cells.get(4)), parseDateCell.apply(cells.get(5)),
                    parseDateCell.apply(cells.get(6)), parseDateCell.apply(cells.get(7)), (c.isEmpty()) ? 0 : Integer.parseInt(c), cells.get(9),
                    Objects.requireNonNull(sampleTerms.get(Integer.parseInt(cells.get(0)))), (m.isEmpty()) ? null : sampleMentors.get(Integer.parseInt(m)));
            Log.d(LOG_TAG, String.format("Creating: %s", course));
            entities.add(course);
        }
        List<Long> ids = appDb.courseDAO().insertAllSynchronous(entities);
        HashMap<Integer, CourseEntity> result = new HashMap<>();
        int index = 0;
        while (index < ids.size()) {
            CourseEntity e = entities.get(index);
            Long id = ids.get(index);
            CourseEntity.applyInsertedId(e, id);
            Log.d(LOG_TAG, String.format("ID %d applied to %s", id, e));
            result.put(++index, e);
        }
        return result;
    }

    @NonNull
    public Completable populateSampleData(Resources resources) {
        return Completable.fromAction(() -> {
            resetDb();
            HashMap<Integer, Long> sampleTerms = createSampleTerms(resources);
            HashMap<Integer, Long> sampleMentors = createSampleMentors(resources);
            HashMap<Integer, CourseEntity> sampleCourses = createSampleCourses(resources, sampleTerms, sampleMentors);

            AssessmentDAO assessmentDAO = appDb.assessmentDAO();
            HashMap<String, AssessmentStatus> am = new HashMap<>();
            for (AssessmentStatus a : AssessmentStatus.values()) {
                am.put(a.name(), a);
            }
            HashMap<String, AssessmentType> at = new HashMap<>();
            for (AssessmentType a : AssessmentType.values()) {
                at.put(a.name(), a);
            }
            assessmentDAO.insertAllSynchronous(Arrays.stream(resources.getStringArray(R.array.sample_assessments)).map(t -> {
                // 0=courseId, 1=code, 2=status, 3=goalDate, 4=type, 5=notes, 6=evaluationDate
                List<String> cells = parseSampleDataCells(t, 7);
                CourseEntity course = Objects.requireNonNull(sampleCourses.get(Integer.parseInt(cells.get(0))));
                @SuppressWarnings("ConstantConditions") AssessmentEntity assessment = new AssessmentEntity(cells.get(1), am.get(cells.get(2)), sampleCellToLocalDate(cells.get(3), course), at.get(cells.get(4)), cells.get(5),
                        sampleCellToLocalDate(cells.get(6), course), course.getId());
                Log.d(LOG_TAG, String.format("Creating: %s", assessment));
                return assessment;
            }).collect(Collectors.toList()));
        }).subscribeOn(this.scheduler).observeOn(AndroidSchedulers.mainThread());
    }

}
