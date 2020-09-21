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
import Erwine.Leonard.T.wguscheduler356334.db.DbLoader;
import Erwine.Leonard.T.wguscheduler356334.entity.CourseDetails;
import Erwine.Leonard.T.wguscheduler356334.entity.CourseEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.CourseStatus;
import Erwine.Leonard.T.wguscheduler356334.entity.MentorEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.MentorListItem;
import Erwine.Leonard.T.wguscheduler356334.entity.TermEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.TermListItem;
import Erwine.Leonard.T.wguscheduler356334.ui.term.EditTermViewModel;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class EditCourseViewModel extends AndroidViewModel {
    private static final String LOG_TAG = EditTermViewModel.class.getName();
    static final String STATE_KEY_STATE_INITIALIZED = "state_initialized";
    public static final String STATE_KEY_COMPETENCY_UNITS_TEXT = "t:" + CourseDetails.STATE_KEY_COMPETENCY_UNITS;

    public static void startAddCourseActivity(@NonNull Context context, long termId, @NonNull LocalDate nextStart) {
        Intent intent = new Intent(context, AddCourseActivity.class);
        intent.putExtra(TermEntity.STATE_KEY_ID, termId);
        intent.putExtra(CourseDetails.COLNAME_EXPECTED_START, nextStart.toEpochDay());
        context.startActivity(intent);
    }

    public static void startViewCourseActivity(@NonNull Context context, long courseId) {
        Intent intent = new Intent(context, ViewCourseActivity.class);
        intent.putExtra(CourseDetails.STATE_KEY_ID, courseId);
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
    private final MutableLiveData<Integer> expectedStartMessageLiveData;
    private final MutableLiveData<Boolean> expectedEndValidLiveData;
    private final MutableLiveData<Integer> actualStartMessageLiveData;
    private final MutableLiveData<Boolean> actualEndValidLiveData;
    private final MutableLiveData<Integer> competencyUnitsMessageLiveData;
    private boolean fromInitializedState;
    private String number;
    private Long termId;
    private Long mentorId;
    private String normalizedNumber;
    private String title;
    private String normalizedTitle;
    private LocalDate expectedStart;
    private LocalDate actualStart;
    private LocalDate expectedEnd;
    private LocalDate actualEnd;
    private CourseStatus status;
    private String competencyUnitsText;
    private Integer competencyUnitsValue;
    private String notes;
    private String normalizedNotes;

    public EditCourseViewModel(@NonNull Application application) {
        super(application);
        dbLoader = DbLoader.getInstance(getApplication());
        termsLiveData = dbLoader.getAllTerms();
        mentorsLiveData = dbLoader.getAllMentors();
        number = normalizedNumber = title = normalizedTitle = competencyUnitsText = notes = normalizedNotes = "";
        status = CourseStatus.UNPLANNED;
        entityLiveData = new MutableLiveData<>();
        termValidLiveData = new MutableLiveData<>(false);
        numberValidLiveData = new MutableLiveData<>(false);
        titleValidLiveData = new MutableLiveData<>(false);
        expectedStartMessageLiveData = new MutableLiveData<>();
        expectedEndValidLiveData = new MutableLiveData<>(false);
        actualStartMessageLiveData = new MutableLiveData<>();
        actualEndValidLiveData = new MutableLiveData<>(false);
        competencyUnitsMessageLiveData = new MutableLiveData<>();
    }

    public Long getId() {
        return (null == courseEntity) ? null : courseEntity.getId();
    }

    public Long getTermId() {
        return termId;
    }

    public void setTermId(@Nullable Long termId) {
        if (!Objects.equals(this.termId, termId)) {
            this.termId = termId;
            termValidLiveData.postValue(null != termId);
        }
    }

    public Long getMentorId() {
        return mentorId;
    }

    public void setMentorId(@Nullable Long mentorId) {
        this.mentorId = mentorId;
    }

    public String getNumber() {
        return number;
    }

    public synchronized void setNumber(String value) {
        number = (null == value) ? "" : value;
        String oldValue = normalizedNumber;
        normalizedNumber = TermEntity.SINGLE_LINE_NORMALIZER.apply(value);
        if (normalizedNumber.isEmpty()) {
            if (!oldValue.isEmpty()) {
                numberValidLiveData.postValue(false);
            }
        } else if (oldValue.isEmpty()) {
            numberValidLiveData.postValue(true);
        }
    }

    public String getTitle() {
        return title;
    }

    public synchronized void setTitle(String value) {
        title = (null == value) ? "" : value;
        String oldValue = normalizedTitle;
        normalizedTitle = TermEntity.SINGLE_LINE_NORMALIZER.apply(value);
        if (normalizedTitle.isEmpty()) {
            if (!oldValue.isEmpty()) {
                titleValidLiveData.postValue(false);
            }
        } else if (oldValue.isEmpty()) {
            titleValidLiveData.postValue(true);
        }
    }

    public LocalDate getExpectedStart() {
        if (status == CourseStatus.UNPLANNED) {
            return null;
        }
        return expectedStart;
    }

    public synchronized void setExpectedStart(LocalDate value) {
        if (!Objects.equals(value, expectedStart)) {
            expectedStart = value;
            expectedStartMessageLiveData.postValue(validateExpectedStart(false).orElse(null));
        }
    }

    public LocalDate getActualStart() {
        switch (status) {
            case UNPLANNED:
            case PLANNED:
                return null;
            default:
                return actualStart;
        }
    }

    public synchronized void setActualStart(LocalDate value) {
        if (!Objects.equals(value, actualStart)) {
            actualStart = value;
            expectedStartMessageLiveData.postValue(validateExpectedStart(false).orElse(null));
        }
    }

    public LocalDate getExpectedEnd() {
        if (status == CourseStatus.UNPLANNED) {
            return null;
        }
        return expectedEnd;
    }

    public synchronized void setExpectedEnd(LocalDate value) {
        if (!Objects.equals(value, expectedEnd)) {
            expectedEnd = value;
            expectedEndValidLiveData.postValue(validateExpectedEnd());
        }
    }

    public LocalDate getActualEnd() {
        switch (status) {
            case UNPLANNED:
            case PLANNED:
            case IN_PROGRESS:
                return null;
            default:
                return actualEnd;
        }
    }

    public synchronized void setActualEnd(LocalDate value) {
        if (!Objects.equals(value, actualEnd)) {
            actualEnd = value;
            actualEndValidLiveData.postValue(validateActualEnd());
        }
    }

    public String getCompetencyUnitsText() {
        return competencyUnitsText;
    }

    public synchronized void setCompetencyUnitsText(String value) {
        if (null == value) {
            if (competencyUnitsText.isEmpty()) {
                return;
            }
            competencyUnitsText = "";
        } else {
            if (competencyUnitsText.equals(value)) {
                return;
            }
            competencyUnitsText = value;
            try {
                competencyUnitsValue = Integer.parseInt(competencyUnitsText.trim());
            } catch (NumberFormatException ex) {
                competencyUnitsValue = null;
            }
        }
        competencyUnitsMessageLiveData.postValue(validateCompetencyUnits(false).orElse(null));
    }

    public CourseStatus getStatus() {
        return status;
    }

    public synchronized void setStatus(CourseStatus status) {
        if (null == status) {
            status = CourseStatus.UNPLANNED;
        }
        if (status != this.status) {
            this.status = status;
            expectedStartMessageLiveData.postValue(validateExpectedStart(false).orElse(null));
            actualStartMessageLiveData.postValue(validateActualStart(false).orElse(null));
            expectedEndValidLiveData.postValue(validateExpectedEnd());
            actualEndValidLiveData.postValue(validateActualEnd());
        }
    }

    public String getNotes() {
        return notes;
    }

    public synchronized void setNotes(String value) {
        if (null == value || value.isEmpty()) {
            normalizedNotes = notes = "";
        } else if (!value.equals(notes)) {
            notes = value;
            normalizedNotes = null;
        }
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

    public MutableLiveData<Integer> getExpectedStartMessageLiveData() {
        return expectedStartMessageLiveData;
    }

    public MutableLiveData<Boolean> getExpectedEndValidLiveData() {
        return expectedEndValidLiveData;
    }

    public MutableLiveData<Integer> getActualStartMessageLiveData() {
        return actualStartMessageLiveData;
    }

    public MutableLiveData<Boolean> getActualEndValidLiveData() {
        return actualEndValidLiveData;
    }

    public MutableLiveData<Integer> getCompetencyUnitsMessageLiveData() {
        return competencyUnitsMessageLiveData;
    }

    public boolean isFromInitializedState() {
        return fromInitializedState;
    }

    public synchronized Single<CourseDetails> initializeViewModelState(@Nullable Bundle savedInstanceState, Supplier<Bundle> getArguments) {
        fromInitializedState = null != savedInstanceState && savedInstanceState.getBoolean(STATE_KEY_STATE_INITIALIZED, false);
        Bundle state = (fromInitializedState) ? savedInstanceState : getArguments.get();
        if (null == state) {
            courseEntity = new CourseDetails(null);
        } else if (state.containsKey(CourseDetails.STATE_KEY_ID)) {
            if (fromInitializedState) {
                courseEntity = new CourseDetails(state, true);
            } else {
                return dbLoader.getCourseById(state.getLong(CourseDetails.STATE_KEY_ID))
                        .doOnSuccess(this::onEntityLoadedFromDb)
                        .doOnError(throwable -> Log.e(getClass().getName(), "Error loading term", throwable));
            }
        } else {
            courseEntity = new CourseDetails(state, fromInitializedState && state.containsKey(CourseDetails.STATE_KEY_ORIGINAL_TERM_ID));
        }
        if (null == state) {
            setNumber(courseEntity.getNumber());
            setTitle(courseEntity.getTitle());
            setCompetencyUnitsText(NumberFormat.getIntegerInstance().format(courseEntity.getCompetencyUnits()));
            setStatus(courseEntity.getStatus());
            setTermId(courseEntity.getTermId());
            setMentorId(courseEntity.getMentorId());
            setExpectedStart(courseEntity.getExpectedStart());
            setExpectedEnd(courseEntity.getExpectedEnd());
            setActualStart(courseEntity.getActualStart());
            setActualEnd(courseEntity.getActualEnd());
            setNotes(courseEntity.getNotes());
        } else if (fromInitializedState) {
            setNumber(state.getString(CourseDetails.STATE_KEY_NUMBER, ""));
            setTitle(state.getString(CourseDetails.STATE_KEY_TITLE, ""));
            setCompetencyUnitsText(state.getString(STATE_KEY_COMPETENCY_UNITS_TEXT, ""));
            setStatus(CourseStatus.valueOf(state.getString(CourseDetails.STATE_KEY_STATUS, CourseStatus.UNPLANNED.name())));
            setTermId((state.containsKey(TermEntity.STATE_KEY_ID)) ? state.getLong(TermEntity.STATE_KEY_ID) : null);
            setMentorId((state.containsKey(MentorEntity.STATE_KEY_ID)) ? state.getLong(MentorEntity.STATE_KEY_ID) : null);
            setExpectedStart((state.containsKey(CourseDetails.STATE_KEY_EXPECTED_START)) ? LocalDate.ofEpochDay(state.getLong(CourseDetails.STATE_KEY_EXPECTED_START)) : null);
            setExpectedEnd((state.containsKey(CourseDetails.STATE_KEY_EXPECTED_END)) ? LocalDate.ofEpochDay(state.getLong(CourseDetails.STATE_KEY_EXPECTED_END)) : null);
            setActualStart((state.containsKey(CourseDetails.STATE_KEY_ACTUAL_START)) ? LocalDate.ofEpochDay(state.getLong(CourseDetails.STATE_KEY_ACTUAL_START)) : null);
            setActualEnd((state.containsKey(CourseDetails.STATE_KEY_ACTUAL_END)) ? LocalDate.ofEpochDay(state.getLong(CourseDetails.STATE_KEY_ACTUAL_END)) : null);
            setNotes(state.getString(CourseDetails.STATE_KEY_NOTES, ""));
        } else {
            setNumber((state.containsKey(CourseDetails.STATE_KEY_NUMBER)) ? state.getString(CourseDetails.STATE_KEY_NUMBER) : courseEntity.getNumber());
            setTitle((state.containsKey(CourseDetails.STATE_KEY_TITLE)) ? state.getString(CourseDetails.STATE_KEY_TITLE) : courseEntity.getTitle());
            setCompetencyUnitsText(NumberFormat.getIntegerInstance().format(courseEntity.getCompetencyUnits()));
            setStatus((state.containsKey(CourseDetails.STATE_KEY_STATUS)) ? CourseStatus.valueOf(state.getString(CourseDetails.STATE_KEY_STATUS, CourseStatus.UNPLANNED.name())) : courseEntity.getStatus());
            setTermId((state.containsKey(TermEntity.STATE_KEY_ID)) ? state.getLong(TermEntity.STATE_KEY_ID) : courseEntity.getTermId());
            setMentorId((state.containsKey(MentorEntity.STATE_KEY_ID)) ? (Long) state.getLong(MentorEntity.STATE_KEY_ID) : courseEntity.getMentorId());
            setExpectedStart((state.containsKey(CourseDetails.STATE_KEY_EXPECTED_START)) ? LocalDate.ofEpochDay(state.getLong(CourseDetails.STATE_KEY_EXPECTED_START)) : courseEntity.getExpectedStart());
            setExpectedEnd((state.containsKey(CourseDetails.STATE_KEY_EXPECTED_END)) ? LocalDate.ofEpochDay(state.getLong(CourseDetails.STATE_KEY_EXPECTED_END)) : courseEntity.getExpectedEnd());
            setActualStart((state.containsKey(CourseDetails.STATE_KEY_ACTUAL_START)) ? LocalDate.ofEpochDay(state.getLong(CourseDetails.STATE_KEY_ACTUAL_START)) : courseEntity.getActualStart());
            setActualEnd((state.containsKey(CourseDetails.STATE_KEY_ACTUAL_END)) ? LocalDate.ofEpochDay(state.getLong(CourseDetails.STATE_KEY_ACTUAL_END)) : courseEntity.getActualEnd());
            setNotes((state.containsKey(CourseDetails.STATE_KEY_NOTES)) ? state.getString(CourseDetails.STATE_KEY_NOTES) : courseEntity.getNotes());
        }
        entityLiveData.postValue(courseEntity);
        return Single.just(courseEntity).observeOn(AndroidSchedulers.mainThread());
    }

    public void saveViewModelState(Bundle outState) {
        outState.putBoolean(STATE_KEY_STATE_INITIALIZED, true);
        courseEntity.saveState(outState, true);
        if (null != courseEntity.getId()) {
            outState.putLong(CourseDetails.STATE_KEY_ID, courseEntity.getId());
        }
        Long id = termId;
        if (null != id) {
            outState.putLong(TermEntity.STATE_KEY_ID, id);
        }
        id = mentorId;
        if (null != id) {
            outState.putLong(MentorEntity.STATE_KEY_ID, id);
        }
        outState.putString(CourseDetails.STATE_KEY_STATUS, status.name());
        Integer i = getCompetencyUnitsMessageLiveData().getValue();
        if (null != i) {
            outState.putInt(CourseDetails.STATE_KEY_COMPETENCY_UNITS, i);
        }
        outState.putString(STATE_KEY_COMPETENCY_UNITS_TEXT, getCompetencyUnitsText());
        outState.putString(CourseDetails.STATE_KEY_NUMBER, number);
        LocalDate date = getExpectedStart();
        if (null != date) {
            outState.putLong(CourseDetails.STATE_KEY_EXPECTED_START, date.toEpochDay());
        }
        date = getExpectedEnd();
        if (null != date) {
            outState.putLong(CourseDetails.STATE_KEY_EXPECTED_END, date.toEpochDay());
        }
        date = getActualStart();
        if (null != date) {
            outState.putLong(CourseDetails.STATE_KEY_ACTUAL_START, date.toEpochDay());
        }
        date = getActualEnd();
        if (null != date) {
            outState.putLong(CourseDetails.STATE_KEY_ACTUAL_END, date.toEpochDay());
        }
        outState.putString(CourseDetails.STATE_KEY_NOTES, notes);
    }

    private void onEntityLoadedFromDb(CourseDetails entity) {
        courseEntity = entity;
        setTermId(entity.getTermId());
        setMentorId(entity.getMentorId());
        setNumber(entity.getNumber());
        setTitle(entity.getTitle());
        setStatus(entity.getStatus());
        setExpectedStart(entity.getExpectedStart());
        setExpectedEnd(entity.getExpectedEnd());
        setActualStart(entity.getActualStart());
        setActualEnd(entity.getActualEnd());
        setCompetencyUnitsText(NumberFormat.getIntegerInstance().format(entity.getCompetencyUnits()));
        setNotes(entity.getNotes());
        entityLiveData.postValue(entity);
    }

    private synchronized Optional<Integer> validateExpectedStart(boolean saveMode) {
        if (null == expectedStart) {
            if (status == CourseStatus.PLANNED) {
                return Optional.of((saveMode) ? R.string.message_expected_start_required : R.string.message_required);
            }
        } else {
            switch (status) {
                case PLANNED:
                case PASSED:
                case NOT_PASSED:
                    if (null != expectedEnd && expectedStart.compareTo(expectedEnd) > 0) {
                        return Optional.of((saveMode) ? R.string.message_expected_start_after_end : R.string.message_start_after_end);
                    }
                    break;
                default:
                    break;
            }
        }
        return Optional.empty();
    }

    private synchronized boolean validateExpectedEnd() {
        return null != expectedEnd || status != CourseStatus.IN_PROGRESS;
    }

    private synchronized Optional<Integer> validateActualStart(boolean saveMode) {
        if (null == actualStart) {
            switch (status) {
                case IN_PROGRESS:
                case PASSED:
                case NOT_PASSED:
                    return Optional.of((saveMode) ? R.string.message_actual_start_required : R.string.message_required);
                default:
                    break;
            }
        } else {
            switch (status) {
                case PASSED:
                case NOT_PASSED:
                    if (null != actualEnd && actualStart.compareTo(actualEnd) > 0) {
                        return Optional.of((saveMode) ? R.string.message_actual_start_after_end : R.string.message_start_after_end);
                    }
                    break;
                default:
                    break;
            }
        }
        return Optional.empty();
    }

    private synchronized boolean validateActualEnd() {
        if (null == actualEnd) {
            switch (status) {
                case PASSED:
                case NOT_PASSED:
                    return false;
                default:
                    break;
            }
        }

        return true;
    }

    private synchronized Optional<Integer> validateCompetencyUnits(boolean saveMode) {
        if (competencyUnitsText.trim().isEmpty()) {
            return Optional.of((saveMode) ? R.string.message_competency_units_required : R.string.message_required);
        }
        if (null == competencyUnitsValue) {
            Optional.of((saveMode) ? R.string.message_invalid_competency_units_value : R.string.message_invalid_number);
        }
        return Optional.empty();
    }

    public synchronized Single<List<Integer>> save() {
        ArrayList<Integer> errors = new ArrayList<>();
        if (null == termId) {
            errors.add(R.string.message_term_not_selected);
        }
        if (normalizedNumber.isEmpty()) {
            errors.add(R.string.message_course_number_required);
        }
        if (normalizedTitle.isEmpty()) {
            errors.add(R.string.message_title_required);
        }
        validateExpectedStart(true).ifPresent(errors::add);
        if (!validateExpectedEnd()) {
            errors.add(R.string.message_expected_end_required);
        }
        validateActualStart(true).ifPresent(errors::add);
        if (!validateActualEnd()) {
            errors.add(R.string.message_actual_end_required);
        }
        validateCompetencyUnits(true).ifPresent(errors::add);
        if (!errors.isEmpty()) {
            return Single.just(errors);
        }
        CourseEntity entity = courseEntity.toEntity();
        entity.setTermId(termId);
        entity.setMentorId(mentorId);
        entity.setNumber(normalizedNumber);
        entity.setTitle(normalizedTitle);
        entity.setStatus(status);
        switch (status) {
            case UNPLANNED:
                entity.setExpectedStart(null);
                entity.setExpectedEnd(null);
                entity.setActualStart(null);
                entity.setActualEnd(null);
                break;
            case IN_PROGRESS:
                entity.setExpectedStart(expectedStart);
                entity.setExpectedEnd(expectedEnd);
                entity.setActualStart(actualStart);
                entity.setActualEnd(null);
            case PLANNED:
                entity.setExpectedStart(expectedStart);
                entity.setExpectedEnd(expectedEnd);
                entity.setActualStart(null);
                entity.setActualEnd(null);
                break;
            default:
                entity.setExpectedStart(expectedStart);
                entity.setExpectedEnd(expectedEnd);
                entity.setActualStart(actualStart);
                entity.setActualEnd(actualEnd);
                break;
        }
        entity.setCompetencyUnits(competencyUnitsValue);
        entity.setNotes(notes);
        return dbLoader.saveCourse(courseEntity.toEntity()).toSingleDefault(Collections.emptyList());
    }

    public Completable delete() {
        Log.d(LOG_TAG, "Enter Erwine.Leonard.T.wguscheduler356334.ui.term.TermPropertiesViewModel.delete");
        return dbLoader.deleteCourse(Objects.requireNonNull(entityLiveData.getValue()).toEntity()).doOnError(throwable -> Log.e(getClass().getName(),
                "Error deleting course", throwable));
    }

    public boolean isChanged() {
        if (null != courseEntity.getId() && normalizedNumber.equals(courseEntity.getNumber()) && normalizedTitle.equals(courseEntity.getTitle()) && Objects.equals(getExpectedStart(), courseEntity.getExpectedStart()) &&
                Objects.equals(getExpectedEnd(), courseEntity.getExpectedEnd()) && Objects.equals(getActualStart(), courseEntity.getActualStart()) && Objects.equals(getActualEnd(), courseEntity.getActualEnd()) &&
                status == courseEntity.getStatus() && null != competencyUnitsValue && competencyUnitsValue == courseEntity.getCompetencyUnits()) {
            if (null == normalizedNotes) {
                normalizedNotes = MentorEntity.MULTI_LINE_NORMALIZER.apply(notes);
            }
            return !normalizedNotes.equals(courseEntity.getNotes());
        }
        return true;
    }

}