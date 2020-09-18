package Erwine.Leonard.T.wguscheduler356334.ui.course;

import android.app.Application;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

import Erwine.Leonard.T.wguscheduler356334.R;
import Erwine.Leonard.T.wguscheduler356334.db.DbLoader;
import Erwine.Leonard.T.wguscheduler356334.entity.CourseEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.CourseStatus;
import Erwine.Leonard.T.wguscheduler356334.entity.MentorEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.TermEntity;
import Erwine.Leonard.T.wguscheduler356334.ui.term.EditTermViewModel;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class EditCourseViewModel extends AndroidViewModel {
    private static final String LOG_TAG = EditTermViewModel.class.getName();
    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("eee, MMM d, YYYY").withZone(ZoneId.systemDefault());
    static final String ARGUMENT_KEY_STATE_INITIALIZED = "state_initialized";
    public static final String ARGUMENT_KEY_COURSE_ID = "course_id";
    public static final String ARGUMENT_KEY_TERM_ID = "term_id";
    public static final String ARGUMENT_KEY_MENTOR_ID = "mentor_id";
    public static final String ARGUMENT_KEY_NUMBER = "number";
    public static final String ARGUMENT_KEY_TITLE = "title";
    public static final String ARGUMENT_KEY_EXPECTED_START = "expected_start";
    public static final String ARGUMENT_KEY_ACTUAL_START = "actual_start";
    public static final String ARGUMENT_KEY_EXPECTED_END = "expected_end";
    public static final String ARGUMENT_KEY_ACTUAL_END = "actual_end";
    public static final String ARGUMENT_KEY_STATUS = "status";
    public static final String ARGUMENT_KEY_COMPETENCY_UNITS = "competency_units";
    public static final String ARGUMENT_KEY_NOTES = "notes";
    public static final String ARGUMENT_KEY_ORIGINAL_TERM_ID = "o_term_id";
    public static final String ARGUMENT_KEY_ORIGINAL_MENTOR_ID = "o_mentor_id";
    public static final String ARGUMENT_KEY_ORIGINAL_NUMBER = "o_number";
    public static final String ARGUMENT_KEY_ORIGINAL_TITLE = "o_title";
    public static final String ARGUMENT_KEY_ORIGINAL_EXPECTED_START = "o_expected_start";
    public static final String ARGUMENT_KEY_ORIGINAL_ACTUAL_START = "o_actual_start";
    public static final String ARGUMENT_KEY_ORIGINAL_EXPECTED_END = "o_expected_end";
    public static final String ARGUMENT_KEY_ORIGINAL_ACTUAL_END = "o_actual_end";
    public static final String ARGUMENT_KEY_ORIGINAL_STATUS = "o_status";
    public static final String ARGUMENT_KEY_ORIGINAL_COMPETENCY_UNITS = "o_competency_units";
    public static final String ARGUMENT_KEY_ORIGINAL_NOTES = "o_notes";

    private final DbLoader dbLoader;
    private CourseEntity courseEntity;
    private final MutableLiveData<CourseEntity> entityLiveData;
    private final LiveData<List<TermEntity>> termsLiveData;
    private final LiveData<List<MentorEntity>> mentorsLiveData;
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

    public void setTermId(Long termId) {
        if (!Objects.equals(this.termId, termId)) {
            this.termId = termId;
            termValidLiveData.postValue(null != termId);
        }
    }

    public Long getMentorId() {
        return mentorId;
    }

    public void setMentorId(Long mentorId) {
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

    public MutableLiveData<CourseEntity> getEntityLiveData() {
        return entityLiveData;
    }

    public LiveData<List<MentorEntity>> getMentorsLiveData() {
        return mentorsLiveData;
    }

    public LiveData<List<TermEntity>> getTermsLiveData() {
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

    public synchronized Single<CourseEntity> restoreState(@Nullable Bundle savedInstanceState, Supplier<Bundle> getArguments) {
        fromInitializedState = null != savedInstanceState && savedInstanceState.getBoolean(ARGUMENT_KEY_STATE_INITIALIZED, false);
        Bundle state = (fromInitializedState) ? savedInstanceState : getArguments.get();
        if (null == state) {
            courseEntity = new CourseEntity();
        } else if (state.containsKey(ARGUMENT_KEY_COURSE_ID)) {
            if (fromInitializedState) {
                courseEntity = new CourseEntity(state.getString(ARGUMENT_KEY_ORIGINAL_NUMBER, ""), state.getString(ARGUMENT_KEY_ORIGINAL_TITLE, ""),
                        (state.containsKey(ARGUMENT_KEY_ORIGINAL_STATUS)) ? CourseStatus.valueOf(CourseStatus.class, state.getString(ARGUMENT_KEY_ORIGINAL_STATUS)) : CourseStatus.UNPLANNED,
                        (state.containsKey(ARGUMENT_KEY_ORIGINAL_EXPECTED_START)) ? LocalDate.ofEpochDay(state.getLong(ARGUMENT_KEY_ORIGINAL_EXPECTED_START)) : null,
                        (state.containsKey(ARGUMENT_KEY_ORIGINAL_ACTUAL_START)) ? LocalDate.ofEpochDay(state.getLong(ARGUMENT_KEY_ORIGINAL_ACTUAL_START)) : null,
                        (state.containsKey(ARGUMENT_KEY_ORIGINAL_EXPECTED_END)) ? LocalDate.ofEpochDay(state.getLong(ARGUMENT_KEY_ORIGINAL_EXPECTED_END)) : null,
                        (state.containsKey(ARGUMENT_KEY_ORIGINAL_ACTUAL_END)) ? LocalDate.ofEpochDay(state.getLong(ARGUMENT_KEY_ORIGINAL_ACTUAL_END)) : null,
                        state.getInt(ARGUMENT_KEY_ORIGINAL_COMPETENCY_UNITS, 0), state.getString(ARGUMENT_KEY_ORIGINAL_NOTES, ""),
                        state.getLong(ARGUMENT_KEY_ORIGINAL_TERM_ID), (state.containsKey(ARGUMENT_KEY_ORIGINAL_MENTOR_ID)) ? state.getLong(ARGUMENT_KEY_ORIGINAL_MENTOR_ID) : null,
                        state.getLong(ARGUMENT_KEY_COURSE_ID));
            } else {
                return dbLoader.getCourseById(state.getLong(ARGUMENT_KEY_COURSE_ID))
                        .doOnSuccess(this::onEntityLoadedFromDb)
                        .doOnError(throwable -> Log.e(getClass().getName(), "Error loading term", throwable));
            }
        } else if (fromInitializedState) {
            if (state.containsKey(ARGUMENT_KEY_ORIGINAL_TERM_ID)) {
                courseEntity = new CourseEntity(state.getString(ARGUMENT_KEY_ORIGINAL_NUMBER, ""), state.getString(ARGUMENT_KEY_ORIGINAL_TITLE, ""),
                        (state.containsKey(ARGUMENT_KEY_ORIGINAL_STATUS)) ? CourseStatus.valueOf(CourseStatus.class, state.getString(ARGUMENT_KEY_ORIGINAL_STATUS)) : CourseStatus.UNPLANNED,
                        (state.containsKey(ARGUMENT_KEY_ORIGINAL_EXPECTED_START)) ? LocalDate.ofEpochDay(state.getLong(ARGUMENT_KEY_ORIGINAL_EXPECTED_START)) : null,
                        (state.containsKey(ARGUMENT_KEY_ORIGINAL_ACTUAL_START)) ? LocalDate.ofEpochDay(state.getLong(ARGUMENT_KEY_ORIGINAL_ACTUAL_START)) : null,
                        (state.containsKey(ARGUMENT_KEY_ORIGINAL_EXPECTED_END)) ? LocalDate.ofEpochDay(state.getLong(ARGUMENT_KEY_ORIGINAL_EXPECTED_END)) : null,
                        (state.containsKey(ARGUMENT_KEY_ORIGINAL_ACTUAL_END)) ? LocalDate.ofEpochDay(state.getLong(ARGUMENT_KEY_ORIGINAL_ACTUAL_END)) : null,
                        state.getInt(ARGUMENT_KEY_ORIGINAL_COMPETENCY_UNITS, 0), state.getString(ARGUMENT_KEY_ORIGINAL_NOTES, ""),
                        state.getLong(ARGUMENT_KEY_ORIGINAL_TERM_ID), (state.containsKey(ARGUMENT_KEY_ORIGINAL_MENTOR_ID)) ? state.getLong(ARGUMENT_KEY_ORIGINAL_MENTOR_ID) : null);
            } else {
                courseEntity = new CourseEntity(state.getString(ARGUMENT_KEY_ORIGINAL_NUMBER, ""), state.getString(ARGUMENT_KEY_ORIGINAL_TITLE, ""),
                        (state.containsKey(ARGUMENT_KEY_ORIGINAL_STATUS)) ? CourseStatus.valueOf(CourseStatus.class, state.getString(ARGUMENT_KEY_ORIGINAL_STATUS)) : CourseStatus.UNPLANNED,
                        (state.containsKey(ARGUMENT_KEY_ORIGINAL_EXPECTED_START)) ? LocalDate.ofEpochDay(state.getLong(ARGUMENT_KEY_ORIGINAL_EXPECTED_START)) : null,
                        (state.containsKey(ARGUMENT_KEY_ORIGINAL_ACTUAL_START)) ? LocalDate.ofEpochDay(state.getLong(ARGUMENT_KEY_ORIGINAL_ACTUAL_START)) : null,
                        (state.containsKey(ARGUMENT_KEY_ORIGINAL_EXPECTED_END)) ? LocalDate.ofEpochDay(state.getLong(ARGUMENT_KEY_ORIGINAL_EXPECTED_END)) : null,
                        (state.containsKey(ARGUMENT_KEY_ORIGINAL_ACTUAL_END)) ? LocalDate.ofEpochDay(state.getLong(ARGUMENT_KEY_ORIGINAL_ACTUAL_END)) : null,
                        state.getInt(ARGUMENT_KEY_ORIGINAL_COMPETENCY_UNITS, 0), state.getString(ARGUMENT_KEY_ORIGINAL_NOTES, ""),
                        (state.containsKey(ARGUMENT_KEY_ORIGINAL_MENTOR_ID)) ? state.getLong(ARGUMENT_KEY_ORIGINAL_MENTOR_ID) : null);
            }
        } else if (state.containsKey(ARGUMENT_KEY_TERM_ID)) {
            courseEntity = new CourseEntity(state.getLong(ARGUMENT_KEY_TERM_ID));
        } else {
            courseEntity = new CourseEntity();
        }
        if (fromInitializedState && null != state) {
            setNumber(state.getString(ARGUMENT_KEY_NUMBER, ""));
            setTitle(state.getString(ARGUMENT_KEY_TITLE, ""));
            setCompetencyUnitsText(state.getString(ARGUMENT_KEY_COMPETENCY_UNITS, ""));
            setStatus(CourseStatus.valueOf(state.getString(ARGUMENT_KEY_STATUS, CourseStatus.UNPLANNED.name())));
            setTermId((state.containsKey(ARGUMENT_KEY_TERM_ID)) ? state.getLong(ARGUMENT_KEY_TERM_ID) : null);
            setMentorId((state.containsKey(ARGUMENT_KEY_MENTOR_ID)) ? state.getLong(ARGUMENT_KEY_MENTOR_ID) : null);
            setExpectedStart((state.containsKey(ARGUMENT_KEY_EXPECTED_START)) ? LocalDate.ofEpochDay(state.getLong(ARGUMENT_KEY_EXPECTED_START)) : null);
            setExpectedEnd((state.containsKey(ARGUMENT_KEY_EXPECTED_END)) ? LocalDate.ofEpochDay(state.getLong(ARGUMENT_KEY_EXPECTED_END)) : null);
            setActualStart((state.containsKey(ARGUMENT_KEY_ACTUAL_START)) ? LocalDate.ofEpochDay(state.getLong(ARGUMENT_KEY_ACTUAL_START)) : null);
            setActualEnd((state.containsKey(ARGUMENT_KEY_ACTUAL_END)) ? LocalDate.ofEpochDay(state.getLong(ARGUMENT_KEY_ACTUAL_END)) : null);
            setNotes(state.getString(ARGUMENT_KEY_NOTES, ""));
        } else {
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
        }
        entityLiveData.postValue(courseEntity);
        return Single.just(courseEntity).observeOn(AndroidSchedulers.mainThread());
    }

    public void saveState(Bundle outState) {
        outState.putBoolean(ARGUMENT_KEY_STATE_INITIALIZED, true);
        if (null != courseEntity.getId()) {
            outState.putLong(ARGUMENT_KEY_COURSE_ID, courseEntity.getId());
        }
        Long id = termId;
        if (null != id) {
            outState.putLong(ARGUMENT_KEY_TERM_ID, id);
        }
        outState.putLong(ARGUMENT_KEY_ORIGINAL_TERM_ID, courseEntity.getTermId());
        id = mentorId;
        if (null != id) {
            outState.putLong(ARGUMENT_KEY_MENTOR_ID, id);
        }
        id = courseEntity.getMentorId();
        if (null != id) {
            outState.putLong(ARGUMENT_KEY_ORIGINAL_MENTOR_ID, id);
        }
        outState.putString(ARGUMENT_KEY_STATUS, status.name());
        outState.putString(ARGUMENT_KEY_ORIGINAL_STATUS, courseEntity.getStatus().name());
        outState.putString(ARGUMENT_KEY_COMPETENCY_UNITS, getCompetencyUnitsText());
        outState.putInt(ARGUMENT_KEY_ORIGINAL_COMPETENCY_UNITS, courseEntity.getCompetencyUnits());
        outState.putString(ARGUMENT_KEY_NUMBER, number);
        outState.putString(ARGUMENT_KEY_ORIGINAL_NUMBER, courseEntity.getNumber());
        LocalDate date = getExpectedStart();
        if (null != date) {
            outState.putLong(ARGUMENT_KEY_EXPECTED_START, date.toEpochDay());
        }
        date = courseEntity.getExpectedStart();
        if (null != date) {
            outState.putLong(ARGUMENT_KEY_ORIGINAL_EXPECTED_START, date.toEpochDay());
        }
        date = getExpectedEnd();
        if (null != date) {
            outState.putLong(ARGUMENT_KEY_EXPECTED_END, date.toEpochDay());
        }
        date = courseEntity.getExpectedEnd();
        if (null != date) {
            outState.putLong(ARGUMENT_KEY_ORIGINAL_EXPECTED_END, date.toEpochDay());
        }
        date = getActualStart();
        if (null != date) {
            outState.putLong(ARGUMENT_KEY_ACTUAL_START, date.toEpochDay());
        }
        date = courseEntity.getActualStart();
        if (null != date) {
            outState.putLong(ARGUMENT_KEY_ORIGINAL_ACTUAL_START, date.toEpochDay());
        }
        date = getActualEnd();
        if (null != date) {
            outState.putLong(ARGUMENT_KEY_ACTUAL_END, date.toEpochDay());
        }
        date = courseEntity.getActualEnd();
        if (null != date) {
            outState.putLong(ARGUMENT_KEY_ORIGINAL_ACTUAL_END, date.toEpochDay());
        }
        outState.putString(ARGUMENT_KEY_NOTES, notes);
        outState.putString(ARGUMENT_KEY_ORIGINAL_NOTES, courseEntity.getNotes());
    }

    private void onEntityLoadedFromDb(CourseEntity entity) {
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
        long originalTermId = courseEntity.getTermId();
        Long originalMentorId = courseEntity.getMentorId();
        String originalNumber = courseEntity.getNumber();
        String originalTitle = courseEntity.getTitle();
        LocalDate originalExpectedStart = courseEntity.getExpectedStart();
        LocalDate originalActualStart = courseEntity.getExpectedEnd();
        LocalDate originalExpectedEnd = courseEntity.getActualStart();
        LocalDate originalActualEnd = courseEntity.getActualEnd();
        CourseStatus originalStatus = courseEntity.getStatus();
        int originalCompetencyUnits = courseEntity.getCompetencyUnits();
        String originalNotes = courseEntity.getNotes();
        courseEntity.setTermId(termId);
        courseEntity.setMentorId(mentorId);
        courseEntity.setNumber(normalizedNumber);
        courseEntity.setTitle(normalizedTitle);
        courseEntity.setStatus(status);
        switch (status) {
            case UNPLANNED:
                courseEntity.setExpectedStart(null);
                courseEntity.setExpectedEnd(null);
                courseEntity.setActualStart(null);
                courseEntity.setActualEnd(null);
                break;
            case IN_PROGRESS:
                courseEntity.setExpectedStart(expectedStart);
                courseEntity.setExpectedEnd(expectedEnd);
                courseEntity.setActualStart(actualStart);
                courseEntity.setActualEnd(null);
            case PLANNED:
                courseEntity.setExpectedStart(expectedStart);
                courseEntity.setExpectedEnd(expectedEnd);
                courseEntity.setActualStart(null);
                courseEntity.setActualEnd(null);
                break;
            default:
                courseEntity.setExpectedStart(expectedStart);
                courseEntity.setExpectedEnd(expectedEnd);
                courseEntity.setActualStart(actualStart);
                courseEntity.setActualEnd(actualEnd);
                break;
        }
        courseEntity.setCompetencyUnits(competencyUnitsValue);
        courseEntity.setNotes(notes);
        return dbLoader.saveCourse(courseEntity).doOnError(throwable -> {
            courseEntity.setTermId(originalTermId);
            courseEntity.setMentorId(originalMentorId);
            courseEntity.setNumber(originalNumber);
            courseEntity.setTitle(originalTitle);
            courseEntity.setStatus(originalStatus);
            courseEntity.setExpectedStart(originalExpectedStart);
            courseEntity.setExpectedEnd(originalExpectedEnd);
            courseEntity.setActualStart(originalActualStart);
            courseEntity.setActualEnd(originalActualEnd);
            courseEntity.setCompetencyUnits(originalCompetencyUnits);
            courseEntity.setNotes(originalNotes);
        }).toSingleDefault(Collections.emptyList());
    }

    public Completable delete() {
        Log.d(LOG_TAG, "Enter Erwine.Leonard.T.wguscheduler356334.ui.term.TermPropertiesViewModel.delete");
        return dbLoader.deleteCourse(entityLiveData.getValue()).doOnError(throwable -> Log.e(getClass().getName(), "Error deleting term", throwable));
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