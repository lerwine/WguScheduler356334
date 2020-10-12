package Erwine.Leonard.T.wguscheduler356334.ui.course;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import Erwine.Leonard.T.wguscheduler356334.AddCourseActivity;
import Erwine.Leonard.T.wguscheduler356334.R;
import Erwine.Leonard.T.wguscheduler356334.ViewCourseActivity;
import Erwine.Leonard.T.wguscheduler356334.db.AppDb;
import Erwine.Leonard.T.wguscheduler356334.db.DbLoader;
import Erwine.Leonard.T.wguscheduler356334.entity.IdIndexedEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.alert.AlertListItem;
import Erwine.Leonard.T.wguscheduler356334.entity.assessment.AssessmentEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.course.Course;
import Erwine.Leonard.T.wguscheduler356334.entity.course.CourseAlert;
import Erwine.Leonard.T.wguscheduler356334.entity.course.CourseDetails;
import Erwine.Leonard.T.wguscheduler356334.entity.course.CourseEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.course.CourseStatus;
import Erwine.Leonard.T.wguscheduler356334.entity.course.TermCourseListItem;
import Erwine.Leonard.T.wguscheduler356334.entity.mentor.AbstractMentorEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.mentor.MentorEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.mentor.MentorListItem;
import Erwine.Leonard.T.wguscheduler356334.entity.term.AbstractTermEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.term.Term;
import Erwine.Leonard.T.wguscheduler356334.entity.term.TermEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.term.TermListItem;
import Erwine.Leonard.T.wguscheduler356334.util.EntityHelper;
import Erwine.Leonard.T.wguscheduler356334.util.ToStringBuilder;
import Erwine.Leonard.T.wguscheduler356334.util.validation.ResourceMessageResult;
import Erwine.Leonard.T.wguscheduler356334.util.validation.ValidationMessage;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;

import static Erwine.Leonard.T.wguscheduler356334.entity.IdIndexedEntity.ID_NEW;
import static Erwine.Leonard.T.wguscheduler356334.ui.course.EditCourseFragment.DATE_FORMATTER;
import static Erwine.Leonard.T.wguscheduler356334.ui.course.EditCourseFragment.NUMBER_FORMATTER;
import static android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE;

/**
 * View model shared by {@link Erwine.Leonard.T.wguscheduler356334.ViewCourseActivity}, {@link Erwine.Leonard.T.wguscheduler356334.AddCourseActivity},
 * {@link Erwine.Leonard.T.wguscheduler356334.ui.assessment.AssessmentListFragment} and {@link EditCourseFragment}
 */
public class EditCourseViewModel extends AndroidViewModel {
    private static final String LOG_TAG = EditCourseViewModel.class.getName();
    static final String STATE_KEY_STATE_INITIALIZED = "state_initialized";
    public static final String STATE_KEY_COMPETENCY_UNITS_TEXT = "t:" + IdIndexedEntity.stateKey(AppDb.TABLE_NAME_COURSES, Course.COLNAME_COMPETENCY_UNITS, false);

    public static void startAddCourseActivity(@NonNull Context context, long termId, @NonNull LocalDate expectedStart) {
        Log.d(LOG_TAG, String.format("Enter startAddCourseActivity(context, %d, %s)", termId, expectedStart));
        Intent intent = new Intent(context, AddCourseActivity.class);
        intent.putExtra(IdIndexedEntity.stateKey(AppDb.TABLE_NAME_TERMS, Term.COLNAME_ID, false), termId);
        intent.putExtra(CourseDetails.COLNAME_EXPECTED_START, expectedStart.toEpochDay());
        context.startActivity(intent);
    }

    public static void startViewCourseActivity(@NonNull Context context, long courseId) {
        Log.d(LOG_TAG, String.format("Enter startViewCourseActivity(context, %d)", courseId));
        Intent intent = new Intent(context, ViewCourseActivity.class);
        intent.putExtra(IdIndexedEntity.stateKey(AppDb.TABLE_NAME_COURSES, Course.COLNAME_ID, false), courseId);
        context.startActivity(intent);
    }

    private final DbLoader dbLoader;
    private CourseDetails courseEntity;
    private final MutableLiveData<CourseDetails> entityLiveData;
    private final LiveData<List<TermListItem>> termsLiveData;
    private final LiveData<List<MentorListItem>> mentorsLiveData;
    private final MutableLiveData<LocalDate> effectiveStartLiveData;
    private final MutableLiveData<LocalDate> effectiveEndLiveData;
    private final MutableLiveData<Boolean> termValidLiveData;
    private final MutableLiveData<Boolean> numberValidLiveData;
    private final MutableLiveData<Boolean> titleValidLiveData;
    private final MutableLiveData<Integer> expectedStartErrorMessageLiveData;
    private final MutableLiveData<Integer> expectedStartWarningMessageLiveData;
    private final MutableLiveData<Integer> expectedEndMessageLiveData;
    private final MutableLiveData<Integer> actualStartErrorMessageLiveData;
    private final MutableLiveData<Integer> actualStartWarningMessageLiveData;
    private final MutableLiveData<Integer> actualEndMessageLiveData;
    private final MutableLiveData<Integer> competencyUnitsMessageLiveData;
    private final MutableLiveData<Function<Resources, String>> titleFactoryLiveData;
    private final MutableLiveData<Function<Resources, Spanned>> overviewFactoryLiveData;
    private final CurrentValues currentValues;
    private final ArrayList<TermCourseListItem> coursesForTerm;
    private LiveData<List<TermCourseListItem>> coursesLiveData;
    private final Observer<List<TermListItem>> termsLoadedObserver;
    private final Observer<List<MentorListItem>> mentorsLoadedObserver;
    private String viewTitle;
    private Spanned overview;
    private Observer<List<TermCourseListItem>> coursesLoadedObserver;
    private boolean fromInitializedState;
    private AbstractTermEntity<?> selectedTerm;
    private AbstractMentorEntity<?> selectedMentor;
    @NonNull
    private String normalizedNumber = "";
    @NonNull
    private String normalizedTitle = "";
    private String normalizedNotes = "";
    private String competencyUnitsText = "";
    private LiveData<List<AssessmentEntity>> assessments;

