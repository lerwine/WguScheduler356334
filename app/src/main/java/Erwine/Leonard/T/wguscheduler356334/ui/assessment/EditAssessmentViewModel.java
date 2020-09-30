package Erwine.Leonard.T.wguscheduler356334.ui.assessment;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

import Erwine.Leonard.T.wguscheduler356334.AddAssessmentActivity;
import Erwine.Leonard.T.wguscheduler356334.R;
import Erwine.Leonard.T.wguscheduler356334.ViewAssessmentActivity;
import Erwine.Leonard.T.wguscheduler356334.db.AppDb;
import Erwine.Leonard.T.wguscheduler356334.db.AssessmentStatusConverter;
import Erwine.Leonard.T.wguscheduler356334.db.DbLoader;
import Erwine.Leonard.T.wguscheduler356334.entity.IdIndexedEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.assessment.AbstractAssessmentEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.assessment.Assessment;
import Erwine.Leonard.T.wguscheduler356334.entity.assessment.AssessmentDetails;
import Erwine.Leonard.T.wguscheduler356334.entity.assessment.AssessmentEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.assessment.AssessmentStatus;
import Erwine.Leonard.T.wguscheduler356334.entity.assessment.AssessmentType;
import Erwine.Leonard.T.wguscheduler356334.entity.course.AbstractCourseEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.course.Course;
import Erwine.Leonard.T.wguscheduler356334.entity.course.TermCourseListItem;
import Erwine.Leonard.T.wguscheduler356334.entity.term.AbstractTermEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.term.Term;
import Erwine.Leonard.T.wguscheduler356334.entity.term.TermListItem;
import Erwine.Leonard.T.wguscheduler356334.util.EntityHelper;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class EditAssessmentViewModel extends AndroidViewModel {
    private static final String LOG_TAG = EditAssessmentViewModel.class.getName();
    static final String STATE_KEY_STATE_INITIALIZED = "state_initialized";

    private final DbLoader dbLoader;
    private AssessmentDetails assessmentEntity;
    private final MutableLiveData<AssessmentDetails> entityLiveData;
    private final MutableLiveData<String> titleLiveData;
    private final LiveData<List<TermListItem>> termsLiveData;
    private final MutableLiveData<Boolean> courseValidLiveData;
    private final MutableLiveData<Boolean> codeValidLiveData;
    private final CurrentValues currentValues;
    private final ArrayList<AssessmentEntity> assessmentsForCourse;
    private final ArrayList<TermCourseListItem> coursesForTerm;
    private LiveData<List<TermCourseListItem>> coursesLiveData;
    private LiveData<List<AssessmentEntity>> assessmentsLiveData;
    private AbstractCourseEntity<?> selectedCourse;
    private AbstractTermEntity<?> selectedTerm;
    private boolean fromInitializedState;
    private String normalizedCode = "";
    private String normalizedNotes = "";
    private Observer<List<TermListItem>> termsLoadedObserver;
    private Observer<List<TermCourseListItem>> coursesLoadedObserver;
    private Observer<List<AssessmentEntity>> assessmentsLoadedObserver;

    // TODO: Call this to add a new assessment
    public static void startAddAssessmentActivity(@NonNull Context context, long courseId, @Nullable LocalDate goalDate) {
        Intent intent = new Intent(context, AddAssessmentActivity.class);
        intent.putExtra(IdIndexedEntity.stateKey(AppDb.TABLE_NAME_COURSES, Term.COLNAME_ID, false), courseId);
        if (null != goalDate) {
            intent.putExtra(AssessmentDetails.COLNAME_GOAL_DATE, goalDate.toEpochDay());
        }
        context.startActivity(intent);
    }

    // TODO: Call this to view an assessment
    public static void startViewAssessmentActivity(@NonNull Context context, long assessmentId) {
        Intent intent = new Intent(context, ViewAssessmentActivity.class);
        intent.putExtra(IdIndexedEntity.stateKey(AppDb.TABLE_NAME_ASSESSMENTS, Course.COLNAME_ID, false), assessmentId);
        context.startActivity(intent);
    }

    public EditAssessmentViewModel(@NonNull Application application) {
        super(application);
        dbLoader = DbLoader.getInstance(getApplication());
        titleLiveData = new MutableLiveData<>("");
        termsLiveData = dbLoader.getAllTerms();
        currentValues = new CurrentValues();
        entityLiveData = new MutableLiveData<>();
        courseValidLiveData = new MutableLiveData<>(false);
        codeValidLiveData = new MutableLiveData<>(false);
        assessmentsForCourse = new ArrayList<>();
        coursesForTerm = new ArrayList<>();
    }

    public LiveData<AssessmentDetails> getEntityLiveData() {
        return entityLiveData;
    }

    public LiveData<String> getTitleLiveData() {
        return titleLiveData;
    }

    public Long getId() {
        return currentValues.getId();
    }

    public AbstractTermEntity<?> getSelectedTerm() {
        return selectedTerm;
    }

    public AbstractCourseEntity<?> getSelectedCourse() {
        return selectedCourse;
    }

    public synchronized void setSelectedCourse(AbstractCourseEntity<?> selectedCourse) {
        AbstractCourseEntity<?> oldCourse = this.selectedCourse;
        if (!Objects.equals(oldCourse, selectedCourse)) {
            this.selectedCourse = selectedCourse;
            assessmentsForCourse.clear();
            if (null != assessmentsLiveData) {
                assessmentsLiveData.removeObserver(assessmentsLoadedObserver);
                assessmentsLiveData = null;
            }
            Long id;
            if (null == selectedCourse || null == (id = selectedCourse.getTermId())) {
                coursesForTerm.clear();
                if (null != coursesLiveData) {
                    coursesLiveData.removeObserver(coursesLoadedObserver);
                    coursesLiveData = null;
                }
            } else {
                if (null == oldCourse || !Objects.equals(oldCourse.getTermId(), id)) {
                    coursesForTerm.clear();
                    if (null != coursesLiveData) {
                        coursesLiveData.removeObserver(coursesLoadedObserver);
                        coursesLiveData = null;
                    }
                    coursesLiveData = dbLoader.getCoursesByTermId(id);
                    coursesLoadedObserver = this::onTermCoursesLoaded;
                    coursesLiveData.observeForever(coursesLoadedObserver);
                }
                id = selectedCourse.getId();
                currentValues.courseId = id;
                if (null != id) {
                    assessmentsLiveData = dbLoader.getAssessmentsByCourseId(id);
                    assessmentsLoadedObserver = this::onAllAssessmentsLoaded;
                    assessmentsLiveData.observeForever(assessmentsLoadedObserver);
                    courseValidLiveData.postValue(true);
                } else {
                    courseValidLiveData.postValue(false);
                }
            }
        }
    }

    @NonNull
    public String getCode() {
        return currentValues.getCode();
    }

    public void setCode(String code) {
        currentValues.setCode(code);
    }

    @NonNull
    public String getName() {
        String name = currentValues.getName();
        return (null == name) ? "" : name;
    }

    public void setName(String name) {
        currentValues.setName(name);
    }

    @NonNull
    public AssessmentStatus getStatus() {
        return currentValues.status;
    }

    public void setStatus(AssessmentStatus status) {
        currentValues.setStatus(status);
    }

    @Nullable
    public LocalDate getGoalDate() {
        return currentValues.goalDate;
    }

    public void setGoalDate(LocalDate goalDate) {
        currentValues.setGoalDate(goalDate);
    }

    @Nullable
    public LocalDate getCompletionDate() {
        return currentValues.completionDate;
    }

    public void setCompletionDate(LocalDate completionDate) {
        currentValues.setCompletionDate(completionDate);
    }

    @NonNull
    public AssessmentType getType() {
        return currentValues.type;
    }

    public void setType(AssessmentType type) {
        currentValues.setType(type);
    }

    @NonNull
    public String getNotes() {
        return currentValues.getNotes();
    }

    public void setNotes(String notes) {
        currentValues.setNotes(notes);
    }

    public String getNormalizedNotes() {
        if (null == normalizedNotes) {
            normalizedNotes = AbstractAssessmentEntity.MULTI_LINE_NORMALIZER.apply(currentValues.notes);
            if (normalizedNotes.equals(currentValues.notes)) {
                currentValues.notes = null;
            }
        }
        return normalizedNotes;
    }

    private synchronized void onTermsLoaded(List<TermListItem> termListItems) {
        if (null == termListItems) {
            return;
        }
        termsLiveData.removeObserver(termsLoadedObserver);
        if (null != selectedCourse) {
            Long termId = selectedCourse.getTermId();
            if (null == termId) {
                selectedTerm = null;
            } else {
                selectedTerm = EntityHelper.findById(termId, termListItems).orElse(null);
            }
        }
    }

    private synchronized void onAllAssessmentsLoaded(List<AssessmentEntity> assessmentEntities) {
        if (null != assessmentEntities) {
            assessmentsLiveData.removeObserver(assessmentsLoadedObserver);
            assessmentsLiveData = null;
            assessmentsForCourse.clear();
            assessmentsForCourse.addAll(assessmentEntities);
        }
    }

    private synchronized void onTermCoursesLoaded(List<TermCourseListItem> termCourseListItems) {
        if (null != termCourseListItems) {
            coursesLiveData.removeObserver(coursesLoadedObserver);
            coursesLiveData = null;
            coursesForTerm.clear();
            coursesForTerm.addAll(termCourseListItems);
            EntityHelper.findById(assessmentEntity.getCourseId(), termCourseListItems).ifPresent(t -> {
                assessmentEntity.setCourse(t);
                this.selectedCourse = t;
            });
            termsLoadedObserver = this::onTermsLoaded;
            termsLiveData.observeForever(termsLoadedObserver);
        }
    }

    public boolean isFromInitializedState() {
        return fromInitializedState;
    }

    // TODO: Ensure AddAssessmentActivity and ViewAssessmentActivity call this
    public synchronized Single<AssessmentDetails> initializeViewModelState(@Nullable Bundle savedInstanceState, Supplier<Bundle> getArguments) {
        fromInitializedState = null != savedInstanceState && savedInstanceState.getBoolean(STATE_KEY_STATE_INITIALIZED, false);
        Bundle state = (fromInitializedState) ? savedInstanceState : getArguments.get();
        assessmentEntity = new AssessmentDetails((AbstractCourseEntity<?>) null);
        if (null != state) {
            Log.d(LOG_TAG, (fromInitializedState) ? "Restoring currentValues from saved state" : "Initializing currentValues from arguments");
            currentValues.restoreState(state, false);
            Long id = currentValues.getId();
            if (null == id || fromInitializedState) {
                Log.d(LOG_TAG, "Restoring courseEntity from saved state");
                assessmentEntity.restoreState(state, fromInitializedState);
            } else {
                // TODO: Make sure callers of initializeViewModelState display error alert and log it upon error
                return dbLoader.getAssessmentById(id).doOnSuccess(this::onEntityLoadedFromDb);
            }
        } else {
            // TODO: Anything need special handling?
        }
        onEntityLoaded();
        return Single.just(assessmentEntity).observeOn(AndroidSchedulers.mainThread());
    }

    private void onEntityLoaded() {
        titleLiveData.postValue(assessmentEntity.getName());
        entityLiveData.postValue(assessmentEntity);
        // TODO: Set up option listing listeners here
    }

    private void onEntityLoadedFromDb(AssessmentDetails entity) {
        Log.d(LOG_TAG, String.format("Loaded %s from database", entity));
        assessmentEntity = entity;
        setCode(entity.getCode());
        // TODO: Initialize remainder of properties
        onEntityLoaded();
    }

    public synchronized Single<List<Integer>> save() {
        ArrayList<Integer> errors = new ArrayList<>();
        if (null == selectedCourse) {
            errors.add(R.string.message_course_not_selected);
        }
        if (normalizedCode.isEmpty()) {
            errors.add(R.string.message_assessment_code_required);
        }
        if (!errors.isEmpty()) {
            Log.d(LOG_TAG, String.format("Returning %d errors", errors.size()));
            return Single.just(errors);
        }
        AssessmentEntity entity = new AssessmentEntity(assessmentEntity);
        entity.setCode(currentValues.getCode());
        entity.setCompletionDate(currentValues.getCompletionDate());
        //noinspection ConstantConditions
        entity.setCourseId(selectedCourse.getId());
        entity.setGoalDate(currentValues.getGoalDate());
        entity.setName(currentValues.getName());
        entity.setStatus(currentValues.getStatus());
        entity.setType(currentValues.getType());
        entity.setNotes(currentValues.getNotes());
        return dbLoader.saveAssessment(entity).toSingleDefault(Collections.emptyList());
    }

    public Completable delete() {
        Log.d(LOG_TAG, "Enter Erwine.Leonard.T.wguscheduler356334.ui.course.EditCourseViewModel.delete");
        AssessmentEntity entity = new AssessmentEntity(assessmentEntity);
        return dbLoader.deleteAssessment(entity).doOnError(throwable -> Log.e(getClass().getName(),
                "Error deleting course", throwable));
    }

    public boolean isChanged() {
        if (currentValues.getCode().equals(assessmentEntity.getCode()) && Objects.equals(currentValues.getName(), assessmentEntity.getName()) && currentValues.getStatus() == assessmentEntity.getStatus() &&
                Objects.equals(currentValues.getGoalDate(), assessmentEntity.getGoalDate()) && Objects.equals(currentValues.getCompletionDate(), assessmentEntity.getCompletionDate()) &&
                currentValues.getType() == assessmentEntity.getType() && Objects.equals(currentValues.getCourseId(), assessmentEntity.getCourseId())) {
            return !getNormalizedNotes().equals(assessmentEntity.getNotes());
        }
        return true;
    }

    private class CurrentValues implements Assessment {
        private Long id;
        private Long courseId;
        private String code = "";
        private String name = "";
        private AssessmentStatus status = AssessmentStatusConverter.DEFAULT;
        private LocalDate goalDate;
        private LocalDate completionDate;
        private AssessmentType type = AssessmentType.OBJECTIVE_ASSESSMENT;
        private String notes;

        @Nullable
        @Override
        public Long getId() {
            return (null == assessmentEntity) ? id : assessmentEntity.getId();
        }

        @Override
        public void setId(Long id) {
            if (null != assessmentEntity) {
                assessmentEntity.setId(id);
            }
            this.id = id;
        }

        @Override
        public Long getCourseId() {
            return courseId;
        }

        @Override
        public void setCourseId(long courseId) {
            this.courseId = courseId;
        }

        @NonNull
        @Override
        public String getCode() {
            return code;
        }

        @Override
        public void setCode(String code) {
            this.code = (null == code) ? "" : code;
            String oldValue = normalizedCode;
            normalizedCode = AbstractAssessmentEntity.SINGLE_LINE_NORMALIZER.apply(code);
            if (normalizedCode.isEmpty()) {
                if (!oldValue.isEmpty()) {
                    codeValidLiveData.postValue(false);
                }
            } else if (oldValue.isEmpty()) {
                codeValidLiveData.postValue(true);
            }
        }

        @Override
        @Nullable
        public String getName() {
            return name;
        }

        @Override
        public void setName(String name) {
            String s = AbstractAssessmentEntity.SINGLE_LINE_NORMALIZER.apply(name);
            this.name = (s.isEmpty()) ? null : name;
        }

        @NonNull
        @Override
        public AssessmentStatus getStatus() {
            return status;
        }

        @Override
        public void setStatus(AssessmentStatus status) {
            this.status = status;
        }

        @Nullable
        @Override
        public LocalDate getGoalDate() {
            return goalDate;
        }

        @Override
        public void setGoalDate(LocalDate goalDate) {
            this.goalDate = goalDate;
        }

        @Nullable
        @Override
        public LocalDate getCompletionDate() {
            return completionDate;
        }

        @Override
        public void setCompletionDate(LocalDate completionDate) {
            this.completionDate = completionDate;
        }

        @NonNull
        @Override
        public AssessmentType getType() {
            return type;
        }

        @Override
        public void setType(AssessmentType type) {
            this.type = type;
        }

        @NonNull
        @Override
        public String getNotes() {
            return (null == notes) ? normalizedNotes : notes;
        }

        @Override
        public void setNotes(String notes) {
            if (null == notes || notes.isEmpty()) {
                normalizedNotes = "";
                this.notes = null;
            } else if (!getNotes().equals(notes)) {
                this.notes = notes;
                normalizedNotes = null;
            }
        }
    }
}
