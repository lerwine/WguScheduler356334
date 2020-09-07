package Erwine.Leonard.T.wguscheduler356334.db;

import android.content.Context;
import android.content.res.Resources;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import Erwine.Leonard.T.wguscheduler356334.R;
import Erwine.Leonard.T.wguscheduler356334.entity.AssessmentEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.AssessmentStatus;
import Erwine.Leonard.T.wguscheduler356334.entity.AssessmentType;
import Erwine.Leonard.T.wguscheduler356334.entity.CourseEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.CourseStatus;
import Erwine.Leonard.T.wguscheduler356334.entity.EmailAddressEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.MentorEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.PhoneNumberEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.TermEntity;
import Erwine.Leonard.T.wguscheduler356334.util.StringHelper;
import Erwine.Leonard.T.wguscheduler356334.util.StringLineIterator;
import io.reactivex.Completable;
import io.reactivex.Scheduler;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class DbLoader {

    //<editor-fold defaultstate="collapsed" desc="Fields">

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
    private final TempDb tempDb;
    private final Scheduler scheduler;
    private LiveData<List<TermEntity>> allTerms;
    private LiveData<List<MentorEntity>> allMentors;
    private LiveData<List<CourseEntity>> allCourses;
    private LiveData<List<AssessmentEntity>> allAssessments;
    private Executor dataExecutor = Executors.newSingleThreadExecutor();

    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Constructors">

    public DbLoader(Context context) {
        appDb = AppDb.getInstance(context);
        tempDb = TempDb.getInstance(context);
        scheduler = Schedulers.from(dataExecutor);
    }

    public static DbLoader getInstance(Context context) {
        if (null == instance) {
            instance = new DbLoader(context);
        }

        return instance;
    }

    //</editor-fold>

    private static <T> void applyInsertedIds(List<Long> ids, List<T> entities, BiConsumer<T, Long> setId) {
        for (int n = 0; n < ids.size() && n < entities.size(); n++) {
            Long i = ids.get(n);
            if (null != i) {
                setId.accept(entities.get(n), i);
            }
        }
    }

    //<editor-fold defaultstate="collapsed" desc="TermEntity methods">

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

    private static <T> List<T> createSampleDataObjectsOld(Resources resources, int resourceId, int expectedCellCount, BiFunction<List<String>, Integer, T> factory) {
        return Arrays.stream(resources.getStringArray(resourceId)).map(new Function<String, T>() {
            private int id = 0;

            @Override
            public T apply(String s) {
                return factory.apply(parseSampleDataCells(s, expectedCellCount), ++id);
            }
        }).collect(Collectors.toList());
    }

    public Completable deleteTerm(TermEntity entity) {
        return appDb.termDAO().delete(entity).subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread());
    }

    public Single<TermEntity> getTermByRowId(int rowId) {
        return appDb.termDAO().getByRowId(rowId).subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread());
    }

    public Single<TermEntity> getTermById(int id) {
        return appDb.termDAO().getById(id).subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread());
    }

    public LiveData<List<TermEntity>> getAllTerms() {
        if (null == allTerms) {
            allTerms = appDb.termDAO().getAll();
        }
        return allTerms;
    }

    public Single<Integer> getTermCount(LiveData<Integer> liveData) {
        return appDb.termDAO().getCount().subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread());
    }

    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="MentorEntity methods">

    private static <T> HashMap<Integer, T> createSampleDataObjects(Resources resources, int resourceId, int expectedCellCount, Function<List<String>, T> factory) {
        final HashMap<Integer, T> result = new HashMap<>();
        Arrays.stream(resources.getStringArray(resourceId)).forEach(new Consumer<String>() {
            private int id = 0;

            @Override
            public void accept(String s) {
                result.put(++id, factory.apply(parseSampleDataCells(s, expectedCellCount)));
            }
        });
        return result;
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

    public Completable saveTerm(TermEntity entity) {
        if (null == entity.getId()) {
            return Completable.fromSingle(appDb.termDAO().insert(entity).subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread())
                    .doAfterSuccess(id -> TermEntity.applyInsertedId(entity, id)));
        }
        return appDb.termDAO().update(entity).subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread());
    }

    public Single<MentorEntity> getMentorByRowId(int rowId) {
        return appDb.mentorDAO().getByRowId(rowId).subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread());
    }

    public Completable insertAllTerms(List<TermEntity> list) {
        return Completable.fromSingle(appDb.termDAO().insertAll(list).subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread())
                .doAfterSuccess(ids -> applyInsertedIds(ids, list, TermEntity::applyInsertedId)));
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

    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="PhoneNumberEntity methods">