    public EditCourseViewModel(@NonNull Application application) {
        super(application);
        Log.d(LOG_TAG, "Constructing EditCourseViewModel");
        dbLoader = DbLoader.getInstance(getApplication());
        termsLiveData = dbLoader.getAllTerms();
        mentorsLiveData = dbLoader.getAllMentors();
        currentValues = new CurrentValues();
        entityLiveData = new MutableLiveData<>();
        effectiveStartLiveData = new MutableLiveData<>();
        effectiveEndLiveData = new MutableLiveData<>();
        termValidLiveData = new MutableLiveData<>(false);
        numberValidLiveData = new MutableLiveData<>(false);
        titleValidLiveData = new MutableLiveData<>(false);
        expectedStartErrorMessageLiveData = new MutableLiveData<>();
        expectedStartWarningMessageLiveData = new MutableLiveData<>();
        expectedEndMessageLiveData = new MutableLiveData<>();
        actualStartErrorMessageLiveData = new MutableLiveData<>();
        actualStartWarningMessageLiveData = new MutableLiveData<>();
        actualEndMessageLiveData = new MutableLiveData<>();
        competencyUnitsMessageLiveData = new MutableLiveData<>();
        titleFactoryLiveData = new MutableLiveData<>(c -> c.getString(R.string.title_activity_view_course));
        overviewFactoryLiveData = new MutableLiveData<>(r -> new SpannableString(""));
        coursesForTerm = new ArrayList<>();
        termsLoadedObserver = this::onTermsLoaded;
        mentorsLoadedObserver = this::onMentorsLoaded;

    }

    public long getId() {
        return currentValues.getId();
    }

    public AbstractTermEntity<?> getSelectedTerm() {
        return selectedTerm;
    }

    public synchronized void setSelectedTerm(AbstractTermEntity<?> selectedTerm) {
        if (!Objects.equals(this.selectedTerm, selectedTerm)) {
            Log.d(LOG_TAG, String.format("selectedTerm changing from:\n\t%s\n\tto\n\t%s", this.selectedTerm, selectedTerm));
            this.selectedTerm = selectedTerm;
            coursesForTerm.clear();
            if (null != coursesLiveData) {
                coursesLiveData.removeObserver(coursesLoadedObserver);
                coursesLiveData = null;
            }
            if (null == selectedTerm) {
                currentValues.termId = ID_NEW;
                termValidLiveData.postValue(false);
            } else {
                long id = selectedTerm.getId();
                currentValues.termId = id;
                if (ID_NEW != id) {
                    coursesLiveData = dbLoader.getCoursesByTermId(id);
                    coursesLoadedObserver = this::onAllCoursesLoaded;
                    coursesLiveData.observeForever(coursesLoadedObserver);
                    termValidLiveData.postValue(true);
                } else {
                    termValidLiveData.postValue(false);
                }
            }
            expectedStartErrorMessageLiveData.postValue(validateExpectedStart().orElse(null));
            actualStartErrorMessageLiveData.postValue(validateActualStart().orElse(null));
            expectedEndMessageLiveData.postValue(validateExpectedEnd().orElse(null));
            actualEndMessageLiveData.postValue(validateActualEnd().orElse(null));
            resetOverview();
            overviewFactoryLiveData.postValue(EditCourseViewModel.this::calculateOverview);
        }
    }

    public AbstractMentorEntity<?> getSelectedMentor() {
        return selectedMentor;
    }

    public synchronized void setSelectedMentor(@Nullable AbstractMentorEntity<?> selectedMentor) {
        if (!Objects.equals(this.selectedMentor, selectedMentor)) {
            Log.d(LOG_TAG, String.format("selectedMentor changing from:\n\t%s\n\tto\n\t%s", this.selectedMentor, selectedMentor));
            this.selectedMentor = selectedMentor;
            currentValues.mentorId = (null == selectedMentor) ? null : selectedMentor.getId();
            resetOverview();
            overviewFactoryLiveData.postValue(EditCourseViewModel.this::calculateOverview);
        }
    }

    public String getNumber() {
        return currentValues.getNumber();
    }

    public synchronized void setNumber(String value) {
        currentValues.setNumber(value);
    }

    public String getTitle() {
        return currentValues.getTitle();
    }

    public synchronized void setTitle(String value) {
        currentValues.setTitle(value);
    }

    public LocalDate getExpectedStart() {
        return currentValues.getExpectedStart();
    }

    public synchronized void setExpectedStart(LocalDate value) {
        currentValues.setExpectedStart(value);
    }

    public LocalDate getActualStart() {
        return currentValues.getActualStart();
    }

    public synchronized void setActualStart(LocalDate value) {
        currentValues.setActualStart(value);
    }

    public LocalDate getExpectedEnd() {
        return currentValues.getExpectedEnd();
    }

    public synchronized void setExpectedEnd(LocalDate value) {
        currentValues.setExpectedEnd(value);
    }

    public LocalDate getActualEnd() {
        return currentValues.getActualEnd();
    }

    public synchronized void setActualEnd(LocalDate value) {
        currentValues.setActualEnd(value);
    }

    public String getCompetencyUnitsText() {
        return competencyUnitsText;
    }

