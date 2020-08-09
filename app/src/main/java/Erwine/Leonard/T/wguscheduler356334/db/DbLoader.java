package Erwine.Leonard.T.wguscheduler356334.db;

import android.content.Context;

import androidx.lifecycle.LiveData;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import Erwine.Leonard.T.wguscheduler356334.entity.AssessmentEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.CourseEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.MentorEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.TermEntity;
import io.reactivex.Completable;
import io.reactivex.Scheduler;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class DbLoader {
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
        scheduler = Schedulers.single();
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

}
