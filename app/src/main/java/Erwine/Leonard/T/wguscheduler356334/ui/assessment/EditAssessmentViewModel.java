package Erwine.Leonard.T.wguscheduler356334.ui.assessment;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

import Erwine.Leonard.T.wguscheduler356334.AddAssessmentActivity;
import Erwine.Leonard.T.wguscheduler356334.R;
import Erwine.Leonard.T.wguscheduler356334.ViewAssessmentActivity;
import Erwine.Leonard.T.wguscheduler356334.db.AppDb;
import Erwine.Leonard.T.wguscheduler356334.db.AssessmentStatusConverter;
import Erwine.Leonard.T.wguscheduler356334.db.AssessmentTypeConverter;
import Erwine.Leonard.T.wguscheduler356334.db.DbLoader;
import Erwine.Leonard.T.wguscheduler356334.entity.IdIndexedEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.assessment.AbstractAssessmentEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.assessment.Assessment;
import Erwine.Leonard.T.wguscheduler356334.entity.assessment.AssessmentDetails;
import Erwine.Leonard.T.wguscheduler356334.entity.assessment.AssessmentEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.assessment.AssessmentStatus;
import Erwine.Leonard.T.wguscheduler356334.entity.assessment.AssessmentType;
import Erwine.Leonard.T.wguscheduler356334.entity.course.AbstractCourseEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.course.TermCourseListItem;
import Erwine.Leonard.T.wguscheduler356334.entity.term.Term;
import Erwine.Leonard.T.wguscheduler356334.util.ValidationMessage;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;

import static Erwine.Leonard.T.wguscheduler356334.entity.IdIndexedEntity.ID_NEW;

public class EditAssessmentViewModel extends AndroidViewModel {
    private static final String LOG_TAG = EditAssessmentViewModel.class.getName();
    static final String STATE_KEY_STATE_INITIALIZED = "state_initialized";

    private final DbLoader dbLoader;
    private AssessmentDetails assessmentEntity;
    private final MutableLiveData<AssessmentDetails> entityLiveData;
    private final MutableLiveData<Function<Resources, String>> titleFactoryLiveData;
    private final MutableLiveData<Boolean> courseValidLiveData;
    private final MutableLiveData<Boolean> codeValidLiveData;
    private final CurrentValues currentValues;
    private final MutableLiveData<LocalDate> effectiveStartLiveData;
    private final MutableLiveData<LocalDate> effectiveEndLiveData;
    private LiveData<List<AssessmentEntity>> assessmentsForCourse;
    private LiveData<List<TermCourseListItem>> coursesForTerm;
    private AbstractCourseEntity<?> selectedCourse;
    private boolean fromInitializedState;
    @NonNull
    private String normalizedName = "";
    @NonNull
    private String normalizedCode = "";
    private String normalizedNotes = "";
    private Observer<List<TermCourseListItem>> coursesLoadedObserver;
    private Observer<List<AssessmentEntity>> assessmentsLoadedObserver;

    public static void startAddAssessmentActivity(@NonNull Context context, long courseId, @Nullable LocalDate goalDate) {
        Intent intent = new Intent(context, AddAssessmentActivity.class);
        intent.putExtra(IdIndexedEntity.stateKey(AppDb.TABLE_NAME_COURSES, Term.COLNAME_ID, false), courseId);
        if (null != goalDate) {
            intent.putExtra(AssessmentDetails.COLNAME_GOAL_DATE, goalDate.toEpochDay());
        }
        context.startActivity(intent);
    }

    public static void startViewAssessmentActivity(@NonNull Context context, long assessmentId) {
        Intent intent = new Intent(context, ViewAssessmentActivity.class);
        intent.putExtra(IdIndexedEntity.stateKey(AppDb.TABLE_NAME_ASSESSMENTS, Assessment.COLNAME_ID, false), assessmentId);
        context.startActivity(intent);
    }