//    public LiveData<List<PhoneNumberEntity>> getPhoneNumbers(int mentorId) { return tempDb.phoneNumberDAO().getAll(); }

    public Completable saveMentor(MentorEntity entity, boolean doNotUseTempDb) {
        if (doNotUseTempDb) {
            if (null == entity.getId()) {
                return Completable.fromSingle(appDb.mentorDAO().insert(entity).subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread())
                        .doAfterSuccess(id -> MentorEntity.applyInsertedId(entity, id)));
            }
            return appDb.mentorDAO().update(entity).subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread());
        }
        return Completable.fromAction(() -> {
            entity.setPhoneNumbers(tempDb.phoneNumberDAO().getAllSynchronous().stream().sorted().map(PhoneNumberEntity::getValue).filter(t -> !t.isEmpty())
                    .collect(Collectors.joining("\n")));
            entity.setEmailAddresses(tempDb.emailAddressDAO().getAllSynchronous().stream().sorted().map(EmailAddressEntity::getValue).filter(t -> !t.isEmpty())
                    .collect(Collectors.joining("\n")));
            if (null == entity.getId()) {
                long id = appDb.mentorDAO().insertSynchronous(entity);
                MentorEntity.applyInsertedId(entity, id);
            } else {
                appDb.mentorDAO().updateSynchronous(entity);
            }
        }).subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread());
    }

    public Completable insertAllMentors(List<MentorEntity> list) {
        return Completable.fromSingle(appDb.mentorDAO().insertAll(list).subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread())
                .doAfterSuccess(ids -> applyInsertedIds(ids, list, MentorEntity::applyInsertedId)));
    }

    public Completable deletePhoneNumber(PhoneNumberEntity entity) {
        return tempDb.phoneNumberDAO().delete(entity).subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread());
    }

    public Completable deleteMentor(MentorEntity entity, boolean clearTempDbData) {
        if (clearTempDbData) {
            return Completable.fromAction(() -> {
                appDb.mentorDAO().deleteSynchronous(entity);
                tempDb.emailAddressDAO().deleteAllSynchronous();
                tempDb.phoneNumberDAO().deleteAllSynchronous();
            }).subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread());
        }
        return appDb.mentorDAO().delete(entity).subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread());
    }

//    public LiveData<List<PhoneNumberEntity>> getPhoneNumbersByMentorId(int mentorId) {
//        return tempDb.phoneNumberDAO().getByMentorId(mentorId);
//    }

    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="EmailAddressEntity methods">

