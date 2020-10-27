package Erwine.Leonard.T.wguscheduler356334.ui.assessment;

import android.app.Activity;
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
import androidx.lifecycle.Observer;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

import Erwine.Leonard.T.wguscheduler356334.AddAssessmentActivity;
import Erwine.Leonard.T.wguscheduler356334.MainActivity;
import Erwine.Leonard.T.wguscheduler356334.R;
import Erwine.Leonard.T.wguscheduler356334.ViewAssessmentActivity;
import Erwine.Leonard.T.wguscheduler356334.db.AppDb;
import Erwine.Leonard.T.wguscheduler356334.db.AssessmentStatusConverter;
import Erwine.Leonard.T.wguscheduler356334.db.AssessmentTypeConverter;
import Erwine.Leonard.T.wguscheduler356334.db.DbLoader;
import Erwine.Leonard.T.wguscheduler356334.db.LocalDateConverter;
import Erwine.Leonard.T.wguscheduler356334.entity.IdIndexedEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.assessment.AbstractAssessmentEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.assessment.Assessment;
import Erwine.Leonard.T.wguscheduler356334.entity.assessment.AssessmentAlert;
import Erwine.Leonard.T.wguscheduler356334.entity.assessment.AssessmentDetails;
import Erwine.Leonard.T.wguscheduler356334.entity.assessment.AssessmentEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.assessment.AssessmentStatus;
import Erwine.Leonard.T.wguscheduler356334.entity.assessment.AssessmentType;
import Erwine.Leonard.T.wguscheduler356334.entity.course.AbstractCourseEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.course.Course;
import Erwine.Leonard.T.wguscheduler356334.entity.course.TermCourseListItem;
import Erwine.Leonard.T.wguscheduler356334.entity.mentor.MentorEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.term.TermEntity;
import Erwine.Leonard.T.wguscheduler356334.util.validation.ResourceMessageResult;
import Erwine.Leonard.T.wguscheduler356334.util.validation.ValidationMessage;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;

import static Erwine.Leonard.T.wguscheduler356334.entity.IdIndexedEntity.ID_NEW;

public class EditAssessmentViewModel extends AndroidViewModel {
    private static final String LOG_TAG = MainActivity.getLogTag(EditAssessmentViewModel.class);
    static final String STATE_KEY_STATE_INITIALIZED = "state_initialized";
    public static final String EXTRA_KEY_ASSESSMENT_ID = IdIndexedEntity.stateKey(AppDb.TABLE_NAME_ASSESSMENTS, Assessment.COLNAME_ID, false);

    private final DbLoader dbLoader;
    @NonNull
    private AssessmentDetails originalValues;
    private TermEntity termEntity;
    private MentorEntity mentorEntity;
    private final PrivateLiveData<AssessmentDetails> entityLiveData;
    private final PrivateLiveData<Function<Resources, String>> titleFactoryLiveData;
    private final PrivateLiveData<Function<Resources, Spanned>> overviewFactoryLiveData;
    private final PrivateLiveData<Boolean> courseValidLiveData;
    private final PrivateLiveData<Boolean> codeValidLiveData;
    private final CurrentValues currentValues;
    private final PrivateLiveData<LocalDate> effectiveStartLiveData;
    private final PrivateLiveData<LocalDate> effectiveEndLiveData;
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

    public static void startAddAssessmentActivity(@NonNull Activity activity, int requestCode, long courseId, @Nullable LocalDate goalDate) {
        Intent intent = new Intent(activity, AddAssessmentActivity.class);
        intent.putExtra(IdIndexedEntity.stateKey(AppDb.TABLE_NAME_COURSES, Course.COLNAME_ID, false), courseId);
        Long d = LocalDateConverter.fromLocalDate(goalDate);
        if (null != d) {
            intent.putExtra(IdIndexedEntity.stateKey(AppDb.TABLE_NAME_ASSESSMENTS, Assessment.COLNAME_GOAL_DATE, false), d);
        }
        activity.startActivityForResult(intent, requestCode);
    }

    public static void startViewAssessmentActivity(@NonNull Context context, long assessmentId) {
        Intent intent = new Intent(context, ViewAssessmentActivity.class);
        intent.putExtra(EXTRA_KEY_ASSESSMENT_ID, assessmentId);
        context.startActivity(intent);
    }

    public EditAssessmentViewModel(@NonNull Application application) {
        super(application);
        Log.d(LOG_TAG, "Constructing");
        dbLoader = DbLoader.getInstance(getApplication());
        originalValues = new AssessmentDetails((AbstractCourseEntity<?>) null);
        titleFactoryLiveData = new PrivateLiveData<>(c -> c.getString(R.string.title_activity_view_assessment));
        overviewFactoryLiveData = new PrivateLiveData<>(r -> new SpannableString(" "));
        currentValues = new CurrentValues();
        entityLiveData = new PrivateLiveData<>();
        courseValidLiveData = new PrivateLiveData<>(false);
        codeValidLiveData = new PrivateLiveData<>(false);
        effectiveStartLiveData = new PrivateLiveData<>();
        effectiveEndLiveData = new PrivateLiveData<>();
    }

    @Override
    protected void onCleared() {
        Log.d(LOG_TAG, "Enter onCleared");
        super.onCleared();
    }

