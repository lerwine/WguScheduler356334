package Erwine.Leonard.T.wguscheduler356334.ui.assessment;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
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
import Erwine.Leonard.T.wguscheduler356334.entity.mentor.MentorEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.term.Term;
import Erwine.Leonard.T.wguscheduler356334.entity.term.TermEntity;
import Erwine.Leonard.T.wguscheduler356334.util.validation.ResourceMessageResult;
import Erwine.Leonard.T.wguscheduler356334.util.validation.ValidationMessage;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;

import static Erwine.Leonard.T.wguscheduler356334.entity.IdIndexedEntity.ID_NEW;

public class EditAssessmentViewModel extends AndroidViewModel {
    private static final String LOG_TAG = EditAssessmentViewModel.class.getName();
    static final String STATE_KEY_STATE_INITIALIZED = "state_initialized";

    private final DbLoader dbLoader;
    private AssessmentDetails assessmentEntity;
    private TermEntity termEntity;
    private MentorEntity mentorEntity;
    private final MutableLiveData<AssessmentDetails> entityLiveData;
    private final MutableLiveData<Function<Resources, String>> titleFactoryLiveData;
    private final MutableLiveData<Function<Resources, Spanned>> overviewFactoryLiveData;
    private final MutableLiveData<Boolean> courseValidLiveData;
    private final MutableLiveData<Boolean> codeValidLiveData;
    private final CurrentValues currentValues;
    private final MutableLiveData<LocalDate> effectiveStartLiveData;
    private final MutableLiveData<LocalDate> effectiveEndLiveData;
    private String viewTitle;
    private Spanned overview;
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
        overviewFactoryLiveData = new MutableLiveData<>(r -> new SpannableString(""));
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

    public LiveData<Function<Resources, String>> getTitleFactoryLiveData() {
        return titleFactoryLiveData;
    }

