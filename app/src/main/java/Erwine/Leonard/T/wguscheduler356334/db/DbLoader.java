package Erwine.Leonard.T.wguscheduler356334.db;

import android.content.Context;
import android.content.res.Resources;

import androidx.lifecycle.LiveData;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import Erwine.Leonard.T.wguscheduler356334.R;
import Erwine.Leonard.T.wguscheduler356334.entity.AssessmentEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.AssessmentStatus;
import Erwine.Leonard.T.wguscheduler356334.entity.AssessmentType;
import Erwine.Leonard.T.wguscheduler356334.entity.CourseEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.CourseStatus;
import Erwine.Leonard.T.wguscheduler356334.entity.MentorEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.TermEntity;
import Erwine.Leonard.T.wguscheduler356334.util.StringHelper;
import io.reactivex.Completable;
import io.reactivex.Scheduler;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class DbLoader {

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

    private static DbLoader instance;
    private final AppDb appDb;
    private final Scheduler scheduler;
    private LiveData<List<TermEntity>> allTerms;
    private LiveData<List<MentorEntity>> allMentors;
    private LiveData<List<CourseEntity>> allCourses;
    private LiveData<List<AssessmentEntity>> allAssessments;
    private Executor dataExecutor = Executors.newSingleThreadExecutor();

    public DbLoader(Context context) {
        appDb = AppDb.getInstance(context);
        scheduler = Schedulers.from(dataExecutor);
    }

    public static DbLoader getInstance(Context context) {
        if (null == instance) {
            instance = new DbLoader(context);
        }

        return instance;
    }

    public Completable saveTerm(TermEntity viewModel) {
        if (null == viewModel.getId()) {
            return appDb.termDAO().insert(viewModel).subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread());
        }
        return appDb.termDAO().update(viewModel).subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread());
    }

//    public void insertTerm(TermEntity viewModel) {
//        dataExecutor.execute(() -> appDb.termDAO().insert(viewModel));
//    }

    public Completable insertAllTerms(List<TermEntity> list) {
        return appDb.termDAO().insertAll(list).subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread());
    }

//    public void deleteTerm(TermEntity viewModel) {
//        dataExecutor.execute(() -> {
//            appDb.termDAO().delete(viewModel);
//        });
//    }

    public Completable deleteTerm(TermEntity viewModel) {
        return appDb.termDAO().delete(viewModel).subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread());
    }

    public Single<TermEntity> getTermByRowId(int rowId) {
        return appDb.termDAO().getByRowId(rowId).subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread());
    }

    public Single<TermEntity> getTermById(int id) {
        return appDb.termDAO().getById(id).subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread());
    }

//    public void getTermById(int id, MutableLiveData<TermEntity> liveData) {
//        dataExecutor.execute(() -> {
//            TermEntity entity = appDb.termDAO().getById(id);
//            liveData.postValue(entity);
//        });
//    }

    public LiveData<List<TermEntity>> getAllTerms() {
        if (null == allTerms) {
            allTerms = appDb.termDAO().getAll();
        }
        return allTerms;
    }