    public synchronized void setCompetencyUnitsText(String value) {
        if (null == value) {
            if (competencyUnitsText.isEmpty()) {
                return;
            }
            Log.d(LOG_TAG, String.format("competencyUnitsText changing from:\n\t\"%s\"\n\tto\n\t\"\"", ToStringBuilder.toEscapedString(this.competencyUnitsText)));
            competencyUnitsText = "";
            currentValues.competencyUnits = null;
        } else {
            if (competencyUnitsText.equals(value)) {
                return;
            }
            Log.d(LOG_TAG, String.format("competencyUnitsText changing from:\n\t\"%s\"\n\tto\n\t\"%s\"", ToStringBuilder.toEscapedString(this.competencyUnitsText), ToStringBuilder.toEscapedString(competencyUnitsText)));
            competencyUnitsText = value;
            try {
                currentValues.setCompetencyUnits(Integer.parseInt(competencyUnitsText.trim()));
            } catch (NumberFormatException ex) {
                currentValues.competencyUnits = null;
            }
        }
        competencyUnitsMessageLiveData.postValue(validateCompetencyUnits(false).orElse(null));
    }

    public CourseStatus getStatus() {
        return currentValues.getStatus();
    }

    public synchronized void setStatus(CourseStatus status) {
        currentValues.setStatus(status);
    }

    public String getNotes() {
        return currentValues.getNotes();
    }

    public synchronized void setNotes(String value) {
        currentValues.setNotes(value);
    }

    public String getNormalizedNotes() {
        if (null == normalizedNotes) {
            normalizedNotes = MentorEntity.MULTI_LINE_NORMALIZER.apply(currentValues.notes);
            if (normalizedNotes.equals(currentValues.notes)) {
                currentValues.notes = null;
            }
        }
        return normalizedNotes;
    }

    public ArrayList<TermCourseListItem> getCoursesForTerm() {
        return coursesForTerm;
    }

    public LiveData<CourseDetails> getEntityLiveData() {
        return entityLiveData;
    }

    public LiveData<List<MentorListItem>> getMentorsLiveData() {
        return mentorsLiveData;
    }

    public LiveData<List<TermListItem>> getTermsLiveData() {
        return termsLiveData;
    }

    public MutableLiveData<LocalDate> getEffectiveStartLiveData() {
        return effectiveStartLiveData;
    }

    public MutableLiveData<LocalDate> getEffectiveEndLiveData() {
        return effectiveEndLiveData;
    }

    public LiveData<Boolean> getTermValidLiveData() {
        return termValidLiveData;
    }

    public LiveData<Boolean> getNumberValidLiveData() {
        return numberValidLiveData;
    }

    public LiveData<Boolean> getTitleValidLiveData() {
        return titleValidLiveData;
    }

    public LiveData<Integer> getExpectedStartErrorMessageLiveData() {
        return expectedStartErrorMessageLiveData;
    }

    public LiveData<Integer> getExpectedStartWarningMessageLiveData() {
        return expectedStartWarningMessageLiveData;
    }

    public LiveData<Integer> getExpectedEndMessageLiveData() {
        return expectedEndMessageLiveData;
    }

    public LiveData<Integer> getActualStartErrorMessageLiveData() {
        return actualStartErrorMessageLiveData;
    }

    public LiveData<Integer> getActualStartWarningMessageLiveData() {
        return actualStartWarningMessageLiveData;
    }

    public LiveData<Integer> getActualEndMessageLiveData() {
        return actualEndMessageLiveData;
    }

    public LiveData<Integer> getCompetencyUnitsMessageLiveData() {
        return competencyUnitsMessageLiveData;
    }

    public LiveData<Function<Resources, String>> getTitleFactoryLiveData() {
        return titleFactoryLiveData;
    }

    public MutableLiveData<Function<Resources, Spanned>> getOverviewFactoryLiveData() {
        return overviewFactoryLiveData;
    }

    public synchronized LiveData<List<AssessmentEntity>> getAssessments() {
        if (null == assessments) {
            assessments = dbLoader.getAssessmentsLiveDataByCourseId(courseEntity.getId());
        }
        return assessments;
    }

    public synchronized LiveData<List<AlertListItem>> getAllAlerts() {
        long id = courseEntity.getId();
        if (id != ID_NEW) {
            return dbLoader.getAllAlertsByCourseId(id);
        }
        MutableLiveData<List<AlertListItem>> result = new MutableLiveData<>();
        result.postValue(Collections.emptyList());
        return result;
    }

    public synchronized LiveData<List<CourseAlert>> getAllCourseAlerts() {
        long id = courseEntity.getId();
        if (id != ID_NEW) {
            return dbLoader.getAlertsByCourseId(id);
        }
        MutableLiveData<List<CourseAlert>> result = new MutableLiveData<>();
        result.postValue(Collections.emptyList());
        return result;
    }

    public boolean isFromInitializedState() {
        return fromInitializedState;
    }

    public synchronized Single<CourseDetails> initializeViewModelState(@Nullable Bundle savedInstanceState, Supplier<Bundle> getArguments) {
        fromInitializedState = null != savedInstanceState && savedInstanceState.getBoolean(STATE_KEY_STATE_INITIALIZED, false);
        Bundle state = (fromInitializedState) ? savedInstanceState : getArguments.get();
        courseEntity = new CourseDetails(null);
        viewTitle = null;
        resetOverview();
        if (null != state) {
            Log.d(LOG_TAG, (fromInitializedState) ? "Restoring currentValues from saved state" : "Initializing currentValues from arguments");
            currentValues.restoreState(state, false);
            long id = currentValues.getId();
            if (ID_NEW == id || fromInitializedState) {
                Log.d(LOG_TAG, "Restoring courseEntity from saved state");
                courseEntity.restoreState(state, fromInitializedState);
            } else {
                Log.d(LOG_TAG, "Loading courseEntity from database");
                competencyUnitsText = (null == currentValues.competencyUnits) ? "" : NumberFormat.getIntegerInstance().format(currentValues.competencyUnits);
                return dbLoader.getCourseById(id)
                        .doOnSuccess(this::onEntityLoadedFromDb);
            }
            if (fromInitializedState) {
                Log.d(LOG_TAG, "Restoring competencyUnitsText from saved state");
                setCompetencyUnitsText(state.getString(STATE_KEY_COMPETENCY_UNITS_TEXT, ""));
            } else {
                Log.d(LOG_TAG, "Initializing competencyUnitsText from currentValues");
                competencyUnitsText = (null == currentValues.competencyUnits) ? "" : NumberFormat.getIntegerInstance().format(currentValues.competencyUnits);
            }
        } else {
            Log.d(LOG_TAG, "No saved state or arguments");
            competencyUnitsText = "";
        }
        onEntityLoaded();
        return Single.just(courseEntity).observeOn(AndroidSchedulers.mainThread());
    }