    public MutableLiveData<Function<Resources, Spanned>> getOverviewFactoryLiveData() {
        return overviewFactoryLiveData;
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
        return normalizedName;
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
        viewTitle = null;
        overview = null;
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

    @NonNull
    public synchronized Spanned calculateOverview(Resources resources) {
        if (null != overview) {
            return overview;
        }
        Spanned result;
        LocalDate completionDate = currentValues.getCompletionDate();
        LocalDate goalDate = currentValues.getGoalDate();
        if (null != completionDate) {
            if (null != goalDate) {
                result = Html.fromHtml(resources.getString(R.string.html_format_assessment_overview_completed, resources.getString(currentValues.status.displayResourceId()),
                        completionDate, goalDate), Html.TO_HTML_PARAGRAPH_LINES_CONSECUTIVE);
            } else {
                result = Html.fromHtml(resources.getString(R.string.html_format_assessment_overview_completed_no_goal, resources.getString(currentValues.status.displayResourceId()),
                        completionDate), Html.TO_HTML_PARAGRAPH_LINES_CONSECUTIVE);
            }
        } else if (currentValues.status == AssessmentStatus.NOT_STARTED) {
            if (null != goalDate) {
                result = Html.fromHtml(resources.getString(R.string.html_format_assessment_overview_goal, resources.getString(currentValues.status.displayResourceId()),
                        goalDate), Html.TO_HTML_PARAGRAPH_LINES_CONSECUTIVE);
            } else {
                result = Html.fromHtml(resources.getString(R.string.html_format_assessment_overview_no_goal, resources.getString(currentValues.status.displayResourceId())), Html.TO_HTML_PARAGRAPH_LINES_CONSECUTIVE);
            }
        } else if (null != goalDate) {
            result = Html.fromHtml(resources.getString(R.string.html_format_assessment_overview_completed_required, resources.getString(currentValues.status.displayResourceId()),
                    goalDate), Html.TO_HTML_PARAGRAPH_LINES_CONSECUTIVE);
        } else {
            result = Html.fromHtml(resources.getString(R.string.html_format_assessment_overview_completed_required_no_goal, resources.getString(currentValues.status.displayResourceId())),
                    Html.TO_HTML_PARAGRAPH_LINES_CONSECUTIVE);
        }
        overview = result;
        return result;
    }

    @NonNull
    public synchronized String calculateViewTitle(Resources resources) {
        if (null == viewTitle) {
            String n = currentValues.name;
            if (null == n) {
                viewTitle = resources.getString(R.string.format_assessment, resources.getString(currentValues.type.displayResourceId()), currentValues.code);
            } else {
                viewTitle = resources.getString(R.string.format_assessment_name, resources.getString(currentValues.type.displayResourceId()), currentValues.code, n);
            }
        }
        return viewTitle;
    }

    private void onEntityLoaded() {
        titleFactoryLiveData.postValue(this::calculateViewTitle);
        overviewFactoryLiveData.postValue(this::calculateOverview);
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

    public synchronized Single<ResourceMessageResult> save(boolean ignoreWarnings) {
        if (null == selectedCourse) {
            return Single.just(ValidationMessage.ofSingleError(R.string.message_course_not_selected)).observeOn(AndroidSchedulers.mainThread());
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
        return dbLoader.saveAssessment(entity, ignoreWarnings).doOnSuccess(m -> {
            if (m.isSucceeded()) {
                assessmentEntity.applyChanges(entity, selectedCourse);
            }
        });
    }

    public Single<Integer> delete() {
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

    public Single<TermEntity> getCurrentTerm() {
        if (null != termEntity) {
            return Single.just(termEntity).observeOn(AndroidSchedulers.mainThread());
        }
        return dbLoader.getTermById(assessmentEntity.getTermId()).doOnSuccess(t -> termEntity = t);
    }

    public Single<MentorEntity> getCourseMentor() {
        if (null != mentorEntity) {
            return Single.just(mentorEntity).observeOn(AndroidSchedulers.mainThread());
        }
        return dbLoader.getMentorById(assessmentEntity.getMentorId()).doOnSuccess(t -> mentorEntity = t);
    }

    public synchronized Long getMentorId() {
        return (null == selectedCourse) ? null : selectedCourse.getMentorId();
    }

    private class CurrentValues implements Assessment {
        private long id;
        private Long courseId;
        @NonNull
        private String code = "";
        @Nullable
        private String name;
        @NonNull
        private AssessmentStatus status = AssessmentStatusConverter.DEFAULT;
        @Nullable
        private LocalDate goalDate;
        @Nullable
        private LocalDate completionDate;
        @NonNull
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
            if (!normalizedCode.equals(oldValue)) {
                titleFactoryLiveData.postValue(EditAssessmentViewModel.this::calculateViewTitle);
            }
        }

        @Override
        @Nullable
        public String getName() {
            return name;
        }

        @Override
        public void setName(String name) {
            String oldValue = normalizedName;
            normalizedName = AbstractAssessmentEntity.SINGLE_LINE_NORMALIZER.apply(name);
            if (normalizedName.isEmpty()) {
                this.name = null;
            } else {
                this.name = name;
            }
            if (!normalizedName.equals(oldValue)) {
                titleFactoryLiveData.postValue(EditAssessmentViewModel.this::calculateViewTitle);
            }
        }

        @NonNull
        @Override
        public AssessmentStatus getStatus() {
            return status;
        }

        @Override
        public void setStatus(@NonNull AssessmentStatus status) {
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
                LocalDate oldValue = effectiveStartLiveData.getValue();
                this.goalDate = goalDate;
                if ((null == completionDate || this.status == AssessmentStatus.NOT_STARTED) && !Objects.equals(oldValue, goalDate)) {
                    effectiveStartLiveData.setValue(goalDate);
                }
            }
        }

        @Nullable
        @Override
        public LocalDate getCompletionDate() {
            return (this.status == AssessmentStatus.NOT_STARTED) ? null : completionDate;
        }

        @Override
        public synchronized void setCompletionDate(LocalDate completionDate) {
            if (!Objects.equals(completionDate, this.completionDate)) {
                LocalDate oldValue = effectiveStartLiveData.getValue();
                this.completionDate = completionDate;
                if (this.status != AssessmentStatus.NOT_STARTED && !Objects.equals(oldValue, completionDate)) {
                    effectiveStartLiveData.setValue(completionDate);
                }
            }
        }

        @NonNull
        @Override
        public AssessmentType getType() {
            return type;
        }

        @Override
        public void setType(AssessmentType type) {
            AssessmentType oldValue = this.type;
            this.type = AssessmentTypeConverter.asNonNull(type);
            if (this.type != oldValue) {
                titleFactoryLiveData.postValue(EditAssessmentViewModel.this::calculateViewTitle);
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