//    public void getTermCount(LiveData<Integer> liveData) {
//        dataExecutor.execute(() -> appDb.termDAO().getCount());
//    }

    public Single<Integer> getTermCount(LiveData<Integer> liveData) {
        return appDb.termDAO().getCount().subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread());
    }

    public Completable saveMentor(MentorEntity viewModel) {
        if (null == viewModel.getId()) {
            return appDb.mentorDAO().insert(viewModel).subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread());
        }
        return appDb.mentorDAO().update(viewModel).subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread());
    }

    public Completable insertAllMentors(List<MentorEntity> list) {
        return appDb.mentorDAO().insertAll(list).subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread());
    }

    public Completable deleteMentor(MentorEntity viewModel) {
        return appDb.mentorDAO().delete(viewModel).subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread());
    }

    public Single<MentorEntity> getMentorByRowId(int rowId) {
        return appDb.mentorDAO().getByRowId(rowId).subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread());
    }

    public Single<MentorEntity> getMentorById(int id) {
        return appDb.mentorDAO().getById(id).subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread());
    }

    public LiveData<List<MentorEntity>> getAllMentors() {
        if (null == allMentors) {
            allMentors = appDb.mentorDAO().getAll();
        }
        return allMentors;
    }

    public Single<Integer> getMentorCount() {
        return appDb.mentorDAO().getCount().subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread());
    }

    public Completable insertCourse(CourseEntity viewModel) {
        if (null == viewModel.getId()) {
            return appDb.courseDAO().insert(viewModel).subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread());
        }
        return appDb.courseDAO().update(viewModel).subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread());
    }

    public Completable insertAllCourses(List<CourseEntity> list) {
        return appDb.courseDAO().insertAll(list).subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread());
    }

    public Completable deleteCourse(CourseEntity course) {
        return appDb.courseDAO().delete(course).subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread());
    }

    public Single<CourseEntity> getCourseByRowId(int rowId) {
        return appDb.courseDAO().getByRowId(rowId).subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread());
    }

    public Single<CourseEntity> getCourseById(int id) {
        return appDb.courseDAO().getById(id).subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread());
    }

    public LiveData<List<CourseEntity>> getAllCourses() {
        if (null == allCourses) {
            allCourses = appDb.courseDAO().getAll();
        }
        return allCourses;
    }

    public LiveData<List<CourseEntity>> getCoursesByTermId(int termId) {
        return appDb.courseDAO().getByTermId(termId);
    }

    public LiveData<List<CourseEntity>> getCoursesByMentorId(int mentorId) {
        return appDb.courseDAO().getByMentorId(mentorId);
    }

    public LiveData<List<CourseEntity>> getUnterminatedCoursesOnOrBefore(LocalDate date) {
        return appDb.courseDAO().getUnterminatedOnOrBefore(date);
    }

    public Single<Integer> getCourseCount() {
        return appDb.courseDAO().getCount().subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread());
    }

    public Completable saveAssessment(AssessmentEntity viewModel) {
        if (null == viewModel.getId()) {
            return appDb.assessmentDAO().insert(viewModel).subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread());
        }
        return appDb.assessmentDAO().update(viewModel).subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread());
    }

    public Completable insertAllAssessments(List<AssessmentEntity> list) {
        return appDb.assessmentDAO().insertAll(list).subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread());
    }

    public Completable deleteAssessment(AssessmentEntity course) {
        return appDb.assessmentDAO().delete(course).subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread());
    }

    public Single<AssessmentEntity> getAssessmentByRowId(int rowId) {
        return appDb.assessmentDAO().getByRowId(rowId).subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread());
    }

    public Single<AssessmentEntity> getAssessmentById(int id) {
        return appDb.assessmentDAO().getById(id).subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread());
    }

    public LiveData<List<AssessmentEntity>> getAssessmentsByCourseId(int courseId) {
        return appDb.assessmentDAO().getByCourseId(courseId);
    }

    public LiveData<List<AssessmentEntity>> getAllAssessments() {
        if (null == allAssessments) {
            allAssessments = appDb.assessmentDAO().getAll();
        }
        return allAssessments;
    }

    public Single<Integer> getAssessmentCount() {
        return appDb.assessmentDAO().getCount().subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread());
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

    public Completable resetDatabase() {
        return Completable.fromAction(this::resetDb).subscribeOn(this.scheduler).observeOn(AndroidSchedulers.mainThread());
    }

    public Completable populateSampleData(Resources resources) {
        return Completable.fromAction(() -> {
            resetDb();
            TermDAO termDAO = appDb.termDAO();
            BiFunction<String, Integer, ArrayList<String>> parseSampleDataCells = (t, u) -> {
                try {
                    ArrayList<ArrayList<String>> rows = StringHelper.parseCsv(t.trim());
                    if (rows.size() != 1)
                        throw new RuntimeException(String.format("Expected 1 parsed CSV row; Actual: %d", rows.size()));
                    ArrayList<String> r = rows.get(0);
                    if (r.size() != u)
                        throw new RuntimeException(String.format("Expected %d parsed CSV cells; Actual: %d", u, r.size()));
                    return r;
                } catch (RuntimeException ex) {
                    throw new RuntimeException(String.format("Error parsing sample data %s", t), ex);
                }
            };
            termDAO.insertAllItems(Arrays.stream(resources.getStringArray(R.array.sample_terms)).map(new Function<String, TermEntity>() {
                private int id = 0;

                @Override
                public TermEntity apply(String s) {
                    List<String> cells = parseSampleDataCells.apply(s, 4);
                    return new TermEntity(cells.get(0), LocalDate.parse(cells.get(1)), LocalDate.parse(cells.get(2)), cells.get(3), ++id);
                }
            }).collect(Collectors.toList()));
            MentorDAO mentorDAO = appDb.mentorDAO();
            mentorDAO.insertAllItems(Arrays.stream(resources.getStringArray(R.array.sample_mentors)).map(new Function<String, MentorEntity>() {
                private int id = 0;

                @Override
                public MentorEntity apply(String s) {
                    List<String> cells = parseSampleDataCells.apply(s, 4);
                    return new MentorEntity(cells.get(0), cells.get(1), cells.get(2), cells.get(3), ++id);
                }
            }).collect(Collectors.toList()));

            CourseDAO courseDAO = appDb.courseDAO();
            HashMap<String, CourseStatus> statusMap = new HashMap<>();
            for (CourseStatus cs : CourseStatus.values()) {
                statusMap.put(cs.name(), cs);
            }
            Function<String, LocalDate> parseDateCell = t -> (t.isEmpty()) ? null : LocalDate.parse(t);
            HashMap<Integer, CourseEntity> courseMap = new HashMap<>();
            courseDAO.insertAllItems(Arrays.stream(resources.getStringArray(R.array.sample_courses)).map(new Function<String, CourseEntity>() {
                private int id = 0;

                @Override
                public CourseEntity apply(String s) {
                    List<String> cells = parseSampleDataCells.apply(s, 11);
                    // 0=termId, 1=number, 2=title, 3=status, 4=expectedStart, 5=actualStart, 6=expectedEnd, 7=actualEnd, 8=competencyUnits, 9=notes, 10=mentorId
                    String n = cells.get(1);
                    String c = cells.get(8);
                    String m = cells.get(10);
                    CourseEntity e = new CourseEntity(n, cells.get(2), statusMap.get(cells.get(3)), parseDateCell.apply(cells.get(4)), parseDateCell.apply(cells.get(5)),
                            parseDateCell.apply(cells.get(6)), parseDateCell.apply(cells.get(7)), (c.isEmpty()) ? null : Integer.parseInt(c), cells.get(9),
                            Integer.parseInt(cells.get(0)), (m.isEmpty()) ? null : Integer.parseInt(m), ++id);
                    courseMap.put(id, e);
                    return e;
                }
            }).collect(Collectors.toList()));
            AssessmentDAO assessmentDAO = appDb.assessmentDAO();
            HashMap<String, AssessmentStatus> am = new HashMap<>();
            for (AssessmentStatus a : AssessmentStatus.values()) {
                am.put(a.name(), a);
            }
            HashMap<String, AssessmentType> at = new HashMap<>();
            for (AssessmentType a : AssessmentType.values()) {
                at.put(a.name(), a);
            }
            BiFunction<String, CourseEntity, LocalDate> convertDateCell = (t, u) -> {
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
            };
            assessmentDAO.insertAllItems(Arrays.stream(resources.getStringArray(R.array.sample_assessments)).map(t -> {
                // 0=courseId, 1=code, 2=status, 3=goalDate, 4=type, 5=notes, 6=evaluationDate
                List<String> cells = parseSampleDataCells.apply(t, 7);
                int courseId = Integer.parseInt(cells.get(0));
                CourseEntity course = Objects.requireNonNull(courseMap.get(courseId));
                return new AssessmentEntity(cells.get(1), am.get(cells.get(2)), convertDateCell.apply(cells.get(3), course), at.get(cells.get(4)), cells.get(5),
                        convertDateCell.apply(cells.get(6), course), course.getId());
            }).collect(Collectors.toList()));
        }).subscribeOn(this.scheduler).observeOn(AndroidSchedulers.mainThread());
    }

}