    private void onEntityLoadedFromDb(CourseDetails entity) {
        Log.d(LOG_TAG, String.format("Loaded %s from database", entity));
        courseEntity = entity;
        setSelectedTerm(entity.getTerm());
        setSelectedMentor(entity.getMentor());
        setNumber(entity.getNumber());
        setTitle(entity.getTitle());
        setStatus(entity.getStatus());
        setExpectedStart(entity.getExpectedStart());
        setExpectedEnd(entity.getExpectedEnd());
        setActualStart(entity.getActualStart());
        setActualEnd(entity.getActualEnd());
        setCompetencyUnitsText(NumberFormat.getIntegerInstance().format(entity.getCompetencyUnits()));
        setNotes(entity.getNotes());
        onEntityLoaded();
    }

    private synchronized void resetOverview() {
        overview = null;
    }

    @NonNull
    public synchronized Spanned calculateOverview(Resources resources) {
        if (null != overview) {
            return overview;
        }
        CourseStatus status = currentValues.status;
        SpannableStringBuilder result = new SpannableStringBuilder("Status: ");
        result.setSpan(new StyleSpan(Typeface.BOLD), 0, result.length(), SPAN_EXCLUSIVE_EXCLUSIVE);
        result.append(resources.getString(status.displayResourceId())).append("; ")
                .append("Competency Units: ", new StyleSpan(Typeface.BOLD), SPAN_EXCLUSIVE_EXCLUSIVE);
        Integer v = currentValues.competencyUnits;
        if (null == v) {
            result.append(resources.getString((competencyUnitsText.trim().isEmpty()) ? R.string.message_required : R.string.message_invalid_number),
                    new ForegroundColorSpan(resources.getColor(R.color.color_error, null)), SPAN_EXCLUSIVE_EXCLUSIVE);
        } else if (v < 0) {
            result.append(resources.getString(R.string.message_invalid_number),
                    new ForegroundColorSpan(resources.getColor(R.color.color_error, null)), SPAN_EXCLUSIVE_EXCLUSIVE);
        } else {
            result.append(NUMBER_FORMATTER.format(v));
        }
        int position = result.append("\n").length();
        LocalDate date = currentValues.getActualStart();
        if (null != date) {
            result.append("Started on: ", new StyleSpan(Typeface.BOLD), SPAN_EXCLUSIVE_EXCLUSIVE);
        } else if (null != (date = currentValues.getExpectedStart())) {
            result.append("Expected Start: ", new StyleSpan(Typeface.BOLD), SPAN_EXCLUSIVE_EXCLUSIVE)
                    .append(DATE_FORMATTER.format(date));
            switch (status) {
                case IN_PROGRESS:
                case NOT_PASSED:
                case PASSED:
                    result.append(DATE_FORMATTER.format(date)).append(" (actual start date missing)",
                            new ForegroundColorSpan(resources.getColor(R.color.color_error, null)), SPAN_EXCLUSIVE_EXCLUSIVE);
                    break;
                default:
                    result.append(DATE_FORMATTER.format(date));
                    break;
            }
        } else {
            switch (status) {
                case IN_PROGRESS:
                case NOT_PASSED:
                case PASSED:
                    result.append("Started on: ", new StyleSpan(Typeface.BOLD), SPAN_EXCLUSIVE_EXCLUSIVE)
                            .append(resources.getString(R.string.message_required),
                                    new ForegroundColorSpan(resources.getColor(R.color.color_error, null)), SPAN_EXCLUSIVE_EXCLUSIVE);
                    break;
                default:
                    break;
            }
        }
        date = currentValues.getActualEnd();
        if (null != date) {
            if (position > result.length()) {
                result.append("; ");
            }
            result.append("Ended On: ", new StyleSpan(Typeface.BOLD), SPAN_EXCLUSIVE_EXCLUSIVE);
        } else if (null != (date = currentValues.getExpectedEnd())) {
            if (position > result.length()) {
                result.append("; ");
            }
            result.append("Expected End: ", new StyleSpan(Typeface.BOLD), SPAN_EXCLUSIVE_EXCLUSIVE)
                    .append(DATE_FORMATTER.format(date));
            switch (status) {
                case NOT_PASSED:
                case PASSED:
                    result.append(DATE_FORMATTER.format(date)).append(" (actual end date missing)",
                            new ForegroundColorSpan(resources.getColor(R.color.color_error, null)), SPAN_EXCLUSIVE_EXCLUSIVE);
                    break;
                default:
                    result.append(DATE_FORMATTER.format(date));
                    break;
            }
        } else {
            switch (status) {
                case NOT_PASSED:
                case PASSED:
                    if (position > result.length()) {
                        result.append("; ");
                    }
                    result.append("Ended on: ", new StyleSpan(Typeface.BOLD), SPAN_EXCLUSIVE_EXCLUSIVE)
                            .append(resources.getString(R.string.message_required),
                                    new ForegroundColorSpan(resources.getColor(R.color.color_error, null)), SPAN_EXCLUSIVE_EXCLUSIVE);
                    break;
                default:
                    break;
            }
        }
        if (position > result.length()) {
            result.append("\n");
        }
        if (null != selectedMentor) {
            result.append("Mentor: ", new StyleSpan(Typeface.BOLD), SPAN_EXCLUSIVE_EXCLUSIVE).append(selectedMentor.getName()).append("; ");
        }
        result.append("Term: ", new StyleSpan(Typeface.BOLD), SPAN_EXCLUSIVE_EXCLUSIVE);
        if (null == selectedTerm) {
            result.append(resources.getString(R.string.message_required),
                    new ForegroundColorSpan(resources.getColor(R.color.color_error, null)), SPAN_EXCLUSIVE_EXCLUSIVE);
        } else {
            String n = selectedTerm.getName();
            String t = resources.getString(R.string.format_term, n);
            int i = t.indexOf(':');
            result.append((i > 0 && n.startsWith(t.substring(0, i))) ? n.substring(i).trim() : n);
        }
        overview = result;
        return result;
    }