//    public LiveData<List<EmailAddressEntity>> getEmailAddressesByMentorId(int mentorId) {
//        return tempDb.emailAddressDAO().getByMentorId(mentorId);
//    }

    public Single<MentorEntity> getMentorById(long id, boolean setTempDbData) {
        if (setTempDbData) {
            return Single.fromCallable(() -> {
                MentorEntity entity = appDb.mentorDAO().getByIdSynchronous(id);
                tempDb.emailAddressDAO().deleteAllSynchronous();
                tempDb.phoneNumberDAO().deleteAllSynchronous();
                if (null != entity) {
                    String phoneNumbers = entity.getPhoneNumbers();
                    if (!phoneNumbers.trim().isEmpty()) {
                        List<PhoneNumberEntity> list = StringLineIterator.getLines(phoneNumbers).filter(t -> !t.isEmpty()).map(new Function<String, PhoneNumberEntity>() {
                            int order = -1;

                            @Override
                            public PhoneNumberEntity apply(String t) {
                                return new PhoneNumberEntity(t, ++order);
                            }
                        }).collect(Collectors.toList());
                        if (!list.isEmpty()) {
                            tempDb.phoneNumberDAO().insertAllSynchronous(list);
                        }
                    }
                    String emailAddresses = entity.getEmailAddresses();
                    if (emailAddresses.isEmpty()) {
                        return entity;
                    }
                    List<EmailAddressEntity> emailAddressEntities = StringLineIterator.getLines(emailAddresses).filter(t -> !t.isEmpty()).map(new Function<String, EmailAddressEntity>() {
                        int order = -1;

                        @Override
                        public EmailAddressEntity apply(String t) {
                            return new EmailAddressEntity(t, ++order);
                        }
                    }).collect(Collectors.toList());
                    if (!emailAddressEntities.isEmpty()) {
                        applyInsertedIds(tempDb.emailAddressDAO().insertAllSynchronous(emailAddressEntities), emailAddressEntities, EmailAddressEntity::applyInsertedId);
                    }
                }
                return entity;
            });
        }
        return appDb.mentorDAO().getById(id).subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread());
    }

    public LiveData<List<PhoneNumberEntity>> getPhoneNumbers() {
        return tempDb.phoneNumberDAO().getAll();
    }

    public Completable deleteEmailAddress(EmailAddressEntity entity) {
        return tempDb.emailAddressDAO().delete(entity).subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread());
    }

    public Completable savePhoneNumber(PhoneNumberEntity entity) {
        if (null == entity.getId()) {
            return Completable.fromSingle(tempDb.phoneNumberDAO().insert(entity).subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread())
                    .doAfterSuccess(id -> PhoneNumberEntity.applyInsertedId(entity, id)));
        }
        return tempDb.phoneNumberDAO().update(entity).subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread());
    }

    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="CourseEntity methods">

    public Completable setPhoneNumbersFromMultiLineString(String phoneNumbers) {
        return Completable.fromAction(() -> {
            tempDb.phoneNumberDAO().deleteAllSynchronous();
            if (null == phoneNumbers || phoneNumbers.trim().isEmpty()) {
                return;
            }
            List<PhoneNumberEntity> list = StringLineIterator.getLines(phoneNumbers).filter(t -> !t.isEmpty()).map(new Function<String, PhoneNumberEntity>() {
                int order = -1;

                @Override
                public PhoneNumberEntity apply(String t) {
                    return new PhoneNumberEntity(t, ++order);
                }
            }).collect(Collectors.toList());
            if (!list.isEmpty()) {
                tempDb.phoneNumberDAO().insertAllSynchronous(list);
            }
        }).subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread());
    }

    public LiveData<List<EmailAddressEntity>> getEmailAddresses() {
        return tempDb.emailAddressDAO().getAll();
    }

    public Completable deleteCourse(CourseEntity entity) {
        return appDb.courseDAO().delete(entity).subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread());
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

    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="AssessmentEntity methods">

    public Completable saveEmailAddress(EmailAddressEntity entity) {
        if (null == entity.getId()) {
            return Completable.fromSingle(tempDb.emailAddressDAO().insert(entity).subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread())
                    .doAfterSuccess(id -> EmailAddressEntity.applyInsertedId(entity, id)));
        }
        return tempDb.emailAddressDAO().update(entity).subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread());
    }

    public Completable setEmailAddressesFromMultiLineString(String emailAddresses) {
        return Completable.fromAction(() -> {
            tempDb.emailAddressDAO().deleteAllSynchronous();
            if (null == emailAddresses || emailAddresses.trim().isEmpty()) {
                return;
            }
            List<EmailAddressEntity> list = StringLineIterator.getLines(emailAddresses).filter(t -> !t.isEmpty()).map(new Function<String, EmailAddressEntity>() {
                int order = -1;

                @Override
                public EmailAddressEntity apply(String t) {
                    return new EmailAddressEntity(t, ++order);
                }
            }).collect(Collectors.toList());
            if (!list.isEmpty()) {
                tempDb.emailAddressDAO().insertAllSynchronous(list);
            }
        }).subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread());
    }

    public Completable deleteAssessment(AssessmentEntity entity) {
        return appDb.assessmentDAO().delete(entity).subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread());
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

    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="resetDatabase">

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

    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="populateSampleData">

    public Completable saveCourse(CourseEntity entity) {
        if (null == entity.getId()) {
            return Completable.fromSingle(appDb.courseDAO().insert(entity).subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread())
                    .doAfterSuccess(id -> CourseEntity.applyInsertedId(entity, id)));
        }
        return appDb.courseDAO().update(entity).subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread());
    }

    public Completable insertAllCourses(List<CourseEntity> list) {
        return Completable.fromSingle(appDb.courseDAO().insertAll(list).subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread())
                .doAfterSuccess(ids -> applyInsertedIds(ids, list, CourseEntity::applyInsertedId)));
    }

    public Completable saveAssessment(AssessmentEntity entity) {
        if (null == entity.getId()) {
            return Completable.fromSingle(appDb.assessmentDAO().insert(entity).subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread())
                    .doAfterSuccess(id -> AssessmentEntity.applyInsertedId(entity, id)));
        }
        return appDb.assessmentDAO().update(entity).subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread());
    }

    public Completable insertAllAssessments(List<AssessmentEntity> list) {
        return Completable.fromSingle(appDb.assessmentDAO().insertAll(list).subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread())
                .doAfterSuccess(ids -> applyInsertedIds(ids, list, AssessmentEntity::applyInsertedId)));
    }

    @NonNull
    private HashMap<Integer, Long> createSampleTerms(Resources resources) {
        List<TermEntity> entities = new ArrayList<>();
        for (String csv : resources.getStringArray(R.array.sample_terms)) {
            List<String> cells = parseSampleDataCells(csv, 4);
            entities.add(new TermEntity(cells.get(0), LocalDate.parse(cells.get(1)), LocalDate.parse(cells.get(2)), cells.get(3)));
        }
        List<Long> ids = appDb.termDAO().insertAllSynchronous(entities);
        HashMap<Integer, Long> result = new HashMap<>();
        int index = 0;
        while (index < ids.size()) {
            long id = ids.get(index);
            TermEntity.applyInsertedId(entities.get(index), id);
            result.put(++index, id);
        }
        return result;
    }

    @NonNull
    private HashMap<Integer, Long> createSampleMentors(Resources resources) {
        List<MentorEntity> entities = new ArrayList<>();
        for (String csv : resources.getStringArray(R.array.sample_mentors)) {
            List<String> cells = parseSampleDataCells(csv, 4);
            entities.add(new MentorEntity(cells.get(0), cells.get(1), cells.get(2), cells.get(3)));
        }
        List<Long> ids = appDb.mentorDAO().insertAllSynchronous(entities);
        HashMap<Integer, Long> result = new HashMap<>();
        int index = 0;
        while (index < ids.size()) {
            long id = ids.get(index);
            MentorEntity.applyInsertedId(entities.get(index), id);
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
            entities.add(new CourseEntity(cells.get(1), cells.get(2), statusMap.get(cells.get(3)), parseDateCell.apply(cells.get(4)), parseDateCell.apply(cells.get(5)),
                    parseDateCell.apply(cells.get(6)), parseDateCell.apply(cells.get(7)), (c.isEmpty()) ? null : Integer.parseInt(c), cells.get(9),
                    Objects.requireNonNull(sampleTerms.get(Integer.parseInt(cells.get(0)))), (m.isEmpty()) ? null : sampleMentors.get(Integer.parseInt(m))));
        }
        List<Long> ids = appDb.courseDAO().insertAllSynchronous(entities);
        HashMap<Integer, CourseEntity> result = new HashMap<>();
        int index = 0;
        while (index < ids.size()) {
            CourseEntity e = entities.get(index);
            CourseEntity.applyInsertedId(e, ids.get(index));
            result.put(++index, e);
        }
        return result;
    }

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
                return new AssessmentEntity(cells.get(1), am.get(cells.get(2)), sampleCellToLocalDate(cells.get(3), course), at.get(cells.get(4)), cells.get(5),
                        sampleCellToLocalDate(cells.get(6), course), course.getId());
            }).collect(Collectors.toList()));
        }).subscribeOn(this.scheduler).observeOn(AndroidSchedulers.mainThread());
    }

    //</editor-fold>

}