    @NonNull
    public LiveData<AssessmentDetails> getEntityLiveData() {
        return entityLiveData;
    }

    public LiveData<Function<Resources, String>> getTitleFactoryLiveData() {
        return titleFactoryLiveData;
    }

    public LiveData<List<AssessmentAlert>> getAllAlerts() {
        long id = originalValues.getId();
        if (id != ID_NEW) {
            return dbLoader.getAlertsByAssessmentId(id);
        }
        PrivateLiveData<List<AssessmentAlert>> result = new PrivateLiveData<>();
        result.postValue(Collections.emptyList());
        return result;
    }

    public LiveData<Function<Resources, Spanned>> getOverviewFactoryLiveData() {
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
        return originalValues.getId();
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
            courseValidLiveData.postValue(ID_NEW != id);
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
        return dbLoader.loadCoursesByTermId(originalValues.getTermId());
    }

    public synchronized Single<AssessmentDetails> initializeViewModelState(@Nullable Bundle savedInstanceState, Supplier<Bundle> getArguments) {
        fromInitializedState = null != savedInstanceState && savedInstanceState.getBoolean(STATE_KEY_STATE_INITIALIZED, false);
        Bundle state = (fromInitializedState) ? savedInstanceState : getArguments.get();
        viewTitle = null;
        overview = null;
        if (null != state) {
            Log.d(LOG_TAG, (fromInitializedState) ? "Restoring currentValues from saved state" : "Initializing currentValues from arguments");
            if (!fromInitializedState) {
                if (state.containsKey(EXTRA_KEY_ASSESSMENT_ID)) {
                    return dbLoader.getAssessmentById(state.getLong(EXTRA_KEY_ASSESSMENT_ID)).doOnSuccess(this::onEntityLoadedFromDb);
                }
                String key = IdIndexedEntity.stateKey(AppDb.TABLE_NAME_ASSESSMENTS, Assessment.COLNAME_GOAL_DATE, false);
                return dbLoader.createAssessmentForCourse(state.getLong(IdIndexedEntity.stateKey(AppDb.TABLE_NAME_COURSES, Course.COLNAME_ID, false)),
                        (state.containsKey(key)) ? LocalDateConverter.toLocalDate(state.getLong(key)) : null).doOnSuccess(this::onEntityLoadedFromDb);
            }
            currentValues.restoreState(state, false);
            originalValues.restoreState(state, true);
        }
        onEntityLoaded();
        return Single.just(originalValues).observeOn(AndroidSchedulers.mainThread());
    }

    public void saveViewModelState(Bundle outState) {
        outState.putBoolean(STATE_KEY_STATE_INITIALIZED, true);
        currentValues.saveState(outState, false);
        originalValues.saveState(outState, true);
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
        entityLiveData.postValue(originalValues);
    }

    private void onEntityLoadedFromDb(AssessmentDetails entity) {
        Log.d(LOG_TAG, String.format("Loaded %s from database", entity));
        originalValues = entity;
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
        AssessmentEntity entity = new AssessmentEntity(originalValues);
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
                originalValues.applyChanges(entity, selectedCourse);
            }
        });
    }

    public Single<Integer> delete() {
        Log.d(LOG_TAG, "Enter delete");
        AssessmentEntity entity = new AssessmentEntity(originalValues);
        return dbLoader.deleteAssessment(entity).doOnError(throwable -> Log.e(getClass().getName(),
                "Error deleting course", throwable));
    }

    public boolean isChanged() {
        if (currentValues.getCode().equals(originalValues.getCode()) && Objects.equals(currentValues.getName(), originalValues.getName()) && currentValues.getStatus() == originalValues.getStatus() &&
                Objects.equals(currentValues.getGoalDate(), originalValues.getGoalDate()) && Objects.equals(currentValues.getCompletionDate(), originalValues.getCompletionDate()) &&
                currentValues.getType() == originalValues.getType() && Objects.equals(currentValues.getCourseId(), originalValues.getCourseId())) {
            return !getNormalizedNotes().equals(originalValues.getNotes());
        }
        return true;
    }

    public Single<TermEntity> getCurrentTerm() {
        if (null != termEntity) {
            return Single.just(termEntity).observeOn(AndroidSchedulers.mainThread());
        }
        return dbLoader.getTermById(originalValues.getTermId()).doOnSuccess(t -> termEntity = t);
    }

    public Single<MentorEntity> getCourseMentor() {
        if (null != mentorEntity) {
            return Single.just(mentorEntity).observeOn(AndroidSchedulers.mainThread());
        }
        return dbLoader.getMentorById(originalValues.getMentorId()).doOnSuccess(t -> mentorEntity = t);
    }

    public synchronized Long getMentorId() {
        return (null == selectedCourse) ? null : selectedCourse.getMentorId();
    }

    private static class PrivateLiveData<T> extends LiveData<T> {
        PrivateLiveData(T value) {
            super(value);
        }

        PrivateLiveData() {
        }

        @Override
        public void postValue(T value) {
            super.postValue(value);
        }

        @Override
        public void setValue(T value) {
            super.setValue(value);
        }
    }

    private class CurrentValues implements Assessment {
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
            return originalValues.getId();
        }

        @Override
        public void setId(long id) {
            originalValues.setId(id);
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