    @NonNull
    public synchronized String calculateViewTitle(Resources resources) {
        if (null == viewTitle) {
            viewTitle = resources.getString(R.string.format_course, normalizedNumber, normalizedTitle);
        }
        return viewTitle;
    }

    private void onEntityLoaded() {
        titleFactoryLiveData.postValue(this::calculateViewTitle);
        overviewFactoryLiveData.postValue(this::calculateOverview);
        entityLiveData.postValue(courseEntity);
        termsLiveData.observeForever(termsLoadedObserver);
        mentorsLiveData.observeForever(mentorsLoadedObserver);
    }

    public void saveViewModelState(Bundle outState) {
        outState.putBoolean(STATE_KEY_STATE_INITIALIZED, true);
        currentValues.saveState(outState, false);
        courseEntity.saveState(outState, true);
    }

    private void onTermsLoaded(List<TermListItem> termListItems) {
        if (null == termListItems) {
            return;
        }
        Log.d(LOG_TAG, String.format("Loaded %d terms from database", termListItems.size()));
        termsLiveData.removeObserver(termsLoadedObserver);
        EntityHelper.findById(courseEntity.getTermId(), termListItems).ifPresent(t -> {
            courseEntity.setTerm(t);
            setSelectedTerm(t);
        });
    }

    private void onMentorsLoaded(List<MentorListItem> mentorListItems) {
        if (null == mentorListItems) {
            return;
        }
        Log.d(LOG_TAG, String.format("Loaded %d mentors from database", mentorListItems.size()));
        mentorsLiveData.removeObserver(mentorsLoadedObserver);
        EntityHelper.findById(courseEntity.getMentorId(), mentorListItems).ifPresent(t -> {
            courseEntity.setMentor(t);
            setSelectedMentor(t);
        });
    }

    private synchronized void onAllCoursesLoaded(List<TermCourseListItem> termCourseListItems) {
        if (null != termCourseListItems) {
            Log.d(LOG_TAG, String.format("Loaded %d courses from database", termCourseListItems.size()));
            coursesLiveData.removeObserver(coursesLoadedObserver);
            coursesLiveData = null;
            coursesForTerm.clear();
            coursesForTerm.addAll(termCourseListItems);
        }
    }

    private synchronized Optional<Integer> validateExpectedStart() {
        if (null == currentValues.expectedStart) {
            Log.d(LOG_TAG, String.format("Validating expectedStart(null); status=%s", currentValues.status.name()));
            if (currentValues.status == CourseStatus.PLANNED) {
                Log.d(LOG_TAG, "Returning R.string.message_required");
                return Optional.of(R.string.message_required);
            }
        } else {
            Log.d(LOG_TAG, String.format("Validating expectedStart(%s); status=%s", currentValues.expectedStart, currentValues.status.name()));
            switch (currentValues.status) {
                case PLANNED:
                case PASSED:
                case NOT_PASSED:
                    if (null != currentValues.expectedEnd && currentValues.expectedStart.compareTo(currentValues.expectedEnd) > 0) {
                        Log.d(LOG_TAG, "Returning R.string.message_start_after_end");
                        return Optional.of(R.string.message_start_after_end);
                    }
                    break;
                default:
                    break;
            }
        }
        Log.d(LOG_TAG, "validateExpectedStart: Returning empty");
        return Optional.empty();
    }

    private synchronized Optional<Integer> validateExpectedEnd() {
        if (null == currentValues.expectedEnd && null != currentValues.expectedStart && currentValues.status == CourseStatus.IN_PROGRESS) {
            Log.d(LOG_TAG, "validateExpectedEnd: Returning R.string.message_required");
            return Optional.of(R.string.message_required);
        }
        if (null != selectedTerm) {
            if (null != currentValues.expectedEnd) {
                Log.d(LOG_TAG, String.format("Validating expectedEnd(%s); status=%s; selectedTerm=%s", currentValues.expectedStart, currentValues.status.name(), selectedTerm));
                LocalDate d = selectedTerm.getStart();
                if (null != d && d.compareTo(currentValues.expectedEnd) > 0) {
                    Log.d(LOG_TAG, "validateExpectedEnd: Returning R.string.message_before_term_start");
                    return Optional.of(R.string.message_before_term_start);
                }
                d = selectedTerm.getEnd();
                if (null != d && d.compareTo(currentValues.expectedEnd) < 0) {
                    Log.d(LOG_TAG, "validateExpectedEnd: Returning R.string.message_after_term_end");
                    return Optional.of(R.string.message_after_term_end);
                }
            } else {
                Log.d(LOG_TAG, String.format("Validating expectedEnd(null); status=%s; selectedTerm=%s", currentValues.status.name(), selectedTerm));
            }
        } else {
            Log.d(LOG_TAG, (null == currentValues.expectedEnd) ? String.format("Validating expectedEnd(null); status=%s; selectedTerm=null", currentValues.status.name()) :
                    String.format("Validating expectedEnd(%s); status=%s; selectedTerm=null", currentValues.expectedEnd, currentValues.status.name()));
        }
        Log.d(LOG_TAG, "validateExpectedEnd: Returning empty");
        return Optional.empty();
    }

