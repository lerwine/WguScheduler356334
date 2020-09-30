package Erwine.Leonard.T.wguscheduler356334.ui.course;

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

import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

import Erwine.Leonard.T.wguscheduler356334.AddCourseActivity;
import Erwine.Leonard.T.wguscheduler356334.R;
import Erwine.Leonard.T.wguscheduler356334.ViewCourseActivity;
import Erwine.Leonard.T.wguscheduler356334.db.AppDb;
import Erwine.Leonard.T.wguscheduler356334.db.DbLoader;
import Erwine.Leonard.T.wguscheduler356334.entity.IdIndexedEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.course.Course;
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
import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;

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
    private final MutableLiveData<String> titleLiveData;
    private final CurrentValues currentValues;
    private final ArrayList<TermCourseListItem> coursesForTerm;
    private LiveData<List<TermCourseListItem>> coursesLiveData;
    private Observer<List<TermListItem>> termsLoadedObserver;
    private Observer<List<MentorListItem>> mentorsLoadedObserver;
    private Observer<List<TermCourseListItem>> coursesLoadedObserver;
    private boolean fromInitializedState;
    private AbstractTermEntity<?> selectedTerm;
    private AbstractMentorEntity<?> selectedMentor;
    private String normalizedNumber = "";
    private String normalizedTitle = "";
    private String normalizedNotes = "";
    private String competencyUnitsText = "";

    public EditCourseViewModel(@NonNull Application application) {
        super(application);
        Log.d(LOG_TAG, "Constructing EditCourseViewModel");
        dbLoader = DbLoader.getInstance(getApplication());
        termsLiveData = dbLoader.getAllTerms();
        mentorsLiveData = dbLoader.getAllMentors();
        currentValues = new CurrentValues();
        entityLiveData = new MutableLiveData<>();
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
        titleLiveData = new MutableLiveData<>("");
        coursesForTerm = new ArrayList<>();
        termsLoadedObserver = this::onTermsLoaded;
        mentorsLoadedObserver = this::onMentorsLoaded;
    }

    public Long getId() {
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
                currentValues.termId = null;
                termValidLiveData.postValue(false);
            } else {
                Long id = selectedTerm.getId();
                currentValues.termId = id;
                if (null != id) {
                    coursesLiveData = dbLoader.getCoursesByTermId(id);
                    coursesLoadedObserver = this::onAllCoursesLoaded;
                    coursesLiveData.observeForever(coursesLoadedObserver);
                    termValidLiveData.postValue(true);
                } else {
                    termValidLiveData.postValue(false);
                }
            }
            expectedStartErrorMessageLiveData.postValue(validateExpectedStart(false).orElse(null));
            actualStartErrorMessageLiveData.postValue(validateActualStart(false).orElse(null));
            expectedEndMessageLiveData.postValue(validateExpectedEnd().orElse(null));
            actualEndMessageLiveData.postValue(validateActualEnd().orElse(null));
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

    public MutableLiveData<CourseDetails> getEntityLiveData() {
        return entityLiveData;
    }

    public LiveData<List<MentorListItem>> getMentorsLiveData() {
        return mentorsLiveData;
    }

    public LiveData<List<TermListItem>> getTermsLiveData() {
        return termsLiveData;
    }

    public MutableLiveData<Boolean> getTermValidLiveData() {
        return termValidLiveData;
    }

    public MutableLiveData<Boolean> getNumberValidLiveData() {
        return numberValidLiveData;
    }

    public MutableLiveData<Boolean> getTitleValidLiveData() {
        return titleValidLiveData;
    }

    public MutableLiveData<Integer> getExpectedStartErrorMessageLiveData() {
        return expectedStartErrorMessageLiveData;
    }

    public MutableLiveData<Integer> getExpectedStartWarningMessageLiveData() {
        return expectedStartWarningMessageLiveData;
    }

    public MutableLiveData<Integer> getExpectedEndMessageLiveData() {
        return expectedEndMessageLiveData;
    }

    public MutableLiveData<Integer> getActualStartErrorMessageLiveData() {
        return actualStartErrorMessageLiveData;
    }

    public MutableLiveData<Integer> getActualStartWarningMessageLiveData() {
        return actualStartWarningMessageLiveData;
    }

    public MutableLiveData<Integer> getActualEndMessageLiveData() {
        return actualEndMessageLiveData;
    }

    public MutableLiveData<Integer> getCompetencyUnitsMessageLiveData() {
        return competencyUnitsMessageLiveData;
    }

    public MutableLiveData<String> getTitleLiveData() {
        return titleLiveData;
    }

    public boolean isFromInitializedState() {
        return fromInitializedState;
    }

    public synchronized Single<CourseDetails> initializeViewModelState(@Nullable Bundle savedInstanceState, Supplier<Bundle> getArguments) {
        fromInitializedState = null != savedInstanceState && savedInstanceState.getBoolean(STATE_KEY_STATE_INITIALIZED, false);
        Bundle state = (fromInitializedState) ? savedInstanceState : getArguments.get();
        courseEntity = new CourseDetails(null);
        if (null != state) {
            Log.d(LOG_TAG, (fromInitializedState) ? "Restoring currentValues from saved state" : "Initializing currentValues from arguments");
            currentValues.restoreState(state, false);
            Long id = currentValues.getId();
            if (null == id || fromInitializedState) {
                Log.d(LOG_TAG, "Restoring courseEntity from saved state");
                courseEntity.restoreState(state, fromInitializedState);
            } else {
                Log.d(LOG_TAG, "Loading courseEntity from database");
                competencyUnitsText = (null == currentValues.competencyUnits) ? "" : NumberFormat.getIntegerInstance().format(currentValues.competencyUnits);
                return dbLoader.getCourseById(id)
                        .doOnSuccess(this::onEntityLoadedFromDb)
                        .doOnError(throwable -> Log.e(getClass().getName(), "Error loading term", throwable));
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

    private void onEntityLoaded() {
        titleLiveData.postValue(courseEntity.getTitle());
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

    private synchronized Optional<Integer> validateExpectedStart(boolean saveMode) {
        if (null == currentValues.expectedStart) {
            Log.d(LOG_TAG, String.format("Validating expectedStart(null); status=%s", currentValues.status.name()));
            if (currentValues.status == CourseStatus.PLANNED) {
                Log.d(LOG_TAG, (saveMode) ? "validateExpectedStart: Returning R.string.message_expected_start_required" : "Returning R.string.message_required");
                return Optional.of((saveMode) ? R.string.message_expected_start_required : R.string.message_required);
            }
        } else {
            Log.d(LOG_TAG, String.format("Validating expectedStart(%s); status=%s", currentValues.expectedStart, currentValues.status.name()));
            switch (currentValues.status) {
                case PLANNED:
                case PASSED:
                case NOT_PASSED:
                    if (null != currentValues.expectedEnd && currentValues.expectedStart.compareTo(currentValues.expectedEnd) > 0) {
                        Log.d(LOG_TAG, (saveMode) ? "validateExpectedStart: Returning R.string.message_expected_start_after_end" : "Returning R.string.message_start_after_end");
                        return Optional.of((saveMode) ? R.string.message_expected_start_after_end : R.string.message_start_after_end);
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

    private synchronized Optional<Integer> validateActualStart(boolean saveMode) {
        if (null == currentValues.actualStart) {
            Log.d(LOG_TAG, String.format("Validating actualStart(null); status=%s", currentValues.status.name()));
            switch (currentValues.status) {
                case IN_PROGRESS:
                case PASSED:
                case NOT_PASSED:
                    Log.d(LOG_TAG, (saveMode) ? "validateActualStart: Returning R.string.message_actual_start_required" : "Returning R.string.message_required");
                    return Optional.of((saveMode) ? R.string.message_actual_start_required : R.string.message_required);
                default:
                    break;
            }
        } else {
            Log.d(LOG_TAG, String.format("Validating actualStart(%s); status=%s", currentValues.actualStart, currentValues.status.name()));
            switch (currentValues.status) {
                case PASSED:
                case NOT_PASSED:
                    if (null != currentValues.actualEnd && currentValues.actualStart.compareTo(currentValues.actualEnd) > 0) {
                        Log.d(LOG_TAG, (saveMode) ? "validateActualStart: Returning R.string.message_actual_start_after_end" : "Returning R.string.message_start_after_end");
                        return Optional.of((saveMode) ? R.string.message_actual_start_after_end : R.string.message_start_after_end);
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

    public synchronized Single<List<Integer>> save() {
        ArrayList<Integer> errors = new ArrayList<>();
        if (null == selectedTerm) {
            errors.add(R.string.message_term_not_selected);
        }
        if (normalizedNumber.isEmpty()) {
            errors.add(R.string.message_course_number_required);
        }
        if (normalizedTitle.isEmpty()) {
            errors.add(R.string.message_title_required);
        }
        validateExpectedStart(true).ifPresent(errors::add);
        validateExpectedEnd().ifPresent(t -> {
            if (t == R.string.message_required) {
                errors.add(R.string.message_expected_end_required);
            }
        });
        validateActualStart(true).ifPresent(errors::add);
        validateActualEnd().ifPresent(t -> {
            if (t == R.string.message_required) {
                errors.add(R.string.message_actual_end_required);
            }
        });
        validateCompetencyUnits(true).ifPresent(errors::add);
        if (!errors.isEmpty()) {
            Log.d(LOG_TAG, String.format("Returning %d errors", errors.size()));
            return Single.just(errors);
        }
        CourseEntity entity = courseEntity.toEntity();
        entity.setTermId(Objects.requireNonNull(Objects.requireNonNull(selectedTerm).getId()));
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
        return dbLoader.saveCourse(entity).toSingleDefault(Collections.emptyList());
    }

    public Completable delete() {
        Log.d(LOG_TAG, "Enter Erwine.Leonard.T.wguscheduler356334.ui.course.EditCourseViewModel.delete");
        return dbLoader.deleteCourse(Objects.requireNonNull(entityLiveData.getValue()).toEntity()).doOnError(throwable -> Log.e(getClass().getName(),
                "Error deleting course", throwable));
    }

    public boolean isChanged() {
        if (null != courseEntity.getId() && normalizedNumber.equals(courseEntity.getNumber()) && normalizedTitle.equals(courseEntity.getTitle()) && Objects.equals(getExpectedStart(), courseEntity.getExpectedStart()) &&
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

        private Long id;
        private String number = "";
        private Long termId;
        private Long mentorId;
        private String title = "";
        private LocalDate expectedStart;
        private LocalDate actualStart;
        private LocalDate expectedEnd;
        private LocalDate actualEnd;
        private CourseStatus status = CourseStatus.UNPLANNED;
        private Integer competencyUnits;
        private String notes = "";

        @Nullable
        @Override
        public Long getId() {
            return (null == courseEntity) ? id : courseEntity.getId();
        }

        @Override
        public void setId(Long id) {
            Log.d(LOG_TAG, (null == id) ? "Setting id to null" : String.format("Setting id to %d", id));
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
        public void setNumber(String number) {
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
            Log.d(LOG_TAG, "Number change complete");
        }

        @Nullable
        @Override
        public Long getTermId() {
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
        public void setTitle(String title) {
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
                titleLiveData.postValue(normalizedTitle);
                if (oldValue.isEmpty()) {
                    Log.d(LOG_TAG, "Setting titleValidLiveData to true");
                    titleValidLiveData.postValue(true);
                }
            }
            titleLiveData.postValue(normalizedTitle);
            Log.d(LOG_TAG, "Title change complete");
        }

        @Nullable
        @Override
        public LocalDate getExpectedStart() {
            if (status == CourseStatus.UNPLANNED) {
                return null;
            }
            return expectedStart;
        }

        @Override
        public void setExpectedStart(LocalDate expectedStart) {
            if (!Objects.equals(this.expectedStart, expectedStart)) {
                this.expectedStart = expectedStart;
                expectedStartErrorMessageLiveData.postValue(validateExpectedStart(false).orElse(null));
                expectedEndMessageLiveData.postValue(validateExpectedEnd().orElse(null));
            }
        }

        @Nullable
        @Override
        public LocalDate getActualStart() {
            switch (status) {
                case UNPLANNED:
                case PLANNED:
                    return null;
                default:
                    return actualStart;
            }
        }

        @Override
        public void setActualStart(LocalDate actualStart) {
            if (!Objects.equals(this.actualStart, actualStart)) {
                this.actualStart = actualStart;
                actualStartErrorMessageLiveData.postValue(validateActualStart(false).orElse(null));
                actualEndMessageLiveData.postValue(validateActualEnd().orElse(null));
            }
        }

        @Nullable
        @Override
        public LocalDate getExpectedEnd() {
            if (status == CourseStatus.UNPLANNED) {
                return null;
            }
            return expectedEnd;
        }

        @Override
        public void setExpectedEnd(LocalDate expectedEnd) {
            if (!Objects.equals(this.expectedEnd, expectedEnd)) {
                this.expectedEnd = expectedEnd;
                expectedEndMessageLiveData.postValue(validateExpectedEnd().orElse(null));
                expectedStartErrorMessageLiveData.postValue(validateExpectedStart(false).orElse(null));
            }
        }

        @Nullable
        @Override
        public LocalDate getActualEnd() {
            switch (status) {
                case UNPLANNED:
                case PLANNED:
                    return null;
                default:
                    return actualEnd;
            }
        }

        @Override
        public void setActualEnd(LocalDate actualEnd) {
            if (!Objects.equals(this.actualEnd, actualEnd)) {
                this.actualEnd = actualEnd;
                actualEndMessageLiveData.postValue(validateActualEnd().orElse(null));
                actualStartErrorMessageLiveData.postValue(validateActualStart(false).orElse(null));
            }
        }

        @NonNull
        @Override
        public CourseStatus getStatus() {
            return status;
        }

        @Override
        public void setStatus(CourseStatus status) {
            if (null == status) {
                status = CourseStatus.UNPLANNED;
            }
            if (status != this.status) {
                Log.d(LOG_TAG, String.format("Status changing from %s to %s", this.status.name(), status.name()));
                this.status = status;
                expectedStartErrorMessageLiveData.postValue(validateExpectedStart(false).orElse(null));
                actualStartErrorMessageLiveData.postValue(validateActualStart(false).orElse(null));
                expectedEndMessageLiveData.postValue(validateExpectedEnd().orElse(null));
                actualEndMessageLiveData.postValue(validateActualEnd().orElse(null));
            }
        }

        @Override
        public int getCompetencyUnits() {
            return (null == competencyUnits) ? 0 : competencyUnits;
        }

        @Override
        public void setCompetencyUnits(int competencyUnits) {
            this.competencyUnits = competencyUnits;
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

        @NonNull
        @Override
        public String toString() {
            return ToStringBuilder.toEscapedString(this, false);
        }

    }
}