    public EditAssessmentViewModel(@NonNull Application application) {
        super(application);
        dbLoader = DbLoader.getInstance(getApplication());
        titleFactoryLiveData = new MutableLiveData<>(c -> c.getString(R.string.title_activity_view_assessment));
        currentValues = new CurrentValues();
        entityLiveData = new MutableLiveData<>();
        courseValidLiveData = new MutableLiveData<>(false);
        codeValidLiveData = new MutableLiveData<>(false);
        effectiveStartLiveData = new MutableLiveData<>();
        effectiveEndLiveData = new MutableLiveData<>();
    }

    @NonNull
    public LiveData<AssessmentDetails> getEntityLiveData() {
        return entityLiveData;
    }

    @NonNull
    public LiveData<Function<Resources, String>> getTitleFactoryLiveData() {
        return titleFactoryLiveData;
    }

    @NonNull
    public LiveData<LocalDate> getEffectiveStartLiveData() {
        return effectiveStartLiveData;
    }

    @NonNull
    public LiveData<LocalDate> getEffectiveEndLiveData() {
        return effectiveEndLiveData;
    }

    public long getId() {
        return currentValues.getId();
    }

    public AbstractCourseEntity<?> getSelectedCourse() {
        return selectedCourse;
    }

    public synchronized void setSelectedCourse(AbstractCourseEntity<?> selectedCourse) {
        Log.d(LOG_TAG, "Enter setSelectedCourse(" + selectedCourse + ")");
        AbstractCourseEntity<?> oldCourse = this.selectedCourse;
        if (!Objects.equals(oldCourse, selectedCourse)) {
            this.selectedCourse = selectedCourse;
            long id = selectedCourse.getId();
            currentValues.courseId = id;
            if (ID_NEW != id) {
                courseValidLiveData.postValue(true);
            } else {
                courseValidLiveData.postValue(false);
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

    public boolean isFromInitializedState() {
        return fromInitializedState;
    }

    @NonNull
    public Single<List<TermCourseListItem>> loadCourses() {
        return dbLoader.loadCoursesByTermId(assessmentEntity.getCourse().getTermId());
    }

    public synchronized Single<AssessmentDetails> initializeViewModelState(@Nullable Bundle savedInstanceState, Supplier<Bundle> getArguments) {
        fromInitializedState = null != savedInstanceState && savedInstanceState.getBoolean(STATE_KEY_STATE_INITIALIZED, false);
        Bundle state = (fromInitializedState) ? savedInstanceState : getArguments.get();
        assessmentEntity = new AssessmentDetails((AbstractCourseEntity<?>) null);
        if (null != state) {
            Log.d(LOG_TAG, (fromInitializedState) ? "Restoring currentValues from saved state" : "Initializing currentValues from arguments");
            currentValues.restoreState(state, false);
            long id = currentValues.getId();
            if (ID_NEW == id || fromInitializedState) {
                Log.d(LOG_TAG, "Restoring courseEntity from saved state");
                assessmentEntity.restoreState(state, fromInitializedState);
            } else {
                return dbLoader.getAssessmentById(id).doOnSuccess(this::onEntityLoadedFromDb);
            }
        }
        onEntityLoaded();
        return Single.just(assessmentEntity).observeOn(AndroidSchedulers.mainThread());
    }

    public void saveViewModelState(Bundle outState) {
        outState.putBoolean(STATE_KEY_STATE_INITIALIZED, true);
        currentValues.saveState(outState, false);
        assessmentEntity.saveState(outState, true);
    }

    private void onEntityLoaded() {
        String n = assessmentEntity.getName();
        if (null == n) {
            titleFactoryLiveData.postValue(r -> r.getString(R.string.format_assessment, r.getString(assessmentEntity.getType().displayResourceId()), assessmentEntity.getCode()));
        } else {
            titleFactoryLiveData.postValue(r -> r.getString(R.string.format_assessment_name, r.getString(assessmentEntity.getType().displayResourceId()), assessmentEntity.getCode(), n));
        }
        entityLiveData.postValue(assessmentEntity);
    }

    private void onEntityLoadedFromDb(AssessmentDetails entity) {
        Log.d(LOG_TAG, String.format("Loaded %s from database", entity));
        assessmentEntity = entity;
        setCode(entity.getCode());
        setCompletionDate(entity.getCompletionDate());
        setGoalDate(entity.getGoalDate());
        setName(entity.getName());
        setNotes(entity.getNotes());
        setSelectedCourse(entity.getCourse());
        setStatus(entity.getStatus());
        setType(entity.getType());
        onEntityLoaded();
    }

    public synchronized Single<ValidationMessage.ResourceMessageResult> save(boolean ignoreWarnings) {
        if (null == selectedCourse) {
            return Single.just(ValidationMessage.ofSingleError(R.string.message_course_not_selected));
        }
        AssessmentEntity entity = new AssessmentEntity(assessmentEntity);
        entity.setCode(currentValues.getCode());
        entity.setCompletionDate(currentValues.getCompletionDate());
        entity.setCourseId(selectedCourse.getId());
        entity.setGoalDate(currentValues.getGoalDate());
        entity.setName(currentValues.getName());
        entity.setStatus(currentValues.getStatus());
        entity.setType(currentValues.getType());
        entity.setNotes(currentValues.getNotes());
        return dbLoader.saveAssessment(entity, ignoreWarnings);
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
        private long id;
        private Long courseId;
        private String code = "";
        private String name = "";
        private AssessmentStatus status = AssessmentStatusConverter.DEFAULT;
        private LocalDate goalDate;
        private LocalDate completionDate;
        private AssessmentType type = AssessmentType.OBJECTIVE_ASSESSMENT;
        private String notes;

        @Override
        public long getId() {
            return (null == assessmentEntity) ? id : assessmentEntity.getId();
        }

        @Override
        public void setId(long id) {
            if (null != assessmentEntity) {
                assessmentEntity.setId(id);
            }
            this.id = id;
        }

        @Override
        public long getCourseId() {
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
            if (normalizedName.isEmpty()) {
                titleFactoryLiveData.postValue(r -> r.getString(R.string.format_assessment, r.getString(type.displayResourceId()), normalizedCode));
            } else {
                titleFactoryLiveData.postValue(r -> r.getString(R.string.format_assessment_name, r.getString(type.displayResourceId()), normalizedCode, normalizedName));
            }
        }

        @Override
        @Nullable
        public String getName() {
            return name;
        }

        @Override
        public void setName(String name) {
            normalizedName = AbstractAssessmentEntity.SINGLE_LINE_NORMALIZER.apply(name);
            if (normalizedName.isEmpty()) {
                this.name = null;
                titleFactoryLiveData.postValue(r -> r.getString(R.string.format_assessment, r.getString(type.displayResourceId()), normalizedCode));
            } else {
                this.name = name;
                titleFactoryLiveData.postValue(r -> r.getString(R.string.format_assessment_name, r.getString(type.displayResourceId()), normalizedCode, normalizedName));
            }
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
        public synchronized void setGoalDate(LocalDate goalDate) {
            if (!Objects.equals(goalDate, this.goalDate)) {
                this.goalDate = goalDate;
                effectiveStartLiveData.setValue(goalDate);
            }
        }

        @Nullable
        @Override
        public LocalDate getCompletionDate() {
            return completionDate;
        }

        @Override
        public synchronized void setCompletionDate(LocalDate completionDate) {
            if (!Objects.equals(completionDate, this.completionDate)) {
                this.completionDate = completionDate;
                effectiveStartLiveData.setValue(completionDate);
            }
        }

        @NonNull
        @Override
        public AssessmentType getType() {
            return type;
        }

        @Override
        public void setType(AssessmentType type) {
            this.type = AssessmentTypeConverter.asNonNull(type);
            if (normalizedName.isEmpty()) {
                titleFactoryLiveData.postValue(r -> r.getString(R.string.format_assessment, r.getString(this.type.displayResourceId()), normalizedCode));
            } else {
                titleFactoryLiveData.postValue(r -> r.getString(R.string.format_assessment_name, r.getString(this.type.displayResourceId()), normalizedCode, normalizedName));
            }
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