    private synchronized Optional<Integer> validateActualStart() {
        if (null == currentValues.actualStart) {
            Log.d(LOG_TAG, String.format("Validating actualStart(null); status=%s", currentValues.status.name()));
            switch (currentValues.status) {
                case IN_PROGRESS:
                case PASSED:
                case NOT_PASSED:
                    Log.d(LOG_TAG, "Returning R.string.message_required");
                    return Optional.of(R.string.message_required);
                default:
                    break;
            }
        } else {
            Log.d(LOG_TAG, String.format("Validating actualStart(%s); status=%s", currentValues.actualStart, currentValues.status.name()));
            switch (currentValues.status) {
                case PASSED:
                case NOT_PASSED:
                    if (null != currentValues.actualEnd && currentValues.actualStart.compareTo(currentValues.actualEnd) > 0) {
                        Log.d(LOG_TAG, "Returning R.string.message_start_after_end");
                        return Optional.of(R.string.message_start_after_end);
                    }
                    break;
                default:
                    break;
            }
        }
        Log.d(LOG_TAG, "validateActualStart: Returning empty");
        return Optional.empty();
    }

    private synchronized Optional<Integer> validateActualEnd() {
        if (null == currentValues.actualEnd) {
            switch (currentValues.status) {
                case PASSED:
                case NOT_PASSED:
                    Log.d(LOG_TAG, "validateActualEnd: Returning R.string.message_required");
                    return Optional.of(R.string.message_required);
                default:
                    break;
            }
        } else if (null != selectedTerm) {
            Log.d(LOG_TAG, String.format("Validating actualEnd(%s); status=%s; selectedTerm=%s", currentValues.actualEnd, currentValues.status.name(), selectedTerm));
            LocalDate d = selectedTerm.getStart();
            if (null != d && d.compareTo(currentValues.actualEnd) > 0) {
                Log.d(LOG_TAG, "validateActualEnd: Returning R.string.message_before_term_start");
                return Optional.of(R.string.message_before_term_start);
            }
            d = selectedTerm.getEnd();
            if (null != d && d.compareTo(currentValues.actualEnd) < 0) {
                Log.d(LOG_TAG, "validateActualEnd: Returning R.string.message_after_term_end");
                return Optional.of(R.string.message_after_term_end);
            }
        } else {
            Log.d(LOG_TAG, String.format("Validating actualEnd(null); status=%s; selectedTerm=%s", currentValues.status.name(), selectedTerm));
        }

        Log.d(LOG_TAG, "validateActualEnd: Returning empty");
        return Optional.empty();
    }

    private synchronized Optional<Integer> validateCompetencyUnits(boolean saveMode) {
        Log.d(LOG_TAG, (null == currentValues.competencyUnits) ? String.format("Validating competencyUnits(null); text=%s", ToStringBuilder.toEscapedString(competencyUnitsText)) :
                String.format("Validating competencyUnits(%d); text=%s", currentValues.competencyUnits, ToStringBuilder.toEscapedString(competencyUnitsText)));
        if (competencyUnitsText.trim().isEmpty()) {
            Log.d(LOG_TAG, (saveMode) ? "validateCompetencyUnits: Returning R.string.message_competency_units_required" : "validateCompetencyUnits: Returning R.string.message_required");
            return Optional.of((saveMode) ? R.string.message_competency_units_required : R.string.message_required);
        }
        if (null == currentValues.competencyUnits || currentValues.competencyUnits < 0) {
            Log.d(LOG_TAG, (saveMode) ? "validateCompetencyUnits: Returning R.string.message_invalid_competency_units_value" : "validateCompetencyUnits: Returning R.string.message_invalid_number");
            Optional.of((saveMode) ? R.string.message_invalid_competency_units_value : R.string.message_invalid_number);
        }
        Log.d(LOG_TAG, "validateCompetencyUnits: Returning empty");
        return Optional.empty();
    }

    public synchronized Single<ResourceMessageResult> save(boolean ignoreWarnings) {
        if (null == selectedTerm) {
            return Single.just(ValidationMessage.ofSingleError(R.string.message_term_not_selected)).observeOn(AndroidSchedulers.mainThread());
        }
        Integer id = validateCompetencyUnits(true).orElse(null);
        if (null != id) {
            return Single.just(ValidationMessage.ofSingleError(id)).observeOn(AndroidSchedulers.mainThread());
        }
        CourseEntity entity = courseEntity.toEntity();
        entity.setTermId(Objects.requireNonNull(selectedTerm).getId());
        entity.setMentorId((null == selectedMentor) ? null : selectedMentor.getId());
        Log.d(LOG_TAG, String.format("Setting number from %s to %s", ToStringBuilder.toEscapedString(entity.getNumber()), ToStringBuilder.toEscapedString(normalizedNumber)));
        entity.setNumber(normalizedNumber);
        entity.setTitle(normalizedTitle);
        entity.setStatus(currentValues.getStatus());
        entity.setExpectedStart(currentValues.getExpectedStart());
        entity.setExpectedEnd(currentValues.getExpectedEnd());
        entity.setActualStart(currentValues.getActualStart());
        entity.setActualEnd(currentValues.getActualEnd());
        entity.setCompetencyUnits(currentValues.getCompetencyUnits());
        entity.setNotes(currentValues.getNotes());
        Log.d(LOG_TAG, String.format("Saving %s to database", entity));
        return dbLoader.saveCourse(entity, ignoreWarnings).doOnSuccess(m -> {
            if (m.isSucceeded()) {
                courseEntity.applyEntity(entity, Collections.singletonList(selectedTerm), Collections.singletonList(selectedMentor));
            }
        });
    }

    public Single<ResourceMessageResult> delete(boolean ignoreWarnings) {
        Log.d(LOG_TAG, "Enter Erwine.Leonard.T.wguscheduler356334.ui.course.EditCourseViewModel.delete");
        return dbLoader.deleteCourse(Objects.requireNonNull(entityLiveData.getValue()).toEntity(), ignoreWarnings);
    }

    public boolean isChanged() {
        if (ID_NEW != courseEntity.getId() && normalizedNumber.equals(courseEntity.getNumber()) && normalizedTitle.equals(courseEntity.getTitle()) && Objects.equals(getExpectedStart(), courseEntity.getExpectedStart()) &&
                Objects.equals(getExpectedEnd(), courseEntity.getExpectedEnd()) && Objects.equals(getActualStart(), courseEntity.getActualStart()) && Objects.equals(getActualEnd(), courseEntity.getActualEnd()) &&
                currentValues.status == courseEntity.getStatus() && null != currentValues.competencyUnits && currentValues.competencyUnits == courseEntity.getCompetencyUnits()) {
            return !getNormalizedNotes().equals(courseEntity.getNotes());
        }
        return true;
    }

    public AbstractTermEntity<?> initializeTermProperty(List<TermListItem> termListItems) {
        if (null == termListItems || null == courseEntity) {
            return null;
        }
        Log.d(LOG_TAG, "Enter Erwine.Leonard.T.wguscheduler356334.ui.course.EditCourseViewModel.initializeTermProperty");
        Optional<TermListItem> result = EntityHelper.findById(courseEntity.getTermId(), termListItems);
        result.ifPresent(t -> courseEntity.setTerm(t));
        return result.orElse(null);
    }

    public AbstractMentorEntity<?> initializeMentorProperty(List<MentorListItem> mentorListItems) {
        if (null == mentorListItems || null == courseEntity) {
            return null;
        }
        Log.d(LOG_TAG, "Enter Erwine.Leonard.T.wguscheduler356334.ui.course.EditCourseViewModel.initializeMentorProperty");
        Optional<MentorListItem> result = EntityHelper.findById(courseEntity.getMentorId(), mentorListItems);
        result.ifPresent(t -> courseEntity.setMentor(t));
        return result.orElse(null);
    }

    private class CurrentValues implements Course {

        private long id;
        @NonNull
        private String number = "";
        private long termId;
        private Long mentorId;
        @NonNull
        private String title = "";
        private LocalDate expectedStart;
        private LocalDate actualStart;
        private LocalDate expectedEnd;
        private LocalDate actualEnd;
        private CourseStatus status = CourseStatus.UNPLANNED;
        private Integer competencyUnits;
        private String notes = "";

        @Override
        public long getId() {
            return (null == courseEntity) ? id : courseEntity.getId();
        }

        @Override
        public synchronized void setId(long id) {
            Log.d(LOG_TAG, String.format("Setting id to %d", id));
            if (null != courseEntity) {
                courseEntity.setId(id);
            }
            this.id = id;
        }

        @NonNull
        @Override
        public String getNumber() {
            return number;
        }

        @Override
        public synchronized void setNumber(String number) {
            Log.d(LOG_TAG, String.format("Number changing from %s to %s", ToStringBuilder.toEscapedString(this.number), ToStringBuilder.toEscapedString(number)));
            this.number = (null == number) ? "" : number;
            String oldValue = normalizedNumber;
            normalizedNumber = TermEntity.SINGLE_LINE_NORMALIZER.apply(number);
            if (normalizedNumber.isEmpty()) {
                if (!oldValue.isEmpty()) {
                    Log.d(LOG_TAG, "Setting numberValidLiveData to false");
                    numberValidLiveData.postValue(false);
                }
            } else if (oldValue.isEmpty()) {
                Log.d(LOG_TAG, "Setting numberValidLiveData to true");
                numberValidLiveData.postValue(true);
            }
            if (!oldValue.equals(normalizedNumber)) {
                viewTitle = null;
                titleFactoryLiveData.postValue(EditCourseViewModel.this::calculateViewTitle);
            }
            Log.d(LOG_TAG, "Number change complete");
        }

        @Override
        public long getTermId() {
            return termId;
        }

        @Override
        public void setTermId(long termId) {
            this.termId = termId;
        }

        @Nullable
        @Override
        public Long getMentorId() {
            return mentorId;
        }

        @Override
        public void setMentorId(Long mentorId) {
            this.mentorId = mentorId;
        }

        @NonNull
        @Override
        public String getTitle() {
            return title;
        }

        @Override
        public synchronized void setTitle(String title) {
            Log.d(LOG_TAG, String.format("Title changing from %s to %s", ToStringBuilder.toEscapedString(this.title), ToStringBuilder.toEscapedString(title)));
            this.title = (null == title) ? "" : title;
            String oldValue = normalizedTitle;
            normalizedTitle = TermEntity.SINGLE_LINE_NORMALIZER.apply(title);
            if (normalizedTitle.isEmpty()) {
                if (!oldValue.isEmpty()) {
                    Log.d(LOG_TAG, "Setting titleValidLiveData to false");
                    titleValidLiveData.postValue(false);
                }
            } else {
                if (oldValue.isEmpty()) {
                    Log.d(LOG_TAG, "Setting titleValidLiveData to true");
                    titleValidLiveData.postValue(true);
                }
            }
            if (!oldValue.equals(normalizedTitle)) {
                viewTitle = null;
                titleFactoryLiveData.postValue(EditCourseViewModel.this::calculateViewTitle);
            }
            Log.d(LOG_TAG, "Title change complete");
        }

        public synchronized LocalDate getEffectiveStart() {
            switch (status) {
                case UNPLANNED:
                    return null;
                case PLANNED:
                    return expectedStart;
                default:
                    return actualStart;
            }
        }

        @Nullable
        @Override
        public synchronized LocalDate getExpectedStart() {
            if (status == CourseStatus.UNPLANNED) {
                return null;
            }
            return expectedStart;
        }

        @Override
        public synchronized void setExpectedStart(LocalDate expectedStart) {
            if (!Objects.equals(this.expectedStart, expectedStart)) {
                LocalDate oldStart = getEffectiveStart();
                this.expectedStart = expectedStart;
                expectedStartErrorMessageLiveData.postValue(validateExpectedStart().orElse(null));
                expectedEndMessageLiveData.postValue(validateExpectedEnd().orElse(null));
                LocalDate newStart = getEffectiveStart();
                if (!Objects.equals(oldStart, newStart)) {
                    resetOverview();
                    overviewFactoryLiveData.postValue(EditCourseViewModel.this::calculateOverview);
                    effectiveStartLiveData.postValue(newStart);
                }
            }
        }

        @Nullable
        @Override
        public synchronized LocalDate getActualStart() {
            switch (status) {
                case UNPLANNED:
                case PLANNED:
                    return null;
                default:
                    return actualStart;
            }
        }

        @Override
        public synchronized void setActualStart(LocalDate actualStart) {
            if (!Objects.equals(this.actualStart, actualStart)) {
                LocalDate oldStart = getEffectiveStart();
                this.actualStart = actualStart;
                actualStartErrorMessageLiveData.postValue(validateActualStart().orElse(null));
                actualEndMessageLiveData.postValue(validateActualEnd().orElse(null));
                LocalDate newStart = getEffectiveStart();
                if (!Objects.equals(oldStart, newStart)) {
                    resetOverview();
                    overviewFactoryLiveData.postValue(EditCourseViewModel.this::calculateOverview);
                    effectiveStartLiveData.postValue(newStart);
                }
            }
        }

        public synchronized LocalDate getEffectiveEnd() {
            switch (status) {
                case UNPLANNED:
                    return null;
                case PLANNED:
                    return expectedEnd;
                default:
                    return actualEnd;
            }
        }

        @Nullable
        @Override
        public synchronized LocalDate getExpectedEnd() {
            if (status == CourseStatus.UNPLANNED) {
                return null;
            }
            return expectedEnd;
        }

        @Override
        public synchronized void setExpectedEnd(LocalDate expectedEnd) {
            if (!Objects.equals(this.expectedEnd, expectedEnd)) {
                LocalDate oldEnd = getEffectiveEnd();
                this.expectedEnd = expectedEnd;
                expectedEndMessageLiveData.postValue(validateExpectedEnd().orElse(null));
                expectedStartErrorMessageLiveData.postValue(validateExpectedStart().orElse(null));
                LocalDate newEnd = getEffectiveEnd();
                if (!Objects.equals(oldEnd, newEnd)) {
                    resetOverview();
                    overviewFactoryLiveData.postValue(EditCourseViewModel.this::calculateOverview);
                    effectiveEndLiveData.postValue(newEnd);
                }
            }
        }

        @Nullable
        @Override
        public synchronized LocalDate getActualEnd() {
            switch (status) {
                case UNPLANNED:
                case PLANNED:
                    return null;
                default:
                    return actualEnd;
            }
        }

        @Override
        public synchronized void setActualEnd(LocalDate actualEnd) {
            if (!Objects.equals(this.actualEnd, actualEnd)) {
                LocalDate oldEnd = getEffectiveEnd();
                this.actualEnd = actualEnd;
                actualEndMessageLiveData.postValue(validateActualEnd().orElse(null));
                actualStartErrorMessageLiveData.postValue(validateActualStart().orElse(null));
                LocalDate newEnd = getEffectiveEnd();
                if (!Objects.equals(oldEnd, newEnd)) {
                    resetOverview();
                    overviewFactoryLiveData.postValue(EditCourseViewModel.this::calculateOverview);
                    effectiveEndLiveData.postValue(newEnd);
                }
            }
        }

        @NonNull
        @Override
        public CourseStatus getStatus() {
            return status;
        }

        @Override
        public synchronized void setStatus(CourseStatus status) {
            if (null == status) {
                status = CourseStatus.UNPLANNED;
            }
            if (status != this.status) {
                Log.d(LOG_TAG, String.format("Status changing from %s to %s", this.status.name(), status.name()));
                LocalDate oldStart = getEffectiveStart();
                LocalDate oldEnd = getEffectiveEnd();
                this.status = status;
                expectedStartErrorMessageLiveData.postValue(validateExpectedStart().orElse(null));
                actualStartErrorMessageLiveData.postValue(validateActualStart().orElse(null));
                expectedEndMessageLiveData.postValue(validateExpectedEnd().orElse(null));
                actualEndMessageLiveData.postValue(validateActualEnd().orElse(null));
                LocalDate newStart = getEffectiveStart();
                if (!Objects.equals(oldStart, newStart)) {
                    effectiveStartLiveData.postValue(newStart);
                }
                LocalDate newEnd = getEffectiveEnd();
                if (!Objects.equals(oldEnd, newEnd)) {
                    effectiveEndLiveData.postValue(newEnd);
                }
                resetOverview();
                overviewFactoryLiveData.postValue(EditCourseViewModel.this::calculateOverview);
            }
        }

        @Override
        public int getCompetencyUnits() {
            return (null == competencyUnits) ? 0 : competencyUnits;
        }

        @Override
        public synchronized void setCompetencyUnits(int competencyUnits) {
            if (!Objects.equals(this.competencyUnits, competencyUnits)) {
                this.competencyUnits = competencyUnits;
                resetOverview();
                overviewFactoryLiveData.postValue(EditCourseViewModel.this::calculateOverview);
            }
        }

        @NonNull
        @Override
        public String getNotes() {
            return (null == notes) ? normalizedNotes : notes;
        }

        @Override
        public synchronized void setNotes(String notes) {
            if (null == notes || notes.isEmpty()) {
                normalizedNotes = "";
                this.notes = null;
            } else if (!getNotes().equals(notes)) {
                this.notes = notes;
                normalizedNotes = null;
            }
        }

        @NonNull
        @Override
        public String toString() {
            return ToStringBuilder.toEscapedString(this, false);
        }

    }
}